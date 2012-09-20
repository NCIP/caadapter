/*
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.dvts.common.function;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import gov.nih.nci.caadapter.dvts.common.util.Config;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: altturbo $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2009-08-20 00:25:26 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyXMLMappingEventHandler.java,v 1.4 2009-08-20 00:25:26 altturbo Exp $";

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
                    (type.equalsIgnoreCase("AssignValue"))||
                    (type.equalsIgnoreCase("taggingSuffix"))||
                    (type.equalsIgnoreCase("taggingPrefix")))
                {
                    if (value == null) throw new SAXException("Invalid VOM file format : " + type + " of the "+name+" element needs its own value.");
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

    public List<String[]> getDomains()
    {
        FunctionVocabularyEventHandlerNode temp = head;
        List<String[]> list = new ArrayList<String[]>();
        while(temp!=null)
        {
            if (temp.getName().equalsIgnoreCase("domain"))
            {
                String name = getAttributeValue(temp, "name");
                String inverseAllowed = getAttributeValue(temp, "inverseAllowed");
                if (inverseAllowed == null) inverseAllowed = "";
                if (inverseAllowed.equalsIgnoreCase("false")) inverseAllowed = "false";
                else if (inverseAllowed.equalsIgnoreCase("f")) inverseAllowed = "false";
                else if (inverseAllowed.equalsIgnoreCase("no")) inverseAllowed = "false";
                else inverseAllowed = "true";

                FunctionVocabularyEventHandlerNode lower = temp.getLower();
                String commentDomain = "";
                String commentSource = "";
                String commentTarget = "";
                while(lower != null)
                {
                    if (!lower.isAttribute())
                    {
                        if ((lower.getName().equalsIgnoreCase("comment"))||
                            (lower.getName().equalsIgnoreCase("annotation")))
                        {
                            commentDomain = lower.getValue();

                            FunctionVocabularyEventHandlerNode lower2 = temp.getLower();
                            while(lower2 != null)
                            {
                                if (!lower2.isAttribute())
                                {
                                    if (lower2.getName().equalsIgnoreCase("source"))
                                    {
                                        commentSource = lower2.getValue();
                                    }
                                    if (lower2.getName().equalsIgnoreCase("target"))
                                    {
                                        commentTarget = lower2.getValue();
                                    }
                                }
                                lower2 = lower2.getRight();
                            }
                        }
                    }
                    lower = lower.getRight();
                }
                list.add(new String[] {name, inverseAllowed, commentDomain, commentSource, commentTarget});
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

        String sourceVal = "";
        String targetVal = "";
        boolean underTranslation = false;
        while(temp!=null)
        {
            if (temp.getName().equalsIgnoreCase("domain"))  
            {
                if ((domain.equals(""))&&(getDomains().size() == 1)) domain = getAttributeValue(temp, "name");
                if (getAttributeValue(temp, "name").equalsIgnoreCase(domain))
                {
                    //System.out.println("@@ domain find : " + domain);
                    domainTag = true;
                }
            }
            if (domainTag)
            {
                //System.out.println("CCCCC SS element name =" + temp.getName());
                //System.out.println("CCCCC SS Line=" + lines);
                if (temp.getName().equalsIgnoreCase("domain"))
                {
                    if (!getAttributeValue(temp, "name").equalsIgnoreCase(domain))
                    {
                        domainTag = false;
                        break;
                    }
                }
                if ((temp.getName().equalsIgnoreCase("translation"))||(!isUnderElement(temp, "translation")))
                {
                    if ((!sourceVal.equals(""))&&(!targetVal.equals("")))
                    {
                        lines = lines + sourceVal + " " + Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR + " ";
                        lines = lines + targetVal + "\t";
                    }
                    sourceVal = "";
                    targetVal = "";
                }


                if (isUnderElement(temp, "translation"))
                {
                    if (temp.getName().equalsIgnoreCase("source"))
                    {
                        sourceVal = getAttributeValue(temp, "value");
                        if (sourceVal == null) sourceVal = "";
                        else sourceVal = sourceVal.trim();
                    }
                    if (temp.getName().equalsIgnoreCase("target"))
                    {
                        targetVal = getAttributeValue(temp, "value");
                        if (targetVal == null) targetVal = "";
                        else targetVal = targetVal.trim();
                    }

                }
                if ((temp.getName().equalsIgnoreCase("elseCase"))||(temp.getName().equalsIgnoreCase("inverseElseCase")))
                {
                    String typ = getAttributeValue(temp, "type");
                    lines = lines + "&" + temp.getName() + ":" + typ;
                    if ((typ.equalsIgnoreCase("valueassign"))||
                        (typ.equalsIgnoreCase("assignvalue"))||
                        (typ.equalsIgnoreCase("taggingsuffix"))||
                        (typ.equalsIgnoreCase("taggingprefix")))
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
 * HISTORY      : Revision 1.3  2008/06/09 19:53:49  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
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
