/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/

package net.hedtech.banner.textmanager;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;


class TmjProps {
    private static Dbif tmdbif;
    private static Properties jprops;
    //private static Properties translations;
    private static TmCtx ctx;

    private static boolean mainStarted = false;

    //Use a common exit point which only exits when running as a command line tool
    //Since we are using this from a web server we don't want to tear down the whole server!
    public static void exitFromMainOrThrow(Integer errorCode, String errorMessage, Exception e) throws Exception{
        exitFromMainOrThrow(errorCode, errorMessage, e, System.err);

    }
    public static void exitFromMainOrThrow(Integer errorCode, String errorMessage, Exception e, PrintStream out) throws Exception {
        if (mainStarted) {
            out.println(errorMessage);
            if (e!=null) {
                e.printStackTrace(out);
            }
            System.exit(errorCode);
        } else {
            System.err.println(errorMessage);
            if (e!=null) {
                throw e;
            } else {
                throw new RuntimeException(errorMessage);
            }
        }
    }
   
    static void  process() throws Exception {
    	int cnt = 0;
        Dbif.ObjectProperty op=tmdbif.getDefaultObjectProp();
        Enumeration<?> propkeys=jprops.keys();
        boolean do_extract = ( ctx.get(TmCtx.mo).equals("s")||
                               ctx.get(TmCtx.mo).equals("r"));
        while (propkeys.hasMoreElements()) {
            final String sep=".";
            int seploc;
            String key = (String)propkeys.nextElement();
            String value = jprops.getProperty(key);
            // separate key as follows
            // for key = com.sghe.dialog.yes
            // parentName = .com.sghe.dialog and objectName = .yes
            // for key = cancel
            // parentName = . and objectName=cancel
            seploc=key.lastIndexOf(sep);
            if (seploc==-1)
                seploc=0;
            op.parentName="."+key.substring(0,seploc);            
            op.objectName=key.substring(seploc);
            op.string=value;
            System.out.println(key + " = " + op.string);
            if (do_extract) {
                tmdbif.setPropString(op);
            }
            else {
                String trans=tmdbif.getPropString(op);
                if (trans != null) {
                    jprops.setProperty(key,trans);
                    System.out.println("  translated to: "+trans);
                }
                else {
                    jprops.remove(key);                    
                    System.out.println("  NOT translated, removed property.");
                }
            }
            cnt++;
        } 
        //Invalidate strings that are in db but not in property file
        if (do_extract && ctx.get(TmCtx.mo).equals("s")) {
            tmdbif.invalidateStrings();
        }
    }
    
    static void smartQuoteReplace()
    {
        Enumeration<?> propkeys=jprops.keys();
        while (propkeys.hasMoreElements()) {
            String key = (String)propkeys.nextElement();
            String value = jprops.getProperty(key);
            jprops.setProperty(key, ReplaceProps.smartQuotesReplace(value));
        }
    }
    
    static void redirectStdout(String filename) throws Exception {
        if (filename!=null && !filename.isEmpty()) {
          PrintStream	stdout	= null;
          try {
              stdout = new PrintStream ( new FileOutputStream (filename));
          }
          catch (Exception e)  {
              TmjProps.exitFromMainOrThrow(1, "Redirect: Unable to open output file " + filename, null);
          }
          System.setOut(stdout);
        }     
    }
    
    private static void loadPropertiesFromFile(String fname) throws FileNotFoundException, 
    																IOException {
        FileInputStream propfile;
        propfile=new FileInputStream(fname);        
        jprops=new Properties();
        jprops.load(propfile);
        propfile.close();   	
    }

    public static void main(String[] args) throws FileNotFoundException, 
                                                  IOException, Exception {

        mainStarted = true; //Indicate that main has Started
        long timestamp=System.currentTimeMillis();
        String fname;
        String copyrtxt = "# Copyright 2008-2013 Ellucian Company L.P. and its affiliates.\n"
                        + "#\n";
        byte[] copyrbuf= copyrtxt.getBytes();
        ctx=new TmCtx();
        ctx.parseArgs(args);
        redirectStdout(ctx.get(TmCtx.logFile));
        fname=ctx.get(TmCtx.sourceFile);
        System.out.println("TM Properties translator 12");

        tmdbif=new Dbif(ctx.get(TmCtx.logon),ctx);
        if (ctx.get(TmCtx.mo).equals("q")) {
        	System.out.println("Quick translate Java properties file "+fname);
        	jprops=tmdbif.getTranslations(); //speed up, fetch all at once
        } else {
            System.out.println("Processing Java properties file "+fname);
	        loadPropertiesFromFile(fname);
	        process();
        }
        if (ctx.get(TmCtx.mo).equals("t")||ctx.get(TmCtx.mo).equals("q")) {
        	
            OutputStream newpropfile;
            fname=ctx.get(TmCtx.targetFile);
            if (fname != null) {
                newpropfile=new FileOutputStream(fname,false);
                if (newpropfile!=null) {
                	smartQuoteReplace();
                    System.out.println("\nWriting new properties file "+fname);
                    newpropfile.write(copyrbuf);
                    jprops.store(newpropfile, " Project: "+ctx.get(TmCtx.pc) + " Language: "+ctx.get(TmCtx.tl) );
                    newpropfile.close();
                }
                //testing xml format - does not match Horizon xml resource bundle
                /*
                newpropfile=new FileOutputStream(fname+".xml",false);
                if (newpropfile!=null) {
                    System.out.println("\nWriting new properties file "+fname);
                    jprops.storeToXML(newpropfile, " Project: "+ctx.get(tmctx.pc) + " Language: "+ctx.get(tmctx.tl) );
                    newpropfile.close();
                }
                */
                             
                
            }
        }
        if ( ctx.get(TmCtx.ba)!=null && ctx.get(TmCtx.ba).equalsIgnoreCase("y") ) {
        	tmdbif.setModuleRecord(ctx);
        	System.out.println("Set module record.");
        }
        tmdbif.closeConnection();
        timestamp=System.currentTimeMillis()-timestamp;
        System.out.println("Processing done in "+timestamp+" ms");     
    }
}
