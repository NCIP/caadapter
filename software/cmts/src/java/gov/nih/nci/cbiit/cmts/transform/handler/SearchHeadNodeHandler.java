package gov.nih.nci.cbiit.cmts.transform.handler;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2011
 * Time: 6:54:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchHeadNodeHandler extends DefaultHandler
{
    private String headElement = null;
    private List<String> elementNames = new ArrayList<String>();
    private int depth;
    private String currentPath = "";

    public void startDocument()
    {

    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {

        currentPath = currentPath + "." + qName;
        if (currentPath.startsWith(".")) currentPath = currentPath.substring(1);
        depth++;
        if (headElement == null) headElement = qName;
        String nameS = qName + ";" + depth;
        boolean found = false;
        for(String str:elementNames)
        {
            if (str.equals(nameS))
            {
                found = true;
                break;
            }
        }
        if (!found) elementNames.add(nameS);


    }
    public void characters(char[] cha, int start, int length)
    {

    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        depth--;
        int idx = currentPath.length() - (qName.length() + 1);
        if (idx < 0) idx = 0;
        if (currentPath.endsWith(qName)) currentPath = currentPath.substring(0, idx);
        //System.out.println("endElement currentPath : " + currentPath);
    }
    public void endDocument() throws SAXException
    {

    }

    public String getHeadElement()
    {
        return headElement;
    }
    public List<String> getElementNameList()
    {
        return elementNames;
    }
    public int getMinDepthWithElementName(String name)
    {
        int res = -1;
        if ((name == null)||(name.trim().equals(""))) return res;
        name = name.trim();
        for (String c:elementNames)
        {
            if (c.startsWith(name + ";"))
            {
                String num = c.substring(name.length() + 1);
                if (num.trim().equals("")) continue;
                int intNum = -1;
                try
                {
                    intNum = Integer.parseInt(num);
                }
                catch(NumberFormatException ne)
                {
                    continue;
                }
                if (res < 0) res = intNum;
                else
                {
                    if (intNum < res) res = intNum;
                }
            }
        }
        return res;
    }

}