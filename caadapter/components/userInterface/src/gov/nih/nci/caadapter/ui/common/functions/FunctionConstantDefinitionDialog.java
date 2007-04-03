/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionConstantDefinitionDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class FunctionConstantDefinitionDialog extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: FunctionConstantDefinitionDialog.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionConstantDefinitionDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

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
	public FunctionConstantDefinitionDialog(Frame owner, String fName) throws HeadlessException
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
	public FunctionConstantDefinitionDialog(Dialog owner, String fName) throws HeadlessException
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


            try
            {
                String type = "";
                if (functionName.equalsIgnoreCase("constant"))
                {
                    //if(constantTypeClass==java.lang.Integer.class) type = "int";
                    //else if(constantTypeClass==java.lang.String.class) type = "String";
                    type = DefaultSettings.getClassNameWithoutPackage(getConstantTypeClass());
                }
                else type = functionName;
                FunctionConstant constant = new FunctionConstant();
                if (functionName.equalsIgnoreCase(constant.getFunctionNameArray()[0])) new FunctionConstant(functionName, type, constantValue);
                else if (functionName.equalsIgnoreCase(constant.getFunctionNameArray()[1])) new FunctionConstant(functionName, type, constantValue + constant.getTestSuffix());
                else new FunctionConstant(functionName, type, constantValue);
            }
            catch(FunctionException fe)
            {
                JOptionPane.showMessageDialog(this, fe.getMessage(), "Constant Value Validation Failed", JOptionPane.ERROR_MESSAGE);
				return;
            }

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
			Log.logException(this, e);
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
 * HISTORY      : Revision 1.13  2006/12/28 20:50:36  umkis
 * HISTORY      : saveValue() and readValue() in FunctionConstant
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/13 17:09:15  jiangsc
 * HISTORY      : Enabled to enter " " space characters.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/29 22:15:52  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/22 21:35:29  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 */
