/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
//
// This script is executed by Grails when the plugin is uninstalled from project.
// Use this script if you intend to do any additional clean-up on uninstall, but
// beware of messing up SVN directories!
//

ant.delete(file:"${basedir}/web-app/WEB-INF/i18n-core-lang-addon.xml")