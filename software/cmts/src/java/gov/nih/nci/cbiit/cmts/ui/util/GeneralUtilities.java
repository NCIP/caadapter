/*L
 * Copyright SAIC, SAIC-Frederick.
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


package gov.nih.nci.cbiit.cmts.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * This class defines a list general utility methods.
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public class GeneralUtilities
{
    private GeneralUtilities()
	{//never instantiable

    }

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
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

