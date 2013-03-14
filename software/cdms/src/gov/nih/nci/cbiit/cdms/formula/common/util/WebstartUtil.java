/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import java.net.URL;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 2, 2010
 * Time: 11:42:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebstartUtil {
    private static boolean isWebstartDeployed=false;

    /**
     * @return the isWebstartDeployed
     */
    public static boolean isWebstartDeployed() {
        return isWebstartDeployed;
    }

    /**
     * @param isWebstartDeployed the isWebstartDeployed to set
     */
    public static void setWebstartDeployed(boolean isWebstartDeployed) {
        WebstartUtil.isWebstartDeployed = isWebstartDeployed;
    }

    public static void downloadFile(String srcFile, String targetFile)
    {
        try {

            URL isURL=FileUtil.retrieveResourceURL(srcFile);
            if (isURL==null)
                return;
            InputStream fis =isURL.openStream();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
            File outFile=new File(targetFile);
            //create the path folders
            if (!outFile.exists())
                outFile.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(targetFile);
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos));
            byte b;
            try{
                while(true){
                    b = (byte) dis.readByte();
                    dos.write(b);
                }
            }
            catch(EOFException e)
            {
                System.err.println("Finish read end of file...");
            }   finally{
                dos.close();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
