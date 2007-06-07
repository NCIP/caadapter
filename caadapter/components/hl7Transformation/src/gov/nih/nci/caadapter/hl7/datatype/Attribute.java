/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import java.io.Serializable;


/**
 * The class defines attributes of a HL7 Datatype.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.4 $ date $Date: 2007-06-07 15:00:41 $
 */

public class Attribute extends DatatypeBaseObject implements Serializable, Cloneable  {
	static final long serialVersionUID = 1L;
	
	private String name;

	private String type;

	private int min;

	private int max;

	private String defaultValue;

	private boolean optional;

	private boolean prohibited;

	private boolean attribute;
	private boolean optionChosen = false;
	
	/**
	 * Return ture if the attribute is visible/accessable/allowed, 
	 * return false, otherwise.
	 * 
	 * @return boolean 
	 */
	public boolean isValid() {
		if (isProhibited())
			return false;
		if (min == 0 && max == 0)
			return false;
		return true;
	}

	/**
	 * @return the attribute
	 */
	public boolean isAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the prohibited
	 */
	public boolean isProhibited() {
		return prohibited;
	}

	/**
	 * @param prohibited the prohibited to set
	 */
	public void setProhibited(boolean prohibited) {
		this.prohibited = prohibited;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public Object clone()
	{
		 try {
             Attribute clonnedObj = (Attribute)super.clone();

             return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

	}
	@Override
	public boolean isOptionChosen() {
		// TODO Auto-generated method stub
		return  optionChosen ;
	}
	@Override
	public void setOptionChosen(boolean option) {
		// TODO Auto-generated method stub
		optionChosen=option;
	}
}
