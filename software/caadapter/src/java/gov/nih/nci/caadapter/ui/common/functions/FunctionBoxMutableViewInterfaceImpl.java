/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.impl.FunctionMetaImpl;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;

import javax.swing.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the default implementation of FunctionBoxMutableViewInterface.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-11-21 16:18:38 $
 */
public class FunctionBoxMutableViewInterfaceImpl extends MetaObjectImpl implements FunctionBoxMutableViewInterface, MappableNode, Cloneable
{
	private boolean mapped = false;

	private Icon icon = null;
//	private String name = null;
	private int totalNumberOfDefinedInputs = 0;
	private int totalNumberOfDefinedOutputs = 0;
	private List inputElementList = new ArrayList();
	private List outputElementList = new ArrayList();

	protected FunctionComponent functionComponent;
	protected FunctionMeta functionMeta;
	protected View viewMeta;
	protected FunctionBoxCell functionBoxCell;
	protected FunctionConstant functionConstant;
    protected FunctionVocabularyMapping functionVocabularyMapping;
	/**
	 * This constuctor is intended to be used when adding a new function on the mapping,
	 * since at that time, no function component is available but just functionMeta and/or viewMeta.
	 *
	 * @param functionMeta
	 * @param viewMeta
	 */
	public FunctionBoxMutableViewInterfaceImpl (FunctionMeta functionMeta, View viewMeta)
	{
		this.functionComponent = null;
		this.functionMeta = functionMeta;
		this.viewMeta = viewMeta;
		resetMetas();
	}

	/**
	 * This constructor is intended to be used when adding functions from
	 * existing mapping file, and will remember the function component associated with the mapping
	 * so as to be referenced upon persistence.
	 * @param functionComponent
	 */
	public FunctionBoxMutableViewInterfaceImpl (FunctionComponent functionComponent)//FunctionMeta functionMeta, View viewMeta)
	{
		this.functionComponent = functionComponent;
		this.functionMeta = this.functionComponent.getMeta();
		this.viewMeta = this.functionComponent.getView();
		resetMetas();
	}

//	public FunctionBoxMutableViewInterfaceImpl (Icon icon, String name, int totalNumberOfDefinedInputs, int totalNumberOfDefinedOutputs)
//	{
//		this(icon, name, totalNumberOfDefinedInputs, totalNumberOfDefinedOutputs, null, null);
//	}
//
//	public FunctionBoxMutableViewInterfaceImpl (Icon icon, String name, int totalNumberOfDefinedInputs, int totalNumberOfDefinedOutputs, List inputList, List outputList)
//	{
//		this.icon = icon;
//		this.name = name;
//		this.totalNumberOfDefinedInputs = totalNumberOfDefinedInputs;
//		this.totalNumberOfDefinedOutputs = totalNumberOfDefinedOutputs;
//
//		if(inputList==null)
//		{
//			inputList = new ArrayList();
//		}
//		this.inputElementList = inputList;
//		if(outputList==null)
//		{
//			outputList = new ArrayList();
//		}
//		this.outputElementList = outputList;
//	}

	public Icon getIcon()
	{
		return icon;
	}

//	public String getName()
//	{
//		return name;
//	}

	public int getTotalNumberOfDefinedInputs()
	{
		return totalNumberOfDefinedInputs;
	}

	public int getTotalNumberOfDefinedOutputs()
	{
		return totalNumberOfDefinedOutputs;
	}

	public int getTotalNumberOfActualInputs()
	{
		return inputElementList.size();
	}

	public int getTotalNumberOfActualOutputs()
	{
		return outputElementList.size();
	}

	public List getInputElementList()
	{
		return inputElementList;
	}

//	public String toString()
//	{
//		return getName();
//	}

	public List getOutputElementList()
	{
		return outputElementList;
	}

	public FunctionMeta getFunctionMeta()
	{
		return this.functionMeta;
	}

	public void setFunctionMeta(FunctionMeta newFunctionMeta)
	{
		this.functionMeta = newFunctionMeta;
		resetMetas();
	}

