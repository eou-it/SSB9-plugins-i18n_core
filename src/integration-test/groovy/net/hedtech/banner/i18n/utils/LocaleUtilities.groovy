package net.hedtech.banner.i18n.utils

import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import grails.util.GrailsWebMockUtil
import org.springframework.web.context.support.WebApplicationContextUtils
import grails.web.context.ServletContextHolder

/**
 * Helper class for i18n locale specific integration testcases
 */
class  LocaleUtilities {

    public static void setLocale(String locale) {
        def webRequest = RequestContextHolder.currentRequestAttributes().request
        if(!webRequest) {
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            webRequest =  grails.util.GrailsWebMockUtil.bindMockWebRequest(applicationContext)
        }
        def localePieces = locale?.split("-")
        //however, for locales with language and country, better to go for Locale class constants,
        //like how it is done for CANADA_FRENCH for date converter test cases. Locale(language, country)
        // does not seem to be identified by UNIX environemnts; notably this constructor converts
        // country case to lower case; not sure if that is why there is side effect on UNIX env.
        //
        if (localePieces.size() == 2) {
            webRequest.addPreferredLocale(new Locale(localePieces[0], localePieces[1]))
        } else {
            webRequest.addPreferredLocale(new Locale(locale))
        }
    }

    public static void setLocale(Locale locale) {
        def webRequest = RequestContextHolder.currentRequestAttributes().request
        if(!webRequest) {
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            webRequest =  grails.util.GrailsWebMockUtil.bindMockWebRequest(applicationContext)
        }
        webRequest.addPreferredLocale(locale)

    }

    public static Enumeration<Locale> getLocales(String locale) {
        def webRequest = RequestContextHolder.currentRequestAttributes().request
        if(!webRequest) {
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            webRequest =  grails.util.GrailsWebMockUtil.bindMockWebRequest(applicationContext)
        }
        return webRequest.locales
    }
}
