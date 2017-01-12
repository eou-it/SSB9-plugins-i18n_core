/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/

package net.hedtech.banner.i18n

import grails.util.Holders
import groovy.sql.Sql
import net.hedtech.banner.textmanager.Dbif
import net.hedtech.banner.textmanager.ReplaceProps
import net.hedtech.banner.textmanager.TmCtx


class TextManagerService {

    static transactional = false //Transaction not managed by hibernate

    final def ROOT_LOCALE_APP  = 'en_US' // This will be the locale assumed for properties without locale
    final def ROOT_LOCALE_TM   = 'root'  // Save the chosen source language as root (as user cannot change translation)
    final def PROJECT_CFG_KEY_APP  = 'BAN_APP'
    final def PROJECT_CFG_KEY_VERSION = 'BAN_APP_VERSION'

    private def dbUrl =
            (Holders.config.tranManDataSource?.url?:Holders.config.bannerDataSource.url).minus("jdbc:oracle:thin:@")
    private def username = Holders.config.tranManDataSource?.username?:Holders.config.bannerDataSource.username
    private def password = Holders.config.tranManDataSource?.password?:Holders.config.bannerDataSource.password
    final def connectString = "${username}/${password}@${dbUrl}"  // Eventually just use Banner connection

    private def tranManProjectCache
    private def cacheTime

    private def TextManagerProject() {
        if ( cacheTime && ( new Date().getTime() - cacheTime.getTime() ) < 5 * 60 * 1000 ) {
            return tranManProjectCache
        }

        def tmdbif = new Dbif(connectString, null) // get a standard connection
        def sql = new Sql(tmdbif.conn)
        def appName = grails.util.Holders.grailsApplication.metadata['app.name']
        def result = ""
        def matches = 0
        // Find projects with a matching application name in tmcfg
        // If more matches exist pick the project with the latest activity date
        def statement = """
          select project_code from tmcfg natural join tmproj
          where cfg_key = $PROJECT_CFG_KEY_APP
          and cfg_value = $appName
          order by project_acty_date
        """
        sql.eachRow(statement) { row ->
            result = row.PROJECT_CODE
            matches++
        }
        tmdbif.closeConnection()
        if (matches > 1) {
            log.warn "Multiple TranMan projects configured for application $appName. Please correct."
        }
        if (matches == 0) {
            log.warn "No TranMan project configured for application $appName."
        }
        tranManProjectCache = result
        cacheTime = new Date()
        result
    }

    def createProjectForApp(projectCode, projectDescription) {
        if (!tranManProject()) {
            def tmdbif = new Dbif(connectString, null) // get a standard connection
            def sql = new Sql(tmdbif.conn)
            def appName = grails.util.Holders.grailsApplication.metadata['app.name']
            try {
                def statement = """
                   insert into tmproj (project_code, project_acty_date, project_desc, owner)
                   values ($projectCode, sysdate, $projectDescription, 'TRANMGR')
                """
                sql.execute(statement)
                statement = """
                   insert into tmcfg (project_code, cfg_key, cfg_value,cfg_desc)
                   values ($projectCode, $PROJECT_CFG_KEY_APP, $appName, 'Banner Application in this project')
                """
                sql.execute(statement)
                cacheTime = null
                log.info "Created TranMan project $projectCode"
            } finally {
                tmdbif.closeConnection()
            }
        }
    }

    //Used to clean test project
    def deleteProjectforApp(){
        def project = tranManProject()
        if (project) {
            def tmdbif = new Dbif(connectString, null) // get a standard connection
            def sql = new Sql(tmdbif.conn)
            try {
                def statement = """
                  begin
                    delete from tmcfg where project_code=$project;
                    delete from tmstrprop where project_code=$project;
                    delete from tmstrprhst where project_code=$project;
                    delete from tmobject where project_code=$project;
                    delete from tmproj where project_code=$project;
                  end;
                """
                sql.execute(statement)
                cacheTime = null
                log.info "Deleted TranMan project $project"
            } finally {
                tmdbif.closeConnection()
            }
        }
    }

