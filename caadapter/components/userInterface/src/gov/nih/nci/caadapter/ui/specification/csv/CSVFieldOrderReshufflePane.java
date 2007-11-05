/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVFieldOrderReshufflePane.java,v 1.4 2007-11-05 15:00:26 jayannah Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * This class defines a panel handles the shuffling field sequence under the same segment.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2007-11-05 15:00:26 $
 */
public class CSVFieldOrderReshufflePane extends JPanel implements ActionListener, ListSelectionListener
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CSVFieldOrderReshufflePane.java,v $";
    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVFieldOrderReshufflePane.java,v 1.4 2007-11-05 15:00:26 jayannah Exp $";
    private static final String MOVE_UP_COMMAND = "Move Up";
    private static final String MOVE_UP_COMMAND_MNEMONIC = "U";
    private static final String MOVE_DOWN_COMMAND = "Move Down";
    private static final String MOVE_DOWN_COMMAND_MNEMONIC = "D";
    private JTable fieldTable;
    private CSVFieldOrderReshuffleTableModel tableModel;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private java.util.List csvFieldMetaList;
    private CSVPanel controller_;

    public CSVFieldOrderReshufflePane()
    {
        initialize();
    }

    public CSVFieldOrderReshufflePane(CSVPanel controller)
    {
        initialize();
        controller_ = controller;
    }

    private void initialize()
    {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new GridLayout(2, 1));
        moveUpButton = new JButton(MOVE_UP_COMMAND);
        moveUpButton.setMnemonic(MOVE_UP_COMMAND_MNEMONIC.charAt(0));
        moveUpButton.addActionListener(this);
        moveDownButton = new JButton(MOVE_DOWN_COMMAND);
        moveDownButton.setMnemonic(MOVE_DOWN_COMMAND_MNEMONIC.charAt(0));
        moveDownButton.addActionListener(this);
        innerPanel.add(moveUpButton);//, BorderLayout.NORTH);
        innerPanel.add(moveDownButton);//, BorderLayout.SOUTH);
        buttonPanel.add(innerPanel, BorderLayout.NORTH);
        tableModel = new CSVFieldOrderReshuffleTableModel(new ArrayList());
        fieldTable = new JTable(tableModel);
        fieldTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fieldTable);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.EAST);
        setButtonsDisabled(); // inserted by umkis 11/07/05 for No selected data on segment property pane
        fieldTable.getSelectionModel().addListSelectionListener(this);
        // Following addMouseListener is inserted by umkis 11/07/05
        // for setting enable or disable button according to selected or non-selected data on segment property pane
        fieldTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                setButtonsEnabled();
                int[] selectedRows = fieldTable.getSelectedRows();
                if (selectedRows[0] == 0) {
                    moveUpButton.setEnabled(false);
                    return;
                } else setButtonsEnabled();
                if (selectedRows[selectedRows.length - 1] == (fieldTable.getRowCount() - 1)) {
                    moveDownButton.setEnabled(false);
                    return;
                } else setButtonsEnabled();

//                    if(selectedRows==null || selectedRows.length==0) setButtonsDisabled();
//		            else
//                    {
//                        if (tableModel.getRowCount() <= 1) setButtonsDisabled();
//                        else setButtonsEnabled();
//                    }
            }
        });
    }

    // Following 2 methods are inserted by umkis 11/07/05
    // for setting enable or disable button according to selected or non-selected data on segment property pane
    public void setButtonsEnabled()
    {
        if (!moveUpButton.isEnabled()) moveUpButton.setEnabled(true);
        if (!moveDownButton.isEnabled()) moveDownButton.setEnabled(true);
    }

    public void setButtonsDisabled()
    {
        if (moveUpButton.isEnabled()) moveUpButton.setEnabled(false);
        if (moveDownButton.isEnabled()) moveDownButton.setEnabled(false);
    }

    public boolean isDataChanged()
    {
        return tableModel.isDataChanged();
    }

    public void setDataChanged(boolean value)
    {
        if (!value) {//clear out the changes.
            tableModel.initializeSequenceIndexArray();
        }
    }

    public boolean setCSVFieldMetaList(List<CSVFieldMeta> newCSVFieldMetaList)
    {
        this.csvFieldMetaList = newCSVFieldMetaList;
        tableModel = new CSVFieldOrderReshuffleTableModel(csvFieldMetaList);
        fieldTable.setModel(tableModel);
        return true;
    }

    public List getCSVFieldMetaList(boolean withUIChanges)
    {
        return tableModel.getCsvFieldMetaList(withUIChanges);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        int[] selectedRows = fieldTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No row selected to move up or down.", "Warning", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int rowCount = tableModel.getRowCount();
        if (MOVE_UP_COMMAND.equals(command)) {
            if (selectedRows[0] == 0) {
                moveUpButton.setEnabled(false);
                return;
            } else setButtonsEnabled();
            // This if block was inserted by umkis 11/07/05 for protecting from over-going move-up button.
            tableModel.moveUp(selectedRows);
            //reselect them
            fieldTable.getSelectionModel().clearSelection();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int index = selectedRows[i];
                if (index == 0) {//skip the last one
                    continue;
                } else {
                    fieldTable.getSelectionModel().addSelectionInterval(index - 1, index - 1);
                    if ((index - 1) == 0) moveUpButton.setEnabled(false);
                }
            }
        } else if (MOVE_DOWN_COMMAND.equals(command)) {
            if (selectedRows[selectedRows.length - 1] == (fieldTable.getRowCount() - 1)) {
                moveDownButton.setEnabled(false);
                return;
            } else setButtonsEnabled();
            // This if block was inserted by umkis 11/07/05 for protecting from over-going move-down button.
            tableModel.moveDown(selectedRows);
            //reselect them
            fieldTable.getSelectionModel().clearSelection();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int index = selectedRows[i];
                if (index == (rowCount - 1)) {//skip the last one
                    continue;
                } else {
                    fieldTable.getSelectionModel().addSelectionInterval(index + 1, index + 1);
                    if ((index + 1) == (fieldTable.getRowCount() - 1)) moveDownButton.setEnabled(false);
                }
            }
        }
    }

