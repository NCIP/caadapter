/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.util;

import gov.nih.nci.caadapter.dvts.common.Message;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 13, 2011
 * Time: 5:54:10 PM
 * To change this template use File | Settings | File Templates.
 */

public class MessageResources
{
    public static Map<String, String> VALIDATION_MESSAGES = null;

    private static void initialize()
    {
        VALIDATION_MESSAGES = new HashMap<String, String>();

        VALIDATION_MESSAGES.put("GEN0", "An unexpected error occurred ({0}).");
        VALIDATION_MESSAGES.put("GEN1", "Input is null.");
        VALIDATION_MESSAGES.put("GEN2", "Input \"{0}\" is not a valid integer.");
        VALIDATION_MESSAGES.put("GEN3", "Open action failed because of unexpected error or invalid file type.");
        VALIDATION_MESSAGES.put("SCM1", "Two or more segments in the same source specification file have the same name: {0}");
        VALIDATION_MESSAGES.put("SCM2", "Two or more fields in the same segment have the same name: {0}.{1}");
        VALIDATION_MESSAGES.put("SCM3", "Segment has no fields defined in source specification: {0}");
        VALIDATION_MESSAGES.put("SCM4", "Segment name should be composed of letters in CAPs (A-Z), numbers, or the underscore character: \"{0}\"");
        VALIDATION_MESSAGES.put("EMP_FT", "{0}");
        VALIDATION_MESSAGES.put("EMP_ER", "{0}");
        VALIDATION_MESSAGES.put("EMP_WN", "{0}");
        VALIDATION_MESSAGES.put("EMP_IN", "{0}");
        VALIDATION_MESSAGES.put("SCM5", "Root segment still has default name: {0}");
        VALIDATION_MESSAGES.put("SCM6", "Segment still has default name: {0}");
        VALIDATION_MESSAGES.put("SCM7", "Field still has default name: {0}.{1}");
        VALIDATION_MESSAGES.put("SCM8", "Field name should should be composed of letters (A-Z or a-z), numbers, or the underscore character: {0}.{1}");
        VALIDATION_MESSAGES.put("CSV2", "Extraneous segment found in CSV data that is not defined in source specification: {0}");
        VALIDATION_MESSAGES.put("CSV3", "Too many fields in CSV data segment: {0} Found: {1} Expecting: {2}");
        VALIDATION_MESSAGES.put("CSV4", "Could not find parent: \"{1}\" for segment: \"{0}\" at row: {2}");
        VALIDATION_MESSAGES.put("HSM1", "Abstract data type has not been specialized: Element:{0}; Data Type:{1}");
        VALIDATION_MESSAGES.put("HSM1INFO", "ABSTRACT specialized for: {0}; Data Type:{1}; Speciliazed Type:{2}");
        VALIDATION_MESSAGES.put("HSM2", "Choice has not been selected: {0}");
        VALIDATION_MESSAGES.put("HSM3", "CHOICE selected: {0}");
        VALIDATION_MESSAGES.put("HSM4", "Have you built out enough occurrences of MULTIPLES to accommodate mappings from source data?");
        VALIDATION_MESSAGES.put("HSM4INFO", "MULTIPLES added for: {0}; Occurence(index): {1} to accommodate mappings from source data");
        VALIDATION_MESSAGES.put("HSM5", "Have you built out enough CLONE levels to accommodate mappings from source data?");
        VALIDATION_MESSAGES.put("HSM6", "Have you defined DEFAULT VALUES for all elements where needed data is not supplied by the source data file?");
        VALIDATION_MESSAGES.put("HSM6INFO", "DEFAULT VALUES defined for element \"{0}\" with value \"{1}\"");
        VALIDATION_MESSAGES.put("MAP1", "\"{0}\" is not a valid \"{1}\" type of Object for mapping.");
        VALIDATION_MESSAGES.put("MAP2", "\"{0}\" and \"{1}\" is not a valid pair for mapping.");
        VALIDATION_MESSAGES.put("MAP3", "Source field reference in map is invalid and has been deleted - mapping was to target field: {0}");
        VALIDATION_MESSAGES.put("MAP4", "Target field reference in map is invalid and has been deleted - mapping was from source field: {0}.{1}");
        VALIDATION_MESSAGES.put("MAP5", "Mandatory target element has no mapping and no default value defined: {0}");
        VALIDATION_MESSAGES.put("MAP6", "Function input parameter has no mapping: {0}.{1}");
        VALIDATION_MESSAGES.put("MAP7", "Function output parameter has no mapping: {0}.{1}");
        VALIDATION_MESSAGES.put("MAP8", "Target field has both mapping and user-defined default value: {0}");
        VALIDATION_MESSAGES.put("MAP9", "Source field is not mapped: {0}.{1}");
        VALIDATION_MESSAGES.put("MAP10", "Component has not been specified: {0}");
        VALIDATION_MESSAGES.put("MAP11", "There is no mapping data in this map file: {0}");
        VALIDATION_MESSAGES.put("MAP12", "Object is not mappable: {0}");
        VALIDATION_MESSAGES.put("MAP13", "Function mapped to multiple segments: {0}");
        VALIDATION_MESSAGES.put("MAP14", "Invalid scs filename: {0}");
        VALIDATION_MESSAGES.put("MAP15", "Invalid h3s file name: {0}");
        VALIDATION_MESSAGES.put("MAP16", "Function error: {0}");
        VALIDATION_MESSAGES.put("MAP17", "Conceptual element (i.e. CSV Segment, Clone, Attribute, etc.) should not be mapped to Function: {0}");
        VALIDATION_MESSAGES.put("RIM1", "HL7 HMD file is not founded for message id {0}");
        VALIDATION_MESSAGES.put("RIM2", "Can not load message type for message id {0}");
        VALIDATION_MESSAGES.put("RIM3", "HL7 v3 Message Type Id: {0} is not equals to H3S Data Object Message Id: {1}");
        VALIDATION_MESSAGES.put("RIM4", "H3S Data Object {2} has {3} attribute(s), but HL7 Attribute {0} specifies cardinality {1}");
        VALIDATION_MESSAGES.put("RIM5", "H3S Data Object {2} has {3} clones(s), but HL7 Association {0} specifies cardinality {1}");
        VALIDATION_MESSAGES.put("RIM6", "Attribute {0} datatype {1} has bad mapping value: {2}");
        VALIDATION_MESSAGES.put("RIM7", "Two or more choices are selected under the choice group {1}");
        VALIDATION_MESSAGES.put("XML1", "XML {0} is not well formed: {1}");
        VALIDATION_MESSAGES.put("XML2", "XML {0} is not valid against {1}: \n {2}");
        VALIDATION_MESSAGES.put("XML3", "XML is valid against {0}.");
        VALIDATION_MESSAGES.put("XML4", "HL7 v3 message is successfully generated!");
        VALIDATION_MESSAGES.put("XML5", "HL7 v3 message is generated! But xml validation error(s) found against {0}.");
        VALIDATION_MESSAGES.put("TRF1", "Transform from CSV to HL7 v3 Data Error:{0}");
        VALIDATION_MESSAGES.put("TRF2", "Sorry, we can not generate HL7 v3 message, please consult the validation message or the log file for more detail");
        VALIDATION_MESSAGES.put("O2DB1", "Attribute {0} is not mapped!");
        VALIDATION_MESSAGES.put("O2DB2", "Association {0} is not mapped!");
        VALIDATION_MESSAGES.put("O2DB3", "Object {0} is not mapped!");
        VALIDATION_MESSAGES.put("O2DB4", "Inherited Attribute {0} is not mapped, please verify its mapping in super class: {1}.");
        VALIDATION_MESSAGES.put("O2DB5", "Super class {0} is not mapped, please verify the mapping of its child class(es).");
        VALIDATION_MESSAGES.put("O2DB6", "Attribute {0} is not mapped, but the super class {1} is extended. Please verify the mapping of derived attribute in child class(es).");
        VALIDATION_MESSAGES.put("HL7TOCSV0", "Transformation service ... {0}.");
        VALIDATION_MESSAGES.put("HL7TOCSV1", "CSV segment has not been found for field: {0} ... value: {1}");
        VALIDATION_MESSAGES.put("HL7TOCSV2", "Error message {0} datatype {1} value: {2}");
        VALIDATION_MESSAGES.put("HL7TOCSV3", "Warning ... {0}");
        VALIDATION_MESSAGES.put("HL7TOCSV4", "Fatal message type:{0} ... value: {1}");
    }

    public static String getMessageTemplate(String id){
        if (VALIDATION_MESSAGES == null) initialize();
        return VALIDATION_MESSAGES.get(id);
    }

    public static Message getMessage(String id, Object[] args){
        if (VALIDATION_MESSAGES == null) initialize();
        return new Message(VALIDATION_MESSAGES.get(id), args);
    }

 }
 // The End of Messages Resource file