    def save(properties, name, sourceLocale=ROOT_LOCALE_APP, locale){
        def project = tranManProject()
        if (project) {
            def ctx = new TmCtx()
            def tmdbif
            int cnt = 0;
            try {
                String[] args = [
                        "pc=${project}", //Todo configure project in translation manager
                        "lo=${connectString}",
                        "mn=${name.toUpperCase()}",
                        "sl=$ROOT_LOCALE_TM",
                        locale == "$ROOT_LOCALE_APP" ? "sf=${name}.properties" : "sf=${name}_${locale}.properties",
                        locale == "$sourceLocale" ? 'mo=s' : 'mo=r',
                        locale == "$sourceLocale" ? '' : "tl=${locale.replace('_', '')}"
                ]

                ctx.parseArgs(args);
                tmdbif = new Dbif(ctx.get(ctx.logon), ctx)
                def op = tmdbif.getDefaultObjectProp();

                properties.each { property ->
                    final String sep = ".";
                    int seploc;
                    String key = property.key;
                    String value = property.value;
                    seploc = key.lastIndexOf(sep);
                    if (seploc == -1) {
                        seploc = 0;
                    }
                    op.parentName = "." + key.substring(0, seploc); //. plus expression between brackets in [x.y...].z
                    op.objectName = key.substring(seploc);       // expression between brackets in x.y....[z]
                    op.string = ReplaceProps.smartQuotesReplace(value);
                    log.info key + " = " + op.string
                    tmdbif.setPropString(op);
                    cnt++;
                }
                //Invalidate strings that are in db but not in property file
                if (ctx.get(ctx.mo).equals("s")) {
                    tmdbif.invalidateStrings();
                }
                tmdbif.setModuleRecord(ctx);

            } finally {
                tmdbif?.closeConnection();
            }
            return [error: null, count: cnt]
        }
        return [error: "Unable to save - no Project configured", count: 0]
    }

    def cacheMsg=[:]
    def localeLoaded=[:]
    def timeOut = 60*1000 as long //milli seconds

    def findMessage(key, locale) {
        def msg
        def t0 = new Date()
        if (localeLoaded[locale] && (t0.getTime() - localeLoaded[locale].getTime()) < timeOut) {
            msg = cacheMsg[key]?cacheMsg[key][locale]:null
        } else {
            def tmLocale = 'enGB'    //Todo: get mapping between web request locale and TranMan locale
            def tmProject = 'TM4BT'  //Todo: get from Config (one or more projects could be used)
            def since = new java.sql.Timestamp(localeLoaded[locale]?localeLoaded[locale].getTime():0) // 0 is like beginning of time
            def params = [locale: tmLocale, pc: tmProject, now: new java.sql.Timestamp(t0.getTime()), since: since]
            def tmdbif = new Dbif(connectString, null) // get a standard connection
            def sql = new Sql(tmdbif.conn)
            //Query fetching changed messages. Don't use message with status pending (11).
            //Can change to use mod_date > :since when changing :since to time in database timezone.
            def statement = """select parent_name||object_name as key
                              ,decode(status,11,null,pre_str||string||pst_str) as string
                               from tmstr natural join tmstrprop
                               where project_code = :pc
                                 and module_type = 'J'
                                 and lang_code = :locale
                                 and (sysdate - mod_date) <= (cast(:now as date) - cast(:since as date) )
                            """
            //and parent_type = 10 and parent_name = :pn and object_type = 26 and object_name = :on and object_prop = 438
            def rows
            try {
                rows = sql.rows(statement, params, null)
            }
            catch (e) {
                println(e)
            }
            finally {
                sql.close()
            }
            def t1 = new Date()
            if (rows.size()) {
                rows.each { row ->
                    def translations = cacheMsg[row.key]?cacheMsg[key]:[:]
                    translations[locale] = row.string
                    cacheMsg[row.key.substring(1)] = translations
                }
            }
            localeLoaded[locale]=t0
            msg = cacheMsg[key]?cacheMsg[key][locale]:null
            def t2 = new Date()
            println "$t0: Reloaded ${rows.size()} modified texts in ${ t2.getTime() - t0.getTime()} ms . Query+Fetch time: ${t1.getTime() - t0.getTime()}"
        }
        msg
    }
}
