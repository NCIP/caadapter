package gov.nih.nci.caadapter.hl7.validation.complement;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBuildEventHandler;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jan 30, 2009
 * Time: 1:26:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class XSDValidationTree
{
    private DefaultMutableTreeNode headNode = null;
    private File mainXSDFile = null;
    private List<String> includeFileList = new ArrayList<String>();
    private List<String> finished = new ArrayList<String>();


    public XSDValidationTree(String xsdFile) throws ApplicationException
    {
        if ((xsdFile == null)||(xsdFile.trim().equals("")))
            throw new ApplicationException("Source schema file is null.");
        xsdFile = xsdFile.trim();
        File file0 = new File(xsdFile);
        if ((!file0.exists())||(!file0.isFile()))
        {
            throw new ApplicationException("Source schema file is neither exist nor a file. (main) : " + xsdFile);
        }
        mainXSDFile = file0;

        String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
        //String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
        if (!grandParentDir.endsWith(File.separator)) grandParentDir = grandParentDir + File.separator;

        String coreDir = grandParentDir + "coreschemas";
        File file = new File(coreDir);
        if ((!file.exists())||(!file.isDirectory()))
           throw new ApplicationException("This is neither exist nor a directory. (coreschemas) : " + xsdFile);

        for(File core:file.listFiles())
        {
            if (!core.isFile()) continue;
            String path = core.getAbsolutePath().trim();
            if (!path.toLowerCase().endsWith(".xsd")) continue;
            includeFileList.add(path);
        }

        parseXSDFile(xsdFile, true);

        while(true)
        {
            boolean cTag = false;
            String str = null;
            for(int i=0;i<includeFileList.size();i++)
            {
                str = includeFileList.get(i);
                boolean cTag2 = false;
                for(String str2:finished)
                {
                    if (str.equals(str2)) cTag2 = true;
                }
                if (cTag2) continue;
                break;
            }
            parseXSDFile(str, false);
            finished.add(str);
            if (finished.size() == includeFileList.size()) break;
        }

    }

    public void insertIncludeFileList(String xsdFile) throws ApplicationException
    {
        if ((xsdFile == null)||(xsdFile.trim().equals("")))
            throw new ApplicationException("Source schema file is null.");
        xsdFile = xsdFile.trim();
        File file = new File(xsdFile);
        if ((!file.exists())||(!file.isFile()))
        {
            String parentDir = mainXSDFile.getParent().trim();
            if (!parentDir.endsWith(File.separator)) parentDir = parentDir + File.separator;

            xsdFile = parentDir + xsdFile;
            file = new File(xsdFile);
            if ((!file.exists())||(!file.isFile()))
            {
                String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
                if (!grandParentDir.endsWith(File.separator)) grandParentDir = grandParentDir + File.separator;

                xsdFile = grandParentDir + "coreschemas" + File.separator + file.getName();
                file = new File(xsdFile);
                if ((!file.exists())||(!file.isFile()))
                   throw new ApplicationException("This xsd file is neither exist nor a file. (include) : " + xsdFile);
            }
        }

        if (!xsdFile.toLowerCase().endsWith(".xsd"))
            throw new ApplicationException("This is not a schema file. : " + xsdFile);

        if (file.getAbsolutePath().trim().toLowerCase().indexOf("coreschemas") > 0) return;

        boolean cTag = false;
        for(String str:includeFileList)
        {
            if (str.equals(xsdFile)) cTag = true;
        }
        if (!cTag)
        {
            includeFileList.add(xsdFile);
            //System.out.println("Inserted Include xsd file : " + xsdFile);
        }
    }
    private void parseXSDFile(String xsdFile, boolean isMain) throws ApplicationException
    {

        if ((xsdFile == null)||(xsdFile.trim().equals("")))
            throw new ApplicationException("Source schema file is null.");
        xsdFile = xsdFile.trim();
        File file = new File(xsdFile);
        if ((!file.exists())||(!file.isFile()))
        {
            if (isMain) throw new ApplicationException("This file is not exist or a file. (main) : " + xsdFile);

//            String parentDir = mainXSDFile.getParent().trim();
//            if (!parentDir.endsWith(File.separator)) parentDir = parentDir + File.separator;
//
//            xsdFile = parentDir + xsdFile;
//            file = new File(xsdFile);
//            if ((!file.exists())||(!file.isFile()))
//            {
//                String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
//                if (!grandParentDir.endsWith(File.separator)) grandParentDir = grandParentDir + File.separator;
//
//                xsdFile = grandParentDir + "coreschemas" + File.separator + file.getName();
//                file = new File(xsdFile);
//                if ((!file.exists())||(!file.isFile()))
//                   throw new ApplicationException("This file is not exist or a file. (include) : " + xsdFile);
//            }
        }

        if (!xsdFile.toLowerCase().endsWith(".xsd"))
            throw new ApplicationException("This is not a schema file. : " + xsdFile);

        if (isMain)
        {
            mainXSDFile = file;
            //System.out.println("Main start : " + xsdFile);
        }
        //else System.out.println("Include start : " + xsdFile);

        if (headNode == null)
        {
            XSDValidationTreeNode headXNode = new XSDValidationTreeNode(0, "Head", "");
            headNode = new DefaultMutableTreeNode(headXNode);
        }

        if (!isMain)
        {
            if (isIncludedFile(xsdFile)) return;
        }

        XSDValidationEventHandler handler = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            handler = new XSDValidationEventHandler(this);

            producer.setContentHandler(handler);

            String path1 = "file:///" + xsdFile.replace(File.separator, "/");
            InputSource is = new InputSource(path1);

            producer.parse(is);
        }
        catch(IOException e)
        {
            throw new ApplicationException("XML Parser IOException : " +  e.getMessage());
        }
        catch(Exception e)
        {
            throw new ApplicationException("XML Parser Other Exception" +  e.getMessage());
        }

        if (handler.getErrMessage() != null)
        {
            throw new ApplicationException(handler.getErrMessage());
        }

        if (isMain)
        {
            DefaultMutableTreeNode node = handler.getHeadNode();
            XSDValidationTreeNode headMainXNode = getXnodeFromDnode(node);
            headMainXNode.setRole(1);
            headMainXNode.setXSDFileName(xsdFile);
            node.setUserObject(headMainXNode);
            headNode.add(node);
            //System.out.println("Added Main ("+headNode.getChildCount()+") : " + ((XSDValidationTreeNode)node.getUserObject()).toString());
        }
        else
        {
            DefaultMutableTreeNode node = handler.getHeadNode();
            XSDValidationTreeNode headIncludeXNode = getXnodeFromDnode(node);
            headIncludeXNode.setRole(2);
            headIncludeXNode.setXSDFileName(xsdFile);
            node.setUserObject(headIncludeXNode);
            headNode.add(node);
            //System.out.println("Added Include ("+headNode.getChildCount()+") : " + ((XSDValidationTreeNode)node.getUserObject()).toString());
        }
    }

    public DefaultMutableTreeNode getHeadNode()
    {
        return headNode;
    }
    public File getMainXSDFile()
    {
        return mainXSDFile;
    }
    public boolean isIncludedFile(String fileName)
    {
        int childCount = headNode.getChildCount();
        if (childCount == 0) return false;
        for (int i=0;i<childCount;i++)
        {
            XSDValidationTreeNode tempX = getXnodeFromDnodeChild(headNode, i);
            if (tempX == null) continue;
            if (fileName.indexOf(tempX.getXSDMessageType()) >= 0) return true;
        }
        return false;
    }
    private XSDValidationTreeNode getXnodeFromDnode(DefaultMutableTreeNode dNode)
    {
        XSDValidationTreeNode tempX = null;
        try
        {
            //DefaultMutableTreeNode tempD = (DefaultMutableTreeNode) dNode.getChildAt(index);
            tempX = (XSDValidationTreeNode) dNode.getUserObject();
        }
        catch(Exception ex)
        {
            return null;
        }
        return tempX;
    }
    private XSDValidationTreeNode getXnodeFromDnodeChild(DefaultMutableTreeNode dNode, int index)
    {
        XSDValidationTreeNode tempX = null;
        try
        {
            //DefaultMutableTreeNode tempD = (DefaultMutableTreeNode) dNode.getChildAt(index);
            tempX = getXnodeFromDnode((DefaultMutableTreeNode) dNode.getChildAt(index));
        }
        catch(Exception ex)
        {
            return null;
        }
        return tempX;

    }

    public DefaultMutableTreeNode searchComplexType(String nodeName)
    {
        if (nodeName == null) return null;
        nodeName = nodeName.trim();
        if (nodeName.equals("")) return null;

        DefaultMutableTreeNode sNode = getHeadNode();

        while(true)
        {
            sNode = sNode.getNextNode();
            if (sNode == null) break;

            String complexTypeName = getComplexTypeName(sNode);

            if (complexTypeName == null) continue;

            if (complexTypeName.equals(nodeName)) return sNode;
        }

        return null;
    }

    public boolean isComplexType(DefaultMutableTreeNode node)
    {
        if (node == null) return false;
        XSDValidationTreeNode xNode = getXnodeFromDnode(node);
        String eleName = xNode.getName();
        int role = xNode.getRole();
        if (role != 3) return false;
        if (eleName.toLowerCase().indexOf("complextype") < 0) return false;
        return true;
    }

    public String getComplexTypeName(DefaultMutableTreeNode node)
    {
        //System.out.println("FFFFF1 : ");
        if (node == null) return null;
        if (!isComplexType(node)) return null;

        return getAttributeValueWithName(node);
    }
    public String getAttributeValueWithName(DefaultMutableTreeNode node)
    {
        return getAttributeValue(node, "name");
    }
    public String getAttributeValueWithType(DefaultMutableTreeNode node)
    {
        return getAttributeValue(node, "type");
    }
    public String getAttributeValue(DefaultMutableTreeNode node, String attName)
    {
        if (node == null) return null;

        for (int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) node.getChildAt(i);

            XSDValidationTreeNode cxNode = getXnodeFromDnode(cNode);
            String eleNameC = cxNode.getName();
            int roleC = cxNode.getRole();
            //System.out.println("FFFFF2 : " + attName + ", En:"+eleNameC +", R:"+ roleC + ", V:" + cxNode.getValue());
            if (roleC != 4) continue;
            if (!eleNameC.equals(attName)) continue;
            //System.out.println("FFFFF3 : " + attName + ", En:"+eleNameC +", R:"+ roleC + ", V:" + cxNode.getValue());

            return cxNode.getValue();
        }
        //System.out.println("FFFFF4 : Not Found : " + attName);
        return null;
    }
    public DefaultMutableTreeNode getSequenceElement(DefaultMutableTreeNode node)
    {
        if (!isComplexType(node)) return null;
        List<DefaultMutableTreeNode> list = getChildElementsWithName(node, "sequence");
        if (list == null) return null;
        return list.get(0);
    }
    public List<DefaultMutableTreeNode> getChildElementsWithName(DefaultMutableTreeNode node, String name)
    {
        if (node == null) return null;
        if (name == null) return null;
        name = name.trim();
        if (name.equals("")) return null;

        List<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
        for (int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) node.getChildAt(i);

            XSDValidationTreeNode cxNode = getXnodeFromDnode(cNode);
            String eleNameC = cxNode.getName();
            int roleC = cxNode.getRole();
            if (roleC != 3) continue;
            if (eleNameC.equalsIgnoreCase(name))
            {
                list.add(cNode);
                continue;
            }
            int idx = eleNameC.indexOf(":");
            if (idx > 0)
            {
                String nodeName = eleNameC.substring(idx + 1);
                if (nodeName.equalsIgnoreCase(name)) list.add(cNode);
            }

            int idx2 = eleNameC.toLowerCase().indexOf("choice");
            if ((name.equalsIgnoreCase("element"))&&(idx2 >= 0))
            {
                List<DefaultMutableTreeNode> li = getChildElementsWithName(cNode, name);
                for (DefaultMutableTreeNode nd:li) list.add(nd);
            }

        }
        if (list.size() == 0) return null;
        return list;
    }

    public boolean isH3SAssociationType(String name)
    {
        if (name == null) return false;
        name = name.trim();
        if (name.equals("")) return false;

        int idx = name.indexOf(".");
        if (idx < 0) return false;

        String str = name.substring(0, idx);
        int len = str.length();
        if ((len < 7)||(len > 17)) return false;

        char[] chrs = str.toCharArray();
        int i = 0;

        for(char chr:chrs)
        {
            int n = (int) chr;
            boolean isNumeric = false;
            int cx = 0;
            //System.out.print("FFFFF 4 ("+i+"): " + chr + "," + n);
            if ((n >= 48)&&(n <= 57)) isNumeric = true;
            else if (((n >= 65)&&(n <= 90))||(n==95)) {}
            else cx = 1;

            if ((i==0)||(i==1)||(i==2)||(i==3)||(i==5)||(i==6))
            {
                if (isNumeric) cx = 2;
            }
            else if ((i==7)||(i==8)||(i==9)||(i==10)||(i==11)||(i==12)||(i==15)||(i==16))
            {
                if (!isNumeric) cx = 3;
            }
            else if (i==4)
            {
                if (n != 95) cx = 4;
            }
            else if (i==13)
            {
                if (n != 85) cx = 5;
            }
            else if (i==14)
            {
                if (n != 86) cx = 6;
            }
            if(cx == 0) {}// System.out.println("");
            else
            {
                //System.out.println(", checked=" + cx);
                return false;
            }

            i++;
        }

        return true;
    }

    public List<String> getH3SAssociationNames(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "association", true);
    }
    public List<String> getH3SAssociationTypes(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "association", false);
    }
    public List<String> getH3SAttributeNames(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "attribute", true);
    }
    public List<String> getH3SAttrinbuteTypes(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "attribute", false);
    }
    public List<String> getH3SSequenceElementsNames(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "all", true);
    }
    public List<String> getH3SSequenceElementsTypes(DefaultMutableTreeNode node)
    {
        return getH3SSequenceTypes(node, "all", false);
    }

    private List<String> getH3SSequenceTypes(DefaultMutableTreeNode node, String classified, boolean isName)
    {
        List<DefaultMutableTreeNode> nList = getChildElementsWithName(getSequenceElement(node), "element");
        if (nList == null) return null;
        List<String> sList = new ArrayList<String>();
        for (DefaultMutableTreeNode aNode:nList)
        {
            String type = getAttributeValueWithType(aNode);
            String data = null;

            if (isName) data = getAttributeValueWithName(aNode);
            else data = type;

            //System.out.println("FFFFF 5: item read("+isName+") : " + classified + ", " + data);

            if (data == null) continue;
            data = data.trim();
            if (data.equals("")) continue;
            int sz = sList.size();
            if (classified.equals("association"))
            {
                if (isH3SAssociationType(type)) sList.add(data);
            }
            else if (classified.equals("attribute"))
            {
                if (!isH3SAssociationType(type)) sList.add(data);
            }
            else if (classified.equals("all")) sList.add(data);

            //if (sList.size() > sz) System.out.println("FFFFF 6: item added("+isName+") : " + classified + ", " + data);
        }

        return sList;
    }
}
