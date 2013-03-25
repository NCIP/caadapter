/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

public class XSLTCallTemplate extends XSLTElement{
	private String calledTemplate; 
	public XSLTCallTemplate()
	{
		super("call-template");
	}
	
	/**
	 * Return the name of the template to be invoked
	 * @return
	 */
	public String getCalledTemplate() {
		return calledTemplate;
	}
	/**
	 * Set the mandatory attribute which is name of the template to be invoked
	 * @param calledTemplate
	 */
	public void setCalledTemplate(String calledTemplate) {
		this.calledTemplate = calledTemplate;
		setAttribute("name", calledTemplate);
	}
	
}
