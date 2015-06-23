package net.hedtech.banner.i18n

import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Created by shaliyak on 6/19/2015.
 */
class CurrencyFormatterIntegrationTests {
def currencyFormatService

    @Test
    void testInjection() {
        assertNotNull currencyFormatService

    }

    @Test
    void testUSDPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("USD",new BigDecimal(1234567890.239)),"\$1,234,567,890.24"
        }

    @Test
    void testUSDNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("USD",new BigDecimal(-1234567890.239)),"(\$1,234,567,890.24)"
    }
    @Test
    void testAUDPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("AUD",new BigDecimal(1234567890.239)),"AU\$1,234,567,890.24"
    }

    @Test
    void testAUDNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("AUD",new BigDecimal(-1234567890.239)),"(AU\$1,234,567,890.24)"
    }
    @Test
    void testCADPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("CAD",new BigDecimal(1234567890.239)),"CA\$1,234,567,890.24"
    }

    @Test
    void testCADNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("CAD",new BigDecimal(-1234567890.239)),"(CA\$1,234,567,890.24)"
    }
    @Test
    void testGBPPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("GBP",new BigDecimal(1234567890.239)),"£1,234,567,890.24"
    }

    @Test
    void testGBPNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("GBP",new BigDecimal(-1234567890.239)),"(£1,234,567,890.24)"
    }
    @Test
    void testPTEPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("PTE",new BigDecimal(1234567890.239)),"PTE1,234,567,890.24"
    }

    @Test
    void testPTENegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("PTE",new BigDecimal(-1234567890.239)),"(PTE1,234,567,890.24)"
    }
    @Test
    void testEURPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("EUR",new BigDecimal(1234567890.239)),"€1,234,567,890.24"
    }

    @Test
    void testEURNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("EUR",new BigDecimal(-1234567890.239)),"(€1,234,567,890.24)"
    }

    @Test
    void testSARPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format("SAR",new BigDecimal(1234567890.239)),"SAR1,234,567,890.24"
    }

    @Test
    void testSARNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format("SAR",new BigDecimal(-1234567890.239)),"(SAR1,234,567,890.24)"
    }
    @Test
    void testInvalidCurrencyFormatter() {
        Exception exception = null

        try {
            currencyFormatService.format("XYZ",new BigDecimal(1234567890.239))
        } catch (CurrencyNotFoundException e) {
            exception = e;
        }
        assertTrue(exception.getMessage().contains("Invalid currency code"))
    }

}
