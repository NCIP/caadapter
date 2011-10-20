package gov.nih.nci.caadapter.dvts.ui;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.common.Log;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.DefaultSettings;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 19, 2011
 * Time: 3:14:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextVOMTranslationGUI extends JFrame implements ActionListener
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


    //private JRadioButton fileLocal;
    //private JRadioButton fileURL;
    //private JRadioButton serviceURL;
    private JLabel contextLabel;
    private JComboBox contextCombo;
    private JLabel domainLabel;
    private JComboBox domainCombo;
    private JLabel inputLabel;
    private JTextField inputField;
    private JLabel outputLabel;
    private JTextField outputField;

    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";

    private JButton okButton;
    //private JButton resetButton;
    private JButton cancelButton;
    private JButton contextFileBrowseButton;
    private JButton domainCollectButton;

    private JCheckBox inverse;

    private boolean inverseTag;

    private java.util.List<String> domainList = null;

    private java.util.List<String> contextName = new ArrayList<String>();
    private java.util.List<String> contextLink = new ArrayList<String>();
    private String currentLink = null;
    private int currentItemIndex = 0;

    private JFrame frame;

    public ContextVOMTranslationGUI()//Frame owner, boolean inverse) throws HeadlessException
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





        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        contextLabel = new JLabel("   Context   ");
        centerPanel.add(contextLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contextCombo = new JComboBox();
        contextCombo.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(contextCombo, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        contextFileBrowseButton = new JButton("     Browse     ");
        centerPanel.add(contextFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        domainLabel = new JLabel("   Domain   ");
        centerPanel.add(domainLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        domainCombo = new JComboBox();
        domainCombo.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(domainCombo, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        domainCollectButton = new JButton("Check Domains");
        centerPanel.add(domainCollectButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        inputLabel = new JLabel("   Input Value   ");
        centerPanel.add(inputLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(inputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        inverse = new JCheckBox();
        JPanel checkPanel = new JPanel(new BorderLayout());
        checkPanel.add(new JLabel("Inverse "), BorderLayout.WEST);
        checkPanel.add(inverse, BorderLayout.CENTER);

        centerPanel.add(checkPanel, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        outputLabel = new JLabel("   Output Value   ");
        centerPanel.add(outputLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        outputField = new JTextField();
        outputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(outputField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton destFileBrowseButton = new JButton(new BrowseHLV3MessageAction(this, DEST_FILE_BROWSE_MODE));
        //centerPanel.add(destFileBrowseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
        //        GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        outputField.setEditable(false);
        this.add(centerPanel, BorderLayout.CENTER);

        contextFileBrowseButton.addActionListener(this);
        contextFileBrowseButton.setEnabled(false);
        domainCollectButton.addActionListener(this);
        domainCollectButton.setEnabled(false);

        domainCombo.addItem("");
        domainCombo.setEditable(true);



        java.util.List<String> contextLine = FileUtil.getContextAddresses();

        if ((contextLine == null)||(contextLine.size() == 0))
        {

            // for test
            //contextLine.add("caBIG/EVS@http://165.112.133.125:8080/caAdapterWS/stellar/ContextVOMTranslation?context=demo0");
            //contextLine.add("CTEP@http://165.112.133.125:8080/caAdapterWS/stellar/ContextVOMTranslation?context=demo1");
        }
        else
        {
            contextCombo.addItem("Select Context");
            for (String line:contextLine)
            {
                int idx = line.indexOf("@");
                if (idx < 0)
                {
                    idx = line.indexOf("=");
                    if (idx < 0) continue;
                }
                String name = line.substring(0, idx);
                String addr = line.substring(idx+1);
                contextName.add(name);
                contextCombo.addItem(name);
                contextLink.add(addr);
            }
        }


        inputField.setText("");
        outputField.setText("");


        okButton = new JButton(" Translate ");
        okButton.setActionCommand(OK_COMMAND);
        okButton.addActionListener(this);

        //resetButton = new JButton("   Reset   ");
        //resetButton.addActionListener(this);

        cancelButton = new JButton("   Close   ");
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        buttonPanel.add(okButton);
        //buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        domainCombo.setEditable(false);
        contextCombo.setEditable(false);

        //okButtonEnabled = false;

        this.setSize(600, 250);


        frame = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    frame.dispose();
                }
            }
        );

        contextCombo.addItemListener(
                new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {
                        if(contextCombo.getSelectedIndex() > 0)
                        {
                            String contextNameS = "";

                            contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
                            String link = "";
                            for (int i=0;i<contextName.size();i++)
                            {
                                String name = contextName.get(i);
                                if (name.equals(contextNameS))
                                {
                                    link = contextLink.get(i);
                                    break;
                                }
                            }
                            if (setDomainListToCombo(link, false))
                            {
                                currentLink = link;
                                currentItemIndex = contextCombo.getSelectedIndex();
                            }
                            else
                            {
                                contextCombo.setSelectedIndex(currentItemIndex);
                            }
                        }
                        else
                        {
                            if (currentItemIndex != 0)
                            {
                                contextCombo.setSelectedIndex(currentItemIndex);
                            }
                        }
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

        if (contextCombo.getItemCount() == 0)
        {
            JOptionPane.showMessageDialog(this, "Context address property file is not found", "No Context property", JOptionPane.ERROR_MESSAGE);
        }
    }





    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        inverseTag = inverse.isSelected();

        String command = e.getActionCommand();

        if (e.getSource() == contextFileBrowseButton)
        {
            String st = (String) contextCombo.getSelectedItem();

            if (st.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) st = st.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
            File file1 = null;
            File file2 = new File(st);
            if ((!st.trim().equals(""))&&(file2.exists())&&(file2.isFile()))
            {
                file1 = DefaultSettings.getUserInputOfFileFromGUI(this, file2.getParentFile().getAbsolutePath(),
                         "*", "Select " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File or Context Dir", false, false);
                         //Config.VOCABULARY_MAPPING_FILE_EXTENSION, "Open " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File..", false, false);
            }
            else file1 = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
                         "*", "Select " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File or Context Dir", false, false);
                         //Config.VOCABULARY_MAPPING_FILE_EXTENSION, "Open " + Config.VOCABULARY_MAPPING_FILE_EXTENSION + " File..", false, false);
            if (file1 == null) return;
            String pathValue = file1.getAbsolutePath();

            boolean ready = true;
            if (file1.isFile())
            {
                if (!(file1.getName().toLowerCase().endsWith(Config.VOCABULARY_MAPPING_FILE_EXTENSION)))
                {
                    JOptionPane.showMessageDialog(this, "This is not a VOM File.", "Not a VOM File",JOptionPane.ERROR_MESSAGE);
                    ready = false;
                }
            }
            if (file1.isDirectory())
            {
                int cnt = 0;
                for(File f:file1.listFiles())
                {
                    if (!f.isFile()) continue;
                    if (f.getName().toLowerCase().endsWith(Config.VOCABULARY_MAPPING_FILE_EXTENSION)) cnt++;
                }

                if (cnt == 0)
                {
                    JOptionPane.showMessageDialog(this, "This Directory have no VOM File.", "Not a VOM Directory", JOptionPane.ERROR_MESSAGE);
                    ready = false;
                }
            }

            if (ready)
            {
                setDomainListToCombo(pathValue, false);

                if (domainCombo.getItemCount() > 1)
                {
                    domainCollectButton.setEnabled(false);
                    //contextField.setText(pathValue);
                }
                else JOptionPane.showMessageDialog(this, "This Context have no VOM domain.", "No VOM Domain", JOptionPane.ERROR_MESSAGE);

            }
            //contextField.setText(pathValue);

        }
        if (e.getSource() == domainCollectButton)
        {
            setDomainListToCombo(true);
        }

//        if (e.getSource() == resetButton)
//        {
//            //contextField.setText("");
//            //contextField.setEditable(true);
//
//            domainCombo.removeAllItems();
//            domainCombo.addItem("");
//            domainCombo.setEditable(true);
//
//            inputField.setText("");
//
//            outputField.setText("");
//            inverse.setSelected(false);
//            domainCollectButton.setEnabled(true);
//            return;
//        }

        if(OK_COMMAND.equals(command))
        {
            boolean ready= true;
            String context = null;


            if ((contextCombo.getItemCount() == 0)||(contextCombo.getSelectedIndex() == 0))
            {
                JOptionPane.showMessageDialog(this, "Any Context is not selected.", "No Context", JOptionPane.ERROR_MESSAGE);
                ready = false;
            }
            else if((domainCombo.getItemCount() == 0)||(domainCombo.getSelectedIndex() == 0))
            {
                JOptionPane.showMessageDialog(this, "Any Domain is not selected.", "No Domain", JOptionPane.ERROR_MESSAGE);
                ready = false;
            }
            else
            {
                String contextNameS = "";

                contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
                String link = "";
                for (int i=0;i<contextName.size();i++)
                {
                    String name = contextName.get(i);
                    if (name.equals(contextNameS))
                    {
                        link = contextLink.get(i);
                        break;
                    }
                }
                context = link;
            }


            String domain = (String) domainCombo.getSelectedItem();
            String input = inputField.getText();
            if (context.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) context = context.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

            if (context.trim().equals(""))
            {
                JOptionPane.showMessageDialog(this, "No Context", "Context field is empty.", JOptionPane.ERROR_MESSAGE);
                ready = false;
            }
            else
            {
                if (domain.trim().equals(""))
                {
                    JOptionPane.showMessageDialog(this, "No domain", "Domain field is empty.", JOptionPane.ERROR_MESSAGE);
                    ready = false;
                }
                else
                {
                    if (input.trim().equals(""))
                    {
                        JOptionPane.showMessageDialog(this, "Input value field is empty.", "No Input Value", JOptionPane.ERROR_MESSAGE);
                        ready = false;
                    }
                }
            }

            if (ready)
            {
                try
                {
                    outputField.setText(ContextVocabularyTranslation.translate(context, domain, input, inverseTag));
                }
                catch(Exception ee)
                {
                    JOptionPane.showMessageDialog(this, ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    //ee.printStackTrace();
                }
            }
        }

        if(CANCEL_COMMAND.equals(command))
        {
            //okButtonClicked = false;
            setVisible(false);
            dispose();
        }


    }

    private boolean setDomainListToCombo(boolean message)
    {
        return setDomainListToCombo(null, message);
    }
    private boolean setDomainListToCombo(String path, boolean message)
    {
        String params = "searchdomain=true";
        if (path.indexOf("?") < 0)
        {
            path = path + "?" + params;
        }
        else
        {
            if (path.endsWith("&")) path = path + params;
            else path = path + "&" + params;
        }
        String result = "";
        try
        {
            result = ContextVocabularyTranslation.translate(path, "any", "any");
        }
        catch(Exception ee)
        {
            if (message) JOptionPane.showMessageDialog(this, ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        java.util.List<String> list = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(result, ";");
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (token.trim().equals("")) continue;
            list.add(token.trim());
        }

        domainList = list;

        if (domainList.size() == 0)
        {
            if (message)
                JOptionPane.showMessageDialog(this, "No Domain in Context", "This context doesn't have any Domain.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else
        {
            domainCombo.removeAllItems();
            domainCombo.addItem("Select a domain");
            for(String str:domainList) domainCombo.addItem(str);
        }
        return true;
    }


    public static void main(String arg[])
    {
        new ContextVOMTranslationGUI();
    }
}


