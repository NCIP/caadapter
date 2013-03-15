/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.castor.csv.meta.impl.C_csvMetadata;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_field;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segmentItem;
import gov.nih.nci.caadapter.common.MetaBuilderBase;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Builds a csv specification from a csv meta object graph.  Converts meta
 * objects to castor objects and then marshals them.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.6 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public class CSVMetaBuilder extends MetaBuilderBase {

    private static CSVMetaBuilder metaBuilder = null;

    private static final String LOGID = "$RCSfile: CSVMetaBuilder.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaBuilder.java,v 1.6 2008-06-09 19:53:49 phadkes Exp $";

    private CSVMetaBuilder()
    {
    }

    public void build(OutputStream outputStream, MetaObject meta) throws MetaException {
        try {

            if (!(meta instanceof CSVMeta)) {
                throw new MetaException("Meta object should be an instance of CSVMeta", null);
            } else {
                CSVMeta csvMeta = (CSVMeta) meta;

                // setup the castor objects.
                C_csvMetadata c = new C_csvMetadata();
                c.setC_segment(processSegment(csvMeta.getRootSegment()));
                c.setVersion(new BigDecimal("1.2"));

                if ( csvMeta.isNonStructure() ) {
                    c.setType("NON_STRUCTURE");
                }

                c.setXmlPath(meta.getXmlPath());

                // set up the Source.
                StringWriter marshalString = new StringWriter();
                //c.marshal(marshalString);
                Marshaller marshaller = new Marshaller(marshalString);
                marshaller.setSuppressXSIType(true);
                marshaller.marshal(c);

                ByteArrayInputStream is = new ByteArrayInputStream(marshalString.toString().getBytes());
                // transform it.
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.transform(new StreamSource(is), new StreamResult(outputStream));
            }
        } catch (IOException e) {
            throw new MetaException(e.getMessage(), e);
        }catch (MarshalException e) {
            throw new MetaException(e.getMessage(), e);
        } catch (ValidationException e) {
            throw new MetaException(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }

    private C_segment processSegment(CSVSegmentMeta metaSegment) {
        C_segment castorSegment = new C_segment();
        castorSegment.setName(metaSegment.getName());
        if (metaSegment.getCardinalityType().getType() != (new C_segment()).getCardinality().getType())
        {
            castorSegment.setCardinality(metaSegment.getCardinalityType());
            //System.out.println("CCCCV : " + metaSegment.getCardinalityType().toString());
        }
        castorSegment.setXmlPath(metaSegment.getXmlPath());
//        System.out.println("metaSegment.getXmlPath() : " +metaSegment.getXmlPath()+"::"+ metaSegment.getName() + " :: " + metaSegment.getCardinalityType().toString());

        List<CSVSegmentMeta> metaChildSegments = metaSegment.getChildSegments();
        for (int i = 0; i < metaChildSegments.size(); i++) {
            CSVSegmentMeta csvSegmentMeta = metaChildSegments.get(i);
            C_segmentItem si = new C_segmentItem();
            si.setC_segment(processSegment(csvSegmentMeta));
            castorSegment.addC_segmentItem(si);
        }

        List<CSVFieldMeta> metaFields = metaSegment.getFields();
        for (int i = 0; i < metaFields.size(); i++) {
            CSVFieldMeta csvFieldMeta = metaFields.get(i);
            C_segmentItem si = new C_segmentItem();
            si.setC_field(processField(csvFieldMeta));
            castorSegment.addC_segmentItem(si);
        }
        return castorSegment;
    }

    private C_field processField(CSVFieldMeta metaField) {
        C_field castorField = new C_field();
        castorField.setColumn(metaField.getColumn());
        castorField.setName(metaField.getName());
        castorField.setXmlPath(metaField.getXmlPath());
        return castorField;
    }

    public static CSVMetaBuilder getInstance()
    {

        if (metaBuilder == null)
        {
            metaBuilder = new CSVMetaBuilder();
        }
        return metaBuilder;
    }
}

/*
    public void build(OutputStream outputStream, MetaObject meta) throws MetaException {
        try {

            if (!(meta instanceof CSVMeta)) {
                throw new MetaException("Meta object should be an instance of CSVMeta", null);
            } else {
                CSVMeta csvMeta = (CSVMeta) meta;

                // setup the castor objects.
                C_csvMetadata c = new C_csvMetadata();
                c.setC_segment(processSegment(csvMeta.getRootSegment()));
                c.setVersion(new BigDecimal("1.2"));
                c.setUuid(meta.getUUID());

                // set up the Source.
                StringWriter marshalString = new StringWriter();
                //c.marshal(marshalString);
                Marshaller marshaller = new Marshaller(marshalString);
                marshaller.setSuppressXSIType(true);
                marshaller.marshal(c);

                ByteArrayInputStream is = new ByteArrayInputStream(marshalString.toString().getBytes());
                // transform it.
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.transform(new StreamSource(is), new StreamResult(outputStream));
            }
        } catch (IOException e) {
            throw new MetaException(e.getMessage(), e);
        }catch (MarshalException e) {
            throw new MetaException(e.getMessage(), e);
        } catch (ValidationException e) {
            throw new MetaException(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }

    private C_segment processSegment(CSVSegmentMeta metaSegment) {
        C_segment castorSegment = new C_segment();
        castorSegment.setName(metaSegment.getName());
        castorSegment.setUuid(metaSegment.getUUID());
        castorSegment.setCardinality(metaSegment.getCardinality());
        List<CSVSegmentMeta> metaChildSegments = metaSegment.getChildSegments();
        for (int i = 0; i < metaChildSegments.size(); i++) {
            CSVSegmentMeta csvSegmentMeta = metaChildSegments.get(i);
            C_segmentItem si = new C_segmentItem();
            si.setC_segment(processSegment(csvSegmentMeta));
            castorSegment.addC_segmentItem(si);
        }

        List<CSVFieldMeta> metaFields = metaSegment.getFields();
        for (int i = 0; i < metaFields.size(); i++) {
            CSVFieldMeta csvFieldMeta = metaFields.get(i);
            C_segmentItem si = new C_segmentItem();
            si.setC_field(processField(csvFieldMeta));
            castorSegment.addC_segmentItem(si);
        }
        return castorSegment;
    }

    private C_field processField(CSVFieldMeta metaField) {
        C_field castorField = new C_field();
        castorField.setColumn(metaField.getColumn());
        castorField.setName(metaField.getName());
        castorField.setUuid(metaField.getUUID());
        return castorField;
    }

    public static CSVMetaBuilder getInstance()
    {

        if (metaBuilder == null)
        {
            metaBuilder = new CSVMetaBuilder();
        }
        return metaBuilder;
    }
}
*/
