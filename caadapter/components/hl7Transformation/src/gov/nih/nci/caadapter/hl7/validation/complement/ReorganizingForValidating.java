package gov.nih.nci.caadapter.hl7.validation.complement;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlReorganizingTree;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBrowsingNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Feb 3, 2009
 * Time: 8:22:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReorganizingForValidating
{
    private String xmlFileName;
    private String xsdFileName;
    private String tempXSDFileName = null;
    private String messageType;
    private String rootElementName;

    private int ERROR = -1;
    private int ELEMENT = 0;
    private int ATTRIBUTE = 1;
    private int INLINE = 2;
    private int OTHER = 3;

    private int XML = 11;
    private int XSD = 12;

    private XmlReorganizingTree xmlTree = null;
    private XSDValidationTree xsdTree = null;
    private DefaultMutableTreeNode xmlHead = null;
    private DefaultMutableTreeNode xsdHead = null;

    public ReorganizingForValidating(String xmlFile, String xsdFile) throws ApplicationException
    {
        processReorganizing(xmlFile, xsdFile, false);
    }
    public ReorganizingForValidating(String xmlFile, String xsdFile, boolean tempXSDCreating) throws ApplicationException
    {
        processReorganizing(xmlFile, xsdFile, tempXSDCreating);
    }
    public void processReorganizing(String xmlFile, String xsdFile, boolean tempXSDCreating) throws ApplicationException
    {
        checkFile("xml", xmlFile);
        checkFile("xsd", xsdFile);

        xmlTree = new XmlReorganizingTree(xmlFile);
        xmlHead = xmlTree.getHeadNode();

        xsdTree = new XSDValidationTree(xsdFile);
        xsdHead = xsdTree.getHeadNode();

        DefaultMutableTreeNode node = null;
        String complexType = "";
        DefaultMutableTreeNode complexTypeNode = null;

        List<String> nameList = null;
        List<String> typeList = null;

        List<DefaultMutableTreeNode> doneXMLNodeList = new ArrayList<DefaultMutableTreeNode>();
        List<DefaultMutableTreeNode> doneXSDNodeList = new ArrayList<DefaultMutableTreeNode>();

        //int depth = 0;
        //int beforeDepth = 0;
        while(true)
        {
            if (node == null) node = xmlHead;
            else node = node.getNextNode();

            if (node == null) break;
            if (getElementType(node) != ELEMENT) continue;

            String nodeName = getName(node);
            int idx = nodeName.indexOf(";");
            if (idx > 0) nodeName = nodeName.substring(0, idx);
            //System.out.println("Node Name : " + nodeName);
            //depth = node.getDepth();
            if (node == xmlHead)
            {
                complexType = messageType + "." + nodeName;
                complexTypeNode = xsdTree.searchComplexType(complexType);
                if (complexTypeNode == null) throw new ApplicationException("unmatched between xml and xsd 1: " + nodeName + ", complexType=" + complexType);
                rootElementName = nodeName;
                doneXMLNodeList.add(node);
                doneXSDNodeList.add(complexTypeNode);
            }
            else
            {
//                if (depth > beforeDepth)
//                {
//                    int n = -1;
//                    for(int i=0;i<nameList.size();i++)
//                    {
//                        if (nameList.get(i).equalsIgnoreCase(nodeName)) n = i;
//                    }
//                    if (n < 0) new ApplicationException("Invalid child node name 1: " + nodeName);
//                    complexType = typeList.get(n);
//                    if (!xsdTree.isH3SAssociationType(complexType)) continue;
//                    complexTypeNode = xsdTree.searchComplexType(complexType);
//                    if (complexTypeNode == null) new ApplicationException("unmatched between xml and xsd 2: " + nodeName);
//
//                    doneXMLNodeList.add(node);
//                    doneXSDNodeList.add(complexTypeNode);
//                }
//                else
//                {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    int n = -1;
                    for(int i=0;i<doneXMLNodeList.size();i++)
                    {
                        if (parent == doneXMLNodeList.get(i)) n = i;
                    }
                    if (n < 0) throw new ApplicationException("no match pre-access node name 1: " + nodeName + ", parent: " + getName(parent));
//                    {
//                        System.out.println(" ---- no match pre-access node name 1: " + nodeName + ", parent: " + getName(parent));
//                        continue;
//                    }
                    DefaultMutableTreeNode jNode = doneXSDNodeList.get(n);
                    if (!xsdTree.isComplexType(jNode)) continue;
                    //if (!xsdTree.isH3SAssociationType(getName(jNode))) continue;
                    List<String> tempNameList = xsdTree.getH3SSequenceElementsNames(jNode);
                    List<String> tempTypeList = xsdTree.getH3SSequenceElementsTypes(jNode);
                    if ((tempNameList == null)||(tempTypeList == null))
                        //throw new ApplicationException("null item found association number between xml and xsd 1: " + nodeName + ", parent: " + getName(parent) + " (" + tempTypeList +"/"+ tempNameList + ")");
                        continue;
                    if (tempTypeList.size() != tempNameList.size())
                               throw new ApplicationException("unmatched child association number between xml and xsd 1: " + nodeName + ", parent: " + getName(parent) + " (" + tempTypeList.size() +"/"+ tempNameList.size() + ")");

                    n = -1;
                    for(int i=0;i<tempNameList.size();i++)
                    {
                        //System.out.println("FFFFF 7 : "  + nodeName + ", parent: " + getName(parent) +" => " + tempNameList.get(i));
                        if (tempNameList.get(i).equalsIgnoreCase(nodeName)) n = i;
                    }
                    if (n < 0) throw new ApplicationException("Invalid child node name 3: " + nodeName + ", parent: " + getName(parent));

                    complexType = tempTypeList.get(n);

                    complexTypeNode = xsdTree.searchComplexType(complexType);
                    if (complexTypeNode == null) throw new ApplicationException("unmatched between xml and xsd 3: " + nodeName + ", parent: " + getName(parent));

                    doneXMLNodeList.add(node);
                    doneXSDNodeList.add(complexTypeNode);
                    if (!xsdTree.isH3SAssociationType(complexType)) continue;
                    if ((tempTypeList.size() == 0)&&(tempNameList.size() == 0)) continue;
                    if ((tempTypeList.size() == 1)&&(tempNameList.size() == 1)) continue;

//                }
            }



            nameList = xsdTree.getH3SAssociationNames(complexTypeNode);
            typeList = xsdTree.getH3SAssociationTypes(complexTypeNode);

            if ((typeList.size() == 0)&&(nameList.size() == 0)) continue;
            if ((typeList.size() == 1)&&(nameList.size() == 1)) continue;

            if ((nameList == null)||(typeList == null)||(typeList.size()*nameList.size() == 0)||
                (typeList.size() != nameList.size()))
                throw new ApplicationException("unmatched child association number between xml and xsd 2: " + nodeName + ", complexType=" +complexType +", (" + typeList.size() +"/"+ nameList.size()+")");

            //for (String sr:nameList) System.out.println("FFFFF 8 : " + sr);
            //for(int i=0;i<node.getChildCount();i++)  System.out.println("FFFFF 9 : " + getName((DefaultMutableTreeNode)node.getChildAt(i)));


            xmlTree.reorderingChildNodes(node, nameList);
            //for(int i=0;i<node.getChildCount();i++)  System.out.println("FFFFF 10 : " + getName((DefaultMutableTreeNode)node.getChildAt(i)));

            //beforeDepth = depth;
        }

        if (tempXSDCreating)
        {
            File file = new File(xsdFileName);
            String dir= file.getParentFile().getAbsolutePath().trim();
            String fileName = file.getName().trim();
            String tempFileName = dir + File.separator + fileName.substring(0, fileName.length()-4) + "___TEMP" + fileName.substring(fileName.length()-4);
            try
            {
                insertRootElmentToXSDFile(tempFileName, true);
            }
            catch(IOException ie) {}
        }
        //xmlTree.printXML();
    }
    public XmlReorganizingTree getXMLTree()
    {
        return xmlTree;
    }
    public XSDValidationTree getXSDTree()
    {
        return xsdTree;
    }

    private void checkFile(String tag, String fileName) throws ApplicationException
    {
        if ((fileName == null)||(fileName.trim().equals("")))
            throw new ApplicationException(tag + "File name is null.");
        fileName = fileName.trim();

        File file = new File(fileName);

        if (!file.exists()) throw new ApplicationException("This "+tag+" file is not exist. : " + fileName);
        if (!file.isFile()) throw new ApplicationException("This "+tag+" is not a file. : " + fileName);

        if (tag.equals("xml")) xmlFileName = file.getAbsolutePath();
        if (tag.equals("xsd"))
        {
            String msgType = file.getName();

            if (!msgType.toLowerCase().endsWith("."+tag)) throw new ApplicationException("This is not a schema file. : " + fileName);
            messageType = msgType.substring(0, msgType.length()-4);
            xsdFileName = file.getAbsolutePath();
        }
    }

    private String getName(DefaultMutableTreeNode node)
    {
        if (node == null) return null;
        Object obj = node.getUserObject();
        if (obj instanceof XSDValidationTreeNode)
        {
            XSDValidationTreeNode xNode = (XSDValidationTreeNode) obj;
            return xNode.getName();
        }
        else if (obj instanceof XmlTreeBrowsingNode)
        {
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) obj;
            return xNode.getName();
        }
        else return null;
    }
    private String getValue(DefaultMutableTreeNode node)
    {
        if (node == null) return null;
        Object obj = node.getUserObject();
        if (obj instanceof XSDValidationTreeNode)
        {
            XSDValidationTreeNode xNode = (XSDValidationTreeNode) obj;
            return xNode.getValue();
        }
        else if (obj instanceof XmlTreeBrowsingNode)
        {
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) obj;
            return xNode.getValue();
        }
        else return null;
    }
    private int getElementType(DefaultMutableTreeNode node)
    {
        if (node == null) return ERROR;

        if (getNodeType(node) == XSD)
        {
            XSDValidationTreeNode xNode = (XSDValidationTreeNode) node.getUserObject();
            int role = xNode.getRole();
            if (role <= 3) return ELEMENT;
            else if (role == 4) return ATTRIBUTE;
            else if (role == 5) return INLINE;
        }
        else if (getNodeType(node) == XML)
        {
            XmlTreeBrowsingNode xNode = (XmlTreeBrowsingNode) node.getUserObject();
            String role = xNode.getRole();
            if (role.equals("E:")) return ELEMENT;
            else if (role.equals("A:")) return ATTRIBUTE;
            else if (role.equals("L:")) return INLINE;
        }
        return ERROR;
    }
    private int getNodeType(DefaultMutableTreeNode node)
    {
        if (node == null) return ERROR;
        Object obj = node.getUserObject();
        if (obj instanceof XSDValidationTreeNode) return XSD;
        else if (obj instanceof XmlTreeBrowsingNode) return XML;
        return ERROR;
    }

    public boolean insertRootElmentToXSDFile() throws IOException
    {
        return insertRootElmentToXSDFile(xsdFileName, false);
    }

    public boolean insertRootElmentToXSDFile(String outFileName, boolean isTemp) throws IOException
    {
        tempXSDFileName = null;
        List<String> listLine = FileUtil.readFileIntoList(xsdFileName);
        String elementLine = "element";
        String nameAttr = "name=\"" + rootElementName + "\"";
        String typeAttr = "type=\"" + messageType + "." + rootElementName + "\"";
        boolean remarkTag = false;
        boolean includeStartTag = false;
        boolean finishedTag = false;
        boolean insertedTag = false;
        String reserved = "";
        List<String> tempListLine = new ArrayList<String>();

        String beforeStr = null;
            for (String str:listLine)
            {
                if (str == null) str = "";
                //System.out.println(":::" + str);

                if (beforeStr == null) beforeStr = str;
                else
                {
                    tempListLine.add(beforeStr);
                    beforeStr = str;
                }

                if (finishedTag) continue;
                if (str.trim().equals("")) continue;
                int idx = str.indexOf("<!--");
                if (idx >= 0)
                {
                    remarkTag = true;
                    reserved = str.substring(0, idx);
                }
                if (remarkTag)
                {
                    idx = str.indexOf("-->");
                    if (idx >= 0)
                    {
                        remarkTag = false;
                        str = reserved + str.substring(idx + 3);
                        reserved = "";
                    }
                }
                if (str.trim().equals("")) continue;
                if (remarkTag) continue;

                idx = str.toLowerCase().indexOf("include schemalocation=\"");
                String elementHeader = "";
                if (!includeStartTag)
                {
                    if (idx > 0)
                    {
                        includeStartTag = true;
                        elementHeader = str.substring(0, idx);
                        elementLine = elementHeader + elementLine;
                    }
                    continue;
                }

                if (idx > 0) continue;

                String strLower = makeStringLowerAndSimple(str);

                boolean isRootElementLine = false;

                while(true)
                {
                    if (!strLower.startsWith(elementLine.toLowerCase().trim())) break;
                    idx = strLower.indexOf(nameAttr.toLowerCase().trim());
                    if (idx < 0) break;
                    idx = strLower.indexOf(typeAttr.toLowerCase().trim());
                    if (idx < 0) break;
                    if (!strLower.endsWith("/>")) break;
                    isRootElementLine = true;
                    break;
                }

                if (isRootElementLine)
                {
                    return false;
                    //finishedTag = true;
                    //continue;
                }
                tempListLine.add("                         <!-- This root element line was inserted by ReorganizingForValidating.java on "+(new DateFunction()).getCurrentTime()+"  -->");
                tempListLine.add(elementLine + " " + nameAttr + " " + typeAttr + "/>");
                insertedTag = true;
                finishedTag = true;
            }
        if (beforeStr != null) tempListLine.add(beforeStr);

        if (insertedTag)
        {
            try
            {
                FileWriter fw = new FileWriter(outFileName);
                for (String str:tempListLine) fw.write(str+"\r\n");
                fw.close();
            }
            catch(Exception ie)
            {
                throw new IOException("Failure insert Root Elment To " + outFileName + " : " + ie.getMessage());
            }
            File fileOut = new File(outFileName);
            File fileXSD = new File(xsdFileName);
            if (!fileOut.getAbsolutePath().equals(fileXSD.getAbsolutePath()))
            {
                tempXSDFileName = outFileName;
                if (isTemp) fileOut.deleteOnExit();
            }

            return true;
        }
        //System.out.println("Nothing changed, so No xsd file created or modified. : " + outFileName);
        return false;
    }

    private String makeStringLowerAndSimple(String str)
    {
        String strL = "";
        String buf = "";
        buf = str.toLowerCase().trim();

        int before = 32;
        for (char chr:buf.toCharArray())
        {
            int in = (int) chr;
            if (in < 32) continue;
            if (in == 32)
            {
                if (before == 32) {}
                else strL = strL + chr;
            }
            else if (in == 39) strL = strL + "\"";
            else strL = strL + chr;

            before = in;
        }
        return strL;
    }

    public String getActiveXSDFileName()
    {
        if (tempXSDFileName == null) return xsdFileName;
        else return tempXSDFileName;
    }

    public static void main(String[] arg)
    {
        //String xml = "C:\\project\\caAdapter_NCI_CVS\\caadapter\\workingspace\\ddd\\0.xml";
        //String xsd = "C:\\project\\schemas\\multicacheschemas\\PORR_MT049006UV01.xsd";
        String xml = "C:\\project\\caadapter2\\caadapter\\workingspace\\ddd\\res.xml";
        String xsd = "C:\\project\\caadapter\\schemas\\multicacheschemas\\PORR_MT049006UV01.xsd";

        ReorganizingForValidating rfv = null;
        try
        {
            rfv = new ReorganizingForValidating(xml, xsd);
            if (rfv.insertRootElmentToXSDFile("c:\\1.xsd", false))  System.out.println("saved ==> c:\\1.xsd");
            else System.out.println("not created ==> c:\\1.xsd");
        }
        catch(ApplicationException ae)
        {
            System.out.println("ApplicationException : " + ae.getMessage());
            ae.printStackTrace();
        }
        catch(IOException ie)
        {
            System.out.println("IOException : " + ie.getMessage());
            ie.printStackTrace();
        }
    }
}
