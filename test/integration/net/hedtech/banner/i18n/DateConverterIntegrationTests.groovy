/*******************************************************************************
 Copyright 2009-2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.converters.JSON
import net.hedtech.banner.i18n.utils.LocaleUtilities
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.After
import org.junit.Test
import org.springframework.context.i18n.LocaleContextHolder

import java.sql.Timestamp
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class DateConverterIntegrationTests   {

    def dateConverterService

    public static final String ARABIC_LOCALE = "ar"
    public static final String US_LOCALE = "en_US"

    @After
    public void tearDown() {
        println "************** Locale values for different Scopes *************"
        println "RequestContextAttributes => " + LocaleUtilities.getLocales()?.collect{Locale locale -> locale}
        println "JVM Locale => " + Locale.default
        println "Current Thread Locale => " + LocaleContextHolder.locale
        println "Grails Web Request => " + GrailsWebRequest.lookup().getLocale()
        println "i18n localizerService resolved locale => " + dateConverterService.localizerService(code: "default.calendar.gregorian.translation")
        println "i18n MessageHelper.message resolved locale=> " + MessageHelper.message(code: "default.calendar.gregorian.translation")
        println ("***************************************************")
    }


    @Test
    void testInjection() {
        assertNotNull dateConverterService

    }

    @Test
    void testGregorianToIslamicDateConversion() {
        assertEquals dateConverterService.convert("2012/01/01","en_US@calendar=gregorian","en_AR@calendar=islamic","yyyy/MM/dd","yyyy/MM/dd"), "1433/02/08"
        assertEquals dateConverterService.convert("01/01/2012","en_US@calendar=gregorian","en_AR@calendar=islamic","MM/dd/yyyy","yyyy/MM/dd"), "1433/02/08"
        assertEquals dateConverterService.convert("01/01/2012","en_US@calendar=gregorian","en_AR@calendar=islamic","MM/dd/yyyy","MM/dd/yyyy"), "02/08/1433"
    }

    @Test
    void testIslamicToGregorianDateConversion() {
        assertEquals dateConverterService.convert("1433/02/08","en_AR@calendar=islamic","en_US@calendar=gregorian","yyyy/MM/dd","yyyy/MM/dd"), "2012/01/01"
        assertEquals dateConverterService.convert("02/08/1433","en_AR@calendar=islamic","en_US@calendar=gregorian","MM/dd/yyyy","yyyy/MM/dd"), "2012/01/01"
        assertEquals dateConverterService.convert("02/08/1433","en_AR@calendar=islamic","en_US@calendar=gregorian","MM/dd/yyyy","MM/dd/yyyy"), "01/01/2012"
    }

    @Test
    void testConvertGregorianToDefaultCalendar(){
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,1),"yyyy/MM/dd"), "2012/01/01")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd/MM/yyyy"), "01/01/2012")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd-MMM-yyyy"), "01-Jan-2012")
        assertEquals( dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01),"dd-MMMM-yyyy"), "01-January-2012")
    }

    @Test
    void testformatDateInObjectsToDefaultCalendarDate(){
        def utilDateArgument = new Date((2012 - 1900), 0, 1)
        def defaultCalendarStringDate = "01/01/2012"
        def defaultCalendarStringDate2 = "01/01/2013"
        def defaultCalendarStringDate3 = "02/01/2012"
        def stringDateArg = "01/01/2012"
        def markedDateFields = ["startDate"]

        def formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", utilDateArgument, markedDateFields)
        assertEquals(formattedDate, defaultCalendarStringDate)

        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", stringDateArg, markedDateFields)
        assertEquals(formattedDate, defaultCalendarStringDate)

        def dateList = [defaultCalendarStringDate, "01/01/2013"]
        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", dateList, markedDateFields)
        assertEquals(formattedDate, dateList)

        def dateMap = [startDate: defaultCalendarStringDate, endDate: "01/01/2013"]
        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", dateMap, markedDateFields)
        assertEquals(formattedDate, dateMap)

        def timestampArgument = new Timestamp(utilDateArgument.time)
        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", timestampArgument, markedDateFields)
        assertEquals(formattedDate, defaultCalendarStringDate)


        def sqlDateArg = new java.sql.Date((2012 - 1900), 0, 1)
        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", sqlDateArg, markedDateFields)
        assertEquals(formattedDate, defaultCalendarStringDate)

        final JSON json = new grails.converters.JSON(startDate: utilDateArgument)
        formattedDate = dateConverterService.formatDateInObjectsToDefaultCalendar("startDate", json, markedDateFields)
        assertEquals(formattedDate, json )
        assertEquals(json.target.startDate, defaultCalendarStringDate)

        MockDomain mo = new MockDomain()
        mo.metaClass."startDate"= defaultCalendarStringDate
        mo.metaClass."prop2"= Boolean.FALSE
        mo.metaClass."endDate"= new Date((2012 - 1900),1,1)
        def map = dateConverterService.formatDateInObjectsToDefaultCalendar("mapping", mo, ["startDate", "endDate"])
        assertEquals map.startDate, defaultCalendarStringDate
        assertEquals map.prop2, Boolean.FALSE
        assertEquals map.endDate, defaultCalendarStringDate3
    }

    @Test
    void testExtractDatePartsInEnglishLocale(){
        LocaleUtilities.setLocale(US_LOCALE)
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(new GregorianCalendar(2000,1,3).time)
        assertEquals dateParts["year"], "2000"
        assertEquals dateParts["month"], "02"
        assertEquals dateParts["day"], "03"
    }

    @Test
    void testExtractDatePartsInArabicLocale(){
        LocaleUtilities.setLocale(ARABIC_LOCALE)
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(new GregorianCalendar(2005,1,3).time)
        assertEquals dateParts["year"], "1425"
        assertEquals dateParts["month"], "12"
        assertEquals dateParts["day"], "23"
    }

    @Test
    void testExtractDatePartsInFrenchLocale(){
        LocaleUtilities.setLocale(Locale.CANADA_FRENCH)
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(new GregorianCalendar(2000,1,3).time)
        assertEquals dateParts["year"], "2000"
        assertEquals dateParts["month"], "02"
        assertEquals dateParts["day"], "03"
    }

    @Test
    void testExtractDatePartsWithNullDate(){
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(null)
        assertEquals dateParts.size(), 0
    }

    @Test
    void testGetStartDateInGregorianCalendarFromEnglishLocale(){
        LocaleUtilities.setLocale(US_LOCALE)
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010))
        assertEquals gregorianStartDateForYear, "2010/01/01"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010,3))
        assertEquals gregorianStartDateForYearAndMonth, "2010/04/01"
    }

    @Test
    void testGetStartDateInGregorianCalendarFromArabicLocale(){
        LocaleUtilities.setLocale(ARABIC_LOCALE)
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(1433))
        assertEquals gregorianStartDateForYear, "2011/11/27"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(1433,2))
        assertEquals gregorianStartDateForYearAndMonth, "2012/01/25"
    }

    @Test
    void testGetStartDateInGregorianCalendarFromFrenchLocale(){
        LocaleUtilities.setLocale(Locale.CANADA_FRENCH)
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010))
        assertEquals gregorianStartDateForYear, "2010/01/01"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010,3))
        assertEquals gregorianStartDateForYearAndMonth, "2010/04/01"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromEnglishLocale(){
        LocaleUtilities.setLocale(US_LOCALE)
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(2010))
        assertEquals endDateFromYear, "2010/12/31"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(2012,1))
        assertEquals endDateFromYearAndMonth, "2012/02/29"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromArabicLocale(){
        LocaleUtilities.setLocale(ARABIC_LOCALE)
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(1433))
        assertEquals endDateFromYear, "2012/11/14"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(1433,2))
        assertEquals endDateFromYearAndMonth, "2012/02/23"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromFrenchLocale(){
        LocaleUtilities.setLocale(Locale.CANADA_FRENCH)
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(2010))
        assertEquals endDateFromYear, "2010/12/31"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(2012,1))
        assertEquals endDateFromYearAndMonth, "2012/02/29"
    }

    @Test
    void testGetMonthNameFromCodeInEnglishLocale(){
        LocaleUtilities.setLocale(US_LOCALE)
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode()
        assertEquals monthNamesWithCode.get(2), "March"
    }

    @Test
    void testGetMonthNameFromCodeInArabicLocale(){
        LocaleUtilities.setLocale(ARABIC_LOCALE)
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode()
        String uniCodeForThirdIslamicMonth = "\u0631"+"\u0628"+"\u064a"+"\u0639 "+"\u0627"+"\u0644"+"\u0623"+"\u0648"+"\u0644"
        assertEquals monthNamesWithCode.get(2), uniCodeForThirdIslamicMonth
    }

    @Test
    void testGetGregorianMonthNameInArabicLocale(){
        LocaleUtilities.setLocale(US_LOCALE)
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode(ARABIC_LOCALE)
        String uniCodeForFirstGregorianMonthInArabicLocale = "\u064a"+"\u0646"+"\u0627"+"\u064a"+"\u0631"
        assertEquals monthNamesWithCode.get(0), uniCodeForFirstGregorianMonthInArabicLocale
    }

    @Test
    void testGetMonthNameFromCodeInFrenchLocale(){
        LocaleUtilities.setLocale(Locale.CANADA_FRENCH)
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode()
        String uniCodeForSecondMonthInFrench = "\u0066"+"\u00e9"+"\u0076"+"\u0072"+"\u0069"+"\u0065"+"\u0072"
        assertEquals monthNamesWithCode.get(1), uniCodeForSecondMonthInFrench
    }

    @Test
    void testJSONDateUnmarshallerWithValidDate(){
        JSONObject data = new JSONObject(startDate: "01/01/2010")
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateUnmarshaller(data, dateFields)
        assertEquals dateParts.startDate, "01/01/2010"
    }


    @Test
    void testJSONDateUnmarshallerWithEmptyString(){
        JSONObject data = new JSONObject(startDate: "")
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateUnmarshaller(data, dateFields)
        assertEquals dateParts.startDate, ""
    }

    @Test
    void testJSONDateUnmarshallerWithnull(){
        JSONObject data = new JSONObject(startDate: null)
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateUnmarshaller(data, dateFields)
        assertEquals dateParts.startDate, null
    }


    private String formatDate(date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.format(date);
    }

}
class MockDomain {}

