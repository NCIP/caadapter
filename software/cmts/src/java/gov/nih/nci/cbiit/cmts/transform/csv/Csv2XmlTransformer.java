/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.xquery.XQException;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CsvReader;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;


public class Csv2XmlTransformer extends MappingTransformer {

	public Csv2XmlTransformer() throws XQException {
		super();
		this.setTemporaryFileCreated(true);
	}

	@Override
	protected String parseRawData(String sourceRawDataFile, Mapping map) throws JAXBException, IOException{
		//parse csv data 
		String xsdFile="";
    	String xsdRoot="";
		for (gov.nih.nci.cbiit.cmts.core.Component comp:map.getComponents().getComponent())
		{
			if (comp.getType().equals(ComponentType.SOURCE))
			{
				xsdFile=comp.getLocation();
				xsdRoot=comp.getRootElement().getName();
			}	
		}
    	
		XSDParser p = new XSDParser();
		p.loadSchema(xsdFile, null);
		ElementMeta element = p.getElementMeta(null, xsdRoot);
		CsvXsd2MetadataConverter converter=new CsvXsd2MetadataConverter(element);
		CSVMeta csvMeta= converter.getCSVMeta();
		
    	String tempXmlSrc=null;
		InputStream  sourceDataStream = new FileInputStream(sourceRawDataFile);
		CsvReader reader = new CsvReader(sourceDataStream, csvMeta);
		
		while(reader.hasMoreRecord())
		{
			CSVDataResult nextResult;
			try {
				nextResult = reader.getNextRecord();
				CsvData2XmlConverter xmlConverter=new CsvData2XmlConverter(nextResult);
				tempXmlSrc=xmlConverter.writeXml2File(null);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tempXmlSrc;
	}
}
