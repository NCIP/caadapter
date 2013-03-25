/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.MetaField;

import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.H3SInstanceSegmentType;
/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.8 $
 *          date        Jul 6, 2007
 *          Time:       3:57:59 PM $
 */
public class GenerateMapFileFromDataFile
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: GenerateMapFileFromDataFile.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/GenerateMapFileFromDataFile.java,v 1.8 2009-06-05 21:28:06 altturbo Exp $";


    private boolean success = false;
    private String message = "";
    private DataTree dt;
    private boolean isNewMethod = false;
    private List<String> scsList = new ArrayList<String>();
    private List<String> h3sList = new ArrayList<String>();
    private List<String> csvList = new ArrayList<String>();
    private List<String> mapList = new ArrayList<String>();

    private int functionID = -1;

    public GenerateMapFileFromDataFile(String dataFilePath, String header, String generatedFilePath, String h3sFileName)
    {
        success = false;
        TestFileGenerateSCS tfg = new TestFileGenerateSCS(dataFilePath, header, generatedFilePath);
        if (!tfg.checkSuccess())
        {
            message = "SCS and CSV file couldn't be generated... : " + tfg.getMessage();
            System.out.println(message);
            return;
        }

        dt = new DataTree(generatedFilePath+".scs", h3sFileName, header);

        h3sList = fileIntoList(h3sFileName);
        generateMapFile(dataFilePath, header, generatedFilePath, h3sFileName);
    }
    public GenerateMapFileFromDataFile(String dataFilePath, String header, String generatedFilePath, String h3sFileName, CommonNode head)
    {
        success = false;
        isNewMethod = true;
        TestFileGenerateSCS tfg = new TestFileGenerateSCS(dataFilePath, header, generatedFilePath, isNewMethod);
        if (!tfg.checkSuccess())
        {
            message = "SCS and CSV file couldn't be generated... : " + tfg.getMessage();
            System.out.println(message);
            return;
        }

        dt = new DataTree(generatedFilePath+".scs", head, header);
        generateMapFile(dataFilePath, header, generatedFilePath, h3sFileName);
    }
    public void generateMapFile(String dataFilePath, String header, String generatedFilePath, String h3sFileName)
    {
        scsList = fileIntoList(generatedFilePath+".scs");
        csvList = fileIntoList(generatedFilePath+".csv");
        //h3sList = fileIntoList(h3sFileName);

        if (!dt.checkSuccess())
        {
            message = "SCS and CSV tree structure couldn't be built... : " + dt.getErrorMessage();
            return;
        }

        String readLineOfFile = "";
        String scsUUID = UUIDGenerator.getUniqueString();
        String h3sUUID = UUIDGenerator.getUniqueString();
        //String constantOutputUUID = "asdf654a6sdv51ae65f4rgv654er";
        FunctionManager fmgr = FunctionManager.getInstance();
        String function1 = "";
        String function2 = "";
        String function3 = "";
        int functionSeq = 0;
        boolean defaultTag = false;
        boolean functionTag =  false;
        FunctionItemList fiL = new FunctionItemList();
        NodeElement h3sNode = null;
        NodeElement scsNode = null;

        try
        {
            FileReader fr = new FileReader(dataFilePath);
            BufferedReader br = new BufferedReader(fr);

            int idx = 0;
            FileWriter fw = null;
            try
            {
                fw = new FileWriter(generatedFilePath + ".map");
                //fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                //         "<mapping version=\"1.2\">\n" +
                //         "   <components>\n" +
                //         "      <component kind=\"scs\" location=\"" + cutFileName(generatedFilePath + ".scs") + "\" type=\"source\" uuid=\"" + scsUUID + "\"/>\n" +
                //         "      <component kind=\"HL7v3\" location=\"" + cutFileName(h3sFileName) + "\" type=\"target\" uuid=\"" + h3sUUID + "\"/>\n" +
                //         "<!--%%1-->\n   </components>\n   <links>\n");
                if (isNewMethod)
                {
                    fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                             "<mapping version=\"1.2\">\n" +
                             "   <components>\n" +
                             "      <component kind=\"scs\" location=\"" + cutFileName(generatedFilePath + ".scs") + "\" type=\"source\"/>\n" +
                             "      <component kind=\"h3s\" location=\"" + cutFileName(h3sFileName) + "\" type=\"target\"/>\n" +
                             "<!--%%1-->\n   </components>\n   <links>\n");
                }
                else
                {
                    fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                             "<mapping version=\"1.2\">\n" +
                             "   <components>\n" +
                             "      <component kind=\"scs\" location=\"" + cutFileName(generatedFilePath + "_1.scs") + "\" type=\"source\" uuid=\"" + scsUUID + "\"/>\n" +
                             "      <component kind=\"HL7v3\" location=\"" + (cutFileName(h3sFileName)).replace(".h3s", "_1.h3s") + "\" type=\"target\" uuid=\"" + h3sUUID + "\"/>\n" +
                             "<!--%%1-->\n   </components>\n   <links>\n");
                }
                //fw.close();
            }
            catch(IOException ie)
            {
                message = "Map file open error : " + ie.getMessage();
                System.out.println(message);
                return;
            }
            String line = "";
            String dat = "";
            int step = 0;
            int xPosition = 20;
            int yPosition = 10;
            while((readLineOfFile=br.readLine())!=null)
            {
                defaultTag = false;
                functionTag =  false;
                if (readLineOfFile.startsWith("#")) continue;

                if (readLineOfFile.indexOf("=>") > 0)
                {
                    idx = readLineOfFile.indexOf("=>");
                    step = 2;
                }
                else if (readLineOfFile.indexOf("=F>") > 0)
                {
                    idx = readLineOfFile.indexOf("=F>");
                    step = 3;
                    functionTag = true;
                }
                else if (readLineOfFile.indexOf("=D>") > 0)
                {
                    idx = readLineOfFile.indexOf("=D>");
                    step = 3;
                    defaultTag = true;
                }
                else continue;

                line = readLineOfFile.substring(0, idx).trim();
                dat = readLineOfFile.substring(idx + step).trim();

                //if (!functionTag) functionTag = fiL.isFunction(line);
                //if (!defaultTag) defaultTag = fiL.isDefault(line);
                if ((functionTag)&&(defaultTag))
                {

                }
                System.out.println("**** This Line : " + line);

                if (line.endsWith(".noneX")) h3sNode = dt.searchH3SPath(line.substring(0, (line.length()-(".noneX").length() )));
                else h3sNode = dt.searchH3SPath(line);

//                if (line.endsWith(".noneX")) line = line.substring(0, (line.length()-(".noneX").length() ));
//                h3sNode = dt.searchH3SPath(line);

                if (h3sNode == null)
                {
                    System.err.println("Not found h3s node for this line : " + line);
                    continue;
                }
                if (defaultTag)
                {
                    if (!isNewMethod) modifyH3SFile(h3sNode.getXmlPath(), dat);
                    continue;
                }
                if (functionTag)
                {
                    FunctionMeta fm = fmgr.getFunctionMeta("core", "constant", "constant");
                    //String functionConstantUUID = fm.getXmlPath();
                    String functionConstantUUID = UUIDGenerator.getUniqueString();
                    List<ParameterMeta> liP = fm.getOuputDefinitionList();

                    String constantOutputUUID = liP.get(0).getXmlPath();

                    if (dat.startsWith("\"")) dat = dat.substring(1);
                    if (dat.endsWith("\"")) dat = dat.substring(0, dat.length()-1);
                    if (isNewMethod)
                    {
                        functionID++;
                        function1 = function1 +
                                "      <component group=\"constant\" kind=\"core\" name=\"constant\" type=\"function\" id=\"" + functionID + "\">\n" +
                                "         <data type=\"String\" value=\"" + dat + "\"/>\n" +
                                "      </component>\n";
                        function2 = function2 +
                                "      <link>\n" +
                                "         <source>\n" +
                                "            <linkpointer kind=\"function\" xmlPath=\"function." + functionID + ".outputs.0\"/>\n" +
                                "         </source>\n" +
                                "         <target>\n" +
                                "            <linkpointer kind=\"h3s\" xmlPath=\"" + h3sNode.getXmlPath() + "\"/>\n" +
                                "         </target>\n" +
                                "      </link>\n";
                    }
                    else
                    {
                        function1 = function1 +
                                "      <component group=\"constant\" kind=\"core\" name=\"constant\" type=\"function\" uuid=\"" + functionConstantUUID + "\">\n" +
                                "         <data type=\"String\" value=\"" + dat + "\"/>\n" +
                                "      </component>\n";
                        function2 = function2 +
                                "      <link uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" +
                                "         <linkpointer component-uuid=\"" + functionConstantUUID + "\" data-uuid=\"" + constantOutputUUID + "\"/>\n" +
                                "         <linkpointer component-uuid=\"" + h3sUUID + "\" data-uuid=\"" + h3sNode.getXmlPath() + "\"/>\n" +
                                "      </link>\n";
                    }
                    if ((functionSeq % 2) == 0)
                    {
                        xPosition = 20;
                        yPosition = yPosition + 45;
                    }
                    else
                    {
                        xPosition = 130;
                    }

                    if (functionSeq == 0) yPosition = 20;

                    if (isNewMethod)
                        function3 = function3 + "      <view component-id=\"function." + functionID + "\" height=\"43\" width=\"109\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                    else function3 = function3 + "      <view component-uuid=\"" + functionConstantUUID + "\" height=\"43\" width=\"109\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                    functionSeq++;
                    continue;
                }

               scsNode = dt.searchSCSPath(line);
//                if ((line.endsWith(".noneX"))||(line.endsWith("_noneX"))) scsNode = dt.searchSCSPath(line.substring(0, (line.length()-(".noneX").length())));
//                else scsNode = dt.searchSCSPath(line);

//                if (line.endsWith(".noneX")) line = line.substring(0, (line.length()-(".noneX").length()));
//                scsNode = dt.searchSCSPath(line);

                if (scsNode == null)
                {
                    System.err.println("Not found scs node for this line : " + line);
                    continue;
                }

                if (line.endsWith("telecom.value"))
                {
                    if (!dat.startsWith("tel:"))
                    {
                        FunctionMeta fm = fmgr.getFunctionMeta("core", "String", "Concatenate");
                        List<ParameterMeta> liI = fm.getInputDefinitionList();
                        List<ParameterMeta> liO = fm.getOuputDefinitionList();

                        //String functionConcatUUID = fm.getXmlPath();
                        String functionConcatUUID = UUIDGenerator.getUniqueString();
                        String inputParam1UUID = liI.get(0).getXmlPath();
                        String inputParam2UUID = liI.get(1).getXmlPath();
                        String outputParamUUID = liO.get(0).getXmlPath();

                        fm = fmgr.getFunctionMeta("core", "constant", "constant");
                        //String functionStrCosntantUUID = fm.getXmlPath();
                        String functionStrCosntantUUID = UUIDGenerator.getUniqueString();
                        List<ParameterMeta> liP = fm.getOuputDefinitionList();
                        String constantOutputUUID = liP.get(0).getXmlPath();

                        if (isNewMethod)
                        {
                            functionID++;
                            int concatID = functionID;
                            functionID++;
                            int constantID = functionID;
                            function1 = function1 +
                                    "      <component group=\"string\" kind=\"core\" name=\"Concatenate\" type=\"function\" id=\"" + concatID + "\"/>\n" +
                                    "      <component group=\"constant\" kind=\"core\" name=\"constant\" type=\"function\" id=\"" + constantID + "\">\n" +
                                    "         <data type=\"String\" value=\"tel:\"/>\n" +
                                    "      </component>\n";
                            function2 = function2 +
                                    "      <link>\n" +
                                    "         <source>\n" +
                                    "            <linkpointer kind=\"function\" xmlPath=\"function." + constantID + ".outputs.0\"/>\n" +
                                    "         </source>\n" +
                                    "         <target>\n" +
                                    "            <linkpointer kind=\"function\" xmlPath=\"function." + concatID + ".inputs.0\"/>\n" +
                                    "         </target>\n" +
                                    "      </link>\n" +
                                    "      <link>\n" +
                                    "         <source>\n" +
                                    "            <linkpointer kind=\"scs\" xmlPath=\"" + scsNode.getXmlPath() + "\"/>\n" +
                                    "         </source>\n" +
                                    "         <target>\n" +
                                    "            <linkpointer kind=\"function\" xmlPath=\"function." + concatID + ".inputs.1\"/>\n" +
                                    "         </target>\n" +
                                    "      </link>\n" +
                                    "      <link>\n" +
                                    "         <source>\n" +
                                    "            <linkpointer kind=\"function\" xmlPath=\"function." + concatID + ".outputs.0\"/>\n" +
                                    "         </source>\n" +
                                    "         <target>\n" +
                                    "            <linkpointer kind=\"h3s\" xmlPath=\""+ h3sNode.getXmlPath() +"\"/>\n" +
                                    "         </target>\n" +
                                    "      </link>\n";

                            xPosition = 20;
                            yPosition = yPosition + 45;

                            function3 = function3 + "      <view component-id=\"function." + constantID + "\" height=\"43\" width=\"109\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                            functionSeq++;

                            xPosition = 130;

                            //yPosition = ((functionSeq / 2) * 45) + 10;

                            function3 = function3 + "      <view component-id=\"function." + concatID + "\" height=\"67\" width=\"91\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                            functionSeq++;
                            if ((functionSeq % 2) == 1) functionSeq++;
                            yPosition = yPosition + 25;
                        }
                        else
                        {
                            function1 = function1 +
                                    "      <component group=\"string\" kind=\"core\" name=\"Concatenate\" type=\"function\" uuid=\"" + functionConcatUUID + "\"/>\n" +
                                    "      <component group=\"constant\" kind=\"core\" name=\"constant\" type=\"function\" uuid=\"" + functionStrCosntantUUID + "\">\n" +
                                    "         <data type=\"String\" value=\"tel:\"/>\n" +
                                    "      </component>\n"
                                    ;
                            function2 = function2 +
                                    "      <link uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" +
                                    "         <linkpointer component-uuid=\"" + functionStrCosntantUUID + "\" data-uuid=\"" + constantOutputUUID + "\"/>\n" +
                                    "         <linkpointer component-uuid=\"" + functionConcatUUID + "\" data-uuid=\"" + inputParam1UUID + "\"/>\n" +
                                    "      </link>\n" +
                                    "      <link uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" +
                                    "         <linkpointer component-uuid=\"" + scsUUID + "\" data-uuid=\"" + scsNode.getXmlPath() + "\"/>\n" +
                                    "         <linkpointer component-uuid=\"" + functionConcatUUID + "\" data-uuid=\"" + inputParam2UUID + "\"/>\n" +
                                    "      </link>\n" +
                                    "      <link uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" +
                                    "         <linkpointer component-uuid=\"" + functionConcatUUID + "\" data-uuid=\"" + outputParamUUID + "\"/>\n" +
                                    "         <linkpointer component-uuid=\"" + h3sUUID + "\" data-uuid=\"" + h3sNode.getXmlPath() + "\"/>\n" +
                                    "      </link>\n";

                            xPosition = 20;
                            yPosition = yPosition + 45;

                            function3 = function3 + "      <view component-uuid=\"" + functionStrCosntantUUID + "\" height=\"43\" width=\"109\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                            functionSeq++;

                            xPosition = 130;

                            //yPosition = ((functionSeq / 2) * 45) + 10;

                            function3 = function3 + "      <view component-uuid=\"" + functionConcatUUID + "\" height=\"67\" width=\"91\" x=\"" + xPosition + "\" y=\"" + yPosition + "\"/>\n";
                            functionSeq++;
                            if ((functionSeq % 2) == 1) functionSeq++;
                            yPosition = yPosition + 25;
                        }
                        continue;
                    }
                }

                if (scsNode.getLinkedUUID().equals("NONE")) scsNode.setLinkedUUID(h3sNode.getXmlPath());
                else System.out.println("*** Warning : scs dual linked : " + line);

                if (h3sNode.getLinkedUUID().equals("NONE")) h3sNode.setLinkedUUID(scsNode.getXmlPath());
                else System.out.println("*** Warning : h3s dual linked : " + line);
                if (!h3sNode.isLeafNode())
                {
                    if (dt.countChildNodes(h3sNode) == 1)
                    {
                        NodeElement child = h3sNode.getLower();
                        if (child.getName().equals("value"))
                        {
                            System.out.println("*** Note : h3s node switched with child node 'value'. : " + h3sNode.getName());
                            h3sNode = child;
                        }
                        else if (child.getName().equals("inlineText"))
                        {
                            System.out.println("*** Note : h3s node switched with child node 'inlineText'. : " + h3sNode.getName());
                            h3sNode = child;
                        }
                        else System.out.println("*** warning : h3s node is not a leaf node. (one but neither inlineText nor value) : " + h3sNode.getName());
                    }
                    else if (dt.countChildNodes(h3sNode) == 2)
                    {
                        if ((h3sNode.getName().equals("numerator"))||(h3sNode.getName().equals("denominator")))
                        {
                            h3sNode = h3sNode.getLower();
                        }
                        else System.out.println("*** warning : h3s node is not a leaf node. (2 children) : " + h3sNode.getName());
                    }
                    else System.out.println("*** warning : h3s node is not a leaf node. (more 3 children) : " + h3sNode.getName());
                }
                //else System.out.println("*** warning : h3s node is not a leaf node. (children) : " + h3sNode.getName() + " : " + h3sNode.getLower().getName());
                try
                {
                    if (isNewMethod)
                    {
                        fw.write("      <link>\n" +
                                 "         <source>\n" +
                                 "            <linkpointer kind=\"scs\" xmlPath=\"" + scsNode.getXmlPath() + "\"/>\n" +
                                 "         </source>\n" +
                                 "         <target>\n" +
                                 "            <linkpointer kind=\"h3s\" xmlPath=\"" + h3sNode.getXmlPath() + "\"/>\n" +
                                 "         </target>\n" +
                                 "      </link>\n");
                    }
                    else
                    {
                        fw.write("      <link uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" +
                                 "         <linkpointer component-uuid=\"" + scsUUID + "\" data-uuid=\"" + scsNode.getXmlPath() + "\"/>\n" +
                                 "         <linkpointer component-uuid=\"" + h3sUUID + "\" data-uuid=\"" + h3sNode.getXmlPath() + "\"/>\n" +
                                 "      </link>\n");
                    }
                    System.out.println("MAP Writing : " + scsNode.getName() + " TO " + h3sNode.getName());
                }
                catch(IOException ie)
                {
                    message = "Map file Writing error : " + ie.getMessage();
                    System.out.println(message);
                    return;
                }
            }
            try
            {
                if (isNewMethod)
                {
                    fw.write("<!--%%2-->\n   </links>\n" +
                            "   <views>\n" +
                            "      <view component-id=\"source.scs.0\" height=\"0\" width=\"0\" x=\"0\" y=\"0\"/>\n" +
                            "      <view component-id=\"target.h3s.0\" height=\"0\" width=\"0\" x=\"0\" y=\"0\"/>\n" +
                            "<!--%%3-->\n   </views>\n" +
                            "</mapping>\n");
                }
                else
                {
                    fw.write("<!--%%2-->\n   </links>\n" +
                            "   <views>\n" +
                            "      <view component-uuid=\"" + scsUUID + "\" height=\"0\" width=\"0\" x=\"0\" y=\"0\"/>\n" +
                            "      <view component-uuid=\"" + h3sUUID + "\" height=\"0\" width=\"0\" x=\"0\" y=\"0\"/>\n" +
                            "<!--%%3-->\n   </views>\n" +
                            "</mapping>\n");
                }
                fw.close();
            }
            catch(IOException ie)
            {
                message = "Map file closing error : " + ie.getMessage();
                System.out.println(message);
                return;
            }
        }
        catch(IOException e)
        {
            message = "Main raw Data File Not Found : " + dataFilePath;
            System.out.println(message);
            return;
        }
        if (!isNewMethod)
        {
            mapList = fileIntoList(generatedFilePath+".map");

            success = true;
            if (!writeMAPList(generatedFilePath, functionSeq, function1, function2, function3))
            {
                //System.err.println("Not found scs node for this line : " + line);
                message = message + "\n" + "Error at writeMAPList";
                success = false;
            }
            if (!writeSCSList(generatedFilePath))
            {
                message = message + "\n" + "Error at writeSCSList";
                success = false;
            }
            if (!writeH3SList(generatedFilePath))
            {
                message = message + "\n" + "Error at writeH3SList";
                success = false;
            }
            if (!writeCSVList(generatedFilePath))
            {
                message = message + "\n" + "Error at writeCSVList";
                success = false;
            }
        }

        if (message.equals("")) success = true;
        else success = false;
    }

    public String getMessage()
    {
        return message;
    }

    private String cutFileName(String fi)
    {
        int len = fi.length();
        String st = "";
        String achar = "";
        for(int i=len;i>0;i--)
        {
            achar = fi.substring(i-1, i);
            if (achar.equals("\\")) break;
            if (achar.equals("/")) break;
            st = achar + st;
        }
        return st;
    }

    public static void main(String args[])
    {

        //new GenerateMapFileFromDataFile("C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\sampleMessageDesign040003.txt",
        //                                "PROCEVN",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040003_generated",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040003.h3s");
        //new GenerateMapFileFromDataFile("C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040002_new2\\SampleMessageDesign.txt",
        //                                "SAE",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040002_new2\\040002_generated",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040002_new2\\040002.h3s");
        //new GenerateMapFileFromDataFile("C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\SampleMessageDesign040001.txt",
        //                                "INVESTEVN",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040001_generated",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040001_Embeded040004.h3s");
        //new GenerateMapFileFromDataFile("C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\SampleMessageDesign040001_simple.txt",
        //                                "INVESTEVN",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040001_generated_simple",
        //                                "C:\\caAdapter\\hl7sdk\\workingspace\\examples\\040011\\040001_Embeded040004_simple.h3s");

//        new GenerateMapFileFromDataFile("C:\\projects\\NewInstance\\710000\\SampleInstanceDesign.txt",
//                                        "PLACE",
//                                        "C:\\projects\\NewInstance\\710000\\710000",
//                                        "C:\\projects\\NewInstance\\710000\\710000.h3s");
//        new GenerateMapFileFromDataFile("C:\\projects\\hl7sdk\\workingspace\\examples\\040011\\SampleInstanceDesign.txt",
//                                        "INVESTEVN",
//                                        "C:\\projects\\hl7sdk\\workingspace\\examples\\040011\\040011",
//                                        "C:\\projects\\hl7sdk\\workingspace\\examples\\040011\\040011.h3s");
//        new GenerateMapFileFromDataFile("C:\\projects\\hl7sdk\\workingspace\\examples\\150003_max_mapping_test\\SampleInstanceDesign.txt",
//                                        "ORGAN",
//                                        "C:\\projects\\hl7sdk\\workingspace\\examples\\150003_max_mapping_test\\150003",
//                                        "C:\\projects\\hl7sdk\\workingspace\\examples\\150003_max_mapping_test\\150003_max_mapping_test.h3s");
//
        new GenerateMapFileFromDataFile("C:\\projects\\NewInstance\\150000\\gen\\SampleInstanceDesign2.txt",
                                        "ORGAN_HEAD",
                                        "C:\\projects\\NewInstance\\150000\\gen\\150000",
                                        "C:\\projects\\NewInstance\\150000\\gen\\150000.h3s");
        //150003_max_mapping_test
        //SVCDLVRYLOCA  240000
        //ASSIGNEDPERSON0 090100
        //PLACE 710000
        //SVCDELILOCA 240001
        //SAE 040002
        //PROCEVN 040003
        //ADMINDRUG 040004
        //IDENDEVICE0 040005
        //INVESTEVN 040011_simple
        //INVESTEVN 040011_simple2
        //INVESTEVN 040011_big
        //ORGANIZA0 150000
        //LOCATEDENTITY 070000
        //ASSIGNEDENTITY 090000
      }

    public boolean getSuccess()
    {
        return success;
    }

    public DataTree getDataTree()
    {
        return dt;
    }

    private List<String> fileIntoList(String fileName)
    {
        List<String> list = new ArrayList<String>();
        String readLineOfFile = "";
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);


            while((readLineOfFile=br.readLine())!=null)
            {
                list.add(readLineOfFile);
            }
        }
        catch(IOException ie)
        {
            System.out.println("**** fileIntoList : " + fileName);
            return null;
        }
        return list;
    }

    private int searchStringList(List<String> list, String srch)
    {
        return searchStringList(list, srch, 0);
    }
    private int searchStringList(List<String> list, String srch, int start)
    {
        int idx = -1;
        for (int i=start;i<list.size();i++)
        {
            if (list.get(i).indexOf(srch) >= 0)
            {
                idx = i;
                break;
            }
        }
        return idx;
    }

    private List<String> replaceList(List<String> list, int idx, String str)
    {
        List<String> li = new ArrayList<String>();
        if (idx > list.size()) return list;
        for (int i=0;i<list.size();i++)
        {
            if (i == idx) li.add(str);
            else li.add(list.get(i));
        }
        return li;
    }

    private void modifyH3SFile(String uuid, String dat)
    {
        int idx = searchStringList(h3sList, uuid);
        if (idx < 0)
        {
            System.out.println("error on modifyH3SFile" + uuid + " : " + dat);
            return;
        }
        String line = h3sList.get(idx);

        if (line.indexOf("user-default=\"") > 0) return;
        if (!line.trim().startsWith("<datatypeField "))
        {
            System.out.println("FDFDF : " + line + " : " + uuid);
            return;
        }
        if (dat.startsWith("\"")) dat = dat.substring(1);
        if (dat.endsWith("\"")) dat = dat.substring(0, dat.length()-1);

        line = line.replace("uuid=", "user-default=\"" + dat + "\" uuid=");
        h3sList = replaceList(h3sList, idx, line);
    }

    private boolean writeMAPList(String path, int seq, String f1, String f2, String f3)
    {
        String writeL = "";
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(path + "_1.map");
            if(seq == 0)
            {
                for(int i=0;i<mapList.size();i++)
                {
                    writeL = mapList.get(i);
                    if (writeL.indexOf("--%%") < 0) fw.write(writeL + "\n");
                }
            }
            else
            {
                for(int i=0;i<mapList.size();i++)
                {
                    writeL = mapList.get(i);
                    if (writeL.indexOf("--%%1") > 0) fw.write(f1);
                    else if (writeL.indexOf("--%%2") > 0) fw.write(f2);
                    else if (writeL.indexOf("--%%3") > 0) fw.write(f3);
                    else fw.write(writeL + "\n");
                }
            }

            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("Map file Writing error (2) : " + ie.getMessage());
            return false;
        }
        return true;
    }
    private boolean checkInt(List<Integer> li, int in)
    {
        boolean res = false;
        int inc = -1;
        for(int i=0;i<li.size();i++)
        {
            inc = li.get(i);
            if (inc == in)
            {
                res = true;
                break;
            }
        }
        return res;
    }
    private String addSuffix(String st)
    {
        String head = "";
        String tail = "";
        if(st == null) return "";
        if(st.equals("")) return "";
        if(st.equals("0")) return "0";
        if(st.length() == 1)
        {
            head = "";
            tail = st;
        }
        else
        {
            head = st.substring(0, st.length()-1);
            tail = st.substring(st.length()-1);
        }
        String newTail = "";
        int code = tail.codePointAt(0);
        code++;
        if (code == 58) code = 65;
        char c = (char) code;
        newTail = "" + c;
        return head + newTail;
    }
    private boolean writeSCSList(String path)
    {
        String writeL = "";

        List<Integer> listI = new ArrayList<Integer>();
        List<String> listS = new ArrayList<String>();
        int n = 0;
        int sc = 0;
        String cs = "";
        while(true)
        {
            sc = this.searchStringList(scsList, "\"dummy\"", n);
            if (sc < 0) break;
            n = sc + 1;
            listI.add(sc);
            listI.add(n);
            cs = scsList.get(n);
            //System.out.println("CCC : " + scsList.get(sc) + " : " + n + "\n  : " + scsList.get(n));
            //System.out.println("VVVV : "+cs.substring(cs.indexOf("<!--")+4, cs.indexOf("-->")));
            listS.add("name=\"" + cs.substring(cs.indexOf("<!--")+4, cs.indexOf("-->")) + "\"");
        }

        List<String> newSCSList = new ArrayList<String>();

        for(int i=0;i<scsList.size();i++)
        {
            if (this.checkInt(listI, i)) continue;
            writeL = scsList.get(i);
            boolean cf = false;
            for(int j=0;j<listS.size();j++)
            {
                if (writeL.indexOf(listS.get(j)) >= 0)
                {
                    cf = true;
                    break;
                }
            }
            if (cf) continue;

            newSCSList.add(writeL);
        }


        FileWriter fw = null;
        /*
        try
        {
            fw = new FileWriter(path + "_1.scs");

            for(int i=0;i<scsList.size();i++)
            {
                if (this.checkInt(listI, i)) continue;
                writeL = scsList.get(i);
                boolean cf = false;
                for(int j=0;j<listS.size();j++)
                {
                    if (writeL.indexOf(listS.get(j)) >= 0)
                    {
                        cf = true;
                        break;
                    }
                }
                if (cf) continue;

                fw.write(writeL + "\n");
                newSCSList.add(writeL);
            }
            //fw.write("");
            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("scs file Writing error (1) : " + ie.getMessage());
            return false;
        }
        */
        scsList = newSCSList;

        newSCSList = new ArrayList<String>();
        List<String> changeNameFrom = new ArrayList<String>();
        List<String> changeNameTo = new ArrayList<String>();

        int arrayNum = 200;
        //int[] stack = new int[arrayNum];
        String[] stackS = new String[arrayNum];
        for (int i=0;i<arrayNum;i++)
        {
            //stack[i] = -1;
            stackS[i] = null;
        }
        //int curr = 0;
        String currS = "0";
        String nameP = "";
        String nameS = "";
        String name = "";
        String space = "";

        int pointer = 0;
        int level = 0;

        for(int i=0;i<scsList.size();i++)
        {

            writeL = scsList.get(i);

            writeL = writeL.trim();
            space = "";
            if (writeL.startsWith("<segment "))
            {
                space = "    ";
                nameP = writeL.substring(writeL.indexOf("name=\"")+6, writeL.indexOf("\" ", writeL.indexOf("name=\"")+6));

                if (nameP.indexOf("_HEAD") > 0) nameP = nameP.replace("_HEAD", "%&%");

                if (nameP.indexOf("_") < 0)
                {
                    if (nameP.indexOf("%&%") > 0) nameP = nameP.replace("%&%", "_HEAD");
                    name = nameP;
                    //stack[pointer] = 0;
                    //curr = 0;
                    stackS[pointer] = "0";
                    currS = "0";

                    pointer++;
                }
                else
                {
                    name = nameP.substring(0, nameP.indexOf("_"));
                    if (name.indexOf("%&%") > 0) name = name.replaceAll("%&%", "_HEAD");
                    if (nameP.indexOf("%&%") > 0) nameP = nameP.replaceAll("%&%", "_HEAD");
                    try
                    {
                        if (stackS[pointer] == null)
                        {
                            //curr = (curr * 10) + 1;
                            if (pointer == 1) currS = "1";//setupSuffix(curr);
                            else currS = currS + "1";
                        }
                        else
                        {
                            //curr = stack[pointer];
                            currS = stackS[pointer];
                            //curr++;
                            currS = addSuffix(currS);
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ae)
                    {
                        System.err.println("ArrayIndexOutOfBoundsException : " + i + " : " + pointer + " : " + writeL + " : " + ae.getMessage());
                        for(int s=0;s<newSCSList.size();s++) System.err.println("SCS : " + newSCSList.get(s));
                        return false;
                    }

                    nameS = name + "_" + currS;
                    if (nameS.trim().endsWith("_"))
                    {
                        System.err.println("Add suffix failure: " + nameS + " : " + i + " : " + pointer + " : " + writeL);
                    }
                    //stack[pointer] = curr;
                    stackS[pointer] = currS;
                    level = pointer;
                    pointer++;

                    for (int j=0;j<level;j++) space = space + "    ";
                    if (!nameP.equals(nameS))
                    {
                        writeL = writeL.replace(nameP, nameS);
                        changeNameFrom.add(nameP);
                        changeNameTo.add(nameS);
                    }
                }
            }
            else if (writeL.startsWith("</segment>"))
            {
                //stack[pointer] = -1;
                stackS[pointer] = null;
                pointer--;
                space = "    ";
                for (int j=0;j<level;j++) space = space + "    ";
                level--;
                for (int j=0;j<changeNameFrom.size();j++)
                {
                    if (writeL.indexOf(changeNameFrom.get(j)) >= 0)
                    {
                        writeL = writeL.replace("<!--"+changeNameFrom.get(j)+"-->", "<!--"+changeNameTo.get(j)+"-->");
                        break;
                    }
                }
            }
            else if (writeL.startsWith("<field "))
            {
                space = "        ";
                for (int j=0;j<level;j++) space = space + "    ";
            }
            newSCSList.add(space + writeL);
        }

        List<String> newCSVList = new ArrayList();
        for(int i=0;i<csvList.size();i++)
        {
            writeL = csvList.get(i);
            for (int j=0;j<changeNameFrom.size();j++)
            {
                if (writeL.indexOf(changeNameFrom.get(j) + ",") >= 0)
                {
                    writeL = writeL.replace(changeNameFrom.get(j)+",", changeNameTo.get(j)+",");
                    break;
                }
            }
            newCSVList.add(writeL);
        }
        csvList = newCSVList;

        //FileWriter fw = null;
        try
        {
            fw = new FileWriter(path + "_1.scs");

            for(int i=0;i<newSCSList.size();i++)
            {
                //if (this.checkInt(listI, i)) continue;
                writeL = newSCSList.get(i);
                //boolean cf = false;
                //for(int j=0;j<listS.size();j++)
                //{
                //    if (writeL.indexOf(listS.get(j)) >= 0)
                //    {
                //        cf = true;
                //        break;
                //    }
                //}
                //if (cf) continue;

                fw.write(writeL + "\r\n");

            }
            //fw.write("");
            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("scs file Writing error (2) : " + ie.getMessage());
            return false;
        }
        return true;
    }
    private boolean writeCSVList(String path)
    {
        String writeL = "";
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(path + "_1.csv");
            for(int i=0;i<csvList.size();i++)
            {
                writeL = csvList.get(i).trim();
                if (writeL.endsWith(",ZZ")) continue;
                fw.write(writeL + "\r\n");
            }

            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("csv file Writing error (2) : " + ie.getMessage());
            return false;
        }
        return true;
    }
    private boolean writeH3SList(String path)
    {
        String writeL = "";
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(path + "_1.h3s");
            for(int i=0;i<h3sList.size();i++)
            {
                writeL = h3sList.get(i);
                fw.write(writeL + "\r\n");
            }
            fw.close();
        }
        catch(IOException ie)
        {
            System.out.println("h3s file Writing error (2) : " + ie.getMessage());
            return false;
        }
        return true;
    }

}


class DataTree
{
    private boolean success = false;
    private String message = "";
    private String header;
    private NodeElement scsHead;
    private NodeElement h3sHead;
    private NodeElement hl7Head;
    private boolean isNewMethod = false;
    //private NodeElement scsCurr;
    //private NodeElement h3sCurr;

    DataTree(String scsPath, String h3sPath, String head)
    {
        header = head;
        if (!buildSCSTree(scsPath)) return;
        if (!buildH3STree(h3sPath)) return;
        success =  true;
    }
    DataTree(String scsPath)
    {
        if (!buildSCSTree(scsPath)) return;
        success =  true;
    }
    DataTree(String scsPath, CommonNode headNode, String head)
    {
        header = head;
        isNewMethod = true;
        if (!buildSCSTree(scsPath)) return;
        if (!buildH3STree(headNode)) return;
        success =  true;
    }

    public boolean checkSuccess(){ return success; }
    public boolean isNewMethod(){ return isNewMethod; }

    private boolean buildSCSTree(String scsFileName)
    {
        boolean res = false;
        try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLReader producer = parser.getXMLReader();
			ContentHandler handler = new SCSEventHandler(this, isNewMethod);

			producer.setContentHandler(handler);

			//producer.parse(new InputSource("file:///" + scsFileName.replaceAll("\\", "/")));
            producer.parse(new InputSource("file:///" + scsFileName));
            res = true;
        }
        catch(org.xml.sax.SAXParseException e)
		{
            //System.out.println("SCStree SAXParseException : " + scsFileName);
            message = "SCStree SAXParseException : " + e.getMessage();
            return false;
        }
        catch(IOException e)
		{
			//System.out.println("SCStree IOException");
            message = "SCStree IOException : " + e.getMessage();
            return false;
        }
		catch(Exception e)
		{
			//e.printStackTrace(System.err);
            message = "SCStree Exception : " + e.getMessage();
            return false;
        }
        //NodeElement tmp = scsHead;
//        while(true)
//        {
//            if (tmp == null) break;
//            System.out.println(this.getSCSNodePath(tmp));
//            tmp = this.getNextNode(tmp);
//        }
        return res;
    }

    private boolean buildH3STree(CommonNode head)
    {
        boolean res = true;
        String mode = "h3s";
        setH3SHead(new NodeElement(mode, "hl7v3meta", "head", "no_uuid", null));

        if (!buildH3STreeRecurrsive(getH3SHead(), head))
        {
            message = "Failure to construct h3s tree with a CommonNode";
            res = false;
        }
        return res;
    }

    private boolean buildH3STreeRecurrsive(NodeElement parent, CommonNode node)
    {
        NodeElement temp = null;
        H3SInstanceMetaSegment segment = null;
        if (node instanceof H3SInstanceMetaSegment)
        {
            segment = (H3SInstanceMetaSegment) node;
            if (segment.getH3SSegmentType().getType() == H3SInstanceSegmentType.CLONE_TYPE)
            {
                temp = new NodeElement("h3s", H3SInstanceSegmentType.CLONE.toString(), segment.getName(), segment.getXPath(), parent);
            }
            else if (segment.getH3SSegmentType().getType() == H3SInstanceSegmentType.ATTRIBUTE_TYPE)
            {
                temp = new NodeElement("h3s", H3SInstanceSegmentType.ATTRIBUTE.toString(), segment.getName(), segment.getXPath(), parent);
            }
        }
        else if (node instanceof MetaField)
        {
            MetaField field = (MetaField) node;
            temp = new NodeElement("h3s", "datatypeField", field.getName(), field.getXPath(), parent);
        }

        if (parent.getLower() == null) parent.setLower(temp);
        else
        {
            NodeElement temp2 = parent.getLower();
            while(temp2.getRight() != null) temp2 = temp2.getRight();
            temp2.setRight(temp);
        }

        if (segment != null)
        {
            List<CommonNode> children = segment.getChildNodes();
            for (int i=0;i<children.size();i++)
            {
                buildH3STreeRecurrsive(temp, children.get(i));
            }
        }
        return true;

    }

    private boolean buildH3STree(String h3sFileName)
    {
        boolean res = false;
        try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLReader producer = parser.getXMLReader();
			ContentHandler handler = new H3SEventHandler(this);

			producer.setContentHandler(handler);

			//producer.parse(new InputSource("file:///" + h3sFileName.replaceAll("\\", "/")));
            producer.parse(new InputSource("file:///" + h3sFileName));
            res = true;
        }
		catch(IOException e)
		{
			System.out.println("H3Stree IOException");
            message = "H3Stree IOException : " + e.getMessage();

        }
		catch(Exception e)
		{
			e.printStackTrace(System.err);
            message = "H3Stree Exception : " + e.getMessage();

        }
