/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;

import gov.nih.nci.cbiit.cmts.common.PropertiesProvider;
import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.core.ViewType;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.properties.PropertiesResult;

import org.jgraph.graph.DefaultGraphCell;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the default implementation of FunctionBoxMutableViewInterface.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxGraphCell extends DefaultGraphCell implements MappableNode, Serializable, PropertiesProvider
{
	private boolean mapped = false;

	private List<FunctionData> inputElementList = new ArrayList<FunctionData>();
	private List<FunctionData> outputElementList = new ArrayList<FunctionData>();

	protected FunctionDef FunctionDef;
	protected ViewType viewMeta;
	private String funcionBoxUUID;
	
	/**
	 * This constuctor is intended to be used when adding a new function on the mapping,
	 * since at that time, no function component is available but just FunctionDef and/or viewMeta.
	 *
	 * @param function
	 * @param viewMeta
	 */
	public FunctionBoxGraphCell (FunctionDef function, ViewType viewMeta)
	{
		super();
		funcionBoxUUID=""+System.currentTimeMillis();
		this.FunctionDef = function;
		this.viewMeta = viewMeta;
		resetMetas();
	}

	public List<FunctionData> getInputElements()
	{
		return inputElementList;
	}

	public List<FunctionData> getOutputElements()
	{
		return outputElementList;
	}

	public FunctionDef getFunctionDef()
	{
		return this.FunctionDef;
	}

	public void setFunctionDef(FunctionDef newFunctionDef)
	{
		this.FunctionDef = newFunctionDef;
		resetMetas();
	}

	public ViewType getViewMeta()
	{
		return this.viewMeta;
	}

	public void setViewMeta(ViewType newViewMeta)
	{
		this.viewMeta = newViewMeta;
	}

	private void resetMetas()
	{
		if (FunctionDef != null)
		{
			for (FunctionData fData:FunctionDef.getData())
			{
				if (fData.isInput())
					inputElementList.add(fData);
				else
					outputElementList.add(fData);
			}
		}
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
	 * Return the title of this provider that may be used to distinguish from others.
	 *
	 * @return the title value of this object for properties display
	 */
	public String getTitle()
	{
		String title = null;
		title = FunctionDef.getName().toString();
		return title;
	}

public FunctionBoxGraphPort findPortByName(String nameToSearch)
{
	for (Object fPort:this.getChildren())
	{
		if (fPort instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort graphPort=(FunctionBoxGraphPort)fPort;
			FunctionData fData=(FunctionData)graphPort.getUserObject();
			if (fData.getName().equals(nameToSearch))
				return graphPort;
		}
	}
	return null;
}
	public String getXmlPath() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return the funcionBoxUUID
	 */
	public String getFuncionBoxUUID() {
		return funcionBoxUUID;
	}


	public void setFuncionBoxUUID(String funcionBoxUUID) {
		this.funcionBoxUUID = funcionBoxUUID;
	}


	public PropertiesResult getPropertyDescriptors() throws Exception {
		// TODO Auto-generated method stub
		Class beanClass = this.getClass();
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		PropertyDescriptor groupProp = new PropertyDescriptor("Group", beanClass, "getGroupProperty", null);
		PropertyDescriptor nameProp = new PropertyDescriptor("Name", beanClass,"getNameProperty", null);
		PropertyDescriptor classProp = new PropertyDescriptor("Class", beanClass, "getClassProperty", null);
		PropertyDescriptor methodProp = new PropertyDescriptor("Method", beanClass,"getMethodProperty", null);
		propList.add(groupProp);
		propList.add(nameProp);
		propList.add(classProp);
		propList.add(methodProp);
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
	
	public String getGroupProperty()
	{
		return  getFunctionDef().getGroup();
	}
	public String getNameProperty()
	{
		return  getFunctionDef().getName();
	}
	public String getClassProperty()
	{
		return  getFunctionDef().getClazz();
	}
	public String getMethodProperty()
	{
		return  getFunctionDef().getMethod();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
