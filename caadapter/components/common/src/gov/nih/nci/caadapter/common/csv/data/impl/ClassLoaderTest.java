/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/ClassLoaderTest.java,v 1.2 2008-06-04 14:46:14 umkis Exp $
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
 *          revision    $Revision: 1.2 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/ClassLoaderTest.java,v 1.2 2008-06-04 14:46:14 umkis Exp $";

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
 * HISTORY      : Revision 1.1  2007/09/08 21:00:50  umkis
 * HISTORY      : Temporary files will be automatically deleted when system exit.
 * HISTORY      :
 */
