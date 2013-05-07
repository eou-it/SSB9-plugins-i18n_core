$(document).ready(function() {

    $.calendars.picker.setDefaults({
        renderer:          $.calendars.picker.themeRollerRenderer,
        changeMonth:       true,
        showAnim:          "fadeIn",
        showOptions:       null,
        showSpeed:         500,
        useMouseWheel:     false,
        showOtherMonths:   true,
        selectOtherMonths: true,
        prevText:          "<span class=\"ui-icon ui-icon-circle-triangle-w\"> </span>",
        nextText:          "<span class=\"ui-icon ui-icon-circle-triangle-e\"> </span>",
        dayStatus:         $.i18n.prop("js.datepicker.selectText") + ' DD, M d, yyyy',
        prevStatus:        $.i18n.prop("js.datepicker.prevStatus"),
        nextStatus:        $.i18n.prop("js.datepicker.nextStatus"),
        yearStatus:        $.i18n.prop("js.datepicker.yearStatus"),
        monthStatus:       $.i18n.prop("js.datepicker.monthStatus")
    });

    var default_calendar=$.i18n.prop("default.calendar");
    var default_calendar1=$.i18n.prop("default.calendar1");
    var default_calendar2=$.i18n.prop("default.calendar2");
    var firstDayOfTheWeek=$.i18n.prop("default.firstDayOfTheWeek");

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
    var timeLocaleProps = {
        ampmNames: [$.i18n.prop('default.time.am'), $.i18n.prop('default.time.pm')],
    	spinnerTexts: [$.i18n.prop('default.time.increment'), $.i18n.prop('default.time.decrement')]
    };

    var dateConverterURL = "dateConverter"
    if($('meta[name=menuBaseURL]').attr("content")){
           dateConverterURL = $('meta[name=menuBaseURL]').attr("content") + '/' + dateConverterURL;
    }

    var converters = {
        gregorianToIslamic: {
            format: {
                url: dateConverterURL,
                nameOfDateParam: 'date',
                extraParams: {
                    calendar: 'islamic-civil',
                    fromDateFormat: 'MM/dd/yyyy',
                    toDateFormat: 'MM/dd/yyyy',
                    toULocale: $.i18n.prop("default.calendar.islamic.ulocale"),
                    fromULocale: $.i18n.prop("default.calendar.gregorian.translation")
                }
            }
        },

        islamicToGregorian: {
            format: {
                url: dateConverterURL,
                nameOfDateParam: 'date',
                extraParams: {
                    calendar: 'islamic-civil',
                    fromDateFormat: 'MM/dd/yyyy',
                    toDateFormat: 'MM/dd/yyyy',
                    toULocale: $.i18n.prop("default.calendar.gregorian.translation"),
                    fromULocale: $.i18n.prop("default.calendar.islamic.ulocale")
                }
            }
        }
    };

    $.multicalendar.setDefaults({
        defaultCalendar: default_calendar,
        converters: converters,
        defaultDateFormat: 'mm/dd/yyyy',
        displayDateFormat: $.i18n.prop("js.datepicker.dateFormat"),
        calendars:[ default_calendar1, default_calendar2 ],
        isRTL: $.i18n.prop("default.language.direction"),
        calendarLocaleProps: calendarLocaleProps,
        buttonClass: 'calendar-img',
        showOn: 'both',
        firstDayOfTheWeek: firstDayOfTheWeek,
        timeLocaleProps: timeLocaleProps
    });
});