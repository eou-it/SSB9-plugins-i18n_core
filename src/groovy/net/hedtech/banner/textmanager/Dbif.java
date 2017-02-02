/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/
//This class originates from TranMan. Can we include it as a submodule from a plain java project?
//ToDo: Refactor package to include net.hedtech and use common java style conventions
package net.hedtech.banner.textmanager;
import java.sql.*;
import oracle.jdbc.*;
import java.util.Properties;


public class Dbif {
    String project_code;
    String lang_code_src;
    String lang_code_tgt;
    String module_type;
    String module_name;
    public class ObjectProperty
    {
        String  lang_code;
        int     parentType;
        //String	parentTypeName;
        String  parentName;
        int     objectType;
        //String	objectTypeName;
        String  objectName;
        int     propCode;
        int     status;
        String  string;
        int     match;
        String	statusX;

        public ObjectProperty() {
            parentType=10; //Module
            objectType=26;  //Property Class;
            propCode=438;  //D2FP_TEXT;
            status=0;
        };
    }

    public Connection conn;
    ObjectProperty defaultProp;
    OracleCallableStatement setStmt;
    OracleCallableStatement getStmt;

    public ObjectProperty getDefaultObjectProp() {
        return defaultProp;
    }

    String getModuleName(String filen, String modn){
        int sep1,sep2;
        if (modn!=null)
            return modn;
        sep1=filen.lastIndexOf("/");
        sep2=filen.lastIndexOf("\\");
        if (sep2>=0 && sep1<sep2)
            sep1=sep2;
        if (sep1<0)
            sep1=0;
        else
            sep1++;
        sep2=filen.lastIndexOf(".");
        return filen.substring(sep1,sep2).toUpperCase();
    }

