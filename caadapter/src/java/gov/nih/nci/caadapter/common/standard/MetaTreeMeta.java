/*
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.common.standard;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.List;
import java.io.File;
import java.io.OutputStream;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:50:20 PM $
 */
public interface MetaTreeMeta extends CommonTreeMeta
{
    public void setHeadSegment(MetaSegment a) throws ApplicationException;

    public DataTree constructInitialDataTree() throws ApplicationException;
    public DataTree constructInitialDataTree(DataTree dataTreeInstance) throws ApplicationException;

    public void addMetaFileExtension(String extension);
    public List<String> getMetaFileExtensions();
    public String getMetaFileExtensionsString();
    public boolean isMetaFileExtension(String extension);

    public void setMetaFileName(String fileName) throws ApplicationException;
    public String getMetaFileName();

    public void build() throws ApplicationException;
    public void build(MetaObject metaObject) throws ApplicationException;
    public void build(File file) throws ApplicationException;
    public void build(OutputStream outputStream) throws ApplicationException;

    public boolean saveMetaFile(String fileName);
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

