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

    /*var islamicCalendarLocaleProps = {
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
    */
     var islamicCalendarLocaleProps = {
        monthNames: $.i18n.prop("default.islamic.monthNames"),
		monthNamesShort: $.i18n.prop("default.islamic.monthNamesShort"),
		dayNames: $.i18n.prop("default.islamic.dayNames"),
		dayNamesShort: $.i18n.prop("default.islamic.dayNamesShort"),
        dayNamesMin: $.i18n.prop("default.islamic.dayNamesMin")
    };

    var gregorianCalendarLocaleProps = {
        monthNames: $.i18n.prop("default.gregorian.monthNames"),
		monthNamesShort: $.i18n.prop("default.gregorian.monthNamesShort"),
		dayNames: $.i18n.prop("default.gregorian.dayNames"),
		dayNamesShort: $.i18n.prop("default.gregorian.dayNamesShort"),
        dayNamesMin: $.i18n.prop("default.gregorian.dayNamesMin")
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