    void setModuleRecord(TmCtx ctx) throws Exception {
        OracleCallableStatement stmt;
        String data_source=ctx.get(TmCtx.sourceFile);;
        String lang_code=lang_code_src;
        String mod_desc;

        switch ( ctx.get(TmCtx.mo).charAt(0) ) {
            case 's' :
                data_source=ctx.get(TmCtx.sourceFile);
                lang_code=lang_code_src;
                mod_desc="Properties batch extract";
                break;
            case 'r' :
                data_source=ctx.get(TmCtx.sourceFile);
                lang_code=lang_code_tgt;
                mod_desc="Properties batch reverse extract";
                break;
            default: //q and t both translate
                data_source=ctx.get(TmCtx.targetFile);
                lang_code=lang_code_tgt;
                mod_desc="Properties batch translate";
        }
        try{
            stmt=(OracleCallableStatement)conn.prepareCall(
                    "Declare \n"+
                            "   b1 GMRMDUL.GMRMDUL_PROJECT%type :=:1;\n"+
                            "   b2 GMRMDUL.GMRMDUL_MODULE_NAME%type  :=:2;\n"+
                            "   b3 GMRMDUL.GMRMDUL_MODULE_TYPE%type  :=:3;\n"+
                            "   b4 GMRMDUL.GMRMDUL_LANG_CODE%type    :=:4;\n"+
                            "   b5 GMRMDUL.GMRMDUL_SRC_LANG_CODE%type:=:5;\n"+
                            "   b6 GMRMDUL.GMRMDUL_MOD_DESC%type     :=:6;\n"+
                            "   b7 GMRMDUL.GMRMDUL_DATASOURCE%type   :=:7;\n"+
                            "Begin \n" +
                            "   insert into \n" +
                            "   GMRMDUL (GMRMDUL_PROJECT,GMRMDUL_MODULE_NAME,GMRMDUL_MODULE_TYPE, \n" +
                            "          GMRMDUL_LANG_CODE,GMRMDUL_SRC_LANG_CODE,GMRMDUL_MOD_DESC,GMRMDUL_DATASOURCE,\n" +
                            "        GMRMDUL_USER_ID,GMRMDUL_ACTIVITY_DATE)\n" +
                            "   values (b1,b2,b3,b4,b5,b6,b7,user,sysdate);\n"+
                            "Exception when dup_val_on_index then\n"+
                            "   update GMRMDUL \n"+
                            "      set GMRMDUL_ACTIVITY_DATE = sysdate\n"+
                            "         ,GMRMDUL_MOD_DESC  = b6 \n"+
                            "         ,GMRMDUL_DATASOURCE= b7 \n"+
                            "         ,GMRMDUL_USER_ID   = user\n"+
                            "   where GMRMDUL_PROJECT = b1 \n"+
                            "     and GMRMDUL_MODULE_NAME  = b2 \n"+
                            "     and GMRMDUL_MODULE_TYPE  = b3 \n"+
                            "     and GMRMDUL_LANG_CODE    = b4;\n"+
                            "End;"   );
            stmt.setString(1,project_code);
            stmt.setString(2,module_name);
            stmt.setString(3,module_type);
            stmt.setString(4,lang_code);
            stmt.setString(5,lang_code_src);
            stmt.setString(6,mod_desc);
            stmt.setString(7,data_source);

            stmt.execute();
        }   catch (SQLException e) {
            TmjProps.exitFromMainOrThrow(1,"Error in setModuleRecord",e);
        }
    }
    void setDBContext(TmCtx ctx) throws Exception {
        int def_status=1; //set to 1 for properties - assume translatable by default
        int SQLTrace=0;
        OracleCallableStatement stmt;
        long timestamp = System.currentTimeMillis();
        project_code=ctx.get(TmCtx.pc);
        lang_code_src=ctx.get(TmCtx.sl);
        lang_code_tgt=ctx.get(TmCtx.tl);
        module_type="J";
        module_name=getModuleName(ctx.get(TmCtx.sourceFile),ctx.get(TmCtx.moduleName));
        //Reverse extract.
        if (ctx.get(TmCtx.mo).equals("r")) {
            def_status = 7; //set to Reverse extracted
        }
        try{
            stmt=(OracleCallableStatement)conn.prepareCall(
                    "Begin \n"+
                            "   if :1>0 then \n"+
                            "         DBMS_SESSION.SET_SQL_TRACE(TRUE); \n"+
                            "   end if; \n"+
                            "   GMKOBJI.P_SETCONTEXT( \n"+
                            "             :2,\n"+
                            "             :3,\n"+
                            "             :4,\n"+
                            "             :5,\n"+
                            "             :6,\n"+
                            "             :7);\n"+
                            "End;"   );
            stmt.setInt(1,SQLTrace);
            stmt.setString(2,project_code);
            stmt.setString(3,lang_code_src);
            stmt.setString(4,lang_code_tgt);
            stmt.setString(5,module_type);
            stmt.setString(6,module_name);
            stmt.setInt(7,def_status);
            stmt.execute();
        }   catch (SQLException e) {
            TmjProps.exitFromMainOrThrow(1,"Error in SetDBContext",e);
        }
        timestamp=System.currentTimeMillis()-timestamp;
        System.out.println("SetDBContext done in "+timestamp+" ms\n");
    }

    // Constructor
    public Dbif(String thinURL,TmCtx ctx ) throws Exception {
        String usr,passwd,host;
        int up_sep=thinURL.indexOf("/");
        int at_sep=thinURL.indexOf("@");
        usr=thinURL.substring(0,up_sep);
        if (at_sep>=0) {
            passwd=thinURL.substring(up_sep+1,at_sep);
            host=thinURL.substring(at_sep);
        } else {
            passwd=thinURL.substring(up_sep+1);
            host="@";
        }
        // now see if environment has an override for the host
        String localThin=System.getenv().get("LOCAL_THIN");
        if (localThin!=null && !localThin.isEmpty()) {
            if (localThin.charAt(0)!='@') {
                localThin = "@" + localThin;
            }
            host="jdbc:oracle:thin:"+localThin;
        } else {
            if (thinURL.indexOf(":")>=0)
                host="jdbc:oracle:thin:"+host;
            else
                host="jdbc:oracle:oci8:"+host;
        }

        try{
            long timestamp = System.currentTimeMillis();
            DriverManager.registerDriver (new OracleDriver());
            conn = DriverManager.getConnection(host, usr, passwd);
            //host like "jdbc:oracle:thin:@localhost:1521:orcl"
            //or host like  "jdbc:oracle:oci8:@orcl"
            timestamp=System.currentTimeMillis()-timestamp;
            System.out.println("Database connect done in "+timestamp+" ms. Host="+host);
        } catch (SQLException e) {
            String msg = "Error making database connection\n"+
                    "  Use lo=user/passwd@alias (tnsnames) or lo=user/passwd@host:port:sid (thin)\n"+
                    "  Override connection with environment variable LOCAL_THIN=host:port:sid\n";
            TmjProps.exitFromMainOrThrow(1, msg, e);
        }
        if (ctx!=null) {
            setDBContext(ctx);
            defaultProp = new ObjectProperty();
            if (ctx.get(TmCtx.mo).equals("s")) {
                defaultProp.lang_code = ctx.get(TmCtx.sl);
            } else {
                defaultProp.lang_code = ctx.get(TmCtx.tl);
            }
        }
    }

