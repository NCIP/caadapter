/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.help;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

/**
 * This class defines frame to view HTML content.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */

public class HTMLViewer extends JDialog
{
  /**
   * Logging constant used to identify source of log entry, that could be later used to create
   * logging mechanism to uniquely identify the logged class.
   */
  private static final String LOGID = "$RCSfile: HTMLViewer.java,v $";

  /**
   * String that identifies the class version and solves the serial version UID problem.
   * This String is for informational purposes only and MUST not be made final.
   *
   * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
   */
  public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HTMLViewer.java,v 1.5 2008-09-26 20:35:27 linc Exp $";


  JEditorPane mainView;
  JDialog thisWindow;
  JButton closeButton;
  JButton holdButton;
  JButton releaseButton;

  String htmlAddress;
  String htmlContent;
  int screenWidth = Config.FRAME_DEFAULT_WIDTH - 30;
  int screenHeight = Config.FRAME_DEFAULT_HEIGHT - 30;
  boolean protectDisposeTag = false;
  String labelName = " URL ";
  String titleName = "";
  String fileName = "";
  int fromTag = 0;

  public HTMLViewer(String addr)
    {
      htmlAddress = addr;
      titleName = addr;
      mainDisplaying();
    }
  public HTMLViewer(String addr, int width, int height, JDialog owner)
    {
      super(owner);
      if (width < 0) width = screenWidth + width;
      if (height < 0) height = screenHeight + height;
      if (width <= 0) width = screenWidth;
      if (height <= 0) height = screenHeight;

      screenWidth = width;
      screenHeight = height;
      htmlAddress = addr;
      titleName = addr;
      mainDisplaying();
    }
  public HTMLViewer(String addr, int width, int height)
    {
      if (width < 0) width = screenWidth + width;
      if (height < 0) height = screenHeight + height;
      if (width <= 0) width = screenWidth;
      if (height <= 0) height = screenHeight;

      screenWidth = width;
      screenHeight = height;
      htmlAddress = addr;
      titleName = addr;
      mainDisplaying();
    }
  public HTMLViewer(String addr, int width, int height, String title)
    {
      if (width < 0) width = screenWidth + width;
      if (height < 0) height = screenHeight + height;
      if (width <= 0) width = screenWidth;
      if (height <= 0) height = screenHeight;

      screenWidth = width;
      screenHeight = height;
      htmlAddress = addr;
      titleName = title;
      mainDisplaying(title.trim().equalsIgnoreCase("caAdapter License Information"), false);

    }

  public HTMLViewer(String content, int width, int height, String title, boolean isString)
  {
    if (width < 0) width = screenWidth + width;
    if (height < 0) height = screenHeight + height;
    if (width <= 0) width = screenWidth;
    if (height <= 0) height = screenHeight;

    screenWidth = width;
    screenHeight = height;
    if(isString)
    	htmlContent = content;
    else
    	htmlAddress = content;
    titleName = title;
    mainDisplaying(title.trim().equalsIgnoreCase("caAdapter License Information"), true);

  }

  public HTMLViewer(String mode, String text, int width, int height)
    {
      if (width < 0) width = screenWidth + width;
      if (height < 0) height = screenHeight + height;
      if (width <= 0) width = screenWidth;
      if (height <= 0) height = screenHeight;
      displayingText(mode, text, width, height);
    }
  public HTMLViewer(String mode, String text, int width, int height, JDialog owner)
    {
      super(owner);
      if (width < 0) width = screenWidth + width;
      if (height < 0) height = screenHeight + height;
      if (width <= 0) width = screenWidth;
      if (height <= 0) height = screenHeight;
      displayingText(mode, text, width, height);
    }

