/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/TransformationResult.java,v 1.2 2007-07-27 20:35:47 wangeug Exp $
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


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * This class contains the generated HL7 V3 message text and
 * corresponding validation results.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 * @date $Date: 2007-07-27 20:35:47 $
 * @since $Revision: 1.2 $
 */

public class TransformationResult extends BaseResult
{
	private static final String LOGID = "$RCSfile: TransformationResult.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/TransformationResult.java,v 1.2 2007-07-27 20:35:47 wangeug Exp $";

	private String messageText;
    private long logicalRecordNumber = 0;
    
	public TransformationResult(String hl7V3MessageText, ValidatorResults validatorResults)
	{
		this.messageText = hl7V3MessageText;
		setValidatorResults(validatorResults);
	}

    public TransformationResult(long logicalRecordNumber, ValidatorResults validatorResults)
    {
        this.logicalRecordNumber = logicalRecordNumber;
        setValidatorResults(validatorResults);
    }

	public String getMessageText()
	{
		return messageText;
	}

    public long getLogicalRecordNumber()
    {
        return logicalRecordNumber;
    }

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final TransformationResult that = (TransformationResult) o;

		if (messageText != null ? !messageText.equals(that.messageText) : that.messageText != null)
		{
			return false;
		}

        if (logicalRecordNumber != that.logicalRecordNumber)
        {
            return false;
        }

		if (getValidatorResults() != null ? !getValidatorResults().equals(that.getValidatorResults()) : that.getValidatorResults() != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (messageText != null ? messageText.hashCode() : 0);
		result = (int) (29 * result + 17 * logicalRecordNumber + (getValidatorResults() != null ? getValidatorResults().hashCode() : 0));
		return result;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 18:26:25  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/05 22:07:46  chene
 * HISTORY      : Integrate GeneralTask, improve the statics
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/03 16:03:15  chene
 * HISTORY      : Re-engineer TransformationServiceCsvToHL7V3 to support estimate record number
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/06 20:40:39  giordanm
 * HISTORY      : prettying the code in the database package.  javadoc, license headers, etc.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/15 16:01:51  giordanm
 * HISTORY      : trying to get result objects working for CSVMetaParser... (result objects contain the information a service returns as well as a list of validation errors/warnings)
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/28 21:43:04  jiangsc
 * HISTORY      : Message result + validation result
 * HISTORY      :
 */
