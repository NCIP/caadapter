/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws.restful.controlloer;

import org.springframework.stereotype.Controller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import gov.nih.nci.caadapter.dvts.ws.restful.bean.EntryPoint;
import gov.nih.nci.caadapter.dvts.ws.restful.bean.EntryPointList;
import gov.nih.nci.caadapter.dvts.ws.restful.bean.EntryPointDS;
import gov.nih.nci.caadapter.dvts.ws.util.TranslationResponseUtil;
import gov.nih.nci.caadapter.dvts.common.meta.*;
import gov.nih.nci.caadapter.dvts.common.util.Config;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.CharArrayReader;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 26, 2011
 * Time: 11:20:35 AM
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class EntryPointController
{

    private EntryPointDS entryPointDS;

    public void setEntryPointDS(EntryPointDS ds) {
        this.entryPointDS = ds;
    }

    private Jaxb2Marshaller jaxb2Mashaller;

    public void setJaxb2Mashaller(Jaxb2Marshaller jaxb2Mashaller) {
        this.jaxb2Mashaller = jaxb2Mashaller;
    }

    private static final String XML_VIEW_NAME = "entryPoints";


    @RequestMapping(method= RequestMethod.GET, value="/entryPoint/{id}")
    public ModelAndView getEntryPoint(@PathVariable String id) {
        EntryPoint e = entryPointDS.get(Long.parseLong(id), "");
        return new ModelAndView(XML_VIEW_NAME, "object", e);
    }

    @RequestMapping(method=RequestMethod.GET, value="/entryPoint/{id}/tag/{tag}")
    public ModelAndView getEntryPoint(@PathVariable String id, @PathVariable String tag) {
        EntryPoint e = entryPointDS.get(Long.parseLong(id), tag);
        return new ModelAndView(XML_VIEW_NAME, "object", e);
    }

    @RequestMapping(method=RequestMethod.GET, value="/context/{context}/domain/{domain}/value/{value}")
    public ModelAndView translateVOM(@PathVariable String context, @PathVariable String domain, @PathVariable String value)
    {
        return getTranslatedData(context, domain, value, "false", false);
    }

    @RequestMapping(method=RequestMethod.GET, value="/context/{context}/searchdomain")
    public ModelAndView translateVOM(@PathVariable String context)//, @PathVariable String domain, @PathVariable String value)
    {
        return getTranslatedData(context, "searchDomain", "searchDomain", "false", true);
    }
    @RequestMapping(method=RequestMethod.GET, value="/context/{context}/searchdomain/{value}")
    public ModelAndView translateVOM(@PathVariable String context, @PathVariable String value)//, @PathVariable String domain, @PathVariable String value)
    {
        if ((value.equalsIgnoreCase("true"))||(value.equalsIgnoreCase("yes")))
            return getTranslatedData(context, "searchDomain", "searchDomain", "false", true);
        else return getTranslatedData(context, "", "", "false", false);
    }

    @RequestMapping(method=RequestMethod.GET, value="/context/{context}/domain/{domain}/value/{value}/inverse/{inverse}")
    public ModelAndView translateVOM(@PathVariable String context, @PathVariable String domain, @PathVariable String value, @PathVariable String inverse)
    {
        return getTranslatedData(context, domain, value, inverse, false);
    }
    @RequestMapping(method=RequestMethod.GET, value="/context/{context}/domain/{domain}/value/{value}/inverse")
    public ModelAndView translateVOM2(@PathVariable String context, @PathVariable String domain, @PathVariable String value)
    {
        return getTranslatedData(context, domain, value, "true", false);
    }

    private ModelAndView getTranslatedData(String context, String domain, String value, String inverseS, boolean searchDomain)
    {
        boolean inverse = false;
        if ((inverseS != null)&&(!inverseS.trim().equals("")))
        {
            inverseS = inverseS.trim().toLowerCase();
            if ((inverseS.equals("true"))||(inverseS.equals("yes"))) inverse = true;
        }

        String searchDomainS = "false";
        if (searchDomain) searchDomainS = "true";

        String result = "";
        if ((context.trim().equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))||
            (domain.trim().equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))||
            (value.trim().equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER)))
        {
            result = TranslationResponseUtil.assemblResultMessage(context, inverse, "Error", "DVTS Restful service cannot use any wild character. Use web service.", value, "unknown", domain, value, false);
        }
        else result = TranslationResponseUtil.generateTranslationResult("unknown", context, domain, searchDomainS, value, inverse, "", false);

        JAXBContext jc = null;
        VocabularyMappingData vmd = null;
        ReturnMessage msge = null;
        try
        {
        	jc=JAXBContext.newInstance("gov.nih.nci.caadapter.dvts.common.meta");
            Unmarshaller u=jc.createUnmarshaller();
            JAXBElement<VocabularyMappingData> jaxbFormula=u.unmarshal(new StreamSource(new CharArrayReader(result.toCharArray())), VocabularyMappingData.class);
            vmd = jaxbFormula.getValue();
		}
        catch(JAXBException je)
        {
            String msg = "JAXBException : " + je.getMessage();
            System.out.println(msg );
            msge = new ReturnMessage();
            msge.setErrorLevel(ErrorLevel.ERROR);
            msge.setValue(msg);

            MappingSource src = new MappingSource();
            src.setDomainName(domain);
            src.setIp("unknown");
            src.setSourceValue(value);
            src.setContext(context);
            src.setInverse(inverse);

            vmd = new VocabularyMappingData();
            vmd.setMappingResults(new MappingResults());
            vmd.setMappingSource(src);
            vmd.setReturnMessage(msge);
        }

        return new ModelAndView(XML_VIEW_NAME, "object", vmd);
    }
    /*
    private VocabularyMappingData createVocabularyMappingData(String context, String domain, String value, boolean inverse)
    {

            String result = TranslationResponseUtil.generateTranslationResult("unknown", context, domain, "false", value, inverse, "", true);

            String contextS = "";
            String inverseS = "";
            String level = "";
            String msg = "";
            String valueC = "";
            String ipS = "";
            String domainP = "";
            String sourceH = "";
            List<String> resultLines = new ArrayList<String>();

            result = result + "\t";

            String buff = "";
            int n = 0;
            for (int i=0;i<result.length();i++)
            {
                String achar = result.substring(i, i+1);
                if (achar.equals("\t"))
                {
                    if (buff.trim().equalsIgnoreCase("%null%")) buff = "";
                    //System.out.println("CCCC GGG Data("+n+") = "+ buff);

                    if (n==0) contextS = buff.trim();
                    else if (n==1) inverseS = buff.trim();
                    else if (n==2) level = buff.trim();
                    else if (n==3) msg = buff.trim();
                    else if (n==4) valueC = buff.trim();
                    else if (n==5) ipS = buff.trim();
                    else if (n==6) domainP = buff.trim();
                    else if (n==7) sourceH = buff.trim();
                    else resultLines.add(buff.trim());
                    n++;
                    buff = "";
                }
                else buff = buff + achar;
            }

            ObjectFactory factory = new ObjectFactory();

            ReturnMessage msge = factory.createReturnMessage();
            //ReturnMessage msge = new ReturnMessage();
            msge.setErrorLevel(ErrorLevel.fromValue(level));
            msge.setValue(msg);

            MappingSource src = new MappingSource();
            src.setDomainName(domainP);
            src.setIp(ipS);
            src.setSourceValue(sourceH);
            src.setContext(context);
            src.setInverse(inverse);


            MappingResults results = new MappingResults();
            if (!valueC.equals("")) results.getResult().add(valueC);
            if (resultLines.size() > 0) for (String str:resultLines) results.getResult().add(str);

            VocabularyMappingData vmd = new  VocabularyMappingData();
            vmd.setMappingResults(results);
            vmd.setMappingSource(src);
            vmd.setReturnMessage(msge);
            //if (tag.equals("4")) return new ModelAndView(factory.createVocabularyMappingData(vmd));

            return vmd;

    }
    */

    @RequestMapping(method=RequestMethod.PUT, value="/entryPoint/{id}")
    public ModelAndView updateEntryPoint(@RequestBody String body) {
        Source source = new StreamSource(new StringReader(body));
        EntryPoint e = (EntryPoint) jaxb2Mashaller.unmarshal(source);
        entryPointDS.update(e);
        return new ModelAndView(XML_VIEW_NAME, "object", e);
    }

    @RequestMapping(method=RequestMethod.POST, value="/entryPoint")
    public ModelAndView addEntryPoint(@RequestBody String body) {
        Source source = new StreamSource(new StringReader(body));
        EntryPoint e = (EntryPoint) jaxb2Mashaller.unmarshal(source);
        entryPointDS.add(e);
        return new ModelAndView(XML_VIEW_NAME, "object", e);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/entryPoint/{id}")
    public ModelAndView removeEntryPoint(@PathVariable String id) {
        entryPointDS.remove(Long.parseLong(id));
        List<EntryPoint> EntryPoints = entryPointDS.getAll();
        EntryPointList list = new EntryPointList(EntryPoints);
        return new ModelAndView(XML_VIEW_NAME, "EntryPoints", list);
    }

    @RequestMapping(method=RequestMethod.GET, value="/entryPoints")
    public ModelAndView getEntryPoints() {
        List<EntryPoint> EntryPoints = entryPointDS.getAll();
        EntryPointList list = new EntryPointList(EntryPoints);
        return new ModelAndView(XML_VIEW_NAME, "EntryPoints", list);
    }

}
