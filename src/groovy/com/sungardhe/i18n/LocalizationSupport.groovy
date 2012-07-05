/*********************************************************************************
 Copyright 2009-2011 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of 
 SunGard Higher Education and its subsidiaries. Any use of this software is limited 
 solely to SunGard Higher Education licensees, and is further subject to the terms 
 and conditions of one or more written license agreements between SunGard Higher 
 Education and the licensee in question. SunGard, Banner and Luminis are either 
 registered trademarks or trademarks of SunGard Higher Education in the U.S.A. 
 and/or other regions and/or countries.
 **********************************************************************************/

package com.sungardhe.i18n

/**
 * A support class to be used to mixin localization support.
 */
class LocalizationSupport {

    def message(Map map) {
        MessageHelper.message(map)
    }

    /**
     * A 'localizer' closure that may be passed into an ApplicationException in order to localize error messages.
     * e.g.:  def returnMap = ae.returnMap( localizer ) // i.e., this is the same usage as within a Controller
     * */
    def localizer = MessageHelper.localizer

}