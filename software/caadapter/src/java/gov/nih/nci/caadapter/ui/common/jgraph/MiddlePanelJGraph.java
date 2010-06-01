/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.jgraph;


import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;

/**
 * This class will handle JGraph specific rendering with some customized functions.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-10-09 18:20:55 $
 */
public class MiddlePanelJGraph extends JGraph
{

	// Construct the Graph using the Model as its Data Source
	public MiddlePanelJGraph(GraphModel model)
	{
		super(model);
		// Make Ports Visible by Default
		setPortsVisible(true);
		// Use the Grid (but don't make it Visible)
		setGridEnabled(true);
		// Set the Grid Size to 10 Pixel
		setGridSize(6);
		// Set the Tolerance to 2 Pixel
		setTolerance(2);
		// Accept edits if click on background
		setInvokesStopCellEditing(true);
		// Allows control-drag
		setCloneable(false);
		// Jump to default port on connect
		setJumpToDefaultPort(false);
		setDoubleBuffered(true);
//		this.setOpaque(false);
	}

	/**
	 * Notification from the <code>UIManager</code> that the L&F has changed.
	 * Replaces the current UI object with the latest version from the
	 * <code>UIManager</code>. Subclassers can override this to support
	 * different GraphUIs.
	 *
	 * @see javax.swing.JComponent#updateUI
	 */
	public void updateUI()
	{
		setUI(new DefaultGraphUI());
		invalidate();
	}

} 
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/09/24 17:51:01  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/09/24 17:50:19  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
