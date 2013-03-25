/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.validation.xml.complement;

//import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.dvts.common.util.SingleFileFilter;
import gov.nih.nci.caadapter.dvts.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.dvts.common.ApplicationException;
import gov.nih.nci.caadapter.dvts.common.tools.XmlTreeBrowsingNode;
import gov.nih.nci.caadapter.dvts.common.tools.XmlTreeBuildEventHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Feb 2, 2009
 * Time: 12:58:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class XSDValidationTreeBrowser extends JPanel implements ActionListener
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: XSDValidationTreeBrowser.java,v $";

    JTree treeMain;
    //HL7V2MessageTree treeHL7V2Main;
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
    JTextArea nodeContent;
    JEditorPane jepHTMLContent;
    //JTabbedPane tabbedPane;

    String commonPath;
    String commonURIPath;
    //String imagePath;
    //String imageURIPath;
    //String stHelpDir = "{Help_Dir}";
    //String stImageDir = "{Image_Dir}";
    //String currentPage;
    //String currentLink;
    //String searchString; // Store searched word in searching function.
    //String searchStr;  // searched word for making bold character display when content showing.
    //boolean searchTag; // When TRUE, searchStr which is a searched word will be displayed in bold font when content displaying.
    //boolean forSearchTag; // set TRUE when a word is searched for. This tag protect from displaying the searched content on the JEditorPane.
    //boolean foundTag; //set TRUE when the searching word is found. The rest cases, set FALSE
    //int pageSetTag; // set 1 during print page set window is activated, set 0 during print dialog window is activated , else -1;
    //boolean protectFrameDispose; // For protecting This Frame disappear when a JOptionPane screen appears

    //JList jlstSearch;
    //JList jlstIndex;
    //DefaultListModel listModelSearch;
    //DefaultListModel listModelIndex;
    //JButton jbWholeSearch;
    //JButton jbKeywordSearch;
    //JTextField jtSearchText;
    //MainFrame mainFrame;
    //int pageSet;
    int stackMAX = 100;
    //int stackCount;
    //int stackPoint = 0;
    //boolean popupTag = false;  // popup menu "Go to Previous item" or "Go to Next item" selected, set TRUE.
    DefaultMutableTreeNode[] beforeContent = new DefaultMutableTreeNode[stackMAX];
    //String FIRST_POPUP_MENU = "Go to Previous item";
    //String SECOND_POPUP_MENU = "Go to Next item";
    //String THIRD_POPUP_MENU = "Print This Content";
    //String FOURTH_POPUP_MENU = "Print Page Setup";
    //String TOOL_BAR_COMMAND_PREVIOUS = "Previous";
    //String TOOL_BAR_COMMAND_NEXT = "Next";
    //String TOOL_BAR_COMMAND_PRINT = "Print";
    //String TOOL_BAR_COMMAND_PAGESETUP = "Page Setup";
    //String INSIDE_NODE_LINK_TAG = "&NODELINK:";
    //JButton toolBarButtonPrevious;
    //JButton toolBarButtonNext;
    //JButton toolBarButtonPrint;
    //JButton toolBarButtonPageSetup;
    //PageFormat pageFormat = null;
    //MouseListener popupListener = null;
    String dirSeparater;
    //JToolBar toolBar;
      //String contentPageTag = "";

    //MetaDataLoader metadataPath = null;
    String messageFileName = "";

      //private boolean taskComplete;

      //private ProgressMonitor progressMonitor;
      //private GeneralTask longTask;

      //private Timer timer;

      public final static int ONE_SECOND = 1000;

  //String dirSeparater;
    public XSDValidationTreeBrowser() //throws HL7MessageTreeException
    {
        //setMetaDataPath(null);
        //metadataPath = FileUtil.getV2ResourceMetaDataLoader();
        try
        {
            constructPanel();
        }
        catch(ApplicationException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR:", JOptionPane.ERROR_MESSAGE);
        }
    }
    /*
    public XmlTreeBrowser(Object datapath) throws HL7MessageTreeException
    {
        //setMetaDataPath(datapath);
        //metadataPath = datapath;
        constructPanel();
    }
    public XmlTreeBrowser(Object datapath, String filename) throws HL7MessageTreeException
    {
        //metadataPath = datapath;
        //setMetaDataPath(datapath);
        //messageFileName = filename;
        constructPanel();
    }
    */
    private void constructPanel() throws ApplicationException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            throw new ApplicationException("(XML Browser) Error while setting system look and feel.");
            //return;
        }


      //stackCount = 0;
      //pageSet = 0;
      selectedNodeOfMain = null;
      //searchStr = "";
      //searchString = "";
      //searchTag = false;
      //forSearchTag = false;
      //foundTag = false;
      //protectFrameDispose = false;
      //pageSetTag = -1;
      //selectedNodeOfContent = null;
      //dirSeparater = File.separator;
      //pageFormat = new PageFormat();
      //Paper paper = new Paper();
      //paper.setSize(Config.DEFAULT_PAPER_SIZE_X_IN_INCH*72, Config.DEFAULT_PAPER_SIZE_Y_IN_INCH*72);
      //paper.setImageableArea((Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH-0.25)*72, Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH*72,
      //            (Config.DEFAULT_PAPER_SIZE_X_IN_INCH - Config.DEFAULT_PAPER_LEFT_MARGIN_IN_INCH - Config.DEFAULT_PAPER_RIGHT_MARGIN_IN_INCH + 0.5)*72,
      //            (Config.DEFAULT_PAPER_SIZE_Y_IN_INCH - Config.DEFAULT_PAPER_TOP_MARGIN_IN_INCH - Config.DEFAULT_PAPER_BOTTOM_MARGIN_IN_INCH)*72);
      //pageFormat.setPaper(paper);

      //commonPath = FileUtil.getWorkingDirPath() + dirSeparater+"etc"+dirSeparater;

      //commonURIPath = "file:///";
      //for(int i=0;i<commonPath.length();i++)
      //  {
      //    String achar = commonPath.substring(i, i+1);
      //    if (achar.equals(dirSeparater)) commonURIPath = commonURIPath + "/";
      //    else commonURIPath = commonURIPath + achar;
      //  }

      //imagePath = FileUtil.getWorkingDirPath() + dirSeparater+"images"+dirSeparater;
      //
      //imageURIPath = "file:///";
      //for(int i=0;i<imagePath.length();i++)
      //  {
      //    String achar = imagePath.substring(i, i+1);
      //    if (achar.equals(dirSeparater)) imageURIPath = imageURIPath + "/";
      //    else imageURIPath = imageURIPath + achar;
      //  }

      //treeInitialConstruction();
      headOfMain = new DefaultMutableTreeNode("Empty") ;
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
        nodeContent = new JTextArea();
        JScrollPane js2 = new JScrollPane(nodeContent);
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
                    displayContentOfSelectedNode(node);

                  //if (!searchTag) showNodeContent(selectedNodeOfMain);
                }
            }
        );

      //linkNodeID("0");

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

    private void displayContentOfSelectedNode(DefaultMutableTreeNode node)
    {
        nodeContent.setText("");
        XmlTreeBrowsingNode cNode = (XmlTreeBrowsingNode) node.getUserObject();
        String role = cNode.getRole();
        String str = "";

        if (node.getParent() != null)
        {
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
            str = str + "*Parent Node : " + xNode.toString() + "\n";
        }

        XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) node.getUserObject();
        str = str + "***This Node : " + xNode.toString() + "\n";

        for(int i=0;i<node.getChildCount();i++)
        {
            XmlTreeBrowsingNode pNode = (XmlTreeBrowsingNode) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
            str = str + "     " + pNode.toString() + "\n";
        }

        nodeContent.setText(str);
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

 // public void linkNodeID(String id)
 // {
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
 // }

  public void actionPerformed(ActionEvent e)
    {
        //System.out.println("XXXXXX0");
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
            //System.out.println("XXXXXX1");
            treeInitialConstruction(jtFileName.getText());
        }

        if (e.getSource() == jbFileSearch)
        {
            File file = findAndSelectFile(this, FileUtil.getUIWorkingDirectoryPath(), "*.*", "Select XML file", false, false);

            if (file == null)
            {
                JOptionPane.showMessageDialog(this, "File object is null", "Invalid File Object", JOptionPane.ERROR_MESSAGE);
                return;
            }
            jtFileName.setText(file.getAbsolutePath());
        }
    }

    private void treeInitialConstruction(String path)
    {
        headOfMain.removeAllChildren();

        XSDValidationTree tree = null;
        try
        {
            tree = new XSDValidationTree(path);
        }
        catch(ApplicationException ae)
        {
            JOptionPane.showMessageDialog(this, ae.getMessage(), "Parsing XSD file error", JOptionPane.ERROR_MESSAGE);
            ae.printStackTrace();
            return;
        }

        DefaultMutableTreeNode head = tree.getHeadNode();
        headOfMain.setUserObject(head.getUserObject());
        //System.out.println("  Child Node Count : " + head.getChildCount());
        java.util.List<DefaultMutableTreeNode> childs = new ArrayList<DefaultMutableTreeNode>();
        for(int i=0;i<head.getChildCount();i++)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) head.getChildAt(i);
            childs.add(node);
        }
        for(DefaultMutableTreeNode node:childs)  headOfMain.add(node);  



        //treeMain = new JTree();
        treeInitialConstruction();
        //treeMain = new JTree(headOfMain);
        //expandingTree();
    }

    private void treeInitialConstruction()
    {
        //ElementNode head = treeHL7V2Main.getHeadNode();
        //if (head != null)
        //{
        //    headOfMain.removeAllChildren();
        //    headOfMain.setUserObject(head);
        //}
        //else return;
        //if (head.getLowerLink() == null) return;
        //System.out.println("CCC : " + head);
        //treeRecurrsiveConstruction(headOfMain, head);
        //DefaultMutableTreeNode treeNode = headOfMain;
        //ElementNode temp = head;
        /*
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
        */
        if (treeMain != null) treeMain.updateUI();
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

    public static void main(String arg[])
    {

            JFrame frame = (new XSDValidationTreeBrowser()).setFrame(new JFrame("XML Browser"));
            frame.setSize(800, 600);
            frame.setVisible(true);


    }

    private File findAndSelectFile(Component parentComponent, String workingDirectoryPath, String fileExtension, String title, boolean saveMode, boolean checkDuplicate)
    {
        File file = null;

        JFileChooser fileChooser = new JFileChooser();
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

        for(FileFilter fileFilter:fileFilters)
        fileChooser.removeChoosableFileFilter(fileFilter);


        return file;
    }

}
