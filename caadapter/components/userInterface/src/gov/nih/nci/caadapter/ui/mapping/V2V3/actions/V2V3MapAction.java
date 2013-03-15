/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.V2V3.actions;

import edu.knu.medinfo.hl7.v2tree.MetaDataLoader;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2ConverterToSCSPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


/**
 * This class creates a Menu item Create V2 V3 action and assigns the action to it.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class V2V3MapAction extends AbstractContextAction  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMAND_NAME = "HL7 v2 to SCS & CSV Conversion";

    private static final Character COMMAND_MNEMONIC = new Character('P');

    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK, false);

    private AbstractMainFrame mainFrame;

    /**
         * Defines an <code>Action</code> object with a default description
         * string and default icon.
         */
    public V2V3MapAction(AbstractMainFrame mainFrame) {
	this(COMMAND_NAME, mainFrame);
	// mainContextManager = cm;
    }

    /**
         * Defines an <code>Action</code> object with the specified
         * description string and a default icon.
         */
    public V2V3MapAction(String name, AbstractMainFrame mainFrame) {
	this(name, null, mainFrame);
    }

    /**
         * Defines an <code>Action</code> object with the specified
         * description string and a the specified icon.
         */
    public V2V3MapAction(String name, Icon icon, AbstractMainFrame mainFrame) {
	super(name, icon);
	this.mainFrame = mainFrame;
	setMnemonic(COMMAND_MNEMONIC);
	setAcceleratorKey(ACCELERATOR_KEY_STROKE);
	setActionCommandType(DESKTOP_ACTION_TYPE);
	// do not know how to set the icon location name, or just do not
	// matter.
    }

    /**
         * The abstract function that descendant classes must be overridden to
         * provide customsized handling.
         *
         * @param e
         * @return true if the action is finished successfully; otherwise,
         *         return false.
         */
    protected boolean doAction(ActionEvent e) throws Exception {
		if (!super.isRequestAuthorized(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
        //new MapV2V3(mainFrame);
        //new MapV2V3(mainFrame, "");
        MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader();
        int check = -1;
        if (loader == null)
        {
            check = JOptionPane.showConfirmDialog(mainFrame, "v3 meta resource zip file isn't exist.\n Do you have another v2 meta source?",
                                                      "No v2 resource zip file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (check != JOptionPane.YES_OPTION)
            {
                setSuccessfullyPerformed(false);
                return isSuccessfullyPerformed();
            }
        }

        try
        {
            V2ConverterToSCSPanel v2ConverterPanel = null;
            if (check == JOptionPane.YES_OPTION)
            {
                v2ConverterPanel = new V2ConverterToSCSPanel(null);
            }
            else v2ConverterPanel = new V2ConverterToSCSPanel(loader);
            JDialog dialog = v2ConverterPanel.setupDialogBasedOnMainFrame(mainFrame);
            v2ConverterPanel.setNextButtonVisible();
            v2ConverterPanel.setCloseButtonVisible();
            dialog.setSize(v2ConverterPanel.getMinimumSize());
            dialog.setVisible(true);
            DefaultSettings.centerWindow(dialog);
        }
        catch (Exception e1)
        {
            JOptionPane.showMessageDialog(mainFrame, "Error : " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            setSuccessfullyPerformed(false);
            return isSuccessfullyPerformed();
        }

//        V2ConverterToSCSPanel v2ConverterPanel = new V2ConverterToSCSPanel(FileUtil.getV2DataDirPath());
//        JDialog dialog = v2ConverterPanel.setupDialogBasedOnMainFrame(mainFrame);
//        v2ConverterPanel.setNextButtonVisible();
//        v2ConverterPanel.setCloseButtonVisible();
//        dialog.setSize(v2ConverterPanel.getMinimumSize());
//        dialog.setVisible(true);
//        DefaultSettings.centerWindow(dialog);
	    setSuccessfullyPerformed(true);
	    return isSuccessfullyPerformed();
    }

    /**
         * Return the associated UI component.
         *
         * @return the associated UI component.
         */
    protected Component getAssociatedUIComponent() {
	return mainFrame;
    }

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_HL7_V2V3_CONVERSION_RESOURCE_REQUIRED);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/05/30 01:03:53  umkis
 * HISTORY      : Advanced mode is directly called.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/10/04 18:09:48  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/19 16:42:37  wangeug
 * HISTORY      : authorized user request
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/11/28 15:13:59  jayannah
 * HISTORY      : Changed the order and names of the menuitems
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/11/27 21:47:51  jayannah
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/11/10 04:01:44  umkis
 * HISTORY      : Switch MapV2V3 to V2Converter to SCS Panel as running module.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/11/08 15:44:53  wuye
 * HISTORY      : Main Menu Re-Org
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/10/30 16:31:42  wuye
 * HISTORY      : Updated the menu item
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/03 13:50:47  jayannah
 * HISTORY      : This class creates a Menu item Create V2 V3 action and assigns the action to it
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/03 13:46:57  jayannah
 * HISTORY      : This class creates a Menu item Create V2 V3 action and assigns the action to it.
 * HISTORY      :
 */
