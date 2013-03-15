/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.ui.specification.csv.actions.AddSegmentAction;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 11, 2007
 *          Time:       12:31:44 PM $
 */
public class CSVSegmentDefinitionDialog extends JDialog implements ActionListener, KeyListener {

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CSVSegmentDefinitionDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVSegmentDefinitionDialog.java,v 1.3 2008-06-09 19:54:07 phadkes Exp $";

    private static final String SEGMENT_NAME_LABEL = "Segment Name:";
	private static final String CARDINALITY_TYPE_LABEL = "Cardinalty Type:";
    private static final String CHOICE_SEGMENT_NAME_LABEL = "Choice Segment Name:";
	private static final String CHOICE_CARDINALITY_TYPE_LABEL = "Choice Cardinalty Type:";

    private JLabel segmentNameLabel;
	private JTextField segmentNameTextField;
	private JLabel cardinalityTypeLabel;
	private JComboBox cardinalityTypeField;

    private JButton okButton;
    private JButton cancelButton;

    private boolean okButtonClicked = false;

    private String segmentName = "";
    private String cardinality = "";

    private boolean cardinalityEditable;

    private String title;

    /**
     * Creates a non-modal dialog without a title with the
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
    public CSVSegmentDefinitionDialog(Frame owner, String title, boolean editable) throws HeadlessException
    {
        super(owner, title, true);
        this.title = title;
        cardinalityEditable = editable;
        //initialize();
    }

    /**
     * Creates a non-modal dialog without a title with the
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

    public CSVSegmentDefinitionDialog(Dialog owner, String title, boolean editable) throws HeadlessException
    {
        super(owner, title, true);
        this.title = title;
        cardinalityEditable = editable;
        //initialize();
    }

    private void constructCardinalityTypeComboBox()
	{
		cardinalityTypeField.removeAllItems();
        java.util.Enumeration enumer = CardinalityType.enumerate();

        if (title.equals(AddSegmentAction.COMMAND_NAME_GENERAL))
        {
            while(enumer.hasMoreElements())
            {
                CardinalityType cardinality = (CardinalityType) enumer.nextElement();
                int idx = cardinality.toString().indexOf(Config.SUFFIX_OF_CHOICE_CARDINALITY);
                if (idx < 0) cardinalityTypeField.addItem(cardinality.toString());
            }
            //cardinalityTypeField.setSelectedItem((new C_segment()).getCardinality().toString());
        }
        else if (title.equals(AddSegmentAction.COMMAND_NAME_CHOICE))
        {
            String defaultChoiceCardinalityString = "";
            while(enumer.hasMoreElements())
            {
                CardinalityType cardinality = (CardinalityType) enumer.nextElement();
                int idx = cardinality.toString().indexOf(Config.SUFFIX_OF_CHOICE_CARDINALITY);
                if (idx >= 0) cardinalityTypeField.addItem(cardinality.toString());
                defaultChoiceCardinalityString = cardinality.toString();
            }
            //cardinalityTypeField.setSelectedItem(defaultChoiceCardinalityString);
        }
    }

	private void initialize()
	{
		this.setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel(new GridBagLayout());

        if (title.equals(AddSegmentAction.COMMAND_NAME_GENERAL))
        {
            segmentNameLabel = new JLabel(SEGMENT_NAME_LABEL);
            cardinalityTypeLabel = new JLabel(CARDINALITY_TYPE_LABEL);
        }
        else if (title.equals(AddSegmentAction.COMMAND_NAME_CHOICE))
        {
            segmentNameLabel = new JLabel(CHOICE_SEGMENT_NAME_LABEL);
            cardinalityTypeLabel = new JLabel(CHOICE_CARDINALITY_TYPE_LABEL);
        }
        else
        {
            segmentNameLabel = new JLabel(SEGMENT_NAME_LABEL);
            cardinalityTypeLabel = new JLabel(CARDINALITY_TYPE_LABEL);
        }

        segmentNameTextField = new JTextField();
        segmentNameTextField.addKeyListener(this);

        cardinalityTypeField = new JComboBox();
        constructCardinalityTypeComboBox();
        if (!cardinalityEditable)
        {
            //cardinalityTypeField.setSelectedItem(CardinalityType.VALUE_1);
            cardinalityTypeField.setEditable(false);
            cardinalityTypeField.setEnabled(false);
        }

        Dimension segmentSize = segmentNameLabel.getPreferredSize();
		Dimension cardinalityTypeSize = cardinalityTypeLabel.getPreferredSize();

		int textFieldWidth = Math.max(segmentSize.width, cardinalityTypeSize.width) + 6;
		int textFieldHeight = Math.max(segmentSize.height, cardinalityTypeSize.height) + 6;

		segmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
		cardinalityTypeField.setPreferredSize(new Dimension(textFieldWidth + 2, textFieldHeight + 2));

		Insets insets = new Insets(5, 5, 5, 5);
		centerPanel.add(segmentNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(segmentNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		//centerPanel.add(fieldNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		//		GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		//centerPanel.add(fieldNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
		//		GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(cardinalityTypeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(cardinalityTypeField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		this.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        okButton = new JButton("  OK  ");
        //okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addKeyListener(this);
        okButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        this.setSize(300, 150);
    }

   public void keyPressed(KeyEvent evt)
   {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            cardinality = (String)cardinalityTypeField.getSelectedItem();
            segmentName = segmentNameTextField.getText();

            if ((segmentName == null)||(segmentName.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "Segment Name is not input.", "Empty Segment Name", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((cardinality == null)||(cardinality.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "Cardinality is not input.", "Empty Cardinality", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean c_Check = false;
            java.util.Enumeration enumer = CardinalityType.enumerate();

            while(enumer.hasMoreElements())
            {
                CardinalityType cardinalityA = (CardinalityType) enumer.nextElement();
                if (cardinality.equals(cardinalityA.toString())) c_Check = true;
            }
            if (!c_Check)
            {
                JOptionPane.showMessageDialog(this, "Cardinality value is invalid : " + cardinality, "Invalid Cardinality", JOptionPane.ERROR_MESSAGE);
                return;
            }
            cardinality = cardinality.trim();
            segmentName = segmentName.trim();
            okButtonClicked = true;

            this.dispose();
        }
   }
   public void keyReleased(KeyEvent evt) { }
   public void keyTyped(KeyEvent evt) { }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            cardinality = (String)cardinalityTypeField.getSelectedItem();
            segmentName = segmentNameTextField.getText();

            if ((segmentName == null)||(segmentName.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "Segment Name is not input.", "Empty Segment Name", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((cardinality == null)||(cardinality.trim().equals("")))
            {
                JOptionPane.showMessageDialog(this, "Cardinality is not input.", "Empty Cardinality", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean c_Check = false;
            java.util.Enumeration enumer = CardinalityType.enumerate();

            while(enumer.hasMoreElements())
            {
                CardinalityType cardinalityA = (CardinalityType) enumer.nextElement();
                if (cardinality.equals(cardinalityA.toString())) c_Check = true;
            }
            if (!c_Check)
            {
                JOptionPane.showMessageDialog(this, "Cardinality value is invalid : " + cardinality, "Invalid Cardinality", JOptionPane.ERROR_MESSAGE);
                return;
            }
            cardinality = cardinality.trim();
            segmentName = segmentName.trim();
            okButtonClicked = true;
        }

        this.dispose();
    }

    public boolean isOkButtonClicked()
    {
        return okButtonClicked;
    }

    public String getSegmentName()
	{
		return segmentName;
	}

	public void setSegmentName(String newValue)
	{
		segmentNameTextField.setText(newValue);
        segmentName = newValue;
	}


	public String getCardinality()
	{
		return cardinality;
	}

//	public void setCardinalityType(int newValue)
//	{
//		Integer newValueInt = new Integer(newValue);
//		setCardinalityType(newValueInt);
//	}

//	public void setCardinalityType(String newValue)
//	{
//		//Integer newValueInt = new Integer(newValue);
//		setCardinalityType(newValue);
//	}

	public void setCardinality(String newValue)
	{
        if (newValue.endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY)) title = AddSegmentAction.COMMAND_NAME_CHOICE;
        else title = AddSegmentAction.COMMAND_NAME_GENERAL;
        initialize();
        cardinalityTypeField.setSelectedItem(newValue);
        cardinality = newValue;
    }
    public void setSingleCardinality(String newValue)
	{
        initialize();
        cardinalityTypeField.removeAllItems();
        cardinalityTypeField.addItem(newValue);
        cardinalityTypeField.setSelectedItem(newValue);
        cardinality = newValue;
    }
    public void setDefaultCardinality()
	{
        initialize();
        String newValue = "";
        if (title.equals(AddSegmentAction.COMMAND_NAME_GENERAL))
        {
            newValue = (new C_segment()).getCardinality().toString();
        }
        else if (title.equals(AddSegmentAction.COMMAND_NAME_CHOICE))
        {
            newValue = Config.CHOICE_CARDINALITY_ONE_TO_ONE;
        }
        cardinalityTypeField.setSelectedItem(newValue);
        cardinality = newValue;
    }




}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/10/05 20:27:39  schroedn
 * HISTORY      : Now can pressed [ENTER] and it works the same as clicking OK button
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/12 15:48:37  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */
