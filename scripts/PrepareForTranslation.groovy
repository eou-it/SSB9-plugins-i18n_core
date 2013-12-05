/*********************************************************************************
 Copyright 2013 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

def supportedLocales = ["en_AU", "en_IE", "es", "fr_CA", "ar", "en_GB", "en_IN", "fr", "pt"]

// This array will contain the results of our processing. Each element will be a map containing:
// pluginName: In the form plugins/<name>. For the main app, this will be "application"
// result: true, means needs translation. false, means doeesn't
// missingMessages: An array of missing messages map
//    [] where each element is a map
//        message: The message
//        locales: An array of locales for which this message is missing.
def translationsResults = []

def processPlugin = { plugin ->
    if (plugin == ".") {
        println "Processing application..."
    }
    else {
        println "Processing " + plugin + "..."
    }

    Boolean needsTranslation = false

    File messageFile = new File(plugin, "grails-app/i18n/messages.properties")
    if (messageFile.exists()) {
        def messagesProperties = new Properties()
        messageFile.withInputStream { stream ->
            messagesProperties.load(stream)
        }

        // Open up a file for each of our supported locales. If there is no file for a locale, then it needs a complete
        // translation
        def translationFileMapList = [] // Each element in the list will contain: locale: <locale from above>,
        // fileName: messages_<locale>.properties, file: The actual file for this plugin
        supportedLocales.each{ locale ->
            def localeFile = new File(plugin, "grails-app/i18n/messages_" + locale + ".properties")
            def otherMessagesProperties = new Properties()
            if (localeFile.exists()) {
                localeFile.withInputStream { stream ->
                    otherMessagesProperties.load(stream)
                }
                translationFileMapList << [locale: locale, fileName: "messages_" + locale + ".properties",
                        file: localeFile, properties: otherMessagesProperties]
            }
            else {
                translationFileMapList << [locale: locale, fileName: "messages_" + locale + ".properties",
                        file: null, properties: otherMessagesProperties]
            }
        }
        def missingPropertiesList = []
        messagesProperties.propertyNames().each { propertyName ->
            def missingFromThese = []
            translationFileMapList.each { it ->
                def value = it.properties.get(propertyName)
                if (null == value) {
                    //println "   Q### Plugin " + plugin + " missing " + propertyName + " for locale " + it.locale
                    missingFromThese.add(it.locale)
                }
            }
            if (!missingFromThese.isEmpty()) {
                missingPropertiesList << [message: propertyName, locales: missingFromThese]
            }
        }
        if (!missingPropertiesList.isEmpty()) {
            translationsResults << [pluginName: plugin, result: true, missingMessages: missingPropertiesList]
        }
        else {
            translationsResults << [pluginName: plugin, result: false, missingMessages: []]
        }
    }
    else {
        // no translation needed for this plugin because it doesn't have messages
        translationsResults << [pluginName: plugin, result: false, missingMessages: []]
    }
}

def copyFile(def pluginName) {
    File messageFile = new File(pluginName, "grails-app/i18n/messages.properties")
    println "File needs translating " + messageFile.absolutePath
    ant.copy(file: messageFile.canonicalFile, toFile: "target/translation/" + pluginName + "/messages.properties")
}

target(main: "Creates translation folder and copies the messages.properteis file from the app and plugins that have changes") {

    def output = new File( "target/translation-report.html" )

    processPlugin(".")

    // Find our list of plugins
    File plugins = new File("plugins")
    plugins.eachFile { plugin ->
        processPlugin(plugin)
    }

    println "Generating report at " + output.absolutePath

    output.write """
	    <html>
	        <body>
	            <head>
	                <title>Translation Report</title>
	                <style type="text/css">
	                    table.report {
	                        border-width: 1px;
	                        border-spacing: 1px;
	                        border-style: outset;
	                        border-color: gray;
	                        border-collapse: separate;
	                        background-color: white;
	                    }
	                    table.report th {
	                        border-width: 1px;
	                        padding: 2px;
	                        border-style: inset;
	                        border-color: gray;
	                        background-color: lightblue;
	                        -moz-border-radius: 0px 0px 0px 0px;
	                        white-space: nowrap;
	                    }
	                    table.report td {
	                        border-width: 1px;
	                        padding: 2px;
	                        border-style: inset;
	                        border-color: gray;
	                        background-color: white;
	                        -moz-border-radius: 0px 0px 0px 0px;
	                    }
	                </style>
	            </head>
	            <h2>Translation Report</h2>
	            Generated: ${new Date()}
"""

    translationsResults.each { result ->
        if (result.result) {
            copyFile(result.pluginName)
            def nameToUse = result.pluginName
            if (nameToUse == ".") {
                output.append "<h3>Application needs translation and is missing <i>'" + result.missingMessages.size() + "'</i> messages.</h3>"
            }
            else {
                output.append "<h3>Plugin needs translation and is missing <i>'" + result.missingMessages.size() + "'</i> messages: <strong>" + result.pluginName + "</strong></h3>"
            }
            output.append """
    <table class="report">
        <thead>
            <th>Message</th>
            <th>Missing Locales</th>
        </thead>
        <tfoot>
            <tr>
                <td colspan="2">Total: ${result.missingMessages.size} Missing Messages</td>
            </tr>
        </tfoot>
        <tbody>
"""
            result.missingMessages.each { missing ->
                def localesString = ""
                missing.locales.each { locale ->
                    localesString = localesString + locale + " "
                }
                output.append "<tr><td>" + missing.message + "</td><td>" + localesString + "</td>"
            }
            output.append "</tbody>"
        }
        output.append "</table>"
    }
    println "Done with report! Files for translating copied to target/translation"

}

setDefaultTarget(main)
