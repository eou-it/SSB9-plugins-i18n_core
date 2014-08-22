/*******************************************************************************
Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
*******************************************************************************/ 
package net.hedtech.banner.i18n

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.i18n.LocaleContextHolder as LCH


/**
 * Helper class to retrieve getMessage values from the resource bundle managed by grails.
 */
class MessageHelper {

    // Developer note:   (DO NOT COPY/PASTE THIS NOTE ANYWHERE ELSE!)
    // While we have easy access to a grails application tag lib (either by instantiating or
    // retrieving from Spring), and using 'g.message( 'resourceCode' ) does correctly localize content,
    // it also results in a '500' being returned from a Composer.  This requires investigation but may be
    // the result of 'request scope' being used within g.message while the composer is session scope.
    //
    // Consequently, we won't use the grails app tag lib for localization, but will instead
    // use messageSource directly. If you need to use the tag library, just uncomment the following line:
    // def g = new ApplicationTagLib() // per GR post, just new-up one versus retrieving from Spring
    //
    // To facilitate localization within a Composer, a 'message' method akin to that available in a Controller
    // and a 'localizer' closure are provided. This essentially provides the same interface available
    // within a controller.

    private static def _messageSource

    /**
     * A 'localizer' closure that may be passed into an ApplicationException in order to localize error messages.
     * e.g.:  def returnMap = ae.returnMap( localizer ) // i.e., this is the same usage as within a Controller
     * */
    public static localizer = { Map args -> message(args) }


    public static String message(String resourceCode) {
        return getMessage(resourceCode)
    }


    public static String message(String resourceCode, Object arg1) {
        return getMessage(resourceCode, [arg1])
    }


    public static String message(String resourceCode, Object arg1, Object arg2) {
        return getMessage(resourceCode, [arg1, arg2])
    }


    public static String message(String resourceCode, Object arg1, Object arg2, Object arg3) {
        return getMessage(resourceCode, [arg1, arg2, arg3])
    }


    public static String message(String resourceCode, Object arg1, Object arg2, Object arg3, Object arg4) {
        return getMessage(resourceCode, [arg1, arg2, arg3, arg4])
    }


    public static String message(String resourceCode, Object[] args) {
        return getMessage(resourceCode, args)
    }


    public static String message(Map map) {

        String localizedMessage

        if (map.error) {
            localizedMessage = getMessageSource().getMessage(map.error, LCH.getLocale())
        }

        else if (map.code) {
            def defaultMessage = map.default != null ? map.default : map.code
            localizedMessage = getMessageSource().getMessage(map.code, map.args instanceof List? map.args?.toArray():map.args, defaultMessage, LCH.getLocale())
        }

        if (!localizedMessage) {
            localizedMessage = getMessageSource().getMessage("default.unknown.banner.api.exception", LCH.getLocale())
        }

        localizedMessage
    }


    private static String getMessage(resourceCode, args = null) {
        def foundCode = getMessageSource().resolveCode(resourceCode, LCH.getLocale())
        Object[] substitutionParameters = args ?: []
        String result = foundCode?.format(substitutionParameters)
        return result ?: resourceCode
    }


    private static def getMessageSource() {
        if (_messageSource == null) {
            ApplicationContext applicationContext = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
            _messageSource = applicationContext.getBean("messageSource")
        }

        return _messageSource
    }
}
