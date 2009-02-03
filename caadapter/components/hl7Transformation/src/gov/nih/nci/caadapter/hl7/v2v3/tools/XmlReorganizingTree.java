package gov.nih.nci.caadapter.hl7.v2v3.tools;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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

    public void reorderingChildrenNodes(DefaultMutableTreeNode node, List<String> list)
    {
        if (node == null) return;
        if ((list == null)||(list.size() == 0)) return;
        if (node.getChildCount() == 0) return;


        DefaultMutableTreeNode[] nodeArray = new DefaultMutableTreeNode[node.getChildCount()];

        for(int i=0;i<node.getChildCount();i++)
        {
            DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) node.getChildAt(i);
            nodeArray[i] = (DefaultMutableTreeNode) node.getChildAt(i);
        }

    }
    public void generatingXMLFile(String fileName)
    {

    }
}
