/*******************************************************************************
 Copyright 2009-2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import grails.core.GrailsApplication
import grails.util.Environment
import groovy.io.FileVisitResult
import org.springframework.web.servlet.support.RequestContextUtils

import static groovy.io.FileType.FILES

/**
 * This class is built off to populate the i18n map.
 * It's goal is to scan all the JS files excluding the modules and minified files
 * for localization call outs and provide them in the i18n map on the client.
 * The messages can be rendered using $.i18n.map("key") or $.i18n.prop("key")
 */
class JavaScriptMessagesTagLib {
    public static boolean loadJSFiles = true
    public static Set keys = []
    public static List jsFiles=[]

    def encodeHTML(msg) {
        msg.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")
    }

    void getJsFilesList(GrailsApplication grailsApplication){
        String appDirPath = getCurrentDirectoryPath(grailsApplication)

        final excludedDirs = ['.git', 'gradle', '.idea', 'node_modules', '.gradle', 'build', 'modules', 'd3', 'target','images']

        new File(appDirPath).traverse(
                type: FILES,
                preDir: {if (it.name in excludedDirs) return FileVisitResult.SKIP_SUBTREE}, // excludes children of excluded dirs
                excludeNameFilter: { it in excludedDirs }, // excludes the excluded dirs as well
                nameFilter: ~/.*.js/,// matched only given names
        ) { jsFiles << it }
    }


    private String getCurrentDirectoryPath(GrailsApplication grailsApplication) {
        String dirPath = ''
        if (Environment.current == Environment.PRODUCTION || Environment.current == Environment.TEST) {
            dirPath = grailsApplication.mainContext.servletContext.getRealPath('/')
        } else if (Environment.current == Environment.DEVELOPMENT) {
            dirPath = System.properties['user.dir']
        }
        return dirPath
    }


    def i18nJavaScript = { attrs ->
        if (loadJSFiles) {
            loadJSFiles = false
            def regex = ~/\(*\.i18n.prop\(.*?[\'\"](.*?)[\'\"].*?\)|['"]([\w\d\s.-]*)['"]\s*\|\s*xei18n|[\$]filter\s*\(\s*['"]xei18n['"]\s*\)\s*\(\s*['"]([\w\d\s.-]+)['"].*?|([\w\d\s.-]*)['"]xei18n['"]\s*\)\s*\(\s*['"]([\w\d\s.-]+)['"].*?\)/
            jsFiles?.each { jsLoadedFile ->
                HashSet localeKeys = new HashSet()
                def fileText = jsLoadedFile.text
                def matcher = regex.matcher(fileText)
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
        return ["default.time.am","default.time.pm","default.time.increment","default.time.decrement","default.time.format", "default.date.time.error", "default.timepicker.time.format"] as Set
    }
}
