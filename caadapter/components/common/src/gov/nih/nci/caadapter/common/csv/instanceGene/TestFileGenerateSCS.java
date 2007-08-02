/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/instanceGene/TestFileGenerateSCS.java,v 1.2 2007-08-02 14:24:46 umkis Exp $
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

package gov.nih.nci.caadapter.common.csv.instanceGene;

import gov.nih.nci.caadapter.common.util.UUIDGenerator;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Jul 6, 2007
 *          Time:       4:02:14 PM $
 */
public class TestFileGenerateSCS
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: TestFileGenerateSCS.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/instanceGene/TestFileGenerateSCS.java,v 1.2 2007-08-02 14:24:46 umkis Exp $";

    //private List<String> list = new ArrayList<String>();
    DataNode head;
    DataNode currNode;
    String cont = "";
    String dataL = "";
    boolean success = false;
    boolean isNewMethod = false;


    TestFileGenerateSCS(String file, String title, String filePath)//, String listPath)
    {
        execute(file, title, filePath);
    }
    TestFileGenerateSCS(String file, String title, String filePath, boolean isNewMethod)//, String listPath)
    {
        this.isNewMethod = isNewMethod;
        execute(file, title, filePath);
    }
    private void execute(String file, String title, String filePath)
    {
        //makeList(listPath);
        if (isNewMethod)
        {
            cont = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                    "<csvMetadata uuid=\"" + UUIDGenerator.getUniqueString() + "\" version=\"1.2\">\r\n";
        }
        else
        {
            cont = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                    "<csvMetadata xmlPath=\"csvMetaData\" version=\"1.2\">\r\n";
        }
        DataNode temp = null;
        //String current = title;
        head = new DataNode(title);
        currNode = head;
        //String curr = "";
        String field = "";
        //String data = title;
        String readLineOfFile = "";
        String space = "    ";
        //int depth = 0;
        //int bdepth = 0;
        try
        {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            boolean catchTag = false;
            int seq = 0;
            int ser = 0;
            int n = 0;
            int m = 0;
            int idx = 0;
            //int idxD = 0;
            //int idxF = 0;
            FunctionItemList fiL = new FunctionItemList();

            while((readLineOfFile=br.readLine())!=null)
            {
                idx = readLineOfFile.indexOf("=>");
                //idxD = readLineOfFile.indexOf("=D>");
                //idxF = readLineOfFile.indexOf("=F>");
                if (idx < 0) continue;
                if (readLineOfFile.startsWith("#")) continue;
                String line = readLineOfFile.substring(0, idx).trim();
                String dat = readLineOfFile.substring(idx + 2).trim();

                /*
                if (fiL.checkChar(line))
                {
                    System.out.println("Del : " + line);
                    continue;
                }
                */
                String lin = "";

                for(int i=0;i<line.length();i++)
                {
                    String achar = line.substring(i, i+1);
                    if (achar.equals("[")) continue;
                    if (achar.equals("]")) continue;
                    lin = lin + achar;
                }

                List<String> list = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(lin, ".");
                while(st.hasMoreTokens())
                {
                    list.add(st.nextToken());
                }

                //curr = "";
                
                if (list.size() < 3)
                {
                    System.err.println("Header item is less than 3 : " + lin);
                    return;
                }
                if ((list.get(0).equals(title))&&(list.size() != 3))
                {
                    System.out.println("Invalid Header list : " + lin);
                    return;
                }
                field = list.get(list.size()-2) + "_" + list.get(list.size()-1);
                if (list.get(0).equals(title))
                {
                    currNode.inputField(field, dat);
                    continue;
                }
                temp = head;
                //System.out.println("SSSSSSSSSSSSSS : " + readLineOfFile);

                for (int i=0;i<(list.size()-2);i++)
                {
                    if (this.getChildNodeWithName(temp, list.get(i)) == null)
                    {
                        if (list.get(i).equalsIgnoreCase(title))  System.out.println("Redundant head Node : " + i + " : " + title);
                        DataNode tmp = new DataNode(list.get(i), temp);
                        if (temp.isLeafNode())
                        {
                            temp.setLower(tmp);
                            temp = tmp;
                        }
                        else
                        {
                            temp = this.getLastChild(temp);
                            temp.setRight(tmp);
                            temp = tmp;

                        }
                    }
                    else temp = this.getChildNodeWithName(temp, list.get(i));
                    //System.out.println("  " + i + " : " + this.getSegmentName(temp));
                }
                if (!temp.isLeafNode())
                {
                    System.out.println("SSS: This node is not a leaf node : " + readLineOfFile + " : " + this.getSegmentName(temp) + " : " + list.get(list.size()-3));
                    temp = this.getChildNodeWithName(temp, list.get(list.size()-3));
                }
                temp.inputField(field, dat);
            }
        }
        catch(IOException e)
        {
            System.out.println("Main Meta File Not Found : " + file);
        }
        currNode = null;
        while(true)
        {
            currNode = this.getNextNode(currNode);
            if (currNode == null) break;
            temp = currNode;
            String str = "";
            while(true)
            {
                str = temp.getNodeName() + "." + str;
                temp = temp.getUpper();
                if (temp == null) break;
            }
            System.out.println("DATA TREE : " + str);
            for(int i=0;i<currNode.getFieldSize();i++) System.out.println("          :    " + currNode.getFieldName(i));
        }

        currNode = null;
        while(true)
        {
            currNode = this.getNextNode(currNode);
            if (currNode == null) break;
            space = "    ";
            for (int i=0;i<(this.getDepth(currNode));i++) space = space + "    ";
            if (currNode.getFieldSize() == 0) dataL = dataL + this.getSegmentName2(currNode) + ",ZZ\r\n";
            else
            {
                dataL = dataL + this.getSegmentName2(currNode) + ",";
                for (int i=0;i<(currNode.getFieldSize());i++)
                {
                    String data = currNode.getFieldData(i).trim();
                    if (data.indexOf(",") >= 0)
                    {
                        if (!data.startsWith("\"")) data = "\"" + data;
                        if (!data.endsWith("\"")) data = data + "\"";
                    }
                    dataL = dataL + data + ",";
                }
                dataL = dataL.substring(0, dataL.length()-1) + "\r\n";
            }

            if (currNode == head)
            {
                if (isNewMethod)
                {
                    cont = cont + space + "<segment name=\"" + this.getSegmentName2(currNode) + "\" xmlPath=\"" + currNode.getXPath() + "\" cardinality=\"0..*\">\r\n";
                }
                else cont = cont + space + "<segment name=\"" + this.getSegmentName2(currNode) + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\r\n";
                //********************************
                if (!currNode.isLeafNode())
                {
                    temp = currNode.getLower();
                    while(true)
                    {
                        cont = cont + "###" + temp.getUUID() + "%%%";
                        if (temp.isEndRight()) break;
                        temp = temp.getRight();
                    }
                }
                temp = currNode;
                if (temp.getFieldSize() == 0)
                {
                    if (isNewMethod)
                    {
                        cont = cont + space + "    <field column=\"1\" name=\"dummy\" xmlPath=\"" + temp.getXPath() + ".dummy\"/>\r\n";
                        cont = cont + space + "</segment> " + "<!--"+this.getSegmentName2(temp)+"-->\r\n";
                    }
                    else
                    {
                        cont = cont + space + "    <field column=\"1\" name=\"dummy\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\r\n";
                        cont = cont + space + "</segment> " + "<!--"+this.getSegmentName2(temp)+"-->\r\n";
                    }
                }
                else
                {
                    for (int i=0;i<(temp.getFieldSize());i++)
                    {
                        if (isNewMethod)
                        {
                            cont = cont + space + "    <field column=\"" + (i+1) + "\" name=\"" + temp.getFieldName(i) + "\" xmlPath=\"" + temp.getXPath() + "\"/>\r\n";
                        }
                        else cont = cont + space + "    <field column=\"" + (i+1) + "\" name=\"" + temp.getFieldName(i) + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\r\n";
                    }
                    cont = cont + space + "</segment> " + "<!--"+this.getSegmentName2(temp)+"-->\r\n";
                }
                cont = cont + "</csvMetadata>\n";
                //System.out.println("&&&&&&&&&&&&&&& Cont ....\n" + cont);

                //**************************************
            }
            else
            {
                String cont2 = "";
                if (isNewMethod)
                {
                    cont2 = cont2 + space + "<segment name=\"" + this.getSegmentName2(currNode) + "\" xmlPath=\"" + currNode.getXPath() + "\" cardinality=\"0..*\">\r\n";
                }
                else cont2 = cont2 + space + "<segment name=\"" + this.getSegmentName2(currNode) + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\r\n";

                if (!currNode.isLeafNode())
                {
                    temp = currNode.getLower();
                    while(true)
                    {
                        cont2 = cont2 + "###" + temp.getUUID() + "%%%";
                        if (temp.isEndRight()) break;
                        temp = temp.getRight();
                    }
                }
                temp = currNode;
                if (temp.getFieldSize() == 0)
                {
                    if (isNewMethod)
                    {
                        cont2 = cont2 + space + "    <field column=\"1\" name=\"dummy\" xmlPath=\"" + temp.getXPath() + ".dummy\"/>\r\n";
                    }
                    else cont2 = cont2 + space + "    <field column=\"1\" name=\"dummy\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\r\n";
                    cont2 = cont2 + space + "</segment> " + "<!--"+this.getSegmentName2(temp)+"-->\r\n";
                }
                else
                {
                    for (int i=0;i<(temp.getFieldSize());i++)
                    {
                        if (isNewMethod)
                        {
                            cont2 = cont2 + space + "    <field column=\"" + (i+1) + "\" name=\"" + temp.getFieldName(i) + "\" xmlPath=\"" + temp.getXPath() + "\"/>\r\n";
                        }
                        else cont2 = cont2 + space + "    <field column=\"" + (i+1) + "\" name=\"" + temp.getFieldName(i) + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\r\n";
                    }
                    cont2 = cont2 + space + "</segment> " + "<!--"+this.getSegmentName2(temp)+"-->\r\n";
                    //if (cont2.indexOf("SUBJECT") > 0) System.out.println("****** Subject ....\n" + cont2);
                    //System.out.println("****** Subject ...."+ this. +"\n" + cont2);

                }
                //if (cont2.indexOf("SUBJECT") > 0) System.out.println("****** Subject ....\n" + cont2);

                cont = cont.replace("###" + currNode.getUUID() + "%%%", cont2);
                //cont = cont + "</csvMetadata>\n";
                //**************************************

            }

            //currNode.setDoHead();
        }
        //cont = cont + "</csvMetadata>\n";
        System.out.println("**** SCS Data ..........\n");
        System.out.println(cont);
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(filePath + ".scs");
            //fw.write(cont.replaceAll("\n", "\r\n"));
            fw.write(cont);
            fw.close();
            success = true;
        }
        catch(IOException ie)
        {
            System.err.println("SCS file Writing error : " + ie.getMessage());
            success = false;
        }
        System.out.println("CVVV : SCS file Writing : " + filePath + ".scs");
        if (isNewMethod)
        {
            SCSIDChangerToXMLPath changer = new SCSIDChangerToXMLPath(filePath + ".scs");//, filePath + "_X.scs");
            if (!changer.wasSuccessful())
            {
                System.err.println("SCS file changer Writing error : " + changer.getChangedFileName() + " : " + changer.getErrorMessage());
                success = false;
            }
        }

        System.out.println("**** CSV Data ..........\n");
        System.out.println(dataL);
        try
        {
            fw = new FileWriter(filePath + ".csv");
            fw.write(dataL);
            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("CSV file Writing error : " + ie.getMessage());
            success = false;
        }
        /*
                //m = 2;
                //String tmp = lin.toUpperCase();
                //if ((tmp.indexOf("TIME") >= 0)&&(tmp.indexOf("VALUE") >= 0)) m=3;
                //if ((tmp.indexOf("ADDR") >= 0)&&(tmp.indexOf("USE") >= 0)) m=1;
                //if ((tmp.indexOf("NAME") >= 0)&&(tmp.indexOf("USE") >= 0)) m=1;
                n = list.size();
                if (n <= depth)
                {
                    System.out.println("Error.....");
                    continue;
                }
                m = n - depth;
                if (curr.equals(title)) depth = 0;
                if (n == m)
                {
                    curr = title;
                    field = "";
                    for (int i=0;i<m;i++) field = field + "_" + list.get(i);
                    field = field.substring(1);
                }
                else
                {
                    curr = "";
                    for(int i=0;i<(n-m);i++) curr = curr + "_" + list.get(i).toUpperCase();
                    curr = curr.substring(1);
                    field = "";
                    for (int i=0;i<m;i++) field = field + "_" + list.get(n-(m-i));
                    field = field.substring(1);

                }

                if (!current.equals(curr))
                {
                    ser++;
                    String str = "";
                    if (current.equals(title)) str = "";
                    else
                    {
                        for (int i=0;i<(bdepth-depth+1);i++)
                        str = str + "      </segment>\n";
                    }
                    current = curr;
                    bdepth = depth;
                    data = data + "\n" + curr; // + ser;
                    cont = cont + "\n" + str + "      <segment name=\"" + curr +  "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">";
                    seq = 0;
                }

                data = data + "," + dat;
                cont = cont + "\n" + "         <field column=\"" + ++seq + "\" name=\"" + field + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>";
            //}

            for (int i=0;i<depth;i++) cont = cont + "\n   </segment>";
            //String
            cont = cont + "\n   </segment>\n</csvMetadata>";

            System.out.println("depth = " + depth + "\n\n\nSCS FILE ---------------------------\n" + cont + "\n\n\nDAT FILE -----------------------------------\n" + data);

            fr.close();
            br.close();
            */
    }

    public boolean checkSuccess()
    {
        return success;
    }

    private int getDepth(DataNode curr)
    {
        int n = 0;
        if (curr == head) return 0;
        DataNode temp = curr;
        while(true)
        {
            n++;
            temp = temp.getUpper();
            if (temp == head) break;
        }
        return n;
    }

    private DataNode getLastChild(DataNode curr)
    {
        if (curr.isLeafNode()) return null;
        DataNode temp = curr.getLower();
        while(true)
        {
            if (temp.isEndRight()) break;
            temp = temp.getRight();
        }
        return temp;
    }

    private DataNode getChildNodeWithName(DataNode curr, String find)
    {
        if (curr.isLeafNode()) return null;
        DataNode temp = curr.getLower();
        String tt = "";
        while(true)
        {
            tt = temp.getNodeName();
            if (tt.trim().equals(find.trim())) break;
            if (temp.isEndRight()) return null;
            temp = temp.getRight();
        }
        return temp;
    }
    private DataNode getChildNodeWithIndex(DataNode curr, int idx)
    {
        if (curr.isLeafNode()) return null;
        DataNode temp = curr.getLower();
        int index = 0;
        while(true)
        {
            if (idx == index) break;
            if (temp.isEndRight()) return null;
            temp = temp.getRight();
            index++;
        }
        return temp;
    }
    private String getSegmentName(DataNode curr)
    {
        if (curr == head) return curr.getNodeName();
        String st = "";
        DataNode temp = curr;
        while (temp!=head)
        {
            st = temp.getNodeName().toUpperCase() + "_" + st;
            temp = temp.getUpper();
        }
        return st.substring(0, st.length()-1);
    }
    private String getSegmentName2(DataNode curr)
    {
        if (curr == head) return curr.getNodeName().toUpperCase();
        else return curr.getNodeName().toUpperCase() + "_" + this.getTreeIndexAddress(curr);
    }

    private String getSequenceNumber(DataNode curr)
    {
        if (curr == head) return "0";
        int n = 0;
        DataNode temp = curr.getUpper().getLower();
        String st = "";
        while(true)
        {
            n++;
            if (temp == curr) break;
            temp = temp.getRight();
        }
        if (n < 10) st = "" + n;
        else if (n == 10) st = "A";
        else if (n == 11) st = "B";
        else if (n == 12) st = "C";
        else if (n == 13) st = "D";
        else if (n == 14) st = "E";
        else if (n == 15) st = "F";
        else if (n == 16) st = "G";
        else if (n == 17) st = "H";
        else if (n == 18) st = "I";
        else if (n == 19) st = "J";
        return st;
    }

    private String getTreeIndexAddress(DataNode curr)
    {
        DataNode temp = curr;
        String addr = "";
        while(temp!=head)
        {
            addr = this.getSequenceNumber(temp) + addr;
            temp = temp.getUpper();
        }
        return addr;
    }

    private DataNode getNextNode(DataNode curr)
    {
        if (curr == null) return head;
        DataNode temp = curr;

        if (!temp.isLeafNode()) return temp.getLower();
        else
        {
            if (!temp.isEndRight())
            {
                return temp.getRight();
            }
            else
            {
                while(true)
                {
                    temp = temp.getUpper();
                    if (temp == head) return null;
                    //if (temp == null) return null;
                    //if (temp == head.getUpper()) return null;
                    if (!temp.isEndRight()) return temp.getRight();
                }
            }
        }
    }

    public static void main(String args[])
    {
        /*
        new TestFileGenerateSCS("C:\\projects\\hl7sdk\\workingspace\\examples\\402001\\SampleMessageDesign.txt",
                                "CTLABORATORY",
                                "C:\\projects\\hl7sdk\\workingspace\\examples\\402001\\depth.txt");
    }
        new TestFileGenerateSCS("C:\\caAdapter_download\\workingspace\\examples\\030001\\SampleMessageDesign2.txt",
                                "CTLABORATORY",
                                "C:\\caAdapter_download\\workingspace\\examples\\030001\\depth.txt");
        */
        //new TestFileGenerateSCS_NoDepthFile("C:\\projects\\hl7sdk\\workingspace\\examples\\040002_new\\SampleMessageDesign.txt",
        //                        "SAE");
        new TestFileGenerateSCS("C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\sampleMessageDesign040003.txt",
                                            "PROCEVN",
                                            "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040003_generated");
    }
}

