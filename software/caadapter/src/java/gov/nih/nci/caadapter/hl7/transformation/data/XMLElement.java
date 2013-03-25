/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.transformation.data;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * The class contains final xml tree for the v3 messages.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.28 $
 *          date        $Date: 2009-03-13 14:53:28 $
 */
public class XMLElement implements Cloneable{

	private String name;
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private Vector<XMLElement> children = new Vector<XMLElement>();
	private ArrayList<String> segments = new ArrayList<String>();
	private ValidatorResults validatorResults = null;
	private boolean hasUserMappedData = false;
	private boolean hasDefaultMappedData = false;
	private String domainName;
	private String codingStrength;
	private boolean populated = false;
	private String messageType;
	private MIFClass mifClass;
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
		int existingLastIndx=-1;
		for(int i=0;i<children.size();i++)
			if (children.get(i).getName().equalsIgnoreCase(child.getName()))
			{
				existingLastIndx=i;
			}
		if (existingLastIndx>-1)
			children.add(existingLastIndx+1, child);
		else
			children.add(child);
	}

	/**
	 * @param children the children XMLElements to be added
	 */
	public void addChildren(List<XMLElement> children) {
		if (children==null)
			return;
		for(XMLElement child:children)
			addChild(child);
//			this.children.add(child);
	}
	/**
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(String name, String value, String datatype, String domainName, String codingStrength) {

//		System.out.println("name:" + name + " value:"+value+ " type:"+datatype);
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setValue(value);
		attribute.setDatatype(datatype);
		attribute.setDomainName(domainName);
		attribute.setCodingStrength(codingStrength);
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
	/**
	 * @return the validatorResults
	 */
	public ValidatorResults getValidatorResults() {
		return validatorResults;
	}
	public void populateValidatorResults() {
		if (!populated)
		{
			validatorResults.addValidatorResults(getValidatorResults(false));
			populated = true;
		}
	}
	public ValidatorResults getValidatorResults(boolean flag) {
		ValidatorResults validatorResults_temp = new ValidatorResults();
		if (flag)
			validatorResults_temp.addValidatorResults(validatorResults);
		for (XMLElement xmlElement:children) {
			ValidatorResults validatorResults_sub = xmlElement.getValidatorResults(true);
			if (validatorResults_sub == null) continue;
			if (validatorResults_sub.getAllMessages().size() == 0) continue;
			validatorResults_temp.addValidatorResults(validatorResults_sub);
		}
		return validatorResults_temp;
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

	public String toString() {
		return this.toXML().toString();
	}

	public StringBuffer toXML() {
		StringBuffer output = new StringBuffer();
		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		MIFClass rootMIFClass = getMifClass();
		Hashtable<String, String> comments = rootMIFClass.getPackageLocation();
		if (comments!=null)
		{
			Set<String> keys = comments.keySet();
			if (keys.size() > 0) {
				output.append("<!--");
			}
			for(String key:keys)
			{
				output.append(" " + key + "=\'" + comments.get(key) + "\'");
			}
			if (keys.size() > 0) {
				output.append("-->\n");
			}
		}
		HL7XMLUtil.cleanNullFlavor(this);
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
			if (key.equalsIgnoreCase("inlineText")) {
				addBody = true;
				bodyValue = value;
			}
			else //if (value!=null)
				output.append(" "+key+"="+"\""+value+"\"");

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
				output.append(bodyValue.replaceAll(CaadapterUtil.LINEFEED_ENCODE, "\n"));
				output.append("</" + getName() + ">\n");

			}
		}
		return output;
	}

	public ValidatorResults validate() {
		try {
			Hashtable datatypes=DatatypeParserUtil.getDatatypeParser().getDatatypes();
			ValidatorResults validatorResults_sub = validate(datatypes, name);
//			if (validatorResults_sub == null) return validatorResults;
//			if (validatorResults_sub.getAllMessages().size() == 0) return validatorResults;
//			validatorResults.addValidatorResults(validatorResults_sub);
			return validatorResults_sub;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ValidatorResults validate(Hashtable datatypes, String pXmlPath) {
		ValidatorResults validatorResults_temp = new ValidatorResults();

		boolean checkCode = false;
		if (getDomainName()!= null && !getDomainName().equals(""))
		{
			checkCode = true;
		}
		for(Attribute attribute: attributes) {
			//Domain related validation
			if (attribute.getDomainName()!= null && !attribute.getDomainName().equals("")||(checkCode && attribute.getName().equals("code")))
			{
				Datatype datatype;
				if (attribute.getDomainName()!= null && !attribute.getDomainName().equals(""))
					datatype = (Datatype)datatypes.get(attribute.getDomainName());
				else
					datatype = (Datatype)datatypes.get(getDomainName());
				if (datatype!= null)
				{
					HashSet predefinedValues = datatype.getPredefinedValues();
					if (predefinedValues.size()>0) {
						if (!predefinedValues.contains(attribute.getValue()))
						{
				            if (attribute.getCodingStrength()!=null && attribute.getCodingStrength().equals("CNE")||(getCodingStrength()!=null && getCodingStrength().equals("CNE")))
				            {
					            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute: " + pXmlPath + "." + attribute.getName() + " does not contain valid value (" + attribute.getValue() + "); valid value(s) are:"+predefinedValues});
				            	validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
				            }
//				            else
//				            {
//					            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute: " + pXmlPath + "." + attribute.getName() + " 	may not contain valid value (" + attribute.getValue() + ")"});
//				            	validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
//				            }
						}
					}
					else {
			            if (attribute.getCodingStrength()!=null && attribute.getCodingStrength().equals("CNE")||(getCodingStrength()!=null && getCodingStrength().equals("CNE")))
			            {
//				            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute" + pXmlPath + "." + attribute.getName() + " does not contain valid value (" + attribute.getValue() + ")"});
			            	Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute: " + pXmlPath + "." + attribute.getName() + " may not contain valid value (" + attribute.getValue() + "); validation is stopped since no value defined with domain: "+getDomainName()});
			            	validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO,msg));//.WARNING, msg));//.ERROR, msg));
			            }
//			            else
//			            {
//				            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute: " + pXmlPath + "." + attribute.getName() + " may not contain valid value (" + attribute.getValue() + "); validation is stopped since no value defined with domain: "+getDomainName()});
//			            	validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
//			            }
					}
				}
			}

			if (attribute.getDatatype() != null)
			{
				if (!attribute.getDatatype().equals(""))
				{
					String dt = attribute.getDatatype();
					if (dt.equals("cs")) continue;

//					System.out.println("Validating:"+pXmlPath + "." + attribute.getName());

					Datatype datatype = (Datatype)datatypes.get(dt);
					if (datatype!= null)
					{
						HashSet predefinedValues = datatype.getPredefinedValues();
						if (predefinedValues.size()>0) {
							if (!predefinedValues.contains(attribute.getValue()))
							{
					            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute" + pXmlPath + "." + attribute.getName() + " does not contain valid value (" + attribute.getValue() + ")"});
					            validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
							}
						}

						if (datatype.getPatterns().size() > 0)
						{
							String vPattern = "";
							boolean patternMatch = false;
							for (String pattern:datatype.getPatterns())
							{
								vPattern = vPattern + " " + pattern;
								if (attribute.getValue().matches(pattern)) {
									patternMatch = true;
									break;
								}
							}
							if (!patternMatch) {
					            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Attribute" + pXmlPath + "." + attribute.getName() + " does not match attribute pattern " + vPattern + "(" + attribute.getValue() + ")"});
					            validatorResults_temp.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
							}
						}
					}
				}
			}
		}
		for (XMLElement xmlElement:children) {
			ValidatorResults validatorResults_sub = xmlElement.validate(datatypes, pXmlPath+"."+xmlElement.getName());
			if (validatorResults_sub == null) continue;
			if (validatorResults_sub.getAllMessages().size() == 0) continue;
			validatorResults_temp.addValidatorResults(validatorResults_sub);
		}
		return validatorResults_temp;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	/**
	 * @return the hasDefaultMappedData
	 */
	public boolean hasDefaultMappedData() {
		return hasDefaultMappedData;
	}
	/**
	 * @param hasDefaultMappedData the hasDefaultMappedData to set
	 */
	public void setHasDefaultMappedData(boolean hasDefaultMappedData) {
		this.hasDefaultMappedData = hasDefaultMappedData;
	}
	/**
	 * @return the codingStrength
	 */
	public String getCodingStrength() {
		return codingStrength;
	}
	/**
	 * @param codingStrength the codingStrength to set
	 */
	public void setCodingStrength(String codingStrength) {
		this.codingStrength = codingStrength;
	}
	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}
	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	/**
	 * @return the mifClass
	 */
	public MIFClass getMifClass() {
		return mifClass;
	}
	/**
	 * @param mifClass the mifClass to set
	 */
	public void setMifClass(MIFClass mifClass) {
		this.mifClass = mifClass;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.27  2009/03/06 18:31:39  wangeug
 * HISTORY :enable web services
 * HISTORY :
 * HISTORY :Revision 1.26  2009/01/09 21:36:02  wangeug
 * HISTORY :check null as adding children
 * HISTORY :
 * HISTORY :Revision 1.25  2008/12/04 20:40:39  wangeug
 * HISTORY :support nullFlavor
 * HISTORY :
 * HISTORY :Revision 1.24  2008/10/29 19:06:08  wangeug
 * HISTORY :read Datatype from xsd
 * HISTORY :
 * HISTORY :Revision 1.23  2008/09/29 15:39:06  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */