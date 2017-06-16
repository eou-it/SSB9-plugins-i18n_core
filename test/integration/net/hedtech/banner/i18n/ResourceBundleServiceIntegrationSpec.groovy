/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.test.spock.IntegrationSpec

class ResourceBundleServiceIntegrationSpec extends IntegrationSpec {

    def messageSource
    def resourceBundleService

    def testLocales = ['en_GB','fr_FR']
    def testLocalesSave =   [
            [
                "enabled": true,
                "code": "en_GB"
            ],
            [
                "enabled": true,
                "code": "fr_FR"
            ]
    ]

    void "tests"() {
        when:
        def resources = resourceBundleService.list()
        def resList =  []
        testLocales.each { locale ->
                resources.each { it ->
                    resList << resourceBundleService.get(it.basename, locale)
                }
            }
        def fr = resourceBundleService.get('testExternalResource/test', 'fr_FR')

        def data = resources[0]
        data.enableTranslation = true
        data.sourceLocale = 'root'
        data.locales = testLocalesSave
        def saveResult = resourceBundleService.save(data)
        then:
        resources.size() > 0
        resList.size() > 0
        fr.locale == "fr_FR"
        saveResult.count > 0 //Note the saving to TranMan is mocked and the value of count is not really meaningful
    }


}
