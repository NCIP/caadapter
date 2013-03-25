/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.transformation.data;

import java.util.List;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.NullFlavorSetting;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.NullFlavorUtil;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 4, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.9 $
 * @date 	 DATE: $Date: 2009-04-01 15:13:14 $
 * @since caAdapter v4.2
 */

public class HL7XMLUtil {

	public static String NOT_PRESENT_NULLFLAVOR_DEFAULT="NP";

	public static void applyNullFlavorDefault(XMLElement element)
	{
		//as long the "inlineText is non-blank, "nullFlavor" is not required
		Attribute inlineTextAttr=getAttributByName("inlineText", element);
		if (inlineTextAttr!=null&&inlineTextAttr.getValue()!=null
				&&!inlineTextAttr.getValue().equals(""))
			return;

		//find the HL7 Datatype
		Attribute hl7dtAttr=getAttributByName("xsi:type", element);
		if (hl7dtAttr==null)
			return;

		String hl7Dt=hl7dtAttr.getValue();
		if (hl7Dt==null||hl7Dt.equals(""))
			return;

		// find the coreAttribute
		List<String> dtCores=NullFlavorUtil.findDatatypeCoreAttributes(hl7Dt);
		if (dtCores==null||dtCores.isEmpty())
			return;


			//check value the coreAttribute
			Attribute coreAttr=null;
			for (String oneAttrName:dtCores)
			{
				Attribute oneAttr =getAttributByName(oneAttrName,element);
				if (oneAttr!=null)
				{
					coreAttr=oneAttr;
					//stop checking if found one coreAttribute being set
					break;
				}
			}
			if (coreAttr==null)
			{
				Attribute  nfAttr=getAttributByName("nullFlavor", element);
				if (nfAttr!=null)
				{
					nfAttr.setValue(NOT_PRESENT_NULLFLAVOR_DEFAULT);
					return;
				}

				element.addAttribute("nullFlavor", NOT_PRESENT_NULLFLAVOR_DEFAULT,null,null,null);
				//move the "nullFlavor" as its first attribute
				int attrSize=element.getAttributes().size();
				Attribute nullAttr=element.getAttributes().get(attrSize-1);
				element.getAttributes().remove(nullAttr);
				element.getAttributes().add(0, nullAttr);
//				nullifyCoreAttribute(element, coreAttr.getName());
				return;
			}

			if (isElementNullFlavored(element)&&coreAttr!=null)
			{
				//the nullFlavor attribute value may be set with the defaultValue retrieved
				//from the Datatype of "nullFlavor" attribute
				//it may conflict with NullFlavorSetting and value of coreAttribute
				//reset the nullFlavor value in case the default is a list of key/value pair
				String coreAttrValue=coreAttr.getValue();
				if (coreAttrValue!=null&&
						(coreAttrValue.equalsIgnoreCase(GeneralUtilities.CAADAPTER_DATA_FIELD_NULL)
							||coreAttrValue.equalsIgnoreCase("\"\"")
						)
					)
				{
					coreAttrValue="NULL";
					coreAttr.setValue(coreAttrValue);
				}
				Attribute nullFlavorAttr=getAttributByName("nullFlavor", element);
				NullFlavorSetting nfSetting=new NullFlavorSetting(nullFlavorAttr.getValue());
				String nfAttrValue=(String)nfSetting.get(coreAttr.getValue());
				if (nfAttrValue!=null&&!nfAttrValue.equals(""))
				{
					nullFlavorAttr.setValue(nfAttrValue);
				}
				else
					element.getAttributes().remove(nullFlavorAttr);
				nullifyCoreAttribute(element, coreAttr.getName());
				return;
			}
		}

