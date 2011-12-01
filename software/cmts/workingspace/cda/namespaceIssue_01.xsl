<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:output method="xml" indent="yes" />
  <xsl:template match="/">
    <ClinicalDocument>
      <xsl:attribute name="classCode">
        <xsl:text>DOCCLIN</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:text>EVN</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_0" />
    </ClinicalDocument>
  </xsl:template>
  <xsl:template name="_template_0">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="string(&quot;A123&quot;)" />
      </xsl:attribute>
    </typeId>
    <id>
      <xsl:attribute name="root">
        <xsl:value-of select="string(&quot;9988&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="ADT_A03/MSH/MSH.10" />
      </xsl:attribute>
    </id>
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="ADT_A03/MSH/MSH.11/PT.1" />
      </xsl:attribute>
    </code>
    <title>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="string(&quot;Discharge Sumary&quot;)" />
    </title>
    <effectiveTime>
      <xsl:attribute name="value">
        <xsl:value-of select="ADT_A03/MSH/MSH.7/TS.1" />
      </xsl:attribute>
    </effectiveTime>
    <confidentialityCode>
      <xsl:attribute name="code">
        <xsl:value-of select="ADT_A03/MSH/MSH.11/PT.2" />
      </xsl:attribute>
    </confidentialityCode>
    <languageCode>
      <xsl:attribute name="code">
        <xsl:value-of select="ADT_A03/MSH/MSH.19/CE.2" />
      </xsl:attribute>
    </languageCode>
    <recordTarget>
      <xsl:attribute name="typeCode">
        <xsl:text>RCT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextControlCode">
        <xsl:text>OP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_1" />
    </recordTarget>
    <author>
      <xsl:attribute name="typeCode">
        <xsl:text>AUT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextControlCode">
        <xsl:text>OP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_6" />
    </author>
    <componentOf>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_8" />
    </componentOf>
    <component>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextConductionInd">
        <xsl:text>true</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_16" />
    </component>
  </xsl:template>
  <xsl:template name="_template_1">
    <patientRole>
      <xsl:attribute name="classCode">
        <xsl:text>PAT</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_2" />
    </patientRole>
  </xsl:template>
  <xsl:template name="_template_2">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="ADT_A03/PID/PID.19" />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="ADT_A03/PID/PID.19/@LongName" />
      </xsl:attribute>
    </id>
    <addr>
      <xsl:call-template name="_template_3" />
    </addr>
    <telecom>
      <xsl:attribute name="use">
        <xsl:value-of select="string(&quot;H&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="concat(string(&quot;tel:&quot;),ADT_A03/PID/PID.13/XTN.1)" />
      </xsl:attribute>
    </telecom>
    <xsl:for-each select="ADT_A03/PID">
      <patient>
        <xsl:attribute name="classCode">
          <xsl:text>PSN</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="determinerCode">
          <xsl:text>INSTANCE</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="_template_4" />
      </patient>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_3">
    <xsl:for-each select="ADT_A03/PID/PID.11/XAD.1/SAD.1">
      <state>
        <xsl:attribute name="partType">
          <xsl:text>STA</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </state>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_4">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="PID.1" />
      </xsl:attribute>
    </typeId>
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PID.3/CX.1" />
      </xsl:attribute>
    </id>
    <name>
      <xsl:attribute name="use">
        <xsl:value-of select="string(&quot;L&quot;)" />
      </xsl:attribute>
      <xsl:call-template name="_template_5" />
    </name>
    <administrativeGenderCode>
      <xsl:attribute name="code">
        <xsl:value-of select="PID.8" />
      </xsl:attribute>
    </administrativeGenderCode>
    <birthTime>
      <xsl:attribute name="value">
        <xsl:value-of select="PID.7/TS.1" />
      </xsl:attribute>
    </birthTime>
    <maritalStatusCode>
      <xsl:attribute name="code">
        <xsl:value-of select="PID.16/CE.1" />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="PID.16/CE.3" />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="PID.16/CE.2" />
      </xsl:attribute>
    </maritalStatusCode>
    <religiousAffiliationCode>
      <xsl:attribute name="code">
        <xsl:value-of select="PID.17/CE.1" />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="PID.17/CE.3" />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="PID.17/CE.2" />
      </xsl:attribute>
    </religiousAffiliationCode>
  </xsl:template>
  <xsl:template name="_template_5">
    <xsl:for-each select="PID.5/XPN.1/FN.1">
      <family>
        <xsl:attribute name="partType">
          <xsl:text>FAM</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </family>
    </xsl:for-each>
    <xsl:for-each select="PID.5/XPN.2">
      <given>
        <xsl:attribute name="partType">
          <xsl:text>GIV</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </given>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_6">
    <time>
      <xsl:attribute name="value">
        <xsl:value-of select="ADT_A03/MSH/MSH.7/TS.1" />
      </xsl:attribute>
    </time>
    <assignedAuthor>
      <xsl:attribute name="classCode">
        <xsl:text>ASSIGNED</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_7" />
    </assignedAuthor>
  </xsl:template>
  <xsl:template name="_template_7">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="ADT_A03/MSH/MSH.4/HD.1" />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="ADT_A03/MSH/MSH.4/HD.2" />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_8">
    <xsl:for-each select="ADT_A03/PV1">
      <encompassingEncounter>
        <xsl:attribute name="classCode">
          <xsl:text>ENC</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="moodCode">
          <xsl:text>EVN</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="_template_9" />
      </encompassingEncounter>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_9">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PV1.1" />
      </xsl:attribute>
    </id>
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="PV1.2" />
      </xsl:attribute>
    </code>
    <effectiveTime>
      <xsl:attribute name="operator">
        <xsl:text>I</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="PV1.44/TS.1" />
      </xsl:attribute>
    </effectiveTime>
    <dischargeDispositionCode>
      <xsl:attribute name="code">
        <xsl:value-of select="PV1.36" />
      </xsl:attribute>
    </dischargeDispositionCode>
    <responsibleParty>
      <xsl:attribute name="typeCode">
        <xsl:text>RESP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_10" />
    </responsibleParty>
    <location>
      <xsl:attribute name="typeCode">
        <xsl:text>LOC</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_14" />
    </location>
  </xsl:template>
  <xsl:template name="_template_10">
    <assignedEntity>
      <xsl:attribute name="classCode">
        <xsl:text>ASSIGNED</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_11" />
    </assignedEntity>
  </xsl:template>
  <xsl:template name="_template_11">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PV1.7/XCN.1" />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="PV1.7/XCN.14/HD.2" />
      </xsl:attribute>
    </id>
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="PV1.10" />
      </xsl:attribute>
    </code>
    <assignedPerson>
      <xsl:attribute name="classCode">
        <xsl:text>PSN</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="determinerCode">
        <xsl:text>INSTANCE</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_12" />
    </assignedPerson>
  </xsl:template>
  <xsl:template name="_template_12">
    <name>
      <xsl:call-template name="_template_13" />
    </name>
  </xsl:template>
  <xsl:template name="_template_13">
    <xsl:for-each select="PV1.7/XCN.2/FN.1">
      <family>
        <xsl:attribute name="partType">
          <xsl:text>FAM</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </family>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_14">
    <healthCareFacility>
      <xsl:attribute name="classCode">
        <xsl:text>SDLOC</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_15" />
    </healthCareFacility>
  </xsl:template>
  <xsl:template name="_template_15">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PV1.3/PL.3" />
      </xsl:attribute>
    </id>
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PV1.3/PL.2" />
      </xsl:attribute>
    </id>
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="PV1.3/PL.2" />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_16">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="string(&quot;COMP&quot;)" />
      </xsl:attribute>
    </typeId>
    <structuredBody>
      <xsl:attribute name="classCode">
        <xsl:text>DOCBODY</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:text>EVN</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_17" />
    </structuredBody>
  </xsl:template>
  <xsl:template name="_template_17">
    <component>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextConductionInd">
        <xsl:text>true</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_18" />
    </component>
    <component>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextConductionInd">
        <xsl:text>true</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_22" />
    </component>
  </xsl:template>
  <xsl:template name="_template_18">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="string(&quot;PRCS&quot;)" />
      </xsl:attribute>
    </typeId>
    <section>
      <xsl:attribute name="classCode">
        <xsl:text>DOCSECT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:text>EVN</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_19" />
    </section>
  </xsl:template>
  <xsl:template name="_template_19">
    <title>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="string(&quot;CDA Record&quot;)" />
    </title>
    <xsl:for-each select="ADT_A03/ADT_A03.PROCEDURE/PR1">
      <entry>
        <xsl:attribute name="typeCode">
          <xsl:text>COMP</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="contextConductionInd">
          <xsl:text>true</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="_template_20" />
      </entry>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_20">
    <procedure>
      <xsl:attribute name="classCode">
        <xsl:value-of select="string(&quot;ACCM&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:value-of select="string(&quot;APT&quot;)" />
      </xsl:attribute>
      <xsl:call-template name="_template_21" />
    </procedure>
  </xsl:template>
  <xsl:template name="_template_21">
    <id>
      <xsl:attribute name="root">
        <xsl:value-of select="string(&quot;2.56.34.2345.5434.444&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="PR1.1" />
      </xsl:attribute>
    </id>
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="PR1.3/CE.1" />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="PR1.3/CE.3" />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="PR1.3/CE.2" />
      </xsl:attribute>
    </code>
    <effectiveTime>
      <xsl:attribute name="operator">
        <xsl:text>I</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="PR1.5/TS.1" />
      </xsl:attribute>
    </effectiveTime>
  </xsl:template>
  <xsl:template name="_template_22">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="string(&quot;OBSR&quot;)" />
      </xsl:attribute>
    </typeId>
    <section>
      <xsl:attribute name="classCode">
        <xsl:text>DOCSECT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:text>EVN</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_23" />
    </section>
  </xsl:template>
  <xsl:template name="_template_23">
    <title>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="string(&quot;Observation results&quot;)" />
    </title>
    <xsl:for-each select="ADT_A03/OBX">
      <entry>
        <xsl:attribute name="typeCode">
          <xsl:text>COMP</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="contextConductionInd">
          <xsl:text>true</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="_template_24" />
      </entry>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_24">
    <observation>
      <xsl:attribute name="classCode">
        <xsl:value-of select="string(&quot;ALRT&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="moodCode">
        <xsl:value-of select="string(&quot;DEF&quot;)" />
      </xsl:attribute>
      <xsl:call-template name="_template_25" />
    </observation>
  </xsl:template>
  <xsl:template name="_template_25">
    <id>
      <xsl:attribute name="root">
        <xsl:value-of select="string(&quot;2.56.34.2345.5434.555&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="OBX.1" />
      </xsl:attribute>
    </id>
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="OBX.3/CE.1" />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="OBX.3/CE.2" />
      </xsl:attribute>
    </code>
    <xsl:for-each select="OBX.4">
      <derivationExpr>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </derivationExpr>
    </xsl:for-each>
    <xsl:for-each select="OBX.5">
      <text>
        <xsl:attribute name="mediaType">
          <xsl:text>text/plain</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="integrityCheckAlgorithm">
          <xsl:text>SHA-1</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="representation">
          <xsl:text>TXT</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </text>
    </xsl:for-each>
    <statusCode>
      <xsl:attribute name="code">
        <xsl:value-of select="string(&quot;completed&quot;)" />
      </xsl:attribute>
    </statusCode>
    <effectiveTime>
      <xsl:attribute name="operator">
        <xsl:text>I</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="OBX.14/TS.1" />
      </xsl:attribute>
    </effectiveTime>
  </xsl:template>
</xsl:transform>

