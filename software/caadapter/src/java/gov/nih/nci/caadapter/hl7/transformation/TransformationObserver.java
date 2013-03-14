/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.transformation;
/**
 * Define the method to watch progress of a transformation service
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 * revision    $Revision: 1.8 $
 * date        $Date: 2008-10-29 19:08:50 $
 */

public interface TransformationObserver {
public static int TRANSFORMATION_DATA_LOADING_START=0;
public static int TRANSFORMATION_DATA_LOADING_READ_MAPPING=1;
public static int TRANSFORMATION_DATA_LOADING_PARSER_MAPPING=2;
public static int TRANSFORMATION_DATA_LOADING_READ_H3S_FILE=3;
public static int TRANSFORMATION_DATA_LOADING_READ_CVS_META=4;
public static int TRANSFORMATION_DATA_LOADING_READ_SOURCE=5;
public static int TRANSFORMATION_DATA_LOADING_PARSER_SOURCE=6;
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
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.7  2008/09/29 15:40:38  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */