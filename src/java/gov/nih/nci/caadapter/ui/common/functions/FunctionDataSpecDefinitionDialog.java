package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.common.function.*;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.HTML_SpecialCharacterTable;
import gov.nih.nci.caadapter.castor.function.impl.C_dataSpec;
import gov.nih.nci.caadapter.castor.function.impl.TypeDef;
import gov.nih.nci.caadapter.castor.function.impl.types.CaseType;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 25, 2010
 * Time: 4:19:00 PM
 * To change this template use File | Settings | File Templates.
 */

public class FunctionDataSpecDefinitionDialog extends JDialog implements ActionListener
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionDataSpecDefinitionDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionDataSpecDefinitionDialog.java,v 1.2 2010-04-29 19:53:51 umkis Exp $";


    private JLabel typeLabel;
    private JComboBox typeCombo;
    private JComboBox valueCombo;
    private JTextField typeField;
    private JTextField valueField;
    private JLabel valueLabel;
    private JLabel annotationLabel;

    private static final String OK_COMMAND = "OK";
    private static final String OK_COMMAND_MNENOMIC = "O";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String CANCEL_COMMAND_MNENOMIC = "C";
    private JPanel centerPanel;

    private JButton okButton;
    private JButton cancelButton;
    private JPanel buttonPanel;

    private boolean okButtonClicked;

    private String typeValue;
    private String valueValue;
    private String functionName;

    private C_dataSpec dataSpec;

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
    public FunctionDataSpecDefinitionDialog(Frame owner, String fName, C_dataSpec spec) throws HeadlessException
    {
        super(owner, "Function " + fName + " Definition", true);
        if (spec == null) throw new HeadlessException("DataSpec Object is null.");
        dataSpec = spec;
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
    public FunctionDataSpecDefinitionDialog(Dialog owner, String fName, C_dataSpec spec) throws HeadlessException
    {
        super(owner, "Function " + fName + " Definition", true);
        if (spec == null) throw new HeadlessException("DataSpec Object is null.");
        dataSpec = spec;
        subconstructor(fName);
    }

    private void subconstructor(String fName)
    {
        functionName = fName;
        TypeDef typeCase = dataSpec.getC_typeDef();
        TypeDef valueCase = dataSpec.getC_valueDef();

        if (typeCase.getFieldType().getType() == CaseType.FIELD_TYPE)
        {
            typeField = setupTextField(dataSpec.getC_typeDef());
            if ((!typeCase.getDefault().equals(""))&&(typeCase.getIsFixed())) typeField.setEditable(false);
        }
        else if (typeCase.getFieldType().getType() == CaseType.COMBO_TYPE)
        {
            typeCombo = setupComboBox(dataSpec.getC_typeDef());
            typeCombo.setEditable(false);
        }
        if (valueCase.getFieldType().getType() == CaseType.FIELD_TYPE)
        {
            valueField = setupTextField(dataSpec.getC_valueDef());
        }
        else if (valueCase.getFieldType().getType() == CaseType.COMBO_TYPE)
        {
            valueCombo = setupComboBox(dataSpec.getC_valueDef());
            valueCombo.setEditable(false);
        }

        typeLabel = new JLabel(dataSpec.getC_typeDef().getName());

        valueLabel = new JLabel(dataSpec.getC_valueDef().getName());

        String annotation = dataSpec.getAnnotation();
        if ((annotation != null)&&(!annotation.trim().equals("")))
        {
            System.out.println("annotation is exists. ("+dataSpec.getName()+") : " + annotation);
            annotationLabel = new JLabel(annotation);
        }
        else System.out.println("annotation is not exists. ("+dataSpec.getName()+") : " + annotation);

        initialize();
    }
    private JTextField setupTextField(TypeDef typeCase)
    {
        String defaultV = typeCase.getDefault();
        if (defaultV == null) defaultV = "";
        JTextField typeFieldC = new JTextField(defaultV);
        if ((!defaultV.equals(""))&&(typeCase.getIsFixed())) typeFieldC.setEditable(false);
        return typeFieldC;
    }
    private JComboBox setupComboBox(TypeDef typeCase)
    {
        JComboBox typeComboC = new JComboBox();

        java.util.List<String> list = getValueArray(typeCase);

        for(String item:list)
        {
            typeComboC.addItem(item);
        }
        typeComboC.setEditable(false);
        return typeComboC;
    }
    private boolean checkValueWithArray(String data, TypeDef typeCase, boolean doTrim)
    {
        java.util.List<String> list = getValueArray(typeCase);

        if (data == null) return false;
        if (doTrim) data = data.trim();
        if (data.equals("")) return false;

        if (typeCase.hasIsFixed())
        {
            String defaultV = typeCase.getDefault();
            if (defaultV == null) defaultV = "";
            else defaultV = defaultV.trim();
            if (!defaultV.equals(""))
            {
                if (!data.equals(defaultV)) return false;
            }
        }

        if (list.size() == 0) return true;
        for(String item:list)
        {
            if (item.equals(data)) return true;
        }
        return false;
    }
    private java.util.List<String> getValueArray(TypeDef typeCase)
    {
        String valArr = typeCase.getValueArray();
        if (valArr == null) valArr = "";
        else valArr = valArr.trim();
        String defaultV = typeCase.getDefault();
        if (defaultV == null) defaultV = "";
        else defaultV = defaultV.trim();
        java.util.List<String> list = new ArrayList<String>();
        if (!defaultV.equals("")) list.add(defaultV);
        if (!valArr.equals(""))
        {
            if (valArr.indexOf(",") >= 0)
            {
                StringTokenizer st = new StringTokenizer(valArr, ",");
                while(st.hasMoreTokens())
                {
                    String token = st.nextToken().trim();
                    if ((!token.equals(""))&&(!token.equals(defaultV))) list.add(token);
                }
            }
            else list.add(valArr);
        }
        return list;
    }

    private void initialize()
    {
        setLayout(new BorderLayout());
        centerPanel = new JPanel(new GridBagLayout());
        Dimension inputFieldSize = computeFieldSize(false);
        valueField.setPreferredSize(inputFieldSize);

        if (typeField != null) typeField.setPreferredSize(computeFieldSize(true));
        if (typeCombo != null) typeCombo.setPreferredSize(computeFieldSize(true));

        Insets insets = new Insets(5, 5, 5, 5);
        centerPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        if (typeField != null) centerPanel.add(typeField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        else if (typeCombo != null) centerPanel.add(typeCombo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        centerPanel.add(valueLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        if (valueField != null) centerPanel.add(valueField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        else if (valueCombo != null) centerPanel.add(valueCombo, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        if (annotationLabel != null) centerPanel.add(annotationLabel, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
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
        if (annotationLabel != null)
        {
            if (dataSpec.getName().toLowerCase().indexOf("compute") >= 0) this.setSize(450, 200);
            else this.setSize(400, 200);
        }
        else this.setSize(300, 200);
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
            Object userSelect = null;
            if (typeField != null) userSelect = typeField.getText();
            if (typeCombo != null) userSelect = typeCombo.getSelectedItem();
            if ((userSelect == null)||(userSelect.toString().trim()).equals(""))
            {
                JOptionPane.showMessageDialog(this, "Null Type '" + typeLabel.getText() + "' value.", "Null Type", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Object userValue = null;
            if (valueField != null) userValue = valueField.getText();
            if (valueCombo != null) userValue = valueCombo.getSelectedItem();
            if ((userValue == null)||(userValue.toString().trim()).equals(""))
            {
                JOptionPane.showMessageDialog(this, "Null Value '"+valueLabel.getText()+"' item.", "Null Value", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String type = userSelect.toString().trim();
            String valueOr = userValue.toString().trim();
            HTML_SpecialCharacterTable hspt = new HTML_SpecialCharacterTable();
            String value = hspt.transformTaggingToString(valueOr);

            if (!checkValueWithArray(type, dataSpec.getC_typeDef(), true))
            {
                JOptionPane.showMessageDialog(this, "Not reserved value for "+ typeLabel.getText() + " '"+type+"' item.", "Invalid Type", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!checkValueWithArray(value, dataSpec.getC_valueDef(), false))
            {
                JOptionPane.showMessageDialog(this, "Not reserved value for '"+valueLabel.getText()+"' item.", "Invalid Value", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Long ll = -1l;
            if ((type.equalsIgnoreCase("integer"))||(type.equalsIgnoreCase("long")))
            {
                try
                {
                    ll = Long.parseLong(value);
                }
                catch(NumberFormatException ne)
                {
                    JOptionPane.showMessageDialog(this, "Not integer value for '"+typeLabel.getText()+"("+type+")' item.", "Invalid number value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            double dd = -1.0;
            if ((type.equalsIgnoreCase("number"))||
                (type.equalsIgnoreCase("numbers"))||
                (type.equalsIgnoreCase("double"))||
                (type.equalsIgnoreCase("float"))||
                (type.equalsIgnoreCase("decimal"))||
                (type.startsWith("##")))
            {
                try
                {
                    dd = Double.parseDouble(value);
                }
                catch(NumberFormatException ne)
                {
                    JOptionPane.showMessageDialog(this, "Not number format value for '"+typeLabel.getText()+"("+type+")' item.", "Invalid number value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if ((type.equalsIgnoreCase("computingExpression1"))||(type.equalsIgnoreCase("computingExpression2")))
            {
                if (value.indexOf("##1") < 0)
                {
                    JOptionPane.showMessageDialog(this, "Input value 1 (##1) is not used", "Unused Parameter", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ((type.equalsIgnoreCase("computingExpression2"))&&(value.indexOf("##2") < 0))
                {
                    JOptionPane.showMessageDialog(this, "Input value 2 (##2) is not used", "Unused Parameter", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ((type.equalsIgnoreCase("computingExpression1"))&&(value.indexOf("##2") >= 0))
                {
                    JOptionPane.showMessageDialog(this, "Input value 2 (##2) cannot be used", "Too Many Parameters", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean isSecond = false;
                while(true)
                {
                    try
                    {
                        MathFunction mf = new MathFunction();
                        double d1 = 0.0;
                        double val1 = 1.25999888999;
                        double val2 = 1.45999888999;
                        if (isSecond)
                        {
                            val1 = 120.25999888999;
                            val2 = 146.45999888999;
                        }
                        if (type.equalsIgnoreCase("computingExpression1"))
                        {
                            d1 = mf.compute("" + val1, value);
                        }
                        if (type.equalsIgnoreCase("computingExpression2"))
                        {
                            d1 = mf.compute("" + val1, "" + val2, value);
                        }
                        //System.out.println("$$$ Result " + type + " : " + d1);
                        break;
                    }
                    catch(FunctionException ne)
                    {
                        if (isSecond)
                        {
                            JOptionPane.showMessageDialog(this, ne.getMessage(), "Invalid Expression", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    isSecond = true;
                }
            }
            if ((dataSpec.getName().equalsIgnoreCase("DividedBy"))||
                (dataSpec.getName().equalsIgnoreCase("RemainderWith")))
            {
                if ((ll == 0l)||(dd == 0.0))
                {
                    JOptionPane.showMessageDialog(this, "Zero Divior Error : '"+typeLabel.getText()+"("+type+")' item.", "Zero Divior Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (type.equalsIgnoreCase("dateformat"))
            {
                try
                {
                    DateFunction dt = new DateFunction();
                    dt.checkSimpleDateFormat(value);
                }
                catch(FunctionException ne)
                {
                    JOptionPane.showMessageDialog(this, "Invalid date format value for '"+typeLabel.getName()+"("+type+")' item.", "Invalid date format value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (type.equalsIgnoreCase("NullFlavor"))
            {
//&umkis                NullFlavorFunctionHelper nfh = new NullFlavorFunctionHelper();
//&umkis                String ret = nfh.validateNullFlavorSetteng(value);
//&umkis                if (ret != null)
//&umkis                {
//&umkis                    JOptionPane.showMessageDialog(this, ret, "Invalid nullFlavor key word", JOptionPane.ERROR_MESSAGE);
//&umkis                    return;
//&umkis                }
            }
            if (type.equalsIgnoreCase("replaceChar"))
            {
                String delimiter = "=>";
                int idx = value.indexOf(delimiter);
                if (idx < 0)
                {
                    JOptionPane.showMessageDialog(this, "Delimiter("+delimiter+") cannot be found.", "Invalid Replace Character type value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ((value.startsWith(delimiter))||(value.endsWith(delimiter)))
                {
                    JOptionPane.showMessageDialog(this, "Either source or target string is null.", "Invalid Replace Character type value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if ((type.equalsIgnoreCase("range"))||(type.equalsIgnoreCase("rangevalue")))
            {
                String delimiter = "~";
                boolean isValid = false;
                while(true)
                {
                    int idxDelim = value.indexOf(delimiter);
                    if (idxDelim <= 0) break;

                    String from = value.substring(0, idxDelim).trim();
                    if (from.equals("")) from = "0";
                    String to = value.substring(idxDelim + delimiter.length()).trim();
                    if (to.equals("")) to = "9999";

                    boolean check = true;
                    String keyWord = "";
                    String keyWordDef = "indexof:";
                    String keyWordInc = "indexof+:";
                    String keyWordExc = "indexof-:";
                    int startV = -1;
                    for (int i=0;i<2;i++)
                    {
                        String str = from;
                        if (i==1)
                        {
                            str = to;
                            if (str.trim().equalsIgnoreCase("end")) str = "99999";
                        }

                        try
                        {
                            int ih = Integer.parseInt(str);

                            if ((ih < 0 )||(startV >= ih))
                            {
                                JOptionPane.showMessageDialog(this, "Range is negative or zero : " + value, "Invalid range value", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (startV < 0) startV = ih;
                        }
                        catch(NumberFormatException ne)
                        {
                            check = false;
                        }
                        if (check) continue;

                        if (str.toLowerCase().startsWith(keyWordDef)) keyWord = keyWordDef;
                        else if (str.toLowerCase().startsWith(keyWordExc)) keyWord = keyWordExc;
                        else if (str.toLowerCase().startsWith(keyWordInc)) keyWord = keyWordInc;
                        else keyWord = null;

                        if (keyWord != null)
                        {
                            if (str.substring(keyWord.length()).trim().equals("")) check = false;
                            else check = true;
                        }
                        else check = false;
                    }
                    if (!check) break;

                    isValid = true;
                    break;
                }
                if (!isValid)
                {
                    JOptionPane.showMessageDialog(this, "Invalid range value : " + value, "Invalid range value", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            typeValue = type;
            valueValue = valueOr;

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

    public String getType()
    {
        return typeValue;
    }
    public String getFunctionName()
    {
        return functionName;
    }

    public String getValue()
    {
        return valueValue;
    }

    public void setType(String type)
    {
        typeValue = type;
        if (typeField!= null && typeField.isVisible())
        {
            typeField.setText(typeValue);
        }
        if (typeCombo!= null && typeCombo.isVisible())
        {
            typeCombo.setSelectedItem(typeValue);
        }
    }

    public void setValue(String value)
    {
        valueValue = value;
        if (valueField!= null && valueField.isVisible())
        {
            valueField.setText(valueValue);
        }
        if (valueCombo!= null && valueCombo.isVisible())
        {
            valueCombo.setSelectedItem(valueValue);
        }
    }

}
