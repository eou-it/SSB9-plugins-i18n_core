/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.i18n

import grails.converters.JSON
import org.junit.Test
import org.spockframework.mock.runtime.MockObject

import java.sql.Timestamp

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

}
class MockDomain {}

