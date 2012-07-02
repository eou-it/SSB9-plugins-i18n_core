package com.sungardhe.banner.i18n

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Created by IntelliJ IDEA.
 * User: naveinf
 * Date: 6/23/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
class I18nDateFormatterTagLib {

   def dateConverterService

   def i18nFormatDate = { attrs ->
       def formattedDate

       if(attrs.date) {
           formattedDate = attrs.date
           def dateFormat = "${g.message(code: 'default.date.format')}"
           println(dateFormat)

           if(attrs.format) {
                dateFormat = attrs.format
           }

           formattedDate = dateConverterService.convert(
                   formattedDate,
                   dateConverterService.getDefaultCalendarULocaleString(),
                   dateConverterService.getDefaultCalendarULocaleString(),
                   "${g.message(code: 'default.date.format')}", dateFormat)
       }

       out << formattedDate
   }
}
