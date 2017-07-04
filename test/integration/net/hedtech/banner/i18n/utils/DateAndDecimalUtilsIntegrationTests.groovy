/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n.utils

import net.hedtech.banner.i18n.DateAndDecimalUtils
import net.hedtech.banner.i18n.DateConverterService
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

import java.text.ParseException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull

/**
 * Integration test cases for DateAndDecimalUtils.
 */
class DateAndDecimalUtilsIntegrationTests {

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
    void testFormatManualDate() {
        LocaleContextHolder.setLocale(new Locale("EN"))
        assertEquals(DateAndDecimalUtils.formatDate("dd/MM/yyyy"), "MM/dd/yyyy")
    }

    @Test
    void testParseDate() {
        ParseException pe
        LocaleContextHolder.setLocale(new Locale("EN"))
        try {
            DateAndDecimalUtils.parseDate("12/12/2012")
        } catch (ParseException e) {
            pe = e
        }
        assertEquals(null, pe)
    }

    @Test
    void testConvertToCommaDelimitedWithEmptyList() {
        String[] list
        assertEquals(DateAndDecimalUtils.convertToCommaDelimited(list), "")
    }

}
