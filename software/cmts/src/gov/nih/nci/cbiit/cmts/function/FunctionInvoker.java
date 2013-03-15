/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FunctionInvoker {

	public static Object invokeFunctionMethod(String functionName, String methodName, Object argList[]) throws FunctionException
	{
		try {
			// Use Class<?> if the class being modeled is unknown.
			Class<?> targetClass = Class.forName(functionName);
			Method[] allMethods=targetClass.getMethods();
			//find the target method by name
			Method targetMethod=null;
			for (Method oneMethod:allMethods)
			{
				if (oneMethod.getName().equals(methodName)){
					targetMethod=oneMethod;
					break;
				}
			}
			if (targetMethod==null)
				throw new FunctionException("Not found method:"+methodName);	        
	        Object retObj  = targetMethod.invoke(targetClass.newInstance(), argList);
	        System.out.println("FunctionInvoker.invokeFunctionMethod()...return:"+retObj);
	        return retObj;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FunctionException(e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FunctionException(e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FunctionException(e.getMessage());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FunctionException(e.getMessage());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FunctionException(e.getMessage());
		}
	}
}
