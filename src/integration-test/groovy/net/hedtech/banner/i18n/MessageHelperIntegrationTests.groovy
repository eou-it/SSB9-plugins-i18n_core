/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.i18n

import grails.testing.mixin.integration.Integration
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.DefaultMessageSourceResolvable

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

@Integration
class MessageHelperIntegrationTests {
    def messageHelper
    public final String EN = "en"
    public final String IN = "IN"
    public final String US = "US"
    def message

    @Before
    public void setUp() {
        LocaleContextHolder.setLocale(new Locale(EN, IN))
        messageHelper = new MessageHelper()
    }

    @After
    public void tearDown() {
        LocaleContextHolder.setLocale(new Locale(EN, US))
        message=null
    }

    @Test
    void testGetMessage() {
        message=messageHelper.message("default.invalid.currency.format.code")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageOneArg() {
        message=messageHelper.message("default.invalid.currency.format.code", "arg1")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithTwoArgs() {
        messageHelper.message("default.invalid.currency.format.code", "arg1", "arg2")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithThreeArgs() {
        message=messageHelper.message("default.invalid.currency.format.code", "arg1", "arg2", "arg3")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithFourArgs() {
        message=messageHelper.message("default.invalid.currency.format.code", "arg1", "arg2", "arg3", "arg4")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithMapCodeNonLocalized() {
        message=messageHelper.message(code: "default.invalid.currency.format.code")
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithMapError() {
        MessageSourceResolvable resolvable=new DefaultMessageSourceResolvable("default.invalid.currency.format.code")
        message=messageHelper.message(error: resolvable)
        assertNotEquals("default.invalid.currency.format.code",message)
    }

    @Test
    void testGetMessageWithMapCode() {
        def map=[code:"default.invalid.currency.format.code",args:null,default:"default.invalid.currency.format.code"]
        message=messageHelper.message(map)
        assertNotEquals("default.invalid.currency.format.code",message)
    }
    @Test
   void testGetMessageWithResolveCode() {
        message=messageHelper.getMessage("invalid.currency.format.code",null);
        assertEquals("invalid.currency.format.code",message)
   }
}
