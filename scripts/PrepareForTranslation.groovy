/*********************************************************************************
 Copyright 2013 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
target(main: "Creates translation folder and copies the messages.properteis file from the app and plugins that have changes") {

    new File("translation").mkdir()
    ant.copy(file: "grails-app/i18n/messages.properties", toFile: "translation/messages.properties")

    // Find our list of plugins
    File plugins = new File("plugins")
    plugins.eachFile { plugin ->
        println "Processing " + plugin
        Boolean copyNeeded = false

        // see if ther is a messages.properties in the plugin
        File messageFile = new File(plugin, "grails-app/i18n/messages.properties")
        if (messageFile.exists()) {

            // see if there is a _fr messages.properties. If so, check to see if it's been modified since the last time the
            // english one was.
            File frenchFile = new File(plugin, "grails-app/i18n/messages_fr.properties")
            if (frenchFile.exists()) {
                if (frenchFile.lastModified() < messageFile.lastModified()) {
                    println "    Plugin needs another translation. File in development is more recent than translations."
                    copyNeeded = true
                }
                else {
                    println "    No translation needed."
                }

            }
            else {
                println "    Plugin is missing French translation."
                copyNeeded = true
            }
        }
        else {
            println "    No messages."
        }
        if (copyNeeded) {
            Boolean pluginTargetDirectoryCreated = new File("translation/" + plugin.getName()).mkdir()
            ant.copy(file: messageFile.canonicalFile, toFile: "translation/" + plugin.getName() + "/messages.properties")
        }

    }
}

setDefaultTarget(main)
