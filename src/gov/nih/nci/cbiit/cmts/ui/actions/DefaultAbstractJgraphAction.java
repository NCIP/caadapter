/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
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
