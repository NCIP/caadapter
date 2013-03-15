/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.csv.data.impl;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.data.*;
import gov.nih.nci.caadapter.common.csv.meta.*;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
public class CSVSegmentedFileExtension extends CSVSegmentedFileImpl {
	private CSVMeta csvMeta;
	private ValidatorResults transformationResults;

	public void insertCsvField(String csvFieldKey, String csvValue)
	{
//		System.out.println("CSVSegmentedFileExtension.insertCsvField().. field:"+csvFieldKey);
		List parentSegs=findParentSegment(csvFieldKey);
//		System.out.println("CSVSegmentedFileExtension.insertCsvField()...parent:"+parentSegs);

		if (parentSegs.size()==0)
		{
			Message msg = MessageResources.getMessage("HL7TOCSV1", new Object[]{csvFieldKey,csvValue});
//			transformationResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			Log.logError(this, msg);
			return;
		}
		String fieldName=csvFieldKey.substring(csvFieldKey.lastIndexOf(".")+1);
		boolean isNewSegmentRequired=true;
		for (Object parentObj:parentSegs)
		{
			CSVSegmentExtension csvExt=(CSVSegmentExtension)parentObj;
			if (setNewFieldValue(csvExt, fieldName, csvValue))
			{
				isNewSegmentRequired=false;
				break;
			}
		}

		//new parent segment is required, create one and set field value
		if (isNewSegmentRequired)
		{
			CSVSegmentExtension valueSetParentSeg=(CSVSegmentExtension)parentSegs.get(0);
			CSVSegment grandParentSeg=valueSetParentSeg.getParentSegment();
			CSVSegment newParent=createChildSegment(grandParentSeg, valueSetParentSeg.getName());
			setNewFieldValue(newParent, fieldName, csvValue);
		}
	}

	/**
	 * Set new value with CSVMeta
	 * @param userCsvMeta
	 */
	public void setCsvMeta(CSVMeta userCsvMeta) {
		csvMeta = userCsvMeta;
	    CSVSegmentMeta rootSegmentMeta = csvMeta.getRootSegment();
	    CSVSegment rootSeg=recursivlyAddEmptySegmentRecords(rootSegmentMeta);
	    addLogicalRecord(rootSeg);
	}
	public void setBuildCSVResult(ValidatorResults validators)
	{
		transformationResults=validators;
	}
	public ValidatorResults getBuildCSVResults() {
		return transformationResults;
	}

	/**
	 * Find all parent segments given the fieldName. There may be more than one
	 * parent segment found, but only zero or one of the parent with the value not
	 * not being set for the field.
	 * @param csvFieldKey
	 * @return
	 */
	private List findParentSegment(String csvFieldKey)
	{
		String parentKey=csvFieldKey.substring(0, csvFieldKey.lastIndexOf("."));
		CSVSegment rootSeg =getLogicalRecords().get(0);
		StringTokenizer stoken=new StringTokenizer(parentKey,".");
		//skip the root name
		stoken.nextToken();
		List<CSVSegmentExtension> parentSegs=new ArrayList<CSVSegmentExtension>();
		parentSegs.add((CSVSegmentExtension)rootSeg);
		while (stoken.hasMoreElements())
		{
			String childSegName=stoken.nextToken();
			if (parentSegs.isEmpty())
				break;
			else
			{
				List childSegs=new ArrayList<CSVSegmentExtension>();
				for (CSVSegmentExtension parentSeg:parentSegs)
				{
					List<CSVSegment> oneChildList=((CSVSegmentExtension)parentSeg).getChildSegmentsByName(childSegName);
						//findOrCreateChildSegment(parentSeg, childSegName);
					for (CSVSegment oneChildSeg:oneChildList)
						childSegs.add((CSVSegmentExtension)oneChildSeg);
				}
				parentSegs=childSegs;
			}
		}
		return parentSegs;
	}

