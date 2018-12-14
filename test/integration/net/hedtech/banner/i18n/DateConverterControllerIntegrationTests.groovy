/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import org.junit.Before
import org.junit.Test


class DateConverterControllerIntegrationTests {
    def dateConverterController

    @Before
    public void setUp() {
        dateConverterController = new DateConverterController()
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

    @Test
    void testWithEmptyDate() {
        dateConverterController.params.date= null
        dateConverterController.params.fromULocale="en_US@calendar=gregorian"
        dateConverterController.params.toULocale="en_AR@calendar=islamic"
        dateConverterController.params.fromDateFormat="yyyy/MM/dd"
        dateConverterController.params.toDateFormat="yyyy/MM/dd"
        try{
            dateConverterController.data()
            fail : "This should have failed"
        }catch (AssertionError ae){
            assert "Date must be supplied", ae.message
        }

    }

}
