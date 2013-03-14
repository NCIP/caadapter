/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.standard;

import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:47:49 PM $
 */
public interface DataTree extends CommonTreeMeta
{
    public MetaTreeMeta getMetaTree();
    public void setMetaTree(MetaTreeMeta meta) throws ApplicationException;
    public void setHeadSegment(DataSegment a) throws ApplicationException;
    //public DataSegment getHeadSegment();
    //public DataSegment nextTraverse(DataSegment a) throws ApplicationException;
    //public DataSegment findNodeWithName(String name);
    //public DataSegment findNodeWithUUID(String uuid);
    //public DataSegment findNodeWithXPath(String xpath);
    public void inputValueIntoNode(CommonNode node, String val) throws ApplicationException;
    public void inputValuesFromArray(String[][] data) throws ApplicationException;
    public void deleteValueOfNode(CommonNode node);
    public DataSegment findNearestSegmentWithName(DataSegment seg, String sName);
    //public boolean doesHaveAnyData(CommonNode node);
    public DataSegment cloneBranch(DataSegment segment) throws ApplicationException;
    //public gov.nih.nci.hl7.validation.ValidatorResults validateDataTree();

    public void addDataFileExtension(String extension);
    public List<String> getDataFileExtensions();
    public String getDataFileExtensionsString();
    public boolean isDataFileExtension(String extension);

    public void setDataFileName(String fileName) throws ApplicationException;
    public String getDataFileName();
    public void inputValuesFromFile(String fileName) throws ApplicationException;

    public boolean generateOutput(String fileName);
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

