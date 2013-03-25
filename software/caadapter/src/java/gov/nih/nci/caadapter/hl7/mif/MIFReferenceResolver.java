/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.common.Log;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;
/**
 * This class resolve the reference defined within a MIF file
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.13 $
 *          date        $Date: 2008-12-30 15:08:07 $
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
			 else if (!asscMifClass.isReference()) //here is a CMET class
				 classReferences.put(asscMifClass.getName(), asscMifClass);

		}
	}
	//process choice class
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			if(choiceClass.getReferenceName().equals(""))
			 {
				 classReferences.put(choiceClass.getName(), choiceClass);
				 findReferenceDefinition(choiceClass);
			 }
			else if (!choiceClass.isReference()) //here is a CMET class
				 classReferences.put(choiceClass.getName(), choiceClass);
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
			 if(!asscMifClass.getReferenceName().equals(""))
			 {
				//it is a local refereence
				 MIFClass referedClass=classReferences.get(asscMifClass.getName());
				 if (referedClass!=null)
				 {
					 if (referencePath.contains(referedClass.getName()))
					 {
						 Log.logInfo(sender,"Recursive reference is not resolved..className:"+asscMifClass.getName() +"..referencePath:"+referencePath);
					 }
					 else
					 {
						 MIFClass asscRefMifClass=(MIFClass)referedClass.clone();
						 assc.setMifClass(asscRefMifClass);
					 }
				 }
				 else
				 {//put the CMET into classReference for later cloning
					 Log.logInfo(sender,"Reference is not resolved..className:"+asscMifClass.getName() +"..messageType:"+messageType);
				 }
			 }
			 //recursively resolve the sub-class
			 resolveReference(assc.getMifClass(),sender, assc.getParticipantTraversalNames());
		}
	}
	//process choice class
		HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			MIFClass clsToAdd=choiceClass;
			if(!choiceClass.getReferenceName().equals(""))
			 {
				 MIFClass referedClass=classReferences.get(choiceClass.getName());
				 if (referedClass!=null) //point the choice class to the reference class
				 {
					 clsToAdd=(MIFClass)referedClass.clone();
				 }
				 else
				 {
					 //put the CMET into classReference for later cloning
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
	referencePath.removeLast();
}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.12  2008/09/29 15:44:40  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */