/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.validation;
/**
 * This class defines XML schema handler validator
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 * revision    $Revision: 1.3 $
 * date        $Date: 2008-09-29 15:37:31 $
 */
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
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */
