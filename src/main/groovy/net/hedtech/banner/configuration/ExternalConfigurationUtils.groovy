/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.configuration

import grails.util.Environment
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j
class ExternalConfigurationUtils {

    /**
     * Loads the external configuration file, using the following search order.
     * 1. Load the configuration file if its location was specified on the command line using -DmyEnvName=myConfigLocation
     * 2. Load the configuration file if its location was specified as a system environment variable.
     * 3. If NOT Grails production env load the configuration file if it exists within the user's .grails directory (i.e., convenient for developers)
     * 4. Load from the classpath (e.g., load file from /WEB-INF/classes within the war file). The installer is used to copy configurations
     *    to this location, so that war files 'may' be self contained (yet can still be overriden using external configuration files)
     **/
    public static setupExternalConfig() {
        def config = Holders.config
        def locations = config.grails.config.locations
        String filePathName
        String configText

        locations.each { propertyName,  fileName ->
            Boolean isConfigFromClasspath = false
            String propertyValue = System.getProperty(propertyName) ?: System.getenv(propertyName)
            filePathName = getFilePath(propertyValue)
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
                    configText   = Thread.currentThread().getContextClassLoader().getResource( "$fileName" )?.text
                    isConfigFromClasspath = true
                    println "Using configuration file '$fileName' from the classpath"
                    log.info "Using configuration file '$fileName' from the classpath (e.g., from within the war file)"
                }
            }
            if(filePathName && configText) {
                try {
                    if(filePathName.endsWith('.groovy')){
                        loadExternalGroovyConfig(configText)
                    }
                    else if(filePathName.endsWith('.properties')){
                        loadExternalPropertiesConfig(filePathName,fileName, isConfigFromClasspath )
                    }
                }
                catch (e) {
                    println "NOTICE: Caught exception while loading configuration files (depending on current grails target, this may be ok): ${e.message}"
                }
            } else {
                println "Configuration files not found either in system variable or in classpath."
            }
        }
    }

    private static String getFilePath( filePath ) {
        if (filePath && new File( filePath ).exists()) {
            "${filePath}"
        }
    }

    private static void loadExternalPropertiesConfig(String filePathName, String fileName, Boolean isConfigFromClasspath) {
        InputStream inputStream
        if (isConfigFromClasspath) {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/$fileName")
        } else {
            inputStream = new FileInputStream(filePathName)
        }
        Properties prop = new Properties()
        prop.load(inputStream)
        Holders.config.merge(prop)
    }

    private static void loadExternalGroovyConfig(String configText) {
        Map properties = configText ? new ConfigSlurper(Environment.current.name).parse(configText)?.flatten() : [:]
        Holders.config.merge(properties)
    }

}
