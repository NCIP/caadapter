package gov.nih.nci.caadapter.ui.mapping.sdtm;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSTransformer;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.1 $
 *          $Date: 2007-10-15 21:15:14 $
 */
public class TestCSVAPITransform4RDS {
    public static void main(String[] args) throws Exception {
        String mapFile=FileUtil.getWorkingDirPath()+File.separator+"workingspace"+File.separator+"RDS"+File.separator+"test_4_SCS.map";
        String csvFile=FileUtil.getWorkingDirPath()+File.separator+"workingspace"+File.separator+"RDS"+File.separator+"test_4_SCS.csv";     
        new RDSTransformer().transformCSV(new File(mapFile), csvFile, "c:/");
    }
}