/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import java.awt.Component;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-10 19:58:16 $
 * @since caAdapter v4.2
 */

public abstract class ItemAnnotationAction extends AbstractContextAction {
	private MappingMiddlePanel middlePanel;
	private MetaObject metaAnnoted;
	private int annotationActionType=0;
	public ItemAnnotationAction(String actionName,int actionType, MappingMiddlePanel midPane)
	{
		super(actionName);
		middlePanel=midPane;
		annotationActionType=actionType;
		this.setEnabled(false);
	}

	/**
	 * @return the annotationActionType
	 */
	public int getAnnotationActionType() {
		return annotationActionType;
	}

	/**
	 * @param annotationActionType the annotationActionType to set
	 */
	public void setAnnotationActionType(int annotationActionType) {
		this.annotationActionType = annotationActionType;
	}

	/**
	 * @return the metaAnnoted
	 */
	public MetaObject getMetaAnnoted() {
		return metaAnnoted;
	}

	/**
	 * @param metaAnnoted the metaAnnoted to set
	 */
	public void setMetaAnnoted(MetaObject metaAnnoted) {
		this.metaAnnoted = metaAnnoted;
	}

	/**
	 * @return the middlePanel
	 */
	public MappingMiddlePanel getMiddlePanel() {
		return middlePanel;
	}

	/**
	 * @param middlePanel the middlePanel to set
	 */
	public void setMiddlePanel(MappingMiddlePanel middlePanel) {
		this.middlePanel = middlePanel;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#doAction(java.awt.event.ActionEvent)
	 */
	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#getAssociatedUIComponent()
	 */
	@Override
	protected Component getAssociatedUIComponent() {
		// TODO Auto-generated method stub
		return middlePanel;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/