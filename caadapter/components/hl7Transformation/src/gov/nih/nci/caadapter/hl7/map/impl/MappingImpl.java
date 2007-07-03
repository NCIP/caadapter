/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MappingImpl.java,v 1.1 2007-07-03 18:26:44 wangeug Exp $
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


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * The primary class which contains all Mapping information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $Date: 2007-07-03 18:26:44 $
 * @since caAdapter v1.2
 */

public class MappingImpl implements Mapping
{
	private static final String LOGID = "$RCSfile: MappingImpl.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MappingImpl.java,v 1.1 2007-07-03 18:26:44 wangeug Exp $";

	private List<Map> maps = new ArrayList<Map>();
	private List<FunctionComponent> functionComponents = new ArrayList<FunctionComponent>();
	private BaseComponent sourceComponent = null;
	private BaseComponent targetComponent = null;

	/**
	 * Constructors.
	 */
	public MappingImpl()
	{
	}

	public MappingImpl(List<Map> maps, List<FunctionComponent> functionComponents,
					   BaseComponent sourceComponent, BaseComponent targetComponent)
	{
		this.maps = maps;
		this.functionComponents = functionComponents;
		this.sourceComponent = sourceComponent;
		this.targetComponent = targetComponent;
	}

	/**
	 * Setters and Getters.
	 */
	public List<Map> getMaps()
	{
		return maps;
	}

	public void setMaps(List<Map> maps)
	{
		this.maps = maps;
	}

	/**
	 * Return a list of function component in this mapping.
	 * This function never return null. If nothing exists, will return an empty list.
	 * @return
	 */
	public List<FunctionComponent> getFunctionComponent()
	{
		if(functionComponents==null)
		{
			functionComponents = new ArrayList<FunctionComponent>();
		}
		return functionComponents;
	}

	public void setFunctionComponent(List<FunctionComponent> functionComponents)
	{
		this.functionComponents = functionComponents;
		if (this.functionComponents == null)
		{
			this.functionComponents = new ArrayList<FunctionComponent>();
		}
	}

	public BaseComponent getSourceComponent()
	{
		return sourceComponent;
	}

	public void setSourceComponent(BaseComponent sourceComponent)
	{
		this.sourceComponent = sourceComponent;
	}

	public BaseComponent getTargetComponent()
	{
		return targetComponent;
	}

	public void setTargetComponent(BaseComponent targetComponent)
	{
		this.targetComponent = targetComponent;
	}

	/**
	 * Convenience Methods.
	 */
	public void addMap(Map m)
	{
		this.maps.add(m);
	}

	public void removeMap(Map m)
	{
		this.maps.remove(m);
	}

	public void removeAllMaps()
	{
		this.maps.clear();
	}

	public void addFunctionComponent(FunctionComponent f)
	{
		this.functionComponents.add(f);
	}

	public void removeFunctionComponent(FunctionComponent f)
	{
		this.functionComponents.remove(f);
	}

	public void removeAllFunctionComponents()
	{
		this.functionComponents.clear();
	}

    public FunctionComponent getFunctionComponent(String uuid){
        List<FunctionComponent> fc = getFunctionComponent();
        for (int i = 0; i < fc.size(); i++) {
            FunctionComponent functionComponent =  fc.get(i);
            if(functionComponent.getUUID().equalsIgnoreCase(uuid)){
                return functionComponent;
            }
        }
        return null;
    }
}
