/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.validation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to validate CSV metadata (scs files).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $Date: 2008-06-09 19:53:50 $
 * @since caAdapter v1.2
 */
public class CSVMetaValidator extends Validator
{
	private static final String LOGID = "$RCSfile: CSVMetaValidator.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/CSVMetaValidator.java,v 1.2 2008-06-09 19:53:50 phadkes Exp $";

	private List<CSVSegmentMeta> segmentArray = null;

	//beginning and ending with all capital letters, numbers, or underscore character
	public static final String SEGMENT_NAME_PATTERN = "^[A-Z0-9_]+$";
	//beginning and ending with all letters, numbers, or underscore character
	public static final String FIELD_NAME_PATTERN = "^[A-Za-z0-9_]+[A-Za-z0-9_-]*$";

	public CSVMetaValidator(CSVMeta toBeValidatedObject)
	{
		super(toBeValidatedObject);
	}

	public ValidatorResults validate()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		validatorResults.addValidatorResults(ScmRule1());
		validatorResults.addValidatorResults(ScmRule2());
		validatorResults.addValidatorResults(ScmRule3());
		validatorResults.addValidatorResults(ScmRule4());
		validatorResults.addValidatorResults(ScmRule5());
		validatorResults.addValidatorResults(ScmRule6());
		validatorResults.addValidatorResults(ScmRule7());
		validatorResults.addValidatorResults(ScmRule8());
		return validatorResults;
	}

	/**
	 * 2 or more segments with same name in SCM
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule1()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		// a hash that contains segmentnames and segmentmetas
		Hashtable<String, CSVSegmentMeta> table = new Hashtable<String, CSVSegmentMeta>();
		for (int i = 0; i < segmentArray.size(); i++)
		{
			// does the table contain a segment with this name already?
			if (table.containsKey(segmentArray.get(i).getName()))
			{
				Message msg = MessageResources.getMessage("SCM1", new Object[]{segmentArray.get(i).getName()});
				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			}
			else
			{
				table.put(segmentArray.get(i).getName(), segmentArray.get(i));
			}
		}
		return validatorResults;
	}

	/**
	 * 2 or more fields with same name in same segment in SCM (case-insensitive)
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule2()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		for (int i = 0; i < segmentArray.size(); i++)
		{
			// a hash that contains fieldnames and fieldmetas
			Hashtable<String, CSVFieldMeta> table = new Hashtable<String, CSVFieldMeta>();
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			List<CSVFieldMeta> fields = csvSegmentMeta.getFields();
			for (int j = 0; j < fields.size(); j++)
			{
				CSVFieldMeta field = fields.get(j);
				// does the table contain a segment with this name already?
				if (table.containsKey(field.getName()))
				{
					Message msg = MessageResources.getMessage("SCM2", new Object[]{csvSegmentMeta.getName(),field.getName()});
					validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
				}
				else
				{
					table.put(field.getName(), field);
				}
			}
		}
		return validatorResults;
	}

	/**
	 * segment with no fields defined in SCM
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule3()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		for (int i = 0; i < segmentArray.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			if (csvSegmentMeta.getFields().size() < 1)
			{
				Message msg = MessageResources.getMessage("SCM3", new Object[]{csvSegmentMeta.getName()});
				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
			}
		}
		return validatorResults;
	}

	/**
	 * Segment name not as beginning and ending with all capital letters, numbers, or underscore character.
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule4()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
//		Pattern pattern = Pattern.compile(SEGMENT_NAME_PATTERN);
		for (int i = 0; i < segmentArray.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			validatorResults.addValidatorResults(validateSegmentMetaName(csvSegmentMeta));
//			String segmentName = csvSegmentMeta.getName();
//			Matcher matcher = pattern.matcher(segmentName);
//			if (!matcher.matches())
//			{
//				Message msg = MessageResources.getMessage("SCM4", new Object[]{segmentName});
//				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
//			}
		}
		return validatorResults;
	}

	/**
	 * root segment with default root segment name in SCM (different default name than regular segment)
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule5()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		CSVSegmentMeta rootsegment = ((CSVMeta) toBeValidatedObject).getRootSegment();
		if (rootsegment.getName().equalsIgnoreCase("root"))
		{
			Message msg = MessageResources.getMessage("SCM5", new Object[]{rootsegment.getName()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
		}
		return validatorResults;
	}

	/**
	 * segment with default segment name in SCM
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule6()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		return validatorResults;
	}

	/**
	 * field with default field name in SCM
	 *
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule7()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		for (int i = 0; i < segmentArray.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			List<CSVFieldMeta> fields = csvSegmentMeta.getFields();
			for (int j = 0; j < fields.size(); j++)
			{
				CSVFieldMeta csvFieldMeta = fields.get(j);
				if (csvFieldMeta.getName().equalsIgnoreCase("FieldName"+csvFieldMeta.getColumn()))
				{
					Message msg = MessageResources.getMessage("SCM7", new Object[]{csvSegmentMeta.getName(), csvFieldMeta.getName()});
					validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
				}
			}
		}
		return validatorResults;
	}

	/**
	 * field name validation as beginning and ending with all letters, numbers, or underscore character.
	 * @return ValidatorResults
	 */
	public ValidatorResults ScmRule8()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		initSegmentArray();
		for (int i = 0; i < segmentArray.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			List<CSVFieldMeta> fields = csvSegmentMeta.getFields();
			for (int j = 0; j < fields.size(); j++)
			{
				CSVFieldMeta csvFieldMeta = fields.get(j);
				validatorResults.addValidatorResults(validateFieldMetaName(csvFieldMeta));
			}
		}
		return validatorResults;
	}

	/**
	 * Validate individual CSVFieldMeta's name.
	 * @param csvFieldMeta
	 * @return ValidatorResults
	 */
	public static ValidatorResults validateFieldMetaName(CSVFieldMeta csvFieldMeta)
	{
		ValidatorResults validatorResults = new ValidatorResults();
		if(csvFieldMeta==null)
		{
			Message msg = MessageResources.getMessage("GEN1", new Object[]{});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
		}
		else
		{
			Pattern pattern = Pattern.compile(FIELD_NAME_PATTERN);
			String fieldName = csvFieldMeta.getName();
			Matcher matcher = pattern.matcher(fieldName);
			if (!matcher.matches())
			{
				Message msg = MessageResources.getMessage("SCM8", new Object[]{csvFieldMeta.getSegmentName(),fieldName});
				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			}
		}
		return validatorResults;
	}

	/**
	 * Validate individual CSVSegmentMeta's name.
	 * @param csvSegmentMeta
	 * @return ValidatorResults
	 */
	public static ValidatorResults validateSegmentMetaName(CSVSegmentMeta csvSegmentMeta)
	{
		ValidatorResults validatorResults = new ValidatorResults();
		if(csvSegmentMeta==null)
		{
			Message msg = MessageResources.getMessage("GEN1", new Object[]{});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
		}
		else
		{
			Pattern pattern = Pattern.compile(SEGMENT_NAME_PATTERN);
			String fieldName = csvSegmentMeta.getName();
			Matcher matcher = pattern.matcher(fieldName);
			if (!matcher.matches())
			{
				Message msg = MessageResources.getMessage("SCM4", new Object[]{fieldName});
				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			}
		}
		return validatorResults;
	}

	private void initSegmentArray()
	{
		if (segmentArray == null)
		{
			segmentArray = new ArrayList<CSVSegmentMeta>();
			CSVSegmentMeta rootsegment = ((CSVMeta) toBeValidatedObject).getRootSegment();
			segmentArray.add(rootsegment);
			segmentArray.addAll(getRecursiveChildren(rootsegment));
		}
	}

	private List<CSVSegmentMeta> getRecursiveChildren(CSVSegmentMeta c)
	{
		List<CSVSegmentMeta> returnArray = new ArrayList<CSVSegmentMeta>();
		List<CSVSegmentMeta> childSegments = c.getChildSegments();
		for (int i = 0; i < childSegments.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = childSegments.get(i);
			// add the child.
			returnArray.add(csvSegmentMeta);
			// add the child's children.
			returnArray.addAll(getRecursiveChildren(csvSegmentMeta));
		}
		return returnArray;
	}
}
