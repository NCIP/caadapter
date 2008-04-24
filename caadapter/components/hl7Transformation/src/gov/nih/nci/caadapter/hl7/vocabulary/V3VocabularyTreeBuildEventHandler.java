/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/V3VocabularyTreeBuildEventHandler.java,v 1.00 Apr 24, 2008 1:12:44 PM umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
