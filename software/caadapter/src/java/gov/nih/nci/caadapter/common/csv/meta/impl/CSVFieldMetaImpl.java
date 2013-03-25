/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a field metadata (contained within csv segment).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.8 $
 * @date        $Date: 2008-09-24 20:40:14 $
 */

public class CSVFieldMetaImpl extends MetaObjectImpl implements CSVFieldMeta {
	private static final String LOGID = "$RCSfile: CSVFieldMetaImpl.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVFieldMetaImpl.java,v 1.8 2008-09-24 20:40:14 phadkes Exp $";

	int column;
//	String name;
	CSVSegmentMeta segment;

	public CSVFieldMetaImpl(int column, String name, CSVSegmentMeta segment) {
		super(name);
        this.column = column;
		this.segment = segment;
	}

	public int getColumn() {
		return column;
	}

//	public String getName() {
//		return name;
//	}

	public void setColumn(int column) {
		this.column = column;
	}

//	public void setName(String name) {
//		this.name = name;
//	}

	public String getSegmentName() {
		return segment.getName();
	}

	public CSVSegmentMeta getSegment() {
		return segment;
	}

	public void setSegment(CSVSegmentMeta newSegmentMeta)
	{
		this.segment = newSegmentMeta;
	}

	public String toString() {
		return getColumn() + " : " + getName();
	}

	/**
	 * Will clone column and name value. Yet it will share the same segment information.
	 * @param copyUUID if true, the cloned object will share the same uuid value of this object; otherwise, it will have different UUID value.
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object clone(boolean copyUUID) throws CloneNotSupportedException
	{
		CSVFieldMetaImpl metaObj = (CSVFieldMetaImpl) super.clone(copyUUID);
		metaObj.segment = this.segment;
		metaObj.setColumn(this.getColumn());
		metaObj.setName(this.getName());
		return metaObj;
	}

	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 * @return the title of this provider that may be used to distinguish from others.
	 */
	public String getTitle()
	{
		return "CSV Field Properties";
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception
	{
		Class beanClass = this.getClass();

		PropertyDescriptor _parentSegmentName = new PropertyDescriptor("SegmentName", beanClass, "getSegmentName", null);
		PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
		PropertyDescriptor _xmlPath = new PropertyDescriptor("XmlPath", beanClass, "getXmlPath", null);
		PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "getClassName", null);
//		PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
// 		{ _parentSegmentName, _name, _class };
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(_parentSegmentName);
		propList.add(_name);
		propList.add(_xmlPath);
		propList.add(_class);
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
	public String getXmlPath()
	{
		if (getSegment()==null)
			return getName();
		return getSegment().getXmlPath()+"."+getName();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/

