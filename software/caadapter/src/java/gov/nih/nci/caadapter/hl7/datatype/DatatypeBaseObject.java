/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.util.PropertiesProvider;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

/**
 * The class defines HL7 DatatypesBaseObject.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2009-04-02 20:34:40 $
 */
public abstract class DatatypeBaseObject implements PropertiesProvider, MetaObject{

    DatatypeBaseObject parent;
    public PropertiesResult getPropertyDescriptors() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	public abstract boolean isEnabled();
	public abstract void setEnabled(boolean enable) ;

	public abstract boolean isOptionChosen();
	public abstract void setOptionChosen(boolean optionChosen);

	public String getXmlPath() {
		if (getParentXmlPath()==null||getParentXmlPath().equals(""))
			return getNodeXmlName();
		return getParentXmlPath()+"."+getNodeXmlName();
	}

	public void setXmlPath(String xmlPath)
	{
		//do nothing, the xmlPath will be dynamically generated
		//with current node name and its parent xmlPath
	}
	public abstract String getNodeXmlName();
	public abstract String getParentXmlPath();
	public abstract void setParentXmlPath(String newName);
	public abstract String getName();
	public abstract void setName(String newName);

    public void setParent(DatatypeBaseObject par)
    {
        parent = par;
    }

    public DatatypeBaseObject getParent()
    {
        return parent;
    }

//&umkis    public abstract String getAnnotation();
//&umkis    public abstract void setAnnotation(String annotation);
//&umkis    public abstract String getComment();
//&umkis    public abstract void setComment(String comment);


    /**
	 * This method is called to set tree icon if the object being
	 * set as userObject of a tree node
	 */
	public String toString()
	{
			return getName();
	}

}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.9  2008/12/18 17:07:12  wangeug
 * HISTORY :move the property:choiceSelected to children class
 * HISTORY :
 * HISTORY :Revision 1.8  2008/09/29 15:48:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */