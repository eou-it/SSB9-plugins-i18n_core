/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/
package net.hedtech.banner.textmanager

import org.apache.log4j.Logger

class TextManagerUtil {
    private HashMap<String, String> valdb = new HashMap<String, String>();
    public static final String logon = "lo";
    public static final String pc = "pc";
    public static final String sl = "sl";
    public static final String tl = "tl";
    public static final String mo = "mo";
    public static final String ba = "ba";
    public static final String sourceFile = "sf";
    public static final String moduleName = "mn";
    public static final String targetFile = "tf";

    private static final def log = Logger.getLogger(getClass())

    private void exitError(String msg) throws Exception {
        String message = msg + "\n" +
                "Arguments: mo=<mode> ba=<batch> lo=<db logon> pc=<TranMan Project> sl=<source language>" +
                " tl=<target language>  sf=<source file> tf=<target file>\n" +
                "  mode: s (extract) | r (reverse extract) | t (translate) | q (quick translate - no check)\n" +
                "  batch: [y|n]. n (No) is default. If y (Yes), the module record will be updated with file locations etc.";
        log.error(message);
    }

    public void parseArgs(String[] args) throws Exception {
        //loop through the arguments and parse key=value pairs
        //store the pairs in tmctx
        log.debug("Arguments:");
        for (int i = 0; i < args.length; i++) {
            int eqp = args[i].indexOf("=");
            if (eqp >= 0) {

                String key = args[i].substring(0, eqp).toLowerCase();
                String val = args[i].substring(eqp + 1);
                valdb.put(key, val);
                log.debug(key + "=" + val);
            }
        }
        if (valdb.get(logon) == null) {
            exitError("No log on specified(lo=user/passwd@connect [tns or jdbc connection])");
        }
        if (valdb.get(sourceFile) == null) {
            exitError("No source file specified (sf=...)");
        }
        if (valdb.get(mo) == null) {
            valdb.put(mo, "s");
        } else if (valdb.get(mo).equals("t")) {
            if (valdb.get(targetFile) == null) {
                exitError("No target file specified (tf=...)");
            }
            if (valdb.get(tl) == null) {
                exitError("No target language specified (tl=...)");
            }
        } else if (valdb.get(mo).equals("r")) {
            if (valdb.get(tl) == null) {
                exitError("No target language specified (tl=...)");
            }
        }
    }

    public String get(String key) {
        return valdb.get(key);
    }

    static String smartQuotesReplace(String s) {
        StringBuffer res = new StringBuffer();
        char c;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            c = s.charAt(i);
            if (c == '\'') {
                // look ahead
                if (i + 1 < len && s.charAt(i + 1) == '\'') {
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
