package gov.nih.nci.caadapter.ui.common.preferences;

import gov.nih.nci.caadapter.common.util.Config;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Hl7V3SpecificationPreferencePane extends JPanel 
	implements ActionListener 
	{
	private JDialog parent;
	private JCheckBox nullFlavorCheck;
	private JCheckBox complexDatatypeCheck;
	
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
		JPanel checkboxPanel = new JPanel(new GridLayout(0, 1));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "HL7 Specification"));
        //Create the radio buttons.
        nullFlavorCheck=new JCheckBox("Enable NullFlavor");
        String nullFlavorValue=PreferenceManager.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_NULLFLAVOR_ENABLED);
        if (nullFlavorValue!=null&&nullFlavorValue.equalsIgnoreCase("true"))
        	nullFlavorCheck.setSelected(true);
        
        complexDatatypeCheck=new JCheckBox("Enable Complex Datatype");
        String complexDatatypeValue=PreferenceManager.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_COMPLEXTYPE_ENABLED);
        if (complexDatatypeValue!=null&&complexDatatypeValue.equalsIgnoreCase("true"))
        	complexDatatypeCheck.setSelected(true);
        

        checkboxPanel.add(nullFlavorCheck);
        checkboxPanel.add(complexDatatypeCheck);
		return checkboxPanel;
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
        	PreferenceManager.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_NULLFLAVOR_ENABLED,
        			String.valueOf(nullFlavorCheck.isSelected()));
       		PreferenceManager.savePrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_COMPLEXTYPE_ENABLED, 
       				String.valueOf(complexDatatypeCheck.isSelected()));
       		
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
