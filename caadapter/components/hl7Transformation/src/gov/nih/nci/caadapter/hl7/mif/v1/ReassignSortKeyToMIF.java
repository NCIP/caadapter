/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.FileUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Mar 20, 2008
 *          Time:       4:12:07 PM $
 */
public class ReassignSortKeyToMIF
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": ReassignSortKeyToMIF.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/ReassignSortKeyToMIF.java,v 1.00 Mar 20, 2008 4:12:07 PM umkis Exp $";


    String newFileName;
    boolean realmCode = false;
    boolean typeID = false;
    boolean templateId = false;
    /*
    NodeElement head;
    Stack<String> eleName = new Stack<String>();
    Stack<Integer> sortKeyNum = new Stack<Integer>();
    int depth = 0;

    public ReassignSortKeyToMIF(String fileName) throws ApplicationException
    {
        List<String> list = null;
        try
        {
            list = FileUtil.readFileIntoList(fileName);
        }
        catch(IOException ie)
        {
            throw new ApplicationException(ie.getMessage());
        }

        StringBuffer sBuff = new StringBuffer();

        for (String lin:list)
        {
            sBuff.append(lin);
        }

        String line = "";
        boolean isStartInnerElement = false;
        boolean isEndInnerElement = false;
        boolean isInnerData = false;

        for(int i=0;i<sBuff.length();i++)
        {
            String achar = sBuff.substring(i, i+1);
            String achar2 = "";
            if (achar.equals("<"))
            {
                try
                {
                    achar2 = sBuff.substring(i+1, i+2);
                }
                catch(Exception e)
                {
                    throw new ApplicationException("Invalid xml format : ");
                }
                if (achar2.equals("/"))
                {

                }
                else
                {

                }
            }
            else if (achar.equals(">"))
            {
                achar2 = sBuff.substring(i-1, i);
                if (achar2.equals("/"))
                {

                }
                else
                {

                }
            }
            else
            {
                line = line + achar;
            }

        }

    }
    */

    //private NodeElement buildMIFTree(String fileName, List<String> list) throws ApplicationException
    public ReassignSortKeyToMIF(java.io.InputStream stream) throws ApplicationException
    {
        realmCode = false;
        typeID = false;
        templateId = false;
        process(null, stream);
    }
    public ReassignSortKeyToMIF(String fileName) throws ApplicationException
    {
        realmCode = false;
        typeID = false;
        templateId = false;
        process(fileName, null);
    }
    public ReassignSortKeyToMIF(java.io.InputStream stream, boolean realmCode, boolean typeID, boolean templateId) throws ApplicationException
    {
        this.realmCode = realmCode;
        this.typeID = typeID;
        this.templateId = templateId;
        process(null, stream);
    }
    public ReassignSortKeyToMIF(String fileName, boolean realmCode, boolean typeID, boolean templateId) throws ApplicationException
    {
        this.realmCode = realmCode;
        this.typeID = typeID;
        this.templateId = templateId;
        process(fileName, null);
    }
    public ReassignSortKeyToMIF(String fileName, java.io.InputStream stream) throws ApplicationException
    {
        process(fileName, stream);
    }
    private void process(String fileName, java.io.InputStream stream) throws ApplicationException
    {
        if ((fileName == null)||(fileName.trim().equals("")))
        {
            if (stream == null) throw new ApplicationException("Null File Name");
        }
        else
        {
            fileName = fileName.trim();
            if (!fileName.toLowerCase().endsWith(".mif")) throw new ApplicationException("This is not mif file : " + fileName);
        }

        MifFileEventHandler handler = null;
        try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLReader producer = parser.getXMLReader();
			//ContentHandler handler = new MifFileEventHandler(head);
            handler = new MifFileEventHandler(fileName, realmCode, typeID, templateId);

            producer.setContentHandler(handler);

			//producer.parse(new InputSource("file:///" + scsFileName.replaceAll("\\", "/")));
            if (stream == null) producer.parse(new InputSource("file:///" + fileName));
            else producer.parse(new InputSource(stream));

        }
        catch(org.xml.sax.SAXParseException e)
		{
            throw new ApplicationException("1 : " + e.getMessage());
        }
        catch(IOException e)
		{
            //System.out.println(FileUtil.readFileIntoString(fileName));
            throw new ApplicationException("2 : " + e.getMessage());
        }
		catch(Exception e)
		{
            throw new ApplicationException("3 : " + e.getMessage());
        }
        if (!handler.checkSuccess()) throw new ApplicationException("Failure");
        newFileName = handler.gewNewFileName();
    }

    public String getNewFileName()
    {
        return newFileName;
    }

    public static void main(String args[])
    {
        //String fileName = "C:\\HL7\\Installations\\v3GeneratorFork_20070110\\OutputFiles\\MIF\\POCD_MT000040.mif";
        //String fileName = "C:\\projects\\javasig\\data\\mif207\\PORR_MT040011UV01.mif";
        //String fileName = "C:\\HL7\\Installations\\v3GeneratorFork_20070110\\OutputFiles\\MIF\\COCT_MT080000UV.mif";
        String fileName = "C:\\projects\\javasig\\data\\mif207\\PORR_MT040011UV01.mif";

        try
        {
            new ReassignSortKeyToMIF(fileName);
        }
        catch(ApplicationException ae)
        {
            System.out.println(ae.getMessage());
        }
    }
}


