/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.function;

import gov.nih.nci.cbiit.cmts.common.ApplicationException;



/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */

public class FunctionException extends ApplicationException
{
    int errorNumber;

    public FunctionException(String message){
        super(message);
    }

    public FunctionException(String message, Throwable cause)
    {
        super(message, cause);
        errorNumber = -1;
    }

    public FunctionException(String message, Throwable cause, String severity)
    {
        super(message, cause, severity);
        errorNumber = -1;
    }
    public FunctionException(String message, int errorNum, Throwable cause, String severity)
    {
        super(message, cause, severity);
        errorNumber = errorNum;
    }
    public int getErrorNumber()
    {
        return errorNumber;
    }
    public String getErrorCause()
    {
       return getErrorCause(errorNumber);
    }
    public String getErrorCause(int i)
    {
       String ca = "";
        // These errors are occurred in the FunctionValidator class
       if (i == 101) ca = "This Parameter is not allowed an array instance.";
       else if (i == 105) ca = "Null Pointer Exception";// + ne.getMessage(), 105
       else if (i == 107) ca = "Index Out Of Bound Exception";// + ie.getMessage(), 107
       else if (i == 109) ca = "Invalid Function Group Name";//, 109
       else if (i == 111) ca = "Invalid Function Name in Function Group";//, 111
       else if (i == 113) ca = "No argument with function.";//, 113
       else if (i == 115) ca = "Invlaid datatype";
       else if (i == 117) ca = "Too many arguments.";
       else if (i == 119) ca = "Not number format string";
       else if (i == 121) ca = "Argument is not enough.";
       else if (i == 123) ca = "Float variable parameter can be allowed only math group functions.";// + calcData[i];
       // These errors are occurred in the ExecuteFunction class
       else if (i == 203) ca = "Substring start point cannot be less than zero.";//, 203
       else if (i == 205) ca = "Invalid substring parameter format : the end point value is less than the start.";//, 205
       else if (i == 207) ca = "Invalid substring parameter format : Start point is greater than string length.";//, 207
       else if (i == 209) ca = "Class Cast Exception (string)";// + ce.getMessage(), 209
       else if (i == 211) ca = "Null Pointer Exception (string)";// + ne.getMessage(), 211
       else if (i == 213) ca = "Index Out Of Bounds Exception (string)";// + ie.getMessage(), 213
       // These errors are occurred in the MathFunction class
       else if (i == 303) ca = "Divided by a zero value error";// 303
       else if (i == 305) ca = "Invalid data type for Math functions";//, 305
       else if (i == 307) ca = "This String data is not number format";// + src, 307
       // These errors are occurred in the DateFunction class
       else if (i == 505) ca = "Illegal Date Format";// + sdfStr , 505
       else if (i == 507) ca = "The given date Format String is null value";//, 507
       else if (i == 509) ca = "Invalid date Format in date String";// + dateStr, 509
       else if (i == 511) ca = "Year, Month or day data is needed.";// + dateStr, 509
       // These errors are occurred in the compute() method of the FunctionMetaImpl class
       else if (i == 603) ca = "Not implemented Function";//, 603
       else if (i == 605) ca = "Illegal Access Exception";//, 605,
       else if (i == 607) ca = "Invocation Target Exception";//, 607,
       else if (i == 609) ca = "Wrong number of arguments passed into this function";//, 609
       else if (i == 611) ca = "Wrong data tpye of arguments";//, 611
       else if (i == 613) ca = "Function Class not found";//, 613
       else if (i == 615) ca = "Null Pointer Exception";//, 615
       else if (i == 617) ca = "Array Index Out Of Bounds Exception";//, 617
       else if (i == 701) ca = "Not exist vocabulary mapping file.";//, 617
       else if (i == 702) ca = "This vocabulary mapping path is not a file.";
       else if (i == 703) ca = "Vocabulary mapping file reading error. : ";
       else if (i == 705) ca = "Vocabulary mapping search failure.";
       else if (i == 707) ca = "Vocabulary type must be either 'FileName' or 'URL'. : ";
       else if (i == 708) ca = "Domain name is not given. : ";
       else if (i == 709) ca = "This domain name is not found. : ";
       else if (i == 710) ca = "This is not a valid Vocabulary mapping type(forward) : ";
       else if (i == 711) ca = "This is not a valid Vocabulary mapping type(inverse) : ";
       else if (i == 714) ca = "This is not a Vocabulary Mapping file found. : ";
       else if (i == 715) ca = "FunctionVocabularyMappingEventHandler ConnectException : ";// + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
       else if (i == 716) ca = "FunctionVocabularyMappingEventHandler IOException : ";// + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
       else if (i == 717) ca = "FunctionVocabularyMappingEventHandler Unknown Exception : ";// + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
       else if (i == 718) ca = "FunctionVocabularyMappingEventHandler (Invalid Data) : ";// + message, 718, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
       else if (i == 720) ca = "URL cannot be applied in case of inverse mapping : ";
       else if (i == 722) ca = "Mis-assigning inverse mapping : ";
       else if (i == 724) ca = "Mis-assigning forward mapping : ";
       else ca = "";

       return ca;
    }
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

