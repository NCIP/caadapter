/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.BaseObject;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;

import javax.swing.*;
import java.util.List;


/**
 * This interface defines a list of contracts that need to be delivered for
 * a functional box view representation. A default implementation of this class
 * is provided by FunctionBoxMutableViewInterfaceImpl.
 *
 * This interface represents a mutable instance of functional box usage.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-11-21 16:18:38 $
 */
public interface FunctionBoxMutableViewInterface extends BaseObject
{
	/**
	 * @return the icon representation of this functional box.
	 */
	Icon getIcon();

	/**
	 * @return the name of this functional box.
	 */
	String getName();

	/**
	 * @return the total number of defined inputs of this functional box.
	 */
	int getTotalNumberOfDefinedInputs();

	/**
	 * @return the total number of defined outputs of this functional box.
	 */
	int getTotalNumberOfDefinedOutputs();

	/**
	 * @return the total number of actual inputs of this functional box.
	 */
	int getTotalNumberOfActualInputs();

	/**
	 * @return the total number of actual outputs of this functional box.
	 */
	int getTotalNumberOfActualOutputs();

	/**
	 * @return the list of inputs of this functional box.
	 */
	List getInputElementList();

	/**
	 * @return the list of outputs of this functional box.
	 */
	List getOutputElementList();

	/**
	 * Return the enclosed function meta object.
	 *
	 * @return a FunctionMeta Object
	 */
	FunctionMeta getFunctionMeta();

	/**
	 * Set a new function meta object.
	 *
	 * @param newFunctionMeta
	 */
	void setFunctionMeta(FunctionMeta newFunctionMeta);

	/**
	 * Return the enclosed view meta object.
	 *
	 * @return a View Object
	 */
	View getViewMeta();

	/**
	 * Set a new view meta object.
	 *
	 * @param newViewMeta
	 */
	void setViewMeta(View newViewMeta);

	/**
	 * Return the associated view object in JGraph.
	 * Could return null if this view has not be put on view yet.
	 *
	 * @return a FunctionBoxCell
	 */
	FunctionBoxCell getFunctionBoxCell();

	/**
	 * Set the function box cell.
	 *
	 * @param newFunctionBoxCell
	 */
	void setFunctionalBoxCell(FunctionBoxCell newFunctionBoxCell);

	/**
	 * Return the embedded function component.
	 *
	 * @param create if true will create a new one given the current one is null.
	 * @return a FunctionComponent
	 */
	FunctionComponent getFunctionComponent(boolean create);

	/**
	 * Set a new function component.
	 * @param functionComponent
	 */
	void setFunctionComponent(FunctionComponent functionComponent);

	/**
	 * Return the function constant.
	 * @return a FunctionConstant
	 */
	FunctionConstant getFunctionConstant();

	/**
	 * Set a new functionConstant.
	 * @param functionConstant
	 */
	void setFunctionConstant(FunctionConstant functionConstant);

	/**
	 * Return the Vocabulary Mapping instance.
	 * @return a VocabularyMapping
	 */
	FunctionVocabularyMapping getFunctionVocabularyMapping();

    /**
	 * Set a new vocabularyMapping.
	 * @param vocabularyMapping
	 */
    void setFunctionVocabularyMapping(FunctionVocabularyMapping vocabularyMapping);
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/11/17 20:10:47  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 18:56:04  wangeug
 * HISTORY      : relocate "View" object from  other package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/09/06 18:28:38  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/22 21:35:27  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:18  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
