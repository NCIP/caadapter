/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/instanceGen/H3SInstanceMetaTree.java,v 1.1 2007-08-02 15:43:55 umkis Exp $
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

package gov.nih.nci.caadapter.hl7.instanceGen;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.standard.impl.MetaTreeMetaImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaFieldImpl;
import gov.nih.nci.caadapter.common.standard.impl.CommonAttributeItemImpl;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Jul 6, 2007
 *          Time:       2:43:54 PM $
 */

public class H3SInstanceMetaTree extends MetaTreeMetaImpl
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: H3SInstanceMetaTree.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/instanceGen/H3SInstanceMetaTree.java,v 1.1 2007-08-02 15:43:55 umkis Exp $";

    boolean isCode = false;
    String[] codeItems = null;
    MetaTreeMeta h3sVocTree = null;
    String displayName = "";

    String header = "";
    String headerName = null;
    String dataFileName;
    String dataPath;
    boolean success = false;

    private static int INPUT_DATA_TYPE_XML = 0;
    private static int INPUT_DATA_TYPE_BINARY = 1;

    int inputDataType = -1;

    public H3SInstanceMetaTree(String h3sFileName)
    {
        super();
        success = false;

        if ((h3sFileName == null)||(h3sFileName.trim().equals("")))
        {
            System.out.println("null h3s filename");
            return;
        }
        h3sFileName = h3sFileName.trim();

        if (!h3sFileName.toLowerCase().endsWith(".h3s"))
        {
            System.out.println("File type is mismatch (.h3s) : " +  h3sFileName);
            return;
        }

        File h3sFile = new File(h3sFileName);

        try
        {
            build(h3sFile);
        }
        catch(ApplicationException ae)
        {
            System.out.println("Build H3s Tree Error : " + ae.getMessage());
            return;
        }

        dataPath = h3sFileName.substring(0, h3sFileName.length()-4);
        dataFileName = dataPath + ".dat";

        FileWriter fw = null;
        try
        {
            fw = new FileWriter(dataFileName);
        }
        catch(IOException ie)
        {
            System.out.println("data file opening error for writing : " + ie.getMessage());
            return;
        }

        ValidatorResults result = validateTree(false);
        System.out.println("SSS...\n" + result.toString() + "\nEND...");

        displayTreeWithText();
        System.out.println("");

        if (!buildVocabularyTree())
        {
            //h3sVocTree.displayTreeWithText();
            System.out.println("Vocabulary Tree buliding failure....");
            return;
        }

        CommonNode temp = this.getHeadSegment();

        List<String> changeList = null;
        try
        {
            changeList = FileUtil.readFileIntoList("C:\\projects\\changeList.txt");
        }
        catch(IOException ie)
        {
            System.out.println("IOException :changeList : " + ie.getMessage());
            return;
        }

        List<String> replaceList = null;
        try
        {
            replaceList = FileUtil.readFileIntoList("C:\\projects\\replaceList.txt");
        }
        catch(IOException ie)
        {
            System.out.println("IOException :replaceList : " + ie.getMessage());
            return;
        }

        List<String> data = new ArrayList<String>();
        while(temp!=null)
        {
            if (temp instanceof MetaField)
            {
                MetaField field = (MetaField) temp;
                String line = field.generateXPath(".");

                if (!line.startsWith(".")) line = "." + line;

                H3SInstanceMetaSegment segment = (H3SInstanceMetaSegment) field.getParent();
                int numClone = 0;
                while(true)
                {
                    if (segment.getH3SSegmentType().getType() == H3SInstanceSegmentType.CLONE.getType()) numClone++;
                    //System.out.println("CVVV : numClone = ? : " +  segment.getName() + " : " + segment.getH3SSegmentType().toString());
                    segment = (H3SInstanceMetaSegment) segment.getParent();
                    if (segment == this.getHeadSegment()) break;
                    //todo
                }

                if (numClone == 1) numClone = 2;
                else if (numClone > 1) numClone = 3;
                else
                {
                    System.err.println("ERROR : numClone = 0 : " +  line);
                    return;
                }

                int thirdPeriodPoint = 0;
                int secondPeriodPoint = 0;
                int countPeriod = 0;
                for(int i=0;i<line.length();i++)
                {
                    String achar = line.substring(i, i+1);
                    if (achar.equals(".")) countPeriod++;
                    if (numClone == 2)
                    {
                        if (countPeriod == 2)
                        {
                            if (secondPeriodPoint == 0) secondPeriodPoint = i;
                        }
                        if (countPeriod == 3)
                        {
                            thirdPeriodPoint = i;
                            break;
                        }
                    }
                    else
                    {
                        if (countPeriod == numClone)
                        {
                            thirdPeriodPoint = i;
                            break;
                        }
                    }
                }

                if (numClone == 2)
                {
                    header = line.substring(secondPeriodPoint+1, thirdPeriodPoint).toUpperCase() + "_HEAD";
                    line = header + "." + line.substring(thirdPeriodPoint+1);
                }
                else
                {
                    line = line.substring(thirdPeriodPoint+1);
                }



                line = replaceLine(line, replaceList);

                line = changeLine(line, changeList, field);

                if ((line!=null)&&(!line.trim().equals("")))
                {
                    System.out.println(line);

                    try
                    {
                        fw.write(line + "\r\n");
                    }
                    catch(IOException ie)
                    {
                        System.out.println("data file Writing error (2) : " + ie.getMessage());
                        return;
                    }
                }
            }

            try
            {
                temp = this.nextTraverse(temp);
            }
            catch(ApplicationException ae)
            {
                System.out.println("Travers Error");
                return;
            }
        }
        try
        {
            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("data file closing error : " + ie.getMessage());
            return;
        }
        GenerateMapFileFromDataFile generate = null;
        if (inputDataType == INPUT_DATA_TYPE_XML)
           generate = new GenerateMapFileFromDataFile(dataFileName, header,  dataPath, h3sFileName);
        else generate = new GenerateMapFileFromDataFile(dataFileName, header,  dataPath, h3sFileName, this.getHeadSegment());

        if (!generate.getSuccess())
        {
            System.out.println("Test Instance generating failure...");
            return;
        }
        success = true;
    }
    public String getHeader()
    {
        return header;
    }

    public String getDataFileName()
    {
        return dataFileName;
    }

    public boolean wasSuccess()
    {
        return success;
    }
    private boolean isCodeDataType(String datatype)
    {
        if ((datatype == null)||(datatype.trim().equals(""))) return false;
        String[] codeDataTypes = new String[] {"CD", "CE", "CS", "SC", "CNE", "CWE"};
        datatype = datatype.trim();
        for (int i=0;i<codeDataTypes.length;i++)
        {
            if (codeDataTypes[i].equals(datatype)) return true;
        }
        return false;
    }
    private String getAttributeItemValue(CommonAttribute att, String itemName)
    {
        String res = "";
        try
        {
            res = att.getAttributeItem(itemName).getItemValue();
        }
        catch(NullPointerException ne)
        {}
        if (res == null) return "";
        else return res.trim();
    }
    private String changeLine(String line, List<String> changeList, MetaField field)
    {
        H3SInstanceMetaSegment parent = (H3SInstanceMetaSegment) field.getParent();
        String datatype = getAttributeItemValue(parent.getAttributes(), "datatype");
        String codingStrength = getAttributeItemValue(parent.getAttributes(), "codingStrength");
        String datatypePN = getAttributeItemValue(parent.getParent().getAttributes(), "datatype");
        String hl7Default = getAttributeItemValue(parent.getAttributes(), "hl7-default");
        String userDefault = getAttributeItemValue(parent.getAttributes(), "user-default");

        if ((line.endsWith("code"))&&(!((isCodeDataType(datatype))||(isCodeDataType(codingStrength)))))
            System.err.println("CCCXX9 : Unmatched code : " + line);
        if ((isCodeDataType(datatype))||(isCodeDataType(codingStrength)))
        {
            if (line.endsWith("code"))
            {
                if (codeItems != null)
                {
                    System.err.println("Code items array is not delete before : ");
                    codeItems = null;
                }
                String domainName = "";
                try
                {
                    domainName = parent.getAttributes().getAttributeItem("domainName").getItemValue();
                }
                catch(NullPointerException ae)
                {
                    System.out.println("NullPointerException : GFG");
                }
                String odi = "";
                try
                {
                    odi = FileUtil.findODIWithDomainName(domainName);
                }
                catch(IOException ie)
                {
                    System.out.println("IOException : FileUtil.findODIWithDomainName() : " + ie.getMessage());
                }
                if ((odi == null)||(odi.trim().equals(""))) odi = "2.16.840.1.113883.19.99999";
                String[] res = null;
                if ((!userDefault.equals(""))||(!hl7Default.equals("")))
                {
                    if (hl7Default.equals("")) hl7Default = userDefault;
                    res = getVocabularyDomainCode(domainName, hl7Default);
                }
                else res = getVocabularyDomainCode(domainName);

                if (res != null)
                {
                    codeItems = new String[] {domainName, odi, res[1], res[0]};
                    isCode = true;
                    displayName = res[1];
                    //return line + " => " + res[0];
                }
                else
                {
                    if ((domainName == null)||(domainName.trim().equals("")))
                    {
                        if (line.endsWith("value.code"))
                        {
                            codeItems = new String[] {"ICD10", "2.16.840.1.113883.6.3", "Chronic gastric ulcer without hemorrhage or perforation", "K25.7"};
                            isCode = true;
                        }
                        else
                        {
                            System.err.println("domain name finding failure. 1 : " +domainName);
                            codeItems = null;
                            isCode = false;
                        }
                    }
                    else
                    {
                        isCode = true;
                        codeItems = new String[] {domainName, odi, "Domain Not Found", "NotFound"};
                        //return line + " => " + "%%Not Found";
                    }
                }
            }
        }
        if(true)
        {
            //codeItems = null;
            String dtTag = "&DataType:";
            for (int i=0;i<changeList.size();i++)
            {
                String lin = changeList.get(i).trim();
                if (lin.startsWith("#")) continue;
                String dt = "";

                int idx = lin.indexOf("=>");
                if (idx <= 0) continue;
                int idxDT = lin.indexOf(dtTag);
                if (idxDT > 0)
                {
                    dt = lin.substring(idxDT + dtTag.length()).trim();
                    lin = lin.substring(0, idxDT);
                }
                String lin1 = lin.substring(0,idx).trim();
                String lin2 = lin.substring(idx + 2).trim();
                if (!dt.equals(""))
                {
                    String dtM = "";
                    if (!datatype.equals("")) dtM = datatype;
                    else
                    {
                        if (!datatypePN.equals("")) dtM = datatypePN;
                    }
                    if (!dtM.equals(""))
                    {
                        if (!dtM.equals(dt)) continue;
                        else
                        {
                            if (line.endsWith(lin1)) return changeLineExe(line, lin2, dt);
                        }
                    }
                }
                else
                {
                    if (line.endsWith(lin1)) return changeLineExe(line, lin2);
                }
            }
        }
        return "";
    }

    private String changeLineExe(String line, String lin)
    {
        return changeLineExe(line, lin, null);
    }
    private String changeLineExe(String line, String lin, String datatype)
    {

        int idx = lin.indexOf("%");
        if (idx < 0) return line + " => " + lin;

        boolean cTag = false;
        String block = "";
        String newLine = "";
        for (int i=0;i<lin.length();i++)
        {
            String achar = lin.substring(i, i+1);
            if (cTag)
            {
                if (achar.equals("%"))
                {
                    cTag = false;
                    String res = changeData(block, datatype);
                    if (res == null) return "";
                    newLine = newLine + res;
                    block = "";
                }
                else block = block + achar;
            }
            else
            {
                if (achar.equals("%"))
                {
                    cTag = true;
                }
                else newLine = newLine + achar;
            }
        }
        if (cTag)
        {
            System.err.println("Change source Data invalid : " + line + " : " + lin);
        }
        return line + " => " + newLine;
    }

    private String changeData(String block, String datatype)
    {
        if (datatype == null) datatype = "";
        if (datatype.equals("PN"))
        {
            if (block.equals("PERSON_FAMILY_NAME_LIST"))
            {
                return "Anderson";
            }
            else if (block.startsWith("PERSON_GIVEN_NAME_LIST"))
            {
                return "Terry";
            }
            else if (block.startsWith("NULL"))
            {
                return null;
            }
        }
        if (datatype.equals("PQ"))
        {
            if (block.startsWith("NULL"))
            {
                return null;
            }
        }

        DateFunction dateF = new DateFunction();
        if (block.equals("RND"))
        {
            return "" + FileUtil.getRandomNumber(0, 10);
        }
        else if (block.startsWith("RND:"))
        {
            String num = block.substring(4);
            int in = 0;
            try
            {
                in = Integer.parseInt(num);
            }
            catch(NumberFormatException ne)
            {
                System.err.println("NumberFormatException (RND) : " + block);
                in = 2;
            }
            if (in < 0)
            {
                int inn = in * in;
                inn = (int) Math.sqrt(inn);
                int rnd = FileUtil.getRandomNumber(inn);
                return "" + FileUtil.getRandomNumber(inn + ((rnd % 3) - 1));
            }
            return "" + FileUtil.getRandomNumber(in);
        }
        else if (block.equals("ARND"))
        {
            int in = FileUtil.getRandomNumber(0, 26) + 65;

            byte bb = (byte) in;
            char cc = (char) bb;
            return "" + cc;
        }
        else if (block.startsWith("ARND:"))
        {
            String num = block.substring(5);
            int in = 0;
            try
            {
                in = Integer.parseInt(num);
            }
            catch(NumberFormatException ne)
            {
                System.err.println("NumberFormatException (ARND) : " + block);
                in = 2;
            }
            String str = "";
            for (int i=0;i<in;i++)
            {

                int inn = FileUtil.getRandomNumber(0, 26) + 65;

                byte bb = (byte) inn;
                char cc = (char) bb;
                str = str + cc;
            }
            return str;
        }
        else if (block.equals("TEXTLIST"))
        {
            //todo
            return "This is a sample text for test data generating...";
        }
        else if (block.equals("DATE"))
        {
            return dateF.getCurrentTime().substring(0,8);
        }
        else if (block.equals("TIME"))
        {
            return dateF.getCurrentTime().substring(8);
        }
        else if ((block.equals("DATE_LOW"))||(block.equals("DATE_HIGH")))
        {
            java.util.Date date = new java.util.Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long days = 10l;
            if (block.equals("DATE_HIGH")) days = 2l;
            long lng = cal.getTimeInMillis() - (1000l * 60l * 60l * 24l * days);
            cal.setTimeInMillis(lng);
            date = cal.getTime();
            return (new SimpleDateFormat(dateF.getDefaultDateFormatString())).format(date).substring(0,8);
        }
        else if (block.equals("ENTITY_NAME_LIST"))
        {
            //todo
            return "Time Def Ltd. Co.";
        }
        else if (block.equals("SUFFIX_LIST"))
        {
            //todo
            return "Jr.";
        }
        else if (block.equals("CITY_LIST"))
        {
            //todo
            return "Rockville";
        }
        else if (block.equals("STREET_LIST"))
        {
            //todo
            return "Durham St.";
        }
        else if (block.equals("STATE_LIST"))
        {
            //todo
            return "MD";
        }
            else if (isCode)
            {
                String ret = "";
                if (block.equals("CODE"))//(line.endsWith("codeSystemName"))
                {
                    if (codeItems != null)
                    {
                        ret = codeItems[3];
                        codeItems[3] = null;
                    }
                    else ret = "ERROR1";
                }
                else if (block.equals("CODE.SYSTEMNAME"))//(line.endsWith("codeSystemName"))
                {
                    if (codeItems != null)
                    {
                        ret = codeItems[0];
                        codeItems[0] = null;
                    }
                    else ret = "ERROR2";
                }
                else if (block.equals("CODE.SYSTEM"))//(line.endsWith("codeSystem"))
                {
                    if (codeItems != null)
                    {
                        ret = codeItems[1];
                        codeItems[1] = null;
                    }
                    else ret = "ERROR3";
                }
                else if (block.equals("CODE.DISPLAYNAME"))//(line.endsWith("displayName"))
                {
                    if (codeItems != null)
                    {
                        ret = codeItems[2];
                        codeItems[2] = null;
                    }
                    else ret = "ERROR4";
                }
                boolean cTag = false;
                if (codeItems == null) System.err.println("Null Code Items array: " + block);
                else
                {
                    for (int i=0;i<codeItems.length;i++) if (codeItems[i] != null) cTag = true;
                    if (!cTag)
                    {
                        isCode = false;
                        codeItems = null;
                    }
                }
                //if (ret.indexOf(",") >= 0) ret = "\"" + ret + "\"";
                return ret;
            }
        else
        {
            System.err.println("Invalid block : " + block);
            return "ERROR";
        }
    }
    private String replaceLine(String line, List<String> replaceList)
    {
        for (int i=0;i<replaceList.size();i++)
        {
            String lin = replaceList.get(i);
            int idx = lin.indexOf("::");
            if (idx <= 0) continue;
            String lin1 = lin.substring(0,idx).trim();
            String lin2 = lin.substring(idx + 2).trim();
            if (line.endsWith(lin1)) return line.replace(lin1, lin2);
        }
        return line;
    }
    private String[] getVocabularyDomainCode(String domainName)
    {
        return getVocabularyDomainCode(domainName, null);
    }
    private String[] getVocabularyDomainCode(String domainName, String findCode)
    {
        if (findCode == null) findCode = "";
        findCode = findCode.trim();
        if ((domainName == null)||(domainName.trim().equals(""))) return null;
        domainName = domainName.trim();
        CommonNode temp = h3sVocTree.getHeadSegment();
        MetaSegment post = null;
        List<String> codeList = new ArrayList<String>();
        List<String> displayList = new ArrayList<String>();
        boolean found = false;
        boolean success = false;
        while(temp!=null)
        {
            CommonAttribute attribute = temp.getAttributes();
            if (!found)
            {
                for (int i=0;i<attribute.getAttributeItems().size();i++)
                {
                    CommonAttributeItem item = attribute.getAttributeItems().get(i);
                    if (item.getItemValue().equals(domainName))
                    {
                        if (temp instanceof MetaSegment)
                        {
                            found = true;
                            success = true;
                            post = (MetaSegment)temp;
                            break;
                        }
                    }
                }
            }
            else
            {
                if (h3sVocTree.isAncestor(post, temp))
                {
                    if (temp.getName().equals("leafTerm"))
                    {
                        String code = "";
                        String display = "";
                        for (int i=0;i<attribute.getAttributeItems().size();i++)
                        {
                            CommonAttributeItem item = attribute.getAttributeItems().get(i);
                            if (item.getName().equals("Code"))
                            {
                                code = item.getItemValue();
                            }
                            if (item.getName().equals("printName"))
                            {
                                display = item.getItemValue();
                            }
                            if ((code.length() > 0)&&(display.length() > 0))
                            {
                                codeList.add(code);
                                displayList.add(display);
                            }
                        }
                    }
                }
                else
                {
                    found = false;
                    post = null;
                    continue;
                }
            }

            try
            {
                temp = h3sVocTree.nextTraverse(temp);
                 if (temp == h3sVocTree.getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return null;
            }
        }
        if (!success) return null;

        int idx = -1;
        if (findCode.equals(""))
        {
            if (codeList.size() == 0) return new String[] {"NoData", "Domain was found but no Data implemented"};
            if (codeList.size() == 1) return new String[] {codeList.get(0), displayList.get(0)};

            idx = FileUtil.getRandomNumber(0, codeList.size());
        }
        else
        {
            for (int i=0;i<codeList.size();i++)
            {
                if (findCode.equals(codeList.get(i).trim())) idx = i;
            }
        }
        if (idx < 0) return new String[] {findCode, "This default code cannot be found"};
        else
        {
            if (idx >= displayList.size()) return new String[] {codeList.get(idx), "No Display Name"};
            else return new String[] {codeList.get(idx), displayList.get(idx)};
        }
    }


        private boolean buildVocabularyTree()
        {
            String VocFileName = "C:\\projects\\javasig\\data\\vocab.xml";
            boolean res = false;
            H3SVocabTreeBuildEventHandler handler = null;
            try
            {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();

                XMLReader producer = parser.getXMLReader();
                handler = new H3SVocabTreeBuildEventHandler();

                producer.setContentHandler(handler);

                //producer.parse(new InputSource("file:///" + h3sFileName.replaceAll("\\", "/")));
                producer.parse(new InputSource("file:///" + VocFileName));
                res = true;
            }
            catch(IOException e)
            {
                System.out.println("H3SVocabTreeBuildEventHandler IOException 1 : " + e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println("H3SVocabTreeBuildEventHandler IOException 2 : " + e.getMessage());

                e.printStackTrace(System.err);
            }
            if(res)
            {
                try
                {
                    h3sVocTree = new MetaTreeMetaImpl(handler.getHeadSegment());
                    return true;
                }
                catch(ApplicationException ae)
                {
                    System.out.println("H3SVocabTreeBuildEventHandler ApplicationException : " + ae.getMessage());
                }
            }
            return false;
        }

    public void build(File h3sFile) throws ApplicationException
    {
        if (h3sFile == null) throw new ApplicationException("H3S File is null.");

        String filePath = h3sFile.getAbsolutePath();
        List<String> fileLines = null;
        try
        {
            fileLines = FileUtil.readFileIntoList(filePath);
        }
        catch(IOException ie)
        {
            throw new ApplicationException("H3S File IOException : " + ie.getMessage());
        }

        if (fileLines.size() == 0) throw new ApplicationException("No String Line in H3S File : " + filePath);

        String line = "";
        int n = 0;

        while(line.equals(""))
        {
            line = fileLines.get(n).trim();
            n++;
            if (n >= fileLines.size()) break;
        }

        if (line.equals("")) throw new ApplicationException("No Content in H3S File : " + filePath);

        if (line.startsWith("<"))
        {
            buildWithXMLFormat(h3sFile);
            inputDataType = INPUT_DATA_TYPE_XML;
        }
        else
        {
            transformH3SFromBinaryToXML(h3sFile);
            this.setNodeIdentifierType(NodeIdentifierType.XPATH);
            inputDataType = INPUT_DATA_TYPE_BINARY;
        }
    }

    private void transformH3SFromBinaryToXML(File h3sFile) throws ApplicationException
    {
        ValidatorResults validatorResults = new ValidatorResults();
        DefaultMutableTreeNode root = null;
        try
        {
        	NewHSMBasicNodeLoader newHsmNodeLoader=new NewHSMBasicNodeLoader(true);
        	root = (DefaultMutableTreeNode) newHsmNodeLoader.loadData(h3sFile);
        	//initializeTreeWithMIFTreeNode(root);
        }
        catch (Throwable e1)
        {
			throw new ApplicationException("Tree build Failure with Binary file : " + e1.getMessage());
		}

        //MIFClass mif = (MIFClass) root.getUserObject();
        //this.setHeadSegment((H3SInstanceMetaSegment)convertTree(tempHead, mif, 0));

        //String h3sFileName = h3sFile.getAbsolutePath();
        //String newH3SFileName = h3sFileName.substring(0, (h3sFileName.length()-4)) + "_XML_Transformed" + h3sFileName.substring(h3sFileName.length()-4);


        H3SInstanceMetaSegment secondHead = convertTree(null, root, 0);
        H3SInstanceMetaSegment firstHead = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE, "head");
        secondHead.setParent(firstHead);

        try
        {
            //CommonNode node = (CommonNode) secondHead;
            firstHead.addChildNode(secondHead);
        }
        catch(ApplicationException ae)
        {
            ae.printStackTrace();
            throw ae;
        }
        this.setHeadSegment(firstHead);//, fw));

        return;
    }

    private H3SInstanceMetaSegment convertTree(H3SInstanceMetaSegment parent, DefaultMutableTreeNode node, int depth/*, FileWriter fw */) throws ApplicationException
    {

        String spaces = "";
        for (int i=0;i<depth;i++) spaces = spaces + "    ";
        MIFClass mif = null;
        MIFAttribute att = null;
        MIFAssociation asso = null;
        Attribute attT = null;
        String nodeName = "";
        String tag = "";

        H3SInstanceMetaSegment tempPar = null;
        try
        {
            mif = (MIFClass) node.getUserObject();
            tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE);
            tempPar.setName(mif.getName());

            addAttributeItem(tempPar, "reference-name", mif.getReferenceName());
            addAttributeItem(tempPar, "sortKey", mif.getSortKey());
            nodeName = mif.getName();
            tag = "C:";
//            writeFileWriter(fw, "<clone clonename=\""+mif.getReferenceName()+"\" sortKey=\""+mif.getSortKey()+"\" cardinality=\"1..1\" uuid=\"66bbf3b3-5ddf-4e50-92b7-af9122b58899\">");
        }
        catch(ClassCastException ce)
        {
            try
            {
                att = (MIFAttribute) node.getUserObject();

                nodeName = att.getName();
                tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.ATTRIBUTE);
                tempPar.setName(att.getName());

                addAttributeItem(tempPar, "codingStrength", att.getCodingStrength());
                addAttributeItem(tempPar, "domainName", att.getDomainName());
                if (att.getDatatype() != null)
                {
                    addAttributeItem(tempPar, "datatype", att.getDatatype().getName());
                    System.out.println("CVVV DataType : " + att.getDatatype().getName());
                }
                addAttributeItem(tempPar, "conformance", att.getConformance());
                addAttributeItem(tempPar, "user-default", att.getDefaultValue());
                addAttributeItem(tempPar, "hl7-default", att.getFixedValue());
                addAttributeItem(tempPar, "sortKey", att.getSortKey());
                addAttributeItemCardinality(tempPar, att.getMinimumMultiplicity(), att.getMaximumMultiplicity());

                tag = " A:";
            }
            catch(ClassCastException cee)
            {
                try
                {
                    attT = (Attribute) node.getUserObject();
                    MetaField field = new MetaFieldImpl();
                    field.setName(attT.getName());

                    addAttributeItem(field, "user-default", attT.getDefaultValue());
                    addAttributeItemCardinality(field, attT.getMin(), attT.getMax());


                    field.setParent(parent);
                    parent.addChildNode(field);
                    field.setXPath(getSimpleXPath(field.generateXPath(".")));
                    nodeName = attT.getName();
                    tag = "  d:";
                }
                catch(ClassCastException ceee)
                {
                    asso = (MIFAssociation) node.getUserObject();

                    nodeName = asso.getName();
                    tag = "B:";
                    tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE);
                    tempPar.setName(asso.getName());

                    addAttributeItem(tempPar, "conformance", asso.getConformance());
                    addAttributeItem(tempPar, "sortKey", asso.getSortKey());
                    addAttributeItemCardinality(tempPar, asso.getMinimumMultiplicity(), asso.getMaximumMultiplicity());

                }
            }
        }
        if (headerName == null) headerName = nodeName;
        System.out.println("MIF ==> " + spaces + tag + nodeName);
        if (tempPar == null) return null;
        if (parent != null)
        {
            tempPar.setParent(parent);
            parent.addChildNode(tempPar);
            tempPar.setXPath(getSimpleXPath(tempPar.generateXPath(".")));

        }
        for (int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
            convertTree(tempPar, child, (depth+1));
        }


        return tempPar;
    }
    private String getSimpleXPath(String xpath)
    {
        xpath = xpath.trim();
        int idx = xpath.indexOf(headerName);
        if (idx < 0)
        {
            System.err.println("CVVV : invalid xpath" + xpath);
            return xpath;
        }
        xpath = xpath.substring(idx);
        if (xpath.endsWith(".")) xpath = xpath.substring(0, xpath.length()-1);
        return xpath;
    }
    //private H3SInstanceMetaSegment addAttributeItemCardinality(H3SInstanceMetaSegment tempPar, int min, int max)
    private CommonNode addAttributeItemCardinality(CommonNode tempPar, int min, int max)
        {
        String sMin = "";
        String sMax = "";
        if (min < 0) return null;
        if (max < 1) return null;
        if (min > 1) return null;
        if (min > max) return null;
        if ((min == 0)&&(max == 0)) return null;
        sMin = "" + min;
        if (max > 1) sMax = "*";
        else sMax = "" + max;

        try
        {
            CommonAttributeItem item = new CommonAttributeItemImpl(tempPar.getAttributes());
            item.setName("cardinality");
            item.setItemValue(sMin + ".." + sMax);
            tempPar.addAttributeItem(item);
        }
        catch(ApplicationException ae)
        {
            return null;
        }
        return tempPar;
    }
    //private H3SInstanceMetaSegment addAttributeItem(H3SInstanceMetaSegment tempPar, String attName, String attValue)
    private CommonNode addAttributeItem(CommonNode tempPar, String attName, String attValue)
        {
        if ((attValue == null)||(attValue.trim().equals(""))) return null;

        try
        {
            CommonAttributeItem item = new CommonAttributeItemImpl(tempPar.getAttributes());
            item.setName(attName);
            item.setItemValue(attValue);
            tempPar.addAttributeItem(item);
        }
        catch(ApplicationException ae)
        {
            return null;
        }
        return tempPar;
    }

