/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.help;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.DocumentRenderer;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class defines a dialog to view help content.
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */

public class HelpContentViewer extends JDialog implements ActionListener, ListSelectionListener
{
  /**
   * Logging constant used to identify source of log entry, that could be later used to create
   * logging mechanism to uniquely identify the logged class.
   */
  private static final String LOGID = "$RCSfile: HelpContentViewer.java,v $";

  /**
   * String that identifies the class version and solves the serial version UID problem.
   * This String is for informational purposes only and MUST not be made final.
   *
   * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
   */
  public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/HelpContentViewer.java,v 1.5 2008-09-26 20:35:27 linc Exp $";


  JTree treeMain;
  DefaultMutableTreeNode selectedNodeOfMain;
  DefaultMutableTreeNode headOfMain;
  DefaultMutableTreeNode currentNodeOfMain;

  JButton jbGoPreviousNode;
  JButton jbGoNextNode;

  JTextField jtID;
  JTextField jtTitle;
  JTextField jtPathDisplay;

  String jaContent;
  JEditorPane jepHTMLContent;
  JTabbedPane tabbedPane;

  String commonPath;
  String commonURIPath;
  String imagePath;
  String imageURIPath;
  String stHelpDir = "{Help_Dir}";
  String stImageDir = "{Image_Dir}";
  String currentPage;
  String currentLink;
  String searchString; // Store searched word in searching function.
  String searchStr;  // searched word for making bold character display when content showing.
  boolean searchTag; // When TRUE, searchStr which is a searched word will be displayed in bold font when content displaying.
  boolean forSearchTag; // set TRUE when a word is searched for. This tag protect from displaying the searched content on the JEditorPane.
  boolean foundTag; //set TRUE when the searching word is found. The rest cases, set FALSE
  int pageSetTag; // set 1 during print page set window is activated, set 0 during print dialog window is activated , else -1;
  boolean protectFrameDispose; // For protecting This Frame disappear when a JOptionPane screen appears

  JList jlstSearch;
  JList jlstIndex;
  DefaultListModel listModelSearch;
  DefaultListModel listModelIndex;
  JButton jbWholeSearch;
  JButton jbKeywordSearch;
  JTextField jtSearchText;
  AbstractMainFrame mainFrame;
  int pageSet;
  int stackMAX = 100;
  int stackCount;
  int stackPoint = 0;
  boolean popupTag = false;  // popup menu "Go to Previous item" or "Go to Next item" selected, set TRUE.
  DefaultMutableTreeNode[] beforeContent = new DefaultMutableTreeNode[stackMAX];
  String FIRST_POPUP_MENU = "Go to Previous item";
  String SECOND_POPUP_MENU = "Go to Next item";
  String THIRD_POPUP_MENU = "Print This Content";
  String FOURTH_POPUP_MENU = "Print Page Setup";
  String TOOL_BAR_COMMAND_PREVIOUS = "Previous";
  String TOOL_BAR_COMMAND_NEXT = "Next";
  String TOOL_BAR_COMMAND_PRINT = "Print";
  String TOOL_BAR_COMMAND_PAGESETUP = "Page Setup";
  String INSIDE_NODE_LINK_TAG = "&NODELINK:";
  JButton toolBarButtonPrevious;
  JButton toolBarButtonNext;
  JButton toolBarButtonPrint;
  JButton toolBarButtonPageSetup;
  PageFormat pageFormat = null;
  MouseListener popupListener = null;
  String dirSeparater;
  JToolBar toolBar;
    String contentPageTag = "";

  public final static int ONE_SECOND = 1000;


  public HelpContentViewer()
	{
      setFrame();
    }
  public HelpContentViewer(AbstractMainFrame mf)
	{
      mainFrame = mf;
      setFrame();
    }

  private void setFrame()
    {
      stackCount = 0;
      pageSet = 0;
      selectedNodeOfMain = null;
      searchStr = "";
      searchString = "";
      searchTag = false;
      forSearchTag = false;
      foundTag = false;
      protectFrameDispose = false;
      pageSetTag = -1;
      //selectedNodeOfContent = null;
      dirSeparater = File.separator;
      pageFormat = new PageFormat();
      Paper paper = new Paper();
      paper.setSize(Config.DEFAULT_PAPER_SIZE_X_IN_INCH*72, Config.DEFAULT_PAPER_SIZE_Y_IN_INCH*72);
      paper.setImageableArea((Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH-0.25)*72, Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH*72,
                  (Config.DEFAULT_PAPER_SIZE_X_IN_INCH - Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH - Config.DEFAULT_PAPER_RIGHT_MARGIN_IN_INCH + 0.5)*72,
                  (Config.DEFAULT_PAPER_SIZE_Y_IN_INCH - Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH - Config.DEFAULT_PAPER_BOTTOM_MARGIN_IN_INCH)*72);
      pageFormat.setPaper(paper);

      commonPath = FileUtil.getWorkingDirPath() + dirSeparater+"etc"+dirSeparater;
      commonURIPath = "file:///";
      for(int i=0;i<commonPath.length();i++)
        {
          String achar = commonPath.substring(i, i+1);
          if (achar.equals(dirSeparater)) commonURIPath = commonURIPath + "/";
          else commonURIPath = commonURIPath + achar;
        }

      imagePath = FileUtil.getUserInterfaceDirPath() + dirSeparater + "resources" + dirSeparater+"images"+dirSeparater +"help_images" +dirSeparater;
      //System.out.println("imagePath : " + imagePath);
      imageURIPath = "file:///";
      for(int i=0;i<imagePath.length();i++)
        {
          String achar = imagePath.substring(i, i+1);
          if (achar.equals(dirSeparater)) imageURIPath = imageURIPath + "/";
          else imageURIPath = imageURIPath + achar;
        }
      //System.out.println("imageURIPath : " + imageURIPath);
      treeInitialConstruction();

      treeMain = new JTree(headOfMain);
      //treeContent = new JTree(headOfContent);
	  JScrollPane js1 = new JScrollPane(treeMain);

      try
        {
          jepHTMLContent = new JEditorPane("text/html", "<html><head><title>help</title></head><body><font color='blue'>Start</font></body></html>");
        }
      catch(Throwable t)
        {
          Log.logInfo(this, "IO Error...");
        }

      //js1.setBounds(1, 1, 236, 428);
      tabbedPane = new JTabbedPane();
      JPanel jp1 = new JPanel(new BorderLayout());
      jp1.add(new JPanel(), BorderLayout.NORTH);
      jp1.add(js1, BorderLayout.CENTER);

      //ImageIcon ii1=new ImageIcon(FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater + "Up24.gif");
      ImageIcon ii1=new ImageIcon(DefaultSettings.getImage("Up24.gif"));
      jbGoPreviousNode = new JButton(ii1);
      //ImageIcon ii2=new ImageIcon(FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater + "Down24.gif");
      ImageIcon ii2=new ImageIcon(DefaultSettings.getImage("Down24.gif"));
      jbGoNextNode = new JButton(ii2);
      JPanel jp1_1 = new JPanel(new BorderLayout());
      JPanel jp1_2 = new JPanel(new BorderLayout());
      jp1_1.add(jbGoNextNode, BorderLayout.EAST);
      jp1_1.add(new JLabel(" "), BorderLayout.CENTER);
      jp1_1.add(jbGoPreviousNode, BorderLayout.WEST);
      jp1_2.add(new JLabel(" "), BorderLayout.EAST);
      jp1_2.add(jp1_1, BorderLayout.CENTER);
      jp1_2.add(new JLabel(" "), BorderLayout.WEST);
      //jbGoPreviousNode.setBounds(1, 438, 112, 30);
      //jbGoNextNode.setBounds(120, 438, 112, 30);
      jp1.add(jp1_2, BorderLayout.SOUTH);
      //jp1.add(jbGoPreviousNode);
      //jp1.add(jbGoNextNode);
      //tabbedPane.add(jp1);
      tabbedPane.insertTab("Content", null, jp1, "Content", 0);

      listModelIndex = new DefaultListModel();
      jlstIndex = new JList(getListIndex());
      jlstIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jlstIndex.setSelectedIndex(0);
      jlstIndex.addListSelectionListener(this);
      //jlstIndex.setVisibleRowCount(5);
      JScrollPane listScrollPane1 = new JScrollPane(jlstIndex);
      JPanel jp2 = new JPanel(new BorderLayout());
      jp2.add(listScrollPane1);
      tabbedPane.insertTab("Index", null, jp2, "Keywords Index", 1);

      listModelSearch = new DefaultListModel();
      //listModelSearch.addElement("Empty");
      //listModelSearch.addElement(" ");
      //jlstSearch = new JList(listModelSearch);
      jlstSearch = new JList(listModelSearch);
      jlstSearch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jlstSearch.setSelectedIndex(0);
      jlstSearch.addListSelectionListener(this);


      jlstSearch.setSize(100,200);
      JScrollPane listScrollPane2 = new JScrollPane(jlstSearch);

      JLabel labelAx = new JLabel(" Searching Text");



      jtSearchText = new JTextField();
      jtSearchText.setColumns(14);


      jtSearchText.setEditable(true);
      jbKeywordSearch = new JButton("Keyword Search");
      jbWholeSearch = new JButton("Search");
      jbKeywordSearch.setSize(100,25);
      jbWholeSearch.setSize(100,25);

      JPanel jp3_Upper = new JPanel(new BorderLayout());
      JPanel jp3_Upper1 = new JPanel(new BorderLayout());
      JPanel jp3_Upper2 = new JPanel(new BorderLayout());


      jp3_Upper1.add(labelAx, BorderLayout.WEST);
      jp3_Upper1.add(jtSearchText, BorderLayout.CENTER);
      jp3_Upper1.add(new JLabel(" "), BorderLayout.EAST);
      jp3_Upper2.add(jbKeywordSearch, BorderLayout.WEST);
      jp3_Upper2.add(new JLabel(" "), BorderLayout.CENTER);
      jp3_Upper2.add(jbWholeSearch, BorderLayout.EAST);
      jp3_Upper.add(new JPanel(), BorderLayout.NORTH);
      jp3_Upper.add(jp3_Upper1, BorderLayout.CENTER);
      jp3_Upper.add(jp3_Upper2, BorderLayout.SOUTH);

      JPanel jp3_Middle = new JPanel(new BorderLayout());

      jp3_Middle.add(new JPanel(), BorderLayout.NORTH);
      jp3_Middle.add(listScrollPane2, BorderLayout.CENTER);


      JPanel jp3 = new JPanel(new BorderLayout());

      jp3.add(jp3_Upper, BorderLayout.NORTH);
      jp3.add(jp3_Middle, BorderLayout.CENTER);
      jp3.add(new JPanel(), BorderLayout.SOUTH);




      tabbedPane.insertTab("Search", null, jp3, "Keyword Search", 2);



      tabbedPane.setBounds(10, 70, 238, 470);
      JPanel leftPanel = new JPanel(new BorderLayout());
      leftPanel.add(new JPanel(), BorderLayout.SOUTH);
      leftPanel.add(tabbedPane, BorderLayout.CENTER);

      jepHTMLContent.setEditable(false);
      JScrollPane js2 = new JScrollPane(jepHTMLContent);
      js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      jtPathDisplay = new JTextField();
	  jtID = new JTextField();
	  jtTitle = new JTextField();

      jtPathDisplay.setEditable(false);
      jtID.setEditable(false);
      jtTitle.setEditable(false);

      JPanel rightPanel = new JPanel(new BorderLayout());
      JPanel rightNorth1Panel = new JPanel(new BorderLayout());
      rightNorth1Panel.add(jtID, BorderLayout.WEST);
      rightNorth1Panel.add(new JLabel(" "), BorderLayout.CENTER);
      rightNorth1Panel.add(jtTitle, BorderLayout.EAST);

      JPanel rightNorthPanel = new JPanel(new BorderLayout());
      rightNorthPanel.add(rightNorth1Panel, BorderLayout.NORTH);
      rightNorthPanel.add(jtPathDisplay, BorderLayout.CENTER);
      rightPanel.add(rightNorthPanel, BorderLayout.NORTH);
      rightPanel.add(js2, BorderLayout.CENTER);


      rightPanel.add(new JPanel(), BorderLayout.SOUTH);


                leftPanel.setPreferredSize(new Dimension(240, 530));
                leftPanel.setMinimumSize(new Dimension(10, 10));


                rightPanel.setPreferredSize(new Dimension(550, 530));
                rightPanel.setMinimumSize(new Dimension(10, 10));

                //Put the editor pane and the text pane in a split pane.
                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                      leftPanel,
                                                      rightPanel);
                splitPane.setOneTouchExpandable(true);
                splitPane.setResizeWeight(0.5);

