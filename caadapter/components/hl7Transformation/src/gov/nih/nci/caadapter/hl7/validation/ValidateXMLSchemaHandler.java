/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.validation;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import gov.nih.nci.caadapter.common.Log;

class ValidateXMLSchemaHandler extends DefaultHandler
{
	private StringBuffer errorsStringBuffer = null;
    private Locator locator_;
    private int errorCount = 0;
    private boolean isValid = true;

    public ValidateXMLSchemaHandler(StringBuffer errorBuffer) throws Exception
    {
    	errorsStringBuffer=errorBuffer;
    	if(errorsStringBuffer==null)
    	{
    		errorsStringBuffer=new StringBuffer();
    	}
    }

    public void setDocumentLocator(Locator locator)
    {
        locator_ = locator;
    }

    public void endDocument()
    {
    	Log.logInfo(this, "  " + errorCount + " error(s) found");
    }

    //Receive notification of a recoverable error.
    public void error(SAXParseException se) throws SAXException
    {
        ++errorCount;
        setValidity(se, "INVALID Recoverable Error:");
    }

    //Receive notification of a non-recoverable error.
    public void fatalError(SAXParseException se) throws SAXException
    {
        ++errorCount;
        setValidity(se, "Fatal Error:");
    }

    //Receive notification of a warning.
    public void warning(SAXParseException se) throws SAXException
    {
        setValidity(se, "Warning !");
    }

    private void setValidity(SAXParseException se, String errortype) throws SAXException
    {
        isValid = false;
        String saxerror = se.toString();
        errorsStringBuffer.append(errorWriter(saxerror, errortype)).append("\n");
    }

    private String errorWriter(String errorstring, String errortype)
    {
        StringBuffer e = new StringBuffer();
        e.append(errortype);

        if (locator_ != null)
        {
            e.append(" " + locator_.getSystemId() + ", " +
                "line:"  + locator_.getLineNumber() + " cloumn:" + locator_.getColumnNumber());
        }
        e.append("  " + errorstring);
        return e.toString();
    }

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}

