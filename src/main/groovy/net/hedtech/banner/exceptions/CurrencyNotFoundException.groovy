/* ****************************************************************************
Copyright 2009-2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.exceptions

import net.hedtech.banner.i18n.MessageHelper

/**
 * A runtime exception indicating an entity or resource was not found.
 **/
public class CurrencyNotFoundException extends RuntimeException {

    public static final String MESSAGE_KEY_CURRENCY_CODE_INVALID = "default.invalid.currency.format.code"
    def currencyCode

    public String getMessage() {
        return MessageHelper.message(MESSAGE_KEY_CURRENCY_CODE_INVALID, [currencyCode]?.toArray() )
    }

    String toString() {
        getMessage()
    }

}

