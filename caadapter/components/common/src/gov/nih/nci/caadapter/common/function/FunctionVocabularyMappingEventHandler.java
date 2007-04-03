/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyMappingEventHandler.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
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
 */

package gov.nih.nci.caadapter.common.function;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:02:37 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyMappingEventHandler.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $";

    private String message = "";
    private String messageLevel = "";
    private String mappingResult = "";
    private String mappingSource = "";
    private String mappingDomain = "";
    private String ip = "";
    private boolean accessTag = false;
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
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.Message"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if (atts.getQName(i).equalsIgnoreCase("level")) messageLevel = atts.getValue(i);
                //System.out.println("   Attribute currentPath : " + currentPath + ", messageLevel : " + messageLevel);

            }
        }
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingResult"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if (atts.getQName(i).equalsIgnoreCase("value"))
                {
                    mappingResult = atts.getValue(i);
                    accessTag = true;
                }
                //System.out.println("   Attribute currentPath : " + currentPath + ", mappingResult : " + mappingResult);

            }
        }
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.MappingSource"))
        {
            for(int i=0;i<atts.getLength();i++)
            {
                if (atts.getQName(i).equalsIgnoreCase("value"))
                {
                    mappingSource = atts.getValue(i);
                    //System.out.println("   Attribute currentPath : " + currentPath + ", mappingSource : " + mappingSource);
                }
                if (atts.getQName(i).equalsIgnoreCase("domain"))
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
        if (currentPath.equalsIgnoreCase("VocabularyMappingData.Message"))
        {
            message = trimed;
            //System.out.println("   innerElement currentPath : " + currentPath + ", value : " + message);
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
        if (!accessTag) throw new SAXException("No output data. Check the xml message format.");
    }

    public String getMessge() { return message; }
    public String getMessgeLevel() { return messageLevel; }
    public String getMappingResult() { return mappingResult; }
    public String getMappingSource() { return mappingSource; }
    public String getMappingDomain() { return mappingDomain; }
    public String getSourceIP() { return ip; }
    public int getStatus() { return status; }


}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2006/11/02 18:38:04  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/02 18:06:26  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 */
