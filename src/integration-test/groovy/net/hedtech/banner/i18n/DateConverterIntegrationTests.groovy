/*******************************************************************************
 Copyright 2009-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import grails.converters.JSON
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.i18n.utils.LocaleUtilities
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.context.WebApplicationContext

import static groovy.test.GroovyAssert.shouldFail
import java.sql.Timestamp
import java.text.SimpleDateFormat

import static org.junit.Assert.*
import grails.util.GrailsWebMockUtil
import org.springframework.web.context.request.RequestContextHolder

@Integration
class DateConverterIntegrationTests extends Assert  {


    def dateConverterService

    @Autowired
    WebApplicationContext ctx
    public static final String ARABIC_LOCALE = "ar"
    public static final String US_LOCALE = "en_US"
    public static final String PT_LOCALE = "pt"
    private final String ES = "es"
    private final String DEFAULT_DATETIME_FORMAT_EN ="MM/dd/yyyy HH:mm:ss"

    @Before
    public void setUp() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)

    }

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
    @AfterClass
    public static void cleanUp() {
        RequestContextHolder.resetRequestAttributes()
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


    @Test
    void testGetMinWeekdays(){
        String[] twoCharMinWeekdays = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
        LocaleContextHolder.setLocale(new Locale(US_LOCALE))
        String[] minWeekdays = dateConverterService.getMinWeekdays(US_LOCALE)
        assertEquals twoCharMinWeekdays, minWeekdays
    }


    @Test
    void testGetMinWeekdaysInPortuguese(){
        String[] threeCharMinWeekdays = ["dom", "seg", "ter", "qua", "qui", "sex", "sáb"]
        LocaleContextHolder.setLocale(new Locale(PT_LOCALE))
        String[] minWeekdays = dateConverterService.getMinWeekdays(PT_LOCALE)
        assertEquals threeCharMinWeekdays, minWeekdays
    }


    @Test
    void testGetMonthsInEnglishUSLocale(){
        String[] months = dateConverterService.getMonths(US_LOCALE)
        months.size()>0?assertTrue(true):assertFalse(true)

    }

    @Test
    void testGetShortMonthsInEnglishUSLocale(){
        String[] shortMonths = dateConverterService.getShortMonths(US_LOCALE)
        shortMonths.size()>0?assertTrue(true):assertFalse(true)

    }

    @Test
    void testGetWeekDaysInEnglishUSLocale(){
        String[] weekDays = dateConverterService.getWeekdays(US_LOCALE)
        weekDays.size()>0?assertTrue(true):assertFalse(true)

    }
    @Test
    void testGetShortWeekDaysInEnglishUSLocale(){
        String[] shortWeekdays = dateConverterService.getShortWeekdays(US_LOCALE)
        shortWeekdays.size()>0?assertTrue(true):assertFalse(true)

    }

    @Test
    void testGetAMPMSettingInEnglishUSLocale(){
        String[] amPmSettings = dateConverterService.getAmPmStrings(US_LOCALE)
        amPmSettings.size()>0?assertTrue(true):assertFalse(true)

    }

    @Test
    void testGetGregorianULocaleString(){
        String gregorianULocaleString = dateConverterService.getGregorianULocaleString()
        gregorianULocaleString!=null?assertTrue(true):assertFalse(true)

    }
    @Test
    void testGetGregorianFromDefaultCalendar(){
        Date gregorianULocaleString = dateConverterService.getGregorianFromDefaultCalendar(2012,10,20)
        gregorianULocaleString!=null?assertTrue(true):assertFalse(true)

    }
    @Test
    void testGetGregorianFromDefaultCalendarFirstDay(){
        Date gregorianULocaleString = dateConverterService.getGregorianFromDefaultCalendar(2012,10,-1)
        gregorianULocaleString!=null?assertTrue(true):assertFalse(true)

    }
    @Test
    void testGetGregorianFromDefaultCalendarLastDay(){
        Date gregorianULocaleString = dateConverterService.getGregorianFromDefaultCalendar(2012,10,-2)
        gregorianULocaleString!=null?assertTrue(true):assertFalse(true)

    }
    @Test
    void testConvertGregorianToDefaultCalendarWithOneArg(){
        def date = dateConverterService.convertGregorianToDefaultCalendar(new Date((2012 - 1900),0,01))
        assertEquals("01/01/2012",date)
    }

    @Test
    void testConvertDefaultCalendarToGregorianrWithOneArg(){
        assertEquals(dateConverterService.convertDefaultCalendarToGregorian(new Date((2012 - 1900),0,01)),"01/01/2012")
    }
    @Test
    void testConvertDefaultCalendarToGregorian(){
        assertEquals(dateConverterService.convertDefaultCalendarToGregorian(new Date((2012 - 1900),0,01),"yyyy/MM/dd"),"01/01/2012")
    }
    @Test
    void testParseDefaultCalendarToGregorian(){
        def date = dateConverterService.parseDefaultCalendarToGregorian("01012013")
        date!=null?assertTrue(true):assertFalse(true)
    }

    @Test
    void testAdjustCalendarDays(){
        ULocale toULocale = new ULocale("en_US@calendar=gregorian")
        Calendar toCalendar = Calendar.getInstance(toULocale);
        dateConverterService.adjustDate(toCalendar,"3")!=null ?assertTrue(true):assertFalse(true)

    }

    @Test
    void testAdjustCalendarDaysWithNoAdjustDate(){
        ULocale toULocale = new ULocale("en_US@calendar=gregorian")
        Calendar toCalendar = Calendar.getInstance(toULocale);
        dateConverterService.adjustDate(toCalendar,null)!=null ?assertTrue(true):assertFalse(true)

    }

    @Test
    void testConvertGregorianToDefaultCalendarWithTime(){
        assertEquals(dateConverterService.convertGregorianToDefaultCalendarWithTime(new Date((2012 - 1900),0,1),"yyyy/MM/dd"),"2012/01/01")
    }

    @Test
    void testConvertGregorianToDefaultCalendarWithTimeForArabicFormat(){
        LocaleUtilities.setLocale(ARABIC_LOCALE)
        assertEquals(dateConverterService.convertGregorianToDefaultCalendarWithTime(new Date((2012 - 1900),0,1,13,32,12),"dd/MMMM/yyyy hh:mm a","hh:mm a"),"06/صفر/1433 01:32 م")
    }

    @Test
    void testConvertDefaultCalendarToGregorianWithTime(){
        assertEquals(dateConverterService.convertDefaultCalendarToGregorianWithTime(new Date((2012 - 1900),0,1),"yyyy/MM/dd"),"2012/01/01")
    }
    @Test
    void testGetGregorianTranslationULocaleString(){
        assertEquals(dateConverterService.getGregorianTranslationULocaleString(),"en_US@calendar=gregorian")
    }

    @Test
    void testJSONDateMarshaller(){
        assertEquals(dateConverterService.JSONDateMarshaller("01/01/2012",null),"01/01/2012")
    }

    @Test
    void testJSONDateUnmarshaller(){
        assertEquals(dateConverterService.JSONDateUnmarshaller("01/01/2012",null),"01/01/2012")
    }


    @Test
    void testConvertGregorianToDefaultCalendarForSpanishLocale(){
        Locale oldLocale = LocaleContextHolder.getLocale()
        LocaleContextHolder.setLocale(new Locale(ES))
        DateAndDecimalUtils.properties(new Locale(ES))
        assertEquals( dateConverterService.convert(new Date((2012 - 1900),0,01),"en_US@calendar\\=gregorian","es@calendar\\=gregorian","dd-MMM-yyyy","dd-MMM-yyyy"), "01-Ene-2012")
        LocaleContextHolder.setLocale(oldLocale)

    }

    @Test
    void testConvertDefaultCalendarToGregorianSpanishLocale(){
        Locale oldLocale = LocaleContextHolder.getLocale()
        LocaleContextHolder.setLocale(new Locale(US_LOCALE))
        DateAndDecimalUtils.properties(new Locale(US_LOCALE))
        assertEquals( dateConverterService.convert(new Date((2012 - 1900),0,01),"es@calendar\\=gregorian","en_US@calendar\\=gregorian","dd-MMM-yyyy","dd-MMM-yyyy"), "01-Jan-2012")
        LocaleContextHolder.setLocale(oldLocale)

    }

    @Test
    void testConvertDefaultCalendarSpanishLocaleToGregorianWithStringFormat(){
        Locale oldLocale = LocaleContextHolder.getLocale()
        LocaleContextHolder.setLocale(new Locale(US_LOCALE))
        DateAndDecimalUtils.properties(new Locale(US_LOCALE))
        assertEquals( dateConverterService.convert("01/01/2012","es@calendar\\=gregorian","en_US@calendar\\=gregorian","dd/MM/yyyy","dd-MMM-yyyy"), "01-Jan-2012")
        LocaleContextHolder.setLocale(oldLocale)

    }


    @Test
    void testGetDefaultCalendarWithTime(){
        Date today = new Date(117,03,12,13,32,12)
        assertEquals(dateConverterService.getDefaultCalendarWithTime(today, DEFAULT_DATETIME_FORMAT_EN), "04/12/2017 13:32:12")
    }

    @Test
    void testAssertNullCheck() {
        shouldFail{
            dateConverterService.convert(null, "en_AR@calendar=islamic", "en_US@calendar=gregorian", "yyyy/MM/dd", "yyyy/MM/dd")
        }
        shouldFail{
            dateConverterService.convert("1433/02/08", null, "en_US@calendar=gregorian", "yyyy/MM/dd", "yyyy/MM/dd")
        }
        shouldFail{
            dateConverterService.convert("1433/02/08", "en_AR@calendar=islamic", null, "yyyy/MM/dd", "yyyy/MM/dd")
        }
        shouldFail{
            dateConverterService.convert("1433/02/08", "en_AR@calendar=islamic", "en_US@calendar=gregorian", null, "yyyy/MM/dd")
        }
        shouldFail{
            dateConverterService.convert("1433/02/08", "en_AR@calendar=islamic", "en_US@calendar=gregorian", "yyyy/MM/dd",null)
        }
    }

    @Test
    void testJSONDateMarshallerObjectWithValidDate(){
        JSONObject data = new JSONObject(startDate: "01/01/2010")
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(data, dateFields)
        assertEquals dateParts.startDate, "01/01/2010"
    }

    @Test
    void testJSONDateMarshallerObjectWithInvalidKey(){
        JSONObject data = new JSONObject(null: "01/01/2010")
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(data, dateFields)
        assertEquals dateParts.startDate, null
    }

    @Test
    void testJSONDateMarshallerObjectWithNonStringValue(){
        JSONObject data = new JSONObject(startDate: new Date("01/01/2010"))
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(data, dateFields)
        assertEquals dateParts.startDate, new Date("01/01/2010")
    }

    @Test
    void testJSONDateMarshallerObjectWithNonStringValueInvalidKey(){
        JSONObject data = new JSONObject(null: new Date("01/01/2010"))
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(data, dateFields)
        assertEquals dateParts.startDate, null
    }

    @Test
    void testJSONDateMarshallerObjectWithNullDate(){
        JSONObject data = new JSONObject(startDate: null)
        def dateFields = ["startDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(data, dateFields)
        assertEquals dateParts.startDate, null
    }

    @Test
    void testJSONDateMarshallerArrayWithValidDate(){
        JSONArray newArray = new JSONArray()
        newArray.put(0, "01/01/2010" )
        newArray.put(1, "02/01/2010")
        def dateFields = ["startDate", "endDate"]
        def dateParts = dateConverterService.JSONDateMarshaller(newArray, null)
        assertEquals dateParts[0], "01/01/2010"
        assertEquals dateParts[1], "02/01/2010"
    }

    private String formatDate(date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.format(date);
    }

}
class MockDomain {}
