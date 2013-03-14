/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import edu.knu.medinfo.hl7.v2tree.ElementNode;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import edu.knu.medinfo.hl7.v2tree.meta_old.MetaDataLoader;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.12 $
 *          date        $Date: 2009-04-24 18:17:55 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/V2ConverterToSCSPanel.java,v 1.12 2009-04-24 18:17:55 wangeug Exp $";

    private JRadioButton jrStrictValidationYes;
    private JRadioButton jrStrictValidationNo;
    private JLabel jlStrictValidation;
    //private JRadioButton jrHL7MessageTypeOnly = new JRadioButton();
    //private JRadioButton jrHL7MessageFile = new JRadioButton();
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

    private JLabel jlGrouping1;
    private JLabel jlGrouping2;

    private JLabel jlHL7VersionLabel;
    private JComboBox jcHL7Version;

    private JLabel jlInputMessageTypeLabel;
    private JTextField jtInputMessageType;
    private JButton jbInputMessageTypeConfirm;
    private JButton jbViewErrorMessages;

    private JTextField jtDataDirectory;
    private JButton jbDataDirectoryBrowse;

    private JLabel jlInputFileLabel;
    private JTextField jtInputFile;
    private JButton jbInputFileBrowse;

    private JLabel jlInputSCSLabel;
    private JTextField jtInputSCSFile;
    private JButton jbInputSCSFileBrowse;

    private JLabel jlValidateSCSLabel;
    private JTextField jtValidateSCSFile;
    private JButton jbValidateSCSFileBrowse;

    private JLabel jlMultiMessageNote = new JLabel("The input v2 message file has multi messages.");

    private JLabel jlInputCSVLabel;
    private JTextField jtInputCSVFile;
    private JButton jbInputCSVFileBrowse;

    private String[] jcbOBXName = {"ST", "TX", "FT", "NA", "MA", "NM", "TS", "DT", "TM", "TN", "XTN",
                                   "CE", "CD", "MO", "CP", "SN", "ED" ,"RP" , "AD", "XAD", "PN", "XPN",
                                   "CX", "CN", "XCN", "CK", "XON"};
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
    private boolean msgTypeOnly = false;
    private boolean hasMultiMessages = false;
    private String oneMessage = "";
    private File oneMessageFile = null;
    private String segmentsOBX = "";
    private MetaDataLoader v2MetaDataPath = null;

    private HL7V2MessageTree v2Tree;
    private V2Converter converter;
    private java.util.List<String> listOBXDataType = new ArrayList<String>();
    //private java.util.List<String> listOBXDataType2 = new ArrayList<String>();

    private ValidatorResults validatorResults = new ValidatorResults();
    private java.util.List<String> errorMessages = new ArrayList<String>();

    private String versionDirTag = "version";

    private AbstractMainFrame mainFrame = null;
    private JFrame frame = null;
    private JDialog dialog = null;
    private Dimension minimum = new Dimension(740, 700);

    public V2ConverterToSCSPanel()
    {
        try
        {
            setupMainPanel(null);
        }
        catch(IOException ee) {}
    }
    public V2ConverterToSCSPanel(Object v2DataPath) throws IOException
    {
        setupMainPanel(v2DataPath);
    }
    public V2ConverterToSCSPanel(Object v2DataPath, AbstractMainFrame mFrame) throws IOException
    {
        setupMainPanel(v2DataPath);
        mainFrame = mFrame;
    }
    private boolean isValidDataDirectory()
    {
        String dataDir = jtDataDirectory.getText();
        if ((dataDir == null)||(dataDir.trim().equals(""))) dataDir = "";
        dataDir = dataDir.trim();
        //System.out.println("CCCC OK 6 : " + dataDir);
        if (setV2DataPath(dataDir).equals("OK")) return true;
        jtDataDirectory.setText("");
        return false;
    }
    private void setInitialState()
    {

        if (jcHL7Version.getItemCount() > 0) jcHL7Version.setEnabled(true);
        else jcHL7Version.setEnabled(false);


        jtDataDirectory.setEditable(false);
        jtInputFile.setEditable(false);

        jbNext.setEnabled(false);
        jbNext.setVisible(buttonNextVisible);
        jbClose.setVisible(buttonCloseVisible);

        jbViewErrorMessages.setEnabled(false);

        jtInputSCSFile.setEditable(false);
        jtValidateSCSFile.setEditable(true);
        jtInputCSVFile.setEditable(true);
        jtInputSCSFile.setEditable(true);

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
        msgTypeOnly = false;

        msgTypeOnly = false;
        hasMultiMessages = false;
        oneMessage = "";
        oneMessageFile = null;

        validatorResults = new ValidatorResults();
        errorMessages = new ArrayList<String>();

        listOBXDataType = new ArrayList<String>();
        //listOBXDataType2 = new ArrayList<String>();
        converter = null;
        for(int i=0;i<jcbOBXCheck.length;i++) jcbOBXCheck[i].setSelected(false);
    }
    private void setRadioButtonState()
    {
        boolean cTag = isValidDataDirectory();

        jtInputMessageType.setEditable(cTag);
        jtInputFile.setEnabled(cTag);

        jtInputMessageType.setEnabled(cTag);
        jbInputMessageTypeConfirm.setEnabled(cTag);
        jbInputFileBrowse.setEnabled(cTag);
        jlInputFileLabel.setEnabled(cTag);
        jlInputMessageTypeLabel.setEnabled(cTag);
        jlHL7VersionLabel.setEnabled(cTag);
        jcHL7Version.setEnabled(cTag);
        jcHL7Version.setEditable(false);
        jrStrictValidationYes.setEnabled(cTag);
        jrStrictValidationNo.setEnabled(cTag);
        jlStrictValidation.setEnabled(cTag);


        //String fileN = jtInputSCSFile.getText();
        String fileN = jtInputFile.getText();
        boolean fTag = (!((fileN==null)||(fileN.trim().equals(""))));
        jrBoth.setEnabled(wasSuccessfullyParsed&&fTag);
        jrSCS.setEnabled(wasSuccessfullyParsed);
        jrCSV.setEnabled(wasSuccessfullyParsed&&fTag);
//        if (hasMultiMessages)
//        {
//            jrBoth.setEnabled(false);
//            jrSCS.setEnabled(wasSuccessfullyParsed&&hasMultiMessages);
//            jrCSV.setEnabled(false);
//        }
        if (fTag)
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
        jlGrouping1.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!(jrOBXDataTypeSTOnly.isEnabled()&&jrOBXDataTypeSTOnly.isSelected())));
        jlGrouping2.setEnabled(jlGrouping1.isEnabled());
        jrOBXSelection.setEnabled(wasSuccessfullyParsed&&foundOBXSegment);
        jrOBXDataTypeSTOnly.setEnabled(wasSuccessfullyParsed&&foundOBXSegment&&(!wasOBXUsed));

        jlMultiMessageNote.setVisible(hasMultiMessages);

        if (jrCSV.isSelected())
        {
            jrGroupingYes.setEnabled(false);
            jrGroupingNo.setEnabled(false);
            jlGrouping1.setEnabled(false);
            jlGrouping2.setEnabled(jlGrouping1.isEnabled());
            jrOBXApparentOnly.setEnabled(false);
            jrOBXSelection.setEnabled(false);
            jrOBXDataTypeSTOnly.setEnabled(false);
        }
          //msgTypeOnly
