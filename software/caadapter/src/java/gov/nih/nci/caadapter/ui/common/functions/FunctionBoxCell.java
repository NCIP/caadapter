/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

import javax.swing.tree.MutableTreeNode;

/**
 * This class defines a distinguished Cell from other type of cells for customized rendering.
 * It will also provide some utility functions to facilitate lookup, etc.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class FunctionBoxCell extends DefaultGraphCell // implements MappableNode
{
	public FunctionBoxCell(Object userObject)
	{
		this(userObject, null);
	}

	public FunctionBoxCell(Object userObject, AttributeMap storageMap)
	{
		this(userObject, storageMap, null);
	}

	public FunctionBoxCell(Object userObject, AttributeMap storageMap, MutableTreeNode[] children)
	{
		super(userObject, storageMap, children);
		if(userObject instanceof FunctionBoxMutableViewInterface)
		{
			((FunctionBoxMutableViewInterface)userObject).setFunctionalBoxCell(this);
		}
	}

	/**
	 * Based on the give parameter meta, find the corresponding DefaultPort.
	 * Return null, if nothing is found.
	 * @param paramMeta
	 * @return the corresponding DefaultPort.
	 */
	public FunctionBoxDefaultPort findPortByParameterMeta(ParameterMeta paramMeta)
	{
		FunctionBoxDefaultPort result = null;
		int count = this.getChildCount();
		for(int i=0; i<count; i++)
		{
			FunctionBoxDefaultPort port = (FunctionBoxDefaultPort) this.getChildAt(i);
			Object localParamMeta = port.getUserObject();
			if(localParamMeta instanceof ParameterMeta && GeneralUtilities.areEqual(localParamMeta, paramMeta))
			{
                result = port;
				break;
			}
		}
		return result;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/25 15:17:07  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 */
