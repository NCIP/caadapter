/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.preferences;
/* Hl7V3SpecificationPreferencePane
 *  * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.8 $
 * @date       $Date: 2008-09-24 17:53:33 $
 */

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Hl7V3SpecificationPreferencePane extends JPanel
	implements ActionListener
	{
	private JDialog parent;
	private JCheckBox nullFlavorCheck;
	private JCheckBox complexDatatypeCheck;
	private JCheckBox oidEnableCheck;

	private ButtonGroup validationLevelGroup;
	public Hl7V3SpecificationPreferencePane(JDialog parentDialog)
	{
		super();
		parent=parentDialog;
		initUI();
	}

	private void initUI()
	{
        setLayout(new BorderLayout());
        add(setSelectionPane(), BorderLayout.CENTER);
        add(setButtonPane(), BorderLayout.SOUTH);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private JPanel setSelectionPane()
	{
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		JPanel checkboxPanel = new JPanel();
		GridBagLayout ckxGridbag = new GridBagLayout();
		checkboxPanel.setLayout(ckxGridbag);

        checkboxPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "HL7 Specification"));
        //Create the radio buttons.
        nullFlavorCheck=new JCheckBox("Enable NullFlavor");
        String nullFlavorValue=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_NULLFLAVOR_ENABLED);
        if (nullFlavorValue!=null&&nullFlavorValue.equalsIgnoreCase("true"))
        	nullFlavorCheck.setSelected(true);

        complexDatatypeCheck=new JCheckBox("Enable Complex Datatype");
        String complexDatatypeValue=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_COMPLEXTYPE_ENABLED);
        if (complexDatatypeValue!=null&&complexDatatypeValue.equalsIgnoreCase("true"))
        	complexDatatypeCheck.setSelected(true);

        oidEnableCheck= new JCheckBox("Enable OID");
        String odiEnableValue=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ODI_ENABLED);
        if (odiEnableValue!=null&&odiEnableValue.equalsIgnoreCase("true"))
        	oidEnableCheck.setSelected(true);

        GridBagConstraints ckx = new GridBagConstraints();
        ckx.gridy=0;
        ckx.weightx=1.0;
        ckx.gridwidth=1;
        ckx.fill = GridBagConstraints.BOTH;

        ckxGridbag.setConstraints(nullFlavorCheck, ckx);
        checkboxPanel.add(nullFlavorCheck);

        ckx.gridx=1;
        ckxGridbag.setConstraints(complexDatatypeCheck, ckx);
        checkboxPanel.add(complexDatatypeCheck);

        ckx.gridwidth=2;//
        ckx.gridy=1;
        ckx.gridx=0;
        ckxGridbag.setConstraints(oidEnableCheck, ckx);
        checkboxPanel.add(oidEnableCheck);

        JPanel groupSelectPane=new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        groupSelectPane.setLayout(gridbag);

        groupSelectPane.setBorder(BorderFactory.createTitledBorder(loweredetched, "Message Validation"));
        String validationLevel=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_VALIDATION_LEVEL);
        JRadioButton valid0= new JRadioButton("Structure");
        valid0.setActionCommand(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_0);
        JRadioButton valid1= new JRadioButton("Structure & Vocabulary");
        valid1.setActionCommand(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_1);
        JRadioButton valid2= new JRadioButton("Structure, Vocabulary & Schema(xsd)");
        valid2.setActionCommand(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_2);

        //set default selection
        if (validationLevel==null)
        	valid0.setSelected(true);
        else if (validationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_1))
        	valid1.setSelected(true);
        else if (validationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_2))
        	valid2.setSelected(true);
        else
        	valid0.setSelected(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy=0;
        c.weightx=1.0;
        c.gridwidth=1;
        c.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(valid0, c);
        groupSelectPane.add(valid0);

        c.gridx=1;
        gridbag.setConstraints(valid1, c);
        groupSelectPane.add(valid1);

        c.gridwidth=2;//
        c.gridy=1;
        c.gridx=0;
        c.weightx=GridBagConstraints.WEST;
        gridbag.setConstraints(valid2, c);
        groupSelectPane.add(valid2);

        validationLevelGroup = new ButtonGroup();
        validationLevelGroup.add(valid0);
        validationLevelGroup.add(valid1);
        validationLevelGroup.add(valid2);


		JPanel rtnPane = new JPanel(new GridLayout(0, 1));
		rtnPane.setBorder(BorderFactory.createTitledBorder(loweredetched,""));
		rtnPane.add(checkboxPanel);
		rtnPane.add(groupSelectPane);
		return rtnPane;
	}

	private JPanel setButtonPane()
	{
		JPanel buttonPanel = new JPanel();
	    buttonPanel.setBorder(BorderFactory.createEtchedBorder());
	    buttonPanel.setLayout(new FlowLayout());
	    JButton okBut = new JButton("OK");
	    okBut.addActionListener(this);
	    JButton canBut = new JButton("Cancel");
	    canBut.addActionListener(this);
	    buttonPanel.add(okBut);
	    buttonPanel.add(canBut);
		return buttonPanel;
	}
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	    //handle 'ok' and 'cancel'
        if (arg0.getActionCommand().equalsIgnoreCase("ok"))
        {
         	//persistent preference as "true" or "false"
        	CaadapterUtil.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_NULLFLAVOR_ENABLED,
        			String.valueOf(nullFlavorCheck.isSelected()));
        	CaadapterUtil.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_COMPLEXTYPE_ENABLED,
       				String.valueOf(complexDatatypeCheck.isSelected()));

        	CaadapterUtil.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ODI_ENABLED,
       				String.valueOf(oidEnableCheck.isSelected()));
       		//read validation level from group selection
       		Enumeration grpButtons=validationLevelGroup.getElements();
       		String validationLevel=CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_0;
       		while(grpButtons.hasMoreElements())
       		{
       			JRadioButton rb=(JRadioButton)grpButtons.nextElement();
       			if (rb.isSelected())
       			{
       				validationLevel=rb.getActionCommand();
       				break;
       			}
       		}
       		CaadapterUtil.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_VALIDATION_LEVEL,
       				validationLevel);

        	if (parent!=null)
        		parent.dispose();
        }
        else if (arg0.getActionCommand().equalsIgnoreCase("Cancel"))
        {
        	if (parent!=null)
        		parent.dispose();
        }
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
