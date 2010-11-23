package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:41:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditTermWizard extends JDialog implements ActionListener
{
    private static final String TITLE = "Edit Term";
    private static final String CREATE_TERM_COMMAND = "Submit";
    private static final String CANCEL_COMMAND = "Cancel";
    private EditTermFrontPage frontPage;
    private TermMeta metaView;
    String type = null;

    public EditTermWizard(Frame frame, TermMeta meta, String type, boolean modal)
            throws HeadlessException
    {
        super(frame, TITLE, modal);
        this.type = type;
        metaView=meta;
        initialize();
    }

    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new EditTermFrontPage(this, type);
        contentPane.add(frontPage, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton okButton = new JButton(CREATE_TERM_COMMAND);
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        JPanel tempPanel = new JPanel(new GridLayout(1, 2));
        tempPanel.add(okButton);
        tempPanel.add(cancelButton);
        buttonPanel.add(tempPanel);
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(southPanel, BorderLayout.SOUTH);
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (CREATE_TERM_COMMAND.equals(command))
        {
            if(!frontPage.validateInputFields())
            {
                JOptionPane.showMessageDialog(this, "Term is null or blank", "Null Term values", JOptionPane.ERROR_MESSAGE);
 
                return;
            }
            else
            {
                String type = frontPage.getTermType();
                if (type.equals(NodeContentElement.TYPES[4]))
                {
                    String val = frontPage.getTermValue();
                    if (val == null) val = "";
                    else val = val.trim();
                    if (val.equals(""))
                    {
                        JOptionPane.showMessageDialog(this, "Null or Empty Number value", "Null Number value", JOptionPane.ERROR_MESSAGE);
 
                        return;
                    }
                    try
                    {
                        Double.parseDouble(val);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(this, "This is Not a Number : " + val, "NumberFormatException", JOptionPane.ERROR_MESSAGE);
 
                        return;
                    }
                }
 
            }
        }
//        else if (CANCEL_COMMAND.equals(command))
//        {
// 
//        }

        setVisible(false);
        dispose();
    }

}
