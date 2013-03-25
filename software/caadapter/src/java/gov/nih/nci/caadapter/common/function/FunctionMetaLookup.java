/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.4 $
 * @date $Date: 2008-09-25 18:57:45 $
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
