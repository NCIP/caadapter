package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Iso21090Util {
	
	private static Hashtable<String, List<ObjectMetadata>> sequenceDatatypes=new Hashtable<String,  List<ObjectMetadata>>();
	private static Hashtable<String, StringTokenizer> sequenceDataypeKeys=new Hashtable<String, StringTokenizer>();
	private static String AD_SEQUENCE_EXP_NAME="ADXP";
	private static String EN_SEQUENCE_EXP_NAME="ENXP";
	public static String ISO21090_DATAYPE_PACKAGE="gov.nih.nci.iso21090";
	private static final int ENXP_COUNT=5;
	private static final int ADXP_COUNT=28;
	/**
	 * Hard code: List all possible sub-class type if a sequence of data types is refered
	 * ADEXP -- sequence of AD type
	 * ENEXP -- sequence of EN types
	 */
	static {
//		String SEQUENCE_ADXP_DATA_TYPES="ADXP.ADL;ADXP.AL;ADXP.BNN;ADXP.BNR;ADXP.BNS;ADXP.BR;ADXP.CAR;ADXP.CEN;"+
//		"ADXP.CNT;ADXP.CPA;ADXP.CTY;ADXP.DAL;ADXP.DINST;ADXP.DINSTA;ADXP.DINSTQ;ADXP.DIR;ADXP.DMOD;ADXP.DMODID;ADXP.INT;ADXP.POB;ADXP.PRE;"+
//		"ADXP.SAL;ADXP.STA;ADXP.STB;ADXP.STR;ADXP.STTYP;ADXP.UNID;ADXP.UNIT;ADXP.ZIP";
		String SEQUENCE_ADXP_DATA_TYPES="ADXP";
		sequenceDataypeKeys.put(AD_SEQUENCE_EXP_NAME, new StringTokenizer(SEQUENCE_ADXP_DATA_TYPES,";"));
//		String SEQUENCE_EN_DATA_TYPES="EN.ON;EN.PN;EN.TN";
		String SEQUENCE_EN_DATA_TYPES="ENXP";
		sequenceDataypeKeys.put(EN_SEQUENCE_EXP_NAME, new StringTokenizer(SEQUENCE_EN_DATA_TYPES,";"));
	}
	
	public  static int findSequenceSize(String typeName)
	{
		if (typeName.contains("ADXP"))
			return ADXP_COUNT;
		return ENXP_COUNT;
		
	}
	/**
	 * Find the list of data types when a sequence is required
	 * @param metaHash MetaData hash of a UML model
	 * @param sequenceKey key of the searching sequence
	 * @return
	 */
	public static List<ObjectMetadata> findSequenceDatatypes(String sequenceKey)
	{
		String upcaseKey=sequenceKey.trim().toUpperCase();
		if (!upcaseKey.startsWith("SEQUENCE"))
			return null;
		if (upcaseKey.indexOf("(")<0)
			return null;
		if (upcaseKey.indexOf(")")<0)
			return null;
		if (upcaseKey.contains(AD_SEQUENCE_EXP_NAME))
			return retrieveSequenceDatatypes(AD_SEQUENCE_EXP_NAME);
		else if (upcaseKey.contains(EN_SEQUENCE_EXP_NAME))
			return retrieveSequenceDatatypes(EN_SEQUENCE_EXP_NAME);
		return null;
	}
	
	private static List<ObjectMetadata> retrieveSequenceDatatypes(String key)
	{
		List<ObjectMetadata> datatypeList=sequenceDatatypes.get(key);
		if (datatypeList==null)
		{
			datatypeList=new ArrayList<ObjectMetadata>();
			StringTokenizer dtKeyToken=sequenceDataypeKeys.get(key);
			while(dtKeyToken.hasMoreElements())
			{
				String dtName=dtKeyToken.nextToken();
				ObjectMetadata seqChild=resolveAttributeDatatype(dtName);
				if (seqChild!=null)
					datatypeList.add(seqChild);
			}
			sequenceDatatypes.put(key, datatypeList);
		}
		return datatypeList;
	}
	/**
	 * If the attribute of an object is an ISO 21090 data type, find it and clone it
	 * @param metaHash
	 * @param typeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ObjectMetadata resolveAttributeDatatype(String typeName)
	{
		ModelMetadata myModel = CumulativeMappingGenerator.getInstance().getMetaModel();
		LinkedHashMap metaHash = myModel.getModelMetadata();
		for (String dtKey:(Set<String>)metaHash.keySet())
		{
			if (dtKey.indexOf(ISO21090_DATAYPE_PACKAGE)<0)
				continue;
			Object dtMeta=metaHash.get(dtKey);
			if (dtMeta instanceof ObjectMetadata)
			{
				ObjectMetadata dtObjectMeta=(ObjectMetadata)dtMeta;
				if (dtObjectMeta.getName().equals(typeName))
					try {
						return (ObjectMetadata)dtObjectMeta.clone(false);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		return null;
	}

}