//	public static void main(String[] args)
//	{
//		JFrame frame = new JFrame();
//		frame.setSize(400, 400);
//		CSVFieldOrderReshufflePane worker = new CSVFieldOrderReshufflePane();
//		frame.getContentPane().add(worker, BorderLayout.CENTER);
//		frame.setVisible(true);
//		DefaultSettings.centerWindow(frame);
//		try
//		{
//			Thread.sleep(3000);
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		}
//		java.util.List fieldMetaList = new ArrayList();
//		CSVFieldMetaImpl impl1 = new CSVFieldMetaImpl(1, "name_1", null);
//		CSVFieldMetaImpl impl2 = new CSVFieldMetaImpl(2, "name_2", null);
//		CSVFieldMetaImpl impl3 = new CSVFieldMetaImpl(3, "name_3", null);
    //		fieldMetaList.add(impl1);
    //		fieldMetaList.add(impl2);
    //		fieldMetaList.add(impl3);
    //		worker.setCSVFieldMetaList(fieldMetaList);
    //	}
    //int[] selectedNodesIndices = null;
    ArrayList selectedNodes = null;
    HashSet selectedIndices= null;

    public boolean areNodesSelected()
    {
        selectedNodes = new ArrayList();
        populateArrayList();
        return (selectedNodes != null && selectedNodes.size() > 0);
    }

    public ArrayList getSelectedNodes()
    {
        return selectedNodes;
    }

    public void clearSelectedNodes()
    {
        selectedIndices.clear();
        selectedNodes = null;
    }

    private void populateArrayList()
    {
        
        Iterator iter = selectedIndices.iterator();
        List tempList = getCSVFieldMetaList(true);
        while (iter.hasNext()) {
            Integer integer = (Integer) iter.next();
            int val = integer.intValue();
            CSVFieldMetaImpl csvFieldMeta = (CSVFieldMetaImpl) tempList.get(val);
            selectedNodes.add(csvFieldMeta);
        }
    }

    public void valueChanged(ListSelectionEvent e)
    {
        selectedIndices = new HashSet();
        //clear the selection of the jtree; so that hittng delete button dont cause headaches
        controller_.getTree().clearSelection();
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting();        
        // System.out.println("Event for indexes " + firstIndex + " - " + lastIndex + "; isAdjusting is " + isAdjusting + "; selected indexes:");
        
        if (lsm.isSelectionEmpty()) {
            //System.out.println(" <none>");
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    selectedIndices.add(new Integer(i));
                    //System.out.println(new Integer(i));
                }
            }
        }
    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/13 03:07:00  jayannah
 * HISTORY      : Changes to enable delete action from the properties pane and refresh the tree as well as the property pane, And show a confirmation window for the delete
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/05 20:27:11  schroedn
 * HISTORY      : Added hiding of MOVE UP/DOWN buttons depending on what field is selected
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/07 23:58:17  umkis
 * HISTORY      : for setting enable or disable button according to selected or non-selected data on segment property pane
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/07 19:07:25  umkis
 * HISTORY      : for setting enable or disable button according to selected or non-selected data on segment property pane
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/04 21:34:16  umkis
 * HISTORY      : defect# 123
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/04 20:49:14  jiangsc
 * HISTORY      : UI Enhancement to fix data inconsistency between tree and properties panel.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/19 18:54:36  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/17 17:40:21  jiangsc
 * HISTORY      : Save point of the CSV Field Re-ordering
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/15 22:26:34  jiangsc
 * HISTORY      : Save point of the CSV Field Re-ordering
 * HISTORY      :
 */
