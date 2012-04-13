/*********************************************************************************
 Copyright 2009-2012 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of 
 SunGard Higher Education and its subsidiaries. Any use of this software is limited 
 solely to SunGard Higher Education licensees, and is further subject to the terms 
 and conditions of one or more written license agreements between SunGard Higher 
 Education and the licensee in question. SunGard is either a registered trademark or
 trademark of SunGard Data Systems in the U.S.A. and/or other regions and/or countries.
 Banner and Luminis are either registered trademarks or trademarks of SunGard Higher 
 Education in the U.S.A. and/or other regions and/or countries.
 **********************************************************************************/
package com.sungardhe.banner.i18n

import java.text.DateFormatSymbols
import java.text.DecimalFormatSymbols

/**
 * This utility class is used to generate the default date and decimal formats used by
 * the UI.
 */
class DateAndDecimalUtils {

    private static propertiesMap = [:]

    static properties = { locale ->

        if (propertiesMap[locale]) {
            return propertiesMap[ locale ]
        }
        else {

            def dateFormatSymbols = new DateFormatSymbols(locale)
            def decimalFormatSymbols = new DecimalFormatSymbols(locale)

            def propertyMap = [
                    "js.datepicker.monthNames":         "${dateFormatSymbols.months[Calendar.JANUARY]}, ${dateFormatSymbols.months[Calendar.FEBRUARY]}, ${dateFormatSymbols.months[Calendar.MARCH]}, ${dateFormatSymbols.months[Calendar.APRIL]}, ${dateFormatSymbols.months[Calendar.MAY]}, ${dateFormatSymbols.months[Calendar.JUNE]}, ${dateFormatSymbols.months[Calendar.JULY]}, ${dateFormatSymbols.months[Calendar.AUGUST]}, ${dateFormatSymbols.months[Calendar.SEPTEMBER]}, ${dateFormatSymbols.months[Calendar.OCTOBER]}, ${dateFormatSymbols.months[Calendar.NOVEMBER]}, ${dateFormatSymbols.months[Calendar.DECEMBER]}",
                    "js.datepicker.monthNamesShort":    "${dateFormatSymbols.shortMonths[Calendar.JANUARY]}, ${dateFormatSymbols.shortMonths[Calendar.FEBRUARY]}, ${dateFormatSymbols.shortMonths[Calendar.MARCH]}, ${dateFormatSymbols.shortMonths[Calendar.APRIL]}, ${dateFormatSymbols.shortMonths[Calendar.MAY]}, ${dateFormatSymbols.shortMonths[Calendar.JUNE]}, ${dateFormatSymbols.shortMonths[Calendar.JULY]}, ${dateFormatSymbols.shortMonths[Calendar.AUGUST]}, ${dateFormatSymbols.shortMonths[Calendar.SEPTEMBER]}, ${dateFormatSymbols.shortMonths[Calendar.OCTOBER]}, ${dateFormatSymbols.shortMonths[Calendar.NOVEMBER]}, ${dateFormatSymbols.shortMonths[Calendar.DECEMBER]}",
                    "js.datepicker.dayNames":           "${dateFormatSymbols.weekdays[Calendar.SUNDAY]}, ${dateFormatSymbols.weekdays[Calendar.MONDAY]}, ${dateFormatSymbols.weekdays[Calendar.TUESDAY]}, ${dateFormatSymbols.weekdays[Calendar.WEDNESDAY]}, ${dateFormatSymbols.weekdays[Calendar.THURSDAY]}, ${dateFormatSymbols.weekdays[Calendar.FRIDAY]}, ${dateFormatSymbols.weekdays[Calendar.SATURDAY]}",
                    "js.datepicker.dayNamesShort":      "${dateFormatSymbols.shortWeekdays[Calendar.SUNDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.MONDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.TUESDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.WEDNESDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.THURSDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.FRIDAY]}, ${dateFormatSymbols.shortWeekdays[Calendar.SATURDAY]}",

                    "js.months.january":                "${dateFormatSymbols.months[Calendar.JANUARY]}",
                    "js.months.february":               "${dateFormatSymbols.months[Calendar.FEBRUARY]}",
                    "js.months.march":                  "${dateFormatSymbols.months[Calendar.MARCH]}",
                    "js.months.april":                  "${dateFormatSymbols.months[Calendar.APRIL]}",
                    "js.months.may":                    "${dateFormatSymbols.months[Calendar.MAY]}",
                    "js.months.june":                   "${dateFormatSymbols.months[Calendar.JUNE]}",
                    "js.months.july":                   "${dateFormatSymbols.months[Calendar.JULY]}",
                    "js.months.august":                 "${dateFormatSymbols.months[Calendar.AUGUST]}",
                    "js.months.september":              "${dateFormatSymbols.months[Calendar.SEPTEMBER]}",
                    "js.months.october":                "${dateFormatSymbols.months[Calendar.OCTOBER]}",
                    "js.months.november":               "${dateFormatSymbols.months[Calendar.NOVEMBER]}",
                    "js.months.december":               "${dateFormatSymbols.months[Calendar.DECEMBER]}",

                    "js.number.decimal": "${decimalFormatSymbols.decimalSeparator}",
                    "js.number.digit": "${decimalFormatSymbols.digit}",
            ]

            propertiesMap[locale] = propertyMap

            return propertyMap
        }
    }
}