                toolBar = new JToolBar("Tool Bar");
                addToolBarButtons(toolBar);
                toolBar.setFloatable(false);
                toolBar.setRollover(true);
                getContentPane().setLayout(new BorderLayout());
                getContentPane().add(toolBar, BorderLayout.NORTH);
                getContentPane().add(splitPane, BorderLayout.CENTER);


      jbGoPreviousNode.addActionListener(this);
      jbGoNextNode.addActionListener(this);
      jbWholeSearch.addActionListener(this);
      jbKeywordSearch.addActionListener(this);
      //jbPrintContent.addActionListener(this);
      //jbSetPrintPage.addActionListener(this);

      jtID.setVisible(false);
      jtTitle.setVisible(false);
      //labelA.setVisible(false);
      //labelB.setVisible(false);
      //jbSetPrintPage.setVisible(false);
      jbKeywordSearch.setVisible(false);

      setTitle("caAdapter Help");
      setSize(900 ,600);
	  //show();

      showNodeContent(headOfMain);
      DefaultMutableTreeNode tempNode = headOfMain;
      while(true)
        {
          try
            {
              tempNode = tempNode.getNextNode();
              if (tempNode.isLeaf()) continue;
            }
          catch(NullPointerException ne)
            {
              break;
            }
          catch(ArrayIndexOutOfBoundsException ne)
            {
              break;
            }

          //if (tempNode.isLeaf()) continue;
          if (tempNode == headOfMain) break;

          if (treeMain.isExpanded(new TreePath(tempNode.getPath())))
            {
              //Log.logInfo(this, "Expanded : " + (String)tempNode.getUserObject());
            }
          else
            {
              treeMain.expandPath(new TreePath(tempNode.getPath()));
              //treeContent.expandPath(new TreePath((findNodeOfContent(getNodeID(tempNode))).getPath()));
              //Log.logInfo(this, "Collapsed : " + (String)tempNode.getUserObject());
            }
        }


      jtSearchText.addKeyListener
      (
          new KeyListener()
          {
              public void keyReleased(KeyEvent e)
              {
                  try
                  {
                      if (!jbWholeSearch.isEnabled())
                      {
                          if (jtSearchText.getText().length() > 0) jbWholeSearch.setEnabled(true);
                      }
                      if (jbWholeSearch.isEnabled())
                      {
                          if (jtSearchText.getText().length() == 0) jbWholeSearch.setEnabled(false);
                      }
                  }
                  catch(NullPointerException ne) { jbWholeSearch.setEnabled(false); }
              }
              public void keyTyped(KeyEvent e)  {}

              public void keyPressed(KeyEvent e)
              {
                  if (e.getKeyCode() == KeyEvent.VK_ENTER) doPressWholeSearchButton(jbWholeSearch);
              }
          }
      );
      jbWholeSearch.setEnabled(false);


      treeMain.addTreeSelectionListener
	    (
	      new TreeSelectionListener()
			{
              public void valueChanged(TreeSelectionEvent e)
				{
                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMain.getLastSelectedPathComponent();

                  if (node == null)
                    {
                      if (!searchTag) showNodeContent(selectedNodeOfMain);
                      return;
                    }

                  selectedNodeOfMain = node;
                  currentNodeOfMain = selectedNodeOfMain;

                  if (!searchTag) showNodeContent(selectedNodeOfMain);
	            }
			}
		);

      linkNodeID("0");

          jepHTMLContent.addHyperlinkListener
            (
              new HyperlinkListener()
                {
                  public void hyperlinkUpdate(HyperlinkEvent e)
                    {
                      //Log.logInfo(this, "Tag 1...");
                      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                        {
                          //Log.logInfo(this, "Tag 2...");
                          //JEditorPane pane = (JEditorPane) e.getSource();
                          jepHTMLContent = (JEditorPane) e.getSource();
                          if (e instanceof HTMLFrameHyperlinkEvent)
                            {
                              //Log.logInfo(this, "Tag 3...");
                              HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                              //HTMLDocument doc = (HTMLDocument)pane.getDocument();
                              HTMLDocument doc = (HTMLDocument)jepHTMLContent.getDocument();
                              doc.processHTMLFrameHyperlinkEvent(evt);
                            }
                          else
                            {
                              //String sURL = "";
                              currentLink = (e.getURL()).toString();

                              int idx = 0;

                              try
                                {
                                  //pane.setPage(e.getURL());
                                  String cURL = (e.getURL()).toString();

                                  if (((cURL).toUpperCase()).startsWith(stHelpDir.toUpperCase()) )
                                    {
                                      //stringHTMLDisplay(urlFile, linkPoint);
                                      showNodeHTMLContent(cURL);
                                    }
                                  else if (((cURL).toUpperCase()).startsWith(stImageDir.toUpperCase()) )
                                    {
                                      //stringHTMLDisplay(urlFile, linkPoint);
                                      showNodeHTMLContent(cURL);
                                    }
                                  else if (((cURL).toUpperCase()).indexOf(INSIDE_NODE_LINK_TAG) > 0)
                                    {
                                      idx = ((cURL).toUpperCase()).indexOf(INSIDE_NODE_LINK_TAG);
                                      String id = cURL.substring(idx + INSIDE_NODE_LINK_TAG.length());
                                      linkNodeID(id);
                                    }
                                  else if (((cURL).toUpperCase()).startsWith("FILE"))
                                    {
                                      int idxx = ((cURL).toUpperCase()).indexOf(stHelpDir.toUpperCase());
                                      int idxy = ((cURL).toUpperCase()).indexOf(stImageDir.toUpperCase());
                                      if (((cURL).toUpperCase()).indexOf(stHelpDir.toUpperCase()) > 0)
                                        {
                                          showNodeHTMLContent(cURL.substring(idxx));
                                          //Log.logInfo(this, "A point : " + cURL.substring(idxx));
                                        }
                                      else if (((cURL).toUpperCase()).indexOf(stImageDir.toUpperCase()) > 0)
                                        {
                                          showNodeHTMLContent(cURL.substring(idxy));
                                          //Log.logInfo(this, "B point : " + cURL.substring(idxy));
                                        }
                                      else
                                        {
                                          idx = -1;
                                          for (int i=65;i<96;i++)
                                            {
                                              char c = (char) i;
                                              String st = "" + c;
                                              idx = (cURL.toUpperCase()).indexOf(st + ":");
                                              if (idx > 0) break;
                                            }
                                          if (idx > 0)
                                            {
                                              cURL = cURL.substring(idx);
                                              String st = "";
                                              for(int i=0;i<cURL.length();i++)
                                                {
                                                  String achar = cURL.substring(i, i+1);
                                                  if (achar.equals("/")) st = st + dirSeparater;
                                                  else st = st + achar;
                                                }
                                              cURL = st;
                                              //stringHTMLDisplay(urlFile, linkPoint);
                                              showNodeHTMLContent(cURL);
                                              //Log.logInfo(this, "C point : " + cURL);
                                            }
                                          else
                                            {
                                              jepHTMLContent.setPage(e.getURL());
                                              currentPage = cURL;
                                            }
                                        }
                                    }
                                  else
                                    {
                                      jepHTMLContent.setPage(e.getURL());
                                      currentPage = cURL;
                                    }
                                }
                              catch (Throwable t)
                                {
                                  stringHTMLDisplay("<html><head><title>Error!</title></head><body><font color='red'>Malformed URL : </font>"+e.getURL()+"</body></html>");
//									Log.logException(this, e);
                                }
                            }
                        }
                    }
                }
            );

