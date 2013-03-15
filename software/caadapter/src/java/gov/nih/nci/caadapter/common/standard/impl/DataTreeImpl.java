/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       8:17:50 PM $
 */
public class DataTreeImpl extends CommonTreeMetaImpl implements DataTree

{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: DataTreeImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/DataTreeImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private MetaTreeMeta metaTree = null;

    private List<String> extensions = new ArrayList<String>();
    private String dataFileName = "";

//    public DataTreeImpl()
//    {
//        super(CommonNodeModeType.DATA);
//    }
    public DataTreeImpl()
    {
        super(CommonNodeModeType.DATA);
    }
    public DataTreeImpl(DataSegment seg, MetaTreeMeta meta) throws ApplicationException
    {
        super(seg);

        setMetaTree(meta);
    }
    public MetaTreeMeta getMetaTree()
    {
        return metaTree;
    }
    public void setMetaTree(MetaTreeMeta meta) throws ApplicationException
    {
        if (meta == null) throw new ApplicationException("Meta tree is null.");
        metaTree = meta;
    }
    public void setHeadSegment(DataSegment a) throws ApplicationException
    {
        super.setHeadSegment(a);
    }

//    public DataSegment nextTraverse(DataSegment a) throws ApplicationException
//    {
//        return (DataSegment) super.nextTraverse(a);
//    }

    public void inputValueIntoNode(CommonNode node, String val) throws ApplicationException
    {
        node.setValue(val);
    }
    public void deleteValueOfNode(CommonNode node)
    {
        node.deleteValue();
    }
    public DataSegment findNearestSegmentWithName(DataSegment seg, String sName)
    {
        return (DataSegment) super.findNearestSegmentWithName(seg, sName);
    }

    public void inputValuesFromArray(String[][] data) throws ApplicationException
    {
        if (getMetaTree() == null) throw new ApplicationException("Meta tree is not set yet.");

        MetaSegment metaCurrent = (MetaSegment) getMetaTree().getHeadSegment();
        if (metaCurrent.getLinkedDataSegments().size() != 1) throw new ApplicationException("Meta head has several data segment.");
        DataSegment dataCurrent = metaCurrent.getLinkedDataSegments().get(0);
        if (dataCurrent != ((DataSegment)getHeadSegment())) throw new ApplicationException("Data head is mismatched with meta head.");

        String beforeSegmentName = "";
        for(int i=0;i<data.length;i++)
        {
            if (data[i] == null) continue;
            String[] dataLine = data[i];
            String segmentName = dataLine[0];
            if (i == 0)
            {
                if (!segmentName.equals(metaCurrent.getName())) throw new ApplicationException("Head segment was lost.");
            }
            else
            {
                if (segmentName.equals(beforeSegmentName))
                {
                    if (!segmentName.equals(dataCurrent.getName())) throw new ApplicationException("This Segment name is misused. (Internal error) :" + segmentName);
                }
                else
                {
                    beforeSegmentName = segmentName;
                    DataSegment tempData = (DataSegment) findNextSegmentWithName(dataCurrent, segmentName);
                    if (tempData == null)
                    {
                        tempData = (DataSegment) findNearestSegmentWithName(dataCurrent, segmentName);
                        if (tempData == null) throw new ApplicationException("This Segment name cannot be found. :" + segmentName);
                    }
                    dataCurrent = tempData;
                    if (dataCurrent.isChoiceNode())
                    {
//                        if (dataCurrent.getMaxCardinality() == 1)
//                            throw new ApplicationException("This is a single choice group. Own data segment is not allowed. :" + segmentName);
                        throw new ApplicationException("Name of any Choice group is not allowed to appear in csv file. :" + segmentName);
                    }
                    if (dataCurrent.isRepetitiveOnly())
                    {
                        if (dataCurrent.getMaxCardinality() == 1)
                            throw new ApplicationException("This is a single repetitive node. This name is not allowed to present in csv file. :" + segmentName);
                    }
                    if (dataCurrent.isOptionalOnly())
                    {
                        throw new ApplicationException("This is a 'Optional Only' node. This name is not allowed to present in csv file. :" + segmentName);
                    }
                }
            }
            if (this.doesThisBranchHaveValues(dataCurrent)) dataCurrent = this.cloneBranch(dataCurrent);

            if (dataCurrent != ((DataSegment) getHeadSegment()))
            {
                DataSegment parent = (DataSegment) dataCurrent.getParent();
                while(true)
                {
                    boolean cTag = false;
                    if ((parent.getFields().size() == 0)&&(parent.getAttributes().getAttributeItems().size() == 0))
                    {
                        cTag = true;
                    }
                    else
                    {
                        for (int j=0;j<parent.getFields().size();j++)
                        {
                            CommonField field = parent.getFields().get(j);
                            if (field.doesThisNodeHaveValues()) cTag = true;
                        }
                        if (!cTag) cTag = parent.doesThisNodeHaveValues();
                    }
                    if (!cTag) throw new ApplicationException("Logical Error: An ancestor segment'"+parent.getName()+"' of '"+dataCurrent.getName()+"' segment is empty, at row: " + i);

                    if (parent == ((DataSegment) getHeadSegment())) break;
                    parent = (DataSegment) parent.getParent();
                }
            }

            int fieldSeq = 0;
            List<CommonNode> nodes = dataCurrent.getChildNodes();
            for (int j=0;j<nodes.size();j++)
            {
                CommonNode node = nodes.get(j);
                if (!(node instanceof DataField)) continue;
                fieldSeq++;
                DataField field = (DataField) node;
                try
                {
                    field.setValue(dataLine[fieldSeq]);
                }
                catch(ArrayIndexOutOfBoundsException ee)
                {
                    break;
                }
                catch(ApplicationException ee)
                {
                    field.setAnotherValue(dataLine[fieldSeq]);
                }
            }
            fieldSeq++;
            try
            {
                String str = dataLine[fieldSeq];
                //System.out.println("CCC vbvb2: dataLine["+fieldSeq+"] " + dataLine[fieldSeq]);
                if (str != null) throw new ApplicationException("Field data overflowing in this segment : " + segmentName);
            }
            catch(ArrayIndexOutOfBoundsException ee)
            {  }
        }
    }

