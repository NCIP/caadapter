package gov.nih.nci.caadapter.dataviewer.helper;


import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 5:04:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class LowerPaneListener implements ChangeListener
{

    MainDataViewerFrame mD;

    public LowerPaneListener(MainDataViewerFrame mD)
    {
        this.mD = mD;
    }

    public void stateChanged(ChangeEvent evt)
    {
        JTabbedPane pane = (JTabbedPane) evt.getSource();        
        handleLowerPanel(pane.getSelectedIndex());
    }

    public void handleLowerPanel(int _s)
    {
        switch (_s)
        {
            case 1:
            {//first button clicked
                if (mD.get_jp1().getComponentCount() > 0)
                    if (mD.get_jp1().getComponentCount() > 0)
                    {
                        mD.get_jp1().removeAll();
                    }
                TextArea _tmpTxtArea;
                _tmpTxtArea = mD.get_showSQL();
                _tmpTxtArea.setEditable(false);
                mD.get_lPane().setSelectedIndex(1);
                try
                {
                    mD.get_jp1().setLayout(new BorderLayout());
                    JTextArea _jt = new JTextArea(10, 850);
                    JScrollPane _jp = new JScrollPane(_jt);
                    String _sqlSTR = (((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
                    _jt.setEditable(false);
                    _jt.setText(_sqlSTR);
                    mD.get_jp1().add(_jp, BorderLayout.CENTER);
                    mD.get_jp1().revalidate();
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                break;
            }
           
            case 2:
            {
                System.out.println("Edit Sql");
                mD.get_lPane().setSelectedIndex(2);
                if (mD.get_jp3().getComponentCount() > 0)
                    mD.get_jp3().removeAll();
                mD.get_jp3().setLayout(new BorderLayout());
                mD.get_jp3().add((((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getBrowser(), BorderLayout.CENTER);
                mD.get_jp3().revalidate();
                break;
            }
            case 3:
            {
                System.out.println("Add Tables");
                mD.get_lPane().setSelectedIndex(3);
                if (mD.get_jp4().getComponentCount() > 0)
                    mD.get_jp4().removeAll();
                mD.get_jp4().setLayout(new BorderLayout());
                mD.get_jp4().add((((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getObjects(), BorderLayout.CENTER);
                mD.get_jp4().revalidate();
                break;
            }
        }
    }
}
