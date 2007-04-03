/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/context/ContextManagerClientReloadDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.context;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JDialog;

import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This class defines the dialog box to display the list of affected context client and
 * support the reload method.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
class ContextManagerClientReloadDialog extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ContextManagerClientReloadDialog.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/context/ContextManagerClientReloadDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

	private static final String RELOAD_ALL_COMMAND = "Reload All";
	private static final String RELOAD_ALL_COMMAND_MNEMONIC = "A";
	private static final String RELOAD_NONE_COMMAND = "Reload None";
	private static final String RELOAD_NONE_COMMAND_MNEMONIC = "N";

	private static final String DEFAULT_TITLE = "Reload Confirmation Dialog";
	private static final String DEFAULT_TABLE_COLUMN_TITLE = "Panel Name";

	private JButton reloadAllButton;
	private JButton reloadNoneButton;
	private JTable contextClientTable;
	private JScrollPane scrollPane;

	private boolean reloadAllClicked = false;


	private Map<String,ContextManagerClient> contextClientMap;

	/**
	 * Creates a modal dialog with a default title with the
	 * specified <code>Frame</code> as its owner.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ContextManagerClientReloadDialog(Frame owner, Map<String, ContextManagerClient> contextClientMap)
			throws HeadlessException
	{
		super(owner, DEFAULT_TITLE, true);
		this.contextClientMap = contextClientMap;
		initialize();
	}

	/**
	 * Creates a modal dialog with a default title with the
	 * specified <code>Dialog</code> as its owner.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ContextManagerClientReloadDialog(Dialog owner, Map<String, ContextManagerClient> contextClientMap)
			throws HeadlessException
	{
		super(owner, DEFAULT_TITLE, true);
		this.contextClientMap = contextClientMap;
		initialize();
	}

	private void initialize()
	{
		reloadAllClicked = false;
		setLayout(new BorderLayout());

		JTextArea textArea = new JTextArea("Warning: if you do not reload all the latest changes in affected panels, it may result in inconsistent data lingering within those associated panels.");
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setPreferredSize(new Dimension(580, 60));
		textArea.setEditable(false);
		textArea.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		this.add(textArea, BorderLayout.NORTH);

		//according to JTable, rowData vector shall be a vector of a vector that contains the single row of data.
		Vector rowData = new Vector();
		Iterator it = this.contextClientMap.keySet().iterator();
		while(it.hasNext())
		{
			Vector oneRowData = new Vector();
			oneRowData.add(it.next());
			rowData.add(oneRowData);
		}
		Vector columnNames = new Vector();
		columnNames.add(DEFAULT_TABLE_COLUMN_TITLE);
		contextClientTable = new JTable(rowData, columnNames);
		scrollPane = new JScrollPane(contextClientTable);
		this.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		reloadAllButton = new JButton(RELOAD_ALL_COMMAND);
		reloadAllButton.setMnemonic(RELOAD_ALL_COMMAND_MNEMONIC.charAt(0));
		reloadAllButton.addActionListener(this);
		reloadNoneButton = new JButton(RELOAD_NONE_COMMAND);
		reloadNoneButton.setMnemonic(RELOAD_NONE_COMMAND.charAt(0));
		reloadNoneButton.addActionListener(this);
		buttonPanel.add(reloadAllButton);
		buttonPanel.add(reloadNoneButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setSize(600, 450);
	}

	public boolean isReloadAllClicked()
	{
		return reloadAllClicked;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if(RELOAD_ALL_COMMAND.equals(command))
		{
			reloadAllClicked = true;
			java.util.List<Throwable> throwableList = new ArrayList<Throwable>();
			Iterator it = this.contextClientMap.keySet().iterator();
			while(it.hasNext())
			{
				ContextManagerClient localClient = this.contextClientMap.get(it.next());
				try
				{
				localClient.reload();
				}
				catch(Throwable t)
				{
					throwableList.add(t);
				}
			}
			if(throwableList.size()>0)
			{
				int size = throwableList.size();
				for(int i=0; i<size; i++)
				{
					Log.logException(this.getClass(), throwableList.get(i));
				}
			}

		}
		else if(RELOAD_NONE_COMMAND.equals(command))
		{
			JOptionPane.showMessageDialog(this, "Those panels will not be reloaded.");
		}

		this.setVisible(false);
		this.dispose();
	}

//	public static void main(String[] args)
//	{
//		java.io.File file1 = new java.io.File("file.txt");
//		java.io.File file2 = new java.io.File("file.txt");
//		Log.logInfo(this, "file1.hashCode()==file2.hashCode()?'" + (file1.hashCode()==file2.hashCode()) + "'");
//		Log.logInfo(this, "file1.equals(file2)?'" + file1.equals(file2) + "'");
//
////		HashMap<String,ContextManagerClient> map = new HashMap<String,ContextManagerClient>();
////		map.put("HL7MappingPanel", new gov.nih.nci.caadapter.ui.main.map.MappingPanel());
////		ContextManagerClientReloadDialog worker = new ContextManagerClientReloadDialog(new JFrame(), map);
////		gov.nih.nci.caadapter.ui.main.util.DefaultSettings.centerWindow(worker);
////		worker.setVisible(true);
//	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/30 20:45:47  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/30 19:53:32  jiangsc
 * HISTORY      : minor arrangement
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/18 21:04:38  jiangsc
 * HISTORY      : Save point of the synchronization effort.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/18 15:30:16  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 */
