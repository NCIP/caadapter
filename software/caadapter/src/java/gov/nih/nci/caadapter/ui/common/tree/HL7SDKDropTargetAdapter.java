/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * This Adapter class implements java.awt.dnd.DropTargetListener interface to provide support
 * for Drop action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-10-14 17:24:56 $
 */
public class HL7SDKDropTargetAdapter implements DropTargetListener
{
	protected boolean asynchronousAdd = false;
	protected boolean acceptAnyFlavor = false;
	protected boolean acceptAnyAction = false;
	protected boolean confirmDropFlavors = false;
	protected DataFlavor selectedDataFlavor;
	protected DataFlavor[] currentDataFlavors;
	protected DataFlavor[] acceptableDropFlavors;
	protected DataFlavor[] preferredLocalFlavors;
	protected int acceptableDropActions = DnDConstants.ACTION_COPY_OR_MOVE;
	protected DropCompatibleComponent dropComponent;
	private SwingWorker worker;

	/**
	 * record the point that dragEnter and dragOver regard to
	 * so as to ignore multi-call to the same point
	 */
	private Point dropPoint;

	public HL7SDKDropTargetAdapter(DropCompatibleComponent dropComponent,
							 int acceptableDropActions,
							 DataFlavor[] acceptableDropFlavors,
							 DataFlavor[] preferredLocalFlavors)
	{
		this.dropComponent = dropComponent;
		this.acceptableDropFlavors = acceptableDropFlavors;
		this.acceptableDropActions = acceptableDropActions;
		this.preferredLocalFlavors = preferredLocalFlavors;
	}

	/**
	 * Called by isDragOk
	 * Checks to see if the flavor drag flavor is acceptable
	 *
	 * @param e the DropTargetDragEvent object
	 * @return whether the flavor is acceptable
	 */
	private boolean isDragFlavorSupported(DropTargetDragEvent e)
	{
		if (this.acceptAnyFlavor == true)
			return true;
		/**
		 if(debug) {
		 System.out.println("acceptable flavors");
		 DataFlavor[] flavors = acceptableDropFlavors;
		 for(int i=0; i< flavors.length; i++)
		 Log.logInfo(this,  flavors[i].getMimeType() );
		 Log.logInfo(this, "end acceptable flavors");
		 }
		 */
		if (acceptableDropFlavors == null)
		{
//			Log.logInfo(this, "acceptable flavors are null!");
			return false;
		}

		for (int i = 0; i < acceptableDropFlavors.length; i++)
		{
			if (e.isDataFlavorSupported(acceptableDropFlavors[i]))
				return true;
		}
		return false;
	}

	/**
	 * Called by drop
	 * Checks the flavors and operations
	 * @param e the DropTargetDropEvent object
	 */
	private void displayDropFlavors(final DropTargetDropEvent e,
									final Transferable trans)
	{
		worker = new SwingWorker()
		{
			public Object construct()
			{
				return doWork();
			}

			public void finished()
			{
				// the boolean is returned from doWork
				Boolean b = new Boolean(false);
				try
				{
					b = (Boolean) worker.get();
				}
				catch (Exception e)
				{
					Log.logException(this, e);
				}
				finally
				{
					if (b.booleanValue() == true)
					{
						processDrop(e, selectedDataFlavor, trans);
					}
				}//end of finally
			}//end of function finished()
		};
	}

	/**
	 * This is modified from the SwingWorker example.
	 * Popup a modal confirm dialog and block until the user responds.
	 * Return true unless the user selects "NO".
	 */
	private boolean waitForUserConfirmation() throws InterruptedException
	{

		/* We're going to show the modal dialog on the event dispatching
		* thread with SwingUtilities.invokeLater(), so we create a
		* Runnable class that shows the dialog and stores the user's
		* response.
		*/
		class DoShowDialog implements Runnable
		{
			boolean proceedConfirmed = true;

			public void run()
			{
				selectedDataFlavor = (DataFlavor)
						JOptionPane.showInputDialog(null, // parent
								"Choose a flavor", // message
								"Possible DataFlavors", //title
								JOptionPane.QUESTION_MESSAGE,
								null, // icon
								currentDataFlavors,
								currentDataFlavors[0]);
				if (selectedDataFlavor == null)
				{
					proceedConfirmed = false;
				}
				else
				{
					proceedConfirmed = true;
				}
			}
		}
		DoShowDialog doShowDialog = new DoShowDialog();

		/* The invokeAndWait utility can throw an InterruptedException,
		* which we don't bother with, and an InvocationException.
		* The latter occurs if our Runnable throws - which would indicate
		* a bug in DoShowDialog.  The invokeAndWait() call will not return
		* until the user has dismissed the modal confirm dialog.
		*/
		try
		{
			SwingUtilities.invokeAndWait(doShowDialog);
		}
		catch (java.lang.reflect.InvocationTargetException e)
		{
			Log.logException(this, e);
		}
		return doShowDialog.proceedConfirmed;
	}

