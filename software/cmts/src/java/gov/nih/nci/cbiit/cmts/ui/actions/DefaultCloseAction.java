/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;
import gov.nih.nci.cbiit.cmts.web.MainApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the default close action.
 * Descendant classes may implement additional functions.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class DefaultCloseAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = ActionConstants.CLOSE;
	protected static final Character COMMAND_MNEMONIC = new Character('C');
	//hotkey//protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK, false);

    public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("closePane.png"));
    public static final String TOOL_TIP_DESCRIPTION = "Close this tab";



    protected MainFrameContainer ownerFrame = null;

	public DefaultCloseAction(MainFrameContainer owner)
	{
		this(COMMAND_NAME, owner);
	}

	public DefaultCloseAction(String name, MainFrameContainer owner)
	{
		this(name, null, owner);
	}

	public DefaultCloseAction(String name, Icon icon, MainFrameContainer owner)
	{
		super(name, icon);
		ownerFrame = owner;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		//hotkey//setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
        setIcon(IMAGE_ICON);
		setShortDescription(TOOL_TIP_DESCRIPTION);
    }

	protected void setFrame(Component newFrame)
	{
		if (newFrame instanceof MainFrame)
		{
			ownerFrame = new MainFrameContainer((MainFrame)newFrame);
		}
        if (newFrame instanceof MainApplet)
		{
			ownerFrame = new MainFrameContainer((MainApplet)newFrame);
		}
    }

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		try
		{
			if (ownerFrame != null)
			{
                JTabbedPane tab = ownerFrame.getTabbedPane();
                //tab.getSelectedIndex()
                Component comp = tab.getSelectedComponent();
                boolean excutableClose = true;
                //System.out.println("CCCC vv component compName:"+comp.getName()+", tabTitle:" + tab.getTitleAt(tab.getSelectedIndex()) + ", className:" + comp.getClass().getCanonicalName()  );
                if (comp instanceof MappingMainPanel)
                {
                    MappingMainPanel mappingPanel = (MappingMainPanel) comp;
                    //System.out.println("CCCC vv MappingMainPanel");
                    if (mappingPanel.isChanged())
                    {
                        int yesORno = JOptionPane.showConfirmDialog(ownerFrame.getAssociatedUIComponent(), "There are unsaved Mapping data. Are you sure to close?", "Unsave Mapping data", JOptionPane.YES_NO_OPTION);
                        if (yesORno == JOptionPane.NO_OPTION) return isSuccessfullyPerformed();
                    }
                }
                else if (comp instanceof MessagePanel)
                {
                    MessagePanel panel = (MessagePanel) comp;

                    String dispMesg = panel.getDisplayedMessage();

                    if ((dispMesg != null)&&(!dispMesg.trim().equals("")))
                    {
                        if (!panel.hasBeenSaved())
                        {
                            int yesORno = JOptionPane.showConfirmDialog(ownerFrame.getAssociatedUIComponent(), "Output is not saved yet. Are you sure to close?", "Unsaved Output data", JOptionPane.YES_NO_OPTION);
                            if (yesORno == JOptionPane.NO_OPTION) return isSuccessfullyPerformed();
                        }
//                        String tabTitle = tab.getTitleAt(tab.getSelectedIndex());
//                        String untitledTag = ActionConstants.FILE_NAME_UNTITLED_TAG;
//                        while (tabTitle.startsWith(untitledTag))
//                        {
//                            String temp = tabTitle.substring(untitledTag.length());
//                            int idx = temp.indexOf(".");
//                            if (idx < 0) break;
//                            temp = temp.substring(0, idx);
//                            try
//                            {
//                                Integer.parseInt(temp);
//                            }
//                            catch(NumberFormatException ne)
//                            {
//                                break;
//                            }
//                            int yesORno = JOptionPane.showConfirmDialog(ownerFrame.getAssociatedUIComponent(), "Output is not saved yet. Are you sure to close?", "Unsaved Output data", JOptionPane.YES_NO_OPTION);
//                            if (yesORno == JOptionPane.NO_OPTION) return isSuccessfullyPerformed();
//                            break;
//                        }
                    }
                }

                ownerFrame.closeTab();
				ownerFrame.resetCenterPanel();  // inserted by umkis on 01/18/2006, defaect# 252
            }
			else
			{
				System.err.println("Main Frame is null. Ignore!");
			}
			setSuccessfullyPerformed(true);
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, ownerFrame.getAssociatedUIComponent());
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return ownerFrame.getAssociatedUIComponent();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 */
