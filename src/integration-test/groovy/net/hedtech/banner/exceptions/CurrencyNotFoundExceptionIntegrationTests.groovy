/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.exceptions

import com.ibm.icu.util.Currency
import grails.testing.mixin.integration.Integration

//import grails.test.GrailsUnitTestCase
import org.junit.Assert
import org.junit.Test

/**
 * Created by arunu on 12/7/2016.
 */
@Integration
class CurrencyNotFoundExceptionIntegrationTests {

    private static final String USD = "USD"

    @Test
    void testGetMessage() {
        def currencyCode = USD + 'test'
        try {
            Assert.assertTrue(format(USD + 'test'))
        } catch (e) {
            Assert.assertNotNull(e.getMessage())
            Assert.assertEquals(e.toString(), e.getMessage())
        }
    }

    private def format(def currencyCode) throws CurrencyNotFoundException {
        if (isInvalidCurrencyCode(currencyCode)) {
            throw new CurrencyNotFoundException()
        } else {
            return true
        }
    }

    private boolean isInvalidCurrencyCode(String currencyCode) {
        Set<Currency> lstValidCurrencyCode = Currency.getAvailableCurrencies()
        currencyCode = currencyCode?.toUpperCase()
        return lstValidCurrencyCode.findAll { it?.isoCode.equals(currencyCode) }.size() < 1
    }

}