	/**
	 * Used by the SwingWorker in construct. This return value is what
	 * is retrieved from the worker's get method.
	 */
	private Object doWork()
	{
		Boolean b = null;
		try
		{
			b = new Boolean(waitForUserConfirmation());
		}
		catch (InterruptedException e)
		{
			System.err.println(e);
			b = new Boolean(false);
		}
		return b;
	}


	private DataFlavor chooseDropFlavor(DropTargetDropEvent e)
	{
		DataFlavor[] flavors = e.getCurrentDataFlavors();

		// check local transfer first
		if (e.isLocalTransfer() == true)
		{
			for (int i = 0; i < preferredLocalFlavors.length; i++)
			{
				if (e.isDataFlavorSupported(preferredLocalFlavors[i]))
					return preferredLocalFlavors[i];
			}
		}
		DataFlavor chosen = null;
		for (int i = 0; i < acceptableDropFlavors.length; i++)
		{
			if (e.isDataFlavorSupported(acceptableDropFlavors[i]))
			{
				chosen = acceptableDropFlavors[i];
				String ccs = chosen.getParameter("charset");
				if (ccs != null)
				{
					if (checkCharSet(flavors, ccs))
						break;
				}
				else
					break;
			}
		}
		return chosen;
	}

	private boolean checkCharSet(DataFlavor[] flavors, String charset)
	{
		for (int i = 0; i < acceptableDropFlavors.length; i++)
		{
			String cs = flavors[i].getParameter("charset");
			if (cs != null)
			{
				if (charset.equals(cs))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Called by dragEnter and dragOver
	 * Checks the flavors and operations
	 *
	 * @param e the event object
	 * @return whether the flavor and operation is ok
	 */
	private boolean isDragOk(DropTargetDragEvent e)
	{
		if (!this.dropComponent.isDropOk(e))
			return false;

		if (isDragFlavorSupported(e) == false)
			return false;


		// the docs on DropTargetDragEvent rejectDrag says that
		// the dropAction should be examined
		int da = e.getDropAction();

		if (this.acceptAnyAction == true)
			return true;

		// we're saying that these actions are necessary
		if ((da & this.acceptableDropActions) == 0)
			return false;
		return true;
	}


	/**
	 * start "drag under" feedback on component
	 * invoke acceptDrag or rejectDrag based on isDragOk
	 */
	public void dragEnter(DropTargetDragEvent e)
	{
		this.dropComponent.setInDragDropMode(true);
		if (this.dropPoint == null)
		{
			dropPoint = e.getLocation();
		}
		else if (dropPoint.equals(e.getLocation()))
		{
			return;
		}

		if (isDragOk(e) == false)
		{
			this.dropComponent.dragUnderFeedback(false, e);
			e.rejectDrag();
			return;
		}
		this.dropComponent.dragUnderFeedback(true, e);
		e.acceptDrag(e.getDropAction());
	}

	/**
	 * continue "drag under" feedback on component
	 * invoke acceptDrag or rejectDrag based on isDragOk
	 */
	public void dragOver(DropTargetDragEvent e)
	{
		this.dropComponent.setInDragDropMode(true);

		if (this.dropPoint == null)
		{
			dropPoint = e.getLocation();
		}
		else if (dropPoint.equals(e.getLocation()))
			return;

		if (isDragOk(e) == false)
		{
			this.dropComponent.dragUnderFeedback(false, e);
//			this.dropComponent.setInDragDropMode(false);
			e.rejectDrag();
			return;
		}

		this.dropComponent.dragUnderFeedback(true, e);
		e.acceptDrag(e.getDropAction());
	}

	public void dropActionChanged(DropTargetDragEvent e)
	{
		this.dropComponent.setInDragDropMode(true);
		if (isDragOk(e) == false)
		{
			this.dropComponent.dragUnderFeedback(false, e);
//			this.dropComponent.setInDragDropMode(false);
			e.rejectDrag();
			return;
		}
		this.dropComponent.dragUnderFeedback(true, e);
		e.acceptDrag(e.getDropAction());
	}

	public void dragExit(DropTargetEvent e)
	{
		//let the dragDropEnd() turn off the the mode indicator.
		//		this.dropComponent.setInDragDropMode(false);
		this.dropComponent.undoDragUnderFeedback();
		this.dropPoint = null;
	}

	/**
	 * perform action from getSourceActions on
	 * the transferrable
	 * invoke acceptDrop or rejectDrop
	 * invoke dropComplete
	 * if its a local (same JVM) transfer, use StringTransferable.localStringFlavor
	 * find a match for the flavor
	 * check the operation
	 * get the transferable according to the chosen flavor
	 * do the transfer
	 */


	public void drop(final DropTargetDropEvent e)
	{
		// the dnd operation will be in the incorrect state if there
		// an InputDialog is shown and then the trans retrieved. This
		// is here for that one special case

		Transferable trans = e.getTransferable();

		this.dropComponent.setInDragDropMode(false);

		this.currentDataFlavors = e.getCurrentDataFlavors();
		if (this.confirmDropFlavors)
		{
			displayDropFlavors(e, trans);
		}
		else
		{
			DataFlavor chosen = chooseDropFlavor(e);
			processDrop(e, chosen, trans);
		}
	}

	private void processDrop(final DropTargetDropEvent e,
					 final DataFlavor chosen,
					 Transferable trans)
	{
		if (chosen == null)
		{
			System.err.println("No flavor match found");
			e.rejectDrop();
			return;
		}

		// the actions that the source has specified with DragGestureRecognizer
		int sa = e.getSourceActions();

		if ((sa & this.acceptableDropActions) == 0)
		{
			System.err.println("No action match found");
			e.rejectDrop();
			this.dropComponent.undoDragUnderFeedback();
			return;
		}

		//Object data=null;
		final Object data;
		try
		{
			/*
			* the source listener receives this action in dragDropEnd.
			* if the action is DnDConstants.ACTION_COPY_OR_MOVE then
			* the source receives MOVE!
			*/
			e.acceptDrop(e.getDropAction());

			// moved this for the inputdialog special case
			//Transferable trans = e.getTransferable();

			// but it won't work for native transfers damnit
			data = trans.getTransferData(chosen);
			if (data == null)
			{
				throw new NullPointerException();
			}
		}
		catch (Throwable t)
		{
			Log.logException(this, t);
			e.dropComplete(false);
			this.dropComponent.undoDragUnderFeedback();
			Runnable except = new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(null,
							"Couldn't get transfer data");
				}
			};
			SwingUtilities.invokeLater(except);
			return;
		}

		boolean isDropSuccess = false;
		if (this.asynchronousAdd)
		{
			Runnable kicker = new Runnable()
			{
				public void run()
				{
					if (!HL7SDKDropTargetAdapter.this.dropComponent.setDropData(data, e, chosen))
					{
						e.dropComplete(false);
//						Log.logInfo(this, "Drop Ejected By Source...");
					}
				}
			};
			try
			{
				SwingUtilities.invokeLater(kicker);
			}
			catch (Exception ex)
			{
				System.err.println(ex);
			}
		}
		else
		{
			isDropSuccess = this.dropComponent.setDropData(data, e, chosen);
		}
//		Log.logInfo(this, "component.add() returned. Is drop successful?" + isDropSuccess);
		if (isDropSuccess)
		{
			e.dropComplete(true);
			this.dropComponent.undoDragUnderFeedback();
		}
		else
		{
			e.dropComplete(false);
			this.dropComponent.undoDragUnderFeedback();
		}
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/17 22:04:55  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/17 20:45:45  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:15  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
