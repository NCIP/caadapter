/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.impl.CommonAttributeItemImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaFieldImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaTreeMetaImpl;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.vocabulary.VocabularyGeneralUtilities;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.H3SInstanceSegmentType;
import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.MIFObjectClassType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.23 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/H3SInstanceMetaTree.java,v 1.23 2009-10-29 21:36:30 altturbo Exp $";

    boolean isCode = false;

    String codeValue = null;
    String codeSystem = null;
    String codeSystemName =  null;
    String codeDisplayName = null;
    String codeFieldName = null;
    int codeItems = 0;
    MetaTreeMeta h3sVocTree = null;
    String displayName = "";
    H3SInstanceMetaSegment currentCodeFieldParent = null;

    String header = "";
    String headerName = null;
    String dataFileName;
    String dataPath;
    String currentDataType;
    boolean success = false;

    private static int INPUT_DATA_TYPE_XML = 0;
    private static int INPUT_DATA_TYPE_BINARY = 1;

    int inputDataType = -1;

    public H3SInstanceMetaTree(String h3sFileName) throws ApplicationException
    {
        super();
        mainProcess(h3sFileName,null, null, null);
    }
    public H3SInstanceMetaTree(String h3sFileName, String replaceFileName, String changeFileName) throws ApplicationException
    {
        super();
        mainProcess(h3sFileName, null, replaceFileName, changeFileName);
    }

    public H3SInstanceMetaTree(String h3sFileName, String datFileName, String replaceFileName, String changeFileName) throws ApplicationException
    {
        super();
        mainProcess(h3sFileName, datFileName, replaceFileName, changeFileName);
    }

    private void mainProcess(String h3sFileName, String datFileName, String replaceFileName, String changeFileName) throws ApplicationException
    {

        success = false;
        //System.out.println("CCCV : V2 Meta Directory : " + FileUtil.getV2DataDirPath());
        if ((h3sFileName == null)||(h3sFileName.trim().equals("")))
        {
//            System.out.println("null h3s filename");
//            return;
            throw new ApplicationException("null h3s filename");
        }
        h3sFileName = h3sFileName.trim();
        String h3sExt = Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION;
        if (!h3sFileName.toLowerCase().endsWith(h3sExt))
        {
//            System.out.println("File type is mismatch (.h3s) : " +  h3sFileName);
//            return;
            throw new ApplicationException("File type is mismatch ("+h3sExt+") : " +  h3sFileName);
        }

        File h3sFile = new File(h3sFileName);

        try
        {
            build(h3sFile);
        }
        catch(ApplicationException ae)
        {
            ae.printStackTrace();
            throw ae;
//            System.out.println("Build H3s Tree Error : " + ae.getMessage());
//            return;
        }

        dataPath = h3sFileName.substring(0, h3sFileName.length()-4);
        dataFileName = dataPath + ".dat";

        if ((datFileName == null)||(datFileName.trim().equals("")))
        {
            generateDATFile(replaceFileName, changeFileName);
        }
        else
        {
            dataFileName = datFileName;
            List<String> tempList = null;
            try
            {
                tempList = FileUtil.readFileIntoList(dataFileName);
            }
            catch(IOException ie)
            {
                throw new ApplicationException("Data File is invalid("+dataFileName+") : " + ie.getMessage());
            }
            for(String str:tempList)
            {
                if (str == null) continue;
                if (str.trim().equals("")) continue;
                str = str.trim();
                if (str.startsWith("#")) continue;
                int idx = str.indexOf("=>");
                if (idx <= 0) throw new ApplicationException("Data File is invalid. 1 ("+dataFileName+")");
                str = str.substring(0, idx);
                idx = str.indexOf(".");
                if (idx <= 0) throw new ApplicationException("Data File is invalid. 2 ("+dataFileName+")");
                header = str.substring(0, idx);
                break;
            }
        }


        GenerateMapFileFromDataFile generate = null;
        if (inputDataType == INPUT_DATA_TYPE_XML)
           generate = new GenerateMapFileFromDataFile(dataFileName, header, dataPath, h3sFileName);
        else generate = new GenerateMapFileFromDataFile(dataFileName, header, dataPath, h3sFileName, this.getHeadSegment());

        if (generate.getSuccess()) System.out.println("V3 meta instance has been successfully generated! : " + h3sFileName);
        else
        {
            throw new ApplicationException("Test Instance generating failure... : " + generate.getMessage());
            //return;
        }
        success = true;
    }

    private void generateDATFile(String replaceFileName, String changeFileName) throws ApplicationException
    {
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(dataFileName);
        }
        catch(IOException ie)
        {
            throw new ApplicationException("data file opening error for writing : " + ie.getMessage());
            //return;
        }

        ValidatorResults result = validateTree(false);
        System.out.println("SSS...\n" + result.toString() + "\nEND...");

        displayTreeWithText();
        System.out.println("");

        if (VocabularyGeneralUtilities.getV3VocabularySeeker() == null)
        {
            //h3sVocTree.displayTreeWithText();
            throw new ApplicationException("Vocabulary Tree buliding failure....");
            //return;
        }

        List<String> changeList = null;
        try
        {
            if ((changeFileName == null)||(changeFileName.trim().equals("")))
            {
                changeFileName = "instanceGen/changeList.txt";

                ClassLoaderUtil loaderUtil = null;
                File f = new File("./lib");
                if ((f.exists())&&(f.isDirectory()))
                {
                    File[] files = f.listFiles();
                    for(File ff:files)
                    {
                        if (!ff.isFile()) continue;
                        String ffN = ff.getAbsolutePath();
                        if (!ffN.toLowerCase().endsWith(".zip")) continue;
                        try
                        {
                            loaderUtil = new ClassLoaderUtil(changeFileName, ffN, true);
                        }
                        catch(IOException ie)
                        {
                            loaderUtil = null;
                        }
                        if (loaderUtil != null) break;
                    }
                }

                if (loaderUtil == null)
                {
                    try
                    {
                        loaderUtil = new ClassLoaderUtil(changeFileName);
                    }
                    catch(IOException ie)
                    {
                        loaderUtil = null;
                    }
                }
                List<String> list = null;

                if (loaderUtil != null) list = loaderUtil.getFileNames();
                if ((list == null)||(list.size() == 0))
                {
                    throw new ApplicationException("Not found class loader : changeList.txt");
                    //return;
                }
                changeList = FileUtil.readFileIntoList(list.get(0));
            }
            else changeList = FileUtil.readFileIntoList(changeFileName.trim());
        }
        catch(IOException ie)
        {
            throw new ApplicationException("IOException :changeList : " + ie.getMessage());
            //return;
        }
        if (!checkChangeReplaceList(changeList, "=>")) throw new ApplicationException("This change file is invalid : " + changeFileName);

        List<String> replaceList = null;
        try
        {
            if ((replaceFileName == null)||(replaceFileName.trim().equals("")))
            {
                replaceFileName = "instanceGen/replaceList.txt";

                ClassLoaderUtil loaderUtil = null;
                File f = new File("./lib");
                if ((f.exists())&&(f.isDirectory()))
                {
                    File[] files = f.listFiles();
                    for(File ff:files)
                    {
                        if (!ff.isFile()) continue;
                        String ffN = ff.getAbsolutePath();
                        if (!ffN.toLowerCase().endsWith(".zip")) continue;
                        try
                        {
                            loaderUtil = new ClassLoaderUtil(replaceFileName, ffN, true);
                        }
                        catch(IOException ie)
                        {
                            loaderUtil = null;
                        }
                        if (loaderUtil != null) break;
                    }
                }

                if (loaderUtil == null)
                {
                    try
                    {
                        loaderUtil = new ClassLoaderUtil(replaceFileName);
                    }
                    catch(IOException ie)
                    {
                        loaderUtil = null;
                    }
                }
                List<String> list = null;

                if (loaderUtil != null)list = loaderUtil.getFileNames();
                if ((list == null)||(list.size() == 0))
                {
                    throw new ApplicationException("Not found class loader : replaceList.txt");
                    //return;
                }
                replaceList = FileUtil.readFileIntoList(list.get(0));
            }
            else replaceList = FileUtil.readFileIntoList(replaceFileName.trim());
        }
        catch(IOException ie)
        {
            throw new ApplicationException("IOException : replaceList : " + ie.getMessage());
            //return;
        }

        if (!checkChangeReplaceList(replaceList, "::")) throw new ApplicationException("This replace file is invalid : " + replaceFileName);

        //List<String> data = new ArrayList<String>();
        CommonNode temp = null;
        while(true)
        {
            if (temp == null) temp = this.getHeadSegment();
            else
            {
                try
                {
                    temp = this.nextTraverse(temp);
                }
                catch(ApplicationException ae)
                {
                    throw new ApplicationException("Travers Error : " + ae.getMessage());
                    //return;
                }
            }

            if (temp == null) break;

            boolean cTag = false;
            H3SInstanceMetaSegment segmentOri = null;
            if (temp instanceof H3SInstanceMetaSegment)
            {
                segmentOri = (H3SInstanceMetaSegment) temp;
                if (segmentOri.getChildNodes().size() == 0)
                {
                    cTag = true;
                }
                else segmentOri = null;
            }

            if ((temp instanceof MetaField)||(cTag))
            {
                MetaField field = null;

                if (cTag)
                {
                    H3SInstanceMetaSegment segment = (H3SInstanceMetaSegment) temp;
                    field = segment.createNewFieldInstance();
                    if (!(segment.getParent()).mutateSegmentToField(segment, field))
                    {
                        System.err.println("----- Segment mutate failure : " + segment.getName());
                        continue;
                    }
                    temp = field;
                }
                else field = (MetaField) temp;

                if (isUnnecessaryAttribute(field)) continue;

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
                    throw new ApplicationException("ERROR : numClone = 0 : " +  line);
                    //return;
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

                line = changeLine(line, changeList, field, segmentOri);


                /*
                if (line.toLowerCase().indexOf("code.originaltext.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("code00.originaltext.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("code01.originaltext.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("code02.originaltext.inlinetext => ") > 0) line = "";

                if (line.toLowerCase().indexOf("uantity.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("uantity00.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("uantity01.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("uantity02.translation.") > 0) line = "";

                if (line.toLowerCase().indexOf("code.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("code00.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("code01.translation.") > 0) line = "";
                if (line.toLowerCase().indexOf("code02.translation.") > 0) line = "";


                if (line.toLowerCase().indexOf("name.validtime.") > 0) line = "";
                if (line.toLowerCase().indexOf("name00.validtime.") > 0) line = "";
                if (line.toLowerCase().indexOf("name01.validtime.") > 0) line = "";
                if (line.toLowerCase().indexOf("name02.validtime.") > 0) line = "";

                if (line.toLowerCase().indexOf("addr.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("addr00.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("addr01.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("addr02.inlinetext => ") > 0) line = "";


                if (line.toLowerCase().indexOf("value.streetname.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("value01.streetname.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("value02.streetname.inlinetext => ") > 0) line = "";
                if (line.toLowerCase().indexOf("value03.streetname.inlinetext => ") > 0) line = "";
                */
                if (checkExceptionCaseOfLine(line, "value#.streetname.inlinetext => ")) line = "";
                if (checkExceptionCaseOfLine(line, "addr#.inlinetext => ")) line = "";
                if (checkExceptionCaseOfLine(line, "name#.validtime.")) line = "";
                if (checkExceptionCaseOfLine(line, "code#.translation.")) line = "";
                if (checkExceptionCaseOfLine(line, "quantity#.translation.")) line = "";
                if (checkExceptionCaseOfLine(line, "code#.originaltext.inlinetext => ")) line = "";

                if ((line!=null)&&(!line.trim().equals("")))
                {
                    System.out.println(line);

                    try
                    {
                        fw.write(line + "\r\n");
                    }
                    catch(IOException ie)
                    {
                        throw new ApplicationException("data file Writing error (2) : " + ie.getMessage());
                        //return;
                    }
                }
            }
        }

        try
        {
            fw.close();
        }
        catch(IOException ie)
        {
            throw new ApplicationException("data file closing error : " + ie.getMessage());
            //return;
        }
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
        String[] codeDataTypes = new String[] {"CD", "CE", "CS", "CV", "CO", "SC", "CNE", "CWE"};
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

    private String changeLine(String line, List<String> changeList, MetaField field, H3SInstanceMetaSegment seg)
    {
        //if (line.toLowerCase().indexOf("quantity") > 0) System.out.println(" GGGG FF1 : " + line + " : ");
        H3SInstanceMetaSegment parent = null;
        if (seg == null)
        {
            if (field == null)
            {
                System.err.println("This node is neither segment nor field. : " + line);
                return "";
            }
            String vr = getAttributeItemValue(field.getAttributes(), "user-default");
            if ((vr != null)&&(!vr.trim().equals("")))
            {
                if ((line.toLowerCase().endsWith("quantity.unit"))&&(vr.trim().equals("1"))) {}
                else return "";
            }

            String en = getAttributeItemValue(field.getAttributes(), "isEnabled");
            if ((en.trim().equals("false"))) return "";

            parent = (H3SInstanceMetaSegment) field.getParent();
        }
        else  parent = seg;

        String en = getAttributeItemValue(parent.getAttributes(), "isEnabled");
        if ((en.trim().equals("false"))) return "";

        String datatype = getAttributeItemValue(parent.getAttributes(), "concreteDatatype");
        if ((datatype == null)||(datatype.trim().equals(""))) datatype = getAttributeItemValue(parent.getAttributes(), "datatype");
        if (datatype == null) datatype = "";
        datatype = datatype.trim();

        String datatypePN = null;
        if (parent.getParent() != null)
        {
            datatypePN = getAttributeItemValue(parent.getParent().getAttributes(), "concreteDatatype");
            if ((datatypePN == null)||(datatypePN.trim().equals(""))) datatypePN = getAttributeItemValue(parent.getParent().getAttributes(), "datatype");
        }
        if (datatypePN == null) datatypePN = "";
        datatypePN = datatypePN.trim();

        String datatypeF = getAttributeItemValue(field.getAttributes(), "datatype");
        if (datatypeF == null) datatypeF = "";
        datatypeF = datatypeF.trim();

        String codingStrength = getAttributeItemValue(parent.getAttributes(), "codingStrength");
        //String datatypePN = getAttributeItemValue(parent.getParent().getAttributes(), "datatype");
        String hl7Default = getAttributeItemValue(parent.getAttributes(), "hl7-default");
        String hl7Fixed = getAttributeItemValue(parent.getAttributes(), "hl7-fixed");
        String userDefault = getAttributeItemValue(parent.getAttributes(), "user-default");
        currentDataType = datatypePN;
        //boolean existsDefaultValue = false;
        if (((hl7Default != null)&&(!hl7Default.trim().equals("")))||
            ((hl7Fixed != null)&&(!hl7Fixed.trim().equals("")))||
            ((userDefault != null)&&(!userDefault.trim().equals(""))))
        {
            //System.out.println("PPPPP 999 : " + seg  +  " : " +field + " : " + " : " +  hl7Default + " : " + userDefault + " : " + hl7Fixed);
            return "";//existsDefaultValue = true;
        }
        else
        {
//            if (line.endsWith("statusCode"))
//            {
//                return line + ".noneX => normal";
//            }
//            if (line.endsWith("determinerCode"))
//            {
//                return line + ".noneX => INSTANCE";
//            }
//            if (line.endsWith("moodCode"))
//            {
//                return line + ".noneX => EVN";
//            }

            if ((line.endsWith("addr"))||(line.endsWith("addr00"))||(line.endsWith("addr01")))
            {
                return line + ".noneX => 1634 Helperton St., Uniline, MD 20919";
            }
        }

        if ((line.endsWith("code"))&&(!((isCodeDataType(datatype))||(isCodeDataType(codingStrength)))))
            System.err.println("CCCXX9 : Unmatched code : " + line);
        if ((isCodeDataType(datatype))||(isCodeDataType(codingStrength)))//&&(currentCodeFieldParent != parent))
        {
            currentCodeFieldParent = parent;
            if (codeFieldName != null)
            {
                if (!codeFieldName.equals(parent.getName()))
                {
                    if (codeItems > 0) System.err.println("CCCXX9 : Not four code item : " + codeFieldName);
                    codeItems = 0;
                    codeValue = null;
                    codeSystem = null;
                    codeSystemName = null;
                    codeDisplayName = null;
                    codeFieldName = parent.getName();
                    isCode = false;
                }
            }
            else codeFieldName = parent.getName();

            if ((!isCode)&&(codeItems == 0)) //(line.endsWith("code"))
            {
                if ((codeValue != null)||(codeSystem != null)||(codeSystemName != null)||(codeDisplayName != null))
                {
                    System.err.println("Code items array is not delete before : " + codeFieldName + " (" + codeValue + " : " + codeSystem + " : " + codeSystemName + " : " + codeDisplayName +  ") ");
                    codeValue = null;
                    codeSystem = null;
                    codeSystemName = null;
                    codeDisplayName = null;
                    isCode = false;
                }
                codeFieldName = parent.getName();
                String domainName = "";
                try
                {
                    domainName = parent.getAttributes().getAttributeItem("domainName").getItemValue();
                }
                catch(NullPointerException ae)
                {
                    System.out.println("Not found Domain Attribute : " + codeFieldName);
                }
                //if (domainName.equals("")) domainName = "No_domain_Name";
                String odi = "";
                try
                {
                    odi = FileUtil.findODIWithDomainName(domainName);
                }
                catch(IOException ie)
                {
                    System.err.println("IOException : FileUtil.findODIWithDomainName() : " + ie.getMessage());
                }
                if ((odi == null)||(odi.trim().equals(""))) odi = "2.16.840.1.113883.19.99999";
                String[] res = null;
                if ((!userDefault.equals(""))||(!hl7Default.equals("")))
                {
                    if (hl7Default.equals("")) hl7Default = userDefault;
                    //res = getVocabularyDomainCode(domainName, hl7Default);
                    res = VocabularyGeneralUtilities.getV3VocabularySeeker().getVocabularyDomainCodes(domainName, hl7Default);
                }
                else
                {
                    //res = getVocabularyDomainCode(domainName);
                    res = VocabularyGeneralUtilities.getV3VocabularySeeker().getVocabularyDomainCodes(domainName);
                }

                if (res != null)
                {
                    codeSystemName = domainName;
                    codeSystem = odi;
                    codeValue = res[0];

                    isCode = true;
                    //System.out.println("CVVV7 : is code true : " + codeItems + " : " + parent.getName() + " : " + field.getName());
                    codeDisplayName = res[1];
                    displayName = codeDisplayName;
                    //return line + " => " + res[0];
                }
                else
                {
                    if ((domainName == null)||(domainName.trim().equals("")))
                    {
                        if ((line.toLowerCase().endsWith("value.code"))||
                            (line.toLowerCase().endsWith("value.codesystem"))||
                            (line.toLowerCase().endsWith("value.codesystemname"))||
                            (line.toLowerCase().endsWith("value.displayname")))
                        {
                            codeSystemName = "ICD10";
                            codeSystem = "2.16.840.1.113883.6.3";
                            codeValue = "K25.7";
                            codeDisplayName = "Chronic gastric ulcer without hemorrhage or perforation";
                            displayName = codeDisplayName;
                            isCode = true;
                            //System.out.println("CVVV8 : is code true : " + codeItems + " : " + parent.getName() + " : " + field.getName());

                        }
                        else
                        {
                            //System.err.println("domain name finding failure. 1 : " +domainName);
                            codeSystemName = null;
                            codeSystem = null;
                            codeValue = null;
                            codeDisplayName = null;
                            displayName = null;

                            isCode = false;
                            codeItems = 0;
                        }
                    }
                    else
                    {
                        System.err.println("domain name finding failure. 1 : " +domainName);
                        codeSystemName = domainName;
                        codeSystem = odi;
                        codeValue = "NotFound";
                        codeDisplayName = "Domain Not Found";
                        displayName = codeDisplayName;

                        isCode = true;
                        //System.out.println("CVVV7 : is code true : " + codeItems + " : " + parent.getName() + " : " + field.getName());

                        //codeItems = new String[] {domainName, odi, "Domain Not Found", "NotFound"};
                        //return line + " => " + "%%Not Found";
                    }
                }
            }
        }
        else
        {
            if (isCode)
            {
                System.err.println("Un-natural Code setting : " + codeFieldName + " : " + field.getName() + " : " + parent.getName() + " : " + datatype);
                isCode = false;
                codeItems = 0;
            }
        }

        if (((hl7Default != null)&&(!hl7Default.trim().equals("")))||
            ((userDefault != null)&&(!userDefault.trim().equals(""))))
        {
            //if (!isCode) return "";
            return "";
        }
        else
        {
            boolean gTag = false;
            String vl = ".noneX";
            if (line.endsWith(".statusCode")) gTag = true;
            if (line.endsWith(".determinerCode")) gTag = true;
            if (line.endsWith(".moodCode")) gTag = true;
            if (line.endsWith(".typeCode")) gTag = true;
            if (line.endsWith(".classCode")) gTag = true;
            if (line.endsWith(".contextControlCode")) gTag = true;

            //if (gTag) System.out.println("PPPPPPP : " + seg + " : " + userDefault + " : " + hl7Default);

            if (line.endsWith(".statusCode.code"))
            {
                vl = "";
                gTag = true;
            }

            if ((seg != null)&&(gTag))
            {
                String val = codeValue;
                codeValue = null;
                codeSystem = null;
                codeSystemName = null;
                codeDisplayName = null;
                isCode = false;
                if (val == null) return "";
                else return line + vl + " => " + val;
            }
        }

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
                        else if (checkMatchString(line, lin1))
                        {
                            //System.out.println("CVVV : Is this matched?(1) : " + line + " : " + lin1 + " : " + lin2);
                            return changeLineExe(line, lin2, dt);
                        }
                    }
                }
            }
            else
            {
                if (line.endsWith(lin1)) return changeLineExe(line, lin2);
                else if (checkMatchString(line, lin1))
                {
                    //System.out.println("CVVV : Is this matched?(2) : " + line + " : " + lin1 + " : " + lin2);
                    return changeLineExe(line, lin2);
                }
            }
        }

        return doMoreAboutValue(line, datatypeF, datatype, datatypePN, field.getName());
    }
    private String doMoreAboutValue(String line, String fieldDataType, String parentDataType, String grandParentDataType, String fieldName)
    {
        if (parentDataType == null) return "";

        if (grandParentDataType == null) grandParentDataType = "";
        else grandParentDataType = grandParentDataType.trim();
        if (!grandParentDataType.equals(""))
        {
            StringTokenizer st = new StringTokenizer(line, ".");
            List<String> list = new ArrayList<String>();
            while(st.hasMoreTokens())
            {
                String token = st.nextToken();
                if (token.trim().equals("")) continue;
                token = token.trim();
                list.add(token);
            }
            String newL = "";
            for(int i=0;i<(list.size());i++)
            {
                String li = list.get(i);
                if (i == (list.size()-1))
                {
                    if (li.equalsIgnoreCase("inlinetext")) li = "in";
                    if (li.equalsIgnoreCase("value")) li = "val";
                    newL = newL + "_" + li;
                }
                else newL = newL + "." + li;
            }
            line = newL;
        }

        if ((line.endsWith(".value"))||(line.endsWith("_val")))
        {
            if  ((parentDataType.equals("INT"))||(parentDataType.equals("PQ"))||(parentDataType.equals("REAL"))||
                 (parentDataType.equals("IVL_INT"))||(parentDataType.equals("IVL_PQ"))||(parentDataType.equals("IVL_REAL"))||
                 (parentDataType.equals("MO"))||(parentDataType.equals("IVL_MO"))) return line + " => " + FileUtil.getRandomNumber(3);
            if  ((parentDataType.equals("TS"))||(parentDataType.equals("IVL_TS"))) return line + " => " + (new DateFunction()).getCurrentTime();
        }
        if ((line.endsWith(".currency"))||(line.endsWith("_curr")))
        {
            if ((parentDataType.equals("MO"))||(parentDataType.equals("IVL_MO"))) return line + " => USD";
        }
        int idx = line.indexOf(".value");
        if (idx > 0)
        {
            if ((line.endsWith(".inlineText"))||(line.endsWith("_in")))
            {
                String rr = null;
                if (parentDataType.equals("AD")) rr = line + " => " + FileUtil.getRandomNumber(3) + " Doedoe Ave., Doe city, DE 32456";
                if ((parentDataType.equals("ED"))||(parentDataType.equals("ST"))||(parentDataType.equals("TX"))) rr = line + " => This is Sample text for test " + FileUtil.getRandomNumber(3);
                if  ((parentDataType.equals("ON"))||(parentDataType.equals("PN"))||(parentDataType.equals("EN"))||
                     (parentDataType.equals("TN"))) rr = line + " => John P. Doe Jr.";
                if (rr != null)
                {
                    System.out.println("TTTTTTTT : " + rr);
                    return rr;
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
        if (idx < 0)
        {
            if (isCode)
            {
                codeItems = 0;
                codeValue = null;
                codeSystem = null;
                codeSystemName = null;
                codeDisplayName = null;
                isCode = false;
            }
            return line + " => " + lin;
        }

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
    private boolean checkDataType(String datatype, String target)
    {
        if (target == null) return false;
        target = target.trim();
        if (target.equals("")) return false;

        List<String> list = new ArrayList<String>();

        if (datatype.indexOf(";") < 0) list.add(datatype);
        else
        {
            StringTokenizer st = new StringTokenizer(datatype, ";");
            while(st.hasMoreTokens())
            {
                String ss = st.nextToken();
                if (ss == null) ss = "";
                ss = ss.trim();
                if (!ss.equals("")) list.add(ss);
            }
        }
        if (list.size() == 0) return false;

        for (String dt:list)
        {
            if (dt.equals(target)) return true;
            if (target.endsWith("_" + dt)) return true;
        }
        return false;
    }
    private String changeData(String block, String datatype)
    {
        if (datatype == null) datatype = "";
        boolean checkDataType = checkDataType(datatype, currentDataType);
        if (!datatype.trim().equals(""))
        {
            if (!currentDataType.trim().equals(""))
            {
                if (!checkDataType) return null;
                else
                {
                    if (block.startsWith("NULL")) return null;
                }
            }
        }
        else
        {
            if (block.startsWith("NULL")) return null;
        }

        if (checkDataType(datatype, "PN"))
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
        if (checkDataType(datatype, "EN"))
        {
            if (block.startsWith("NULL"))
            {
                return null;
            }
        }

        if (checkDataType(datatype, "PQ"))
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
                codeItems++;
                if (codeValue != null)
                {
                    ret = codeValue;
                    codeValue = null;
                   // System.out.println("CVVV1 : Look code value ("+codeItems+") : " + ret + " : " + codeValue);
                }
                else ret = "ERROR1";
            }
            else if (block.equals("CODE.SYSTEMNAME"))//(line.endsWith("codeSystemName"))
            {
                codeItems++;
                if (codeSystemName != null)
                {
                    ret = codeSystemName;
                    codeSystemName = null;
                    //System.out.println("CVVV2 : Look codeSystemName ("+codeItems+") : " + ret + " : " + codeSystemName);

                }
                else ret = "ERROR2";
            }
            else if (block.equals("CODE.SYSTEM"))//(line.endsWith("codeSystem"))
            {
                codeItems++;
                if (codeSystem != null)
                {
                    ret = codeSystem;
                    codeSystem = null;
                    //System.out.println("CVVV3 : Look codeSystem ("+codeItems+") : " + ret + " : " + codeSystem);

                }
                else ret = "ERROR3";
            }
            else if (block.equals("CODE.DISPLAYNAME"))//(line.endsWith("displayName"))
            {
                codeItems++;
                if (codeDisplayName != null)
                {
                    ret = codeDisplayName;
                    codeDisplayName = null;
                    //System.out.println("CVVV4 : Look codeDisplayName ("+codeItems+") : " + ret + " : " + codeSystemName);

                }
                else ret = "ERROR4";
            }

            if (codeItems == 4)
            {
                isCode = false;
                codeItems = -1;
            }
            //System.out.println("CVVV0 : Look codeItems ("+codeItems+") : " + ret + " : " + codeSystemName + " : " + isCode);

            //if (ret.indexOf(",") >= 0) ret = "\"" + ret + "\"";
            return ret;
        }
        else
        {
            System.out.println(" GGGG : Invalid block : " + block + " : " + datatype);
            return "ERROR";
        }
    }
    private String[] stringTokenizerToArray(String line, String firstDelimiter, String secondDelimiter)
    {
        String[] res = new String[3];
        int idx1 = line.indexOf(firstDelimiter);
        if (idx1 < 0) return null;
        res[0] = line.substring(0, idx1);
        line = line.substring(idx1+1);
        int idx2 = line.indexOf(secondDelimiter);
        if (idx2 < 0) return null;
        res[1] = line.substring(0, idx2);
        res[2] = line.substring(idx2+1, line.length());
        if (res[2].trim().length() == 0) return null;
        return res;
    }
    private String[] checkStringAndNumberSuffix(String line)
    {
        if ((line == null)||(line.trim().equals(""))) return null;
        line = line.trim();
        if (line.length() < 3) return null;
        String[] res = new String[2];
        res[0] = line.substring(0, line.length()-2);
        res[1] = line.substring(line.length()-2);
        try
        {
            Integer.parseInt(res[0]);
            return null;
        }
        catch(NumberFormatException ne)
        { }
        int num = 0;
        try
        {
            num = Integer.parseInt(res[1]);
        }
        catch(NumberFormatException ne)
        {
            return null;
        }
        if (num < 0) return null;
        return res;
    }

    private boolean checkChangeReplaceList(List<String> list, String delimiter)
    {
        int count = 0;
        for (String str:list)
        {
            if (str == null) continue;
            if (str.trim().equals("")) continue;
            str = str.trim();
            if (str.startsWith("#")) continue;
            int idx = str.indexOf(delimiter);
            if (idx <= 0) continue;
            str = str.substring(0, idx);
            idx = str.indexOf(".");
            if (idx <= 0) continue;
            count++;
        }
        if (count > 10) return true;
        return false;
    }

    private String replaceLine(String line, List<String> replaceList)
    {
        String[] body = new String[] {"@@", "$$", "!!"};
        String[] source = null;
        String[] target = null;
        String[] divided = null;
        for (int i=0;i<replaceList.size();i++)
        {
            String lin = replaceList.get(i);
            int idx = lin.indexOf("::");
            if (idx <= 0) continue;
            String lin1 = lin.substring(0,idx).trim();
            String lin2 = lin.substring(idx + 2).trim();
            if (line.endsWith(lin1)) return line.replace(lin1, lin2);

            StringTokenizer st = new StringTokenizer(line, ".");

            while(st.hasMoreTokens())
            {
                String token = st.nextToken().trim();
                if (!token.equals(""))
                {
                    body[0] = body[1];
                    body[1] = body[2];
                    body[2] = token;
                }
            }

            source = stringTokenizerToArray(lin1, ".", ".");
            target = stringTokenizerToArray(lin2, ".", "_");
            if ((source == null)||(target == null)) continue;
            String res = "";
            int matchCount = -1;
            int index = 0;


            divided = checkStringAndNumberSuffix(body[index]);
            if (divided == null)
            {
                if ((body[index].equals(source[index]))||(body[index].endsWith(source[index])))
                {
                    res = res + target[index] + ".";
                    matchCount++;
                }
            }
            else
            {
                if ((divided[0].equals(source[index]))||(divided[0].endsWith(source[index])))
                {
                    res = res + target[index] + divided[1] + ".";
                    matchCount++;
                }
            }

            index++;
            divided = checkStringAndNumberSuffix(body[index]);
            if (divided == null)
            {
                if (body[index].equals(source[index]))
                {
                    res = res + target[index] + "_";
                    matchCount++;
                }
            }
            else
            {
                if (divided[0].equals(source[index]))
                {
                    res = res + target[index] + divided[1] + "_";
                    matchCount++;
                }
            }

            index++;
            boolean cTag = false;
            if (body[index].equals(source[index])) cTag = true;
            if ((body[index].equalsIgnoreCase("inlineText"))&&(target[index].equalsIgnoreCase("in"))) cTag = true;
            if ((body[index].startsWith(source[index]))&&(target[index].length() > 2)) cTag = true;

            if(cTag)
            {
                res = res + target[index];
                matchCount++;
            }
            //System.out.println("CVVV PPP("+index+") : "+ line + " : " + lin1 + " : " + lin2 + " : " + res);
            if (matchCount == index)
            {
                String lineR = line.replace(body[0]+"."+body[1]+"."+body[2], res);
                //System.out.println("CVVV QQQ : " +line + " : " + lineR + " : " + res);
                return lineR;
            }
        }

        return line;
    }
    private String[] getTailStringArray(String str)
    {
        int idx = str.indexOf(".");
        if (idx < 0) return null;
        str = str.trim();
        if (str.startsWith(".")) str = "*" + str;
        StringTokenizer st = new StringTokenizer(str, ".");
        String first = "";
        String second = "";
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (!token.trim().equals(""))
            {
                second = first;
                first = token;
            }
        }
        idx = first.indexOf("_");
        if (idx > 0)
        {
            return new String[] {second, first.substring(0, idx), first.substring(idx+1)};
        }
        else
        {
            return new String[] {second, first};
        }
    }
    private boolean checkMatchString(String line, String tail)
    {
        if (line.endsWith(tail)) return true;
        if (line.length() <= tail.length()) return false;

        String[] lineArr = getTailStringArray(line);
        String[] tailArr = getTailStringArray(tail);
        String[] divided = null;


        if ((lineArr == null)||(tailArr == null)) return false;
        if (lineArr.length != tailArr.length) return false;
        int index = 0;
        int matchCount = -1;
        if (tailArr[index].equals("*")) matchCount++;
        else
        {
            divided = checkStringAndNumberSuffix(lineArr[index]);
            if (divided == null)
            {
                if (lineArr[index].equals(tailArr[index])) matchCount++;
                else if (lineArr[index].toLowerCase().endsWith(tailArr[index].toLowerCase())) matchCount++;
            }
            else
            {
                if (divided[0].equals(tailArr[index])) matchCount++;
                else if (divided[0].toLowerCase().endsWith(tailArr[index].toLowerCase())) matchCount++;
            }
        }

        for (int i=0;i<(lineArr.length-1);i++)
        {
            index++;
            divided = checkStringAndNumberSuffix(lineArr[index]);
            if (divided == null)
            {
                if (lineArr[index].equals(tailArr[index])) matchCount++;
            }
            else
            {
                if (divided[0].equals(tailArr[index])) matchCount++;
            }
        }
        if ((index == matchCount)&&(index == (tailArr.length-1)))
        {
            //System.out.print("CVVV  QQQ : LineArr : ");
//            for (int i=0;i<(lineArr.length);i++)
//            {
//                System.out.print(", " + lineArr[i]);
//            }
//            System.out.println("");
//            System.out.print("CVVV : TailArr : ");
//            for (int i=0;i<(tailArr.length);i++)
//            {
//                System.out.print(", " + tailArr[i]);
//            }
//            System.out.println(" : " + divided);
            return true;
        }
        else return false;
    }
//    private String[] getVocabularyDomainCode(String domainName)
//    {
//        return getVocabularyDomainCode(domainName, null);
//    }
//    private String[] getVocabularyDomainCode(String domainName, String findCode)
//    {
//        if (findCode == null) findCode = "";
//        findCode = findCode.trim();
//        if ((domainName == null)||(domainName.trim().equals(""))) return null;
//        domainName = domainName.trim();
//        CommonNode temp = h3sVocTree.getHeadSegment();
//        MetaSegment post = null;
//        List<String> codeList = new ArrayList<String>();
//        List<String> displayList = new ArrayList<String>();
//        boolean found = false;
//        boolean success = false;
//        while(temp!=null)
//        {
//            CommonAttribute attribute = temp.getAttributes();
//            if (!found)
//            {
//                for (int i=0;i<attribute.getAttributeItems().size();i++)
//                {
//                    CommonAttributeItem item = attribute.getAttributeItems().get(i);
//                    if (item.getItemValue().equals(domainName))
//                    {
//                        if (temp instanceof MetaSegment)
//                        {
//                            found = true;
//                            success = true;
//                            post = (MetaSegment)temp;
//                            break;
//                        }
//                    }
//                }
//            }
//            else
//            {
//                if (h3sVocTree.isAncestor(post, temp))
//                {
//                    if (temp.getName().equals("leafTerm"))
//                    {
//                        String code = "";
//                        String display = "";
//                        for (int i=0;i<attribute.getAttributeItems().size();i++)
//                        {
//                            CommonAttributeItem item = attribute.getAttributeItems().get(i);
//                            if (item.getName().equals("Code"))
//                            {
//                                code = item.getItemValue();
//                            }
//                            if (item.getName().equals("printName"))
//                            {
//                                display = item.getItemValue();
//                            }
//                            if ((code.length() > 0)&&(display.length() > 0))
//                            {
//                                codeList.add(code);
//                                displayList.add(display);
//                            }
//                        }
//                    }
//                }
//                else
//                {
//                    found = false;
//                    post = null;
//                    continue;
//                }
//            }
//
//            try
//            {
//                temp = h3sVocTree.nextTraverse(temp);
//                 if (temp == h3sVocTree.getHeadSegment()) break;
//            }
//            catch(ApplicationException ae)
//            {
//                return null;
//            }
//        }
//        if (!success) return null;
//
//        int idx = -1;
//        if (findCode.equals(""))
//        {
//            if (codeList.size() == 0) return new String[] {"NoData", "Domain was found but no Data implemented"};
//            if (codeList.size() == 1) return new String[] {codeList.get(0), displayList.get(0)};
//
//            idx = FileUtil.getRandomNumber(0, codeList.size());
//        }
//        else
//        {
//            for (int i=0;i<codeList.size();i++)
//            {
//                if (findCode.equals(codeList.get(i).trim())) idx = i;
//            }
//        }
//        if (idx < 0) return new String[] {findCode, "This default code cannot be found"};
//        else
//        {
//            if (idx >= displayList.size()) return new String[] {codeList.get(idx), "No Display Name"};
//            else return new String[] {codeList.get(idx), displayList.get(idx)};
//        }
//    }


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

//        if (line.startsWith("<"))
//        {
//            buildWithXMLFormat(h3sFile);
//            inputDataType = INPUT_DATA_TYPE_XML;
//        }
//        else
//        {
            transformH3SFromBinaryToXML(h3sFile);
            this.setNodeIdentifierType(NodeIdentifierType.XPATH);
            inputDataType = INPUT_DATA_TYPE_BINARY;
//        }
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
        String xmlPath = "";
        String dtype = null;

        H3SInstanceMetaSegment tempPar = null;
        try
        {
            mif = (MIFClass) node.getUserObject();
            tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE);
            //tempPar.setName(mif.getName());
            xmlPath = mif.getXmlPath();
            if ((xmlPath == null)||(xmlPath.trim().equals("")))
            {
                System.out.println("   CVVV mif : xmlPath is null : " + mif.getName());
                xmlPath = mif.getName();
                //return null;
            }
            setNameAndCardinality(parent, tempPar, xmlPath, MIFObjectClassType.CLONE.toString(), 1, 1);

            //addAttributeItem(tempPar, "mif-type", MIFObjectClassType.CLONE.toString());

            addAttributeItem(tempPar, "type", "MIFClass");
            addAttributeItem(tempPar, "reference-name", mif.getReferenceName());
            addAttributeItem(tempPar, "title", mif.getTitle());
            addAttributeItem(tempPar, "sortKey", mif.getSortKey());

            if (mif.isEnabled()) addAttributeItem(tempPar, "isEnabled", "true");
            else addAttributeItem(tempPar, "isEnabled", "false");

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
                xmlPath = att.getXmlPath();
                if ((xmlPath == null)||(xmlPath.trim().equals("")))
                {
                    System.out.println("   CVVV att : xmlPath is null : " + att.getName());
                    xmlPath = att.getName();
                    //return null;
                }
                tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.ATTRIBUTE);

                setNameAndCardinality(parent, tempPar, xmlPath, MIFObjectClassType.ATTRIBUTE.toString(), att.getMinimumMultiplicity(), att.getMaximumMultiplicity());

                //tempPar.setName(att.getName());

                //addAttributeItem(tempPar, "mif-type", MIFObjectClassType.ATTRIBUTE.toString());
                addAttributeItem(tempPar, "type", "MIFAttribute");
                addAttributeItem(tempPar, "codingStrength", att.getCodingStrength());
                addAttributeItem(tempPar, "domainName", att.getDomainName());
                if (att.getDatatype() != null)
                {
                    dtype = att.getDatatype().getName();
                    addAttributeItem(tempPar, "datatype", dtype);
                    //System.out.println("CVVV DataType : " + att.getDatatype().getName());
                }
                if (att.getConcreteDatatype() != null)
                {
                    dtype = att.getConcreteDatatype().getName();
                    addAttributeItem(tempPar, "concreteDatatype", dtype);
                    //System.out.println("CVVV DataType : " + att.getDatatype().getName());
                }
                addAttributeItem(tempPar, "conformance", att.getConformance());
                addAttributeItem(tempPar, "user-default", att.getDefaultValue());
                addAttributeItem(tempPar, "hl7-fixed", att.getFixedValue());
                //System.out.println("PPPPP 000 " + att.getDefaultValue() + " : " + att.getFixedValue() + " : " + att.findHL7DefaultValueProperty());
                //addAttributeItem(tempPar, "user-default", att.get);
                addAttributeItem(tempPar, "hl7-default", att.findHL7DefaultValueProperty());
                addAttributeItem(tempPar, "sortKey", att.getSortKey());

                if (att.isEnabled()) addAttributeItem(tempPar, "isEnabled", "true");
                else addAttributeItem(tempPar, "isEnabled", "false");
                //addAttributeItemCardinality(tempPar, att.getMinimumMultiplicity(), att.getMaximumMultiplicity());

                tag = " A:";
            }
            catch(ClassCastException cee)
            {
                try
                {
                    attT = (Attribute) node.getUserObject();
                    xmlPath = attT.getXmlPath();
                    if ((xmlPath == null)||(xmlPath.trim().equals("")))
                    {
                        System.out.println("   CVVV attT : xmlPath is null : " + attT.getName());
                        xmlPath = attT.getName();
                        //return null;
                    }
                    nodeName = attT.getName();

                    Datatype dataT = attT.getReferenceDatatype();
                    if (dataT != null) dtype = dataT.getName();
                    else dtype = "";

                    if ((dtype == null)||(dtype.trim().equals(""))) dtype = attT.getType();
                    if (node.getChildCount() == 0)
                    {

                        MetaField field = new MetaFieldImpl();
                        //field.setName(attT.getName());

                        setNameAndCardinality(parent, field, xmlPath, MIFObjectClassType.DATAFIELD.toString(), attT.getMin(), attT.getMax());
                        addAttributeItem(field, "type", "attribute");
                        //addAttributeItem(tempPar, "mif-type", MIFObjectClassType.DATAFIELD.toString());
                        addAttributeItem(field, "user-default", attT.getDefaultValue());

                        if ((dtype != null)&&(!dtype.trim().equals(""))) addAttributeItem(field, "datatype", dtype);
                        //if (attT.)
                        //addAttributeItemCardinality(field, attT.getMin(), attT.getMax());
                        //if (!attT.getDefaultValue().equals("")) System.out.println("PPPPP 111 : " + attT.getDefaultValue());
                        if (attT.isEnabled()) addAttributeItem(field, "isEnabled", "true");
                        else addAttributeItem(field, "isEnabled", "false");

                        field.setParent(parent);
                        parent.addChildNode(field);
                        //field.setXPath(getSimpleXPath(field.generateXPath(".")));
                        field.setXPath(xmlPath);
                        nodeName = attT.getName();
                        tag = "  d:";
                    }
                    else
                    {
                        tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.ATTRIBUTE);
                        setNameAndCardinality(parent, tempPar, xmlPath, MIFObjectClassType.DATA_ATTRIBUTE.toString(), attT.getMin(), attT.getMax());
                        addAttributeItem(tempPar, "type", "Attribute");
                        addAttributeItem(tempPar, "user-default", attT.getDefaultValue());

                        if ((dtype != null)&&(!dtype.trim().equals(""))) addAttributeItem(tempPar, "datatype", dtype);

                        if (attT.isEnabled()) addAttributeItem(tempPar, "isEnabled", "true");
                        else addAttributeItem(tempPar, "isEnabled", "false");
                        //if (!attT.getDefaultValue().equals("")) System.out.println("PPPPP 112 : " + attT.getDefaultValue());
                        tag = "  D:";
                    }


                }
                catch(ClassCastException ceee)
                {
                    asso = (MIFAssociation) node.getUserObject();

                    nodeName = asso.getName();
                    tag = "B:";
                    xmlPath = asso.getXmlPath();
                    if ((xmlPath == null)||(xmlPath.trim().equals("")))
                    {
                        System.out.println("   CVVV asso : xmlPath is null : " + asso.getName());
                        xmlPath = asso.getName();
                        //return null;
                    }
                    tempPar = new H3SInstanceMetaSegment(H3SInstanceSegmentType.CLONE);

                    setNameAndCardinality(parent, tempPar, xmlPath, MIFObjectClassType.ASSOCIATION.toString(), asso.getMinimumMultiplicity(), asso.getMaximumMultiplicity());

                    //addAttributeItem(tempPar, "mif-type", MIFObjectClassType.ASSOCIATION.toString());
                    addAttributeItem(tempPar, "type", "MIFAssociation");
                    addAttributeItem(tempPar, "conformance", asso.getConformance());
                    addAttributeItem(tempPar, "sortKey", asso.getSortKey());

                    if (asso.isEnabled()) addAttributeItem(tempPar, "isEnabled", "true");
                    else addAttributeItem(tempPar, "isEnabled", "false");

                }
            }
        }
        if (headerName == null) headerName = nodeName;
        if ((dtype != null)&&(!dtype.trim().equals("")))
        {
            dtype = "(" + dtype + ") : ";
        }
        else dtype = "";
        System.out.println("MIF ==> " + spaces + tag + nodeName + " --> "+ dtype + xmlPath);
        if (tempPar == null) return null;
        if (parent != null)
        {
            tempPar.setParent(parent);
            parent.addChildNode(tempPar);
            tempPar.setXPath(xmlPath);
            //tempPar.setXPath(getSimpleXPath(tempPar.generateXPath(".")));
        }
        for (int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
            try
            {
                convertTree(tempPar, child, (depth+1));
            }
            catch(ApplicationException ae)
            {
                ae.printStackTrace();
                throw ae;
            }
        }


        return tempPar;
    }
