package gov.nih.nci.caadapter.dvts.ui;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.common.Log;
import gov.nih.nci.caadapter.dvts.common.util.FileSearchUtil;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.DefaultSettings;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.applet.Applet;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 19, 2011
 * Time: 3:14:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextVOMTranslationGUI extends JPanel implements ActionListener
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


    private static final String TITLE = "Context VOM TransLation Demo";


    //private FunctionVocabularyMapping functionVocabularyMapping = new FunctionVocabularyMapping();


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
    private JButton contextVOMBrowseButton;
    private JButton domainCollectButton;

    private JCheckBox inverse;

    private boolean inverseTag;

    private java.util.List<String> domainList = null;

    private java.util.List<String> contextName = new ArrayList<String>();
    private java.util.List<String> contextLink = new ArrayList<String>();
    private String currentLink = "";
    private int currentItemIndex = 0;

    private Dimension dim = new Dimension(600, 250);

    private Frame frame = null;
    private Dialog dialog = null;

    private String contextAddressPropertyFile = null;
    private java.util.List<String> contextAddressList = null;
    //private Applet applet;

    public ContextVOMTranslationGUI(Frame fr)
    {
        frame = fr;
        if (frame != null) frame.setTitle(TITLE);
        initialize();
    }
    public ContextVOMTranslationGUI(Dialog di)
    {
        dialog = di;
        if (dialog != null) dialog.setTitle(TITLE);
        initialize();
    }
    public ContextVOMTranslationGUI()
    {
        initialize();
    }

    public ContextVOMTranslationGUI(Frame fr, String addrFile) throws Exception
    {
        frame = fr;
        if (frame != null) frame.setTitle(TITLE);
        checkAddrFile(addrFile);
        initialize();
    }
    public ContextVOMTranslationGUI(Dialog di, String addrFile) throws Exception
    {
        dialog = di;
        if (dialog != null) dialog.setTitle(TITLE);
        checkAddrFile(addrFile);
        initialize();
    }
    public ContextVOMTranslationGUI(String addrFile) throws Exception
    {
        checkAddrFile(addrFile);
        initialize();
    }

    private void checkAddrFile(String addrFile) throws Exception
    {
        if ((addrFile == null)||(addrFile.trim().equals("")))
            throw new Exception("Context AddressProperty File is null.");
        File file = new File(addrFile);
        if ((!file.exists())||(!file.isFile()))
            throw new Exception("This Context Address Property File is not exist.");

        contextAddressPropertyFile = addrFile;
    }
