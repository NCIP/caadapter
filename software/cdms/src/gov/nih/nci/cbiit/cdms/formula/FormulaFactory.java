package gov.nih.nci.cbiit.cdms.formula;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

import java.io.File;
import java.io.ObjectInputStream.GetField;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

public class FormulaFactory {

	public static FormulaMeta loadFormula(File f) throws JAXBException
	{
		JAXBContext jc=getJAXBContext();
		Unmarshaller u=jc.createUnmarshaller();
		JAXBElement<FormulaMeta> jaxbFormula=u.unmarshal(new StreamSource(f), FormulaMeta.class);
		return jaxbFormula.getValue();
	}
	
	public static void saveFormula(FormulaMeta formula, File f) throws JAXBException
	{
		JAXBContext jc=getJAXBContext();
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		m.marshal(new JAXBElement<FormulaMeta>(new QName("formula"),FormulaMeta.class, formula), f);
	}
	
	private static JAXBContext getJAXBContext() throws JAXBException
	{
		JAXBContext jc=null;		
//		jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
//		jc=com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.newInstance("gov.nih.nci.cbiit.cdms.formula.core");
		jc=JAXBContext.newInstance("gov.nih.nci.cbiit.cdms.formula.core");
		return jc;
	}
}
