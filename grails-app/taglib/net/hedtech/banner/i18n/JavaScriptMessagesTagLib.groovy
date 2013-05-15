/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/ 
package net.hedtech.banner.i18n

import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * This class is built off the knowledge provided within the ResourceTagLib from
 * the resources plug-in.  It's goal is to scan the files that have been processed
 * for localication call outs and provide them in the i18n map on the client.
 */
class JavaScriptMessagesTagLib {

    static LOCALE_KEYS_ATTRIBUTE = "localeKeys"

    def resourceService

    def encodeHTML( msg ) {
        msg.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
    }

    private def resourceModuleNames(request) {
	def names = []

	if (request.resourceDependencyTracker != null) {
	    // resources plugin <= 1.0.2
	    request.resourceDependencyTracker.each {
		addDependendNames(it, names)
	    }
	} else if (request.resourceModuleTracker != null) {
	    // resources plugin >= 1.0.3
	    request.resourceModuleTracker.each {
		if (it.value) { // todo what does 'false' for this property mean? validate usage
		    addDependendNames(it.key, names)
		}
	    }
	}

	names
    }
    
    void addDependendNames(name, list) {
	     // After moving to submodule, the dependent resources where not picked for bundling the message properties
	     // We are explicitly adding all the dependend modules to the list so that all the properties defined in the
	     // JS file gets picked.
	     list << name
	     if(resourceService.getModule(name)?.dependsOn) {
		 resourceService.getModule(name)?.dependsOn.each {
		      addDependendNames(it, list)
		 }
	     }
    }

    def i18nJavaScript = { attrs ->
        def names = resourceModuleNames(request)
        Set keys = []

        if (names.size() > 0) {

            // Search for any place where we are referencing message codes
            def regex = ~/\(*\.i18n.prop\(.*?[\'\"](.*?)[\'\"].*?\)/

            names.each { name ->
                resourceService.getModule(name)?.resources?.findAll { it.sourceUrlExtension == "js" }?.each {

                    if (!it.attributes.containsKey( LOCALE_KEYS_ATTRIBUTE )) {
                        it.attributes[LOCALE_KEYS_ATTRIBUTE] = new HashSet()

                        if (it.processedFile) {
                            def fileText

                            // Check to see if the file has been zipped.  This only occurs in the Environment.DEVELOPMENT
                            // If it occurs, we'll create a reference to the original file and parse it instead.
                            if (it.processedFile.path.endsWith(".gz")) {
                                def originalFile = new File( "${it.workDir}${it.sourceUrl}" )
                                if (originalFile.exists()) {
                                    fileText = originalFile.text
                                }
                                else {
                                    fileText = ""
                                }
                            }
                            else {
                                fileText = it.processedFile.text
                            }

                            def matcher = regex.matcher(fileText)
                            while (matcher.find()) {
                                it.attributes[LOCALE_KEYS_ATTRIBUTE] << matcher.group(1)
                            }
                        }
                    }

                    keys.addAll( it.attributes[LOCALE_KEYS_ATTRIBUTE] )
                }
            }
        } else {
            keys = ["default.calendar", "default.calendar1", "default.calendar2", "default.calendar.gregorian.ulocale", "default.calendar.islamic.ulocale", "default.date.format", "default.gregorian.dayNames", "default.gregorian.dayNamesMin", "default.gregorian.dayNamesShort", "default.gregorian.monthNames", "default.gregorian.monthNamesShort", "default.islamic.dayNames", "default.islamic.dayNamesMin", "default.islamic.dayNamesShort", "default.islamic.monthNames", "default.islamic.monthNamesShort", "default.language.direction", "js.datepicker.dateFormat", "default.century.pivot", "default.century.above.pivot", "default.century.below.pivot", "default.dateEntry.format", "js.datepicker.selectText", "js.datepicker.prevStatus", "js.datepicker.nextStatus","js.datepicker.yearStatus","js.datepicker.monthStatus","default.calendar.islamic.translation","default.calendar.gregorian.translation"]
            keys.addAll(addTimeKeys())
        }

        out << '\$.i18n.map = {'
            if (keys) {
                def javaScriptProperties = []
                keys.sort().each {
                    String msg = "${g.message(code: it)}"

                    // Assume the key was not found.  Look to see if it exists in the bundle
                    if (msg == it) {
                        def value = DateAndDecimalUtils.properties( RCU.getLocale( request ) )[it]

                        if (value) {
                            msg = value
                        }
                    }
                    if (msg && it != msg){
                        msg = encodeHTML(msg)
                        javaScriptProperties << "\"$it\": \"$msg\""
                    }
                }

                out << javaScriptProperties.join(",")
            }
            out << '};'
    }

    private addTimeKeys() {
        return ["default.time.am","default.time.pm","default.time.increment","default.time.decrement","default.time.format", "default.date.time.error"] as Set
    }
}