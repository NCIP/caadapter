/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.common.preferences;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.main.MainMenuBar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * This class implements preferences in caAdapter
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.14 $
 *          $Date: 2008-07-10 15:51:00 $
 */
public class PreferenceManager extends JDialog implements ActionListener {

    private PrefListener prefListener=null;
    private String regValue=null;

    public PreferenceManager(JFrame callingFrame) {
        super(callingFrame, "Preference Menu", true);
        //get the value stored in the registry
        try {
            regValue = (String) CaadapterUtil.getCaAdapterPreferences().get("FIXED_LENGTH_VAR");
            if (regValue == null) {
                regValue = "none";
            }
        } catch (Exception e) {
            regValue = "none";
            e.printStackTrace();
        }
        //
        JTabbedPane tabbedPane = new JTabbedPane();
        //
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_SDTM_TRANSFORMATION_ACTIVATED))
        	tabbedPane.addTab("RDS Module", null, makeRDSPanel(), "");
        //
        JComponent panel2 = new Hl7V3SpecificationPreferencePane(this);
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
            tabbedPane.addTab("HL7 V3 Transformation", null, panel2, "");
        JComponent panel3 = new MMSPreferencePane(this);
        if(CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED))
        	tabbedPane.addTab("MMS Preference Pane", null, panel3, "");
        //JComponent panel4 = new NamespacePreferencePane(this);
        //tabbedPane.addTab("Namespace Preference Pane", null, panel4, "");
        //
        add(tabbedPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 280);
        setLocation(400, 300);
        setVisible(true);
    }

    public PreferenceManager() {
        JTabbedPane tabbedPane = new JTabbedPane();
        //
        tabbedPane.addTab("RDS Module", null, makeRDSPanel(), "");
        //
        JComponent panel2 = new Hl7V3SpecificationPreferencePane(this);
        tabbedPane.addTab("Hl7 V3 Transformation", null, panel2, "");
        //
        JComponent panel3 = new MMSPreferencePane(this);
        tabbedPane.addTab("MMS Preference Pane", null, panel3, "");
        //
        //JComponent panel4 = new NamespacePreferencePane(this);
        //tabbedPane.addTab("Namespace Preference Pane", null, panel4, "");
        //
        add(tabbedPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 280);
        setLocation(400, 300);
        setVisible(true);
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    protected JComponent makeRDSPanel() {
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
        //radioPanel.add(comma);
        //radioPanel.add(tab);
        radioPanel.add(fixedLength);
        //radioPanel.add(spreadSheet);
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

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PreferenceManager();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        //handle 'ok' and 'cancel'
        if (e.getActionCommand().equalsIgnoreCase("ok")) {
            if (prefListener.getValue() != null) {
            	CaadapterUtil.savePrefParams("FIXED_LENGTH_VAR", prefListener.getValue());
            } else {
            	CaadapterUtil.savePrefParams("FIXED_LENGTH_VAR", regValue);
            }
            this.dispose();
        } else {
            this.dispose();
        }
    }

    class PrefListener implements ActionListener {
        private String value = null;

        public void actionPerformed(ActionEvent e) {
            value = e.getActionCommand();
        }

        public String getValue() {
            return value;
        }
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.13  2008/06/09 19:53:51  phadkes
 * New license text replaced for all .java files.
 *
 * Revision 1.12  2007/09/20 16:38:34  schroedn
 * License text
 *
 * Revision 1.11  2007/09/07 19:30:38  wangeug
 * relocate readPreference and savePreference methods
 *
 * Revision 1.10  2007/09/06 20:03:07  wangeug
 * set ODI optional
 *
 * Revision 1.9  2007/08/17 15:15:25  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 * Revision 1.8  2007/08/17 14:31:22  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
