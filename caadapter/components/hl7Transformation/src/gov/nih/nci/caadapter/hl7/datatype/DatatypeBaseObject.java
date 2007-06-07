package gov.nih.nci.caadapter.hl7.datatype;

public abstract class DatatypeBaseObject {

	private boolean choiceSelected =false;
	private boolean enabled = true;
	private String xmlPath;
	
	public boolean isChoiceSelected() {
		return choiceSelected;
	}

	public void setChoiceSelected(boolean choiceSelected) {
		this.choiceSelected = choiceSelected;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public abstract boolean isOptionChosen();
	public abstract void setOptionChosen(boolean optionChosen);

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public abstract String getName();
	public abstract void setName(String newName);

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
