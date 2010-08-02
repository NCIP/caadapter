package gov.nih.nci.cbiit.cmts.transform.hl7v2;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.xquery.XQException;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.transform.XQueryTransformer;

public class Hl7v2XmlTransformer extends XQueryTransformer {

	public Hl7v2XmlTransformer() throws XQException {
		super();
		// TODO Auto-generated constructor stub
		setTemporaryFileCreated(true);
		System.out.println("Hl7v2XmlTransformer.Hl7v2XmlTransformer()");
	}
	
	@Override
	protected String parseRawData(String sourceRawDataFile, Mapping map) throws JAXBException, IOException, ApplicationException
	{
		return sourceRawDataFile;
	}

}
