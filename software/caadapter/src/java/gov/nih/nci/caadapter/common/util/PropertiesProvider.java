/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.util;




/**
 * Define the interface to provide underline data or meta bean's properties information
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public interface PropertiesProvider
{
	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 * @return the title of this provider that may be used to distinguish from others.
	 */
	String getTitle();
	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception;
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 18:57:19  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/18 19:46:22  jiangsc
 * HISTORY      : Added textual display for functions and properties.
 * HISTORY      : Beautified port display.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 18:17:58  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 */
