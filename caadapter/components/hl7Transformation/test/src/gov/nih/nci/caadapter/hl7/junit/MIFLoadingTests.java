/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFToXmlExporter;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import junit.framework.*;

/**
 * The class will test the following features of the MIF Parser.
 * 1. Load Hl7 V3 specification from a .h3s file
 * 2. MIF association parser
 * 3. MIF commonModelElement parser
 * 4. MIF choice parser
 * 5. MIF composite datatype parser
 * 
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2007-09-19 13:56:10 $
 */

public class MIFLoadingTests extends TestCase {


	 public void testExportHL7V3Specification () {
		 String outFileName="mifClass.xml";
//		 String specFileName="C:\\CVS\\caadapter\\workingspace\\examples\\xmlpathSpec\\coct_mt010000.h3s";
//		 String specFileName="mifClassImportReExport.h3s";
		 String specFileName="C:/Documents and Settings/wangeug/My Documents/caAdapter/test0822/COCT_MT010000_Simple.h3s";
		 FileInputStream fis;
		 MIFClass rootMif = null;
		try {
			long tBegin=System.currentTimeMillis();
			File specFile=new File(specFileName);
			fis = new FileInputStream (specFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
        	rootMif = (MIFClass)ois.readObject();
    		ois.close();
    		fis.close();
    		System.out
					.println("Hl7V3SpecificationTests.testExportHL7V3Specification()..readMif time:"+(System.currentTimeMillis()-tBegin));
		} catch (FileNotFoundException e) {
			Log.logException(this, e);
		} catch (IOException e) {
			Log.logException(this, e);
		} catch (ClassNotFoundException e) {
			Log.logException(this, e);
		}
			
		MIFToXmlExporter xmlExporter;
		try {
			xmlExporter = new MIFToXmlExporter(rootMif);
			xmlExporter.exportToFile(outFileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
//		 attribute = (Attribute)attributes.get("nullFlavor");
//		 assertNotNull(attribute);

	 }
	 
 public void testImportHL7V3Specification () 
 {
		String specFileName="mifClassExport.xml";
//			 String specFileName="C:\\CVS\\caadapter\\workingspace\\examples\\xmlpathSpec\\newCOCT_MT150003.h3s";//PORRT_MT040011.h3s";//newCOCT_MT150003.h3s";
		XmlToMIFImporter mifImport=new XmlToMIFImporter();
		MIFClass rootMif = null;
		File specFile=new File(specFileName);
		long impBegintime = System.currentTimeMillis();
		rootMif=mifImport.importMifFromXml(specFile);
		long impTime=System.currentTimeMillis();
		OutputStream os;
		try {
			os = new FileOutputStream("mifClassImportReExport.h3s");
			ObjectOutputStream oos = new ObjectOutputStream(os); 
			oos.writeObject(rootMif);
			oos.close();
			os.close();
			long impWriteTime=System.currentTimeMillis();
			System.out
					.println("Hl7V3SpecificationTests.testImportHL7V3Specification()..importMIFXmlTime:"+(impTime-impBegintime));
			System.out
			.println("Hl7V3SpecificationTests.testImportHL7V3Specification()..writeMIFTime:"+(impWriteTime-impBegintime));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 }
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(MIFLoadingTests.class);    
		}
}
