/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.validation.complement;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBuildEventHandler;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
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
    private String mainXSDFile = null;
    private List<String> includeFileList = new ArrayList<String>();
    private List<String> finished = new ArrayList<String>();

    private List<String> typeList = new ArrayList<String>();
    private List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
    //private boolean isHL7TypeOnly = false;

    public XSDValidationTree(String xsdFile) throws ApplicationException
    {
        doMainProcess(xsdFile, false);
    }
    public XSDValidationTree(String xsdFile, boolean forOneFile) throws ApplicationException
    {
        doMainProcess(xsdFile, forOneFile);
    }
    private void doMainProcess(String xsdFile, boolean forOneFile) throws ApplicationException
    {

        if ((xsdFile == null)||(xsdFile.trim().equals("")))
            throw new ApplicationException("Source schema file is null.");
        xsdFile = xsdFile.trim();

        String fileName = "";
        File file = new File(xsdFile);
        if ((file.exists())&&(file.isFile()))
        {
            xsdFile = file.getAbsolutePath();

            if ((!File.separator.equals("/"))&&(xsdFile.indexOf(File.separator) >= 0))
            {
                xsdFile = xsdFile.replace(File.separator, "/");
                //System.out.println("WWWW 91 : " + xsdFile);
            }
            if (xsdFile.toLowerCase().startsWith("file:/")) fileName = xsdFile;
            else fileName = "file:///" + xsdFile;
        }
        else fileName = xsdFile;

        String baseUri = null;

            int idx = fileName.indexOf(Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME);
            //if (idx < 0) throw new ApplicationException("This file is not an HL7 schema. : " + xsdFile + ", " + fileName + ", " + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME);
            baseUri = fileName.substring(0, idx);


        parseXSDFile(fileName, true);


        if (forOneFile) return;

        while(true)
        {
            String str = null;
            String parentXSD = null;
            for(int i=0;i<includeFileList.size();i++)
            {
                String str1 = includeFileList.get(i);
                int index = str1.indexOf("|");
                if (index > 0)
                {
                    parentXSD = str1.substring(0, index);
                    str1 = str1.substring(index+1);
                }
                boolean cTag2 = false;
                for(String str2:finished)
                {
                    if (getTypeName(str1).equals(getTypeName(str2)))
                    {
                        cTag2 = true;
                    }
                }
                if (!cTag2)
                {
                    str = str1;
                    break;
                }
            }
            String str3 = assembleURI(baseUri, str, parentXSD);
            //System.out.println("WWWWW FFFF : " + str3 + ", " + baseUri + ", " + str + ", " + parentXSD);
            if (str3 == null) break;

            parseXSDFile(str3, false);
            //System.out.println("WWWW parsed : " + str3);
            finished.add(str3);
            if (finished.size() == includeFileList.size()) break;
        }
    }
    public List<String> getFinishedSchemaList()
    {
        return finished;
    }
    public String getTypeName(String str)
    {
        String msgType = "";
        for (int i=str.length();i>0;i--)
        {
            String achar = str.substring(i-1, i);
            if (achar.equals("/")) break;
            if (achar.equals(File.separator)) break;
            msgType = achar + msgType;
        }
        return msgType;
    }

    private String assembleURI(String baseUri, String xsdFile, String parentXSD)
    {

        String finalPath = null;
        boolean isLocalFile = false;

        if (xsdFile == null) return null;
        xsdFile = xsdFile.trim();
        if (xsdFile.equals("")) return null;
        String str = xsdFile;
        String msgType = getTypeName(str);
        while(true)
        {
            File file = new File(str);

            if ((file.exists())&&(file.isFile()))
            {
                finalPath = file.getAbsolutePath();

                isLocalFile = true;
                break;
            }
            else if (str.toLowerCase().startsWith("file:/"))
            {
                str = str.substring(("file:/").length());
                while(true)
                {
                    String achar = str.substring(0, 1);
                    if (achar.equals("/")) str = str.substring(1);
                    else break;
                }
                if (!File.separator.equals("/")) str = str.replace("/", File.separator);
            }
            else break;
        }
        if (finalPath != null) return finalPath;

        if ((xsdFile.toLowerCase().startsWith("jar:file:/"))&&(xsdFile.indexOf("!") > 0)) return xsdFile;

        if (parentXSD == null) parentXSD = "";
        else parentXSD = parentXSD.trim();
        while(!parentXSD.equals(""))
        {
            int idx = parentXSD.indexOf(Config.V3_XSD_CORE_SCHEMAS_DIRECTORY_NAME);
            if (idx < 0) idx = parentXSD.indexOf(Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME);
            if (idx < 0) break;
            baseUri = parentXSD.substring(0, idx);
            break;
        }

        if (isH3SAssociationType(msgType)) baseUri = baseUri + Config.V3_XSD_MULTI_CACHE_SCHEMAS_DIRECTORY_NAME + "/" + msgType;
        else baseUri = baseUri + Config.V3_XSD_CORE_SCHEMAS_DIRECTORY_NAME + "/" + msgType;
        return baseUri;
    }
    /*
    private boolean isV3MessageType(String str)
    {
        int idx = str.indexOf(".");
        if (idx > 0) str = str.substring(0, idx);

        int len = str.length();
        if (len < 13) return false;
        if (len > 17) return false;
        if ((len == 16)||(len == 14)) return false;
        char[] charArr = str.toCharArray();
        for (int i=0;i<len;i++)
        {
            String achar = str.substring(i, i+1);
            int it = (int) charArr[i];
            if ((i==0)||(i==1)||(i==2)||(i==3)||(i==5)||(i==6))
            {
                if ((it < 65)||(it > 90)) return false;
            }
            else if(i == 4)
            {
                if (!achar.equals("_")) return false;
            }
            else if ((i==7)||(i==8)||(i==9)||(i==10)||(i==11)||(i==12)||(i==15)||(i==16))
            {
                if ((it < 48)||(it > 57)) return false;
            }
            else if (i==13)
            {
                if (!achar.equals("U")) return false;
            }
            else if (i==14)
            {
                if (!achar.equals("V")) return false;
            }
        }
        return true;
    }
    */
    public void insertIncludeFileList(String xsdFile) throws ApplicationException
    {
//        if ((xsdFile == null)||(xsdFile.trim().equals("")))
//            throw new ApplicationException("Source schema file is null.");
//        xsdFile = xsdFile.trim();
//        File file = new File(xsdFile);
//        if ((!file.exists())||(!file.isFile()))
//        {
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
//                   throw new ApplicationException("This xsd file is neither exist nor a file. (include) : " + xsdFile);
//            }
//        }
//
//        if (!xsdFile.toLowerCase().endsWith(".xsd"))
//            throw new ApplicationException("This is not a schema file. : " + xsdFile);
//
//        if (file.getAbsolutePath().trim().toLowerCase().indexOf("coreschemas") > 0) return;

        if (xsdFile == null) return;
        xsdFile = xsdFile.trim();
        if (xsdFile.equals("")) return;
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
        //File file = new File(xsdFile);
        //if ((!file.exists())||(!file.isFile()))
        //{
        //    if (isMain) throw new ApplicationException("This file is not exist or a file. (main) : " + xsdFile);

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
        //}

        if (!xsdFile.toLowerCase().endsWith(".xsd"))
            throw new ApplicationException("This is not a schema file. : " + xsdFile);

        if (isMain)
        {
            mainXSDFile = xsdFile;
            //System.out.println("Main start : " + xsdFile);
        }
        //else System.out.println("Include start : " + xsdFile);

        if (headNode == null)
        {
            XSDValidationTreeNode headXNode = new XSDValidationTreeNode(0, "Head", "");
            headNode = new DefaultMutableTreeNode(headXNode);
        }

        //if (!isMain)
        //{
        //    if (isIncludedFile(xsdFile)) return;
        //}

        XSDValidationEventHandler handler = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            handler = new XSDValidationEventHandler(this, xsdFile);

            producer.setContentHandler(handler);

            //String path1 = "file:///" + xsdFile.replace(File.separator, "/");
            InputSource is = new InputSource(xsdFile);

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
    public String getMainXSDFile()
    {
        return mainXSDFile;
    }
    //public boolean isIncludedFile_DEPRE(String fileName)
    //{
    //    int childCount = headNode.getChildCount();
    //    if (childCount == 0) return false;
    //    for (int i=0;i<childCount;i++)
    //    {
    //        XSDValidationTreeNode tempX = getXnodeFromDnodeChild(headNode, i);
    //        if (tempX == null) continue;
    //        if (fileName.indexOf(tempX.getXSDMessageType()) >= 0) return true;
    //    }
    //    return false;
    //}
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
    //public DefaultMutableTreeNode searchChildComplexType(DefaultMutableTreeNode parent, String nodeName)
    //{
    //    return searchComplexType(nodeName, true);
    //}
    public DefaultMutableTreeNode searchComplexType(DefaultMutableTreeNode startNode, String nodeName)
    {
        return searchComplexType(startNode, nodeName, true);
    }
    public DefaultMutableTreeNode searchComplexType(String nodeName)
    {
        return searchComplexType(null, nodeName, true);
    }
    public DefaultMutableTreeNode searchMessageHeadType(DefaultMutableTreeNode startNode, String nodeName)
    {
        return searchComplexType(startNode, nodeName, false);
    }
    public DefaultMutableTreeNode searchMessageHeadType(String nodeName)
    {
        return searchComplexType(null, nodeName, false);
    }
    private DefaultMutableTreeNode searchComplexType(DefaultMutableTreeNode startNode, String nodeName, boolean complexTag)
    {
        if (nodeName == null) return null;
        nodeName = nodeName.trim();
        if (nodeName.equals("")) return null;

        DefaultMutableTreeNode sNode = null;
        if (startNode == null ) sNode = getHeadNode();
        else sNode = startNode;

        while(true)
        {
            sNode = sNode.getNextNode();
            if (sNode == null) break;


            String complexTypeName = getComplexTypeName(sNode);

            if (complexTypeName == null) continue;

            if (complexTag)
            {
                if (complexTypeName.equals(nodeName)) return sNode;
            }
            else
            {  // only for searching control message type
                if ((complexTypeName.startsWith(nodeName))&&(complexTypeName.endsWith("Message"))) return sNode;
            }
        }

        return null;
    }
    /*
    public DefaultMutableTreeNode searchNodeName(String nodeName)
    {
        if (nodeName == null) return null;
        nodeName = nodeName.trim();
        if (nodeName.equals("")) return null;
        return searchNodeName(new String[] {nodeName});
    }
    public DefaultMutableTreeNode searchNodeName(String[] nodeNames)
    {
        if (nodeNames == null) return null;
        nodeName = nodeName.trim();
        if (nodeName.equals("")) return null;

        DefaultMutableTreeNode sNode = getHeadNode();

        while(true)
        {
            sNode = sNode.getNextNode();
            if (sNode == null) break;


            String complexTypeName = getComplexTypeName(sNode);

            if (complexTypeName == null) continue;

            if (complexTag)
            {
                if (complexTypeName.equals(nodeName)) return sNode;
            }
            else
            {
                if ((complexTypeName.startsWith(nodeName))&&(complexTypeName.endsWith("Message"))) return sNode;
            }

        }

        return null;
    }
    */
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

        name = this.getTypeName(name);

        if (name.toLowerCase().endsWith(".xsd")) name = name.substring(0, name.length()-4);

        int idx = name.indexOf(".");
        if (idx > 0) name = name.substring(0, idx);
        //System.out.println("WWWWW RERERE  name=" + name + ", int=" + nn);

        //String str = name.substring(0, idx);
        String str = name;
        int len = str.length();
        if ((len < 13)||(len > 17)) return false;

        char[] chrs = str.toCharArray();
        int i = 0;

        for(char chr:chrs)
        {
            int n = (int) chr;
            boolean isNumeric = false;
            boolean isCapital = false;
            int cx = 0;
            //System.out.print("FFFFF 4 ("+i+"): " + chr + "," + n);
            if ((n >= 48)&&(n <= 57)) isNumeric = true;
            else if ((n >= 65)&&(n <= 90)) isCapital = true;
            else if (n == 95) {}
            else cx = 1;

            if ((i==0)||(i==1)||(i==2)||(i==3)||(i==5)||(i==6))
            {
                if (!isCapital) cx = 2;
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
                //System.out.println("WWWWW RERERE  cx=" + cx + ", name=" + name );
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
    public void registerTypeHeadPointer(DefaultMutableTreeNode node, String type)
    {
        nodeList.add(node);
        if (type.toLowerCase().endsWith(".xsd")) type = type.substring(0, type.length()-4);
        type = getTypeName(type);
        typeList.add(type);
        //System.out.println("WWWWW insert type=" + type + ", node=" + this.getComplexTypeName(node));
    }
    public DefaultMutableTreeNode searchTypeHeadPointer(String type)
    {
        //if (type.toLowerCase().endsWith(".xsd")) type = type.substring(0, type.length()-4);
        //type = getTypeName(type);
        String rec = type;
        if (type == null) return null;

        String typeP = null;
        int idx = type.indexOf(".");

        if (idx < 0)
        {
            typeP = "datatypes";
        }
        else
        {
            StringTokenizer st = new StringTokenizer(type, ".");
            while(st.hasMoreTokens())
            {
                String token = st.nextToken();
                if (isH3SAssociationType(token)) typeP = token;
                //System.out.println("WWWWW 99 token=" + token + ", typeP=" + typeP);
            }
        }
        //System.out.println("WWWWW 01 type=" + type + ", rec=" + rec + ", typeP=" + typeP);
        if (typeP == null) return null;

        int n = -1;
        for (int i=0;i<typeList.size();i++)
        {
            if (typeList.get(i).equals(typeP))
            {
                //System.out.println("WWWWW  ---->>  typeList.get(i)=" + typeList.get(i) + ", typeP=" + typeP);
                n = i;
            }
        }
        if (n < 0) return null;
        return nodeList.get(n);
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
