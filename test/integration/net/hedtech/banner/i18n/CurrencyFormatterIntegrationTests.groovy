/*******************************************************************************
 Copyright 2009-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n


import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.junit.Test
import org.junit.After
import org.springframework.context.i18n.LocaleContextHolder as LCH



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
    private final String CLP = "CLP"
    private final String COP = "COP"
    private final String CRC = "CRC"
    private final String MXN = "MXN"
    private final String PEN = "PEN"
    private final String DOP = "DOP"
    private final String VEF = "VEF"
    private final String ARS = "ARS"
    private final String GTQ = "GTQ"
    private final String PAB = "PAB"

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
    private final String ES_CL_POS_TEST_VALUE = "\$1.234.567.890"
    private final String ES_CL_NEG_TEST_VALUE = "\$-1.234.567.890"
    private final String ES_CO_POS_TEST_VALUE = "\$ 1.234.567.890"
    private final String ES_CO_NEG_TEST_VALUE = "-\$ 1.234.567.890"
    private final String ES_CR_POS_TEST_VALUE = "₡1.234.567.890"
    private final String ES_CR_NEG_TEST_VALUE = "-₡1.234.567.890"
    private final String ES_EC_POS_TEST_VALUE = "\$1.234.567.890,24"
    private final String ES_EC_NEG_TEST_VALUE = "-\$1.234.567.890,24"
    private final String ES_MX_POS_TEST_VALUE = "\$1,234,567,890.24"
    private final String ES_MX_NEG_TEST_VALUE = "-\$1,234,567,890.24"
    private final String ES_PE_POS_TEST_VALUE = "S/.1,234,567,890.24"
    private final String ES_PE_NEG_TEST_VALUE = "-S/.1,234,567,890.24"
    private final String ES_PR_POS_TEST_VALUE = "\$1,234,567,890.24"
    private final String ES_PR_NEG_TEST_VALUE = "-\$1,234,567,890.24"
    private final String ES_DO_POS_TEST_VALUE = "\$1,234,567,890.24"
    private final String ES_DO_NEG_TEST_VALUE = "-\$1,234,567,890.24"
    private final String ES_VE_POS_TEST_VALUE = "Bs.1.234.567.890,24"
    private final String ES_VE_NEG_TEST_VALUE = "-Bs.1.234.567.890,24"
    private final String ES_AR_POS_TEST_VALUE = "\$ 1.234.567.890,24"
    private final String ES_AR_NEG_TEST_VALUE = "-\$ 1.234.567.890,24"
    private final String ES_GT_POS_TEST_VALUE = "Q1,234,567,890.24"
    private final String ES_GT_NEG_TEST_VALUE = "-Q1,234,567,890.24"
    private final String ES_PA_POS_TEST_VALUE = "B/.1,234,567,890.24"
    private final String ES_PA_NEG_TEST_VALUE = "-B/.1,234,567,890.24"

    private final String INVALID_CODE ="XYZ"
    private final String AR = "ar"
    private final String ARSA = "ar_SA"
    public final String EN = "en"
    public final String US = "US"
    public final String FRCA = "fr-CA"
    public final String FR = "fr"
    public final String PT = "pt"
    public final String ES = "es"
    public final String CL = "CL"
    public final String CO = "CO"
    public final String CR = "CR"
    public final String EC = "EC"
    public final String MX = "MX"
    public final String PE = "PE"
    public final String PR = "PR"
    public final String DO = "DO"
    public final String VE = "VE"
    public final String ARGENTINA = "AR"
    public final String GT = "GT"
    public final String PA = "PA"


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
    void testUSDForSaudiArabicLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ARSA))
        assertEquals currencyFormatService.format(USD, new BigDecimal(POSITIVE_TEST_VALUE)), USD_POS_TEST_VALUE
    }

    @Test
    void testUSDForSaudiArabicLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ARSA))
        assertEquals currencyFormatService.format(USD, new BigDecimal(NEGATIVE_TEST_VALUE)), USD_NEG_TEST_VALUE
    }


    @Test
    void testCLPForSpanishChileLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CL))
        assertEquals ES_CL_POS_TEST_VALUE, currencyFormatService.format(CLP, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testCLPForSpanishChileLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CL))
        assertEquals ES_CL_NEG_TEST_VALUE, currencyFormatService.format(CLP, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testCOPForSpanishColombiaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CO))
        assertEquals ES_CO_POS_TEST_VALUE, currencyFormatService.format(COP, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testCOPForSpanishColombiaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CO))
        assertEquals ES_CO_NEG_TEST_VALUE, currencyFormatService.format(COP, new BigDecimal(NEGATIVE_TEST_VALUE))
    }

    @Test
    void testCRCForSpanishCostaRicaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CR))
        assertEquals ES_CR_POS_TEST_VALUE, currencyFormatService.format(CRC, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testCRCForFSpanishCostaRicaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, CR))
        assertEquals ES_CR_NEG_TEST_VALUE, currencyFormatService.format(CRC, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testUSDForSpanishEcuadorLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, EC))
        assertEquals ES_EC_POS_TEST_VALUE, currencyFormatService.format(USD, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testUSDForSpanishEcuadorLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, EC))
        assertEquals ES_EC_NEG_TEST_VALUE, currencyFormatService.format(USD, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testMXNForSpanishMexicanLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES,MX))
        assertEquals ES_MX_POS_TEST_VALUE, currencyFormatService.format(MXN, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testMXNForSpanishMexicanLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, MX))
        assertEquals ES_MX_NEG_TEST_VALUE, currencyFormatService.format(MXN, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testPENForSpanishPeruLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, PE))
        assertEquals ES_PE_POS_TEST_VALUE, currencyFormatService.format(PEN, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testPENForSpanishPeruLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, PE))
        assertEquals ES_PE_NEG_TEST_VALUE, currencyFormatService.format(PEN, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testUSDForSpanishPeurtoRicoLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, PR))
        assertEquals ES_PR_POS_TEST_VALUE, currencyFormatService.format(USD, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testUSDForSpanishPeurtoRicoLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES,PR))
        assertEquals ES_PR_NEG_TEST_VALUE, currencyFormatService.format(USD, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testDOPForSpanishDominicanRepublicLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, DO))
        assertEquals ES_DO_POS_TEST_VALUE, currencyFormatService.format(DOP, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testDOPForSpanishDominicanRepublicLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, DO))
        assertEquals ES_DO_NEG_TEST_VALUE, currencyFormatService.format(DOP, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testVEFForSpanishVenezuelaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, VE))
        assertEquals ES_VE_POS_TEST_VALUE, currencyFormatService.format(VEF, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testVEFForSpanishVenezuelaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, VE))
        assertEquals ES_VE_NEG_TEST_VALUE, currencyFormatService.format(VEF, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testARSForSpanishArgentinaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, ARGENTINA))
        assertEquals ES_AR_POS_TEST_VALUE, currencyFormatService.format(ARS, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testARSForSpanishArgentinaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, ARGENTINA))
        assertEquals ES_AR_NEG_TEST_VALUE, currencyFormatService.format(ARS, new BigDecimal(NEGATIVE_TEST_VALUE))
    }


    @Test
    void testGTQForSpanishGuatemalaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, GT))
        assertEquals ES_GT_POS_TEST_VALUE, currencyFormatService.format(GTQ, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testGTQForSpanishGuatemalaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, GT))
        assertEquals ES_GT_NEG_TEST_VALUE, currencyFormatService.format(GTQ, new BigDecimal(NEGATIVE_TEST_VALUE))
    }

    @Test
    void testPABForSpanishPanamaLocalePositiveCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, PA))
        assertEquals ES_PA_POS_TEST_VALUE, currencyFormatService.format(PAB, new BigDecimal(POSITIVE_TEST_VALUE))
    }


    @Test
    void testPABForSpanishPanamaLocaleNegativeCurrencyFormatter() {
        LCH.setLocale(new Locale(ES, PA))
        assertEquals ES_PA_NEG_TEST_VALUE, currencyFormatService.format(PAB, new BigDecimal(NEGATIVE_TEST_VALUE))
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