    public DataSegment cloneBranch(DataSegment segment) throws ApplicationException
    {
        if (segment == null)
        {
            throw new ApplicationException("Branch Cloning Failure : Null segment");
        }
        if (segment == getHeadSegment())
        {
            throw new ApplicationException("Head segment cannot be clonned.");
        }
        MetaSegment sourceSegment = segment.getMetaSegment();
        DataSegment seg = segment.createNewInstance();
        seg = cloneBranchRecurrsive(segment, seg);


        DataSegment par = (DataSegment) segment.getParent();

        List<CommonNode> siblings = par.getChildNodes();
        List<CommonNode> list = new ArrayList<CommonNode>();


        sourceSegment.addLinkedDataSegment(seg);
        seg.setSourceMetaSegment(sourceSegment);

        boolean cTag = false;
        for(int i=0;i<siblings.size();i++)
        {
            if (!(siblings.get(i) instanceof DataSegment))
            {
                if (cTag)
                {
                    cTag = false;
                    list.add(seg);
                }
                list.add(siblings.get(i));
                continue;
            }
            DataSegment segmentD = (DataSegment) siblings.get(i);
            if (segment.getMetaSegment() == segmentD.getMetaSegment())
            {
                cTag = true;
            }
            else
            {
                if (cTag)
                {
                    cTag = false;
                    list.add(seg);
                }
            }
            list.add(siblings.get(i));
        }
        if (cTag) list.add(seg);
        if (list.size() != (siblings.size()+1))
        {
            throw new ApplicationException("Branch Cloning Failure : " + segment.getName() +" : " + seg.getName() + " : " + list.size() + " : " + siblings.size());
        }
        par.replaceChildDataNodes(list);
        return seg;
    }
    private DataSegment cloneBranchRecurrsive(DataSegment segment, DataSegment clonnedSeg) throws ApplicationException
    {
        clonnedSeg.cloneNode(clonnedSeg, segment, segment.getXmlPath(), segment.getXPath(), segment.getParent());

        MetaSegment metaSegment = segment.getMetaSegment();
        List<CommonNode> metaNodes = new ArrayList<CommonNode>();

        if (segment.getChildNodes().size() > 0)
        {
            for(int i=0;i<segment.getChildNodes().size();i++)
            {
                CommonNode node = segment.getChildNodes().get(i);

                if (node instanceof CommonField)
                {
                    DataField sourceField = (DataField) segment.getChildNodes().get(i);

                    MetaField metaField = sourceField.getSourceMetaField();
                    boolean cTag = false;
                    for(int j=0;j<metaNodes.size();j++)
                    {
                        if (!(metaNodes.get(j) instanceof MetaField)) continue;
                        MetaField metaFieldOfList = (MetaField) metaNodes.get(j);
                        if (metaFieldOfList == metaField) cTag = true;
                    }
                    if (cTag) continue;

                    DataField field = sourceField.createNewInstance();
                    field.cloneNode(field, sourceField, sourceField.getXmlPath(), sourceField.getXPath(), clonnedSeg);
                    sourceField.getSourceMetaField().addLinkedDataSegment(field);
                    field.setSourceMetaField(metaField);
                    //field.deleteValue();
                    clonnedSeg.addChildNode(field);
                    metaNodes.add(metaField);
                }
                else
                {
                    DataSegment sourceSegment = (DataSegment) segment.getChildNodes().get(i);

                    MetaSegment metaSegmentS = sourceSegment.getMetaSegment();
                    boolean cTag = false;
                    for(int j=0;j<metaNodes.size();j++)
                    {
                        if (!(metaNodes.get(j) instanceof MetaSegment)) continue;
                        MetaSegment metaSegmentOfList = (MetaSegment) metaNodes.get(j);
                        if (metaSegmentOfList == metaSegmentS) cTag = true;
                    }
                    if (cTag) continue;

                    DataSegment segment2 = sourceSegment.createNewInstance();

                    segment2 = cloneBranchRecurrsive(sourceSegment, segment2);

                    sourceSegment.getMetaSegment().addLinkedDataSegment(segment2);
                    segment2.setSourceMetaSegment(metaSegmentS);
                    //segment2.deleteValue();
                    clonnedSeg.addChildNode(segment2);
                    metaNodes.add(metaSegmentS);
                }

            }
        }
        clonnedSeg.setSourceMetaSegment(metaSegment);
        metaSegment.addLinkedDataSegment(clonnedSeg);
        return clonnedSeg;
    }
    public ValidatorResults validateTree()
    {
        //TODO
        ValidatorResults validatorResults = new ValidatorResults();
        if (getMetaTree() == null)
        {
            validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Meta tree is not set yet.");
            return validatorResults;
        }
        if (getHeadSegment() == null)
        {
            validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Data tree is not built yet.");
            return validatorResults;
        }

        //if ((getDataFileName() == null)||(getDataFileName().trim().equals("")))
        DataSegment head = (DataSegment) this.getHeadSegment();
        if (!this.doesThisBranchHaveValues(head))
        {
            validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "This data tree has no data.");
            return validatorResults;
        }

        return validateTreeRecurrsive(head);
    }

    private ValidatorResults validateTreeRecurrsive(CommonNode dataNode)//DataSegment dataSeg)
    {

        String valueCheck = "";
        ValidatorResults validatorResults = new ValidatorResults();
        if ((dataNode instanceof DataSegment)||(dataNode instanceof DataField)) {}
        else
        {
            validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Invalid instance, This is neither DataSegment nor DataField.");
            return validatorResults;
        }
        if (dataNode == null)
        {
            return GeneralUtilities.addValidatorMessage(validatorResults, "Null Data Segment");
        }

        if (this.getNodeIdentifierType().getType() == NodeIdentifierType.UUID.getType())
        {


            if ((dataNode.getXmlPath() == null)||(dataNode.getXmlPath().trim().equals("")))
            {
                String metaUUID = "";
                if (dataNode instanceof MetaSegment)
                {
                    metaUUID = ((DataSegment)dataNode).getMetaSegment().getXmlPath();
                }
                else if (dataNode instanceof MetaField)
                {
                    metaUUID = ((DataField)dataNode).getSourceMetaField().getXmlPath();
                }
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Null UUID found (Data tree). : " + dataNode.getName() + " : " + dataNode.getXmlPath() + " : " + metaUUID);
            }



            //if ((dataNode.getXmlPath() == null)||(dataNode.getXmlPath().trim().equals("")))
            //    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Null UUID found. : " + dataNode.getName());
        }

        List<CommonAttributeItem> items = dataNode.getAttributes().getAttributeItems();

        valueCheck = validateValueOfNode(dataNode);
        if (!valueCheck.equals("")) validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, valueCheck);



        // Attribute check
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            valueCheck = validateValueOfNode(item);
            if (!valueCheck.equals(""))
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, valueCheck);
            else if ((!item.doesHaveValue())&&(item.isThisRequired()))
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Required Attribute has no data : " + item.getName() + "@" + dataNode.getName());

            if (this.getNodeIdentifierType().getType() == NodeIdentifierType.UUID.getType())
            {
                if ((item.getXmlPath() == null)||(item.getXmlPath().trim().equals("")))
                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Null UUID found (AttributeItem). : " + item.getName());
            }

        }

        if (!(dataNode instanceof DataSegment)) return validatorResults;

        DataSegment dataSeg = (DataSegment) dataNode;

        if (!dataSeg.isValueInputEnabled())
        {
            if (dataSeg.doesHaveValue())
            {
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "This segment ("+dataSeg.getName()+") is not allowed to have own value, but it does : " + dataSeg.getValue());
            }
            else if (dataSeg.getAnotherValue() != null)
            {
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "This segment ("+dataSeg.getName()+") is not allowed to have own value, but it does : " + dataSeg.getAnotherValue());
            }
        }
        MetaSegment metaSeg = dataSeg.getMetaSegment();
        List<CommonNode> list = metaSeg.getChildNodes();
        List<CommonNode> listDataSeg = dataSeg.getChildNodes();

        if ((listDataSeg == null)||(listDataSeg.size() == 0)) return validatorResults;


        // Choice Segment validation
        if (dataSeg.isChoiceNode())
        {
            String segName = "";
            int numOfSegments = 0;
            for(int i=0;i<listDataSeg.size();i++)
            {
                CommonNode aNode = listDataSeg.get(i);
                if (!(aNode instanceof DataSegment))
                {
                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "This is a choice gorup. Field is not allowed. : " + dataSeg.getName());
                    continue;
                }
                DataSegment segment = (DataSegment) aNode;

                if (!this.doesThisBranchHaveValues(segment)) continue;
                if (segName.equals("")) segName = segment.getName();

//                if (!segName.equals(segment.getName()))
//                {
//                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Several choice member type were found in this choice segment("+dataSeg.getName()+") : " + segName + ", " + segment.getName());
//                }
//                else
//                {
//                    numOfSegments++;
//                    validatorResults.addValidatorResults(validateTreeRecurrsive(segment));
//                }
                numOfSegments++;
                validatorResults.addValidatorResults(validateTreeRecurrsive(segment));
            }
            if (numOfSegments > dataSeg.getMaxCardinality())
            {
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Too many data in this Choice segment. ("+dataSeg.getCardinalityType()+") : " + dataSeg.getName() + " : " + numOfSegments + " of " + segName);
            }
            if (numOfSegments < dataSeg.getMinCardinality())
            {
                validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Too less data in this Choice segment. ("+dataSeg.getCardinalityType()+") : " + dataSeg.getName() + " : " + numOfSegments + " of " + segName);
            }
            //System.out.println("CCC XXX1 : " + dataSeg.getName());
            return validatorResults;
        }
        else
        {
            // cardinality check
            for(int i=0;i<list.size();i++)
            {
                CommonNode node = list.get(i);
                String errMsg = "";

                int numOfData = countDataSegments(listDataSeg, node);

                if (numOfData < 0)
                {
                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Internal problem : Invalid data or meta tree structure : " + node.getName() + "@" + dataSeg.getName());
                    continue;
                }

                if (node instanceof CommonField)
                    errMsg = "field '"+node.getName()+"' ("+node.getCardinalityType()+") : "  + numOfData + " at " + dataSeg.getName() + " segment";
                else if (node instanceof CommonSegment)
                {
                    String addMsg = "";
                    if (node.isChoiceNode()) addMsg = "in this choice ";
                    errMsg = addMsg + "segment '"+node.getName()+"' ("+node.getCardinalityType()+") : "  + numOfData + " at " + dataSeg.getName() + " segment";
                }
                if (numOfData > node.getMaxCardinality())
                {
                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Too many data " + errMsg);
                }
                if (numOfData < node.getMinCardinality())
                {
                    validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, "Too less data " + errMsg);
                }
            }
        }

        for(int i=0;i<listDataSeg.size();i++)
        {
            CommonNode aNode = listDataSeg.get(i);

            if (!this.doesThisBranchHaveValues(aNode)) continue;
            validatorResults.addValidatorResults(validateTreeRecurrsive(aNode));
        }

        return validatorResults;
    }

    private String validateValueOfNode(CommonNode node)
    {
        String msg = "";
        //if (node.doesHaveValue()) return msg;

        if (node.getAnotherValue() == null) return msg;

        //System.out.println("CCC ZZZ1 : " + node.getAnotherValue());

        try
        {
            node.setValue(node.getAnotherValue());
        }
        catch(ApplicationException ae)
        {
            msg = ae.getMessage();
        }
        //node.setAnotherValue(null);
        return msg;
    }



    private int countDataSegments(List<CommonNode> list, CommonNode node)
    {
        if (list == null) return 0;
        if (list.size() == 0) return 0;
        if (node == null) return -1;

        int count = 0;
        int check = 0;
        String name = node.getName();

        for (int i=0;i<list.size();i++)
        {
            CommonNode oneNode = list.get(i);
            if (!name.equals(oneNode.getName())) continue;
            if (node instanceof MetaField)
            {
                if (!(oneNode instanceof DataField)) continue;
                check++;
                DataField field = (DataField) oneNode;
                if (field.doesThisNodeHaveValues()) count++;
            }
            else if (node instanceof MetaSegment)
            {
                if (!(oneNode instanceof DataSegment)) continue;
                check++;
                DataSegment segment = (DataSegment) oneNode;
                if (this.doesThisBranchHaveValues(segment)) count++;
            }
            else return -1;
        }
        if (check == 0) return -1;
        return count;
    }


    public void addDataFileExtension(String extension)
    {
        if ((extension == null)||(extension.trim().equals(""))) return;
        extensions.add(extension);
    }
    public List<String> getDataFileExtensions()
    {
        return extensions;
    }
    public String getDataFileExtensionsString()
    {
        if ((extensions == null)||(extensions.size() == 0)) return "";
        String out = "";
        for (int i=0;i<extensions.size();i++)
        {
            out = out + "," + extensions.get(i).trim();
        }
        return out.substring(1);
    }
    public boolean isDataFileExtension(String extension)
    {
        if ((extensions == null)||(extensions.size() == 0)) return false;
        if ((extension == null)||(extension.trim().equals(""))) return false;
        for (int i=0;i<extensions.size();i++)
        {
            if(extension.trim().equals(extensions.get(i).trim())) return true;
        }
        return false;
    }

    public void setDataFileName(String fileName) throws ApplicationException
    {
        if ((fileName == null)||(fileName.trim().equals(""))) throw new ApplicationException("Null data file name : ");

        File file = new File(fileName);

        if (!file.exists()) throw new ApplicationException("This data file is not exists. : " + fileName);
        if (!file.isFile()) throw new ApplicationException("This data file name is not a file. : " + fileName);

        dataFileName = fileName.trim();
    }
    public String getDataFileName()
    {
        return dataFileName;
    }


    public void inputValuesFromFile(String fileName) throws ApplicationException
    {
        setDataFileName(fileName);
        //TODO
    }

    public boolean generateOutput(String fileName)
    {
        //TODO
        return false;
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:11:38  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
