/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/
//This class originates from TranMan. Can we include it as a submodule from a plain java project?
//ToDo: Refactor package to include net.hedtech
package net.hedtech.banner.textmanager;

public class ReplaceProps {
	
	static String smartQuotesReplace (String s) {
		StringBuffer res = new StringBuffer();
		char c;
		int len = s.length();
		for (int i=0;i<len;i++) {
			c=s.charAt(i);
			if (c=='\'') {
				// look ahead
				if (i+1<len && s.charAt(i+1)=='\'') {
					res.append(c);
					i++;
				} else {
					res.append("\u2019");
				}
			} else {
				res.append(c);
			}
		}			
		return res.toString();
	}

}
