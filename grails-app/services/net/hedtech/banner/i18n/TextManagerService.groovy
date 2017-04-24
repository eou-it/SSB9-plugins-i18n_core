/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/

package net.hedtech.banner.i18n

import grails.util.Holders
import groovy.sql.Sql
import java.sql.Timestamp
import net.hedtech.banner.textmanager.TextManagerDB
import net.hedtech.banner.textmanager.TextManagerUtil


class TextManagerService {

    static transactional = false //Transaction not managed by hibernate

    def dataSource

    final def ROOT_LOCALE_APP  = 'en_US' // This will be the locale assumed for properties without locale
    final def ROOT_LOCALE_TM   = 'root'  // Save the chosen source language as root (as user cannot change translation)
    final def PROJECT_CFG_KEY_APP  = 'BAN_APP'

    private def tranManProjectCache
    private def cacheTime
    private def tmEnabled = true


    private String getConnectionString() {
        String dbUrl =dataSource.underlyingSsbDataSource.url
        def url = dbUrl.substring(dbUrl.lastIndexOf("@") + 1)
        def username = dataSource.underlyingSsbDataSource.username
        def password = dataSource.underlyingSsbDataSource.password
        return "${username}/${password}@${url}" // Eventually just use Banner connection
    }

    private def tranManProject() {
        if (!tmEnabled) {
            return
        }
        if ( cacheTime && ( new Date().getTime() - cacheTime.getTime() ) < 5 * 60 * 1000 ) {
            return tranManProjectCache
        }

        def textManagerDB = new TextManagerDB(getConnectionString(), null) // get a standard connection
        def sql = new Sql(textManagerDB.conn)
        def appName = Holders.grailsApplication.metadata['app.name']
        def result = ""
        def matches = 0
        try {
            // Find projects with a matching application name in GMRPCFG
            // If more matches exist pick the project with the latest activity date
            def statement = """
          select GMRPCFG_PROJECT from GMRPCFG join GMBPROJ on GMBPROJ_PROJECT=GMRPCFG_PROJECT
          where GMRPCFG_KEY = $PROJECT_CFG_KEY_APP
          and GMRPCFG_VALUE = $appName
          order by GMRPCFG_ACTIVITY_DATE
        """
            sql.eachRow(statement) { row ->
                result = row.GMRPCFG_PROJECT
                matches++
            }
        } catch (e) {
            tmEnabled = false
        } finally {
            textManagerDB.closeConnection()
        }
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
        if (!tmEnabled) {
            return
        }
        if (!tranManProject()) {
            def textManagerDB = new TextManagerDB(getConnectionString(), null) // get a standard connection
            def sql = new Sql(textManagerDB.conn)
            def appName = Holders.grailsApplication.metadata['app.name']
            def curDate = new Date()
            try {
                def statement = """
                   insert into GMBPROJ (GMBPROJ_PROJECT, GMBPROJ_ACTIVITY_DATE, GMBPROJ_DESC, GMBPROJ_OWNER,GMBPROJ_USER_ID)
                   values ($projectCode, sysdate, $projectDescription, 'TRANMGR','ban_ss_user')
                """
                sql.execute(statement)
                statement = """
                   insert into GMRPCFG (GMRPCFG_PROJECT, GMRPCFG_KEY, GMRPCFG_VALUE,GMRPCFG_DESC,GMRPCFG_USER_ID,GMRPCFG_ACTIVITY_DATE)
                   values ($projectCode, $PROJECT_CFG_KEY_APP, $appName, 'Banner Application in this project','ban_ss_user',sysdate )
                """
                sql.execute(statement)
                cacheTime = null
                log.info "Created TranMan project $projectCode"
            } finally {
                textManagerDB.closeConnection()
            }
        }
    }

    //Used to clean test project
    def deleteProjectforApp(){
        if (!tmEnabled) {
            return
        }
        def project = tranManProject()
        if (project) {
            def textManagerDB = new TextManagerDB(getConnectionString(), null) // get a standard connection
            def sql = new Sql(textManagerDB.conn)
            try {
                def statement = """
                  begin
                    delete from GMRPCFG where GMRPCFG_project=$project;
                    delete from GMRSPRP where GMRSPRP_project=$project;
                    delete from GMRSHST where GMRSHST_project=$project;
                    delete from GMRPOBJ where GMRPOBJ_project=$project;
                    delete from GMBPROJ where GMBPROJ_project=$project;
                  end;
                """
                sql.execute(statement)
                cacheTime = null
                log.info "Deleted TranMan project $project"
            } finally {
                textManagerDB.closeConnection()
            }
        }
    }

