package i18n.core

import grails.plugins.*
import grails.util.Environment
import grails.util.Holders
import org.grails.config.PropertySourcesConfig
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.web.context.support.StandardServletEnvironment

class I18nCoreGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "I18n Core" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/i18n-core"

    Closure doWithSpring() { {->
        setupExternalConfig()
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
        PropertySourcesConfig config = Holders.config
        def locations = config.grails.config.locations
        String filePathName

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
            } else {
                //filePathName = Thread.currentThread().getContextClassLoader().getResource( "$fileName" )?.toURI()
                //filePathName = "classpath:$fileName"
            }
            if(filePathName) {
                println "External configuration file: " + filePathName
                try {
                    String configText = new File(filePathName).text
                    Map properties = configText ? new ConfigSlurper(Environment.current.name).parse(configText)?.flatten() : [:]
                    Holders.config.merge(properties)
                }
                catch (e) {
                    println "NOTICE: Caught exception while loading configuration files (depending on current grails target, this may be ok): ${e.message}"
                }
            }
        }
    }

    private static String getFilePath( filePath ) {
        if (filePath && new File( filePath ).exists()) {
            "${filePath}"
        }
    }

}
