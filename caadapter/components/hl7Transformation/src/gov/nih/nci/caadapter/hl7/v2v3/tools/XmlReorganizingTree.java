package gov.nih.nci.caadapter.hl7.v2v3.tools;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Feb 3, 2009
 * Time: 1:17:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlReorganizingTree
{
    DefaultMutableTreeNode headOfMain;

    public XmlReorganizingTree(String source) throws ApplicationException
    {
        if ((source == null)||(source.trim().equals("")))
            throw new ApplicationException("XML soruce is null.");

        source = source.trim();

        boolean isFileObject = true;

        if (source.length() > 500) isFileObject = false;

        headOfMain = new DefaultMutableTreeNode("Empty") ;
        if (isFileObject)
        {
            File file = new File(source);
            if ((file.exists())&&(file.isFile()))
            {
                treeInitialConstruction(source);
                return;
            }
        }
        String tempFile = "";

        try
        {
            tempFile = FileUtil.saveStringIntoTemporaryFile(source);
        }
        catch(IOException ie)
        {
            throw new ApplicationException(ie.getMessage());
        }

        treeInitialConstruction(tempFile);
    }

    public DefaultMutableTreeNode getHeadNode()
    {
        return headOfMain;
    }

    private void treeInitialConstruction(String path) throws ApplicationException
    {
        InputSource is = null;
        File file = new File(path);
        if (file.exists())
        {
            if (!file.isFile())
            {
                throw new ApplicationException("This is not a file : " + path);//, "Invalid File Object", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                String path1 = "file:///" + path.replace("\\", "/");
                is = new InputSource(path1);
            }
        }
        if (is == null)
        {
            try
            {
                ClassLoaderUtil loaderUtil = new ClassLoaderUtil(path, false);
                is = new InputSource(loaderUtil.getInputStreams().get(0));
            }
            catch(IOException ie)
            {
                System.out.println("XXXXX : " + ie.getMessage());
                return;
            }
            if (is == null) is = new InputSource(path);
        }


        XmlTreeBuildEventHandler handler = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            handler = new XmlTreeBuildEventHandler(headOfMain);

            producer.setContentHandler(handler);


            producer.parse(is);
            //producer.parse(new InputSource(loaderUtil.getInputStreams().get(0)));
            //headOfMain = handler.getHeadNode();//new MetaTreeMetaImpl(handler.getHeadSegment());
        }
        catch(IOException e)
        {
            throw new ApplicationException("XML Parser IOException" + e.getMessage());//, "XML Parser IOException", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e)
        {
            throw new ApplicationException("XML Parser Other Exception"+e.getMessage());//, "XML Parser Other Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reorderingChildNodes(DefaultMutableTreeNode node, List<String> list) throws ApplicationException
    {
        if (node == null) throw new ApplicationException("null node.");
        if ((list == null)||(list.size() == 0)) throw new ApplicationException("empty list");
        if (node.getChildCount() == 0) throw new ApplicationException("no child");


        //DefaultMutableTreeNode[] nodeArray = new DefaultMutableTreeNode[node.getChildCount()];

        List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
        XmlTreeBrowsingNode x1Node = (XmlTreeBrowsingNode) node.getUserObject();
        int n = -1;
        int m = 0;
        //for(String str:list)
        //{
            //if (n < 0)
            //{
        //System.out.println("");
                for(int i=0;i<node.getChildCount();i++)
                {
                    DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
                    XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
                    //System.out.println("FFFFF 16 : " + xNode.getName());
                    for(String str:list)
                    {
                        //System.out.print("  FFF 17 : "  + ", parent: " + x1Node.getName() +" => " + str);

                        if (xNode.getName().equalsIgnoreCase(str))
                        {
                            n = i;
                            //System.out.println(" => found");
                            break;
                        }
                        //else System.out.println(" => no!");
                    }
                    if (n >= 0) break;
                }
                if (n < 0)
                {
                    //continue;
                    //throw new ApplicationException(" any list item cannot be found.. , node=" + x1Node.getName());
                    //System.out.println(" FFFF 15 : any list item cannot be found.. , node=" + x1Node.getName());
                    return;
                    //return;
                }
                //else break;
            //}
        //}
                if ((node.getChildCount() - n) < 2)
                {
                    //System.out.println(" FFFF 14 : Too less items. "+x1Node.getName()+" ("+n+"/"+node.getChildCount()+")");
                    return;
                }
                for(int i=0;i<n;i++) nodeList.add((DefaultMutableTreeNode) node.getChildAt(i));
//                {
//                    DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
//                    XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
//                    if (xNode.getRole().equals(xNode.getRoleKind()[0])) m++;
//                    else
//                    {
//                        System.out.println("FFFFF 12 : " + xNode.getName() + ":" + xNode.getRole());
//                        nodeList.add(aNode);
//                    }
//                }

            for(String str:list)
            {
                for(int i=n;i<node.getChildCount();i++)
                {
                    DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
                    XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
                    //System.out.println("FFFFF 13 : " + xNode.getName() + ":" + xNode.getRole());
                    if (!xNode.getRole().equals(xNode.getRoleKind()[0])) continue;
                    if (xNode.getName().equalsIgnoreCase(str))
                    {
                        //System.out.println("  FFF 14 : " + xNode.getName() + ":" + xNode.getRole());
                        nodeList.add(aNode);
                    }
                }
            }
            //n++;

        if (nodeList.size() != node.getChildCount())
        {
            throw new ApplicationException("Unmatched size ("+nodeList.size()+"/"+node.getChildCount()+") : " + ((XmlTreeBrowsingNode) node.getUserObject()).getName());
            //return;
        }
//        System.out.println("\nFFFFF 15 : " + x1Node.getName());
//        for(String str:list) System.out.println("  FFF 16 : " + str);
//        for(int i=n;i<node.getChildCount();i++)
//        {
//            DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
//            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
//            System.out.println("  --FFF 17 : " + xNode.getName());
//        }
        node.removeAllChildren();
        for (DefaultMutableTreeNode aNode:nodeList) node.add(aNode);

//        for(int i=n;i<node.getChildCount();i++)
//        {
//            DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
//            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
//            System.out.println("  -+FFF 18 : " + xNode.getName());
//        }
    }
    private int getDepth(DefaultMutableTreeNode node)
    {
        if (node == null) return -1;
        DefaultMutableTreeNode hNode = getHeadNode();
        //if (node == hNode) return 0;
        int n = 0;
        while(node != hNode)
        {
            node = (DefaultMutableTreeNode) node.getParent();
            n++;
        }
        return n;
    }
    private List<String> prepareXMLList()
    {
        DefaultMutableTreeNode node = null;
        List<String> pre = new ArrayList<String>();
        //List<String> post = new ArrayList<String>();

        Stack<String> stack = new Stack<String>();
        int depth = 0;
        int beforeDepth = -1;

        while(true)
        {
            if (node == null) node = getHeadNode();
            else
            {
                beforeDepth = depth;
                node = node.getNextNode();
            }

            if (node == null) break;

            depth = getDepth(node);
            if ((beforeDepth >= 0)&&(depth < beforeDepth))
            {
                for (int i=0;i<((beforeDepth-depth));i++)
                {
                    String endTag = "";
                    if (!stack.isEmpty()) endTag = stack.pop();
                    if (endTag.equals("")) continue;
                    pre.add(endTag);
                }
            }

            XmlTreeBrowsingNode xNode0 = (XmlTreeBrowsingNode) node.getUserObject();
            if (!xNode0.getRole().equals(xNode0.getRoleKind()[0])) continue;


            String spaces = "";
            for (int i=0;i<depth;i++) spaces = spaces + "    ";

            String eName = xNode0.getName();
            int idx = eName.indexOf(";");
            if (idx > 0) eName = eName.substring(0, idx);

            String startE = spaces + "<" + eName;
            String endE = spaces + "</" + eName + ">";


            int cntEle = 0;
            String att = "";
            String line = "";

            for(int i=0;i<node.getChildCount();i++)
            {
                DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
                XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
                if (xNode.getRole().equals(xNode.getRoleKind()[0])) cntEle++;
                else if (xNode.getRole().equals(xNode.getRoleKind()[1]))
                {
                    att = att + " " + xNode.getName() + "=\"" + xNode.getValue() + "\"";
                }
                else if (xNode.getRole().equals(xNode.getRoleKind()[2]))
                {
                    line = line + xNode.getValue();
                }
            }

            line = line.trim();
            if (cntEle > 0)
            {
                pre.add(startE + att + ">");
                if (!line.equals("")) pre.add(spaces + "    " + line);
                stack.push(endE);//post.add(endE);
            }
            else
            {
                if (line.equals(""))
                {
                    pre.add(startE + att + "/>");
                }
                else
                {
                    pre.add(startE + att + ">" + line + endE.trim());
                }
                stack.push("");
            }
        }
        while(!stack.isEmpty())
        {
            String str = stack.pop();
            if (!str.equals("")) pre.add(str);
        }
        return pre;
    }
    public void printXML()
    {
        List<String> pre = prepareXMLList();
        if ((pre == null)||(pre.size() == 0)) System.err.println("Failure building xml element list : ");
        for (String string:pre) System.out.println(string);
    }
    public void generatingXMLFile(String fileName) throws IOException
    {
        List<String> pre = prepareXMLList();
        if ((pre == null)||(pre.size() == 0)) throw new IOException("Failure building xml element list for saving (" + fileName + ") : ");
        try
        {
            FileWriter fw = new FileWriter(fileName);
            for (String string:pre) fw.write(string+"\r\n");
            fw.close();
        }
        catch(Exception ie)
        {
            throw new IOException("File Writing Error(" + fileName + ") : " + ie.getMessage());
        }
    }
}
