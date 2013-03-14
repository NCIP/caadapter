/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.hl7message.instanceGen.util;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.data.impl.ClassLoaderTest;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
 *          date        Aug 3, 2007
 *          Time:       11:33:55 AM $
 */
public class MessageTypeDictionary
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: MessageTypeDictionary.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/util/MessageTypeDictionary.java,v 1.5 2008-10-28 20:54:11 umkis Exp $";

    private String MIF = "mif.zip";
    List<String> result = null;

    public MessageTypeDictionary(String msgType) throws ApplicationException
    {
        ClassLoaderTest clt = new ClassLoaderTest(MIF, msgType);

        if (!clt.wasSuccessful()) throw new ApplicationException(clt.getErrormessage());

        result = clt.getListEntryNames();
        /*
        Enumeration<URL> fileURLs = null;
        try
        {
            fileURLs= ClassLoader.getSystemResources(MIF);
        }
        catch(IOException ie)
        {
            throw new ApplicationException("IOException #1 : " + ie.getMessage());
        }
        if (fileURLs == null)
        {
            throw new ApplicationException("Result : Not Found any mif class");
        }
        //System.out.println("Number of Result : " + fileURLs.toString());
        int n = 0;
        while(fileURLs.hasMoreElements())
        {
            n++;
            URL fileURL = fileURLs.nextElement();
            System.out.println("fileURL ("+n+") : " + fileURL);

            String url = fileURL.toString();

            //System.out.println("Result "+n+" : " + name + " : " +  url);
            //URLConnection conn = null;

            InputStream stream = null;

            if ((url.toLowerCase().startsWith("jar:"))||(url.toLowerCase().startsWith("zip:")))
            {
                int idx = url.indexOf("!");
                if (idx < 0)
                {
                    System.err.println("Invalid jar file url : " + url);
                    continue;
                }
                String jarFileName = url.substring(4, idx);
                ZipFile jarFile = null;
                try
                {
                    jarFile = new JarFile(new File(new URI(jarFileName)));
                }
                catch(IOException ie)
                {
                    System.err.println("IOException - jar file failure : " + jarFileName);
                    continue;
                }
                catch(URISyntaxException ue)
                {
                    System.err.println("URISyntaxException - jar file failure : " + jarFileName);
                    continue;
                }
                Enumeration<? extends ZipEntry> jarEntries = jarFile.entries();
                //List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
                while(jarEntries.hasMoreElements())
                {
                    ZipEntry jarEntry = jarEntries.nextElement();
                    //System.out.println("JarEntry : " + jarEntry.getName());
                    String nameE = jarEntry.getName();
                    if (nameE.length() < (MIF.length()+5)) continue;
                    if (nameE.startsWith(MIF))
                    {

                        String mifName = nameE.substring(4);
                        if (!mifName.endsWith(MIF)) continue;
                        //if (mifName.indexOf("_MT") < 0) continue;
                        System.out.println("=====================================================================================================================================================================================================================");
                        //System.out.println("JarEntry : " + mifName);
                        MIFClass mifClass = null;
                        try
                        {
                            mifClass= MIFParserUtil.getMIFClass(mifName);

                        }
                        catch(Exception e)
                        {
                            System.err.println("JarEntry : " + mifName + " class loading failure!! : " + e.getMessage());
                            continue;
                        }
                        System.out.println("JarEntry : " + mifName + " ,1: " + mifClass.getName() + " ,2: " + mifClass.getNodeXmlName() + " ,3: " + mifClass.getParentXmlPath() + " ,4: " + mifClass.getReferenceName() + " ,5: " + mifClass.getCsvSegment() + " ,6: " + mifClass.getTitle() + " ,7: " + mifClass.getXmlPath() + " ,8: " + mifClass.toString());

                    }
                }
            }
            else
            {

            }

        }
        if (n==0) System.err.println("No Elements in this file : " + fileURLs);
        */
    }
    public List<String> getResult()
    {
        return result;
    }

    public static void main(String[] args)
    {
        try
        {
            String c = "";
            if (args.length == 0) c = null;
            else c = args[0];
            MessageTypeDictionary mm = new MessageTypeDictionary(c);
            int n = 0;
            for(String rst:mm.getResult())
            {
                n++;
                System.out.println("result of searching for '"+c+"' (" + n + ") :" + rst);
            }
        }
        catch(ApplicationException ae)
        {
            System.out.println("Error : " + ae.getMessage());
        }
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/09/29 20:05:54  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:53:53  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/03/20 03:49:26  umkis
 * HISTORY      : for re-assigning sort key to mif files
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/07 04:10:07  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 */
