/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;

import javax.swing.*;
import java.awt.*;

/**
 * This class defines a list general utility methods.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2009-10-12 19:56:04 $
 */
public class GeneralUtilities
{
    public static String CAADAPTER_DATA_FIELD_NULL="CAADAPTER_NULL_VALUE";
    public static String CAADAPTER_NULLFLAVOR_ATTRIBUTE_VALUE="CAADAPPTER_NULLFLAVOR_ATTRIBUTE_VALUE";
    public static String CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK="CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK";
    /**
	 * Return true if one is equal to another.
	 * @param one
	 * @param another
	 * @return true if one is equal to another.
	 */
	public static final boolean areEqual(Object one, Object another)
	{
		boolean result = (one==null? another==null : one.equals(another));
		return result;
	}

	/**
	 * Return true if one is equal to another.
	 * @param one
	 * @param another
	 * @param treatNullAndBlankStringEquals if true will treat null and blank string the same; otherwise, it will not.
	 * @return true if one is equal to another.
	 */
	public static final boolean areEqual(Object one, Object another, boolean treatNullAndBlankStringEquals)
	{
		boolean result = (one==null? another==null : one.equals(another));
		if(!result && treatNullAndBlankStringEquals)
		{
			String oneStr = one==null? "" : one.toString();
			String anotherStr = another==null? "" : another.toString();
			result = isBlank(oneStr) && isBlank(anotherStr);
		}
		return result;
	}

	/**
	 * Force convert String object null to a blank string
	 * @param s
	 * @return converted String object null to a blank string
	 */
	public static final String forceNullToBlankString(String s)
	{
		return  (s == null) ? "" : s;
	}

	/**
	 * Null string or blank string is considered as blank
	 * @param s
	 * @return true if blank
	 */
	public static final boolean isBlank(String s)
	{
		return  (s == null) || (s.trim().length()==0);
	}


	/**
	 * Return the given class name with or without package name. If aClass is null, will return null.
	 * @param aClass
	 * @param withPackageName
	 * @return the given class name with or without package name. If aClass is null, will return null.
	 */
	public static final String getClassName(Class aClass, boolean withPackageName)
	{
		if(aClass==null)
		{
			return null;
		}
		String beanClassName = aClass.getName();
		if(!withPackageName)
		{//trim off package name.
			beanClassName = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
		}
		return beanClassName;
	}

