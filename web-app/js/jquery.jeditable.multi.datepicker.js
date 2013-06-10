// add :focus selector
jQuery.expr[':'].focus = function( elem ) {
  return elem === document.activeElement && ( elem.type || elem.href );
};

/* _wrap copied from underscore.js to avoid circular dependency on banner_ui_ss */
var _wrap =  function(func, wrapper) {
    return function() {
      var args = [func];
      push.apply(args, arguments);
      return wrapper.apply(this, args);
    };
  };

$.datepicker._doKeyDown = _wrap( $.datepicker._doKeyDown, function(func, event) {
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
      input.attr( 'autocomplete','off' );
      form.append( input );
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

      input.multiCalendarPicker(datepicker);
    }
} );
