/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import edu.knu.medinfo.hl7.v2tree.images.source.mapping.CompareInstance;
import edu.knu.medinfo.hl7.v2tree.images.source.mapping.CheckVersionAndItem;
import edu.knu.medinfo.hl7.v2tree.images.source.mapping.GroupingMetaInstance;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;

import javax.swing.*;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
 *          date        Sep 26, 2007
 *          Time:       12:46:19 AM $
 */
public class InitialInstallDialog  extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: InitialInstallDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/InitialInstallDialog.java,v 1.5 2008-06-09 19:54:05 phadkes Exp $";

    private JTextField jtPath;
    private JButton jbBrowse;
    private JButton jbOK;
    private JButton jbCancel;
    private JDialog dialog;

    private String path;

    private String title = "";
    private String message1 = "";
    private String message2 = "";
    private String message3 = "";
    private String extension = "";
    private boolean addMessage = false;

    public InitialInstallDialog(JDialog dialog, String title, String msg1, String msg2, String msg3, String exten)
    {
        super(dialog, title, true);
        message1 = msg1;
        message2 = msg2;
        message3 = msg3;
        addMessage = false;
        extension = exten;
        this.title = title;
        initialize();
    }
    public InitialInstallDialog(JDialog dialog, String title, String msg1, String msg2, String msg3, String exten, boolean addMsg)
    {
        super(dialog, title, true);
        message1 = msg1;
        message2 = msg2;
        message3 = msg3;
        extension = exten;
        addMessage = addMsg;
        this.title = title;
        initialize();
    }
    public InitialInstallDialog(JFrame dialog, String title, String msg1, String msg2, String msg3, String exten)
    {
        super(dialog, title, true);
        message1 = msg1;
        message2 = msg2;
        message3 = msg3;
        addMessage = false;
        extension = exten;
        this.title = title;
        initialize();
    }
    public InitialInstallDialog(JFrame dialog, String title, String msg1, String msg2, String msg3, String exten, boolean addMsg)
    {
        super(dialog, title, true);
        message1 = msg1;
        message2 = msg2;
        message3 = msg3;
        addMessage = addMsg;
        extension = exten;
        this.title = title;
        initialize();
    }
    private void initialize()
    {
        String fileLabel = " File Path : ";
        if (extension == null) fileLabel = " Directory Path : ";
        Object[] ob = inputFileNameCommon(fileLabel, jtPath, jbBrowse, "Browse", "Browse");
        JPanel pathPanel = (JPanel) ob[0];
        //jlPath = (JLabel) ob[1];
        jtPath = (JTextField) ob[2];
        jbBrowse = (JButton) ob[3];
        jtPath.setText(FileUtil.getV2DataDirPath());
        jtPath.setEditable(false);

        JLabel label1 = new JLabel(message1);
        JLabel label2 = new JLabel(message2);
        JLabel label3 = new JLabel(message3);
        JPanel norhtPanel = new JPanel(new BorderLayout());
        JPanel norhtPanel0 = new JPanel(new BorderLayout());
        JPanel norhtPanel1 = new JPanel(new BorderLayout());
        JPanel norhtPanel2 = new JPanel(new BorderLayout());
        JPanel norhtPanel3 = new JPanel(new BorderLayout());

        norhtPanel3.add(label3, BorderLayout.NORTH);
        norhtPanel2.add(label2, BorderLayout.NORTH);
        norhtPanel2.add(norhtPanel3, BorderLayout.CENTER);
        norhtPanel1.add(label1, BorderLayout.NORTH);
        norhtPanel1.add(norhtPanel2, BorderLayout.CENTER);

        norhtPanel0.add(norhtPanel1, BorderLayout.NORTH);

        norhtPanel.add(norhtPanel0, BorderLayout.CENTER);
        norhtPanel.add(pathPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = generateButtonPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(norhtPanel, BorderLayout.CENTER);
        getContentPane().add(new JLabel(" "), BorderLayout.WEST);
        getContentPane().add(new JLabel(" "), BorderLayout.EAST);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        dialog = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    dialog.dispose();
                }
            }
        );

        setSize(420, 160);

        DefaultSettings.centerWindow(this);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == jbCancel)
        {
            path = "";
            this.dispose();
        }
        if (e.getSource() == jbBrowse)
        {
            //File file = DefaultSettings.getUserInputOfFileFromGUI(this, FileUtil.getV2DataDirPath(), null, "V2 Meta Base Directory", false, false);
            File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
			            extension, title, false, false);

            if (file != null) jtPath.setText(file.getAbsolutePath());

        }
        if (e.getSource() == jbOK)
        {
            String pth = jtPath.getText();
            if ((pth==null)||(pth.trim()).equals(""))
            {
                JOptionPane.showMessageDialog(this, "Null Path name", "Null path", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (addMessage)
            {
                String str = "This prcess may takes some minute. Please, wait for a while.";
                JOptionPane.showMessageDialog(this, str, "Long Processing", JOptionPane.INFORMATION_MESSAGE);
            }
            path = pth;
            this.dispose();
        }
    }

    private JPanel generateButtonPanel()
    {
        jbOK = new JButton("OK");
        jbOK.setActionCommand("OK");
        jbOK.addActionListener(this);
        jbOK.setEnabled(true);
        jbCancel = new JButton("Cancel");
        jbCancel.setActionCommand("Cancel");
        jbCancel.addActionListener(this);
        jbCancel.setEnabled(true);

        JPanel east = new JPanel(new BorderLayout());

        east.add(jbOK, BorderLayout.WEST);
        east.add(jbCancel, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(east, BorderLayout.EAST);
        //south.add(west, BorderLayout.WEST);

        return south;
    }

    private Object[] inputFileNameCommon(String label, JTextField textField, JButton button, String buttonLabel, String buttonCommand)
    {
        JPanel aPanel = new JPanel(new BorderLayout());
        JLabel jl = new JLabel(label, JLabel.LEFT);
        aPanel.add(jl, BorderLayout.WEST);
        textField = new JTextField();
        aPanel.add(textField, BorderLayout.CENTER);
        button = new JButton(buttonLabel);
        button.setActionCommand(buttonCommand);
        button.addActionListener(this);
        aPanel.add(button, BorderLayout.EAST);
        Object[] out = new Object[4];
        out[0] = aPanel;
        out[1] = jl;
        out[2] = textField;
        out[3] = button;
        return out;
    }

    public String getPath()
    {
        return path;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/10/02 15:08:03  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/27 19:39:34  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/26 20:14:57  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/09/26 16:24:16  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 */
