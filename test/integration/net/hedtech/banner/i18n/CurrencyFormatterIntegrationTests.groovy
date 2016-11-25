/*******************************************************************************
 Copyright 2009-2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.junit.Test
import org.junit.After
import org.springframework.context.i18n.LocaleContextHolder as LCH
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class CurrencyFormatterIntegrationTests {
def currencyFormatService
    private final String POSITIVE_TEST_VALUE = "1234567890.239"
    private final String NEGATIVE_TEST_VALUE ="-1234567890.239"
    private final String USD ="USD"
    private final String AUD ="AUD"
    private final String CAD ="CAD"
    private final String GBP ="GBP"
    private final String PTE ="PTE"
    private final String EUR ="EUR"
    private final String SAR ="SAR"
    private final String USD_POS_TEST_VALUE = "\$1,234,567,890.24"
    private final String USD_NEG_TEST_VALUE = "-\$1,234,567,890.24"
    private final String AUD_POS_TEST_VALUE = "A\$1,234,567,890.24"
    private final String AUD_NEG_TEST_VALUE = "-A\$1,234,567,890.24"
    private final String CAD_POS_TEST_VALUE = "CA\$1,234,567,890.24"
    private final String CAD_NEG_TEST_VALUE = "-CA\$1,234,567,890.24"
    private final String GBP_POS_TEST_VALUE = "£1,234,567,890.24"
    private final String GBP_NEG_TEST_VALUE = "-£1,234,567,890.24"
    private final String PTE_POS_TEST_VALUE = "PTE1,234,567,890.24"
    private final String PTE_NEG_TEST_VALUE = "-PTE1,234,567,890.24"
    private final String EUR_POS_TEST_VALUE = "€1,234,567,890.24"
    private final String EUR_NEG_TEST_VALUE = "-€1,234,567,890.24"
    private final String SAR_POS_TEST_VALUE = "SAR1,234,567,890.24"
    private final String SAR_NEG_TEST_VALUE = "-SAR1,234,567,890.24"
    private final String FR_POS_TEST_VALUE  = "1 234 567 890,24 \$US"
    private final String FRCA_POS_TEST_VALUE= "1 234 567 890,24 \$ US"
    private final String PT_NEG_TEST_VALUE = "-US\$1.234.567.890,24"
    private final String ES_NEG_TEST_VALUE = "-1.234.567.890,24 \$"
    private final String INVALID_CODE ="XYZ"
    private final String AR = "ar"
    public final String EN = "en"
    public final String US = "US"
    public final String FRCA = "fr-CA"
    public final String FR = "fr"
    public final String PT = "pt"
    public final String ES = "es"


    @After
    public void tearDown() {
        LCH.setLocale(new Locale(EN, US))

    }

    @Test
    void testInjection() {
        assertNotNull currencyFormatService

    }

    @Test
    void testUSDForArabicLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(AR))
        assertEquals currencyFormatService.format(USD,new BigDecimal(POSITIVE_TEST_VALUE)),USD_POS_TEST_VALUE
    }
    @Test
    void testUSDForArabicLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(AR))
        assertEquals currencyFormatService.format(USD,new BigDecimal(NEGATIVE_TEST_VALUE)),USD_NEG_TEST_VALUE
    }
    @Test
    void testUSDPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(USD,new BigDecimal(POSITIVE_TEST_VALUE)),USD_POS_TEST_VALUE
        }

    @Test
    void testUSDNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(USD,new BigDecimal(NEGATIVE_TEST_VALUE)),USD_NEG_TEST_VALUE
    }

    @Test
    void testAUDPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(AUD,new BigDecimal(POSITIVE_TEST_VALUE)),AUD_POS_TEST_VALUE
    }

    @Test
    void testAUDNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(AUD,new BigDecimal(NEGATIVE_TEST_VALUE)),AUD_NEG_TEST_VALUE
    }
    @Test
    void testCADPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(CAD,new BigDecimal(POSITIVE_TEST_VALUE)),CAD_POS_TEST_VALUE
    }

    @Test
    void testCADNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(CAD,new BigDecimal(NEGATIVE_TEST_VALUE)),CAD_NEG_TEST_VALUE
    }
    @Test
    void testGBPPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(GBP,new BigDecimal(POSITIVE_TEST_VALUE)),GBP_POS_TEST_VALUE
    }

    @Test
    void testGBPNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(GBP,new BigDecimal(NEGATIVE_TEST_VALUE)),GBP_NEG_TEST_VALUE
    }
    @Test
    void testPTEPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(PTE,new BigDecimal(POSITIVE_TEST_VALUE)),PTE_POS_TEST_VALUE
    }

    @Test
    void testPTENegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(PTE,new BigDecimal(NEGATIVE_TEST_VALUE)),PTE_NEG_TEST_VALUE
    }
    @Test
    void testEURPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(EUR,new BigDecimal(POSITIVE_TEST_VALUE)),EUR_POS_TEST_VALUE
    }

    @Test
    void testEURNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(EUR,new BigDecimal(NEGATIVE_TEST_VALUE)),EUR_NEG_TEST_VALUE
    }

    @Test
    void testSARPositiveCurrencyFormatter() {
        assertEquals currencyFormatService.format(SAR,new BigDecimal(POSITIVE_TEST_VALUE)),SAR_POS_TEST_VALUE
    }

    @Test
    void testSARNegativeCurrencyFormatter() {
        assertEquals currencyFormatService.format(SAR,new BigDecimal(NEGATIVE_TEST_VALUE)),SAR_NEG_TEST_VALUE
    }

    @Test
    void testUSDForCanadianFrenchLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(FRCA))
        assertEquals FRCA_POS_TEST_VALUE, currencyFormatService.format(USD,new BigDecimal(POSITIVE_TEST_VALUE))
    }

    @Test
    void testUSDForFrenchLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(FR))
        assertEquals FR_POS_TEST_VALUE, currencyFormatService.format(USD,new BigDecimal(POSITIVE_TEST_VALUE))
    }

    @Test
    void testUSDForPortugueseLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(PT))
        assertEquals currencyFormatService.format(USD,new BigDecimal(NEGATIVE_TEST_VALUE)),PT_NEG_TEST_VALUE
    }
    @Test
    void testUSDForSpanishLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES))
        assertEquals currencyFormatService.format(USD,new BigDecimal(NEGATIVE_TEST_VALUE)),ES_NEG_TEST_VALUE
    }

    @Test
    void testInvalidCurrencyFormatter() {
        Exception exception = null

        try {
            currencyFormatService.format(INVALID_CODE,new BigDecimal(POSITIVE_TEST_VALUE))
        } catch (CurrencyNotFoundException e) {
            exception = e;
        }
        assertTrue(exception.getMessage().contains(INVALID_CODE))
    }

}
