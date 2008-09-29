/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.map.BaseComponent;

import java.util.List;

/**
 * The primary interface which contains all Mapping information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-09-29 15:47:19 $
 */

public interface Mapping
{
	//define mapping type
	public String getMappingType();
	public void setMappingType(String newType);
    // source stuff
	public BaseComponent getSourceComponent();
	public void setSourceComponent(BaseComponent sourceComponent);
    // target stuff
	public BaseComponent getTargetComponent();
	public void setTargetComponent(BaseComponent targetComponent);
    // map stuff
	public List<Map> getMaps();
	public void setMaps(List<Map> maps);
    public void addMap(Map m);
    public void removeMap(Map m);
	public void removeAllMaps();
    // function stuff
	/**
	 * Return a list of function component in this mapping.
	 * This function never return null. If nothing exists, will return an empty list.
	 * @return
	 */
	public List<FunctionComponent> getFunctionComponent();
	public void setFunctionComponent(List<FunctionComponent> functionComponent);
    public void addFunctionComponent(FunctionComponent f);
    public void removeFunctionComponent(FunctionComponent f);
	public void removeAllFunctionComponents();
    public FunctionComponent getFunctionComponent(String uuid);
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */