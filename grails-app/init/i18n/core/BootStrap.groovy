package i18n.core

import grails.core.GrailsApplication
import net.hedtech.banner.i18n.JavaScriptMessagesTagLib

class BootStrap {

    def javaScriptMessagesTagLib = new JavaScriptMessagesTagLib()
    def grailsApplication
    def init = { servletContext ->
        javaScriptMessagesTagLib.getJsFilesList(grailsApplication)

    }
    def destroy = {
    }
}