class MifFileEventHandler extends DefaultHandler
{
    String fileName;
    String newFileName;

    FileWriter writer = null;
    String data = null;
    String space = "";
    String spaceUnit = "   ";
    String line = null;
    int[] sortKeys = new int[200];
    String[] sortKeyStrings = new String[200];
    Stack<String> stack = null;
    Stack<String> stack2 = null;


    String currentElement = "";
    int currentDepth = 0;

    boolean success = false;
    boolean wellStarted = false;
    boolean realmCode = false;
    boolean typeID = false;
    boolean templateId = false;

    int addedNum = 0;

    String msg = null;

    MifFileEventHandler(String lst)
    {
        super();

        realmCode = false;
        typeID = false;
        templateId = false;
        fileName = lst;
    }

    MifFileEventHandler(String lst, boolean realmCode, boolean typeID, boolean templateId)
    {
        super();

        this.realmCode = realmCode;
        this.typeID = typeID;
        this.templateId = templateId;
        fileName = lst;
    }

    public void startDocument()
    {
        addedNum = 0;
        if (realmCode) addedNum++;
        if (typeID) addedNum++;
        if (templateId) addedNum++;
        try
        {
            if (fileName == null) newFileName = FileUtil.getTemporaryFileName(".mif");
            else newFileName = fileName.substring(0, fileName.length()-4) + "_" + FileUtil.getRandomNumber(4) + ".mif";
            writer = new FileWriter(newFileName);
            wellStarted = true;
        }
        catch(IOException ie)
        {
            newFileName = null;
            System.out.println("A0 : " + ie.getMessage());
            return;
        }
        for (int i=currentDepth;i<sortKeys.length;i++)
        {
            sortKeys[i] = -1;
            sortKeyStrings[i] = null;
        }
        stack = new Stack<String>();
        stack2 = new Stack<String>();
        System.out.println("### Start : " + fileName);
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        if (!wellStarted) return;
        if (!((line == null)||(line.trim().equals(""))))
        {
            if (data == null) data = "\r\n";
            try
            {
                writer.write(line + ">" + data);
            }
            catch(IOException ie)
            {
                System.out.println("A1 : " + ie.getMessage());
            }
            line = null;
            data = null;
        }
        currentDepth++;
        space = "";
        for (int i=0;i<(currentDepth-1);i++) space = space + spaceUnit;
        line = space + "<" + qName;

        stack.push(qName);
        data = null;
        for(int i=0;i<atts.getLength();i++)
        {
            String attName = atts.getQName(i);

            if ((stack2.isEmpty())&&(qName.equals("class"))&&(attName.equals("name"))) stack2.push(atts.getValue(i).trim());

            if ((qName.equals("targetConnection"))&&(attName.equals("name")))
            {
                stack2.push(atts.getValue(i).trim());
                if (msg != null)
                {
                    Enumeration<String> elements = stack2.elements();
                    String str = "";
                    while(elements.hasMoreElements())
                    {
                        String element = elements.nextElement();
                        str = str + element + ".";
                    }
                    str = str.substring(0, str.length()-1);
                    System.out.println(msg + " : " + str);
                    msg = null;
                }
            }
            if (attName.equals("sortKey"))
            {
                String val = atts.getValue(i).trim();
                int srt = -1;
                try
                {
                    srt = Integer.parseInt(val);
                }
                catch(NumberFormatException ne)
                {
                    srt = -20000;
                }

                if (srt == -20000) line = line + " " + attName + "=\"" + val + "\"";
                else
                {
                    int sortKey = sortKeys[currentDepth];
                    String sortKeyString = sortKeyStrings[currentDepth];
                    if (!qName.equals(sortKeyString))
                    {
                        sortKeyStrings[currentDepth] = qName;
                        sortKey = -1;
                    }
                    if (sortKey < 0)
                    {
                        sortKey = 1;
                    }
                    else sortKey++;
                    sortKeys[currentDepth] = sortKey;

                    if (srt != sortKey)
                    {
                        msg = "Different sort key ("+qName+") : depth = "+ currentDepth +" : " + srt + " => " + sortKey;
                    }
                    int sortKeyNum = sortKey;
                    if (qName.equals("attribute")) sortKeyNum = sortKeyNum + addedNum;
                    line = line + " " + attName + "=\"" + sortKeyNum + "\"";
                }

            }
            else
            {
                String str = atts.getValue(i);
                String st = "";
                for(int j=0;j<str.length();j++)
                {
                    String s = str.substring(j, j+1);
                    if (s.equals("&")) st = st + "&amp;";
                    else if (s.equals(">")) st = st + "&gt;";
                    else if (s.equals("<")) st = st + "&lt;";
                    else if (s.equals("\"")) st = st + "&quot;";
                    else st = st + s;
                }
                line = line + " " + attName + "=\"" + st + "\"";
            }

            //System.out.println("  LocalName : " + atts.getLocalName(i) + ", QName : " + atts.getQName(i) + ", Value : " + atts.getValue(i) +  ", URI : " + atts.getURI(i));
        }

        currentElement = qName;
    }
    public void characters(char[] cha, int start, int length)
    {
        //String str = new String(cha);
        if (!wellStarted) return;
        String st = "";
		for(int i=start;i<(start+length);i++)
        {
            String s = "" + cha[i];
            if (s.equals("&")) st = st + "&amp;";
            else if (s.equals(">")) st = st + "&gt;";
            else if (s.equals("<")) st = st + "&lt;";
            else if (s.equals("\"")) st = st + "&quot;";
            else st = st + s;
        }
        String trimed = st.trim();
        if (trimed.equals("")) return;
        if (data == null) data = "";
        data = data + st;
        //if (currentElement.equals("renderingNotes")) System.out.println(st);
        //if (currentElement.equals("renderingNotes")) System.out.println("start: " + start + ", length:" + length + "\n " + "st:" + st + "\ntrimed:" + trimed + "\n--------------------------------------------\n" + str);

    }
    public void endElement(String namespaceURI, String localName, String qName)
    {

        if (!wellStarted) return;
        if (qName.equals("targetConnection")) stack2.pop();
        for (int i=(currentDepth+1);i<sortKeys.length;i++)
        {
            sortKeys[i] = -1;
            sortKeyStrings[i] = null;
        }
        String before = stack.pop();
        if (!before.equals(qName))
        {
            System.out.println("A9 : mis-match element name.");
        }

        if (!((line == null)||(line.trim().equals(""))))
        {

            if (data == null)
            {
                line = line + "/>\r\n";
            }
            else
            {
                line = line + ">" + data + "</" + qName + ">\r\n";
                data = null;
            }
            try
            {
                writer.write(line);
            }
            catch(IOException ie)
            {
                System.out.println("A2 : " + ie.getMessage());
            }
            line = null;
        }
        else
        {
            space = "";
            for (int i=0;i<(currentDepth-1);i++) space = space + spaceUnit;
            try
            {
                writer.write(space + "</" + qName + ">\r\n");
            }
            catch(IOException ie)
            {
                System.out.println("A6 : " + ie.getMessage());
            }
            line = null;
        }
        currentDepth--;

    }
    public void endDocument()
    {
        if (!wellStarted) return;
        if (!((line == null)||(line.trim().equals(""))))
        {
           System.out.println("A8 : Invalid mif file format");
        }

        try
        {
            writer.close();
        }
        catch(IOException ie)
        {
            System.out.println("A5 : " + ie.getMessage());
        }
        success = true;
    }

    public String gewNewFileName()
    {
        return newFileName;
    }

    public boolean checkSuccess()
    {
        return success;
    }
}
/**
 * HISTORY      : : ReassignSortKeyToMIF.java,v $
 */
