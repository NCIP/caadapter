/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import javax.swing.*;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.caadapter.common.util.FileUtil;

public class HelpViewAction extends AbstractContextAction {
    private static final String COMMAND_NAME = ActionConstants.HELP;
    private static final Character COMMAND_MNEMONIC = new Character('H');
    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
//	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("Help16.gif"));
//	private static final String TOOL_TIP_DESCRIPTION = ActionConstants.HELP;

    //private JApplet mainApplet;
    private MainFrameContainer mainFrame;
    public HelpViewAction(MainFrameContainer container) {
        super(COMMAND_NAME);
        this.setMnemonic(COMMAND_MNEMONIC);
         mainFrame=container;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doAction(ActionEvent e) throws Exception {
        // TODO Auto-generated method stub
                
        String location = ActionConstants.CMTS_HELP_MENU_CONTENT_URL;

        try
        {
            URL ur = new URL(location);
            URLConnection ct = ur.openConnection();
            ct.getInputStream();
        }
        catch(Exception ee)
        {
            location = null;
        }

        if (mainFrame.getMainApplet() != null)
        {
            try
            {
                if (location == null)
                {
                    location = mainFrame.getMainApplet().getCodeBase()+"/help/CMTS_User_Guide_Document.pdf";
                }
                mainFrame.getMainApplet().getAppletContext().showDocument(new URL(location), "_blank");
            }
            catch(Exception me)
            {
                //System.out.println("CCCCX : " + me.getMessage());
                JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Sorry, Help content is not found.", "Not found Help Content", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        else
        {
            BrowserLauncher browserLauncher =null;
            boolean c2 = true;
            try {
                browserLauncher=new BrowserLauncher();
            } catch (BrowserLaunchingInitializingException e1) {
                c2 = false;
            } catch (UnsupportedOperatingSystemException e2) {
                c2 = false;
            }

            while (c2)
            {
                c2 = false;

                if (location != null)
                {
                    browserLauncher.openURLinBrowser(location);
                    c2 = true;
                    break;
                }
                String str =  FileUtil.searchFile("CMTS_User_Guide_Document.pdf");
                if ((str == null)||(str.trim().equals(""))) break;
                File f = new File(str.trim());
                if ((!f.exists())||(!f.isFile())) break;
                URL url = f.toURI().toURL();
                browserLauncher.openURLinBrowser(url.toString());
                c2 = true;
                break;
            }

            if (!c2)
            {
                JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Sorry, Help Content is not found.", "Not found Help Content", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    @Override
    protected Component getAssociatedUIComponent() {
        // TODO Auto-generated method stub
        return mainFrame.getAssociatedUIComponent();
    }

}
