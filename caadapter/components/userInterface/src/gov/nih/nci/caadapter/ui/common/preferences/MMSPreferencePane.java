/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.common.preferences;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class MMSPreferencePane extends JPanel
	implements ActionListener
	{
	private JDialog parent;
    private JLabel mmsPrefixLabelObjectModel;
    private JLabel mmsPrefixLabelDataModel;
    private JTextField mmsPrefixObjectModel;
    private JTextField mmsPrefixDataModel;

    public MMSPreferencePane(JDialog parentDialog)
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
		JPanel checkboxPanel = new JPanel(new FlowLayout() );
        checkboxPanel.setBorder(BorderFactory.createTitledBorder(loweredetched, "MMS Prefix"));

        //TODO: IF BLANK, Default to Logical View.Logical Model / Logical View.Data Model

        mmsPrefixLabelObjectModel = new JLabel( "MMS Prefix 1 (Logical View.Logical Model) " );
        mmsPrefixObjectModel = new JTextField( 25 );
        if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) == null )
        {
            mmsPrefixObjectModel.setText( "Logical View.Logical Model" );
        }else {
            mmsPrefixObjectModel.setText( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) );
        }

        mmsPrefixLabelDataModel = new JLabel( "MMS Prefix 2 (Logical View.Data Model) " );
        mmsPrefixDataModel = new JTextField( 25 );
        if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) == null ){
          mmsPrefixDataModel.setText( "Logical View.Data Model" );
        } else {
            mmsPrefixDataModel.setText( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) );
        }


        checkboxPanel.add( mmsPrefixLabelObjectModel );
        checkboxPanel.add( mmsPrefixObjectModel );

        checkboxPanel.add( mmsPrefixLabelDataModel );
        checkboxPanel.add( mmsPrefixDataModel );

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
	    //handle 'ok' and 'cancel'
        if (arg0.getActionCommand().equalsIgnoreCase("ok"))
        {
         	//persistent preference as "true" or "false"
        	CaadapterUtil.savePrefParams( Config.MMS_PREFIX_OBJECTMODEL, mmsPrefixObjectModel.getText() );

        	CaadapterUtil.savePrefParams( Config.MMS_PREFIX_DATAMODEL, mmsPrefixDataModel.getText() );

            if (parent!=null)
        		parent.dispose();
        }
        else if (arg0.getActionCommand().equalsIgnoreCase("Cancel"))
        {
        	if (parent!=null)
        		parent.dispose();
        }

        //System.out.println( "MMS_Prefix = " + (PreferenceManager.readPrefParams( Config.MMS_PREFIX_DATAMODEL  ) + ".").length() );
    }
}
