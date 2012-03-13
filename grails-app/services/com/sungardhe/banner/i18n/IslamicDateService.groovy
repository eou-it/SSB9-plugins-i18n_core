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

import com.ibm.icu.util.IslamicCalendar
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * This utility class is used to convert Gregorian Calendar to Islamic Calendar and vice versa.
 * for the UI.
 */
class IslamicDateService {

    static transactional = false

    def localizerService = { mapToLocalize ->
        new ValidationTagLib().message(mapToLocalize)
    }


    String getIslamicDateString(String gregorianDateString, String fromDateFormatString = null, String toDateFormatString = null, String locale = null) {

        assert gregorianDateString
        return convertIslamicGregorianDateString(gregorianDateString, fromDateFormatString, toDateFormatString, locale, true)
    }


    String getGregorianDateString(String islamicDateString, String fromDateFormatString = null, String toDateFormatString = null, String locale = null) {

        assert islamicDateString
        return convertIslamicGregorianDateString(islamicDateString, fromDateFormatString, toDateFormatString, locale, false)
    }


    private String convertIslamicGregorianDateString(String fromDateString, String fromDateFormatString = null, String toDateFormatString = null, String localeString = null, boolean isGregorianDate = true) {

        assert fromDateString

        String toDateString

        // Use ValidationTagLib
        if (!fromDateFormatString) fromDateFormatString = isGregorianDate ? localizerService(code: "default.islamicDate.conversion.gregorian.format") : localizerService(code: "default.islamicDate.conversion.islamic.format")
        if (!toDateFormatString) toDateFormatString = isGregorianDate ? localizerService(code: "default.islamicDate.conversion.islamic.format") : localizerService(code: "default.islamicDate.conversion.gregorian.format")

        if (isGregorianDate) {
            SimpleDateFormat fromSimpleDateFormat = new SimpleDateFormat(fromDateFormatString)
            Date fromDate = fromSimpleDateFormat.parse(fromDateString)
            IslamicCalendar islamicCalendar = getIslamicDate(fromDate)

            if (!localeString) localeString = localizerService(code: "default.islamicDate.conversion.islamic.locale")
            Locale locale = localeString ? new Locale(localeString) : Locale.getDefault()

            com.ibm.icu.text.DateFormat d = islamicCalendar.handleGetDateFormat(toDateFormatString, locale)
            toDateString = d.format(islamicCalendar)

        } else {
            Date toDate = getGregorianDate(fromDateString, fromDateFormatString)
            SimpleDateFormat toSimpleDateFormat = new SimpleDateFormat(toDateFormatString)
            toDateString = toSimpleDateFormat.format(toDate)
        }
        return toDateString
    }

    private Calendar adjustDate(Calendar calendar, String adjustDays, String adjustMonths, String adjustYears) {

        if (adjustDays) calendar.add(Calendar.DATE, adjustDays.toInteger())
        if (adjustMonths) calendar.add(Calendar.MONTH, adjustMonths.toInteger())
        if (adjustYears) calendar.add(Calendar.YEAR, adjustYears.toInteger())

        return calendar
    }

    private IslamicCalendar adjustDate(com.ibm.icu.util.IslamicCalendar calendar, String adjustDays, String adjustMonths, String adjustYears) {

        if (adjustDays) calendar.add(Calendar.DATE, adjustDays.toInteger())
        if (adjustMonths) calendar.add(Calendar.MONTH, adjustMonths.toInteger())
        if (adjustYears) calendar.add(Calendar.YEAR, adjustYears.toInteger())

        return calendar
    }


    private IslamicCalendar getIslamicDate(Date date) {

        assert date

        com.ibm.icu.util.IslamicCalendar islamicCalendar = new com.ibm.icu.util.IslamicCalendar(date)

        String islamicAdjust = localizerService(code: "default.islamicDate.conversion.islamic.adjust")
        if (islamicAdjust.toBoolean()) islamicCalendar = adjustDate(islamicCalendar, localizerService(code:"default.islamicDate.conversion.islamic.adjust.days"), localizerService(code:"default.islamicDate.conversion.islamic.adjust.months"), localizerService(code:"default.islamicDate.conversion.islamic.adjust.years"))

        return islamicCalendar

    }


    private Date getGregorianDate(String fromDateString, String fromDateFormatString) {

        assert fromDateString
        assert fromDateFormatString

        IslamicCalendar islamicCalendar = new com.ibm.icu.util.IslamicCalendar()
        com.ibm.icu.text.DateFormat d = islamicCalendar.handleGetDateFormat(fromDateFormatString, new Locale("en"))
        Date date = d.parse(fromDateString)

        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        String gregorianAdjust = localizerService(code:"default.islamicDate.conversion.gregorian.adjust")
        if (gregorianAdjust.toBoolean()) calendar = adjustDate(calendar, localizerService(code:"default.islamicDate.conversion.gregorian.adjust.days"), localizerService(code:"default.islamicDate.conversion.gregorian.adjust.months"), localizerService(code:"default.islamicDate.conversion.gregorian.adjust.years"))

        date = calendar.getTime()
        return date

    }
}
