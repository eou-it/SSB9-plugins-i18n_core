(function($) {

function MultiCalendarsPicker() {
	this.calendarContainer = 'multiCalendarContainer';
	this.calendarIdPrefix = 'multiCalendar';
	this.TO = 'To';
    this.calendarGregorian = 'gregorian';
	
	this._defaults = {
		defaultCalendar: 'gregorian',
		defaultDateFormat : 'mm/dd/yyyy',
		converters: [],
		dateFormats: {},
		orientation: 'horizontal',
		language: 'en',
		isRTL: false,
		calendars: 'gregorian',
		firstDayOfTheWeek: 0
	};
	
	/*this._converterTemplate = {
		isService: false,
		format: null
	};
	
	this._converterFormatTemplate = {
		url: null,
		dateFormat: null
	};*/

    $(document).mousedown({calendarContainer: this.calendarContainer}, this._checkExternalClick);
}

$.extend(MultiCalendarsPicker.prototype, {
	_markerClass: 'hasMultiCalendarPicker',
	
	setDefaults: function(settings) {
		$.extend(this._defaults, settings || {});
		return this;
	},
	
	_createDatePickerDOMStructure : function(inst) {
		
		var calendarOptions = inst.settings;
		$('#' + this.calendarContainer).remove();
		var DOMStructure = '<div id="' + this.calendarContainer + '" class="' + calendarOptions.orientation + '" style="display:none;">';
		var calendars = calendarOptions.calendars;
		var numberOfCalendars = calendars.length;
		if(calendars && numberOfCalendars > 0) {
			for(var i = 0; i < numberOfCalendars; i++) {
				DOMStructure += '<div id="' + this.calendarIdPrefix  + (i + 1) + '"></div>';
			}
		}
		DOMStructure += '</div>';
		
		$(document).find('body').append(DOMStructure);
	},
	
	_addCalendarsToDOM : function (inst) {
		var calendarOptions = inst.settings;
		var calendars = calendarOptions.calendars;
		var numberOfCalendars = calendars.length;
		
		if(calendars && numberOfCalendars > 0) {
			for(var i = 0; i < numberOfCalendars; i++) {
				var dateFormat = calendarOptions.dateFormats[calendars[i]];
				dateFormat = dateFormat ? dateFormat : calendarOptions.defaultDateFormat;

                var isRTL = false;
                if(calendarOptions.isRTL && calendarOptions.isRTL == true) {
                    isRTL = true;
                }
				$('#' + this.calendarIdPrefix + (i + 1)).calendarsPicker({
					altField: '#' + inst.id,
					calendar: $.calendars.instance(calendars[i]),
                    isRTL: isRTL,
                    firstDay: calendarOptions.firstDayOfTheWeek,
					dateFormat: dateFormat,
					onSelect: function ( target ) {
						var settings = inst.settings;
						var calendarOrder = $.multicalendar._getCalendarOrder(this.id);
						if(settings.defaultCalendar != calendars[calendarOrder]) {
							if($.multicalendar._isFormatDateAServiceCall(calendarOrder, settings)) {
								console.log('service');
								$.multicalendar._formatDateAsAService(calendarOrder, inst, target[0].formatDate());
							}
							else if($.multicalendar._isFormatDateAFunctionCall(calendarOrder, settings)) {
								console.log('function');
								//var data = $(inst).val();
                                var data = target[0].formatDate();
								var formatDate = $.multicalendar._getFormatFn(calendarOrder, settings);
								data = formatDate(data);
								$(inst).val(data);
							}
							else {
								console.log('no change');
							}
						}
						
						$.multicalendar._hideCalendar(inst);
					}
				});
                $.calendars.instance(calendars[i]).local.dateFormat = dateFormat;
			}
		}
	},
	
	_getConverterName : function (from, to) {
		return from + $.multicalendar.TO + to.charAt(0).toUpperCase() + to.substr(1).toLowerCase();
	},
	
	_isFormatDateAServiceCall : function (calendarOrder, calendarOptions) {
		var isServiceCall = false;
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var selectedCalendar = calendars[calendarOrder];
		var converterName = $.multicalendar._getConverterName(selectedCalendar, defaultCalendar);
		
		if(calendarOptions.converters[converterName] 
			&& calendarOptions.converters[converterName].format 
			&& calendarOptions.converters[converterName].format.url) {
			isServiceCall = true;
		}
		return isServiceCall;
	},
	
	_isFormatDateAFunctionCall : function (calendarOrder, calendarOptions) {
		var isFunctionCall = false;
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var selectedCalendar = calendars[calendarOrder];
		var converterName = $.multicalendar._getConverterName(selectedCalendar, defaultCalendar);
		
		if(calendarOptions.converters[converterName] 
			&& calendarOptions.converters[converterName].format 
			&& (typeof calendarOptions.converters[converterName].format) == 'function') {
			isFunctionCall = true;
		}
		return isFunctionCall;
	},
	
	_getFormatFn : function (calendarOrder, calendarOptions) {
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var selectedCalendar = calendars[calendarOrder];
		var converterName = $.multicalendar._getConverterName(selectedCalendar, defaultCalendar);
		
		return calendarOptions.converters[converterName].format;	
	},
	
	_formatDateAsAService : function(calendarOrder, inst, date) {
		var calendarOptions = inst.settings;
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var selectedCalendar = calendars[calendarOrder];
		var converterName = $.multicalendar._getConverterName(selectedCalendar, defaultCalendar);
		var formatProps = calendarOptions.converters[converterName].format;
		var nameOfDateParam = formatProps.nameOfDateParam;

        console.log('date selected: ' + $(inst).val());
		var jsonString = '{"' + nameOfDateParam + '": "' + date +'"}';
		var data = $.parseJSON(jsonString);
		data = $.extend(data, formatProps.extraParams); 
		$.ajax({
		  url: formatProps.url,
		  data: data,
		  dataType: 'text',
		  success: function(date){
			  $(inst).val(date);
		  }
		});
	
	},
	
	_adjustPositionOfCalendar : function(inst) {
            var actualScreenHeightAvailable = $(window).height() - $('#footerApplicationBar').outerHeight();
            var actualScreenWidthAvailable = $(window).width();
			var instPosition = $(inst).offset();
			var instHeight = $(inst).outerHeight();
			var instWidth = $(inst).outerWidth();
            var pickerContainerHeight = $("#" + this.calendarContainer + "> #multiCalendar1").height();
            var pickerContainerWidth = $("#" + this.calendarContainer).width();
            if(instPosition.top + instHeight + pickerContainerHeight >= actualScreenHeightAvailable ){
                $("#" + this.calendarContainer).css({top: (instPosition.top - pickerContainerHeight) + "px"});
            }
            else{
                $("#" + this.calendarContainer).css({top: (instPosition.top + instHeight) + "px"});
            }

            if(instPosition.left + instWidth + pickerContainerWidth >= actualScreenWidthAvailable && instPosition.left >= pickerContainerWidth){
                $("#" + this.calendarContainer).css({right: (actualScreenWidthAvailable - instPosition.left - instWidth) + "px"});
            }
            else{$("#" + this.calendarContainer).css({left: (instPosition.left ) + "px"});
            }
	},
	
	_hideCalendar : function (inst) {
		$("#" + this.calendarContainer).hide("slow");
	},
	
	_showCalendar : function (inst) {
		$("#" + this.calendarContainer).show("slow");
		this._adjustPositionOfCalendar(inst);
	},
	
	_showDateInCalendarSuccessCallback : function (calendarIndex, inputElementId, originalDate) {
		return function(date) {
			console.log(calendarIndex);
			$.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (calendarIndex + 1) )[0], date, null, true);
			$('#' + inputElementId).val(originalDate);
		}
	},
	
	_showDateInCalendar : function(inst) {
		var date = $(inst).val();
		if(date != '') {
			console.log(date);
			
			var calendarOptions = inst.settings;
			var defaultCalendar = calendarOptions.defaultCalendar;
			var calendars = calendarOptions.calendars;
			var numberOfCalendars = calendars.length;
			
			if(calendars && numberOfCalendars > 0) {
				for(var i = 0; i < numberOfCalendars; i++) {
					if(calendars[i] == defaultCalendar) {
						$.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (i + 1) )[0], date, null, true);
						$(inst).val(date);
					}
					else {
						var converterName = $.multicalendar._getConverterName(defaultCalendar, calendars[i]);
						var formatProps = calendarOptions.converters[converterName].format;
						var nameOfDateParam = formatProps.nameOfDateParam;
						
						var jsonString = '{"' + nameOfDateParam + '": "' + date +'"}';
						var data = $.parseJSON(jsonString);
						data = $.extend(data, formatProps.extraParams); 
						$.ajax({
						  url: formatProps.url,
						  data: data,
						  dataType: 'text',
						  success: $.multicalendar._showDateInCalendarSuccessCallback(i, inst.id, date)
						});
					}
                    //$.multicalendar._adjustTodaysDateInCalendar(i, inst, calendars[i]);
				}
			}
		}
	},

    _registerEvents : function (inst) {
		$(inst).focus( function (evt) {		
			$.multicalendar._createDatePickerDOMStructure(inst);			
			$.multicalendar._addCalendarsToDOM(inst);
			$.multicalendar._showDateInCalendar(inst);			
			$.multicalendar._showCalendar(this); 
        });
	},
	
	_checkExternalClick: function(event) {
		var clickedOutsideCalendar = $(event.target).parents('#' + event.data.calendarContainer).length == 0
		                                && !$(event.target).hasClass($.multicalendar._markerClass);//,
        if(clickedOutsideCalendar) {
            $.multicalendar._hideCalendar();
        }
	},
	
	_getCalendarOrder: function (id) {
		return parseInt(id.replace($.multicalendar.calendarIdPrefix,'')) - 1;
	}
});

$.fn.multiDatePicker = function(opts) {
	var options = $.extend([], $.multicalendar._defaults, opts);
	console.log($.multicalendar._defaults);
	
	if(options.calendars && typeof options.calendars == 'string') {
		options.calendars = [options.calendars];
	}
	else {
		if(options.isRTL) {
			var calendars = $.extend([], options.calendars);
			for(var i = options.calendars.length - 1, j = 0; i >= 0; i--, j++) {
				options.calendars[j] = calendars[i];
			}
		}
	}
	
	var inst = $(this)[0];
	inst.settings = options;
	
	$(inst).addClass($.multicalendar._markerClass);
	$.multicalendar._registerEvents(inst);
}

$.multicalendar = new MultiCalendarsPicker(); // singleton instance
//$.multicalendar.initialized = false;

})(jQuery);