//    private CommonNode convertTree(H3SInstanceMetaSegment meta, MIFClass mif, int depth) throws ApplicationException
//    {
//        //MIFClass mif = (MIFClass) node.getUserObject();
//        String spaces = "";
//        for (int i=0;i<depth;i++) spaces = spaces + "    ";
//        System.out.println("MIF ==> " + spaces + "C:" + mif.getName());
//        String nodeName = mif.getName();
//        meta.setName(nodeName);
//        HashSet<MIFAttribute> atts = mif.getAttributes();
//        if (!atts.isEmpty())
//        {
//            Iterator<MIFAttribute> iterA = atts.iterator();
//            while(iterA.hasNext())
//            {
//                MIFAttribute att = iterA.next();
//
//                System.out.println("MIF ==> " + spaces + "  A:" + att.getName());
//            }
//        }
//
//        HashSet<MIFAssociation> assos = mif.getAssociations();
//        if (!assos.isEmpty())
//        {
//            Iterator<MIFAssociation> iter = assos.iterator();
//            while(iter.hasNext())
//            {
//                MIFAssociation asso = iter.next();
//                MIFClass mifClass = asso.getMifClass();
//                H3SInstanceMetaSegment temp = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE);
//                convertTree(temp, mifClass, depth++);
//            }
//        }
//        return meta;
//    }
    private void buildWithXMLFormat(File h3sFile) throws ApplicationException
    {
        String h3sFileName = h3sFile.getAbsolutePath();
        boolean res = false;
        H3SBuildEventHandler handler = null;
        try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLReader producer = parser.getXMLReader();
			handler = new H3SBuildEventHandler();

			producer.setContentHandler(handler);

			//producer.parse(new InputSource("file:///" + h3sFileName.replaceAll("\\", "/")));
            producer.parse(new InputSource("file:///" + h3sFileName));
            res = true;
        }
		catch(IOException e)
		{
			//System.out.println("H3Stree IOException : " + e.getMessage());
            throw new ApplicationException("H3Stree build IOException : " + e.getMessage());
        }
		catch(Exception e)
		{
			//e.printStackTrace(System.err);
            throw new ApplicationException("H3Stree build Exception : " + e.getMessage());
        }

        this.setHeadSegment(handler.getHeadSegment());
    }

    public static void main(String[] args)
    {
        //String fileName = "C:\\projects\\NewInstance\\040011_big\\040011.h3s";
        //String fileName = "C:\\projects\\NewInstance\\150000\\150000.h3s";
        //String fileName = "C:\\projects\\NewInstance\\150000\\gen\\040011.h3s";
        //String fileName = "T:\\YeWu\\xmlpathSpec\\newCOCT_MT150003.h3s";
        String fileName = "C:\\projects\\caadapter\\workingspace\\NewEncounter\\NewEncounter.h3s";
        new H3SInstanceMetaTree(fileName);
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
