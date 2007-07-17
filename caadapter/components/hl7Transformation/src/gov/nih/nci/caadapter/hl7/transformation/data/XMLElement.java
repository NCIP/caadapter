/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.transformation.data;

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
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-17 20:42:23 $
 */
public class XMLElement implements Cloneable{
	
	private String name;
	private Hashtable<String, String> attributes = new Hashtable<String, String>();
	private Vector<XMLElement> children = new Vector<XMLElement>();
	private ArrayList<String> segments = new ArrayList<String>();

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
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	/**
	 * @return the attributes
	 */
	public Hashtable<String, String> getAttributes() {
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
		addAttribute("xmlns","urn:hl7-org:v3");
		addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		output.append(toXMLBody(0));
		return output;
	}
	public StringBuffer toXMLBody(int level) {
		boolean addBody = false;
		String bodyValue = "";
		StringBuffer output = new StringBuffer();
		for(int i = 0; i<level;i++) output.append("   ");
		output.append("<" + getName());
		for(String key:attributes.keySet()) {
			if (key.equalsIgnoreCase("inlineText")) {
				addBody = true;
				bodyValue = attributes.get(key);
			}
			else {
				output.append(" "+key+"="+"\""+attributes.get(key)+"\"");
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
}
