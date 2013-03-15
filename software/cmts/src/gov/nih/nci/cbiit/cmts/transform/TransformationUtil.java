/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

public class TransformationUtil {
	public static String XML_VERSION="<?xml version=\"1.0\"";
	public static String XML_ENCODING_ISO_8859_1=" encoding=\"ISO-8859-1\"";
	public static String XML_ENCODING_UTF_8     =" encoding=\"UTF-8\"";
	public static String XML_ENCODING_UTF_16    =" encoding=\"UTF-16\"";

	public static String formatXqueryResult(String sIn)
	{
		StringBuffer rtnSb=new StringBuffer();
		rtnSb.append(XML_VERSION+"?>\n");
		StringReader sr=new StringReader(sIn);
		LineNumberReader lR=new LineNumberReader(sr);
		String line;
		try {
			while ((line=lR.readLine())!=null)
			{
				if (line.trim().length()>0)
					rtnSb.append(line+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtnSb.toString();
	}
}
