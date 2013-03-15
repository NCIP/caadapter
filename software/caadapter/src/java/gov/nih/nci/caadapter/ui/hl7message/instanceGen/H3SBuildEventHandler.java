/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.util.Stack;

import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.CommonAttribute;
import gov.nih.nci.caadapter.common.standard.MetaField;
import gov.nih.nci.caadapter.common.standard.CommonAttributeItem;
import gov.nih.nci.caadapter.common.standard.impl.CommonAttributeImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaFieldImpl;
import gov.nih.nci.caadapter.common.standard.impl.CommonAttributeItemImpl;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.H3SInstanceSegmentType;
/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 6, 2007
 *          Time:       2:29:15 PM $
 */
public class H3SBuildEventHandler extends DefaultHandler
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: H3SBuildEventHandler.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/H3SBuildEventHandler.java,v 1.4 2008-06-09 19:53:52 phadkes Exp $";

    String currentElement = "";
    String currentLevel = "";
    H3SInstanceMetaSegment head;
    H3SInstanceMetaSegment currentParent;
    CommonNode curr;
    CommonNode temp;
    //String mode = "hl7";
    boolean success = false;
    int errCount = 0;
    H3SInstanceMetaTree tree;

    boolean justStrat = false;
    boolean hasElement = false;
    boolean errorOnThisElement = false;

    boolean isCurrentElementField = false;

    String TOP_LEVEL = "hl7v3meta";

    Stack<H3SInstanceMetaSegment> stack = new Stack<H3SInstanceMetaSegment>();

    public H3SBuildEventHandler()
    {
        super();
        //tree = dTree;
    }

    public void startDocument()
    {
        justStrat = true;
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        CommonNode cNode = null;
        String nameAttr = null;
        H3SInstanceSegmentType currCloneType = null;

        CommonAttribute attribute = new CommonAttributeImpl();

        if (qName.trim().equals(TOP_LEVEL))
        {
            currCloneType = H3SInstanceSegmentType.CLONE;
            cNode = new H3SInstanceMetaSegment(currCloneType);
            nameAttr = "messageId";
        }
        else if (qName.trim().equals("clone"))
        {
            currCloneType = H3SInstanceSegmentType.CLONE;
            H3SInstanceMetaSegment aSegment = new H3SInstanceMetaSegment(currCloneType);
            cNode = aSegment;
            nameAttr = "clonename";
        }
        else if (qName.trim().equals("attribute"))
        {
            currCloneType = H3SInstanceSegmentType.ATTRIBUTE;
            H3SInstanceMetaSegment aSegment = new H3SInstanceMetaSegment(currCloneType);
            cNode = aSegment;
            nameAttr = "name";
        }
        else if (qName.trim().equals("datatypeField"))
        {
            currCloneType = null;
            MetaField aField = new MetaFieldImpl();
            cNode = aField;
            nameAttr = "name";
        }
        else
        {
            errorOnThisElement = true;
            System.out.println("Error 1 : Invalid Element Name : " + qName);
            currentElement = "ERROR:" + qName;
            currentLevel = "ERROR:" + qName;
            curr = null;
            return;
        }

        for(int i=0;i<atts.getLength();i++)
        {
            String name = atts.getQName(i).trim();
            String val = atts.getValue(i).trim();
            if (name.equals(nameAttr))
            {
                currentElement = val;
                currentLevel = qName;
                String nameS = val;
//                if (qName.trim().equals("clone"))
//                {
//                    H3SMetaSegment segment = (H3SMetaSegment) cNode;
//                    List<CommonNode> sibling = currentParent.getChildNodes();
//                    int n = 0;
//                    for(int j=0;j<sibling.size();j++)
//                    {
//
//                        CommonNode child = sibling.get(j);
//                        if (!(child instanceof H3SMetaSegment)) continue;
//                        H3SMetaSegment seg = (H3SMetaSegment) child;
//                        if (seg.getH3SSegmentType().getType() != H3SSegmentType.CLONE.getType()) continue;
//                        String nameSeg = seg.getOriginalName();
//                        if (nameSeg.equals(val)) n++;
//                    }
//                    nameS = val + segment.getSequenceSeparator() + (n+1);
//                }
//                else
//                {
//                    nameS = val;
//                }

                try
                {
                    cNode.setName(nameS);
                }
                catch(ApplicationException ae)
                {
                    System.err.println("Error 2 : " + ae.getMessage());
                }
            }
            else if (name.equals("uuid"))
            {
                if (val.equals("")) System.out.println("Error 2 : Null uuid");
                else
                {
                    cNode.setXmlPath(val);
                }
            }
            else if (name.equals("cardinality"))
            {
                try
                {
                    cNode.setCardinalityType(CardinalityType.valueOf(val));
                }
                catch(ApplicationException ae)
                {
                    System.out.println("Error 3 : " + ae.getMessage());
                }
            }
            else
            {
                try
                {
                    CommonAttributeItem item = new CommonAttributeItemImpl(cNode.getAttributes());
                    item.setName(name);
                    item.setItemValue(val);
                    cNode.addAttributeItem(item);
                }
                catch(ApplicationException ae)
                {
                    System.out.println("Error 4 : " + ae.getMessage());
                }
            }
        }



        if (currCloneType == null)
        {
            isCurrentElementField = true;
            MetaField field = (MetaField) cNode;
            try
            {
                currentParent.addChildNode(field);
            }
            catch(ApplicationException ae)
            {
                System.out.println("Error 5 : " + ae.getMessage());
            }
            curr = field;
        }
        else
        {
            isCurrentElementField = false;

            H3SInstanceMetaSegment segment = (H3SInstanceMetaSegment) cNode;

            if (head == null)
            {
                head = segment;
                currentParent = segment;
                stack.push(currentParent);
                return;
            }

            try
            {
                currentParent.addChildNode(segment);
            }
            catch(ApplicationException ae)
            {
                if (!currentParent.addChildNodeEnforced(segment))
                    System.out.println("Error 6 : " + ae.getMessage());
            }

            stack.push(currentParent);
            currentParent = segment;
            curr = segment;
        }
    }
    public void characters(char[] cha, int start, int length)
    {
        char[] chars = new char[length];
        for (int i=0;i<length;i++) chars[i] = cha[(start + i)];
        String str = new String(chars);
        if (!str.trim().equals("")) System.out.println("Error 7 : Element has data : " + currentElement + " : " + currentLevel + " : " + length + " : " + str);
    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        //System.out.println("End Element Name : " + qName + ",  namespaceURI : " + namespaceURI + ", LocalName : " + localName);
        //currentPath = currentPath.substring(0, (currentPath.length() - (qName.length() + 1)));


        if (isCurrentElementField)
        {

        }
        else
        {
            currentLevel = currentParent.getH3SSegmentType().toString();
            currentElement = currentParent.getName();

            curr = currentParent;
            try
            {
                currentParent = stack.pop();
            }
            catch(Exception c)
            {
                System.out.println("Error 8 : Stack Exception : " + c.getMessage());
            }

        }
        isCurrentElementField = false;
        if(!qName.equals(currentLevel)) System.out.println("** not matched end element name : "+ qName + " : " + currentLevel);

    }
    public void endDocument()
    {

    }

    public H3SInstanceMetaSegment getHeadSegment()
    {
        return head;
    }

}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/08/03 05:01:32  umkis
 * HISTORY      : add items which have to be input data
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/02 16:30:28  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 16:29:40  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 15:43:55  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:13:11  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
