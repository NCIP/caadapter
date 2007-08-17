package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.common.Log;

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
	resolveReference(mifClass,this, null);
}
/**
 * Resolve reference of a MIFClass recursively
 * If the MIFClass to be resolved is an association MIFClass, 
 * traversal name should be reset for all choice element class
 * @param mifClass
 */
private  void resolveReference(MIFClass mifClass, Object sender, Hashtable<String, String>asscParticipants)
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
				 resolveReference(asscMifClass,sender, assc.getParticipantTraversalNames());
			 }
			 else 
			 {
				//it is a local refereence
				 MIFClass referedClass=classReferences.get(asscMifClass.getName());
				 if (referedClass!=null)
				 {
					 MIFClass asscRefMifClass=(MIFClass)referedClass.clone();
					 resolveReference(asscRefMifClass,sender, assc.getParticipantTraversalNames());
					 assc.setMifClass(asscRefMifClass);
				 }
				 else
				 {//put the CMET into classReference for later cloning
					 classReferences.put(asscMifClass.getName(), asscMifClass);
					 Log.logInfo(sender,"Reference is not resolved..className:"+asscMifClass.getName() +"..messageType:"+messageType);
				 }
			 }		 
		}
	}
	//process choice class
	if(mifClass.getSortedChoices()!=null)
	{
		HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			MIFClass clsToAdd=choiceClass;
			if(choiceClass.getReferenceName().equals(""))
			 {
				 classReferences.put(choiceClass.getName(), choiceClass);
				 resolveReference(choiceClass,sender, null);
			 }
			else 
			 {
				 MIFClass referedClass=classReferences.get(choiceClass.getName());
				 if (referedClass!=null) //point the choice class to the reference class
					 clsToAdd=(MIFClass)referedClass.clone();
				 else
				 {
					 //put the CMET into classReference for later cloning
					 classReferences.put(choiceClass.getName(), choiceClass);
					 Log.logError(sender,"Reference is not resolved..className:"+choiceClass.getName() +"..messageType:"+messageType);
				 }
			}
			resolvedChoices.add(choiceClass);
			if (asscParticipants!=null)
			{
				String resolvedTraversalName=asscParticipants.get(choiceClass.getName());
				if (resolvedTraversalName!=null)
					choiceClass.setTraversalName(resolvedTraversalName);
			}
		}
		mifClass.setChoice(resolvedChoices);
	}
}
}
