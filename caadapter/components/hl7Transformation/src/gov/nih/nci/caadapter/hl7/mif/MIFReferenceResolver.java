package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.common.Log;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;
/**
 * This class resolve the reference defined within a MIF file
 * @author wangeug
 *
 */
public class MIFReferenceResolver {
private Hashtable<String, MIFClass> classReferences=new Hashtable<String, MIFClass>();;
private String messageType;
private LinkedList <String> referencePath=new LinkedList<String>();
/**
 * Publically accessed to resolve reference for a MIFClass
 * @param mifClass
 */
public void getReferenceResolved(MIFClass mifClass)
{
	//clear the reference hashtable for a new MIFClass
	classReferences.clear();
	messageType=mifClass.getMessageType();
	findReferenceDefinition(mifClass);
	referencePath.clear();
	resolveReference(mifClass,this, null);
}

/**
 * Find the definition of all the class defined locally
 * -- local class is defined with an association
 * -- local class is defined within a choice group
 * @param mifClass
 */
private  void findReferenceDefinition(MIFClass mifClass)
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
				 findReferenceDefinition(asscMifClass);
			 }

		}
	}
	//process choice class
	if(mifClass.getSortedChoices()!=null)
	{
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			if(choiceClass.getReferenceName().equals(""))
			 {
				 classReferences.put(choiceClass.getName(), choiceClass);
				 findReferenceDefinition(choiceClass);
			 }
		}
	}
}
/**
 * Resolve reference of a MIFClass recursively
 * If the MIFClass to be resolved is an association MIFClass,
 * traversal name should be reset for all choice element class
 * @param mifClass
 */
private  void resolveReference(MIFClass mifClass, Object sender, Hashtable<String, String>asscParticipants)
{
	String clsTodo=mifClass.getName();
	if (referencePath.contains(clsTodo))
		return;

	referencePath.addLast(mifClass.getName());
	TreeSet<MIFAssociation> mifAsscs=mifClass.getSortedAssociations();
	if (mifAsscs!=null
		&&!mifAsscs.isEmpty())
	{
		for(MIFAssociation assc:mifAsscs)
		{
			 MIFClass asscMifClass=assc.getMifClass();
			 if(asscMifClass.isReference())//.getReferenceName().equals(""))
			 {
				//it is a local refereence
				 MIFClass referedClass=classReferences.get(asscMifClass.getName());
				 if (referedClass!=null)
				 {
					 MIFClass asscRefMifClass=(MIFClass)referedClass.clone();
					 //the resolved class can not be referenced any more
					 asscRefMifClass.setReference(false);
					 assc.setMifClass(asscRefMifClass);
				 }
				 else
				 {//put the CMET into classReference for later cloning
					 classReferences.put(asscMifClass.getName(), asscMifClass);
					 Log.logInfo(sender,"Reference is not resolved..className:"+asscMifClass.getName() +"..messageType:"+messageType);
				 }
			 }
			 //recursively resolve the sub-class
			 resolveReference(assc.getMifClass(),sender, assc.getParticipantTraversalNames());

		}
		referencePath.removeLast();
	}
	//process choice class
	if(mifClass.getSortedChoices()!=null)
	{
		HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			MIFClass clsToAdd=choiceClass;
			if(choiceClass.isReference())//.getReferenceName().equals(""))
			 {
				 MIFClass referedClass=classReferences.get(choiceClass.getName());
				 if (referedClass!=null) //point the choice class to the reference class
				 {
					 clsToAdd=(MIFClass)referedClass.clone();
					 //the resoleved class can not be  referenced any more
					 clsToAdd.setReference(false);
				 }
				 else
				 {
					 //put the CMET into classReference for later cloning
					 classReferences.put(choiceClass.getName(), choiceClass);
					 Log.logError(sender,"Reference is not resolved..className:"+choiceClass.getName() +"..messageType:"+messageType);
				 }
			}
			resolvedChoices.add(clsToAdd);
			 //recursively resolve the sub-class
			resolveReference(clsToAdd,sender, null);
			
			if (asscParticipants!=null)
			{
				String resolvedTraversalName=asscParticipants.get(choiceClass.getName());
				if (resolvedTraversalName!=null)
					clsToAdd.setTraversalName(resolvedTraversalName);
			}
		}
		mifClass.setChoice(resolvedChoices);
	}
}
}
