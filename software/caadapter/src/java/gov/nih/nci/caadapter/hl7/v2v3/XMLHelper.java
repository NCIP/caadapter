/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3;

/**
 * This class provides creates a XML document with the basic method implementations
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public class XMLHelper {
    private StringBuffer xmlString;
    private static String xmlVersion = "1.0";
    private static String encoding = "UTF-8";

    public XMLHelper() {
	xmlString = new StringBuffer();
    }

    /**
     * @param s
     */
    public XMLHelper(String s) {
	xmlString = new StringBuffer(s);
    }
    /*
     * If the flag is false; then the element will be <dithu>
     * else <dithu attr01="milk" attr02="butter"></dithu>
     *
     */
    public void addBeginTag(String s, boolean flag) {
	xmlString.append("<");
	xmlString.append(s);
	if (!flag)
	    xmlString.append(">");
    }

    public void closeEmptyTag() {
	xmlString.append("/>");
	xmlString.append("\n");
    }

    public void closeTag() {
	xmlString.append(">");
	xmlString.append("\n");
    }

    public void addEndTag(String s) {
	xmlString.append("</");
	xmlString.append(s);
	xmlString.append(">");
	xmlString.append("\n");
    }

    /**
     * @param s
     * @param s1
     */
    public void addAttribute(String s, String s1) {
	xmlString.append(" ");
	xmlString.append(s);
	xmlString.append("=\"");
	xmlString.append(convertSpecialChars(s1));
	xmlString.append("\"");
    }

    public void addValue(String s) {
	xmlString.append(s);
    }

    public void append(String s) {
	xmlString.append(s);
    }

    public String toString() {
	return xmlString.toString();
    }

    public static String convertSpecialChars(String s) {
	StringBuffer stringbuffer = new StringBuffer();
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c == '&')
		stringbuffer.append("&amp;");
	    else if (c == '<')
		stringbuffer.append("&lt;");
	    else if (c == '>')
		stringbuffer.append("&gt;");
	    else if (c == '"')
		stringbuffer.append("&quot;");
	    else if (c == '\'')
		stringbuffer.append("&apos;");
	    else
		stringbuffer.append(c);
	}
	return stringbuffer.toString();
    }

    /**
     * @param xmlHelper
     */
    public static void appendHeader(XMLHelper xmlHelper) {
	xmlHelper.append("<?xml version=\"" + xmlVersion + "\" encoding=\"" + encoding + "\" ?>");
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 18:24:28  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/03 15:14:29  jayannah
 * HISTORY      : changed the package names
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/03 14:59:57  jayannah
 * HISTORY      : Created the files
 * HISTORY      :
 */

