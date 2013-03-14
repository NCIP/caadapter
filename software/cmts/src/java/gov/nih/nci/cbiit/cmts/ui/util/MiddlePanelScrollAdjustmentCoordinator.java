/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.util;

import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingSourceTree;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingTargetTree;

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
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-11-03 18:31:32 $
 *
 */
public class MiddlePanelScrollAdjustmentCoordinator implements AdjustmentListener
{
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
		System.out.println("enter MiddlePanelScrollAdjustmentCoordinator.adjustmentValueChanged:"+e);
//		if(middlePanel.getGraphAdjustmentAdapter().isInScrollingMode())
//		{//ignore if the other is scrolling
//			return;
//		}
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

		int scrollValue = (int) (((localMax - localMin)) * ratio);
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
 * HISTORY      : Revision 1.2  2008/12/10 15:43:02  linc
 * HISTORY      : Fixed component id generator and delete link.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY      : updated.
 * HISTORY      :
 */
