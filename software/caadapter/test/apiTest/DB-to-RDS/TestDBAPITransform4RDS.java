/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.sdtm;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.QBTransformAction;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2008-06-09 19:54:07 $
 */
public class TestDBAPITransform4RDS {
    public static void main(String[] args) throws Exception {
    	if (args==null||args.length<1)
    	{
    		System.out.println("Usage:mapFile|output(Optonal)");
    		System.exit(-1);
    	}
    	String workingDir=FileUtil.getWorkingDirPath();
    	String mapFile=workingDir+File.separator+args[0];
    	String outPut=workingDir+File.separator+"dbOutput";
    	if (args.length==2)
    		outPut=args[1];
       File outputFile=new File(outPut);
       if (!outputFile.exists())
    	   outputFile.mkdir();

        String driver = System.getProperty("driver");
        String url = System.getProperty("url");
        String user = System.getProperty("user");
        String pass = System.getProperty("pass");
        new QBTransformAction().transformDB(mapFile, obtainConnection(driver, url, user, pass), outPut);
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
