/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2ConverterToSCSPanel.java,v 1.1 2007-07-03 19:32:58 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The HL7 SDK Software License, Version 1.0
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

import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.ElementNode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.util.*;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 19:32:58 $
 */
public class V2ConverterToSCSPanel extends JPanel implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2ConverterToSCSPanel.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2ConverterToSCSPanel.java,v 1.1 2007-07-03 19:32:58 wangeug Exp $";

    private JRadioButton jrHL7MessageTypeOnly;
    private JRadioButton jrHL7MessageFile;
    private JRadioButton jrBoth;
    private JRadioButton jrCSV;
    private JRadioButton jrSCS;
    private JRadioButton jrOutputWhole;
    private JRadioButton jrOutputApparentOnly;
    private JRadioButton jrOBXSelection;
    private JRadioButton jrOBXApparentOnly;
    private JRadioButton jrOBXDataTypeSTOnly;
    private JRadioButton jrGroupingYes;
    private JRadioButton jrGroupingNo;

    private JLabel jlGrouping;

    private JLabel jlHL7VersionLabel;
    private JComboBox jcHL7Version;

    private JTextField jtInputMessageType;
    private JButton jbInputMessageTypeConfirm;

    private JTextField jtInputFile;
    private JButton jbInputFileBrowse;

    private JLabel jlInputSCSLabel;
    private JTextField jtInputSCSFile;
    private JButton jbInputSCSFileBrowse;

    private JLabel jlValidateSCSLabel;
    private JTextField jtValidateSCSFile;
    private JButton jbValidateSCSFileBrowse;

    private JLabel jlInputCSVLabel;
    private JTextField jtInputCSVFile;
    private JButton jbInputCSVFileBrowse;

    private String[] jcbOBXName = {"ST", "TX", "FT", "NM", "TS", "DT", "TM", "TN", "XTN", "CE",
                                   "MO", "CP", "SN", "ED" ,"RP" , "AD", "XAD", "PN", "XPN", "CX",
                                   "CF", "CN", "XCN", "CK", "XON"};
    private JCheckBox[] jcbOBXCheck = new JCheckBox[jcbOBXName.length];

    private JButton jbGenerate;
    private JButton jbReset;

    private JButton jbNext;
    private JButton jbClose;

    private JLabel jlPanelTitle;
    private JPanel optionSourcePanel;
    private JPanel optionOutputPanel;
    private JPanel inputSourcePanel;
    private JPanel inputFilesPanel;
    private JPanel optionOBXSelection;
    private JPanel generateButtonPanel;

    private boolean wasSuccessfullyParsed = false;
    private boolean foundOBXSegment = false;
    private boolean wasOBXUsed = false;
    private boolean buttonNextVisible = false;
    private boolean buttonCloseVisible = false;
    private String v2MetaDataPath = "";
    private int countV2Versions = 0;
    private HL7V2MessageTree v2Tree;
    private V2Converter converter;
    private java.util.List<String> listOBXDataType = new ArrayList<String>();

    private String versionDirTag = "Version";

    private AbstractMainFrame mainFrame = null;
    private JFrame frame = null;
    private JDialog dialog = null;
    private Dimension minimum = new Dimension(500, 685);

    V2ConverterToSCSPanel()
    {
        setupMainPanel();
    }
    public V2ConverterToSCSPanel(String v2DataPath) throws IOException
    {
        if (!setV2DataPath(v2DataPath).equals("OK")) throw new IOException("Invalid V2 meta data directory : " + v2DataPath);
        setupMainPanel();
    }
    public V2ConverterToSCSPanel(String v2DataPath, AbstractMainFrame mFrame) throws IOException
    {
        if (!setV2DataPath(v2DataPath).equals("OK")) throw new IOException("Invalid V2 meta data directory : " + v2DataPath);
        mainFrame = mFrame;
        //dialog = setupDialogBasedOnAFrame();
        setupMainPanel();
    }
    private void setInitialState()
    {
        jrHL7MessageTypeOnly.setSelected(false);
        jrHL7MessageTypeOnly.setEnabled(true);
        jrHL7MessageFile.setSelected(true);
        jrHL7MessageFile.setEnabled(true);

        jbNext.setEnabled(false);
        jbNext.setVisible(buttonNextVisible);
        jbClose.setVisible(buttonCloseVisible);

        jtInputFile.setEditable(false);
        jtInputSCSFile.setEditable(false);
        jtValidateSCSFile.setEditable(false);
        jtInputCSVFile.setEditable(false);

        jtInputFile.setText("");
        jtInputMessageType.setText("");
        jtInputSCSFile.setText("");
        jtValidateSCSFile.setText("");
        jtInputCSVFile.setText("");

        jbReset.setEnabled(true);

        partlyReset();
        setRadioButtonState();
    }
    private void partlyReset()
    {
        wasSuccessfullyParsed = false;
        foundOBXSegment = false;
        wasOBXUsed = false;

        listOBXDataType = new ArrayList<String>();
        converter = null;
        for(int i=0;i<jcbOBXCheck.length;i++) jcbOBXCheck[i].setSelected(false);
    }
    private void setRadioButtonState()
    {
        jrBoth.setEnabled(wasSuccessfullyParsed&&jrHL7MessageFile.isSelected());
        jrSCS.setEnabled(wasSuccessfullyParsed);
        jrCSV.setEnabled(wasSuccessfullyParsed&&jrHL7MessageFile.isSelected());
        if (jrHL7MessageFile.isSelected())
        {
            jrOutputWhole.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrSCS.isEnabled()&&jrSCS.isSelected()));
            jrOutputApparentOnly.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrSCS.isEnabled()&&jrSCS.isSelected()));
            jrOBXApparentOnly.setEnabled(wasSuccessfullyParsed&&foundOBXSegment);
        }
        else
        {
            jrOutputWhole.setSelected(true);
            jrOutputWhole.setEnabled(false);
            jrOutputApparentOnly.setEnabled(false);
            jrOBXApparentOnly.setEnabled(false);
            if (jrOBXApparentOnly.isSelected()) jrOBXSelection.setSelected(true);
        }

        jrGroupingYes.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!(jrOBXDataTypeSTOnly.isEnabled()&&jrOBXDataTypeSTOnly.isSelected())));
        jrGroupingNo.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!(jrOBXDataTypeSTOnly.isEnabled()&&jrOBXDataTypeSTOnly.isSelected())));
        jlGrouping.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!(jrOBXDataTypeSTOnly.isEnabled()&&jrOBXDataTypeSTOnly.isSelected())));

        jrOBXSelection.setEnabled(wasSuccessfullyParsed&&foundOBXSegment);
        jrOBXDataTypeSTOnly.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!wasOBXUsed));

        if (jrCSV.isSelected())
        {
            jrGroupingYes.setEnabled(false);
            jrGroupingNo.setEnabled(false);
            jlGrouping.setEnabled(false);
            jrOBXApparentOnly.setEnabled(false);
            jrOBXSelection.setEnabled(false);
            jrOBXDataTypeSTOnly.setEnabled(false);
        }

        jlHL7VersionLabel.setEnabled(jrHL7MessageTypeOnly.isSelected());
        jcHL7Version.setEnabled(jrHL7MessageTypeOnly.isSelected());
        jtInputMessageType.setEnabled(jrHL7MessageTypeOnly.isSelected());
        jtInputMessageType.setEditable(jrHL7MessageTypeOnly.isSelected());
        jbInputMessageTypeConfirm.setEnabled(jrHL7MessageTypeOnly.isSelected());

        jtInputFile.setEnabled(jrHL7MessageFile.isSelected());
        jbInputFileBrowse.setEnabled(jrHL7MessageFile.isSelected());

        jlValidateSCSLabel.setEnabled(jrCSV.isEnabled()&&jrCSV.isSelected());
        jtValidateSCSFile.setEnabled(jrCSV.isEnabled()&&jrCSV.isSelected());
        jbValidateSCSFileBrowse.setEnabled(jrCSV.isEnabled()&&jrCSV.isSelected());

        jlInputSCSLabel.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrSCS.isEnabled()&&jrSCS.isSelected()));
        jtInputSCSFile.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrSCS.isEnabled()&&jrSCS.isSelected()));
        jbInputSCSFileBrowse.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrSCS.isEnabled()&&jrSCS.isSelected()));

        jlInputCSVLabel.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrCSV.isEnabled()&&jrCSV.isSelected()));
        jtInputCSVFile.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrCSV.isEnabled()&&jrCSV.isSelected()));
        jbInputCSVFileBrowse.setEnabled((jrBoth.isEnabled()&&jrBoth.isSelected())||(jrCSV.isEnabled()&&jrCSV.isSelected()));

        if (!jtInputFile.isEnabled()) jtInputFile.setText("");
        if (!jtInputMessageType.isEnabled()) jtInputMessageType.setText("");
        if (!jtInputCSVFile.isEnabled()) jtInputCSVFile.setText("");
        if (!jtInputSCSFile.isEnabled()) jtInputSCSFile.setText("");
        if (!jtValidateSCSFile.isEnabled()) jtValidateSCSFile.setText("");

        for(int i=0;i<jcbOBXCheck.length;i++)
        {
            jcbOBXCheck[i].setEnabled((jrOBXSelection.isEnabled())&&(jrOBXSelection.isSelected()));
            //jcbOBXCheck[i].setSelected(false);
        }
        for(int j=0;j<listOBXDataType.size();j++)
        {
            for(int i=0;i<jcbOBXName.length;i++)
            {
                if (jcbOBXName[i].equals(listOBXDataType.get(j)))
                {
                    jcbOBXCheck[i].setSelected(true);
                }
            }
        }
        jbGenerate.setEnabled(wasSuccessfullyParsed);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == jbReset)
        {
            setInitialState();
        }
        if (e.getSource() == jbClose)
        {
            if (frame != null) frame.dispose();
            if (dialog != null) dialog.dispose();
        }
        if (e.getSource() == jbGenerate)
        {
            doPressGenerate();
            if (jbNext.isVisible()) jbNext.setEnabled(true);
        }
        if (e.getSource() == jbNext)
        {
            doPressNext();
        }
        if (e.getSource() == jbInputFileBrowse)
        {
            doPressHL7MessageFileBrowse();
        }
        if (e.getSource() == jbInputMessageTypeConfirm)
        {
            doPressHL7MessageTypeConfirm();
        }
        if ((e.getSource() == jbInputSCSFileBrowse)||
            (e.getSource() == jbInputCSVFileBrowse)||
            (e.getSource() == jbValidateSCSFileBrowse))
        {
            doPressSCSOrCSVFileBrowse((JButton) e.getSource());
        }
        if (e.getSource() == jrHL7MessageFile)
        {
            String st = jtInputMessageType.getText();
            if (st == null) st = "";
            if ((wasSuccessfullyParsed)&&((st.trim().length() == 7)&&(st.substring(3,4).equals("^"))))
            {
                jtInputMessageType.setText("");
                partlyReset();
            }
            jrOutputApparentOnly.setSelected(true);
            jrOBXApparentOnly.setSelected(true);
        }
        if (e.getSource() == jrHL7MessageTypeOnly)
        {
            String st = jtInputFile.getText();
            if (st == null) st = "";
            if ((wasSuccessfullyParsed)&&(st.trim().length() > 7))
            {
                jtInputFile.setText("");
                partlyReset();
            }
            jrOutputApparentOnly.setSelected(true);
            jrOBXApparentOnly.setSelected(true);
        }
        if (e.getSource() == jrOBXApparentOnly)
        {

        }
        if (e.getSource() == jrOBXDataTypeSTOnly)
        {
            for(int i=0;i<jcbOBXCheck.length;i++) jcbOBXCheck[i].setSelected(false);
            jcbOBXCheck[0].setSelected(true);
        }
        if (e.getSource() instanceof JCheckBox)
        {
            int idx = -1;

            for(int i=0;i<jcbOBXCheck.length;i++) if (e.getSource() == jcbOBXCheck[i]) idx = i;

            if (idx < 0) return;
            boolean cTag = false;
            for(int j=0;j<listOBXDataType.size();j++) if (jcbOBXName[idx].equals(listOBXDataType.get(j))) cTag = true;

            if (cTag)
            {
                JOptionPane.showMessageDialog(this, "This data type can not set 'Unselected' because of already being included in the source message", "Ready set Data Type",JOptionPane.WARNING_MESSAGE);
                jcbOBXCheck[idx].setSelected(true);
                return;
            }
        }
        if (e.getSource() instanceof JRadioButton) setRadioButtonState();

    }
    private String setV2DataPath(String v2DataPath)
    {
        try
        {
            HL7V2MessageTree mt = new HL7V2MessageTree(v2DataPath);
        }
        catch(HL7MessageTreeException he)
        {
            return he.getMessage();
        }
        v2MetaDataPath = v2DataPath;
        return "OK";
    }
    private void setupMainPanel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {  }
        jlPanelTitle = new JLabel("V2 Message Converter Main", JLabel.CENTER);
        optionSourcePanel = wrappingBorder("V2 Message Source" ,optionPanel_MessageOrType());
        optionOutputPanel = wrappingBorder("Output File Option", constructOutputOptionPanel());
        //inputSourcePanel = wrappingBorder("Input Source", inputV2Source());
        inputFilesPanel = wrappingBorder("Output Files", inputOutputFiles());
        optionOBXSelection = wrappingBorder("OBX Data Type Selection", optionOBXDataTypes());
        generateButtonPanel = generateButtonPanel();

        //this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        constructMainPanel();
    }
    private void constructMainPanel()
    {

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JLabel(""), BorderLayout.WEST);
        center.add(optionOutputPanel, BorderLayout.CENTER);
        center.add(new JLabel(""), BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());

        //top.add(inputSourcePanel, BorderLayout.NORTH);
        top.add(center, BorderLayout.CENTER);
        top.add(inputFilesPanel, BorderLayout.SOUTH);


        JPanel mid = new JPanel(new BorderLayout());

        mid.add(optionSourcePanel, BorderLayout.NORTH);
        mid.add(top, BorderLayout.CENTER);
        mid.add(optionOBXSelection, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(mid, BorderLayout.CENTER);
        bottom.add(generateButtonPanel, BorderLayout.SOUTH);


        add(jlPanelTitle, BorderLayout.NORTH);
        add(bottom, BorderLayout.CENTER);
        //add(new JPanel(), BorderLayout.SOUTH);
        add(new JLabel(" "), BorderLayout.WEST);
        add(new JLabel(" "), BorderLayout.EAST);
        //optionOBXSelection.setEnabled(false);
        setInitialState();
        this.setMinimumSize(getMinimumSize());
    }
    private JPanel wrappingBorder(String borderTitle, JPanel aPanel)
    {
        //Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
        JPanel titledBorders = new JPanel(new BorderLayout());
        //JPanel titledBorders = new JPanel(new GridLayout(1, 0));
        //titledBorders.setBorder(paneEdge);

        TitledBorder titled = BorderFactory.createTitledBorder(borderTitle);
        titledBorders.add(aPanel, BorderLayout.CENTER);
        titledBorders.setBorder(titled);
        //titledBorders.add(Box.createRigidArea(new Dimension(0, 10)));

        return titledBorders;
    }
    private Object[] setupRadioButtonPanel(JRadioButton radioButton01, String buttonLabel01, String buttonCommand01, boolean selected01,
                                         JRadioButton radioButton02, String buttonLabel02, String buttonCommand02, boolean selected02, boolean vertical)
    {
        return setupRadioButtonPanel(radioButton01, buttonLabel01, buttonCommand01, selected01,
                                     radioButton02, buttonLabel02, buttonCommand02, selected02,
                                     null, null, null, false, vertical);
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
    private JPanel optionPanel_MessageOrType()
    {
        Object[] out = setupRadioButtonPanel(jrHL7MessageFile, "Message File", "File", true,
                              jrHL7MessageTypeOnly, "Message Type", "Type", false, false);

        ((JPanel)out[0]).removeAll();

        jrHL7MessageFile = (JRadioButton) out[1];
        jrHL7MessageTypeOnly = (JRadioButton) out[2];

        JPanel bPanel = new JPanel(new BorderLayout());
        bPanel.add(jrHL7MessageTypeOnly, BorderLayout.WEST);

        Object[] out1 = inputFileNameCommon("", jtInputMessageType, jbInputMessageTypeConfirm, "Confirm", "Confirm");
        bPanel.add((JPanel)out1[0], BorderLayout.CENTER);
        jtInputMessageType =(JTextField) out1[2];
        jbInputMessageTypeConfirm = (JButton) out1[3];

        JPanel cPanel = new JPanel(new BorderLayout());
        cPanel.add(bPanel, BorderLayout.CENTER);

        File aFile = new File(v2MetaDataPath);
        File[] files = aFile.listFiles();
        java.util.List<String> listVersionDir = new ArrayList<String>();

        jcHL7Version = new JComboBox();
        for (int i=0;i<files.length;i++)
        {
            File file = files[i];

            if(!file.isDirectory()) continue;
            String dirName = file.getName();
            //System.out.println("VersionsXX = > " + dirName);// + " : " + dirName.substring(versionDirTag.length()));

            if (dirName.toLowerCase().startsWith(versionDirTag.toLowerCase()))
            {
                listVersionDir.add(dirName.substring(versionDirTag.length()));
                //System.out.println("Versions = > " + dirName + " : " + dirName.substring(versionDirTag.length()));
            }
        }
        countV2Versions = listVersionDir.size();
        jcHL7Version = new JComboBox(listVersionDir.toArray());


        JPanel ePanel = new JPanel(new BorderLayout());

        String ss = "";
        if (listVersionDir.size() > 1) ss = "s";

        jlHL7VersionLabel = new JLabel("                      Available Version"+ss+"  ", JLabel.LEFT);
        ePanel.add(jlHL7VersionLabel, BorderLayout.WEST);

        ePanel.add(jcHL7Version, BorderLayout.CENTER);
        cPanel.add(ePanel, BorderLayout.SOUTH);

        JPanel dPanel = new JPanel(new BorderLayout());
        dPanel.add(jrHL7MessageFile, BorderLayout.WEST);

        Object[] out2 = inputFileNameCommon("", jtInputFile, jbInputFileBrowse, "Browse..", "Browse");
        dPanel.add((JPanel) out2[0], BorderLayout.CENTER);
        jtInputFile = (JTextField) out2[2];
        jbInputFileBrowse = (JButton) out2[3];

        cPanel.add(dPanel, BorderLayout.NORTH);

        return cPanel;
    }

    private JPanel constructOutputOptionPanel()
    {
        //Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10);
        JPanel aPanel = new JPanel(new BorderLayout());

        JPanel bPanel = new JPanel(new BorderLayout());
        bPanel.add(optionPanel_OutputFiles(), BorderLayout.CENTER);
        bPanel.add(new JLabel(" "), BorderLayout.WEST);

        aPanel.add(bPanel, BorderLayout.CENTER);
        aPanel.add(wrappingBorder("SCS File Option", optionPanel_SCSOption_WholeOrApparent()),BorderLayout.SOUTH);

        return aPanel;
    }
    private JPanel optionPanel_OutputFiles()
    {
        Object[] out = setupRadioButtonPanel(jrBoth, "Both", "Both", false,
                                             jrSCS, "SCS", "SCS", true,
                                             jrCSV, "CSV", "CSV", true, false);
        jrBoth = (JRadioButton) out[1];
        jrSCS = (JRadioButton) out[2];
        jrCSV = (JRadioButton) out[3];
        JPanel north= (JPanel) out[0];


        Object[] out2 = inputFileNameCommon("   Validating SCS for this CSV ", jtValidateSCSFile, jbValidateSCSFileBrowse, "Browse..", "Browse");
        JPanel north2 = new JPanel(new BorderLayout());
        north2.add((JPanel) out2[0], BorderLayout.NORTH);
        jlValidateSCSLabel = (JLabel) out2[1];
        jtValidateSCSFile = (JTextField) out2[2];
        jbValidateSCSFileBrowse = (JButton) out2[3];
        north.add(north2, BorderLayout.CENTER);
        return north;
    }
    private JPanel optionPanel_SCSOption_WholeOrApparent()
    {
        Object[] out = setupRadioButtonPanel(jrOutputApparentOnly, "Apparent Segments only in this Message File", "Apparent", true,
                                     jrOutputWhole, "Whole Segments of this message type", "Whole", false, true);
        jrOutputApparentOnly = (JRadioButton) out[1];
        jrOutputWhole = (JRadioButton) out[2];
        return (JPanel) out[0];
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

    private JPanel inputOutputFiles()
    {
        //JPanel aPanel = new JPanel(new GridLayout(0, 1));
        JPanel aPanel = new JPanel(new BorderLayout());

        Object[] out = inputFileNameCommon("SCS File ", jtInputSCSFile, jbInputSCSFileBrowse, "Browse..", "Browse");
        aPanel.add((JPanel) out[0], BorderLayout.NORTH);
        jlInputSCSLabel = (JLabel) out[1];
        jtInputSCSFile = (JTextField) out[2];
        jbInputSCSFileBrowse = (JButton) out[3];

        out = inputFileNameCommon("CSV File ", jtInputCSVFile, jbInputCSVFileBrowse, "Browse..", "Browse");
        aPanel.add((JPanel) out[0], BorderLayout.CENTER);
        jlInputCSVLabel = (JLabel) out[1];
        jtInputCSVFile = (JTextField) out[2];
        jbInputCSVFileBrowse = (JButton) out[3];

        //aPanel.add(inputFileNameCommon("CSV File ", jtInputCSVFile, jbInputCSVFileBrowse, "Browse..", "Browse"), BorderLayout.CENTER);
        return aPanel;
    }
    private JPanel optionOBXDataTypes()
    {
        Object[] out = setupRadioButtonPanel(jrOBXApparentOnly, "Apparent Data Type only", "ApparentDT", true,
                                             jrOBXSelection, "Selecting Data Types", "Selecting", false,
                                             jrOBXDataTypeSTOnly, "ST Data Type only", "STOnly", false, false);
        JPanel north = wrappingBorder("OBX Option", (JPanel)out[0] );
        jrOBXApparentOnly = (JRadioButton) out[1];
        jrOBXSelection = (JRadioButton) out[2];
        jrOBXDataTypeSTOnly = (JRadioButton) out[3];

        out = setupRadioButtonPanel(jrGroupingYes, "Yes", "Yes", false,
                                    jrGroupingNo, "No", "No", true, false);
        jlGrouping = new JLabel("Want grouping? (ST, TX and FT will be simplfied into ST)", JLabel.LEFT);

        JPanel north1 = new JPanel(new BorderLayout());
        north1.add(jlGrouping, BorderLayout.NORTH);
        north1.add((JPanel)out[0], BorderLayout.CENTER);
        JPanel north2 = wrappingBorder("Grouping", north1);
        jrGroupingYes = (JRadioButton) out[1];
        jrGroupingNo = (JRadioButton) out[2];



        JPanel selectionOBX = new JPanel(new GridBagLayout());
        int insetN = 1;
        Insets insets = new Insets(insetN, insetN, insetN, insetN);

        for(int i=0;i<jcbOBXName.length;i++)
        {
            String obxName = "";
            if (jcbOBXName[i].length() == 2) obxName = jcbOBXName[i] + " ";
            else obxName = jcbOBXName[i];

            jcbOBXCheck[i] = new JCheckBox(obxName);
            jcbOBXCheck[i].setSelected(false);
            jcbOBXCheck[i].setEnabled(false);
            jcbOBXCheck[i].addActionListener(this);
            int modeBase = 9;

            if ((i+1) == modeBase) selectionOBX.add(jcbOBXCheck[i], new GridBagConstraints((i % modeBase), (i / modeBase), 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            else selectionOBX.add(jcbOBXCheck[i], new GridBagConstraints((i % modeBase), (i / modeBase), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        }
        //JScrollPane jsp = new JScrollPane();
        //jsp.add(selectionOBX);

        JPanel obxMain = new JPanel(new BorderLayout());
        obxMain.add(north, BorderLayout.NORTH);
        obxMain.add(north2, BorderLayout.CENTER);
        obxMain.add(selectionOBX, BorderLayout.SOUTH);
        return obxMain;
    }
    private JPanel generateButtonPanel()
    {
        jbGenerate = new JButton("Generate");
        jbGenerate.setActionCommand("Generate");
        jbGenerate.addActionListener(this);
        jbGenerate.setEnabled(true);
        jbReset = new JButton("Reset");
        jbReset.setActionCommand("Reset");
        jbReset.addActionListener(this);
        jbReset.setEnabled(true);

        jbNext = new JButton("Next");
        jbNext.setActionCommand("Next");
        jbNext.addActionListener(this);
        jbNext.setEnabled(true);
        jbClose = new JButton("Close");
        jbClose.setActionCommand("Close");
        jbClose.addActionListener(this);
        jbClose.setEnabled(true);

        JPanel east = new JPanel(new BorderLayout());

        east.add(jbGenerate, BorderLayout.WEST);
        east.add(jbClose, BorderLayout.EAST);
        east.add(new JLabel(" "), BorderLayout.CENTER);

        JPanel west = new JPanel(new BorderLayout());

        west.add(jbNext, BorderLayout.WEST);
        west.add(jbReset, BorderLayout.EAST);
        west.add(new JLabel(" "), BorderLayout.CENTER);


        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel(" "), BorderLayout.CENTER);
        south.add(east, BorderLayout.EAST);
        south.add(west, BorderLayout.WEST);
        return south;

        //JPanel aPanel = new JPanel(new GridLayout(1, 0));
        //aPanel.add(jbGenerate);
        //aPanel.add(jbReset);
        //return aPanel;
    }
    private void doPressGenerate()
    {
        try
        {
            converter = new V2Converter(v2Tree);
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "V2 Converter initialize error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.List<String> listDataTypeOfOBX = new ArrayList<String>();

        for(int i=0;i<jcbOBXCheck.length;i++)
        {
            if (jcbOBXCheck[i].isSelected())
            {
                listDataTypeOfOBX.add(jcbOBXName[i].trim());
            }
        }

        String fileSCS = jtInputSCSFile.getText();
        String fileCSV = jtInputCSVFile.getText();
        String fileSCSValidate = jtValidateSCSFile.getText();

        if (fileSCS == null) fileSCS = "";
        if (fileCSV == null) fileCSV = "";
        if (fileSCSValidate == null) fileSCSValidate = "";

        converter.process(fileSCS, fileCSV, jrOutputApparentOnly.isSelected(), jrGroupingYes.isSelected(), listDataTypeOfOBX, fileSCSValidate);
        if (converter.wasSuccessful())
        {
            boolean validateResultSCS = true;
            boolean validateResultCSV = true;
            if ((!fileSCS.equals(""))&&(!converter.isSCSValid())) validateResultSCS = false;
            if ((!fileCSV.equals(""))&&(!converter.isCSVValid())) validateResultCSV = false;
            if ((validateResultSCS)&&(validateResultCSV))
            {
                JOptionPane.showMessageDialog(this, "V2 Converter Process is successfully Complete!", "V2 Converter Process Complete!",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                ValidationMessageDialog dialogVal = null;

                if (frame != null) dialogVal = new ValidationMessageDialog(frame, "V2 Converter Validation Result", true);
                else if (dialog != null) dialogVal = new ValidationMessageDialog(dialog, "V2 Converter Validation Result", true);
                else
                {
                    return;
                }
                dialogVal.setValidatorResults(converter.getValidationResults());

                dialogVal.setVisible(true);
            }
        }
        else
        {
            String errMsg = "";
            if(converter.getErrorMessage().trim().equals("")) errMsg = "null message";
            else errMsg = converter.getErrorMessage();
            JOptionPane.showMessageDialog(this, errMsg, "V2 Converter Process error!!",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void doPressHL7MessageFileBrowse()
    {   /*
        JFileChooser chooser = new JFileChooser(new File(FileUtil.getWorkingDirPath()));

        int returnVal = chooser.showOpenDialog(this);
        String pathValue = "";
        if(returnVal != JFileChooser.APPROVE_OPTION) return;

        pathValue = chooser.getSelectedFile().getAbsolutePath();
        */
        File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
					"", Config.OPEN_DIALOG_TITLE_FOR_HL7_V3_MESSAGE_FILE.replace("3", "2"), false, false);
        if (file == null) return;
        String pathValue = file.getAbsolutePath();
        try
        {
            parseV2Message(v2MetaDataPath, "2.4", pathValue);
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7 Message Parsing Error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jtInputFile.setText(pathValue);
    }
    private void doPressSCSOrCSVFileBrowse(JButton button)
    {

        String extension = "";
        if (button == jbInputSCSFileBrowse) extension = Config.CSV_METADATA_FILE_DEFAULT_EXTENTION;
        else if (button == jbInputCSVFileBrowse) extension = Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
        else if (button == jbValidateSCSFileBrowse) extension = Config.CSV_METADATA_FILE_DEFAULT_EXTENTION;

        /*
        JFileChooser chooser = new JFileChooser(new File(FileUtil.getWorkingDirPath()));
        chooser.setFileFilter(new SingleFileFilter(extension));
        int returnVal = chooser.showOpenDialog(this);
        String pathValue = "";
        if(returnVal != JFileChooser.APPROVE_OPTION) return;
        pathValue = chooser.getSelectedFile().getAbsolutePath();
        */

        File file1 = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
					extension, "Open " + extension + " File..", (button != jbValidateSCSFileBrowse), false);
        if (file1 == null) return;
        String pathValue = file1.getAbsolutePath();

        for(int i=(pathValue.length()-1);i>=0;i--)
        {
            String achar = pathValue.substring(i, i+1);

            if (achar.equals(File.separator))
            {
                pathValue = pathValue + extension;
                break;
            }
            if (achar.equals(".")) break;
        }

        if (!pathValue.toLowerCase().endsWith(extension.toLowerCase()))
        {
            JOptionPane.showMessageDialog(this, "File name must be ended with '" + extension + "'.", "Invalid File extension",JOptionPane.ERROR_MESSAGE);
            return;
        }
        File file = new File(pathValue);
        if (file.exists())
        {
            if (button != jbValidateSCSFileBrowse)
            {
                int res = JOptionPane.showConfirmDialog(this, pathValue + " is already exist. Are you sure to overwrite it?", "Duplicated File Name", JOptionPane.YES_NO_OPTION);
                if (res != JOptionPane.YES_OPTION) return;
            }
            else
            {
                if (!file.isFile())
                {
                    JOptionPane.showMessageDialog(this, "This is not a file : " + pathValue, "Not a file",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        else
        {
            if (button == jbValidateSCSFileBrowse)
            {
                JOptionPane.showMessageDialog(this, "This file is not exist : " + pathValue, "Not exist file",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (button == jbInputSCSFileBrowse) jtInputSCSFile.setText(pathValue);
        else if (button == jbInputCSVFileBrowse) jtInputCSVFile.setText(pathValue);
        else if (button == jbValidateSCSFileBrowse) jtValidateSCSFile.setText(pathValue);
    }
    private void doPressNext()
    {
        if (mainFrame == null)
        {
            JOptionPane.showMessageDialog(this, "This Frame diesn't connect to the MainFrame.", "Null MainFrame",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (converter == null)
        {
            JOptionPane.showMessageDialog(this, "Generating Process is not complete.", "Null MainFrame",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (converter.getSCSFileName().equals(""))
        {
            JOptionPane.showMessageDialog(this, "No SCS File was generated.", "No Output SCS File",JOptionPane.ERROR_MESSAGE);
            return;
        }
        /*
        int ans = JOptionPane.showConfirmDialog(this, "Do you want to edit SCS file?", "Editing SCS or MAP ", JOptionPane.YES_NO_CANCEL_OPTION);
        if (ans == JOptionPane.YES_OPTION)
        {
            CSVPanel cp = new CSVPanel();

            try
            {
			    ValidatorResults result = cp.setSaveFile(new File(converter.getSCSFileName()), true);
			    mainFrame.addNewTab(cp);
            }
            catch (Exception ee)
            {
			    JOptionPane.showMessageDialog(this, "Unexpected Exception(34) : " + ee.getMessage(), "Unexpected Exception",JOptionPane.ERROR_MESSAGE);
                return;
		    }
        }
        else if (ans == JOptionPane.NO_OPTION)
        {
            gov.nih.nci.caadapter.ui.main.map.MappingPanel mp;
		    try
            {
			    mp = new gov.nih.nci.caadapter.ui.main.map.MappingPanel(converter.getSCSFileName(), null);
			    mainFrame.addNewTab(mp);
            }
            catch (Exception ee)
            {
			    JOptionPane.showMessageDialog(this, "Unexpected Exception(34) : " + ee.getMessage(), "Unexpected Exception",JOptionPane.ERROR_MESSAGE);
                return;
		    }
        }
        else return;
        */
        V2ConverterNextProcessDialog nextDialog = null;
        if (dialog != null) nextDialog = new V2ConverterNextProcessDialog(dialog);
        if (frame != null) nextDialog = new V2ConverterNextProcessDialog(frame);

        if (nextDialog == null)
        {
            JOptionPane.showMessageDialog(this, "With Neither dialog nor frame", "No parent component",JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultSettings.centerWindow(nextDialog);
		nextDialog.setVisible(true);
        //System.out.println("VVVV v: " +  nextDialog.wasClickedGoNext());
        if (!nextDialog.wasClickedGoNext()) return;
        //System.out.println("VVVV a: ");
        if (nextDialog.wasSelectedSCS())
        {
            CSVPanel cp = new CSVPanel();
            //System.out.println("VVVV b: ");
            try
            {
			    ValidatorResults result = cp.setSaveFile(new File(converter.getSCSFileName()), true);
			    mainFrame.addNewTab(cp);
            }
            catch (Exception ee)
            {
			    JOptionPane.showMessageDialog(this, "SCS Panel Exception : " + ee.getMessage(), "Unexpected Exception",JOptionPane.ERROR_MESSAGE);
                return;
		    }
        }
        else if (nextDialog.wasSelectedMAP())
        {
            gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel mp = null;
            //System.out.println("VVVV c: ");
            try
            {
                if (nextDialog.getH3SFileName().equals(""))
                    mp = new gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel(converter.getSCSFileName(), null);
			    else if (!nextDialog.getH3SFileName().equals(""))
                    mp = new gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel(converter.getSCSFileName(), nextDialog.getH3SFileName(), null);
//	                   mp = new gov.nih.nci.caadapter.ui.mapping.hl7.NewHL7MappingPanel(converter.getSCSFileName(), nextDialog.getH3SFileName(), null);

                mainFrame.addNewTab(mp);
            }
            catch (Exception ee)
            {
			    JOptionPane.showMessageDialog(this, "Map Panel Exception : " + ee.getMessage(), "Unexpected Exception",JOptionPane.ERROR_MESSAGE);
                return;
		    }
        }
        //System.out.println("VVVV d: ");
        if (dialog != null) dialog.dispose();
        if (frame != null) frame.dispose();
    }
    private void doPressHL7MessageTypeConfirm()
    {
        String type = jtInputMessageType.getText();
        if (type == null) type = "";
        type = type.trim();
        if ((type.length() != 7)||(!type.substring(3, 4).equals("^")))
        {
            JOptionPane.showMessageDialog(this, "Invalid Message Type : " + type, "Invalid Message Type", JOptionPane.ERROR_MESSAGE);
            jtInputMessageType.setFocusable(true);
            return;
        }

        String version = jcHL7Version.getSelectedItem().toString();
        if (version == null) version = "";
        version = version.trim();
        if (version.length() < 3)
        {
            JOptionPane.showMessageDialog(this, "Invalid HL7 Version : " + version, "Invalid HL7 Version", JOptionPane.ERROR_MESSAGE);
            jtInputMessageType.setFocusable(true);
            return;
        }

        try
        {
            parseV2Message(v2MetaDataPath, version, type);
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7 Message Parsing Error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void parseV2Message(String dataPath, String versionS, String messageSourse) throws HL7MessageTreeException
    {
        messageSourse = messageSourse.trim();
        wasSuccessfullyParsed = false;
        try
        {
            v2Tree = new HL7V2MessageTree(dataPath, versionS, messageSourse);
        }
        catch(HL7MessageTreeException he)
        {
            throw he;
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
            throw new HL7MessageTreeException("Un expected Exception - This may not be a HL7 V2 Message. : " + ee.getMessage());
        }
        wasSuccessfullyParsed = true;


        ElementNode node = null;
        try
        {
            node = v2Tree.nodeAddressSearch("OBX.1.2");
            foundOBXSegment = true;
        }
        catch(HL7MessageTreeException he)
        {
            node = null;
            foundOBXSegment = false;
        }

        if ((messageSourse.substring(3, 4).equals("^"))&&(messageSourse.length() == 7))
        {
            jtInputFile.setText("");
            listOBXDataType = new ArrayList<String>();
        }
        else
        {
            jtInputMessageType.setText("");
            if (foundOBXSegment) listOBXDataType = searchOBXDataTypes();


        }

        if (listOBXDataType.size() > 0) wasOBXUsed = true;
        else wasOBXUsed = false;


        setRadioButtonState();
    }

    private  java.util.List<String> searchOBXDataTypes()
    {
        java.util.List<String> list = new ArrayList<String>();
        ElementNode node = v2Tree.getHeadNode();
        boolean cTag = false;
        while(true)
        {
            node = v2Tree.nextTraverse(node);
            if (node == null) break;
            if (node == v2Tree.getHeadNode()) break;
            if (!((node.getLevel().equals("field"))&&(node.getSequence() == 2))) continue;
            if (!node.getUpperLink().getType().equals("OBX")) continue;

            cTag = false;
            String dt = node.getValue().trim();
            for(int i=0;i<jcbOBXName.length;i++) if (dt.equals(jcbOBXName[i])) cTag = true;
            if (!cTag) continue;
            cTag = false;
            for(int i=0;i<list.size();i++) if (dt.equals(list.get(i))) cTag = true;
            if (!cTag) list.add(dt);
        }
        return list;
    }
    public void setNextButtonVisible()
    {
        buttonNextVisible = true;
        jbNext.setVisible(buttonNextVisible);
    }
    public void setCloseButtonVisible()
    {
        buttonNextVisible = true;
        jbClose.setVisible(buttonNextVisible);
    }
    public Dimension getMinimumSize()
    {
        return minimum;
    }
    public JFrame setFrame(JFrame frame)
    {
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.addWindowListener(new FrameCloseExit(frame));
        frame.setMinimumSize(getMinimumSize());
        this.frame = frame;
        this.dialog = null;
        return frame;
    }
    public JDialog setDialog(JDialog dialog)
    {
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.addWindowListener(new DialogCloseExit(dialog));
        dialog.setMinimumSize(getMinimumSize());
        this.frame = null;
        this.dialog = dialog;
        return dialog;
    }
    public JDialog setupDialogBasedOnMainFrame(AbstractMainFrame mFrame) throws HeadlessException
    {
        JDialog dialog = new JDialog(mFrame, "V2 Converting to SCS and CSV");
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.addWindowListener(new DialogCloseExit(dialog));
        dialog.setMinimumSize(getMinimumSize());
        this.frame = null;
        this.dialog = dialog;
        mainFrame = mFrame;
        return dialog;
    }
    class FrameCloseExit extends WindowAdapter
    {
        JFrame tt;

        FrameCloseExit(JFrame st) { tt = st; }
        public void windowClosing(WindowEvent e)
        {
            tt.dispose();
        }
    }
    class DialogCloseExit extends WindowAdapter
    {
        JDialog tt;

        DialogCloseExit(JDialog st) { tt = st; }
        public void windowClosing(WindowEvent e)
        {
            tt.dispose();
        }
    }

    public static void main(String arg[])
    {
        String dataPath = "C:\\projects\\hl7sdk\\data\\v2Meta";
        try
        {
            JFrame frame = (new V2ConverterToSCSPanel(dataPath)).setFrame(new JFrame("V2 Converter"));

            frame.setSize(500, 640);
            frame.setVisible(true);
        }
        catch(IOException he)
        {}

    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2006/12/05 20:01:56  umkis
 * HISTORY      : minor modifying
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/10 03:56:02  umkis
 * HISTORY      : V2-V3 mapping job version 1.0
 * HISTORY      :
 */