	public View getViewMeta()
	{
		return this.viewMeta;
	}

	public void setViewMeta(View newViewMeta)
	{
		this.viewMeta = newViewMeta;
	}

	/**
	 * Return the embedded function component.
	 * @param create if true will create a new one given the current one is null.
	 * @return a FunctionComponent
	 */
	public FunctionComponent getFunctionComponent(boolean create)
	{
		if(create && functionComponent==null)
		{
			functionComponent = FunctionComponent.getFunctionComponent();
			functionComponent.setMeta(functionMeta);
			//new FunctionComponent(functionMeta);
			functionComponent.setView(viewMeta);
			functionComponent.setFunctionConstant(getFunctionConstant());
            functionComponent.setFunctionVocabularyMapping(getFunctionVocabularyMapping());
        }
		else if(functionComponent!=null)
		{
			functionComponent.setMeta(this.functionMeta);
			functionComponent.setView(this.viewMeta);
			functionComponent.setFunctionConstant(getFunctionConstant());
            functionComponent.setFunctionVocabularyMapping(getFunctionVocabularyMapping());
        }
		return functionComponent;
	}

	/**
	 * Set a new function component.
	 *
	 * @param functionComponent
	 */
	public void setFunctionComponent(FunctionComponent functionComponent)
	{
		this.functionComponent = functionComponent;
		setFunctionMeta(functionComponent.getMeta());
		setViewMeta(functionComponent.getView());
		setFunctionConstant(functionComponent.getFunctionConstant());
        setFunctionVocabularyMapping(functionComponent.getFunctionVocabularyMapping());
    }

	private void resetMetas()
	{
		if (functionMeta != null)
		{
			this.name = functionMeta.getName();
			this.totalNumberOfDefinedInputs = functionMeta.getSizeOfDefinedInput();
			this.totalNumberOfDefinedOutputs = functionMeta.getSizeOfDefinedOutput();
		}
		this.inputElementList = new ArrayList();
		this.outputElementList = new ArrayList();
	}

	/**
	 * Return the associated view object in JGraph.
	 * Could return null if this view has not be put on view yet.
	 *
	 * @return a FunctionBoxCell
	 */
	public FunctionBoxCell getFunctionBoxCell()
	{
		return this.functionBoxCell;
	}

	/**
	 * Set the function box cell.
	 *
	 * @param newFunctionBoxCell
	 */
	public void setFunctionalBoxCell(FunctionBoxCell newFunctionBoxCell)
	{
		this.functionBoxCell = newFunctionBoxCell;
	}

	/**
	 * Return the function constant.
	 * @return a FunctionConstant
	 */
	public FunctionConstant getFunctionConstant()
	{
		return functionConstant;
	}

	/**
	 * Set a new functionConstant.
	 * @param functionConstant
	 */
	public void setFunctionConstant(FunctionConstant functionConstant)
	{
		this.functionConstant = functionConstant;
	}

/**
	 * Return the Vocabulary Mapping instance.
	 * @return a VocabularyMapping
	 */
	public FunctionVocabularyMapping getFunctionVocabularyMapping()
	{
		return functionVocabularyMapping;
	}

    /**
	 * Set a new vocabularyMapping function.
	 * @param vocabularyMapping
	 */
	public void setFunctionVocabularyMapping(FunctionVocabularyMapping vocabularyMapping)
	{
		this.functionVocabularyMapping = vocabularyMapping;
	}

    /**
	 * Set the map status to new value, which might trigger underline property change.
	 *
	 * @param newValue
	 */
	public void setMapStatus(boolean newValue)
	{
		this.mapped = newValue;
	}

	/**
	 * Answer if this given node is mapped.
	 *
	 * @return if this given node is mapped.
	 */
	public boolean isMapped()
	{
		return mapped;
	}

