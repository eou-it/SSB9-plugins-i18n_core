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
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import java.sql.Timestamp
import java.text.SimpleDateFormat
import grails.converters.JSON
import com.ibm.icu.text.DateFormat

/**
 * This utility class is used to convert Calendars supported by ICU4J for the UI.
 */
class DateConverterService {

    static transactional = false
    Logger logger = Logger.getLogger(this.getClass())

    public static int FIRST_DAY_OF_MONTH = -1
    public static int LAST_DAY_OF_MONTH = -2

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

    public String getGregorianULocaleString() {
        return getULocaleStringForCalendar("gregorian")
    }

    public String getDefaultCalendarULocaleString() {
        return getULocaleStringForCalendar(localizerService(code: "default.calendar"))
    }

    public String getULocaleStringForCalendar(String calendar) {
       String uLocaleCode = "default.calendar." + calendar + ".ulocale";
       String property = localizerService(code: uLocaleCode)
       if (!property) log.error("message property key: " + uLocaleCode + " is missing")
       return property
    }

    public String getGregorianTranslationULocaleString() {
           return getULocaleTranslationStringForCalendar("gregorian")
    }

    public String getULocaleTranslationStringForCalendar(String calendar) {
       String uLocaleCode = "default.calendar." + calendar + ".translation";
       String property = localizerService(code: uLocaleCode)
       if (!property) log.error("message property key: " + uLocaleCode + " is missing")
       return property
    }

