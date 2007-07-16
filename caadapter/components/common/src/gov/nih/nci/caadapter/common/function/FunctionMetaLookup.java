/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionMetaLookup.java,v 1.2 2007-07-16 19:05:50 wangeug Exp $
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


package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.MetaLookup;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Provides quick access when doing function metadata lookups.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.2 $
 * @date $Date: 2007-07-16 19:05:50 $
 * @since caAdapter v1.2
 */

public class FunctionMetaLookup implements MetaLookup{

	private FunctionMeta meta;
	private Hashtable<String, MetaObject> table = new Hashtable<String, MetaObject>();

	public FunctionMetaLookup(FunctionMeta meta) throws MetaException{
		this.meta = meta;
		initTable();
	}
	private void initTable()throws MetaException{
		List<ParameterMeta> inputParameterMeta= meta.getInputDefinitionList();
		for (int i = 0; i < inputParameterMeta.size(); i++) {
			ParameterMeta parameterMeta =  inputParameterMeta.get(i);
			table.put(parameterMeta.getXmlPath(),parameterMeta);
		}
		List<ParameterMeta> outputParameterMeta= meta.getOuputDefinitionList();
		for (int i = 0; i < outputParameterMeta.size(); i++) {
			ParameterMeta parameterMeta =  outputParameterMeta.get(i);
			table.put(parameterMeta.getXmlPath(),parameterMeta);
		}

	}

	public MetaObject lookup(String uuid) {
		return table.get(uuid);
	}

	/**
	 * Return all key elements.
	 *
	 * @return all key elements.
	 */
	public Set getAllKeys()
	{
		Set keySet = new HashSet(table.keySet());
		return keySet;
	}

}
