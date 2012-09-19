/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/ 

package net.hedtech.banner.i18n

/**
 * Controller focused on providing Conversion of Calendars supported by ICU4J
 */
class DateConverterController {

    static defaultAction = "data"
    def DateConverterService

    def data = {

        assert params.date, "Date must be supplied"
        render dateConverterService.convert(params.date, params?.fromULocale, params?.toULocale, params?.fromDateFormat, params?.toDateFormat, params.adjustDays)
    }
}
