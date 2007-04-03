package gov.nih.nci.caadapter.sdtm;

import java.util.ArrayList;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.1 $
 */
public class SDTMMappingGenerator
{
	public ArrayList<String> results = null;

	int counter;

	String scsSDTMFile;

	String scsDefineXMLFIle;


	public static SDTMMappingGenerator _sdtmMappingGeneratorReference;
	// new ArrayList();
	public SDTMMappingGenerator() {
		results = new ArrayList<String>();
		counter = 0;
	}

	public void removeObject(String source, String target){
		results.remove(source+"~"+target);
	}
	public boolean put(String source, String target) throws Exception
	{
		
		try {
			results.add(counter++, source + "~" + target);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			results.add(source + "~" + target);
		}
		return true;
	}

	public String getScsSDTMFile()
	{
		return scsSDTMFile;
	}

	public void setScsSDTMFile(String scsSDTMFile)
	{
		this.scsSDTMFile = scsSDTMFile;
	}

	public String getScsDefineXMLFIle()
	{
		return scsDefineXMLFIle;
	}

	public void setScsDefineXMLFIle(String scsDefineXMLFIle)
	{
		this.scsDefineXMLFIle = scsDefineXMLFIle;
	}

	public static SDTMMappingGenerator get_sdtmMappingGeneratorReference()
	{
		return _sdtmMappingGeneratorReference;
	}

	public void set_sdtmMappingGeneratorReference(SDTMMappingGenerator mappingGeneratorReference)
	{
		_sdtmMappingGeneratorReference = mappingGeneratorReference;
	}
	


}