//        NodeElement tmp = h3sHead;
//        while(true)
//        {
//            if (tmp == null) break;
//            System.out.println(this.getH3SNodePath(tmp));
//            tmp = this.getNextNode(tmp);
//        }

        return res;
    }

    public boolean buildHL7Tree(String hl7)
    {
        boolean res = false;
        try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLReader producer = parser.getXMLReader();
			ContentHandler handler = new HL7EventHandler(this);

			producer.setContentHandler(handler);

			//producer.parse(new InputSource("file:///" + h3sFileName.replaceAll("\\", "/")));
            InputStream bs = new ByteArrayInputStream(hl7.getBytes());

            producer.parse(new InputSource(bs));
            res = true;
        }
		catch(IOException e)
		{
			System.out.println("HL7tree IOException");
            message = "HL7tree IOException : " + e.getMessage();

        }
		catch(Exception e)
		{
			e.printStackTrace(System.err);
            message = "HL7tree Exception : " + e.getMessage();

        }
//        NodeElement tmp = hl7Head;
//        while(true)
//        {
//            if (tmp == null) break;
//            System.out.println(this.getHL7NodePath(tmp));
//            tmp = this.getNextNode(tmp);
//        }

        return res;
    }

    public String getSCSNodePath(NodeElement node)
    {
        if (node == null) return "null";
        if (node.isHead()) return node.getName();
        NodeElement tmp = node;
        String path = "";
        while(true)
        {
            String nm = tmp.getName();
            if (nm.indexOf("_") < 0) path = "_" + nm + path;
            else if (tmp.getLevel().equals("field")) path = "_" + nm + path;
            else
            {
                while(true)
                {
                    if (nm.endsWith("_"))
                    {
                        path = "_" + path;
                        break;
                    }
                    path = nm.substring(nm.length()-1, nm.length()) + path;
                    nm = nm.substring(0, nm.length()-1);
                }
            }
            if (tmp.getUpper() == null) break;
            tmp = tmp.getUpper();
        }
        return path.substring(1);
    }

    public String getH3SNodePath(NodeElement node)
    {
        if (node == null) return "null";
        if (node.isHead()) return node.getName();
        NodeElement tmp = node;
        String path = "";
        while(true)
        {
            path = "." + tmp.getName() + path;

            if (tmp.getUpper() == null) break;
            tmp = tmp.getUpper();
        }
        return path.substring(1);
    }

    public String getHL7NodePath(NodeElement node)
    {
        if (node == null) return "null";
        if (node.isHead()) return node.getName();
        NodeElement tmp = node;
        String path = "";
        while(true)
        {
            path = "." + tmp.getName() + path;

            if (tmp.getUpper() == null) break;
            tmp = tmp.getUpper();
        }
        if (node.getXmlPath().equals("")) return path.substring(1);
        else return path.substring(1) + " => " + node.getXmlPath();
    }

    public int getDepth(NodeElement curr)
    {
        int n = 0;
        if (curr.isHead()) return 0;
        NodeElement temp = curr;
        while(true)
        {
            n++;
            temp = temp.getUpper();
            if (temp.isHead()) break;
        }
        return n;
    }

    public NodeElement getLastChild(NodeElement curr)
    {
        if (curr.isLeafNode()) return null;
        NodeElement temp = curr.getLower();
        while(true)
        {
            if (temp.isEndRight()) break;
            temp = temp.getRight();
        }
        return temp;
    }

    public int countChildNodes(NodeElement curr)
    {
        if (curr.isLeafNode()) return 0;
        int n = 1;
        NodeElement temp = curr.getLower();
        while(true)
        {
            if (temp.isEndRight()) break;
            temp = temp.getRight();
            n++;
        }
        return n;
    }

    public NodeElement getChildNodeWithName(NodeElement curr, String find)
    {
        if (curr.isLeafNode()) return null;
        NodeElement temp = curr.getLower();
        String tt = "";
        while(true)
        {
            tt = temp.getName();
            //if (find.indexOf("entry") >= 0) System.out.println("QQQQQ : " + tt + ", " + find);
            if (tt.trim().equals(find.trim())) break;
            if (temp.isEndRight()) return null;
            temp = temp.getRight();
        }
        return temp;
    }
    public NodeElement getChildNodeWithNameSimilar(NodeElement curr, String find)
    {
        return getChildNodeWithNameSimilar(curr, find, 0);
    }

    public NodeElement getChildNodeWithNameSimilar(NodeElement curr, String find, int seq)
    {
        NodeElement temp = null;
        if (seq <= 1)
        {
            temp = getChildNodeWithName(curr, find);
            if (temp != null) return temp;
        }
        if (curr.isLeafNode()) return null;
        temp = curr.getLower();
        String tt = "";
        List<NodeElement> li = new ArrayList<NodeElement>();
        while(true)
        {
            tt = temp.getName();
            if (checkNameSimilar(tt.trim(), find.trim())) li.add(temp);
            if (temp.isEndRight()) break;
            temp = temp.getRight();
        }
        if (li.size() == 0) return null;
        if (seq <= 1)
        {
            if (li.size() == 1) return li.get(0);
            String st  = "";
            for (int i=0;i<li.size();i++) st = st + (li.get(i)).getName() + ", ";
            //System.out.println("Several matched child node name on h3s file : " + find + " : " +  st);
            return li.get(0);
        }
        else
        {
            if (li.size() < seq) return null;
            else return li.get(seq-1);
        }
    }

    public boolean checkNameSimilar(String nodeData, String find)
    {
        int pos = 0;
        boolean check = false;
        if (find.length() == 0) return false;
        if (nodeData.length() == 0) return false;
        if (find.length() > nodeData.length()) return false;
        String findU = find.toUpperCase();
        String nodeU = nodeData.toUpperCase();
        String achar1 = nodeU.substring(0,1);
        String achar2 = findU.substring(0,1);
        if (!achar1.equals(achar2)) return false;
        for (int i=0;i<findU.length();i++)
        {
            achar1 = findU.substring(i, i+1);
            if (pos >= nodeU.length()) return false;
            for (int j=pos;j<nodeU.length();j++)
            {
                check = false;
                achar2 = nodeU.substring(j, j+1);
                if (achar1.equals(achar2))
                {
                    check = true;
                    pos = j+1;
                    break;
                }
            }

            if (!check) return false;
        }
        return true;
    }

    public NodeElement getChildNodeWithIndex(NodeElement curr, int idx)
    {
        if (curr.isLeafNode()) return null;
        NodeElement temp = curr.getLower();
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

    public NodeElement getNextNode(NodeElement curr)
    {
        if (curr == null) return null;
        NodeElement temp = curr;

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
                    //if (temp.isHead()) return null;
                    if (temp == null) return null;
                    if (!temp.isEndRight()) return temp.getRight();
                }
            }
        }
    }

    public NodeElement searchSCSPath(String str)
    {
        if (str.startsWith(".")) str = str.substring(1);
        if (str.equals(header)) return scsHead;
        if (!str.startsWith(header)) str = header + "." + str;

        List<String> li = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, ".");
        while(st.hasMoreTokens()) li.add(st.nextToken());
        //String segmentName = "";
        String fieldName = "";

        List<String> li_seg = new ArrayList<String>();
        for(int i=0;i<li.size();i++)
        {
            if (i < (li.size()-2))
            {
                li_seg.add(li.get(i).toUpperCase());
            }
            else
            {
                if (li.get(i).equals("noneX")) {}
                else fieldName = fieldName + "_" + li.get(i);
            }
        }

        NodeElement tmp = scsHead;
        NodeElement tmp2 = null;
        String segName= "";
        for(int i=0;i<li_seg.size();i++)
        {
            segName = li_seg.get(i);
            if (segName.equals(header))
            {
//                tmp = scsHead;
//                continue;

            }
            else segName = segName + "_";
            if (tmp.isLeafNode())
            {
                System.out.println("**** scs Search Failure (meet leaf node)("+segName+") : " + header+ " : " + str);
                return null;
            }
            tmp2 = tmp.getLower();
            boolean found = false;
            while(true)
            {   //System.out.println("**** scs Node Search ("+segName+") : " + tmp.getName()+ " : " + tmp2.getName() + " : " + header+ " : " + str);

                if (tmp2.getName().startsWith(segName))
                {
                    found = true;
                    break;
                }
                if (tmp2.isEndRight()) break;
                tmp2 = tmp2.getRight();
            }
            if (found) tmp = tmp2;
            else
            {
                System.out.println("**** scs Search Failure (Not found)("+segName+") : " + tmp.getName()+ " : " + header+ " : " + str);
                return null;
            }
        }
        if (fieldName.startsWith("_")) fieldName = fieldName.substring(1);
        tmp2 = this.getChildNodeWithName(tmp, fieldName);
        if (tmp2 == null)
        {
            System.out.println("***SCS: Not fount field '" + fieldName + ", at '" + tmp.getName() + "' Node : path => " + segName);
            return null;
        }
        return tmp2;


    }

    public NodeElement searchSCSPath2(String str)
    {
        System.out.println("**** search scs  : " + header+ " : " + str);
        if (str.equals(header)) return scsHead;
        List<String> li = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, ".");
        while(st.hasMoreTokens()) li.add(st.nextToken());
        String segmentName = "";
        String fieldName = "";

        for(int i=0;i<li.size();i++)
        {
            if (i < (li.size()-2))
            {
                segmentName = segmentName + "_" + li.get(i).toUpperCase();
            }
            else
            {
                fieldName = fieldName + "_" + li.get(i);
            }
        }
        /*
        if (seg.equals(header))
        {
            NodeElement tmp2 = this.getChildNodeWithName(scsHead, fieldName);
            if (tmp2 == null)
            {
                System.out.println("***SCS2: Not fount field '" + fieldName + ", at '" + scsHead.getName() + "' Node : path => " + segmentName);
                return null;
            }
            return tmp2;
        }
        */
        segmentName = segmentName.substring(1);
        if (!segmentName.startsWith(header)) segmentName = header + "_" + segmentName;
        String seg = segmentName;
        fieldName = fieldName.substring(1);
        NodeElement tmp = scsHead;
        System.out.println("**** Header : " + header+ " : " + seg);
        while(true)
        {
            int idx = seg.indexOf("_");
            String stx = "";
            if (seg.equals(header))
            {
                stx = seg;
                idx = -1;
            }
            else
            {
                if (idx < 0) stx = seg;
                else
                {
                    stx = seg.substring(0, idx);
                    seg = seg.substring(idx+1);
                }
            }
            NodeElement tmp2 = tmp.getLower();
            while(true)
            {
                if (tmp2.getName().startsWith(stx))
                {
                    tmp = tmp2;
                    break;
                }
                if ((stx.equals(header))&&(tmp == scsHead)) break;
                //else  h
                if (tmp2.isEndRight())
                {   /*
                    if ((stx.equals(header))&&(tmp.getName().equals(header)))
                    //if ((stx.equals(header))&&(tmp == scsHead))
                    {
                        System.out.println("Something wrong : " + segmentName + " : " + str + " : " + scsHead.getName());
                        break;
                    }
                    */
                    System.out.println("***SCS: Not fount segment '" + stx + ", at '" + tmp.getName() +"' (" +header +") Node : path => " + segmentName);
                    idx = -1;
                    tmp = null;
                    break;
                }
                tmp2 = tmp2.getRight();
            }
            if (idx < 0) break;
        }
        if (tmp == null) return null;
        NodeElement tmp2 = this.getChildNodeWithName(tmp, fieldName);
        if (tmp2 == null)
        {
            System.out.println("***SCS: Not fount field '" + fieldName + ", at '" + tmp.getName() + "' Node : path => " + segmentName);
            return null;
        }
        return tmp2;
    }

    public NodeElement searchH3SPath(String str)
    {
        return searchBothPath(str, h3sHead);
    }

    public NodeElement searchHL7Path(String str)
    {
        return searchBothPath(str, hl7Head);
    }

    public NodeElement searchBothPath(String str, NodeElement headNode)
    {
        if (str.startsWith(".")) str = str.substring(1);
        if (!str.startsWith(header)) str = header + "." + str;
        //System.out.println("Search : " + str);
        List<String> li = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, ".");
        while(st.hasMoreTokens()) li.add(st.nextToken());
        if (li.get(li.size()-1).indexOf("_") > 0)
        {
            String stt = li.get(li.size()-1);
            li.remove(li.size()-1);
            String st1 = "";
            String st2 = "";
            while(true)
            {
                st1 = stt.substring(0, stt.indexOf("_"));
                st2 = stt.substring(stt.indexOf("_")+1);
                li.add(st1);
                if (st2.equals("in"))
                {
                    li.add("inlineText");
                    break;
                }
                else if (st2.equals("val"))
                {
                    li.add("value");
                    break;
                }
                else
                {
                    if (st2.indexOf("_") > 0)
                    {
                        stt = st2;
                    }
                    else
                    {
                        li.add(st2);
                        break;
                    }
                }
            }
        }
        //String segmentName = "";
        //String fieldName = "";
        NodeElement curr = headNode;
        if (isNewMethod) curr = headNode.getLower();
        NodeElement temp = null;
        String search = "";

        for(int i=0;i<li.size();i++)
        {
            String ser = li.get(i);
            if (i==0)
            {
                int idx = ser.indexOf("_HEAD");
                if (idx > 0) ser = ser.substring(0, idx);
            }
            int seq = 0;
            if (curr.isLeafNode())
            {
                System.out.println("This is already a leaf node : " + search + " : " + curr.getLevel() + ", " + curr.getName() + "\n  **str : " + str + "\n  **pth : " + this.getH3SNodePath(curr));
                return null;
            }

            temp = curr.getLower();
            while(true)
            {
                String nodeName = temp.getName();
                if (nodeName.equals(ser)) break;
                if (temp.getRight() == null)
                {
                    temp = null;
                    break;
                }
                temp = temp.getRight();
            }
            if (temp != null)
            {
                curr = temp;
                continue;
            }

            if (ser.equals("pert21")) temp = this.getChildNodeWithNameSimilar(curr, "perti2");
            else if (ser.equals("pert22")) temp = this.getChildNodeWithNameSimilar(curr, "perti2", 2);
            else if (ser.equals("pert23")) temp = this.getChildNodeWithNameSimilar(curr, "perti2", 3);
            else if (ser.equals("pert24")) temp = this.getChildNodeWithNameSimilar(curr, "perti2", 4);
            else if (ser.equals("pert25")) temp = this.getChildNodeWithNameSimilar(curr, "perti2", 5);
            else if (ser.equals("pert11")) temp = this.getChildNodeWithNameSimilar(curr, "perti1");
            else if (ser.equals("pert12")) temp = this.getChildNodeWithNameSimilar(curr, "perti1", 2);
            else if (ser.equals("pert13")) temp = this.getChildNodeWithNameSimilar(curr, "perti1", 3);
            else if (ser.equals("pert14")) temp = this.getChildNodeWithNameSimilar(curr, "perti1", 4);
            else if (ser.equals("pert15")) temp = this.getChildNodeWithNameSimilar(curr, "perti1", 5);
            else if (ser.equals("subIngredient1")) temp = this.getChildNodeWithNameSimilar(curr, "subIngredient");
            else if (ser.equals("subIngredient2")) temp = this.getChildNodeWithNameSimilar(curr, "subIngredient", 2);
            else if (ser.indexOf("XX") > 0)
            {
                int idx = ser.indexOf("XX");
                int in = 0;
                String ser1 = ser.substring(0, idx);
                String ser2 = ser.substring(idx);
                if (ser2.equals("XX1")) in = 0;
                else if (ser2.equals("XX2")) in = 2;
                else if (ser2.equals("XX3")) in = 3;
                else if (ser2.equals("XX4")) in = 4;
                else if (ser2.equals("XX5")) in = 5;
                else if (ser2.equals("XX6")) in = 6;
                else if (ser2.equals("XX7")) in = 7;
                else if (ser2.equals("XX8")) in = 8;
                else if (ser2.equals("XX9")) in = 9;
                else if (ser2.equals("XX10")) in = 10;

                temp = this.getChildNodeWithNameSimilar(curr, ser1, in);
            }
            else
            {
                if (ser.startsWith("pertinentInformation")) search = ser;
                else if (ser.startsWith("component")) search = ser;
                else if (ser.startsWith("pert1")) search = ser;
                else if (ser.startsWith("pert2")) search = ser;
                else if (ser.startsWith("pert3")) search = ser;
                else if (ser.startsWith("pert4")) search = ser;
                else if (ser.startsWith("pert5")) search = ser;
                else if (ser.startsWith("pert6")) search = ser;
                else if (ser.startsWith("pert7")) search = ser;
                else if (ser.startsWith("pert8")) search = ser;
                else if (ser.startsWith("pert9")) search = ser;
                else if (ser.startsWith("pert10")) search = ser;
                else if (ser.startsWith("perti1")) search = ser;
                else if (ser.startsWith("perti2")) search = ser;
                else if (ser.startsWith("perti3")) search = ser;
                else if (ser.startsWith("perti4")) search = ser;
                else if (ser.startsWith("perti5")) search = ser;
                else if (ser.startsWith("perti6")) search = ser;
                else if (ser.startsWith("perti7")) search = ser;
                else if (ser.startsWith("perti8")) search = ser;
                else if (ser.startsWith("perti9")) search = ser;
                else if (ser.startsWith("perti10")) search = ser;
                else if (ser.startsWith("subjectOf1")) search = ser;
                else if (ser.startsWith("subjectOf2")) search = ser;
                else if (ser.startsWith("subjectOf3")) search = ser;
                else if (ser.startsWith("subjectOf4")) search = ser;
                else if (ser.startsWith("subjectOf5")) search = ser;
                else if (ser.startsWith("subjectOf6")) search = ser;
                else if (ser.startsWith("subjectOf7")) search = ser;
                else if (ser.startsWith("subjectOf8")) search = ser;
                else if (ser.startsWith("subjectOf9")) search = ser;
                else if (ser.startsWith("subjectOf10")) search = ser;
                else
                {
                    String achar = "";
                    search = "";
                    for (int j=0;j<ser.length();j++)
                    {
                        achar = ser.substring(j, j+1);
                        if (achar.equals("0")) continue;
                        if (achar.equals("1")) continue;
                        if (achar.equals("2")) continue;
                        if (achar.equals("3")) continue;
                        if (achar.equals("4")) continue;
                        if (achar.equals("5")) continue;
                        if (achar.equals("6")) continue;
                        if (achar.equals("7")) continue;
                        if (achar.equals("8")) continue;
                        if (achar.equals("9")) continue;
                        search = search + achar;
                    }
                }
                temp = this.getChildNodeWithNameSimilar(curr, search);
            }
            if (temp != null) curr = temp;
            else
            {
                System.err.println("Not found search : " + ser + " ; " + curr.getLevel() + ", " + curr.getName() + ", " + curr.getLower().getName() + ", "/* + curr.getLower().getLower().getName()*/ +"\n  **str : " + str  + "\n  **pth : " + this.getH3SNodePath(curr));
                return null;
            }
        }
        return curr;
    }

    public NodeElement getSCSHead() { return scsHead; }
    public NodeElement getH3SHead() { return h3sHead; }
    public NodeElement getHL7Head() { return hl7Head; }
    public void setSCSHead(NodeElement nd) { scsHead = nd; }
    public void setH3SHead(NodeElement nd) { h3sHead = nd; }
    public void setHL7Head(NodeElement nd) { hl7Head = nd; }
    public String getErrorMessage() { return message; }
}


