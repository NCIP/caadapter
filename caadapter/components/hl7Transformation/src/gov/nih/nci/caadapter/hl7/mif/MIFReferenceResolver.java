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
private Hashtable<String, MIFClass> classReferences=new Hashtable<String, MIFClass>();;
private String messageType;
/**
 * Publically accessed to resolve reference for a MIFClass
 * @param mifClass
 */
public void getReferenceResolved(MIFClass mifClass)
{
	//clear the reference hashtable for a new MIFClass
	classReferences.clear();
	messageType=mifClass.getMessageType();
	resolveReference(mifClass,this);
}
/**
 * Resolve reference of a MIFClass recursively
 * @param mifClass
 */
private  void resolveReference(MIFClass mifClass, Object sender)
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
//				 System.out.println("MIFReferenceResolver.resolveReference()..Association add to reference:"+messageType+"-"+asscMifClass.getName());
				 classReferences.put(asscMifClass.getName(), asscMifClass);
				 resolveReference(asscMifClass,sender);
			 }
			 else 
			 {
				//it is a local refereence
//				 CMETRef cmetRef = CMETUtil.getCMET(asscMifClass.getReferenceName());
//				 if (cmetRef==null)
//				 {
					 MIFClass referedClass=classReferences.get(asscMifClass.getName());
					 if (referedClass!=null)
						 assc.setMifClass((MIFClass)referedClass.clone());
					 else
					 {//put the CMET into classReference for later cloning
						 classReferences.put(asscMifClass.getName(), asscMifClass);
						 Log.logError(sender,"Reference is not resolved..className:"+asscMifClass.getName() +"..messageType:"+messageType);
					 }
//				}
//				else
//				{
//					System.out
//					.println("MIFReferenceResolver.resolveReference()..add cmetReference:"+asscMifClass.getName());
//
//					classReferences.put(asscMifClass.getName(), asscMifClass);
//					Log.logInfo(sender,"CMET Reference will be resolved later..className:"+asscMifClass.getName()+"..CMET Reference:"+asscMifClass.getReferenceName());
//				}
			 }
			 
		}
	}
	//process choice class
	if(mifClass.getSortedChoices()!=null)
	{
		HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			if(choiceClass.getReferenceName().equals(""))
			 {
//				System.out.println("MIFReferenceResolver.resolveReference()..choice add to reference:"+messageType+"-"+choiceClass.getName());
				 classReferences.put(choiceClass.getName(), choiceClass);
				 resolveReference(choiceClass,sender);
				 resolvedChoices.add(choiceClass);
			 }
			else 
			 {
				//it is a local refereence
//				 CMETRef cmetRef = CMETUtil.getCMET(choiceClass.getReferenceName());
//				 if (cmetRef==null)
//				 {
					 MIFClass referedClass=classReferences.get(choiceClass.getName());
					 if (referedClass!=null) //point the choice class to the reference class
					 {
						 System.out
							.println("MIFReferenceResolver.resolveReference()..reference:"+choiceClass.getReferenceName() +"..resolved:"+messageType +"--"+referedClass.getName()); 
						 resolvedChoices.add((MIFClass)referedClass.clone());
					 }
					 else
					 {
//						put the CMET into classReference for later cloning
						 classReferences.put(choiceClass.getName(), choiceClass);
						 resolvedChoices.add(choiceClass);
						 Log.logError(sender,"Reference is not resolved..className:"+choiceClass.getName() +"..messageType:"+messageType);
					 }
//				}
//				else
//				{
//					resolvedChoices.add(choiceClass);
//					System.out
//					.println("MIFReferenceResolver.resolveReference()..add cmetReference:"+choiceClass.getName());
//					classReferences.put(choiceClass.getName(), choiceClass);
//
//					Log.logInfo(sender,"CMET Reference will be resolved later..className:"+choiceClass.getName()+"..CMET Reference:"+choiceClass.getReferenceName());
//				}
			 }
			mifClass.setChoice(resolvedChoices);
		}
	}
}
}
