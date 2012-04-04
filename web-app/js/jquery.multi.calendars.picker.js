(function($) {

function MultiCalendarsPicker() {
	this.calendarContainer = 'multiCalendarContainer';
	this.calendarIdPrefix = 'multiCalendar';
	this.TO = 'To';
    this.VALID_CALENDAR_NAMES = ['gregorian', 'islamic'];
    this.CALENDAR_GREGORIAN = 'gregorian';
    this.DEFAULT_DATE_FORMAT = 'mm/dd/yyyy';
    this.JAVA_DATE_FORMAT = 'MM/dd/yyyy';
    this._CAL_LOCALE_PARAMS_THAT_ARE_ARRAYS = ['epochs', 'monthNames', 'monthNamesShort', 'dayNames', 'dayNamesShort', 'dayNamesMin'];

	this._defaults = {
		defaultCalendar: this.CALENDAR_GREGORIAN,
		defaultDateFormat: this.DEFAULT_DATE_FORMAT,
        displayDateFormat: this.DEFAULT_DATE_FORMAT,
		converters: [],
		dateFormats: {},
		orientation: 'horizontal',
		language: 'en',
		isRTL: false,
		calendars: this.CALENDAR_GREGORIAN,
		firstDayOfTheWeek: 0,
        todaysDates:[]
	};
	
	$(document).mousedown(this._checkExternalClick);
}

$.extend(MultiCalendarsPicker.prototype, {
	_markerClass: 'hasMultiCalendarPicker',
	
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
				dateFormat = dateFormat ? dateFormat : ( calendarOptions.displayDateFormat ? calendarOptions.displayDateFormat : calendarOptions.defaultDateFormat);

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
                        var onSelectExt = inst.settings.onSelect? inst.settings.onSelect : null;
						if(settings.defaultCalendar != calendars[calendarOrder]) {
							if($.multicalendar._isFormatDateAServiceCall(calendarOrder, settings)) {
								$.multicalendar._formatDateAsAService(calendarOrder, inst, target[0].formatDate());
							}
							else if($.multicalendar._isFormatDateAFunctionCall(calendarOrder, settings)) {
								var data = target[0].formatDate();
								var formatDate = $.multicalendar._getFormatFn(calendarOrder, settings);
								data = formatDate(data);
								$(inst).val(data);
                                if(onSelectExt)onSelectExt(data,inst );
							}
							else {
								//console.log('no change');
							}
						}
                        else{if(onSelectExt)onSelectExt(data,inst );}

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

     _convertDateBetweenCalendarFormats_old : function(fromCalendar, toCalendar, date) {
        var toCalendarObj = $.calendars.calendars[toCalendar].prototype;

        var fromCalLocalProps = $.calendars._localCals[fromCalendar + '-'].local;
        //$.extend(fromCalendarObj, fromCalendarObj.local? {} : {local: fromCalLocalProps});
        var cDateObj = toCalendarObj.parseDate(fromCalLocalProps.dateFormat, date, toCalendarObj.regional['']);

        var toCalLocalProps = $.calendars._localCals[toCalendar + '-'].local;
        //$.extend(cDateObj._calendar, toCalendarObj);
        $.extend(toCalendarObj, toCalendarObj.local? {} : {local: toCalLocalProps});
        date = toCalendarObj.formatDate(toCalendarObj.local.dateFormat, cDateObj);
        return date;
    },

    _convertDateBetweenCalendarFormats : function(calendar, fromFormat, toFormat, date) {
        var calendarObj = $.calendars.calendars[calendar].prototype;

        var calLocalProps = $.calendars._localCals[calendar + '-'].local;
        var cDateObj = calendarObj.parseDate(fromFormat, date, calLocalProps);

        $.extend(calendarObj, calendarObj.local? {} : {local: calLocalProps});
        date = calendarObj.formatDate(toFormat, cDateObj);
        return date;
    },

    _getDateFormat : function (calendar) {
        var calendarProps = $.calendars._localCals[calendar + '-'].local;
        return calendarProps.dateFormat;
    },

	
	_formatDateAsAService : function(calendarOrder, inst, date) {
		var calendarOptions = inst.settings;
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var selectedCalendar = calendars[calendarOrder];
		var converterName = $.multicalendar._getConverterName(selectedCalendar, defaultCalendar);
		var formatProps = calendarOptions.converters[converterName].format;
		var nameOfDateParam = formatProps.nameOfDateParam;

        var fromFormat = $.multicalendar._getDateFormat(selectedCalendar);
        var toFormat = calendarOptions.defaultDateFormat;
        date = $.multicalendar._convertDateBetweenCalendarFormats(selectedCalendar, fromFormat, toFormat, date);

        fromFormat = calendarOptions.defaultDateFormat;
        toFormat = $.multicalendar._getDateFormat(defaultCalendar);

        var jsonString = '{"' + nameOfDateParam + '": "' + date +'"}';
		var data = $.parseJSON(jsonString);
		data = $.extend(data, formatProps.extraParams);
		$.ajax({
		  url: formatProps.url,
		  data: data,
		  dataType: 'text',
          success: $.multicalendar._formatDateAsAServiceSuccess(defaultCalendar, fromFormat, toFormat, inst)
		});
	
	},

    _formatDateAsAServiceSuccess: function (selectedCalendar, fromFormat, toFormat, inst) {
       var calendarOptions = inst.settings;
       var onSelectExt = inst.settings.onSelect? inst.settings.onSelect : null;
       return function (date) {

          date = $.multicalendar._convertDateBetweenCalendarFormats(selectedCalendar, fromFormat, toFormat, date);
          $(inst).val(date);
       }
       if(onSelectExt)onSelectExt(data,inst );
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
	
	_showDateInCalendarSuccessCallback : function (selectedCalendar, fromFormat, toFormat, calendarIndex, inputElementId, originalDate) {
		return function(date) {

            date = $.multicalendar._convertDateBetweenCalendarFormats(selectedCalendar, fromFormat, toFormat, date);
           	$.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (calendarIndex + 1) )[0], date, null, true);
			$('#' + inputElementId).val(originalDate);
		}
	},


	_showDateInCalendar : function(inst) {
		var date = $(inst).val();

		if(date != '') {

			var calendarOptions = inst.settings;
			var defaultCalendar = calendarOptions.defaultCalendar;
			var calendars = calendarOptions.calendars;
			var numberOfCalendars = calendars.length;
            var originalDate = date;
			
			if(calendars && numberOfCalendars > 0) {
				for(var i = 0; i < numberOfCalendars; i++) {
					if(calendars[i] == defaultCalendar) {
						$.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (i + 1) )[0], originalDate, null, true);
						$(inst).val(originalDate);
					}
					else {
						var converterName = $.multicalendar._getConverterName(defaultCalendar, calendars[i]);
						var formatProps = calendarOptions.converters[converterName].format;
						var nameOfDateParam = formatProps.nameOfDateParam;

                        var fromFormat = $.multicalendar._getDateFormat(defaultCalendar);
                        var toFormat = calendarOptions.defaultDateFormat;
                        date = $.multicalendar._convertDateBetweenCalendarFormats(defaultCalendar, fromFormat, toFormat, originalDate);

                        fromFormat = calendarOptions.defaultDateFormat;
                        toFormat = $.multicalendar._getDateFormat(calendars[i]);

						var jsonString = '{"' + nameOfDateParam + '": "' + date +'"}';
						var data = $.parseJSON(jsonString);
						data = $.extend(data, formatProps.extraParams);
						$.ajax({
						  url: formatProps.url,
						  data: data,
						  dataType: 'text',
						  success: $.multicalendar._showDateInCalendarSuccessCallback(calendars[i], fromFormat, toFormat, i, inst.id, originalDate)
						});
					}
				}
			}
		}
	},

    _todaysDate : function(calendar) {
        var cDate = $.multicalendar._defaults.todaysDates[calendar.local.name.toLowerCase()];
        if(!cDate) {
            cDate = calendar.today();
        }
        return cDate;
    },

     _getTodayDates : function (calendarOptions) {
        //var calendarOptions = inst.settings;
		var defaultCalendar = calendarOptions.defaultCalendar;
		var calendars = calendarOptions.calendars;
		var numberOfCalendars = calendars.length;

        var calendar = $.calendars.calendars[$.multicalendar.CALENDAR_GREGORIAN].prototype;
        var dateFormat = calendarOptions.defaultDateFormat;

		var cDateObj = calendar.parseDate(dateFormat, $.calendars.newDate().formatDate(dateFormat), calendar.regional[''])
        $.multicalendar._defaults.todaysDates[$.multicalendar.CALENDAR_GREGORIAN] = cDateObj;

		if(calendars && numberOfCalendars > 0) {
			for(var i = 0; i < numberOfCalendars; i++) {
				if(this.CALENDAR_GREGORIAN != calendars[i]) {
					var converterName = $.multicalendar._getConverterName(this.CALENDAR_GREGORIAN, calendars[i]);
					var formatProps = calendarOptions.converters[converterName].format;

					var nameOfDateParam = formatProps.nameOfDateParam;
					var jsonString = '{"' + nameOfDateParam + '": "' + $.calendars.newDate().formatDate() +'"}';
					var data = $.parseJSON(jsonString);
					data = $.extend(data, formatProps.extraParams);
					$.ajax({
					  url: formatProps.url,
					  data: data,
					  dataType: 'text',
					  success: $.multicalendar._storeTodaysDateSuccessCallback(calendarOptions, calendars[i])
					});
				}
			}
		}
    },

    _storeTodaysDateSuccessCallback : function (calendarOptions, calendar) {
		return function(date) {
            var calendarObj = $.calendars.calendars[calendar].prototype;
			var dateFormat = calendarOptions.defaultDateFormat;

			var cDateObj = calendarObj.parseDate(dateFormat, date, calendarObj.regional['']);
            $.multicalendar._defaults.todaysDates[calendar] = cDateObj;
        }
    },

    _registerEvents : function (inst) {
		$(inst).focus( function (evt) {
			$.multicalendar._createDatePickerDOMStructure(inst);
			$.multicalendar._addCalendarsToDOM(inst);
			$.multicalendar._showDateInCalendar(inst);
			//$.multicalendar._adjustTodaysDateInCalendar(inst);
			$.multicalendar._showCalendar(this);
        });
	},
	
	_checkExternalClick: function(event) {
		var clickedOutsideCalendar = $(event.target).parents('#' + $.multicalendar.calendarContainer).length == 0
		                                && !$(event.target).hasClass($.multicalendar._markerClass);//,
        if(clickedOutsideCalendar) {
            $.multicalendar._hideCalendar();
        }
	},
	
	_getCalendarOrder: function (id) {
		return parseInt(id.replace($.multicalendar.calendarIdPrefix,'')) - 1;
	},

    setDefaults: function(settings) {
		$.extend(this._defaults, settings || {});
		return this;
	},

    _splitString: function(string , char) {
        var splitArr = string.split(char);
        for(var i = 0; i < splitArr.length; i++) {
             splitArr[i] = splitArr[i].trim();
        }
        return splitArr;
    },

    _processCalendarLocaleProps : function (options) {
        if(options.calendars && options.calendarLocaleProps) {
            for(var i = 0; i < options.calendars.length; i++) {
                if(options.calendarLocaleProps[options.calendars[i]]) {
                    var calendarLocaleProps = options.calendarLocaleProps[options.calendars[i]];

                    for(var key in calendarLocaleProps) {
                        var calPropValues = calendarLocaleProps[key];
                        if($.inArray(key, $.multicalendar._CAL_LOCALE_PARAMS_THAT_ARE_ARRAYS) != -1 && calPropValues.indexOf(key) == -1) {
                            calendarLocaleProps[key] = this._splitString(calPropValues, ',');
                        }
                        else {
                            delete calendarLocaleProps[key];
                        }
                    }

                    var localeCalendar = $.calendars.calendars[options.calendars[i]];
                    $.extend(localeCalendar.prototype.regional[''], calendarLocaleProps);
                }
            }
        }
    },

    _removeInvalidCalendars: function (calendars) {
        for(var i = 0; i < calendars.length; i++) {
            if(!($.inArray(calendars[i], $.multicalendar.VALID_CALENDAR_NAMES) != -1 )) {
                delete calendars[i];
            }
        }
        return calendars;
    }
});

$.fn.multiDatePicker = function(opts) {
	var options = $.extend([], $.multicalendar._defaults, opts);

	if(options.calendars && typeof options.calendars == 'string') {
		options.calendars = [options.calendars];
	}
	else {
        var calendars = $.extend([], $.multicalendar._removeInvalidCalendars(options.calendars));
        var newCalendarsList = new Array();
		for(var i = 0, j = 0; i < calendars.length; i++) {
            if(calendars[i] && calendars[i].trim() != '') {
                newCalendarsList[j] = calendars[i];
                j++;
            }

		}
        options.calendars = newCalendarsList;
	}

    $.multicalendar._processCalendarLocaleProps(options);

    $.multicalendar._getTodayDates(options);

    var inst = $(this)[0];
	inst.settings = options;
	
	$(inst).addClass($.multicalendar._markerClass);
	$.multicalendar._registerEvents(inst);
}

$.multicalendar = new MultiCalendarsPicker(); // singleton instance

})(jQuery);