    public void closeConnection() throws SQLException {
        if ( !conn.isClosed()) {
            conn.commit();
            conn.close();
        }
    }

    void setPropString(ObjectProperty op) throws Exception {
        try{
            if (setStmt == null) {
                setStmt=(OracleCallableStatement)conn.prepareCall(
                        "Begin                    \n"+
                                "   :1  := GMKOBJI.F_SETPROPSTRINGX(\n"+
                                "      pLang_Code    => :2,   \n"+
                                "      pParent_type  => :3,   \n"+
                                "      pParent_name  => :4,   \n"+
                                "      pObject_type  => :5,   \n"+
                                "      pObject_name  => :6,   \n"+
                                "      pObject_prop  => :7,   \n"+
                                "      pTransl_stat  => :8,   \n"+
                                "      pProp_string  => :9    \n"+
                                "   );\n"+
                                //"   commit;\n"+
                                "End;");
                setStmt.registerOutParameter(1,OracleTypes.VARCHAR);
                setStmt.registerOutParameter(8,OracleTypes.INTEGER);
            }
            setStmt.setString(2,op.lang_code);
            setStmt.setInt(   3,op.parentType);
            setStmt.setString(4,op.parentName);
            setStmt.setInt(   5,op.objectType);
            setStmt.setString(6,op.objectName);
            setStmt.setInt(   7,op.propCode);
            setStmt.setInt(   8,op.status);
            setStmt.setString(9,op.string);
            setStmt.execute();
            op.statusX=setStmt.getString(1);
            op.status=setStmt.getInt(8);
            System.out.println("  "+op.statusX);
        }   catch (SQLException e) {
            TmjProps.exitFromMainOrThrow(1,"Error in setPropString",e);
        }
    }

    String getPropString(ObjectProperty op) throws Exception {
        try{
            if (getStmt == null) {
                getStmt=(OracleCallableStatement)conn.prepareCall(
                        "Begin\n"+
                                "  :1:= GMKOBJI.F_GETPROPTRANSGUI(\n"+
                                "      pLang_code     => :2,   \n"+
                                "      pParent_type   => :3,   \n"+
                                "      pParent_name   => :4,   \n"+
                                "      pObject_type   => :5,   \n"+
                                "      pObject_name   => :6,   \n"+
                                "      pObject_prop   => :7,   \n"+
                                "      pTransl_stat   => :8,   \n"+
                                "      pString        => :9,   \n"+
                                "      pVerify        => :10,  \n"+
                                "      pWantAttributes=> :11,  \n"+
                                "      pAttributes    => :12   \n"+
                                "      );  \n"+
                                "End;");

                getStmt.registerOutParameter(1,OracleTypes.INTEGER);
                getStmt.registerOutParameter(8,OracleTypes.INTEGER);
                getStmt.registerOutParameter(9,OracleTypes.VARCHAR);
                getStmt.registerOutParameter(11,OracleTypes.INTEGER);
                getStmt.registerOutParameter(12,OracleTypes.VARCHAR);
            }

            getStmt.setString(2,op.lang_code);
            getStmt.setInt(   3,op.parentType);
            getStmt.setString(4,op.parentName);
            getStmt.setInt(   5,op.objectType);
            getStmt.setString(6,op.objectName);
            getStmt.setInt(   7,op.propCode);
            getStmt.setInt(   8,0); //don't have to provide status
            getStmt.setString(9,op.string);
            getStmt.setInt(   10,1); // do verify
            getStmt.setInt(   11,0); // no attributes needed
            getStmt.execute();
            if (getStmt.getInt(1)==1)
                op.string=getStmt.getString(9);
            else
                op.string=null; // no matching translation
            if (op.string!=null && op.string.charAt(0)=='\0')
                op.string=null; // C Null string
        }   catch (SQLException e) {
            TmjProps.exitFromMainOrThrow(1,"Error Getting Property String",e, System.out);
        }
        return op.string;
    }

