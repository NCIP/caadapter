package gov.nih.nci.caadapter.hl7.map.impl;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

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
	
	private void initLookupTable(MIFClass mif)
	{
		table.put(mif.getXmlPath(), mif);
		//process attribute
		for (MIFAttribute mifAttr:mif.getAttributes())
		{
			table.put(mifAttr.getXmlPath(), mifAttr);
			//process datatype attribute
			Datatype attrDt=mifAttr.getDatatype();
			if (attrDt==null)
				continue;
			
			Hashtable dtAttrs=attrDt.getAttributes();
			Enumeration childAttrsEnum=dtAttrs.elements();
			while (childAttrsEnum.hasMoreElements())
			{
				Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
				table.put(childAttr.getXmlPath(), childAttr);
			}
		}
		//process assocations
		if(mif.getAssociations()!=null)
			for(MIFAssociation assc:mif.getAssociations())
			{
				MIFClass asscMif=assc.getMifClass();
				initLookupTable(asscMif);
			}
		
		//process choices
		if (mif.getChoices()!=null)
			for(MIFClass choice:mif.getChoices())
			{
				if (choice.isChoiceSelected())
					initLookupTable(choice);
			}
	}

}
