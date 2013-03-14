/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.mapping;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSObject;

public class CellRenderXSObject implements Comparable {
private XSObject coreObject;

/**
 * @return the coreObject
 */
public XSObject getCoreObject() {
	return coreObject;
}

/**
 * @param coreObject the coreObject to set
 */
public void setCoreObject(XSObject coreObject) {
	this.coreObject = coreObject;
}

public CellRenderXSObject(XSObject xsObject)
{
	coreObject=xsObject;
}

@Override
public String toString()
{
	StringBuffer rtBuffer=new StringBuffer();
	if (getCoreObject()==null)
		return null;
	if (getCoreObject().getNamespace()!=null)
		rtBuffer.append(getCoreObject().getNamespace()+":");
	rtBuffer.append(getCoreObject().getName());
	if (getCoreObject() instanceof XSElementDecl)
		rtBuffer.append(" (Element)");
	else if (getCoreObject() instanceof XSComplexTypeDecl)
		rtBuffer.append(" (Complex Type)");
	else if (getCoreObject() instanceof XSSimpleTypeDecl)
		rtBuffer.append(" (Smple Type)");
	else
		rtBuffer.append(" (Unknown)");
	

	return rtBuffer.toString();
}

public int compareTo(Object arg0) {
	// TODO Auto-generated method stub
	XSObject toCompareObj=((CellRenderXSObject)arg0).getCoreObject();
	if (getCoreObject()==null|toCompareObj==null)
		return 0;
	if (getCoreObject() instanceof XSElementDecl)
	{
		if (toCompareObj instanceof XSElementDecl)
			return this.toString().compareTo(arg0.toString());
		else
			return -1;
		
	}
	else if (getCoreObject() instanceof XSComplexTypeDecl)
	{
		if (toCompareObj instanceof XSElementDecl)
			return 1;
		else if (toCompareObj instanceof XSComplexTypeDecl)
			return this.toString().compareTo(arg0.toString());
		else
			return -1;
	}
	else if (getCoreObject() instanceof XSSimpleTypeDecl)
	{
		if (toCompareObj instanceof XSSimpleTypeDecl)
			return this.toString().compareTo(arg0.toString());
		else
			return 1;
	}
	else
		return this.toString().compareTo(arg0.toString());
//	return 0;
}
}
