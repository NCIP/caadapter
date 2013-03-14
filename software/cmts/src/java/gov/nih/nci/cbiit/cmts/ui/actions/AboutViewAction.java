/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;

import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.util.CdeBrowserLauncher;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.applet.Applet;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class AboutViewAction extends AbstractContextAction {
    private static final String COMMAND_NAME = "About CMTS";
    private static final Character COMMAND_MNEMONIC = new Character('A');
    private static final String LICENSE_INFORMATION_HTML = "LicenseInformation.html";
    private MainFrameContainer mainFrame;

    public AboutViewAction(MainFrameContainer container) {
        super(COMMAND_NAME);
        this.setMnemonic(COMMAND_MNEMONIC);
        mainFrame=container;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doAction(ActionEvent e) throws Exception {
        // TODO Auto-generated method stub
         String aboutTextPath="/CMTS_About.txt";

         String warningMsg=readMessageFromFile(aboutTextPath).toString();
        //String warningMsg  = "<html><head>DDD</head><body>FFF<br><a href=\"http://www.google.com\" target=\"_blank\">CCC</a></body></html>";
         String frmName="About CMTS";
            //JOptionPane.showMessageDialog(mainApplet, warningMsg, frmName, JOptionPane.DEFAULT_OPTION);
//        Object c = JOptionPane.showInputDialog(mainApplet,
//                                     warningMsg,
//                                     frmName,
//                                     JOptionPane.INFORMATION_MESSAGE,
//                                     null,
//                                     new String[] {"S1", "S3"},
//                                     "S1");

        int c = JOptionPane.showOptionDialog(mainFrame.getAssociatedUIComponent(),
                                           warningMsg,
                                           frmName,
                                           JOptionPane.DEFAULT_OPTION,
                                           JOptionPane.PLAIN_MESSAGE,
                                           null,
                                           new String[] {"Show License Information", "Close"},
                                           "Close");
        //System.out.println("Selected : " + c);
        if (c == 0)
        {
            if (mainFrame.getMainApplet() != null)
            {
                try
                {
                    URL url = mainFrame.getMainApplet().getCodeBase();
                    //System.out.println("CCCCX URL : " + url);
                    mainFrame.getMainApplet().getAppletContext().showDocument(new URL(url.toString()+LICENSE_INFORMATION_HTML), "_blank");
//                    mainFrame.getMainApplet().getAppletContext();
//                    URL url = mainFrame.getMainApplet().getCodeBase();
                    //applet.getAppletContext().showDocument(new URL("javascript:window.open('https://cdebrowser.nci.nih.gov/CDEBrowser/search?elementDetails=9&FirstTimer=0&PageId=ElementDetailsGroup&publicId="+metaPublicId.getText()+"&version="+metaVersion.getText()+"', 'caDSR Element Details', 'width=1000,height=700,scrollbars=yes,menubar=no,toolbar=no,titlebar=no,location=no,status=no')"));
                }
                catch(Exception me)
                {
                    //System.out.println("CCCCX : " + me.getMessage());
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Sorry, '"+LICENSE_INFORMATION_HTML+"' is not found.", "Not found License Info", JOptionPane.ERROR_MESSAGE);
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
                    String str =  FileUtil.searchFile(LICENSE_INFORMATION_HTML);
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
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Sorry, '"+LICENSE_INFORMATION_HTML+"' is not found.", "Not found License Info", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return false;
    }
    private StringBuffer readMessageFromFile(String filePath)
    {
        StringBuffer licenseBf=new StringBuffer();
        try {
            InputStream input=this.getClass().getResourceAsStream(filePath);
            InputStreamReader reader=new InputStreamReader(input);
            BufferedReader bfReader=new BufferedReader(reader);
            String lineSt;
            lineSt = bfReader.readLine();
            while (lineSt!=null)
            {
                licenseBf.append(lineSt+"\n");
                lineSt = bfReader.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        return licenseBf;
    }
    @Override
    protected Component getAssociatedUIComponent() {
        // TODO Auto-generated method stub
        return null;
    }

}
