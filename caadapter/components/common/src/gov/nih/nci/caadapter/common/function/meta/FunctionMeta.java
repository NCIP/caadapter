/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.function.meta;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.function.FunctionException;

import java.util.List;

/**
 * The function meta information that may obtained from XML.
 *
 * This class also defines the computational logic as well as defined the number
 * of inputs and outputs, etc.
 * @version 1.0
 */
public interface FunctionMeta extends MetaObject{


	public List<ParameterMeta> getInputDefinitionList();
	public void setInputDefinitionList(List<ParameterMeta> newDefinitionList);

    public List<ParameterMeta> getOuputDefinitionList();
    public void setOutputDefinitionList(List<ParameterMeta> newDefinitionList);

    public String getFunctionName();
	public void setFunctionName(String newName);

	public String getImplementationClass();
	public void setImplementationClass(String newImplementationClass);

	public String getImplementationMethod();
	public void setImplementationMethod(String newImplementationMethod);

	public int getSizeOfDefinedInput();
	public int getSizeOfDefinedOutput();

    public String getGroupName();
    public void setGroupName(String groupName);

    public String getKind();
    public void setKind(String kind);

	public Object[] compute(Object[] objArguments) throws FunctionException;

	public boolean isConstantFunction();
    public boolean isFunctionVocabularyMapping();

}
