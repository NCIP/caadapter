/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2MetaCollectorDialog.java,v 1.3 2007-09-26 16:24:16 umkis Exp $
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

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.File;

import edu.knu.medinfo.hl7.v2tree.images.source.mapping.CheckVersionAndItem;
import edu.knu.medinfo.hl7.v2tree.images.source.mapping.GroupingMetaInstance;
import edu.knu.medinfo.hl7.v2tree.images.source.mapping.CompareInstance;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Sep 23, 2007
 *          Time:       6:57:06 PM $
 */
public class V2MetaCollectorDialog extends JDialog implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2MetaCollectorDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2MetaCollectorDialog.java,v 1.3 2007-09-26 16:24:16 umkis Exp $";

    private String title = "V2 Meta Data Collector";



    //private JComboBox jcMessageType;
    private JComboBox jcVersion;
    private JComboBox jcItem;
    private JComboBox jcName;

    private JTextField jtPath;
    private JTextField jtMessage;
    private JTextArea jaData;

    private JButton jbBrowse;
    private JButton jbAccept;
    private JButton jbValidate;
    private JButton jbCancel;

    private JLabel jlPath;

    private JDialog dialog;

    private String path;

    private int versionIndex = -1;
    private boolean success = false;
    private CompareInstance compare = null;
    private boolean substructure = false;
    private String substructureLevel = null;
    private String substructureName = null;
    private int substructureSegmentIndex = 0;
    private int substructureTableIndex = 0;
    private java.util.List<String> substructureSegmentList = null;
    private java.util.List<String> substructureTableList = null;

    public V2MetaCollectorDialog(JDialog dialog)
    {
        super(dialog, "V2 Meta Data Collector", true);
        initialize();
    }
    public V2MetaCollectorDialog(JFrame frame)
    {
        super(frame, "V2 Meta Data Collector", true);
        initialize();
    }

    private void initialize()
    {
        Object[] ob = inputFileNameCommon(" Meta Data Base Dir ", jtPath, jbBrowse, "Browse", "Browse");
        JPanel pathPanel = (JPanel) ob[0];
        jlPath = (JLabel) ob[1];
        jtPath = (JTextField) ob[2];
        jbBrowse = (JButton) ob[3];
        jtPath.setText(FileUtil.getV2DataDirPath());
        jtPath.setEditable(false);

        JPanel buttonPanel = generateButtonPanel();

        JPanel comboPanel = generateVersionAndItemPanel();

        JPanel namePanel = generateNamePanel();

        jaData = new JTextArea();
        jaData.setEditable(true);
        JScrollPane js2 = new JScrollPane(jaData);
        js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel messagePanel = new JPanel(new BorderLayout());
        jtMessage = new JTextField();
        jtMessage.setEditable(false);
        jtMessage.setForeground(Color.RED);
        messagePanel.add(jtMessage, BorderLayout.CENTER);
        messagePanel.add(new JLabel(" Message : "), BorderLayout.WEST);

        JPanel level1 = new JPanel(new BorderLayout());
        level1.add(js2, BorderLayout.CENTER);
        level1.add(namePanel, BorderLayout.NORTH);
        level1.add(messagePanel, BorderLayout.SOUTH);

        JPanel level2 = new JPanel(new BorderLayout());
        level2.add(level1, BorderLayout.CENTER);
        level2.add(comboPanel, BorderLayout.NORTH);
        level2.add(buttonPanel, BorderLayout.SOUTH);

        JPanel level3 = new JPanel(new BorderLayout());
        level3.add(level2,BorderLayout.CENTER);
        level3.add(pathPanel,BorderLayout.NORTH);
        level3.add(new JLabel(" "),BorderLayout.SOUTH);

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

        setSize(550, 500);
        //setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == jbValidate)
        {
            doButtonValidate();
        }
        if (e.getSource() == jbCancel)
        {
            this.dispose();
        }
        if (e.getSource() == jbBrowse)
        {
            File file = DefaultSettings.getUserInputOfFileFromGUI(this, FileUtil.getV2DataDirPath(), null, "V2 Meta Base Directory", false, false);
            jtPath.setText(file.getAbsolutePath());
            path = jtPath.getText();
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

        jbValidate = new JButton("Validate");
        jbValidate.setActionCommand("Validate");
        jbValidate.addActionListener(this);
        jbValidate.setEnabled(true);

        JPanel west = new JPanel(new BorderLayout());

        west.add(new JLabel(" "), BorderLayout.WEST);
        west.add(jbValidate, BorderLayout.EAST);
        west.add(new JLabel(" "), BorderLayout.CENTER);


        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(east, BorderLayout.EAST);
        south.add(west, BorderLayout.WEST);

        return south;
    }

    private JPanel generateNamePanel()
    {
        JPanel east = new JPanel(new BorderLayout());

        east.add(new JLabel(" Name "), BorderLayout.WEST);
        east.add(jcName, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(east, BorderLayout.EAST);

        JLabel areaLabel = new JLabel(" Meta Unload Area ");
        Font font = new Font("Courier", Font.ITALIC, 12);

        areaLabel.setFont(font);
        areaLabel.setForeground(Color.BLUE);

        south.add(areaLabel, BorderLayout.WEST);

        return south;
    }


    private JPanel generateVersionAndItemPanel()
    {
        CheckVersionAndItem check = new CheckVersionAndItem();
        jcVersion = new JComboBox();
        String versionT = "Select Domain.";
        jcVersion.addItem(versionT);
        for(String version:check.getVersionTo()) jcVersion.addItem(version);
        jcVersion.setEditable(false);

        jcItem = new JComboBox();
        String itemT = "Select Item.";
        jcItem.addItem(itemT);
        for(String item:check.getItemTo()) jcItem.addItem(item);
        jcItem.setEditable(false);

        jcName = new JComboBox();
        jcName.setEnabled(false);


        jcVersion.addItemListener(
            new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if ((jcVersion.getSelectedIndex() > 0)&&(jcItem.getSelectedIndex() > 0))
                    {
                        CheckVersionAndItem check = new CheckVersionAndItem();
                        GroupingMetaInstance group = null;
                        try
                        {
                            group = new GroupingMetaInstance(check.getVersionTo()[jcVersion.getSelectedIndex()], check.getItemTo()[jcItem.getSelectedIndex()]);
                        }
                        catch(HL7MessageTreeException he) { return; }
                        jcName.setEnabled(true);
                        jcName.removeAllItems();
                        jcName.addItem("Select Name");
                        
                        for(String name:group.getOutList()) //jcName.addItem(name);
                        {

                            jcName.addItem(name);
                        }
                    }
                }
            }
        );
        jcItem.addItemListener(
            new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if ((jcVersion.getSelectedIndex() > 0)&&(jcItem.getSelectedIndex() > 0))
                    {
                        CheckVersionAndItem check = new CheckVersionAndItem();
                        GroupingMetaInstance group = null;
                        try
                        {
                            group = new GroupingMetaInstance(check.getVersionTo()[jcVersion.getSelectedIndex()-1], check.getItemTo()[jcItem.getSelectedIndex()-1]);
                        }
                        catch(HL7MessageTreeException he) { return; }


                        jcName.setEnabled(true);
                        jcName.removeAllItems();
                        jcName.addItem("Select Name");

                        for(String name:group.getOutList())
                        {

                            jcName.addItem(name);
                        }
                    }
                }
            }
        );

        JPanel east = new JPanel(new BorderLayout());

        east.add(new JLabel(" Item "), BorderLayout.WEST);
        east.add(jcItem, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);

        JPanel west = new JPanel(new BorderLayout());

        west.add(new JLabel(" Version "), BorderLayout.WEST);
        west.add(jcVersion, BorderLayout.EAST);
        west.add(new JLabel(" "), BorderLayout.CENTER);


        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);

        south.add(east, BorderLayout.EAST);
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
        String tempPath = jtPath.getText();
        if ((tempPath == null)||(tempPath.trim().equals("")))
        {
            JOptionPane.showMessageDialog(this, "Null Target V2 Meta Directory.", "No Target Directory.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tempPath.equals(FileUtil.getV2DataDirPath()))
        {
            if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkDirectoryExists(tempPath))
            {
                int ans = JOptionPane.showConfirmDialog(this, "Base V2 Meta Directory is not exist yet.\nDo you want to create now?", "Creat v2 Base Directory?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ans == JOptionPane.YES_OPTION)
                {
                    File dir = new File(tempPath);
                    if (!dir.mkdirs())
                    {
                        JOptionPane.showMessageDialog(this, "Create Directory Failure : " + tempPath, "Create Directory Failure", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                else return;
            }
        }
        else
        {
            if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkDirectoryExists(tempPath))
            {
                JOptionPane.showMessageDialog(this, "Target v2 Meta Directory is Invalid", "Invalid Directory", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        path = tempPath;
        //System.out.println("CCC : tempPath : " + tempPath);
        //System.out.println("CCC : jcVersion : " + jcVersion.getSelectedIndex());
        if (jcVersion.getSelectedIndex() == 0)
        {
            JOptionPane.showMessageDialog(this, "Version is not selected yet.", "No version", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (jcItem.getSelectedIndex() == 0)
        {
            JOptionPane.showMessageDialog(this, "Item is not selected yet.", "No item", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jcName.getSelectedIndex() == 0)
        {
            JOptionPane.showMessageDialog(this, "Name is not selected yet.", "No name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("CCC : 1");
        String tempData = jaData.getText();
        //System.out.println("CCC : tempData : " + tempData);
        if ((tempData == null)||(tempData.trim().equals("")))
        {
            JOptionPane.showMessageDialog(this, "No meta data", "No meta Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (substructure)
        {
            if ((substructureLevel == null)||(substructureLevel.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "No sub-structure level", "No sub-structure level", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((substructureName == null)||(substructureName.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "No sub-structure name", "No sub-structure name", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        System.out.println("CCC : 2");

        try
        {
            if (substructure)
            {
                System.out.println("CCC : 3");
                compare = new CompareInstance(path, (String)jcVersion.getSelectedItem(), substructureLevel);
                System.out.println("CCC : 4");
                if (!compare.doCompare(tempData.trim(), substructureName))
                {
                    JOptionPane.showMessageDialog(this, "This input meta data is not fit for '" + substructureName + "' of" + (String)jcVersion.getSelectedItem() + " " + (String)jcItem.getSelectedItem(), "Unmatched input data", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            else
            {
                System.out.println("CCC : 5");
                compare = new CompareInstance(path, (String)jcVersion.getSelectedItem(), (String)jcItem.getSelectedItem());
                System.out.println("CCC : 6");
                String name = (String)jcName.getSelectedItem();
                if (!compare.doCompare(tempData.trim(), name))
                {
                    JOptionPane.showMessageDialog(this, "This input meta data is not fit for '" + name + "' of" + (String)jcVersion.getSelectedItem() + " " + (String)jcItem.getSelectedItem(), "Unmatched input data", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                System.out.println("CCC : 7");
            }
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7MessageTreeException during comparing", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("CCC : 8");
        success = true;
    }
    private void doButtonAccept()
    {
        doButtonValidate();
        if (!success) return;

        CheckVersionAndItem check = null;
        try
        {
            System.out.println("CCC : 9");
            check = new CheckVersionAndItem((String) jcVersion.getSelectedItem(), (String) jcItem.getSelectedItem());
                       
            String spr = File.separator;
            String pathN = path + spr + check.getVersion()[0] + spr + check.getItems()[1] + spr + "9901" + ".dat";

            if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkFileExists(pathN))
            {
                String str = "This may be the first time meta installing.\nDo you want to install initial meta data? \n(Data type and defintion table) ";

                int res = JOptionPane.showConfirmDialog(this, str, "Install Initial Meta", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res != JOptionPane.YES_OPTION)
                {
                    this.dispose();
                    return;
                }

                InitialInstallDialog dial = new InitialInstallDialog(this);
                String pathF = dial.getPath();
                if ((pathF==null)||(pathF.trim()).equals(""))
                {
                    this.dispose();
                    return;
                }
                compare.initialInstall(pathF, false);
            }
            System.out.println("CCC : 10");
            compare.doInstall();
            if (substructure)
            {
                System.out.println("CCC : 11-1");
                check = new CheckVersionAndItem((String) jcVersion.getSelectedItem(), substructureLevel);
                if (substructureLevel.equals(check.getItemTo()[3]))
                {
                    //String pathD = jtPath.getText().trim() + spr + check.getVersion()[0] + spr + check.getItems()[3];
                    substructureTableList = compare.gatheringDefinitionTable(path, substructureTableList, substructureName);
                }
            }
            else
            {
                System.out.println("CCC : 11-2");
                check = new CheckVersionAndItem((String) jcVersion.getSelectedItem(), (String) jcItem.getSelectedItem());
            }
            System.out.println("CCC : 12");
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7MessageTreeException during install", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String spr = File.separator;

        String item = (String) jcItem.getSelectedItem();
        boolean hasNext = false;
        if (item.equals(check.getItemTo()[2]))
        {
            if (substructureSegmentList == null) substructureSegmentList = compare.getSegmentList();

            for (int i=substructureSegmentIndex;i<substructureSegmentList.size();i++)
            {
                String seg = substructureSegmentList.get(i);
                String pathN = path + spr + check.getVersion()[0] + spr + check.getItem()[0] + spr + seg + ".dat";

                if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkFileExists(pathN))
                {
                    hasNext = true;
                    substructure = true;
                    substructureLevel = check.getItemTo()[3];
                    substructureName = seg;
                    String str = "Copy and Paste '"+seg+"' segment data from the manual";
                    JOptionPane.showMessageDialog(this, str, "Input segment meta", JOptionPane.INFORMATION_MESSAGE);

                    jtMessage.setText(str);
                    substructureSegmentIndex = i+1;
                    break;
                }
            }
        }

        if (hasNext)
        {
            jbBrowse.setEnabled(false);
            jcVersion.setEnabled(false);
            jcItem.setEnabled(false);
            jcName.setEnabled(false);
            jaData.setText("");
            return;
        }

        substructureSegmentIndex = 0;
        substructureSegmentList = null;
        /*
        if ((item.equals(check.getItemTo()[2]))||(item.equals(check.getItemTo()[3])))
        {
            if (substructureTableList == null) substructureTableList = compare.getTableList();

            for (int i=substructureTableIndex;i<substructureTableList.size();i++)
            {
                String tbl = substructureTableList.get(i);
                String pathN = path + spr + check.getVersion()[0] + spr + check.getItem()[0] + spr + tbl + ".dat";

                if (!edu.knu.medinfo.hl7.v2tree.images.source.mapping.FileUtil.checkFileExists(pathN))
                {
                    hasNext = true;
                    substructure = true;
                    substructureLevel = check.getItemTo()[1];
                    substructureName = tbl;
                    String str = "Copy and Paste '" + tbl + "' defintion table data from the manual";
                    JOptionPane.showMessageDialog(this, str, "Input definition table meta", JOptionPane.INFORMATION_MESSAGE);

                    jtMessage.setText(str);
                    substructureTableIndex = i+1;
                    break;
                }
            }
        }
        if (hasNext)
        {
            jbBrowse.setEnabled(false);
            jcVersion.setEnabled(false);
            jcItem.setEnabled(false);
            jcName.setEnabled(false);
            jaData.setText("");
            return;
        }
        */

        jbBrowse.setEnabled(true);
        jcVersion.setEditable(true);
        jcItem.setEditable(true);
        jcName.setEditable(true);
        jaData.setText("");

        substructure = false;
        substructureTableIndex = 0;
        substructureTableList = null;
        substructureLevel = null;
        substructureName = null;
        jtMessage.setText("");
        
    }

}



/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/09/25 15:22:58  umkis
 * HISTORY      : update V2 meta data collector
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/09/24 20:05:28  umkis
 * HISTORY      : Add v2 Meta data collector
 * HISTORY      :
 */
