/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import edu.knu.medinfo.hl7.v2tree.ElementNode;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import edu.knu.medinfo.hl7.v2tree.MetaDataLoader;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.help.HelpContentElement;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.util.StringTokenizer;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jan 17, 2008
 *          Time:       4:30:30 PM $
 */
public class V2MessageBrowser extends JPanel implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2MessageBrowser.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/v2v3/tools/V2MessageBrowser.java,v 1.4 2008-06-09 19:53:50 phadkes Exp $";

    private HL7V2MessageTree messaggTree;



    JTree treeMain;
    HL7V2MessageTree treeHL7V2Main;
    DefaultMutableTreeNode selectedNodeOfMain;
    DefaultMutableTreeNode headOfMain;
    DefaultMutableTreeNode currentNodeOfMain;

    JButton jbGoPreviousNode;
    JButton jbGoNextNode;
    JButton jbFileSearch;
    JButton jbValueEdit;
    JButton jbGenerateHL7;
    JButton jbGenerateXML;
    JButton jbStart;
    JButton jbEnd;

    JTextField jtValueInput;
    JTextField jtFileName;
    JTextField jtPathDisplay;
    JPanel jp1;
    String jaContent;
    JTable nodeAttributeTable;
    JEditorPane jepHTMLContent;
    //JTabbedPane tabbedPane;

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
    //MainFrame mainFrame;
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

    MetaDataLoader metadataPath = null;
    String messageFileName = "";

      private boolean taskComplete;

      private ProgressMonitor progressMonitor;
      //private GeneralTask longTask;

      private Timer timer;

      public final static int ONE_SECOND = 1000;

  //String dirSeparater;
    public V2MessageBrowser() throws HL7MessageTreeException
    {
        setMetaDataPath(null);
        //metadataPath = FileUtil.getV2ResourceMetaDataLoader();
        constructPanel();
    }
    public V2MessageBrowser(Object datapath) throws HL7MessageTreeException
    {
        setMetaDataPath(datapath);
        //metadataPath = datapath;
        constructPanel();
    }
    public V2MessageBrowser(Object datapath, String filename) throws HL7MessageTreeException
    {
        //metadataPath = datapath;
        setMetaDataPath(datapath);
        messageFileName = filename;
        constructPanel();
    }

    private void setMetaDataPath(Object meta) throws HL7MessageTreeException
    {
        if (meta == null)
        {
            meta = "";
        }
        if (meta instanceof String)
        {
            String metaS = ((String)meta).trim();
            if (metaS.equals(""))
            {
                metadataPath = FileUtil.getV2ResourceMetaDataLoader();
                if (metadataPath == null)
                {
                    throw new HL7MessageTreeException("No v2 resource zip file");
                }
            }
            else
            {
                metadataPath = FileUtil.getV2ResourceMetaDataLoader(metaS);
                if (metadataPath == null)
                {
                    throw new HL7MessageTreeException("Invalid v2 resource path or zip file : " + metaS);
                }
            }
        }
        else if (meta instanceof MetaDataLoader) metadataPath = (MetaDataLoader) meta;
        else throw new HL7MessageTreeException("Invalid instance of v2 resource object");
    }
    private void constructPanel() throws HL7MessageTreeException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            throw new HL7MessageTreeException("(V2MessageBrowser) Error while setting system look and feel.");
            //return;
        }


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

      imagePath = FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater;

      imageURIPath = "file:///";
      for(int i=0;i<imagePath.length();i++)
        {
          String achar = imagePath.substring(i, i+1);
          if (achar.equals(dirSeparater)) imageURIPath = imageURIPath + "/";
          else imageURIPath = imageURIPath + achar;
        }

      //treeInitialConstruction();
      headOfMain = new DefaultMutableTreeNode(new ElementNode());
      //treeInitialConstruction("ADT^A03");
      treeMain = new JTree(headOfMain);
      //treeContent = new JTree(headOfContent);
      JScrollPane js1 = new JScrollPane(treeMain);



      //JPanel jp1 = new JPanel(new BorderLayout());
      jp1 = new JPanel(new BorderLayout());
      jp1.add(new JPanel(), BorderLayout.NORTH);
      jp1.add(js1, BorderLayout.CENTER);

      ImageIcon ii1=new ImageIcon(FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater + "Up24.gif");
      jbGoPreviousNode = new JButton(ii1);
      ImageIcon ii2=new ImageIcon(FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater + "Down24.gif");
      jbGoNextNode = new JButton(ii2);
      JPanel jp1_1 = new JPanel(new BorderLayout());
      JPanel jp1_2 = new JPanel(new BorderLayout());
      jp1_1.add(jbGoNextNode, BorderLayout.EAST);
      jp1_1.add(new JLabel(" "), BorderLayout.CENTER);
      jp1_1.add(jbGoPreviousNode, BorderLayout.WEST);
      jp1_2.add(new JLabel(" "), BorderLayout.EAST);
      jp1_2.add(jp1_1, BorderLayout.CENTER);
      jp1_2.add(new JLabel(" "), BorderLayout.WEST);

      jp1.add(jp1_2, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JPanel(), BorderLayout.SOUTH);
        leftPanel.add(jp1, BorderLayout.CENTER);



        JPanel rightUpperPanel = new JPanel(new BorderLayout());
        jtPathDisplay = new JTextField();
        rightUpperPanel.add(jtPathDisplay, BorderLayout.NORTH);
        nodeAttributeTable = new JTable();
        JScrollPane js2 = new JScrollPane(nodeAttributeTable);
        rightUpperPanel.add(js2, BorderLayout.CENTER);
        rightUpperPanel.add(new JPanel(), BorderLayout.SOUTH);

        JPanel rightLowerPanel = new JPanel(new BorderLayout());

        JPanel northOfRightLowerPanel = new JPanel(new BorderLayout());
        northOfRightLowerPanel.add(new JLabel(" Value   "), BorderLayout.WEST);
        jtValueInput = new JTextField();
        northOfRightLowerPanel.add(jtValueInput, BorderLayout.CENTER);
        jbValueEdit = new JButton("Input");
        northOfRightLowerPanel.add(jbValueEdit, BorderLayout.EAST);
        rightLowerPanel.add(northOfRightLowerPanel, BorderLayout.NORTH);

        JPanel leftOfLowerCenterPanel = new JPanel(new BorderLayout());
        leftOfLowerCenterPanel.add(new JLabel(" TValue "), BorderLayout.NORTH);
        leftOfLowerCenterPanel.add(new JPanel(), BorderLayout.CENTER);

        JPanel centerOfLowerPanel = new JPanel(new BorderLayout());
        centerOfLowerPanel.add(leftOfLowerCenterPanel, BorderLayout.WEST);
        try
        {
          jepHTMLContent = new JEditorPane("text/html", "<html><head><title>help</title></head><body><font color='blue'> </font></body></html>");
        }
        catch(Throwable t)
        {
          //Log.logInfo(this, "IO Error...");
        }
        jepHTMLContent.setEditable(false);
        JScrollPane js3 = new JScrollPane(jepHTMLContent);
        js3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        centerOfLowerPanel.add(js3, BorderLayout.CENTER);
        centerOfLowerPanel.add(new JPanel(), BorderLayout.EAST);

        rightLowerPanel.add(centerOfLowerPanel, BorderLayout.CENTER);

        JPanel leftCenterOfSouthRightLowerPanel = new JPanel(new BorderLayout());
        leftCenterOfSouthRightLowerPanel.add(new JLabel(" "), BorderLayout.WEST);
        jbGenerateHL7 = new JButton("Generate HL7 Message");
        leftCenterOfSouthRightLowerPanel.add(jbGenerateHL7, BorderLayout.CENTER);
        leftCenterOfSouthRightLowerPanel.add(new JLabel(" "), BorderLayout.EAST);

        JPanel rightCenterOfSouthRightLowerPanel = new JPanel(new BorderLayout());
        rightCenterOfSouthRightLowerPanel.add(new JLabel(" "), BorderLayout.WEST);
        jbGenerateXML = new JButton("Generate XML Message");
        rightCenterOfSouthRightLowerPanel.add(jbGenerateXML, BorderLayout.CENTER);
        rightCenterOfSouthRightLowerPanel.add(new JLabel(" "), BorderLayout.EAST);

        JPanel centerOfSouthRightLowerPanel = new JPanel(new BorderLayout());
        centerOfSouthRightLowerPanel.add(leftCenterOfSouthRightLowerPanel, BorderLayout.WEST);
        centerOfSouthRightLowerPanel.add(new JLabel(" "), BorderLayout.CENTER);
        centerOfSouthRightLowerPanel.add(rightCenterOfSouthRightLowerPanel, BorderLayout.EAST);

        JPanel southRightLowerPanel = new JPanel(new BorderLayout());
        southRightLowerPanel.add(new JLabel(" "), BorderLayout.WEST);
        southRightLowerPanel.add(centerOfSouthRightLowerPanel, BorderLayout.CENTER);
        jbEnd = new JButton("Close");
        southRightLowerPanel.add(jbEnd, BorderLayout.EAST);

        rightLowerPanel.add(southRightLowerPanel, BorderLayout.SOUTH);

        rightUpperPanel.setPreferredSize(new Dimension(240, 530));
        rightUpperPanel.setMinimumSize(new Dimension(10, 10));


        rightLowerPanel.setPreferredSize(new Dimension(550, 530));
        rightLowerPanel.setMinimumSize(new Dimension(10, 10));

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                              rightUpperPanel,
                                                              rightLowerPanel);
                        rightSplitPane.setOneTouchExpandable(true);
                        rightSplitPane.setResizeWeight(0.5);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);







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


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel southOfMainNorthPenel = new JPanel(new BorderLayout());
        southOfMainNorthPenel.add(new JLabel(" Message Type or File "), BorderLayout.WEST);
        JPanel centerOfSouthOfMainNorthPenel = new JPanel(new BorderLayout());
        jtFileName = new JTextField();
        centerOfSouthOfMainNorthPenel.add(new JLabel(""), BorderLayout.WEST);
        centerOfSouthOfMainNorthPenel.add(jtFileName, BorderLayout.CENTER);
        jbFileSearch = new JButton("File Search");
        centerOfSouthOfMainNorthPenel.add(jbFileSearch, BorderLayout.EAST);
        southOfMainNorthPenel.add(centerOfSouthOfMainNorthPenel, BorderLayout.CENTER);
        jbStart = new JButton("Start");
        southOfMainNorthPenel.add(jbStart, BorderLayout.EAST);


        JPanel centerOfMainNorthPenel = new JPanel(new BorderLayout());

        JLabel jl = new JLabel("HL7 v2 Message Browser and Editor");
        jl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        jl.setForeground(Color.blue);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        centerOfMainNorthPenel.add(jl, BorderLayout.CENTER);

        JLabel jl2 = new JLabel("Developed by KNU");
        jl2.setFont(new Font("Arial", Font.PLAIN, 9));
        centerOfMainNorthPenel.add(jl2, BorderLayout.EAST);
        centerOfMainNorthPenel.add(new JPanel(), BorderLayout.WEST);
        centerOfMainNorthPenel.add(new JPanel(), BorderLayout.SOUTH);

        JPanel northOfMainPenel = new JPanel(new BorderLayout());
        northOfMainPenel.add(new JPanel(), BorderLayout.NORTH);
        northOfMainPenel.add(centerOfMainNorthPenel, BorderLayout.CENTER);
        northOfMainPenel.add(southOfMainNorthPenel, BorderLayout.SOUTH);


        //mainPanel.add(southOfMainNorthPenel, BorderLayout.NORTH);

        setLayout(new BorderLayout());
        add(northOfMainPenel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.SOUTH);

        jbGoPreviousNode.addActionListener(this);
        jbGoNextNode.addActionListener(this);
        jbFileSearch.addActionListener(this);
        jbValueEdit.addActionListener(this);
        jbGenerateHL7.addActionListener(this);
        jbGenerateXML.addActionListener(this);
        jbStart.addActionListener(this);
        jbEnd.addActionListener(this);

        jtValueInput.setEditable(false);
        jtFileName.setEditable(true);
        jtPathDisplay.setEditable(false);



      //show();





      treeMain.addTreeSelectionListener
        (
          new TreeSelectionListener()
            {
              public void valueChanged(TreeSelectionEvent e)
                {
                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMain.getLastSelectedPathComponent();

                  if (node == null)
                    {
                      //if (!searchTag) showNodeContent(selectedNodeOfMain);
                      return;
                    }

                  selectedNodeOfMain = node;
                  currentNodeOfMain = selectedNodeOfMain;

                  //if (!searchTag) showNodeContent(selectedNodeOfMain);
                }
            }
        );

      linkNodeID("0");

      //createPopupMenu();
      //toolBarButtonNext.setEnabled(false);
      //toolBarButtonPrevious.setEnabled(false);
      //addWindowListener(new WinCloseExit(this));
      //Log.logInfo(this, "Final");
    }

    public JFrame setFrame(JFrame frame)
    {
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.addWindowListener(new FrameCloseExit(frame));
        return frame;
    }
    public JDialog setDialog(JDialog dialog)
    {
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.addWindowListener(new DialogCloseExit(dialog));
        return dialog;
    }

    private void expandingTree()
    {

      //showNodeContent(headOfMain);
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
    }
  public void linkNodeID(String id)
  {
      /*
      int idx = id.indexOf("#");

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

        */
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
              //showNodeContent(selectedNodeOfMain);
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
              //showNodeContent(selectedNodeOfMain);
            }
          catch(NullPointerException ne)
            {
              //JOptionPane.showMessageDialog(this,"There is no Foward Node.","Moving Error!",JOptionPane.ERROR_MESSAGE);
              //currentNodeOfContent = cnode;
              currentNodeOfMain = tnode;
              selectedNodeOfMain = currentNodeOfMain;
              //selectedNodeOfContent = currentNodeOfContent;
              //showNodeContent(selectedNodeOfMain);
            }
        }
        if (e.getSource() == jbStart)
        {
            treeInitialConstruction(jtFileName.getText());
        }

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
          else contC = contC + "`" + resCont;
        }
      return contC;
    }
    private void treeInitialConstruction(String msg)
    {
        //String messageSource = jtFileName.getText();
        try
        {
            //treeHL7V2Main = new HL7V2MessageTree(metadataPath);
            treeHL7V2Main = new HL7V2MessageTree(FileUtil.getV2ResourceMetaDataLoader());
            treeHL7V2Main.setVersion("2.5");
            treeHL7V2Main.parse(msg);
        }
        catch(HL7MessageTreeException he)
        {
            JOptionPane.showMessageDialog(this, he.getMessage(), "V2 Message parsing error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //treeMain = new JTree();
        treeInitialConstruction();
        //treeMain = new JTree(headOfMain);
        //expandingTree();
    }
    private void treeInitialConstruction()
    {
        ElementNode head = treeHL7V2Main.getHeadNode();
        if (head != null)
        {
            headOfMain.removeAllChildren();
            headOfMain.setUserObject(head);
        }
        else return;
        if (head.getLowerLink() == null) return;
        //System.out.println("CCC : " + head);
        //treeRecurrsiveConstruction(headOfMain, head);
        DefaultMutableTreeNode treeNode = headOfMain;
        ElementNode temp = head;
        while(true)
        {
            //System.out.println("DDD : " + temp);
            if (temp.getLowerLink() != null)
            {
                temp = temp.getLowerLink();
                DefaultMutableTreeNode tempTreeNode = new DefaultMutableTreeNode(temp);
                treeNode.add(tempTreeNode);
                tempTreeNode.setParent(treeNode);
                treeNode = tempTreeNode;
                continue;
            }
            if (temp.getRightLink() != null)
            {
                temp = temp.getRightLink();
                DefaultMutableTreeNode tempTreeNode = new DefaultMutableTreeNode(temp);
                DefaultMutableTreeNode tempTreeNodeParent = (DefaultMutableTreeNode)treeNode.getParent();
                tempTreeNodeParent.add(tempTreeNode);
                tempTreeNode.setParent(tempTreeNodeParent);
                treeNode = tempTreeNode;
                continue;
            }

            boolean t = false;
            while(true)
            {
                if ((temp.getUpperLink() == treeHL7V2Main.getHeadNode())||(temp.getUpperLink() == null))
                {
                    t = true;
                    break;
                }
                temp = temp.getUpperLink();
                treeNode = (DefaultMutableTreeNode)treeNode.getParent();

                if (temp.getRightLink() != null)
                {
                    temp = temp.getRightLink();
                    DefaultMutableTreeNode tempTreeNode = new DefaultMutableTreeNode(temp);
                    DefaultMutableTreeNode tempTreeNodeParent = (DefaultMutableTreeNode)treeNode.getParent();
                    tempTreeNodeParent.add(tempTreeNode);
                    tempTreeNode.setParent(tempTreeNodeParent);
                    treeNode = tempTreeNode;
                    break;
                }
            }
            if (t) break;
        }
        if (treeMain != null) treeMain.updateUI();
    }
    /*
    private void treeRecurrsiveConstruction(DefaultMutableTreeNode treeNode, ElementNode v2Node)
    {   try{
        if (v2Node.getLowerLink() == null) return;
        System.out.println("BBB : ");
        //return;
        ElementNode temp = v2Node.getLowerLink();
        while(temp!=null)
        {
            System.out.println("AAA : " + temp.toString());
            DefaultMutableTreeNode tempTreeNode = new DefaultMutableTreeNode(temp);
            treeNode.add(tempTreeNode);
            tempTreeNode.setParent(treeNode);
            if (temp.getLowerLink() != null) treeRecurrsiveConstruction(tempTreeNode, temp);
            temp = v2Node.getRightLink();
        }
    }catch(java.lang.OutOfMemoryError er)
    {
        return;
    }
    }
    */


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
      //contNode.setUserObject(new HelpContentElement("Table of Content", cont));
      HelpContentElement ele = (HelpContentElement) headOfMain.getUserObject();
      cont = "<img src='../images/" + Config.SPLASH_WINDOW_IMAGE_FILENAME + "' width='520' height='390'>";
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




  private String assembleContent(DefaultMutableTreeNode node)
    {
//      HelpContentElement contElement = (HelpContentElement) node.getUserObject();
//      if (node == headOfMain) return contElement.getNodeContent();
//      if (node == findNodeOfMain("0")) return contElement.getNodeContent();
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
    {   /*
//      HelpContentElement contElement = (HelpContentElement) node.getUserObject();
//      String content = (contElement.getNodeContent()).trim();
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
            + "</font>" + content + brTag + "<br>"; */  return "";
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

    class FrameCloseExit extends WindowAdapter
      {
          JFrame tt;

          FrameCloseExit(JFrame st)
              {
                  tt = st;
              }
          public void windowClosing(WindowEvent e)
              {
                  tt.dispose();
              }
         }
    class DialogCloseExit extends WindowAdapter
      {
          JDialog tt;

          DialogCloseExit(JDialog st)
              {
                  tt = st;
              }
          public void windowClosing(WindowEvent e)
              {
                  tt.dispose();
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
        //String dataPath = "C:\\projects\\temp\\v2Meta";
        String dataPath = null;
        try
        {
            JFrame frame = (new V2MessageBrowser(dataPath)).setFrame(new JFrame("HL7 Version2 Message Browser"));
            frame.setSize(800, 600);
            frame.setVisible(true);
        }
        catch(HL7MessageTreeException he)
        {
            System.out.println("Error Message : " + he.getMessage());
        }

    }

}

/**
 * HISTORY      : : V2MessageBrowser.java,v $
 */
