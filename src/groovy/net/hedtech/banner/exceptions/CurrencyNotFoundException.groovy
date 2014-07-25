/* ****************************************************************************
Copyright 2009-2014 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.exceptions
/**
 * A runtime exception indicating an entity or resource was not found.
 **/
public class CurrencyNotFoundException extends RuntimeException {

    public static final String MESSAGE_KEY_CURRCODE_INVALID = "currcode.invalid.message"
    def currencyCode

    public String getMessage() {
       //     MessageResolver.message(MESSAGE_KEY_CURRCODE_INVALID, [currencyCode]?.toArray() )
        return MESSAGE_KEY_CURRCODE_INVALID
    }

    String toString() {
        getMessage()
    }

}

