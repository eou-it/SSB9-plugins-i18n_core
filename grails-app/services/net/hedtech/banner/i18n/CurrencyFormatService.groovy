/*******************************************************************************
 Copyright 2009-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.i18n

import com.ibm.icu.text.ArabicShaping
import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.Currency
import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.springframework.context.i18n.LocaleContextHolder as LCH

/**
 * This utility class is used to format the amount based on the locale format and the amount will
 * be still mentioned with the base currency
 */


class CurrencyFormatService {

    public final String ARABIC_LOCALE = "ar"

    public final String EN = "en"

    public final String US = "US"

    public final String ARABIC_SUB_LOCALE = "ar_"

    public String format(String currencyCode, BigDecimal amount, int currencyStyle = 1) throws CurrencyNotFoundException {
        if (isInvalidCurrencyCode(currencyCode)) {
            throw new CurrencyNotFoundException(currencyCode: currencyCode)
        }

        Locale locale = LCH.getLocale()
        locale = (locale.toString().equalsIgnoreCase(ARABIC_LOCALE) || locale.toString().toLowerCase().startsWith(ARABIC_SUB_LOCALE)) ? new Locale(EN, US) : locale
        String fmtMonetaryValue;
        ArabicShaping shaping = new ArabicShaping(ArabicShaping.DIGITS_AN2EN)
        String defaultCurrencyStyle  = MessageHelper.message("default.currency.formatter.style")
        NumberFormat numberFormat = defaultCurrencyStyle.isInteger()? NumberFormat.getInstance(locale, new Integer(defaultCurrencyStyle)): NumberFormat.getInstance(locale, currencyStyle)
        Currency currency = Currency.getInstance(currencyCode)
        numberFormat.setCurrency(currency)
        fmtMonetaryValue = numberFormat.format(amount)
        fmtMonetaryValue = shaping.shape(fmtMonetaryValue)
        return fmtMonetaryValue
    }


    private boolean isInvalidCurrencyCode(String currencyCode) {
        Set<Currency> lstValidCurrencyCode = Currency.getAvailableCurrencies()
        currencyCode = currencyCode?.toUpperCase()
        return lstValidCurrencyCode.findAll { it?.isoCode.equals(currencyCode) }.size() < 1
    }
}
