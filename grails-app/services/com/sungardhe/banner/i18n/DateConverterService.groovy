/** *******************************************************************************
 Copyright 2009-2012 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of
 SunGard Higher Education and its subsidiaries. Any use of this software is limited
 solely to SunGard Higher Education licensees, and is further subject to the terms
 and conditions of one or more written license agreements between SunGard Higher
 Education and the licensee in question. SunGard is either a registered trademark or
 trademark of SunGard Data Systems in the U.S.A. and/or other regions and/or countries.
 Banner and Luminis are either registered trademarks or trademarks of SunGard Higher
 Education in the U.S.A. and/or other regions and/or countries.
 ********************************************************************************* */

package com.sungardhe.banner.i18n

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * This utility class is used to convert Calendars supported by ICU4J for the UI.
 */
class DateConverterService {

    static transactional = false
    Logger logger = Logger.getLogger(this.getClass())

    def localizerService = { mapToLocalize ->
        new ValidationTagLib().message(mapToLocalize)
    }


    String convert(String fromDateString, String fromULocaleString = null, String toULocaleString = null, String fromDateFormatString = null, String toDateFormatString = null, String adjustDays = null) {

        assert fromDateString

        String toDateString = ""


        try {
            if (!fromDateFormatString) fromDateFormatString = getDefaultFromDateFormatString()
            if (!toDateFormatString) toDateFormatString = getDefaultToDateFormatString()
            if (!fromULocaleString) fromULocaleString = getDefaultFromULocaleString()
            if (!toULocaleString) toULocaleString = getDefaultToULocaleString()

            //String fromULocaleString = fromLocaleString + "@calendar=" + fromCalendarString
            ULocale fromULocale = new ULocale(fromULocaleString)
            Calendar fromCalendar = Calendar.getInstance(fromULocale);
            com.ibm.icu.text.DateFormat fromDateFormat = fromCalendar.handleGetDateFormat(fromDateFormatString, fromULocale)
            Date fromDate = fromDateFormat.parse(fromDateString)

            //String toULocaleString = toLocaleString + "@calendar=" + toCalendarString
            ULocale toULocale = new ULocale(toULocaleString)
            Calendar toCalendar = Calendar.getInstance(toULocale);
            toCalendar.setTime(fromDate)

            if (adjustDays) toCalendar = adjustDate(toCalendar, adjustDays)

            com.ibm.icu.text.DateFormat toDateFormat = toCalendar.handleGetDateFormat(toDateFormatString, toULocale)
            toDateString = toDateFormat.format(toCalendar)

        } catch (Exception exception) {
            String errorString = "Unable to perform conversion --  fromDateString: " + fromDateString + ", fromULocaleString: " + fromULocaleString + ", toULocaleString: " + toULocaleString + ", fromDateFormatString: " + fromDateFormatString + ", toDateFormatString: " + toDateFormatString
            logger.error errorString, exception
            return "error"
        }
    }


    private String getDefaultToULocaleString() {
        String property = localizerService(code: "default.date.conversion.to.ULocale")
        if (!property) property = "en_US@calendar=gregorian"
        return property
    }


    private String getDefaultFromULocaleString() {
        String property = localizerService(code: "default.date.conversion.from.ULocale")
        if (!property) property = "en_US@calendar=gregorian"
        return property
    }


    private String getDefaultToDateFormatString() {
        String property = localizerService(code: "default.date.conversion.to.format")
        if (!property) property = localizerService(code: "default.date.format")
        if (!property) property = "MM/dd/yyyy"

        return property

    }


    private String getDefaultFromDateFormatString() {
        String property = localizerService(code: "default.date.conversion.from.format")
        if (!property) property = localizerService(code: "default.date.format")
        if (!property) property = "MM/dd/yyyy"

        return property
    }


    private Calendar adjustDate(Calendar calendar, String adjustDays) {

        if (adjustDays) calendar.add(Calendar.DATE, adjustDays.toInteger())

        return calendar
    }

}
