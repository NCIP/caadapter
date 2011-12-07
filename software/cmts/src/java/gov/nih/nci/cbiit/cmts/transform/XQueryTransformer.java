/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;


import gov.nih.nci.cbiit.cmts.common.ApplicationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.bind.JAXBException;

public class XQueryTransformer extends MappingTransformer {
	public XQueryTransformer() throws XQException {
		super();
	}
	public static void main(String args[]){
		if (args.length<2)
		{
			System.out.println("XQueryTransformer.main()...\nusage:sourcedata:stylesheet");
			System.exit(0);
		} else if (args.length<3)
		{
			args[2]="result_out.xml";
		}
		System.out.println("XQueryTransformer.main()...Source Data:"+args[0]);
		System.out.println("XQueryTransformer.main()...Mapping Data:"+args[1]);
		System.out.println("XQueryTransformer.main()...Result Data:"+args[2]);
		try {
			XQueryTransformer transformer = new XQueryTransformer();
			try {
				FileWriter sWriter = new FileWriter(new File(args[2]));
				sWriter.write(transformer.transfer(args[0],args[1]));
				sWriter.flush();
				sWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	protected XQPreparedExpression prepareXQExpression(String instruction) throws XQException, JAXBException
	{
		InputStream in;
		try {
			in = new FileInputStream(new File(instruction));
			InputStreamReader inputStream=new InputStreamReader(in);	
			XQPreparedExpression exp = getConn().prepareExpression(inputStream);
			return exp;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public List<ApplicationResult> validateXmlData(Object validator,
			String xmlData) {
		// TODO Auto-generated method stub
		System.out.println("XQueryTransformer.validateXmlData()");
		return null;
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $ HISTORY: Revision 1.2 2008/10/20
 * 20:46:15 linc HISTORY: updated. HISTORY: HISTORY: Revision 1.1 2008/10/01
 * 18:59:13 linc HISTORY: updated. HISTORY:
 */

