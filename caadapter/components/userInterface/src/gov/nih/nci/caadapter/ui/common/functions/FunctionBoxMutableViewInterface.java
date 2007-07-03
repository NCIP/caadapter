/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionBoxMutableViewInterface.java,v 1.2 2007-07-03 18:56:04 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.BaseObject;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;

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
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-03 18:56:04 $
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