	/**
	 * As the second step of NullFlavor function, set "nullFlavor" attribute of target element
	  * <ul>
     *  <li>Case I: The input data is available but NullFlavorSetting is not, then<p>
     * 	CAADAPPTER_NULL_FLAVOR_ATTRIBUTE_VALUE:value was set with one attribute<p>
     *  Read NullFlavorSetting from H3S file or system default and retrieve value for "nullFlavor" attribute
     *
     * <li>Case II: NullFlavorSetting is available but input data is not, then <p>
     *  CAADAPPTER_NULL_FLAVOR_ATTRIBUTE_MARK:null<p>
     *  nullFlavor attribute was set NullFlavorSetting<p>
     *  Read the default value and use is a as key to retrieve value from NullFlavorSetting
     *
     * </ul>
	 * @param element
	 * @param datatype
	 */
	public static void applyNullFlavorFunctionSecondStep(XMLElement element, Datatype datatype)
	{
		//case I:
		//CAADAPPTER_NULL_FLAVOR_VALUE:value was set with one attribute<p
		Attribute coreAttr=getAttributByValue(GeneralUtilities.CAADAPTER_NULLFLAVOR_ATTRIBUTE_VALUE, element);
		if (coreAttr!=null)
		{

			String nullFlavorDefault=getDatatypeAttributeDefault(datatype, "nullFlavor");
			NullFlavorSetting nullFlavorSetting=new NullFlavorSetting(nullFlavorDefault);
			String nullFlavorKey=coreAttr.getValue();
			int semicolIndex=nullFlavorKey.indexOf(":");
			if (semicolIndex>-1)
				nullFlavorKey=nullFlavorKey.substring(semicolIndex+1);
			String nullFlavorValue=(String)nullFlavorSetting.get(nullFlavorKey);
			coreAttr.setValue(nullFlavorKey);
			Attribute typeAttribute=getAttributByName("xsi:type", element);
			element.getAttributes().remove(typeAttribute);
			//add "nullFlavor" attribute after "xsi:type" attribute
			element.addAttribute("nullFlavor", nullFlavorValue, null, null, null);
			element.getAttributes().add(typeAttribute);
			nullifyCoreAttribute(element, coreAttr.getName());
			return;
		}

		//Case II:
		//CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK was set with one attribute
		Attribute markAttr=getAttributByValue(GeneralUtilities.CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK, element);
		if (markAttr!=null)
		{
			Attribute nullFlavorAttribute=getAttributByName("nullFlavor", element);
			String nullFlavorString=null;
			if (nullFlavorAttribute!=null)
				nullFlavorString=nullFlavorAttribute.getValue();

			if (nullFlavorString==null)
				nullFlavorString=getDatatypeAttributeDefault(datatype, "nullFlavor");

			//read the default value of the marked Attribute
			String defaultAttrValue=getDatatypeAttributeDefault(datatype, markAttr.getName());
			if (defaultAttrValue==null)
				defaultAttrValue="NULL";
			markAttr.setValue(defaultAttrValue);
			if (defaultAttrValue.equals(""))
			{
				//use "BLANK" to retrieve NullFlavor value
				//but set value as ""
				defaultAttrValue="BLANK";
				markAttr.setValue("");
			}


			//read NullFlavor value using the defaultValue as key
			NullFlavorSetting nullFSetting=new NullFlavorSetting(nullFlavorString);
			String nullFlavorAttributeValue=(String)nullFSetting.get(defaultAttrValue);

			if (nullFlavorAttribute!=null)
				nullFlavorAttribute.setValue(nullFlavorAttributeValue);
			else
			{
				element.addAttribute("nullFlavor", nullFlavorAttributeValue, null, null, null);
			}
			nullifyCoreAttribute(element, markAttr.getName());
			return;
		}
		//Case II:
		//the nullFlavor attribute value may be set with the defaultValue retrieved
		//from the Datatype of "nullFlavor" attribute
		//it may conflict with NullFlavorSetting and value of coreAttribute
//		if (isElementNullFlavored(element))
//		{
//			String nullFDefautSt=getAttributByName("nullFlavor", element).getValue();
//
//			to be process in cleanNullFlavor()
//			return;
//		}
	}

