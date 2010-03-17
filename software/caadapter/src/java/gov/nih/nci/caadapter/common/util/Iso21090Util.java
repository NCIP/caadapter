package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;

import java.util.LinkedHashMap;

public class Iso21090Util {
	/**
	 * If the attribute of an object is an ISO 21090 data type, find it and clone it
	 * @param metaHash
	 * @param typeName
	 * @return
	 */
	public static ObjectMetadata resolveAttributeDatatype(LinkedHashMap metaHash, String typeName)
	{
		for (Object dtKey:metaHash.keySet())
		{
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
