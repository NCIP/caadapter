/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.function;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionConstantEditionDialog extends JDialog implements ActionListener
{
	//private static final String TITLE = "Function Constant Definition";
	private static final Map typeMap = new TreeMap();

	static
	{
		typeMap.put("Integer", java.lang.Integer.class);
		typeMap.put("String", java.lang.String.class);
	}

	private JLabel typeLabel = new JLabel("Type:");
	private JComboBox typeField;
	private JLabel valueLabel;
	private JTextField valueField = new JTextField();

	private static final String OK_COMMAND = "OK";
	private static final String OK_COMMAND_MNENOMIC = "O";
	private static final String CANCEL_COMMAND = "Cancel";
	private static final String CANCEL_COMMAND_MNENOMIC = "C";
	private JPanel centerPanel;

	private JButton okButton;
	private JButton cancelButton;
	private JPanel buttonPanel;

	private boolean okButtonClicked;

	private Class constantTypeClass;
	private String constantValue;
    private String functionName;

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
     * @param fName the non-null <code>functionName</code> the name of the target function
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public FunctionConstantEditionDialog(Frame owner, String fName) throws HeadlessException
	{
		super(owner, "Function " + fName + " Definition", true);
        subconstructor(fName);
	}

	/**
	 * Creates a non-modal dialog without a title with the
	 * specified <code>Dialog</code> as its owner.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param fName the non-null <code>functionName</code> the name of the target function
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public FunctionConstantEditionDialog(Dialog owner, String fName) throws HeadlessException
	{
		super(owner, "Function " + fName + " Definition", true);
        subconstructor(fName);
    }
    private void subconstructor(String fName)
    {
        functionName = fName;
        if (functionName.equalsIgnoreCase("constant"))
        {
            typeField = new JComboBox(new Vector(typeMap.keySet()));
            valueLabel = new JLabel("Value: ");
        }
        else
        {
            String[] types = new String[1];
            types[0] = functionName;
            typeField = new JComboBox(types);
            valueLabel = new JLabel("Key Code: ");
        }
        initialize();
    }
    private void initialize()
	{
		setLayout(new BorderLayout());
		centerPanel = new JPanel(new GridBagLayout());
		Dimension inputFieldSize = computeFieldSize(false);
		valueField.setPreferredSize(inputFieldSize);

		typeField.setPreferredSize(computeFieldSize(true));

		Insets insets = new Insets(5, 5, 5, 5);
		centerPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(typeField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(valueLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(valueField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		this.add(centerPanel, BorderLayout.CENTER);

		okButton = new JButton(OK_COMMAND);
		okButton.setMnemonic(OK_COMMAND_MNENOMIC.charAt(0));
		okButton.addActionListener(this);
		cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setMnemonic(CANCEL_COMMAND_MNENOMIC.charAt(0));
		cancelButton.addActionListener(this);
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setSize(300, 200);
	}

	private Dimension computeFieldSize(boolean zoom)
	{
		Dimension typeLabelSize = typeLabel.getPreferredSize();
		Dimension valueLabelSize = valueField.getPreferredSize();
		int INTERVAL = (zoom) ? 4 : 2;
		int width = Math.max(typeLabelSize.width, valueLabelSize.width) + INTERVAL * 2;
		int height = Math.max(typeLabelSize.height, valueLabelSize.height) + INTERVAL;
		return new Dimension(width, height);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if(OK_COMMAND.equals(command))
		{
			Object userSelect = typeField.getSelectedItem();
			if (functionName.equalsIgnoreCase("constant")) constantTypeClass = (Class)typeMap.get(userSelect);
            else constantTypeClass = null;
            constantValue = valueField.getText();
            if (constantValue == null) constantValue = "";
            else constantValue = constantValue.trim();
            if (constantValue.length() == 0)
			{
				JOptionPane.showMessageDialog(this, "Input the "+valueLabel.getName()+" value.", "Missing Value", JOptionPane.ERROR_MESSAGE);
				return;
			}


            String type = "";
			if (functionName.equalsIgnoreCase("constant"))
			{
			    //if(constantTypeClass==java.lang.Integer.class) type = "int";
			    //else if(constantTypeClass==java.lang.String.class) type = "String";
			    type = DefaultSettings.getClassNameWithoutPackage(getConstantTypeClass());
			}
			else type = functionName;
//                FunctionConstant constant = new FunctionConstant();
//                if (functionName.equalsIgnoreCase(constant.getFunctionNameArray()[0])) new FunctionConstant(functionName, type, constantValue);
//                else if (functionName.equalsIgnoreCase(constant.getFunctionNameArray()[1])) new FunctionConstant(functionName, type, constantValue + constant.getTestSuffix());
//                else new FunctionConstant(functionName, type, constantValue);

            /*
            if(constantTypeClass==java.lang.Integer.class)
			{//so far only know how to do validation on integers
				IntegerValidator validator = new IntegerValidator(constantValue);
				ValidatorResults result = validator.validate();
				java.util.List msgList = result.getMessages(ValidatorResult.Level.ERROR);
				if(msgList!=null && msgList.size()>0)
				{
					JOptionPane.showMessageDialog(this, msgList.toArray(new Object[0]), "Validation Failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
            */
            okButtonClicked = true;
		}
		else
		{
			okButtonClicked = false;
		}
		setVisible(false);
		dispose();
	}

	public boolean isOkButtonClicked()
	{
		return okButtonClicked;
	}

	public Class getConstantTypeClass()
	{
        if (!functionName.equalsIgnoreCase("constant")) return null;
        return constantTypeClass;
	}
    public String getConstantFunctionName()
    {
        return functionName;
    }

    public String getConstantValue()
	{
		return constantValue;
	}

	public void setConstantTypeClass(Class constantTypeClass)
	{
        if (!functionName.equalsIgnoreCase("constant")) return;
        this.constantTypeClass = constantTypeClass;
		if(typeField!=null && typeField.isVisible())
		{
			typeField.setSelectedItem(findStringKeyInTypeMap(this.constantTypeClass));
		}
	}

	public void setConstantTypeClassString(String classString, boolean withPackageName)
	{
        if (!functionName.equalsIgnoreCase("constant")) return;
        try
		{
			Class typeClass = null;
			if(withPackageName)
			{
				typeClass = Class.forName(classString);
				setConstantTypeClass(typeClass);
			}
			else
			{
				typeField.setSelectedItem(classString);
			}
		}
		catch(Throwable e)
		{
			//intentionally do nothing.
			e.printStackTrace();
		}
	}

	public void setConstantValue(String constantValue)
	{
		this.constantValue = constantValue;
		if (valueField!= null && valueField.isVisible())
		{
			valueField.setText(this.constantValue);
		}
	}

	private String findStringKeyInTypeMap(Class toFindValue)
	{
        if (!functionName.equalsIgnoreCase("constant")) return functionName;
        Iterator it = typeMap.keySet().iterator();
		String result = null;
		while(it.hasNext())
		{
			Object key = it.next();
			Object value = typeMap.get(key);
			if(GeneralUtilities.areEqual(value, toFindValue))
			{
				result = (String) key;
			}
		}
		return result;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
