/*******************************************************************************
 Copyright 2009-2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.util.Environment
import org.apache.commons.io.FileUtils
import org.springframework.web.servlet.support.RequestContextUtils


/**
 * This class is built off to populate the i18n map.
 * It's goal is to scan all the JS files excluding the modules and minified files
 * for localization call outs and provide them in the i18n map on the client.
 * The messages can be rendered using $.i18n.map("key") or $.i18n.prop("key")
 */
class JavaScriptMessagesTagLib {

    def encodeHTML(msg) {
        msg.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
    }


    String getCurrentDirectoryPath() {
        String dirPath = ''
        if (Environment.current == Environment.PRODUCTION || Environment.current == Environment.TEST) {
            dirPath = grailsApplication.mainContext.servletContext.getRealPath('/')
        } else if (Environment.current == Environment.DEVELOPMENT) {
            dirPath = System.properties['user.dir']
        }
        return dirPath
    }


    def i18nJavaScript = { attrs ->
        Set keys = []
        def regex = ~/\(*\.i18n.prop\(.*?[\'\"](.*?)[\'\"].*?\)|['"]([\w\d\s.-]*)['"]\s*\|\s*xei18n|[\$]filter\s*\(\s*['"]xei18n['"]\s*\)\s*\(\s*['"]([\w\d\s.-]+)['"].*?\)/
        String appDirPath = getCurrentDirectoryPath()
        String[] jsExtension = ["js"] as String[]
        List<File> jsFilesList = (List<File>) FileUtils.listFiles(new File(appDirPath), jsExtension, true)

        jsFilesList?.each { jsLoadedFile ->
            HashSet localeKeys = new HashSet()

            if (jsLoadedFile.exists() && !(jsLoadedFile.path.endsWith('-mf.js') || jsLoadedFile.path.endsWith('.min.js')) ){
                def  fileText = jsLoadedFile.text
                def matcher = regex.matcher(fileText)
                //TODO :grails_332_change, needs to revisit
               /*if (it.processedFile) {
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

                }*/
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        localeKeys << matcher.group(1)
                    }
                    if (matcher.group(2) != null) {
                        localeKeys << matcher.group(2)
                    }
                    if (matcher.group(3) != null) {
                        localeKeys << matcher.group(3)
                    }
                }
                keys.addAll(localeKeys)
            }
        }
        if(keys.isEmpty()){
            keys = ["default.calendar", "default.calendar1", "default.calendar2", "default.calendar.gregorian.ulocale",
                    "default.calendar.islamic.ulocale", "default.date.format", "default.gregorian.dayNames", "default.gregorian.dayNamesMin",
                    "default.gregorian.dayNamesShort", "default.gregorian.monthNames", "default.gregorian.monthNamesShort", "default.gregorian.amPm",
                    "default.islamic.dayNames", "default.islamic.dayNamesMin", "default.islamic.dayNamesShort", "default.islamic.monthNames",
                    "default.islamic.monthNamesShort", "default.islamic.amPm", "default.language.direction", "js.datepicker.dateFormat", "default.century.pivot",
                    "default.century.above.pivot", "default.century.below.pivot", "default.dateEntry.format", "js.datepicker.selectText",
                    "js.datepicker.prevStatus", "js.datepicker.nextStatus","js.datepicker.yearStatus","js.datepicker.monthStatus",
                    "default.calendar.islamic.translation","default.calendar.gregorian.translation", "default.firstDayOfTheWeek",
                    "js.datepicker.datetimeFormat","js.input.datepicker.dateformatinfo","js.input.datepicker.info","js.datepicker.info",
                    "default.calendar.ummalqura.ulocale", "default.ummalqura.dayNames", "default.ummalqura.dayNamesMin", "default.ummalqura.dayNamesShort",
                    "default.ummalqura.monthNames","default.ummalqura.monthNamesShort", "default.ummalqura.amPm","default.calendar.ummalqura.translation"
            ]
            keys.addAll(addTimeKeys())
        }

        out << '\$.i18n.map = {'
        if (keys) {
            def javaScriptProperties = []
            keys.sort().each {
                String msg = "${g.message(code: it)}"

                // Assume the key was not found.  Look to see if it exists in the bundle
                if (msg == it) {
                    def value = DateAndDecimalUtils.properties( RequestContextUtils.getLocale( request ) )[it]

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
