/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyXMLMappingEventHandler.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The HL7 SDK Software License, Version 1.0
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
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.function;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import gov.nih.nci.caadapter.common.util.Config;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-06 18:54:28 $
 */
public class FunctionVocabularyXMLMappingEventHandler extends DefaultHandler
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyXMLMappingEventHandler.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyXMLMappingEventHandler.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $";

    private FunctionVocabularyEventHandlerNode head = null;
    private FunctionVocabularyEventHandlerNode current = null;

    private int status = 0;

    public void startDocument()
    {
        status = 1;
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
    {
        if (status == 1)
        {
            head = new FunctionVocabularyEventHandlerNode(qName);
            current = head;
            status = 2;
            return;
        }
        FunctionVocabularyEventHandlerNode temp = null;
        try
        {
            if (current.getLower() == null)
            {
                temp = new FunctionVocabularyEventHandlerNode(current, qName);
                current.setLower(temp);
                current = temp;
            }
            else
            {
                temp = current.getLower();
                while(temp.getRight()!=null) temp = temp.getRight();
                FunctionVocabularyEventHandlerNode temp1 = new FunctionVocabularyEventHandlerNode(current, qName);
                temp.setRight(temp1);
                current = temp1;
            }

            for(int i=0;i<atts.getLength();i++)
            {
                FunctionVocabularyEventHandlerNode temp1 = current;

                if (temp1.getLowerAttr() == null)
                {
                    temp = new FunctionVocabularyEventHandlerNode(temp1, atts.getQName(i), true);
                    temp1.setLowerAttr(temp);
                    temp.setValue(atts.getValue(i));
                }
                else
                {
                    temp = temp1.getLowerAttr();
                    while(temp.getRight()!=null) temp = temp.getRight();
                    FunctionVocabularyEventHandlerNode temp2 = new FunctionVocabularyEventHandlerNode(temp1, atts.getQName(i), true);
                    temp.setRight(temp2);
                    temp2.setValue(atts.getValue(i));
                }
            }
        }
        catch(FunctionException fe)
        {
            throw new SAXException(fe.getMessage(), fe);
        }
    }
    public void characters(char[] cha, int start, int length)
    {
        String st = "";
        for(int i=start;i<(start+length);i++) st = st + cha[i];
        String trimed = st.trim();
        if (trimed.equals("")) return;
        current.setValue(trimed);
    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        //if (!qName.equals(current.getName())) System.out.println("Different : " + qName + " : "  + current.getName());
        current = current.getUpper();

    }
    public void endDocument() throws SAXException
    {
        status = 3;
        boolean cTag = false;
        FunctionVocabularyEventHandlerNode temp = head;
        while(temp!=null)
        {
            String name = temp.getName();
            if ((name.equalsIgnoreCase("elseCase"))||(name.equalsIgnoreCase("inverseElseCase")))
            {
                String type = getAttributeValue(temp, "type");
                String value = getAttributeValueOriginal(temp, "value");
                if ((type.equalsIgnoreCase("valueAssign"))||
                    (type.equalsIgnoreCase("taggingSuffix"))||
                    (type.equalsIgnoreCase("taggingPrefix")))
                {
                    if (value == null) throw new SAXException("Invalid vom file format : " + type + " of the "+name+" element needs its own value.");
                }
            }
            temp = nextTraverse(temp);
        }
        /*
        FunctionVocabularyEventHandlerNode temp = head;
        while(temp!=null)
        {
            System.out.println("MMMMM98: "+printXMLPath(temp));
            String attributePath = printXMLAttributePath(temp);
            if (!attributePath.equals("")) System.out.println("MMMMM99: "+attributePath);
            temp = nextTraverse(temp);
        }
        */
    }

    private String printXMLAttributePath(FunctionVocabularyEventHandlerNode nde)
    {
        if (nde.getLowerAttr() == null) return "";
        nde = nde.getLowerAttr();
        String path = "";
        while(true)
        {
            path = path + "\n" + printXMLPath(nde);
            if (nde.getRight() == null) break;
            nde = nde.getRight();
        }
        return path.substring(1);
    }
    private String printXMLPath(FunctionVocabularyEventHandlerNode nde)
    {
        String path = "";
        if (nde.isAttribute()) path = "@" + nde.getName() + ":" + nde.getValue();
        else path = "." + nde.getName() + ":" + nde.getValue();
        while(nde.getUpper() != head)
        {
            nde = nde.getUpper();
            if (nde == null) break;
            path = "." + nde.getName() + path;
        }
        return path.substring(1);
    }
    private FunctionVocabularyEventHandlerNode nextTraverse(FunctionVocabularyEventHandlerNode nde)
    {
        if (nde == head) return head.getLower();

        if (nde.getLower() != null) return nde.getLower();
        if (nde.getRight() != null) return nde.getRight();
        FunctionVocabularyEventHandlerNode temp = nde;
        if (temp.getUpper() == head) return null;
        while(temp.getRight()==null)
        {
            temp = temp.getUpper();
            if (temp == head) return null;
        }
        return temp.getRight();
    }
    private boolean isUnderElement(FunctionVocabularyEventHandlerNode nde, String tag)
    {
        boolean tTag = false;
        while(true)
        {
            if (nde.getName().equalsIgnoreCase(tag)) return true;

            nde = nde.getUpper();
            if (nde == head) break;
        }
        return tTag;
    }
    private String getAttributeValue(FunctionVocabularyEventHandlerNode nde, String attr)
    {
        String c = getAttributeValueOriginal(nde, attr);
        if (c == null) return "";
        return c;
    }
    private String getAttributeValueOriginal(FunctionVocabularyEventHandlerNode nde, String attr)
    {
        if (nde.getLowerAttr() == null) return null;
        nde = nde.getLowerAttr();
        while(true)
        {
            if (nde.getName().equalsIgnoreCase(attr)) return nde.getValue();
            if (nde.getRight() == null) break;
            nde = nde.getRight();

        }
        return null;
    }

    public List<String> getDomains()
    {
        FunctionVocabularyEventHandlerNode temp = head;
        List<String> list = new ArrayList<String>();
        while(temp!=null)
        {
            if (temp.getName().equalsIgnoreCase("domain"))
            {
                list.add(getAttributeValue(temp, "name"));
            }

            temp = nextTraverse(temp);
        }
        return list;
    }
    public String getLinesSearchValue(String domain)
    {
        FunctionVocabularyEventHandlerNode temp = head;
        //List<String> list = new ArrayList<String>();
        boolean domainTag = false;

        String lines = "";
        while(temp!=null)
        {
            if (temp.getName().equalsIgnoreCase("domain"))
            {
                if ((domain.equals(""))&&(getDomains().size() == 1)) domain = getAttributeValue(temp, "name");
                if (getAttributeValue(temp, "name").equalsIgnoreCase(domain)) domainTag = true;
            }
            if (domainTag)
            {
                if (temp.getName().equalsIgnoreCase("domain"))
                {
                    if (!getAttributeValue(temp, "name").equalsIgnoreCase(domain))
                    {
                        domainTag = false;
                        break;
                    }
                }
                if (isUnderElement(temp, "translation"))
                {
                    if (temp.getName().equalsIgnoreCase("source"))
                    {
                        lines = lines + getAttributeValue(temp, "value") + " " + Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR + " ";
                    }
                    if (temp.getName().equalsIgnoreCase("target"))
                    {
                        lines = lines + getAttributeValue(temp, "value") + "\t";
                    }
                }
                if ((temp.getName().equalsIgnoreCase("elseCase"))||(temp.getName().equalsIgnoreCase("inverseElseCase")))
                {
                    String typ = getAttributeValue(temp, "type");
                    lines = lines + "&" + temp.getName() + ":" + typ;
                    if ((typ.equalsIgnoreCase("valueassign"))||(typ.equalsIgnoreCase("taggingsuffix"))||(typ.equalsIgnoreCase("taggingprefix")))
                    {
                        lines = lines + "='" + getAttributeValue(temp, "value") + "' \t";
                    }
                    else lines = lines + "\t";
                }
            }

            temp = nextTraverse(temp);
        }
        return lines;
    }

    public String getFirstValue(String domain)
    {
        FunctionVocabularyEventHandlerNode temp = head;
        //List<String> list = new ArrayList<String>();
        boolean domainTag = false;
        boolean sourceTag = false;
        String lines = "";
        while(temp!=null)
        {
            if (temp.getName().equalsIgnoreCase("domain"))
            {
                if (domain.equals("")) domain = temp.getValue();
                if (temp.getValue().equalsIgnoreCase(domain)) domainTag = true;
            }
            if (domainTag)
            {
                if (temp.getName().equalsIgnoreCase("domain"))
                {
                    if (!temp.getValue().equalsIgnoreCase(domain))
                    {
                        domainTag = false;
                        break;
                    }
                }
                if (isUnderElement(temp, "translation"))
                {
                    if (temp.getName().equalsIgnoreCase("source"))
                    {
                        sourceTag = true;
                    }
                    if (temp.getName().equalsIgnoreCase("target"))
                    {
                        if (sourceTag) return getAttributeValue(temp, "value");
                    }
                }
            }

            temp = nextTraverse(temp);
        }
        return "";
    }

    public int getStatus() { return status; }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/11/02 18:38:04  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/01 02:06:09  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 */
