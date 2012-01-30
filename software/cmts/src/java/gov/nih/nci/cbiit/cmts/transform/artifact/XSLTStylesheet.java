package gov.nih.nci.cbiit.cmts.transform.artifact;

import java.io.IOException;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



public class XSLTStylesheet extends Document  {
	protected static Namespace xsltNamespace=Namespace.getNamespace("xsl","http://www.w3.org/1999/XSL/Transform");
	private static Namespace xsNamespace=Namespace.getNamespace("xs","http://www.w3.org/2001/XMLSchema");
    public XSLTStylesheet(String namespace)
	{
        super();
        initXSLTStylesheet(namespace);
    }
    public XSLTStylesheet()
	{
        super();
        initXSLTStylesheet(null);
    }
    private void initXSLTStylesheet(String namespace)
	{

		XSLTElement xlstRoot = new XSLTElement("transform");
		xlstRoot.addNamespaceDeclaration(xsNamespace);
        if ((namespace != null)&&(!namespace.trim().equals(""))) xlstRoot.addNamespaceDeclaration(Namespace.getNamespace("",namespace));
        xlstRoot.setAttribute("version", "2.0");
		setRootElement(xlstRoot);
		XSLTElement outputElement=new XSLTElement("output");
		outputElement.setAttribute("method","xml");
		outputElement.setAttribute("indent","yes");
		xlstRoot.addContent(outputElement);
	}

	/**
	 * <xsl:template> is top level element, add it to the root element 
	 * @param template
	 */
	public void addTempate(Element template)
	{
		getRootElement().addContent(template);
	}
	public void writeOut(Writer out) throws IOException
	{
		Format outFormat=Format.getPrettyFormat();
		outFormat.setLineSeparator("\n");
		XMLOutputter xmlOut=new XMLOutputter(outFormat);
//		xmlOut.getFormat().setEncoding("ISO-8859-1");
//		xmlOut.getFormat().setLineSeparator("\n");
		xmlOut.output(this, out);
	}
}
