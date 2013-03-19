(function($) {

function MultiCalendarsPicker() {
	this.calendarContainer = 'multiCalendarContainer';
	this.calendarIdPrefix = 'multiCalendar';
	this.TO = 'To';
    this.CALENDAR_GREGORIAN = 'gregorian';
    this.DEFAULT_DATE_FORMAT = 'mm/dd/yyyy';
    this.JAVA_DATE_FORMAT = 'MM/dd/yyyy';
    this._CAL_LOCALE_PARAMS_THAT_ARE_ARRAYS = ['epochs', 'monthNames', 'monthNamesShort', 'dayNames', 'dayNamesShort', 'dayNamesMin'];
    this._isCalendarShown = false;
    this._currentObj = null;
    this.activeCalendar = 1;
    this.numberOfCalendars;

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
        todaysDates:[],
        buttonImage: '',
        buttonClass: '',
        showOn: 'focus'

	};

	$(document).mousedown(this._checkExternalClick);
}

$.extend(MultiCalendarsPicker.prototype, {
	_markerClass: 'hasMultiCalendarPicker',

	_createDatePickerDOMStructure : function(inst) {

		var calendarOptions = inst.settings;
		$('#' + this.calendarContainer).remove();
		var DOMStructure = '<div id="' + this.calendarContainer + '" class="' + calendarOptions.orientation + '" style="display:none;"><div id="sceenReaderText" aria-live="rude" aria-atomic="true"></div>';
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
                    firstDay: parseInt(calendarOptions.firstDayOfTheWeek),
					dateFormat: dateFormat,
					onSelect: function ( target ) {
					if(target[0]) {
                        $(inst).focus();

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
                                if(onSelectExt) {
                                    onSelectExt(data,inst );
                                }
							}
							else {
								//console.log('no change');
							}
						}
                        else{
                            if(onSelectExt) {
                                var data = target[0].formatDate();
                                onSelectExt(data ,inst);
                            }
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

     _convertDateBetweenCalendarFormats_old : function(fromCalendar, toCalendar, date) {
        var toCalendarObj = $.calendars.calendars[toCalendar].prototype;

        var fromCalLocalProps = $.calendars._localCals[fromCalendar + '-'].local;
        var cDateObj = toCalendarObj.parseDate(fromCalLocalProps.dateFormat, date, toCalendarObj.regional['']);

        var toCalLocalProps = $.calendars._localCals[toCalendar + '-'].local;
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

          if(onSelectExt) {
            onSelectExt(date ,inst );
          }
       }
    },

    adjustPositionOfCalendar : function(inst) {
            var screenHeightAvailable = $(window).height() - $('#footerApplicationBar').outerHeight();
            var screenWidthAvailable = $(window).width();
			var instPosition = $(inst).offset();
			var instHeight = $(inst).outerHeight();
			var instWidth = $(inst).outerWidth();
            var pickerContainerHeight = $("#" + this.calendarContainer + "> #multiCalendar1").height();
            var firstPickerOuterWidth = $("#" + this.calendarContainer + " .hasCalendarsPicker:first .ui-datepicker").outerWidth();
            var lastPickerOuterWidth = $("#" + this.calendarContainer + " .hasCalendarsPicker:first .ui-datepicker").outerWidth();
            var pickerContainerWidth = $('.hasCalendarsPicker').length > 1 ? firstPickerOuterWidth + lastPickerOuterWidth : firstPickerOuterWidth;
            if(instPosition.top + instHeight + pickerContainerHeight >= screenHeightAvailable && instPosition.top > pickerContainerHeight){
                $("#" + this.calendarContainer).css({top: (instPosition.top - pickerContainerHeight) + "px"});
            }
            else{
                $("#" + this.calendarContainer).css({top: (instPosition.top + instHeight) + "px"});
            }

            if(instPosition.left >= firstPickerOuterWidth && $('.hasCalendarsPicker').length > 1 && screenWidthAvailable-instPosition.left >= lastPickerOuterWidth){
                $("#" + this.calendarContainer).css({left: (instPosition.left - firstPickerOuterWidth) + "px"});
            }
            else if(instPosition.left + pickerContainerWidth >= screenWidthAvailable){
                $("#" + this.calendarContainer).css({right: (screenWidthAvailable - instPosition.left -instWidth ) + "px"});
            }
            else {
                $("#" + this.calendarContainer).css({left: (instPosition.left ) + "px"});
            }

	},

    toggleCalendar: function(inst){
        if($.multicalendar._isCalendarShown && $.multicalendar._currentObj && $.multicalendar._currentObj.get(0) === inst) {
            $.multicalendar._hideCalendar();
        }
        else {
            $.multicalendar._createDatePickerDOMStructure(inst);
            $.multicalendar._addCalendarsToDOM(inst);
            $.multicalendar._showDateInCalendar(inst);

            if(inst) {
                $.multicalendar._showCalendar(inst);
            }
        }
    },

    _hideCalendar : function (inst) {
        $.multicalendar._isCalendarShown = false;
        $("#" + this.calendarContainer).hide("slow");
        $.multicalendar.activeCalendar = null;
    },


    _showCalendar : function (inst) {
        if(!$('#multiCalendarContainer').length){
            $.multicalendar._createDatePickerDOMStructure(inst);
            $.multicalendar._addCalendarsToDOM(inst);
            $.multicalendar._showDateInCalendar(inst);
        }
        $("#" + this.calendarContainer).show("slow");
        $.multicalendar.activeCalendar = 1;
         $('#' + $.multicalendar.calendarIdPrefix + $.multicalendar.activeCalendar ).addClass('activeCalendar');
        this.adjustPositionOfCalendar(inst);
         $.multicalendar._isCalendarShown = true;
         $.multicalendar._currentObj = $(inst);
    },


	_showDateInCalendarSuccessCallback : function (selectedCalendar, fromFormat, toFormat, calendarIndex, inputElementId, originalDate) {
		return function(date) {

            date = $.multicalendar._convertDateBetweenCalendarFormats(selectedCalendar, fromFormat, toFormat, date);
            try {
                $.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (calendarIndex + 1) )[0], date, null, true);
            }
            catch (e) {
                //do nothing
            }
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
						try {
                            $.calendars.picker.setDate($('#' + $.multicalendar.calendarIdPrefix + (i + 1) )[0], originalDate, null, true);
                        }
                        catch(e) {
                            //do nothing
                        }
						$(inst).val(originalDate);
					}
					else {
						var converterName = $.multicalendar._getConverterName(defaultCalendar, calendars[i]);
                        if($.multicalendar._isConverterDefined(calendarOptions, converterName)) {
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
                        else {
                            $(inst).val(originalDate);
                        }
					}
				}
			}
		}
	},

    _isConverterDefined : function (calendarOptions, converter) {
        var isConverterDefined = false;
        if(calendarOptions.converters
            && calendarOptions.converters[converter]) {
           isConverterDefined = true;
        }
        return isConverterDefined;
    },

    _todaysDate : function(calendar) {
        var cDate = $.multicalendar._defaults.todaysDates[calendar.local.name.toLowerCase()];
        if(!cDate) {
            cDate = calendar.today();
        }
        return cDate;
    },

     _getTodayDates : function (calendarOptions) {
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
                    if($.multicalendar._isConverterDefined(calendarOptions, converterName)) {
                        var formatProps = calendarOptions.converters[converterName].format;

                        var nameOfDateParam = formatProps.nameOfDateParam;
                        var jsonString = '{"' + nameOfDateParam + '": "' + $.calendars.newDate().formatDate(dateFormat) +'"}';
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
		}
    },


    getCalendar : function(calendarName){
        var calendarObj = $.calendars.calendars[calendarName].prototype;
        var calLocalProps = $.calendars._localCals[calendarName + '-'].local;
        $.extend(calendarObj, calendarObj.local? {} : {local: calLocalProps});
        return calendarObj;
    },

    _storeTodaysDateSuccessCallback : function (calendarOptions, calendar) {
		return function(date) {
            var calendarObj = $.calendars.calendars[calendar].prototype;
			var dateFormat = calendarOptions.defaultDateFormat;

            var calLocalProps = $.calendars._localCals[calendar + '-'].local;
            $.extend(calendarObj, calendarObj.local? {} : {local: calLocalProps});
			var cDateObj = calendarObj.parseDate(dateFormat, date, calendarObj.regional['']);
            $.multicalendar._defaults.todaysDates[calendar] = cDateObj;
        }
    },

    _extractFullDate : function (dateString) {
        var format = $.i18n.prop('js.datepicker.dateFormat');

        var separator = '';

        if (format.indexOf('-') >= 0) {
            separator = '-';
        }
        else if (format.indexOf('/') >= 0) {
            separator = '/';
        }
        else if (format.indexOf('.') >= 0) {
            separator = '.';
        }

        if (dateString.indexOf(separator) >= 0)  {
            var dateArray = dateString.split(separator);
            var formatArray = format.split(separator);
            var yearIndex = 0;

            for(var i = 0; i < formatArray.length; i++) {
                if(formatArray[i].toLowerCase().indexOf('y') != -1) {
                    yearIndex = i;
                    break;
                }
            }

            var year = dateArray[yearIndex];
            if(year.length == 2) {
                year = Number($.multicalendar._getCentury(parseInt(year))) + parseInt(year);
                dateArray[yearIndex] = year;
                dateString = dateArray.join(separator);
            }
        }
        return dateString;
    },

    _registerEvents : function (inst) {
        var settings = inst.settings;
        var showOn = settings.showOn

        $(inst).change( function (evt) {
            try {
                var valEntered = $(inst).val();
                var cDateObj;

                valEntered = $.multicalendar._extractFullDate(valEntered);
                var defaultCalendar = settings.defaultCalendar;
                if($.multicalendar.isValidDateFormat(defaultCalendar, valEntered)) {
                    $(inst).val(valEntered);
                    return;
                }

                var calendar = $.calendars.instance(defaultCalendar);

                var displayFormat = $.i18n.prop("js.datepicker.dateFormat");

                if (valEntered.length == 1 && isNaN(valEntered)){
                    // put system date
                    cDateObj = calendar.today();
                } else {
                    var matches = valEntered.match( /\d+/g );
                    // no special characters
                    if (matches.length == 1){
                        if(valEntered.length > 2 ){
                            //slice by 2 characers
                            matches = valEntered.match(/.{2}/g);
                        }
                    }
                    if(cDateObj == null){
                        var dateFormat = $.i18n.prop("default.dateEntry.format").toLowerCase();//calendar.local.dateFormat.toLowerCase();
                        var dateArr = {
                          'd': dateFormat.indexOf("d"),
                          'm': dateFormat.indexOf("m"),
                          'y': dateFormat.indexOf("y")
                        };

                        var sortable = [];
                        for (var val in dateArr)
                        sortable.push([val, dateArr[val]]);
                        sortable.sort(function(a, b) {return a[1] - b[1]});

                        cDateObj = calendar.today();
                        var day = cDateObj.day();
                        var month = cDateObj.month();
                        var year = cDateObj.year();
                        for (i = 0; i < matches.length; i++){
                            if(sortable[i][0] == "y"){
                                year = Number(matches[i]) + Number($.multicalendar._getCentury(Number(matches[i])));
                            } else if (sortable[i][0] == "m") {
                                month = matches[i];
                            } else {
                                day = matches[i];
                            }
                        }

                        try {
                            cDateObj = cDateObj.newDate(year, month, day);
                        }
                        catch(e) {
                            cDateObj = null;
                            return;
                        }
                    }
                }

                if(cDateObj)
                    var dateStr = calendar.formatDate(calendar.local.dateFormat, cDateObj);

                $(inst).val(dateStr);
            } catch(e) {
            }
        });

        if(showOn == "both" || showOn == "focus") {
		    $(inst).focus( function (evt) {
                if(!$.multicalendar._isCalendarShown || ($.multicalendar._currentObj && $.multicalendar._currentObj.get(0) !== $(evt.target).get(0))) {
                    $.multicalendar._createDatePickerDOMStructure(inst);
                    $.multicalendar._addCalendarsToDOM(inst);
                    $.multicalendar._showDateInCalendar(inst);
                    $.multicalendar._showCalendar(this);
                }
            });
        }

        if(showOn == "both" || showOn == "button") {
            //var img = $(inst).next('img');
		    //$(img).click( function (evt) {
            var span = $(inst).next('span');
		    $(span).click( function (evt) {
                var input = $(this).prev('input.' + $.multicalendar._markerClass);
                if($.multicalendar._isCalendarShown && $.multicalendar._currentObj && $.multicalendar._currentObj.get(0) === input.get(0)) {
                    $.multicalendar._hideCalendar();
                }
                else {
                    $.multicalendar._createDatePickerDOMStructure(inst);
                    $.multicalendar._addCalendarsToDOM(inst);
                    $.multicalendar._showDateInCalendar(inst);

                    if(input) {
                        $.multicalendar._showCalendar(input);
                        input.focus();
                    }
                }
            });

            if(showOn == "button") {
                $(inst).focus( function (evt) {
                    if($.multicalendar._currentObj && $.multicalendar._currentObj.get(0) !== $(evt.target).get(0)) {
                        $.multicalendar._hideCalendar();
                    }
                });
            }
        }

        $(inst).dblclick(function (evt) {
            $.multicalendar.toggleCalendar(evt.target);
        });

        $(inst).bind('keydown keypress', function (evt) {

            if(evt.type == 'keydown' && evt.keyCode == 120) {
                $.multicalendar.toggleCalendar(evt.target);
                evt.preventDefault();
			    evt.stopPropagation();

            }
            else if($.multicalendar._isCalendarShown){
                var activeCalendar = $('#' + $.multicalendar.calendarIdPrefix + $.multicalendar.activeCalendar )[0];
                if(evt.type == 'keydown') $.calendars.picker.keyDownMultipicker( evt, activeCalendar);
                else $.calendars.picker.keyPressMultipicker( evt, activeCalendar);
            }
        });


       $('#multiCalendarContainer .hasCalendarsPicker').live( "mouseenter" , function(){
            $('.hasCalendarsPicker').removeClass('activeCalendar');
            $(this).addClass('activeCalendar');
            $.multicalendar.activeCalendar =  $(this).attr('id').substring(13);
        });

	},

	_checkExternalClick: function(event) {
		var clickedOutsideCalendar = $(event.target).parents('#' + $.multicalendar.calendarContainer).length == 0
		                                && !$(event.target).hasClass($.multicalendar._markerClass);//,

        if(clickedOutsideCalendar) {
            //if($(event.target).is('img')
            if($(event.target).is('span')
                && $(event.target).prev('input').hasClass($.multicalendar._markerClass)) {
                clickedOutsideCalendar = false;
            }
        }

        if(clickedOutsideCalendar) {
            $.multicalendar._hideCalendar();
        }
	},

	_getCalendarOrder: function (id) {
		return parseInt(id.replace($.multicalendar.calendarIdPrefix,'')) - 1;
	},

    setDefaults: function(settings) {
        if(settings.firstDayOfTheWeek && isNaN(settings.firstDayOfTheWeek)) {
           settings.firstDayOfTheWeek = $.multicalendar._defaults.firstDayOfTheWeek;
        }

		$.extend(this._defaults, settings || {});

        settings.calendars=this._defaults.calendars;
        if(settings.calendars && typeof settings.calendars == 'string') {
		    settings.calendars = [settings.calendars];
        }
        else {
            var calendars = $.extend([], $.multicalendar._removeInvalidCalendars(settings.calendars));
            var newCalendarsList = new Array();
            for(var i = 0, j = 0; i < calendars.length; i++) {
                if(calendars[i] && $.trim(calendars[i]) != '') {
                    newCalendarsList[j] = calendars[i];
                    j++;
                }

            }
            settings.calendars = newCalendarsList;
        }
        settings.defaultDateFormat=this._defaults.defaultDateFormat;
        settings.displayDateFormat=this._defaults.displayDateFormat;

        $.extend(this._defaults, settings || {});

        $.multicalendar._processCalendarLocaleProps(settings);
        $.multicalendar._getTodayDates(settings);
		return this;
	},

    _splitString: function(stringToSplit, charIdentifier) {
        var splitArr = stringToSplit;
        if(typeof stringToSplit == "string") {
            splitArr = stringToSplit.split(charIdentifier);
            for(var i = 0; i < splitArr.length; i++) {
                 splitArr[i] = $.trim(splitArr[i]);
            }
        }
        return splitArr;
    },

    parse: function (dateString, calendarType) {
         var calendar = $.calendars.calendars[calendarType].prototype;
         var dateFormat = $.multicalendar._getDateFormat(calendarType);
         var cDateObj = calendar.parseDate(dateFormat, dateString, calendar.regional['']);
         return cDateObj;
    },

    formatCDateObject:function (cDateObj, dateFormat, calendar) {
        var calendarObj = $.calendars.calendars[calendar].prototype;
        var calLocalProps = $.calendars._localCals[calendar + '-'].local;
        $.extend(calendarObj, calendarObj.local? {} : {local: calLocalProps});
        var formattedDate = calendarObj.formatDate(dateFormat, cDateObj);
        return formattedDate;
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
                    $.calendars.instance(options.calendars[i]).local.dateFormat = options.displayDateFormat;
                }
            }
        }
    },

    _removeInvalidCalendars: function (calendars) {
        for(var i = 0; i < calendars.length; i++) {
            if(!$.calendars.calendars[calendars[i]]) {
                calendars.splice(i,1);
                i--;
            }
        }
        return calendars;
    },

    isValidDateFormat: function(calendar, dateString) {
        var isValid = false;
        try {
            var calendarObj = $.calendars.calendars[calendar].prototype;
	        var calLocalProps = $.calendars._localCals[calendar + '-'].local;
            var cDateObj = calendarObj.parseDate(calLocalProps.dateFormat, dateString, calLocalProps);
            isValid = true;
        } catch (e) {
            isValid = false;
        }
        return isValid;
    },

    _addCalendarImage: function(inst) {
        var options = inst.settings;
        //var img = $('<img>');

        /*var img = $('<img>'); //Equivalent: $(document.createElement('img'))
        img.attr('src', inst.settings.buttonImage);
        img.insertAfter($(inst));*/

        var span = $('<span>');
        span.attr('class', 'calendar-icon');
        if(options.buttonImage && options.buttonImage != '') {
            span.attr('style', 'background-image: url("' + options.buttonImage + '");');
        }
        else if(options.buttonClass && options.buttonClass != '') {
            $(span).addClass(options.buttonClass);
        }
        span.insertAfter($(inst));
    },

    _getCentury: function(val) {
        var century = 0;
        try{
            century = parseInt($.i18n.prop("default.century.below.pivot"));
            if (val > parseInt($.i18n.prop("default.century.pivot")))
                century = parseInt($.i18n.prop("default.century.above.pivot"));
        }catch(e){
        }
        if(!Number(century))
            century = 0;
        return century;
    }
});

$.fn.multiCalendarPicker = function(opts) {
	var inst = $(this)[0];

    if (!inst.isInstantiated) {
        if(opts && opts.firstDayOfTheWeek && isNaN(opts.firstDayOfTheWeek)) {
            opts.firstDayOfTheWeek = $.multicalendar._defaults.firstDayOfTheWeek;
        }
        var options = $.extend([], $.multicalendar._defaults, opts);

        inst.settings = options;

        $(inst).addClass($.multicalendar._markerClass);

        if((options.buttonImage && options.buttonImage != '') || options.buttonClass && options.buttonClass != '') {
            $.multicalendar._addCalendarImage(inst);
        }

        $.multicalendar._registerEvents(inst);
        inst.isInstantiated = true;
    }
}

$.multicalendar = new MultiCalendarsPicker(); // singleton instance

})(jQuery);