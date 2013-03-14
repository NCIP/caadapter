/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.validation;

import gov.nih.nci.cbiit.cmts.common.ApplicationMessage;
import gov.nih.nci.cbiit.cmts.common.ApplicationResult;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XsdSchemaErrorHandler extends DefaultHandler {
	
	private List<ApplicationResult> errorMessage;

	public XsdSchemaErrorHandler()
	{
		super();
		errorMessage=new ArrayList<ApplicationResult>();
	}
	
	public void error(SAXParseException e) 
	{
		ApplicationMessage message=formatMessage(e);
		errorMessage.add(new ApplicationResult(ApplicationResult.Level.ERROR, message));
	}
	public void fatalError(SAXParseException e) 
	{
		ApplicationMessage message=formatMessage(e);
		errorMessage.add(new ApplicationResult(ApplicationResult.Level.FATAL, message));

	}
	
	public void warning(SAXParseException e) 
	{
		ApplicationMessage message=formatMessage(e);
		errorMessage.add(new ApplicationResult(ApplicationResult.Level.WARNING, message));
	}
	 
	private ApplicationMessage formatMessage( SAXParseException e)
	{
		String msg;
		msg="Line:"+e.getLineNumber()+"...Column:"+e.getColumnNumber()+"...:"+e.getMessage();

		return new ApplicationMessage(msg);
	}

	public List<ApplicationResult> getErrorMessage() {
		return errorMessage;
	}
}
