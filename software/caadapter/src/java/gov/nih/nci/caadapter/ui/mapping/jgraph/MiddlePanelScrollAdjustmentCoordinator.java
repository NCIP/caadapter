/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.Container;
import java.awt.Component;
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
public class MiddlePanelScrollAdjustmentCoordinator implements AdjustmentListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MiddlePanelScrollAdjustmentCoordinator.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/MiddlePanelScrollAdjustmentCoordinator.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	public static final int FROM_SOURCE_TREE = 1;
	public static final int FROM_TARGET_TREE = 2;
	public static final int FROM_UNKNOWN_SOURCE = -1;

	private int scrollSource = FROM_UNKNOWN_SOURCE;

	private MappingMiddlePanel middlePanel;
	private JScrollPane affectedScrollPane;
	private boolean inScrollingMode = false;

	public MiddlePanelScrollAdjustmentCoordinator(MappingMiddlePanel middlePanel, JScrollPane affectedScrollPane)
	{
		this.middlePanel = middlePanel;
		this.affectedScrollPane = affectedScrollPane;
	}

	public int getScrollSource()
	{
		return scrollSource;
	}

	public void clearScrollSource()
	{
		this.scrollSource = FROM_UNKNOWN_SOURCE;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if(middlePanel.getGraphAdjustmentAdapter().isInScrollingMode())
		{//ignore if the other is scrolling
			return;
		}
		//start scrolling
		inScrollingMode = true;

		int componentSource = FROM_UNKNOWN_SOURCE;
		JScrollPane localScrollPane = null;
		JScrollBar localScrollBar = null;
		JTree treeComponent = null;
		Object obj = e.getSource();
//		System.out.println("source is of type: '" + (obj==null? "null" : obj.getClass().getName() + "'."));
		if(obj instanceof JScrollBar)
		{
			localScrollBar = (JScrollBar) obj;
			Container container = localScrollBar.getParent();
			if(container instanceof JScrollPane)
			{
				localScrollPane = (JScrollPane) container;
				Component comp = localScrollPane.getViewport().getView();
				if(comp instanceof MappingSourceTree)
				{
					componentSource = FROM_SOURCE_TREE;
					treeComponent = (MappingSourceTree) comp;
				}
				else if(comp instanceof MappingTargetTree)
				{
					componentSource = FROM_TARGET_TREE;
					treeComponent = (MappingTargetTree) comp;
				}
			}
		}
		scrollSource = componentSource;
		if(componentSource!=FROM_UNKNOWN_SOURCE)
		{
			int localMin = localScrollBar.getMinimum();
			int localMax = localScrollBar.getMaximum();
			int scrollValue = e.getValue();
			int heightHidden = (int) localScrollPane.getViewport().getViewPosition().getY();

			double ratio = ((double) scrollValue) / ((double) (localMax - localMin));
//			System.out.println("scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
//			System.out.println("ratio:'" + ratio + "'.");
//			System.out.println("hidden: '" + heightHidden + "'.");
			scrollAffectedScrollPane(ratio);
		}

		//end scrolling
		inScrollingMode = false;
	}

	private void scrollAffectedScrollPane(double ratio)
	{
		if(affectedScrollPane==null)
		{
			return;
		}
		JScrollBar verticalBar = affectedScrollPane.getVerticalScrollBar();
		if(verticalBar==null || !verticalBar.isVisible())
		{
			return;
		}
		int localMin = verticalBar.getMinimum();
		int localMax = verticalBar.getMaximum();

		int scrollValue = (int) (((double)(localMax - localMin)) * ratio);
//		System.out.println("MiddlePanelScrollAdjustmentCoordinator: affected scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
		verticalBar.setValue(scrollValue);
	}

	public boolean isInScrollingMode()
	{
		return inScrollingMode;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/20 20:31:49  jiangsc
 * HISTORY      : to Scroll consistently for source, target, and map panel on the HL7MappingPanel.
 * HISTORY      :
 */
