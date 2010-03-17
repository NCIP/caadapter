package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;

import java.util.LinkedHashMap;

public class Iso21090Util {
	
	public static ObjectMetadata resolveAttributeDatatype(LinkedHashMap metaHash, String typeName)
	{
		System.out.println("Iso21090Util.resolveAttributeDatatype()..search type:"+typeName);
		for (Object dtKey:metaHash.keySet())
		{
			Object dtMeta=metaHash.get(dtKey);
			if (dtMeta instanceof ObjectMetadata)
			{
				ObjectMetadata dtObjectMeta=(ObjectMetadata)dtMeta;
//				System.out.println("Iso21090Util.resolveAttributeDatatype()..objectName:"+dtObjectMeta.getName());
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
