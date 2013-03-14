/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.validation.complement;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nih.nci.caadapter.common.ApplicationException;

//import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBrowsingNode;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jan 30, 2009
 * Time: 1:27:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class XSDValidationEventHandler extends DefaultHandler
{

    private DefaultMutableTreeNode headNode = null;

    private XSDValidationTree tree = null;
    //private DefaultMutableTreeNode currentParent = null;
    private DefaultMutableTreeNode curr = null;

    boolean success = false;
    String errMessage = null;

    boolean justStart = false;
    DefaultMutableTreeNode toBeContinued = null;
    String xsdFileName = "";

    public XSDValidationEventHandler(XSDValidationTree treeN, String xsdFile)
    {
        super();
        tree = treeN;
        headNode = null;
        xsdFileName = xsdFile;
        //headNode.removeAllChildren();
        //justStart = false;
        //System.out.println("XXXXX 888");
    }

    public void startDocument()
    {
        //System.out.println("XXXXX 999");
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        if (errMessage != null) return;
        toBeContinued = null;
        XSDValidationTreeNode node = new XSDValidationTreeNode();
        node.setRole(3);
        node.setName(qName);
        if (headNode == null)
        {
            //headNode.setUserObject(node);
            headNode = new DefaultMutableTreeNode(node);
            curr = headNode;
            justStart = true;
            tree.registerTypeHeadPointer(headNode, xsdFileName);
        }
        else
        {
            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(node);
            //dNode.setAllowsChildren(false);
            //curr.setAllowsChildren(true);
            //dNode.setParent(curr);
            curr.add(dNode);
            curr = dNode;
        }

        for(int i=0;i<atts.getLength();i++)
        {
            String name = atts.getQName(i).trim();
            String val = atts.getValue(i).trim();

            XSDValidationTreeNode xNode = new XSDValidationTreeNode(4, name, val);
            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(xNode);

//            if (name.toLowerCase().indexOf("name") >= 0)
//            {
//                XSDValidationTreeNode nTemp1 = (XSDValidationTreeNode) curr.getUserObject();
//                String title = nTemp1.getName();
//                XSDValidationTreeNode nTemp2 = new XSDValidationTreeNode(node.getRoleKind()[0], title + ";" + val, null);
//                curr.setUserObject(nTemp2);
//            }
            curr.add(dNode);
            //xs:include schemaLocation=
            if ((qName.equals("xs:include"))&&(name.equals("schemaLocation")))
            {
                try
                {
                    //System.out.println("WWWW xsdFileName : " + xsdFileName);
                    if ((xsdFileName == null)||(xsdFileName.trim().equals(""))) tree.insertIncludeFileList(val);
                    else tree.insertIncludeFileList(xsdFileName.trim() + "|" + val);
                }
                catch(ApplicationException ae)
                {
                    errMessage = ae.getMessage();
                    System.out.print("ApplicationException : " + errMessage);
                }
            }


        }
    }

    public void characters(char[] cha, int start, int length)
    {
        if (errMessage != null) return;
        //hasElement = true;
        char[] chars = new char[length];
        for (int i=0;i<length;i++) chars[i] = cha[(start + i)];
        String str = new String(chars);

        str = str.trim();
        if (!str.equals(""))
        {
            if (toBeContinued != null)
            {
                XSDValidationTreeNode oNode = (XSDValidationTreeNode) toBeContinued.getUserObject();
                if (oNode.getRole() == 5)//.equals(oNode.getRoleKind()[2]))
                {
                    XSDValidationTreeNode xNode = new XSDValidationTreeNode(5, "inlineText", oNode.getValue()+str);

                    curr.remove(toBeContinued);
                    toBeContinued = new DefaultMutableTreeNode(xNode);
                    curr.add(toBeContinued);
                }
                else System.err.println("This is a big error ::::");
            }
            else
            {
                XSDValidationTreeNode xNode = new XSDValidationTreeNode(5, "inlineText", str);
                toBeContinued = new DefaultMutableTreeNode(xNode);

                curr.add(toBeContinued);
            }
        }

    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        if (errMessage != null) return;
        curr = (DefaultMutableTreeNode) curr.getParent();
        toBeContinued = null;
    }
    public void endDocument()
    {

    }

    public String getErrMessage()
    {
        return errMessage;
    }

    public DefaultMutableTreeNode getHeadNode()
    {
        return headNode;
    }

}