class SCSEventHandler extends DefaultHandler
{
    String currentElement = "";
    String currentPath = "";
    NodeElement head;
    NodeElement curr;
    NodeElement temp;
    String mode = "scs";
    boolean success = false;
    boolean isNewMethod = false;
    int errCount = 0;
    DataTree tree;

    SCSEventHandler(DataTree dTree, boolean isNewMethod)
    {
        super();
        tree = dTree;
        this.isNewMethod = isNewMethod;
    }

    public void startDocument()
    {
        //System.out.println("&&&Start Parsing!! ----------------------------");
        //currentPath = "Document";
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        //System.out.println("Start Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //for(int i=0;i<atts.getLength();i++)
        //{
        //    System.out.println("  LocalName : " + atts.getLocalName(i) + ", QName : " + atts.getQName(i) + ", Value : " + atts.getValue(i) +  ", URI : " + atts.getURI(i));
        //}
        //currentPath = currentPath + "." + qName;
        //currentElement = qName;
        String id = "uuid";
        if (isNewMethod) id = "xmlPath";
        if (qName.equals("csvMetadata"))
        {
            head = new NodeElement(mode, qName, "head", atts.getValue(id), null);
            tree.setSCSHead(head);
            curr = head;
        }
        else if (qName.equals("segment"))
        {

            temp = new NodeElement(mode, qName, atts.getValue("name"), atts.getValue(id), curr);
            if (curr.isLeafNode()) curr.setLower(temp);
            else (tree.getLastChild(curr)).setRight(temp);
            curr = temp;

        }
        else if (qName.equals("field"))
        {
            temp = new NodeElement(mode, qName, atts.getValue("name"), atts.getValue(id), curr);
            if (curr.isLeafNode()) curr.setLower(temp);
            else (tree.getLastChild(curr)).setRight(temp);
        }
        else
        {
            errCount++;
            System.out.println("Invalid qName for scs Tree : " + qName);
        }

    }
    public void characters(char[] cha, int start, int length)
    {
        errCount++;
        String chars = new String(cha);
		String st = "";
		for(int i=start;i<(start+length);i++) st = st + cha[i];
        String trimed = st.trim();
        if (trimed.equals("")) return;
        //System.out.println("Invalid inLine text for scs Tree : " + trimed);
        //int len = trimed.length();
        //if (len < 1000) System.out.println("***** Path : " + currentPath + ", content : " + trimed + ", start : " + start + ", length : " + length);
        //else System.out.println("$$$$$ Path : " + currentPath + ", length : " + len + ", content : " + chars.substring(0,200));
    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        //System.out.println("End Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //currentPath = currentPath.substring(0, (currentPath.length() - (qName.length() + 1)));
        if (qName.equals("csvMetadata"))
        {
            if (curr != head)
            {
                errCount++;
                System.out.println("This scs File is not well-formed : 1 " + curr.getName()+ " : " + curr.getMode()  + " : " + curr.getLevel() + " : " + head.getName()+ " : " + head.getMode()  + " : " + head.getLevel());
            }
//            curr = curr.getUpper();
//            if (curr != head)
//            {
//                errCount++;
//                System.out.println("This scs File is not well-formed : 2");// + curr.getName()+ " : " + curr.getMode()  + " : " + curr.getLevel());
//            }
        }
        else if (qName.equals("segment"))
        {
            if (curr == head)
            {
                errCount++;
                System.out.println("This scs File is not well-formed : 2 " + curr.getName()+ " : " + curr.getMode()  + " : " + curr.getLevel());
            }
            if (!curr.getLevel().equals(qName))
            {
                errCount++;
                System.out.println("This scs File is not well-formed : 3 " + curr.getName()+ " : " + curr.getMode()  + " : " + curr.getLevel());
            }
            curr = curr.getUpper();
        }
        else if (qName.equals("field"))
        {

        }
        else
        {
            errCount++;
            System.out.println("Invalid qName for scs Tree (Tail) : " + qName);
        }
    }
    public void endDocument()
    {

    }

