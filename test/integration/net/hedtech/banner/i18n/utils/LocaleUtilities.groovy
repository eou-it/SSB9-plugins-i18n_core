package net.hedtech.banner.i18n.utils

import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder

/**
 * Helper class for i18n locale specific integration testcases
 */
class LocaleUtilities {
    public static void setLocale(String locale) {
        GrailsMockHttpServletRequest request = RequestContextHolder.currentRequestAttributes().request
        request.addPreferredLocale(new Locale(locale))
    }
}