    void invalidateStrings() throws Exception{
        OracleCallableStatement stmt;
        long timestamp = System.currentTimeMillis();
        try{
            stmt=(OracleCallableStatement)conn.prepareCall(
                    "Begin\n"+
                            "update GMRSPRP set GMRSPRP_STAT_CODE=-5\n"+
                            "where GMRSPRP_PROJECT=:1\n"+
                            "  and GMRSPRP_LANG_CODE   =:2\n"+
                            "  and GMRSPRP_MODULE_TYPE =:3\n"+
                            "  and GMRSPRP_MODULE_NAME =:4\n"+
                            "  and GMRSPRP_ACTIVITY_DATE<GMKOBJI.g_session_time;\n"+
                            ":5:=GMKOBJI.f_CleanUp(-5);\n"+
                            "End;"
            );

            stmt.setString(1,project_code);
            stmt.setString(2,lang_code_src);
            stmt.setString(3,module_type);
            stmt.setString(4,module_name);
            stmt.registerOutParameter(5,OracleTypes.INTEGER);
            stmt.execute();
            timestamp=System.currentTimeMillis()-timestamp;
            System.out.println("\nObsoleted "+stmt.getInt(5)+" properties in "+timestamp+" ms");
        }   catch (SQLException e) {
            TmjProps.exitFromMainOrThrow(1, "Error in dbif.invalidateStrings", e, System.out);
        }

    }

    /* New method to retrieve all translations with one statement */
    Properties getTranslations() throws Exception {
        OraclePreparedStatement stmt = null;
        Properties result = new Properties();
        String sqlcode =    "select\n"+
                "substr(p.GMRSPRP_PARENT_NAME,2)||p.GMRSPRP_OBJECT_NAME key,\n"+
                "p.GMRSPRP_PRE_STR||s.GMBSTRG_STRING||p.GMRSPRP_PST_STR value\n"+
                "from GMRSPRP p, GMBSTRG s "+
                "where s.GMBSTRG_STRCODE=p.GMRSPRP_STRCODE" +
                "  and p.GMRSPRP_PROJECT=:1\n"+
                "  and p.GMRSPRP_LANG_CODE   =:2\n"+
                "  and p.GMRSPRP_MODULE_TYPE =:3\n"+
                "  and p.GMRSPRP_MODULE_NAME =:4\n"+
                "order by key";
        long timestamp = System.currentTimeMillis();
        try{
            stmt=(OraclePreparedStatement)conn.prepareStatement(sqlcode);
            stmt.setString(1,project_code);
            stmt.setString(2,lang_code_tgt);
            stmt.setString(3,module_type);
            stmt.setString(4,module_name);
            ResultSet rs = stmt.executeQuery();
            //copy rs into properties
            while (rs.next()) {
                result.setProperty(rs.getString(1), rs.getString(2));
            }
            timestamp=System.currentTimeMillis()-timestamp;
            System.out.println("\nFetched translations for properties in "+
                    timestamp +" ms");
        }   catch (SQLException e) {
            String msg = "Error in dbif.getTranslations\n";
            if (stmt != null) {
                msg += "SQL statement :\n" + sqlcode + "\n";
            }
            TmjProps.exitFromMainOrThrow(1,msg,e,System.out);
        }
        return result;
    }

}