    public NodeElement getHead() { return head; }

    public boolean checkScuuess()
    {
        if (errCount == 0) return true;
        else return false;
    }
}

class H3SEventHandler extends DefaultHandler
{
    String currentElement = "";
    String currentPath = "";
    NodeElement head;
    NodeElement curr;
    NodeElement temp;
    String mode = "h3s";
    boolean success = false;
    int errCount = 0;
    DataTree tree;

    H3SEventHandler(DataTree dTree)
    {
        super();
        tree = dTree;
    }

    public void startDocument()
    {
        //System.out.println("&&&Start Parsing!! ----------------------------");
        //currentPath = "Document";
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        //System.out.println("Start Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //for(int i=0;i<atts.getLength();i++)
        //{
        //    System.out.println("  LocalName : " + atts.getLocalName(i) + ", QName : " + atts.getQName(i) + ", Value : " + atts.getValue(i) +  ", URI : " + atts.getURI(i));
        //}
        //currentPath = currentPath + "." + qName;
        //currentElement = qName;
        if (qName.equals("hl7v3meta"))
        {
            head = new NodeElement(mode, qName, "head", "no_uuid", null);
            tree.setH3SHead(head);
            curr = head;
        }
        else if (qName.equals("clone"))
        {

            temp = new NodeElement(mode, qName, atts.getValue("clonename"), atts.getValue("uuid"), curr);
            if (curr.isLeafNode()) curr.setLower(temp);
            else (tree.getLastChild(curr)).setRight(temp);
            curr = temp;

        }
        else if (qName.equals("attribute"))
        {
            temp = new NodeElement(mode, qName, atts.getValue("name"), atts.getValue("uuid"), curr);
            if (curr.isLeafNode()) curr.setLower(temp);
            else (tree.getLastChild(curr)).setRight(temp);
            curr = temp;
        }
        else if (qName.equals("datatypeField"))
        {
            temp = new NodeElement(mode, qName, atts.getValue("name"), atts.getValue("uuid"), curr);
            if (curr.isLeafNode()) curr.setLower(temp);
            else (tree.getLastChild(curr)).setRight(temp);
        }
        else
        {
            errCount++;
            System.out.println("Invalid qName for h3s Tree : " + qName);
        }

    }
    public void characters(char[] cha, int start, int length)
    {

        errCount++;
        String chars = new String(cha);
		String st = "";
		for(int i=start;i<(start+length);i++) st = st + cha[i];
        String trimed = st.trim();

        if (trimed.equals("")) return;
        //System.out.println("Invalid inLine text for h3s Tree : " + trimed);
        //int len = trimed.length();
        //if (len < 1000) System.out.println("***** Path : " + currentPath + ", content : " + trimed + ", start : " + start + ", length : " + length);
        //else System.out.println("$$$$$ Path : " + currentPath + ", length : " + len + ", content : " + chars.substring(0,200));
    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        //System.out.println("End Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //currentPath = currentPath.substring(0, (currentPath.length() - (qName.length() + 1)));
        NodeElement before = curr;
        if (qName.equals("hl7v3meta"))
        {
            if (curr != head)
            {
                errCount++;
                System.out.println("This h3s File is not well-formed : 1 " + curr.getName() + " : " + curr.getLevel() + ", " + qName);
            }
//            else
//            {
//                curr = curr.getUpper();
//                if (curr != head)
//                {
//                    errCount++;
//                    System.out.println("This h3s File is not well-formed : 2 " + curr.getName() + " : " + curr.getLevel() + ", " + qName + " : " + before.getName() + " , " + before.getLevel());
//                }
//            }
        }
        else if (qName.equals("attribute"))
        {
            if (curr == head)
            {
                errCount++;
                System.out.println("This h3s File is not well-formed : 3 " + curr.getName() + " : " + curr.getLevel() + ", " + qName);
            }
            if (!(curr.getLevel()).equals(qName))
            {
                errCount++;
                System.out.println("This h3s File is not well-formed : 4 , " + curr.getName() + " : " + curr.getLevel() + ", " + qName + " : " + before.getName() + " , " + before.getLevel());
            }
            curr = curr.getUpper();

        }
        else if (qName.equals("clone"))
        {
            if (curr == head)
            {
                errCount++;
                System.out.println("This h3s File is not well-formed : 5" + curr.getName() + " : " + curr.getLevel() + ", " + qName);
            }

            if (!(curr.getLevel()).equals(qName))
            {
                errCount++;
                System.out.println("This h3s File is not well-formed : 6 , " + curr.getName() + " : " + curr.getLevel() + ", " + qName + " : " + before.getName() + " , " + before.getLevel());
            }
            curr = curr.getUpper();
        }
        else if (qName.equals("datatypeField"))
        {

        }
        else
        {
            errCount++;
            System.out.println("Invalid qName for scs Tree (Tail) : " + qName);
        }
    }
    public void endDocument()
    {

    }

