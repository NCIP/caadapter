/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.hl7;
/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 22, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-23 18:20:55 $
 * @since caAdapter v4.2
 */

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

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class TransformationSourceSelectionPanel extends JPanel
	implements ActionListener
	{
	private TransformationSourceSelectionDialog parent;

	private ButtonGroup sourceTypeGroup;

	public TransformationSourceSelectionPanel(TransformationSourceSelectionDialog parentDialog)
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

		sourceTypeGroup = new ButtonGroup();
        checkboxPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "Source Data Type"));
        //Create the radio buttons.
        JRadioButton scsCheck=new JRadioButton(TransformationSourceSelectionDialog.SOURCE_TYPE_CSV);
        scsCheck.setSelected(true);

        JRadioButton v2messageChcke=new JRadioButton(TransformationSourceSelectionDialog.SOURCE_TYPE_V2);
        v2messageChcke.setSelected(false);
        sourceTypeGroup.add(scsCheck);
        sourceTypeGroup.add(v2messageChcke);

        GridBagConstraints ckx = new GridBagConstraints();
        ckx.gridy=0;
        ckx.weightx=1.0;
        ckx.gridwidth=1;
        ckx.fill = GridBagConstraints.BOTH;

        ckxGridbag.setConstraints(scsCheck, ckx);
        checkboxPanel.add(scsCheck);

        ckx.gridy=1;
        ckxGridbag.setConstraints(v2messageChcke, ckx);
        checkboxPanel.add(v2messageChcke);



		JPanel rtnPane = new JPanel(new GridLayout(0, 1));
		rtnPane.setBorder(BorderFactory.createTitledBorder(loweredetched,""));
		rtnPane.add(checkboxPanel);
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
        	String typeSelected=null;
        	Enumeration grpButtons=sourceTypeGroup.getElements();
        	while(grpButtons.hasMoreElements())
       		{
       			JRadioButton rb=(JRadioButton)grpButtons.nextElement();
       			if (rb.isSelected())
       			{
       				typeSelected=rb.getActionCommand();
       				break;
       			}
       		}
        	parent.setSourceDatatype(typeSelected);
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
* HISTORY: $Log: not supported by cvs2svn $
**/
