/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.function;

import gov.nih.nci.cbiit.cmps.core.FunctionData;
import gov.nih.nci.cbiit.cmps.ui.util.GeneralUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

import javax.swing.tree.MutableTreeNode;

/**
 * This class defines a distinguished Cell from other type of cells for customized rendering.
 * It will also provide some utility functions to facilitate lookup, etc.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
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
		if(userObject instanceof FunctionBoxUserObject)
		{
			((FunctionBoxUserObject)userObject).setFunctionalBoxCell(this);
		}
	}

	/**
	 * Based on the give parameter meta, find the corresponding DefaultPort.
	 * Return null, if nothing is found.
	 * @param paramMeta
	 * @return the corresponding DefaultPort.
	 */
	public FunctionBoxDefaultPort findPortByParameterMeta(FunctionData paramMeta)
	{
		FunctionBoxDefaultPort result = null;
		int count = this.getChildCount();
		for(int i=0; i<count; i++)
		{
			FunctionBoxDefaultPort port = (FunctionBoxDefaultPort) this.getChildAt(i);
			Object localParamMeta = port.getUserObject();
			if(localParamMeta instanceof FunctionData && GeneralUtilities.areEqual(localParamMeta, paramMeta))
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
 */
