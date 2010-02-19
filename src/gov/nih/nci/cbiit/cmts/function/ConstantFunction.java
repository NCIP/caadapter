package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class ConstantFunction {

	public String retrieveValue(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(functionType.getData().get(0).getValue());
		return rtnBf.toString();
	}
}
