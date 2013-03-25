/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.properties.PropertiesSwitchController;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;

/**
 * Defines the interface that an implementation of UI mapping data manager needs to
 * provide to facilitate other parties to take real actions against underline mapping data.
 * Current implementation is a JGraph based controller, but it may change at later time.
 *
 * This interface will then help isolate the underline implementation technology from back-end
 * mapping handling.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public interface MappingDataManager
{
	/**
	 * Add the specified function at the specific start location.
	 * @param function
	 * @param startPoint
	 * @return true if function is successfully added.
	 */
	boolean addFunction(FunctionMeta function, Point2D startPoint);

	/**
	 * Create mapping relation between the source and target nodes.
	 * @param sourceNode
	 * @param targetNode
	 * @return true if mapping is successfully created.
	 */
	boolean createMapping(MappableNode sourceNode, MappableNode targetNode);

	void setMappingData(Mapping mappingData);

    void setMappingData(Mapping mappingData, boolean flag);

    /**
	 * Get mapping relation consolidated.
	 * @param refresh if true, the underline implementation will refresh data from user's input; otherwise, it
	 * will return what it has now, which may not be up-to-date;
	 * @return the mapping object.
	 */
	Mapping retrieveMappingData(boolean refresh);

	/**
	 * Call this method only if you do not have a base component handy;
	 * otherwise, call the overloaded function, so as to help you preserve your UUID value.
	 * @param metaInfo
	 * @param file
	 */
	void registerSourceComponent(MetaObject metaInfo, File file);

	void registerSourceComponent(BaseComponent sourceComponent);

	/**
	 * Call this method only if you do not have a base component handy;
	 * otherwise, call the overloaded function, so as to help you preserve your UUID value.
	 * @param metaInfo
	 * @param file
	 */
	void registerTargetComponent(MetaObject metaInfo, File file);

	void registerTargetComponent(BaseComponent targetComponent);

	boolean isGraphChanged();

	void setGraphChanged(boolean newValue);

	void clearAllGraphCells();

	PropertiesSwitchController getPropertiesSwitchController();

	/**
	 *
	 * @param node
	 * @param searchMode any of the SEARCH_BY constants defined above.
	 * @return a list of MappingViewCommonComponent if any being found; an empty list if nothing is found.
	 */
	List<MappingViewCommonComponent> findMappingViewCommonComponentList(Object node, String searchMode);
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/19 17:49:02  jayannah
 * HISTORY      : Changes to add link selection highlighter for the map file
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 19:05:40  wangeug
 * HISTORY      : relocate "BaseMappingCompoent" object from  other package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/09/26 15:57:11  wuye
 * HISTORY      : add new function to clear all cells
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/09/27 21:47:59  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/22 21:35:29  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/05 20:35:54  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 22:22:18  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