//        jlHL7VersionLabel.setEnabled(jrHL7MessageTypeOnly.isSelected());
//        jcHL7Version.setEnabled(jrHL7MessageTypeOnly.isSelected());
//        jtInputMessageType.setEnabled(jrHL7MessageTypeOnly.isSelected());
//        jtInputMessageType.setEditable(jrHL7MessageTypeOnly.isSelected());
//        jbInputMessageTypeConfirm.setEnabled(jrHL7MessageTypeOnly.isSelected());



//        jtInputFile.setEnabled(jrHL7MessageFile.isSelected());
//        jbInputFileBrowse.setEnabled(jrHL7MessageFile.isSelected());

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
        if ((v2MetaDataPath == null)&&(e.getSource() != jbDataDirectoryBrowse))
        {
            JOptionPane.showMessageDialog(this, "First of all, you MUST set v2 meta resource.", "No v2 meta source", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (e.getSource() == jbDataDirectoryBrowse)
        {
            doPressDataDirectoryBrowse();
        }
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
        if (e.getSource() == jbViewErrorMessages)
        {
            doPressViewErrorMessages();
        }
        if ((e.getSource() == jbInputSCSFileBrowse)||
            (e.getSource() == jbInputCSVFileBrowse)||
            (e.getSource() == jbValidateSCSFileBrowse))
        {
            doPressSCSOrCSVFileBrowse((JButton) e.getSource());
        }
//        if (e.getSource() == jrHL7MessageFile)
//        {
//            String st = jtInputMessageType.getText();
//            if (st == null) st = "";
//            if ((wasSuccessfullyParsed)&&((st.trim().length() == 7)&&(st.substring(3,4).equals("^"))))
//            {
//                jtInputMessageType.setText("");
//                partlyReset();
//            }
//            jrOutputApparentOnly.setSelected(true);
//            jrOBXApparentOnly.setSelected(true);
//        }
//        if (e.getSource() == jrHL7MessageTypeOnly)
//        {
//            String st = jtInputFile.getText();
//            if (st == null) st = "";
//            if ((wasSuccessfullyParsed)&&(st.trim().length() > 7))
//            {
//                jtInputFile.setText("");
//                partlyReset();
//            }
//            jrOutputApparentOnly.setSelected(true);
//            jrOBXApparentOnly.setSelected(true);
//        }
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
    private String setV2DataPath(Object v2DataPath)
    {
        if ((v2MetaDataPath != null)&&(v2DataPath != null))
        {
            if (v2DataPath instanceof String)
            {
                String c = (String)v2DataPath;
                String tt = v2MetaDataPath.getSourceXSDAddress();
                if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getSourceTextAddress();
                if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getTextMetaPath();
                if (c.equals(tt)) return "OK";
            }
            else if (v2DataPath instanceof MetaDataLoader)
            {
                MetaDataLoader l = (MetaDataLoader) v2DataPath;
                if (v2MetaDataPath == l) return "OK";
            }
        }

        HL7V2MessageTree mt = null;
        //boolean cTag = false;
        String msg = null;
        try
        {
            if ((v2DataPath != null)&&(v2DataPath instanceof String))
            {
                String c = (String)v2DataPath;
                if (c.trim().equals("")) v2DataPath = null;
                if (c.trim().equalsIgnoreCase("null")) v2DataPath = null;
                //System.out.println("CCCC OK 1 : " + c);
            }

            if ((v2DataPath == null)||(v2DataPath instanceof String))
            {
                MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader((String)v2DataPath);
                if (loader == null) throw new HL7MessageTreeException("V2 Meta Data Loader creation failure");
                //else mt = new HL7V2MessageTree(loader);
                v2MetaDataPath = loader;
                return "OK";
                //System.out.println("CCCC OK 2 : ");// + loader.getPath());
                //cTag = true;
            }
            else
            {
                mt = new HL7V2MessageTree(v2DataPath);
                v2MetaDataPath = mt.getMetaDataLoader();
                return "OK";
                //System.out.println("CCCC OK 3 : ");
                //cTag = true;
            }

//            if (v2DataPath instanceof String)
//            {
//                File dir = new File((String)v2DataPath);
//                if (dir.isDirectory())
//                {
//                    for (File sDir:dir.listFiles())
//                    {
//                        if (!sDir.isDirectory()) continue;
//                        try
//                        {
//                            mt.setVersion(sDir.getName());
//                            cTag = true;
//                        }
//                        catch(HL7MessageTreeException he)
//                        {
//                            msg = he.getMessage();
//                            continue;
//                        }
//                        break;
//                    }
//                }
//            }

        }
        catch(HL7MessageTreeException he)
        {
            msg = he.getMessage();
        }
        catch(Exception ee)
        {
            msg = ee.getMessage();
        }
//        if (cTag)
//        {
//            v2MetaDataPath = mt.getMetaDataLoader();//v2DataPath;
//            //System.out.println("CCCC OK : " + v2MetaDataPath.getPath());
//            return "OK";
//        }
        //System.out.println("CCCC err : " + msg);
        return msg;
    }

    private void setupMainPanel(Object v2DataPathObject) throws IOException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {  }

        boolean cTag = false;
        if ((v2DataPathObject != null)&&(v2DataPathObject instanceof String))
        {
            String v2DataPath = (String) v2DataPathObject;
            if (v2DataPath.trim().equals(""))
            {
                v2DataPathObject = null;
                cTag = true;
            }
            else
            {
                v2DataPath = v2DataPath.trim();

                MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader(v2DataPath);

                if (loader != null) v2MetaDataPath = loader;
                else throw new IOException("Invalid V2 meta data directory or zip file : " + v2DataPath);

//                jcHL7Version = new JComboBox();
//                jtDataDirectory = new JTextField();
//                jtDataDirectory.setText(v2DataPath);
//                if (setV2DataPath(v2DataPath).equals("OK")) setHL7VersionComboBox(v2DataPath);
//                else throw new IOException("Invalid V2 meta data directory or zip file : " + v2DataPath);
            }
        }
        else cTag = true;

        if(cTag)
        {
            if (v2DataPathObject == null)
            {
                v2MetaDataPath = null;
                System.out.println("KKKKK : null loader");
                //MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader();

                //if (loader == null) throw new IOException("V2 Meta Data Loader creation failure");
                //v2DataPathObject = loader;
            }
            else if (v2DataPathObject instanceof MetaDataLoader)
            {
                v2MetaDataPath = (MetaDataLoader) v2DataPathObject;
            }
            else throw new IOException("Invalid instance of v2 meta object : ");
        }
        jcHL7Version = new JComboBox();
        jtDataDirectory = new JTextField();
        if (v2MetaDataPath == null)
        {
            jtDataDirectory.setText("");
            setHL7VersionComboBox(null);
        }
        else
        {
            String tt = v2MetaDataPath.getSourceXSDAddress();
            if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getSourceTextAddress();
            if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getTextMetaPath();
            System.out.println("KKKKK loader Path : " + tt);
            jtDataDirectory.setText(tt);
            setHL7VersionComboBox(v2MetaDataPath);
        }
        setupMainPanel();
    }

    private void setupMainPanel()
    {

        //jlPanelTitle = new JLabel("V2 Message Converter Main", JLabel.CENTER);
        jlPanelTitle = new JLabel("", JLabel.CENTER);
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

        bottom.add(wrappingBorder("V2 Meta Source" ,panel_DataDirectory()), BorderLayout.NORTH);
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
    private JPanel panel_DataDirectory()
    {
        JPanel bPanel = new JPanel(new BorderLayout());
        //bPanel.add(jrHL7MessageTypeOnly, BorderLayout.WEST);

        Object[] out1 = inputFileNameCommon("V2 Meta Dir or Zip  ", jtDataDirectory, jbDataDirectoryBrowse, "Browse", "Browse..");
        bPanel.add((JPanel)out1[0], BorderLayout.CENTER);
        jtDataDirectory =(JTextField) out1[2];
        jbDataDirectoryBrowse = (JButton) out1[3];

        if (v2MetaDataPath == null) jtDataDirectory.setText("");
        else
        {
            String tt = v2MetaDataPath.getSourceXSDAddress();
            if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getSourceTextAddress();
            if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getTextMetaPath();
            jtDataDirectory.setText(tt);
        }
        JPanel cPanel = new JPanel(new BorderLayout());
        cPanel.add(bPanel, BorderLayout.CENTER);

        if (jcHL7Version == null) jcHL7Version = new JComboBox();

        JPanel ePanel = new JPanel(new BorderLayout());
        jlHL7VersionLabel = new JLabel("                      Target Version  ", JLabel.LEFT);
        ePanel.add(jlHL7VersionLabel, BorderLayout.WEST);


        ePanel.add(jcHL7Version, BorderLayout.CENTER);
        cPanel.add(ePanel, BorderLayout.SOUTH);

        return cPanel;
    }

    private JPanel optionPanel_MessageOrType()
    {
//        Object[] out = setupRadioButtonPanel(jrHL7MessageFile, "Message File", "File", true,
//                              jrHL7MessageTypeOnly, "Message Type", "Type", false, false);
//
//        ((JPanel)out[0]).removeAll();
//
//        jrHL7MessageFile = (JRadioButton) out[1];
//        jrHL7MessageTypeOnly = (JRadioButton) out[2];

        JPanel bPanel = new JPanel(new BorderLayout());
        bPanel.add((new JLabel("")), BorderLayout.WEST);

        Object[] out1 = inputFileNameCommon("Message Type", jtInputMessageType, jbInputMessageTypeConfirm, "Start V2 Parsing", "Start V2 Parsing");
        bPanel.add((JPanel)out1[0], BorderLayout.CENTER);
        jlInputMessageTypeLabel = (JLabel) out1[1];
        jtInputMessageType =(JTextField) out1[2];
        jbInputMessageTypeConfirm = (JButton) out1[3];

        JPanel cPanel = new JPanel(new BorderLayout());
        cPanel.add(bPanel, BorderLayout.CENTER);

//        File aFile = new File(v2MetaDataPath);
//        File[] files = aFile.listFiles();
//        java.util.List<String> listVersionDir = new ArrayList<String>();
//
//        jcHL7Version = new JComboBox();
//        for (int i=0;i<files.length;i++)
//        {
//            File file = files[i];
//
//            if(!file.isDirectory()) continue;
//            String dirName = file.getName();
//            //System.out.println("VersionsXX = > " + dirName);// + " : " + dirName.substring(versionDirTag.length()));
//
//            if (dirName.toLowerCase().startsWith(versionDirTag.toLowerCase()))
//            {
//                listVersionDir.add(dirName.substring(versionDirTag.length()));
//                //System.out.println("Versions = > " + dirName + " : " + dirName.substring(versionDirTag.length()));
//            }
//        }
//        countV2Versions = listVersionDir.size();
//        jcHL7Version = new JComboBox(listVersionDir.toArray());
//        for(int i=0;i<jcHL7Version.getItemCount();i++)
//        {
//            String ver = (String) jcHL7Version.getItemAt(i);
//            if (ver.indexOf("2.4") >= 0) jcHL7Version.setSelectedIndex(i);
//        }
//        JPanel ePanel = new JPanel(new BorderLayout());
//
//        String ss = "";
//        if (listVersionDir.size() > 1) ss = "s";
//
//        jlHL7VersionLabel = new JLabel("                      Available Version"+ss+"  ", JLabel.LEFT);
//        ePanel.add(jlHL7VersionLabel, BorderLayout.WEST);
//
//        ePanel.add(jcHL7Version, BorderLayout.CENTER);
//        cPanel.add(ePanel, BorderLayout.SOUTH);
        cPanel.add(optionStrictValidation(), BorderLayout.SOUTH);

        JPanel dPanel = new JPanel(new BorderLayout());
        dPanel.add((new JLabel()), BorderLayout.WEST);

        Object[] out2 = inputFileNameCommon("Message File", jtInputFile, jbInputFileBrowse, "Browse..", "Browse");
        dPanel.add((JPanel) out2[0], BorderLayout.CENTER);

        JPanel fPanel = new JPanel(new BorderLayout());
        jlMultiMessageNote = new JLabel("This input v2 message file has multi v2 messages.");
        jlMultiMessageNote.setHorizontalAlignment(JLabel.TRAILING);
        fPanel.add(jlMultiMessageNote, BorderLayout.EAST);
        fPanel.add(new JLabel(""), BorderLayout.WEST);
        dPanel.add(fPanel, BorderLayout.NORTH);
        jlInputFileLabel = (JLabel) out2[1];
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
        JPanel north = (JPanel) out[0];


        Object[] out2 = inputFileNameCommon("  Validating SCS ", jtValidateSCSFile, jbValidateSCSFileBrowse, "Browse..", "Browse");
        JPanel north2 = new JPanel(new BorderLayout());
        north2.add((JPanel) out2[0], BorderLayout.NORTH);
        jlValidateSCSLabel = (JLabel) out2[1];
        jtValidateSCSFile = (JTextField) out2[2];
        jbValidateSCSFileBrowse = (JButton) out2[3];

        //north.add(north2, BorderLayout.CENTER);

        JPanel north3 = new JPanel(new BorderLayout());
        north3.add(north, BorderLayout.WEST);
        north3.add(north2, BorderLayout.CENTER);
        return north3;
    }
    private JPanel optionPanel_SCSOption_WholeOrApparent()
    {
        Object[] out = setupRadioButtonPanel(jrOutputApparentOnly, "Apparent Segments only    ", "Apparent", true,
                                     jrOutputWhole, "Whole Segments of this message type", "Whole", false, false);
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

    private JPanel optionStrictValidation()
    {
        Object[] out = setupRadioButtonPanel(jrStrictValidationYes, "Yes", "Yes", false,
                                    jrStrictValidationNo, "No", "No", true, false);
        jlStrictValidation = new JLabel("Strict Validation?", JLabel.LEFT);

        JPanel north1 = new JPanel(new BorderLayout());
        north1.add(jlStrictValidation, BorderLayout.WEST);
        north1.add((JPanel)out[0], BorderLayout.CENTER);
        jbViewErrorMessages = new JButton("View Error Messages");
        jbViewErrorMessages.addActionListener(this);
        north1.add(jbViewErrorMessages, BorderLayout.EAST);
        jrStrictValidationYes = (JRadioButton) out[1];
        jrStrictValidationNo = (JRadioButton) out[2];
        return north1;
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
        jlGrouping1 = new JLabel("Want grouping? ", JLabel.LEFT);
        jlGrouping2 = new JLabel("   (If Yes, ST, TX and FT will be simplfied into ST.)", JLabel.LEFT);
        JPanel north1 = new JPanel(new FlowLayout(FlowLayout.LEADING));
        north1.add(jlGrouping1);//, BorderLayout.WEST);
        //JPanel north12 = new JPanel(new BorderLayout());

        north1.add((JPanel)out[0]);//, BorderLayout.CENTER);
        north1.add(jlGrouping2);//, BorderLayout.WEST);
        //north1.add(north12, BorderLayout.EAST);
        JPanel north2 = wrappingBorder("Grouping", north1);

        jrGroupingYes = (JRadioButton) out[1];
        jrGroupingNo = (JRadioButton) out[2];



        JPanel selectionOBX = new JPanel(new GridBagLayout());
        //JPanel selectionOBX = new JPanel(new FlowLayout(FlowLayout.LEADING));
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
            int modeBase = 14;

            if ((i+1) == modeBase) selectionOBX.add(jcbOBXCheck[i], new GridBagConstraints((i % modeBase), (i / modeBase), 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            else selectionOBX.add(jcbOBXCheck[i], new GridBagConstraints((i % modeBase), (i / modeBase), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            //selectionOBX.add(jcbOBXCheck[i]);
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
        if (hasMultiMessages) doPressGenerateMultiMessage();
        else doPressGenerateSingleMessage();
    }
    private void doPressGenerateMultiMessage()
    {
        String fileSCS = jtInputSCSFile.getText();
        String fileCSV = jtInputCSVFile.getText();
        String fileSCSValidate = jtValidateSCSFile.getText();

        if (fileSCS == null) fileSCS = "";
        if (fileCSV == null) fileCSV = "";
        if (fileSCSValidate == null) fileSCSValidate = "";
        //System.out.println("DDDDDD : start");
        if ((jrBoth.isSelected())||(jrSCS.isSelected()))
        {
            if (!doPressGenerateSingleMessage(v2Tree, fileSCS, "", "", !jrSCS.isSelected())) return;
            //System.out.println("DDDDDD : OK");
        }
        if ((jrBoth.isSelected())||(jrCSV.isSelected()))
        {
            String scsVal = "";
            if (jrBoth.isSelected()) scsVal = fileSCS;
            if (jrCSV.isSelected()) scsVal = fileSCSValidate;

            boolean strict = jrStrictValidationYes.isSelected();
            String mType = jtInputMessageType.getText();
            if (mType == null) mType = "";
            mType = mType.trim();
            String versionS = ((String)jcHL7Version.getSelectedItem()).trim();

            ConvertFromV2ToCSV con = new ConvertFromV2ToCSV(v2MetaDataPath, jtInputFile.getText().trim(), mType, versionS, fileCSV, scsVal, strict);

            JOptionPane.showMessageDialog(this, con.getMessage(), con.getMessageTitle(), con.getErrorLevel());
        }
    }

    public java.util.List<String> getErrorMessages()
    {
        return errorMessages;
    }

    private void doPressGenerateSingleMessage()
    {

        String fileSCS = jtInputSCSFile.getText();
        String fileCSV = jtInputCSVFile.getText();
        String fileSCSValidate = jtValidateSCSFile.getText();

        if (fileSCS == null) fileSCS = "";
        if (fileCSV == null) fileCSV = "";
        if (fileSCSValidate == null) fileSCSValidate = "";

        doPressGenerateSingleMessage(v2Tree, fileSCS, fileCSV, fileSCSValidate, false);
    }
    public boolean doPressGenerateSingleMessage(HL7V2MessageTree tree, String fileSCS, String fileCSV, String fileSCSValidate, boolean mute)
    {

        try
        {
            converter = new V2Converter(tree);
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "V2 Converter initialize error!",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        java.util.List<String> listDataTypeOfOBX = new ArrayList<String>();

        for(int i=0;i<jcbOBXCheck.length;i++)
        {
            if (jcbOBXCheck[i].isSelected())
            {
                listDataTypeOfOBX.add(jcbOBXName[i].trim());
            }
        }

        converter.process(fileSCS, fileCSV, jrOutputApparentOnly.isSelected(), jrGroupingYes.isSelected(), listDataTypeOfOBX, fileSCSValidate);
        if (converter.wasSuccessful())
        {
            boolean validateResultSCS = true;
            boolean validateResultCSV = true;
            if ((!fileSCS.equals(""))&&(!converter.isSCSValid())) validateResultSCS = false;
            if ((!fileCSV.equals(""))&&(!converter.isCSVValid())) validateResultCSV = false;
            if ((validateResultSCS)&&(validateResultCSV))
            {
                if (!mute) JOptionPane.showMessageDialog(this, "V2 Converter Process is successfully Complete!", "V2 Converter Process Complete!",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                java.util.List<Message> messages = converter.getValidationResults().getAllMessages();
                if (messages.size() > 0)
                {
                    errorMessages.add("###=== Converter error : SCS=" + fileSCS + ", CSV=" + fileCSV + ", Validation=" + fileSCSValidate);
                    for (Message message:messages) errorMessages.add(message.toString());

                    if (mute)
                    {
                        validatorResults.addValidatorResults(converter.getValidationResults());
                    }
                    else
                    {
                        ValidationMessageDialog dialogVal = null;

                        if (frame != null) dialogVal = new ValidationMessageDialog(frame, "V2 Converter Validation Result", true);
                        else if (dialog != null) dialogVal = new ValidationMessageDialog(dialog, "V2 Converter Validation Result", true);
                        else
                        {
                            return false;
                        }
                        validatorResults.addValidatorResults(converter.getValidationResults());
                        dialogVal.setValidatorResults(validatorResults);
                        dialogVal.setVisible(true);
                        validatorResults = new ValidatorResults();
                    }
                }
            }
        }
        else
        {
            String errMsg = "";
            if(converter.getErrorMessage().trim().equals("")) errMsg = "null message";
            else errMsg = converter.getErrorMessage();
            validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, errMsg);
            errorMessages.add("###=== Conveting Error: " + errMsg);
            if (!mute) JOptionPane.showMessageDialog(this, errMsg, "V2 Converter Process error!!",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void doPressDataDirectoryBrowse()
    {
        if (jcHL7Version.getItemCount() > 0) jcHL7Version.setEnabled(true);
        else jcHL7Version.setEnabled(false);

        String dataDir = jtDataDirectory.getText();
        if ((dataDir == null)||(dataDir.trim().equals(""))) dataDir = "";
        File ff = new File(dataDir);
        if ((ff.exists())&&(ff.isFile()))
        {
            dataDir = ff.getParentFile().getAbsolutePath();
        }
        File fileD = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
					dataDir, "*", "Finding v2 Meta Data Directory", false, false);//Config.OPEN_DIALOG_TITLE_FOR_HL7_V3_MESSAGE_FILE.replace("3", "2"), false, false);
        if (fileD == null) return;
        if (!fileD.isDirectory())
        {
            if (!fileD.getName().toLowerCase().endsWith(".zip"))
            {
                JOptionPane.showMessageDialog(this, "This is Neither a directory nor a zip file : " + fileD.getAbsolutePath(), "Meta data directory finding error!",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        //MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader(dataDir);
        MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader(fileD.getAbsolutePath());
        if (loader == null)
        {
            JOptionPane.showMessageDialog(this, "This is Neither a V2 meta directory nor a V2 Meta zip file : " + fileD.getAbsolutePath(), "Meta data directory finding error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        v2MetaDataPath = loader;
        if (setHL7VersionComboBox(loader))
        {
            //System.out.println("CCCC OK 7 : " + v2MetaDataPath.getPath() + ", " + dataDir);
            //String tt = v2MetaDataPath.getSourceXSDAddress();
            //if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getSourceTextAddress();
            //if ((tt == null)||(tt.trim().equals(""))) v2MetaDataPath.getTextMetaPath();
            jtDataDirectory.setText(fileD.getAbsolutePath());
            setRadioButtonState();
        }
    }

    private boolean setHL7VersionComboBox(MetaDataLoader loader)
    {
        jcHL7Version.removeAllItems();
        if (loader == null) return true;
        String[] listVersionDir = loader.getVersions();
        for (String dir:listVersionDir) jcHL7Version.addItem(dir);
        //jcHL7Version = new JComboBox(listVersionDir.toArray());
        for(int i=0;i<jcHL7Version.getItemCount();i++)
        {
            String ver = (String) jcHL7Version.getItemAt(i);
            if (ver.indexOf("2.4") >= 0) jcHL7Version.setSelectedIndex(i);
        }

        return true;
    }
//    private boolean setHL7VersionComboBox(String pathValue)
//    {
//        File fileD = new File(pathValue);
//        if ((!fileD.exists())||(!fileD.isDirectory())) return false;
//        pathValue = fileD.getAbsolutePath();
//
//        pathValue = pathValue.trim();
//        if (setV2DataPath(pathValue).equals("OK")) {} //jtDataDirectory.setText(v2MetaDataPath);
//        else
//        {
//            JOptionPane.showMessageDialog(this, "This is NOT a V2 Meta data directory : " + fileD.getAbsolutePath(), "Meta data directory finding error!",JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//
//        jcHL7Version.setEnabled(true);
//        jcHL7Version.removeAllItems();
//
//        String fileName = v2MetaDataPath.getPath();
//        File aFile = new File(fileName);
//        java.util.List<String> listVersionDir = new ArrayList<String>();
//        if (aFile.isDirectory())
//        {
//            File[] files = aFile.listFiles();
//
//
//            for (int i=0;i<files.length;i++)
//            {
//                File file = files[i];
//
//                if (!file.isDirectory()) continue;
//                String dirName = file.getName();
//                //System.out.println("VersionsXX = > " + dirName);// + " : " + dirName.substring(versionDirTag.length()));
//
//                if (dirName.toLowerCase().startsWith(versionDirTag.toLowerCase()))
//                {
//                    listVersionDir.add(dirName.substring(versionDirTag.length()));
//                    //System.out.println("Versions = > " + dirName + " : " + dirName.substring(versionDirTag.length()));
//                }
//            }
//        }
//        else
//        {
//            if (!fileName.toLowerCase().endsWith(".zip"))
//            {
//                JOptionPane.showMessageDialog(this, "Unidentified meta directory or file : " + fileName, "Meta data directory finding error!",JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
//            java.util.List<String> keys = v2MetaDataPath.getMetaKeys();
//            for(String key:keys)
//            {
//                int idx = key.toLowerCase().indexOf("version");
//                if (idx < 0) continue;
//                key = key.substring(idx);
//                idx = key.indexOf("/");
//                if (idx < 0) continue;
//                String vers = key.substring(0, idx);
//                vers = vers.trim();
//                boolean cTag = false;
//                for (String dir:listVersionDir)
//                {
//                    dir = dir.trim();
//                    if (dir.equalsIgnoreCase(vers)) cTag = true;
//                }
//                if (!cTag) listVersionDir.add(vers);
//            }
//        }
//        //countV2Versions = listVersionDir.size();
//        for (String dir:listVersionDir) jcHL7Version.addItem(dir);
//        //jcHL7Version = new JComboBox(listVersionDir.toArray());
//        for(int i=0;i<jcHL7Version.getItemCount();i++)
//        {
//            String ver = (String) jcHL7Version.getItemAt(i);
//            if (ver.indexOf("2.4") >= 0) jcHL7Version.setSelectedIndex(i);
//        }
//
//        return true;
//    }

    private void doPressHL7MessageFileBrowse()
    {
        File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
					"*.*", Config.OPEN_DIALOG_TITLE_FOR_HL7_V3_MESSAGE_FILE.replace("3", "2"), false, false);
        if (file == null) return;
        String pathValue = file.getAbsolutePath();

        if (organizeInputFile(pathValue))
        {
            jtInputFile.setText(pathValue);
            setRadioButtonState();
        }
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
                    mp = new gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel(converter.getSCSFileName(), " ", null);
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

    private void doPressViewErrorMessages()
    {
        //System.out.println("VVVV CCCCCCCCC1");

        ValidationMessageDialog frm = null;

        if (frame != null)
            frm = new ValidationMessageDialog(frame, "V2 message parsing error list.", false);

        if (dialog != null)
            frm = new ValidationMessageDialog(dialog, "V2 message parsing error list.", false);

        ValidatorResults validatorResults = new ValidatorResults();
        //System.out.println("VVVV CCCCCCCCC2");
        for(String msg:v2Tree.getErrorMessageList())
        {
            if (msg.startsWith("FAT")) validatorResults = GeneralUtilities.addValidatorMessageFatal(validatorResults, msg);
            else if (msg.startsWith("ERR")) validatorResults = GeneralUtilities.addValidatorMessage(validatorResults, msg);
            else if (msg.startsWith("WAN")) validatorResults = GeneralUtilities.addValidatorMessageWarning(validatorResults, msg);
            else if (msg.startsWith("OBS")) validatorResults = GeneralUtilities.addValidatorMessageInfo(validatorResults, msg);
            else validatorResults = GeneralUtilities.addValidatorMessageInfo(validatorResults, msg);
        }

        frm.setValidatorResults(validatorResults);

        frm.setVisible(true);


    }

    private void doPressHL7MessageTypeConfirm()
    {
        jbViewErrorMessages.setEnabled(false);
        String type = jtInputMessageType.getText();
        if (type == null) type = "";
        type = type.trim();
        if (!type.equals(""))
        {
            if ((type.length() != 7)||(!type.substring(3, 4).equals("^")))
            {
                JOptionPane.showMessageDialog(this, "Invalid Message Type : " + type, "Invalid Message Type", JOptionPane.ERROR_MESSAGE);
                //jtInputMessageType.setFocusable(true);
                return;
            }
        }
        String version = jcHL7Version.getSelectedItem().toString();
        if (version == null) version = "";
        version = version.trim();
        if (version.length() < 3)
        {
            JOptionPane.showMessageDialog(this, "Invalid HL7 Version : " + version, "Invalid HL7 Version", JOptionPane.ERROR_MESSAGE);
            //jtInputMessageType.setFocusable(true);
            return;
        }

        try
        {
            parseV2Message();
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "HL7 Message Parsing Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void parseV2Message() throws HL7MessageTreeException
    {
        wasSuccessfullyParsed = false;
        boolean strict = jrStrictValidationYes.isSelected();
        String inputF = jtInputFile.getText();
        if (inputF == null) inputF = "";
        inputF = inputF.trim();
        String mType = jtInputMessageType.getText();
        if (mType == null) mType = "";
        mType = mType.trim();
        String versionS = ((String)jcHL7Version.getSelectedItem()).trim();
        msgTypeOnly = false;


        try
        {
            if (strict)
            {
                if (!inputF.equals(""))
                {
                    v2Tree = new HL7V2MessageTree(v2MetaDataPath);
                    v2Tree.setVersion(versionS);
                    if (oneMessageFile != null) v2Tree.parse(oneMessageFile.getAbsolutePath());
                    else v2Tree.parse(inputF);
                    if ((inputF.length() == 7)||(inputF.substring(3, 4).equals("^"))) msgTypeOnly = true;
                }
                else
                {
                    v2Tree = new HL7V2MessageTree(v2MetaDataPath);
                    v2Tree.setVersion(versionS);
                    v2Tree.parse(mType);
                    msgTypeOnly = true;
                }
            }
            else
            {

                v2Tree = new HL7V2MessageTree(v2MetaDataPath);
                v2Tree.setVersion(versionS);
                v2Tree.setFlagDataValidation(false);
                if (!mType.equals(""))
                {
                    if ((mType.length() != 7)||(!mType.substring(3, 4).equals("^")))
                    {
                        throw new HL7MessageTreeException("Invalid Message Type : " + mType);
                    }
                    v2Tree.makeTreeHead(mType.substring(0, 3), mType.substring(4));
                }
                if (oneMessageFile != null) v2Tree.parse(oneMessageFile.getAbsolutePath());
                else v2Tree.parse(inputF);

            }

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

        if (v2Tree.getErrorMessageList().size() > 0)
        {
            errorMessages.add("###=== v2 Main Parsing Error Messages : " + jtInputFile.getText());
            for (String ss:v2Tree.getErrorMessageList()) errorMessages.add(ss);
            String errMsg = v2Tree.getErrorMessageList().get(0);
            if ((errMsg.startsWith("FAT"))||(errMsg.startsWith("ERR")))
            {
                jbViewErrorMessages.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Some errors were found. For view these messages, Press 'View Error Messages' button.", "Finding Errors", JOptionPane.INFORMATION_MESSAGE);
            }
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

        if (msgTypeOnly)
        {
            jtInputFile.setText("");
            listOBXDataType = new ArrayList<String>();
            //listOBXDataType2 = new ArrayList<String>();
        }
        else
        {
            //jtInputMessageType.setText("");
            if (foundOBXSegment) listOBXDataType = searchOBXDataTypes();
        }

        if (listOBXDataType.size() > 0) wasOBXUsed = true;
        else wasOBXUsed = false;


        setRadioButtonState();
    }

    private boolean organizeInputFile(String pathValue)
    {
        String inputF = pathValue;
        if (!inputF.equals(""))
        {
            boolean isMessageStarted = false;
            boolean wasFirstMessageEnded = false;
            hasMultiMessages = false;
            oneMessage = "";
            oneMessageFile = null;
            segmentsOBX = "";
            java.util.List<String> listOBXDataType2 = new ArrayList<String>();
            try
            {
                FileReader fr = new FileReader(inputF);
                BufferedReader br = new BufferedReader(fr);
                while(true)
                {
                    String line = br.readLine();
                    if (line == null) break;
                    line = line.trim();
                    if (line.equals("")) continue;
                    if (!isMessageStarted)
                    {
                        if (line.startsWith("MSH")) isMessageStarted = true;
                        else
                        {
                            JOptionPane.showMessageDialog(this, "This file does not start with 'MSH' segment. : " + inputF, "Not a v2 Messahe File.", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        oneMessage = oneMessage + line + "\r";
                        continue;
                    }
                    if ((isMessageStarted)&&(line.startsWith("MSH")))
                    {
                        wasFirstMessageEnded = true;
                        hasMultiMessages = true;
                    }
                    if (line.startsWith("OBX"))
                    {
                        String sr = "";
                        String line2 = line;
                        int idx = line2.indexOf("|");
                        if (idx < 0) continue;
                        line2 = line2.substring(idx+1);
                        idx = line2.indexOf("|");
                        if (idx < 0) continue;
                        line2 = line2.substring(idx+1);
                        idx = line2.indexOf("|");
                        if (idx < 0) continue;
                        sr = line2.substring(0,idx).trim();

                        if (sr.equals("")) continue;
                        boolean isThereSameDataType = false;
                        for (String cx:listOBXDataType2) if (cx.equals(sr)) isThereSameDataType = true;
                        if (!isThereSameDataType)
                        {
                            segmentsOBX = segmentsOBX + line + "\r";
                            listOBXDataType2.add(sr);
                        }
                    }
                    if (!wasFirstMessageEnded)
                    {
                        if (line.startsWith("OBX"))
                        {
                            if (listOBXDataType2.size() < 1) oneMessage = oneMessage + "OBX|" + "\r";
                        }
                        else oneMessage = oneMessage + line + "\r";
                    }
                }
                if (hasMultiMessages)
                {
                    if (!segmentsOBX.equals(""))
                    {
                        StringTokenizer st = new StringTokenizer(oneMessage, "\r");
                        String str = "";
                        boolean wasOBXFound = false;
                        while(st.hasMoreTokens())
                        {
                            String stt = st.nextToken().trim();
                            if (stt.equals("")) continue;
                            if ((stt.startsWith("OBX"))&&(!wasOBXFound))
                            {
                                wasOBXFound = true;
                                str = str + segmentsOBX;
                            }
                            else str = str + stt + "\r";
                        }
                        if (!wasOBXFound) str = str + segmentsOBX;
                        oneMessage = str;
                    }
                    oneMessageFile = new File(FileUtil.saveStringIntoTemporaryFile(oneMessage));
                }
                fr.close();
                br.close();
                //System.out.println("New inputF file : " + inputF);
            }
            catch(IOException ie)
            {
                JOptionPane.showMessageDialog(this, "Error on message file reorganizing(" + inputF + ") : " + ie.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
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
            if (!((node.getLevel().equals(v2Tree.getLevelField()))&&(node.getSequence() == 2))) continue;
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
        String dataPath = "";
        MetaDataLoader loader = null;
        String message = null;
        if (arg.length == 0)
        {
            loader = FileUtil.getV2ResourceMetaDataLoader();
            message = "No v2 Resource zip file.";
        }
        else
        {
            loader = FileUtil.getV2ResourceMetaDataLoader(arg[0]);
            message = "Invalid v2 meta resource directory or zip file : " + arg[0];
        }

        if (loader == null)
        {
            System.out.println(message);
            return;
        }

        try
        {
            V2ConverterToSCSPanel panel = new V2ConverterToSCSPanel(loader);
            JFrame frame = panel.setFrame(new JFrame("V2 Converter"));

            frame.setSize(panel.getMinimumSize());
            frame.setVisible(true);
        }
        catch(IOException he)
        {
            System.out.println("Error : " + he.getMessage());
        }
//        JFrame frame = (new V2ConverterToSCSPanel()).setFrame(new JFrame("V2 Converter"));
//
//            frame.setSize(500, 715);
//            frame.setVisible(true);
    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.11  2008/06/09 19:54:05  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2008/05/30 04:50:08  umkis
 * HISTORY      : frame size adjusted
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/05/30 04:43:32  umkis
 * HISTORY      : frame size adjusted
 * HISTORY      :
 * HISTORY      : Revision 1.8  2008/05/30 04:05:39  umkis
 * HISTORY      : frame size adjusted
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/05/30 02:00:29  umkis
 * HISTORY      : frame size adjusted
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/05/30 00:59:29  umkis
 * HISTORY      : update: v2 resource zip file can be accessed not only meta directory.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/05/29 01:25:20  umkis
 * HISTORY      : update: v2 resource zip file can be accessed not only meta directory
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/01/31 22:48:17  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/01/31 22:39:39  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/01/31 21:39:49  umkis
 * HISTORY      : csv converting from multi message included v2 file.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/12/05 20:01:56  umkis
 * HISTORY      : minor modifying
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/10 03:56:02  umkis
 * HISTORY      : V2-V3 mapping job version 1.0
 * HISTORY      :
 */
