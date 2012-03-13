package com.sungardhe.banner.i18n

import grails.test.*

class IslamicDateTests extends GroovyTestCase {

    def islamicDateService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testInjection() {
        assertNotNull islamicDateService
    }

    void testGregorianToIslamicDateConversion() {
        assertEquals islamicDateService.getIslamicDateString("2012/01/01"), "1433/02/06"
        assertEquals islamicDateService.getIslamicDateString("12/01/01","yy/MM/dd"), "1433/02/06"
        assertEquals islamicDateService.getIslamicDateString("12/01/01","yy/MM/dd","dd/MM/yyyy"), "06/02/1433"
        assertEquals islamicDateService.getIslamicDateString("2012/01/01",null,"dd/MM/yyyy"), "06/02/1433"
    }

    void testIslamicToGregorianDateConversion() {
        assertEquals islamicDateService.getGregorianDateString("1433/02/06"), "2012/01/01"
        assertEquals islamicDateService.getGregorianDateString("1433/02/06","yyyy/MM/dd"), "2012/01/01"
        assertEquals islamicDateService.getGregorianDateString("1433/02/06","yyyy/MM/dd","dd/MM/yyyy"), "01/01/2012"
        assertEquals islamicDateService.getGregorianDateString("1433/02/06",null,"dd/MM/yyyy"), "01/01/2012"
    }
}