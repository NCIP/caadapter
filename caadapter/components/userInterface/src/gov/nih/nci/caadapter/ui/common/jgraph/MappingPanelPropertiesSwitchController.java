/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.ui.common.properties.DefaultPropertiesSwitchController;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;

import javax.swing.event.ChangeEvent;

/**
 * This class is to provide property information of any selected graph component on the middle panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class MappingPanelPropertiesSwitchController extends DefaultPropertiesSwitchController implements GraphSelectionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MappingPanelPropertiesSwitchController.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/MappingPanelPropertiesSwitchController.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

    private MiddlePanelJGraph graph = null;

    public MappingPanelPropertiesSwitchController(MiddlePanelJGraph jgraph)
    {
        graph = jgraph;
    }

    /**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(GraphSelectionEvent e)
	{
        Object cell = e.getCell();

        //		Log.logInfo(this, "A new Graph Cell is selected. '"  + (e==null? e : e.getCell()) + "'");

        if (graph.getSelectionCount() == 0)
        {
			setSelectedItem(null);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
			//System.out.println("zero selection count!!");
            //return;
        }
		else if(cell instanceof DefaultGraphCell)
		{
			Object newSelection = ((DefaultGraphCell)cell).getUserObject();
			setSelectedItem(newSelection);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/23 20:03:59  jiangsc
 * HISTORY      : Enhancement on highlight functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/17 16:31:44  umkis
 * HISTORY      : (defect# 196) clearing selection can be run when only left button click.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/29 19:57:05  jiangsc
 * HISTORY      : save point
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 18:57:17  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:12  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 18:18:00  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 */
