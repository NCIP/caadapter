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
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

/**
 * This class defines a basic validator to check if two objects are valid to participate in mapping.
 * <p/>
 * Future enhancement or refactory may be needed along the expansion of functionality.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2008-10-09 18:09:20 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MapLinkValidator.java,v 1.11 2008-10-09 18:09:20 wangeug Exp $";

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
			else if (!MIFUtil.isEditableMIFAttributeDefault(mifAttr))
				foundError=true;
		}
        else if ((source instanceof gov.nih.nci.caadapter.common.metadata.AttributeMetadata)
                && (target instanceof gov.nih.nci.caadapter.common.metadata.AttributeMetadata))
        {
//            System.out.println("target" + target.getClass().toString());
//            System.out.println("target" + target.toString());
            //valid
        }
        else if ((source instanceof gov.nih.nci.caadapter.common.metadata.AssociationMetadata)
                && (target instanceof gov.nih.nci.caadapter.common.metadata.AssociationMetadata))
        {
            System.out.println("target" + target.getClass().toString());
            System.out.println("target" + target.toString());
            //valid
        }

        else if((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta)
				&& (target instanceof gov.nih.nci.caadapter.hl7.datatype.Attribute))
		{
			//Do not allowed map from a CSV field to Datatype field
			//if that field is treated as an MIFAttribute(complex type)
			Attribute dtAttr=(Attribute)target;
			//"inlineText" filed dose not have a reference datatype
			if (dtAttr.getReferenceDatatype()!=null&&!dtAttr.getReferenceDatatype().isSimple())
				foundError=true;
			//a valid map between a csv field and MIF datatype attribute
			//only if the referenced datatype is simple or "inlineText(null)" .
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
				&& (target instanceof gov.nih.nci.caadapter.hl7.mif.MIFClass))
		{
			//a valid map between a csv segment and a clone.
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
				&& (target instanceof gov.nih.nci.caadapter.hl7.mif.MIFAssociation))
		{
			//a valid map between a csv segment and a clone datatype (attribute).
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
				&& (target instanceof  gov.nih.nci.caadapter.hl7.datatype.Attribute))
		{
			//Allow map from a CSV segment to a Datatype field
			//only  if that field is treated as an MIFAttribute (complex)
			Attribute dtAttr=(Attribute)target;
			//"inlineText" Attribute dose not have a referenced datatype
			if (dtAttr.getReferenceDatatype()==null|dtAttr.getReferenceDatatype().isSimple())
				foundError=true;
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta)
				&& (target instanceof   gov.nih.nci.caadapter.common.metadata.TableMetadata))
		{
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta)
				&& (target instanceof   gov.nih.nci.caadapter.common.metadata.ColumnMetadata))
		{
		}
		else if ((source instanceof gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta)
				&& (target instanceof   gov.nih.nci.caadapter.common.metadata.AttributeMetadata))
		{
		}
		else if ((source instanceof gov.nih.nci.cbiit.cmps.core.ElementMeta)
				&& (target instanceof   gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject))
		{
		}

		else
			foundError=true;

		if (foundError)
		{
			//an invalid map - create an error.
			String strSourceValue = source == null ? "null" : source.toString();
			String strTargetValue = target == null ? "null" : target.toString();
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
 * HISTORY      : Revision 1.10  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/02/20 22:07:25  schroedn
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/12/13 21:06:08  wangeug
 * HISTORY      : resolve code dependence in compiling
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/12/13 19:29:57  wangeug
 * HISTORY      : resolve code dependence in compiling
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/12/06 20:46:02  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/12/04 15:10:12  wangeug
 * HISTORY      : apply mapping rule for csv-to-xmi mapping
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/10/25 20:18:13  wangeug
 * HISTORY      : allow mapping from a CVS metaField to MIFAttribute if the MIFAttribute is "INSTANCE+KIND" or editable
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/10/25 14:54:19  wangeug
 * HISTORY      : allow mapping from a CSVSegment to Complex datatype;
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/31 16:26:00  wangeug
 * HISTORY      : allow mapping from CSVSegment to MIFAssociation
 * HISTORY      :
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
