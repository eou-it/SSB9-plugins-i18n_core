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
                    "   b1 tmmod.project_code%type :=:1;\n"+
                    "   b2 tmmod.module_name%type  :=:2;\n"+
                    "   b3 tmmod.module_type%type  :=:3;\n"+
                    "   b4 tmmod.lang_code%type    :=:4;\n"+
                    "   b5 tmmod.lang_code_src%type:=:5;\n"+
                    "   b6 tmmod.mod_desc%type     :=:6;\n"+
                    "   b7 tmmod.datasource%type   :=:7;\n"+
                    "Begin \n" +
                    "   insert into \n" +
                    "   tmmod (project_code,module_name,module_type, \n" +
                    "          lang_code,lang_code_src,mod_desc,datasource,user_id,acty_date)\n" +
                    "   values (b1,b2,b3,b4,b5,b6,b7,user,sysdate);\n"+
                    "Exception when dup_val_on_index then\n"+
                    "   update tmmod \n"+
                    "      set acty_date = sysdate\n"+
                    "         ,mod_desc  = b6 \n"+
                    "         ,datasource= b7 \n"+
                    "         ,user_id   = user\n"+
                    "   where project_code = b1 \n"+
                    "     and module_name  = b2 \n"+
                    "     and module_type  = b3 \n"+
                    "     and lang_code    = b4;\n"+
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
                    "   tm_objif.SetContext( \n"+
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
                "   :1  := tm_objif.SetPropStringX(\n"+
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
                     "  :1:= tm_objif.GetPropTransGUI(\n"+
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
                  "update tmstrprop set status=-5\n"+
                  "where project_code=:1\n"+
                  "  and lang_code   =:2\n"+
                  "  and module_type =:3\n"+
                  "  and module_name =:4\n"+
                  "  and mod_date<tm_objif.g_session_time;\n"+
                  ":5:=tm_objif.CleanUp(-5);\n"+
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
					        "substr(p.parent_name,2)||p.object_name key,\n"+
					        "p.pre_str||s.string||p.pst_str value\n"+
					        "from tmstrprop p, tmstr s "+
					        "where s.strcode=p.strcode" +
					        "  and p.project_code=:1\n"+
					        "  and p.lang_code   =:2\n"+
					        "  and p.module_type =:3\n"+
					        "  and p.module_name =:4\n"+
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
