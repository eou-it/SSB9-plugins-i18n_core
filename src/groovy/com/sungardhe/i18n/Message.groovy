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

import org.zkoss.zk.ui.HtmlMacroComponent

/**
 * ZK macro component for resolving resourceCode and args to a Grails resource bundle.
 */
public class Message extends HtmlMacroComponent {

    def String value
    def String arg0
    def String arg1
    def String arg2
    def String arg3

    public String getI18n() {
        if (arg0 && arg1 && arg2 && arg3) {
            return com.sungardhe.i18n.MessageHelper._(value, arg0, arg1, arg2, arg3)
        }
        if (arg0 && arg1 && arg2) {
            return com.sungardhe.i18n.MessageHelper._(value, arg0, arg1, arg2)
        }
        if (arg0 && arg1) {
            return com.sungardhe.i18n.MessageHelper._(value, arg0, arg1)
        }
        if (arg0) {
            return com.sungardhe.i18n.MessageHelper._(value, arg0)
        }

        return com.sungardhe.i18n.MessageHelper._(value);
    }
}
