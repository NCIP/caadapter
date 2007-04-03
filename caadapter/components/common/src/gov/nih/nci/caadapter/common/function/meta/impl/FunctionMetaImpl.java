/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/meta/impl/FunctionMetaImpl.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The function meta information that may obtained from XML.
 * <p/>
 * This class also defines the computational logic as well as defined the number
 * of inputs and outputs, etc.
 *
 * @version 1.0
 */
public class FunctionMetaImpl extends MetaObjectImpl implements FunctionMeta {

    private String methodName;
    private String functionName;
    private String implementationClass;
    private List<ParameterMeta> inputDefinitionList;
    private List<ParameterMeta> outputDefinitionList;
    public ParameterMeta m_ParameterMeta;

    private String groupName;
    private String kind;

    public FunctionMetaImpl() {

    }

    public void finalize() throws Throwable {

    }

    // Setters and Getters.
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public List<ParameterMeta> getInputDefinitionList() {
        return inputDefinitionList;
    }

    /**
     * @param newDefinitionList
     */
    public void setInputDefinitionList(List<ParameterMeta> newDefinitionList) {
        this.inputDefinitionList = newDefinitionList;
    }

    public List<ParameterMeta> getOuputDefinitionList() {
        return outputDefinitionList;
    }

    /**
     * @param newDefinitionList
     */
    public void setOutputDefinitionList(List<ParameterMeta> newDefinitionList) {
        this.outputDefinitionList = newDefinitionList;
    }


    public String getFunctionName() {
        return functionName;
    }

    /**
     * @param newName
     */
    public void setFunctionName(String newName) {
        this.functionName = newName;
        this.name = newName;
    }

    public String getImplementationClass() {
        return implementationClass;
    }

    /**
     * @param newImplementationClass
     */
    public void setImplementationClass(String newImplementationClass) {
        this.implementationClass = newImplementationClass;
    }

    public String getImplementationMethod() {
        return methodName;
    }

    /**
     * @param newImplementationMethod
     */
    public void setImplementationMethod(String newImplementationMethod) {
        this.methodName = newImplementationMethod;
    }

    public int getSizeOfDefinedInput() {
        return inputDefinitionList.size();

    }

    public int getSizeOfDefinedOutput() {
        return outputDefinitionList.size();
    }

    /**
     * Override to be consistent with function name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Override to be consistent with function name
     *
     * @param name
     */
    public void setName(String name) {
        this.functionName = name;
        this.name = name;
    }


    /**
     * This method calls a class and method based on attribute values defined in the function map file implementation.
     * This method is designed to abstract specific Java method calls.
     *
     * @param objArguments - list of method parameters
     * @return objFunctionResult Result of the computation
     */
    public Object[] compute(Object[] objArguments) throws FunctionException {

        Class functionClass = null;
        Method functionMethod = null;
        Class[] parameterTypes = new Class[this.getSizeOfDefinedInput()];
        Object[] parameterData = new Object[this.getSizeOfDefinedInput()];
        Object[] functionResult = null;
        Object tempResult = null;

        String functionDescription = "Method [" + this.getImplementationMethod() +
                "] in Class [" + this.getImplementationClass() + "]";

        // 1. Find the Implementation Class.
        try {
            functionClass = Class.forName(this.getImplementationClass());
        } catch (ClassNotFoundException e) {
            throw new FunctionException("Class could not be found [" + this.getImplementationClass()+"]");
        }

        // 2. Find the parameters types to the method.
        for (int i = 0; i < this.getSizeOfDefinedInput(); i++) {
            parameterTypes[i] = paramConvert(inputDefinitionList.get(i).getParameterType());
        }

        // 3. Find the method.
        try {
            functionMethod = functionClass.getMethod(this.getImplementationMethod(), parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new FunctionException(functionDescription + " could not be found.");
        }

        // 4. Parse data to expected format.
        try {
            parameterData = parseData(parameterTypes, objArguments);
            if (parameterData.length != parameterTypes.length) {
                throw new FunctionException("Parameter count doesn't match.  Data provided [" + CaadapterUtil.join(parameterData, ",") +
                        "] ParameterTypes expected [" + CaadapterUtil.join(parameterTypes, ",") + "] in " + functionDescription);
            }
        } catch (NumberFormatException e) {
            throw new FunctionException("Cannot cast data [" + CaadapterUtil.join(objArguments, ",") + "] to type ["
                    + CaadapterUtil.join(parameterTypes, ",") + "] in "+ functionDescription);
        }


        // 5. Invoke the method.
        try {

            if(this.isFunctionVocabularyMapping())
            {

            }
            else
            {
                //System.out.println("KKKKKKKKK : ");
                tempResult = functionMethod.invoke(functionClass.newInstance(), parameterData);
            }
        } catch (IllegalAccessException e) {
            throw new FunctionException("Unexpected error while accessing: " + functionDescription);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FunctionException) {
                String errMessage = e.getTargetException().getMessage() + " in " + functionDescription;
                throw new FunctionException(errMessage);
            } else {
                throw new FunctionException("Unexpected error while invoking: " + functionDescription);
            }
        } catch (InstantiationException e) {
            throw new FunctionException("Unexpected error while instanciating: " + functionDescription);
        }

        // 6. Convert to an array.
        if (tempResult instanceof Array) {
            functionResult = (Object[]) tempResult;
        } else if (tempResult instanceof List) {
            functionResult = ((List) tempResult).toArray();
        } else {
            functionResult = new Object[]{tempResult};
        }

        return functionResult;
    }

