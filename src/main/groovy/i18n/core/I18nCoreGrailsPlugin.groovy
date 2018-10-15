package i18n.core

import grails.plugins.*
import grails.util.Environment
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.config.PropertySourcesConfig



@Slf4j
class I18nCoreGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    //List loadAfter = ['springSecurityCore']
    List loadBefore = ['bannerCore','springSecurityCore','springSecuritySaml','springSecurityCas']

    def dependsOn =  [
            springSecurityCore: '3.2.3 => *'
    ]
    def authorEmail = ""
    def description = '''\
                        Brief summary/description of the plugin.
                       '''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/i18n-core"

    Closure doWithSpring() { {->
        println "--------- In Banner i18n: doWithSpring ----------------"
        println "Before merge Holders.config.size()"  + grailsApplication.config.size()
        setupExternalConfig()
        println "After mergeHolders.config.size()"  + grailsApplication.config.size()
        println "SPRING UTILS - FAILURE URL - ${Holders.config.grails.plugin.springsecurity.failureHandler.defaultFailureUrl}"
        println "\n AuthenticationProvider = " + Holders.flatConfig.banner.sso.authenticationProvider
        println "CH.config.bannerDataSource - ${Holders.config.bannerDataSource}"
        println "--------- In Banner i18n: doWithSpring End \n----------------"
    }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    private setupExternalConfig() {
        def config = Holders.config
        def locations = config.grails.config.locations
        String filePathName
        String configText

        locations.each { propertyName,  fileName ->
            filePathName = getFilePath(System.getProperty(propertyName))
            if (Environment.getCurrent() != Environment.PRODUCTION) {
                if (!filePathName) {
                    filePathName = getFilePath("${System.getProperty('user.home')}/.grails/${fileName}")
                    if (filePathName) log.info "Using configuration file '\$HOME/.grails/$fileName'"
                }
                if (!filePathName) {
                    filePathName = getFilePath("${fileName}")
                    if (filePathName) log.info "Using configuration file '$fileName'"
                }
                if (!filePathName) {
                    filePathName = getFilePath("grails-app/conf/$fileName")
                    if (filePathName) log.info "Using configuration file 'grails-app/conf/$fileName'"
                }
                println "External configuration file: " + filePathName
                configText = new File(filePathName)?.text
            } else {
                if (filePathName) {
                    println "In prod mode using configuration file '$fileName' from the system path"
                    log.info "In prod mode using configuration file '$fileName' from the system path"
                    configText = new File(filePathName)?.text
                } else {
                    filePathName = Thread.currentThread().getContextClassLoader().getResource( "$fileName" )?.getFile()
                    configText   = Thread.currentThread().getContextClassLoader().getResource( "$fileName" ).text
                    println "Using configuration file '$fileName' from the classpath"
                    log.info "Using configuration file '$fileName' from the classpath (e.g., from within the war file)"
                }
            }
            if(filePathName && configText) {
                try {
                    Map properties = configText ? new ConfigSlurper(Environment.current.name).parse(configText)?.flatten() : [:]
                    Holders.config.merge(properties)
                }
                catch (e) {
                    println "NOTICE: Caught exception while loading configuration files (depending on current grails target, this may be ok): ${e.message}"
                }
            } else {
                log.error "Configuration files not found either in system variable or in classpath."
            }
        }
    }


    private static String getFilePath( filePath ) {
        if (filePath && new File( filePath ).exists()) {
            "${filePath}"
        }
    }

}
