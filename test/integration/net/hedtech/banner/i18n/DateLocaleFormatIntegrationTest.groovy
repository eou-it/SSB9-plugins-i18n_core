/*******************************************************************************
 Copyright 2009-2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import static org.junit.Assert.assertEquals
import org.junit.Test

class DateLocaleFormatIntegrationTest {

    public final String KEY_TO_TEST = "js.datepicker.dateFormat"
    public final String EN_US_DATE_FORMAT = "mm/dd/yyyy"
    public final String AR_DATE_FORMAT = "dd/MM/yyyy"
    public final String OTHER_EN_PT_DATE_FORMAT = "dd/mm/yyyy"
    public final String FR_CA_DATE_FORMAT = "yyyy/mm/dd"

    public final String EN = "en"
    public final String US = "US"
    public final String GB = "GB"
    public final String IE = "IE"
    public final String IN = "IN"
    public final String AU = "AU"
    public final String CA = "CA"

    public final String FR = "fr"
    public final String PT = "pt"
    public final String ES = "es"
    public final String AR = "ar"




    @Test
    public void testJSDatePickerKeyFor_en_US(){
        Locale.setDefault(new Locale(EN,US))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),EN_US_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_en_AU(){
        Locale.setDefault(new Locale(EN,AU))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_ar() {
        Locale.setDefault(new Locale(AR))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),AR_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_en_GB(){
        Locale.setDefault(new Locale(EN,GB))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_en_IE(){
        Locale.setDefault(new Locale(EN,IE))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_en_IN(){
        Locale.setDefault(new Locale(EN,IN))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_es(){
        Locale.setDefault(new Locale(ES))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }
    @Test
    public void testJSDatePickerKeyFor_fr(){
        Locale.setDefault(new Locale(FR))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }
    @Test
    public void testJSDatePickerKeyFor_fr_CA(){
        Locale.setDefault(new Locale(FR,CA))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),FR_CA_DATE_FORMAT);
    }

    @Test
    public void testJSDatePickerKeyFor_pt(){
        Locale.setDefault(new Locale(PT))
        assertEquals(MessageHelper.getMessage(KEY_TO_TEST),OTHER_EN_PT_DATE_FORMAT);
    }
}