    public NodeElement getHead() { return head; }

    public boolean checkScuuess()
    {
        if (errCount == 0) return true;
        else return false;
    }
}

class HL7EventHandler extends DefaultHandler
{
    String currentElement = "";
    String currentPath = "";
    NodeElement head;
    NodeElement curr;
    NodeElement temp;
    String mode = "hl7";
    boolean success = false;
    int errCount = 0;
    DataTree tree;

    HL7EventHandler(DataTree dTree)
    {
        super();
        tree = dTree;
    }

    public void startDocument()
    {
        //System.out.println("&&&Start Parsing!! ----------------------------");
        //currentPath = "Document";
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        //System.out.println("Start Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //for(int i=0;i<atts.getLength();i++)
        //{
        //    System.out.println("  LocalName : " + atts.getLocalName(i) + ", QName : " + atts.getQName(i) + ", Value : " + atts.getValue(i) +  ", URI : " + atts.getURI(i));
        //}
        //currentPath = currentPath + "." + qName;
        //currentElement = qName;
        if (head == null)
        {
            String st = qName;
            if (qName.indexOf(".") > 0) st = qName.substring(qName.indexOf(".") +1);
            head = new NodeElement(mode, "head", "H", "", null);

            temp = new NodeElement(mode, "element", st, "", head);
            head.setLower(temp);
            tree.setHL7Head(head);
            curr = temp;
        }
        else
        {
            temp = new NodeElement("mode", "element", qName, "", curr);
            //tree.setHL7Head(head);
            if (curr.isLeafNode())
            {
                curr.setLower(temp);
            }
            else
            {
                NodeElement tmp = curr.getLower();
                while(!tmp.isEndRight()) tmp = tmp.getRight();
                tmp.setRight(temp);
            }
            curr = temp;
            for(int i=0;i<atts.getLength();i++)
            {
                temp = new NodeElement(mode, "attribute", atts.getQName(i), atts.getValue(i), curr);
                if (curr.isLeafNode())
                {
                    curr.setLower(temp);
                }
                else
                {
                    NodeElement tmp = curr.getLower();
                    while(!tmp.isEndRight()) tmp = tmp.getRight();
                    tmp.setRight(temp);
                }
            }
        }

    }
    public void characters(char[] cha, int start, int length)
    {
        String chars = new String(cha);
		String st = "";
		for(int i=start;i<(start+length);i++) st = st + cha[i];
        String trimed = st.trim();

        if (trimed.equals("")) return;
        temp = new NodeElement(mode, "inlineText", "inlineText", trimed, curr);
        if (curr.isLeafNode())
        {
            curr.setLower(temp);
        }
        else
        {
            NodeElement tmp = curr.getLower();
            while(!tmp.isEndRight()) tmp = tmp.getRight();
            tmp.setRight(temp);
        }

    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        //System.out.println("End Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //currentPath = currentPath.substring(0, (currentPath.length() - (qName.length() + 1)));
        if(!qName.equals(curr.getName())) System.out.println("** not matched end element name : "+ qName + " : " + curr.getName());
        curr = curr.getUpper();
    }
    public void endDocument()
    {

    }