  public HTMLViewer()
    {
        new HTMLViewer("http://www.google.com");
    }
  public void mainDisplaying()
    {
        mainDisplaying(false, false);
    }
  public void mainDisplaying(boolean htmlAddressDisplay, boolean isString)
    {
      thisWindow = this;
      mainView = new JEditorPane("text/html", "<html><head><title>help</title></head><body><font color='blue'>Start</font></body></html>");

      this.getContentPane().setLayout(new BorderLayout());
      setTitle(titleName);
      JScrollPane js2 = new JScrollPane(mainView);
      js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      JPanel northPanel = new JPanel(new BorderLayout());
      JTextField jtfHtmlAddress = null;
      JLabel jlLabelName = null;
      if (fromTag == 0)
      {
        jlLabelName = new JLabel(labelName);
        northPanel.add(jlLabelName, BorderLayout.WEST);
        jtfHtmlAddress = new JTextField(htmlAddress);
        northPanel.add(jtfHtmlAddress, BorderLayout.CENTER);
        northPanel.add(new JLabel(" "), BorderLayout.EAST);
      }
      else if (fromTag == 2)
      {
        jlLabelName = new JLabel(labelName);
        northPanel.add(jlLabelName, BorderLayout.WEST);
        jtfHtmlAddress = new JTextField(fileName);
        northPanel.add(jtfHtmlAddress, BorderLayout.CENTER);
        northPanel.add(new JLabel(" "), BorderLayout.EAST);
      }
      getContentPane().add(northPanel, BorderLayout.NORTH);

      getContentPane().add(js2, BorderLayout.CENTER);

      JLabel lb = new JLabel("Any mouse clicking on this viewer will not be met any response.");
      JPanel southPanel = new JPanel(new BorderLayout());
      southPanel.add(lb, BorderLayout.WEST);
      southPanel.add(new JLabel(""), BorderLayout.CENTER);
      JPanel buttonPanel = new JPanel(new BorderLayout());
      closeButton = new JButton("Close");
      buttonPanel.add(closeButton, BorderLayout.EAST);
      holdButton = new JButton("Hold Screen");
      buttonPanel.add(holdButton, BorderLayout.CENTER);
      releaseButton = new JButton("Release");
      buttonPanel.add(releaseButton, BorderLayout.WEST);
      releaseButton.setEnabled(false);
      southPanel.add(buttonPanel, BorderLayout.EAST);
      getContentPane().add(southPanel, BorderLayout.SOUTH);
      if ((jtfHtmlAddress!=null)&&(htmlAddressDisplay))
      {
          jtfHtmlAddress.setVisible(false);
          lb.setVisible(false);
          if (jlLabelName!=null) jlLabelName.setVisible(false);
      }

      closeButton.addActionListener(new ActionListener()
      {
          public void actionPerformed(ActionEvent e)
          {
              thisWindow.dispose();
          }
      });

      holdButton.addActionListener(new ActionListener()
      {
          public void actionPerformed(ActionEvent e)
          {
              protectDisposeTag = true;
              thisWindow.setAlwaysOnTop(true);
              releaseButton.setEnabled(true);
              holdButton.setEnabled(false);
          }
      });
      releaseButton.addActionListener(new ActionListener()
      {
          public void actionPerformed(ActionEvent e)
          {
              protectDisposeTag = false;
              thisWindow.setAlwaysOnTop(false);
              releaseButton.setEnabled(false);
              holdButton.setEnabled(true);
          }
      });
      if (fromTag == 0)
      {
          releaseButton.setVisible(false);
          holdButton.setVisible(false);
      }
      //js2.setBounds(2, 2, screenWidth-10, screenHeight-10);
      //mainView.setBounds(0, 0, 543, 413);
      mainView.setEditable(false);
      //getContentPane().add(mainView);
      getContentPane().add(js2);
      setSize(screenWidth, screenHeight);


      DefaultSettings.centerWindow(this);
      this.setVisible(true);
      try
        {
    	  if(isString)
    		  mainView.setText(htmlContent);
    	  else
    		  mainView.setPage(new URL(htmlAddress));
        }
      catch(IOException ie)
        {
          mainView.setText("<html><head><title>help</title></head><body><font color='blue'>Source File not Found<br>"+ htmlAddress +"</font></body></html>");
        }
      //this.getContentPane().add(new JTextField(htmlAddress), BorderLayout.NORTH);
      //this.getContentPane().add(js2, BorderLayout.CENTER);
      //this.getContentPane().add(new JPanel(), BorderLayout.SOUTH);


     mainView.addMouseListener
       (
         new MouseListener()
         {
           public void mouseExited(MouseEvent e) { }
           public void mouseReleased(MouseEvent e) {}
           public void mouseEntered(MouseEvent e) { }
           public void mousePressed(MouseEvent e) {}
           public void mouseClicked(MouseEvent e) {}

         }
       );


          mainView.addHyperlinkListener
            (
              new HyperlinkListener()
                {
                  public void hyperlinkUpdate(HyperlinkEvent e)
                    {
                      //Log.logInfo(this, "Tag 1...");
                      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                        {
                          //Log.logInfo(this, "Tag 2...");
                          JEditorPane pane = (JEditorPane) e.getSource();
                          //mainView = (JEditorPane) e.getSource();
                          if (e instanceof HTMLFrameHyperlinkEvent)
                            {
                              //Log.logInfo(this, "Tag 3...");
                              HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                              HTMLDocument doc = (HTMLDocument)pane.getDocument();
                              //HTMLDocument doc = (HTMLDocument)mainView.getDocument();
                              doc.processHTMLFrameHyperlinkEvent(evt);
                            }
                          else
                            {
                              //thisWindow.dispose();
                              protectDisposeTag = true;
                              if (titleName.trim().equalsIgnoreCase("caAdapter License Information"))
                              {
                                  String cURL = (e.getURL()).toString();
                                  //System.out.println("CCC : " + cURL);
                                    try
                                    {
                                        //mainView.setPage(new URL(cURL));
                                        mainView.setPage(e.getURL());
                                    }
                                    catch(Throwable ie)
                                    {
                                        //System.out.println("CCC_ERR : " + cURL);
                                        displayInformationWhenClickOnHyperlink();
                                    }
                              }
                              else displayInformationWhenClickOnHyperlink();
                            }
                        }
                    }
                }
            );


      addWindowListener(new WinCloseExit(this));
    }

