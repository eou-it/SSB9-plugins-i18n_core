/*******************************************************************************
Copyright 2009-2015 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.i18n

import com.ibm.icu.text.DateFormat
import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.util.ClassUtils

import java.sql.Timestamp
import com.ibm.icu.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import grails.converters.JSON

/**
 * This utility class is used to convert Calendars supported by ICU4J for the UI.
 */
class DateConverterService {

    static transactional = false
    Logger logger = Logger.getLogger(this.getClass())

    public static int FIRST_DAY_OF_MONTH = -1
    public static int LAST_DAY_OF_MONTH = -2

    private static final DATE_YMD_DIGITS_FORMAT = "yyyy/MM/dd"
    private static final SLASH_SEPARATOR = "/"

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

        if (fromULocaleString == toULocaleString && fromDateFormatString == toDateFormatString) {
            return fromDateValue
        }

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

    public String[] getAmPmStrings(String uLocaleString) {
          return (new com.ibm.icu.text.DateFormatSymbols(new ULocale(uLocaleString))).getAmPmStrings();
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

    public String getDefaultTranslationULocaleString() {
       return getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar"))
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
            day = defaultCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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

    public convertDefaultCalendarToGregorian(date, fromDateFormat) {
        return convert(date ,
                getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                getGregorianULocaleString(),
                fromDateFormat,
                "MM/dd/yyyy");
    }

    public convertGregorianToDefaultCalendar(date, format) {
        return convert(date,
                getDefaultTranslationULocaleString(),
                getULocaleStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                "MM/dd/yyyy" ,
                format)
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
        String defaultDateFormat = localizerService(code: "default.date.format")
        if (! isGregorianDate(value, defaultDateFormat)) {
            try {
                def tempValue = convert(value,
                        getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar", default: 'gregorian')),
                        getULocaleStringForCalendar('gregorian'),
                        defaultDateFormat,
                        defaultDateFormat)
                //Check if date passed is valid or not.
                def checkValue = convert(tempValue,
                        getULocaleStringForCalendar('gregorian'),
                        getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar", default: 'gregorian')),
                        defaultDateFormat,
                        defaultDateFormat)
                if (tempValue != "error") {
                    if (value.equals(checkValue)) {
                        //Date is valid
                        value = tempValue
                    }
                }
            } catch (Exception e ) {
                //If an exception occurs ignore and return original value.
            }
        }
        return value
    }

    public boolean isGregorianDate(String value, String defaultDateFormat) {
        boolean isGregorianCalendarDate = true
        try {
            ULocale fromULocale = new ULocale(value)
            Calendar fromCalendar = Calendar.getInstance(fromULocale);
            DateFormat fromDateFormat = fromCalendar.handleGetDateFormat(defaultDateFormat, fromULocale)
            fromDateFormat.parse(value)
        } catch (ParseException e) {
            isGregorianCalendarDate = false
        }
        return isGregorianCalendarDate
    }

    public parseGregorianToDefaultCalendar(value) {
       try {
           if(value instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat(localizerService(code: "default.date.format"));
                value = sdf.format(new Date(value.getTime()));
           }
           String defaultDateFormat = localizerService(code: "default.date.format")
           def tempValue = convert(value,
                              getULocaleStringForCalendar('gregorian'),
                              getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                              defaultDateFormat ,
                              defaultDateFormat)

           //Check if date passed is valid or not.
           def checkValue = convert(tempValue,
                               getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                               getULocaleStringForCalendar('gregorian'),
                               defaultDateFormat ,
                               defaultDateFormat)

           if(tempValue != "error") {
              if(value.equals(checkValue)) {
                //Date is valid
                value = tempValue
              }
           }
       } catch (Exception e) {
           //If an exception occurs ignore and return original value.
       }
       return value
   }

   public formatDateInObjectsToDefaultCalendar(key, Object obj, List<String> dateFields) {
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
            else if(! ClassUtils.isPrimitiveWrapper(obj.class)) {
                obj = convertNonWrapperObjectIntoPropertyMap(obj, dateFields)
            }
        }
        return obj;
    }

    private Map<String, Object> convertNonWrapperObjectIntoPropertyMap(Object nonWrapperObject, List<String> dateFields) {
        Map propertyCollectedMap = null
        if(nonWrapperObject != null) {
            propertyCollectedMap = [:]
            nonWrapperObject.metaClass.properties.each {
                String propertyName = it.name
                def propertyValue = nonWrapperObject.properties[propertyName];
                if(propertyValue != null && dateFields.contains(propertyName)) {
                    propertyValue = formatDateInObjectsToDefaultCalendar(propertyName, propertyValue, dateFields)
                }
                propertyCollectedMap[propertyName] = propertyValue
            }
        }
        return propertyCollectedMap
    }

    public convertGregorianToDefaultCalendarWithTime(date, format) {
       return convert(date,
                  getDefaultTranslationULocaleString(),
                  getULocaleStringForCalendar(localizerService(code: "default.calendar", default: 'gregorian')),
                  format,
                  format)
      }


    public convertDefaultCalendarToGregorianWithTime(date, format) {
           return convert(date ,
                   getULocaleTranslationStringForCalendar(localizerService(code: "default.calendar",default:'gregorian')),
                   getGregorianULocaleString(),
                   format,
                   format);
    }

    public Map convertGregorianToDefaultCalendarAndExtractDateParts(Date date){
        if(date) {
            String dateString = convertGregorianToDefaultCalendar(date, DATE_YMD_DIGITS_FORMAT)
            ArrayList dateParts = dateString.tokenize(SLASH_SEPARATOR)
            return [year: dateParts[0], month: dateParts[1], day: dateParts[2]]
        }
        return [:]
    }

    private Date autoCompleteDateAndConvertToGregorianEquivalent(int year, int month, int day){
        Calendar calendar = getDefaultCalendarInstance()
        calendar.set(calendar.YEAR, year)
        calendar.set(calendar.MONTH, month)
        calendar.set(calendar.DAY_OF_MONTH, day)
        return calendar.time
    }

    private Calendar getDefaultCalendarInstance() {
        String localeString = getDefaultTranslationULocaleString()
        ULocale locale = new ULocale(localeString)
        return Calendar.getInstance(locale)
    }

    public Date getStartDateInGregorianCalendar(Integer year,Integer month=null,Integer day=null){
        Calendar calendar = getDefaultCalendarInstance()
        if(month == null){
            month = calendar.getMinimum(calendar.MONTH)
        }
        if(day == null){
            day = calendar.getMinimum(calendar.DAY_OF_MONTH)
        }

        autoCompleteDateAndConvertToGregorianEquivalent(year,month,day)
    }

    public Date getEndDateInGregorianCalendar(Integer year,Integer month=null,Integer day=null){
        Calendar calendar = getDefaultCalendarInstance()
        if(month == null){
            month = calendar.getMaximum(calendar.MONTH)
        }
        if(day == null){
            day = calendar.handleGetMonthLength(year,month)
        }

        autoCompleteDateAndConvertToGregorianEquivalent(year,month,day)
    }

    /**
     *
     * @param locale : Defaulted to browser locale if not passed
     * @return : Map of Month names with code which is zero based index
     */
    public Map getMonthNamesWithCode(String locale=getDefaultTranslationULocaleString()){
        Map monthNamesWithCode = [:]
        String[] monthNames = new DateFormatSymbols(getDefaultCalendarInstance(),new ULocale(locale)).getMonths()
        monthNames.eachWithIndex{ String monthName, int monthCode ->
            monthNamesWithCode.put(monthCode,monthName);
        }
        return monthNamesWithCode
    }
}
