/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/InitialInstallDialog.java,v 1.2 2007-09-26 20:14:57 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/InitialInstallDialog.java,v 1.2 2007-09-26 20:14:57 umkis Exp $";

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
        Object[] ob = inputFileNameCommon(" File Name : ", jtPath, jbBrowse, "Browse", "Browse");
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

            jtPath.setText(file.getAbsolutePath());

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
 * HISTORY      : Revision 1.1  2007/09/26 16:24:16  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 */
