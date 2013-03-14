/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.validation.xml.complement;

import gov.nih.nci.caadapter.dvts.common.ApplicationException;

import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.tools.XMLReorganizingTree;
import gov.nih.nci.caadapter.dvts.common.tools.XmlTreeBrowsingNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import org.apache.tools.zip.ZipFile;
//import org.apache.tools.zip.ZipOutputStream;

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

    private XMLReorganizingTree xmlTree = null;
    private XSDValidationTree xsdTree = null;
    private DefaultMutableTreeNode xmlHead = null;
    private DefaultMutableTreeNode xsdHead = null;

    private boolean isZipFileEntry = false;

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
        //System.out.println("WWWWW X1");
        xmlTree = new XMLReorganizingTree(xmlFile);
        xmlHead = xmlTree.getHeadNode();
        //System.out.println("WWWWW X2");
        xsdTree = new XSDValidationTree(xsdFile);
        xsdHead = xsdTree.getHeadNode();

        if (!xsdTree.isH3SAssociationType(xsdFile)) throw new ApplicationException("This xsd File is not an HL7 schema. : " + xsdFile);

        //System.out.println("WWWWW X3");
        DefaultMutableTreeNode node = null;
        String complexType = "";
        DefaultMutableTreeNode complexTypeNode = null;

        List<String> nameList = null;
        List<String> typeList = null;

        //List<DefaultMutableTreeNode> doneXMLNodeList = new ArrayList<DefaultMutableTreeNode>();
        //List<DefaultMutableTreeNode> doneXSDNodeList = new ArrayList<DefaultMutableTreeNode>();

        //int depth = 0;
        //int beforeDepth = 0;
        //System.out.println("WWWWW X4");
        int t = 0;
        while(true)
        {
            t++;

            if (node == null) node = xmlHead;
            else node = node.getNextNode();

            if (node == null) break;
            if (getElementType(node) != ELEMENT) continue;
            //System.out.println("WWWWW X4-1 : " + t + ", elementName=" + this.getName(node));

            String nodeName = getName(node);
            int idx = nodeName.indexOf(";");
            if (idx > 0) nodeName = nodeName.substring(0, idx);
            //System.out.println("Node Name : " + nodeName);
            //depth = node.getDepth();
            if (node == xmlHead)
            {
                if (messageType.equals(nodeName))
                {
                    complexType = messageType;
                    complexTypeNode = xsdTree.searchMessageHeadType(complexType);
                }
                else
                {
                    complexType = messageType + "." + nodeName;
                    complexTypeNode = xsdTree.searchComplexType(complexType);
                }
                if (complexTypeNode == null)
                {
                    //if (nodeName.indexOf("_IN") > 0)
                    //{
                    //    complexType = nodeName + ".MCCI_MT000100UV01.Message";
                    //    complexTypeNode = xsdTree.searchComplexType(complexType);
                    //}
                    throw new ApplicationException("unmatched between xml and xsd 1: " + nodeName + ", complexType=" + complexType);
                }
                rootElementName = nodeName;
                //doneXMLNodeList.add(node);
                //doneXSDNodeList.add(complexTypeNode);
                ((XmlTreeBrowsingNode)node.getUserObject()).setXSDNode(complexTypeNode);
                //System.out.println("Node Name : " + nodeName + ", " + ((XmlTreeBrowsingNode)node.getUserObject()).getXSDNode());
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




                    /*
                    System.out.println("WWWWW X4-1-1 : doneXMLNodeList.size()" + doneXMLNodeList.size());
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
                    */
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    DefaultMutableTreeNode jNode = ((XmlTreeBrowsingNode)parent.getUserObject()).getXSDNode();
                    if (jNode == null) throw new ApplicationException("No match ready-accessed node name 1: " + nodeName + ", parent: " + getName(parent));
                    if (!xsdTree.isComplexType(jNode)) continue;
                    //if (!xsdTree.isH3SAssociationType(getName(jNode))) continue;
                    List<String> tempNameList = xsdTree.getH3SSequenceElementsNames(jNode);
                    List<String> tempTypeList = xsdTree.getH3SSequenceElementsTypes(jNode);
                    if ((tempNameList == null)||(tempTypeList == null))
                        //throw new ApplicationException("null item found association number between xml and xsd 1: " + nodeName + ", parent: " + getName(parent) + " (" + tempTypeList +"/"+ tempNameList + ")");
                        continue;
                    if (tempTypeList.size() != tempNameList.size())
                               throw new ApplicationException("Unmatched number of child associations between name and type of XSD 1: " + nodeName + ", parent: " + getName(parent) + " (" + tempTypeList.size() +"/"+ tempNameList.size() + ")");

                    int n = -1;
                    //System.out.println("WWWWW X4-1-2 : tempNameList.size()" + tempNameList.size());
                    for(int i=0;i<tempNameList.size();i++)
                    {
                        //System.out.println("FFFFF 7 : "  + nodeName + ", parent: " + getName(parent) +" => " + tempNameList.get(i));
                        String xsdNodeName = tempNameList.get(i);
                        if (xsdNodeName.equalsIgnoreCase(nodeName))
                        {
                            n = i;
                            if (!xsdNodeName.equals(nodeName))
                            {
                                ((XmlTreeBrowsingNode)node.getUserObject()).setName(xsdNodeName);
                            }
                        }
                    }
                    if (n < 0) throw new ApplicationException("Invalid child node name 3: " + nodeName + ", parent: " + getName(parent));

                    complexType = tempTypeList.get(n);

                    //complexTypeNode = xsdTree.searchComplexType(jNode, complexType);
                    //System.out.print("WWWW jNode=" + xsdTree.getComplexTypeName(jNode) + ", " + complexType);
                    //if (complexTypeNode == null)
                    //{
                        DefaultMutableTreeNode snode = xsdTree.searchTypeHeadPointer(complexType);
                        complexTypeNode = xsdTree.searchComplexType(snode, complexType);
                        //String sss = "";
                        //if (snode == null) sss = " ******";
                        //System.out.println(", complexTypeNode(2nd)=" + xsdTree.getComplexTypeName(complexTypeNode) + sss);
                        if (complexTypeNode == null)
                        {
                            complexTypeNode = xsdTree.searchComplexType(complexType);
                            if (complexTypeNode == null) throw new ApplicationException("unmatched between xml and xsd 3: " + nodeName + ", parent: " + getName(parent) + ", complexType=" + complexType);
                        }
                    //}
                    //else System.out.println(", complexTypeNode(1st)" + xsdTree.getComplexTypeName(complexTypeNode));
                    ((XmlTreeBrowsingNode)node.getUserObject()).setXSDNode(complexTypeNode);
                    //doneXMLNodeList.add(node);
                    //doneXSDNodeList.add(complexTypeNode);
                    if (!xsdTree.isH3SAssociationType(complexType)) continue;
                    if ((tempTypeList.size() == 0)&&(tempNameList.size() == 0)) continue;
                    if ((tempTypeList.size() == 1)&&(tempNameList.size() == 1)) continue;

//                }
            }
            //System.out.println("WWWWW X4-2 : ");


            nameList = xsdTree.getH3SAssociationNames(complexTypeNode);
            typeList = xsdTree.getH3SAssociationTypes(complexTypeNode);

            if ((typeList.size() == 0)&&(nameList.size() == 0)) continue;
            if ((typeList.size() == 1)&&(nameList.size() == 1)) continue;

            if ((nameList == null)||(typeList == null)||(typeList.size()*nameList.size() == 0)||
                (typeList.size() != nameList.size()))
                throw new ApplicationException("unmatched child association number between xml and xsd 2: " + nodeName + ", complexType=" +complexType +", (" + typeList.size() +"/"+ nameList.size()+")");

            //for (String sr:nameList) System.out.println("FFFFF 8 : " + sr);
            //for(int i=0;i<node.getChildCount();i++)  System.out.println("FFFFF 9 : " + getName((DefaultMutableTreeNode)node.getChildAt(i)));

            //System.out.println("WWWWW X4-3 : ");
            xmlTree.reorderingChildNodes(node, nameList);
            //System.out.println("WWWWW X4-4 : ");
            //for(int i=0;i<node.getChildCount();i++)  System.out.println("FFFFF 10 : " + getName((DefaultMutableTreeNode)node.getChildAt(i)));

            //beforeDepth = depth;
        }
        //System.out.println("WWWWW X5");
        if (tempXSDCreating)
        {
            try
            {
                if (!insertRootElmentToXSDFile(null, true)) System.out.println("WWWWW Inserting Element : false");
            }
            catch(IOException ie)
            {
                System.out.println("WWWWW Inserting Element IOException : " + ie.getMessage());
            }
        }
        //System.out.println("WWWWW X6");
        //xmlTree.printXML();
    }
    public XMLReorganizingTree getXMLTree()
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
            throw new ApplicationException(tag + " File name is null.");
        fileName = fileName.trim();

        File file = new File(fileName);

        if (tag.equals("xml"))
        {
            if (!file.exists()) throw new ApplicationException("This "+tag+" file is not exist. : " + fileName);
            if (!file.isFile()) throw new ApplicationException("This "+tag+" is not a file. : " + fileName);
            xmlFileName = file.getAbsolutePath();
        }
        if (tag.equals("xsd"))
        {
            if ((file.exists())&&(file.isFile()))
            {
                String xsdFile = file.getAbsolutePath();

                if ((!File.separator.equals("/"))&&(xsdFile.indexOf(File.separator) >= 0))
                {
                    xsdFile = xsdFile.replace(File.separator, "/");
                    //System.out.println("WWWW 91 : " + xsdFile);
                }
                fileName = "file:///" + xsdFile;
            }

            String msgType = "";
            for (int i=fileName.length();i>0;i--)
            {
                String achar = fileName.substring(i-1, i);
                if (achar.equals("/")) break;
                msgType = achar + msgType;
            }

            if (!msgType.toLowerCase().endsWith("."+tag)) throw new ApplicationException("This is not a schema file. : " + fileName);
            messageType = msgType.substring(0, msgType.length()-4);
            xsdFileName = fileName;
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
        List<String> listLine = null;

        if (outFileName == null) outFileName = "";
        else outFileName = outFileName.trim();

        String xsdFile = xsdFileName;

        if (xsdTree.getTypeName(xsdFile).indexOf("_IN") > 0) return false;
        boolean isLocalFile = false;
        while(true)
        {
            File file = new File(xsdFile);

            if ((file.exists())&&(file.isFile()))
            {
                String dir= file.getParentFile().getAbsolutePath().trim();
                String fileName = file.getName().trim();
                if (outFileName.equals(""))
                {
                    while(true)
                    {
                        outFileName = dir + File.separator + fileName.substring(0, fileName.length()-4) + "_TEMP" + FileUtil.getRandomNumber(5) + fileName.substring(fileName.length()-4);
                        File file2 = new File(outFileName);
                        if ((!file2.exists())||(!file2.isFile())) break;
                    }
                }
                xsdFile = file.getAbsolutePath();
                listLine = FileUtil.readFileIntoList(xsdFile);
                isLocalFile = true;
                break;
            }
            else if (xsdFile.toLowerCase().startsWith("file:/"))
            {
                xsdFile = xsdFile.substring(("file:/").length());
                while(true)
                {
                    String achar = xsdFile.substring(0, 1);
                    if (achar.equals("/")) xsdFile = xsdFile.substring(1);
                    else break;
                }
                if (!File.separator.equals("/")) xsdFile = xsdFile.replace("/", File.separator);
            }
            else
            {
                xsdFile = null;
                break;
            }
        }

        String zipFileName = null;
        //String entryName = null;
        if (xsdFile == null)
        {
            xsdFile = xsdFileName;
            if (xsdFile.toLowerCase().startsWith("jar:file:/"))
            {
                xsdFile = xsdFile.substring(("jar:file:/").length());

                while(true)
                {
                    String achar = xsdFile.substring(0, 1);
                    if (achar.equals("/")) xsdFile = xsdFile.substring(1);
                    else break;
                }
            }
            else throw new IOException("This schema file cannot be transformed. : " + xsdFile);



            int idx = xsdFile.indexOf("!");
            if (idx < 0) throw new IOException("Invalid zip file URL : " + xsdFile);
            zipFileName = xsdFile.substring(0, idx);
            //entryName = xsdFile.substring(idx+1);
            if (!zipFileName.toLowerCase().endsWith(".zip")) throw new IOException("This is not a zip file. : " + zipFileName);
            if (!File.separator.equals("/")) zipFileName = zipFileName.replace("/", File.separator);

            File file = new File(zipFileName);

            if ((!file.exists())||(!file.isFile())) throw new IOException("This zip file is not exist : " + zipFileName);


            String tempF = FileUtil.downloadFromURLtoTempFile(xsdFileName);
            listLine = FileUtil.readFileIntoList(tempF);
        }

        String elementLine = "element";
        String nameAttr = "name=\"" + rootElementName + "\"";
        String typeAttr = "type=\"" + messageType + "." + rootElementName + "\"";
        boolean remarkTag = false;
        boolean includeStartTag = false;
        boolean finishedTag = false;
        boolean insertedTag = false;
        String reserved = "";
        List<String> tempListLine = new ArrayList<String>();

        String searchIncludeKey = "include schemalocation=\"";
        String beforeStr = null;
            for (String str:listLine)
            {
                if (str == null) str = "";
                //System.out.println(":::" + str);

                if (beforeStr == null) beforeStr = str;
                else
                {
                    int index = beforeStr.toLowerCase().indexOf(searchIncludeKey);
                    while((!isLocalFile)&&(index > 0))
                    {
                        String beforeStr2 = beforeStr;
                        String line = "";
                        line = line + beforeStr2.substring(0, index + (searchIncludeKey).length());
                        beforeStr2 = beforeStr2.substring(index + (searchIncludeKey).length());
                        index = beforeStr2.indexOf("\"");
                        String fileL = beforeStr2.substring(0, index);
                        String tail = beforeStr2.substring(index);
                        if (index < 0) break;
                        List<String> list = xsdTree.getFinishedSchemaList();
                        String url = null;
                        for (String sst:list)
                        {
                            if (xsdTree.getTypeName(sst).equals(xsdTree.getTypeName(fileL))) url = sst;
                        }
                        if (url == null) break;
                        beforeStr = line + url + tail;

                        break;
                    }
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

                idx = str.toLowerCase().indexOf(searchIncludeKey);
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

        if (!insertedTag) return false;
        if ((tempListLine == null)||(tempListLine.size() == 0)) throw new IOException("inserted string list creation failure.");

        if (!isLocalFile) outFileName = FileUtil.getTemporaryFileName(".xsd");

            try
            {
                //System.out.println("WWWW outFileName=" + outFileName);
                FileWriter fw = new FileWriter(outFileName);
                for (String str:tempListLine)
                {
                    //System.out.println(str);
                    fw.write(str+"\r\n");
                }
                fw.close();
            }
            catch(Exception ie)
            {
                throw new IOException("Failure insert Root Elment To " + outFileName + " : " + ie.getMessage());
            }

                tempXSDFileName = outFileName;
                if (isTemp) (new File(outFileName)).deleteOnExit();


            return true;





        /*
        if(zipFileName == null) throw new IOException("Zip File name is null. : " + xsdFile);

        ZipFile zipFile = new ZipFile(zipFileName);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        OutputStream outputStream = new FileOutputStream(zipFileName);
        ZipOutputStream zipOut = new ZipOutputStream(outputStream);

        OutputStreamWriter writer=new OutputStreamWriter(zipOut);
        String entryName2 = null;
        while(true)
        {
            entryName2 = entryName.substring(0, entryName.length()-4) + "_TEMP" + FileUtil.getRandomNumber(5) + entryName.substring(entryName.length()-4);
            if (zipFile.getEntry(entryName2) == null) break;
        }

        try
        {

            while(entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                System.out.println("WWWWW Entry : " + entry.getName());
                zipOut.putNextEntry(entry);
                zipOut.closeEntry();
            }
            zipOut.putNextEntry(new ZipEntry(entryName2));
            for (String str:tempListLine) writer.write(str+"\r\n");
            zipOut.closeEntry();
            writer.flush();
            zipOut.finish();
        }
        catch(Exception ie)
        {
            throw new IOException("Failure insert Root Elment To zip file " + outFileName + " : " + ie.getMessage());
        }

        tempXSDFileName = xsdFileName.substring(0, xsdFileName.indexOf("!") + 1) + entryName2;
        isZipFileEntry = true;
        return true;
        */
    }

    public boolean isZipFileEntry()
    {
        return isZipFileEntry;
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
    //
    //public ValidatorResults validate()
    //{
    //
    //}

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
