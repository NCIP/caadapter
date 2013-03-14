/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.mms;

import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 29, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-30 17:37:44 $
 * @since caAdapter v4.2
 */

public class PanelPrimaryKeyGenerator extends JPanel {

	private HashMap<String, String> parameters;
	private JTextField gName;
	private JTextField gProperty1;
	private JTextField gProperty2;
	private JTextField gProperty3;

	public PanelPrimaryKeyGenerator(HashMap<String, String> pkgSetting)
	{
		super();
		parameters=pkgSetting;
		initUI();
	}

	private void initUI()
	{
		this.setLayout(new GridLayout(4,2));
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.setSize(450, 300);
		this.add(new JLabel("Generator Name:"));
		gName=new JTextField(parameters.get("NCI_GENERATOR"));
		this.add(gName);

		this.add(new JLabel("Property One:"));
		gProperty1=new JTextField(parameters.get("NCI_GENERATOR_PROPERTY1"));
		this.add(gProperty1);

		this.add(new JLabel("Property Two:"));
		gProperty2=new JTextField(parameters.get("NCI_GENERATOR_PROPERTY2"));
		this.add(gProperty2);

		this.add(new JLabel("Property Three:"));
		gProperty3=new JTextField(parameters.get("NCI_GENERATOR_PROPERTY3"));
		this.add(gProperty3);
	}

	public HashMap<String, String> getUserInputs()
	{
		parameters.put("NCI_GENERATOR", gName.getText());
		parameters.put("NCI_GENERATOR_PROPERTY1", gProperty1.getText());
		parameters.put("NCI_GENERATOR_PROPERTY2", gProperty2.getText());
		parameters.put("NCI_GENERATOR_PROPERTY3", gProperty3.getText());

		return parameters;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/