	/**
	 * @param copyUUID if true, the cloned object will share the same uuid value of this object; otherwise, it will have different UUID value.
	 */
	public Object clone(boolean copyUUID) throws CloneNotSupportedException
	{
		FunctionBoxMutableViewInterfaceImpl cloneObject = (FunctionBoxMutableViewInterfaceImpl) super.clone(copyUUID);
		cloneObject.name = this.name;
		cloneObject.icon = this.icon;

		cloneObject.totalNumberOfDefinedInputs = this.totalNumberOfDefinedInputs;
		cloneObject.totalNumberOfDefinedOutputs = this.totalNumberOfDefinedOutputs;

		cloneObject.inputElementList = new ArrayList();
		cloneObject.inputElementList.addAll(this.inputElementList);

		cloneObject.outputElementList = new ArrayList();
		cloneObject.outputElementList.addAll(this.outputElementList);
		cloneObject.mapped = this.mapped;
		return cloneObject;
	}

	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 *
	 * @return the title value of this object for properties display
	 */
	public String getTitle()
	{
		String title = null;
		if(functionMeta instanceof FunctionMetaImpl)
		{
			title = ((FunctionMetaImpl)functionMeta).getTitle();
		}
		else
		{
			title = "Properties";
			if(functionConstant!=null)
			{
				title = "Constant " + title;
			}
            else if(functionVocabularyMapping!=null)
			{
				title = "Vocabulary Mapping " + title;
			}
            else
			{
				title = "Function " + title;
			}
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
	public PropertiesResult getPropertyDescriptors() throws Exception
	{
		PropertiesResult result = ((FunctionMetaImpl)functionMeta).getPropertyDescriptors();
		if(functionConstant!=null)
		{
			List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
//			Class beanClass = this.getClass();
//			PropertyDescriptor prop = null;
//			Method readMethod = constructMethod(beanClass, "getFunctionConstantType");
//			if(readMethod !=null)
//			{
//				prop = new PropertyDescriptor("Constant Type", readMethod, null);
//				propList.add(prop);
//			}
//			readMethod = constructMethod(beanClass, "getFunctionConstantValue");
//			if(readMethod !=null)
//			{
//				prop = new PropertyDescriptor("Constant Value", readMethod, null);
//				propList.add(prop);
//			}
			Class beanClass = functionConstant.getClass();//this.getClass();
			PropertyDescriptor prop = null;
//			Method readMethod = constructMethod(beanClass, "getFunctionConstantType");
//			if(readMethod !=null)
//			{
//				prop = new PropertyDescriptor("Constant Type", readMethod, null);
				prop = new PropertyDescriptor("Constant Type", beanClass, "getType", null);
				propList.add(prop);
//			}
//			readMethod = constructMethod(beanClass, "getFunctionConstantValue");
//			if(readMethod !=null)
//			{
//				prop = new PropertyDescriptor("Constant Value", readMethod, null);
				prop = new PropertyDescriptor("Constant Value", beanClass, "getValue", null);
				propList.add(prop);
//			}

			if(propList.size()>0)
			{
				result.addPropertyDescriptors(functionConstant, propList);
//				result.addPropertyDescriptors(this, propList);
			}
		}
        if(functionVocabularyMapping!=null)
		{
			List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();

			Class beanClass = functionVocabularyMapping.getClass();//this.getClass();
			PropertyDescriptor prop = null;

			prop = new PropertyDescriptor("Source Type", beanClass, "getType", null);
			propList.add(prop);

			prop = new PropertyDescriptor("Source Map Data", beanClass, "getValue", null);
			propList.add(prop);

            if(propList.size()>0)
			{
				result.addPropertyDescriptors(functionVocabularyMapping, propList);
//				result.addPropertyDescriptors(this, propList);
			}
		}
        return result;
	}

	protected Method constructMethod(Class aClass, String methodName)
	{
		Method method = null;
		try
		{
			method = aClass.getDeclaredMethod(methodName, new Class[0]);
		}
		catch (NoSuchMethodException e)
		{
			Log.logWarning(aClass, e);
		}
		return method;
	}

	protected Object getFunctionConstantType()
	{
		if(functionConstant!=null)
		{
			return functionConstant.getType();
		}
		else
		{
			return "";
		}
	}

	protected Object getFunctionConstantValue()
	{
		if(functionConstant!=null)
		{
			return functionConstant.getValue();
		}
		else
		{
			return "";
		}
	}

    protected Object getFunctionVocabularyMappingType()
        {
            if(functionVocabularyMapping!=null)
            {
                return functionVocabularyMapping.getType();
            }
            else
            {
                return "";
            }
        }

        protected Object getFunctionVocabularyMappingValue()
        {
            if(functionVocabularyMapping!=null)
            {
                return functionVocabularyMapping.getValue();
            }
            else
            {
                return "";
            }
        }

//	public FunctionBoxMutableViewInterfaceImpl(Icon icon, String name, int totalNumberOfDefinedInputs, int totalNumberOfDefinedOutputs)
//	{
//		super(icon, name, totalNumberOfDefinedInputs, totalNumberOfDefinedOutputs);
//	}
//
//	public FunctionBoxMutableViewInterfaceImpl(Icon icon, String name, int totalNumberOfDefinedInputs, int totalNumberOfDefinedOutputs, List inputList, List outputList)
//	{
//		super(icon, name, totalNumberOfDefinedInputs, totalNumberOfDefinedOutputs, inputList, outputList);
//	}

//	public boolean addElement(Object element, String inputOrOutput)
//	{
//		//todo: implement assert betweent the actual number of added vs the total number of defined so as to avoid overflow or underflow
//		boolean result = false;
//		if(element==null)
//		{
//			throw new IllegalArgumentException("Element should not be null!");
//		}
//		if(INPUT_PARAM.equals(inputOrOutput))
//		{
//            result = getInputElementList().add(element);
//		}
//		else if(OUTPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getOutputElementList().add(element);
//		}
//		else
//		{
//			throw new IllegalArgumentException("The '" + inputOrOutput + "' is not valid input!");
//		}
//		return result;
//	}
//
//	public boolean removeElement(Object element, String inputOrOutput)
//	{
//		boolean result = false;
//		if (element == null)
//		{
//			throw new IllegalArgumentException("Element should not be null!");
//		}
//		if (INPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getInputElementList().remove(element);
//		}
//		else if (OUTPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getOutputElementList().remove(element);
//		}
//		else
//		{
//			throw new IllegalArgumentException("The '" + inputOrOutput + "' is not valid input!");
//		}
//		return result;
//	}
//
//	public boolean removeAll(List elementList, String inputOrOutput)
//	{
//		boolean result = false;
//		if (INPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getInputElementList().removeAll(elementList);
//		}
//		else if (OUTPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getOutputElementList().removeAll(elementList);
//		}
//		else
//		{
//			throw new IllegalArgumentException("The '" + inputOrOutput + "' is not valid input!");
//		}
//		return result;
//	}
//
//	public boolean addAll(List elementList, String inputOrOutput)
//	{
//		boolean result = false;
//		if (elementList == null)
//		{
//			throw new IllegalArgumentException("Element should not be null!");
//		}
//		if (INPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getInputElementList().addAll(elementList);
//		}
//		else if (OUTPUT_PARAM.equals(inputOrOutput))
//		{
//			result = getOutputElementList().addAll(elementList);
//		}
//		else
//		{
//			throw new IllegalArgumentException("The '" + inputOrOutput + "' is not valid input!");
//		}
//		return result;
//	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/11/17 20:10:47  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/18 20:42:13  wangeug
 * HISTORY      : create CSV-H7L mapping with mapppingV4.0.xsd
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 18:57:11  wangeug
 * HISTORY      : relocate "View" object from  other package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/10/02 18:05:08  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/09/06 18:29:05  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/23 18:57:16  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/22 21:35:27  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 18:05:04  jiangsc
 * HISTORY      : Refactorized clone() methods to have explicit clone(boolean copyUUID)
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:19  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
