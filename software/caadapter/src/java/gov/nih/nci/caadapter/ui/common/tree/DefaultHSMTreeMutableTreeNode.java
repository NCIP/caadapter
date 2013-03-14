/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;


/**
 * This class defines a customized tree node used in HSM tree manipulation.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class DefaultHSMTreeMutableTreeNode extends DefaultMappableTreeNode
{
	private MIFClass rootMif=null;
	/**
	 * Default constructor to create root MIFClass tree node
	 * @param userObject
	 * @param allowsChildren
	 */
	public DefaultHSMTreeMutableTreeNode(Object userObject, boolean allowsChildren)
	{
		this(userObject, allowsChildren, null);
	}
	/**
	 * Construct a tree node given root MIFClass
	 * @param userObject valid objects include MIFClass, MIFAssociation, MIFAttribute
	 * @param allowsChildren
	 * @param rootClass
	 */
	public DefaultHSMTreeMutableTreeNode(Object userObject, boolean allowsChildren, MIFClass rootClass)
	{
		super(userObject, allowsChildren);
		rootMif=rootClass;
	}

	public MIFClass getRootMif() {
		return rootMif;
	}
	public void setRootMif(MIFClass rootMif) {
		this.rootMif = rootMif;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/08/23 17:54:15  wangeug
 * HISTORY      : set rootMIFClass reference for each MIFClass tree node
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/12 18:38:11  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 */
