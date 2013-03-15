/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.csv.data.impl;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.io.*;
import java.util.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.Attributes;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 12, 2007
 *          Time:       4:46:01 PM $
 */
public class ClassLoaderTest
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: ClassLoaderTest.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/ClassLoaderTest.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    public ClassLoaderTest(String name)
    {
        System.out.println("Strat search : " + name);
        Enumeration<URL> fileURLs = null;
        try
        {
            fileURLs= ClassLoader.getSystemResources(name);
        }
        catch(IOException ie)
        {
            System.out.println("IOException #1 : " + ie.getMessage());
        }
        if (fileURLs == null)
        {
            System.out.println("Result : " + name + " : Not Found");
            return;
        }
        System.out.println("Number of Result : " + fileURLs.toString());
        int n = 0;
        while(fileURLs.hasMoreElements())
        {
            n++;
            URL fileURL = fileURLs.nextElement();

            String url = fileURL.toString();

            System.out.println("Result "+n+" : " + name + " : " +  url);
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
                List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
                while(jarEntries.hasMoreElements())
                {
                    ZipEntry jarEntry = jarEntries.nextElement();
                    //System.out.println("JarEntry : " + jarEntry.getName());
                    String nameE = jarEntry.getName();
                    if (nameE.startsWith(name))
                    {
                        System.out.println("=====================================================================================================================================================================================================================");
                        System.out.println("JarEntry : " + jarEntry.getName());
                        DataInputStream dis = null;
                        try
                        {
                            stream = jarFile.getInputStream(jarEntry);
                            dis = new DataInputStream(stream);
                        }
                        catch(IOException ie)
                        {
                            System.out.println("Connection IOException : " + ie.getMessage());
                        }

                        while(true)
                        {

                            byte bt = -1;
                            try
                            {

                                bt = dis.readByte();
                            }
                            catch(IOException ie) { break; }
                            catch(NullPointerException ne) { break; }

                            if (bt < 0) break;
                            //byte bt = (byte) i;
                            char ch = (char) bt;
                            String c = "" + ch;
                            System.out.print(c);
                        }
                    }
                }
            }
            else
            {
                try
                {
                    stream = (fileURL.openConnection()).getInputStream();
                }
                catch(IOException ie)
                {
                    System.out.println("Connection IOException : " + ie.getMessage());
                }

                try
                {
                    while(true)
                    {
                        int i = stream.read();

                        if (i < 0) break;
                        byte bt = (byte) i;
                        char ch = (char) bt;
                        String c = "" + ch;

                        System.out.print(c);
                    }
                }
                catch(IOException ie)
                {
                    System.out.println("IOException : ");
                }
                catch(NullPointerException ne)
                {
                    System.out.println("NullPointerException : ");
                }
            }
            try
            {
                if (stream != null) stream.close();
            }
            catch(IOException ie)
            {
                System.out.println("IOException 2 : ");
            }
        }
    }

    public static void main(String args[])
    {
        new ClassLoaderTest(args[0]);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:53:56  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/04 14:46:14  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/09/08 21:00:50  umkis
 * HISTORY      : Temporary files will be automatically deleted when system exit.
 * HISTORY      :
 */
