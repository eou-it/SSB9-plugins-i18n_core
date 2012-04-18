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


    def convert(def fromDateValue, String fromULocaleString, String toULocaleString, String fromDateFormatString, String toDateFormatString, String adjustDays = null) {

        assert fromDateValue
        assert fromULocaleString
        assert toULocaleString
        assert fromDateFormatString
        assert toDateFormatString

        String toDateString = ""


        try {
            Date fromDate

            //String fromULocaleString = fromLocaleString + "@calendar=" + fromCalendarString
            if(fromDateValue instanceof String) {
                ULocale fromULocale = new ULocale(fromULocaleString)
                Calendar fromCalendar = Calendar.getInstance(fromULocale);
                com.ibm.icu.text.DateFormat fromDateFormat = fromCalendar.handleGetDateFormat(fromDateFormatString, fromULocale)
                fromDate = fromDateFormat.parse(fromDateValue)
            } else if ( fromDateValue instanceof Date){
                ULocale fromULocale = new ULocale(fromULocaleString)
                Calendar fromCalendar = Calendar.getInstance(fromULocale);
                fromCalendar.setTime(fromDateValue)
                com.ibm.icu.text.DateFormat fromDateFormat = fromCalendar.handleGetDateFormat(fromDateFormatString, fromULocale)
                fromDateValue = fromDateFormat.format(fromCalendar)
                fromDate = fromDateFormat.parse(fromDateValue)
            }

            //String toULocaleString = toLocaleString + "@calendar=" + toCalendarString
            ULocale toULocale = new ULocale(toULocaleString)
            Calendar toCalendar = Calendar.getInstance(toULocale);
            toCalendar.setTime(fromDate)

            if (adjustDays) toCalendar = adjustDate(toCalendar, adjustDays)

            com.ibm.icu.text.DateFormat toDateFormat = toCalendar.handleGetDateFormat(toDateFormatString, toULocale)
            if(fromDateValue instanceof Date){
                return toCalendar.getTime()
            } else {
                toDateString = toDateFormat.format(toCalendar)
                return arabicToDecimal(toDateString)
            }

        } catch (Exception exception) {
            String errorString = "Unable to perform conversion --  date: " + fromDateValue + ", fromULocaleString: " + fromULocaleString + ", toULocaleString: " + toULocaleString + ", fromDateFormatString: " + fromDateFormatString + ", toDateFormatString: " + toDateFormatString
            logger.error errorString, exception
            return "error"
        }
    }

     private String arabicToDecimal(String number) {
	    StringBuilder sb = new StringBuilder();
		 for(int i=0;i<number.length();i++) {
			 char ch = number.charAt(i);
			 int x = (int)ch;
			 if(x >= 1632 && x <= 1641) {
				x -= 1632;
				sb.append(x);
			 }
			 else {
				sb.append(ch);
			 }
		 }
		 return sb.toString();
	 }

    public String[] getMonths(String uLocaleString) {
        return (new com.ibm.icu.text.DateFormatSymbols(new ULocale(uLocaleString))).getMonths();
    }

    public String[] getShortMonths(String uLocaleString) {
          return (new com.ibm.icu.text.DateFormatSymbols(new ULocale(uLocaleString))).getShortMonths();
    }

     public String[] getWeekdays(String uLocaleString) {
        return (new com.ibm.icu.text.DateFormatSymbols(new ULocale(uLocaleString))).getWeekdays();
    }

    public String[] getShortWeekdays(String uLocaleString) {
          return (new com.ibm.icu.text.DateFormatSymbols(new ULocale(uLocaleString))).getShortWeekdays();
    }

    public String getDefaultULocaleString() {
           return getULocaleStringForCalendar("gregorian")
    }

    public String getULocaleStringForCalendar(String calendar) {
       String uLocaleCode = "default.calendar." + calendar + ".ulocale";
       String property = localizerService(code: uLocaleCode)
       if (!property) log.error("message property key: " + uLocaleCode + " is missing")
       return property
    }

    public convertGregorianToDefaultCalendar(date) {
        return convert(date,
                getDefaultULocaleString(),
                getULocaleStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                localizerService(code: "default.date.format") ,
                localizerService(code: "default.date.format.display"))
    }

    public convertDefaultCalendarToGregorian(date) {
        return convert(date ,
                getULocaleStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                getDefaultULocaleString(),
                localizerService(code: "default.date.format.display"),
                localizerService(code: "default.date.format"));
    }

    private Calendar adjustDate(Calendar calendar, String adjustDays) {

        if (adjustDays) calendar.add(Calendar.DATE, adjustDays.toInteger())

        return calendar
    }

}