    public Date getGregorianFromDefaultCalendar(int year, int month, int day) {
        String uLocaleString = getDefaultCalendarULocaleString()
        ULocale uLocale = new ULocale(uLocaleString)
        Calendar defaultCalendar = Calendar.getInstance(uLocale);

        defaultCalendar.set(year, month, 1)

        if(day == FIRST_DAY_OF_MONTH) {
            day = 1
        }
        else if(day == LAST_DAY_OF_MONTH){
            defaultCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        defaultCalendar.set(Calendar.DATE, day);

        return defaultCalendar.getTime()
    }

    public convertGregorianToDefaultCalendar(date) {
        return convert(date,
                getGregorianULocaleString(),
                getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                "MM/dd/yyyy" ,
                localizerService(code: "default.date.format"))
    }

    public convertDefaultCalendarToGregorian(date) {
        return convert(date ,
                getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                getGregorianULocaleString(),
                localizerService(code: "default.date.format"),
                "MM/dd/yyyy");
    }


    private Calendar adjustDate(Calendar calendar, String adjustDays) {

        if (adjustDays) calendar.add(Calendar.DATE, adjustDays.toInteger())

        return calendar
    }

    public JSONDateMarshaller(data, dateFields) {
        try {
           if(data instanceof JSONArray) {
               //collection
               JSONArray newArray = new JSONArray()
               data.each { entry ->
                   newArray.add(JSONDateMarshaller(entry, dateFields));
               }
               data = newArray
           }
           else if(data instanceof JSONObject) {
               JSONObject jsonObj = new JSONObject();
               data.each { key, value ->
                  if(value instanceof String && dateFields.contains(key) && value != JSONObject.NULL) {
                       value = parseGregorianToDefaultCalendar(value)
                       jsonObj.put(key, value);
                  }
                  else if(data instanceof JSONObject){
                       jsonObj.put(key, JSONDateMarshaller(value, dateFields))
                  }
               }
               jsonObj.each {key, value ->
                   data.put(key, value)
               }
           }
           else if(data instanceof String) {
               if(dateFields.contains(data)) {
                data =  parseGregorianToDefaultCalendar(data)
               }
           }
        }
        catch (Exception e) {
           //If an exception occurs ignore and return original data.
        }
        return data
    }

    private JSONObject marshallDateForJSONObject(data, dateFields) {
           JSONObject jsonObj = new JSONObject();
           data.each { key, value ->
              if(dateFields.contains(key) && value != JSONObject.NULL) {
                   value = parseGregorianToDefaultCalendar(value)
              }
              jsonObj.put(key, value);
           }
           return jsonObj
    }

    public JSONDateUnmarshaller(data, dateFields) {
        try {

           if(data instanceof JSONArray) {
               //collection
               JSONArray newArray = new JSONArray()
               data.each { entry ->
                   newArray.add(unmarshallDateForJSONObject(entry, dateFields));
               }
               data = newArray
           }
           else if(data instanceof JSONObject) {
               //Single data
               data = unmarshallDateForJSONObject(data, dateFields)
           }
        }
        catch (Exception e) {
            //If an exception occurs ignore and return original data.
        }
           return data
    }

    private JSONObject unmarshallDateForJSONObject(data, dateFields) {
        JSONObject jsonObj = new JSONObject();
        data.each { key, value ->
           if(dateFields.contains(key) && value != JSONObject.NULL) {
                   value = parseDefaultCalendarToGregorian(value)
           }
           jsonObj.put(key, value);
        }
        return jsonObj
    }

    public parseDefaultCalendarToGregorian(value) {
       try {
          String defaultDateFormat = localizerService(code: "default.date.format")
          def tempValue = convert(value,
                               getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                               getULocaleStringForCalendar('gregorian'),
                               defaultDateFormat ,
                               defaultDateFormat)
          if(tempValue != "error") {
              value = tempValue
          }
       } catch (Exception e) {
           //If an exception occurs ignore and return original value.
       }
       return value
    }

   public parseGregorianToDefaultCalendar(value) {
       try {
           String defaultDateFormat = localizerService(code: "default.date.format")
          def tempValue = convert(value,
                               getULocaleStringForCalendar('gregorian'),
                               getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                               defaultDateFormat ,
                               defaultDateFormat)
          if(tempValue != "error") {
              value = tempValue
          }
       } catch (Exception e) {
           //If an exception occurs ignore and return original value.
       }
       return value
   }

   public formatDateInObjectsToDefaultCalendar(key, obj, dateFields) {
        if(obj != null) {

            if(obj instanceof ArrayList) {
                List newList = new ArrayList()
                int listSize = obj.size()
                for (int i = 0; i < listSize; i ++) {
                    newList.add(formatDateInObjectsToDefaultCalendar(key, obj.get(i), dateFields))
                }
                obj = newList;
            }
            else if(obj instanceof Map) {
                def modifiedMap = [:]
                obj.each { innerKey, innerValue ->
                    if(dateFields.contains(innerKey)) {
                        if(innerValue instanceof java.sql.Date){
                            innerValue = new Date(innerValue.getTime())
                        }
                        modifiedMap.put(innerKey, formatDateInObjectsToDefaultCalendar(innerKey, innerValue,dateFields))
                    }
                    else if(innerValue instanceof Map) {
                        modifiedMap.put(innerKey, formatDateInObjectsToDefaultCalendar(innerKey, innerValue,dateFields))
                    }
                }
                modifiedMap.each {newKey, newValue ->
                    obj.put(newKey, newValue)
                }
            }
            else if(obj instanceof String) {
                if(dateFields.contains(key)) {
                    obj = parseGregorianToDefaultCalendar(obj)
                }
            }
            else if(obj instanceof Timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat(localizerService(code: "default.date.format"));
                String date = sdf.format(new Date(obj.getTime()));
                obj = parseGregorianToDefaultCalendar(date)
            }
            else if(obj instanceof java.sql.Date){
                obj = new Date(obj.getTime())
                obj = parseGregorianToDefaultCalendar(obj)
            }
            else if(obj instanceof java.util.Date){
                obj = parseGregorianToDefaultCalendar(obj)
            }
            else if( obj instanceof JSON) {
                //Do nothing to JSON
                //May need to add code to handle inner JSON data
                if(obj != null && obj.target) {
                    obj.target = formatDateInObjectsToDefaultCalendar(key, obj.target, dateFields)
                }
            }
            else if(obj.metaClass) {
                obj = convertClassToMap(obj, dateFields)
            }
        }
        return obj;
    }

    public convertClassToMap(obj, dateFields) {
        Object returnObj = null

        if(obj != null) {
            if(obj instanceof java.sql.Date){
                obj = new Date(obj.getTime())
                returnObj = parseGregorianToDefaultCalendar(obj)
            }
            else if(obj instanceof Timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat(localizerService(code: "default.date.format"));
                String date = sdf.format(new Date(obj.getTime()));
                returnObj = parseGregorianToDefaultCalendar(date)
            }
            else if(obj instanceof java.util.Date){
                returnObj = parseGregorianToDefaultCalendar(obj)
            }
            else if(obj.metaClass) {
               Map objMap = new HashMap()

               if(obj == null || obj.metaClass == null || obj.metaClass.properties == null) {
                   println obj
               }
               obj.metaClass.properties.each {
                   def newValue = obj.properties[it.name];
                   if(newValue != null && dateFields.contains(it.name)) {
                        newValue = formatDateInObjectsToDefaultCalendar(it.name, newValue, dateFields)
                   }
                   objMap.put(it.name, newValue)
               }

               returnObj = objMap
            }
        }

        return returnObj
    }
}
