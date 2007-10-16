package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.QBTransformAction;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2007-10-16 15:38:15 $
 */
public class TestDBAPITransform4RDS {
    public static void main(String[] args) throws Exception {
        String mapFile = FileUtil.getWorkingDirPath() + File.separator + "workingspace" + File.separator + "RDS_Example" + File.separator + "test_4_DB.map";
        String defineXML = FileUtil.getWorkingDirPath() + File.separator + "workingspace" + File.separator + "RDS_Example" + File.separator + "define.xml";
        String driver = System.getProperty("driver");
        String url = System.getProperty("url");
        String user = System.getProperty("user");
        String pass = System.getProperty("pass");
        new QBTransformAction().transformDB(mapFile, defineXML, obtainConnection(driver, url, user, pass), "c:/");
    }

    private static Connection obtainConnection(String drv, String url, String uid, String pwd) throws Exception {
        Connection connection;
        try {
            Class.forName(drv);
            connection = DriverManager.getConnection(url, uid, pwd);
        } catch (Exception e) {
            throw e;
        }
        return connection;
    }
}
