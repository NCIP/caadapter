/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.DataInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 22, 2010
 * Time: 10:41:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassLoaderUtil
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: ClassLoaderUtil.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/ClassLoaderUtil.java,v 1.12 2009-07-20 14:03:36 altturbo Exp $";

    private List<InputStream> streams = new ArrayList<InputStream>();
    private List<String> names = new ArrayList<String>();
    private List<String> urls = new ArrayList<String>();
    private List<String> fileNames = new ArrayList<String>();

    public ClassLoaderUtil(String name) throws IOException
    {
        initialWork(name, null, true);
    }
    public ClassLoaderUtil(String name, boolean transformYNStreamsToFiles) throws IOException
    {
        initialWork(name, null, transformYNStreamsToFiles);
    }
    public ClassLoaderUtil(String name, String zipF) throws IOException
    {
        initialWork(name, zipF, true);
    }
    public ClassLoaderUtil(String name, String zipF, boolean transformYNStreamsToFiles) throws IOException
    {
        initialWork(name, zipF, transformYNStreamsToFiles);
    }
    private void initialWork(String name, String zipF, boolean transformYNStreamsToFiles) throws IOException
    {
        if (!transformYNStreamsToFiles) fileNames = null;
        if ((name == null)||(name.trim().equals(""))) throw new IOException("Class loader Path is null");
        name = name.trim();

        if (zipF != null)
        {
            File file = new File(zipF);
            if ((!file.exists())||(!file.isFile())) zipF = null;
            else
            {
                if ((zipF.toLowerCase().trim().endsWith(".zip"))||(zipF.toLowerCase().trim().endsWith(".jar"))) {}
                else zipF = null;
            }
        }

        if (zipF == null) initialWorkWithoutZipFile(name, transformYNStreamsToFiles);
        else initialWorkWithZipFile(name, zipF, transformYNStreamsToFiles);
    }

    private void initialWorkWithoutZipFile(String name, boolean transformYNStreamsToFiles) throws IOException
    {
        if (streams == null) streams = new ArrayList<InputStream>();
        Enumeration<URL> fileURLs = null;
        String messages = "";
        fileURLs = ClassLoader.getSystemResources(name);

        if (fileURLs == null) throw new IOException("Class loader search Result : " + name + " : Not Found");
        int count = 0;
        //System.out.println("&&& 11 URL for ZIP: " + name);
        while(fileURLs.hasMoreElements())
        {
            count++;
            URL fileURL = fileURLs.nextElement();
            String url = fileURL.toString();
            urls.add(url);
            InputStream stream = null;
            //System.out.println("&&& 22 URL for ZIP: " + url);
            if ((url.toLowerCase().startsWith("jar:"))||(url.toLowerCase().startsWith("zip:")))
            {

                int idx = url.indexOf("!");
                if (idx < 0)
                {
                    messages = messages + "Invalid jar file url : " + url + "\r\n";
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
                    messages = messages + "IOException - jar file failure : " + jarFileName + "\r\n";
                    continue;
                }
                catch(URISyntaxException ue)
                {
                    messages = messages + "URISyntaxException - jar file failure : " + jarFileName + "\r\n";
                    continue;
                }
                Enumeration<? extends ZipEntry> jarEntries = jarFile.entries();

                while(jarEntries.hasMoreElements())
                {
                    ZipEntry jarEntry = jarEntries.nextElement();

                    String nameE = jarEntry.getName();
                    if (nameE.startsWith(name))
                    {
                        //System.out.println("JarEntry : " + jarEntry.getName());
                        DataInputStream dis = null;
                        try
                        {
                            stream = jarFile.getInputStream(jarEntry);
                            streams.add(stream);
                            names.add(nameE);
                            //System.out.println("WWWZZ : " + getFileName(nameE) + " : " + nameE);
                        }
                        catch(IOException ie)
                        {
                            messages = messages + "Connection IOException : " + ie.getMessage() + "\r\n";
                            continue;
                        }
                    }
                }
            }
            else
            {
                try
                {
                    stream = (fileURL.openConnection()).getInputStream();
                    streams.add(stream);
                    names.add(getFileName(fileURL.toString()));
                }
                catch(IOException ie)
                {
                    messages = messages + "Connection IOException : " + ie.getMessage() + "\r\n";
                    continue;
                }
            }
        }
        //System.out.println("&&& 12 URL for ZIP: " + count + " : " + streams.size());

        if ((count == 0)||(streams.size() == 0))
        {
            String res1 = FileUtil.searchFile("caAdapter.jar");
            if (res1 == null) throw new IOException("Class loader search Result (2) : " + name + " : Not Found (caAdapter.jar)");
            //System.out.println("&&& 12-1  URL for ZIP: " + res1);

            try
            {
                initialWorkWithZipFile(name, res1, transformYNStreamsToFiles);
            }
            catch(Exception e)
            {
                //System.out.println("&&& 12-2  ERROR (caAapter.jar)  : " + e.getMessage());
                streams = null;
            }
            if (streams == null) streams = new ArrayList<InputStream>();
            if ((streams.size() == 0)&&(fileNames.size() == 0))
            {
                File file = new File(res1);
                File parent = file.getParentFile();
                File[] list = parent.listFiles();
                for(File f:list)
                {
                    try
                    {
                        if (!f.isFile()) continue;
                        String fName = f.getName();
                        ///System.out.println("&&& 12-1-1  FNAME  : " + f.getAbsolutePath());
                        if ((fName.toLowerCase().trim().endsWith(".zip"))||(fName.toLowerCase().trim().endsWith(".jar"))) {}
                        else continue;
                        initialWorkWithZipFile(name, f.getAbsolutePath(), transformYNStreamsToFiles);
                    }
                    catch(Exception e)
                    {
                        //System.out.println("&&& 12-1-3  ERROR  : " + e.getMessage());
                        streams = null;
                    }
                    if (streams == null) streams = new ArrayList<InputStream>();
                    //System.out.println("&&& 12-1-2 SIZE : " + streams.size());
                    if ((streams.size() > 0)||(fileNames.size() > 0)) break;
                }
            }
        }

        if (streams == null) streams = new ArrayList<InputStream>();
        //System.out.println("&&& 13 URL for ZIP: "  + streams.size());
        if ((streams.size() == 0)&&(fileNames.size() == 0))
        {
            if (messages.equals("")) throw new IOException("Not found any InputStream (1) : " + name);
            else throw new IOException(messages);
        }


        if ((transformYNStreamsToFiles)&&(fileNames.size() == 0))
        {
            List<InputStream> Tstreams = new ArrayList<InputStream>();
            List<String> Tnames = new ArrayList<String>();
            for (int i=0;i<streams.size();i++)
            {
                InputStream stream = streams.get(i);
                String nameS = names.get(i);


                String fileName = FileUtil.getTemporaryFileName();
                if ((nameS.length() > 5)&&(nameS.substring(nameS.length()-4, nameS.length()-3).equals(".")))
                {
                    fileName = fileName.substring(0, fileName.length()-4) + "." + nameS.substring(nameS.length()-3);
                }

                try
                {
                    fileName = FileUtil.downloadFromInputStreamToFile(stream, fileName);
                }
                catch(IOException ie)
                {
                    continue;
                }
                Tstreams.add(stream);
                Tnames.add(nameS);
                fileNames.add(fileName);
                File file = new File(fileName);
                file.deleteOnExit();
            }

            if (streams.size() != Tstreams.size()) names = Tnames;

            streams = null;
        }
    }

    private void initialWorkWithZipFile(String name, String zipF, boolean transformYNStreamsToFiles) throws IOException
    {
        if (streams == null) streams = new ArrayList<InputStream>();
        InputStream stream1 = null;
        String messages = "";
            urls.add((new File(zipF)).toURI().toURL().toString());

                ZipFile jarFile = null;
                try
                {
                    jarFile = new JarFile(new File(zipF));
                }
                catch(IOException ie)
                {
                    messages = messages + "IOException - jar file failure : " + zipF + "\r\n";
                    //continue;
                }

                Enumeration<? extends ZipEntry> jarEntries = jarFile.entries();

                while(jarEntries.hasMoreElements())
                {
                    ZipEntry jarEntry = jarEntries.nextElement();

                    String nameE = jarEntry.getName();
                    if (nameE.startsWith(name))
                    {
                        //System.out.println("JarEntry : " + jarEntry.getName());
                        DataInputStream dis = null;
                        try
                        {
                            stream1 = jarFile.getInputStream(jarEntry);
                            streams.add(stream1);
                            names.add(nameE);
                            //System.out.println("WWWZZ : " + getFileName(nameE) + " : " + nameE);
                        }
                        catch(IOException ie)
                        {
                            messages = messages + "Connection IOException : " + ie.getMessage() + "\r\n";
                            continue;
                        }
                    }
                }


        if (streams == null) streams = new ArrayList<InputStream>();
        if (streams.size() == 0)
        {
            if (messages.equals("")) throw new IOException("Not found any InputStream (2) : " + name);
            else throw new IOException(messages);
        }


        if (transformYNStreamsToFiles)
        {
            List<InputStream> Tstreams = new ArrayList<InputStream>();
            List<String> Tnames = new ArrayList<String>();
            for (int i=0;i<streams.size();i++)
            {
                InputStream stream = streams.get(i);
                String nameS = names.get(i);


                String fileName = FileUtil.getTemporaryFileName();
                if ((nameS.length() > 5)&&(nameS.substring(nameS.length()-4, nameS.length()-3).equals(".")))
                {
                    fileName = fileName.substring(0, fileName.length()-4) + "." + nameS.substring(nameS.length()-3);
                }

                try
                {
                    fileName = FileUtil.downloadFromInputStreamToFile(stream, fileName);
                }
                catch(IOException ie)
                {
                    continue;
                }
                Tstreams.add(stream);
                Tnames.add(nameS);
                fileNames.add(fileName);
                File file = new File(fileName);
                file.deleteOnExit();
            }

            if (streams.size() != Tstreams.size()) names = Tnames;

            streams = null;
        }
    }


    public List<InputStream> getInputStreams()
    {
        return streams;
    }
    public InputStream getInputStreamWithName(String name)
    {
        if (streams == null) return null;

        try
        {
            return streams.get(getIndexWithName(name));
        }
        catch(Exception ee)
        {
            return null;
        }
    }
    public String getFileNameWithName(String name)
    {
        if (fileNames == null) return null;

        try
        {
            return fileNames.get(getIndexWithName(name));
        }
        catch(Exception ee)
        {
            return null;
        }
    }
    public InputStream getInputStreamWithPath(String path)
    {
        if (streams == null) return null;

        try
        {
            return streams.get(getIndexWithPath(path));
        }
        catch(Exception ee)
        {
            return null;
        }
    }
    public String getFileNameWithPath(String path)
    {
        if (fileNames == null) return null;

        try
        {
            return fileNames.get(getIndexWithPath(path));
        }
        catch(Exception ee)
        {
            return null;
        }
    }
    public List<String> getFileNames()
    {
        return fileNames;
    }

    public String getFileName(int index)
    {
        if (fileNames == null) return null;
        if (fileNames.size() <= index) return null;
        return fileNames.get(index);
    }

    public int getSizeOfInputStreams()
    {
        if (streams == null) return 0;
        return streams.size();
    }
    public int getSizeOfFiles()
    {
        if (fileNames == null) return 0;
        return fileNames.size();
    }

    private String getFileName(String addr)
    {
        addr = addr.trim();
        String name = "";
        for (int i=addr.length();i>=0;i--)
        {

            String achar = addr.substring(i-1, i);
            //System.out.println("  TTT : " + achar);
            if (achar.equals("/")) break;
            if (achar.equals("\\")) break;
            name = achar + name;
        }
        //System.out.println("TTTZZ : " + name);
        return name;
    }

    public List<String> getURLs()
    {
        return urls;
    }


    public List<String> getNames()
    {
        return names;
    }
    public String getName(int index)
    {
        if (names.size() <= index) return null;
        return getFileName(names.get(index));
    }
    public String getPath(int index)
    {
        if (names.size() <= index) return null;
        return names.get(index);
    }
    public int getIndexWithName(String name)
    {
        if ((name == null)||(name.trim().equals(""))) return -1;
        name = name.trim();

        for (int i=0;i<names.size();i++)
        {
            String str = names.get(i).trim();
            if ((getFileName(str)).equals(name)) return i;
        }
        return -1;
    }
    public int getIndexWithPath(String path)
    {
        if ((path == null)||(path.trim().equals(""))) return -1;
        path = path.trim();

        for (int i=0;i<names.size();i++)
        {
            String str = names.get(i).trim();
            if (str.equals(path)) return i;
        }
        return -1;
    }
}