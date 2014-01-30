/*********************************************************************************
 Copyright 2013 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

def dummyArabicCharacters = ["\u0645", "\u0639", "\u0644", "\u062D", "\u062F", "\u0625", "\u0626", "\u0627", "\u0628", "\u0639"]
def mainArabicProperties = new Properties()

def generateFakeProperty(def fromThis, def arabicCharacterList) {
    String arabicString = fromThis == "" ? "" : "0"
    Integer wordNumber = 1
    def random = new Random()
    def chars = fromThis.getChars()
    for (int i=0;i<chars.size();i++)
    {
        def currentChar = chars[i]
        if (currentChar.isWhitespace()) {
            arabicString += " " + wordNumber
            wordNumber++
        }
        else
        {
            arabicString += arabicCharacterList[random.nextInt(10)]
        }
    }
    return arabicString
}


def toUnicode(def thisString) {
    def returnString = ""
    thisString.each{ c ->
        returnString += String.format ("\\u%04x", (int)c)
    }
    return returnString
}


def processPlugin(plugin, mainArabicProperties, arabicCharacterList) {

    // Load up the main arabic properties file from the grails app
    def arabicProperties = new Properties()
    Boolean needReplacing = false
    File arabicMessageFile = new File(plugin, "grails-app/i18n/messages_ar.properties")
    if (arabicMessageFile.exists()) {
        arabicMessageFile.withInputStream { stream ->
            arabicProperties.load(stream)
        }
    }

    // Load in the main properties file
    def messagesProperties = new Properties()
    File messageFile = new File(plugin, "grails-app/i18n/messages.properties")
    if (messageFile.exists()) {
        messageFile.withInputStream { stream ->
            messagesProperties.load(stream)
        }
    }

    messagesProperties.propertyNames().each { propertyName ->
        if (!arabicProperties.getProperty(propertyName)) {
            needReplacing = true
            arabicProperties.setProperty(propertyName,
                    generateFakeProperty(messagesProperties.getProperty(propertyName), arabicCharacterList))
        }
    }

    if (needReplacing) {
        println "Generating new grails-app/i18n/messages_ar.properties for plugin: " + plugin


        arabicMessageFile.write("#GENERATED FILE!!! DO NOT CHECK IN!!!\n")

        arabicProperties.propertyNames().each { propertyName ->
            String arabicString = propertyName + "=" + toUnicode(arabicProperties.getProperty(propertyName))
            arabicMessageFile.append(arabicString)
            arabicMessageFile.append("\n")
        }

    }
}

/**
 * Takes all of the missing arabic strings from the application and all of its plugins and adds those strings to the
 * applications messages_ar.properties file.
 */
target(main: "Provides fake translations of missing Arabic properties. Used for RTL testing.") {
    // Load up the main arabic properties file from the grails app
    File mainArabicMessageFile = new File("grails-app/i18n/messages_ar.properties")
    if (mainArabicMessageFile.exists()) {
        mainArabicMessageFile.withInputStream { stream ->
            mainArabicProperties.load(stream)
        }
    }

    // Load in the main properties file
    def messagesProperties = new Properties()
    File messageFile = new File("grails-app/i18n/messages.properties")
    if (messageFile.exists()) {
        messageFile.withInputStream { stream ->
            messagesProperties.load(stream)
        }
    }

    messagesProperties.propertyNames().each { propertyName ->
        if (!mainArabicProperties.getProperty(propertyName)) {
            mainArabicProperties.setProperty(propertyName, generateFakeProperty(messagesProperties.getProperty(propertyName), dummyArabicCharacters))
        }
    }

    // Find our list of plugins
    File plugins = new File("plugins")
    plugins.eachFile { plugin ->
        processPlugin(plugin, mainArabicProperties, dummyArabicCharacters)
    }

    println "Generating new grails-app/i18n/messages_ar.properties"
    mainArabicMessageFile.write("#GENERATED FILE!!! DO NOt CHECK IN!!!\n")

    mainArabicProperties.propertyNames().each { propertyName ->
        def arabicString = propertyName + "=" + toUnicode(mainArabicProperties.getProperty(propertyName))
        mainArabicMessageFile.append(arabicString)
        mainArabicMessageFile.append("\n")
    }
}

setDefaultTarget(main)
