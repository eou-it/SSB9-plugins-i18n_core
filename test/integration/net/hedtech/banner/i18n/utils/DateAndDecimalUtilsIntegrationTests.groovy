/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n.utils

import net.hedtech.banner.i18n.DateAndDecimalUtils
import net.hedtech.banner.i18n.DateConverterService
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.Test
import org.springframework.web.servlet.support.RequestContextUtils

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull

/**
 * Integraton test cases for DateAndDecimalUtils.
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

}
