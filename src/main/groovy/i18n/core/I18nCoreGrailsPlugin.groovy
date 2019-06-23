/*******************************************************************************
 Copyright 2009-2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package i18n.core

import grails.plugins.*
import groovy.util.logging.Slf4j
import net.hedtech.banner.configuration.ExternalConfigurationUtils

@Slf4j
class I18nCoreGrailsPlugin extends Plugin {

    def grailsVersion = "3.3.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    //List loadAfter = ['springSecurityCore']
    List loadBefore = ['XFrameOptions','bannerCore','springSecurityCore','springSecuritySaml','springSecurityCas']

    def dependsOn =  [
            springSecurityCore: '3.2.3 => *'
    ]

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/i18n-core"
	
	def profiles = ['web']

    Closure doWithSpring() {
        { ->
            ExternalConfigurationUtils.setupExternalConfig()
        }
    }

    void doWithDynamicMethods() {

    }

    void doWithApplicationContext() {

    }

    void onChange(Map<String, Object> event) {

    }

    void onConfigChange(Map<String, Object> event) {

    }

    void onShutdown(Map<String, Object> event) {

    }

}
