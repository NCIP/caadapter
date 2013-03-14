/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ui;

import gov.nih.nci.caadapter.dvts.common.util.DefaultSettings;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 24, 2011
 * Time: 11:19:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomainInformationBrowser extends JDialog
{
    private final String TITLE = "VOM Domain Information Browser";
    String context = null;
    JSplitPane splitPane = null;
    JTable table = null;
    JTextArea textArea = null;
    JTextField contextSymbol;
    JTextField contextAddress;
    Dialog dialog = null;
    TableModel tableModel;

    private String contextAddressPropertyFile = null;

    DomainInformationBrowser(Frame fr, String contextS, String propertyFile)
    {
        super(fr);
        setTitle(TITLE);
        context = contextS;
        contextAddressPropertyFile = propertyFile;
        initialize();
    }
    DomainInformationBrowser(Dialog di, String contextS, String propertyFile)
    {
        super(di);
        setTitle(TITLE);
        context = contextS;
        contextAddressPropertyFile = propertyFile;
        initialize();
    }
    private void initialize()
    {
        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel northPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(3, 3, 3, 3);
        JLabel contextLabel = new JLabel("   Context   ");
        northPanel.add(contextLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contextSymbol = new JTextField();
        contextSymbol.setPreferredSize(new Dimension(350, 25));
        northPanel.add(contextSymbol, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        JLabel domainLabel = new JLabel("   Location   ");
        northPanel.add(domainLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        contextAddress = new JTextField();
        contextAddress.setPreferredSize(new Dimension(350, 25));
        northPanel.add(contextAddress, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        String conAddr = ContextVocabularyTranslation.searchContextPhysicalAddress(contextAddressPropertyFile, context);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);







        java.util.List<String[]> result = null;

        try
        {
            result = ContextVocabularyTranslation.getDomainInformation(contextAddressPropertyFile, context, "");
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
            JOptionPane.showMessageDialog(this, ee.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] header = new String[] {"VOM File Name","Domain Name", "Inverse Allow", "Information"};
        tableModel = new DefaultTableModel(header, result.size());

        String fileName = "";
        int row = -1;

        for(String[] arr: result)
        {
            row++;
            String info = "";
            for (int i=0;i< arr.length;i++)
            {
                if (i == 0)
                {
                    int idx = arr[i].indexOf("@");
                    String domain = arr[i];
                    String file = "";
                    if (idx > 0)
                    {
                        domain = arr[i].substring(0, idx);
                        file = arr[i].substring(idx+1);
                    }
                    if (!file.trim().equals(""))
                    {
                        if (fileName.equals(file)) file = "";
                        else fileName = file;
                    }
                    else
                    {
                        fileName = "";
                        file = "No File";
                    }
                    tableModel.setValueAt(file, row, 0);
                    tableModel.setValueAt(domain, row, 1);
                }
                else if (i == 1) tableModel.setValueAt(arr[i], row, 2);
                else info = info + arr[i];

            }
            tableModel.setValueAt(info, row, 3);
        }



        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Context Domains"));
        //add(tablePanel, BorderLayout.CENTER);
        splitPane.add(tablePanel, JSplitPane.TOP);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        textArea = new JTextArea();
        JScrollPane scroll_lower = new JScrollPane(textArea);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.add(scroll_lower, BorderLayout.CENTER);
        textAreaPanel.setBorder(BorderFactory.createTitledBorder("Content"));
        textArea.setEditable(false);

        splitPane.add(textAreaPanel, JSplitPane.BOTTOM);

        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty())
                {
                    textArea.setText("");
                }
                else
                {

                    displaySelectedLine(lsm.getMinSelectionIndex());
                }
            }
        });
        //---------------------------------------------------------------------------

        this.add(northPanel, BorderLayout.NORTH);
        this.add(splitPane, BorderLayout.CENTER);

        contextSymbol.setText(context);
        contextSymbol.setEditable(false);
        contextAddress.setText(conAddr);
        contextAddress.setEditable(false);



        dialog = this;
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    dialog.dispose();
                }
            }
        );
        this.setSize(500, 600);

    }

    public void displaySelectedLine(int sRow)
	{
        String st = tableModel.getValueAt(sRow, 1).toString();
        textArea.setText(st);
    }
}
