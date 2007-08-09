/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.transformation.data;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * The class contains final xml tree for the v3 messages.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2007-08-09 20:17:26 $
 */
public class XMLElement implements Cloneable{
	
	private String name;
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private Vector<XMLElement> children = new Vector<XMLElement>();
	private ArrayList<String> segments = new ArrayList<String>();
	private ValidatorResults validatorResults = null;
	private boolean hasUserMappedData = false;

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
	 * @param child the child XMLElement to be added
	 */
	public void addChild(XMLElement child) {
		children.add(child);
	}

	/**
	 * @param children the children XMLElements to be added
	 */
	public void addChildren(List<XMLElement> children) {
		for(XMLElement child:children)
			this.children.add(child);
	}
	/**
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(String name, String value, String datatype) {
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setValue(value);
		attribute.setDatatype(datatype);
		attributes.add(attribute);
	}

	/**
	 * @return the attributes
	 */
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the children
	 */
	public Vector<XMLElement> getChildren() {
		return children;
	}

	public XMLElement clone() {
		  try {
		    return (XMLElement)super.clone();
		  } catch (CloneNotSupportedException e) {
		    return null; // never invoked
		  }
		}

	public ArrayList<String> getSegments() {
		return segments;
	}

	public void addSegment(String segment) {
		segments.add(segment);
	}
	
	public StringBuffer toXML() {
		StringBuffer output = new StringBuffer();
		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//		addAttribute("xmlns","urn:hl7-org:v3");
//		addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		output.append(toXMLBody(0));
		return output;
	}
	public StringBuffer toXMLBody(int level) {
		boolean addBody = false;
		String bodyValue = "";
		StringBuffer output = new StringBuffer();
		for(int i = 0; i<level;i++) output.append("   ");
		output.append("<" + getName());
		
		if (level == 0) {
			output.append(" xmlns=\"urn:hl7-org:v3\"");
			output.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");			
		}
		for(Attribute attribute:attributes) {
			String key = attribute.getName();
			String value = attribute.getValue();
//			if (key.equals("extension")) {
//				output.append(" extension="+"\"abac\"");
//				continue;
//			}
			if (key.equalsIgnoreCase("inlineText")) {
				addBody = true;
				bodyValue = value;
			}
			else {
				output.append(" "+key+"="+"\""+value+"\"");
			}
		}
		
		if (getChildren().size() == 0&&!addBody) {
			output.append("/>\n");
		}
		else {
			if (!addBody) {
				output.append(">\n");
				for(XMLElement xmlElement:getChildren()) {
					output.append(xmlElement.toXMLBody(level+1));
				}
				for(int i = 0; i<level;i++) output.append("   ");
				output.append("</" + getName() + ">\n");
			}
			else {
				output.append(">");
				output.append(bodyValue);
				output.append("</" + getName() + ">\n");
				
			}
		}
		return output;
	}
	/**
	 * @return the validatorResults
	 */
	public ValidatorResults getValidatorResults() {
		return validatorResults;
	}
	/**
	 * @param validatorResults the validatorResults to set
	 */
	public void setValidatorResults(ValidatorResults validatorResults) {
		this.validatorResults = validatorResults;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}
	/**
	 * @return the hasUserMappedData
	 */
	public boolean hasUserMappedData() {
		return hasUserMappedData;
	}
	/**
	 * @param hasUserMappedData the hasUserMappedData to set
	 */
	public void setHasUserMappedData(boolean hasUserMappedData) {
		this.hasUserMappedData = hasUserMappedData;
	}
}
