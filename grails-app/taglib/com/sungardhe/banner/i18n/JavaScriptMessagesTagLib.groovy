/*********************************************************************************
 Copyright 2009-2012 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of 
 SunGard Higher Education and its subsidiaries. Any use of this software is limited 
 solely to SunGard Higher Education licensees, and is further subject to the terms 
 and conditions of one or more written license agreements between SunGard Higher 
 Education and the licensee in question. SunGard is either a registered trademark or
 trademark of SunGard Data Systems in the U.S.A. and/or other regions and/or countries.
 Banner and Luminis are either registered trademarks or trademarks of SunGard Higher 
 Education in the U.S.A. and/or other regions and/or countries.
 **********************************************************************************/
package com.sungardhe.banner.i18n

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

    def i18nJavaScript = { attrs ->

        if (request.resourceDependencyTracker) {
            Set keys = []

            // Search for any place where we are referencing message codes
            def regex = ~/\(*\.i18n.prop\(.*?[\'\"](.*?)[\'\"].*?\)/

            request.resourceDependencyTracker.each { name ->
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

            out << '\$.i18n.map = {'
            if (keys) {
                def javaScriptProperties = []
                keys.sort().each {
                    def msg = "${g.message(code: it)}"

                    // Assume the key was not found.  Look to see if it exists in the bundle
                    if (msg == it) {
                        def value = DateAndDecimalUtils.properties( RCU.getLocale( request ) )[it]

                        if (value) {
                            msg = value
                        }
                    }

                    msg = encodeHTML(msg)
                    javaScriptProperties << "\"$it\": \"$msg\""
                }

                out << javaScriptProperties.join(",")
            }
            out << '};'
        }
    }
}