/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;

import java.io.File;
import java.util.StringTokenizer;

public class XsdUtil {

    /**
     * Returns a string which is the URI of a file.
     * <ul>
     * <li>file:///DOSpath</li>
     * <li>file://UnixPath</li>
     * </ul>
     * No validation is done to check whether the file exists or not. This
     * method will be no longer used when the JDK URL.toString() is fixed.
     *
     * @param path The absolute path of the file.
     * @return A string representing the URI of the file.
     * @author LAST UPDATE $Author: phadkes $
     * @since      caAdapter  v4.2
     * @version    $Revision: 1.3 $
     * @date       $Date: 2008-09-25 19:30:39 $
     */
    public static String toURIRepresentation(final String path) {
        String result = path;
        if (!new File(result).isAbsolute()) {
           throw new IllegalArgumentException("The parameter must represent an absolute path.");
        }

        if (File.separatorChar != '/') {
            result = result.replace(File.separatorChar, '/');
        }

        if (result.startsWith("/")) {
            result = "file://" + result;    /*Unix platform*/
        } else {
            result = "file:///" + result;   /*DOS platform*/
        }

        return result;
    }

    public static String parsePackageNameFromURI(String nsURI)
    {
    	if (nsURI.indexOf("//")<0)
    		return null;
    	String pkContent=nsURI.substring(nsURI.indexOf("//")+2);
    	if (pkContent.indexOf("/")<0)
    		return reversePackageName(pkContent);
    	String pkName=pkContent.substring(0, pkContent.indexOf("/"));
    	return reversePackageName(pkName);
    }

    private static String reversePackageName(String uriName)
    {
    	if (uriName.indexOf(".")<0)
    		return uriName;

    	StringTokenizer tokens=new StringTokenizer(uriName,".");
    	StringBuffer sb=new StringBuffer();
    	while(tokens.hasMoreTokens())
    	{
    		String tokenValue=tokens.nextToken();
    		sb.insert(0,"."+tokenValue);
    	}
    	//remove the first "."
    	return sb.toString().substring(1);

    }

    public static String writeXsdObjectString(ObjectMetadata xsdObj)
    {
    	StringBuffer sb=new StringBuffer(xsdObj.getXPath());
    	if (!xsdObj.getAttributes().isEmpty())
    	{
    		sb.append("\n\tAttributeMapping:");
    		for(AttributeMetadata attr:xsdObj.getAttributes())
    			sb.append("\n\t\t"+attr.getXPath()+":"+attr.getDatatype());
    	}
    	if (!xsdObj.getAssociations().isEmpty())
    	{
    		sb.append("\n\tAssociationMapping:");
    		for(AssociationMetadata assc:xsdObj.getAssociations())
    			sb.append("\n\t\t"+assc.getXPath()+":"+assc);
    	}
    	return sb.toString();
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
