/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import edu.knu.medinfo.hl7.v2tree.ElementNode;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import edu.knu.medinfo.hl7.v2tree.MetaDataLoader;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.SegmentedCSVParserImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.FunctionUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.hl7message.instanceGen.SCSIDChangerToXMLPath;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v3.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2008-06-09 19:54:05 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2Converter.java,v 1.9 2008-06-09 19:54:05 phadkes Exp $";


    HL7V2MessageTree messageTree = null;
    HL7V2MessageTree messageTreeEmpty = null;
    MetaDataLoader dataPathOfV2Meta = null;
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

    public V2Converter()
    {

    }
    public V2Converter(HL7V2MessageTree messTree) throws HL7MessageTreeException
    {
        if (messTree == null) throw new HL7MessageTreeException("This Message Tree is null.");
        ElementNode head = messTree.getHeadNode();
        if (head == null) throw new HL7MessageTreeException("The head node is null.");
        if (messTree.isTreeEmpty()) throw new HL7MessageTreeException("This v2 message tree is empty.");
        messageTreeEmpty = createV2MessageTree(messTree.getMetaDataLoader(), messTree.getVersion(), messTree.getMessageType());
        //dataPathOfV2Meta = messTree.getDataPath();
        messageTree = messTree;
        messageType = messageTree.getMessageType();
    }

    public V2Converter(Object dataPath) throws HL7MessageTreeException
    {
        messageTree = createV2MessageTree(dataPath, "2.5", "ADT^A03");
        //dataPathOfV2Meta = dataPath;
        messageTree = null;
    }

    public V2Converter(String v2FileName, Object dataPath) throws HL7MessageTreeException
    {
        messageTree = createV2MessageTree(dataPath, "2.5", v2FileName);
        messageType = messageTree.getMessageType();
        messageTreeEmpty = createV2MessageTree(messageTree.getMetaDataLoader(), messageTree.getVersion(), messageType);
        //dataPathOfV2Meta = dataPath;
    }

    public V2Converter(String messageType, String version, Object dataPath) throws HL7MessageTreeException
    {
        messageTree = null;
        messageTreeEmpty = createV2MessageTree(dataPath, version, messageType);
        //dataPathOfV2Meta = dataPath;
    }

    private HL7V2MessageTree createV2MessageTree(Object v2MetaPathObject, String version, String mType) throws HL7MessageTreeException
    {
        HL7V2MessageTree aTree = null;
        if (((v2MetaPathObject != null))&&(v2MetaPathObject instanceof String))
        {
            String v2MetaPath = (String)v2MetaPathObject;
            if (v2MetaPath.trim().equals("")) v2MetaPathObject = null;
        }
        if (v2MetaPathObject == null)
        {
            MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader();
            if (loader == null) throw new HL7MessageTreeException("V2 Meta Data Loader creation failure");
            else aTree = new HL7V2MessageTree(loader, version, mType);
        }
        else aTree = new HL7V2MessageTree(v2MetaPathObject, version, mType);

        dataPathOfV2Meta = aTree.getMetaDataLoader();
        return aTree;
    }

    private HL7V2MessageTree buildV2MessageTree(Object dataPath, String version, String v2FileName) throws HL7MessageTreeException
    {
        return new HL7V2MessageTree(dataPath, version, v2FileName);
    }
    public void process(String outSCS, String outCSV, boolean filteringNonDataSegment, boolean groupingYesOrNo, List<String> listDataTypeOfOBX, String scsTargetFileName)
    {
        process(outSCS, outCSV, filteringNonDataSegment, groupingYesOrNo, listDataTypeOfOBX, scsTargetFileName, true);
    }
    public void process(String outSCS, String outCSV, boolean filteringNonDataSegment, boolean groupingYesOrNo, List<String> listDataTypeOfOBX, String scsTargetFileName, boolean xmlPathAsID)
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
            if (!outSCS.equals("")) generateSCSFile(outSCS, filteringNonDataSegment, listDataTypeOfOBX, groupingYesOrNo, xmlPathAsID);
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

    private void generateSCSFile(String pathSCS, boolean filteringNonDataSegment, List<String> list, boolean groupingYesOrNo, boolean xmlPathAsID)
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
            if (level.equals(messageTree.getLevelMessage()))
            {
                String type = temp.getType().replace("^", "_");
                //nextUUID = "$$$" + UUIDGenerator.getUniqueString() + "@@@";
                scsContent = "    <segment name=\"" + type + "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\">\n";
                headSegmentTail = "        <field column=\"1\" name=\""+setupDesc(temp.getDescription())+ "\" uuid=\"" + UUIDGenerator.getUniqueString() + "\"/>\n" +
                                  "    </segment> <!--" + type + "-->\n";
            }
            if (level.equals(messageTree.getLevelSegment()))
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
                        String prefix = messageTreeEmpty.getSegmentPrefixSeq(temp);
                        if ((prefix == null)||(prefix.trim().equals(""))) segName = temp.getType();
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
                    String prefix = messageTreeEmpty.getSegmentPrefixSeq(temp);
                    if ((prefix == null)||(prefix.trim().equals(""))) segName = temp.getType();
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

            if (level.equals(messageTree.getLevelField()))
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
                        if (temp.getLevel().equals(messageTree.getLevelComponent()))
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
                        if (temp.getLevel().equals(messageTree.getLevelSubcomponent()))
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

        if (xmlPathAsID)
        {
            SCSIDChangerToXMLPath changer = new SCSIDChangerToXMLPath(pathSCS);
            if (!changer.wasSuccessful())
            {
                errorMessage = "ERR017 : Failure to change SCS file node ID to xmlPath : " + changer.getErrorMessage();
                return;
            }
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
            if (level.equals(messageTree.getLevelMessage()))
            {
                String type = temp.getType().replace("^", "_");

                if (messageTree.hasValue(temp)) csvContent = type + ","+ setupDesc(temp.getDescription())+"\n";
                else return;
            }
            if (level.equals(messageTree.getLevelSegment()))
            {
                //scsContent = scsContent + beforeSegmentTail;

                String prefix = messageTree.getSegmentPrefixSeq(temp);
                if ((prefix == null)||(prefix.trim().equals(""))) segName = temp.getType();
                else segName = "V" + prefix + temp.getType();
                if (temp.getType().equals("OBX"))
                {
                    ElementNode temp2 = temp.getLowerLink().getRightLink();
                    String dt = temp2.getValue().trim();
                    if (groupingYesOrNo)
                    {
                        if ((dt.equals("ST"))||(dt.equals("FT"))||(dt.equals("TX"))) segName = segName + "_" + defaultOBXDataType;
                        else segName = segName + "_" + dt;
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
            if (level.equals(messageTree.getLevelField()))
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
                    if (messageTree.getThisNodeLevel(tmp).equals(messageTree.getLevelFieldRepetition()))
                    {
                        repeatTag = true;
                        temp = tmp;
                    }
                }
                if (temp.getLowerLink() == null)
                {
                    if (messageTree.getThisNodeLevel(temp).equals(messageTree.getLevelField()))
                    {
                        String value = transformValue(temp.getValue().trim());
                        if (value.indexOf(",") >= 0) value = "\"" + value + "\"";
                        imsi = checkSegmentData(type + "," + value);
                        if (!imsi.equals("")) csvContent = csvContent + imsi + "\n";
                    }
                    else if (messageTree.getThisNodeLevel(temp).equals(messageTree.getLevelFieldRepetition()))
                    {
                        ElementNode tmp = temp;
                        while(tmp!=null)
                        {
                            String value = transformValue(tmp.getValue().trim());
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
                            String value = transformValue(temp.getValue().trim());
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

    private String transformValue(String src)
    {
        String source = src;
        if (src.startsWith(messageTreeEmpty.getFileHead()))
        {
            String fileName = src.substring(messageTreeEmpty.getFileHead().length());
            source = FileUtil.readFileIntoString(fileName);
            (new File(fileName)).delete();
        }
        return source;
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
        messageTreeEmpty = new HL7V2MessageTree(messageTree.getMetaDataLoader(), messageTree.getVersion(), messageType);

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

    public ValidatorResults validateSpecification(File scsFile)
	{
        CSVPanel csvPanel = new CSVPanel();
        ValidatorResults validatorResults = csvPanel.setSaveFile(scsFile, true);
        ValidatorResults validatorResults2 = new ValidatorResults();
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			CSVMetaValidator metaValidator = new CSVMetaValidator(rootMeta);
			validatorResults2 = metaValidator.validate();
        }
        validatorResults.addValidatorResults(validatorResults2);
        return validatorResults;
    }

	private ValidatorResults validateDataAgainstSpecification(File scsFile, File csvFile)
	{
        CSVPanel csvPanel = new CSVPanel();
        ValidatorResults validatorResults = csvPanel.setSaveFile(scsFile, true);
        //if (!validatorResults.isValid()) System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV------------------------------------------------------------");
        ValidatorResults validatorResults2 = new ValidatorResults();
        CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			SegmentedCSVParserImpl segmentedCSVParser = new SegmentedCSVParserImpl();
            CSVDataResult result = null;
            try
            {
                result = segmentedCSVParser.parse(csvFile, rootMeta);
            }
            catch(ApplicationException ae)
            {
                //System.out.println("VVVVVVVVVVV : " +  ae.getMessage());
                return GeneralUtilities.addValidatorMessage(validatorResults2, ae.getMessage());
            }
            validatorResults2 = result.getValidatorResults();
            validatorResults.addValidatorResults(validatorResults2);
        }
        return validatorResults;
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
 * HISTORY      : Revision 1.8  2008/05/29 01:25:34  umkis
 * HISTORY      : update: v2 resource zip file can be accessed not only meta directory
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/02/01 02:11:41  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/02/01 02:01:56  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/02/01 01:59:54  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/01/31 21:40:06  umkis
 * HISTORY      : csv converting from multi message included v2 file.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/17 01:13:28  umkis
 * HISTORY      : generated SCS file using xml path
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/09 16:21:42  umkis
 * HISTORY      : add a try-catch block on to 'result = segmentedCSVParser.parse(csvFile, rootMeta);'
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
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
