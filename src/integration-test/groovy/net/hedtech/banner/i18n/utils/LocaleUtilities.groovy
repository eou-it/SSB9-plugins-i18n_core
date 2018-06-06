package net.hedtech.banner.i18n.utils

import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder

/**
 * Helper class for i18n locale specific integration testcases
 */
class LocaleUtilities {

    public static void setLocale(String locale) {
        GrailsMockHttpServletRequest request = RequestContextHolder.currentRequestAttributes().request
        def localePieces = locale?.split("-")
        //however, for locales with language and country, better to go for Locale class constants,
        //like how it is done for CANADA_FRENCH for date converter test cases. Locale(language, country)
        // does not seem to be identified by UNIX environemnts; notably this constructor converts
        // country case to lower case; not sure if that is why there is side effect on UNIX env.
        //
        if (localePieces.size() == 2) {
            request.addPreferredLocale(new Locale(localePieces[0], localePieces[1]))
        } else {
            request.addPreferredLocale(new Locale(locale))
        }
    }

    public static void setLocale(Locale locale) {
        GrailsMockHttpServletRequest request = RequestContextHolder.currentRequestAttributes().request
        request.addPreferredLocale(locale)

    }

    public static Enumeration<Locale> getLocales(String locale) {
        GrailsMockHttpServletRequest request = RequestContextHolder.currentRequestAttributes().request
        return request.locales
    }
}
