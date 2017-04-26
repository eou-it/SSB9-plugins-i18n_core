/******************************************************************************
 *  Copyright 2017 Ellucian Company L.P. and its affiliates.                  *
 ******************************************************************************/
package net.hedtech.banner.textmanager

import oracle.jdbc.*
import org.apache.log4j.Logger

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class TextManagerDB {
    String project_code
    String lang_code_src
    String lang_code_tgt
    String module_type
    String module_name
    private static final def log = Logger.getLogger(TextManagerDB.class)

    static class ObjectProperty {
        String lang_code
        int parentType
        String parentName
        int objectType
        String objectName
        int propCode
        int status
        String string
        int match
        String statusX

        public ObjectProperty() {
            parentType = 10  //Module
            objectType = 26  //Property Class
            propCode = 438  //D2FP_TEXT
            status = 0
        }
    }

    public Connection conn
    ObjectProperty defaultProp
    OracleCallableStatement setStmt

    public ObjectProperty getDefaultObjectProp() {
        return defaultProp
    }

    // Constructor
    public TextManagerDB(String thinURL, TextManagerUtil tmUtil) throws SQLException {
        String usr, passwd, host
        int up_sep = thinURL.indexOf("/")
        int at_sep = thinURL.indexOf("@")
        usr = thinURL.substring(0, up_sep)
        if (at_sep >= 0) {
            passwd = thinURL.substring(up_sep + 1, at_sep)
            host = thinURL.substring(at_sep)
        } else {
            passwd = thinURL.substring(up_sep + 1)
            host = "@"
        }
        // now see if environment has an override for the host
        String localThin = System.getenv().get("LOCAL_THIN")
        if (localThin != null && !localThin.isEmpty()) {
            if (localThin.charAt(0) != '@') {
                localThin = "@" + localThin
            }
            host = "jdbc:oracle:thin:" + localThin
        } else {
            if (thinURL.indexOf(":") >= 0)
                host = "jdbc:oracle:thin:" + host
            else
                host = "jdbc:oracle:oci8:" + host
        }

        try {
            long timestamp = System.currentTimeMillis()
            DriverManager.registerDriver(new OracleDriver())
            conn = DriverManager.getConnection(host, usr, passwd)
            //host like "jdbc:oracle:thin:@localhost:1521:orcl"
            //or host like  "jdbc:oracle:oci8:@orcl"
            timestamp = System.currentTimeMillis() - timestamp
            log.info("Database connect done in " + timestamp + " ms. Host=" + host)
        } catch (SQLException e) {
            String msg = "Error making database connection\n" +
                    "  Use lo=user/passwd@alias (tnsnames) or lo=user/passwd@host:port:sid (thin)\n" +
                    "  Override connection with environment variable LOCAL_THIN=host:port:sid\n"
            log.error(msg, e)
        }
        if (tmUtil != null) {
            setDBContext(tmUtil)
            defaultProp = new ObjectProperty()
            if (tmUtil.get(TextManagerUtil.mo).equals("s")) {
                defaultProp.lang_code = tmUtil.get(TextManagerUtil.sl)
            } else {
                defaultProp.lang_code = tmUtil.get(TextManagerUtil.tl)
            }
        }
    }

    String getModuleName(String fileName, String moduleName) {
        int begin, end
        if (moduleName != null)
            return moduleName
        begin = fileName.lastIndexOf("/")
        end = fileName.lastIndexOf("\\")
        if (end >= 0 && begin < end)
            begin = end
        if (begin < 0)
            begin = 0
        else
            begin++
        end = fileName.lastIndexOf(".")
        return fileName.substring(begin, end).toUpperCase()
    }

    void setDBContext(TextManagerUtil tmUtil) throws SQLException {
        int def_status = 1 //set to 1 for properties - assume translatable by default
        int SQLTrace = 0
        OracleCallableStatement stmt
        long timestamp = System.currentTimeMillis()
        project_code   = tmUtil.get(TextManagerUtil.pc)
        lang_code_src  = tmUtil.get(TextManagerUtil.sl)
        lang_code_tgt  = tmUtil.get(TextManagerUtil.tl)
        module_type = "J"
        module_name = getModuleName(tmUtil.get(TextManagerUtil.sourceFile), tmUtil.get(TextManagerUtil.moduleName))
        //Reverse extract.
        if (tmUtil.get(TextManagerUtil.mo).equals("r")) {
            def_status = 7 //set to Reverse extracted
        }
        try {
            stmt = (OracleCallableStatement) conn.prepareCall(
                    "Begin \n" +
                            "   if :1>0 then \n" +
                            "         DBMS_SESSION.SET_SQL_TRACE(TRUE) \n" +
                            "   end if \n" +
                            "   GMKOBJI.P_SETCONTEXT( \n" +
                            "             :2,\n" +
                            "             :3,\n" +
                            "             :4,\n" +
                            "             :5,\n" +
                            "             :6,\n" +
                            "             :7)\n" +
                            "End")
            stmt.setInt(1, SQLTrace)
            stmt.setString(2, project_code)
            stmt.setString(3, lang_code_src)
            stmt.setString(4, lang_code_tgt)
            stmt.setString(5, module_type)
            stmt.setString(6, module_name)
            stmt.setInt(7, def_status)
            stmt.execute()
        } catch (SQLException e) {
            log.error("Error in SetDBContext", e)
        }
        timestamp = System.currentTimeMillis() - timestamp
        log.debug("SetDBContext done in " + timestamp + " ms")
    }

    public void closeConnection() throws SQLException {
        if (!conn.isClosed()) {
            conn.commit()
            conn.close()
        }
    }

    void setPropString(ObjectProperty op) throws SQLException {
        try {
            if (setStmt == null) {
                setStmt = (OracleCallableStatement) conn.prepareCall(
                        "Begin                    \n" +
                                "   :1  := GMKOBJI.F_SETPROPSTRINGX(\n" +
                                "      pLang_Code    => :2,   \n" +
                                "      pParent_type  => :3,   \n" +
                                "      pParent_name  => :4,   \n" +
                                "      pObject_type  => :5,   \n" +
                                "      pObject_name  => :6,   \n" +
                                "      pObject_prop  => :7,   \n" +
                                "      pTransl_stat  => :8,   \n" +
                                "      pProp_string  => :9    \n" +
                                "   )\n" +
                                //"   commit\n"+
                                "End")
                setStmt.registerOutParameter(1, OracleTypes.VARCHAR)
                setStmt.registerOutParameter(8, OracleTypes.INTEGER)
            }
            setStmt.setString(2, op.lang_code)
            setStmt.setInt(3, op.parentType)
            setStmt.setString(4, op.parentName)
            setStmt.setInt(5, op.objectType)
            setStmt.setString(6, op.objectName)
            setStmt.setInt(7, op.propCode)
            setStmt.setInt(8, op.status)
            setStmt.setString(9, op.string)
            setStmt.execute()
            op.statusX = setStmt.getString(1)
            op.status = setStmt.getInt(8)
            log.debug("  " + op.statusX)
        } catch (SQLException e) {
            log.error("Error in setPropString string=", e)
        }
    }

    void invalidateStrings() throws SQLException {
        OracleCallableStatement stmt
        long timestamp = System.currentTimeMillis()
        try {
            stmt = (OracleCallableStatement) conn.prepareCall(
                    "Begin\n" +
                            "update GMRSPRP set GMRSPRP_STAT_CODE=-5\n" +
                            "where GMRSPRP_PROJECT=:1\n" +
                            "  and GMRSPRP_LANG_CODE   =:2\n" +
                            "  and GMRSPRP_MODULE_TYPE =:3\n" +
                            "  and GMRSPRP_MODULE_NAME =:4\n" +
                            "  and GMRSPRP_ACTIVITY_DATE<GMKOBJI.g_session_time\n" +
                            ":5:=GMKOBJI.f_CleanUp(-5)\n" +
                            "End"
            )

            stmt.setString(1, project_code)
            stmt.setString(2, lang_code_src)
            stmt.setString(3, module_type)
            stmt.setString(4, module_name)
            stmt.registerOutParameter(5, OracleTypes.INTEGER)
            stmt.execute()
            timestamp = System.currentTimeMillis() - timestamp
            log.debug("Obsoleted " + stmt.getInt(5) + " properties in " + timestamp + " ms")
        } catch (SQLException e) {
            log.error("Error in dbif.invalidateStrings", e)
        }

    }
}
