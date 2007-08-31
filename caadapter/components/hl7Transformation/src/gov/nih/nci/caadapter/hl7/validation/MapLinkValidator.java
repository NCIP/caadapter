/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MapLinkValidator.java,v 1.2 2007-08-31 16:26:00 wangeug Exp $
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


package gov.nih.nci.caadapter.hl7.validation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

/**
 * This class defines a basic validator to check if two objects are valid to participate in mapping.
 * <p/>
 * Future enhancement or refactory may be needed along the expansion of functionality.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-08-31 16:26:00 $
 */
public class MapLinkValidator extends Validator {
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MapLinkValidator.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MapLinkValidator.java,v 1.2 2007-08-31 16:26:00 wangeug Exp $";

	private transient Object source;
	private transient Object target;

	public MapLinkValidator(Object source, Object target) {
		this.source = source;
		this.target = target;
	}

	public ValidatorResults validate() {
		ValidatorResults result = new ValidatorResults();
		//todo: later we should make this class adaptive to any kind of source and target types

		/**
		 * 1) exam individual source and target node;
		 * 2) exam if the pair is valid;
		 */

		boolean foundError=false;
		if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta)
				&&(target instanceof gov.nih.nci.caadapter.hl7.mif.MIFAttribute ))
		{
			MIFAttribute mifAttr=(MIFAttribute)target;
			if (!mifAttr.isStrutural())
				foundError=true;
		}
		else if((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta)
				&& (target instanceof gov.nih.nci.caadapter.hl7.datatype.Attribute))
		{
			//a valid map between a csv field and MIF datatype attribute.
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
//				&& (target instanceof gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta))
				&& (target instanceof gov.nih.nci.caadapter.hl7.mif.MIFClass))
		{
			//a valid map between a csv segment and a clone.
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
//				&& (target instanceof gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta))
				&& (target instanceof gov.nih.nci.caadapter.hl7.mif.MIFAssociation))
		{
			//a valid map between a csv segment and a clone datatype (attribute).
		}
		else
			foundError=true;
		
		if (foundError)
		{
			//an invalid map - create an error.
			String strSourceValue = source == null ? "null" : source.toString();
			String strTargetValue = target == null ? "null" : target.toString();
			//"MAP2", "\"{0}\" and \"{1}\" is not a valid pair for mapping."
			Message msg = MessageResources.getMessage("MAP2", new Object[]{strSourceValue, strTargetValue});
			ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, msg);
			result.addValidatorResult(one);
		}

		return result;
	}

	/**
	 * Validate if the given meta object is mappable.
	 * @param metaObject
	 * @return non-null ValidatorResults instance.
	 * If ValidatorResults does not contain any message, it implies the validation is passed successfully.
	 */
	public static ValidatorResults isMetaObjectMappable(MetaObject metaObject)
	{
		ValidatorResults validatorResults = new ValidatorResults();
		if (metaObject instanceof DatatypeBaseObject)
		{
			DatatypeBaseObject cloneAttributeMeta = (DatatypeBaseObject) metaObject;
//			if (!cloneAttributeMeta.isMappable())
			if (!cloneAttributeMeta.isEnabled())
			{//"MAP12", "Object is not mappable: {0}"
				Message msg = MessageResources.getMessage("MAP12", new Object[]{cloneAttributeMeta.getParentXmlPath() + "." + cloneAttributeMeta.getName()});
				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			}
		}
		return validatorResults;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/23 18:47:16  wangeug
 * HISTORY      : enable mapping validator for CSV to MIF mapping
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/23 16:37:59  jiangsc
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/16 23:19:12  chene
 * HISTORY      : Database prototype GUI support, but can not be loaded
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/13 20:24:07  giordanm
 * HISTORY      : Trying to get Segment -> Attribute maps working.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/25 22:40:11  jiangsc
 * HISTORY      : Enhanced mapping validation.
 * HISTORY      :
 */