    public NodeElement getHead() { return head; }

    public boolean checkScuuess()
    {
        if (errCount == 0) return true;
        else return false;
    }
}

class FunctionItemList
{
    private List<String> listDefault = null;
    private List<String> listFunction = null;
    FunctionItemList()
    {
        List<String> list = new ArrayList<String>();
        // default
        list.add("use");
        list.add("lowClosed");
        list.add("highClosed");
        list.add("codeSystem");
        list.add("codeSystemName");
        list.add("root");
        list.add("assigningAuthorityName");
        listDefault = list;


        list = new ArrayList<String>();
        // function
        list.add("contextConductionInd");
        list.add("statusCode");
        list.add("contextControlCode");
        list.add("determinerCode");
        list.add("moodCode");
        list.add("classCode");
        list.add("typeCode");
        list.add("negationInd");

        listFunction = list;
    }
    /*
    private String cutString(String st)
    {
        if (st.indexOf(".") < 0) return st;
        StringTokenizer token = new StringTokenizer(st, ".");

        String s1 = "";
        String s2 = "";
        while(token.hasMoreTokens())
        {
            s1 = s2;
            s2 = token.nextToken();
        }
        return s1 + "." + s2;
    }
    */
    private boolean checkString(String st, String sc)
    {
        if (sc.equals("")) return false;
        if (st.indexOf("_") > 0) st = st.replace("_", ".");
        if (st.indexOf(".") < 0) return false;
        StringTokenizer token = new StringTokenizer(st, ".");

        boolean t = false;
        while(token.hasMoreTokens())
        {
            if (sc.equals(token.nextToken())) t = true;
        }
        return t;
    }

    public boolean isDefault(String st)
    {
        //st = cutString(st);

        boolean res = false;

        for (int i=0;i<listDefault.size();i++)
        {

            if (checkString(st, listDefault.get(i)))
            {
                res = true;
                //System.out.println("is Default : " + st + " : " + listDefault.get(i));
                break;
            }
        }
        return res;
    }

    public boolean isFunction(String st)
    {
        //st = cutString(st);

        boolean res = false;

        for (int i=0;i<listFunction.size();i++)
        {
            if (checkString(st, listFunction.get(i)))
            {
                res = true;
                //System.out.println("is Function : " + st + " : " + listFunction.get(i));
                break;
            }
        }
        return res;
    }

    public boolean checkChar(String st)
    {
        if (isFunction(st)) return true;
        return isDefault(st);
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.7  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/03/26 14:43:30  umkis
 * HISTORY      : Re-assigning sortkey
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/03/20 03:49:26  umkis
 * HISTORY      : for re-assigning sort key to mif files
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/17 01:11:38  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/06 14:27:43  umkis
 * HISTORY      : upgrade test instance generator
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/03 05:01:32  umkis
 * HISTORY      : add items which have to be input data
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 16:29:40  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 15:43:55  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:12:59  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
