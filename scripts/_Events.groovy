/*********************************************************************************
 Copyright 2009-2012 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of 
 SunGard Higher Education and its subsidiaries. Any use of this software is limited 
 solely to SunGard Higher Education licensees, and is further subject to the terms 
 and conditions of one or more written license agreements between SunGard Higher 
 Education and the licensee in question. SunGard, Banner and Luminis are either 
 registered trademarks or trademarks of SunGard Higher Education in the U.S.A. 
 and/or other regions and/or countries.
 **********************************************************************************/

eventCompileEnd = {
    
    /**
    	If thie plugin is used in Admin modules, we need to have these JS files placed under
    	src/java/web directory since that would be the location where ZK looks for the JS resources
    	Also, the lang addon file needs to be copied on to the module that uses this plugin.
     **/
    println "i18n-core.. Verifying ZK framework" 
    def zkFile = "${basedir}/web-app/WEB-INF/zk.xml"
    def targetLocation = "${basedir}/plugins/i18n_core.git/src/java/web"
    
    
    if ((new File(zkFile).exists())) {
    
    	println "i18n-core.. Copying files for ZK framework" 
        if ((new File(targetLocation).exists())) {
            ant.delete(dir: targetLocation)
        }
    
        ant.copy(todir: targetLocation + "/js") {
            fileset(dir: "${basedir}/plugins/i18n_core.git/web-app/js")
        }
        ant.copy(todir: targetLocation + "/css") {
            fileset(dir: "${basedir}/plugins/i18n_core.git/web-app/css")
        }
        ant.copy(file: "${basedir}/plugins/i18n_core.git/web-app/i18n_properties.gsp", toFile:"${basedir}/web-app/i18n_properties.gsp")
        ant.copy(file: "${basedir}/plugins/i18n_core.git//src/java/metainfo/zk/lang-addon.xml", tofile: "${basedir}/web-app/WEB-INF/i18n-core-lang-addon.xml", overwrite: "true")
    }    
}