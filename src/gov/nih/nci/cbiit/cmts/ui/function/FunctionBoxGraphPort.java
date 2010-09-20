/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.function;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.PortRenderer;

import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;

import java.util.Set;

/**
 * This class defines a custom port implementation to facilitate rendering and other convenience usage.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxGraphPort extends DefaultPort implements MappableNode
{
	/**
	 * Renderer for the class.
	 */
	public static transient PortRenderer renderer = new PortRenderer();

	private boolean mapped = false;

//	public FunctionBoxDefaultPort()
//	{
//		super();
//	}

	public FunctionBoxGraphPort(Object userObject)
	{
		super(userObject);
	}

//	public FunctionBoxDefaultPort(Object userObject, Port anchor)
//	{
//		super(userObject, anchor);
//	}

	/**
	 * Set the map status to new value, which might trigger underline property change.
	 *
	 * @param newValue
	 */
	public void setMapStatus(boolean newValue)
	{
		this.mapped = newValue;
		if (getUserObject() instanceof MappableNode)
		{
			((MappableNode) getUserObject()).setMapStatus(newValue);
		}
	}

	/**
	 * Answer if this given node is mapped.
	 *
	 * @return if the given node is mapped.
	 */
	public boolean isMapped()
	{
		Set edgeSet = getEdges();
		return (edgeSet!=null && !edgeSet.isEmpty());
//		return this.mapped;
	}

	public boolean isInput()
	{
		if(userObject instanceof FunctionData)
		{
			return true;//((FunctionData)userObject).isInput();
		}
		else
		{
			System.out.println("FunctionBoxDefaultPort does not have a user object associated with. Check the logic.");
			return false;
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
