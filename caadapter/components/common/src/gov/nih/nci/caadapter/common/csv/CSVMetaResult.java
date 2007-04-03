/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaResult.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
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


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * Contains CSV meta information and validation results.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $Date: 2007-04-03 16:02:37 $
 * @since caAdapter v1.2
 */

public class CSVMetaResult extends BaseResult {
    private static final String LOGID = "$RCSfile: CSVMetaResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaResult.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $";

    private CSVMeta csvMeta = null;

    // constructors.
    public CSVMetaResult() {
    }
    public CSVMetaResult(CSVMeta csvMeta, ValidatorResults validatorResults) {
        this.csvMeta = csvMeta;
        this.validatorResults = validatorResults;
    }

    // setters and getters.
    public CSVMeta getCsvMeta() {
        return csvMeta;
    }
    public void setCsvMeta(CSVMeta csvMeta) {
        this.csvMeta = csvMeta;
    }
}

/**
 *  HISTORY      : $Log: not supported by cvs2svn $
 *  HISTORY      : Revision 1.7  2006/08/02 18:44:20  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.6  2006/01/03 19:16:51  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.5  2006/01/03 18:27:13  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.4  2005/12/29 23:06:15  jiangsc
 *  HISTORY      : Changed to latest project name.
 *  HISTORY      :
 *  HISTORY      : Revision 1.3  2005/09/16 23:18:53  chene
 *  HISTORY      : Database prototype GUI support, but can not be loaded
 *  HISTORY      :
 */

