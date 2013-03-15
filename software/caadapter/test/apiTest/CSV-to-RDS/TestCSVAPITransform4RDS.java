/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.sdtm;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSTransformer;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:54:07 $
 */
public class TestCSVAPITransform4RDS {
    public static void main(String[] args) throws Exception {
    	if (args==null||args.length<2)
    	{
    		System.out.println("Usage:mapFile|csvDataFile|(Optonal)");
    		System.exit(-1);
    	}
    	String workingDir=FileUtil.getWorkingDirPath();
    	String mapFile=workingDir+File.separator+args[0];
    	String csvFile=workingDir+File.separator+args[1];
    	String outPut=workingDir+File.separator+"output";
    	if (args.length==3)
    		outPut=args[2];
       File outputFile=new File(outPut);
       if (!outputFile.exists())
    	   outputFile.mkdir();

        new RDSTransformer().transformCSV(new File(mapFile), csvFile, outPut);
    }
}