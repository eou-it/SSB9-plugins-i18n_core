package net.hedtech.banner.i18n

class DateConverterTests extends GroovyTestCase {

    def dateConverterService


    protected void setUp() {
        super.setUp()
    }


    protected void tearDown() {
        super.tearDown()
    }


    void testInjection() {
        assertNotNull dateConverterService
    }


    void testGregorianToIslamicDateConversion() {
        assertEquals dateConverterService.convert("2012/01/01","en_US@calendar=gregorian","en_AR@calendar=islamic","yyyy/MM/dd","yyyy/MM/dd"), "1433/02/08"
        assertEquals dateConverterService.convert("01/01/2012","en_US@calendar=gregorian","en_AR@calendar=islamic","MM/dd/yyyy","yyyy/MM/dd"), "1433/02/08"
        assertEquals dateConverterService.convert("01/01/2012","en_US@calendar=gregorian","en_AR@calendar=islamic","MM/dd/yyyy","MM/dd/yyyy"), "02/08/1433"
    }

    void testIslamicToGregorianDateConversion() {
        assertEquals dateConverterService.convert("1433/02/08","en_AR@calendar=islamic","en_US@calendar=gregorian","yyyy/MM/dd","yyyy/MM/dd"), "2012/01/01"
        assertEquals dateConverterService.convert("02/08/1433","en_AR@calendar=islamic","en_US@calendar=gregorian","MM/dd/yyyy","yyyy/MM/dd"), "2012/01/01"
        assertEquals dateConverterService.convert("02/08/1433","en_AR@calendar=islamic","en_US@calendar=gregorian","MM/dd/yyyy","MM/dd/yyyy"), "01/01/2012"
    }

    void testConvertGregorianToDefaultCalendar(){
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,1),"yyyy/MM/dd"), "2012/01/01")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd/MM/yyyy"), "01/01/2012")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd-MMM-yyyy"), "01-Jan-2012")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd-MMMM-yyyy"), "01-January-2012")
    }
}