        tabbedPane.addChangeListener(
              new ChangeListener()
              {
                  public void stateChanged(ChangeEvent e)
                  {
                      //int tabIndex = e.getSource()
                      int tabIndex = tabbedPane.getSelectedIndex();
                      if (tabIndex == 0) showNodeContent(selectedNodeOfMain);
                      else if (tabIndex == 1) doWhenIndexListChange(false);
                      else if (tabIndex == 2) doWhenSearchListChange(false);
                  }
              }
      );
      createPopupMenu();
      toolBarButtonNext.setEnabled(false);
      toolBarButtonPrevious.setEnabled(false);
      addWindowListener(new WinCloseExit(this));
      //Log.logInfo(this, "Final");
    }

    protected void addToolBarButtons(JToolBar toolBar) {
        JButton button = null;

        //first button
        toolBarButtonPrevious = makeToolBarButton("Back24.gif", TOOL_BAR_COMMAND_PREVIOUS,
                                      "Back to previous referred Content",
                                      "Previous");
        toolBar.add(toolBarButtonPrevious);

        //third button
        toolBarButtonNext = makeToolBarButton("Forward24.gif", TOOL_BAR_COMMAND_NEXT,
                                      "Forward to referred Content",
                                      "Next");
        toolBar.add(toolBarButtonNext);

        //separator
        toolBar.addSeparator();

        toolBarButtonPrint = makeToolBarButton("Print24.gif", TOOL_BAR_COMMAND_PRINT,
                                      "Print this content",
                                      "Print");
        toolBar.add(toolBarButtonPrint);

        toolBarButtonPageSetup = makeToolBarButton("PageSetup24.gif", TOOL_BAR_COMMAND_PAGESETUP,
                                      "Print Page Setup",
                                      "PageSetup");
        toolBar.add(toolBarButtonPageSetup);

    }

    protected JButton makeToolBarButton(String imageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText) {
        //Look for the image.
        ImageIcon ii2=new ImageIcon(FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater + imageName);

        JButton button = new JButton(ii2);
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
        return button;
    }


  public void linkNodeID(String id)
  {   int idx = id.indexOf("#");

      if (idx < 0)
      {
            treeMain.setSelectionPath(new TreePath(findNodeOfMain(id).getPath()));
            selectedNodeOfMain = findNodeOfMain(id);
      }
      else
      {
          String nodeID = id.substring(0, idx);
          String contTag = "";
          try
          {
              contTag = id.substring(idx+1);
          }
          catch(ArrayIndexOutOfBoundsException ae)
          {
              contTag = "";
          }
          selectedNodeOfMain = findNodeOfMain(nodeID);
          if (contTag.equals(""))
          {
              treeMain.setSelectionPath(new TreePath(findNodeOfMain(nodeID).getPath()));
          }
          else
          {
              contentPageTag = contTag;
              showNodeContent(findNodeOfMain(nodeID));
              contentPageTag = "";
          }
      }


  }

  public void valueChanged(ListSelectionEvent e)
    {
      try
        {
          String ndeStr = "";

          if (e.getSource() == jlstIndex)
            {
              doWhenIndexListChange(true);
            }
          else
            {
              doWhenSearchListChange(true);
            }
        }
      catch(NullPointerException ne)
        {
          //Log.logInfo(this, "NullPointerException : " + ne.getMessage());
//			Log.logException(this, e);
		}
    }
  public void doWhenIndexListChange(boolean fromList)
    {
      String ndeStr = "";

      if (jlstIndex.getSelectedIndex() < 0) return;
      String lstStr = (String) jlstIndex.getSelectedValue();
      //System.out.println("VVV1 : "+ lstStr);
      if ((lstStr.startsWith(" "))|| (lstStr.startsWith("*"))|| (lstStr.equals(""))) return;
      String srcStr = (lstStr.substring(0,lstStr.indexOf(","))).trim();
      ndeStr = (lstStr.substring(lstStr.indexOf(",")+1)).trim();
      //if ((fromList)&&(srcStr.equalsIgnoreCase(searchStr))) return;
      //else searchStr = srcStr;
      searchStr = srcStr;
      doWhenListChange(ndeStr);
    }
  public void doWhenSearchListChange(boolean fromList)
    {
      String ndeStr = "";
      if (jlstSearch.getSelectedIndex() < 0) return;
      String lstStr = (String) jlstSearch.getSelectedValue();
      //System.out.println("VVV2 : "+ lstStr);
      if ((lstStr.startsWith(" "))|| (lstStr.equals(""))) return;
      ndeStr = (lstStr.substring(0,lstStr.indexOf(","))).trim();
      String tit = (lstStr.substring(lstStr.indexOf(",")+1)).trim();
      if ((fromList)&&(tit.equalsIgnoreCase(jtTitle.getText()))) return;
      searchStr = searchString;
      jtSearchText.setText(searchStr);
      doWhenListChange(ndeStr);
    }
  public void doWhenListChange(String ndeStr)
    {
      searchTag = true;
      DefaultMutableTreeNode findNode = findNodeOfMain(ndeStr);
      treeMain.setSelectionPath(new TreePath(findNode.getPath()));
      selectedNodeOfMain = findNode;
      showNodeContent(findNode);
    }


  public void actionPerformed(ActionEvent e)
	{
      if (e.getSource() == jbGoPreviousNode)
	    {

          if (currentNodeOfMain == headOfMain)
            {
              //JOptionPane.showMessageDialog(this,"This Node is the Head","Moving Error!",JOptionPane.ERROR_MESSAGE);
            }
          else
            {
              currentNodeOfMain = currentNodeOfMain.getPreviousNode();
              //currentNodeOfContent = findNodeOfContent(getNodeID(currentNodeOfMain));
              //showNodeContent(currentNodeOfContent);
              treeMain.setSelectionPath(new TreePath(currentNodeOfMain.getPath()));
              selectedNodeOfMain = currentNodeOfMain;
              //selectedNodeOfContent = currentNodeOfContent;
              showNodeContent(selectedNodeOfMain);
            }

        }

      if (e.getSource() == jbGoNextNode)
		{
          //DefaultMutableTreeNode cnode = currentNodeOfContent;
          DefaultMutableTreeNode tnode = currentNodeOfMain;
          try
            {
              currentNodeOfMain = currentNodeOfMain.getNextNode();
              //currentNodeOfContent = findNodeOfContent(getNodeID(currentNodeOfMain));
              //showNodeContent(currentNodeOfContent);
              treeMain.setSelectionPath(new TreePath(currentNodeOfMain.getPath()));
              selectedNodeOfMain = currentNodeOfMain;
              //selectedNodeOfContent = currentNodeOfContent;
              showNodeContent(selectedNodeOfMain);
            }
          catch(NullPointerException ne)
            {
              //JOptionPane.showMessageDialog(this,"There is no Foward Node.","Moving Error!",JOptionPane.ERROR_MESSAGE);
              //currentNodeOfContent = cnode;
              currentNodeOfMain = tnode;
              selectedNodeOfMain = currentNodeOfMain;
              //selectedNodeOfContent = currentNodeOfContent;
              showNodeContent(selectedNodeOfMain);
            }
        }
      if ( (e.getSource() == jbWholeSearch) || (e.getSource() == jbKeywordSearch) )
	    {
           doPressWholeSearchButton(e.getSource());
        }

      if (e.getSource() == toolBarButtonPrint)
	    {
          printContent();
        }
      if (e.getSource() == toolBarButtonPageSetup)
        {
          setPrintPage();
        }
      if (e.getSource() == toolBarButtonPrevious)
	    {
          backwardWind();
        }
      if (e.getSource() == toolBarButtonNext)
        {
          forwardWind();
        }
      if (e.getSource() instanceof JMenuItem)
        {
          JMenuItem source = (JMenuItem) e.getSource();
          String menu = source.getText();
          if (menu.equals(FIRST_POPUP_MENU)) backwardWind();
          if (menu.equals(SECOND_POPUP_MENU)) forwardWind();
          if (menu.equals(THIRD_POPUP_MENU)) printContent();
          if (menu.equals(FOURTH_POPUP_MENU)) setPrintPage();
        }
    }
  private void doPressWholeSearchButton(Object source)
  {
      String srchWord = jtSearchText.getText();
          if ((srchWord == null) || ((srchWord.trim()).equals("")))
          {
              //jtSearchText.setText(searchString);
              return;
          }
          //SimpleProgressMonitor spm = new SimpleProgressMonitor(this, treeMain.getRowCount(), "Searching progress");

          //ProgressViewer spm = new ProgressViewer("text", "Searching progress",350, 160, treeMain.getRowCount());
          //DefaultSettings.centerWindow(spm);
          //spm.setVisible(true);
          //spm.createBufferStrategy(2);
          //BufferStrategy strategy = spm.getBufferStrategy();
          //spm.resetCenterPanel();
          boolean cancelSearch = false;

          DefaultMutableTreeNode tempNode = findNodeOfMain("1");
          HelpContentElement ele = null;
          java.util.List<String> ls = new ArrayList<String>();
          int finding = 0;
          int searching = 0;
          while(true)
            {
              try
                {
                  ele = (HelpContentElement) tempNode.getUserObject();
                }
              catch(NullPointerException ne)
                {
                  break;
                }
                searching++;
              //Log.logInfo(this, "CCCC : " + getNodeID(tempNode) + " : "+ ele.getNodeTitle());
              if (source == jbWholeSearch)
                {
                  //if (srchWord.length() <= 2)
                  if (srchWord.length() == 1)
                  {

                    //protectFrameDispose = true;
                    //JOptionPane.showMessageDialog(this,"3 or more characters are needed for whole search","Too less Characters",JOptionPane.WARNING_MESSAGE);
                    //return;
                  }
                  if (searchNodeContent(tempNode, srchWord))
                  {
                      finding++;
                      ls.add(getNodeID(tempNode) + ", " + ele.getNodeTitle());
                  }
                  /*
                  System.out.println("strategy.contentsLost1 : " + (strategy.contentsLost()));
                  Graphics g = strategy.getDrawGraphics();
                  System.out.println("strategy.contentsLost2 : " + (strategy.contentsLost()));

                  String progressNoteText = "searching... : " + finding + " node(s) found / .." + searching + " / " + treeMain.getRowCount();
                  cancelSearch = spm.operateProgressMonitor(searching, progressNoteText, true);
                    System.out.println("strategy.contentsLost3 : " + (strategy.contentsLost()));
                    g = spm.getGraphics();
                    System.out.println("strategy.contentsLost4 : " + (strategy.contentsLost()));
                    strategy.show();
                    System.out.println("strategy.contentsLost5 : " + (strategy.contentsLost()));
                  //spm.resetCenterPanel();
                  if (searching == 14)
                  {
                      JPanel p = new JPanel();
                      p = null;
                      p.isVisible();
                  }
                  if (cancelSearch) break;
                   //JOptionPane.showMessageDialog(this,"searching..." + finding + " / " + searching + " / " + treeMain.getRowCount());
                  */
                }
              else if (source == jbKeywordSearch)
                {
                  if (ele.searchKeyWord(srchWord)) ls.add(getNodeID(tempNode) + ", " + ele.getNodeTitle());
                }
              tempNode = tempNode.getNextNode();
              if (tempNode == null) break;
              if (tempNode == headOfMain) break;
            }
          //spm.dispose();
          //if (cancelSearch) return;
          if (ls.size() == 0)
          {
              protectFrameDispose = true;
              JOptionPane.showMessageDialog(this,"Not Found this word","Not Found",JOptionPane.WARNING_MESSAGE);
          }
          else
            {
              listModelSearch.clear();
              for(int i=0;i<ls.size();i++) listModelSearch.add(i, ls.get(i));

              //jlstSearch.setModel(listModelSearch);
              searchString = srchWord;
              jlstSearch.setSelectedIndex(0);
              jlstSearch.ensureIndexIsVisible(0);
            }
  }
  private void setPrintPage()
  {
      pageSetTag = 1;
      //PrintPageSet pps = new PrintPageSet(this, pageSet);
      PrinterJob pj = PrinterJob.getPrinterJob();
      pageFormat = pj.pageDialog(pageFormat);
      mainFrame.setVisible(true);
      this.setVisible(true);
  }

  public void setProtectFrameDispose(boolean setF)
    {
        protectFrameDispose = setF;
    }

  private void printContent()
  {
      //protectFrameDispose = true;
      pageSetTag = 0;
      DocumentRenderer documentRenderer1 = new HelpViewerDocumentRenderer(pageFormat, mainFrame, this);
	  documentRenderer1.print(jepHTMLContent);


      /*   ----------------------- version 2 printing part  , not worked
      //PrintService service = PrintServiceLookup.lookupDefaultPrintService();
      //DocPrintJob job = service.createPrintJob();
      URL url = null;
      try
      {         //http://www.javaworld.com/javaworld/jw-07-2005/jw-0725-print_p.html
          //url = new URL("http://www.apress.com/ApressCorporate/supplement/1/421/bcm.gif ");
          //url = new URL("http://www.javaworld.com/javaworld/jw-07-2005/jw-0725-print_p.html");
          url = new URL(currentPage);
      }
      catch(MalformedURLException me)
      {
          System.out.println("MalformedURLException : " + currentPage);
      }
      //DocFlavor flavor = DocFlavor.URL.GIF;
      DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

      //DocFlavor flavor = DocFlavor.STRING.TEXT_PLAIN;
      PagePrintPrepare vista = new PagePrintPrepare(jepHTMLContent, pageFormat);
      //vista.setScale(0.8, 0.8);
      Doc doc = new SimpleDoc(vista, flavor, null);
      //Doc doc = new SimpleDoc(url, flavor, null);
      //Doc doc = new SimpleDoc(currentLink, flavor, null);
      PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
      attrs.add(new Copies(2));
      PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
      PrintService service = null;
      for (int i=0;i<services.length;i++)
      {
          System.out.println("Printer List : " + services[i].getName());
          if (services[i].isDocFlavorSupported(flavor))
          {
              System.out.println("OK : " + services[i].getName());
              service = services[i];
              break;
          }
      }
      if (service == null)
      {
          System.out.println("No fit printer. ");
          return;
      }
      DocPrintJob job = service.createPrintJob();
      try
      {
          job.print(doc, attrs);
      }
      catch(PrintException pe)
      {
          System.out.println("Print Exception : " + pe.getMessage());
      }
      */

      /*  ----------------  Version 1  printing part
      PagePrintPrepare vista = new PagePrintPrepare(jepHTMLContent, pageFormat);
      vista.setScale(0.8, 0.8);
      PrinterJob pj = PrinterJob.getPrinterJob();
      pj.setPageable(vista);
      String errMsg = "";
      boolean errSig = false;
      try
        {
          pageSetTag = 0;
          if (pj.printDialog())
            {

              pj.print();
            }
          //pageSetTag = -1;
        }
      catch (PrinterException pe)
        {
          errSig = true;
          errMsg = pe.getMessage();
        }

      //mainFrame.setFocusable(true);
      //pageSetTag = 1;
      mainFrame.setVisible(true);
      //this.setFocusable(true);
      this.setVisible(true);
      //pageSetTag = -1;

      if (errSig)
      {
          protectFrameDispose = true;
          JOptionPane.showMessageDialog(this, errMsg,"Print Page Error!",JOptionPane.ERROR_MESSAGE);
      }
      */
  }


  private void pushStack(DefaultMutableTreeNode nde)
  {
    if (stackCount == 0)
      {
        if (nde != findNodeOfMain("0")) return;
        stackCount++;
        stackPoint = 0;
        beforeContent[0] = nde;
        createPopupMenu();
        return;
      }
    if (popupTag)
    {
        popupTag = false;
        return;
    }
    if (nde == beforeContent[0])
    {
        stackPoint = 0;
        createPopupMenu();
        return;
    }
    if (stackCount < stackMAX) stackCount++;
    for(int i=(stackCount-1);i>0;i--) beforeContent[i] = beforeContent[i-1];
    beforeContent[0] = nde;
    stackPoint = 0;
    createPopupMenu();
  }
  private void backwardWind()
  {
      popupTag = true;
      if (stackPoint == (stackCount-1)) return;
      stackPoint++;
      treeMain.setSelectionPath(new TreePath(beforeContent[stackPoint].getPath()));
      createPopupMenu();
  }
  private void forwardWind()
  {
      popupTag = true;
      if (stackPoint == 0) return;
      stackPoint--;
      treeMain.setSelectionPath(new TreePath(beforeContent[stackPoint].getPath()));
      createPopupMenu();
  }

  public void createPopupMenu()
    {
        if (popupListener != null) jepHTMLContent.removeMouseListener(popupListener);
        JMenuItem menuItem;
        JPopupMenu popup = new JPopupMenu();
        if (stackCount > 1)
          {
            if (stackPoint < (stackCount-1))
              {
                menuItem = new JMenuItem(FIRST_POPUP_MENU);
                menuItem.addActionListener(this);
                popup.add(menuItem);
                toolBarButtonPrevious.setEnabled(true);
              }
            if (stackPoint > 0)
              {
                menuItem = new JMenuItem(SECOND_POPUP_MENU);
                menuItem.addActionListener(this);
                popup.add(menuItem);
                toolBarButtonNext.setEnabled(true);
              }
            if (stackPoint == 0)
              {
                toolBarButtonNext.setEnabled(false);
              }
            if (stackPoint == (stackCount-1))
              {
                toolBarButtonPrevious.setEnabled(false);
              }
            popup.addSeparator();
          }
        menuItem = new JMenuItem(THIRD_POPUP_MENU);
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem(FOURTH_POPUP_MENU);
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popupListener = new PopupListener(popup);
        jepHTMLContent.addMouseListener(popupListener);
    }

  private DefaultListModel getListIndex()
    {
      DefaultMutableTreeNode tempNode = findNodeOfMain("0");

      HelpContentElement ele = null;
      java.util.List<String> ls = new ArrayList<String>();
      java.util.List<String> ls1 = null;
      while(true)
        {
          try
            {
              ele = (HelpContentElement) tempNode.getUserObject();
            }
          catch(NullPointerException ne)
            {
              break;
            }
          ls1 = ele.getKeyWordsList();
          if (ls1.size() != 0)
            {
              for(int i=0;i<ls1.size();i++)
                {
                  String ms = (ls1.get(i)).trim();
                  if (ms.equals("")) continue;
                  if (ms == null) continue;
                  String spaceC = " , ";
                  //if (ms.length() < 20) for(int j=0;j<(20-ms.length());j++) spaceC = spaceC + " ";
                  ls.add(ms + spaceC + getNodeID(tempNode));
                }
            }
          tempNode = tempNode.getNextNode();
          if (tempNode == headOfMain) break;
          if (tempNode == null) break;
        }
      String[] sa = new String[ls.size()];
      String str = "";
      for(int i=0;i<ls.size();i++) sa[i] = ls.get(i);
      for(int i=0;i<ls.size();i++)
        {
          for(int j=(i+1);j<ls.size();j++)
            {
              if ((sa[i].compareToIgnoreCase(sa[j])) > 0)
                {
                  str = sa[j];
                  sa[j] = sa[i];
                  sa[i] = str;
                }
            }
        }
      //String headChar = "";
      for(int i=0;i<ls.size();i++) listModelIndex.addElement(sa[i]);

      return listModelIndex;
    }

  private String transformDataIntoContent(String cont)
    {
      int n = 0;
      String contC = "";
      String achar = "";
      String resCont = "";
      if (cont.indexOf("`") < 0) return cont;
      if (cont.startsWith("`")) cont = "$#" + cont;

      StringTokenizer st = new StringTokenizer(cont, "`");
      while(st.hasMoreTokens())
        {
          resCont = st.nextToken();

          if (n==0)
            {
              if (!resCont.equals("$#")) contC = resCont;
              n++;
              continue;
            }
          if ((resCont.toUpperCase()).startsWith("LT;")) contC = contC + "<" + resCont.substring(3);
          else if ((resCont.toUpperCase()).startsWith("GT;")) contC = contC + ">" + resCont.substring(3);
          else if ((resCont.toUpperCase()).startsWith("AMP;")) contC = contC + "&" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("QUOT;")) contC = contC + "\"" + resCont.substring(5);
          else if ((resCont.toUpperCase()).startsWith("CR;")) contC = contC + "\r" + resCont.substring(3);
          else if ((resCont.toUpperCase()).startsWith("LF;")) contC = contC + "\n" + resCont.substring(3);
          else if ((resCont.toUpperCase()).startsWith("NBSP;")) contC = contC + " " + resCont.substring(5);
          else if ((resCont.toUpperCase()).startsWith("SLH;")) contC = contC + "/" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("MNS;")) contC = contC + "-" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("|LT;")) contC = contC + "<" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("|GT;")) contC = contC + ">" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("|AMP;")) contC = contC + "&" + resCont.substring(5);
          else if ((resCont.toUpperCase()).startsWith("|QUOT;")) contC = contC + "\"" + resCont.substring(6);
          else if ((resCont.toUpperCase()).startsWith("|CR;")) contC = contC + "\r" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("|LF;")) contC = contC + "\n" + resCont.substring(4);
          else if ((resCont.toUpperCase()).startsWith("|NBSP;")) contC = contC + " " + resCont.substring(6);
          else if ((resCont.toUpperCase()).startsWith("|MNS;")) contC = contC + "-" + resCont.substring(5);
          else contC = contC + "`" + resCont;
        }
      return contC;
    }
    private void treeInitialConstruction()
      {
        String id = "";
        String title = "";
        String kwds = "";
        String content = "";
        String readLineOfFile = "";
        String mainCont = "";
        String recNumS = "";
        int recNum = 0;
        String[] param = new String[6];
        try
          {
            //FileReader fr = new FileReader(commonPath + Config.DEFAULT_HELP_DATA_FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(Config.DEFAULT_HELP_DATA_FILENAME)));
            id = "";
            title = "";
            kwds = "";
            content = "";
            int linCnt = 0;
            boolean cTag = false;
            boolean kTag = false;

            while((readLineOfFile=br.readLine())!=null)
              {
                linCnt++;
                if (readLineOfFile.startsWith("<?xml version='1.0' encoding")) continue;
                if (readLineOfFile.startsWith("<HelpData packagename=\"gov.nih.nci.caadapter.ui.help\"")) continue;
                if (readLineOfFile.indexOf("<HelpDataItem") >= 0)
                {
                    cTag = false;
                    kTag = true;
                }
                if (readLineOfFile.indexOf("</HelpDataItem") >= 0)
                {
                    cTag = true;
                    kTag = false;
                }
                mainCont = mainCont + readLineOfFile.trim();


                param[0] = id;
                param[1] = title;
                param[2] = kwds;
                param[3] = content;
                param[4] = "" + recNum;
                param[5] = mainCont;
                if (!cTag) continue;
                param = treeNodeGenerate(param);

                  //System.out.println("Line Count : " + linCnt);
                if (param == null) break;
                id = param[0];
                title = param[1];
                kwds = param[2];
                content = param[3];
                recNumS = param[4]; // = "" + recNum;
                mainCont = param[5];
                recNum = Integer.parseInt(recNumS);
              }
//            fr.close();
            br.close();
          }
        catch(IOException e)
          {
            protectFrameDispose = true;
            JOptionPane.showMessageDialog(this,"Help Data File Reading Failure..","Tree Construction Error!",JOptionPane.ERROR_MESSAGE);
          }
        while(param!=null)
        {
            param[0] = id;
                param[1] = title;
                param[2] = kwds;
                param[3] = content;
                param[4] = "" + recNum;
                param[5] = mainCont;
                param = treeNodeGenerate(param);
                  //System.out.println("Line Count : " + linCnt);
                if (param == null) break;
                id = param[0];
                title = param[1];
                kwds = param[2];
                content = param[3];
                recNumS = param[4]; // = "" + recNum;
                mainCont = param[5];
                recNum = Integer.parseInt(recNumS);//
        }
      }
    private String[] treeNodeGenerate(String[] param)
    {
        String id = param[0];
        String title = param[1];
        String kwds = param[2];
        String content = param[3];
        String recNumS = param[4]; // = "" + recNum;
        String mainCont = param[5];
        int recNum = Integer.parseInt(recNumS);
        //while(true)
        //  {
            if (mainCont.startsWith("</HelpData"))
            {
                generateContentNode();
                return null;
            }
            if (mainCont.equals(""))
            {
                generateContentNode();
                return null;
            }
            if (mainCont.startsWith("<HelpDataItem"))
              {
                int idx = mainCont.indexOf("\"");
                id = mainCont.substring(idx+1, mainCont.indexOf("\"", idx+1));

                String helpDataItem = mainCont.substring(mainCont.indexOf(">")+1, mainCont.indexOf("</HelpDataItem>"));
                mainCont = mainCont.substring(mainCont.indexOf("</HelpDataItem>") + 15);
                title = helpDataItem.substring(helpDataItem.indexOf("<NodeTitle>")+11, helpDataItem.indexOf("</NodeTitle>"));
                content = helpDataItem.substring(helpDataItem.indexOf("<NodeContent>")+13, helpDataItem.indexOf("</NodeContent>"));
                if (helpDataItem.indexOf("<KeyWords>") < 0) kwds = "";
                else
                  {
                    while(helpDataItem.indexOf("<KeyWords>") >= 0)
                      {
                        kwds = kwds + ", " + (helpDataItem.substring(helpDataItem.indexOf("<KeyWords>")+10, helpDataItem.indexOf("</KeyWords>"))).trim();
                        helpDataItem = helpDataItem.substring(helpDataItem.indexOf("</KeyWords>")+10);
                      }
                    kwds = kwds.substring(2);
                  }

                DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(new HelpContentElement(transformDataIntoContent(title), transformDataIntoContent(content), kwds));
                kwds = "";
                if (recNum == 0)
                  {
                    headOfMain = mainNode;
                    //headOfContent = contentNode;
                    currentNodeOfMain = headOfMain;
                        //currentNodeOfContent = headOfContent;
                  }
                else
                  {
                    setMainNodeAtFirst(id, mainNode);
                    //setContentNodeAtFirst(id, contentNode);
                  }

                recNum++;

              }
          //}
          param[0] = id;
          param[1] = title;
          param[2] = kwds;
          param[3] = content;
          param[4] = "" + recNum;
          param[5] = mainCont;
          return param;
      }

  private void generateContentNode()
  {
      DefaultMutableTreeNode tempNode = headOfMain;
      tempNode = tempNode.getNextNode();
      DefaultMutableTreeNode contNode = tempNode;
      String cont = "<br><font face='Times New Roman' size='6' color='blue'>Table of Content"
                  + "</font><br><font size='4' color='blue'><br>";
      String bold = "";
      String debold = "";
      while(true)
      {
          try { tempNode = tempNode.getNextNode(); }
          catch(Throwable e) { break; }
          if (tempNode == null) break;
          int level = tempNode.getLevel();
          if (level == 1) { bold = "<br><b>"; debold = "</b>";}
          else  {bold = ""; debold = ""; }
          String space = "";
          for (int i=0;i<level;i++) space = space + "&nbsp;&nbsp;&nbsp;&nbsp;";
          HelpContentElement ele = (HelpContentElement) tempNode.getUserObject();
          cont = cont + bold + space + "<a href='&NodeLink:" + getNodeID(tempNode) + "'>"
               + getNodeID(tempNode) + ". " + ele.getNodeTitle() + "</a>"+ debold + "<br>";
      }
      cont = cont + "</font>";
      contNode.setUserObject(new HelpContentElement("Table of Content", cont));
      HelpContentElement ele = (HelpContentElement) headOfMain.getUserObject();
      cont = "<img src='images/" + Config.SPLASH_WINDOW_IMAGE_FILENAME + "' width='520' height='390'>";
      ele.setNodeContent(cont);
      headOfMain.setUserObject(ele);
  }

  private void setMainNodeAtFirst(String id, DefaultMutableTreeNode node)
    {
      StringTokenizer st = new StringTokenizer(id, ".");
      //int id_s = 0;
      String curr = "";
      DefaultMutableTreeNode tempNode = headOfMain;
      int n = 0;

      while(st.hasMoreTokens())
        {
          curr = st.nextToken();
          int seq = Integer.parseInt(curr);
          if (n==0) seq = seq + 1;
          if ((tempNode.getChildCount()) >= seq)
            {
              tempNode = (DefaultMutableTreeNode) tempNode.getChildAt(seq-1);
            }
          else
            {
              tempNode.add(node);
              break;
            }
          n++;
        }
       return;
    }

  private DefaultMutableTreeNode findNodeOfMain(String id)
    {
      if (id.equals("-1")) return headOfMain;
      StringTokenizer st = new StringTokenizer(id, ".");
      String curr = "";
      DefaultMutableTreeNode tempNode = headOfMain;
      int n = 0;
      try
        {
          while(st.hasMoreTokens())
            {
              curr = st.nextToken();
              int seq = Integer.parseInt(curr);
              if (n == 0) seq = seq + 1;
              tempNode = (DefaultMutableTreeNode) tempNode.getChildAt(seq-1);
              n++;
            }
        }
      catch(NullPointerException ne)
        {
           return null;
        }
      catch(ArrayIndexOutOfBoundsException ne)
        {
           return null;
        }
      return tempNode;
    }

  private DefaultMutableTreeNode findNodeOfContent(String id)
    {
      if (id.equals("0")) return headOfMain;
      StringTokenizer st = new StringTokenizer(id, ".");
      String curr = "";
      DefaultMutableTreeNode tempNode = headOfMain;

      while(st.hasMoreTokens())
        {
          curr = st.nextToken();
          int seq = Integer.parseInt(curr);
          tempNode = (DefaultMutableTreeNode) tempNode.getChildAt(seq-1);
        }
       return tempNode;
    }

  private String getNodeID(DefaultMutableTreeNode node)
    {
      if (node == headOfMain) return "-1";
      //if (node == headOfContent) return "0";
      String st_id = "";
      DefaultMutableTreeNode tempNode = node;
      DefaultMutableTreeNode parentNode = null;

      while(true)
        {
          if (tempNode == headOfMain) break;
          //if (tempNode == headOfContent) break;
          parentNode = (DefaultMutableTreeNode) tempNode.getParent();
          if (parentNode == headOfMain) st_id = "." + ((parentNode.getIndex(tempNode))) + st_id;
          else st_id = "." + ((parentNode.getIndex(tempNode))+1) + st_id;
          tempNode = parentNode;
        }
      if (st_id.startsWith(".")) st_id = st_id.substring(1, st_id.length());
      return st_id;

    }
    private boolean searchNodeContent(DefaultMutableTreeNode node, String word)
      {
        HelpContentElement contElement = (HelpContentElement) node.getUserObject();
        String tit = (contElement.getNodeTitle()).trim();
        String cont = contElement.getNodeContent();
        if ((tit.toUpperCase()).indexOf(word.toUpperCase()) >= 0) return true;

        if (cont == null) return false;
        else cont = cont.trim();
        if (cont.length() == 0) return false;
        if ((cont.toUpperCase()).indexOf(word.toUpperCase()) < 0) return false;
        String core = "";
        String temp = cont;
        int idx = 0;
        int idx2 = 0;
        while(true)
        {
            idx = temp.indexOf("<");
            if (idx < 0) break;
            if (idx != 0) core = core + " " + temp.substring(0, idx);
            temp = temp.substring(idx);
            idx2 = temp.indexOf(">");
            if (idx2 < 0)
            {
                System.out.println("invalid HTML format : " + cont);
                continue;
            }
            temp = temp.substring(idx2+1);

        }
        core = core + " " +temp;

        foundTag = false;
        if ((core.toUpperCase()).indexOf(word.toUpperCase()) >= 0) foundTag = true;
        searchTag = true;
        searchStr = word;

        forSearchTag = true;
        //showNodeHTMLContent(cont);
        forSearchTag = false;
        return foundTag;
      }

  private void showNodeContent(DefaultMutableTreeNode node)
    {
      if (searchTag) showNodeContent(node, false);
      else showNodeContent(node, true);
    }

  private void showNodeContent(DefaultMutableTreeNode node, boolean assembleYN)
    {
      pushStack(node);
      //if (popupTag) popupTag = false;

      HelpContentElement contElement = (HelpContentElement) node.getUserObject();
      jtID.setText("");
      jtTitle.setText("");
      jaContent = "";
      //jaContent.updateUI();
      jtPathDisplay.setText("");
      //jepHTMLContent.setText("<html><head><title>help</title></head><body>.</body></html>");
      jtID.setText(getNodeID(node));
      jtTitle.setText(contElement.getNodeTitle());
      //showNodeContent("");
      jaContent = contElement.getNodeContent();
      if (assembleYN) showNodeHTMLContentSimple(assembleContent(node), false);
      else  showNodeHTMLContentSimple(addNodeIDAndTitleOnContent(node), true);

      if (node == headOfMain)
        {
          jtPathDisplay.setText(contElement.getNodeTitle());
          //showNodeContent(jaContent);
          return;
        }
      DefaultMutableTreeNode tempNode = node;
      String st = "";
      while(true)
        {
          if (tempNode == headOfMain) break;
          contElement = (HelpContentElement) tempNode.getUserObject();
          st = " > " + (contElement.getNodeTitle()) + st;
          tempNode = (DefaultMutableTreeNode) tempNode.getParent();
        }
      jtPathDisplay.setText(st.substring(3));

      return;
    }

  private String assembleContent(DefaultMutableTreeNode node)
    {
      HelpContentElement contElement = (HelpContentElement) node.getUserObject();
      if (node == headOfMain) return contElement.getNodeContent();
      if (node == findNodeOfMain("0")) return contElement.getNodeContent();
      String assembeledContent = addNodeIDAndTitleOnContent(node);
      DefaultMutableTreeNode tempNode = node;

      if (node.isLeaf()) return assembeledContent;
      while(true)
        {
          tempNode = tempNode.getNextNode();
          if (node.isNodeDescendant(tempNode)) assembeledContent = assembeledContent + addNodeIDAndTitleOnContent(tempNode);
          else break;
        }
      return assembeledContent;
    }

  private String addNodeIDAndTitleOnContent(DefaultMutableTreeNode node)
    {
      HelpContentElement contElement = (HelpContentElement) node.getUserObject();
      String content = (contElement.getNodeContent()).trim();
      if ((node.isLeaf())&&(content.equals(""))) content = "<font color='red'>&nbsp;&nbsp;&nbsp;Empty</font>";
      content = content.replace("!\n", " ");
      content = content.replace("!\r", " ");
      if ((content.toUpperCase()).startsWith("<HTML"))
        {
           content = content.substring(content.toUpperCase().indexOf("<BODY"), content.toUpperCase().indexOf("</BODY"));
           content = content.substring(content.indexOf(">")+1);
           content = content.replace("\r\n", " ");
           content = content.replace("\n", " ");
        }
      else if ((content.toUpperCase()).startsWith("HTTP://"))
        {
           content = "<a href='" + content + "'>" + content + "</a>";
        }
      else if ((content.toUpperCase()).startsWith("FILE:/"))
        {
           content = "<a href='" + content + "'>" + content + "</a>";
        }

      int depth = node.getLevel();
      String indentSpace = "";
      String fontStr = "";
      for(int i=0;i<(depth-1);i++) indentSpace = indentSpace + "&nbsp;&nbsp;";
      if (depth == 1) fontStr = "<font size='6' color='blue'>";
      else if (depth == 2) fontStr = "<font size='5' color='blue'>";
      else if (depth == 3) fontStr = "<font size='4' color='blue'>";
      else if (depth == 4) fontStr = "<font size='4' color='blue'>";
      else fontStr = "<font size='3' color='black'>";

      String brTag = "";
      if (depth == 1) brTag = "<br><br>";
      else if (depth == 2) brTag = "<br><br>";
      else if (depth == 3) brTag = "<br><br>";
      else brTag = "<br>";
      return fontStr + indentSpace + getNodeID(node) + ".&nbsp;&nbsp;" + contElement.getNodeTitle() + brTag
            + "</font>" + content + brTag + "<br>";
    }

    private String findImageFile(String srcX)
          {
            if ((srcX.toUpperCase()).startsWith(imagePath.toUpperCase())) return srcX;
            if ((srcX.toUpperCase()).startsWith(imageURIPath.toUpperCase())) return srcX;
            if ((srcX.toUpperCase()).startsWith(commonPath.toUpperCase())) return srcX;
            if ((srcX.toUpperCase()).startsWith(commonURIPath.toUpperCase())) return srcX;
            if ((srcX.toUpperCase()).startsWith(("../images/").toUpperCase())) return srcX;
            String src = srcX + "    ";
            String srcP = "";
            String achar = "";
            String chars = "";
            int n = 0;
            int tagPoint = 0;
            int endPoint = 0;
            int setPoint = 0;
            boolean findTag = false;
            boolean actTag = false;
            boolean isWrap = false;
            for(int i=0;i<(src.length()-4);i++)
              {
                isWrap = false;
                findTag = false;
                chars = src.substring(i, i+4);
                if ((chars.toUpperCase()).equals(".JPG")) findTag = true;
                if ((chars.toUpperCase()).equals(".TIF")) findTag = true;
                if ((chars.toUpperCase()).equals(".PNG")) findTag = true;
                if ((chars.toUpperCase()).equals(".BMP")) findTag = true;
                if ((chars.toUpperCase()).equals(".GIF")) findTag = true;
                if (findTag)
                  {
                    //tagPoint = i;
                    //findTag2 = false;
                    //Log.logInfo(this, "XXXXXX : "+chars);
                    for(int j=i;j>setPoint;j--)
                      {
                        achar = src.substring(j-1,j);
                        //Log.logInfo(this, "JJJ : "+achar);
                        if (achar.equals(">"))
                          {
                            for(int k=j;k<i;k++)
                              {
                                String achar2 = src.substring(k,k+1);
                                //Log.logInfo(this, "kkk : "+achar2);
                                if ((achar2.toUpperCase()).equals("<"))
                                  {
                                    String achar3 = src.substring(k,k+4);
                                    //Log.logInfo(this, "XXkkk : "+achar3);
                                    if ((achar3.toUpperCase()).equals("<IMG")) isWrap = true;
                                    else if (((achar3.substring(0,2)).toUpperCase()).equals("<A")) isWrap = true;
                                    else isWrap = false;
                                    break;
                                  }
                              }
                            if (isWrap) break;
                            tagPoint = 0;
                            endPoint = i+4;
                            for(int k=i;k>0;k--)
                              {
                                achar = src.substring(k-1, k);
                                //Log.logInfo(this, "2kkk : "+achar);
                                if (achar.equals(" ")) tagPoint = k;
                                if (achar.equals(">")) tagPoint = k;
                                if (tagPoint!=0) break;
                              }
                            srcP = srcP + src.substring(setPoint, tagPoint) + " <a href='" + src.substring(tagPoint, endPoint) + "'>"+src.substring((tagPoint), endPoint)+"</a> ";
                            i = endPoint;
                            setPoint = endPoint;
                            actTag = true;
                            isWrap = true;
                          }
                      }
                    if (isWrap) continue;
                    for(int k=setPoint;k<i;k++)
                      {
                        String achar2 = src.substring(k,k+1);
                        //Log.logInfo(this, "3kkk : "+achar);
                        if ((achar2.toUpperCase()).equals("<"))
                          {
                            String achar3 = src.substring(k,k+4);
                            if ((achar3.toUpperCase()).equals("<IMG")) isWrap = true;
                            else if (((achar3.substring(0,2)).toUpperCase()).equals("<A")) isWrap = true;
                            else isWrap = false;
                            break;
                          }
                      }
                    if (isWrap) continue;
                    tagPoint = 0;
                    endPoint = i+4;
                    for(int j=i;j>0;j--)
                      {
                        achar = src.substring(j-1, j);
                        //Log.logInfo(this, "2jjj : "+achar);
                        if (achar.equals(" ")) tagPoint = j;
                        if (achar.equals(">")) tagPoint = j;
                        if (tagPoint!=0) break;
                      }
                    //srcP = srcP + src.substring(setPoint, tagPoint) + " <img src='" + src.substring(tagPoint, endPoint) + "'> ";
                    srcP = srcP + src.substring(setPoint, tagPoint) + " <a href='" + src.substring(tagPoint, endPoint) + "'>"+src.substring((tagPoint), endPoint)+"</a> ";

                    i = endPoint;
                    setPoint = endPoint;
                    actTag = true;
                  }
              }
            if (!actTag) return src;
            else return srcP + src.substring(endPoint);
          }


  private String organizeString(String src, String folderMark, String tag)
    {
      String srcP = "";
      String tempA = src;
      int idx = 0;
      String pref = "";
      if (folderMark.equals(stImageDir)) pref = "../images/";

      while(true)
        {
          tempA = tempA.substring(idx);
          idx = (tempA.toUpperCase()).indexOf(folderMark.toUpperCase());

          if (idx < 0)
            {
              srcP = srcP + tempA;
              break;
            }
          srcP = srcP + tempA.substring(0,idx) + pref;
          //Log.logInfo(this, "  ===> " + srcP);
          idx = idx + folderMark.length();
        }
      if (searchTag) srcP = markingFoundWords(srcP, tag);
      return srcP;
    }
    private int checkIndexTag(String src, String tag)
    {
        int idxTag = -1;
        idxTag = (src.toUpperCase()).indexOf("<A NAME=\""+tag.toUpperCase()+"\"");
        if (idxTag < 0) idxTag = (src.toUpperCase()).indexOf("<A NAME='"+tag.toUpperCase()+"'");
        if (idxTag < 0) idxTag = (src.toUpperCase()).indexOf("<A NAME="+tag.toUpperCase());
        if (idxTag < 0) idxTag = (src.toUpperCase()).indexOf("<A  NAME=\""+tag.toUpperCase()+"\"");
        if (idxTag < 0) idxTag = (src.toUpperCase()).indexOf("<A  NAME='"+tag.toUpperCase()+"'");
        if (idxTag < 0) idxTag = (src.toUpperCase()).indexOf("<A  NAME="+tag.toUpperCase());
        return idxTag;
    }
    private String markingFoundWords(String src, String tag)
    {
      if (tag == null) tag = "";
      int p = 0;
      String tail = "";
      String tempA = "";
      String srcP = "";
      int idxTag = 0;
      int idxTag2 = 0;
      if (!(tag.equals("")))
        {
          idxTag = checkIndexTag(src, tag);

          if (idxTag < 0) tag = "";
          else
            {
              int in = src.toUpperCase().indexOf(">", idxTag);
              idxTag2 = (src.toUpperCase()).indexOf("<A NAME=", idxTag+5);
              if (idxTag2 < 0) idxTag2 = (src.toUpperCase()).indexOf("<A  NAME=", idxTag+5);
              if (idxTag2 < 0) idxTag2 = (src.toUpperCase()).indexOf("</BODY");

              srcP = src.substring(0, in+1);
              tempA = src.substring(in+1, idxTag2);
              tail = src.substring(idxTag2);
            }
        }
      if (tag.equals(""))
        {
          p = (src.toUpperCase()).indexOf("<BODY");
          p = src.indexOf(">", p) + 1;
          tempA = (src.substring(p)).trim();
          srcP = src.substring(0, p);
          tempA = (tempA.substring(0, (tempA.toUpperCase()).indexOf("</BODY"))).trim();
          tail = (src.substring((src.toUpperCase()).indexOf("</BODY"))).trim();
        }
      return srcP + editBodyString(tempA) + tail;
    }
  private String editBodyString(String tempA)
  {
      searchTag = false;
      int idx = 0;
      String cont = "";
      while(true)
        {
          if (tempA.startsWith("<"))
          {
              idx = tempA.indexOf(">");
              if (idx < 0)
              {
                  cont = cont + tempA;
                  System.out.println("Invalid HTML format!! : " + cont);
                  break;
              }
              cont = cont + tempA.substring(0, idx);
              tempA = tempA.substring(idx);
              continue;
          }
          idx = tempA.indexOf("<");
          if (idx < 0)
          {
              cont = cont + transformBoldFontForSearchString(tempA, searchStr);
              break;
          }
          cont = cont + transformBoldFontForSearchString(tempA.substring(0,idx), searchStr);
          tempA = tempA.substring(idx);
        }
      return cont;
  }
  private String transformBoldFontForSearchString(String src, String search)
  {
      search = search.trim();
      String cont = "";
      int searchLen = search.length();
      int idx = src.toUpperCase().indexOf(search.toUpperCase());
      if (idx < 0) return src;
      if (forSearchTag)
      {
          foundTag = true;
          return src;
      }
      while(true)
      {
          cont = cont + src.substring(0, idx) + "<b>";
          src = src.substring(idx);
          cont = cont + src.substring(0, searchLen) + "</b>";
          src = src.substring(searchLen);
          idx = src.toUpperCase().indexOf(search.toUpperCase());
          if (idx < 0) break;
      }
      return cont + src;
  }

  private String organizeHTML(String src, String tag)
    {
      String srcP = "";
      srcP = organizeString(src, stHelpDir, tag);
      return organizeString(srcP, stImageDir, tag);
    }
  protected void showNodeHTMLContentSimple(String srcX, boolean srch)
    {
       String h1 = "<html><head><title>";
       String h2 = "</title></head><body>";
       String h3 = "</body></html>";

       if (srch) srcX = editBodyString(srcX);
       srcX = srcX.replaceAll("\n", "<br>");
       srcX = srcX.replaceAll("!<br>", " ");
       if (contentPageTag.equals(""))
        {
            stringHTMLDisplay(h1+"Title"+h2+srcX+h3);
        }
       else
        {
            String contTag = contentPageTag;
            contentPageTag = "";
            stringHTMLDisplay(h1+"Title"+h2+srcX+h3, contTag);
        }
    }


  protected void showNodeHTMLContent(String srcX)
    {
       String src = srcX.trim();
       String cont = "";
       String brTag = "";
       String brTagX = "";
       boolean enterKeyChangeIntoBRTag = true;
       if ((src.toUpperCase()).startsWith("{NOBR}"))
         {
           brTagX = "";
           src = (src.substring(("{NOBR}").length())).trim();
         }
       else if ((src.toUpperCase()).startsWith("<HTML")) brTagX = "";
       else if (((src.toUpperCase()).startsWith("FILE:"))||((src.toUpperCase()).startsWith("HTTP:")))
               brTagX = "";
       else if (((src.toUpperCase()).startsWith(stHelpDir.toUpperCase()))||((src.toUpperCase()).startsWith(stImageDir.toUpperCase())))
               brTagX = "";
       else brTagX = "<br>";
       for (int i=0;i<src.length();i++)
         {
           String currentStartsWith = (src.substring(i)).toUpperCase();
           String achar = src.substring(i, i+1);
           if (currentStartsWith.startsWith("<TABLE")) {enterKeyChangeIntoBRTag = false; }
           if (currentStartsWith.startsWith("<TD")) {enterKeyChangeIntoBRTag = true; }
           if (currentStartsWith.startsWith("</TD>")){ enterKeyChangeIntoBRTag = false; }
           if (currentStartsWith.startsWith("</TABLE>")) {enterKeyChangeIntoBRTag = true; }

           if (enterKeyChangeIntoBRTag) brTag = brTagX;
           else brTag = "!\r\n";

           if (achar.equals("\n")) cont = cont + brTag;
           else if (achar.equals("\r")) achar = "";
           else cont = cont + achar;
         }
       cont = cont.replace("!\r\n", " ");
       cont = cont.replace("!<br>", " ");
       //String srcP = organizeHTML(cont);

       String ss = findImageFile(cont);
       if (((src.toUpperCase()).startsWith("FILE:"))||((src.toUpperCase()).startsWith("HTTP:")))
               showNodeHTMLContent(ss, "F");
       else if (((src.toUpperCase()).startsWith(stHelpDir.toUpperCase()))||((src.toUpperCase()).startsWith(stImageDir.toUpperCase())))
               showNodeHTMLContent(ss, "F");
       else showNodeHTMLContent(ss, "");
    }

  private void showNodeHTMLContent(String srcP, String linkP)
    {
      String src = srcP;
      String link = "";

      int idxx = -1;
      while(linkP.equals("F"))
        {
          idxx = srcP.indexOf("##");
          if (idxx > 0)
            {
              src = (srcP.substring(0, idxx)).trim();
              link = (srcP.substring(idxx + 2)).trim();
              break;
            }
          idxx = srcP.indexOf("#");
          if (idxx > 0)
            {
              src = (srcP.substring(0, idxx)).trim();
              link = (srcP.substring(idxx+1)).trim();
            }
          break;
        }
      linkP = "";
          //Log.logInfo(this, "E point : " + src + ", " + link);

      String h1 = "<html><head><title>";
      String h2 = "</title></head><body>";
      String h3 = "</body></html>";
      String cont = "";
      String contSource = "";
      String contSource2 = src.trim();
      boolean emptyContentTag = false;
      String achar = "";

      if ((contSource2+"!").equals("!")) emptyContentTag = true;
      contSource = src;
      if (emptyContentTag)
        {
          String tit = ((HelpContentElement) selectedNodeOfMain.getUserObject()).getNodeTitle();
          if (tit.equals("")) tit = "</font><font size='5' color='red'>No Title";
          cont = "<html><head><title>help</title></head><body><font size='5' color='blue'>"
               + getNodeID(selectedNodeOfMain) + ". " + tit + "</font><font size='3' color='red'><br>(No content)</font></body></html>";
          stringHTMLDisplay(cont);
          return;
        }
      if ((contSource2.toUpperCase()).startsWith("HTTP://"))
        {
          if (link.length() > 0) link = "#" + link;
          else link = "";
          try
            {
              jepHTMLContent.setPage(new URL(contSource2 + link));
              currentPage = contSource2 + link;
            }
          catch(IOException ie)
            {
              stringHTMLDisplay(h1 + "Error!" + h2 + "<font color='red'>Unidentified URL address : </font>" + contSource2 + link + h3);
            }
          catch(Throwable t)
            {
              stringHTMLDisplay(h1 + "Error!" + h2 + "Malformed Exception(2) : " + contSource2 + link + h3);
            }
        }
      else if ((contSource2.toUpperCase()).startsWith("<HTML"))
        {
           stringHTMLDisplay(contSource2, "");
        }
      else if (((contSource2.toUpperCase()).startsWith(stHelpDir.toUpperCase()))||((contSource2.toUpperCase()).startsWith(commonPath.toUpperCase())))
        {
          String urlPath = "";
          String comPath = "";
          if (contSource2.startsWith(commonPath))
            {
               comPath = contSource2;
               urlPath = commonURIPath + contSource2.substring(commonPath.length());
            }
          else
            {
              urlPath = commonURIPath + contSource2.substring(stHelpDir.length());
              comPath = commonPath + contSource2.substring(stHelpDir.length());
            }
          cont = "";
          String rn = "";
          String filetype = "";
          if ((contSource2.toUpperCase()).endsWith("HTML"))  filetype = "HTML";
          else if ((contSource2.toUpperCase()).endsWith("HTM"))  filetype = "HTML";
          else if ((contSource2.toUpperCase()).endsWith("TXT"))  filetype = "TEXT";
          else if ((contSource2.toUpperCase()).endsWith("JPEG"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("GIF"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("PNG"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("BMP"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("TIF"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("TIFF"))  filetype = "IMAGE";
          else filetype = "TEXT";
          String tg = "img";
          if (filetype.equals("HTML")) tg = "\r\n";
          if (filetype.equals("TEXT")) tg = "<br>\r\n";
          if (tg.equals("img"))
            {
              stringHTMLDisplay(h1 + "Image Display</title></head><body><img src='" + urlPath.substring(commonURIPath.length()) + "'><br></body></html>");
            }
          else
            {
              if (filetype.equals("TEXT")) cont = h1 + "Text Display" + h2 + "<br>\r\n";

              try
                {
                  FileReader fr = new FileReader(comPath);
                  BufferedReader br = new BufferedReader(fr);
                  while((rn=br.readLine())!=null)
                    {
                      cont = cont + rn + tg;
                    }

                  fr.close();
                  br.close();

                  //jepHTMLContent.setPage(new URL(urlPath));
                  if (filetype.equals("TEXT")) stringHTMLDisplay(cont + "<br>" + h3);
                  else stringHTMLDisplay(cont, link);
                }
              catch(IOException ie)
                {
                  if (!link.equals("")) link = "#" + link;
                  stringHTMLDisplay(h1 + "URL Path Error!" + h2 + "<font color='red'>This URL Path cannot be found the Page(1) : </font>" + urlPath + link + "<br>" + comPath + "<br>" + ie.getMessage() + h3);
                }
            }
        }
      else if (((contSource2.toUpperCase()).startsWith(stImageDir.toUpperCase()))||((contSource2.toUpperCase()).startsWith(imagePath.toUpperCase())))
        {
          String urlPath = "";
          String comPath = "";
          if (contSource2.startsWith(imagePath))
            {
               comPath = contSource2;
               urlPath = imageURIPath + contSource2.substring(imagePath.length());
            }
          else
            {
              urlPath = imageURIPath + contSource2.substring(stImageDir.length());
              comPath = imagePath + contSource2.substring(stImageDir.length());
            }

          cont = "";
          String rn = "";
          String filetype = "";
          if ((contSource2.toUpperCase()).endsWith("HTML"))  filetype = "HTML";
          else if ((contSource2.toUpperCase()).endsWith("HTM"))  filetype = "HTML";
          else if ((contSource2.toUpperCase()).endsWith("TXT"))  filetype = "TEXT";
          else if ((contSource2.toUpperCase()).endsWith("JPEG"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("GIF"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("PNG"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("BMP"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("TIF"))  filetype = "IMAGE";
          else if ((contSource2.toUpperCase()).endsWith("TIFF"))  filetype = "IMAGE";
          else filetype = "TEXT";
          String tg = "img";
          if (filetype.equals("HTML")) tg = "\r\n";
          if (filetype.equals("TEXT")) tg = "<br>\r\n";
          if (tg.equals("img"))
            {
              stringHTMLDisplay(h1 + "Image Display</title></head><body><img src='../images/" + urlPath.substring(imageURIPath.length()) + "'><br></body></html>");
            }
          else
            {
              if (filetype.equals("TEXT")) cont = h1 + "Text Display" + h2 + "<br>\r\n";
              try
                {
                  FileReader fr = new FileReader(comPath);
                  BufferedReader br = new BufferedReader(fr);

                  while((rn=br.readLine())!=null)
                    {
                      cont = cont + rn + tg;
                    }
                  fr.close();
                  br.close();

                  //jepHTMLContent.setPage(new URL(urlPath));
                  if (filetype.equals("TEXT")) stringHTMLDisplay(cont + "<br>" + h3);
                  else stringHTMLDisplay(cont, link);
                }
              catch(IOException ie)
                {
                  stringHTMLDisplay(h1 + "URL Path Error!" + h2 + "<font color='red'>This URL Path cannot be found the Page(2) : </font>" + urlPath + link + "<br>" + comPath + "<br>" + ie.getMessage() + h3);
                }
            }
        }

      else
        {
          cont = "";
          for(int i=0;i<contSource.length();i++)
            {
              achar = srcP.substring(i, i+1);
              if (achar.equals("\n")) cont = cont + "<br>";
              else if (achar.equals("\r")) continue;
              else cont = cont + achar;
            }
          cont = "<html><head><title>help</title></head><body>" + cont + "</body></html>";
          stringHTMLDisplay(cont);
        }
    }

  protected void stringHTMLDisplay(String src)
    {
      stringHTMLDisplay(src, "");
    }

  private void stringHTMLDisplay(String src, String link)
    {
      String srcP = "";
      if (searchTag) srcP = organizeHTML(src, link);
      else srcP = src;
      if (forSearchTag) return;
      //Log.logInfo(this, "F point : "+src+", " + link);
      FileWriter fw = null;
      //Random rn = new Random();
      String url = "Initial value!";
      try
        {
          url = (jepHTMLContent.getPage()).toString();
        }
      catch(NullPointerException ne)
        {}
      String filename = "";

      int idx = url.indexOf(Config.HELP_TEMPORARY_FILENAME_FIRST);
      if (idx < 0) filename = Config.HELP_TEMPORARY_FILENAME_FIRST;
      else filename = Config.HELP_TEMPORARY_FILENAME_SECOND;

      srcP = srcP.replaceAll("<img src='../", "<img src='../components/userInterface/resources/");
      //srcP = srcP.replaceAll("<img src='../", "<img src='jar:file:/C:/projects/caadapter/lib/caAdapter.jar!");
      //todo change this code to read image file from caadapter.jar file using class loader.
      //Log.logInfo(this, "G point : "+src+", " + link);

      int t = 0;
      try
        {
          fw = new FileWriter(commonPath + filename);
          t++;
          fw.write(srcP);
          t++;
          fw.close();
        }
      catch(IOException ie)
        {
          protectFrameDispose = true;
          if (t==0) JOptionPane.showMessageDialog(this,"Temp HTML File Writing Open Error.","",JOptionPane.ERROR_MESSAGE);
          else if (t==1) JOptionPane.showMessageDialog(this,"Temp HTML File Writing Error.","",JOptionPane.ERROR_MESSAGE);
          else if (t==2) JOptionPane.showMessageDialog(this,"Temp HTML File Closing Error.","",JOptionPane.ERROR_MESSAGE);
          protectFrameDispose = false;
          return;
        }
      String conURL = "";
      if (link.length() > 0) idx = checkIndexTag(srcP, link);
      else idx = -1;
      try
        {
          if (idx < 0) conURL = commonURIPath + filename;
          else conURL = commonURIPath + filename + "#" + link;

          jepHTMLContent.setPage(new URL(conURL));
          currentPage = conURL;
          //Log.logInfo(this, "***** current page :" + currentPage + "\n" + srcP);
          if ((link.length() > 0)&&(idx < 0))
          {
              protectFrameDispose = true;
              JOptionPane.showMessageDialog(this,"There is no '#" + link + "' pointer in this HTML file.\r\n" + conURL,"",JOptionPane.WARNING_MESSAGE);
          }

          //Log.logInfo(this, "G point : " + conURL);
        }
      catch(IOException ie)
        {
          protectFrameDispose = true;
          JOptionPane.showMessageDialog(this,"Invalid Temp HTML File!. : " + conURL,"",JOptionPane.ERROR_MESSAGE);
          return;
        }
    }




    class WinCloseExit extends WindowAdapter
	  {
		  HelpContentViewer tt;

		  WinCloseExit(HelpContentViewer st)
			  {
				  tt = st;
			  }
		  public void windowClosing(WindowEvent e)
			  {
                  tt.dispose();
              }
          public void windowLostFocus(WindowEvent e)
              {

                  //tt.dispose();
              }
          public void windowStateChange(WindowEvent e)
              {

                  //tt.dispose();
              }
          public void windowDeactivated(WindowEvent e)
            {
                int i = 0;
                if (protectFrameDispose) protectFrameDispose = false;
                else if(pageSetTag < 0) i++; //tt.dispose();

                ////else if(pageSetTag == 0)
                ////Log.logInfo(this, "DEACTIVATE : " + pageSetTag);
            }
          public void windowActivated(WindowEvent e)
            {
               if(pageSetTag == 0) pageSetTag = 2;
               else if(pageSetTag == 2) pageSetTag = -1;
               //pageSetTag++;
               //Log.logInfo(this, "ACTIVATE : " + pageSetTag);
            }
          public void windowClosed(WindowEvent e)
            {
               //Log.logInfo(this, "Closed : " + pageSetTag);
            }
          public void windowOpened(WindowEvent e)
            {
               //Log.logInfo(this, "Opened : " + pageSetTag);
            }
        }

  class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }



  public static void main(String arg[])
    {
      new HelpContentViewer();
    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/14 20:35:16  umkis
 * HISTORY      : add '//todo change this code to read image file from caadapter.jar file using class loader.'
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/07 15:15:24  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.46  2007/01/04 00:28:56  umkis
 * HISTORY      : Trivial error correction.
 * HISTORY      :
 * HISTORY      : Revision 1.45  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.44  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.43  2006/02/06 16:22:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.42  2006/01/19 16:42:32  umkis
 * HISTORY      : change title from "Help" to "caAdapter Help".
 * HISTORY      :
 * HISTORY      : Revision 1.41  2006/01/19 05:39:20  umkis
 * HISTORY      : Change codes related to text searching
 * HISTORY      :
 * HISTORY      : Revision 1.40  2006/01/18 18:06:24  umkis
 * HISTORY      : use SimpleProgressMonitor for text searching
 * HISTORY      :
 * HISTORY      : Revision 1.39  2006/01/17 19:39:06  umkis
 * HISTORY      : HelpContentViewer is Locate in mainFrame, sequencial data file reading.
 * HISTORY      :
 * HISTORY      : Revision 1.38  2006/01/12 19:50:04  umkis
 * HISTORY      : by asking from UAT team, the help frame can be NOT-disappeared in spite of its lost-focusing.
 * HISTORY      :
 * HISTORY      : Revision 1.37  2006/01/04 15:02:50  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.36  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.35  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.34  2006/01/03 16:12:31  umkis
 * HISTORY      : defect number 239 : print support class change -> DocumentRenderer
 * HISTORY      :
 * HISTORY      : Revision 1.33  2006/01/03 16:10:22  umkis
 * HISTORY      : defect# 239 : printing support class change => DocumentRenderer
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/12/14 15:39:46  umkis
 * HISTORY      : defect# 232
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/11/23 17:32:23  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/11/18 05:57:53  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/11/16 07:52:10  umkis
 * HISTORY      : default page set up
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/11/15 06:43:14  umkis
 * HISTORY      : final
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/11/09 19:31:13  umkis
 * HISTORY      : Define default variables for printing paper size and margins
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/11/03 14:30:38  umkis
 * HISTORY      : code re-organize
 * HISTORY      :
 */
