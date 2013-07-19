/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;


import gov.nih.nci.cbiit.cmts.common.ApplicationResult;


import java.io.*;
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

        String sourceData = null;
        String xqlFile = null;
        String outFile = null;
        boolean display = false;
        if (args.length<2)
		{
			System.out.println("XQueryTransformer.main()...\nusage:sourcedata:stylesheet");
			System.exit(0);
		}
        else if (args.length<3)
		{
            sourceData = args[0];
            xqlFile = args[1];
            outFile = "result_out.xml";
            display = true;
		}
        else
        {
            sourceData = args[0];
            xqlFile = args[1];
            outFile = args[2];
        }

        System.out.println("XQueryTransformer.main()...Source Data:"+sourceData);
		System.out.println("XQueryTransformer.main()...Mapping Data:"+xqlFile);
		System.out.println("XQueryTransformer.main()...Result Data:"+outFile);
		try {
			XQueryTransformer transformer = new XQueryTransformer();
			try {
				FileWriter sWriter = new FileWriter(new File(outFile));
				sWriter.write(transformer.transfer(sourceData, xqlFile));
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

        if (display)
        {
            String outT = gov.nih.nci.caadapter.common.util.FileUtil.readFileIntoString(outFile);
            File f = new File(outFile);
            f.delete();
            if ((outT == null)||(outT.trim().equals(""))) System.out.println("NULL Result...");
            else System.out.println(outT);
        }
    }
	protected XQPreparedExpression prepareXQExpression(String instruction) throws XQException, JAXBException
	{
		//InputStream in;
        String xql = gov.nih.nci.caadapter.common.util.FileUtil.readFileIntoString(instruction);
        if (xql == null) throw new XQException("XQuery File Reading Failure : file=" + instruction);
        if (xql.trim().equals("")) throw new XQException("This XQuery File is empty. : file=" + instruction);

        String mm = null;

        try
        {
            mm = modifyMappingInstForWebFunction(xql, false);
        }
        catch(Exception ee)
        {
            throw new XQException(ee.getMessage());
        }

        try {
			//in = new FileInputStream(new File(instruction));
			//InputStreamReader inputStream=new InputStreamReader(in);
			//XQPreparedExpression exp = getConn().prepareExpression(inputStream);
            XQPreparedExpression exp = getConn().prepareExpression(new CharArrayReader(mm.toCharArray()));
            return exp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            throw new XQException("Error on XQPreparedExpression (file="+instruction+"): " + e.getMessage());
        }
		//return null;

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