    public boolean isFunctionVocabularyMapping() {

        //if (groupName.toLowerCase().indexOf("functionvocabularymapping") >= 0) return true;
        if (groupName.toLowerCase().indexOf("vocabulary") >= 0) return true;
        else return false;
    }
    public boolean isConstantFunction() {
        boolean result = false;
        if (groupName != null) {
            result = groupName.toLowerCase().indexOf("const") != -1;
        } else if (functionName != null) {
            result = ((functionName.toLowerCase().indexOf("const") != -1)||
                      (functionName.toLowerCase().indexOf("saveV") != -1)||
                      (functionName.toLowerCase().indexOf("readV") != -1));
        } else return false; /*if (name != null) {
            result = name.toLowerCase().indexOf("const") != -1;
        } */
        return result;
    }

    private Object[] parseData(Class[] classes, Object[] data) {
        Object[] parsedData = new Object[data.length];

        for (int i = 0; i < data.length; i++) {
            if (classes[i] == String.class) {
                parsedData[i] = data[i].toString();
            } else if (classes[i] == Integer.TYPE) {
                if ("".equalsIgnoreCase(data[i].toString())) {
                    parsedData[i] = Integer.valueOf(0);
                } else if ((data[i].toString()).indexOf(".") >= 0) {
                    parsedData[i] = Double.valueOf(data[i].toString());
                } else {
                    parsedData[i] = Integer.valueOf(data[i].toString());
                }
            } else if (classes[i] == Double.TYPE) {
                if ("".equalsIgnoreCase(data[i].toString())) {
                    parsedData[i] = Double.valueOf(0);
                } else {
                    parsedData[i] = Double.valueOf(data[i].toString());
                }
            }
        }
        return parsedData;
    }

    /**
     * Takes a list of parameters defined in the function map file and corresponding FunctionMeta object
     * as string values and converts them to thier corresponding datatype.
     *
     * @param strParam - param definition as a String
     * @return param definition as a Class
     */
    private Class paramConvert(String strParam) {
        Class clazz = null;

        if (strParam.equalsIgnoreCase("string")) {
            clazz = String.class;
        } else if (strParam.equalsIgnoreCase("int")) {
            clazz = Integer.TYPE;
        } else if (strParam.equalsIgnoreCase("double")) {
            clazz = Double.TYPE;
        }

        return clazz;
    }

    /**
     * Return the title of this provider that may be used to distinguish from others.
     *
     * @return the title that might be displayed on properties.
     */
    public String getTitle() {
        String title = "Properties";
        String localName = getFunctionName();
        if (localName != null && localName.toLowerCase().indexOf("const") != -1)
        {
            title = "Constant " + title;
        }
        else if (localName != null && localName.toLowerCase().indexOf("vocabularymapping") != -1)
        {
            title = "VocabularyMapping " + title;
        }
        else
        {
            title = "Function " + title;
        }
        return title;
    }

    /**
     * This functions will return an array of PropertyDescriptor that would
     * help reflection and/or introspection to figure out what information would be
     * presented to the user.
     * <p/>
     * descendant classes are free to override to provide additional information.
     */
    public PropertiesResult getPropertyDescriptors() throws Exception {
        Class beanClass = this.getClass();

        PropertyDescriptor _kind = new PropertyDescriptor("Kind", beanClass, "getKind", null);
        PropertyDescriptor _groupName = new PropertyDescriptor("Group Name", beanClass, "getGroupName", null);
        PropertyDescriptor _functionName = new PropertyDescriptor("Function Name", beanClass, "getFunctionName", null);
        PropertyDescriptor _implementationClass = new PropertyDescriptor("Implementation Class", beanClass, "getImplementationClass", null);
        PropertyDescriptor _method = new PropertyDescriptor("Method", beanClass, "getImplementationMethod", null);
        PropertyDescriptor _numberOfInputs = new PropertyDescriptor("Input #", beanClass, "getSizeOfDefinedInput", null);
        PropertyDescriptor _numberOfOutputs = new PropertyDescriptor("Output #", beanClass, "getSizeOfDefinedOutput", null);

//		PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
//		{
//			_kind, _groupName, _functionName, _implementationClass, _method, _numberOfInputs, _numberOfOutputs
//		};
//		return propertiesArray;

        List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
        propList.add(_kind);
        propList.add(_groupName);
        propList.add(_functionName);
        propList.add(_implementationClass);
        propList.add(_method);
        propList.add(_numberOfInputs);
        propList.add(_numberOfOutputs);
        PropertiesResult result = new PropertiesResult();
        result.addPropertyDescriptors(this, propList);
        return result;

    }

}
