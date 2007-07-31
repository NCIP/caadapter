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
 * To change this template use File | Settings | File Templates.
 */

public class QBGetPasswordWindow implements WindowListener, KeyListener
{

    private JPasswordField _passWd;

    private JDialog dialog;

    public QBGetPasswordWindow(Frame owner, String params, String title)
    {
        dialog = new JDialog(owner);
        dialog.setTitle("Get password to open map file " + title);
        EmptyStringTokenizer empt = new EmptyStringTokenizer(params, "~");
        JPanel mainPan = new JPanel();
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Connection Information");
        mainPan.setBorder(titleBorder);
        //
        JPanel centerPan = new JPanel();
        centerPan.setLayout(new GridLayout(5, 2));
        centerPan.add(new JLabel("Host Name :"));
        String url = empt.getTokenAt(0);
        JTextField one = new JTextField(url);
        one.setEditable(false);
        centerPan.add(one);
        centerPan.add(new JLabel("User :"));
        String user = empt.getTokenAt(2);
        JTextField two = new JTextField(user);
        two.setEditable(false);
        centerPan.add(two);
        centerPan.add(new JLabel("Enter Password :"));
        centerPan.add(_passWd = new JPasswordField());
        String tmpStr = GetConnectionSingleton.isConnectionAvailable();
        if (tmpStr != null)
        {
            if (tmpStr.equalsIgnoreCase(url))
            {
                _passWd.setText(GetConnectionSingleton.getPassword());
                _passWd.setSelectionStart(0);
                _passWd.setSelectionEnd(_passWd.getPassword().toString().length());
            } else
            {
                JOptionPane.showMessageDialog(dialog, "The current connection object points to <b>" + tmpStr + "</b> Database \n Please exit from the panel and re-open again", "Connection INVALID", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        centerPan.add(new JLabel("Driver :"));
        JTextField three = new JTextField(empt.getTokenAt(1));
        three.setEditable(false);
        centerPan.add(three);
        centerPan.add(new JLabel("Schema :"));
        JTextField four = new JTextField(empt.getTokenAt(3));
        four.setEditable(false);
        centerPan.add(four);
        //
        JPanel butPan = new JPanel();
        butPan.setLayout(new FlowLayout());
        JButton ok = new JButton("Ok");
        butPan.add(ok);
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (!(_passWd.getPassword().length > 0))
                {
                    JOptionPane.showMessageDialog(dialog, "Please enter a password");
                } else
                {
                    dialog.dispose();
                }
            }
        });
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        centerPan.setBorder(raisedetched);
        butPan.setBorder(raisedetched);
        mainPan.setLayout(new BorderLayout());
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

    public void windowOpened(WindowEvent e)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                _passWd.requestFocus();
            }
        });
    }

    public void windowClosing(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosed(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyTyped(KeyEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            if (!(_passWd.getPassword().length > 0))
            {
                JOptionPane.showMessageDialog(dialog, "Please enter a password");
            } else
            {
                dialog.dispose();
            }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public String getPassword()
    {
        return _passWd.getText();
    }

    public static void main(String args[])
    {
        new QBGetPasswordWindow(null, "jdbc:oracle:thin:@localhost:1521:XE~oracle.jdbc.OracleDriver~hr~hr", "blah blah");
    }
}
