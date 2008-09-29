/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.util.PropertiesProvider;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

/**
 * The class defines HL7 DatatypesBaseObject.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2008-09-29 15:48:56 $
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

	private boolean choiceSelected =false;

	
	public boolean isChoiceSelected() {
		return choiceSelected;
	}

	public void setChoiceSelected(boolean choiceSelected) {
		this.choiceSelected = choiceSelected;
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
 */