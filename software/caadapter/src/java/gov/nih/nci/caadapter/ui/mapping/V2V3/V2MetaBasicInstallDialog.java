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
 *          revision    $Revision: 1.3 $
 *          date        Sep 29, 2007
 *          Time:       7:19:35 PM $
 */
public class V2MetaBasicInstallDialog extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2MetaBasicInstallDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2MetaBasicInstallDialog.java,v 1.3 2008-06-09 19:54:05 phadkes Exp $";


    private String title = "V2 Meta Data Collector";



    //private JComboBox jcMessageType;
    private JComboBox jcVersion;
    //private JComboBox jcItem;
    //private JComboBox jcName;

    private JTextField jtPathTargetDir;
     private JTextField jtPathSourceFile;
    //private JTextField jtMessage;
    //private JTextArea jaData;

    private JButton jbBrowseTargetDir;
    private JButton jbBrowseSourceFile;
    private JButton jbAccept;
    //private JButton jbValidate;
    private JButton jbCancel;

    //private JLabel jlPath;

    private JDialog dialog;

    private String path;


    private boolean success = false;


    public V2MetaBasicInstallDialog(JDialog dialog)
    {
        super(dialog, "V2 Basic Meta Data Installer", true);
        initialize();
    }
    public V2MetaBasicInstallDialog(JFrame frame)
    {
        super(frame, "V2 Basic Meta Data Installer", true);
        initialize();
    }

    private void initialize()
    {
        Object[] ob1 = inputFileNameCommon(" Target Meta Data Base Directory ", jtPathTargetDir, jbBrowseTargetDir, "Browse", "Browse");
        JPanel pathPanelTargetDir = (JPanel) ob1[0];
        //jlPathTargetDir = (JLabel) ob1[1];
        jtPathTargetDir = (JTextField) ob1[2];
        jbBrowseTargetDir = (JButton) ob1[3];
        jtPathTargetDir.setText(FileUtil.getV2DataDirPath());
        jtPathTargetDir.setEditable(false);

        Object[] ob2 = inputFileNameCommon(" v2 Manual Appendix A Chapter File ", jtPathSourceFile, jbBrowseSourceFile, "Browse", "Browse");
        JPanel pathPanelSourceFile = (JPanel) ob2[0];
        //jlPathSourceFile = (JLabel) ob2[1];
        jtPathSourceFile = (JTextField) ob2[2];
        jbBrowseSourceFile = (JButton) ob2[3];
        //jtPathSourceFile.setText(FileUtil.getV2DataDirPath());
        jtPathSourceFile.setEditable(false);

        JPanel buttonPanel = generateButtonPanel();

        JPanel comboPanel = generateVersionAndItemPanel();


        String msg1 = "For initial install,";
        String msg2 = "Appendix A chapter file among the v2 manual .doc files is needed.";
        String msg3 = "Please, input the absolute file path.";

        JLabel label1 = new JLabel(msg1);
        JLabel label2 = new JLabel(msg2);
        JLabel label3 = new JLabel(msg3);
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel centerPanel0 = new JPanel(new BorderLayout());
        JPanel centerPanel1 = new JPanel(new BorderLayout());
        JPanel centerPanel2 = new JPanel(new BorderLayout());
        JPanel centerPanel3 = new JPanel(new BorderLayout());

        centerPanel3.add(label3, BorderLayout.NORTH);
        centerPanel2.add(label2, BorderLayout.NORTH);
        centerPanel2.add(centerPanel3, BorderLayout.CENTER);
        centerPanel1.add(label1, BorderLayout.NORTH);
        centerPanel1.add(centerPanel2, BorderLayout.CENTER);

        centerPanel0.add(centerPanel1, BorderLayout.NORTH);

        centerPanel.add(new JLabel(" "), BorderLayout.NORTH);
        centerPanel.add(centerPanel0, BorderLayout.CENTER);
        centerPanel.add(pathPanelSourceFile, BorderLayout.SOUTH);

        JPanel level1 = new JPanel(new BorderLayout());
        level1.add(pathPanelTargetDir, BorderLayout.NORTH);
        level1.add(centerPanel, BorderLayout.CENTER);
        level1.add(new JLabel(" "), BorderLayout.SOUTH);

        JPanel level2 = new JPanel(new BorderLayout());
        level2.add(comboPanel, BorderLayout.NORTH);
        level2.add(level1, BorderLayout.CENTER);
        level2.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(level2, BorderLayout.CENTER);
        getContentPane().add(new JLabel(" "), BorderLayout.WEST);
        getContentPane().add(new JLabel(" "), BorderLayout.EAST);
        getContentPane().add(new JLabel(" "), BorderLayout.NORTH);
        getContentPane().add(new JLabel(" "), BorderLayout.SOUTH);

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
        //setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {

        if (e.getSource() == jbCancel)
        {
            this.dispose();
        }
        if (e.getSource() == jbBrowseTargetDir)
        {
            File file = DefaultSettings.getUserInputOfFileFromGUI(this, FileUtil.getV2DataDirPath(), null, "V2 Meta Base Directory", false, false);
            if (file != null) jtPathTargetDir.setText(file.getAbsolutePath());
            //path = jtPath.getText();
        }
        if (e.getSource() == jbBrowseSourceFile)
        {
            File file = DefaultSettings.getUserInputOfFileFromGUI(this, FileUtil.getV2DataDirPath(), ".doc", "V2 Meta Base Directory", false, false);
            if (file != null) jtPathSourceFile.setText(file.getAbsolutePath());
            //path = jtPath.getText();
        }
        if (e.getSource() == jbAccept)
        {
            doButtonAccept();
        }
    }

    private JPanel generateButtonPanel()
    {
        jbAccept = new JButton("Accept");
        jbAccept.setActionCommand("Accept");
        jbAccept.addActionListener(this);
        jbAccept.setEnabled(true);
        jbCancel = new JButton("Cancel");
        jbCancel.setActionCommand("Cancel");
        jbCancel.addActionListener(this);
        jbCancel.setEnabled(true);

        JPanel east = new JPanel(new BorderLayout());

        east.add(jbAccept, BorderLayout.WEST);
        east.add(jbCancel, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);


        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(east, BorderLayout.EAST);
        south.add(new JLabel(" "), BorderLayout.WEST);

        return south;
    }

    private JPanel generateVersionAndItemPanel()
    {
        CheckVersionAndItem check = new CheckVersionAndItem();
        jcVersion = new JComboBox();
        String versionT = "Select Version.";
        jcVersion.addItem(versionT);
        for(String version:check.getVersionTo())
        {

            jcVersion.addItem(version);
        }
        jcVersion.setEditable(false);

       JPanel west = new JPanel(new BorderLayout());

        west.add(new JLabel(" Version "), BorderLayout.WEST);
        west.add(jcVersion, BorderLayout.EAST);
        west.add(new JLabel(" "), BorderLayout.CENTER);


        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(new JLabel(" "), BorderLayout.EAST);
        south.add(west, BorderLayout.WEST);

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

    private void doButtonValidate()
    {
        success = false;
        String tempPathTargetDir = jtPathTargetDir.getText();
        if ((tempPathTargetDir == null)||(tempPathTargetDir.trim().equals("")))
        {
            JOptionPane.showMessageDialog(this, "Null Target V2 Meta Directory.", "No Target Directory.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tempPathTargetDir.equals(FileUtil.getV2DataDirPath()))
        {
            if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkDirectoryExists(tempPathTargetDir))
            {
                int ans = JOptionPane.showConfirmDialog(this, "Base V2 Meta Directory is not exist yet.\nDo you want to create now?", "Creat v2 Base Directory?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ans == JOptionPane.YES_OPTION)
                {
                    File dir = new File(tempPathTargetDir);
                    if (!dir.mkdirs())
                    {
                        JOptionPane.showMessageDialog(this, "Create Directory Failure : " + tempPathTargetDir, "Create Directory Failure", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                else return;
            }
        }
        else
        {
            if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkDirectoryExists(tempPathTargetDir))
            {
                JOptionPane.showMessageDialog(this, "Target v2 Meta Directory is Invalid", "Invalid Directory", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        path = tempPathTargetDir;

        String tempPathSourceFile = jtPathSourceFile.getText();
        if ((tempPathSourceFile == null)||(tempPathSourceFile.trim().equals("")))
        {
            JOptionPane.showMessageDialog(this, "Null Appendix A chapter File name", "Null Source file ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkFileExists(tempPathSourceFile))
        {
            JOptionPane.showMessageDialog(this, "This Appendix A chapter File is not Exist.", "Invalid Source File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (jcVersion.getSelectedIndex() == 0)
        {
            JOptionPane.showMessageDialog(this, "Version is not selected yet.", "No version", JOptionPane.ERROR_MESSAGE);
            return;
        }

        success = true;
    }
    private void doButtonAccept()
    {
        doButtonValidate();
        if (!success) return;

        CheckVersionAndItem check = null;
        try
        {
            //System.out.println("CCC : 9");
            check = new CheckVersionAndItem((String) jcVersion.getSelectedItem(), (new CheckVersionAndItem()).getItems()[2]);

            String spr = File.separator;
            String pathN = path + spr + check.getVersion()[0] + spr + check.getItems()[1] + spr + "9901" + ".dat";

            boolean force = false;
            if (edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkFileExists(pathN))
            {
                String str = "Data type and defintion table look already installed.\nDo you want to overwrite them?";

                int res = JOptionPane.showConfirmDialog(this, str, "Overwrite Initial Meta?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (res != JOptionPane.YES_OPTION)
                {
                    force = true;
                }
                else this.dispose();
            }

            String pathF = jtPathSourceFile.getText();
            if ((pathF==null)||(pathF.trim()).equals(""))
            {
                this.dispose();
                return;
            }

            JOptionPane.showMessageDialog(this, "This installing may take some minutes. Please, wait till next message.", "Please, wait foe s while.", JOptionPane.INFORMATION_MESSAGE);

            CompareInstance compare = new CompareInstance(path, (String)jcVersion.getSelectedItem(), check.getItems()[2]);

            compare.initialInstall(pathF, force);

        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7MessageTreeException during install", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Installing process is complete.", "Complete", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();

    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/10/02 18:23:15  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/10/02 15:08:04  umkis
 * HISTORY      : Upgrade v2 meta collector
 * HISTORY      :
 */
