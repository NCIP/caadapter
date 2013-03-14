/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class XSLTErrorListener implements ErrorListener {

	@Override
	public void error(TransformerException e) throws TransformerException 
	{
		show("Error",e);
		throw(e);

	}

	@Override
	public void fatalError(TransformerException e)
			throws TransformerException 
	{
		show("Fatal ",e);
		throw(e);
	}

	@Override
	public void warning(TransformerException e) throws TransformerException 
	{
		show("Warning",e);
		throw(e);
	}
	
	private void show(String type,TransformerException e) 
	{
		System.out.println(type + ": " + e.getMessage());
		if(e.getLocationAsString() != null)
			System.out.println(e.getLocationAsString());
	}
}
