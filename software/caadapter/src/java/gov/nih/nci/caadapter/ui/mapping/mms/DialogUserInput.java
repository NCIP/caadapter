/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.mms;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 * Dialog for adding user's input values
 *
 * @author OWNER: wuye
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-09-28 20:12:19 $
 *
 */
@SuppressWarnings("serial")
public class DialogUserInput extends JDialog implements ActionListener {

	private JComponent inputField;
	private Object userInput;
	private int inputType=1;
	public static int INPUT_TYPE_TEXT=1;
	public static int INPUT_TYPE_SELECTION=2;
	public static int INPUT_TYPE_CHOOSE=3;
	public static int INPUT_TYPE_TABBED=4;

	/**
	 * @author OWNER: Ye Wu
	 * @author LAST UPDATE $Author: wangeug $
	 * @version Since caAdapter v3.2 revision $Revision: 1.3 $
	 */

	public DialogUserInput(final JFrame _callingFrame, final Object  DefaultObject) {
		this(_callingFrame, DefaultObject, "Constant");
	}
	public DialogUserInput(final JFrame _callingFrame, final Object  DefaultObject, String titleParameter) {
		this(_callingFrame, DefaultObject, titleParameter, DialogUserInput.INPUT_TYPE_TEXT );
	}
	public DialogUserInput(final JFrame _callingFrame, final Object  DefaultObject, String titleParameter, int userType) {
		this(_callingFrame, DefaultObject, null,titleParameter, DialogUserInput.INPUT_TYPE_TEXT );
	}
	    public DialogUserInput(final JFrame _callingFrame, final Object  DefaultObject, Vector<Object> chooseObjects, String titleParameter, int userType) {
	        super(_callingFrame, true);
	        inputType=userType;
	        this.setSize(450, 300);
	        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        if (inputType==INPUT_TYPE_CHOOSE)
	        	this.setTitle(titleParameter);
	        else
	        	this.setTitle("Define "+titleParameter);

	        this.setAlwaysOnTop(true);
	        this.setResizable(false);
            JPanel mainPanel = new JPanel();
	        mainPanel.setLayout(new BorderLayout());

	        JPanel centerPan = new JPanel(new GridLayout(1, 3));
	        boolean packView=true;
	        //add left label if it not tabbedPane
	        if (inputType!=INPUT_TYPE_TABBED)
	        	centerPan.add(new JLabel(titleParameter+" :"));

	        if (inputType==INPUT_TYPE_TEXT)
	        {
	        	centerPan.setBorder(new TitledBorder("Input "));
	        	inputField=new JTextField();
	        	((JTextField)inputField).setText((String)DefaultObject);
	        	inputField.setEnabled(true);
	        }
	        else  if (inputType==INPUT_TYPE_SELECTION)
	        {
	        	centerPan.setBorder(new TitledBorder("Selection"));
	        	inputField=new PanelCacadeSetting((String)DefaultObject);
	        }
	        else  if (inputType==INPUT_TYPE_CHOOSE)
	        {
	        	centerPan.setBorder(new TitledBorder("Choose"));
	        	inputField=new JComboBox(chooseObjects);
	        	if (DefaultObject!=null)
	        		((JComboBox)inputField).setSelectedItem(DefaultObject);
	        }
	        else  if (inputType==INPUT_TYPE_TABBED)
	        {
	        	inputField=initPKGeneratorSelection(DefaultObject);
	        	if (((JTabbedPane)inputField).getTabCount()==0)
	        		packView=false;
	        }
	        else
	        	inputField=new JLabel("Undefined Input Type");
        	centerPan.add(inputField);

	        JPanel butPan = new JPanel();
	        butPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	        if (inputType==INPUT_TYPE_TABBED)
	        {
	        	JButton addPKGenerator=new JButton("Add");
	        	addPKGenerator.addActionListener(this);
	        	butPan.add(addPKGenerator);
	        }
	        JButton okBut = new JButton("OK");
	        okBut.addActionListener(this);
	        JButton canBut = new JButton("Cancel");
	        canBut.addActionListener(this);
	        butPan.add(okBut);
	        butPan.add(canBut);

	        mainPanel.add(centerPan, BorderLayout.CENTER);
	        mainPanel.add(butPan, BorderLayout.SOUTH);

	        this.add(mainPanel);
	        if(packView)
	        	this.pack();
	        DefaultSettings.centerWindow(this);
	        this.setVisible(true);
	    }

	private JTabbedPane initPKGeneratorSelection(Object controlObject)
	{
		JTabbedPane rtnPan=new JTabbedPane();
		HashMap<String, HashMap<String, String>> pkSettings=(HashMap<String, HashMap<String,String>>)controlObject;
		Iterator<String> dbList=(Iterator<String>)pkSettings.keySet().iterator();
		while (dbList.hasNext())
		{
			String dbName=dbList.next();
			rtnPan.addTab(dbName, new PanelPrimaryKeyGenerator(pkSettings.get(dbName)));
		}
		return rtnPan;
	}
	    /**
		 * @return the userInput
		 */
		public Object getUserInput() {
			return userInput;
		}
		/**
		 * @param userInput the userInput to set
		 */
		public void setUserInput(Object userInput) {
			this.userInput = userInput;
		}
		public void actionPerformed(ActionEvent e) {
	    	System.out.println("DialogUserInput.actionPerformed()..:"+e.getActionCommand());
	    	String cmdValue=e.getActionCommand();
	    	if (cmdValue!=null&&cmdValue.equalsIgnoreCase("OK"))
	    	{
	    		if (inputType==INPUT_TYPE_TEXT)
	    			this.setUserInput(((JTextField)inputField).getText());
	    		else if (inputType==INPUT_TYPE_SELECTION)
	    			this.setUserInput(((PanelCacadeSetting)inputField).getUserSelection());
	    		else if (inputType==INPUT_TYPE_CHOOSE)
	    			this.setUserInput(((JComboBox)inputField).getSelectedItem().toString());
	    		else if (inputType==INPUT_TYPE_TABBED)
	    		{
	    			JTabbedPane pkgSettingTab=(JTabbedPane)inputField;
	    			HashMap<String, HashMap<String, String>> pkSettingInputs=new HashMap<String, HashMap<String,String>>();
					for (int tbInx=0; tbInx<pkgSettingTab.getTabCount(); tbInx++)
					{
						String dbName=pkgSettingTab.getTitleAt(tbInx);
						System.out.println("DialogUserInput.actionPerformed()..db:"+dbName+"..index:"+tbInx);
						HashMap<String, String> oneDbSetting=((PanelPrimaryKeyGenerator)pkgSettingTab.getComponentAt(tbInx)).getUserInputs();
								//.getTabComponentAt(tbInx)).getUserInputs();
						pkSettingInputs.put(dbName, oneDbSetting);
					}
					this.setUserInput(pkSettingInputs);
	    		}

	    	}
	    	else if(cmdValue!=null&&cmdValue.equalsIgnoreCase("Add"))
	    	{
	    		Vector<String> defDb=new Vector<String>();
	    		defDb.add("dbName");
	    		DialogUserInput dialogUserInput = new DialogUserInput(null, defDb, "Database Name",DialogUserInput.INPUT_TYPE_TEXT );
				if (dialogUserInput.getUserInput()!=null)
				{
					System.out.println("DialogUserInput.actionPerformed()..select database name:"+dialogUserInput.getUserInput());
					//check if the input DB generator exist
					String dbInputName=(String)dialogUserInput.getUserInput();
					JTabbedPane pkgSettings=(JTabbedPane)inputField;
					for (int tbInx=0; tbInx<pkgSettings.getTabCount(); tbInx++)
					{
						if (dbInputName.equalsIgnoreCase(pkgSettings.getTitleAt(tbInx)))
						{

							JOptionPane.showMessageDialog(this, "Primary key generator exists for database:"+dbInputName +",\nPlease set another database name !", "Duplicate Database Name", 1);
							return;
						}
					}

					pkgSettings.addTab(dbInputName, new PanelPrimaryKeyGenerator(new HashMap<String,String>()));
					//leave the setting panel visible
					pkgSettings.setSelectedIndex(pkgSettings.getTabCount()-1);
					//pack the tabbed pane of multiple PK generators
					this.pack();
					return;
		        }
	    	}
	    	else
	    		this.setUserInput(null);

	    	this.setVisible(false);
	    	dispose();
	    	return;
	    }
	}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2009/09/28 20:09:53  wangeug
 * HISTORY: fix bug: display empty PK generator window
 * HISTORY:
 * HISTORY: Revision 1.1  2009/07/30 17:37:44  wangeug
 * HISTORY: clean codes: implement 4.1.1 requirements
 * HISTORY:
 * HISTORY: Revision 1.8  2009/06/12 15:53:49  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.7  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
