/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;

import javax.swing.*;

/**
 * The class is the default action class that will be inherited by other action class in this package.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-10 15:43:02 $
 */
public abstract class DefaultAbstractJgraphAction extends AbstractContextAction
{
	private MappingMiddlePanel middlePanel;
	private MiddlePanelJGraphController controller;

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	protected DefaultAbstractJgraphAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, (Icon)null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	protected DefaultAbstractJgraphAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(name, icon);
		if (middlePanel == null && controller != null)
		{
			middlePanel = controller.getMiddlePanel();
		}
		this.middlePanel = middlePanel;
		this.controller = controller;
	}

	protected MappingMiddlePanel getMiddlePanel()
	{
		return middlePanel;
	}

	protected MiddlePanelJGraphController getController()
	{
		return controller;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
