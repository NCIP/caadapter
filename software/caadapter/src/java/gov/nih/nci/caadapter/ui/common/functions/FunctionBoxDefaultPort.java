/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.ui.common.MappableNode;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.PortRenderer;

import java.util.Set;

/**
 * This class defines a custom port implementation to facilitate rendering and other convenience usage.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class FunctionBoxDefaultPort extends DefaultPort implements MappableNode
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: FunctionBoxDefaultPort.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionBoxDefaultPort.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	/**
	 * Renderer for the class.
	 */
	public static transient PortRenderer renderer = new PortRenderer();

	private boolean mapped = false;

//	public FunctionBoxDefaultPort()
//	{
//		super();
//	}

	public FunctionBoxDefaultPort(Object userObject)
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
		if(userObject instanceof ParameterMeta)
		{
			return ((ParameterMeta)userObject).isInput();
		}
		else
		{
			Log.logInfo(this, "FunctionBoxDefaultPort does not have a user object associated with. Check the logic.");
			return false;
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/13 21:07:56  jiangsc
 * HISTORY      : Enhanced the source and target allocation in the MappingViewCommonComponent
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/25 15:17:08  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/19 22:28:06  jiangsc
 * HISTORY      : 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * HISTORY      : 2) Added SwingWorker to OpenObjectToDbMapAction;
 * HISTORY      : 3) Save Point for Function Change.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 22:49:12  jiangsc
 * HISTORY      : View change
 * HISTORY      :
 */