//    private void setSegmentName(H3SInstanceMetaSegment parent, H3SInstanceMetaSegment node, String name) throws ApplicationException
//    {
//        if (node == null) throw new ApplicationException("Node for setNane is null.");
//        if (parent == null) node.setName(name);
//        List<CommonNode> list = parent.getChildNodes();
//        for
//    }
//    private String getSimpleXPath(String xpath)
//    {
//        xpath = xpath.trim();
//        int idx = xpath.indexOf(headerName);
//        if (idx < 0)
//        {
//            System.err.println("CVVV : invalid xpath" + xpath);
//            return xpath;
//        }
//        xpath = xpath.substring(idx);
//        if (xpath.endsWith(".")) xpath = xpath.substring(0, xpath.length()-1);
//        return xpath;
//    }
    //private H3SInstanceMetaSegment addAttributeItemCardinality(H3SInstanceMetaSegment tempPar, int min, int max)
    private CommonNode setNameAndCardinality(H3SInstanceMetaSegment parent, CommonNode tempPar, String xmlPath, String mifType, int min, int max) throws ApplicationException
    {

        if (tempPar == null) throw new ApplicationException("Node for setName is null.");
        if (parent == null)
        {
            tempPar.setName(xmlPath);
        }

        String cardinality = "";
        if ((min == 0)&&(max == -1)) cardinality = Config.CARDINALITY_ZERO_TO_MANY;
        if ((min == 0)&&(max == 1)) cardinality = Config.CARDINALITY_ZERO_TO_ONE;
        if ((min == 1)&&(max == -1)) cardinality = Config.CARDINALITY_ONE_TO_MANY;
        if ((min == 1)&&(max == 1)) cardinality = Config.CARDINALITY_ONE_TO_ONE;

//        if ((cardinality.equals(Config.CARDINALITY_ONE_TO_MANY))||(cardinality.equals(Config.CARDINALITY_ZERO_TO_MANY)))
//        {
//            String segmentNum = "";
//            int seq = 0;
//            if (tempPar instanceof H3SInstanceMetaSegment)
//            {
//                //H3SInstanceMetaSegment segment = (H3SInstanceMetaSegment) tempPar;
//                List<CommonNode> list = parent.getChildNodes();
//                for(int i=0;i<list.size();i++)
//                {
//                    CommonNode node = list.get(i);
//                    if (!(node instanceof H3SInstanceMetaSegment)) continue;
//                    H3SInstanceMetaSegment segmentNode = (H3SInstanceMetaSegment) node;
//                    String nodeNm = segmentNode.getName();
//                    String nameBody = nodeNm.substring(0, nodeNm.length()-2);
//                    String seqTail = nodeNm.substring(nodeNm.length()-2);
//
//                    int n = -1;
//                    try
//                    {
//                        n = Integer.parseInt(seqTail);
//                    }
//                    catch(NumberFormatException ne)
//                    {
//                        continue;
//                    }
//                    if ((nameBody.equals(name))&&(n >= 0)) seq++;
//                }
//                if (seq < 10) segmentNum = "0" + seq;
//                else segmentNum = "" + seq;
//                tempPar.setName(name+segmentNum);
//                //System.out.println("CVVV : Set Name : " + tempPar.getName());
//            }
//            else tempPar.setName(name);
//        }
//        else tempPar.setName(name);

        String lastToken = "";
        StringTokenizer st = new StringTokenizer(xmlPath.trim(), ".");

        while(st.hasMoreTokens())
        {
            String tempS = st.nextToken();
            if (!tempS.trim().equals("")) lastToken = tempS.trim();
        }
        tempPar.setName(lastToken);

        addAttributeItem(tempPar, "mif-type", mifType);

        if (!cardinality.equals(""))
        {
            addAttributeItem(tempPar, "cardinality", cardinality);
        }
        //System.out.println("CVVV : cardinality : " + tempPar.getName() + " : " + cardinality);

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

    private boolean isUnnecessaryAttribute(MetaField field)
    {
        int n = 0;
        H3SInstanceMetaSegment parent = null;
        String type = null;
        String name = null;
        while(n < 2)
        {
            if (n == 0)
            {
                parent = (H3SInstanceMetaSegment) field.getParent();

                type = getAttributeItemValue(parent.getAttributes(), "concreteDatatype");
                if ((type == null)||(type.trim().equals(""))) type = getAttributeItemValue(parent.getAttributes(), "datatype");
                name = field.getName();
            }
            else
            {
                name = parent.getName();
                parent = (H3SInstanceMetaSegment) parent.getParent();
                if (parent == null) return false;
                type = getAttributeItemValue(parent.getAttributes(), "concreteDatatype");
                if ((type == null)||(type.trim().equals(""))) type = getAttributeItemValue(parent.getAttributes(), "datatype");
            }

            boolean isAvoidElement = false;

            if ((type.equals("EN"))||(type.equals("PN"))||(type.equals("ON"))||(type.equals("TN")))
            {
                if ((name.equalsIgnoreCase("validTime"))||(name.equalsIgnoreCase("delimiter")))
                    isAvoidElement = true;
            }
            else if ((type.equals("CS"))||(type.equals("CV"))||(type.equals("CO"))||(type.equals("CE"))||
                     (type.equals("CD"))||(type.equals("ED"))||(type.equals("ST"))||(type.equals("SC")))
            {
                if ((name.equalsIgnoreCase("reference"))||(name.equalsIgnoreCase("originalText"))||
                    (name.equalsIgnoreCase("qualifier"))||(name.equalsIgnoreCase("translation"))||
                    (name.equalsIgnoreCase("usablePeriod"))||(name.equalsIgnoreCase("thumbnail")))
                    isAvoidElement = true;
            }
            else if (type.equals("PQ"))
            {
                if (name.equalsIgnoreCase("translation"))
                    isAvoidElement = true;
            }

            if (isAvoidElement)
            {
                //System.out.println(" GGGGG : This is Avoid Element : " + type + " : " + name);
                return true;
            }
            n++;
        }
        return false;
    }

    private boolean checkExceptionCaseOfLine(String line, String check)
    {
        if ((line == null)||(line.trim().equals(""))) return false;
        line = line.trim();
        if ((check == null)||(check.trim().equals(""))) return false;
        check = check.trim();

        int idx = check.indexOf("#");
        if (idx < 0) return (line.toLowerCase().indexOf(check.toLowerCase()) > 0);

        String target = "";
        String lne = line;
        for(char chr:(check + "^").toCharArray())
        {
            String acharS = "" + chr;
            if (acharS.equals("#"))
            {
                if (target.equals("")) continue;
            }
            else if (acharS.equals("^"))
            {
                if (target.equals("")) return true;
            }
            else
            {
                target = target + acharS;
                continue;
            }
            idx = lne.toLowerCase().indexOf(target.toLowerCase());
            if (idx < 0) return false;
            target = "";
            if (acharS.equals("^")) return true;
            lne = lne.substring(idx + target.length());

            while(true)
            {
                if (lne.length() == 0)
                {
                    if (acharS.equals("^")) return true;
                    else return false;
                }
                String achar = lne.substring(0,1);
                if ((achar.equals("0"))||(achar.equals("1"))||(achar.equals("2"))||(achar.equals("3"))||(achar.equals("4"))||
                    (achar.equals("5"))||(achar.equals("6"))||(achar.equals("7"))||(achar.equals("8"))||(achar.equals("9")))
                {
                    lne = lne.substring(1);
                }
                else break;
            }
        }
        return false;
    }

    public static void main(String[] args)
    {
        //String fileName = "C:\\projects\\NewInstance\\040011_big\\040011.h3s";
        //String fileName = "C:\\projects\\NewInstance\\150000\\150000.h3s";
        //String fileName = "C:\\projects\\NewInstance\\150000\\gen\\040011.h3s";
        //String fileName = "T:\\YeWu\\xmlpathSpec\\newCOCT_MT150003.h3s";
        //String fileName = "C:\\projects\\caadapter\\workingspace\\NewEncounter\\NewEncounter.h3s";
        //String fileName = "C:\\projects\\caadapter\\workingspace\\010000\\010000-Person.h3s";
        //String fileName = "C:\\projects\\caadapter\\workingspace\\COCT_MT010000\\COCT_MT010000_Simple.h3s";
        //String fileName = "C:\\projects\\caadapter\\workingspace\\COCT_MT010000\\" + args[0] + ".h3s";
        //String fileName = "C:\\caAdapter_Test\\caadapter40\\workingspace\\CDA\\POCD_MT000030.h3s";
        //String fileName = "C:\\caAdapter_Test\\caadapter40\\workingspace\\CDA\\POCD_MT000040.h3s";
    //    String fileName = "C:\\projects\\caadapter\\workingspace\\CDA\\POCD_MT000040UV02_3.h3s";
        //String fileName = "C:\\project\\caadapter\\workingspace\\PORR_MT049006_T.h3s";
        String fileName = "C:\\project\\caadapter\\workingspace\\PORR_MT040002.h3s";
        //new H3SInstanceMetaTree(args[0]);

        String arg = "";
        try
        {
            arg = args[0];
        }
        catch(Exception ee) {}

        try
        {
            if ((arg == null)||(arg.trim().equals("")))
            {
                System.out.println("No H3S file has been input");
                System.out.println("  **Default H3S file : " + fileName);
                new H3SInstanceMetaTree(fileName);
            }
            else new H3SInstanceMetaTree(args[0].trim());
        }
        catch(ApplicationException ae)
        {
            System.out.println(ae.getMessage());
        }
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.22  2009/06/05 21:27:34  altturbo
 * HISTORY      : update for PQ data type
 * HISTORY      :
 * HISTORY      : Revision 1.21  2009/05/21 14:06:16  altturbo
 * HISTORY      : update and add checkExceptionCaseOfLine()
 * HISTORY      :
 * HISTORY      : Revision 1.20  2008/10/28 20:53:26  umkis
 * HISTORY      : upgrade
 * HISTORY      :
 * HISTORY      : Revision 1.19  2008/10/21 21:18:31  umkis
 * HISTORY      : To harmonize with version 4.2
 * HISTORY      :
 * HISTORY      : Revision 1.18  2008/09/29 20:05:06  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.17  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2008/06/04 14:45:25  umkis
 * HISTORY      : add some exception cases.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2008/04/25 04:46:19  umkis
 * HISTORY      : getVocabularySeeker() was picked out from GeneralUtilities and moved into VocabulraryGeneralUtilities.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2008/04/23 18:12:02  umkis
 * HISTORY      : H3S test instance generator install onto ManuBar
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/03/26 14:43:30  umkis
 * HISTORY      : Re-assigning sortkey
 * HISTORY      :
 * HISTORY      : Revision 1.12  2008/03/20 03:49:26  umkis
 * HISTORY      : for re-assigning sort key to mif files
 * HISTORY      :
 * HISTORY      : Revision 1.11  2008/03/06 17:25:53  umkis
 * HISTORY      : update end minor change
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/09/08 20:59:38  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/08/27 20:43:22  umkis
 * HISTORY      : fix the Bug of infinite looping when choice included HL7 transformation
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/08/17 01:11:36  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/08/09 01:56:52  umkis
 * HISTORY      : add a feature that v2Meta directory creating when search the directory
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/08/08 20:45:48  umkis
 * HISTORY      : Change to V3VocabularyTreeBuildEventHandler
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/07 15:43:26  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/07 04:10:03  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/06 14:27:43  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/03 05:01:31  umkis
 * HISTORY      : add items which have to be input data
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 16:29:40  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 15:43:55  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
