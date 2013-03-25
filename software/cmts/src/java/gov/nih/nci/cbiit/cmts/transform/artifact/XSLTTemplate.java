/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

import org.jdom.Namespace;



public class XSLTTemplate extends XSLTElement {

	private String templatename;
	private String match;
	private String mode;
	private String priority;
	public XSLTTemplate()
	{
		this("template");
	}
	public XSLTTemplate(String name)
	{
		this(name, XSLTStylesheet.xsltNamespace);
	}
	public XSLTTemplate(String name, Namespace space)
	{
		super(name, space);
	}
	
	/**
	 * Return value of an optional attribute:name
	 * @return
	 */
	public String getTemplatename() {
		return templatename;
	}
	/**
	 * Set an optional attribute that names this template. 
	 * Named templates are invoked with the <xsl:call-template> element
	 * @param pName
	 */
	public void setTemplatename(String pName) {
		this.templatename = pName;
		setAttribute("name", pName);
		
	}
	/**
	 * Return value of an optional attribute:name
	 * @return
	 */
	public String getMatch() {
		return match;
	}
	/**
	 * Set an optional attribute that defines a pattern of elements for which
	 * this template should be invoked. 
	 * Named templates are invoked with the <xsl:call-template> element
	 * @param pName
	 */
	public void setMatch(String pMatch) {
		this.match = pMatch;
		setAttribute("match", pMatch);
	}
	/**
	 * Return value of an optional attribute:mode
	 * @return
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * Set an optional attribute that defines a mode for this template. 
	 * A mode is a convenient syntax that allows you to write specific templates for 
	 * specific purposes.
	 * @param pName
	 */
	public void setMode(String pMode) {
		mode = pMode;
		setAttribute("name", pMode);
	}
	/**
	 * Return value of an optional attribute:name
	 * @return
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * Set an optional attribute that assigns a numeric priority to this template. 
	 * The value can be any numeric value except infinity.
	 * @param pName
	 */
	public void setPriority(String pPriority) {
		priority = pPriority;
		setAttribute("priority", pPriority);
	}

}
