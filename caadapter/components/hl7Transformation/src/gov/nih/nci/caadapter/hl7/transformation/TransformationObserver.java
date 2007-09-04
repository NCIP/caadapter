package gov.nih.nci.caadapter.hl7.transformation;
/**
 * Define the method to watch progress of a transformation service 
 * @author wangeug
 *
 */
public interface TransformationObserver {
public static int TRANSFORMATION_BEGIN=0;
public static int TRANSFORMATION_FINISH=100;

/**
 * Notify the transformation observer for the progress with completion percentage
 * Range:0-100
 * @param completionPercent
 */
public void progressUpdate(int completionPercent);

/**
 * Inform the transformation observer if the request is valid.
 * @return The status of a transformation request
 */
public boolean isRequestValid();

/**
 * Inform the HL7 transformation request is cancelled 
 * @return
 */
public boolean isRequestCancelled();


/**
 * Inform the transformation observer if the transformation server is ready to process
 * a transformation request.
 * @return The status of a transformation server
 */
public boolean isServiceReady();

/*
 * Set the total number of HL7 message with a transformation service request
 * @param count The number of messages 
 */
public void setMessageCount(int count);


}
