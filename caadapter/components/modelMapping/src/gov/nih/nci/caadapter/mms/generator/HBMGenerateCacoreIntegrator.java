package gov.nih.nci.caadapter.mms.generator;

import java.util.Collection;
import java.util.Properties;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.metadata.XmiModelMetadata;
import gov.nih.nci.codegen.GenerationException;
import gov.nih.nci.codegen.handler.FileHandler;
import gov.nih.nci.codegen.transformer.jet.HibernateMappingTransformer;
import gov.nih.nci.codegen.util.ObjectFactory;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;

public class HBMGenerateCacoreIntegrator {
	public static String GENERATOR_CONFIG="conf/CodegenConfig.xml";//CodegenConfig.xml";
	private HibernateMappingTransformer transformer;
	private static HBMGenerateCacoreIntegrator generator;
	/**
	 * Impplemnt HBMGenerateCacoreIntegrator with factory design pattern
	 * @return HBMGenerateCacoreIntegrator instance
	 */
	public static HBMGenerateCacoreIntegrator getInstance()
	{
		if (generator==null)
		{
			generator=new HBMGenerateCacoreIntegrator();
			generator.init(GENERATOR_CONFIG);
		}
		return generator;
	}

	private void init(String configFile)
	{
		ObjectFactory.initialize(configFile);
		try {
			transformer=(HibernateMappingTransformer)ObjectFactory.getObject("HibernateMappingTransformer");
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generate HBM mapping given the UML object/data model
	 * @param model The Object/Data model created with Enterprise Architecture following caCORE SDK convention
	 * @param outDir The root directory of output files
	 * @throws GenerationException
	 */
	public void generateMapping(UMLModel model, String outDir) throws GenerationException
	{
		Properties umlProp=(Properties)ObjectFactory.getObject("UMLModelFileProperties");
		Log.logInfo(this,"Generate Hibernate mapping... Include Package... default setting:"+ umlProp.getProperty("Include Package"));
		//found the root package name of the "Logical Model"
		UMLPackage logicalPck=model.getPackage("Logical View").getPackage("Logical Model");
		Collection <UMLPackage> modelPcks=logicalPck.getPackages();
		String rootPckName="";
		for (UMLPackage onePck :modelPcks)
		{
			String pckName=onePck.getName();
			if (!(pckName==null||pckName.equals("")||pckName.equals("java")||pckName.equals("Diagrams")))
				rootPckName=pckName;
		}
		if (!rootPckName.equals(""))
			umlProp.setProperty("Include Package", rootPckName);
		Log.logInfo(this,"Generate Hibernate mapping...  included package:"+ umlProp.getProperty("Include Package"));

		//set output directory
		FileHandler fileHandler=(FileHandler)ObjectFactory.getObject("HibernateMappingFilehandler");
		Log.logInfo(this,"Generate Hibernate mapping... Output Directory... default setting:"+ fileHandler.getOutputDir());
		if (outDir!=null&&!outDir.equals(""))
			fileHandler.setOutputDir(outDir);

		Log.logInfo(this,"Generate Hibernate mapping... Output Directory:"+fileHandler.getOutputDir());
		transformer.execute(model);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/**
		//initialize code generator
		String fileName = (args!=null && args.length >0) ? args[0] : "CodegenConfig.xml";
		ObjectFactory.initialize(fileName);
		//manipulate fileHandler
		FileHandler fileHandler=(FileHandler)ObjectFactory.getObject("HibernateMappingFilehandler");
		fileHandler.setOutputDir("new_output");
		System.out.println("CodegenTest.main()..output dir:"+fileHandler.getOutputDir());

		//manipulate umlProp
//		Properties umlProp=(Properties)ObjectFactory.getObject("UMLModelFileProperties");
//		Enumeration umlKeys=umlProp.propertyNames();//.keys();
//		umlProp.setProperty("Include Package", "com");
//
//		while(umlKeys.hasMoreElements())
//		{
//			String propKey=(String)umlKeys.nextElement();
//			String propValue=(String)umlProp.getProperty(propKey);
//			System.out.println("CodegenTest.main()..UML property:"+propKey +"="+propValue);
//		}
//
		HibernateMappingTransformer transformer=(HibernateMappingTransformer)ObjectFactory.getObject("HibernateMappingTransformer");
//		String xmiFilePath="C:\\myProject\\caCORE40Src\\models\\sdk.xmi";
		XmiModelMetadata xmlModelMeta=new XmiModelMetadata(xmiFilePath);
		UMLModel model=xmlModelMeta.getHandler().getModel();
		generator.generateMapping(model);
		transformer.execute(model);
		**/
		HBMGenerateCacoreIntegrator generator= HBMGenerateCacoreIntegrator.getInstance();
		String xmiFilePath="C:\\caAdapter\\caAdapter4.0\\binDownload\\workingspace\\Object_to_DB_Example\\sdk.xmi";
		XmiModelMetadata xmlModelMeta=new XmiModelMetadata(xmiFilePath);
		UMLModel model=xmlModelMeta.getHandler().getModel();
		generator.generateMapping(model,"generatorOut");
	}

}
