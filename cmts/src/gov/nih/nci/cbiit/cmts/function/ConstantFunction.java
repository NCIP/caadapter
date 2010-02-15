package gov.nih.nci.cbiit.cmts.function;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class ConstantFunction {

	public String retrieveValue(FunctionType functionType)
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(functionType.getData().get(0).getValue());
		System.out.println("ConstantFunction.retrieveValue()..value="+rtnBf.toString());
		return rtnBf.toString();
	}
}
