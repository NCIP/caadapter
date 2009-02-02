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
            throw new ApplicationException("Source schema file is null.");
        }
        mainXSDFile = file0;

        String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
        //String grandParentDir = mainXSDFile.getParentFile().getParent().trim();
        if (!grandParentDir.endsWith(File.separator)) grandParentDir = grandParentDir + File.separator;

        String coreDir = grandParentDir + "coreschemas";
        File file = new File(coreDir);
        if ((!file.exists())||(!file.isDirectory()))
           throw new ApplicationException("This file is not exist or a file. (include) : " + xsdFile);

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
                   throw new ApplicationException("This file is not exist or a file. (include) : " + xsdFile);
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
            System.out.println("Inserted Include xsd file : " + xsdFile);
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
            System.out.println("Main start : " + xsdFile);
        }
        else System.out.println("Include start : " + xsdFile);

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
            System.out.println("Added Main ("+headNode.getChildCount()+") : " + ((XSDValidationTreeNode)node.getUserObject()).toString());
        }
        else
        {
            DefaultMutableTreeNode node = handler.getHeadNode();
            XSDValidationTreeNode headIncludeXNode = getXnodeFromDnode(node);
            headIncludeXNode.setRole(2);
            headIncludeXNode.setXSDFileName(xsdFile);
            node.setUserObject(headIncludeXNode);
            headNode.add(node);
            System.out.println("Added Include ("+headNode.getChildCount()+") : " + ((XSDValidationTreeNode)node.getUserObject()).toString());
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
}
