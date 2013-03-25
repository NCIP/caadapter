/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.map.impl;

/**
 * The class defines a lookup utility for MIF item.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2009-02-12 19:44:11 $
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
		if (mif.getSortedChoices()!=null)
			for(MIFClass choice:mif.getSortedChoices())
			{
				if (choice.isChoiceSelected())
					initLookupTable(choice);
				//MIFAttribute and MIFAssociation are required
				//if the choice item is a list of other choice items
				if(choice.getSortedChoices().size()>0)
				{
					initLookupTable(choice);
					for (MIFClass listChoiceItem:choice.getSortedChoices())
						if (listChoiceItem.isChoiceSelected())
							initLookupTable(listChoiceItem);
				}
			}
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.8  2009/01/06 17:45:33  wangeug
 * HISTORY :add item to MifMetaLookup if it is a chosenItem or it is a list of ohter choice items
 * HISTORY :
 * HISTORY :Revision 1.7  2009/01/05 16:39:54  wangeug
 * HISTORY :Process MIFClass with isAbstract=true
 * HISTORY :
 * HISTORY :Revision 1.6  2008/09/29 15:45:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */