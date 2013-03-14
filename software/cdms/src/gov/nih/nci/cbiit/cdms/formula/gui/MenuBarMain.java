/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.gui.action.ExecuteFormulaAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.OpenFormulaStoreAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.SaveFormulaStoreAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.NewFormulaStoreAction;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

 

public class MenuBarMain extends JMenuBar implements ActionListener {

	
	public MenuBarMain(JFrame frame)
	{
		super();
		add(createNewMenu());
		add(createExcuteMenu());
		add(createHelpMenu());
	}
 
	private JMenu createNewMenu()
	{
		JMenu rtnMenu=new JMenu ("File");
		
		JMenuItem newItem=new JMenuItem(new NewFormulaStoreAction("New Formula"));
		rtnMenu.add(newItem);
		
		
		JMenuItem openItem=new JMenuItem(new OpenFormulaStoreAction("Open Formula Store"));
		rtnMenu.add(openItem);
		JMenuItem saveItem=new JMenuItem(new SaveFormulaStoreAction("Save"));
		rtnMenu.add(saveItem);
		JMenuItem saveAsItem=new JMenuItem(new SaveFormulaStoreAction("Save As ... ", false));
		rtnMenu.add(saveAsItem);
		rtnMenu.addSeparator();
		JMenuItem closeItem=new JMenuItem("Close");
//		rtnMenu.add(closeItem);
		JMenuItem exitItem=new JMenuItem("Exit");
		exitItem.addActionListener(this);
		rtnMenu.add(exitItem);
		return rtnMenu;
		
	}
	
	private JMenu createExcuteMenu()
	{
		JMenu rtnMenu=new JMenu ("Run");
		
		JMenuItem excItem=new JMenuItem(new ExecuteFormulaAction("Execute Formula", ExecuteFormulaAction.FORMULA_ACTION_EXECUTION));
		rtnMenu.add(excItem);
		return rtnMenu;
		
	}
	
	private JMenu createHelpMenu()
	{
		JMenu rtnMenu=new JMenu ("Help");
		
		JMenuItem abtItem=new JMenuItem("About Us..");
		rtnMenu.add(abtItem);
		return rtnMenu;
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Component comp=(Component)arg0.getSource();
		if (arg0.getActionCommand().contains("Exit"));
		{
			System.exit(-1);
		}
		

	}
}
