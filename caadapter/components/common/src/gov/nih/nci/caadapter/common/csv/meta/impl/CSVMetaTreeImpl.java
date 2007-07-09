/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVMetaTreeImpl.java,v 1.1 2007-07-09 15:37:33 umkis Exp $
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVMetaTreeImpl.java,v 1.1 2007-07-09 15:37:33 umkis Exp $";

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
        metaSeg.setUUID(csvSeg.getUUID());
        metaSeg.setCardinalityType(csvSeg.getCardinalityType());

        for (int i=0;i<csvSeg.getFields().size();i++)
        {
            CSVFieldMeta field = csvSeg.getFields().get(i);
            MetaField newField = metaSeg.createNewFieldInstance();
            newField.setName(field.getName());
            newField.setUUID(field.getUUID());

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
 */
