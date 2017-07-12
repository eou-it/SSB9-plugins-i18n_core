/*******************************************************************************
Copyright 2012-2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

/**
 * A Grails Plugin providing core i18n framework for Self Service Banner application.
 **/
import org.codehaus.groovy.grails.context.support.PluginAwareResourceBundleMessageSource
import net.hedtech.banner.i18n.BannerMessageSource

class I18nCoreGrailsPlugin {

    String groupId = "net.hedtech"

    def version = "9.24"

    def grailsVersion = "2.2.1 > *"

    def dependsOn = [:]

    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "SunGard Higher Education"
    def authorEmail = "horizon-support@sungardhe.com"
    def title = "SunGard Higher Education i18n Plugin"
    def description = '''i18n components.'''

    def documentation = ""

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // Reconfigure the messageSource to use BannerMessageSource
        //Inspired by https://sergiosmind.wordpress.com/2013/07/25/getting-all-i18n-messages-in-javascript/
        def beanConf = springConfig.getBeanConfig('messageSource')

        def beanDef = beanConf ? beanConf.beanDefinition : springConfig.getBeanDefinition('messageSource')

        if (beanDef?.beanClassName == PluginAwareResourceBundleMessageSource.class.canonicalName) {
            //just change the target class of the bean, maintaining all configurations.
            beanDef.beanClassName = BannerMessageSource.class.canonicalName
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
