/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/
package net.hedtech.banner.textmanager;

class ReplaceProps {
	
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
