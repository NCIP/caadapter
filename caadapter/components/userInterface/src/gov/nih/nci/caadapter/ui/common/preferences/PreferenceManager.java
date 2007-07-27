package gov.nih.nci.caadapter.ui.common.preferences;

import gov.nih.nci.caadapter.ui.main.MainMenuBar;
import nickyb.sqleonardo.environment.Preferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jun 26, 2007
 * Time: 11:06:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreferenceManager extends JDialog implements ActionListener
{

    //HashMap prefs;

    PrefListener prefListener;

    String regValue;

    public PreferenceManager(JFrame callingFrame)
    {
        super(callingFrame, "Preference Menu", true);
        //get the value stored in the registry
        try
        {
            regValue = (String) MainMenuBar.getCaAdapterPreferences().get("FIXED_LENGTH_VAR");
            if (regValue == null)
            {
                regValue = "none";
            }
        } catch (Exception e)
        {
            regValue = "none";
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        //
        JTabbedPane tabbedPane = new JTabbedPane();
        //
        tabbedPane.addTab("RDS Module", null, makeRDSPanel(), "");
        //
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("V3 Module", null, panel2, "");
        //
        add(tabbedPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 250);
        setLocation(400, 300);
        setVisible(true);
    }

    public PreferenceManager()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        //
        tabbedPane.addTab("RDS Module", null, makeRDSPanel(), "");
        //
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("V3 Module", null, panel2, "");
        //
        add(tabbedPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 250);
        setLocation(400, 300);
        setVisible(true);
    }

    protected JComponent makeTextPanel(String text)
    {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    protected JComponent makeRDSPanel()
    {
        JPanel mainPan = new JPanel(false);
        //initialize the preference listener
        prefListener = new PrefListener();
        mainPan.setLayout(new BorderLayout());
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        //
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Transformation"));
        //Create the radio buttons.
        JRadioButton comma = new JRadioButton("Comma Separated");
        comma.setMnemonic(KeyEvent.VK_C);
        comma.setActionCommand("Comma");
        comma.addActionListener(prefListener);
        if (regValue.equals("Comma"))
            comma.setSelected(true);
        //
        JRadioButton tab = new JRadioButton("Tab Separated");
        tab.setMnemonic(KeyEvent.VK_T);
        tab.setActionCommand("Tab");
        tab.addActionListener(prefListener);
        if (regValue.equals("Tab"))
            tab.setSelected(true);
        //
        JRadioButton fixedLength = new JRadioButton("Fixed Length");
        fixedLength.setMnemonic(KeyEvent.VK_T);
        fixedLength.setActionCommand("Fixed");
        fixedLength.addActionListener(prefListener);
        if (regValue.equals("Fixed"))
            fixedLength.setSelected(true);
        //
        JRadioButton spreadSheet = new JRadioButton("Spread Sheet (xls)");
        spreadSheet.setMnemonic(KeyEvent.VK_E);
        spreadSheet.setActionCommand("xls");
        spreadSheet.addActionListener(prefListener);
        if (regValue.equals("xls"))
            spreadSheet.setSelected(true);
        //
        JRadioButton none = new JRadioButton("none");
        none.setMnemonic(KeyEvent.VK_N);
        none.setActionCommand("none");
        none.addActionListener(prefListener);
        if (regValue.equals("none"))
            none.setSelected(true);
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(comma);
        group.add(tab);
        group.add(fixedLength);
        group.add(spreadSheet);
        group.add(none);
        radioPanel.add(comma);
        radioPanel.add(tab);
        radioPanel.add(fixedLength);
        radioPanel.add(spreadSheet);
        radioPanel.add(none);
        //
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.setLayout(new FlowLayout());
        JButton okBut = new JButton("OK");
        okBut.addActionListener(this);
        JButton canBut = new JButton("Cancel");
        canBut.addActionListener(this);
        buttonPanel.add(okBut);
        buttonPanel.add(canBut);
        //
        mainPan.add(radioPanel, BorderLayout.CENTER);
        mainPan.add(buttonPanel, BorderLayout.SOUTH);
        mainPan.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return mainPan;
    }

    public static void main(String args[])
    {
        Preferences.loadDefaults();
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new PreferenceManager();
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        //handle 'ok' and 'cancel'
        if (e.getActionCommand().equalsIgnoreCase("ok"))
        {
            if (prefListener.getValue() != null)
            {
                savePrefParams("FIXED_LENGTH_VAR", prefListener.getValue());
            } else
            {
                savePrefParams("FIXED_LENGTH_VAR", regValue);
            }
            this.dispose();
        } else
        {
            this.dispose();
        }
    }

    private void savePrefParams(String key, String value)
    {
        try
        {
            if(MainMenuBar.getCaAdapterPreferences() != null){
                MainMenuBar.getCaAdapterPreferences().put(key, value);
            } else {
                HashMap tempMap = new HashMap();
                tempMap.put(key, value);
                MainMenuBar.setCaAdapterPreferences(tempMap);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class PrefListener implements ActionListener
    {

        private String value = null;

        public void actionPerformed(ActionEvent e)
        {
            value = e.getActionCommand();
        }

        public String getValue()
        {
            return value;
        }
    }
}
