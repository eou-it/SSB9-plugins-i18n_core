/*******************************************************************************
 Copyright 2009-2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.converters.JSON
import net.hedtech.banner.i18n.utils.LocaleUtilities
import org.junit.Test

import java.sql.Timestamp
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class DateConverterIntegrationTests   {

    def dateConverterService


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
        LocaleUtilities.setLocale("en_US")
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(new GregorianCalendar(2000,1,3).time)
        assertEquals dateParts["year"], "2000"
        assertEquals dateParts["month"], "02"
        assertEquals dateParts["day"], "03"
    }

    @Test
    void testExtractDatePartsInArabicLocale(){
        LocaleUtilities.setLocale("ar")
        Map dateParts = dateConverterService.convertGregorianToDefaultCalendarAndExtractDateParts(new GregorianCalendar(2005,1,3).time)
        assertEquals dateParts["year"], "1425"
        assertEquals dateParts["month"], "12"
        assertEquals dateParts["day"], "23"
    }

    @Test
    void testExtractDatePartsInFrenchLocale(){
        LocaleUtilities.setLocale("fr_CA")
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
        LocaleUtilities.setLocale("en_US")
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010))
        assertEquals gregorianStartDateForYear, "2010/01/01"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010,3))
        assertEquals gregorianStartDateForYearAndMonth, "2010/04/01"
    }

    @Test
    void testGetStartDateInGregorianCalendarFromArabicLocale(){
        LocaleUtilities.setLocale("ar")
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(1433))
        assertEquals gregorianStartDateForYear, "2011/11/27"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(1433,2))
        assertEquals gregorianStartDateForYearAndMonth, "2012/01/25"
    }

    @Test
    void testGetStartDateInGregorianCalendarFromFrenchLocale(){
        LocaleUtilities.setLocale("fr_CA")
        String gregorianStartDateForYear = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010))
        assertEquals gregorianStartDateForYear, "2010/01/01"

        String gregorianStartDateForYearAndMonth = formatDate(dateConverterService.getStartDateInGregorianCalendar(2010,3))
        assertEquals gregorianStartDateForYearAndMonth, "2010/04/01"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromEnglishLocale(){
        LocaleUtilities.setLocale("en_US")
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(2010))
        assertEquals endDateFromYear, "2010/12/31"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(2012,1))
        assertEquals endDateFromYearAndMonth, "2012/02/29"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromArabicLocale(){
        LocaleUtilities.setLocale("ar")
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(1433))
        assertEquals endDateFromYear, "2012/11/14"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(1433,2))
        assertEquals endDateFromYearAndMonth, "2012/02/23"
    }

    @Test
    void testGetEndDateInGregorianCalendarFromFrenchLocale(){
        LocaleUtilities.setLocale("fr_CA")
        String endDateFromYear = formatDate(dateConverterService.getEndDateInGregorianCalendar(2010))
        assertEquals endDateFromYear, "2010/12/31"

        String endDateFromYearAndMonth = formatDate(dateConverterService.getEndDateInGregorianCalendar(2012,1))
        assertEquals endDateFromYearAndMonth, "2012/02/29"
    }

    @Test
    void testGetMonthNameFromCodeInEnglishLocale(){
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode("en_US")
        assertEquals monthNamesWithCode.get(2), "March"
    }

    @Test
    void testGetMonthNameFromCodeInArabicLocale(){
        Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode("ar")
        String uniCodeForThirdIslamicMonth = "\u0645"+"\u0627"+"\u0631"+"\u0633"
        assertEquals monthNamesWithCode.get(2), uniCodeForThirdIslamicMonth
    }

    @Test
    void testGetMonthNameFromCodeInFrenchLocale(){
     Map monthNamesWithCode = dateConverterService.getMonthNamesWithCode("fr_CA")
     String uniCodeForSecondMonthInFrench = "\u0066"+"\u00e9"+"\u0076"+"\u0072"+"\u0069"+"\u0065"+"\u0072"
     assertEquals monthNamesWithCode.get(1), uniCodeForSecondMonthInFrench
    }

    private String formatDate(date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.format(date);
    }

}
class MockDomain {}

