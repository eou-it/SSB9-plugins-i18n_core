/******************************************************************************
 *  Copyright 2016 Ellucian Company L.P. and its affiliates.             *
 ******************************************************************************/

package net.hedtech.banner.i18n


import grails.test.spock.IntegrationSpec
import org.junit.Test

class TextManagerServiceIntegrationSpec {

    def textManagerService

    def setup() {
        textManagerService = new TextManagerService()
        textManagerService.createProjectForApp('ZZZ_INTEGTEST', 'Integration Test Banner Core')
    }

    def cleanup() {
        textManagerService.deleteProjectforApp()
    }

    @Test
    void "test saving a source and target locales"() {
        given:
        def name = "integrationTest"
        def sourceProperties = new Properties()
        def sourceLocale = textManagerService.ROOT_LOCALE_APP
        sourceProperties.put("dummy.label1", "Dummy English text1")
        sourceProperties.put("dummy.label2", "Dummy English text2")
        def targetProperties = new Properties()
        def targetLocale = "frFR"
        targetProperties.put("dummy.label1", "Dummy French text1")
        targetProperties.put("dummy.label2", "Dummy French text2")
        when: "Saved OK"
        def sourceStatus = textManagerService.save(sourceProperties, name, sourceLocale, sourceLocale)
        def targetStatus = textManagerService.save(targetProperties, name, sourceLocale, targetLocale)
        then: "Great"
        sourceStatus.error == null
        sourceStatus.count == 2
        targetStatus.error == null
        targetStatus.count == 2
    }

    //Test result of saving when the query part is implemented


}
