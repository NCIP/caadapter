/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2Converter.java,v 1.1 2007-07-03 19:32:58 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The HL7 SDK Software License, Version 1.0
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

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.ElementNode;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.SegmentedCSVParserImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.FunctionUtil;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since HL7 SDK v3.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 19:32:58 $
 */
public class V2Converter
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2Converter.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2Converter.java,v 1.1 2007-07-03 19:32:58 wangeug Exp $";


    HL7V2MessageTree messageTree = null;
    HL7V2MessageTree messageTreeEmpty = null;
    String dataPathOfV2Meta;
    String messageType = "";
    String errorMessage = "";
    boolean wasSuccessful = false;
    String scsFileName = "";
    String csvFileName = "";
    String targetSCSFileName = "";
    boolean isSCSValid = false;
    boolean isCSVValid = false;
    List<String> validationMessages;
    String markingOBXGrouping = "TTTTT_OBX_Grouping_On";
    String defaultOBXDataType = "ST";
    ValidatorResults resultValidationSCS = null;
    ValidatorResults resultValidationCSV = null;

    public V2Converter(HL7V2MessageTree messTree) throws HL7MessageTreeException
    {
        if (messTree == null) throw new HL7MessageTreeException("This Message Tree is null.");
        ElementNode head = messTree.getHeadNode();
        if (head == null) throw new HL7MessageTreeException("The head node is null.");
        if (messTree.isTreeEmpty()) throw new HL7MessageTreeException("This v2 message tree is empty.");
        messageTreeEmpty = new HL7V2MessageTree(messTree.getDataPath(), messTree.getVersion(), messTree.getMessageType());
        dataPathOfV2Meta = messTree.getDataPath();
        messageTree = messTree;
        messageType = messageTree.getMessageType();
    }

    public V2Converter(String dataPath) throws HL7MessageTreeException
    {
        messageTree = buildV2MessageTree(dataPath, "2.5", "ADT^A03");
        dataPathOfV2Meta = dataPath;
        messageTree = null;
    }

    public V2Converter(String v2FileName, String dataPath) throws HL7MessageTreeException
    {
        messageTree = buildV2MessageTree(dataPath, "2.5", v2FileName);
        messageType = messageTree.getMessageType();
        messageTreeEmpty = new HL7V2MessageTree(messageTree.getDataPath(), messageTree.getVersion(), messageType);
        dataPathOfV2Meta = dataPath;
    }

    public V2Converter(String messageType, String version, String dataPath) throws HL7MessageTreeException
    {
        messageTree = null;
        messageTreeEmpty = new HL7V2MessageTree(dataPath, version, messageType);
        dataPathOfV2Meta = dataPath;
    }

    private HL7V2MessageTree buildV2MessageTree(String dataPath, String version, String v2FileName) throws HL7MessageTreeException
    {
        return new HL7V2MessageTree(dataPath, version, v2FileName);
    }

    public void process(String outSCS, String outCSV, boolean filteringNonDataSegment, boolean groupingYesOrNo, List<String> listDataTypeOfOBX, String scsTargetFileName)
    {
        wasSuccessful = false;
        validationMessages = new ArrayList<String>();


        boolean checkYesOrNoForGeneratingSCS = false;
        if (outSCS == null) outSCS = "";
        if (outCSV == null) outCSV = "";
        if (!outSCS.equals(""))
        {
            if (messageTreeEmpty == null)
            {
                errorMessage = "ERR901 : Empty Message Tree object is not built for generating SCS.";
                return;
            }
            checkYesOrNoForGeneratingSCS = true;
        }
        if (!outCSV.equals(""))
        {
            if (messageType == null)
            {
                errorMessage = "ERR902 : Message Tree object is not built for generating CSV.";
                return;
            }
        }
        if ((filteringNonDataSegment)&&(messageType == null))
        {
            errorMessage = "ERR903 : Invalid Filtering Non-Data Segment option (Message Tree is null.)";
            return;
        }
        if (listDataTypeOfOBX == null) listDataTypeOfOBX = new ArrayList<String>();


        // validate scsTargetFileName
        if (scsTargetFileName == null) scsTargetFileName = "";
        if ((!checkYesOrNoForGeneratingSCS)&&(scsTargetFileName.trim().equals("")))
        {
            errorMessage = "ERR001 : Validating SCS file must be given when CSV file generating only.";
            return;
        }
        if ((!checkYesOrNoForGeneratingSCS)&&(!scsTargetFileName.trim().equals("")))
        {
            File tempFile = new File(scsTargetFileName);
            if (!tempFile.exists())
            {
                errorMessage = "ERR002 : This target scs file is not exist. : " + scsTargetFileName;
                return;
            }
            else
            {
                if (!tempFile.isFile())
                {
                    errorMessage = "ERR003 : This target scs file is not a file. : " + scsTargetFileName;
                    return;
                }
                else
                {
                    ValidatorResults validatorResults = validateSpecification(new File(scsTargetFileName));
                    if (!validatorResults.isValid())
                    {
                        errorMessage = "ERR004 : This target scs file is invalid. : " + scsTargetFileName;
                        collectValidateResultMessages("scs", validatorResults);
                        for (int i=0;i<validationMessages.size();i++) errorMessage = errorMessage + "\n   " + validationMessages.get(i);
                        return;
                    }
                }
            }
        }
        targetSCSFileName = scsTargetFileName;
        // validate scsTargetFileName end

        if (!validateOutputPath(outSCS, "scs")) return;
        if (!validateOutputPath(outCSV, "csv")) return;

        // validate output path end



        if (checkYesOrNoForGeneratingSCS)
        {
            if (!outSCS.equals("")) generateSCSFile(outSCS, filteringNonDataSegment, listDataTypeOfOBX, groupingYesOrNo);
            if (!wasSuccessful)
            {
                if (errorMessage.equals("")) errorMessage = "Unidentified error during SCS generating";
                return;
            }
        }

        if (!outCSV.equals(""))
        {
            wasSuccessful = false;
            if (outSCS.equals("")) generateCSVFile(outCSV, scsTargetFileName);
            else generateCSVFile(outCSV, outSCS);
            if (!wasSuccessful)
            {
                if (errorMessage.equals("")) errorMessage = "Unidentified error during CSV generating";
                return;
            }
        }

        wasSuccessful = false;

        String tempFileName = "";
        ValidatorResults validatorResults = null;
        if (!outSCS.equals(""))
        {
            tempFileName = scsFileName;
            validatorResults = validateSpecification(new File(tempFileName));
            resultValidationSCS = validatorResults;
            if (validatorResults.isValid())
            {
                //System.out.println("Valid SCS file : " + tempFileName);
                isSCSValid = true;
            }
            else
            {
                //System.out.println("Invalid SCS file : " + tempFileName);
                collectValidateResultMessages("scs", validatorResults);
                isSCSValid = false;
            }
        }
        else tempFileName = targetSCSFileName;

        if (!outCSV.equals(""))
        {
            validatorResults = validateDataAgainstSpecification(new File(tempFileName), new File(csvFileName));
            resultValidationCSV = validatorResults;
            if (validatorResults.isValid())
            {
                //System.out.println("Valid CSV file ("+csvFileName+") against SCS file : " + tempFileName);
                isCSVValid = true;
            }
            else
            {
                //System.out.println("Invalid CSV file ("+csvFileName+") against SCS file : " + tempFileName);
                collectValidateResultMessages("csv", validatorResults);
                isCSVValid = false;
            }
        }
        wasSuccessful = true;
        errorMessage = "";
    }

    private void generateSCSFile(String pathSCS, boolean filteringNonDataSegment, List<String> list, boolean groupingYesOrNo)
    {
        int numberOfOBXSegment = 0;
        wasSuccessful = false;
        ElementNode temp = null;

        List<String> listDataTypeOfOBX = null;
        if (groupingYesOrNo)
        {
            listDataTypeOfOBX = new ArrayList<String>();
            boolean cTag = false;
            for (int i=0;i<list.size();i++)
            {
                String dt = list.get(i).trim();
                if ((dt.equals("ST"))||(dt.equals("FT"))||(dt.equals("TX")))
                {
                    if (!cTag)
                    {
                        listDataTypeOfOBX.add(defaultOBXDataType);
                        cTag = true;
                    }
                }
                else listDataTypeOfOBX.add(dt);
            }
        }
        else listDataTypeOfOBX = list;

        // collecting the addresses of OBX segments from the initial empty tree
        List<ElementNode> listElementNode = new ArrayList<ElementNode>();
        while(true)
        {
            String prefix = "";
            if (numberOfOBXSegment == 0) prefix = "";
            else prefix = "" + (numberOfOBXSegment+1);
            try
            {
                listElementNode.add(messageTreeEmpty.nodeAddressSearch(prefix + "OBX.1.2"));
                numberOfOBXSegment++;
            }
            catch(HL7MessageTreeException hhe)
            {
                //System.out.println("CCV : "+hhe.getMessage());  //ERR138
                break;
            }
        }

        // build various OBX segment from listDataTypeOfOBX
        for (int k=0;k<listElementNode.size();k++)
        {
            String prefix = "" + (k+1);
            if ((listDataTypeOfOBX == null)||(listDataTypeOfOBX.size() == 0))
            {
                try
                {
                    messageTreeEmpty.setNodeValueInGeneratingWithException(listElementNode.get(k), defaultOBXDataType);
                }
                catch(HL7MessageTreeException hhe)
                {
                    errorMessage = "ERR031 : Failure of Default Data Type assigning into an OBX-2 : " + hhe.getMessage();
                    return;
                }
            }
            else
            {
                ElementNode temp1 = listElementNode.get(k);
                //System.out.println("10 : "+ k + " : " + messageTreeEmpty.findNodeAddress(temp1));
                for(int i=0;i<listDataTypeOfOBX.size();i++)
                {
                //System.out.println("11");
                    int m = 0;
                    try
                    {
                        if (i == 0)
                        {
                            messageTreeEmpty.setNodeValueInGeneratingWithException(temp1, listDataTypeOfOBX.get(i));
                            continue;
                        }
                        m = 1;
                        messageTreeEmpty.makeNewSubTree(temp1.getUpperLink());
                        m = 2;
                        messageTreeEmpty.setNodeValueInGeneratingWithException(messageTreeEmpty.nodeAddressSearch(prefix + "OBX." + (i+1) + ".2"), listDataTypeOfOBX.get(i));
                    }
                    catch(HL7MessageTreeException hhe)
                    {
                        if (m==0) errorMessage = "ERR033 : Failure to set initial '"+listDataTypeOfOBX.get(i)+"' daya type into an OBX-2 : " + hhe.getMessage();
                        if (m==1) errorMessage = "ERR035 : Failure to build a new sub structure for OBX segment ("+i+") : " + hhe.getMessage();
                        if (m==2) errorMessage = "ERR037 : Failure to set '"+listDataTypeOfOBX.get(i)+"' daya type into an OBX-2 ("+i+") : " + hhe.getMessage();
                        return;
                    }
                }
            }
        }


        temp = messageTreeEmpty.getHeadNode();
        String scsContent = "";
        String nextUUID = "";
        String segName = "";
        int seq = 0;

        String headSegmentTail = "";
        String beforeSegmentTail = "";
        String markingExistDataNode = "";
        boolean markingOBXGroupingFinish = false;
        while(true)//(temp!=null)
        {
            markingExistDataNode = "";
            //System.out.println("101 : ");
            try
            {
                if (messageTree.hasValue(messageTree.nodeAddressSearch(messageTreeEmpty.findNodeAddress(temp)))) markingExistDataNode = "";
            }
            catch(HL7MessageTreeException he)
            {
                markingExistDataNode = "";
            }


            seq = 0;
            String level = temp.getLevel();

            //System.out.println(level + "." + messageTreeEmpty.getNodeSequence(temp) + "." + temp.getType() + "." + setupDesc(temp.getDescription()) + "." + temp.getValue());
            if (level.equals("message"))
            {
                String type = temp.getType().replace("^", "_");
                //nextUUID = "$$$" + UUIDGenerator.getUniqueString() + "@@@";
                scsContent = "    <segment name=\"" + type + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
                headSegmentTail = "        <field column=\"1\" name=\""+setupDesc(temp.getDescription())+ "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n" +
                                  "    </segment> <!--" + type + "-->\n";
            }
            if (level.equals("segment"))
            {
                if (filteringNonDataSegment)
                {
                    ElementNode nodeAddress = null;
                    try
                    {
                        nodeAddress = messageTree.nodeAddressSearch(messageTreeEmpty.findNodeAddress(temp));
                    }
                    catch(HL7MessageTreeException hhe)
                    {
                        errorMessage = "ERR011 : Node address search Error : " + hhe.getMessage();
                        return;
                    }
                    if (messageTree.hasValue(nodeAddress))
                    {
                        markingExistDataNode = "";

                        scsContent = scsContent + beforeSegmentTail;
                        //segmentTag = true;
                        //fieldTag = false;
                        int prefix = messageTreeEmpty.getSegmentPrefix(temp);
                        if (prefix == 1) segName = temp.getType();
                        else segName = "V" + prefix + temp.getType();

                        String markingOBXGroupingLine = "";
                        if (temp.getType().equals("OBX"))
                        {
                            ElementNode temp2 = temp.getLowerLink().getRightLink();
                            segName = segName + "_" + temp2.getValue();
                            if(!markingOBXGroupingFinish)
                            {
                                markingOBXGroupingLine = "            <field column=\"2\" name=\"S_"+markingOBXGrouping+ "_DoNotDeleteOrChangeThis\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
                                markingOBXGroupingFinish = true;
                            }
                        }
                        scsContent = scsContent + "        <segment name=\"" + segName + markingExistDataNode +"\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
                        beforeSegmentTail = "            <field column=\"1\" name=\"S_"+setupDesc(temp.getDescription())+ "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n" + markingOBXGroupingLine +
                                            "        </segment> <!--" + segName + markingExistDataNode + "-->\n";
                    }
                    else
                    {
                        //temp = messageTreeEmpty.goNextSegment(messageTreeEmpty.nextTraverse(temp));
                        while(true)
                        {
                            ElementNode tmp = temp;
                            temp = messageTreeEmpty.nextTraverse(temp);
                            if (temp == messageTreeEmpty.getHeadNode())
                            {
                                temp = tmp;
                                break;
                            }
                            String str =  temp.getLevel();
                            //System.out.println("Level :" + str + ":");
                            if (str.equals("segment")) break;
                        }
                        //System.out.println("exit ");
                        continue;
                    }

                }
                else
                {
                    markingExistDataNode = "";

                    scsContent = scsContent + beforeSegmentTail;
                    //segmentTag = true;
                    //fieldTag = false;
                    int prefix = messageTreeEmpty.getSegmentPrefix(temp);
                    if (prefix == 1) segName = temp.getType();
                    else segName = "V" + prefix + temp.getType();

                    String markingOBXGroupingLine = "";
                    if (temp.getType().equals("OBX"))
                    {
                        ElementNode temp2 = temp.getLowerLink().getRightLink();
                        segName = segName + "_" + temp2.getValue();
                        if(!markingOBXGroupingFinish)
                        {
                            markingOBXGroupingLine = "            <field column=\"2\" name=\"S_"+markingOBXGrouping+ "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
                            markingOBXGroupingFinish = true;
                        }
                    }
                    scsContent = scsContent + "        <segment name=\"" + segName + markingExistDataNode +"\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
                    beforeSegmentTail = "            <field column=\"1\" name=\"S_"+setupDesc(temp.getDescription())+"\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n" + markingOBXGroupingLine +
                                        "        </segment> <!--" + segName + markingExistDataNode + "-->\n";
                }
            }

            if (level.equals("field"))
            {

                try
                {
                    if (messageTree.hasValue(messageTree.nodeAddressSearch(messageTreeEmpty.findNodeAddress(temp)))) markingExistDataNode = "";
                }
                catch(HL7MessageTreeException hhe)
                {
                    if (!hhe.getMessage().startsWith("ERR140"))
                    {
                        errorMessage = "ERR013 : Node Address search failure (2) : "+ hhe.getMessage();
                        return;
                    }
                }

                String type = segName + messageTreeEmpty.getFieldSequence(temp) + "_" + temp.getType() + "_" + setupDesc(temp.getDescription()).toUpperCase() + markingExistDataNode;
                //fieldTag = true;
                String content = "";
                String contentH = "            <segment name=\"" + type  + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" ;
                String contentM = "";
                String contentT = "            </segment> <!--" + type + "-->\n" + nextUUID;
                if (temp.getLowerLink() != null)
                {
                    ElementNode tmp = temp.getLowerLink();
                    if (messageTreeEmpty.getThisNodeLevel(tmp).equals("repetition_field")) temp = tmp;
                }
                if (temp.getLowerLink() == null)
                {
                    contentM = "                <field column=\"1\" name=\"" + setupDesc(temp.getDescription()) + markingExistDataNode + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
                }
                else
                {
                    int depth = messageTreeEmpty.getDepth(temp);
                    String componentName = "";
                    //System.out.println("depth : " + depth);
                    while(true)
                    {
                        markingExistDataNode = "";
                        try
                        {
                            if (messageTree.hasValue(messageTree.nodeAddressSearch(messageTreeEmpty.findNodeAddress(temp)))) markingExistDataNode = "";
                        }
                        catch(HL7MessageTreeException he)
                        {
                            markingExistDataNode = "";
                        }
                        //System.out.println(level + "." + messageTreeEmpty.getNodeSequence(temp) + "." + temp.getType() + "." + setupDesc(temp.getDescription()) + "." + temp.getValue() + "." + messageTreeEmpty.getDepth(temp) + "("+depth+")");
                        ElementNode tmp = temp;
                        temp = messageTreeEmpty.nextTraverse(temp);
                        if (messageTreeEmpty.getDepth(temp) <= depth)
                        {
                            temp = tmp;
                            break;
                        }

                        String comp = "";
                        if (temp.getLevel().equals("component"))
                        {
                            comp = "C"+ messageTreeEmpty.getNodeSequence(temp) + "_" + temp.getType();
                            if (temp.getLowerLink() == null)
                            {
                                componentName = "";
                            }
                            else
                            {
                                componentName = comp + "_" + setupDesc(temp.getDescription());
                                comp = "";
                            }
                        }
                        if (temp.getLevel().equals("subcomponent"))
                        {
                            comp = componentName + "_S" + messageTreeEmpty.getNodeSequence(temp) + "_" + temp.getType();
                        }
                        if (!comp.equals(""))
                        {
                            seq++;
                            contentM = contentM + "                <field column=\"" + seq + "\" name=\"" + comp + "_" + setupDesc(temp.getDescription()) + markingExistDataNode +"\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n";
                        }
                    }
                }

                scsContent = scsContent + contentH + contentM + contentT;
            }
            //if (temp == null) break;
            temp = messageTreeEmpty.nextTraverse(temp);

            if (temp == messageTreeEmpty.getHeadNode()) break;
            if (temp == null) break;
        }

        scsFileName = "";
        try
        {

            FileWriter fw = new FileWriter(pathSCS);
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<csvMetadata uuid=\"" + UUIDGenerator.getUniqueString() + "\" version=\"1.2\">\n");
            fw.write(scsContent);
            fw.write(beforeSegmentTail);
            fw.write(headSegmentTail);
            fw.write("</csvMetadata>");
            fw.close();
            scsFileName = pathSCS;
        }
        catch(IOException ie)
        {
            errorMessage = "ERR017 : SCS File Writing error (" + scsFileName + ") : " + ie.getMessage();
            return;
        }
        wasSuccessful = true;
    }

    private void generateCSVFile(String pathCSV, String scsTargetFileName)
    {
        wasSuccessful = false;
        boolean groupingYesOrNo = false;
        try
        {
            groupingYesOrNo = checkOBXDataTypeGroupingInTargetSCSFile(scsTargetFileName);
        }
        catch(IOException ie)
        {
            errorMessage = "ERR810 : (Grouping DT check) SCS Target File error (" + scsTargetFileName + ") : " + ie.getMessage();
            return;
        }

        ElementNode temp = messageTree.getHeadNode();
        String markingExistDataNode = "";
        String csvContent = "";
        String segName = "";
        while(true)//(temp!=null)
        {
            markingExistDataNode = "";
            if (messageTree.hasValue(temp)) markingExistDataNode = "";

            String level = temp.getLevel();//messageTree.getThisNodeLevel(temp);

            //System.out.println(level + "." + messageTree.getNodeSequence(temp) + "." + temp.getType() + "." + setupDesc(temp.getDescription()) + "." + temp.getValue());
            if (level.equals("message"))
            {
                String type = temp.getType().replace("^", "_");

                if (messageTree.hasValue(temp)) csvContent = type + ","+ setupDesc(temp.getDescription())+"\n";
                else return;
            }
            if (level.equals("segment"))
            {
                //scsContent = scsContent + beforeSegmentTail;

                int prefix = messageTree.getSegmentPrefix(temp);
                if (prefix == 1) segName = temp.getType();
                else segName = "V" + prefix + temp.getType();
                if (temp.getType().equals("OBX"))
                {
                    ElementNode temp2 = temp.getLowerLink().getRightLink();
                    String dt = temp2.getValue().trim();
                    if (groupingYesOrNo)
                    {
                        if ((dt.equals("ST"))||(dt.equals("FT"))||(dt.equals("TX"))) segName = segName + "_" + defaultOBXDataType;
                    }
                    else segName = segName + "_" + dt;
                }
                if (messageTree.hasValue(temp)) csvContent = csvContent + segName + markingExistDataNode + "," + setupDesc(temp.getDescription()) + "\n";
                else
                {
                    temp = messageTree.goNextSegment(temp);
                    if (temp == null) break;
                    else continue;
                }
            }
            if (level.equals("field"))
            {
                String dt = temp.getType();

                if (groupingYesOrNo)
                {
                    String compare = segName + messageTree.getFieldSequence(temp);
                    if ((compare.equals("OBX_" + defaultOBXDataType + "5"))&&
                        ((dt.equals("ST"))||(dt.equals("FT"))||(dt.equals("TX"))))
                    {
                        dt = defaultOBXDataType;
                    }
                }
                String type = segName + messageTree.getFieldSequence(temp) + "_" + dt + "_" + setupDesc(temp.getDescription()).toUpperCase() + markingExistDataNode;
                boolean repeatTag = false;
                String content = "";
                String imsi = "";
                //String contentH = "            <segment name=\"" + type + "_" + setupDesc(temp.getDescription()).toUpperCase() + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n" ;
                //String contentM = "";
                //String contentT = "            </segment> <!--" + type + "_" + setupDesc(temp.getDescription()).toUpperCase() + "-->\n" + nextUUID;
                if (temp.getLowerLink() != null)
                {
                    ElementNode tmp = temp.getLowerLink();
                    if (messageTree.getThisNodeLevel(tmp).equals("repetition_field"))
                    {
                        repeatTag = true;
                        temp = tmp;
                    }
                }
                if (temp.getLowerLink() == null)
                {
                    if (messageTree.getThisNodeLevel(temp).equals("field"))
                    {
                        String value = temp.getValue().trim();
                        if (value.indexOf(",") >= 0) value = "\"" + value + "\"";
                        imsi = checkSegmentData(type + "," + value);
                        if (!imsi.equals("")) csvContent = csvContent + imsi + "\n";
                    }
                    else if (messageTree.getThisNodeLevel(temp).equals("repetition_field"))
                    {
                        ElementNode tmp = temp;
                        while(tmp!=null)
                        {
                            String value = tmp.getValue().trim();
                            if (value.indexOf(",") >= 0) value = "\"" + value + "\"";
                            imsi = checkSegmentData(type + "," + value);
                            if (!imsi.equals("")) csvContent = csvContent + imsi + "\n";
                            if (tmp.getRightLink() == null)
                            {
                                temp = tmp;
                                break;
                            }
                            else tmp = tmp.getRightLink();
                        }
                    }
                }
                else
                {
                    int depth = messageTree.getDepth(temp);
                    //System.out.println("depth : " + depth);
                    content = type;
                    String cr = "";
                    while(true)
                    {
                        //System.out.println(level + "." + messageTree.getNodeSequence(temp) + "." + temp.getType() + "." + setupDesc(temp.getDescription()) + "." + temp.getValue() + "." + messageTree.getDepth(temp) + "("+depth+")");
                        if ((repeatTag)&&(messageTree.getDepth(temp) == depth))
                        {
                            imsi = checkSegmentData(content);
                            if (!imsi.equals("")) csvContent = csvContent + imsi + cr;
                            content = type;
                            cr = "\n";
                        }

                        ElementNode tmp = temp;
                        temp = messageTree.nextTraverse(temp);
                        if (((repeatTag)&&(messageTree.getDepth(temp) < depth))||((!repeatTag)&&(messageTree.getDepth(temp) <= depth)))
                        {
                            imsi = checkSegmentData(content);
                             if (!imsi.equals("")) csvContent = csvContent + imsi + "\n";
                             content = "";

                             temp = tmp;
                             repeatTag = false;
                             break;
                        }
                        if (temp.getLowerLink() == null)
                        {
                            String value = temp.getValue().trim();
                            if (value.indexOf(",") >= 0) value = "\"" + value + "\"";
                            content = content + "," + value;
                        }
                    }
                }

                    //scsContent = scsContent + content;
            }

            temp = messageTree.nextTraverse(temp);

            if (temp == messageTree.getHeadNode())
            {
                break;
            }

        }

        csvFileName = "";
        try
        {
            FileWriter fw = new FileWriter(pathCSV);
            fw.write(csvContent);
            fw.close();
            csvFileName = pathCSV;
        }
        catch(IOException ie)
        {
            errorMessage = "ERR019 : csv File Writing error (" + csvFileName + ") : " + ie.getMessage();
            return;
        }
        wasSuccessful = true;
    }

    public void convertV2ToCSV(String v2Message, String csvFile, String fileSCSForValidation) throws HL7MessageTreeException
    {
        if ((v2Message == null)||(v2Message.trim().equals(""))) throw new HL7MessageTreeException("V2 Message is empty.");
        boolean isMessageFile = false;
        try
        {
            File file = new File(v2Message);
            if (file.exists())
            {
                if (file.isFile()) isMessageFile = true;
                else if (file.isDirectory()) throw new HL7MessageTreeException("This is Directory Name : " + v2Message);
            }
        }
        catch(OutOfMemoryError er) { isMessageFile = false; }
        catch(Exception ex)
        {
            if (ex instanceof HL7MessageTreeException) throw (HL7MessageTreeException) ex;
            else isMessageFile = false;
        }
        String v2FileName = "";

        try
        {
            if (isMessageFile) v2FileName = v2Message;
            else v2FileName = FunctionUtil.saveStringIntoTemporaryFile(v2Message);
        }
        catch(IOException ie)
        {
            throw new HL7MessageTreeException("IOException : " + ie.getMessage());
        }

        messageTree = buildV2MessageTree(dataPathOfV2Meta, "2.5", v2FileName);
        messageType = messageTree.getMessageType();
        messageTreeEmpty = new HL7V2MessageTree(messageTree.getDataPath(), messageTree.getVersion(), messageType);

        wasSuccessful = true;
        process("", csvFile, false, false, null, fileSCSForValidation);
        if (!wasSuccessful) throw new HL7MessageTreeException(errorMessage);
    }

    private void collectValidateResultMessages(String classificaion, ValidatorResults validatorResults)
    {

        List<ValidatorResult.Level> listLevel = validatorResults.getLevels();
        for(int i=0;i<listLevel.size();i++)
        {
            ValidatorResult.Level level = listLevel.get(i);
            List<Message> listMessage = validatorResults.getMessages(level);
            for(int j=0;j<listMessage.size();j++)
            {
                Message message= listMessage.get(j);
                //System.out.println("    " + level.name() + " : " + message.toString());
                validationMessages.add(level.name() + "("+classificaion+") : " + message.toString());
            }
        }
    }

    private ValidatorResults validateSpecification(File scsFile)
	{
        CSVPanel csvPanel = new CSVPanel();
        //ValidatorResults validatorResults = csvPanel.setSaveFile(scsFile, true);
        ValidatorResults validatorResults2 = new ValidatorResults();
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			CSVMetaValidator metaValidator = new CSVMetaValidator(rootMeta);
			validatorResults2 = metaValidator.validate();
        }
        return validatorResults2;
    }

	private ValidatorResults validateDataAgainstSpecification(File scsFile, File csvFile)
	{
        CSVPanel csvPanel = new CSVPanel();
        //ValidatorResults validatorResults = csvPanel.setSaveFile(scsFile, true);
        ValidatorResults validatorResults2 = new ValidatorResults();
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			SegmentedCSVParserImpl segmentedCSVParser = new SegmentedCSVParserImpl();
			CSVDataResult result = segmentedCSVParser.parse(csvFile, rootMeta);
			validatorResults2 = result.getValidatorResults();
		}
        return validatorResults2;
    }


    private String setupDesc(String desc)
    {
        desc = desc.trim();
        String res = "";
        int spaceCount = 0;
        boolean spaceTagBefore = false;
        for (int i=0;i<desc.length();i++)
        {
            String achar1 = desc.substring(i, i+1);
            String achar2 = achar1.toUpperCase();
            char c = achar2.toCharArray()[0];
            int in = (int) c;

            if ((achar1.equals(" "))||(achar1.equals("-"))||(achar1.equals("/"))||(achar1.equals("'"))||(achar1.equals(","))||(achar1.equals("`")))
            {
                if (spaceCount < 3)
                {
                    if (!spaceTagBefore)
                    {
                        res = res + "_";
                        spaceCount++;
                    }
                    spaceTagBefore = true;
                    continue;
                }
                else break;
            }
            if (in < 48) continue;
            if (in > 90) continue;
            if ((in > 57)&&(in < 65)) continue;
            res = res + achar1;
            spaceTagBefore = false;
        }
        if (res.length() > 0)
        {
            while(true)
            {
                String achar = res.substring(res.length()-1, res.length());
                if (achar.equals("_")) res = res.substring(0, res.length()-1);
                else break;
            }
        }
        return res;
    }
    private String getPathDtrctory(String path)
    {
        while(true)
        {
            String achar = path.substring(path.length()-1, path.length());
            path = path.substring(0, path.length() - 1);
            if (path.trim().equals(""))
            {
                return "";
            }
            if (achar.equals(File.separator)) break;
        }
        return path;
    }
    private boolean validateOutputPath(String path1, String job)
    {
        if (path1 == null) path1 = "";
        if (path1.trim().equals(""))
        {
            return true;
        }
        String path = getPathDtrctory(path1);
        if (path.trim().equals(""))
        {
            errorMessage = "ERR021 : Invalid Drectory " + job + " directory : " + path1;
            return false;
        }
        File tempFile = new File(path);
        if (!tempFile.exists())
        {
            errorMessage = "ERR022 : This " + job + " directory is not exist. : " + path;
            return false;
        }
        else
        {
            if (!tempFile.isDirectory())
            {
                errorMessage = "ERR023 : This " + job + " path is not a directory. : " + path;
                return false;
            }
        }

        return true;
    }
    private boolean checkOBXDataTypeGroupingInTargetSCSFile(String scsTargetFileName) throws IOException
    {
        FileReader fr = null;
        boolean cTag = false;
        try { fr = new FileReader(scsTargetFileName); }
        catch(FileNotFoundException fe) { throw new IOException("File not found : " + scsTargetFileName); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";

        String achar = "";
        String buff = "";
        int len = markingOBXGrouping.length();
        while((readLineOfFile=br.readLine())!=null)
        {
            while(!readLineOfFile.equals(""))
            {
                achar = readLineOfFile.substring(0,1);
                readLineOfFile = readLineOfFile.substring(1);
                buff = buff + achar;
                if (buff.length() != len) continue;
                if (buff.equals(markingOBXGrouping))
                {
                    cTag = true;
                    break;
                }

                buff = buff.substring(1);
            }
            if (cTag) break;
        }

        fr.close();
        br.close();
        return cTag;
    }
    private String checkSegmentData(String st)
    {
        int idx = st.indexOf(",");
        if (idx < 0) return "";
        String name = st.substring(0, idx);
        String body = st.substring(idx);
        boolean sTag = false;
        int idx2 = 0;
        for(int i=0;i<body.length();i++)
        {
            String achar = body.substring(i,i+1);
            if (!achar.equals(",")) sTag = true;

        }
        if(!sTag) return "";
        while(true)
        {
            String achar = st.substring(st.length()-1, st.length());
            if (achar.equals(","))
            {
                st = st.substring(0, st.length()-1);
            }
            else break;
            if (st.length() < 3) return "";
        }
        return st;
    }

    public boolean wasSuccessful() { return wasSuccessful; }
    public boolean isSCSValid() { return isSCSValid; }
    public boolean isCSVValid() { return isCSVValid; }
    public String getErrorMessage() { return errorMessage; }
    public List<String> getValidationMessages() { return validationMessages; }
    public ValidatorResults getValidationResults()
    {
        ValidatorResults result = new ValidatorResults();
        if (resultValidationSCS != null) result.addValidatorResults(resultValidationSCS);
        if (resultValidationCSV != null) result.addValidatorResults(resultValidationCSV);
        return result;
    }
    public String getCSVFileName() { return csvFileName; }
    public String getSCSFileName()
    {
        if (scsFileName.equals("")) return targetSCSFileName;
        else return scsFileName;
    }

    public static void main(String args[])
    {
        /*
        List<String> listDataTypeOfOBX = new ArrayList<String>();
        listDataTypeOfOBX.add("FT");
        listDataTypeOfOBX.add("ST");
        listDataTypeOfOBX.add("ED");
        listDataTypeOfOBX.add("XAD");
        String filePath = FileUtil.getWorkingDirPath() + File.separator + "data" + File.separator + "v2Meta";

        //String fileName = "C:\\projects\\temp\\hl7V2sample.hl7";
        String fileName = "";
        String path = "C:\\projects\\temp\\";
        new V2Converter(fileName, filePath, path, false, false, listDataTypeOfOBX, "C:\\projects\\temp\\ADT_A03.scs");
        */
    }


}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2006/12/05 20:02:10  umkis
 * HISTORY      : minor modifying
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/12/04 20:09:26  umkis
 * HISTORY      : minor modifying
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/11/14 00:12:57  umkis
 * HISTORY      : simple code correcting
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/10 03:56:28  umkis
 * HISTORY      : V2-V3 mapping job version 1.0
 * HISTORY      :
 */
