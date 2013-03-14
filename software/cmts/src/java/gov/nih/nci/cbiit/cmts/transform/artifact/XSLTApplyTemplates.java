/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

public class XSLTApplyTemplates extends XSLTElement{

	private String mode;
	private String select;
	public XSLTApplyTemplates()
	{
		super("apply-templates");
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
	 * Return an XPath expression that select the nodes to which templates should be applied.
	 * @return select
	 **/
	public String getSelect() {
		return select;
	}
	/**
	 * Set an optional attribute that contains an XPath expression that select the nodes to which templates should be applied.
	 * @param select
	 */
	public void setSelect(String pSelect) {
		select = pSelect;
		setAttribute("select", pSelect);
	}
			
}
