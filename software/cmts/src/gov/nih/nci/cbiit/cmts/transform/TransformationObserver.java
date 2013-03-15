/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;
/**
 * Define the method to watch progress of a transformation service
 *  
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 *
 */
public interface TransformationObserver {
	public static int TRANSFORMATION_DATA_LOADING_START=0;
	public static int TRANSFORMATION_DATA_LOADING_READ_MAPPING=1;
	public static int TRANSFORMATION_DATA_LOADING_PARSER_MAPPING=2;
	public static int TRANSFORMATION_DATA_LOADING_READ_H3S_FILE=3;
	public static int TRANSFORMATION_DATA_LOADING_READ_SOURCE=4;
	public static int TRANSFORMATION_DATA_LOADING_PARSER_SOURCE=5;
	public static int TRANSFORMATION_DATA_LOADING_READ_CVS_META=6;

	public static int TRANSFORMATION_DATA_LOADING_COUNT_MESSAGE=7;

	public static int TRANSFORMATION_DATA_LOADING_STEPS=8;
	public static String TRANSFORMATION_MESSAGE_GENERATING_STEP="Loading Data ...";

	/**
	 * Notify the transformation observer for the progress with completion percentage
	 * Range:0-100
	 * @param completionPercent
	 */
	public void progressUpdate(int completionPercent);

	/**
	 * Inform the HL7 transformation request is cancelled 
	 * @return
	 */
	public boolean isRequestCanceled();


	/*
	 * Set the total number of HL7 message with a transformation service request
	 * @param count The number of messages 
	 */
	public void setMessageCount(int count);

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
