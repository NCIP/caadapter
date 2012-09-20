package gov.nih.nci.caadapter.dvts.ui;

import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.DefaultSettings;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.Log;
import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 13, 2011
 * Time: 4:49:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionVocabularyMappingDemo extends JFrame implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyMappingDemo.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionVocabularyMappingDemo.java,v 1.4 2008-11-21 16:18:38 wangeug Exp $";


    private static final String TITLE = "Function Vocabulary Mapping Definition";


    private FunctionVocabularyMapping functionVocabularyMapping = new FunctionVocabularyMapping();


    private JRadioButton fileLocal;
    private JRadioButton fileURL;
    private JRadioButton serviceURL;
    private JLabel domainLabel;
    private JComboBox domainField;
    private JLabel serviceLabel;
    private JTextField serviceField;
    private JLabel valueLabel;
    private JLabel locationVOMFile;
    private JLabel locationVOMURL;
    private JTextField valueField;

    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String SEARCH_COMMAND = "Browse..";
    private static final String CHECK_COMMAND = "Domain Check";
    private static final String URL_CHECK_COMMAND = "URL Check";

    private JButton okButton;
    private JButton searchButton;
    private JButton checkButton;
    private JButton checkServiceButton;
    private JButton cancelButton;

    private JCheckBox inverse;
    private JTextField inputField;
    private JTextField outputField;


    private boolean okButtonClicked;

    private String typeField;
    private String mappingTypeClass;
    private String mappingValue;
    //private String selectedItem;
    private boolean inverseTag;
    //private boolean foundDomain;
    //private boolean isFileSearched;
    private boolean okButtonEnabled;
    private JFrame frame;

    public FunctionVocabularyMappingDemo()//Frame owner, boolean inverse) throws HeadlessException
    {
        //super(owner, TITLE, true);
        super(TITLE);
        //inverseTag = inverse;
        initialize();
    }


    private void initialize()
    {
        try
        {
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
            catch (ClassNotFoundException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (InstantiationException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (IllegalAccessException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (UnsupportedLookAndFeelException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        catch (Throwable t)
        {
            Log.logException(new Object(), t);
        }

        Object[] ob = inputFileNameCommon("", valueField, searchButton, "   " + SEARCH_COMMAND + "   ", SEARCH_COMMAND);

        JPanel locationVOMPanel = new JPanel(new BorderLayout());
        valueField = (JTextField) ob[2];
        searchButton = (JButton) ob[3];

        JPanel locationVOMLabelPanel = new JPanel(new BorderLayout());
        valueLabel = new JLabel("   VOM File Location", JLabel.LEFT);
        locationVOMFile = new JLabel(" (File) ", JLabel.LEFT);
        locationVOMURL = new JLabel(" (URL) ", JLabel.LEFT);

        JPanel aPanel = new JPanel(new BorderLayout());
        aPanel.add(locationVOMURL, BorderLayout.WEST);
        JPanel bPanel = new JPanel(new BorderLayout());
        bPanel.add(locationVOMFile, BorderLayout.WEST);
        bPanel.add(aPanel, BorderLayout.CENTER);
        locationVOMLabelPanel.add(bPanel, BorderLayout.CENTER);
        locationVOMLabelPanel.add(valueLabel, BorderLayout.WEST);
        locationVOMPanel.add((JPanel) ob[0], BorderLayout.CENTER);
        locationVOMPanel.add(locationVOMLabelPanel, BorderLayout.WEST);

        JPanel domainPanel = new JPanel(new BorderLayout());
        JPanel cPanel = new JPanel(new BorderLayout());

        domainLabel = new JLabel("                                                  Select Domain ");//, JLabel.LEFT);
        domainField = new JComboBox();
        checkButton = new JButton("Get Domain");
        checkButton.setActionCommand(CHECK_COMMAND);
        checkButton.addActionListener(this);

        cPanel.add(domainField, BorderLayout.CENTER);
        cPanel.add(domainLabel, BorderLayout.WEST);
        cPanel.add(checkButton, BorderLayout.EAST);
        domainField.setEnabled(false);
        domainLabel.setEnabled(false);
        checkButton.setEnabled(false);
        //cPanel.add(new JLabel("        "), BorderLayout.WEST);
        domainPanel.add(cPanel, BorderLayout.NORTH);
        //locationVOMPanel.add(domainPanel, BorderLayout.SOUTH);

        JPanel serviceURLPanel = new JPanel(new BorderLayout());
        JPanel dPanel = new JPanel(new BorderLayout());

        serviceLabel = new JLabel("   Vocabulaty Mapping Service URL", JLabel.LEFT);

        ob = inputFileNameCommon("    ", serviceField, checkServiceButton, " Check URL", URL_CHECK_COMMAND);

        serviceField = (JTextField) ob[2];
        checkServiceButton = (JButton) ob[3];


        dPanel.add((JPanel) ob[0], BorderLayout.NORTH);
        serviceURLPanel.add(dPanel, BorderLayout.CENTER);
        serviceURLPanel.add(serviceLabel, BorderLayout.NORTH);

        ob = setupRadioButtonPanel(fileLocal, "VOM File (Local)", "Local_VOM", true,
                                           fileURL, "VOM File (URL)", "URL_VOM", false,
                                           serviceURL, "Service URL", "URL", false, false);
        JPanel radioButtonPanel = wrappingBorder("Select Type of Domain Value Mapping Resource", (JPanel) ob[0]);
        fileLocal = (JRadioButton) ob[1];
        fileURL = (JRadioButton) ob[2];
        serviceURL = (JRadioButton) ob[3];

        okButton = new JButton("    " + OK_COMMAND + "   ");
        okButton.setActionCommand(OK_COMMAND);
        okButton.addActionListener(this);

        cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel centerPanel = new JPanel(new BorderLayout());


        centerPanel.add(locationVOMPanel, BorderLayout.NORTH);


        JPanel centerPanel1 = new JPanel(new BorderLayout());

        centerPanel1.add(domainPanel, BorderLayout.NORTH);
        centerPanel1.add(serviceURLPanel, BorderLayout.CENTER);
        centerPanel.add(centerPanel1, BorderLayout.CENTER);

        JPanel southWestPanel = new JPanel(new BorderLayout());
        southWestPanel.add(new JLabel("         Source value : "), BorderLayout.WEST);
        inputField = new JTextField();
        southWestPanel.add(inputField, BorderLayout.CENTER);
        inputField.setEditable(true);
        inputField.setEnabled(true);

        JPanel southEastPanel = new JPanel(new BorderLayout());
        southEastPanel.add(new JLabel("         Target value : "), BorderLayout.WEST);
        outputField = new JTextField();
        southEastPanel.add(outputField, BorderLayout.CENTER);
        outputField.setEditable(false);
        outputField.setEnabled(true);

        JPanel southSouthPanel = new JPanel(new BorderLayout());
        southSouthPanel.add(southWestPanel, BorderLayout.CENTER);
        southSouthPanel.add(southEastPanel, BorderLayout.SOUTH);

        southSouthPanel = wrappingBorder("Vocabulary Demo Data and Result", southSouthPanel);


        JPanel southMainPanel = new JPanel(new BorderLayout());
        southMainPanel.add(southSouthPanel, BorderLayout.SOUTH);


        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(southMainPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(radioButtonPanel, BorderLayout.NORTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(new JLabel(" "), BorderLayout.WEST);
        getContentPane().add(new JLabel(" "), BorderLayout.EAST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(new JPanel(), BorderLayout.NORTH);
        getContentPane().add(new JPanel(), BorderLayout.SOUTH);
        serviceField.setText("");
        valueField.setText("");
        domainField.removeAllItems();
        //foundDomain = false;
        //isFileSearched = false;
        okButtonEnabled = false;
        setupComponentState();
        this.setSize(600, 300);

        if (mappingValue == null) mappingValue = "";

        typeField = functionVocabularyMapping.getTypeNamePossibleList()[0];
        if (inverseTag) serviceURL.setVisible(false);

        //else typeField = new JComboBox(functionVocabularyMapping.getTypeNamePossibleList());
        domainField.addItemListener(
                new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {
                        if (domainField.getSelectedIndex() > 0)
                        {
                            okButtonEnabled = true;
                            setupComponentState();
                        }
                    }
                }
        );
        valueField.addKeyListener(
                new KeyListener()
                {
                    public void keyTyped(KeyEvent e) {}

                    public void keyPressed(KeyEvent e)
                    {
                        //System.out.println("CCC : " + e.getKeyChar() + " : " + e.getKeyCode());
                        if (!checkButton.isEnabled())
                        {
                            String st = "";
                            int idx = mappingValue.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
                            if (idx > 0) st = mappingValue.substring(0, idx);
                            else st = mappingValue;
                            if (!st.trim().equals(valueField.getText().trim())) checkButton.setEnabled(true);
                        }
                    }
                    public void keyReleased(KeyEvent e) {}
                }
        );

        frame = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    frame.dispose();
                }
            }
        );

//        serviceURL.setVisible(false);
//        serviceLabel.setVisible(false);
//        checkServiceButton.setVisible(false);
//        serviceField.setVisible(false);
        this.setVisible(true);
        //this.addWindowListener(new CaWindowClosingListener());
        DefaultSettings.centerWindow(this);
    }

    private void setupComponentState()
    {
        valueField.setEditable(fileURL.isSelected());
        valueField.setEditable(!fileLocal.isSelected());
        valueField.setEnabled(!serviceURL.isSelected());

        searchButton.setEnabled(fileLocal.isSelected());
        checkButton.setEnabled(fileURL.isSelected());
        checkServiceButton.setEnabled(serviceURL.isSelected());
        inverse.setEnabled(!serviceURL.isSelected());

        valueLabel.setEnabled(!serviceURL.isSelected());
        locationVOMFile.setEnabled(valueLabel.isEnabled());
        locationVOMURL.setEnabled(valueLabel.isEnabled());
        locationVOMFile.setVisible(fileLocal.isSelected());
        locationVOMURL.setVisible(fileURL.isSelected());

        domainLabel.setEnabled(domainField.getItemCount() > 1);
        //if (!foundDomain) domainField.removeAllItems();
        domainField.setEnabled(domainField.getItemCount() > 1);

        if (fileURL.isSelected())
        {
            String st = "";
            if (mappingValue == null) mappingValue = "";
            int idx = mappingValue.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
            if (idx > 0) st = mappingValue.substring(0, idx);
            else st = mappingValue;
            if (st.trim().equals(valueField.getText().trim())) checkButton.setEnabled(false);
            else checkButton.setEnabled(true);
        }


        serviceField.setEnabled(serviceURL.isSelected());
        serviceLabel.setEnabled(serviceURL.isSelected());
        checkServiceButton.setEnabled(serviceURL.isSelected());

        okButton.setEnabled(okButtonEnabled);
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
    private JPanel wrappingBorder(String borderTitle, JPanel aPanel)
    {
        JPanel titledBorders = new JPanel(new BorderLayout());

        TitledBorder titled = BorderFactory.createTitledBorder(borderTitle);
        titledBorders.add(aPanel, BorderLayout.CENTER);
        titledBorders.setBorder(titled);

        return titledBorders;
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


        inverse = new JCheckBox();
        JPanel checkPanel = new JPanel(new BorderLayout());
        checkPanel.add(new JLabel("Inverse Map "), BorderLayout.WEST);
        checkPanel.add(inverse, BorderLayout.CENTER);

        aPanel.add(checkPanel, BorderLayout.EAST);

        bPanel.add(aPanel, BorderLayout.NORTH);

        Object[] out = new Object[4];
        out[0] = bPanel;
        out[1] = radioButton01;
        out[2] = radioButton02;
        if (buttonLabel03 != null) out[3] = radioButton03;
        return out;
    }

    /*
    private Dimension computeFieldSize(boolean zoom)
    {
        Dimension typeLabelSize = typeLabel.getPreferredSize();
        Dimension valueLabelSize = valueField.getPreferredSize();
        int INTERVAL = (zoom) ? 4 : 2;
        int width = Math.max(typeLabelSize.width, valueLabelSize.width) + INTERVAL * 2;
        int height = Math.max(typeLabelSize.height, valueLabelSize.height) + INTERVAL;
        return new Dimension(width, height);
    }
    */

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        inverseTag = inverse.isSelected();

        String command = e.getActionCommand();
        if (e.getSource() instanceof JRadioButton)
        {
            if (e.getSource() == fileLocal)  typeField = functionVocabularyMapping.getTypeNamePossibleList()[0];
            if (e.getSource() == fileURL)    typeField = functionVocabularyMapping.getTypeNamePossibleList()[2];
            if (e.getSource() == serviceURL) typeField = functionVocabularyMapping.getTypeNamePossibleList()[1];

            serviceField.setText("");
            valueField.setText("");
            domainField.removeAllItems();
            serviceField.setEditable(true);
            okButtonEnabled = false;
        }

        if (SEARCH_COMMAND.equals(command))
        {
            doPressSearchButton();
        }
        if (CHECK_COMMAND.equals(command))
        {
            doPressCheckButton();
        }
        if (URL_CHECK_COMMAND.equals(command))
        {
            if (okButtonEnabled)
            {
                serviceField.setEditable(true);
                okButtonEnabled = false;
            }
            else doPressURLCheckButton();
        }


        if(OK_COMMAND.equals(command))
        {

            String inputValue = "";
            if ((typeField.equals(functionVocabularyMapping.getTypeNamePossibleList()[0]))||
                (typeField.equals(functionVocabularyMapping.getTypeNamePossibleList()[2])))
            {
                inputValue = valueField.getText();
            }
            else
            {
                inputValue = serviceField.getText();
            }
            if((inputValue==null)||(inputValue.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "No Input value.", "Missing Value", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sourceValue = inputField.getText();
            if((sourceValue==null)||(sourceValue.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "No Source value.", "Missing Value", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sourceValue = sourceValue.trim();

            String userSelect = typeField;
            if ((userSelect.equals(functionVocabularyMapping.getTypeNamePossibleList()[0]))||
                (userSelect.equals(functionVocabularyMapping.getTypeNamePossibleList()[2])))
            {
                if ((domainField.getItemCount() > 0)&&(domainField.getSelectedIndex() == 0))
                {
                    JOptionPane.showMessageDialog(this, "You did'n select any domain name yet.", "No selected domain", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (domainField.getItemCount() > 0)
                        mappingValue = valueField.getText() + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR + domainField.getSelectedItem();
                else
                {
                    mappingValue = valueField.getText();
                    if (mappingValue == null) mappingValue = "";
                }
            }
            else
            {

            }
            mappingTypeClass = userSelect;
            try
            {
                //System.out.println("" + );
                FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(getMappingTypeClass(), getMappingValue(), inverseTag);
                if (inverseTag) outputField.setText(fvm.translateInverseValue(sourceValue));
                else outputField.setText(fvm.translateValue(sourceValue));
            }
            catch(FunctionException fe)
            {
                JOptionPane.showMessageDialog(this, fe.getMessage(), "Vocabulary Mapping Failure", JOptionPane.ERROR_MESSAGE);
                return;
            }



//            okButtonClicked = true;
//            setVisible(false);
//            dispose();
        }
        if(CANCEL_COMMAND.equals(command))
        {
            okButtonClicked = false;
            setVisible(false);
            dispose();
        }

        setupComponentState();
    }

    private void doPressSearchButton()
    {
        /*
        JFileChooser chooser = new JFileChooser(new File(FileUtil.getWorkingDirPath()));
        chooser.setFileFilter(new SingleFileFilter(Config.VOCABULARY_MAPPING_FILE_EXTENSION));

        int returnVal = chooser.showOpenDialog(this);
        String pathValue = "";

        if(returnVal == JFileChooser.APPROVE_OPTION) return;

        pathValue = chooser.getSelectedFile().getAbsolutePath();
        */

        String st = valueField.getText();
        if (st.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) st = st.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        File file1 = null;
        File file2 = new File(st);
        if ((!st.trim().equals(""))&&(file2.exists())&&(file2.isFile()))
        {
            file1 = DefaultSettings.getUserInputOfFileFromGUI(this, file2.getParentFile().getAbsolutePath(),
                     Config.VOCABULARY_MAPPING_FILE_EXTENSION, "Open " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File..", false, false);
        }
        else file1 = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
                     Config.VOCABULARY_MAPPING_FILE_EXTENSION, "Open " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File..", false, false);
        if (file1 == null) return;
        String pathValue = file1.getAbsolutePath();

        if (setDomainField(pathValue)) valueField.setText(pathValue);
        else return;
        okButtonEnabled = true;

    }
    private boolean setDomainField(String pathValue)
    {
        java.util.List<String> listDomain = null;
        try
        {   String selectS = typeField;

            if (selectS.equals(functionVocabularyMapping.getTypeNamePossibleList()[0]))
                    listDomain = functionVocabularyMapping.checkMappingFileAndGatheringDomainName(pathValue);
            else if (selectS.equals(functionVocabularyMapping.getTypeNamePossibleList()[2]))
                    listDomain = functionVocabularyMapping.getDomains(pathValue);

            //listDomain = functionVocabularyMapping.checkMappingFileAndGatheringDomainName(pathValue);
            //functionVocabularyMapping.setType(functionVocabularyMapping.getTypeNamePossibleList()[0]);
            functionVocabularyMapping.setType(selectS);
        }
        catch(FunctionException fe)
        {
            JOptionPane.showMessageDialog(this, fe.getMessage(), "FunctionException",JOptionPane.WARNING_MESSAGE);
            return false;
        }

        domainField.removeAllItems();
        if (listDomain.size() > 0)
        {
            domainField.setEnabled(true);
            domainField.addItem("Please, Select Domain.");
            for (int i=0;i<listDomain.size();i++) domainField.addItem(listDomain.get(i));
            domainField.setSelectedIndex(0);
        }
        return true;
    }

    private void doPressCheckButton()
    {
        String val = valueField.getText();
        if ((val == null)||(val.trim().equals("")))
        {
            JOptionPane.showMessageDialog(this, "Text field is empty.", "Empty Text Field",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!setDomainField(val))
        {
            String st = "";
            int idx = mappingValue.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
            if (idx > 0) st = mappingValue.substring(0, idx);
            else st = mappingValue;
            valueField.setText(st);
            return;
        }

        okButtonEnabled = true;
    }
    private void doPressURLCheckButton()
    {
        String serviceAddr = serviceField.getText();

        try
        {
            FunctionVocabularyMapping sample = new FunctionVocabularyMapping();
            sample.setType(sample.getTypeNamePossibleList()[1]);
            sample.setValue(serviceAddr);
            String sampleConnectData = extractSampleConnectData(serviceAddr);
            if (sampleConnectData.equals("")) sample.searchMappingURL(Config.VOCABULARY_MAP_URL_CONNECTION_TEST_DATA);
            else sample.searchMappingURL(sampleConnectData);
        }
        catch(FunctionException fe)
        {
            JOptionPane.showMessageDialog(this, "Invalid Vocabulary Mapping Service URL. Check again. \nOr it'd be better to change connecting test data such as '#ABC#'.: \n" + fe.getMessage()+"\n"+valueField.getText(), "Invalid URL", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mappingValue = serviceAddr;
        if (mappingValue == null) mappingValue = "";
        JOptionPane.showMessageDialog(this, "Good! This service URL is valid. ", "Valid URL", JOptionPane.INFORMATION_MESSAGE);

        serviceField.setEditable(false);
        okButtonEnabled = true;
    }
    public boolean isOkButtonClicked()
    {
        return okButtonClicked;
    }

    public String getMappingTypeClass()
    {
        return mappingTypeClass;
    }

    public String getMappingValue()
    {
        if (mappingValue == null) mappingValue = "";
        return mappingValue;
    }

    public boolean setMappingTypeClass(String mapTypeClass)
    {
        int idx = -1;
        for (int i=0;i<functionVocabularyMapping.getTypeNamePossibleList().length;i++)
        {
            if (mapTypeClass.equals(functionVocabularyMapping.getTypeNamePossibleList()[i])) idx = i;
        }

        if (idx < 0) return false;
        mappingTypeClass = mapTypeClass;
        typeField = functionVocabularyMapping.getTypeNamePossibleList()[idx];
        if (idx == 0) fileLocal.setSelected(true);
        else if (idx == 1) serviceURL.setSelected(true);
        else if (idx == 2) fileURL.setSelected(true);

        //String type = (String)typeField.getSelectedItem();
        //selectedItem = type;
        setupComponentState();
        //doItemChanged(type);
        return true;
    }
    /*
    private void doItemChanged(String type)
    {
        domainField.removeAll();
        domainField.setEnabled(false);

        if (type.equals(functionVocabularyMapping.getTypeNamePossibleList()[0]))
        {
            checkButton.setEnabled(false);
            searchButton.setEnabled(true);
            valueField.setEditable(false);
            okButton.setEnabled(true);
        }
        else if (type.equals(functionVocabularyMapping.getTypeNamePossibleList()[2]))
        {
            checkButton.setEnabled(true);
            searchButton.setEnabled(false);
            valueField.setEditable(true);
            okButton.setEnabled(false);
        }
        else if (type.equals(functionVocabularyMapping.getTypeNamePossibleList()[1]))
        {
            checkButton.setEnabled(false);
            searchButton.setEnabled(false);
            valueField.setEditable(true);
            okButton.setEnabled(true);
        }
    }
    */
    public void setMappingValue(String mapValue)
    {
        //mappingValue = mapValue;
        String domain = "";
        String fileName = "";
        if (mapValue.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

        if (mapValue.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) > 0)
        {
            int idx = mapValue.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
            fileName = mapValue.substring(0, idx);
            domain = mapValue.substring(idx + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
        }
        else fileName = mapValue;
        java.util.List<String> listDomain = null;
        if (!domain.equals(""))
        {
            if(mappingTypeClass.equals(functionVocabularyMapping.getTypeNamePossibleList()[1]))
            {
                JOptionPane.showMessageDialog(this, "This is unfit type.", "FunctionException",JOptionPane.WARNING_MESSAGE);
                return;
            }
            try
            {
                if (mappingTypeClass.equals(functionVocabularyMapping.getTypeNamePossibleList()[0]))
                    listDomain = functionVocabularyMapping.checkMappingFileAndGatheringDomainName(fileName);
                else if (mappingTypeClass.equals(functionVocabularyMapping.getTypeNamePossibleList()[2]))
                    listDomain = functionVocabularyMapping.getDomains(fileName);
                //functionVocabularyMapping.setType(functionVocabularyMapping.getTypeNamePossibleList()[0]);
                functionVocabularyMapping.setType(mappingTypeClass);
            }
            catch(FunctionException fe)
            {
                JOptionPane.showMessageDialog(this, fe.getMessage(), "FunctionException",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (listDomain.size() == 0)
            {
                JOptionPane.showMessageDialog(this, "This file has no any Domain.", "No domain",JOptionPane.WARNING_MESSAGE);
                return;
            }
            domainField.removeAllItems();
            domainField.addItem("Please, Select Domain.");
            boolean findTag = false;
            for (int i=0;i<listDomain.size();i++)
            {
                String domainItem = listDomain.get(i);
                if (domainItem.equals(domain)) findTag = true;
                domainField.addItem(domainItem);
            }
            if (!findTag)
            {
                JOptionPane.showMessageDialog(this, "Not matched Domain name : " + domain, "Not matched Domain",JOptionPane.WARNING_MESSAGE);
                domainField.setSelectedIndex(0);
                return;
            }
            //domainField.setEnabled(true);
            //searchButton.setEnabled(true);
            domainField.setSelectedItem(domain);
            valueField.setText(fileName);
        }
        else
        {
            if(mappingTypeClass.equals(functionVocabularyMapping.getTypeNamePossibleList()[1])) serviceField.setText(mapValue);
            else valueField.setText(mapValue);
        }
        mappingValue = mapValue;
        okButtonEnabled = false;
        setupComponentState();
    }

    private String extractSampleConnectData(String val)
    {
        String sharpChar = Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER;
        int idx = val.indexOf("=" + sharpChar);
        String val2 = val.substring(idx + sharpChar.length() + 1);
        idx = val2.indexOf(sharpChar);
        if (idx < 0) return "";
        else return val2.substring(0, idx);
    }

    public static void main(String arg[])
    {
        new FunctionVocabularyMappingDemo();
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/11/17 20:10:47  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/10/17 14:50:42  umkis
 * HISTORY      : Add demo runner
 * HISTORY      :
 */

