/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.testing.mixin.integration.Integration
import grails.util.GrailsWebMockUtil
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder

@Integration

class DateConverterControllerIntegrationTests {
    def dateConverterController
    def dateConverterService

    @Autowired
    WebApplicationContext ctx
    @Before
    public void setUp() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
        dateConverterController = new DateConverterController()
        dateConverterController.dateConverterService =dateConverterService
    }

    @Test
    void testDataRender(){
        dateConverterController.params.date="2012/01/01"
        dateConverterController.params.fromULocale="en_US@calendar=gregorian"
        dateConverterController.params.toULocale="en_AR@calendar=islamic"
        dateConverterController.params.fromDateFormat="yyyy/MM/dd"
        dateConverterController.params.toDateFormat="yyyy/MM/dd"
        dateConverterController.data()
        assert 200,dateConverterController.response.status
    }

    @Test
    void testI18nPropertiesRender(){
        dateConverterController.i18nProperties()
        assert 200,dateConverterController.response.status
    }
    @AfterClass
    public static void cleanUp() {
        RequestContextHolder.resetRequestAttributes()
    }
}
