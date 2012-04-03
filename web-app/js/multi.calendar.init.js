$(document).ready(function() {

    $.calendars.picker.setDefaults({
        renderer:          $.calendars.picker.themeRollerRenderer,
        changeMonth:       false,
        showAnim:          "fadeIn",
        showOptions:       null,
        showSpeed:         500,
        useMouseWheel:     false,
        showOtherMonths:   true,
        selectOtherMonths: true,
        changeMonth:       false,
        prevText:          "<span class=\"ui-icon ui-icon-circle-triangle-w\"> </span>",
        nextText:          "<span class=\"ui-icon ui-icon-circle-triangle-e\"> </span>"
    });

    var default_calendar=$.i18n.prop("default.calendar");
    var default_calendar1=$.i18n.prop("default.calendar1");
    var default_calendar2=$.i18n.prop("default.calendar2");
    var calendar1_date_format=$.i18n.prop("default.calendar1.date.format");
    var calendar2_date_format=$.i18n.prop("default.calendar2.date.format");

    var islamicCalendarLocaleProps = {
        monthNames: $.i18n.prop("default.calendar1.monthNames"),
		monthNamesShort: $.i18n.prop("default.calendar1.monthNamesShort"),
		dayNames: $.i18n.prop("default.calendar1.dayNames"),
		dayNamesShort: $.i18n.prop("default.calendar1.dayNamesShort"),
        dayNamesMin: $.i18n.prop("default.calendar1.dayNamesMin")
    };

    var gregorianCalendarLocaleProps = {
        monthNames: $.i18n.prop("default.calendar2.monthNames"),
		monthNamesShort: $.i18n.prop("default.calendar2.monthNamesShort"),
		dayNames: $.i18n.prop("default.calendar2.dayNames"),
		dayNamesShort: $.i18n.prop("default.calendar2.dayNamesShort"),
        dayNamesMin: $.i18n.prop("default.calendar2.dayNamesMin")
    };

    var calendarLocaleProps = {islamic: islamicCalendarLocaleProps, gregorian: gregorianCalendarLocaleProps};

    var converters = {
        gregorianToIslamic: {
            format: {
                url: 'dateConverter',
                nameOfDateParam: 'date',
                extraParams: {
                    calendar: 'islamic-civil',
                    fromDateFormat: $.i18n.prop("default.date.format"),
                    toDateFormat: $.i18n.prop("default.date.format"),
                    toULocale: 'en_US@calendar=islamic-civil',
                    fromULocale: 'en_US@calendar=gregorian'
                }
            }
        },

        islamicToGregorian: {
            format: {
                url: 'dateConverter',
                nameOfDateParam: 'date',
                extraParams: {
                    calendar: 'islamic-civil',
                    fromDateFormat: $.i18n.prop("default.date.format"),
                    toDateFormat: $.i18n.prop("default.date.format"),
                    toULocale: 'en_US@calendar=gregorian',
                    fromULocale: 'en_US@calendar=islamic-civil'
                }
            }
        }
    };

    //var dateJsonString = '{"' + default_calendar1 + '": "' + calendar1_date_format +'", "' + default_calendar2 + '": "' + calendar2_date_format +'"}';
    //var dateFormats = $.parseJSON(dateJsonString);

    $.multicalendar.setDefaults({
        defaultCalendar: default_calendar,
        converters: converters,
        defaultDateFormat: $.i18n.prop("js.datepicker.dateFormat"),
        displayDateFormat: $.i18n.prop("js.datepicker.dateFormat.display"),
        calendars:[ default_calendar1, default_calendar2 ],
        isRTL: $.i18n.prop("default.language.direction"),
        calendarLocaleProps: calendarLocaleProps
    });
});