/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;


/**
 * A simple implementation of the DataObject interface.
 *
 * @author OWNER: Eric Chen  Date: Jun 8, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public class DataObjectImpl extends BaseObjectImpl implements DataObject
{

	private MetaObject metaObject;

	// constructors.
	public DataObjectImpl(MetaObject metaObject)
	{
		this.metaObject = metaObject;
	}

	// setters and getters.
	public MetaObject getMetaObject()
	{
		return metaObject;
	}

	public void setMetaObject(MetaObject metaObject)
	{
		this.metaObject = metaObject;
	}

	// other methods.
	public String getName()
	{
		return metaObject.getName();
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:51  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:27:13  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/28 21:50:46  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/06 19:35:11  giordanm
 * HISTORY      : prettying the code in the common package.  javadoc, license headers, etc.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/18 22:18:26  jiangsc
 * HISTORY      : First implementation of the Log functions.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/07 17:09:33  giordanm
 * HISTORY      : changes to the DataObject interface and DataObjectImpl class..
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/08 22:29:52  chene
 * HISTORY      : Update Clone Data implementation
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/08 17:30:02  chene
 * HISTORY      : First Cut of HL7 Data Objects
 * HISTORY      :
 */

