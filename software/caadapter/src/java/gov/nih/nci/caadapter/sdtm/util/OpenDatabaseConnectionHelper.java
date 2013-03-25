/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.sdtm.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 18, 2007
 * Time: 3:29:32 PM
 * @author LAST UPDATE $Author: phadkes $
 * @since  caAdapter v4.2
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-29 19:09:42 $
 * To change this template use File | Settings | File Templates.
 */
public class OpenDatabaseConnectionHelper
{

    private JDialog dialog;

    private JTextField hostField;

    private JTextField userIdField;

    private JTextField dataBaseDriver;

    private JPasswordField pwdField;

    private JTextField schemaField;


    public OpenDatabaseConnectionHelper(JFrame owner) throws Exception
    {
        dialog = new JDialog(owner);
        dialog.setTitle("Enter Connection Parameters");
        dialog.setUndecorated(true);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dialog.setSize(600, 230);
        dialog.setLocation(330, 350);
        JPanel superpanel = new JPanel();
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(6, 2));
        p1.add(new JLabel("Enter the Host URL: "));
        p1.add(hostField = new JTextField());
        p1.add(new JLabel("Enter the Database driver: "));
        p1.add(dataBaseDriver = new JTextField());
        p1.add(new JLabel("Enter your User ID: "));
        p1.add(userIdField = new JTextField());
        p1.add(new JLabel("Enter your Password:"));
        p1.add(pwdField = new JPasswordField());
        p1.add(new JLabel("Enter your Schema"));
        p1.add(schemaField = new JTextField());
        JPanel _butPan = new JPanel();
        _butPan.setBounds(2, 1, 10, 10);
        _butPan.setBorder(raisedetched);
        JButton _ok = new JButton("      OK      ");
        _ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        JButton _cancel = new JButton("Cancel");
        _butPan.add(_ok);
        // _butPan.add(_cancel);
        _cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        JCheckBox _def = new JCheckBox("Click to Default Values");
        _def.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                hostField.setText("jdbc:oracle:thin:@localhost:1521:XE");
                dataBaseDriver.setText("oracle.jdbc.driver.OracleDriver");
                userIdField.setText("hr");
                pwdField.setText("hr");
                schemaField.setText("hr");
            }
        });
        p1.add(_def);
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Enter Connection Information");
        p1.setBorder(title);
        superpanel.add(p1);
        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
        JLabel label01 = new JLabel("Enter the Connection Parameters");
        label01.setBorder(lineBorder);
        label01.setBackground(Color.BLACK);
        label01.setFont(new Font("Arial", Font.BOLD, 13));
        label01.setForeground(Color.BLACK);
        label01.setHorizontalAlignment(SwingConstants.CENTER);

        superpanel.setLayout(new BorderLayout());
        superpanel.add(label01,BorderLayout.NORTH);
        superpanel.add(p1, BorderLayout.CENTER);
        superpanel.add(_butPan, BorderLayout.SOUTH);
        superpanel.setBorder(lineBorder);
        //dialog.getContentPane().add(superpanel, BorderLayout.NORTH);
        //dialog.getContentPane().add(_butPan, BorderLayout.CENTER);
        dialog.add(superpanel);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public OpenDatabaseConnectionHelper() throws Exception
    {
        dialog = new JDialog();
        dialog.setTitle("Enter Connection Parameters");
        dialog.setUndecorated(true);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dialog.setSize(600, 230);
        dialog.setLocation(330, 350);
        JPanel superpanel = new JPanel();
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(6, 2));
        p1.add(new JLabel("Enter the Host URL: "));
        p1.add(hostField = new JTextField());
        p1.add(new JLabel("Enter the Database driver: "));
        p1.add(dataBaseDriver = new JTextField());
        p1.add(new JLabel("Enter your User ID: "));
        p1.add(userIdField = new JTextField());
        p1.add(new JLabel("Enter your Password:"));
        p1.add(pwdField = new JPasswordField());
        p1.add(new JLabel("Enter your Schema"));
        p1.add(schemaField = new JTextField());
        JPanel _butPan = new JPanel();
        _butPan.setBounds(2, 1, 10, 10);
        _butPan.setBorder(raisedetched);
        JButton _ok = new JButton("      OK      ");
        _ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        JButton _cancel = new JButton("Cancel");
        _butPan.add(_ok);
        // _butPan.add(_cancel);
        _cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        JCheckBox _def = new JCheckBox("Click to Default Values");
        _def.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                hostField.setText("jdbc:oracle:thin:@localhost:1521:XE");
                dataBaseDriver.setText("oracle.jdbc.driver.OracleDriver");
                userIdField.setText("hr");
                pwdField.setText("hr");
                schemaField.setText("hr");
            }
        });
        p1.add(_def);
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Enter Connection Information");
        p1.setBorder(title);
        superpanel.add(p1);
        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
        JLabel label01 = new JLabel("Enter the Connection Parameters");
        label01.setBorder(lineBorder);
        label01.setBackground(Color.BLACK);
        label01.setFont(new Font("Arial", Font.BOLD, 13));
        label01.setForeground(Color.BLACK);
        label01.setHorizontalAlignment(SwingConstants.CENTER);

        superpanel.setLayout(new BorderLayout());
        superpanel.add(label01,BorderLayout.NORTH);
        superpanel.add(p1, BorderLayout.CENTER);
        superpanel.add(_butPan, BorderLayout.SOUTH);
        superpanel.setBorder(lineBorder);
        //dialog.getContentPane().add(superpanel, BorderLayout.NORTH);
        //dialog.getContentPane().add(_butPan, BorderLayout.CENTER);
        dialog.add(superpanel);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public Hashtable getDatabaseConnectionInfo()
    {
        Hashtable<String, String> _conInfo = new Hashtable<String, String>();
        _conInfo.put("URL", hostField.getText());
        _conInfo.put("UserID", userIdField.getText());
        _conInfo.put("PWD", pwdField.getText());
        _conInfo.put("SCHEMA", schemaField.getText());
        _conInfo.put("Driver", dataBaseDriver.getText());
        return _conInfo;
    }

    public static void main(String args[]) throws Exception
    {
        OpenDatabaseConnectionHelper tt = new OpenDatabaseConnectionHelper();
        tt.dialog.setVisible(true);
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/09/29 19:08:04  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