	/**
	 * Sets the cursor to hourglass.  Should be called BEFORE time-intensive task has started.
	 * Disables the mouse from clicking on anything in the current window.
	 *
	 * @param cont the current Window, Panel, Dialog, or other JComponent;
	 *             just pass 'this', as long as it's a Container
	 */
	public static void setCursorWaiting(Container cont)
	{
		if (cont == null)
		{
			System.err.println("setCursorWaiting(Container cont): Container argument cannot be null. Will return and do nothing.");
			return;
		}

		Component glasspane = null;

		if (cont instanceof RootPaneContainer)
		{
			glasspane = ((RootPaneContainer) cont).getGlassPane();
		}
		else if (cont instanceof JComponent)
		{
			glasspane = ((JComponent) cont).getRootPane().getGlassPane();
		}
		glasspane.setVisible(true);
		Window window = SwingUtilities.windowForComponent(glasspane);
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * Sets the cursor to default.  Should be called AFTER time-intensive task has finished.
	 * Re-enables the mouse pointer for the current window.
	 *
	 * @param cont the current Window, Panel, Dialog, or other JComponent;
	 *             just pass 'this', as long as it's a Container
	 */
	public static void setCursorDefault(Container cont)
	{
		if(cont==null)
		{
			System.err.println("setCursorDefault(Container cont): Container argument cannot be null. Will return and do nothing.");
			return;
		}

		Component glasspane = null;

		if (cont instanceof RootPaneContainer)
		{
			glasspane = ((RootPaneContainer) cont).getGlassPane();
		}
		else if (cont instanceof JComponent)
		{
			glasspane = ((JComponent) cont).getRootPane().getGlassPane();
		}

		Window window = SwingUtilities.windowForComponent(glasspane);
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		glasspane.setVisible(false);
	}


	/**
	 * Return a string presentation of the object. If it is a null, will return a string "null" if returnStringValue_null is true; otherwise, return an empty string ("").
	 * @param object
	 * @param returnStringValue_null
	 * @return a string presentation of the object. If it is a null, will return a string "null" if returnStringValue_null is true; otherwise, return an empty string ("").
	 */
	public static final String getStringValue(Object object, boolean returnStringValue_null)
	{
		String result = null;
		if(object==null)
		{
			if(returnStringValue_null)
			{
				result = "null";
			}
			else
			{
				result = "";
			}
		}
		else
		{
			result = object.toString();
		}

		return result;
	}

	/**
	 * Help to convert the given throwable to an instance of message by utilizing the GEN0 messsage.
	 * @param t
	 * @return the message object.
	 */
	public static final Message convertToGeneralMessage(Throwable t)
	{
		String reportMsg;
		if(t==null)
		{
			reportMsg = "Unknown Error.";
		}
		else
		{
			reportMsg = t.getMessage();
		}
		Message msg = MessageResources.getMessage("GEN0", new Object[]{t.getMessage()});
		return msg;
	}

        /**
	 * Help to insert an Error Message into a ValidatorResults object.
	 * @param validatorResults which is the ValidatorResults object to be added a message into
     * @param mesg is the messge string.
	 * @return the added ValidatorResults.
	 */
    public static final ValidatorResults addValidatorMessage(ValidatorResults validatorResults, String mesg)
    {
        Message msg = MessageResources.getMessage("EMP_ER", new Object[]{mesg});
	    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        return validatorResults;
    }

    /**
	 * Help to insert a Fatal Message into a ValidatorResults object.
	 * @param validatorResults which is the ValidatorResults object to be added a message into
     * @param mesg is the messge string.
	 * @return the added ValidatorResults.
	 */
    public static final ValidatorResults addValidatorMessageFatal(ValidatorResults validatorResults, String mesg)
    {
        Message msg = MessageResources.getMessage("EMP_FT", new Object[]{mesg});
	    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
        return validatorResults;
    }

    /**
	 * Help to insert a Warning Message into a ValidatorResults object.
	 * @param validatorResults which is the ValidatorResults object to be added a message into
     * @param mesg is the messge string.
	 * @return the added ValidatorResults.
	 */
    public static final ValidatorResults addValidatorMessageWarning(ValidatorResults validatorResults, String mesg)
    {
        Message msg = MessageResources.getMessage("EMP_WN", new Object[]{mesg});
	    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
        return validatorResults;
    }

    /**
	 * Help to insert an Info Message into a ValidatorResults object.
	 * @param validatorResults which is the ValidatorResults object to be added a message into
     * @param mesg is the messge string.
	 * @return the added ValidatorResults.
	 */
    public static final ValidatorResults addValidatorMessageInfo(ValidatorResults validatorResults, String mesg)
    {
        Message msg = MessageResources.getMessage("EMP_IN", new Object[]{mesg});
	    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
        return validatorResults;
    }

    /**
	 * Check whether parameter string is consist of alphabetic, nemeric and the unser score character as an element name.
	 * @param name is the checked string.
     * @return If any error is found the error message will be return, but null.
	 */
    public static final String checkElementName(String name)
    {
        return checkElementName(name, false);
    }
    /**
	 * Check whether parameter string is consist of alphabetic, nemeric and the unser score character as an element name.
	 * @param name is the checked string.
     * @param capitalOnly is an option for checking non-CAPITAL character.
	 * @return If any error is found the error message will be return, but null.
	 */
    public static final String checkElementName(String name, boolean capitalOnly)
    {
        if ((name == null)||(name.trim().equals(""))) return "This String array is null or empty.";
        name = name.trim();
        String achar = "";
        int inFirst = 0;
        for (int i=0;i<name.length();i++)
        {
            achar = name.substring(i, i+1);
            char[] chars = achar.toCharArray();
            int in = (int) chars[0];
            if (i == 0) inFirst = in;
            boolean tr = false;
            if ((in >= 65)&&(in <= 90)) tr = true;
            else if ((in >= 97)&&(in <= 122))
            {
                if (capitalOnly) return "This String array includes a non-CAPITAL alphabetic character. : " + name;
                else tr = true;
            }
            else if ((in >= 48)&&(in <= 57)) tr = true;
            else if (in == 95) tr = true;
            else if (in == 45) tr = true;
            else tr = false;
            if (!tr) return "This String array includes an invalid character. : " + name;
        }
        if (inFirst == 95) return "This String array starts with an under score character. : " + name;

        return null;
    }
    public static final String getCurrentTime()
    {
        return getCurrentTime(null);
    }
    public static final String getCurrentTime(String format)
    {
        DateFunction df = new DateFunction();
        if (format == null) format = "";
        else format = format.trim();
        if (format.equals("")) format = df.getDefaultDateFormatString();
        try
        {
            return df.getCurrentTime(format);
        }
        catch(FunctionException fe)
        {
            return df.getCurrentTime();
        }
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.10  2009/01/09 21:30:12  wangeug
 * HISTORY      : define CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK/VALUE
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/12/04 20:36:09  wangeug
 * HISTORY      : support nullFlavor
 * HISTORY      :
 * HISTORY      : Revision 1.8  2008/11/17 20:12:06  wangeug
 * HISTORY      : Handle NULL in CSVData field:""
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/04/25 04:45:53  umkis
 * HISTORY      : getVocabularySeeker() was picked out from GeneralUtilities and moved into VocabulraryGeneralUtilities.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/04/24 20:24:21  umkis
 * HISTORY      : Move from the 'common' component to 'hl7' (V3VocabularySeeker)
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/08 21:17:53  umkis
 * HISTORY      : Change to V3VocabularyTreeBuildEventHandler
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/08 20:33:11  umkis
 * HISTORY      : V3 Vocavulary utility objects initializing setup
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/09 15:39:58  umkis
 * HISTORY      : Update for csv cardinality and test instance generating.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/21 22:51:50  giordanm
 * HISTORY      : more function rework.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/09/28 20:14:27  jiangsc
 * HISTORY      : Added a new String function.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/31 15:03:26  jiangsc
 * HISTORY      : Fixed some UI medium defects. Thanks to Dan's test.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/08 15:36:07  jiangsc
 * HISTORY      : Update isBlank() method to use:
 * HISTORY      : (s.trim().length()==0) instead of ""==s
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 22:22:29  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
