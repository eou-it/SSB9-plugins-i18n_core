/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.i18n.utils

import grails.testing.mixin.integration.Integration
import net.hedtech.banner.i18n.DateAndDecimalUtils
import net.hedtech.banner.i18n.DateConverterService
import net.hedtech.banner.i18n.MessageHelper
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import static org.junit.Assert.*

/**
 * Integration test cases for DateAndDecimalUtils.
 */
@Integration
class DateAndDecimalUtilsIntegrationTests {

    private final String EN = "en"
    private final String US = "US"
    private final String FR = "fr"
    private final String CA = "CA"
    private final String ES = "es"
    private final String AR = "ar"
    private final String SA = "SA"

    @Before
    public void setUp() {
        LocaleContextHolder.resetLocaleContext()
    }

    @After
    public void tearDown() {
        LocaleContextHolder.resetLocaleContext()
    }

    @Test
    public void testProperties() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def properties = DateAndDecimalUtils.properties(RequestContextUtils.getLocale(request))
        assertNotNull(properties)

        def propertiesMapTest = DateAndDecimalUtils.properties(RequestContextUtils.getLocale(request))
        assertNotNull(propertiesMapTest)
    }

    @Test
    public void testGetListOfCalendars() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def list = DateAndDecimalUtils.getListOfCalendars(RequestContextUtils.getLocale(request))
        assertFalse(list.isEmpty())
    }

    @Test
    public void testConvertToCommaDelimited() {
        GrailsMockHttpServletRequest request = new GrailsMockHttpServletRequest()
        def dateConverterService = new DateConverterService()
        def months = DateAndDecimalUtils.convertToCommaDelimited(dateConverterService.getMonths(RequestContextUtils.getLocale(request).toString()))
        assertNotNull(months)
    }

    @Test
    public void testFormatDate() {
        def formatDate = DateAndDecimalUtils.formatDate(new Date())
        assertNotNull(formatDate)
    }


    @Test
    void testFormatForDifferentLocale() {
        LocaleContextHolder.setLocale(new Locale(EN, US))
        def dateFormat = DateAndDecimalUtils.formatDate("10/01/2010")
        assertEquals("MM/dd/yyyy", dateFormat)
        LocaleContextHolder.resetLocaleContext()

        LocaleContextHolder.setLocale(new Locale(FR, CA))
        dateFormat = DateAndDecimalUtils.formatDate("10/01/2010")
        assertEquals("yyyy/MM/dd", dateFormat)
        LocaleContextHolder.resetLocaleContext()

        LocaleContextHolder.setLocale(new Locale(ES))
        dateFormat = DateAndDecimalUtils.formatDate("10/01/2010")
        assertEquals("dd/MM/yyyy", dateFormat)
        LocaleContextHolder.resetLocaleContext()

        LocaleContextHolder.setLocale(new Locale(AR, SA))
        dateFormat = DateAndDecimalUtils.formatDate("10/01/2010")
        assertEquals("dd/MMMM/yyyy", dateFormat)
        LocaleContextHolder.resetLocaleContext()
    }


    @Test
    void testParseDate() {
        ParseException pe
        LocaleContextHolder.setLocale(new Locale("EN"))
        def parseValue
        try {
            parseValue = DateAndDecimalUtils.parseDate("01/10/2010")
        } catch (ParseException e) {
            e.printStackTrace()
        }
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy")
        Date parsedDate = dateFormat.parse('01/10/2010')
        assertEquals (parsedDate, parseValue)
    }


    @Test
    void testParseDateException() {
        ParseException pe
        def parseValue
        try {
            parseValue = DateAndDecimalUtils.parseDate("31/31/2010")
        } catch (ParseException e) {
            pe = e
        }
        assertNull (parseValue)
        assertTrue (pe instanceof ParseException)
    }

    @Test
    void testConvertToCommaDelimitedWithEmptyList() {
        String[] list = []
        assertEquals(DateAndDecimalUtils.convertToCommaDelimited(list), "")
    }


    @Test
    void testConvertToCommaDelimitedWithNull() {
        String[] list = null
        assertEquals(DateAndDecimalUtils.convertToCommaDelimited(list), "")
    }

    @Test
    void testConvertToCommaDelimitedWithValidList() {
        String[] list = ["TEST","APP"]
        assertEquals(DateAndDecimalUtils.convertToCommaDelimited(list), "TEST,APP")
    }

}
