/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/SCSIDChangerToXMLPath.java,v 1.1 2007-08-02 16:29:40 umkis Exp $
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

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Aug 1, 2007
 *          Time:       11:13:15 AM $
 */
public class SCSIDChangerToXMLPath
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SCSIDChangerToXMLPath.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/SCSIDChangerToXMLPath.java,v 1.1 2007-08-02 16:29:40 umkis Exp $";

    private String saveFileName;

    private boolean success = false;
    private String message = "";

    public SCSIDChangerToXMLPath(String fileName)
    {
        saveFileName = fileName;
        mainProcess(fileName, saveFileName);
    }
    public SCSIDChangerToXMLPath(String fileName, String fileNameTarget)
    {
        saveFileName = fileNameTarget;
        mainProcess(fileName, saveFileName);
    }
    public SCSIDChangerToXMLPath(String fileName, boolean overWrite)
    {
        if ((fileName == null)||(fileName.trim().length() < 5))
        {
            message = "Null or Invalid source file neme";
            return;
        }
        if (overWrite) saveFileName = fileName;
        else saveFileName = fileName.substring(0, fileName.length()-4) + "_Changed.scs";
        mainProcess(fileName, saveFileName);
    }

    public boolean wasSuccessful() { return success; }
    public String getErrorMessage() { return message; }
    public String getChangedFileName() { return saveFileName; }

    private void mainProcess(String source, String target)
    {
        System.out.println("CVVV : X1 " +source + " : " + target);
        success = false;
        if ((source == null)||(source.trim().length() < 5)||(!source.trim().toLowerCase().endsWith(".scs")))
        {
            message = "Null or Invalid source file neme";
            return;
        }
        if ((target == null)||(target.trim().length() < 5)||(!target.trim().toLowerCase().endsWith(".scs")))
        {
            message = "Null or Invalid target file neme";
            return;
        }

        DataTree dataTree = new DataTree(source);
        if (!dataTree.checkSuccess())
        {
            message = dataTree.getErrorMessage();


            return;
        }

        NodeElement node = dataTree.getSCSHead();
        Stack<String> stack = new Stack<String>();
        //mainBuffer.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        FileWriter fw = null;

        try
        {
            fw = new FileWriter(target);
        }
        catch(IOException ie)
        {
            message = "Writing file open error (" + target + ") : " + ie.getMessage();
            return;
        }
        System.out.println("CVVV : Writing file open (" + target + ") " + node);
        int beforeDepth = 0;
        int depth = 0;
        int column = 0;
        String line = "";
        try
        {
            while(true)
            {
                //System.out.println("CVVV AAA2: " + node);
                if (node == null) depth = 0;
                else depth = dataTree.getDepth(node);

                if (beforeDepth != depth) column = 0;

                if (beforeDepth > depth)
                {
                    int depthDiffer = beforeDepth - depth;
                    for (int i=0;i<depthDiffer;i++)
                    {
                        line = stack.pop();
                        fw.write(line);
                        //System.out.println("CVVV BBB2: " + line);
                    }
                }
                if (node == null) break;
                String space = "";
                for (int i=0;i<depth;i++) space = space + "    ";

                String level = node.getLevel();
                if (level.equals("csvMetadata"))
                {
                    line = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                           "<csvMetadata xmlPath=\"csvMetaData\" version=\"1.2\">\r\n";
                    stack.push("</csvMetadata>\r\n");
                }
                else if (level.equals("segment"))
                {
                    line = space + "<segment name=\""+node.getName()+"\" xmlPath=\""+getXMLPath(node)+"\" cardinality=\"0..*\">\r\n";
                    stack.push(space + "</segment> <!--"+node.getName()+"-->\r\n");
                }
                else if (level.equals("field"))
                {
                    column++;
                    line = space + "<field column=\""+column+"\" name=\""+node.getName()+"\" datatype=\"String\" xmlPath=\""+getXMLPath(node)+"\"/>\r\n";
                }
                fw.write(line);
                //System.out.println("CVVV BBB2: " + line);
                beforeDepth = depth;
                node = dataTree.getNextNode(node);
            }
        }
        catch(IOException ie)
        {
            message = "Output file writing error (" + target + ") : " + line + " : " + ie.getMessage();
            return;
        }
        try
        {
            fw.close();
        }
        catch(IOException ie)
        {
            message = "Output file closing error (" + target + ") : " + ie.getMessage();
            return;
        }
        success = true;
    }
    private String getXMLPath(NodeElement node)
    {
        String xpath = "";
        while(true)
        {
            if (node.getLevel().equals("csvMetadata")) break;
            xpath = node.getName() + "." + xpath;
            node = node.getUpper();
        }
        return xpath.substring(0, xpath.length()-1);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/08/02 15:43:55  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 */