  private void displayInformationWhenClickOnHyperlink()
  {
      JOptionPane.showMessageDialog(this, "This Screen is just a viewer NOT a Navigator. "
                                    + "So, any mouse clicking on this viewer can not be met any response.\r\n"
                                    + "If you want to do so, please call an internet browser of your Windows "
                                    + "environment with the texts \r\nin the current URL text field on up side "
                                    + "of this screen.",
                                    "HTML Viewer",JOptionPane.INFORMATION_MESSAGE );
  }

    public void displayingText(String mode, String text, int width, int height)
        {
          screenWidth = width;
          screenHeight = height;
          String commonPath = FileUtil.getWorkingDirPath() + File.separator + "etc" + File.separator +  Config.HELP_TEMPORARY_FILENAME_FIRST;
          String commonURIPath = "file:///" + commonPath.replace(File.separator, "/");
          FileWriter fw = null;

          if (mode.equalsIgnoreCase("text"))
            {
              fromTag = 1;
              try
                {
                  fw = new FileWriter(commonPath);

                  text = text.replace("&", "&amp;");
                  text = text.replace("\"", "&quot;");
                  text = text.replace("<", "&lt;");
                  text = text.replace(">", "&gt;");
                  text = text.replace("\n", "<br>");
                  text = text.replace("  ", "&nbsp;&nbsp;");
                  fw.write("<html><head><title>cc</title></head><body>"+ text + "</body></html>");
                  fw.close();
                }
              catch(IOException ie)
                {
                  return;
                }
              htmlAddress = commonURIPath;
              titleName = "Text Viewing";
              mainDisplaying();
            }
          else if (mode.equalsIgnoreCase("file"))
            {
              labelName = "Text File Name";
              fromTag = 2;
              fileName = text;
              FileReader fr = null;

              try { fr = new FileReader(text); }
              catch(FileNotFoundException fe) { displayingText("text", "File Not Found ERROR : " + text, width, height);}

              BufferedReader br = new BufferedReader(fr);
              String readLineOfFile = "";
              String tot = "";
              try
                {
                  while((readLineOfFile=br.readLine())!=null) tot = tot + readLineOfFile + "\n";
                  fr.close();
                  br.close();
                }
              catch(IOException ie) { displayingText("text", "ERROR : File Reading Error : " + text, width, height);}
              catch(NullPointerException ie) { displayingText("text", "ERROR : File Closing Error : " + text, width, height);}
              htmlAddress = commonURIPath;
              titleName = "Text File Viewing";
              mainDisplaying();
            }
          else displayingText("text", "ERROR : Invalid Mode : " + mode, width, height);

        }

  class WinCloseExit extends WindowAdapter //implements WindowListener        //extends WindowAdapter
      {
          HTMLViewer tt;
          WinCloseExit(HTMLViewer st) { tt = st; }
          public void windowClosing(WindowEvent e)  {tt.dispose();}
          public void windowClosed(WindowEvent e)  {}
          public void windowOpened(WindowEvent e) {}
          public void windowDeiconified(WindowEvent e) {}
          public void windowIconified(WindowEvent e) {}
          public void windowActivated(WindowEvent e) {}
          public void windowDeactivated(WindowEvent e)
            {
              if (!protectDisposeTag) tt.dispose();
              protectDisposeTag = false;
              //thisWindow.setAlwaysOnTop(false);
            }
        }

 }

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/09/11 16:06:30  linc
 * HISTORY      : read license from jar file.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/14 20:26:58  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/01/08 20:50:47  umkis
 * HISTORY      : Modify for hidng the url address when license note showing
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/03 19:09:21  umkis
 * HISTORY      : Whenever click 'license information' displaying content is generated from files in 'license' directory.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/20 23:02:20  umkis
 * HISTORY      : add Extending caAdapter to include new HL7 message types (6.1.3)
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/05 15:33:22  umkis
 * HISTORY      : defect# 223, When clicking on one message line of the message table, message viewing screen shows up and the whole body of the message can be displayed.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/30 20:47:31  umkis
 * HISTORY      : defect# 212
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/02 21:12:18  umkis
 * HISTORY      : code re-organized
 * HISTORY      :
 */
