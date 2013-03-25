/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws.action;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Apr 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-04-13 15:24:56 $
 * @since caAdapter v4.2
 */
import gov.nih.nci.cbiit.cmts.ws.ScenarioUtil;
import gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.validator.DynaValidatorActionForm;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
public class ScenarioManagerAction extends DispatchAction {

	/**
	 * List all the prefixes units defined by the UCUM system
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	 public ActionForward browseScenarioRegistration(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	            HttpServletResponse response) throws IOException, ServletException
	 {

//		ArrayList<ScenarioRegistration> rtnList=new ArrayList<ScenarioRegistration>();
//		ScenarioRegistration itemOne=new ScenarioRegistration();
//		itemOne.setName("Test CSV to V3");
//		itemOne.setSourceSpecFile("CSV source One");
//		itemOne.setMappingFile("mapping One");
//		itemOne.setTargetFile("target One");
//		rtnList.add(itemOne);
//		ScenarioRegistration itemTwo=new ScenarioRegistration();
//
//		itemTwo.setName("Test V2 to V3");
//
//		itemTwo.setMappingFile("Mapping  Two");
//		itemTwo.setTargetFile("Target Two");
//
//		itemTwo.addVocabuaryMappingFile("VOM One");
//		itemTwo.addVocabuaryMappingFile("VOM Two");
//		rtnList.add(itemTwo);


		ArrayList<ScenarioRegistration> rtnList=null;
		try {
			rtnList = (ArrayList<ScenarioRegistration>) ScenarioUtil.retrieveScenarioRegistrations();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("rtnMessage", e.getMessage());
		}
		if (rtnList==null||rtnList.size()==0)
			request.setAttribute("rtnMessage", "No scenario being registered!");

		request.setAttribute("results", rtnList);
		return mapping.findForward("success");
//	     return mapping.findForward("failure");
	 } 
	
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/