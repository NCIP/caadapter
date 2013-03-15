/*L
 * Copyright SAIC.
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
//		if(getXmlPath()!=null)
//			return getXmlPath();
//		else
			return getName();
	}

}
