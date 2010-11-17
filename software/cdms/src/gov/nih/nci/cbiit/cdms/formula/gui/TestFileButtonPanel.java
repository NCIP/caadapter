package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 17, 2010
 * Time: 12:31:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestFileButtonPanel extends JPanel implements ActionListener
{
    JButton[] buttons = new JButton[8];
    FormulaMainPanel mainPanel;
    TestFileButtonPanel(FormulaMainPanel root)
    {

        mainPanel = root;
        //useOperator = true;
        initialize();
    }
    private void initialize()
    {
        this.setLayout(new GridLayout(1, buttons.length));
        for (int i=0;i< buttons.length;i++)
        {
            buttons[i] = new JButton("BSA" + (i+1));
            this.add(buttons[i]);
            buttons[i].addActionListener(this);
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        int inn = -1;
        for (int i=0;i< buttons.length;i++)
        {
            if (e.getSource() == buttons[i])
            {
                inn = i;
                break;
            }
        }
        if (inn < 0) return;

        inn++;
        String testFile="workingspace/BSA"+inn+".xml";
        if (!mainPanel.openFile(new File(testFile)))
        {
            System.out.println("Open file Failure : " + testFile);
        }
    }
}
