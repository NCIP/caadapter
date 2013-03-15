/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.common.ApplicationException;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Jan 27, 2009
 * Time: 12:46:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReassignSortKeyToMIF_DirBatch
{
    ReassignSortKeyToMIF_DirBatch(String dir)
    {
        process(dir);
    }

    private void process(String dir)
    {
        if ((dir == null)||(dir.trim().equals("")))
        {
            System.out.println("Null directory name:");
            return;
        }
        File fDir = new File(dir.trim());
        if (!fDir.exists())
        {
            System.out.println("This is not exist: " + dir);
            return;
        }
        if (!fDir.isDirectory())
        {
            System.out.println("This is not a directory name: " + dir);
            return;
        }
        File[] files = fDir.listFiles();
        for (File file:files)
        {
            String aPath = file.getAbsolutePath();
            if (file.isDirectory()) process(aPath);
            if (!file.isFile()) continue;

            if (!aPath.toLowerCase().endsWith(".mif")) continue;
            ReassignSortKeyToMIF re = null;
            try
            {
                re = new ReassignSortKeyToMIF(aPath, true);
            }
            catch(ApplicationException ae)
            {
                System.err.println("Failure ReassignSortKeyToMIF: " + aPath + " : " + ae.getMessage());
                continue;
            }
            if (re.wasOverWritten()) System.out.println("Success ReassignSortKeyToMIF: " + re.getNewFileName());
            else System.out.println("Not over Written ReassignSortKeyToMIF: " + re.getNewFileName());
        }
    }

    public static void main(String[] arg)
    {
        String file = "C:\\hl7\\V3_2008\\mifFile_Resorted";
        new ReassignSortKeyToMIF_DirBatch(file);
    }
}
