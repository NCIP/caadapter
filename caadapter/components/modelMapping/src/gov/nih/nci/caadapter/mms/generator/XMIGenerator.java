/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLClassBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLDependencyBean;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;
import gov.nih.nci.caadapter.common.Log;
import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.*;

/**
 * The purpose of this class is to add tagged values and dependencies to
 * an xmi file based on the contents of a source to target mapping file.
 * 
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.38 $
 * @date       $Date: 2009-07-10 19:54:06 $
 * @created 11-Aug-2006 8:18:19 AM
 */
public class XMIGenerator 
{
    private String xmiOutputName;
	private ModelMetadata modelMetadata = null;
    private Log logger =new Log();
	
	public XMIGenerator(String mappingFile, String xmiFile)
	{
		this.xmiOutputName = xmiFile;
	    try {
		    modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();//ModelMetadata.getInstance();				        						
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	    }
	}
		
	/**
	 * 
	 * 
	 */
	public void annotateXMI()
	{
		//save xmi file
	    try 
	    {	
	    	modelMetadata.getHandler().save(xmiOutputName);
    
	    } catch (Exception e){
	      e.printStackTrace();
	    } 
	}	
		
	  public static void main(String[] args) {
		  try {
			    XMIGenerator generator = new XMIGenerator("C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.map","C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.xmi");
		  } catch (Exception e){
			  e.printStackTrace();
		  }
	   
	  }
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.37  2009/06/12 15:51:06  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.36  2008/10/20 15:42:47  linc
 * HISTORY: remove clob, inverse-of, lazy/eager functionality from mms.
 * HISTORY:
 * HISTORY: Revision 1.35  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
