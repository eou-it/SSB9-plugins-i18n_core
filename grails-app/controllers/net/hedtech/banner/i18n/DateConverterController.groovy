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
