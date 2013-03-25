/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 17, 2010
 * Time: 12:57:56 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


public class DefaultSettings
{

	private static final String PROPERTY_FONT = "Font";
	public static final Font DEFAULT_FONT;

	private static final Object[] uiDefaults = {PROPERTY_FONT, new Font("Sans Serif", Font.PLAIN, 12)};

	private static final UIDefaults newDefaults = new UIDefaults(uiDefaults);
	private static JFileChooser defaultFileChooser;
	private static File lastPathLocation;

	static
	{
		DEFAULT_FONT = (Font) newDefaults.get(PROPERTY_FONT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		installLookAndFeel();
		installFonts();
	}

	private static final JFileChooser getJFileChooser(boolean newOne)
	{
		if (newOne)
		{
			return new JFileChooser();
		}
		else
		{
			if (defaultFileChooser == null)
			{
				defaultFileChooser = new JFileChooser();
			}
			return defaultFileChooser;
		}
	}


	/**
	 * Installs the default LookAndFeel.
	 */
	private static void installLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			//Log.logException(e, e);
            System.out.println("DefaultSettings.installLookAndFeel() Exception : " + e.getMessage());
        }
	}

	/**
	 * Installs the default fonts.  Currently sets to Sans Serif 11.
	 */
	private static void installFonts()
	{
		Font newFont = (Font) newDefaults.get(PROPERTY_FONT);
		//keep them in alphabetical order
		UIDefaults currentDefaults = UIManager.getDefaults();
		//		dumpMap(currentDefaults);
		currentDefaults.put("AbstractButton.clickText.font", newFont);
		currentDefaults.put("Button.clickText.font", newFont);
		currentDefaults.put("Button.font", newFont);
		currentDefaults.put("CheckBox.font", newFont);
		currentDefaults.put("CheckBoxMenuItem.acceleratorFont", newFont);
		currentDefaults.put("CheckBoxMenuItem.font", newFont);
		currentDefaults.put("ColorChooser.font", newFont);
		currentDefaults.put("ComboBox.font", newFont);
		currentDefaults.put("DesktopIcon.font", newFont);
		currentDefaults.put("EditorPane.font", newFont);
		currentDefaults.put("FileChooser.listFont", newFont);
		currentDefaults.put("InternalFrame.font", newFont);
		currentDefaults.put("Label.font", newFont);
		currentDefaults.put("List.font", newFont);
		currentDefaults.put("Menu.acceleratorFont", newFont);
		currentDefaults.put("Menu.font", newFont);
		currentDefaults.put("MenuBar.font", newFont);
		currentDefaults.put("MenuItem.font", newFont);
		currentDefaults.put("MenuItem.acceleratorFont", newFont);
		//		currentDefaults.put("MenuItem.accelerator.font", newFont);
		currentDefaults.put("OptionPane.font", newFont);
		currentDefaults.put("Panel.font", newFont);
		currentDefaults.put("PasswordField.font", newFont);
		currentDefaults.put("PopupMenu.font", newFont);
		currentDefaults.put("ProgressBar.font", newFont);
		currentDefaults.put("RadioButton.font", newFont);
		currentDefaults.put("RadioButtonMenuItem.acceleratorFont", newFont);
		currentDefaults.put("RadioButtonMenuItem.font", newFont);
		currentDefaults.put("ScrollPane.font", newFont);
		currentDefaults.put("TabbedPane.font", newFont);
		currentDefaults.put("Table.font", newFont);
		currentDefaults.put("TableHeader.font", newFont);
		currentDefaults.put("TextArea.font", newFont);
		currentDefaults.put("TextField.font", newFont);
		currentDefaults.put("TextPane.font", newFont);
		currentDefaults.put("TitledBorder.font", newFont);
		currentDefaults.put("ToggleButton.font", newFont);
		currentDefaults.put("ToolBar.font", newFont);
		currentDefaults.put("ToolTip.font", newFont);
		currentDefaults.put("Tree.font", newFont);
		currentDefaults.put("Viewport.font", newFont);
		currentDefaults.put("win.frame.captionFont", newFont);
		currentDefaults.put("win.messagebox.font", newFont);
		//		dumpMap(currentDefaults);
	}


	/**
	 * Returns the classname without the package.
	 * Example: If the input class is java.lang.String
	 * than the return value is String.
	 *
	 * @param cl The class to inspect
	 * @return The classname
	 */
	public static String getClassNameWithoutPackage(Class cl)
	{
		// build the name for this action
		// without the package prefix
		String className = cl.getName();
		int pos = className.lastIndexOf('.') + 1;
		if (pos == -1)
		{
			pos = 0;
		}
		String name = className.substring(pos);
		return name;
	}

	/**
	 * Center the window to the middle of the screen.
	 *
	 * @param frame
	 */
	public static void centerWindow(Window frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
	}

	/**
	 * Title shall better contain "open" or "save" to indicate whether to open or save.
	 *
	 * @param parentComponent
	 * @param fileExtension
	 * @param title
	 * @param saveMode		if true, it will show a SaveDialog
	 * @param checkDuplicate  if true, will check if user specified file already exists and will ask for either overwrite or specify another one;
	 * @return The File Object that contains user input; null if user cancelled the input dialog.
	 */
	public static final File getUserInputOfFileFromGUI(Component parentComponent, String fileExtension, String title, boolean saveMode, boolean checkDuplicate)
	{
		if (lastPathLocation == null)
		{
			lastPathLocation = new File(getUIWorkingDirectoryPath());
		}
		return getUserInputOfFileFromGUI(parentComponent, lastPathLocation.getAbsolutePath(), fileExtension, title, saveMode, checkDuplicate);
	}
    public static String getUIWorkingDirectoryPath()
    {
        File f = new File("./workingspace");
        if ((!f.exists())||(!f.isDirectory()))
        {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }
	/**
	 * Title shall better contain "open" or "save" to indicate whether to open or save.
	 *
	 * @param parentComponent
	 * @param workingDirectoryPath
	 * @param fileExtension
	 * @param title
	 * @param saveMode			 if true, it will show a SaveDialog
	 * @param checkDuplicate	   if true, will check if user specified file already exists and will ask for either overwrite or specify another one;
	 * @return The File Object that contains user input; null if user cancelled the input dialog.
	 */
	public static final File getUserInputOfFileFromGUI(Component parentComponent, String workingDirectoryPath, String fileExtension, String title, boolean saveMode, boolean checkDuplicate)
	{
		File file = null;

		JFileChooser fileChooser = getJFileChooser(true);
		fileChooser.setCurrentDirectory(new File(workingDirectoryPath));
		boolean toSelectedDir=false;
		ArrayList<FileFilter> fileFilters=new ArrayList<FileFilter>();
		if ((fileExtension==null)||(fileExtension.trim().equals("")))
		{
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			toSelectedDir=true;
		}
        else if ((fileExtension.equals("*.*"))||(fileExtension.equals(".*")))      //JFileChooser.FILES_AND_DIRECTORIES
		{
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//.DIRECTORIES_ONLY);
			toSelectedDir=true;
		}
        else if (fileExtension.equals("*"))      //JFileChooser.FILES_AND_DIRECTORIES
		{
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//.DIRECTORIES_ONLY);
			toSelectedDir=true;
		}
        else
		{
			StringTokenizer stk=new StringTokenizer(fileExtension,";");
			while (stk.hasMoreElements())
			{
				String nxtExt=(String)stk.nextElement();
				FileFilter singleFileFilter = new SingleFileFilter(nxtExt);
				fileFilters.add(singleFileFilter);
				fileChooser.addChoosableFileFilter(singleFileFilter);
			}
		}
		fileChooser.setDialogTitle(title);

		do
		{
			int returnVal = -1;
			file = null;
			if (saveMode || (title != null && title.toLowerCase().indexOf("save") != -1))
			{
				returnVal = fileChooser.showSaveDialog(parentComponent);
			}
			else if (toSelectedDir)
				returnVal = fileChooser.showDialog(parentComponent, "Select");
			else
			{
				returnVal = fileChooser.showOpenDialog(parentComponent);
			}

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fileChooser.getSelectedFile();
				if (file == null)
				{//this condition could happen when a double click occurred in the file selection panel, but did not select anything
					JOptionPane.showMessageDialog(parentComponent, "No file is selected. Please select a file.", "No file selected", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				//since this point on, file will not be null.
				for(FileFilter fileFilter:fileFilters)
				{
				if (areEqual(fileFilter, fileChooser.getFileFilter()))
				{//if and only if the currently used file filter in fileChooser is the same as the given fileFilter.
					file = appendFileNameWithGivenExtension(file, ((SingleFileFilter)fileFilter).getExtension(), true);
					break;
				}
				}
				if (checkDuplicate)
				{
					if (file.exists())
					{
						String msg = "'" + file.getName() + "' already exists in the specified directory. Would you like to overwrite it?";
						int userChoice = JOptionPane.showConfirmDialog(parentComponent, msg, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (userChoice == JOptionPane.YES_OPTION)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{//do not check duplicate, then check if file exists for Open mode.
					if (!saveMode)
					{//if not in save mode, it will be in open mode
						if (!file.exists())
						{
							String msg = "'" + file.getName() + "' does not exist in the specified directory. Please select another file.";
							JOptionPane.showMessageDialog(parentComponent, msg, "File does not exist", JOptionPane.ERROR_MESSAGE);
							continue;
						}
					}
					break;
				}
			}
			else
			{
				break;
			}
		}
		while (true);

		if (file != null)
		{
			lastPathLocation = file.getParentFile();
		}

		//clear out the file filter after usage.
		for(FileFilter fileFilter:fileFilters)
		fileChooser.removeChoosableFileFilter(fileFilter);

		System.out
		.println("DefaultSettings.getUserInputOfFileFromGUI()..currentDir:"+fileChooser.getCurrentDirectory());

		return file;
	}

    /**
     * This function will return the file with the given extension. If it already contains, return immediately.
     * @param file
     * @param extension
     * @param extensionIncludesDelimiter
     * @return the File object contains the right file name with the given extension.
     */
    public static final File appendFileNameWithGivenExtension(File file, String extension, boolean extensionIncludesDelimiter)
    {
        String extensionLocal = getFileExtension(file, extensionIncludesDelimiter);
        if(areEqual(extensionLocal, extension))
        {//already contains the given extension, return
            return file;
        }
        else
        {
            String newFileName = file.getAbsolutePath();
            if(extensionIncludesDelimiter)
            {
                newFileName += extension;
            }
            else
            {
                newFileName += "." + extension;
            }
            File resultFile = new File(newFileName);
            return resultFile;
        }
    }

    /**
     * Return the extension part given file name.
     * For example, if the name of the file is "foo.bar", ".bar" will be returned
     * if includeDelimiter is true, or "bar" will be returned if includeDelimiter is false;
     * otherwise, if no extension is specified in the file name, empty string is
     * returned instead of null.
     *
     * @param file
     * @param includeDelimiter
     * @return the extension or an empty string if nothing is found
     */
    public static final String getFileExtension(File file, boolean includeDelimiter)
    {
        String result = "";
        if (file != null)
        {
            String absoluteName = file.getAbsolutePath();
            if (absoluteName != null)
            {
                int delimIndex = absoluteName.lastIndexOf(".");
                if (delimIndex != -1)
                {//include the . delimiter
                    if (!includeDelimiter)
                    {//skip the . delimiter
                        delimIndex++;
                    }
                    result = absoluteName.substring(delimIndex);
                }
            }
        }
        return result;
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
	 * @param imageFileName the name of the file, does not need path information
	 * @return the image retrieved from the system.
	 */
	public static final Image getImage(String imageFileName)
	{
		String imgFilePath="images/";
		imgFilePath = imgFilePath + imageFileName;
		URL imgUrl =FileUtil.retrieveResourceURL(imgFilePath);
		Image rtnImg=null;

		try {
			InputStream imgStream=imgUrl.openStream();
			BufferedImage bfImage= ImageIO.read(imgStream);
			rtnImg=(Image)bfImage;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Log.logException("Failed to load image:"+imageFileName, e);
			e.printStackTrace();
		}
		return rtnImg;
	}

	/**
	 * Currently will setOneTouchExpandable to be true and setDividerSize to be Config.DEFAULT_DIVIDER_SIZE.
	 *
	 * @param splitPane
	 */
	public static final void setDefaultFeatureForJSplitPane(JSplitPane splitPane)
	{
		if (splitPane != null)
		{
			splitPane.setBorder(BorderFactory.createEmptyBorder());
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerSize(200);
		}
	}




	/**
	 * Find the root container class of the given component.
	 *
	 * @param component
	 * @return null if nothing is found or the given component is null.
	 */
	public static Container findRootContainer(JComponent component)
	{
		if (component != null)
		{
			Container container = null;
			JRootPane rootPane = component.getRootPane();
			if (rootPane != null)
			{
				container = rootPane.getParent();
			}
			return container;
		}
		else
		{
			return null;
		}
	}

    public static String changeCharacter(String str)
    {
        if (str == null) return null;
        String formula2 = "";
        for(int i=0;i<str.length();i++)
        {
            String achar = str.substring(i, i+1);

            if (achar.equals("\"")) formula2 = formula2 + "%22";
            else if (achar.equals("\n")) formula2 = formula2 + "";
            else if (achar.equals("\r")) formula2 = formula2 + "";
            else if (achar.equals("\t")) formula2 = formula2 + "";
            else if (achar.equals(" ")) formula2 = formula2 + "%20";
            else if (achar.equals("<")) formula2 = formula2 + "%3C";
            else if (achar.equals("=")) formula2 = formula2 + "%3D";
            else if (achar.equals(">")) formula2 = formula2 + "%3E";
            else if (achar.equals("/")) formula2 = formula2 + "%2F";
            else if (achar.equals("^")) formula2 = formula2 + "%52";
            else if (achar.equals(";")) formula2 = formula2 + "%3B";
            else if (achar.equals(":")) formula2 = formula2 + "%3A";
            else if (achar.equals(".")) formula2 = formula2 + "%2E";
            else if (achar.equals("+")) formula2 = formula2 + "%2B";
            else if (achar.equals("%")) formula2 = formula2 + "%25";
            else if (achar.equals("&")) formula2 = formula2 + "%26";
            else if (achar.equals("@")) formula2 = formula2 + "%40";
            else if (achar.equals("?")) formula2 = formula2 + "%3F";
            else if (achar.equals("!")) formula2 = formula2 + "%21";
            else if (achar.equals("#")) formula2 = formula2 + "%23";
            else if (achar.equals("*")) formula2 = formula2 + "%2A";
            else if (achar.equals("-")) formula2 = formula2 + "%2D";
            else if (achar.equals("$")) formula2 = formula2 + "%24";
            else if (achar.equals(",")) formula2 = formula2 + "%2C";
            else formula2 = formula2 + achar;
        }
        return formula2;
    }
}
