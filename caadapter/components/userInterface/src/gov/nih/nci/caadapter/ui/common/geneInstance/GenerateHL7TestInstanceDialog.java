/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/caadapter/ui/common/geneInstance/GenerateHL7TestInstanceDialog.java,v 1.00 Apr 21, 2008 3:46:15 PM umkis Exp $
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

package gov.nih.nci.caadapter.ui.common.geneInstance;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.hl7message.instanceGen.H3SInstanceMetaTree;
import gov.nih.nci.caadapter.ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Apr 21, 2008
 *          Time:       3:46:15 PM $
 */
public class GenerateHL7TestInstanceDialog extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": GenerateHL7TestInstanceDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/caadapter/ui/common/geneInstance/GenerateHL7TestInstanceDialog.java,v 1.00 Apr 21, 2008 3:46:15 PM umkis Exp $";

    private JTextField jtH3SFilePath;
    private JButton jbH3SFileBrowse;
    private JLabel jlH3SFileBrowse = new JLabel("          H3S File Path ");

    private JTextField jtDATFilePath;
    private JButton jbDATFileBrowse;
    private JCheckBox jcbDATFileBrowse = new JCheckBox("DAT File Path ");

    private JTextField jtChangeFilePath;
    private JButton jbChangeFileBrowse;
    private JCheckBox jcbChangeFileBrowse = new JCheckBox("Chane File Path ");

    private JTextField jtReplaceFilePath;
    private JButton jbReplaceFileBrowse;
    private JCheckBox jcbReplaceFileBrowse = new JCheckBox("Replace File Path ");

    private JButton jbGenerate;
    private JButton jbCancel;

    MainFrame mainFrame;
    JDialog dialog;
    boolean wasSuccessful = false;

    public GenerateHL7TestInstanceDialog(JFrame mainFrame) throws HeadlessException
    {
        super(mainFrame, "Generate HL7 Test Instance", true);
        this.mainFrame = (MainFrame) mainFrame;


        //setSize(600, 250);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        initialize();



        dialog = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    dialog.dispose();
                }
            }
        );

        setSize(550, 250);
        this.setResizable(false);

        DefaultSettings.centerWindow(this);
        setVisible(true);
    }

    private void initialize()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JPanel frontPage = constructFrontPage();
		contentPane.add(frontPage, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
		jbGenerate = new JButton("Generate");
		jbGenerate.setMnemonic('G');
		jbGenerate.addActionListener(this);
		jbCancel = new JButton("Cancel");
		jbCancel.setMnemonic('C');
		jbCancel.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
		tempPanel.add(jbGenerate);
		tempPanel.add(jbCancel);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		southPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		pack();
        initialSet();
    }

    private void initialSet()
    {

        //jcbChangeFileBrowse.setSelected(!jcbDATFileBrowse.isSelected());
        //jcbReplaceFileBrowse.setSelected(!jcbDATFileBrowse.isSelected());
        jcbChangeFileBrowse.setEnabled(!jcbDATFileBrowse.isSelected());
        jcbReplaceFileBrowse.setEnabled(!jcbDATFileBrowse.isSelected());

        if ((!jcbDATFileBrowse.isSelected())||(!jcbDATFileBrowse.isEnabled())) jtDATFilePath.setText("");
        if ((!jcbChangeFileBrowse.isSelected())||(!jcbChangeFileBrowse.isEnabled())) jtChangeFilePath.setText("");
        if ((!jcbReplaceFileBrowse.isSelected())||(!jcbReplaceFileBrowse.isEnabled())) jtReplaceFilePath.setText("");

        jtDATFilePath.setEnabled(jcbDATFileBrowse.isSelected());
        jbDATFileBrowse.setEnabled(jcbDATFileBrowse.isSelected());

        jtChangeFilePath.setEnabled(jcbChangeFileBrowse.isSelected()&&jcbChangeFileBrowse.isEnabled());
        jbChangeFileBrowse.setEnabled(jcbChangeFileBrowse.isSelected()&&jcbChangeFileBrowse.isEnabled());

        jtReplaceFilePath.setEnabled(jcbReplaceFileBrowse.isSelected()&&jcbReplaceFileBrowse.isEnabled());
        jbReplaceFileBrowse.setEnabled(jcbReplaceFileBrowse.isSelected()&&jcbReplaceFileBrowse.isEnabled());
    }

    private JPanel constructFrontPage()
	{
		//this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		String resourcHome="";


		//JLabel dataFileLabel = new JLabel(resourcHome);
		centerPanel.add(jlH3SFileBrowse, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		jtH3SFilePath = new JTextField();
		jtH3SFilePath.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(jtH3SFilePath, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		jbH3SFileBrowse = new JButton("Browse");

        centerPanel.add(jbH3SFileBrowse, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        jbH3SFileBrowse.addActionListener(this);
        jtH3SFilePath.setEditable(false);


        centerPanel.add(jcbDATFileBrowse, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		jtDATFilePath = new JTextField();
		jtDATFilePath.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(jtDATFilePath, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		jbDATFileBrowse = new JButton("Browse");

		centerPanel.add(jbDATFileBrowse, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        jcbDATFileBrowse.addActionListener(this);
        jcbDATFileBrowse.setSelected(false);
        jbDATFileBrowse.addActionListener(this);
        jtDATFilePath.setEditable(false);


        centerPanel.add(jcbChangeFileBrowse, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		jtChangeFilePath = new JTextField();
		jtChangeFilePath.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(jtChangeFilePath, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		jbChangeFileBrowse = new JButton("Browse");

		centerPanel.add(jbChangeFileBrowse, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        jcbChangeFileBrowse.addActionListener(this);
        jcbChangeFileBrowse.setSelected(false);
        jbChangeFileBrowse.addActionListener(this);
        jtChangeFilePath.setEditable(false);


        centerPanel.add(jcbReplaceFileBrowse, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		jtReplaceFilePath = new JTextField();
		jtReplaceFilePath.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(jtReplaceFilePath, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		jbReplaceFileBrowse = new JButton("Browse");

        centerPanel.add(jbReplaceFileBrowse, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        jcbReplaceFileBrowse.addActionListener(this);
        jcbReplaceFileBrowse.setSelected(false);
        jbReplaceFileBrowse.addActionListener(this);
        jtReplaceFilePath.setEditable(false);

        return centerPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
        Object evtObj = e.getSource();
		if (evtObj instanceof JButton)
        {
            String extension = null;
            String title = null;
            JButton evtbtn = (JButton) evtObj;
            JTextField jtPath = null;
            if (evtbtn == jbH3SFileBrowse)
            {
                extension = ".h3s";
                title = "Select H3S file..";
                jtPath = jtH3SFilePath;
            }
            if (evtbtn == jbDATFileBrowse)
            {
                extension = ".dat";
                title = "Select DAT file..";
                jtPath = jtDATFilePath;
            }
            if (evtbtn == jbReplaceFileBrowse)
            {
                extension = ".txt";
                title = "Select Replace data file..";
                jtPath = jtReplaceFilePath;
            }
            if (evtbtn == jbChangeFileBrowse)
            {
                extension = ".txt";
                title = "Select Change data file..";
                jtPath = jtChangeFilePath;
            }

            if (extension != null)
            {
                File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
			            extension, title, false, false);

                if (file != null) jtPath.setText(file.getAbsolutePath());
            }

            if (evtbtn == jbGenerate)
            {
                doGenerate();
            }
            if (evtbtn == jbCancel) this.dispose();

        }
        if (evtObj instanceof JCheckBox)
        {
            initialSet();
        }
    }

    private void doGenerate()
    {
        String h3sFile = jtH3SFilePath.getText();

        if (h3sFile == null) h3sFile = "";
        if (h3sFile.trim().equals(""))
        {
            JOptionPane.showMessageDialog(this,"H3S file name is null. Nothing works.", "Null H3S file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        h3sFile = h3sFile.trim();

        String head = h3sFile.substring(0, h3sFile.toLowerCase().indexOf(".h3s"));

        String datFile = jtDATFilePath.getText();
        String msg1 = "";
        if ((datFile == null)||(datFile.trim().equals(""))) msg1 = head + ".dat\n";

        String msg = "Following files will be generated.\n"
                   + "Files with same names will be overwritten.\n"
                   + "Do you want to start this job anyway?\n\n"
                   + msg1 + head + ".scs\n"
                   + head + ".csv\n"
                   + head + ".map\n";

        int res = JOptionPane.showConfirmDialog(this, msg, "Progress Confirming", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.NO_OPTION) return;

        H3SInstanceMetaTree metaTree = null;

        try
        {
            metaTree = new H3SInstanceMetaTree(h3sFile, datFile, jtReplaceFilePath.getText(), jtChangeFilePath.getText());
        }
        catch(ApplicationException ae)
        {
            JOptionPane.showMessageDialog(this, ae.getMessage(), "Test Instance Generating Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (metaTree.wasSuccess())
        {
            JOptionPane.showMessageDialog(this, "All Test Instances are successfully generated.", "Successfully Generating", JOptionPane.INFORMATION_MESSAGE);
            wasSuccessful = true;
        }
        else
            JOptionPane.showMessageDialog(this, "Something wrong", "Something wrong", JOptionPane.WARNING_MESSAGE);

        this.dispose();
    }

    public boolean wasSuccessful()
    {
        return wasSuccessful;
    }
}

/**
 * HISTORY      : : GenerateHL7TestInstanceDialog.java,v $
 */
