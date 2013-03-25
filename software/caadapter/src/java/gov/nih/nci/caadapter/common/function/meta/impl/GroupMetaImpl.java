/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.function.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.GroupMeta;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides default implementation of GroupMeta.
 *
 * @author OWNER: Jayfus Doswell
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */
public class GroupMetaImpl extends MetaObjectImpl implements GroupMeta
{
    private String strGroupName;
    private List<FunctionMeta> lstfunctionList;


    public String getGroupName()
    {
        return strGroupName;
    }

    public void setGroupName(String strNewGroupName)
    {
        this.strGroupName = strNewGroupName;
		this.name = strNewGroupName;
    }

    public List<FunctionMeta> getFunctionList()
    {
       return lstfunctionList;
    }

    public void setFunctionList(List<FunctionMeta> lstNewFunctionList)
    {
        lstfunctionList = lstNewFunctionList;
    }

	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 *
	 * @return the title of this provider that may be used to distinguish from others.
	 */
	public String getTitle()
	{
		return "Function Group Properties";    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 * <p/>
	 * descendant classes are free to override to provide additional information.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception
	{
		Class beanClass = this.getClass();

		PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
		PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "getClassName", null);
//		PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
// 		{ _name, _class };
//		return propertiesArray;
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(_name);
		propList.add(_class);
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/18 17:03:28  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/23 18:57:16  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/27 22:35:15  jiangsc
 * HISTORY      : Added seveal enhancement:
 * HISTORY      : 1) file header and footer;
 * HISTORY      : 2) property support;
 * HISTORY      : 3) removal of set and get UUID methods;
 * HISTORY      :
 */
