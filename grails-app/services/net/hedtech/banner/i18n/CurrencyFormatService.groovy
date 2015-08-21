/*******************************************************************************
 Copyright 2009-2014 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.i18n

import com.ibm.icu.text.ArabicShaping
import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.Currency
import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.apache.log4j.Logger
import org.springframework.context.i18n.LocaleContextHolder as LCH

/**
 * This utility class is used to format the amount based on the locale format and the amount will
 * be still mentioned with the base currency
 */


class CurrencyFormatService {

    Logger logger = Logger.getLogger(this.getClass())
    public static final String ARABIC_LOCALE = "ar"
    public final String EN = "en"
    public final String US = "US"

    public String format(String currencyCode, BigDecimal amount) throws CurrencyNotFoundException {
        if(isInvalidCurrencyCode(currencyCode))   {
            throw new CurrencyNotFoundException(currencyCode:currencyCode)
        }

        Locale locale = LCH.getLocale()
        if(locale.toString().equalsIgnoreCase(ARABIC_LOCALE)){
            LCH.setLocale(new Locale(EN,US))
        }
        String fmtMonetaryValue;
        ArabicShaping shaping = new ArabicShaping(ArabicShaping.DIGITS_AN2EN)
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(LCH.getLocale())
        Currency currency = Currency.getInstance(currencyCode)
        numberFormat.setCurrency(currency)
        fmtMonetaryValue = numberFormat.format(amount)
        fmtMonetaryValue = shaping.shape(fmtMonetaryValue)
        return fmtMonetaryValue
    }

    private boolean isInvalidCurrencyCode(String currencyCode) {
        List<String> lstValidCurrencyCode = Currency.getAvailableCurrencyCodes()
        currencyCode = currencyCode?.toUpperCase()
        boolean bResult = true
        if(lstValidCurrencyCode.contains(currencyCode)) {
            bResult = false
        }
        return bResult
    }
}