    def save(properties, name, sourceLocale=ROOT_LOCALE_APP, locale){
        if (!tmEnabled) {
            return
        }
        def project = tranManProject()
        if (project) {
            def textManagerUtil = new TextManagerUtil()
            def textManagerDB
            int cnt = 0;
            String sl = sourceLocale.replace('_','')
            try {
                String[] args = [
                        "pc=${project}", //Todo configure project in translation manager
                        "lo=${getConnectionString()}",
                        "mn=${name.toUpperCase()}",
                        "sl=$ROOT_LOCALE_TM",
                        locale == "$ROOT_LOCALE_APP" ? "sf=${name}.properties" : "sf=${name}_${locale}.properties",
                        locale == "$sourceLocale" ? 'mo=s' : 'mo=r',
                        locale == "$sourceLocale" ? '' : "tl=${locale.replace('_', '')}"
                ]

                textManagerUtil.parseArgs(args);
                textManagerDB = new TextManagerDB(textManagerUtil.get(textManagerUtil.logon), textManagerUtil)
                def op = textManagerDB.getDefaultObjectProp();

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
                    op.string = TextManagerUtil.smartQuotesReplace(value);
                    log.info key + " = " + op.string
                    textManagerDB.setPropString(op);
                    cnt++;
                }
                //Invalidate strings that are in db but not in property file
                if (textManagerUtil.get(textManagerUtil.mo).equals("s")) {
                    textManagerDB.invalidateStrings();
                }
                textManagerDB.setModuleRecord(textManagerUtil);

            } finally {
                textManagerDB?.closeConnection();
            }
            return [error: null, count: cnt]
        }
        return [error: "Unable to save - no Project configured", count: 0]
    }

    def cacheMsg=[:]
    def localeLoaded=[:]
    def timeOut = 60*1000 as long //milli seconds

    def findMessage(key, locale) {
        if (!tmEnabled) {
            return null
        }
        def msg
        def t0 = new Date()
        if (localeLoaded[locale] && (t0.getTime() - localeLoaded[locale].getTime()) < timeOut) {
            msg = cacheMsg[key]?cacheMsg[key][locale]:null
        } else {
            def tmLocale = locale?.toString().replace('_','')
            tmLocale = getAppropriateLocale(tmLocale)
            def tmProject = tranManProject()
            if (!tmEnabled) {
                return null
            }
            def since = new Timestamp(localeLoaded[locale]?localeLoaded[locale].getTime():0) // 0 is like beginning of time
            def params = [locale: tmLocale, pc: tmProject, now: new Timestamp(t0.getTime()), since: since]
            def textManagerDB = new TextManagerDB(getConnectionString(), null) // get a standard connection
            Sql sql = new Sql(textManagerDB.conn)
            sql.cacheStatements = false
            //Query fetching changed messages. Don't use message with status pending (11).
            //Can change to use mod_date > :since when changing :since to time in database timezone.
            def statement = """select GMRSPRP_PARENT_NAME||GMRSPRP_OBJECT_NAME as key
                              ,decode(GMRSPRP_STAT_CODE,11,null,GMRSPRP_PRE_STR||GMBSTRG_STRING||GMRSPRP_PST_STR) as string
                               from GMBSTRG join GMRSPRP on GMBSTRG_STRCODE=GMRSPRP_STRCODE
                               where GMRSPRP_PROJECT = :pc
                                 and GMRSPRP_MODULE_TYPE = 'J'
                                 and GMRSPRP_LANG_CODE = :locale
                                and (sysdate - GMRSPRP_ACTIVITY_DATE) <= (cast(:now as date) - cast(:since as date) )
                            """

            //and parent_type = 10 and parent_name = :pn and object_type = 26 and object_name = :on and object_prop = 438
            def rows
            try {
                rows = sql.rows(statement, params, null)
            }
            catch (e) {
                log.error("Exception in findMessage for key=$key, locale=$locale \n$e")
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

    String getAppropriateLocale(String locale){
        if(locale.contains("ar"))
            return "arSA"
        else if(locale.contains("es"))
            return "esMX"
        else return locale
    }
}
