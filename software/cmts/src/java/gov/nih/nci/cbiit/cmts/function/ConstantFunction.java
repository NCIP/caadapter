/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class ConstantFunction {
/**
 * Returns the xml query statement of retrieving a constant value
 * @param functionType
 * @param paramters
 * @return
 */
	public String retrieveValue(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(functionType.getData().get(0).getValue());
		return "string(\""+rtnBf.toString()+"\")";
	}
}
