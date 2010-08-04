Readme for HL7 2.x schemas
Updated July 18, 2008

Earlier HL7 v2.xml schemas available from HL7 website had some issues and thus an effort was made by Sun Microsystems to create schemas. Some issues noted: 
1. XSDs for version 2.1, 2.2 and 2.3 weren't available.2. Some of the v2 XSDs have typos and cannot be validated.  Examples are:     . v2.5 fields.xsd is not well-formed     . v2.3.1 ORN_O02.xsd, ORR_O02.xsd, ORS_O02.xsd, OSR_Q06.xsd, RRA_O02.xsd and RRD_O02.xsd have typos     . v2.3.1 REF_I12.xsd has REF_I12_PV1PV2_SUPPGRP2 duplicated and causes UPA issue3. Version 2.3.1 XSDs do not use user-friendly and meaningful group names but uses concatenated names for groups.4. XSDs are not friendly to JAXB tools (have name clash during JAXB schema compilation).5. XSDs do not have hints needed by open source design time tools.

XML Schemas were generated from the Access database form of the HL7 V2 metadata using a Java program to implement HL7 Version 2.XML Encoding Syntax Rules. The SQL statements used to query the metadata are included in this package. All XSDs were validated by XMLSpy and XmlBeans to ensure XML schema validity. 

The current package includes schemas for the following releases:
HL7 v 2.1 
HL7 v 2.2
HL7 v 2.3 
HL7 v 2.3.1 
HL7 v 2.4
HL7 v 2.5 
HL7 v 2.5.1 