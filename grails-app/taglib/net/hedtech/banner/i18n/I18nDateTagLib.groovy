/*******************************************************************************
Copyright 2009-2016 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.i18n
/**
 * Created by IntelliJ IDEA.
 * User: naveinf
 * Date: 6/23/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
class I18nDateTagLib {

   def dateConverterService

   def i18nFormatDate = { attrs ->
       def formattedDate

       if(attrs.date) {
           formattedDate = attrs.date
           def dateFormat = "${g.message(code: 'default.date.format')}"

           if(attrs.format) {
                dateFormat = attrs.format
           }

           formattedDate = dateConverterService.convert(
                   formattedDate,
                   dateConverterService.getDefaultTranslationULocaleString(),
                   dateConverterService.getDefaultTranslationULocaleString(),
                   "${g.message(code: 'default.date.format')}", dateFormat)
       }

       out << formattedDate
   }
}
