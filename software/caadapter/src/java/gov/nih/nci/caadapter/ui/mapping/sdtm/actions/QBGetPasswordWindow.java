/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: HarshaUshaDithu
 * <p/>
 * Date: May 21, 2007
 * <p/>
 * Time: 11:07:41 PM
 * <p/>
 * @author LAST UPDATE $Author: phadkes $
 * @since  caAdapter v4.2
 * @version    $Revision: 1.9 $
 * @date       $Date: 2008-09-29 21:22:50 $
 * To change this template use File | Settings | File Templates.
 */
public class QBGetPasswordWindow implements WindowListener, KeyListener {
    private JPasswordField _passWd;
    private JDialog dialog;
    private boolean result = false;

    public boolean isResult() {
        return result;
    }

    public QBGetPasswordWindow(Frame owner, String params, String title) {
        dialog = new JDialog(owner);
        dialog.setUndecorated(true);
        dialog.setTitle("Get password to open map file " + title);
        EmptyStringTokenizer empt = new EmptyStringTokenizer(params, "~");
        JPanel mainPan = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Connection Information");
        mainPan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //
        JPanel centerPan = new JPanel();
        centerPan.setLayout(new GridLayout(5, 2));
        centerPan.add(new JLabel("Host URL :"));
        String url = empt.getTokenAt(0);
        JTextField one = new JTextField(url);
        one.setEditable(false);
        centerPan.add(one);
        centerPan.add(new JLabel("User ID:"));
        String user = empt.getTokenAt(2);
        JTextField two = new JTextField(user);
        two.setEditable(false);
        centerPan.add(two);
        centerPan.add(new JLabel("Enter Password :"));
        centerPan.add(_passWd = new JPasswordField());
        String tmpStr = GetConnectionSingleton.isConnectionAvailable();
        if (tmpStr != null) {
            if (tmpStr.equalsIgnoreCase(url)) {
                _passWd.setText(GetConnectionSingleton.getPassword());
                _passWd.setSelectionStart(0);
                _passWd.setSelectionEnd(_passWd.getPassword().toString().length());
            } else {
                JOptionPane.showMessageDialog(dialog, "The current connection object points to <b>" + tmpStr + "</b> Database \n Please exit from the panel and re-open again", "Connection INVALID", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        centerPan.add(new JLabel("Database Driver :"));
        JTextField three = new JTextField(empt.getTokenAt(1));
        three.setEditable(false);
        centerPan.add(three);
        centerPan.add(new JLabel("Schema Name:"));
        JTextField four = new JTextField(empt.getTokenAt(3));
        four.setEditable(false);
        centerPan.add(four);
        //
        JPanel butPan = new JPanel();
        butPan.setLayout(new FlowLayout());
        JButton ok = new JButton("Ok");
        butPan.add(ok);
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                result = true;
                if (!(_passWd.getPassword().length > 0)) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a password");
                } else {
                    dialog.dispose();
                }
            }
        });
        JButton cancel = new JButton("Cancel");
        butPan.add(cancel);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });
        //title begin
        JLabel label01 = new JLabel("Enter Password...");
        label01.setBorder(BorderFactory.createLineBorder(Color.black));
        label01.setOpaque(true);
        Color blue = new Color(0,0,153);
        label01.setBackground(blue);
        label01.setFont(new Font("Arial", Font.BOLD, 13));
        label01.setForeground(Color.WHITE);
        label01.setHorizontalAlignment(SwingConstants.CENTER);
        //title end
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        centerPan.setBorder(raisedetched);
        butPan.setBorder(raisedetched);
        mainPan.setLayout(new BorderLayout());
        mainPan.add(label01, BorderLayout.NORTH);
        mainPan.add(centerPan, BorderLayout.CENTER);
        mainPan.add(butPan, BorderLayout.SOUTH);
        dialog.setModal(true);
        dialog.addWindowListener(this);
        _passWd.addKeyListener(this);
        dialog.getContentPane().add(mainPan);
        dialog.setSize(500, 230);
        dialog.setLocation(350, 325);
        dialog.setVisible(true);
    }

    public void windowOpened(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                _passWd.requestFocus();
            }
        });
    }

    public void windowClosing(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosed(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
             result = true;
            if (!(_passWd.getPassword().length > 0)) {
                JOptionPane.showMessageDialog(dialog, "Please enter a password");
            } else {
                dialog.dispose();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public String getPassword() {
        return _passWd.getText();
    }

    public static void main(String args[]) {
        new QBGetPasswordWindow(null, "jdbc:oracle:thin:@localhost:1521:XE~oracle.jdbc.OracleDriver~hr~hr", "blah blah");
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
