/*******************************************************************************
 Copyright 2009-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.i18n

import com.ibm.icu.text.ArabicShaping
import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.Currency
import net.hedtech.banner.exceptions.CurrencyNotFoundException
import org.springframework.context.i18n.LocaleContextHolder as LCH
import com.ibm.icu.text.DecimalFormatSymbols
/**
 * This utility class is used to format the amount based on the locale format and the amount will
 * be still mentioned with the base currency
 */


class CurrencyFormatService {

    public final String ARABIC_LOCALE = "ar"

    public final String EN = "en"

    public final String US = "US"

    public final String ARABIC_SUB_LOCALE = "ar_"

    public final String SPANISH_COSTA_RICA_LOCALE = "es_CR"

    public final String SPANISH_DOMINICAN_REPUBLIC_LOCALE = "es_DO"

    public final String SPANISH_DOMINICAN_REPUBLIC_CURRENCY_CODE = "DOP"

    public final String SPANISH_DOMINICAN_REPUBLIC_POS_CURRENCY_SYMBOL = "\$"

    public final String SPANISH_DOMINICAN_REPUBLIC_NEG_CURRENCY_SYMBOL = "-\$"

    public final String PORTUGUESE_LOCALE = "pt"

    public final String PORTUGUESE_BRAZIL_LOCALE = "pt_BR"

    public final String PORTUGUESE_CURRENCY_CODE = "BRL"

    public final String PORTUGUESE_POS_CURRENCY_SYMBOL = "R\$ "

    public final String PORTUGUESE_NEG_CURRENCY_SYMBOL = "-R\$ "

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

//icu4j library Returns Grouping Separator as a space for Costa Rica.Below Changes are for making Grouping Separator as dot.(As per Translation Team req.)
        if(locale.toString().equalsIgnoreCase(SPANISH_COSTA_RICA_LOCALE)){
            DecimalFormatSymbols decimalFormatSymbols=setGroupingSeparator(locale)
            numberFormat.setDecimalFormatSymbols(decimalFormatSymbols)
        }

//icu4j library returns the Currency symbol as RD$ for Republic Dominican Locale(es_DO).Below Changes are for making Currency symbol as $  (As per Translation Team req.)
        if(locale.toString().equalsIgnoreCase(SPANISH_DOMINICAN_REPUBLIC_LOCALE) && currencyCode.equalsIgnoreCase(SPANISH_DOMINICAN_REPUBLIC_CURRENCY_CODE)){
            numberFormat.setPositivePrefix(SPANISH_DOMINICAN_REPUBLIC_POS_CURRENCY_SYMBOL)
            numberFormat.setNegativePrefix(SPANISH_DOMINICAN_REPUBLIC_NEG_CURRENCY_SYMBOL)
        }

//icu4j library returns the formatted number without space after R$ symbol(ex.R$1234) for BRL currency.Below changes are for adding space after R$ symbol ex.R$ 1234 (As per Translation Team req.)
        if((locale.toString().equalsIgnoreCase(PORTUGUESE_LOCALE) || locale.toString().equalsIgnoreCase(PORTUGUESE_BRAZIL_LOCALE))&& currencyCode.equalsIgnoreCase(PORTUGUESE_CURRENCY_CODE)){
            numberFormat.setPositivePrefix(PORTUGUESE_POS_CURRENCY_SYMBOL)
            numberFormat.setNegativePrefix(PORTUGUESE_NEG_CURRENCY_SYMBOL)
        }

        numberFormat.setCurrency(currency)
        fmtMonetaryValue = numberFormat.format(amount)
        fmtMonetaryValue = shaping.shape(fmtMonetaryValue)
        return fmtMonetaryValue
    }

    private DecimalFormatSymbols setGroupingSeparator(Locale locale){
        DecimalFormatSymbols decimalFormatSymbols=new DecimalFormatSymbols(locale)
        char groupingSeparator='.'
        decimalFormatSymbols.setGroupingSeparator(groupingSeparator)
        decimalFormatSymbols.setMonetaryGroupingSeparator(groupingSeparator)
        return decimalFormatSymbols
    }

    private boolean isInvalidCurrencyCode(String currencyCode) {
        Set<Currency> lstValidCurrencyCode = Currency.getAvailableCurrencies()
        currencyCode = currencyCode?.toUpperCase()
        return lstValidCurrencyCode.findAll { it?.isoCode.equals(currencyCode) }.size() < 1
    }
}
