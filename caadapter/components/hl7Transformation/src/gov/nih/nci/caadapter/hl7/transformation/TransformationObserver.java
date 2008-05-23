package gov.nih.nci.caadapter.hl7.transformation;
/**
 * Define the method to watch progress of a transformation service 
 * @author wangeug
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
