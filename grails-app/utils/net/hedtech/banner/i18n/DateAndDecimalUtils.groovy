/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/ 
package net.hedtech.banner.i18n

import java.text.DateFormatSymbols
import java.text.DecimalFormatSymbols
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.context.i18n.LocaleContextHolder as LCH
import java.text.ParseException

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

            List calendars = getListOfCalendars(locale)
            addCalendarProps(calendars, locale, propertyMap)

            propertiesMap[locale] = propertyMap

            return propertyMap
        }
    }

    public static List getListOfCalendars(def locale) {
        def messageSource =  ApplicationHolder.application.mainContext.getBean('messageSource')

        List calendars = new ArrayList();

        int i = 1
        while (true) {
            try {
                calendars.add(messageSource.getMessage("default.calendar" + i , null, locale));
            }
            catch (Exception e) {
                break;
            }
            i++;
        }
        return calendars;
    }

     public static String convertToCommaDelimited(String[] list) {
        StringBuilder sb = new StringBuilder();
        if(list != null) {
            for (int i = 0; i < list.length; i++) {
                if(!list[i].trim().equals("")) {
                    sb.append(list[i]);
                    if (i < list.length - 1) {
                        sb.append(',');
                    }
                }
            }
        }

        return sb.toString();
    }

    public static void addCalendarProps(List calendars, Locale locale, def propertyMap = [:]) {
        def messageSource =  ApplicationHolder.application.mainContext.getBean('messageSource')
        if(calendars != null) {
            def dateConverterService = new DateConverterService();

            for(int i = 0; i < calendars.size(); i++) {
               String calendar = calendars.get(i);
               String uLocale = messageSource.getMessage("default.calendar." + calendar + ".translation" , null, locale)

               propertyMap.put("default." + calendar + ".monthNames", convertToCommaDelimited(dateConverterService.getMonths(uLocale)))
               propertyMap.put("default." + calendar + ".monthNamesShort", convertToCommaDelimited(dateConverterService.getShortMonths(uLocale)))
               propertyMap.put("default." + calendar + ".dayNames", convertToCommaDelimited(dateConverterService.getWeekdays(uLocale)))
               propertyMap.put("default." + calendar + ".dayNamesShort", convertToCommaDelimited(dateConverterService.getShortWeekdays(uLocale)))
               propertyMap.put("default." + calendar + ".dayNamesMin", convertToCommaDelimited(dateConverterService.getShortWeekdays(uLocale)))
            }
        }
        //propertyMap.put("js.datepicker.dateFormat", "mm/dd/yyyy")
        propertyMap.put("js.datepicker.dateFormat", messageSource.getMessage("js.datepicker.dateFormat", null, locale))
    }

    def static formatDate = {
        def messageSource =  ApplicationHolder.application.mainContext.getBean('messageSource')
        //def pattern = MessageUtility.message("default.date.format")
        Locale locale = LCH.getLocale()
        def pattern = messageSource.getMessage("default.date.format", null, locale)
        def value = it
        try {
            try {
                value = it?.format(pattern)
            }
            catch (IllegalArgumentException x) {
                x.printStackTrace()
                value = it?.format('yyyy-MM-dd')
            }
        }
        catch (Exception x) {
            x.printStackTrace()
            //Logger.getLogger( LocalizeUtil ).debug( "Unexpected exception formatting date", x )
            // Eat the exception and do nothing
        }

        return value
    }

    def static parseDate = {
       def value = it

       if (value) {
           try {
               def messageSource =  ApplicationHolder.application.mainContext.getBean('messageSource')
               Locale locale = LCH.getLocale()
               def pattern = messageSource.getMessage("default.date.format", null, locale)
               value = Date.parse(pattern, it)
               if (value.format(pattern) != it) {
                   throw new ParseException(it, 0)
               }
           }
           catch (Exception x) {
               throw new ParseException(x)
           }
       }

       return value
   }
}
