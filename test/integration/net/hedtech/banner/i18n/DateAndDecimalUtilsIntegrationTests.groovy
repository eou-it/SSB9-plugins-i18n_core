/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

package net.hedtech.banner.i18n

import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.ParseException

import static org.junit.Assert.*

class DateAndDecimalUtilsIntegrationTests {
    def dateAndDecimalUtils
    public final String EN = "en"
    public final String IN = "IN"

    @Before
    void setUp(){
        dateAndDecimalUtils=new DateAndDecimalUtils()
    }

    @Test
    void testProperties(){
        dateAndDecimalUtils.properties(new Locale(EN, IN)).size()>0?assertTrue(true):assertFalse(true)
    }

    @Test
    void testFormatDate(){
        assertEquals(dateAndDecimalUtils.formatDate("dd/MM/yyyy"),"MM/dd/yyyy")
    }

    @Test
    void testParseDate(){
        ParseException pe
        try{
            dateAndDecimalUtils.parseDate("12/12/2012")
        }catch(ParseException e){
                pe=e
        }
        assertEquals(null,pe)
    }

    @Test
    void testConvertToCommaDelimited(){
        String[] list
        assertEquals(dateAndDecimalUtils.convertToCommaDelimited(list),"")
    }
}
