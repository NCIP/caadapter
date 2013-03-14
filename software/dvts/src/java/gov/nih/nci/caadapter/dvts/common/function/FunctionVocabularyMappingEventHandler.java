/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common.function;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */
public class FunctionVocabularyMappingEventHandler extends DefaultHandler
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyMappingEventHandler.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyMappingEventHandler.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

    private String message = "";
    private String messageLevel = null;
    private List<String> mappingResults = null;
    private String mappingSource = "";
    private String mappingDomain = "";
    private String ip = "";
    //private boolean accessTag = false;
    private String currentPath = "";
    private int status = 0;

    public void startDocument()
    {
        status = 1;
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {

        currentPath = currentPath + "." + qName;
        if (currentPath.startsWith(".")) currentPath = currentPath.substring(1);
        //System.out.println("startElement currentPath : " + currentPath);
        //currentElement = qName;
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.ReturnMessage"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if ((atts.getQName(i).equalsIgnoreCase("errorLevel"))||(atts.getQName(i).equalsIgnoreCase("level")))
                {
                     messageLevel = atts.getValue(i);
                }

                //System.out.println("   Attribute currentPath : " + currentPath + ", messageLevel : " + messageLevel);

            }
        }
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingResult"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if (atts.getQName(i).equalsIgnoreCase("value"))
                {
                    String res = atts.getValue(i);
                    if ((res != null)&&(!res.trim().equals("")))
                    {
                        if (mappingResults == null) mappingResults = new ArrayList<String>();
                        mappingResults.add(atts.getValue(i));
                        //accessTag = true;
                    }
                }
                //System.out.println("   Attribute currentPath : " + currentPath + ", mappingResult : " + mappingResult);

            }
        }
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingResults.Result"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if (atts.getQName(i).equalsIgnoreCase("value"))
                {
                    String res = atts.getValue(i);

                    if ((res != null)&&(!res.trim().equals("")))
                    {
                        if (mappingResults == null) mappingResults = new ArrayList<String>();
                        mappingResults.add(atts.getValue(i));
                        //accessTag = true;
                    }

                }
                //System.out.println("   Attribute currentPath : " + currentPath + ", mappingResult : " + mappingResult);

            }
        }
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingSource"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if ((atts.getQName(i).equalsIgnoreCase("sourceValue"))||(atts.getQName(i).equalsIgnoreCase("value")))
                {
                    mappingSource = atts.getValue(i);
                    //System.out.println("   Attribute currentPath : " + currentPath + ", mappingSource : " + mappingSource);
                }
                if ((atts.getQName(i).equalsIgnoreCase("domainName"))||(atts.getQName(i).equalsIgnoreCase("domain")))
                {
                    mappingDomain = atts.getValue(i);
                    //System.out.println("   Attribute currentPath : " + currentPath + ", mappingDomain : " + mappingDomain);
                }
                 if (atts.getQName(i).equalsIgnoreCase("ip"))
                {
                    ip = atts.getValue(i);
                    //System.out.println("   Attribute currentPath : " + currentPath + ", ip : " + ip);
                }
            }
        }
    }
    public void characters(char[] cha, int start, int length)
    {
        String st = "";
        for(int i=start;i<(start+length);i++) st = st + cha[i];
        String trimed = st.trim();
        if (trimed.equals("")) return;
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.ReturnMessage"))
        {
            message = trimed;
            //System.out.println("   innerElement currentPath : " + currentPath + ", value : " + message);
        }

        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingResults.Result"))
        {
            if ((trimed != null)&&(!trimed.trim().equals("")))
            {
                if (mappingResults == null) mappingResults = new ArrayList<String>();
                mappingResults.add(trimed);
            }
        }


    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        int idx = currentPath.length() - (qName.length() + 1);
        if (idx < 0) idx = 0;
        if (currentPath.endsWith(qName)) currentPath = currentPath.substring(0, idx);
        //System.out.println("endElement currentPath : " + currentPath);
    }
    public void endDocument() throws SAXException
    {
        status = 2;
        if (messageLevel == null) throw new SAXException("This is not a VocabularyMappingData object. Check the xml message format.");
        //if (!accessTag) throw new SAXException("No output data. Check the xml message format.");
    }

    public String getMessge() { return message; }
    public String getMessgeLevel() { return messageLevel; }
//    public String getMappingResult()
//    {
//        if ((mappingResults == null)||(mappingResults.size() == 0)) return null;
//        return mappingResults.get(0);
//    }
    public List<String> getMappingResults() { return mappingResults; }
    public String getMappingSource() { return mappingSource; }
    public String getMappingDomain() { return mappingDomain; }
    public String getSourceIP() { return ip; }
    public int getStatus() { return status; }


}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/11/02 18:38:04  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/02 18:06:26  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 */
