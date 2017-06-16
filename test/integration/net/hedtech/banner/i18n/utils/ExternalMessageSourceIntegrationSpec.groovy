/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n.utils

import grails.test.spock.IntegrationSpec
import net.hedtech.banner.i18n.ExternalMessageSource

class ExternalMessageSourceIntegrationSpec extends IntegrationSpec {

    def messageSource
    def externalLocation = 'target/i18n'
    def externalMessageSource

    def setup() {
        def subDir = new File(externalLocation)
        subDir.mkdirs()
        new File(externalLocation+"/test.properties").write("key = Text")
        new File(externalLocation+"/test_fr.properties").write("key = Fr Text")
        //Set up the externalMessageSource
        externalMessageSource = new ExternalMessageSource(
                externalLocation, 'integrationTest',
                "Setting up external message for integration test")
        messageSource?.setExternalMessageSource(externalMessageSource)
        /*Holders.config.bannerSsbDataSource.username="general"
        Holders.config.bannerSsbDataSource.url="10.42.4.24:1521:BAN83"//"localhost:1521:BAN83"//
        Holders.config.bannerSsbDataSource.password="u_pick_it"
        textManagerService = new TextManagerService()
        textManagerService.createProjectForApp('UNITTEST', 'Integration Test i18n Core')*/
    }

    def cleanup() {
        def subDir = new File(externalLocation)
        subDir.deleteDir()
    }

    void "test message source"() {
        when:
        def names = messageSource.getNormalizedNames()
        def properties = []
        names.each { name ->
            properties << messageSource.getPropertiesByNormalizedName(name, new Locale('en'))
        }
        then:
        names.size > 0
        properties.size  > 0
    }


    void "test add baseName" (){
        given:
        /*def sourceProperties = new Properties()
        def sourceLocale = textManagerService.ROOT_LOCALE_APP
        sourceProperties.put("dummy.label1", "Dummy English text1")*/
        when:
        /*textManagerService.save(sourceProperties, "integrationTest", sourceLocale, sourceLocale)*/
        externalMessageSource.addBasename("Dummy French text1")
        /*externalMessageSource.resolveCodeWithoutArguments("dummy.label1",sourceLocale)*/
        then:
        noExceptionThrown()
    }

}
