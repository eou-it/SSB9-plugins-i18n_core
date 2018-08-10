/*******************************************************************************
     Copyright 2009-2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

import grails.util.GrailsNameUtils
import groovy.io.FileType
import groovy.json.JsonSlurper


    //def grailsUserhome = System.getProperty('user.home') + '\\.grails\\2.5.0\\projects\\i18n-core\\plugins'
    def jsonSlurper = new JsonSlurper()

    //TODO
    //Object reportMessages = jsonSlurper.parseText('{ "exclude": ["default","typeMismatch"] ,"dynamic": []}' )
    Object reportMessages = jsonSlurper.parseText('{ "exclude": [] ,"dynamic": []}' )
    def parseVariants = { variants ->
        int runs = 0
        while (variants.find { it ==~ /.*?\$\{.*?\}.*/ } && runs++ < 10) {
            def newVariants = []
            variants.each { match ->
                def knownVariants = reportMessages.dynamicKeys.find { key, value ->
                    match.contains("\${${key}}") }
                if (knownVariants) {
                    knownVariants.value.each { variant ->
                        newVariants << match.replace("\${${knownVariants.key}}", variant) }
                } else if (match ==~ /.*?\?.*?:.*/) {
                    def replaced = false
                    newVariants << match.replaceAll(/\$\{.*?\?.*?["'](.*?)['"].*?:.*?\}/) { f, m ->
                        def result = replaced ? f : m; replaced = true; result }
                    replaced = false
                    newVariants << match.replaceAll(/\$\{.*?\?.*?:.*?["'](.*?)['"].*?\}/) { f, m ->
                        def result = replaced ? f : m; replaced = true; result }
                } else {
                    newVariants << match.replaceAll("\\\$", "#")
                }
            }
            variants = newVariants
        }
        variants
    }

    def escapeAndUpdateUsed = { variants, file, used ->
        variants.each {
            def key = it.replaceAll(/^\.(.*)$/) { f, m -> "#.$m"
            }.replaceAll(/^(.*)\.$/) { f, m -> "${m}.#" }
            def keyFiles = used."$key"
            if (!keyFiles) { keyFiles = used."$key" = [] as SortedSet}
            keyFiles << file.absolutePath
        }
    }

    int numberOfFiles = 0
    def gatherKeys = { folderName ->
        def used = [:]
        new File(folderName).eachFileRecurse { file ->
            if (!file.isDirectory()) {
                numberOfFiles++
                def text = file.text
                text.eachMatch(/message[\s\(].*?code.*?[=:].*?(?:"(.*?)"|'(.*?)')/) { full ->
                    def variants = [full[1] ?: full[2]]
                    if (variants[0] ==~ /^\$\{[^\}]*\}$/) { return }
                    variants = parseVariants(variants)
                    escapeAndUpdateUsed(variants, file, used)
                } } }
        used
    }
    def parseAssignment = { line, localVars ->
        def assignmentMatcher = line =~ /\s*?(?:\w*?\s*?)(\w+?)\s*?=\s*(.*)\s*/
        if (assignmentMatcher.matches()) {
            localVars[assignmentMatcher[0][1]] =
                    assignmentMatcher[0][2].replaceAll(/^.*?(?:"(.*?)"|'(.*?)').*$/) { f, m1, m2 -> m1 ?: m2 }
        }
        assignmentMatcher.matches()
    }

    def parseMessageTag = { line, localVars, file, used ->
        line.eachMatch(/message\(.*?code\s*:\s*(.*?)[\),]/) { full, match ->
            def variants = []
            if (match ==~ /^["'].*['"]$/) {
                variants << match[1..-2]
            } else {
                def var = localVars."$match"
                if (var) { variants << var }
            }
            if (!variants) {
                println "couldn't parse $match"
            } else {
                variants[0] = variants[0].replaceAll("[\"'].*['\"]", "\\\${dynamic}")
                if (variants[0] ==~ /^\$\{.*\}$/) { return }
                variants = parseVariants(variants)
                escapeAndUpdateUsed(variants, file, used)
            }
        } }

    def parseJavascriptFile = { line, localVars, file, used ->
        line.eachMatch(/\(*\.i18n.prop\(.*?([\'\"].*?[\'\"].*?)[\),]/) { full, match ->
            def variants = []

            if (match ==~ /^["'].*['"]$/) {
                variants << match[1..-2]
            } else {
                def var = localVars."$match"
                if (var) { variants << var }
            }
            if (!variants) {
                println "couldn't parse $match"
            } else {
                variants[0] = variants[0].replaceAll("[\"'].*['\"]", "\\\${dynamic}")
                if (variants[0] ==~ /^\$\{.*\}$/) { return }
                variants = parseVariants(variants)
                escapeAndUpdateUsed(variants, file, used)
            }
        } }

    def parseMessageSource = { line, localVars, file, used ->
        line.eachMatch(/getMessage\s*\(\s*(.*?)\s*[\),]/) { full, match ->
            def variants = []
            if (match ==~ /^["'].*['"]$/) {
                variants << match[1..-2]
            } else {
                def var = localVars."$match"
                if (var) { variants << var }
            }
            if (!variants) {
                println "couldn't parse $match"
            } else {
                variants[0] = variants[0].replaceAll("[\"'].*['\"]", "\\\${dynamic}")
                if (variants[0] ==~ /^\$\{.*\}$/) { return }
                variants = parseVariants(variants)
                escapeAndUpdateUsed(variants, file, used)
            }
        }
        line.eachMatch(/message\s*\(\s*(.*?)\s*[\),]/) { full, match ->
            def variants = []
            if (match ==~ /^["'].*['"]$/) {
                variants << match[1..-2]
            } else {
                def var = localVars."$match"
                if (var) { variants << var }
            }
            if (!variants) {
                println "couldn't parse $match"
            } else {
                variants[0] = variants[0].replaceAll("[\"'].*['\"]", "\\\${dynamic}")
                if (variants[0] ==~ /^\$\{.*\}$/) { return }
                variants = parseVariants(variants)
                escapeAndUpdateUsed(variants, file, used)
            }
        }
        line.eachMatch(/i18n:m\s*\(\s*(.*?)\s*\)/) { full, match ->
            def variants = []
            if (match ==~ /^["'].*['"]$/) {
                variants << match[1..-2]
            } else {
                def var = localVars."$match"
                if (var) { variants << var }
            }
            if (!variants) {
                println "couldn't parse $match"
            } else {
                variants[0] = variants[0].replaceAll("[\"'].*['\"]", "\\\${dynamic}")
                if (variants[0] ==~ /^\$\{.*\}$/) { return }
                variants = parseVariants(variants)
                escapeAndUpdateUsed(variants, file, used)
            }
        }
    }

    def gatherKeysFromSource = { folderName ->
        def used = [:]

        if (new File(folderName).exists()) {
            new File(folderName).eachFileRecurse { file ->
                if (!file.isDirectory()) {
                    numberOfFiles++
                    def localVars = [:]
                    println(file)
                    file.eachLine { line ->
                        parseAssignment(line, localVars)
                        parseMessageTag(line, localVars, file, used)
                        parseMessageSource(line, localVars, file, used)
                        parseJavascriptFile(line, localVars, file, used)
                    } } }
        }
        used
    }


    def baseDir = System.getProperty('user.dir')
    def base = baseDir+"\\grails-app\\i18n"
    def baseView = baseDir+"\\grails-app\\views"
    def basetaglib = baseDir+"\\grails-app\\taglib"
    def basecontrollers = baseDir+"\\grails-app\\controllers"
    def basecomposers = baseDir+"\\grails-app\\composers"
    def baseservices = baseDir+'\\grails-app\\services'
    def basegroovy= baseDir+"\\src\\main\\groovy"
    def basewebapp = baseDir+"\\grails-app\\assets"
    def messagesFile
    def used = [:]
    def defined = [] as Set

    if (args) {
        def lc = args.split("\n")[0]
        messagesFile = new File("${base}/messages_${lc}.properties")
        if (!messagesFile.exists()) {
            println "\nOops, locale '${lc}' does not exist yet! Please create ${base}/messages_${lc}.properties first.\n"
            return
        }
        defined.addAll(fetchAllKeysFromFiles(messagesFile))
    } else {
        messagesFile = new File("${base}")
        messagesFile.eachFileRecurse { file ->
            if (!file.isDirectory()) {
                defined.addAll(fetchAllKeysFromFiles(file))
            }
            //println("defined"+defined)
        }
    }

    used += gatherKeys(baseView);
    used += gatherKeysFromSource(basetaglib)
    used += gatherKeysFromSource(basecontrollers)
    used += gatherKeysFromSource(basecomposers)
    used += gatherKeysFromSource(baseservices)
    used += gatherKeysFromSource(basegroovy)
    used += gatherKeysFromSource(basewebapp)



    def missing = [] as Set
    def obsolete = [] as Set
    def dynamic = [] as Set
    def matched = [] as Set

    used.each { key, filenames ->
        if (key.contains("#")) {
            println key
            dynamic << key
        } else if (!(key in defined)) {
            missing << key
        } else {
            matched << key
        }
    }

    def domainClassNames = reportMessages.exclude


    missing = missing.findAll { key ->
        !domainClassNames.any { key.startsWith(it) } }

    //TODO Task for finding 18n keys in .grails folder
    /*def pluginDir = new File(System.getProperty('user.home') + '\\.grails\\2.5.0\\projects\\i18n-core\\plugins')

    def pluginProps = [] as Set
    if(pluginDir.exists()) {
        pluginDir.traverse(
                type: FileType.FILES,
                nameFilter: ~/messages\.properties/
        ) { srcFile ->
            srcFile.eachLine { line ->
                if (!line.trim().startsWith("#") && line.contains("=")) {
                    pluginProps << line.substring(0, line.indexOf("=")).trim()
                }
            }
        }
    }*/
    /*println("pluginProps"+pluginProps)
    def keysDefinedInPlugins = [] as Set
    missing.each { key ->
        if(pluginProps.contains(key)) {
            keysDefinedInPlugins << key
        }
    }

    println("keysDefinedInPlugins"+keysDefinedInPlugins)*/
    //missing = missing - keysDefinedInPlugins

    println "\n=================="
    println "== missing keys ==\n"

    missing.each { key ->
        def filenames = used."$key".collect {
            def name
            /*if (it.contains("views/")) { name = it.substring(it.indexOf("views/"))
            } else if (it.endsWith("groovy")) { name = it.substring(it.lastIndexOf("/")+1)
            } else if (it.endsWith("js")) { name = it.substring(it.lastIndexOf("/")+1)
            } else if (it.endsWith("gsp")) { name = it.substring(it.lastIndexOf("/")+1)
            }*/

            if (it.contains("views/")) {
                name = it.substring(it.indexOf("views/"))
            } else {
                name = it.substring(it.lastIndexOf("/")+1)
            }

            name
        }
        println "${key.padRight(50)} ${filenames[0]}"
        if (filenames.size() > 1) {
            filenames[1..-1].each { println " ".padLeft(51) + it }
        } }

    def unmatched = defined - matched

    def dynamicMatches = [:]
    def dynamicMissing = []

    dynamic.each { key ->
        if(key != "#.#") {
            def pattern = key.replaceAll("\\.", "\\\\.").replaceAll(/#\{.*?\}/) { ".*?" }.replaceAll("#", ".*?")
            println pattern
            def matches = unmatched.findAll { it ==~ /$pattern/ }

            if (matches) {
                dynamicMatches."$key" = matches
                matched += matches
            } else {
                dynamicMissing << key
            }
        }
    }

    println "\n=================="
    println "== dynamic keys =="
    dynamicMissing.each { key ->
        def filenames = used."$key".collect {
            def name
            /*if (it.contains("views/")) { name = it.substring(it.indexOf("views/"))
            } else if (it.endsWith("groovy")) { name = it.substring(it.lastIndexOf("/")+1) }*/
            if (it.contains("views/")) {
                name = it.substring(it.indexOf("views/"))
            } else {
                name = it.substring(it.lastIndexOf("/")+1)
            }
            name
        }
        println "\n${(key + ' (missing)').padRight(50)} ${filenames[0]}"
        if (filenames.size() > 1) {
            filenames[1..-1].each { println " ".padLeft(51) + it } }
    }

    dynamicMatches.each { key, matches ->
        def filenames = used."$key".collect {
            def name
            /*if (it.contains("views/")) { name = it.substring(it.indexOf("views/"))
            } else if (it.endsWith("groovy")) { name = it.substring(it.lastIndexOf("/")+1) }*/
            if (it.contains("views/")) {
                name = it.substring(it.indexOf("views/"))
            } else {
                name = it.substring(it.lastIndexOf("/")+1)
            }
            name
        }
        println "\n${key.padRight(50)} ${filenames[0]}"
        if (filenames.size() > 1) {
            filenames[1..-1].each { println " ".padLeft(51) + it } }

        matches.each { println " - $it" }
    }

    println "\n\n==================="
    println "== obsolete keys ==\n"
    unmatched = defined - matched

    /*if(new File("grails-app/domain").exists()) {
        new File("grails-app/domain").eachFileRecurse { file ->
            println file
            if (!file.isDirectory()) {
                domainClassNames << GrailsNameUtils.getPropertyName(file.name - ".groovy")
        } }
    }*/

    unmatched = unmatched.findAll { key ->
        !domainClassNames.any { key.startsWith(it) } }
    unmatched.each { println it }

    println "\nfound ${used.size()} keys in $numberOfFiles files"
    println "found ${defined.size()} defined keys\n"
    println "found ${missing.size()} missing keys"
    println "found ${dynamic.size()} dynamic keys (${dynamicMissing.size()} missing)"
    //println "found ${keysDefinedInPlugins.size()} keys in plugins"
    println "found ${unmatched.size()} obsolete keys"

    if (!args) {
        println"\nTo generate a report for a specific locale provide the exact language code as a parameter\nlike e.g. 'grails report-messages de'"
    }

    private Set fetchAllKeysFromFiles(File file) {
        def defined = [] as Set
        file.eachLine { line ->
            if (!line.trim().startsWith("#") && line.contains("=")) {
                defined << line.substring(0, line.indexOf("=")).trim()
            }
        }
        defined
    }


