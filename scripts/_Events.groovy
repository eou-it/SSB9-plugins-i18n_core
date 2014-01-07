/*******************************************************************************
 Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

eventCompileEnd = {

    if (new File("${basedir}/plugins/i18n_core.git").exists()) {

        /**
         If thie plugin is used in Admin modules, we need to have these JS files placed under
         src/java/web directory since that would be the location where ZK looks for the JS resources
         Also, the lang addon file needs to be copied on to the module that uses this plugin.
         * */
        // Verify ZK framework
        def zkFile = "${basedir}/web-app/WEB-INF/zk.xml"
        def targetLocation = "${i18nCorePluginDir}/src/java/web"


        if ((new File(zkFile).exists())) {

            println "i18n-core.. Copying files for ZK framework"
            if ((new File(targetLocation).exists())) {
                ant.delete(dir: targetLocation)
            }

            ant.copy(todir: targetLocation + "/js") {
                fileset(dir: "${i18nCorePluginDir}/web-app/js")
            }
            ant.copy(todir: targetLocation + "/css", overwrite: "true") {
                fileset(dir: "${i18nCorePluginDir}/web-app/css")
            }
            ant.copy(file: "${i18nCorePluginDir}/lang-addon/i18n-core-lang-addon.xml", tofile: "${basedir}/web-app/WEB-INF/i18n-core-lang-addon.xml", overwrite: "true")
        }
    }
}
