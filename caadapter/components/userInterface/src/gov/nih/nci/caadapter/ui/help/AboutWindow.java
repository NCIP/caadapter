/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/AboutWindow.java,v 1.5 2007-09-07 14:25:53 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caadapter Software License, Version 1.3
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


package gov.nih.nci.caadapter.ui.help;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;

import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;

/**
 * This class defines the help window.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caadapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2007-09-07 14:25:53 $
 */
public class AboutWindow extends JWindow //implements ActionListener
  {

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AboutWindow.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/AboutWindow.java,v 1.5 2007-09-07 14:25:53 umkis Exp $";


    private JEditorPane mainView;
    private JWindow thisWindow;
    private String dirSeparater;
    private String commonPath;
    private String commonURIPath = "file:///";
    private String env;

      private String ERROR_TAG = "ERROR";
      private String ERROR_MESSAGE_FILE_NOT_FOUND = ERROR_TAG + " : File Not Found";
      private String ERROR_MESSAGE_FILE_READING_ERROR = ERROR_TAG + " : File Reading Error";
      private String ERROR_MESSAGE_FILE_WRITING_ERROR = ERROR_TAG + " : File Writing Error";
      private String ERROR_MESSAGE_MALFORMED_URL = ERROR_TAG + " : Malformed URL";
      private String SYSTEM_PROPERTY_OS_NAME = "os.name";
      private String SYSTEM_PROPERTY_WINDOWS_OS_NAME = "WINDOWS";
      private String RUNNING_DIRECTORY_NAME = "etc";
      //private String IMAGE_DIRECTORY_PATH = "../images/";
      private String LICENSE_DIRECTORY_PATH = "license";
      private int DEFAULT_SCREEN_WIDTH = 557;
      private int DEFAULT_SCREEN_HEIGHT = 409;
      private int DEFAULT_SCREEN_WIDTH_DEFFER = 6;
      private int DEFAULT_SCREEN_HEIGHT_DEFFER = 6;
      private String VERSION_TAG_IN_SOURCE_HTML_FILE = "VERSION ";
      private String VERSION_MARKER_IN_SOURCE_HTML_FILE = "<!--@@:VERSION_MARKER;Don't touch this Paragraph-->";
      private String BUILD_TAG_IN_SOURCE_HTML_FILE = "BUILD# ";
      private String BUILD_MARKER_IN_SOURCE_HTML_FILE = "<!--$$:BUILD_MARKER;Don't touch this Paragraph-->";
      private String JDK_VERSION_TAG_IN_SOURCE_HTML_FILE = "JDK ";
      private String JDK_VERSION_MARKER_IN_SOURCE_HTML_FILE = "<!--$$:JDK_VERSION_MARKER;Don't touch this Paragraph-->";
      private String COPYRIGHT_YEARS_TAG_IN_SOURCE_HTML_FILE = "Copyright © ";
      private String COPYRIGHT_YEARS_MARKER_IN_SOURCE_HTML_FILE = "<!--$$:COPYRIGHT_YEARS_MARKER;Don't touch this Paragraph-->";
      private String BACKGROUND_FILE_NAME_TAG_IN_SOURCE_HTML_FILE = "<body background=\"";
      private String BACKGROUND_FILE_NAME_MARKER_IN_SOURCE_HTML_FILE = "<!--$$:BACKGROUND_FILE_NAME_MARKER;Don't touch this Paragraph-->";
      private String WINDOWS_MARKER_IN_SOURCE_HTML_FILE = "<!--&&:Variable Area;Don't touch this Paragraph-->";
      private String EXIT_HYPERLINK_IN_SOURCE_HTML_FILE = "EXIT.HTML";
      private String LICENSE_INFORMATION_HYPERLINK_IN_SOURCE_HTML_FILE = "licenseinformation";
      private String WEB_BROWSER_COMMAND_ON_WINDOW_OS = "C:/Program Files/Internet Explorer/IEXPLORE.EXE ";

    public AboutWindow(JFrame mainFrame)
      {
        super(mainFrame);
        new AboutWindow();
      }
    public AboutWindow()
      {
        dirSeparater = File.separator;
        thisWindow = this;
        mainView = new JEditorPane("text/html", "<html><head><title>help</title></head><body><font color='blue'>Start</font></body></html>");
        commonPath = FileUtil.getWorkingDirPath() + dirSeparater + RUNNING_DIRECTORY_NAME + dirSeparater;

        for(int i=0;i<commonPath.length();i++)
          {
            String achar = commonPath.substring(i, i+1);
            if (achar.equals(dirSeparater)) commonURIPath = commonURIPath + "/";
            else commonURIPath = commonURIPath + achar;
          }
        JScrollPane js2 = new JScrollPane(mainView);

        env = System.getProperty(SYSTEM_PROPERTY_OS_NAME);//"os.name");
        int width = DEFAULT_SCREEN_WIDTH;
        int height = DEFAULT_SCREEN_HEIGHT;
        if (env.toUpperCase().indexOf(SYSTEM_PROPERTY_WINDOWS_OS_NAME) >= 0)
        {
            width = width + DEFAULT_SCREEN_WIDTH_DEFFER;
            height = height + DEFAULT_SCREEN_HEIGHT_DEFFER;
        }

        js2.setBounds(0, 0, width, height);
        mainView.setEditable(false);
        this.add(js2);
        //JLabel jlb = new JLabel(" ");
        //jlb.setBounds(0, 0, 20, 30);
        //this.add(jlb);
        setSize(width, height);   
        String st = setVersionAndBuildNumber(commonPath + Config.DEFAULT_ABOUT_WINDOW_DATA_FILENAME);

        try
          {
            if (st.startsWith(ERROR_TAG))
                mainView.setText("<html><head><title>" + ERROR_TAG + "</title></head>"
                               + "<body><font size='5' color='red'>" + st + "</font></body></html>");
            else mainView.setPage(new URL("file:///" + st));

          }
        catch(Throwable ie)
          {
            mainView.setText("<html><head><title>help</title></head><body><font color='blue'>"
                             + ERROR_MESSAGE_MALFORMED_URL + " : "+st+"</font></body></html>");
          }

        mainView.addMouseListener
          (
            new MouseListener()
              {
                public void mouseExited(MouseEvent e) { }
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) { }
                public void mousePressed(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {thisWindow.dispose();}
              }
          );

        mainView.addHyperlinkListener
          (
            new HyperlinkListener()
              {
                public void hyperlinkUpdate(HyperlinkEvent e)
                  {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                      {
                        JEditorPane pane = (JEditorPane) e.getSource();
                        if (e instanceof HTMLFrameHyperlinkEvent)
                          {
                            HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                            HTMLDocument doc = (HTMLDocument)pane.getDocument();
                            doc.processHTMLFrameHyperlinkEvent(evt);
                          }
                        else
                          {
                            String sURL = (e.getURL()).toString();
                            if ((sURL.toUpperCase()).indexOf(EXIT_HYPERLINK_IN_SOURCE_HTML_FILE) > 0) thisWindow.dispose();
                            else if ((sURL.toUpperCase()).indexOf(LICENSE_INFORMATION_HYPERLINK_IN_SOURCE_HTML_FILE.toUpperCase()) > 0)
                            {
                                String licenseHTML_URL = generateLicenseInformationHTML(LICENSE_DIRECTORY_PATH);
                                if (licenseHTML_URL.startsWith(ERROR_TAG)) new HTMLViewer(commonURIPath + LICENSE_INFORMATION_HYPERLINK_IN_SOURCE_HTML_FILE + ".html", 700, 500, "caadapter License Information");
                                else new HTMLViewer(licenseHTML_URL, 700, 500, "caAdapter License Information");
                            }
                            else
                              {
                                  edu.stanford.ejalbert.BrowserLauncher brLauncher = null;
                                  try
                                  {
                                      brLauncher = new edu.stanford.ejalbert.BrowserLauncher(null);
                                      brLauncher.openURLinBrowser(sURL);
                                  }
                                  catch(UnsupportedOperatingSystemException ue)
                                  {
                                      new HTMLViewer(sURL);
                                  }
                                  catch(BrowserLaunchingInitializingException ue)
                                  {
                                      new HTMLViewer(sURL);
                                  }
                                  catch(BrowserLaunchingExecutionException ue)
                                  {
                                      new HTMLViewer(sURL);
                                  }


        	                    /*
                                if (env.toUpperCase().indexOf(SYSTEM_PROPERTY_WINDOWS_OS_NAME) < 0) new HTMLViewer(sURL);
                                else
                                  {
                                    try
                                      {
                                        Runtime.getRuntime().exec(WEB_BROWSER_COMMAND_ON_WINDOW_OS + sURL);
                                      }
                                    catch(IOException ie)
                                      {
                                        new HTMLViewer(sURL);
                                      }
                                  }
                                */
                              }
                          }
                      }
                  }
              }
          );

        addWindowListener(new WinCloseExit(this));
      }

    private String setVersionAndBuildNumber(String commonPath)
      {
        String tot = "";
        boolean normal = true;
        FileReader fr = null;
        BufferedReader br = null;
        try { fr = new FileReader(commonPath); }
        catch(FileNotFoundException fe) 
        { 
        	//fe.printStackTrace();
            normal = false;
            //return ERROR_MESSAGE_FILE_NOT_FOUND+":"+commonPath;
        }

        if (!normal)
        {
            String aboutWinHTMLFileClassPath = "etc/aboutwin.html";
            ClassLoaderUtil loader = null;
            try
            {
                loader = new ClassLoaderUtil(aboutWinHTMLFileClassPath);
            }
            catch(IOException ie)
            {
                return ERROR_TAG + " : Class Loading failure with this path => " + aboutWinHTMLFileClassPath;
            }

            List<String> licenseFileNames = loader.getFileNames();

            if (licenseFileNames.size() == 0) return ERROR_TAG + " : No File in this Class path => " + aboutWinHTMLFileClassPath;
            for (int i=0;i<licenseFileNames.size();i++)
            {
                try
                {
                    fr = new FileReader(loader.getFileName(i));
                    break;
                }
                catch(FileNotFoundException fe)
                {
                    fr = null;
                }
            }
            if (fr == null) return ERROR_TAG + " : No File Reader Object in this Class path => " + aboutWinHTMLFileClassPath;
        }

        br = new BufferedReader(fr);
        String readLineOfFile = "";

        try { while((readLineOfFile=br.readLine())!=null) tot = tot + readLineOfFile + "\r\n"; }
        catch(IOException ie) { return ERROR_MESSAGE_FILE_READING_ERROR; } // "ERROR : File Reading Error"; }


        tot = replaceTaggedContent(tot, VERSION_TAG_IN_SOURCE_HTML_FILE, Config.CAADAPTER_VERSION, VERSION_MARKER_IN_SOURCE_HTML_FILE);
        tot = replaceTaggedContent(tot, BUILD_TAG_IN_SOURCE_HTML_FILE, Config.CAADAPTER_BUILD_NUMBER, BUILD_MARKER_IN_SOURCE_HTML_FILE);
        tot = replaceTaggedContent(tot, JDK_VERSION_TAG_IN_SOURCE_HTML_FILE, Config.JDK_VERSION, JDK_VERSION_MARKER_IN_SOURCE_HTML_FILE);
        tot = replaceTaggedContent(tot, COPYRIGHT_YEARS_TAG_IN_SOURCE_HTML_FILE, Config.COPYRIGHT_YEARS, COPYRIGHT_YEARS_MARKER_IN_SOURCE_HTML_FILE);
        //replace background image path
        URL bkgUrl=ClassLoader.getSystemResource("images/"+Config.ABOUT_WINDOW_BACKGROUND_IMAGE_FILENAME);
        //tot = replaceTaggedContent(tot, BACKGROUND_FILE_NAME_TAG_IN_SOURCE_HTML_FILE, IMAGE_DIRECTORY_PATH + Config.ABOUT_WINDOW_BACKGROUND_IMAGE_FILENAME + "\">", BACKGROUND_FILE_NAME_MARKER_IN_SOURCE_HTML_FILE);
        tot = replaceTaggedContent(tot, BACKGROUND_FILE_NAME_TAG_IN_SOURCE_HTML_FILE, bkgUrl+ "\">", BACKGROUND_FILE_NAME_MARKER_IN_SOURCE_HTML_FILE);
        //replace ok Button image path
        String okImageName="OK_Button.png";
        URL okButtonUrl=ClassLoader.getSystemResource("images/"+okImageName);
        int okNameStart=tot.indexOf(okImageName);
        tot=tot.substring(0,okNameStart)
        	+okButtonUrl+tot.substring(okNameStart+okImageName.length());
        
        if (env.toUpperCase().indexOf(SYSTEM_PROPERTY_WINDOWS_OS_NAME) < 0) tot = tot.replace(WINDOWS_MARKER_IN_SOURCE_HTML_FILE, "<br><br>");
        FileWriter fw = null;
        //String displayFileName = commonPath.replace(Config.DEFAULT_ABOUT_WINDOW_DATA_FILENAME, Config.HELP_TEMPORARY_FILENAME_FIRST);
        String displayFileName = FileUtil.getTemporaryFileName("html");
                try
        {
            fw = new FileWriter(displayFileName);
            fw.write(tot);
            fw.close();
        }
        catch(IOException ie)
        {
            return ERROR_MESSAGE_FILE_WRITING_ERROR + " : " + displayFileName; //"ERROR : File Writing Error";
        }
        File aFile = new File(displayFileName);
        aFile.deleteOnExit();
        return displayFileName;
    }
    private String generateLicenseInformationHTML(String licensePath)
    {
        ClassLoaderUtil loader = null;
        try
        {
            loader = new ClassLoaderUtil(licensePath);
        }
        catch(IOException ie)
        {
            return ERROR_TAG + " : Class Loading failure with this path => " + licensePath;
        }
//        File licensePathFile = new File(FileUtil.getETCDirPath() + File.separator + licensePath);
//        if (!licensePathFile.exists()) return ERROR_TAG + " : Not Exist";
//        if (!licensePathFile.isDirectory()) return ERROR_TAG + " : Not Directory";
        String displayFileName = commonPath + Config.HELP_TEMPORARY_FILENAME_SECOND;
        String mainContent = "<a name='top'><h2><font color='blue'>caadapter License Information</font></h2></a><br><br> This " +
                "license information is automatically merged and generated from the following license files in " +
                licensePath + " directory.<br><br>%%XX<br><br><hr width=\"80%\"><br>";
        List<String> licenseFileNames = loader.getFileNames();
        String fileList = "";
        FileReader fr = null;
        BufferedReader br = null;
        int count = 0;
        if (licenseFileNames.size() == 0) return ERROR_TAG + " : No File";
        for (int i=0;i<licenseFileNames.size();i++)
        {

            try { fr = new FileReader(loader.getFileName(i)); }
            catch(FileNotFoundException fe)
            {
        	    continue;
            }

            br = new BufferedReader(fr);

            count++;

            String readLineOfFile = "";
            String buffer = "";
            try
            {
                mainContent = mainContent + "<a name='a" + i + "'><b>Source File</b></a> : " + licensePath + File.separator + loader.getName(i) + "<br><br>";
                while(true)
                {
                    readLineOfFile=br.readLine();
                    if (readLineOfFile==null) break;


                    String c = readLineOfFile.trim();
                    if (c.equals("")) mainContent = mainContent + "<br><br>";
                    else mainContent = mainContent + " " + c;
                }
                mainContent = mainContent + "<br><br><a href='#top'>go to top</a><br><hr width=\"80%\"><br><br>";
                fileList = fileList + "&nbsp;&nbsp;&nbsp;&nbsp;<a href='#a"+i+"'>" + licensePath + File.separator + loader.getName(i) + "</a><br>";
            }
            catch(IOException ie)
            {
                return ERROR_MESSAGE_FILE_READING_ERROR; // "ERROR : File Reading Error"; }
            }
        }
        if (count == 0) return ERROR_TAG + " : No File";
        FileWriter fw = null;
        try
          {
              fr.close();
              br.close();
          }
        catch(IOException ie)
          {}
        try
          {
            fw = new FileWriter(displayFileName);
            fw.write("<html><head><title>caadapter Licence Agreement</title></head><body><br><br>" + mainContent.replace("%%XX", fileList) + "<br></body></html>");
            fw.close();
          }
        catch(IOException ie)
          {

            return ERROR_MESSAGE_FILE_WRITING_ERROR; //"ERROR : File Writing Error";
          }
        String achar = "";
        String cc = "";
        for (int i=0;i<displayFileName.length();i++)
        {
            achar = displayFileName.substring(i, i+1);
            if (achar.equals(File.separator)) cc = cc + "/";
            else cc = cc + achar;
        }
        return "file:///" + cc;
    }
    private String replaceTaggedContent(String tot, String tagOfContent, String replacingStr, String tagOfTail)
    {
        try
        {
            return tot.substring(0, ((tot.toUpperCase()).indexOf(tagOfContent.toUpperCase()) + tagOfContent.length()))
                        + replacingStr + tot.substring(tot.toUpperCase().indexOf(tagOfTail.toUpperCase()));
        }
        catch(StringIndexOutOfBoundsException soobe)
        {
            return tot;
        }
    }

    class WinCloseExit extends WindowAdapter //implements WindowListener        //extends WindowAdapter
      {
        AboutWindow tt;
        WinCloseExit(AboutWindow st) { tt = st; }
//        public void windowClosing(WindowEvent e)  {}
        public void windowClosing(WindowEvent e)  {tt.dispose();}
        public void windowClosed(WindowEvent e)  {}
        public void windowOpened(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
//        public void windowDeactivated(WindowEvent e) {tt.dispose();  	}
      
      }
  }
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/09/06 21:49:49  umkis
 * HISTORY      : caAdapter version => 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/06 21:33:18  umkis
 * HISTORY      : class loader as the finding method for aboutwin.html instead of file path
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/15 05:25:23  umkis
 * HISTORY      : License Information upgrade (using ClassLoaderUtil)
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.34  2007/01/08 20:49:03  umkis
 * HISTORY      : calling OS's web browser using Stanford Univ's product
 * HISTORY      :
 * HISTORY      : Revision 1.33  2006/12/18 20:31:44  wuye
 * HISTORY      : new size for aboutwindow
 * HISTORY      :
 * HISTORY      : Revision 1.32  2006/12/12 23:13:59  umkis
 * HISTORY      : add JDK_VERSION and COPYRIGHT_YEARS
 * HISTORY      :
 * HISTORY      : Revision 1.31  2006/08/03 19:09:21  umkis
 * HISTORY      : Whenever click 'license information' displaying content is generated from files in 'license' directory.
 * HISTORY      :
 * HISTORY      : Revision 1.30  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/05/02 18:22:56  chene
 * HISTORY      : Add isReference interface
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/09 20:33:45  umkis
 * HISTORY      : Protect from StringIndexOutOfBoundsException when the source HTML file editing.
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/01/09 18:18:13  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/01/05 16:30:38  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/01/05 16:27:22  umkis
 * HISTORY      : Remove Hard code
 * HISTORY      :
 * HISTORY      : Revision 1.23  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/12/23 22:34:53  umkis
 * HISTORY      : Adaptation for Linux env
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/14 15:39:22  umkis
 * HISTORY      : defect# 232
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/11/30 20:47:31  umkis
 * HISTORY      : defect# 212
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/07 23:59:24  umkis
 * HISTORY      : image change from HL7SDK to caadapter
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/02 22:36:06  chene
 * HISTORY      : change "\\" to "/"
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/02 21:12:18  umkis
 * HISTORY      : code re-organized
 * HISTORY      :
 */
