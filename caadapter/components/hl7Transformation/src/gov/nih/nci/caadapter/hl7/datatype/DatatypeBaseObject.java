package gov.nih.nci.caadapter.hl7.datatype;

public abstract class DatatypeBaseObject {

	private boolean enabled =true;
	private boolean choiceSelected =true;
	private String xmlPath;
	
	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}
	
	public boolean isChoiceSelected() {
		return choiceSelected;
	}

	public void setChoiceSelected(boolean choiceSelected) {
		this.choiceSelected = choiceSelected;
	}
	/**
	 * This method is called to set tree icon if the object being
	 * set as userObject of a tree node
	 */
	public String toString()
	{
		return getName();
	}
	public abstract String getName();
	public abstract void setName(String newName);

}
