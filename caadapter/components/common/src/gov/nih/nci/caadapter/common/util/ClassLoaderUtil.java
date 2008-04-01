/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/ClassLoaderUtil.java,v 1.7 2008-03-25 02:07:02 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.common.util;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.DataInputStream;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.7 $
 *          date        Jul 13, 2007
 *          Time:       5:31:06 PM $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/ClassLoaderUtil.java,v 1.7 2008-03-25 02:07:02 umkis Exp $";

    private List<InputStream> streams = new ArrayList<InputStream>();
    private List<String> names = new ArrayList<String>();
    private List<String> urls = new ArrayList<String>();
    private List<String> fileNames = new ArrayList<String>();

    public ClassLoaderUtil(String name) throws IOException
    {
        initialWork(name, true);
    }
    public ClassLoaderUtil(String name, boolean transformYNStreamsToFiles) throws IOException
    {
        initialWork(name, transformYNStreamsToFiles);
    }
    private void initialWork(String name, boolean transformYNStreamsToFiles) throws IOException
    {
        if (!transformYNStreamsToFiles) fileNames = null;
        if ((name == null)||(name.trim().equals(""))) throw new IOException("Class loader Path is null");
        name = name.trim();

        Enumeration<URL> fileURLs = null;
        String messages = "";
        fileURLs = ClassLoader.getSystemResources(name);

        if (fileURLs == null) throw new IOException("Class loader search Result : " + name + " : Not Found");

        while(fileURLs.hasMoreElements())
        {
            URL fileURL = fileURLs.nextElement();
            String url = fileURL.toString();
            urls.add(url);
            InputStream stream = null;

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

        if (streams.size() == 0)
        {
            if (messages.equals("")) throw new IOException("Not found any InputStream : " + name);
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2007/09/07 14:40:21  umkis
 * HISTORY      : Temporary files will be automatically deleted when system exit.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/09 01:56:52  umkis
 * HISTORY      : add a feature that v2Meta directory creating when search the directory
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/08 21:18:10  umkis
 * HISTORY      : Change to V3VocabularyTreeBuildEventHandler
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/08 20:33:11  umkis
 * HISTORY      : V3 Vocavulary utility objects initializing setup
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/15 05:27:25  umkis
 * HISTORY      : change InputStream to Temp file
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/14 20:17:17  umkis
 * HISTORY      : new utils
 * HISTORY      :
 */
