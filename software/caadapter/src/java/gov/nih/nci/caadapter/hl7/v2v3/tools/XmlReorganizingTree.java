/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

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
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.XMLValidator;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Feb 3, 2009
 * Time: 1:17:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlReorganizingTree
{
    private DefaultMutableTreeNode headOfMain;
    private DefaultMutableTreeNode current = null;
    private String schemaFileName = null;
    private ZipUtil zipUtil = null;

    public XmlReorganizingTree()
    {

    }
    public XmlReorganizingTree(String source) throws ApplicationException
    {
        if ((source == null)||(source.trim().equals("")))
            throw new ApplicationException("XML soruce is null.");

        source = source.trim();

        boolean isFileObject = true;

        if (source.length() > 1000) isFileObject = false;

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
                //System.out.println("XXXXX : " + ie.getMessage());
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
        return prepareXMLList(0);
    }
    /*
     *   int levelOfDatatypeOutputValue is always zero because it's role was alredy done by XMLElement.java.
     */
    private List<String> prepareXMLList(int levelOfDatatypeOutputValue)
    {
        DefaultMutableTreeNode node = null;
        List<String> pre = new ArrayList<String>();
        //List<String> post = new ArrayList<String>();
        pre.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
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

            if (node.getChildCount() == 0)
            {
                XmlTreeBrowsingNode xNode1 = new XmlTreeBrowsingNode(xNode0.getRoleKind()[1], "_dummy__", "_dummy__");
                DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(xNode1);
                node.add(aNode);
            }

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
                    String nm = xNode.getName();
                    String vl = xNode.getValue();

                    boolean checked = true;
                    if ((nm.equalsIgnoreCase("nullFlavor"))&&(vl.equals("NP")))
                    {
                        if (hasNodeValue(node)) checked = false;
                        String replaceNP_value = FileUtil.searchProperty("replaceNullFlavorNP");
                        if (replaceNP_value != null)
                        {
                            replaceNP_value = replaceNP_value.trim();
                            String[] NULL_FLAVOR_VALUES = new String[] {"NI", "OTH", "NINF", "PINF", "UNK", "ASKU", "NAV", "NASK", "TRC", "MSK", "NA"};

                            for(String nullF:NULL_FLAVOR_VALUES)
                            {
                                if (nullF.equals(replaceNP_value)) vl = nullF;
                            }
                        }
                    }
                    if (nm.equalsIgnoreCase("_dummy__")) checked = false;
//                    if (nm.equalsIgnoreCase("xsi:type"))
//                    {
//                        if (levelOfDatatypeOutputValue == 2) checked = false;
//
//                        int idx1 = vl.indexOf(":");
//
//                        if ((levelOfDatatypeOutputValue == 1)&&(idx1 < 0)) checked = false;
//
//                        while(idx1 >= 0)
//                        {
//                            vl = vl.substring(idx1 + 1);
//                            idx1 = vl.indexOf(":");
//                        }
//                    }
                    if (checked)
                        att = att + " " + nm + "=\"" + convertValue(vl) + "\"";
                }
                else if (xNode.getRole().equals(xNode.getRoleKind()[2]))
                {
                    line = line + convertValue(xNode.getValue());
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

    private boolean hasNodeValue(DefaultMutableTreeNode node)
    {
        for(int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) aNode.getUserObject();
            if (xNode.getRole().equals(xNode.getRoleKind()[0]))
            {
                if (hasNodeValue(aNode)) return true;
            }
            else if (xNode.getRole().equals(xNode.getRoleKind()[1]))
            {
                String nm = xNode.getName();

                if ((nm.equalsIgnoreCase("value"))||
                    (nm.equalsIgnoreCase("code"))||
                    (nm.equalsIgnoreCase("inlinetext"))||
                    (nm.equalsIgnoreCase("extension")))
                {
                    return true;
                }
            }
            else if (xNode.getRole().equals(xNode.getRoleKind()[2]))
            {
                return true;
            }
        }
        return false;
    }
    public void printXML()
    {
        printXML(-1);
    }
    public void printXML(int levelOfDatatypeOutputValue)
    {
        int levelOfDatatypeValue = getLevelOfDatatypeOutputValue(levelOfDatatypeOutputValue);
        List<String> pre = prepareXMLList(levelOfDatatypeValue);
        if ((pre == null)||(pre.size() == 0)) System.err.println("Failure building xml element list : ");
        for (String string:pre) System.out.println(string);
    }
    public String printStringXML()
    {
        return printStringXML(-1);
    }
    public String printStringXML(int levelOfDatatypeOutputValue)
    {
        int levelOfDatatypeValue = getLevelOfDatatypeOutputValue(levelOfDatatypeOutputValue);
        List<String> pre = prepareXMLList(levelOfDatatypeValue);
        if ((pre == null)||(pre.size() == 0)) System.err.println("Failure building xml element list : ");

        String res = "";
        for (String string:pre) res = res + string + "\r\n";
        return res;
    }
    public void generatingXMLFile(String fileName) throws IOException
    {
        generatingXMLFile(fileName, -1);
    }
    public void generatingXMLFile(String fileName, int levelOfDatatypeOutputValue) throws IOException
    {
        int levelOfDatatypeValue = getLevelOfDatatypeOutputValue(levelOfDatatypeOutputValue);
        List<String> pre = prepareXMLList(levelOfDatatypeValue);
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

    /*
     *   int levelOfDatatypeOutputValue is always zero because it's role was alredy done by XMLElement.java.
     */
    private int getLevelOfDatatypeOutputValue(int levelOfDatatypeOutputValue)
    {
        return 0;
        /*
        int levelOfDatatypeValue = 0;
        if (levelOfDatatypeOutputValue < 0)
        {
            String levelOfDatatype = FileUtil.searchProperty("levelOfDatatypeOutput");
            if (levelOfDatatype == null) levelOfDatatype = "";

            try
            {
                levelOfDatatypeValue = Integer.parseInt(levelOfDatatype);
            }
            catch(NumberFormatException ne)
            {
                levelOfDatatypeValue = 0;
            }
        }
        else levelOfDatatypeValue = levelOfDatatypeOutputValue;
        return levelOfDatatypeValue;
        */
    }

    public void setSchemaFileName(String fileName) throws ApplicationException
    {
        if (fileName == null) fileName = "";
        else fileName = fileName.trim();
        if (fileName.equals("")) throw new ApplicationException("Schema file name is null or empty.");
        if (!fileName.toLowerCase().endsWith(".xsd")) throw new ApplicationException("This file is not a XML schema file. : " + fileName);
        File file = new File(fileName);
        if ((!file.exists())||(!file.isFile()))
        {
            if ((fileName.toLowerCase().startsWith("jar:file:/"))&&(fileName.indexOf("!/") > 0)) {}
            else throw new ApplicationException("This Schema file is not exist. : " + fileName);
        }

        schemaFileName = fileName;
    }
    public String getSchemaFileName(String fileName)
    {
        return schemaFileName;
    }
    public ValidatorResults validate()
    {
        return validate(true);
    }
    public ValidatorResults validate(boolean reorganize)
    {
        ValidatorResults results = null;

        if (schemaFileName == null) return null;
        String msg = printStringXML();
        if ((msg == null)||(msg.trim().equals(""))) return null;
        String s = schemaFileName;
        if (this.getZipUtil() != null) s = this.getZipUtil().getInitialFile();
        XMLValidator v = new XMLValidator(msg, s, reorganize);

        results = v.validate();

        return results;
    }

    public void setCurrentNode(DefaultMutableTreeNode node)
    {
        current = node;
    }
    public DefaultMutableTreeNode getCurrentNode()
    {
        return current;
    }
    public ZipUtil getZipUtil()
    {
        return zipUtil;
    }
    public void setZipUtil(ZipUtil zUtil)
    {
        zipUtil = zUtil;
    }

    public String getNodeName(DefaultMutableTreeNode node)
    {
        if (node == null) return null;
        if (node.getUserObject() == null) return null;
        XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) node.getUserObject();
        if (xNode == null) return null;
        return xNode.getName();
    }
    public String getNodeValue(DefaultMutableTreeNode node)
    {
        if (node == null) return null;
        if (node.getUserObject() == null) return null;
        XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) node.getUserObject();
        if (xNode == null) return null;
        return xNode.getValue();
    }
    public String getNodeRole(DefaultMutableTreeNode node)
    {
        if (node == null) return null;
        if (node.getUserObject() == null) return null;
        XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) node.getUserObject();
        if (xNode == null) return null;
        return xNode.getRole();
    }

    private String convertValue(String value)
    {
        String STARTING_TAG = "%!";
        String ENDING_TAG = "!%";

        if (value == null) return "";
        //int idx = value.indexOf(STARTING_TAG);
        //if (idx < 0) return value;

        String str = "";
        String val1 = value;
        String val2 = "";
        String val = "";
        while(true)
        {
            int idx = val1.indexOf(STARTING_TAG);

            if (idx > 0)
            {
                str = str + val1.substring(0, idx);
                val2 = val2 + val1.substring(0, idx + STARTING_TAG.length());
                val1 = val1.substring(idx + STARTING_TAG.length());
            }
            else if (idx == 0)
            {
                val2 = val2 + val1.substring(0, STARTING_TAG.length());
                val1 = val1.substring(STARTING_TAG.length());
            }
            else
            {
                str = str + val1;
                break;
            }

            int idx1 = val1.indexOf(ENDING_TAG);

            if (idx1 >= 0)
            {
                val = val1.substring(0, idx1);
                val2 = val2 + val1.substring(0, idx1 + ENDING_TAG.length());
                val1 = val1.substring(idx1 + ENDING_TAG.length());
            }
            else
            {
                val = val1;
                val2 = val2 + val1;
                val1 = "";
            }

            //System.out.println("FFFFF1 val=" + val);
            if ((val.trim().toLowerCase().startsWith("currenttime"))||
                (val.trim().toLowerCase().startsWith("current_time")))
            {
                val = val.trim();
                DateFunction df = new DateFunction();
                String format = df.getDefaultDateFormatString();
                int idx2 = val.indexOf(":");
                if (idx2 > 0) format = val.substring(idx2+1);
                String dt = "";

                try
                {
                    dt = df.getCurrentTime(format);
                }
                catch(FunctionException fe)
                {
                    dt = df.getCurrentTime();
                }
                //System.out.println("FFFFF2 foramt=" + format + ", date=" + dt + ", df.getDefaultDateFormatString()=" + df.getDefaultDateFormatString());
                str = str + dt;
            }
            else if (val.trim().toLowerCase().startsWith("random"))
            {
                int digit = 0;
                String digitO = "";
                int idx2 = val.indexOf(":");
                if (idx2 > 0) digitO = val.substring(idx2+1);
                else digitO = "x";

                try
                {
                    digit = Integer.parseInt(digitO);
                }
                catch(NumberFormatException fe)
                {
                    digit = 5;
                }
                int vl = FileUtil.getRandomNumber(digit);
                //System.out.println("FFFFF2 digitO=" + digitO + ", digit=" + digit + ", value=" + vl);
                str = str + vl;
            }
            else str = str + val2;

            val2 = "";
        }

        return str;
    }
}
