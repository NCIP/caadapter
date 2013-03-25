/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * This class implements the fixed length records
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:54:06 $
 */
public class RDSFixedLenghtInput extends JDialog implements ActionListener {
    JPanel butPan=null;
    HashMap userValues=null;

    public RDSFixedLenghtInput(JFrame callingFrame, HashSet keyList) {
        super(callingFrame, "Enter the length for desired key field(s)... ", true);
        userValues = new HashMap();
        JPanel finalPane = createPanels(keyList);
        finalPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Fixed Length parameters"));
        add(finalPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        if (keyList.size() > 9) {
            setSize(350, 300);
        } else {
            pack();
        }
        setLocation(400, 300);
        setResizable(false);
        setVisible(true);
    }

    public HashMap getUserValues() {
        return userValues;
    }

    public RDSFixedLenghtInput(HashSet keyList) {
        JPanel finalPane = createPanels(keyList);
        finalPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Fixed Length parameters"));
        add(finalPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        if (keyList.size() > 9) {
            setSize(350, 300);
        } else {
            pack();
        }
        setLocation(400, 300);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createPanels(HashSet keyList) {
        JPanel mainPan = new JPanel();
        butPan = new JPanel();
        butPan.setLayout(new GridLayout(keyList.size(), 2));
        Iterator iterator = keyList.iterator();
        JTextField _tempFld;
        JLabel _tempLabel;
        while (iterator.hasNext()) {
            _tempLabel = new JLabel(iterator.next().toString());
            _tempLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 25));
            butPan.add(_tempLabel);
            _tempFld = new JTextField("");
            _tempFld.addActionListener(this);
            // _tempFld.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
            butPan.add(_tempFld);
        }
        mainPan.setLayout(new BorderLayout());
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
        JScrollPane sp = new JScrollPane(butPan, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPan.add(sp, BorderLayout.CENTER);
        mainPan.add(buttonPanel, BorderLayout.SOUTH);
        return mainPan;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("OK")) {
            for (int i = 0; i < butPan.getComponentCount(); i++) {
                try {
                    if ((butPan.getComponent(i) instanceof javax.swing.JTextField)) {
                        String _temp = ((JTextField) butPan.getComponent(i)).getText();
                        if (_temp.length() > 0) {
                            userValues.put(((JLabel) butPan.getComponent(i - 1)).getText(), _temp);
                        }
                    }
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());
                }
            }
            this.dispose();
        } else if (e.getActionCommand().equalsIgnoreCase("Cancel")) {
            this.dispose();
        }
    }

    public static void main(String[] args) {
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
                LinkedHashSet _set = new LinkedHashSet();
                _set.add("TE.STUDYID");
                _set.add("TE.TESTRL");
                _set.add("DM.DMDTC");
                _set.add("DM.RFSTDTC");
                _set.add("DM.ARM");
                _set.add("DM.DOMAIN");
                _set.add("DM.BRTHDTC");
                _set.add("TE.STU3DYID");
                _set.add("TE.TESdTRL");
                _set.add("DM.DdMDTC");
                _set.add("DM.RdFSTDTC");
                _set.add("DM.AdRM");
                _set.add("DM.DOdMAIN");
                _set.add("DM.BRdTHDTC");
                _set.add("TE.dSTUDYID");
                _set.add("TE.TESTRL");
                _set.add("DM.DMDfTC");
                _set.add("DM.RFfSTDTC");
                _set.add("DM.A3RM");
                _set.add("DM.DOfMAIN");
                //                                _set.add("DM.BRTgHDTC");
                //                                _set.add("TE.dSTUDYID");
                //                                _set.add("TE.TESxTRL");
                //                                _set.add("DM.DMDTC");
                //                                _set.add("DM.RFzSTDTC");
                //                                _set.add("DM.AcRM");
                //                                _set.add("DM.qDOMAIN");
                //                                _set.add("DM.BRTHzDTC");
                //                                //
                //                                _set.add("T2E.STUDYID");
                //                                _set.add("TwE.TESTRL");
                //                                _set.add("DM.DMDTC");
                //                                _set.add("DwM.RFSTDTC");
                //                                _set.add("DrM.ARM");
                //                                _set.add("DM.DtOMAIN");
                //                                _set.add("DgM.BRTHDTC");
                //                                _set.add("TdE.STU3DYID");
                //                                _set.add("TE.TEvSdTRL");
                //                                _set.add("DvM.DdMDTC");
                //                                _set.add("DM.RdFScTDTC");
                //                                _set.add("DdM.AbdRM");
                //                                _set.add("DM.DOduMAIN");
                //                                _set.add("DM.BRdeTHDTC");
                //                                _set.add("2E.dSTUDYID");
                //                                _set.add("TE.TEtSTRL");
                //                                _set.add("DkM.DMDfTC");
                //                                _set.add("DM.RFfSTnDTC");
                //                                _set.add("D4M.A43RM");
                //                                _set.add("D7M.DOfMAIN");
                //                                _set.add("DM.BRTngHDTC");
                //                                _set.add("3TE.dSTUDYID");
                //                                _set.add("TE.TESxbTRL");
                //                                _set.add("DiM.DMDTC");
                //                                _set.add("DM.RFz bTDTC");
                //                                _set.add("DM.AcRbM");
                //                                _set.add("DdM.qDOM6AIN");
                //                                _set.add("DdM.BRTH5zDTC");
                new RDSFixedLenghtInput(_set);
            }
        });
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
