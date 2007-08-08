package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;
/**
 * This class resolve the reference defined within a MIF file
 * @author wangeug
 *
 */
public class MIFReferenceResolver {
private static Hashtable<String, MIFClass> classReferences=new Hashtable<String, MIFClass>();;
private static String messageType;
/**
 * Publically accessed to resolve reference for a MIFClass
 * @param mifClass
 */
public static void getReferenceResolved(MIFClass mifClass, Object sender)
{
	//clear the reference hashtable for a new MIFClass
	classReferences.clear();
	messageType=mifClass.getMessageType();
	resolveReference(mifClass,sender);
}
/**
 * Resolve reference of a MIFClass recursively
 * @param mifClass
 */
private static void resolveReference(MIFClass mifClass, Object sender)
{
	TreeSet<MIFAssociation> mifAsscs=mifClass.getSortedAssociations();
	if (mifAsscs!=null
		&&!mifAsscs.isEmpty())
	{
		for(MIFAssociation assc:mifAsscs)
		{
			 MIFClass asscMifClass=assc.getMifClass();
			 if(asscMifClass.getReferenceName().equals(""))
			 {
				 classReferences.put(asscMifClass.getName(), asscMifClass);
				 resolveReference(asscMifClass,sender);
			 }
			 else 
			 {
				//it is a local refereence
				 CMETRef cmetRef = CMETUtil.getCMET(asscMifClass.getReferenceName());
				 if (cmetRef==null)
				 {
					 MIFClass referedClass=classReferences.get(asscMifClass.getName());
					 if (referedClass!=null)
						 assc.setMifClass((MIFClass)referedClass.clone());
					 else
						 Log.logError(sender,"Reference is not resolved..className:"+asscMifClass.getName() +"..messageType:"+messageType);
				}
				else
					Log.logInfo(sender,"CMET Reference will be resolved later..className:"+asscMifClass.getName()+"..CMET Reference:"+asscMifClass.getReferenceName());
			 }
			 
		}
	}
}
}
