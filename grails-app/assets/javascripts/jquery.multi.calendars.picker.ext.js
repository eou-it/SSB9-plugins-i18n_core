(function(){
    $.extend($.calendars.picker.commands,

        { // Command actions that may be added to a layout by name
            // name: { // The command name, use '{button:name}' or '{link:name}' in layouts
            //		text: '', // The field in the regional settings for the displayed text
            //		status: '', // The field in the regional settings for the status text
            //      // The keystroke to trigger the action
            //		keystroke: {keyCode: nn, ctrlKey: boolean, altKey: boolean, shiftKey: boolean},
            //		enabled: fn, // The function that indicates the command is enabled
            //		date: fn, // The function to get the date associated with this action
            //		action: fn} // The function that implements the action
            prev: {text: 'prevText', status: 'prevStatus', // Previous month
                keystroke: {keyCode: 33}, // Page up
                enabled: function(inst) {
                    var minDate = inst.curMinDate();
                    return (!minDate || inst.drawDate.newDate().
                        add(1 - inst.get('monthsToStep') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay).add(-1, 'd').compareTo(minDate) != -1); },
                date: function(inst) {
                    return inst.drawDate.newDate().
                        add(-inst.get('monthsToStep') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay); },
                action: function(inst) {
                    $.calendars.picker.changeMonth(this, -inst.get('monthsToStep')); }
            },
            prevJump: {text: 'prevJumpText', status: 'prevJumpStatus', // Previous year
                keystroke: {keyCode: 33, ctrlKey: true, shiftKey:true}, // Ctrl + shift + Page up
                enabled: function(inst) {
                    var minDate = inst.curMinDate();
                    return (!minDate || inst.drawDate.newDate().
                        add(1 - inst.get('monthsToJump') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay).add(-1, 'd').compareTo(minDate) != -1); },
                date: function(inst) {
                    return inst.drawDate.newDate().
                        add(-inst.get('monthsToJump') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay); },
                action: function(inst) {
                    $.calendars.picker.changeMonth(this, -inst.get('monthsToJump')); }
            },
            next: {text: 'nextText', status: 'nextStatus', // Next month
                keystroke: {keyCode: 34}, // Page down
                enabled: function(inst) {
                    var maxDate = inst.get('maxDate');
                    return (!maxDate || inst.drawDate.newDate().
                        add(inst.get('monthsToStep') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay).compareTo(maxDate) != +1); },
                date: function(inst) {
                    return inst.drawDate.newDate().
                        add(inst.get('monthsToStep') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay); },
                action: function(inst) {
                    $.calendars.picker.changeMonth(this, inst.get('monthsToStep')); }
            },
            nextJump: {text: 'nextJumpText', status: 'nextJumpStatus', // Next year
                keystroke: {keyCode: 34, ctrlKey: true, shiftKey:true}, // Ctrl + shift + Page down
				enabled: function(inst) {
                    var maxDate = inst.get('maxDate');
                    return (!maxDate || inst.drawDate.newDate().
                        add(inst.get('monthsToJump') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay).compareTo(maxDate) != +1);	},
                date: function(inst) {
                    return inst.drawDate.newDate().
                        add(inst.get('monthsToJump') - inst.get('monthsOffset'), 'm').
                        day(inst.get('calendar').minDay); },
                action: function(inst) {
					$.calendars.picker.changeMonth(this, inst.get('monthsToJump')); }
            },
			current: {text: 'currentText', status: 'currentStatus', // Current month
				keystroke: {keyCode: 36, ctrlKey: true, shiftKey:true}, // Ctrl + shift + Home
				enabled: function(inst) {
					var minDate = inst.curMinDate();
					var maxDate = inst.get('maxDate');
					var curDate = inst.selectedDates[0] || inst.get('calendar').today();
					return (!minDate || curDate.compareTo(minDate) != -1) &&
						(!maxDate || curDate.compareTo(maxDate) != +1); },
				date: function(inst) {
					return inst.selectedDates[0] || inst.get('calendar').today(); },
				action: function(inst) {
					var curDate = inst.selectedDates[0] || inst.get('calendar').today();
					$.calendars.picker.showMonth(this, curDate.year(), curDate.month()); }
			},
			today: {text: 'todayText', status: 'todayStatus', // Today's month
				keystroke: {keyCode: 36, ctrlKey: true, shiftKey:true}, // Ctrl + shift +Home
				enabled: function(inst) {
					var minDate = inst.curMinDate();
					var maxDate = inst.get('maxDate');
					return (!minDate || inst.get('calendar').today().compareTo(minDate) != -1) &&
						(!maxDate || inst.get('calendar').today().compareTo(maxDate) != +1); },
				date: function(inst) { return inst.get('calendar').today(); },
				action: function(inst) { $.calendars.picker.showMonth(this); }
			},
            clear: {text: 'clearText', status: 'clearStatus', // Clear the datepicker
                keystroke: {keyCode: 35, ctrlKey: true, shiftKey:true}, // Ctrl + shift + End
                enabled: function(inst) { return true; },
                date: function(inst) { return null; },
                action: function(inst) { $.calendars.picker.clear(this); }
            },
            close: {text: 'closeText', status: 'closeStatus', // Close the datepicker
                keystroke: {keyCode: 27}, // Escape
                enabled: function(inst) { return true; },
                date: function(inst) { return null; },
                action: function(inst) {
					$.multicalendar._hideCalendar(inst);}
            },
            prevWeek: {text: 'prevWeekText', status: 'prevWeekStatus', // Previous week
                keystroke: {keyCode: 38}, // Up
                enabled: function(inst) {
                    var minDate = inst.curMinDate();
                    return (!minDate || inst.drawDate.newDate().
                        add(-inst.get('calendar').daysInWeek(), 'd').compareTo(minDate) != -1); },
                date: function(inst) { return inst.drawDate.newDate().
                    add(-inst.get('calendar').daysInWeek(), 'd'); },
                action: function(inst) { $.calendars.picker.changeDay(
                    this, -inst.get('calendar').daysInWeek()); }
			},
            prevDay: {text: 'prevDayText', status: 'prevDayStatus', // Previous day
                keystroke: {keyCode: 37}, //  Left
                enabled: function(inst) {
                    var minDate = inst.curMinDate();
                    return (!minDate || inst.drawDate.newDate().add(-1, 'd').
                        compareTo(minDate) != -1); },
                date: function(inst) { return inst.drawDate.newDate().add(-1, 'd'); },
                action: function(inst) {
					if(isRTLMode()){
						$.calendars.picker.changeDay(this, 1);
					}
					else
						$.calendars.picker.changeDay(this, -1);
				}
            },
            nextDay: {text: 'nextDayText', status: 'nextDayStatus', // Next day
                keystroke: {keyCode: 39}, // Right
                enabled: function(inst) {
					var maxDate = inst.get('maxDate');
                    return (!maxDate || inst.drawDate.newDate().add(1, 'd').
                        compareTo(maxDate) != +1); },
                date: function(inst) { return inst.drawDate.newDate().add(1, 'd'); },
                action: function(inst) {
					if (isRTLMode()) {
						$.calendars.picker.changeDay(this, -1);
					}
					else
						$.calendars.picker.changeDay(this, 1);
				}
            },
            nextWeek: {text: 'nextWeekText', status: 'nextWeekStatus', // Next week
                keystroke: {keyCode: 40}, // Down
                enabled: function(inst) {
                    var maxDate = inst.get('maxDate');
                    return (!maxDate || inst.drawDate.newDate().
                        add(inst.get('calendar').daysInWeek(), 'd').compareTo(maxDate) != +1); },
                date: function(inst) { return inst.drawDate.newDate().
                    add(inst.get('calendar').daysInWeek(), 'd'); },
                action: function(inst) { $.calendars.picker.changeDay(
                    this, inst.get('calendar').daysInWeek()); }
            },

			firstDayOfMonth: {text: 'firstDayText', status: 'prevDayStatus', // first day of month
				keystroke: {keyCode: 36}, //  Home
				enabled: function(inst) {
					var target = $(this);
					var minDate = inst.curMinDate();
					console.info(inst.drawDate._day);

					return (!minDate || inst.drawDate.newDate().add(-1, 'd').
						compareTo(minDate) != -1); },
				date: function(inst) {

					return inst.drawDate.newDate().add(0, 'd'); },
				action: function(inst) { $.calendars.picker.changeDay(this, -inst.drawDate._day+1); }
			},
			lastDayOfMonth: {text: 'lastDayText', status: 'prevDayStatus', // last day of month
				keystroke: {keyCode: 35}, //  end

				enabled: function(inst) {
					var target = $(this);
					var minDate = inst.curMinDate();
					return (!minDate || inst.drawDate.newDate().add(-1, 'd').
						compareTo(minDate) != -1); },
				date: function(inst) {
					return inst.drawDate.newDate().add(0, 'd'); },
				action: function(inst) {
					var daysInMonth=$.calendars.picker._checkMinMax(inst.drawDate, inst).daysInMonth(inst.drawDate._year,inst.drawDate._month)
					$.calendars.picker.changeDay(this, daysInMonth-inst.drawDate._day); }
			},
            activateNextCalendar: {text: 'activateNextCalendarText', status: 'activateNextCalendarStatus',
                keystroke: {keyCode: 83,shiftKey:true }, // shift + s
                enabled: function(inst) {
                    return true;
                },
                date: function(inst) {
                },
                action: function(inst) {
                    $('#' + $.multicalendar.calendarIdPrefix + $.multicalendar.activeCalendar ).removeClass('activeCalendar');
                    $.multicalendar.activeCalendar = $.multicalendar.activeCalendar + 1;
                    if(!$('#' + $.multicalendar.calendarIdPrefix + $.multicalendar.activeCalendar ).length){$.multicalendar.activeCalendar = 1; }
                    $('#' + $.multicalendar.calendarIdPrefix + $.multicalendar.activeCalendar ).addClass('activeCalendar');
                }

            },
			showCalendar: {text: 'showCalendarText', status: 'showCalendarStatus',
                keystroke: {keyCode: 120 }, // F9
                enabled: function(inst) {
                    return true;
                },
                date: function(inst) {
                },
                action: function(inst) {
                    $.multicalendar._showCalendar(inst);
					$(inst).attr('readOnly','true');

                }

            }

        }

    );



    $.extend($.calendars.picker,{
	/* Generate the datepicker content for this control.
	   @param  target  (element) the control to affect
	   @param  inst    (object) the current instance settings
	   @return  (jQuery) the datepicker content */
	_generateContent: function(target, inst) {
		var calendar = inst.get('calendar');
		var renderer = inst.get('renderer');
		var monthsToShow = inst.get('monthsToShow');
		monthsToShow = ($.isArray(monthsToShow) ? monthsToShow : [1, monthsToShow]);
		inst.drawDate = this._checkMinMax(
			inst.drawDate || inst.get('defaultDate') || calendar.today(), inst);
		var drawDate = inst.drawDate.newDate().add(-inst.get('monthsOffset'), 'm');
		// Generate months
		var monthRows = '';
		for (var row = 0; row < monthsToShow[0]; row++) {
			var months = '';
			for (var col = 0; col < monthsToShow[1]; col++) {
				months += this._generateMonth(target, inst, drawDate.year(),
					drawDate.month(), calendar, renderer, (row == 0 && col == 0));
				drawDate.add(1, 'm');
			}
			monthRows += this._prepare(renderer.monthRow, inst).replace(/\{months\}/, months);
		}
		var picker = this._prepare(renderer.picker, inst).replace(/\{months\}/, monthRows).
			replace(/\{weekHeader\}/g, this._generateDayHeaders(inst, calendar, renderer)) +
			($.browser.msie && parseInt($.browser.version, 10) < 7 && !inst.inline ?
			'<iframe src="javascript:void(0);" class="' + this._coverClass + '"></iframe>' : '');
		// Add commands
		var commands = inst.get('commands');
		var asDateFormat = inst.get('commandsAsDateFormat');
		var addCommand = function(type, open, close, name, classes) {
			if (picker.indexOf('{' + type + ':' + name + '}') == -1) {
				return;
			}
			var command = commands[name];
			var date = (asDateFormat ? command.date.apply(target, [inst]) : null);
			picker = picker.replace(new RegExp('\\{' + type + ':' + name + '\\}', 'g'),
				'<' + open +
				(command.status ? ' title="' + inst.get(command.status) + '"' : '') +
				' class="' + renderer.commandClass + ' ' +
				renderer.commandClass + '-' + name + ' ' + classes +
				(command.enabled(inst) ? '' : ' ' + renderer.disabledClass) + '">' +
				(date ? date.formatDate(inst.get(command.text)) : inst.get(command.text)) +
				'</' + close + '>');
		};
		for (var name in commands) {
			addCommand('button', 'button type="button"', 'button', name,
				renderer.commandButtonClass);
			addCommand('link', 'a href="javascript:void(0)"', 'a', name,
				renderer.commandLinkClass);
		}
		picker = $(picker);
		if (monthsToShow[1] > 1) {
			var count = 0;
			$(renderer.monthSelector, picker).each(function() {
				var nth = ++count % monthsToShow[1];
				$(this).addClass(nth == 1 ? 'first' : (nth == 0 ? 'last' : ''));
			});
		}
		// Add calendar behaviour
		var self = this;
		picker.find(renderer.daySelector + ' a').hover(
				function() { $(this).addClass(renderer.highlightedClass); },
				function() {
					(inst.inline ? $(this).parents('.' + self.markerClass) : inst.div).
						find(renderer.daySelector + ' a').
						removeClass(renderer.highlightedClass);
				}).
			click(function() {
				self.selectDate(target, this);
			}).end().
			find('select.' + this._monthYearClass + ':not(.' + this._anyYearClass + ')').change(function() {
				var monthYear = $(this).val().split('/');
				self.showMonth(target, parseInt(monthYear[1], 10), parseInt(monthYear[0], 10));
			}).end().
			find('select.' + this._anyYearClass).click(function() {
				$(this).next('input').css({left: this.offsetLeft, top: this.offsetTop,
					width: this.offsetWidth, height: this.offsetHeight}).show().focus();
			}).end().
			find('input.' + self._monthYearClass).change(function() {
				try {
					var year = parseInt($(this).val(), 10);
					year = (isNaN(year) ? inst.drawDate.year() : year);
					self.showMonth(target, year, inst.drawDate.month(), inst.drawDate.day());
				}
				catch (e) {
					alert(e);
				}
			}).keydown(function(event) {
				if (event.keyCode == 27) { // Escape
					$(event.target).hide();
					inst.target.focus();
				}
			});
		// Add command behaviour
		picker.find('.' + renderer.commandClass).click(function() {
				if (!$(this).hasClass(renderer.disabledClass)) {
					var action = this.className.replace(
						new RegExp('^.*' + renderer.commandClass + '-([^ ]+).*$'), '$1');
					$.calendars.picker.performAction(target, action);
				}
            return false;
			});
		// Add classes
		if (inst.get('isRTL')) {
			picker.addClass(renderer.rtlClass);
		}
		if (monthsToShow[0] * monthsToShow[1] > 1) {
			picker.addClass(renderer.multiClass);
		}
		var pickerClass = inst.get('pickerClass');
		if (pickerClass) {
			picker.addClass(pickerClass);
		}
		// Resize
		$('body').append(picker);
		/*
		var width = 0;
		picker.find(renderer.monthSelector).each(function() {
			width += $(this).outerWidth();
		});
		picker.width(width / monthsToShow[0]);
		*/
		// Pre-show customisation
		var onShow = inst.get('onShow');
		if (onShow) {
			onShow.apply(target, [picker, calendar, inst]);
		}
		return picker;
	},

    _generateMonth : function(target, inst, year, month, calendar, renderer, first) {
		var daysInMonth = calendar.daysInMonth(year, month);
		var monthsToShow = inst.get('monthsToShow');
		monthsToShow = ($.isArray(monthsToShow) ? monthsToShow : [1, monthsToShow]);
		var fixedWeeks = inst.get('fixedWeeks') || (monthsToShow[0] * monthsToShow[1] > 1);
		var firstDay = inst.get('firstDay');
		firstDay = (firstDay == null ? calendar.local.firstDay : firstDay);
		var leadDays = (calendar.dayOfWeek(year, month, calendar.minDay) -
			firstDay + calendar.daysInWeek()) % calendar.daysInWeek();
		var numWeeks = (fixedWeeks ? 6 : Math.ceil((leadDays + daysInMonth) / calendar.daysInWeek()));
		var showOtherMonths = inst.get('showOtherMonths');
		var selectOtherMonths = inst.get('selectOtherMonths') && showOtherMonths;
		var dayStatus = inst.get('dayStatus');
		var minDate = (inst.pickingRange ? inst.selectedDates[0] : inst.get('minDate'));
		var maxDate = inst.get('maxDate');
		var rangeSelect = inst.get('rangeSelect');
		var onDate = inst.get('onDate');
		var showWeeks = renderer.week.indexOf('{weekOfYear}') > -1;
		var calculateWeek = inst.get('calculateWeek');
		var today = $.multicalendar._todaysDate(calendar);//calendar.today();
		var drawDate = calendar.newDate(year, month, calendar.minDay);
		drawDate.add(-leadDays - (fixedWeeks &&
			(drawDate.dayOfWeek() == firstDay || drawDate.daysInMonth() < calendar.daysInWeek())?
			calendar.daysInWeek() : 0), 'd');
		var jd = drawDate.toJD();
		// Generate weeks
		var weeks = '';
		for (var week = 0; week < numWeeks; week++) {
			var weekOfYear = (!showWeeks ? '' : '<span class="jd' + jd + '">' +
				(calculateWeek ? calculateWeek(drawDate) : drawDate.weekOfYear()) + '</span>');
			var days = '';
			for (var day = 0; day < calendar.daysInWeek(); day++) {
				var selected = false;
				if (rangeSelect && inst.selectedDates.length > 0) {
					selected = (drawDate.compareTo(inst.selectedDates[0]) != -1 &&
						drawDate.compareTo(inst.selectedDates[1]) != +1)
				}
				else {
					for (var i = 0; i < inst.selectedDates.length; i++) {
						if (inst.selectedDates[i].compareTo(drawDate) == 0) {
							selected = true;
							break;
						}
					}
				}
				var dateInfo = (!onDate ? {} :
					onDate.apply(target, [drawDate, drawDate.month() == month]));
				var selectable = (selectOtherMonths || drawDate.month() == month) &&
					this._isSelectable(target, drawDate, dateInfo.selectable, minDate, maxDate);
				days += this._prepare(renderer.day, inst).replace(/\{day\}/g,
					(selectable ? '<a href="javascript:void(0)" "onclick="return false;"' : '<span') +
					' class="jd' + jd + ' ' + (dateInfo.dateClass || '') +
					(selected && (selectOtherMonths || drawDate.month() == month) ?
					' ' + renderer.selectedClass : '') +
					(selectable ? ' ' + renderer.defaultClass : '') +
					(drawDate.weekDay() ? '' : ' ' + renderer.weekendClass) +
					(drawDate.month() == month ? '' : ' ' + renderer.otherMonthClass) +
					(drawDate.compareTo(today) == 0 && drawDate.month() == month ?
					' ' + renderer.todayClass : '') +
                    (drawDate.compareTo(inst.drawDate) == 0 && drawDate.month() == month && $.multicalendar._isCalendarShown?
					' ' + renderer.highlightedClass : '') + '"' +
					(dateInfo.title || (dayStatus && selectable) ? ' title="' +
					(dateInfo.title || $.i18n.prop("js.datepicker.selectText") +" "+ drawDate.formatDate(dayStatus)) + '"' : '') + '>' +
					(showOtherMonths || drawDate.month() == month ?
					dateInfo.content || drawDate.day() : '&nbsp;') +
					(selectable ? '</a>' : '</span>'));
				days =  days.replace(/<td>/g,
					'<td class="' +
					(drawDate.compareTo(today) == 0 && drawDate.month() == month ?
					' ' + renderer.todayClass : '') + '">');
				drawDate.add(1, 'd');
				jd++;
			}
			weeks += this._prepare(renderer.week, inst).replace(/\{days\}/g, days).
				replace(/\{weekOfYear\}/g, weekOfYear);
		}
		var monthHeader = this._prepare(renderer.month, inst).match(/\{monthHeader(:[^\}]+)?\}/);
		monthHeader = (monthHeader[0].length <= 13 ? 'MM yyyy' :
			monthHeader[0].substring(13, monthHeader[0].length - 1));
		monthHeader = (first ? this._generateMonthSelection(
			inst, year, month, minDate, maxDate, monthHeader, calendar, renderer) :
			calendar.formatDate(monthHeader, calendar.newDate(year, month, calendar.minDay)));
		var weekHeader = this._prepare(renderer.weekHeader, inst).
			replace(/\{days\}/g, this._generateDayHeaders(inst, calendar, renderer));
		return this._prepare(renderer.month, inst).replace(/\{monthHeader(:[^\}]+)?\}/g, monthHeader).
			replace(/\{weekHeader\}/g, weekHeader).replace(/\{weeks\}/g, weeks);
	},

    keyDownMultipicker : function(event, activeCalendar) {
		var target = activeCalendar;
		var inst = $.data(target, $.calendars.picker.dataName);
		var handled = false;
        var visibleInstance = $.multicalendar._isCalendarShown && $.multicalendar._currentObj && $.multicalendar._currentObj.get(0);

        if (event.keyCode == 9 ) { // Tab - close
	    $.multicalendar._hideCalendar(visibleInstance);
	}
	else if( event.keyCode == 27) { // Esc - close
            $.multicalendar._hideCalendar(visibleInstance);
            handled = true;
        }
        else if (event.keyCode == 13) { // Enter - select
			$('#multiCalendarContainer .activeCalendar a.ui-state-hover').click();
            handled = true;
        }
		else if (event.keyCode == 32) { // Space Select date and close.
			$('#multiCalendarContainer .activeCalendar a.ui-state-hover').click();
			handled = true;
		}
        else { // Command keystrokes
            var commands = inst.get('commands');
			handled = true;
            for (var name in commands) {
                var command = commands[name];
                if (command.keystroke.keyCode == event.keyCode &&
                        !!command.keystroke.ctrlKey == !!(event.ctrlKey || event.metaKey) &&
                        !!command.keystroke.altKey == event.altKey &&
                        !!command.keystroke.shiftKey == event.shiftKey) {
                    $.calendars.picker.performAction(target, name);
                    break;
                }
            }
        }

		inst.ctrlKey = ((event.keyCode < 48 && event.keyCode != 32) ||
			event.ctrlKey || event.metaKey);
		if (handled) {
			event.preventDefault();
			event.stopPropagation();
            if($.multicalendar._isCalendarShown && $('.activeCalendar a.ui-state-hover')[0])
                $('#sceenReaderText').html($('.activeCalendar a.ui-state-hover')[0].title);
		}
		return !handled;
	},

	keyPressMultipicker: function(event, activeCalendar) {
		var target = activeCalendar;
		var inst = $.data(target, $.calendars.picker.dataName);
		if (inst && inst.get('constrainInput')) {
			var ch = String.fromCharCode(event.keyCode || event.charCode);
			var allowedChars = $.calendars.picker._allowedChars(inst);
			return (event.metaKey || inst.ctrlKey || ch < ' ' ||
				!allowedChars || allowedChars.indexOf(ch) > -1);
		}
		return true;
	}
    });
})(jQuery);
