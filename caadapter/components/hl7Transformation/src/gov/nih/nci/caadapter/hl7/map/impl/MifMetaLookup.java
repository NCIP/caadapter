/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.map.impl;

/**
 * The class defines a lookup utility for MIF item.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2009-01-05 16:39:54 $
 */
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import gov.nih.nci.caadapter.common.MetaLookup;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;

public class MifMetaLookup implements MetaLookup {
	private Hashtable<String, MetaObject> table = new Hashtable<String, MetaObject>();
	public MifMetaLookup(MIFClass mifClass)
	{
		initLookupTable(mifClass);
	}
	public Set getAllKeys() {
		// TODO Auto-generated method stub
		Set <String>keySet = new HashSet<String>(table.keySet());
		return keySet;
	}

	public MetaObject lookup(String uuid) {
		// TODO Auto-generated method stub
		return table.get(uuid);
	}
	
	private void processDatatype(Datatype datatype) 
	{
		if (datatype==null)
			return;
		
		Hashtable dtAttrs=datatype.getAttributes();
		Enumeration<Attribute> childAttrsEnum=dtAttrs.elements();
		while (childAttrsEnum.hasMoreElements())
		{
			boolean isSimple = false;
			Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
			table.put(childAttr.getXmlPath(), childAttr);
    		if (childAttr.getReferenceDatatype() == null) {
    			isSimple = true;
    		}
    		else {
    			if (childAttr.getReferenceDatatype().isSimple()) isSimple = true;
    		}
    		if (isSimple) {
    			continue;
    		}
    		else { //complexdatatype
    			processDatatype(childAttr.getReferenceDatatype());
    		}
		}
	}
	private void initLookupTable(MIFClass mif)
	{
		table.put(mif.getXmlPath(), mif);
		//process attribute
		for (MIFAttribute mifAttr:mif.getAttributes())
		{
			table.put(mifAttr.getXmlPath(), mifAttr);
			//process datatype attribute
			Datatype attrDt=mifAttr.getConcreteDatatype();
			if (attrDt==null)
				attrDt=mifAttr.getDatatype();
			processDatatype(attrDt);
		}
		//process assocations
		if(mif.getSortedAssociations()!=null)
			for(MIFAssociation assc:mif.getSortedAssociations())
			{
				MIFClass asscMif=assc.getMifClass();
				table.put(assc.getXmlPath(), assc);
				initLookupTable(asscMif);
				
			}
		
		//process choices
		if (mif.getChoices()!=null)
			for(MIFClass choice:mif.getChoices())
			{
				if (choice.isChoiceSelected())
					initLookupTable(choice);
				else if(!choice.getChoices().isEmpty())
				{
					for (MIFClass listChoiceItem:choice.getChoices())
						if (listChoiceItem.isChoiceSelected())
							initLookupTable(listChoiceItem);
				}
			}
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.6  2008/09/29 15:45:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */