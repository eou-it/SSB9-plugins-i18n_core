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
    List loadAfter = ['springSecurityCore']
    List loadBefore = ['bannerCore','springSecuritySaml','springSecurityCas']
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
        //println "--------- In Banner i18n: doWithSpring ----------------"
        //println "Before merge Holders.config.size()"  + grailsApplication.config.size()
        //setupExternalConfig()
        //println "After mergeHolders.config.size()"  + grailsApplication.config.size()
        //println "\n AuthenticationProvider = " + Holders.flatConfig.banner.sso.authenticationProvider
        //println "--------- In Banner i18n: doWithSpring End \n----------------"
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

}