class DataNode
{
    private String uuid = UUIDGenerator.getUniqueString();
    private String nodeName = "";
    //private String data = "";
    private List<String> fieldName = new ArrayList<String>();
    private List<String> fieldData = new ArrayList<String>();
    boolean doHead = false;
    boolean doTail = false;

    DataNode upper = null;
    DataNode right = null;
    DataNode lower = null;

    DataNode(String node)
    {
        nodeName = node;
    }
    DataNode(String node, DataNode upperK)
    {
        nodeName = node;
        upper = upperK;
    }

    public boolean inputField(String inF, String inD)
    {
        if (!fieldName.add(inF)) return false;
        if (!fieldData.add(inD)) return false;
        return true;
    }
    public int getFieldSize()
    {
        return fieldName.size();
    }
    public String getFieldName(int i)
    {
        return fieldName.get(i);
    }
    public String getFieldData(int i)
    {
        return fieldData.get(i);
    }
    public boolean checkDataHas()
    {
        if (fieldName.size() > 0) return true;
        else return false;
    }
    public void setUpper(DataNode d)
    {
        upper = d;
    }
    public DataNode getUpper()
    {
        return upper;
    }
    public void setRight(DataNode d)
    {
        right = d;
    }
    public DataNode getRight()
    {
        return right;
    }
    public void setLower(DataNode d)
    {
        lower = d;
    }
    public DataNode getLower()
    {
        return lower;
    }
    public boolean isHead()
    {
        if (upper == null) return true;
        else return false;
    }
    public boolean isEndRight()
    {
        if (right == null) return true;
        else return false;
    }
    public boolean isLeafNode()
    {
        if (lower == null) return true;
        else return false;
    }
    public String getNodeName()
    {
        return nodeName;
    }
    public boolean checkDoHead()
    {
        return doHead;
    }
    public void setDoHead()
    {
        doHead = true;
    }
    public boolean checkDoTail()
    {
        return doTail;
    }
    public void setDoTail()
    {
        doTail = true;
    }

    public String getXPath()
    {
        DataNode node = this;
        String xpath = "";
        while(node != null)
        {
            xpath = node.getNodeName() + "." + xpath;
            node = node.getUpper();
        }
        return xpath.substring(0, xpath.length()-1);
    }

    public String getUUID()
    {
        return uuid;
    }
    /*
    class DataNode
    {
        List<String> listValue = new ArrayList<String>();
        List<String> listField = new ArrayList<String>();

    }
    */
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
