package gov.nih.nci.caadapter.hl7.datatype;

public abstract class DatatypeBaseObject {

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

	public abstract String getNodeXmlName();
	public abstract String getParentXmlPath();
	public abstract void setParentXmlPath(String newName);
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
