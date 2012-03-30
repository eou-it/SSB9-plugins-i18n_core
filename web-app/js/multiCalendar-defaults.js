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

var default_calendar='islamic';
var default_calendar1='islamic';
var default_calendar2='gregorian';
var default_multicalendar_date_format='dd/mm/yyyy';
var default_service_date_format='dd/MM/yyyy';

var converters = {
    gregorianToIslamic: {
        format: {
            url: 'dateConverter',
            nameOfDateParam: 'date',
            extraParams: {
                test: 'dummyString',
                fromDateFormat: default_service_date_format,
                toDateFormat: default_service_date_format,
                toULocale: 'en_US@calendar=islamic',
                fromULocale: 'en_US@calendar=gregorian'
            }
        }
    },

    islamicToGregorian: {
        format: {
            url: 'dateConverter',
            nameOfDateParam: 'date',
            extraParams: {
                test: 'dummyString',
                fromDateFormat: default_service_date_format,
                toDateFormat: default_service_date_format,
                toULocale: 'en_US@calendar=gregorian',
                fromULocale: 'en_US@calendar=islamic'
            }
        }
    }
};

//var dateFormats = {islamic: 'yyyy/mm/dd'};
$.multicalendar.setDefaults({
    defaultCalendar: default_calendar,
    converters: converters,
    //dateFormats: dateFormats,
    defaultDateFormat : default_multicalendar_date_format,
    calendars:[default_calendar1, default_calendar2],
    firstDayOfTheWeek: 4,
    isRTL: false
});