	/**
	 * Create a new CSVSegment and attached it with its parent segment
	 * @param parentSeg
	 * @param childSegName
	 * @return
	 */
	private CSVSegment createChildSegment (CSVSegment parentSeg, String childSegName)
	{

		CSVSegmentMeta newChildMeta=null;
		if (parentSeg==null)
		{
			//create root segement
			newChildMeta=this.csvMeta.getRootSegment();
		}
		else
		{
			for (CSVSegmentMeta childMeta:((CSVSegmentMeta)parentSeg.getMetaObject()).getChildSegments())
			{
				if (childMeta.getName().equals(childSegName))
					newChildMeta=childMeta;
			}
		}
		CSVSegment newSeg=null;
		if (newChildMeta!=null)
		{
			newSeg=initializeEmptyCsvSegment(newChildMeta);
			if (parentSeg!=null)
				((CSVSegmentExtension)parentSeg).attachDuplicateChildSegment(newSeg);
			else
				getLogicalRecords().add(newSeg); //add to root
		}

		return newSeg;
	}

	/**
	 * Create an empty CSVSegment given it metadata
	 * @param meta
	 * @param transformationResults
	 * @return
	 */
	private CSVSegment initializeEmptyCsvSegment(CSVSegmentMeta meta) {
        CSVSegmentImpl segment = new CSVSegmentExtension(meta);
        segment.setXmlPath(meta.getXmlPath());
		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{"initialize new segment "+meta.getXmlPath()});
		transformationResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
		Log.logInfo(this, msg);
        //setup the fields.
        ArrayList<CSVField> fields = new ArrayList<CSVField>();
        List<CSVFieldMeta> fieldMeta = meta.getFields();
        for (int i = 0; i < fieldMeta.size(); i++) {
            CSVFieldMeta csvFieldMeta = fieldMeta.get(i);
            CSVFieldImpl field = new CSVFieldExtension(csvFieldMeta);
            field.setColumn(csvFieldMeta.getColumn());
            field.setXmlPath(csvFieldMeta.getXmlPath());
            field.setValue("");
            fields.add(field);
        }
        segment.setFields(fields);
        return segment;
    }

	private CSVSegment recursivlyAddEmptySegmentRecords(CSVSegmentMeta segMeta )
	{
		CSVSegment segment=initializeEmptyCsvSegment(segMeta);
		for (CSVSegmentMeta childSegmentMeta:segMeta.getChildSegments())
	    {
	    	CSVSegment childSeg=recursivlyAddEmptySegmentRecords( childSegmentMeta);
	    	childSeg.setParentSegment(segment);
	    	segment.addChildSegment(childSeg);
	    }
	    return segment;
	}

	/**
	 * Find the field from parent, set the it with new value if not being set yet; otherwise
	 * require a new parent segment
	 * @param parentSeg
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	private boolean setNewFieldValue(CSVSegment parentSeg, String fieldName, String fieldValue)
	{
//		System.out.println("CSVSegmentedFileExtension.setNewFieldValue()..parent:"+parentSeg +"..field:"+fieldName+"..value:"+fieldValue);
		boolean rtnValue=false;
		CSVSegmentExtension segExt=(CSVSegmentExtension)parentSeg;
		if (parentSeg==null)
			return rtnValue;
		CSVFieldExtension fieldExt=(CSVFieldExtension)segExt.getFieldByName(fieldName);
		if (fieldExt!=null&&!fieldExt.isValueSet())
		{
			fieldExt.setValue(fieldValue);
			fieldExt.setValueSetFlag(true);
			rtnValue=true;
		}
		return rtnValue;
	}

	/**
	 * Express the CSVSegmentFile as string
	 */
	public String toString()
	{
		return outputCSVString();
	}
	/**
	 * Output the CSVSegmentFile as string
	 */
	private String outputCSVString()
	{
		StringBuffer sb=new StringBuffer();
		for (CSVSegment csvSeg:this.getLogicalRecords())
			sb.append(recursivelyConvertSegmentToCSVString(csvSeg));

		return sb.toString();
	}

	private String recursivelyConvertSegmentToCSVString(CSVSegment csvSeg)
	{
		StringBuffer sb=new StringBuffer();
		sb.append(csvSeg.getName());
		for (CSVField csvField:csvSeg.getFields())
		{
			sb.append(","+csvField.getValue());
		}
		sb.append("\n");
		List<CSVSegment> childList=csvSeg.getChildSegments();
		if (childList.size()>0)
		{
			for (CSVSegment childSeg:childList)
				sb.append(recursivelyConvertSegmentToCSVString(childSeg));
			sb.append("\n");
		}
		return sb.toString();
	}
}
