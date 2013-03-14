/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

import org.jdom.Element;
import org.jdom.Namespace;

public class XSLTElement extends Element {

	public XSLTElement()
	{
		this("");
	}
	public XSLTElement(String name)
	{
		this(name, XSLTStylesheet.xsltNamespace);
	}
	public XSLTElement(String name, Namespace space)
	{
		super(name, space);
	}
}
