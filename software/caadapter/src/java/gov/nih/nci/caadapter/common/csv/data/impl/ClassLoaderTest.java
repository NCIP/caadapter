/*L
 * Copyright SAIC, SAIC-Frederick.
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/ClassLoaderTest.java,v 1.5 2008-10-28 20:51:56 umkis Exp $";

    private List<DataInputStream> listStream = null;
    private List<String> listEntryNames = null;
    private String errorMessage;
    private boolean success = true;

    public ClassLoaderTest(String name)
    {
        process(name, null, false);
    }
    public ClassLoaderTest(String name, String entry)
    {
        process(name, entry, false);
    }

    private ClassLoaderTest(String name, String entry, boolean displayTag)
    {
        process(name, entry, displayTag);
    }

    private void process(String name, String entry, boolean displayTag)
    {
        if ((name == null)||(name.trim().equals("")))
        {
            processError(displayTag, "Search zip entry name is null.");
            return;
        }
        String entryFile = entry;
        if (entry == null) entryFile = name;
        if (displayTag) System.out.println("Start search : " + name + " : " + entryFile);
        Enumeration<URL> fileURLs = null;
        try
        {
            fileURLs= ClassLoader.getSystemResources(name);
        }
        catch(IOException ie)
        {
            processError(displayTag, "IOException #1 : " + ie.getMessage());
            return;
        }
        if (fileURLs == null)
        {
            processError(displayTag, "Result : " + name + " : Not Found");
            return;
        }
        if (displayTag) System.out.println("Number of Result : " + fileURLs.toString());
        int n = 0;
        while(fileURLs.hasMoreElements())
        {
            n++;
            URL fileURL = fileURLs.nextElement();

            String url = fileURL.toString();

            if (displayTag) System.out.println("Result "+n+" : " + name + " : " +  url);
            //URLConnection conn = null;
            String jarFileName = null;
            InputStream stream = null;
            boolean isZipFileNameDirect = false;
            if (((url.toLowerCase().startsWith("jar:"))||(url.toLowerCase().startsWith("zip:")))||
                ((url.toLowerCase().endsWith(".jar"))||(url.toLowerCase().endsWith(".zip"))))
            {
                int idx = url.indexOf("!");
                if (idx < 0)
                {
                    if ((url.toLowerCase().endsWith(".jar"))||(url.toLowerCase().endsWith(".zip")))
                    {
                        jarFileName = url;
                        isZipFileNameDirect = true;
                        if (entry == null) entryFile = null;
                        //jarFileName = url.substring(6);
                    }
                    else
                    {
                        processError(displayTag, "Invalid jar file url : " + url);
                        continue;
                    }
                }
                else jarFileName = url.substring(4, idx);

                ZipFile jarFile = null;
                try
                {
                    //File file = new File(jarFileName);
                    //if (file.exists()) jarFile = new JarFile(file);
                    //else
                        jarFile = new JarFile(new File(new URI(jarFileName)));
                }
                catch(IOException ie)
                {
                    processError(displayTag, "IOException - jar file failure : " + jarFileName);
                    continue;
                }
                catch(URISyntaxException ue)
                {
                    processError(displayTag, "URISyntaxException - jar file failure : " + jarFileName);
                    continue;
                }
                catch(IllegalArgumentException ue)
                {
                    processError(displayTag, "IllegalArgumentException - jar file failure : " + jarFileName);
                    continue;
                }
                Enumeration<? extends ZipEntry> jarEntries = jarFile.entries();
                //List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
                int m = 0;
                while(jarEntries.hasMoreElements())
                {
                    m++;
                    ZipEntry jarEntry = jarEntries.nextElement();
                    //System.out.println("JarEntry : " + jarEntry.getName());
                    String nameE = jarEntry.getName();
                    boolean check = false;
                    if (entryFile == null) check = true;
                    else if (nameE.indexOf(entryFile) >= 0) check = true;
                    if (check)
                    {
                        if (displayTag) System.out.println("=====================================================================================================================================================================================================================");
                        if (displayTag) System.out.println("JarEntry ("+m+") : " + nameE);
                        DataInputStream dis = null;
                        try
                        {
                            InputStream stream2 = jarFile.getInputStream(jarEntry);
                            dis = new DataInputStream(stream2);
                        }
                        catch(IOException ie)
                        {
                            processError(displayTag, "Connection IOException : " + ie.getMessage());
                            continue;
                        }

                        if (displayTag)
                        {
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
                        else
                        {
                            if (listStream == null) listStream = new ArrayList<DataInputStream>();
                            listStream.add(dis);
                            if (listEntryNames == null) listEntryNames = new ArrayList<String>();
                            listEntryNames.add(nameE);
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
                    processError(displayTag, "Connection IOException : " + ie.getMessage());
                }

                if (displayTag)
                {
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
            }
            try
            {
                if (stream != null) stream.close();
            }
            catch(IOException ie)
            {
                if (displayTag) System.out.println("IOException 2 : ");
            }
        }
        if (n==0) System.out.println("No item contains : ");
    }

    public List<DataInputStream> getListInputStreams()
    {
        return listStream;
    }
    public List<String> getListEntryNames()
    {
        return listEntryNames;
    }
    public String getErrormessage()
    {
        return errorMessage;
    }
    public boolean wasSuccessful()
    {
        return success;
    }
    private void processError(boolean displayTag, String msg)
    {
        errorMessage = msg;
        success = false;
        if (displayTag) System.out.println(errorMessage);
    }


    public static void main(String args[])
    {
        if (args.length == 0)
        {
            System.out.println("No Zip entry name");
        }
        else if (args.length == 1) new ClassLoaderTest(args[0], null, true);
        else if (args.length > 1) new ClassLoaderTest(args[0], args[1], true);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/06/09 19:53:49  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
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