	private  static void nullifyCoreAttribute(XMLElement element, String coreAttributeName)
	{
    	String nullifyMissingData=CaadapterUtil.findApplicationConfigValue(Config.CAADAPTER_COMPONENT_HL7_MISSING_DATA_NULLFLAVOR_NULLIFIED);

    	if (nullifyMissingData==null)
    		return;
    	if(!nullifyMissingData.equalsIgnoreCase("true"))
		 	return;
    	Attribute coreAttr=getAttributByName(coreAttributeName, element);
    	if (coreAttr!=null)
    		element.getAttributes().remove(coreAttr);

	}
	/**
	 * Clean NullFlavor setting with an XMLElement
	 * <ul>
	 * <li>An element without child:
	 * set nullFlavor if and only if the value/coreAttribute of this element it null
	 * <li>An element with one or more children:
	 * set nullFlavor if its value/coreAttribute is null
	 * 			and ALL its children have nullFlavore being set
	 * @param element
	 */
	public static void cleanNullFlavor(XMLElement element)
	{
		applyNullFlavorDefault(element);
		//do not re-set "nullFlavor" for leaf element
		if (element.getChildren().isEmpty())
		{
			return;
		}
		//re-set the "nullFlavor" based on its children
		boolean isAllChildrenNullFlavored=true;
		for(XMLElement child:element.getChildren())
		{
			//re-set "nullFlavor" for all children
			cleanNullFlavor(child);
			if (!isElementNullFlavored(child))
			{
				isAllChildrenNullFlavored=false;
			}
		}

		if (!isElementNullFlavored(element))
		{
			for (Attribute xmlAttr:element.getAttributes())
			{
				String attrValueTemp=xmlAttr.getValue();
				if(attrValueTemp!=null&&attrValueTemp.equalsIgnoreCase(GeneralUtilities.CAADAPTER_DATA_FIELD_NULL))
		    	{//Null read from CSV
		    		xmlAttr.setValue(null);
		    	}
			}
			return;
		}


		//remove "nullFlavor" attribute if not all children nullFlavored
		if (!isAllChildrenNullFlavored)
		{
			for (Attribute xmlAttr:element.getAttributes())
			{
				String attrName=xmlAttr.getName();
				if (attrName!=null&&attrName.equalsIgnoreCase("nullFlavor"))
				{
					Log.logInfo(element, element.getName()+" remove nullFlavore attribute:"+xmlAttr.getValue());
					element.getAttributes().remove(xmlAttr);
					break;
				}
			}
		}
	}

	private static Attribute getAttributByName(String attributeName, XMLElement element)
	{
		if (attributeName==null||attributeName.equals(""))
			return null;

		Attribute rtnAttr=null;
		for (Attribute elmAttr:element.getAttributes())
		{
			if (elmAttr.getName().startsWith(attributeName))
			{
				rtnAttr=elmAttr;
				break;
			}
		}

		return rtnAttr;
	}

	private static Attribute getAttributByValue(String attributeValue, XMLElement element)
	{
		if (attributeValue==null||attributeValue.equals(""))
			return null;

		Attribute rtnAttr=null;
		for (Attribute elmAttr:element.getAttributes())
		{
			if (elmAttr.getValue()!=null&&elmAttr.getValue().startsWith(attributeValue))
			{
				rtnAttr=elmAttr;
				break;
			}
		}

		return rtnAttr;
	}
	/**
	 * Instantiate a NullFlavorSetting instance using the default value of the "nullFlavor" attribute of a HL7 datatype
	 * @param datatype
	 * @return
	 */
	private static String getDatatypeAttributeDefault(Datatype datatype, String attributeName)
	{
		if (datatype==null)
			return null;
		if (attributeName==null||attributeName.equals(""))
			return null;
		gov.nih.nci.caadapter.hl7.datatype.Attribute dtAttr=null;
		dtAttr=(gov.nih.nci.caadapter.hl7.datatype.Attribute)datatype.getAttributes().get(attributeName);

		String rtnDefaultValue=dtAttr.getDefaultValue();
		return rtnDefaultValue;
	}

	private static boolean isElementNullFlavored(XMLElement element)
	{
		if (element.getAttributes().isEmpty())
			return false;

		Attribute nullFAttr=getAttributByName("nullFlavor", element);
		if (nullFAttr!=null)
			return true;

		return false;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.8  2009/03/06 18:32:02  wangeug
* HISTORY: enable web services
* HISTORY:
* HISTORY: Revision 1.7  2009/02/19 19:48:41  wangeug
* HISTORY: check null value with  "nullFlavor" attribute
* HISTORY:
* HISTORY: Revision 1.6  2009/02/12 19:50:18  wangeug
* HISTORY: avoid duplicated "nullFlavor" attribute
* HISTORY:
* HISTORY: Revision 1.5  2009/02/09 21:42:45  wangeug
* HISTORY: correct errors in "nullFlavor" setting: set value with "nullFlavor" attribute only if a NULLFLAVOR constant being found with the value of a "coreAttribute"
* HISTORY:
* HISTORY: Revision 1.4  2009/01/14 20:30:24  wangeug
* HISTORY: only process "inlineText" as core attribute if defined by user/system for a datatype
* HISTORY:
* HISTORY: Revision 1.3  2009/01/12 17:46:57  wangeug
* HISTORY: nullify  coreAttribute if nullFlavor is set
* HISTORY:
* HISTORY: Revision 1.2  2009/01/09 21:33:52  wangeug
* HISTORY: apply default value with nullFlavor attributes
* HISTORY:
* HISTORY: Revision 1.1  2008/12/04 20:41:20  wangeug
* HISTORY: support nullFlavor
* HISTORY:
**/