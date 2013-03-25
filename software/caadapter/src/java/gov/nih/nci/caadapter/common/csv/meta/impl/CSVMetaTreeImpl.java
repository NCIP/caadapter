/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.csv.meta.impl;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.standard.MetaSegment;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.MetaField;
import gov.nih.nci.caadapter.common.standard.impl.MetaSegmentImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaTreeMetaImpl;
import gov.nih.nci.caadapter.common.csv.data.CSVDataTree;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVDataTreeImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMetaTree;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 3, 2007
 *          Time:       11:21:13 AM $
 */
public class CSVMetaTreeImpl extends MetaTreeMetaImpl implements CSVMetaTree
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CSVMetaTreeImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVMetaTreeImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private final String csvMetaExtention = "scs";
    private final String csvDataExtention = "csv";

    public CSVMetaTreeImpl()
    {
        super();
        super.addMetaFileExtension(csvMetaExtention);
    }

    public void setSCSFileName(String fileName) throws ApplicationException
    {
        super.setMetaFileName(fileName);
    }
    public String getSCSFileName()
    {
        return super.getMetaFileName();
    }

    public void build(MetaObject metaObject) throws ApplicationException
    {
        if (metaObject instanceof CSVMeta)
        {
            CSVMeta csvMeta = (CSVMeta) metaObject;
            CSVSegmentMeta root = csvMeta.getRootSegment();
            MetaSegment newHead = new MetaSegmentImpl();

            buildMetaSegmentFromCSVSegment(root, newHead);
            this.setHeadSegment(newHead);
        }
        else throw new ApplicationException("Invalid instance (This is not a CSVMeta instance.)");
    }
    private void buildMetaSegmentFromCSVSegment(CSVSegmentMeta csvSeg, MetaSegment metaSeg) throws ApplicationException
    {
        metaSeg.setName(csvSeg.getName());
        metaSeg.setXmlPath(csvSeg.getXmlPath());
        metaSeg.setCardinalityType(csvSeg.getCardinalityType());

        for (int i=0;i<csvSeg.getFields().size();i++)
        {
            CSVFieldMeta field = csvSeg.getFields().get(i);
            MetaField newField = metaSeg.createNewFieldInstance();
            newField.setName(field.getName());
            newField.setXmlPath(field.getXmlPath());

            //if (csvSeg.isChoiceMemberSegment()) newField.setDataType(BasicDataType.NUMBER.toString());
            metaSeg.addChildNode(newField);
        }
        for (int i=0;i<csvSeg.getChildSegments().size();i++)
        {
            MetaSegment newSeg = metaSeg.createNewInstance();
            buildMetaSegmentFromCSVSegment(csvSeg.getChildSegments().get(i), newSeg);
            metaSeg.addChildNode(newSeg);
        }
    }

    public CSVDataTree constructInitialDataTree() throws ApplicationException
    {
        CSVDataTree csvDataTree = (CSVDataTree) super.constructInitialDataTree(new CSVDataTreeImpl());
        csvDataTree.addDataFileExtension(csvDataExtention);
        return csvDataTree;
    }

    public ValidatorResults validateTree()
    {
        ValidatorResults results = super.validateTree();
        List<String> nameList = new ArrayList<String>();
        results.addValidatorResults(validateMetaTreeRecurrsive((MetaSegment) this.getHeadSegment(), nameList));
        return results;
    }

    private ValidatorResults validateMetaTreeRecurrsive(CommonNode node, List<String> nameList)
    {
        ValidatorResults results = new ValidatorResults();

        if (node instanceof MetaSegment)
        {
            String name = node.getName().trim();

            String check = GeneralUtilities.checkElementName(node.getName(), true);
            if (check != null) results = GeneralUtilities.addValidatorMessage(results, "Invalid segment name : " + check);

            boolean cTag = false;
            for (int i=0;i<nameList.size();i++)
            {
                if (name.equals(nameList.get(i)))
                {
                    cTag = true;
                    results = GeneralUtilities.addValidatorMessage(results, "Two or more segments redundantly have this same name. : " + name);
                }
            }
            if (!cTag)
            {
                nameList.add(name);
            }
        }
        else
        {
            String check = GeneralUtilities.checkElementName(node.getName());
            if (check != null) results = GeneralUtilities.addValidatorMessage(results, "Invalid name for an attribute item or field node : " + check);

            return results;
        }

        MetaSegment segment = (MetaSegment) node;

        List<CommonNode> children = segment.getChildNodes();

        for (int i=0;i<children.size();i++) results.addValidatorResults(validateMetaTreeRecurrsive(children.get(i), nameList));

        return results;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:16:34  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:33  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */
