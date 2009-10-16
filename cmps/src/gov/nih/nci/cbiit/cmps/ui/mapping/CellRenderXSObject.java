package gov.nih.nci.cbiit.cmps.ui.mapping;

import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSObject;

public class CellRenderXSObject {
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

public String toString()
{
	StringBuffer rtBuffer=new StringBuffer();
	if (getCoreObject()==null)
		return null;
	if (getCoreObject() instanceof XSElementDecl)
		rtBuffer.append("Element--");
	else if (getCoreObject() instanceof XSComplexTypeDecl)
		rtBuffer.append("ComplexType--");
	else if (getCoreObject() instanceof XSSimpleTypeDecl)
		rtBuffer.append("ComplexType--");
	else
		rtBuffer.append("Unknown--");
	
	if (getCoreObject().getNamespace()!=null)
		rtBuffer.append("\"" +getCoreObject().getNamespace()+"\":");
	rtBuffer.append(getCoreObject().getName());
	return rtBuffer.toString();
}
}
