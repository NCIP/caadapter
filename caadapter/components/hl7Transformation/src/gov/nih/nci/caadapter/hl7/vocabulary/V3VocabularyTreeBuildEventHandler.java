/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.vocabulary;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.CommonAttributeItem;
import gov.nih.nci.caadapter.common.standard.MetaSegment;
import gov.nih.nci.caadapter.common.standard.MetaTreeMeta;
import gov.nih.nci.caadapter.common.standard.impl.CommonAttributeItemImpl;
import gov.nih.nci.caadapter.common.standard.impl.MetaSegmentImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Apr 24, 2008
 *          Time:       1:12:44 PM $
 */
public class V3VocabularyTreeBuildEventHandler extends DefaultHandler
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": V3VocabularyTreeBuildEventHandler.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/V3VocabularyTreeBuildEventHandler.java,v 1.00 Apr 24, 2008 1:12:44 PM umkis Exp $";


    String currentElement = "";
    String currentLevel = "";
    MetaSegment head;
    MetaSegment currentParent;
    MetaSegment curr;
    MetaSegment temp;
    //String mode = "hl7";
    boolean success = false;
    int errCount = 0;
    MetaTreeMeta tree;

    boolean justStart = false;
    boolean hasElement = false;
    boolean errorOnThisElement = false;

    boolean isCurrentElementField = false;

    String TOP_LEVEL = "hl7v3meta";

    Stack<MetaSegment> stack = new Stack<MetaSegment>();


    public V3VocabularyTreeBuildEventHandler()
    {
        super();
    }

    public void startDocument()
    {
        justStart = true;
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        hasElement = false;

        MetaSegment aSegment = new MetaSegmentImpl();
        try
        {
            aSegment.setName(qName.trim());
            for(int i=0;i<atts.getLength();i++)
            {
                String name = atts.getQName(i).trim();
                String val = atts.getValue(i).trim();

                CommonAttributeItem item = new CommonAttributeItemImpl(aSegment.getAttributes());
                item.setName(name);
                item.setItemValue(val);
                aSegment.addAttributeItem(item);
            }
        }
        catch(ApplicationException ae)
        {
            System.err.println("ApplicationException (1) : " + ae.getMessage());
            return;
        }


        if (justStart)
        {
            justStart = false;
            head = aSegment;
            currentParent = head;
            stack.push(head);
            return;
        }

        try
        {
            currentParent.addChildNode(aSegment);
        }
        catch(ApplicationException ae)
        {
            currentParent.addChildNodeEnforced(aSegment);
        }
        curr = aSegment;
        currentParent = aSegment;
        stack.push(aSegment);

    }
    public void characters(char[] cha, int start, int length)
    {
        hasElement = true;
        char[] chars = new char[length];
        for (int i=0;i<length;i++) chars[i] = cha[(start + i)];
        String str = new String(chars);
        if (!str.trim().equals("")) curr.setAnotherValue(str.trim());//System.out.println("Error 7 : Element has data : " + currentElement + " : " + currentLevel + " : " + length + " : " + str);
    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        if (!currentParent.getName().equals(qName.trim()))
        {
            System.err.println("Element name mismatched : " + currentParent.getName() + " : " + qName.trim());
        }
        MetaSegment tempParent = (MetaSegment) currentParent.getParent();

//        if (qName.trim().equals("p"))
//        {
//            if ((currentParent.getAnotherValue() != null)&&(!currentParent.getAnotherValue().equals("")))
//            {
//                if ((tempParent.getAnotherValue() != null)&&(!tempParent.getAnotherValue().equals("")))
//                {
//                    tempParent.setAnotherValue(tempParent.getAnotherValue() + "<p>" + currentParent.getAnotherValue() + "</p>");
//                }
//                else tempParent.setAnotherValue("<p>" + currentParent.getAnotherValue() + "</p>");
//                if (!tempParent.removeChildSegment(currentParent))
//                currentParent = tempParent;
//                tempParent = (MetaSegment) tempParent.getParent();
//            }
//        }

        if (tempParent != null)
        {
            if (currentParent.getChildNodes().size() == 0)
            {
                if (!tempParent.mutateSegmentToField(currentParent, currentParent.createNewFieldInstance()))
                    System.err.println("Element mutation failure : " + currentParent.getName());
            }
        }
        currentParent = tempParent;
        if (currentParent == null)
        {
            System.err.println("null parent segment : " + qName.trim());
        }

    }
    public void endDocument()
    {

    }

    public MetaSegment getHeadSegment()
    {
        return head;
    }

}

/**
 * HISTORY      : : V3VocabularyTreeBuildEventHandler.java,v $
 */
