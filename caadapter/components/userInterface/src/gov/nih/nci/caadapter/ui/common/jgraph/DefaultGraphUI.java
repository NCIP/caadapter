/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/DefaultGraphUI.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.jgraph;


import org.jgraph.graph.CellHandle;
import org.jgraph.graph.GraphContext;
import org.jgraph.plaf.basic.BasicGraphUI;

import javax.swing.TransferHandler;
import java.awt.Point;

import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

/**
 * This class defines a custom implementation JGraphUI so as to provide a custom version of JGraphTransferHandler
 * and a custom Root Handle support the link drag-and-drop movement.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class DefaultGraphUI extends BasicGraphUI
{

	/**
	 * Creates an instance of TransferHandler. Used for subclassers to provide
	 * different TransferHandler.
	 */
	protected TransferHandler createTransferHandler()
	{
		return new MiddlePanelJGraphTransferHandler();
	}

	/**
	 * Constructs the "root handle" for <code>context</code>.
	 * Override this function from base implemenation so as to provide customized implementation of "root handle"
	 *
	 * @param context reference to the context of the current selection.
	 */
	public CellHandle createHandle(GraphContext context)
	{
		if (context != null && !context.isEmpty() && graph.isEnabled())
		{
			try
			{
				return new DefaultGraphRootHandle(context);
			}
			catch (NullPointerException e)
			{
				// ignore for now...
			}
		}
		return null;
	}

	/**
	 * Override the BasicGraphUI.RootHandle so as to provide customized DragAndDrop Action definition
	 */
	public class DefaultGraphRootHandle extends BasicGraphUI.RootHandle
	{
		/**
		 * Creates a root handle which contains handles for the given cells. The
		 * root handle and all its childs point to the specified JGraph
		 * instance. The root handle is responsible for dragging the selection.
		 */
		public DefaultGraphRootHandle(GraphContext ctx)
		{
			super(ctx);
		}

		/**
		 * The super class implementation defines using either COPY or MOVE, however for our application,
		 * we actually use link, so we have to override this method to support LINK action.
		 *
		 * @param event
		 */
		protected void startDragging(MouseEvent event)
		{
			TransferHandler th = graph.getTransferHandler();
			if(!pointWithinBound(event.getPoint()))
			{
				//set isInDragDropMode is true so that this function could be called to adjust its status.
				isDragging = true;
				if (graph.isDragEnabled())
				{
					//using bit or operation to include both COPY (or MOVE) and LINK so as to accommodate the move out of this graphic application to other applications.
					int transferHandlerSupportedActions = graph.getTransferHandler().getSourceActions(graph);
					int action = (event.isControlDown() && graph.isCloneable()) ? (DnDConstants.ACTION_COPY | transferHandlerSupportedActions) : (DnDConstants.ACTION_MOVE | transferHandlerSupportedActions);
					setInsertionLocation(event.getPoint());
					try
					{
						th.exportAsDrag(graph, event, action);
					}
					catch (Exception ex)
					{
						// Ignore
					}
				}
			}
			else
			{//regular mouse move.
				//do nothing
			}
		}

		private boolean pointWithinBound(Point p)
		{
			boolean result = graph.contains(p);
			return result;
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.11  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/25 15:17:07  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 */
