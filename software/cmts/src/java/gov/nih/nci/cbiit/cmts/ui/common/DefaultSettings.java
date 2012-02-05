/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.common;

import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmts.ui.util.SingleFileFilter;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

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
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.net.URL;

/**
 * This class defines a list of default settings for GUI Settings.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.6 $
 * @date       $Date: 2009-11-04 19:08:36 $
 *
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
    public static final String MAP_FILE_DEFAULT_EXTENTION = ".map";
    public static final String XML_DATA_FILE_DEFAULT_EXTENSTION = ".xml";
    public static final String CSV_DATA_FILE_DEFAULT_EXTENSTION = ".csv";
    public static final String HL7_V2_DATA_FILE_DEFAULT_EXTENSTION = ".hl7";
    public static final String CSV_METADATA_FILE_DEFAULT_EXTENTION = ".scs";
    public static final String OPEN_DIALOG_TITLE_FOR_CSV_FILE = "Open CSV File";
    public static final String OPEN_DIALOG_TITLE_FOR_HL7_FILE = "Open HL7 v2 File";
    public static final String OPEN_DIALOG_TITLE_FOR_MAP_FILE = "Open Mapping File";
    public static final Color DEFAULT_READ_ONLY_BACK_GROUND_COLOR = Color.LIGHT_GRAY;
    public static final String FUNCTION_DEFINITION_FILE_LOCATION = "etc/functions/corefunc.xml";
    private static JFileChooser defaultFileChooser;
    private static File lastPathLocation;
    public static int FRAME_DEFAULT_HEIGHT = 780;
    public static int FRAME_DEFAULT_WIDTH = 1080;
    public static int DEFAULT_DIVIDER_SIZE = 6;

    static
    {
        DEFAULT_FONT = (Font) newDefaults.get(PROPERTY_FONT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME_DEFAULT_HEIGHT = (int) screenSize.getHeight() - 60;//considering the existence of underneath task bar.
        FRAME_DEFAULT_WIDTH = (int) screenSize.getWidth() - 20;
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
            e.printStackTrace();
            //Log.logException(e, e);
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
                //System.out.println("CCCCC FF 1 : " + file.getName() + ", path=" + file.getAbsolutePath());
                String fileName = file.getName();
                String path = file.getAbsolutePath();
                boolean isFullPath = false;
                if ((!fileName.startsWith("\""))&&(path.endsWith("\"")))
                {
                    if (path.startsWith("\""))
                    {
                        fileName = path;
                    }
                    else
                    {
                        int idx = path.substring(0, path.length()-1).indexOf("\"");
                        if (idx < 0)
                        {
                            JOptionPane.showMessageDialog(parentComponent, "Invalid usage of '\"' character in file name. (0) : " + path, "Invalid file name", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                        else
                        {
                            fileName = path.substring(idx);
                            isFullPath = true;
                            //System.out.println("CCCCC FF 1 : " + fileName + ", path=" + file.getAbsolutePath());
                        }
                    }
                }
                if (fileName.startsWith("\""))
                {
                    if (!fileName.endsWith("\""))
                    {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid usage of '\"' character in file name. (1) : " + fileName, "Invalid file name", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    String fName = fileName.substring(1, fileName.length() - 1).trim();

                    if (fName.indexOf("\"") >= 0)
                    {
                        JOptionPane.showMessageDialog(parentComponent, "Invalid usage of '\"' character in file name. (2) : " + fName, "Invalid file name", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    if (fName.equals(""))
                    {
                        JOptionPane.showMessageDialog(parentComponent, "File Name is null. Please select a file again.", "Null file name", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    File parent = file.getParentFile();
                    String parPath = parent.getAbsolutePath();
                    if (!parPath.endsWith(File.separator)) parPath = parPath + File.separator;

                    if (isFullPath) file = new File(fName);
                    else file = new File(parPath + fName);

                    File par = file.getParentFile();
                    if ((!par.exists())||(!par.isDirectory()))
                    {
                        JOptionPane.showMessageDialog(parentComponent, "This directory is not exist. : " + par.getAbsolutePath(), "Not exist dirctory", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                else
                {
                    //since this point on, file will not be null.
                    for(FileFilter fileFilter:fileFilters)
                    {
                        if (GeneralUtilities.areEqual(fileFilter, fileChooser.getFileFilter()))
                        {//if and only if the currently used file filter in fileChooser is the same as the given fileFilter.
                            if (!fileFilter.accept(file))
                            {
                                String ext = ((SingleFileFilter)fileFilter).getExtension();
                                if (ext.endsWith("*")) ext = ext.substring(0, ext.length()-1);
                                file = FileUtil.appendFileNameWithGivenExtension(file, ext, true);
                            }
                            break;
                        }
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
        return file;
    }

    public static final Image getMainframeImage()
    {
        //using default imageFile
        String defaultImageName="caAdapter-icon.gif";
        return getImage(defaultImageName);
    }

    /**
     * @param imageFileName the name of the file, does not need path information
     * @return the image retrieved from the system.
     */
    public static final Image getImage(String imageFileName)
    {
        Image rtnImg=null;
        try
        {
            File file = new File(imageFileName);
            if ((file.exists())&&(file.isFile()))
            {
                BufferedImage bfImage=ImageIO.read(file);
                rtnImg=(Image)bfImage;
            }
        }
        catch(IOException ie)
        {
            rtnImg=null;
        }
        if (rtnImg != null) return rtnImg;

        String imgFilePath="images/";
        imgFilePath = imgFilePath + imageFileName;
        //Thread.currentThread().getContextClassLoader().getResource( imgFilePath );

//		URL imgUrl=Thread.currentThread().getContextClassLoader().getResource( imgFilePath );
//		return Toolkit.getDefaultToolkit().createImage(imgUrl);

        //URL imgUrl=ClassLoader.getSystemResource(imgFilePath);
        try {
//            URL url = DefaultSettings.class.getClassLoader().getResource(imgFilePath);
//            if (url != null)
//            {
//                InputStream imgStream=DefaultSettings.class.getClassLoader().getResource(imgFilePath).openStream();
//                BufferedImage bfImage=ImageIO.read(imgStream);
//                rtnImg=(Image)bfImage;
//            }

            Enumeration<URL> urls = FileUtil.getResources(imgFilePath);
            if (urls != null)
            {
                URL uu = null;

                while(urls.hasMoreElements())
                {
                    URL url = urls.nextElement();
                    if ((uu == null)&&(url != null)) uu = url;
                }
                if (uu != null)
                {
                    InputStream imgStream=uu.openStream();
                    BufferedImage bfImage=ImageIO.read(imgStream);
                    rtnImg=(Image)bfImage;
                }
                else
                {
                    System.out.println("Not found this resource : " + imgFilePath);
                    return null;
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Log.logException("Failed to load image:"+imageFileName, e);
            e.printStackTrace();
        }
        return rtnImg;
    }

    /**
     * Currently will setOneTouchExpandable to be true and setDividerSize to be DEFAULT_DIVIDER_SIZE.
     *
     * @param splitPane
     */
    public static final void setDefaultFeatureForJSplitPane(JSplitPane splitPane)
    {
        if (splitPane != null)
        {
            splitPane.setBorder(BorderFactory.createEmptyBorder());
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerSize(DEFAULT_DIVIDER_SIZE);
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

                reportMsg = t.getMessage();
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
        t.printStackTrace();
        if (t instanceof Error || t instanceof RuntimeException)
        {//unchecked exception occurred. Just report the nice one
            //			Log.logError(sender, reportMsg);
            //Log.logError(sender, t);
        }
        else if (t != null)
        {//checked exception, just report directly.
            //Log.logException(sender, reportMsg, t);
        }
        else
        {//t is null, so log the report msg
            //Log.logInfo(sender, reportMsg);
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

    public static Object showListChoiceDialog(Component parent, String title, String msg, Object[] choices){
        JOptionPane p = new JOptionPane(msg, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = p.createDialog(parent, title);
        Arrays.sort(choices);
        JComboBox c = new JComboBox( choices);
        dlg.getContentPane().add(c,BorderLayout.NORTH);
        dlg.pack();
        dlg.setVisible(true);
        if((p.getValue() != null)&&(p.getValue().equals(Integer.valueOf(JOptionPane.OK_OPTION)))) {
            return c.getSelectedItem();
        }
        return null;

    }
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.5  2009/10/15 18:35:41  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.4  2008/12/29 22:18:18  linc
 * HISTORY: function UI added.
 * HISTORY:
 * HISTORY: Revision 1.3  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

