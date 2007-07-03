/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/v2v3/XMLHelper.java,v 1.1 2007-07-03 18:24:28 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 3.2
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear.
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.v2v3;

/**
 * This class provides creates a XML document with the basic method implementations
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 18:24:28 $
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
 * HISTORY      : Revision 1.2  2006/10/03 15:14:29  jayannah
 * HISTORY      : changed the package names
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/03 14:59:57  jayannah
 * HISTORY      : Created the files
 * HISTORY      :
 */

