/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v 3.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class V2ConverterNextProcessDialog extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2ConverterNextProcessDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2ConverterNextProcessDialog.java,v 1.3 2008-06-09 19:54:05 phadkes Exp $";

    private String title = "Select next job";

    private JRadioButton jrNextSCS;
    private JRadioButton jrNextMAP;
    private JLabel jlH3SLabel;
    private JTextField jtH3SFile;

    private JButton jbBrowse;
    private JButton jbGoNext;
    private JButton jbCancel;

    private String fileH3S = "";

    private boolean wasClickedGoNext = false;
    private boolean wasSelectedSCS = false;
    private boolean wasSelectedMAP = false;

    private JDialog dialog;

    public V2ConverterNextProcessDialog(JDialog dialog)
    {
        super(dialog, "Select next job", true);
        initialize();
    }
    public V2ConverterNextProcessDialog(JFrame frame)
    {
        super(frame, "Select next job", true);
        initialize();
    }

    private void initialize()
    {
        Object[] ob = inputFileNameCommon("       Input H3S file ", jtH3SFile, jbBrowse, "Browse", "Browse");
        JPanel h3sFilePanel = (JPanel) ob[0];
        jlH3SLabel = (JLabel) ob[1];
        jtH3SFile = (JTextField) ob[2];
        jbBrowse = (JButton) ob[3];
        jtH3SFile.setEditable(false);

        ob = setupRadioButtonPanel(jrNextMAP, "Create a new MAP", "MAP", true,
                                   jrNextSCS, "Edit the SCS", "SCS", false,
                                   null, null, null, false, false);
        JPanel radioButtonPanel = (JPanel) ob[0];
        jrNextMAP = (JRadioButton) ob[1];
        jrNextSCS = (JRadioButton) ob[2];

        JPanel buttonPanel = generateButtonPanel();

        JPanel level1 = new JPanel(new BorderLayout());
        level1.add(h3sFilePanel, BorderLayout.NORTH);

        JPanel level2 = new JPanel(new BorderLayout());
        level2.add(radioButtonPanel, BorderLayout.NORTH);
        level2.add(level1, BorderLayout.CENTER);

        JPanel level3 = new JPanel(new BorderLayout());

        level3.add(new JLabel("Which do you want to do next?", JLabel.CENTER), BorderLayout.NORTH);
        level3.add(wrappingBorder("Job Options", level2), BorderLayout.CENTER);
        level3.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(level3, BorderLayout.CENTER);
        getContentPane().add(new JLabel(" "), BorderLayout.WEST);
        getContentPane().add(new JLabel(" "), BorderLayout.EAST);

        dialog = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    dialog.dispose();
                }
            }
        );

        setSize(430, 170);
        //setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        wasSelectedSCS = jrNextSCS.isSelected();
        wasSelectedMAP = jrNextMAP.isSelected();
        if (e.getSource() == jbGoNext)
        {
            wasClickedGoNext = true;
            this.dispose();
        }
        if (e.getSource() == jbCancel)
        {
            wasClickedGoNext = false;
            this.dispose();
        }
        if (e.getSource() == jbBrowse)
        {
            File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
			            Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION, Config.OPEN_DIALOG_TITLE_FOR_H3S_HSM_FILE, false, false);
            if (file == null) return;
            jtH3SFile.setText(file.getAbsolutePath());
            fileH3S = jtH3SFile.getText();
        }
        if (e.getSource() instanceof JRadioButton)
        {
            jlH3SLabel.setEnabled(jrNextMAP.isSelected());
            jtH3SFile.setEnabled(jrNextMAP.isSelected());
            if (!jtH3SFile.isEnabled()) jtH3SFile.setText("");
            fileH3S = jtH3SFile.getText();
            jbBrowse.setEnabled(jrNextMAP.isSelected());
        }
    }
    private JPanel wrappingBorder(String borderTitle, JPanel aPanel)
    {
        JPanel titledBorders = new JPanel(new BorderLayout());

        TitledBorder titled = BorderFactory.createTitledBorder(borderTitle);
        titledBorders.add(aPanel, BorderLayout.CENTER);
        titledBorders.setBorder(titled);
        return titledBorders;
    }
    private JPanel generateButtonPanel()
    {
        jbGoNext = new JButton("Go Next");
        jbGoNext.setActionCommand("Next");
        jbGoNext.addActionListener(this);
        jbGoNext.setEnabled(true);
        jbCancel = new JButton("Cancel");
        jbCancel.setActionCommand("Cancel");
        jbCancel.addActionListener(this);
        jbCancel.setEnabled(true);

        JPanel east = new JPanel(new BorderLayout());

        east.add(jbGoNext, BorderLayout.WEST);
        east.add(jbCancel, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);
        south.add(east, BorderLayout.EAST);

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

    private Object[] setupRadioButtonPanel(JRadioButton radioButton01, String buttonLabel01, String buttonCommand01, boolean selected01,
                                           JRadioButton radioButton02, String buttonLabel02, String buttonCommand02, boolean selected02,
                                           JRadioButton radioButton03, String buttonLabel03, String buttonCommand03, boolean selected03, boolean vertical)
    {
        JPanel aPanel = new JPanel(new BorderLayout());
        JPanel bPanel = new JPanel(new BorderLayout());

        radioButton01 = new JRadioButton(buttonLabel01);
        radioButton01.setActionCommand(buttonCommand01);
        radioButton01.setSelected(selected01);
        radioButton02 = new JRadioButton(buttonLabel02);
        radioButton02.setActionCommand(buttonCommand02);
        radioButton02.setSelected(selected02);

        if (buttonLabel03 != null)
        {
            radioButton03 = new JRadioButton(buttonLabel03);
            radioButton03.setActionCommand(buttonCommand03);
            radioButton03.setSelected(false);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton01);
        group.add(radioButton02);
        if (buttonLabel03 != null) group.add(radioButton03);

        radioButton01.addActionListener(this);
        radioButton02.addActionListener(this);
        if (buttonLabel03 != null) radioButton03.addActionListener(this);
        if (buttonLabel03 == null)
        {
            if (vertical)
            {
                aPanel.add(radioButton01, BorderLayout.NORTH);
                aPanel.add(radioButton02, BorderLayout.CENTER);
            }
            else
            {
                aPanel.add(radioButton01, BorderLayout.WEST);
                aPanel.add(radioButton02, BorderLayout.CENTER);
            }
        }
        else
        {
            aPanel.add(radioButton01, BorderLayout.WEST);
            JPanel cPanel = new JPanel(new BorderLayout());
            cPanel.add(radioButton02, BorderLayout.WEST);
            cPanel.add(radioButton03, BorderLayout.CENTER);
            aPanel.add(cPanel, BorderLayout.CENTER);
        }
        bPanel.add(aPanel, BorderLayout.NORTH);

        Object[] out = new Object[4];
        out[0] = bPanel;
        out[1] = radioButton01;
        out[2] = radioButton02;
        if (buttonLabel03 != null) out[3] = radioButton03;
        return out;
    }

    public String getH3SFileName() { return fileH3S; }
    public boolean wasClickedGoNext() { return wasClickedGoNext; }
    public boolean wasSelectedSCS() { return wasSelectedSCS; }
    public boolean wasSelectedMAP() { return wasSelectedMAP; }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/08/13 15:23:25  wangeug
 * HISTORY      : add new menu:open H3S with "xml" format
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/10 03:55:43  umkis
 * HISTORY      : V2-V3 mapping job version 1.0
 * HISTORY      :
 */