//    public ContextVOMTranslationGUI(Applet ap)
//    {
//        applet = ap;
//        initialize();
//    }


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
        contextVOMBrowseButton = new JButton(" Context Browse  ");
        centerPanel.add(contextVOMBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
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

        contextVOMBrowseButton.addActionListener(this);
        contextVOMBrowseButton.setEnabled(true);
        domainCollectButton.addActionListener(this);
        domainCollectButton.setEnabled(false);

        domainCombo.addItem("");
        domainCombo.setEditable(true);

        String path = contextAddressPropertyFile;
        if ((path == null)||(path.trim().equals(""))) path = (new FileSearchUtil()).searchFileLight(Config.CONTEXT_LINK_ADDRESS_PROPERTY_FILE_NAME);

        java.util.List<String> contextLine = null;
        try
        {
            if (path != null) contextLine = FileUtil.readFileIntoList(path);
        }
        catch(IOException ie)
        {

        }

        if ((contextLine == null)||(contextLine.size() == 0))
        {
            contextLine = FileUtil.getContextAddresses(contextAddressPropertyFile);
        }
        else
        {
            if (contextAddressPropertyFile == null) contextAddressPropertyFile = path;
        }

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
                if (line == null) continue;
                line = line.trim();
                if (line.startsWith("#")) continue;
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
            contextAddressList = contextLine;
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

        contextCombo.addItemListener(
            new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if(contextCombo.getSelectedIndex() > 0)
                    {
                        String contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());

                        //contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
                        String link = contextNameS.trim();
//                        String link = "";
//                        for (int i=0;i<contextName.size();i++)
//                        {
//                            String name = contextName.get(i);
//                            if (name.equals(contextNameS))
//                            {
//                                link = contextLink.get(i);
//                                break;
//                            }
//                        }
                        if (setDomainListToCombo(link, false))
                        {
                            currentLink = link;
                            currentItemIndex = contextCombo.getSelectedIndex();
                        }
                        else
                        {
                            if (frame != null)
                            {
                                JOptionPane.showMessageDialog(frame, "This context is not found : " + link, "Invalid Context", JOptionPane.ERROR_MESSAGE);
                            }

                            if (dialog != null)
                            {
                                JOptionPane.showMessageDialog(dialog, "This context is not found : " + link, "Invalid Context", JOptionPane.ERROR_MESSAGE);
                            }

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

        if (frame != null)
        {
            frame.add(this);
            frame.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        frame.dispose();
                    }
                }
            );
            frame.setSize(dim);
            frame.setVisible(true);
            DefaultSettings.centerWindow(frame);
        }

        if (dialog != null)
        {
            dialog.add(this);
            dialog.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        dialog.dispose();
                    }
                }
            );
            dialog.setSize(dim);
            dialog.setVisible(true);
            DefaultSettings.centerWindow(dialog);
        }

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

        if (e.getSource() == contextVOMBrowseButton)
        {
            if ((contextCombo.getItemCount() == 0)||(contextCombo.getSelectedIndex() == 0))
            {
                JOptionPane.showMessageDialog(this, "No Context is selected.", "No Context", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DomainInformationBrowser dib = null;
            String contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
            if (frame != null) dib = new DomainInformationBrowser(frame, contextNameS, contextAddressPropertyFile);
            if (dialog != null) dib = new DomainInformationBrowser(dialog, contextNameS, contextAddressPropertyFile);
            dib.setVisible(true);
            DefaultSettings.centerWindow(dib);
//            boolean ready= true;
//            String context = null;
//
//            if ((contextCombo.getItemCount() == 0)||(contextCombo.getSelectedIndex() == 0))
//            {
//                JOptionPane.showMessageDialog(this, "Any Context is not selected.", "No Context", JOptionPane.ERROR_MESSAGE);
//                ready = false;
//            }
//            else
//            {
//                String contextNameS = "";
//
//                contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
//                context = getContextPysicalAddress(contextNameS);
//            }
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
            boolean alreadyTrans= false;
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

                for (String line:contextAddressList)
                {
                    if (line == null) continue;
                    line = line.trim();
                    if (line.startsWith("#")) continue;
                    int idx = line.indexOf("@");
                    if (idx < 0)
                    {
                        idx = line.indexOf("=");
                        if (idx < 0) continue;
                    }
                    String name = line.substring(0, idx);
                    String addr = line.substring(idx+1);
                    if (name.equals(contextNameS))
                    {
                        context = addr;
                        alreadyTrans = true;
                        break;
                    }
                    if ((name.endsWith("/EVS"))&&(contextNameS.equals("EVS")))
                    {
                        context = addr;
                        alreadyTrans = true;
                        break;
                    }
                }

                if (context == null) context = getContextPysicalAddress(contextNameS);
            }

            if (!ready) return;

            String domain = (String) domainCombo.getSelectedItem();
            if (domainCombo.getSelectedIndex() == 0) domain = "";
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
                    //String contextNameS = (String) contextCombo.getItemAt(contextCombo.getSelectedIndex());
                    if (alreadyTrans)
                        outputField.setText(ContextVocabularyTranslation.translate(contextAddressPropertyFile, context, domain, input, inverseTag, true));
                    else
                        outputField.setText(ContextVocabularyTranslation.translate(contextAddressPropertyFile, context, domain, input, inverseTag));
                }
                catch(Exception ee)
                {
                    JOptionPane.showMessageDialog(this, ".." +ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ee.printStackTrace();
                }
            }
        }

        if(CANCEL_COMMAND.equals(command))
        {
            //okButtonClicked = false;
            setVisible(false);
            if (frame != null) frame.dispose();
            if (dialog != null) dialog.dispose();
        }


    }

    private String getContextPysicalAddress(String contextNameS)
    {
        String res = null;
        for (int i=0;i<contextName.size();i++)
        {
            String name = contextName.get(i);
            if (name.equals(contextNameS))
            {
                res = contextLink.get(i);
                break;
            }
        }
        return res;
    }

    private boolean setDomainListToCombo(boolean message)
    {
        return setDomainListToCombo(null, message);
    }
    private boolean setDomainListToCombo(String path, boolean message)
    {
        if (path.equals(currentLink)) return true;
        java.util.List<String[]> result = null;
        try
        {
            //result = ContextVocabularyTranslation.translate(path, "any", "any");
            result = ContextVocabularyTranslation.getDomainInformation(contextAddressPropertyFile, path, "");
        }
        catch(Exception ee)
        {
            if (message) JOptionPane.showMessageDialog(this, ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ((result == null)||(result.size() == 0))
        {
            JOptionPane.showMessageDialog(this, "No Result of Domain Searching", "No Domain result", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        currentLink = path;

        java.util.List<String> list = new ArrayList<String>();

        for(String[] arr:result)
        {
            String str = arr[0];
            int idx = str.indexOf("@");
            if (idx > 0) list.add(str.substring(0, idx));
            else list.add(str);
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
        if ((arg == null)||(arg.length == 0))
        {
            new ContextVOMTranslationGUI(new Frame());
            return;
        }

        try
        {
            if ((arg[0] == null)||(arg[0].trim().equals(""))) new ContextVOMTranslationGUI(new Frame());
            else new ContextVOMTranslationGUI(new Frame(), arg[0]);
        }
        catch(Exception ee)
        {
            System.out.println();
        }

    }
}


