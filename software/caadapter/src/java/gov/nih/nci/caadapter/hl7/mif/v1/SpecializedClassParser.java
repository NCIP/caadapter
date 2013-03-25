/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a specializedClass section  from the mif XML file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.7 $ date $Date: 2009-11-11 20:26:47 $
 */
public class SpecializedClassParser {
	/**
	 * Parse a <specializedClass> or a <participantClass
	 *  <P>
	 *  <ul>
	 *  	<li><node is <participantClass>:AssociationParser
	 *  	<li>node is <specializedClass> within <ownedEntryPoint>:MIFParser
	 *  	<li>node is <specializedClass> within <specializedChild>:ClassParser
	 *  </ul>
	 *  <P>
	 * @param node The XML node to be parsed
	 * @param prefix The possible prefix in the nodeName
	 * @param participantTraversalNames Hash of traversal names which to refer the current MIFClass or its child MIFClass
	 * @return MIFClass parsed with given XML node
	 */
	public MIFClass parseSpecializedClass(Node node,String prefix, Hashtable<String,String> participantTraversalNames) {

        Node child = node.getFirstChild();
        String annotation = null;
        while (child != null)
        {
            MIFClass mifClass = null;
            if (child.getNodeName().equals(prefix+"class")
        			||child.getNodeName().equals("class")) {
        		ClassParser classParser = new ClassParser();
                mifClass = classParser.parseClass(child, prefix,participantTraversalNames);
                //return classParser.parseClass(child, prefix,participantTraversalNames);
        	}
        	if (child.getNodeName().equals(prefix+"commonModelElementRef")
        			||child.getNodeName().equals("commonModelElementRef")) {
        		CMETRefParser cmetRefParser = new CMETRefParser();
        		mifClass = cmetRefParser.parseCMetRef(child, prefix,false);
                //return cmetRefParser.parseCMetRef(child, prefix,false);
            }
        	/*
        	 * The following is not part of the spec but appears in MIF files anyway. The reference
        	 * is a link to a class of the same CMET. (Personal observation)
        	 */
        	if (child.getNodeName().equals(prefix+"reference")
        			||child.getNodeName().equals("reference")) {
        		CMETRefParser cmetRefParser = new CMETRefParser();
        		mifClass = cmetRefParser.parseCMetRef(child, prefix, true);
                //return cmetRefParser.parseCMetRef(child, prefix, true);
            }
        	//templateparameter will be ignore in this release
        	/*
        	 * To Do
        	 */
        	if (child.getNodeName().equals(prefix+"templateParameter")) {
        		mifClass = new MIFClass();
        		mifClass.setName(XSDParserUtil.getAttribute(child, "name"));
        		mifClass.setDynamic(true);
        		//return mifClass;
        	}
            if (child.getNodeName().equals(prefix+"annotation")||child.getNodeName().equals("annotation")||
                child.getNodeName().equals(prefix+"annotations")||child.getNodeName().equals("annotations"))
            {
                annotation = MIFParserUtil.searchAnnotation(child);
            }
            if (mifClass != null)
            {
//&umkis                if (annotation != null) mifClass.setAnnotation(annotation);
                return mifClass;
            }
            child = child.getNextSibling();
        }
        return null;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.6  2008/12/11 17:05:37  wangeug
 * HISTORY :MIF Parsing: A item of a choice is a list of other MIFClass.
 * HISTORY :
 * HISTORY :Revision 1.5  2008/09/29 15:42:45  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */