/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;

import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.core.ViewType;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;

import javax.swing.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the default implementation of FunctionBoxMutableViewInterface.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxUserObject implements MappableNode
{
	private boolean mapped = false;

	private Icon icon = null;
//	private String name = null;
	private int totalNumberOfDefinedInputs = 0;
	private int totalNumberOfDefinedOutputs = 0;
	private List inputElementList = new ArrayList();
	private List outputElementList = new ArrayList();

	protected FunctionDef FunctionDef;
	protected ViewType viewMeta;
	protected FunctionBoxCell functionBoxCell;
	//protected FunctionConstant functionConstant;
	/**
	 * This constuctor is intended to be used when adding a new function on the mapping,
	 * since at that time, no function component is available but just FunctionDef and/or viewMeta.
	 *
	 * @param function
	 * @param viewMeta
	 */
	public FunctionBoxUserObject (FunctionDef function, ViewType viewMeta)
	{
		this.FunctionDef = function;
		this.viewMeta = viewMeta;
		resetMetas();
	}


	public Icon getIcon()
	{
		return icon;
	}

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

	public List getOutputElementList()
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
//		this.inputElementList = new ArrayList();
//		this.outputElementList = new ArrayList();
		if (FunctionDef != null)
		{
			for (FunctionData fData:FunctionDef.getData())
			{
				if (fData.isInput())
					totalNumberOfDefinedInputs++;
				else
					totalNumberOfDefinedOutputs++;
			}
		}
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


	public String getXmlPath() {
		// TODO Auto-generated method stub
		return null;
	}



}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
