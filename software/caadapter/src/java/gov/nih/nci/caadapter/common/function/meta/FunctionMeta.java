/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-25 18:48:58 $
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
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
