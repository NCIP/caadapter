/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.validation;

import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMetaTree;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaTreeImpl;
import gov.nih.nci.caadapter.common.csv.CsvCache;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVDataTreeImpl;
import gov.nih.nci.caadapter.common.csv.data.CSVDataTree;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       5:59:39 PM $
 */
/**
 * Used to validate CSV metadata (scs files).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $Date: 2008-06-09 19:53:50 $
 * @since caAdapter v1.2
 */
public class CSVMetaValidator extends Validator
{
	private static final String LOGID = "$RCSfile: CSVMetaValidator.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/validation/CSVMetaValidator.java,v 1.3 2008-06-09 19:53:50 phadkes Exp $";

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

        /*
        initSegmentArray();
        for (int i = 0; i < segmentArray.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentArray.get(i);
			if (csvSegmentMeta.getFields().size() < 1)
			{
                if (!csvSegmentMeta.isChoiceSegment())
                {
                    Message msg = MessageResources.getMessage("SCM3", new Object[]{csvSegmentMeta.getName()});
				    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
                }
            }
		}
        */
        try
        {
            CSVMetaTree treeMeta = new CSVMetaTreeImpl();

            treeMeta.build((CSVMeta) toBeValidatedObject);

            validatorResults.addValidatorResults(treeMeta.validateTree());

        }
        catch(ApplicationException ae)
        {
            Message msg = MessageResources.getMessage("EMP_ER", new Object[]{ae.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            //ae.printStackTrace();
            //return validatorResults;
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
	 * Cardinality validation between CSVMeta and CSV data
     * @param dataFile, csv data file in instance of String or File
     *
	 * @return ValidatorResults
	 */
    public ValidatorResults ScmRule9(Object dataFile)
	{
        Exception ee;
        try
        {
            String[][] data = null;
            if (dataFile instanceof String)
            {
                data = CsvCache.getCsv((String) dataFile);
            }
            else if (dataFile instanceof File)
            {
                data = CsvCache.getCsv(((File) dataFile).getPath());
            }
            else throw new IOException("Invalid CSV data Instance : " + dataFile.toString());
            return ScmRule9(data);
    	}
        catch (FileNotFoundException e)
        {    ee = e; }
        catch (IOException e)
        {    ee = e; }

        ValidatorResults validatorResults = new ValidatorResults();
        Log.logException(this, ee);
        Message msg = MessageResources.getMessage("GEN0", new Object[]{ee.getMessage()});
        validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
        return validatorResults;

    }
    public ValidatorResults ScmRule9(String[][] csvData)
	{
		ValidatorResults validatorResults = new ValidatorResults();

        CSVSegmentMeta rootsegment = ((CSVMeta) toBeValidatedObject).getRootSegment();
        java.util.List<String> segmentNameList = new java.util.ArrayList<String>();

        for (int i = 0; i < csvData.length; i++)
        {
                String[] row = csvData[i];
                String segmentName = row[0];
                segmentNameList.add(segmentName);
        }
        List<String> res = validateCardinality(rootsegment, segmentNameList);
        if (res != null)
        {
            for(int i=0;i<res.size();i++)
            {
                Message msg = MessageResources.getMessage("EMP_ER", new Object[]{res.get(i)});
			    validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            }
        }

        return validatorResults;
	}

    public ValidatorResults ScmRule10(String[][] csvData)
	{
		ValidatorResults validatorResults = new ValidatorResults();


        CSVMeta csvMeta = (CSVMeta) toBeValidatedObject;
        CSVSegmentMeta rootsegment = csvMeta.getRootSegment();

        String headSegmentName = csvData[0][0];
        if (!headSegmentName.equals(rootsegment.getName()))
        {
            Message msg = MessageResources.getMessage("EMP_ER", new Object[]{"This data is not started with the head segment name("+rootsegment.getName()+"). : " + headSegmentName});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            //ae.printStackTrace();
            return validatorResults;
        }
        int numOfMsgLine = 0;
        List<Integer> numbers = new ArrayList<Integer>();
        int maxLength = 0;
        for(int i=0;i<csvData.length;i++)
        {
            if (headSegmentName.equals(csvData[i][0]))
            {
                if (numOfMsgLine > 0) numbers.add(numOfMsgLine);
                numOfMsgLine = 0;
            }
            numOfMsgLine++;
            if (csvData[i].length > maxLength) maxLength = csvData[i].length;
        }
        if (numOfMsgLine > 0) numbers.add(numOfMsgLine);

        if (numbers.size() == 1)
        {
            return validateCSVDataTree(csvData, "");
        }
        else if (numbers.size() > 1)
        {
            int n = 0;

            for(int i=0;i<numbers.size();i++)
            {
                int num = numbers.get(i);
                String[][] tempStringArr = new String[num][maxLength];
                for(int j=0;j<num;j++)
                {
                    //System.out.println("CCC vv: " + csvData[n][0] + " : " + csvData[n].length);
                    for(int k=0;k<csvData[n].length;k++)
                    {
                        tempStringArr[j][k] = csvData[n][k];
                    }
                    n++;
                }
                validatorResults.addValidatorResults(validateCSVDataTree(tempStringArr, "#" + (i+1) + " Message: "));
            }
        }
        else
        {
            Message msg = MessageResources.getMessage("EMP_ER", new Object[]{"Not found the head segment name("+rootsegment.getName()+")."});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            return validatorResults;
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

    public List<String> validateCardinality(CSVSegmentMeta rootsegment, java.util.List<String> segmentNameList)
    {
        String rootName = rootsegment.getName();
        List<String> li = new ArrayList<String>();
        if ((segmentNameList == null)||(segmentNameList.size()==0))
        {
            li.add("This CSV data is empty.");
            return li;
        }
        if (!rootName.equals(segmentNameList.get(0)))
        {
            li.add("This CSV data doesn't start with root segment(" + rootName + ") : " + segmentNameList.get(0));
            return li;
        }
        if (countSegmentName(rootName, segmentNameList) == 0)
        {
            li.add("Not Found Root segment name : " + rootName);
            return li;
        }
        else if (countSegmentName(rootName, segmentNameList) > 1)
        {
            //return "Found More than 1 times Root segment name : " + rootName;
            java.util.List<String> list = null;
            java.util.List<String> errList = new ArrayList<String>();
            int csvDataCount = 0;
            int i = 0;
            while(true)
            {
                if ((i==segmentNameList.size())||(rootName.equals(segmentNameList.get(i))))
                {
                    if (list != null)
                    {
                        csvDataCount++;
                        List<String> res = recurrsiveValidateCardinality(rootsegment, list);
                        if (res != null)
                        {
                            for(int k=0;k<res.size();k++) errList.add("#" + csvDataCount + " CSV Data : " + res.get(k));
                        }
                    }
                    list = new ArrayList<String>();
                }
                if (i==segmentNameList.size()) break;
                try { list.add(segmentNameList.get(i)); }
                catch(NullPointerException ne)
                {
                    errList.add("#" + csvDataCount + " CSV Data : " + "Invalid Structure of CSV data");
                }
                i++;
            }
            if (errList.size() == 0) return null;
            else return errList;
        }
        else return recurrsiveValidateCardinality(rootsegment, segmentNameList);
    }
    private List<String> recurrsiveValidateCardinality(CSVSegmentMeta segmentMeta, java.util.List<String> segmentNameList)
    {
        if ((segmentNameList == null)||(segmentNameList.size()==0))
        {
            return null;
        }
        java.util.List<CSVSegmentMeta> metas = segmentMeta.getChildSegments();
        List<String> li = new ArrayList<String>();
        for(int i=0;i<metas.size();i++)
        {
            CSVSegmentMeta ameta = metas.get(i);
            CardinalityType cType = ameta.getCardinalityType();
            if (cType.getType() == (new C_segment()).getCardinality().getType()) continue;
            int times = countSegmentName(ameta.getName(), segmentNameList);
            String errMsg = null;
            if (times < ameta.getMinCardinality())
            {
                errMsg = "Under '" + segmentMeta.getName() + "' segment, less or zero '" + ameta.getName() + "' segment in spite of '"+ cType.toString() + "' cardinality.";
            }
            if (times > ameta.getMaxCardinality())
            {
                errMsg = "Under '" + segmentMeta.getName() + "' segment, too many("+times+") '" + ameta.getName() + "' segment in spite of '"+ cType.toString() + "' cardinality.";
            }
            List<String> res = recurrsiveValidateCardinality(ameta, segmentNameList);
            if ((res == null)&&(errMsg == null)) continue;
            if (errMsg != null) li.add(errMsg);
            if (res != null)
            {
                for (int j=0;j<res.size();j++) li.add(res.get(j));
            }
        }
        if (li.size() == 0) return null;
        else return li;
    }
    public int countSegmentName(String segmentName, java.util.List<String> segmentNameList)
    {
        int count = 0;
        String segName = segmentName.trim();
        for (int i=0;i<segmentNameList.size();i++)
        {
            String name = segmentNameList.get(i).trim();
            if (segName.equals(name)) count++;
        }
        return count;
    }
    private ValidatorResults validateCSVDataTree(String[][] csvData, String msgHead)
    {
        ValidatorResults validatorResults = new ValidatorResults();


        CSVDataTree treeData = null;
        try
        {
            CSVMetaTree treeMeta = new CSVMetaTreeImpl();

            treeMeta.build((CSVMeta) toBeValidatedObject);

            //treeMeta.displayTreeWithText();


            treeData = (CSVDataTree)treeMeta.constructInitialDataTree(new CSVDataTreeImpl());
            treeData.inputValuesFromArray(csvData);
            //treeData.displayTreeWithText();
            validatorResults.addValidatorResults(treeData.validateTree());
        }
        catch(ApplicationException ae)
        {
            Message msg = MessageResources.getMessage("EMP_ER", new Object[]{ae.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            //ae.printStackTrace();
            //return validatorResults;
        }

        //treeData.displayTreeWithText();
        if ((msgHead == null)||(msgHead.trim().equals(""))) return validatorResults;
        else return reorganizeMessageWithPrefix(validatorResults, msgHead);
    }

    public ValidatorResults reorganizeMessageWithPrefix(ValidatorResults validatorResults, String msgHead)
    {
        ValidatorResults results = new ValidatorResults();
        for(int i=0;i<validatorResults.getMessages(ValidatorResult.Level.FATAL).size();i++)
        {
            Message oneMesg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(i);
            Message msg = MessageResources.getMessage("EMP_FT", new Object[]{msgHead + oneMesg.toString()});
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
        }
        for(int i=0;i<validatorResults.getMessages(ValidatorResult.Level.ERROR).size();i++)
        {
            Message oneMesg = validatorResults.getMessages(ValidatorResult.Level.ERROR).get(i);
            Message msg = MessageResources.getMessage("EMP_ER", new Object[]{msgHead + oneMesg.toString()});
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        for(int i=0;i<validatorResults.getMessages(ValidatorResult.Level.WARNING).size();i++)
        {
            Message oneMesg = validatorResults.getMessages(ValidatorResult.Level.WARNING).get(i);
            Message msg = MessageResources.getMessage("EMP_WN", new Object[]{msgHead + oneMesg.toString()});
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
        }
        for(int i=0;i<validatorResults.getMessages(ValidatorResult.Level.INFO).size();i++)
        {
            Message oneMesg = validatorResults.getMessages(ValidatorResult.Level.INFO).get(i);
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{msgHead + oneMesg.toString()});
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
        }
        return results;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:40:56  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */
