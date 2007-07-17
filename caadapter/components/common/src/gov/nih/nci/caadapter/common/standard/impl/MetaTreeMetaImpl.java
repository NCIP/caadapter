/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaTreeMetaImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $
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

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.OutputStream;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Jul 2, 2007
 *          Time:       8:23:38 PM $
 */
public class MetaTreeMetaImpl extends CommonTreeMetaImpl implements MetaTreeMeta
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: MetaTreeMetaImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaTreeMetaImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $";

    private List<String> extensions = new ArrayList<String>();
    private String metaFileName = "";

    public MetaTreeMetaImpl()
    {
        super(CommonNodeModeType.META);
    }
    public MetaTreeMetaImpl(MetaSegment seg) throws ApplicationException
    {
        super(seg);
    }
    public void setHeadSegment(MetaSegment a) throws ApplicationException
    {
        super.setHeadSegment(a);
    }


    protected void clearLinksToDataTree()
    {
        CommonNode node = getHeadSegment();

        while(node!=null)
        {
            //for (int i=0;i<node.getAttributes().getAttributeItems().size();i++) node.getAttributes().getAttributeItems().get(i).c
            if (node instanceof MetaSegment)
            {
                MetaSegment segment = (MetaSegment) node;
                if (segment == getHeadSegment()) break;
                segment.clearLinkedDataSegments();
            }
            else if (node instanceof MetaField)
            {
                MetaField field = (MetaField) node;
                field.clearLinkedDataFields();
            }
            try
            {
                node = this.nextTraverse(node);
            }
            catch(ApplicationException ae)
            {
                break;
            }
        }
    }

    public DataTree constructInitialDataTree() throws ApplicationException
    {
        return constructInitialDataTree(new DataTreeImpl());
    }
    public DataTree constructInitialDataTree(DataTree dataTreeInstance) throws ApplicationException
    {
        if (dataTreeInstance == null) throw new ApplicationException("Data Tree Instance is null");
        clearLinksToDataTree();
        DataSegment dataSeg = ((MetaSegment) getHeadSegment()).createNewDataInstance();//((MetaSegment) getHeadSegment()).creatDataSegment(null, ((MetaSegment) getHeadSegment()).createNewDataInstance());
        //DataSegment dataSeg = ((MetaSegment) getHeadSegment()).creatDataSegment(null, ((MetaSegment) getHeadSegment()).createNewDataInstance());

        dataSeg = cloneSegmentNode(dataSeg, (MetaSegment) getHeadSegment(), null);
        ((MetaSegment) getHeadSegment()).addLinkedDataSegment(dataSeg);
        dataSeg.setSourceMetaSegment((MetaSegment) getHeadSegment());

        dataTreeInstance.setHeadSegment(dataSeg);
        dataTreeInstance.setMetaTree(this);
        dataTreeInstance.setNodeIdentifierType(this.getNodeIdentifierType());

        return dataTreeInstance;
    }

    private DataSegment cloneSegmentNode(DataSegment dataSeg, MetaSegment metaSeg, DataSegment parent) throws ApplicationException
    {
        dataSeg.cloneNode(dataSeg, metaSeg, metaSeg.getXmlPath(), metaSeg.getXPath(), parent);

        if (metaSeg.getChildNodes().size() > 0)
        {
            for(int i=0;i<metaSeg.getChildNodes().size();i++)
            {
                if (metaSeg.getChildNodes().get(i) instanceof CommonField)
                {
                    MetaField sourceField = (MetaField) metaSeg.getChildNodes().get(i);
                    DataField field = sourceField.createNewDataInstance();
                    field.cloneNode(field, sourceField, sourceField.getXmlPath(), sourceField.getXPath(), dataSeg);

                    sourceField.addLinkedDataSegment(field);
                    field.setSourceMetaField(sourceField);
                    dataSeg.addChildNode(field);
                }
                else
                {
                    MetaSegment sourceSegment = (MetaSegment) metaSeg.getChildNodes().get(i);
                    DataSegment segment = sourceSegment.createNewDataInstance();
                    //segment.cloneNode(segment, sourceSegment, sourceSegment.getXmlPath(), sourceSegment.getXPath(), dataSeg);
                    segment = cloneSegmentNode(segment, sourceSegment, dataSeg);
                    sourceSegment.addLinkedDataSegment(segment);
                    segment.setSourceMetaSegment(sourceSegment);
                    dataSeg.addChildNode(segment);
                }
            }
        }
        return dataSeg;
    }
    public ValidatorResults validateTree()
    {
       return validateTree(true);
    }
    public ValidatorResults validateTree(boolean AttributeUuidCheck)
    {
        List<String> uuidList = new ArrayList<String>();
        ValidatorResults results = validateMetaTreeRecurrsive((MetaSegment) this.getHeadSegment(), uuidList, AttributeUuidCheck);
        return results;
    }

    private ValidatorResults validateMetaTreeRecurrsive(CommonNode node, List<String> uuidList, boolean AttributeUuidCheck)
    {
        ValidatorResults results = new ValidatorResults();

        if (!((node instanceof MetaSegment)||(node instanceof MetaField)||(node instanceof CommonAttributeItem)))
        {
            results = GeneralUtilities.addValidatorMessage(results, "Invalid instance, This is neither MetaSegment, MetaField nor Attribute Item.");
            return results;
        }

        String name = node.getName();
        if ((name == null)||(name.trim().equals(""))) results = GeneralUtilities.addValidatorMessage(results, "There is null name. : " + node.getNodeType().toString());

        if (this.getNodeIdentifierType().getType() == NodeIdentifierType.UUID.getType())
        {
            if ((node.getXmlPath() == null)||(node.getXmlPath().trim().equals("")))
            {
                if ((!AttributeUuidCheck)&&(node instanceof CommonAttributeItem)) {}
                else results = GeneralUtilities.addValidatorMessage(results, "Null UUID found. : " + node.getName() + " : " + node.getXmlPath());
            }

            if (!((node.getXmlPath() == null)||(node.getXmlPath().trim().equals(""))))
            {
                String uuid = node.getXmlPath().trim();
                boolean cTag = false;
                for (int i=0;i<uuidList.size();i++)
                {
                    if (uuid.equals(uuidList.get(i)))
                    {
                        cTag = true;
                        results = GeneralUtilities.addValidatorMessage(results, "Redundant UUIDs are found : " + uuid + " : " + name + " and " + this.findNodeWithUUID(uuid).getName());
                    }
                }
                if (!cTag) uuidList.add(uuid);
            }
        }
        if (node instanceof CommonAttributeItem)
        {
            String check = GeneralUtilities.checkElementName(name);
            if (check != null) results = GeneralUtilities.addValidatorMessage(results, check);

            return results;
        }

        List<CommonAttributeItem> attributes = node.getAttributes().getAttributeItems();
        List<CommonNode> list = new ArrayList<CommonNode>();
        for (int i=0;i<attributes.size();i++) list.add(attributes.get(i));
        String msg = checkNames(list);

        if (msg != null) results = GeneralUtilities.addValidatorMessage(results, msg);
        for (int i=0;i<attributes.size();i++) results.addValidatorResults(validateMetaTreeRecurrsive(attributes.get(i), uuidList, AttributeUuidCheck));

        if (!(node instanceof MetaSegment)) return results;

        MetaSegment segment = (MetaSegment) node;

        List<CommonNode> children = segment.getChildNodes();

        if (segment.isChoiceNode())
        {
            if (segment.getFields().size() > 0) results = GeneralUtilities.addValidatorMessage(results, "Choice Group can not have any own field. : " + segment.getName());
            if (segment.getParent().isChoiceNode()) results = GeneralUtilities.addValidatorMessage(results, "Choice Group can not have any Choice Group. : " + segment.getName());
            for (int i=0;i<segment.getChildSegments().size();i++)
            {
                CommonSegment aSegment = segment.getChildSegments().get(i);
                if (aSegment.isOptionalOnly()) results = GeneralUtilities.addValidatorMessage(results, "Choice Group can not have any 'Optional Only' group. : " + segment.getName());
                if (aSegment.isRepetitiveOnly()) results = GeneralUtilities.addValidatorMessage(results, "Choice Group can not have any 'Repetitive Only' group. : " + segment.getName());
            }
        }
        else if (segment.isOptionalOnly())
        {
            if (segment.getFields().size() > 0) results = GeneralUtilities.addValidatorMessage(results, "Any 'Optional Only' mode can not have any own field. : " + segment.getName());
            if (((MetaSegment)segment.getParent()).isOptionalOnly()) results = GeneralUtilities.addValidatorMessage(results, "'Optional Only' mode can not have any 'Optional Only'. : " + segment.getName());
        }
        else if (segment.isRepetitiveOnly())
        {
            if (segment.getFields().size() > 0) results = GeneralUtilities.addValidatorMessage(results, "Any 'Repetitive Only' mode can not have any own field. : " + segment.getName());
            if (((MetaSegment)segment.getParent()).isRepetitiveOnly()) results = GeneralUtilities.addValidatorMessage(results, "'Repetitive Only' mode can not have any 'Repetitive Only'. : " + segment.getName());
        }
        else if (segment == this.getHeadSegment())
        {
            if (segment.getFields().size() == 0) results = GeneralUtilities.addValidatorMessageInfo(results, "This Head segment doesn't have any field. : " + segment.getName());
        }
        else
        {
            if (segment.getFields().size() == 0) results = GeneralUtilities.addValidatorMessageWarning(results, "This segment doesn't have any field. : " + segment.getName());
        }

        msg = checkNames(children);

        if (msg != null) results = GeneralUtilities.addValidatorMessage(results, msg);

        /*
        if (segment.getChildSegments().size() == 0)
        {
            if (segment == this.getHeadSegment()) results = GeneralUtilities.addValidatorMessage(results, "Invalid Tree : There is only head. :" + segment.getName());
        }
        */
         if (children.size() == 0)
        {
            results = GeneralUtilities.addValidatorMessage(results, "No Child Segment : " + segment.getName());
        }

        for (int i=0;i<children.size();i++) results.addValidatorResults(validateMetaTreeRecurrsive(children.get(i), uuidList, AttributeUuidCheck));


        return results;
    }

    private String checkNames(List<CommonNode> list)
    {
        if ((list == null)||(list.size() < 1)) return null;

        for (int i=0;i<(list.size()-1);i++)
        {
            CommonNode node1 = list.get(i);
            for (int j=(i+1);j<list.size();j++)
            {
                CommonNode node2 = list.get(j);
                if (node1.getName().equalsIgnoreCase(node2.getName())) return "There are redundant names : " + node1.getName();
            }
        }

        return null;
    }

    public void addMetaFileExtension(String extension)
    {
        if ((extension == null)||(extension.trim().equals(""))) return;
        extensions.add(extension);
    }
    public List<String> getMetaFileExtensions()
    {
        return extensions;
    }
    public String getMetaFileExtensionsString()
    {
        if ((extensions == null)||(extensions.size() == 0)) return "";
        String out = "";
        for (int i=0;i<extensions.size();i++)
        {
            out = out + "," + extensions.get(i).trim();
        }
        return out.substring(1);
    }
    public boolean isMetaFileExtension(String extension)
    {
        if ((extensions == null)||(extensions.size() == 0)) return false;
        if ((extension == null)||(extension.trim().equals(""))) return false;
        for (int i=0;i<extensions.size();i++)
        {
            if(extension.trim().equals(extensions.get(i).trim())) return true;
        }
        return false;
    }

    public void setMetaFileName(String fileName) throws ApplicationException
    {
        if ((fileName == null)||(fileName.trim().equals(""))) throw new ApplicationException("Null meta file name : ");

        File file = new File(fileName);

        if (!file.exists()) throw new ApplicationException("This meta file is not exists. : " + fileName);
        if (!file.isFile()) throw new ApplicationException("This meta file name is not a file. : " + fileName);

        metaFileName = fileName.trim();
    }
    public String getMetaFileName()
    {
        return metaFileName;
    }

    public void build() throws ApplicationException
    {
        String fileName = getMetaFileName();
        if ((fileName == null)||(fileName.trim().equals(""))) throw new ApplicationException("Meta file name hasn't been given yet.");

        //TODO
    }
    public void build(MetaObject metaObject) throws ApplicationException
    {
        //TODO
    }
    public void build(File file) throws ApplicationException
    {
        setMetaFileName(file.getAbsolutePath());
        //TODO
    }
    public void build(OutputStream outputStream) throws ApplicationException
    {
        //TODO
    }

    public boolean saveMetaFile(String fileName)
    {
        //TODO
        return false;
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
