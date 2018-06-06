/* Copyright 2011-2013 Ellucian Company L.P. and its affiliates. */

modules = {

    'i18n-core' {
        dependsOn "jquery"

        defaultBundle environment == "development" ? false : "i18n-core"

        resource url:[plugin: 'i18n-core', file: 'css/multiCalendar.css']

        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.js']
        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.plus.js']
        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.picker.js']
        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.picker.ext.js']
        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.islamic.js']

        resource url:[plugin: 'i18n-core', file: 'js/time/jquery.timeentry.js']
        resource url:[plugin: 'i18n-core', file: 'js/jquery.multi.calendars.picker.js']
        resource url:[plugin: 'i18n-core', file: 'js/jquery.jeditable.multi.datepicker.js']
        resource url:[plugin: 'i18n-core', file: 'js/jquery.multi.calendars.picker.ext.js']

        resource url:[plugin: 'i18n-core', file: 'js/multi.calendar.init.js']
        resource url:[plugin: 'i18n-core', file: 'js/calendars/jquery.calendars.ummalqura.js']
    }

}
