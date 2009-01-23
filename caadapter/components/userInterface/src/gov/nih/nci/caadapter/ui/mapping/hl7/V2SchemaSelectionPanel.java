/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.hl7;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 21, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-23 18:22:00 $
 * @since caAdapter v4.2
 */

public class V2SchemaSelectionPanel extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: V2SchemaSelectionPanel.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/V2SchemaSelectionPanel.java,v 1.1 2009-01-23 18:22:00 wangeug Exp $";

	private static final String HL7_MESSAGE_CATEGORY_LABEL = "Select an HL7 V2 Message Version:";
	private static final String HL7_MESSAGE_TYPE_LABEL = "Select an HL7 V2 Message Schema:";
	
	
	private JComboBox hl7MessageTypeComboBox;
	private V2MessageIndex v2MessageIndex;
	private JComboBox hl7MessageCategoryComboBox;
    private String errorMessage = "";
    /**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public V2SchemaSelectionPanel(V2SchemaSelectionDialog wizard)
	{
		super();
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		centerPanel.add(new JLabel(HL7_MESSAGE_CATEGORY_LABEL), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(new JLabel(HL7_MESSAGE_TYPE_LABEL), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		try {
			v2MessageIndex =V2MessageSchemaIndexLoader.loadMessageInfos();
			hl7MessageTypeComboBox=new JComboBox();
			hl7MessageTypeComboBox.setEnabled(false);
			hl7MessageCategoryComboBox = new JComboBox(v2MessageIndex.getMessageCategory().toArray());
			hl7MessageCategoryComboBox.addActionListener(this);
			hl7MessageCategoryComboBox.setSelectedIndex(-1);
			
		} catch (Exception e) {  
            errorMessage = e.getMessage();
            return;
        }
		
		centerPanel.add(hl7MessageCategoryComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(hl7MessageTypeComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		this.add(centerPanel);
 
        errorMessage = "";
    }

 
    public String getErrorMessage()
    {
        return errorMessage;
    }
 
	public String getUserSelectedMessageVersion ()
	{
		String slctMsgType= (String)hl7MessageCategoryComboBox.getSelectedItem().toString();	
		return slctMsgType;
	}

 
	public String getUserSelectedMessageSchema ()
	{
		String slctMsgType= (String)hl7MessageTypeComboBox.getSelectedItem().toString();	
		return slctMsgType;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==hl7MessageCategoryComboBox)
		{
			hl7MessageTypeComboBox.removeAllItems();
			String slctMsgCat=(String)hl7MessageCategoryComboBox.getSelectedItem();
			if (slctMsgCat==null)
				return;
			hl7MessageTypeComboBox.setEnabled(true);
			Set<String> msgTypes=v2MessageIndex.fingMessageTypesWithCategory(slctMsgCat);
			if (!msgTypes.isEmpty())
			{
				for(Object msgOneType:msgTypes.toArray())
					hl7MessageTypeComboBox.addItem(msgOneType);
			}
		}
 
	}
}

/**
* HISTORY: $Log: not supported by cvs2svn $
**/