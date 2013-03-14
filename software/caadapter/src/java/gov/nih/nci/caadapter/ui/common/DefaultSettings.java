/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.SingleFileFilter;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage ;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class defines a list of default settings for GUI Settings.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.12 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultSettings
{

	private static final String PROPERTY_FONT = "Font";
	public static final Font DEFAULT_FONT;

	private static final Object[] uiDefaults = {PROPERTY_FONT, new Font("Sans Serif", Font.PLAIN, 12),
			//		PROPERTY_FONT, new Font("Serif", Font.PLAIN, 14),
			//		"Red", Color.red,
			//		"Yellow", Color.yellow,
			//		"Blue", Color.blue
	};

	private static final UIDefaults newDefaults = new UIDefaults(uiDefaults);
	private static JFileChooser defaultFileChooser;
	private static File lastPathLocation;

	static
	{
		DEFAULT_FONT = (Font) newDefaults.get(PROPERTY_FONT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Config.FRAME_DEFAULT_HEIGHT = (int) screenSize.getHeight() - 60;//considering the existence of underneath task bar.
		Config.FRAME_DEFAULT_WIDTH = (int) screenSize.getWidth() - 20;
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
			Log.logException(e, e);
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
			lastPathLocation = new File(FileUtil.getUIWorkingDirectoryPath());
		}
		return getUserInputOfFileFromGUI(parentComponent, lastPathLocation.getAbsolutePath(), fileExtension, title, saveMode, checkDuplicate);
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
				if (GeneralUtilities.areEqual(fileFilter, fileChooser.getFileFilter()))
				{//if and only if the currently used file filter in fileChooser is the same as the given fileFilter.
					file = FileUtil.appendFileNameWithGivenExtension(file, ((SingleFileFilter)fileFilter).getExtension(), true);
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
	 * @param imageFileName the name of the file, does not need path information
	 * @return the image retrieved from the system.
	 */
	public static final Image getImage(String imageFileName)
	{
		String imgFilePath="images/";
		imgFilePath = imgFilePath + imageFileName;
		//Thread.currentThread().getContextClassLoader().getResource( imgFilePath );

//		URL imgUrl=Thread.currentThread().getContextClassLoader().getResource( imgFilePath );
//		return Toolkit.getDefaultToolkit().createImage(imgUrl);
		Image rtnImg=null;
		//URL imgUrl=ClassLoader.getSystemResource(imgFilePath);
		try {
			InputStream imgStream=DefaultSettings.class.getClassLoader().getResource(imgFilePath).openStream();
			BufferedImage bfImage=ImageIO.read(imgStream);
			rtnImg=(Image)bfImage;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.logException("Failed to load image:"+imageFileName, e);
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
			splitPane.setDividerSize(Config.DEFAULT_DIVIDER_SIZE);
		}
	}


	/**
	 * Currently utilize JOptionPane to report any given throwable to UI.
	 *
	 * @param t					 the throwable
	 * @param alternativeMessageStr if null or empty, just ignore;
	 * @param parentComponent
	 * @param substitute			if true and alternativeMessageStr is not null or empty,, will use alternativeMessageStr for display other than the error message within the Throwable.
	 * @param supressToUI		   if true, the message will not report to UI; otherwise, it will report to UI.
	 */
	public static void reportThrowableToLogAndUI(Object sender, Throwable t, String alternativeMessageStr, Component parentComponent, boolean substitute, boolean supressToUI)
	{
		String reportMsg = null;
		if (substitute && !GeneralUtilities.isBlank(alternativeMessageStr))
		{
			reportMsg = alternativeMessageStr;
		}
		else
		{
			if (t instanceof Error || t instanceof RuntimeException)
			{//unchecked exception occurred. Just report the nice one
				Message msg = MessageResources.getMessage("GEN0", new Object[]{t.getMessage()});
				reportMsg = msg.toString();
			}
			else
			{//checked exception, just report directly.
				if (t != null)
				{//throwable is not null, pick message from it
					String message = t.getMessage();
					if (GeneralUtilities.isBlank(message))
					{//if its own message is empty, pick from stack trace.
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						t.printStackTrace(pw);
						pw.flush();
						reportMsg = sw.toString();
					}
					else
					{//otherwise, use its message directly.
						reportMsg = message;
					}
				}
				else
				{
					reportMsg = "Unknown Error.";
				}
			}
		}
		if (t instanceof Error || t instanceof RuntimeException)
		{//unchecked exception occurred. Just report the nice one
			//			Log.logError(sender, reportMsg);
			Log.logError(sender, t);
		}
		else if (t != null)
		{//checked exception, just report directly.
			Log.logException(sender, reportMsg, t);
		}
		else
		{//t is null, so log the report msg
			Log.logInfo(sender, reportMsg);
		}

		if (!supressToUI)
		{
			if (parentComponent != null && !(parentComponent instanceof Container))
			{
				JOptionPane.showMessageDialog(parentComponent.getParent(), reportMsg, "Exception Occurred(1)", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(parentComponent, reportMsg, "Exception Occurred(2)", JOptionPane.ERROR_MESSAGE);
			}
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
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.11  2008/05/29 00:30:18  umkis
 * HISTORY      : add a FileChooser option 'DIRECTORY_FILE' onto getUserInputOfFileFromGUI().
 * HISTORY      :
 * HISTORY      : Revision 1.10  2008/04/01 21:07:18  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/01/31 21:44:22  umkis
 * HISTORY      : csv converting from multi message included v2 file.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/11/28 15:05:40  wangeug
 * HISTORY      : setup UI environment with DefaultSettings implementation
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/11/14 20:55:57  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/09/18 15:25:52  wangeug
 * HISTORY      : modify FileChooser to select Directory
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/10 16:48:24  wangeug
 * HISTORY      : make File choose work with multiple extensions
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/14 20:26:18  umkis
 * HISTORY      : add a comment about 'imageFileName'
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/06/12 15:47:59  wangeug
 * HISTORY      : load image with "getResource()" as an input stream and read the image with bufferedImage
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/07 15:15:14  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.40  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.39  2006/01/09 17:49:11  umkis
 * HISTORY      : change the icon image of mainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.38  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.37  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.36  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.35  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.34  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/11/23 20:03:30  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/11/16 22:33:50  jiangsc
 * HISTORY      : Update License Information
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/11/08 23:09:48  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/10/31 21:31:52  jiangsc
 * HISTORY      : Fix to Defect 164 and 162.
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/10/21 15:12:19  jiangsc
 * HISTORY      : Reporting error enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/10/18 14:44:04  jiangsc
 * HISTORY      : Fixed the defect that caused file name not populated in the FileChooser upon double clicking.
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/10/14 21:03:50  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/10/13 17:37:42  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/13 15:44:39  jiangsc
 * HISTORY      : Re-engineered exception reporting to DefaultSettings class.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/12 21:42:46  jiangsc
 * HISTORY      : Added validation on invalid file type.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/10 20:49:02  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/04 20:22:22  jiangsc
 * HISTORY      : Updated FileChooser with consistent look and feel.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/04 17:20:30  jiangsc
 * HISTORY      : Keep Open/Save dialog remember last location.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/09/29 22:16:02  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/09/16 23:19:08  chene
 * HISTORY      : Database prototype GUI support, but can not be loaded
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/11 22:10:37  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/04 22:22:24  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
