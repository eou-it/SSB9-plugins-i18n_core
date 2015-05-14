// add :focus selector
jQuery.expr[':'].focus = function( elem ) {
  return elem === document.activeElement && ( elem.type || elem.href );
};

$(function() {
    // requires _ and jquery.editable to be initialized
    $.datepicker._doKeyDown = _.wrap( $.datepicker._doKeyDown, function(func, event) {
        if ( !this._pressedKeys && event.keyCode == 13 ) {
            // if ENTER is the first keypress in the open datepicker, just close it
            $.datepicker._hideDatepicker();
        } else {
            this._pressedKeys = true;
            return func( event );
        }
    });

$.editable.addInputType( 'datepicker', { // note that this hides banner_ui_ss jquery.jeditable.datepicker.js

        /* create input element */
        element: function( settings, original ) {
            var form = $( this ),
            input = $( '<input id="multiCalendarDestination"/>' );
            input.attr( 'autocomplete','off' );
            form.append( input );
            return input;
        },

        /* attach jquery.ui.datepicker to the input element */
        plugin: function( settings, original ) {
            var form = this,
            input = form.find( "input" );

            // Don't cancel inline editing onblur to allow clicking datepicker
            // this is the jeditable settings, not the datepicker options
            settings.onblur = 'nothing';


            var datepicker = jQuery.extend( {}, settings.datepicker, {
                onSelect: function() {
                    // clicking specific day in the calendar should
                    // submit the form and close the input field
                    form.submit();
                    var handler = settings.datepicker.onSelect;
                    return handler && handler.apply( this, arguments );
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
                        var handler = settings.datepicker.onClose;
                        return handler && handler.apply( this, arguments );

                        // the delay is necessary; calendar must be already
                        // closed for the above :focus checking to work properly;
                        // without a delay the form is submitted in all scenarios, which is wrong
                    }, 150 );
                }
            });

            if (settings.datepicker) {
                jQuery.extend(datepicker, settings.datepicker);
            }

            input.multiCalendarPicker(datepicker);
        }
    } );
});
