/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.hl7.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
//import gov.nih.nci.caadapter.ui.hl7.map.HL7MappingPanel;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import java.io.File;
import java.util.Map;

/**
 * This class defines the logic to handle refresh mapping panel function.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class RefreshMapAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: RefreshMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/actions/RefreshMapAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	private static final String COMMAND_NAME = ActionConstants.REFRESH;
	protected static final Character COMMAND_MNEMONIC = new Character('R');
	protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);

	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("refresh.gif"));
	public static final String TOOL_TIP_DESCRIPTION = "Refresh";


	private AbstractMappingPanel mappingPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public RefreshMapAction(AbstractMappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public RefreshMapAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, IMAGE_ICON, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public RefreshMapAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
	{
		super(name, icon);
		this.mappingPanel = mappingPanel;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * This function will be called by doAction() to do some pre-work
	 * before the real action is performed.
	 * @param e
	 * @return true if the action shall proceed;
	 *         false if the action cannot continue, shall return immediately.
	 */
	protected boolean preActionPerformed(ActionEvent e)
	{
		try
		{
			if (mappingPanel.isChanged())
			{
				int choice = JOptionPane.showConfirmDialog(mappingPanel, "Data has been changed but is not saved. Would you like to save your changes?", "Data Changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (choice == JOptionPane.CANCEL_OPTION)
				{//do nothing just abort the close action.
					return false;
				}
				if (choice == JOptionPane.YES_OPTION)
				{
					Action saveAction = mappingPanel.getDefaultSaveAction();
					saveAction.actionPerformed(e);
				}
				return true;
			}
			else
			{
				return true;
			}
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, this.mappingPanel);
			return false;
		}
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		boolean result = true;
		if (!preActionPerformed(e))
		{//return immediately if no further action is needed.
			setSuccessfullyPerformed(false);
			return false;
		}
		//do the actual refresh work.
		MappingFileSynchronizer fileSynchronizer = mappingPanel.getMappingFileSynchronizer();
		Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap = fileSynchronizer.doSynchronizationCheck(false);
		if(!changedFileMap.isEmpty())
		{
			try
			{
				mappingPanel.reload(changedFileMap);
			}
			catch (Exception e1)
			{
				reportThrowableToUI(e1, this.mappingPanel);
				result = false;
			}
			JOptionPane.showMessageDialog(mappingPanel, "Mapping information is now up to date.", "Information", JOptionPane.INFORMATION_MESSAGE);
		}
		return result;
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 19:37:42  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/02 23:02:57  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 */
