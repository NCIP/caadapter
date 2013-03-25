/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * This class defines ...
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class MiddlePanelJGraphScrollAdjustmentAdapter implements AdjustmentListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MiddlePanelJGraphScrollAdjustmentAdapter.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/MiddlePanelJGraphScrollAdjustmentAdapter.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	private MappingMiddlePanel middlePanel;

	private boolean inScrollingMode = false;

	public boolean isInScrollingMode()
	{
		return inScrollingMode;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter(MappingMiddlePanel middlePanel)
	{
		this.middlePanel = middlePanel;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
//		if(middlePanel!=null)
//		{
//			middlePanel.repaint();
//		}

		if(middlePanel.getAdjustmentCoordinator().isInScrollingMode())
		{//ignore if the other one is scrolling mode.
			return;
		}
		//start scrolling
		inScrollingMode = true;

		JScrollBar localScrollBar = (JScrollBar) e.getSource();
		int localMin = localScrollBar.getMinimum();
		int localMax = localScrollBar.getMaximum();
		int scrollValue = e.getValue();
		double ratio = ((double) scrollValue) / ((double) (localMax - localMin));
		scrollAffectedScrollPane(middlePanel.getMappingPanel().getSourceScrollPane(), ratio);
		scrollAffectedScrollPane(middlePanel.getMappingPanel().getTargetScrollPane(), ratio);

		//end scrolling
		inScrollingMode = false;
	}

	private void scrollAffectedScrollPane(JScrollPane affectedScrollPane, double ratio)
	{
		if (affectedScrollPane == null)
		{
			return;
		}
		JScrollBar verticalBar = affectedScrollPane.getVerticalScrollBar();
		if (verticalBar == null || !verticalBar.isVisible())
		{
			return;
		}
		int localMin = verticalBar.getMinimum();
		int localMax = verticalBar.getMaximum();

		int scrollValue = (int) (((double) (localMax - localMin)) * ratio);
		Log.logInfo(this, "MiddlePanelJGraphScrollAdjustmentAdapter: affected scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
		verticalBar.setValue(scrollValue);
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/20 20:31:49  jiangsc
 * HISTORY      : to Scroll consistently for source, target, and map panel on the HL7MappingPanel.
 * HISTORY      :
 */
