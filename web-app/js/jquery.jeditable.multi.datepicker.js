/*
 * Datepicker for Jeditable
 *
 * Copyright (c) 2011 Piotr 'Qertoip' Włodarek
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Depends on jQuery UI Datepicker
 *
 * Project home:
 *   http://github.com/qertoip/jeditable-datepicker
 *
 */

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
var default_calendar3='gregorian';

//var default_calendar1='gregorian';
//var default_calendar2='islamic';


//var default_multicalendar_date_format='mm/dd/yyyy';

//This is a mandate for widget. It needs can only accept dates that are in integer format.
//var default_service_date_format='MM/dd/yyyy';

var converters = {
    gregorianToIslamic: {
        format: {
            url: 'dateConverter',
            nameOfDateParam: 'date',
            extraParams: {
                calendar: 'islamic-civil',
                fromDateFormat: $.multicalendar.JAVA_DATE_FORMAT,
                toDateFormat: $.multicalendar.JAVA_DATE_FORMAT,
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
                fromDateFormat: $.multicalendar.JAVA_DATE_FORMAT,
                toDateFormat: $.multicalendar.JAVA_DATE_FORMAT,
                toULocale: 'en_US@calendar=gregorian',
                fromULocale: 'en_US@calendar=islamic-civil'
            }
        }
    }
};

var dateFormats = {islamic: 'dd/M/yyyy'};
$.multicalendar.setDefaults({
    defaultCalendar: default_calendar,
    converters: converters,
    dateFormats: dateFormats,
    calendars:[ default_calendar1, default_calendar2 ],
    firstDayOfTheWeek: 4,
    isRTL: false
});

// add :focus selector
jQuery.expr[':'].focus = function( elem ) {
  return elem === document.activeElement && ( elem.type || elem.href );
};

$.datepicker._doKeyDown = _.wrap( $.datepicker._doKeyDown, function(func, event) {
    if ( !this._pressedKeys && event.keyCode == 13 ) {
        // if ENTER is the first keypress in the open datepicker, just close it
        $.datepicker._hideDatepicker();
    } else {
        this._pressedKeys = true;
        return func( event );
    }
});

$.editable.addInputType( 'datepicker', {

    /* create input element */
    element: function( settings, original ) {
      var form = $( this ),
          input = $( '<input id="multiCalendarDestination"/>' );
          btn = $( "<button type='button'>&nbsp;</button>" );
      input.attr( 'autocomplete','off' );
      form.append( input );
      form.append( btn );
      return input;
    },

    /* attach jquery.ui.datepicker to the input element */
    plugin: function( settings, original ) {
      var form = this,
          input = form.find( "input" );

      // Don't cancel inline editing onblur to allow clicking datepicker
      settings.onblur = 'nothing';

      datepicker = {
        onSelect: function() {
          // clicking specific day in the calendar should
          // submit the form and close the input field
          form.submit();
        },

        onClose: function() {
          setTimeout( function() {
            if ( !input.is( ':focus' ) ) {
              // input has NO focus after 150ms which means
              // calendar was closed due to click outside of it
              // so let's close the input field without saving
              original.reset( form );
            } else {
              // input still HAS focus after 150ms which means
              // calendar was closed due to Enter in the input field
              // so lets submit the form and close the input field
              form.submit();
            }

            // the delay is necessary; calendar must be already
            // closed for the above :focus checking to work properly;
            // without a delay the form is submitted in all scenarios, which is wrong
          }, 150 );
        }
      };

      if (settings.datepicker) {
        jQuery.extend(datepicker, settings.datepicker);
      }

      //input.id = 'multiCalendarDestination';



      //input.multiDatePicker([calendar1Options, calendar2Options])
      input.multiDatePicker();


    }
} );
