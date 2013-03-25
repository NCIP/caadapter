/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 20, 2009
 * Time: 3:51:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchSchemaFiles
{
    public static void main(String[] args)
    {
        ZipFile zip = null;
        try
        {
            zip = new ZipFile("C:\\project\\caadapter\\hl7_home\\Normative_2008\\schemas.zip");
        }
        catch(IOException ie)
        {
            System.out.println("ERROR01 : " + ie.getMessage());
            return;
        }
        Enumeration entries = zip.entries();
        while(entries.hasMoreElements())
        {
            ZipEntry entry = null;
            try
            {
                entry = (ZipEntry) entries.nextElement();
            }
            catch(Exception ee)
            {
                System.out.println("ERROR02 : " + ee.getMessage());
                continue;
            }
            if (entry == null) continue;

            String name = entry.getName();
            if (!name.trim().toLowerCase().endsWith(".xsd")) continue;
            if (name.trim().toLowerCase().indexOf("_in") < 0) continue;

            XSDValidationTree xsdTree = null;
            try
            {
                xsdTree = new XSDValidationTree("jar:file:///C:/project/caadapter/hl7_home/Normative_2008/schemas.zip!/" + name, true);
            }
            catch(ApplicationException ae)
            {
                System.out.println("ERROR03 : " + ae.getMessage());
                continue;
            }
            catch(StringIndexOutOfBoundsException se)
            {
                System.out.println("ERROR04 : " + se.getMessage());
                System.out.println("      ENTRY=" + "jar:file:///C:/project/caadapter/hl7_home/Normative_2008/schemas.zip!/" + name);
                continue;
            }

            System.out.println("  XSD Entry : " + name);

            DefaultMutableTreeNode node = null;
            List<String> list = new ArrayList<String>();
            List<String> list2 = new ArrayList<String>();
            List<DefaultMutableTreeNode> list3 = new ArrayList<DefaultMutableTreeNode>();
            List<String> list4 = new ArrayList<String>();
            String messageType = null;
            while(true)
            {
                if (node == null) node = xsdTree.getHeadNode();
                else node = node.getNextNode();

                if (node == null) break;

                if (messageType == null)
                {
                    String nName = xsdTree.getAttributeValue(node, "name");
                    if (xsdTree.isH3SAssociationType(nName)) messageType = nName;
                }

                String nodeName = xsdTree.getAttributeValue(node, "type");
                if (nodeName == null)
                {
                    nodeName = xsdTree.getAttributeValue(node, "base");
                    if (nodeName == null) continue;
                }
                int idx1 = nodeName.indexOf(".");
                if (idx1 < 0) continue;
                String[] ar = new String[] {null, null, null};

                StringTokenizer st = new StringTokenizer(nodeName, ".");
                int n = 0;
                while(st.hasMoreTokens())
                {
                    n++;
                    String token = st.nextToken().trim();
                    ar[n-1] = token;
                }

                boolean cTag2 = false;
                for (String sa:ar)
                {
                    if ((sa == null)||(sa.equals(""))) cTag2 = true;
                    if (xsdTree.isH3SAssociationType(sa))
                    {
                        boolean cTag = false;
                        for(String ss:list2) if (ss.equals(sa)) cTag = true;
                        if (!cTag)
                        {
                            list2.add(sa);
                            list3.add(node);
                            list4.add(nodeName);
                        }
                    }
                }
                if (cTag2) continue;


                if ((xsdTree.isH3SAssociationType(ar[0]))&&(xsdTree.isH3SAssociationType(ar[1])))
                {

                }
                else continue;


                boolean cTag = false;
                for(String ss:list) if (ss.equals(ar[0] + "." + ar[1])) cTag = true;
                if (cTag) continue;

                list.add(ar[0] + "." + ar[1]);

                System.out.println("    Find Node : " + nodeName + ", parent=" + xsdTree.getAttributeValue((DefaultMutableTreeNode)node.getParent(), "type") + ",  Message Type=" + messageType);



            }

            for(int i=0;i<list.size();i++)
            {
                List<String> list7 = new ArrayList<String>();
                List<String> list8 = new ArrayList<String>();
                List<DefaultMutableTreeNode> list9 = new ArrayList<DefaultMutableTreeNode>();

                for(int j=0;j<list2.size();j++)
                {
                    String cc = list2.get(j);
                    if (list.get(i).indexOf(cc) >= 0) continue;
                    //if (list4.get(i).indexOf(messageType) >= 0) continue;

                    list8.add(cc);
                    list9.add(list3.get(j));
                    list7.add(list4.get(j));
                }
                list2 = list8;
                list3 = list9;
                list4 = list7;
            }
            String parname = null;
            DefaultMutableTreeNode sNode = null;
            for(int i=0;i<list2.size();i++)
            {
                //if (!list2.get(i).startsWith("COCT_MT"))

                while(true)
                {
                    if (sNode == null) sNode = (DefaultMutableTreeNode)(list3.get(i)).getParent();
                    else sNode = (DefaultMutableTreeNode)sNode.getParent();
                    if (sNode == null) break;
                    parname = xsdTree.getAttributeValue(sNode, "name");
                    if (parname != null) break;

                }
                    System.out.println("    == Concreted : " + list4.get(i) + ", parent=" + parname);

            }
            if ((list2.size() == 1)&&(list.size() == 2))
            {
                System.out.println("     Control Message Type = " + messageType);
                System.out.println("     1st level wrapping message type = " + list.get(0).substring(list.get(0).indexOf(".") + 1));
                System.out.println("     2nd level wrapping message type = " + list.get(1).substring(list.get(1).indexOf(".") + 1));
                System.out.println("     Concreted payload message type = " + list2.get(0));
                System.out.println("     Concreted payload head element name = " + xsdTree.getAttributeValue(list3.get(0), "name"));

                DefaultMutableTreeNode tnode = null;
                String fs = null;
                while(true)
                {
                    if (tnode == null) tnode = xsdTree.getHeadNode();
                    else tnode = tnode.getNextNode();

                    if (tnode == null) break;
                    String fs2 = xsdTree.getAttributeValue(tnode, "type");
                    if (fs2 == null) continue;
                    if (fs2.equals(parname))
                    {
                        fs = xsdTree.getAttributeValue(tnode, "name");
                        break;
                    }
                }


                System.out.println("     Concreted payload message parent element name = " + fs);
                System.out.println("     Concreted payload message parent element type = " + parname);
            }
            /*
            InputStream is = null;
            try
            {
                is = zip.getInputStream(entry);
            }
            catch(IOException ie)
            {
                System.out.println("ERROR03 : " + ie.getMessage());
                continue;
            }
            if (is == null) continue;

            String str = "";

            boolean toggle = false;
            String
            while(true)
            {
                int ii = -1;
                try
                {
                    ii = is.read();
                }
                catch(IOException ie)
                {
                    System.out.println("ERROR04 : " + ie.getMessage());
                    break;
                }
                if (ii < 0) break;
                byte bt = (byte) ii;
                char chr = (char) bt;
                String achar = "" + chr;
                str = str + achar;
            }
            */
        }

    }
}
