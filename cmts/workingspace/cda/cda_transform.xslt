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
      <xsl:call-template name="_template_1" />
      <xsl:call-template name="_template_2" />
      <xsl:call-template name="_template_3" />
      <xsl:call-template name="_template_4" />
      <xsl:call-template name="_template_5" />
      <xsl:call-template name="_template_6" />
      <xsl:call-template name="_template_7" />
      <xsl:call-template name="_template_8" />
      <xsl:call-template name="_template_9" />
      <xsl:call-template name="_template_10" />
      <xsl:call-template name="_template_11" />
      <xsl:call-template name="_template_12" />
      <xsl:call-template name="_template_44" />
      <xsl:call-template name="_template_60" />
      <xsl:call-template name="_template_61" />
    </ClinicalDocument>
  </xsl:template>
  <xsl:template name="_template_0">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_1">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="string(&quot;A123&quot;)" />
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_2">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_3">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_4">
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
    </code>
  </xsl:template>
  <xsl:template name="_template_5">
    <title>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="string(&quot;Discharge Sumary&quot;)" />
    </title>
  </xsl:template>
  <xsl:template name="_template_6">
    <effectiveTime>
      <xsl:attribute name="value">
        <xsl:value-of select="." />
      </xsl:attribute>
    </effectiveTime>
  </xsl:template>
  <xsl:template name="_template_7">
    <confidentialityCode>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
    </confidentialityCode>
  </xsl:template>
  <xsl:template name="_template_8">
    <languageCode>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
    </languageCode>
  </xsl:template>
  <xsl:template name="_template_9">
    <setId />
  </xsl:template>
  <xsl:template name="_template_10">
    <versionNumber />
  </xsl:template>
  <xsl:template name="_template_11">
    <copyTime />
  </xsl:template>
  <xsl:template name="_template_12">
    <recordTarget>
      <xsl:attribute name="typeCode">
        <xsl:text>RCT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextControlCode">
        <xsl:text>OP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_13" />
      <xsl:call-template name="_template_14" />
      <xsl:call-template name="_template_15" />
      <xsl:call-template name="_template_16" />
    </recordTarget>
  </xsl:template>
  <xsl:template name="_template_13">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_14">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_15">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_16">
    <patientRole>
      <xsl:attribute name="classCode">
        <xsl:text>PAT</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_17" />
      <xsl:call-template name="_template_18" />
      <xsl:call-template name="_template_19" />
      <xsl:call-template name="_template_20" />
      <xsl:call-template name="_template_21" />
      <xsl:call-template name="_template_24" />
      <xsl:call-template name="_template_25" />
      <xsl:call-template name="_template_43" />
    </patientRole>
  </xsl:template>
  <xsl:template name="_template_17">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_18">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_19">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_20">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="@LongName" />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_21">
    <addr>
      <xsl:call-template name="_template_22" />
      <xsl:call-template name="_template_23" />
    </addr>
  </xsl:template>
  <xsl:template name="_template_22">
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
    </state>
  </xsl:template>
  <xsl:template name="_template_23">
    <useablePeriod />
  </xsl:template>
  <xsl:template name="_template_24">
    <telecom>
      <xsl:attribute name="use">
        <xsl:value-of select="string(&quot;H&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="value">
        <xsl:value-of select="concat(string(&quot;tel:&quot;),ADT_A03/PID/PID.13/XTN.1)" />
      </xsl:attribute>
    </telecom>
  </xsl:template>
  <xsl:template name="_template_25">
    <xsl:for-each select="ADT_A03/PID">
      <patient>
        <xsl:attribute name="classCode">
          <xsl:text>PSN</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="determinerCode">
          <xsl:text>INSTANCE</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="_template_26" />
        <xsl:call-template name="_template_27" />
        <xsl:call-template name="_template_28" />
        <xsl:call-template name="_template_29" />
        <xsl:call-template name="_template_30" />
        <xsl:call-template name="_template_34" />
        <xsl:call-template name="_template_35" />
        <xsl:call-template name="_template_36" />
        <xsl:call-template name="_template_37" />
        <xsl:call-template name="_template_38" />
        <xsl:call-template name="_template_39" />
        <xsl:call-template name="_template_40" />
        <xsl:call-template name="_template_41" />
        <xsl:call-template name="_template_42" />
      </patient>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_26">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_27">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_28">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_29">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_30">
    <name>
      <xsl:attribute name="use">
        <xsl:value-of select="string(&quot;L&quot;)" />
      </xsl:attribute>
      <xsl:call-template name="_template_31" />
      <xsl:call-template name="_template_32" />
      <xsl:call-template name="_template_33" />
    </name>
  </xsl:template>
  <xsl:template name="_template_31">
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
    </family>
  </xsl:template>
  <xsl:template name="_template_32">
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
    </given>
  </xsl:template>
  <xsl:template name="_template_33">
    <validTime />
  </xsl:template>
  <xsl:template name="_template_34">
    <administrativeGenderCode>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
    </administrativeGenderCode>
  </xsl:template>
  <xsl:template name="_template_35">
    <birthTime>
      <xsl:attribute name="value">
        <xsl:value-of select="." />
      </xsl:attribute>
    </birthTime>
  </xsl:template>
  <xsl:template name="_template_36">
    <maritalStatusCode>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="." />
      </xsl:attribute>
    </maritalStatusCode>
  </xsl:template>
  <xsl:template name="_template_37">
    <religiousAffiliationCode>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="." />
      </xsl:attribute>
    </religiousAffiliationCode>
  </xsl:template>
  <xsl:template name="_template_38">
    <raceCode />
  </xsl:template>
  <xsl:template name="_template_39">
    <ethnicGroupCode />
  </xsl:template>
  <xsl:template name="_template_40">
    <guardian>
      <xsl:attribute name="classCode">
        <xsl:text>GUARD</xsl:text>
      </xsl:attribute>
    </guardian>
  </xsl:template>
  <xsl:template name="_template_41">
    <birthplace>
      <xsl:attribute name="classCode">
        <xsl:text>BIRTHPL</xsl:text>
      </xsl:attribute>
    </birthplace>
  </xsl:template>
  <xsl:template name="_template_42">
    <languageCommunication />
  </xsl:template>
  <xsl:template name="_template_43">
    <providerOrganization>
      <xsl:attribute name="classCode">
        <xsl:text>ORG</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="determinerCode">
        <xsl:text>INSTANCE</xsl:text>
      </xsl:attribute>
    </providerOrganization>
  </xsl:template>
  <xsl:template name="_template_44">
    <author>
      <xsl:attribute name="typeCode">
        <xsl:text>AUT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextControlCode">
        <xsl:text>OP</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_45" />
      <xsl:call-template name="_template_46" />
      <xsl:call-template name="_template_47" />
      <xsl:call-template name="_template_48" />
      <xsl:call-template name="_template_49" />
      <xsl:call-template name="_template_50" />
    </author>
  </xsl:template>
  <xsl:template name="_template_45">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_46">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_47">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_48">
    <functionCode />
  </xsl:template>
  <xsl:template name="_template_49">
    <time>
      <xsl:attribute name="value">
        <xsl:value-of select="." />
      </xsl:attribute>
    </time>
  </xsl:template>
  <xsl:template name="_template_50">
    <assignedAuthor>
      <xsl:attribute name="classCode">
        <xsl:text>ASSIGNED</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_51" />
      <xsl:call-template name="_template_52" />
      <xsl:call-template name="_template_53" />
      <xsl:call-template name="_template_54" />
      <xsl:call-template name="_template_55" />
      <xsl:call-template name="_template_56" />
      <xsl:call-template name="_template_57" />
      <xsl:call-template name="_template_58" />
      <xsl:call-template name="_template_59" />
    </assignedAuthor>
  </xsl:template>
  <xsl:template name="_template_51">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_52">
    <typeId>
      <xsl:attribute name="root">
        <xsl:text>2.16.840.1.113883.1.3</xsl:text>
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_53">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_54">
    <id>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="." />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_55">
    <code />
  </xsl:template>
  <xsl:template name="_template_56">
    <addr />
  </xsl:template>
  <xsl:template name="_template_57">
    <telecom />
  </xsl:template>
  <xsl:template name="_template_58">
    <assignedPerson>
      <xsl:attribute name="classCode">
        <xsl:text>PSN</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="determinerCode">
        <xsl:text>INSTANCE</xsl:text>
      </xsl:attribute>
    </assignedPerson>
    <assignedAuthoringDevice>
      <xsl:attribute name="classCode">
        <xsl:text>DEV</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="determinerCode">
        <xsl:text>INSTANCE</xsl:text>
      </xsl:attribute>
    </assignedAuthoringDevice>
  </xsl:template>
  <xsl:template name="_template_59">
    <representedOrganization>
      <xsl:attribute name="classCode">
        <xsl:text>ORG</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="determinerCode">
        <xsl:text>INSTANCE</xsl:text>
      </xsl:attribute>
    </representedOrganization>
  </xsl:template>
  <xsl:template name="_template_60">
    <componentOf>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
    </componentOf>
  </xsl:template>
  <xsl:template name="_template_61">
    <component>
      <xsl:attribute name="typeCode">
        <xsl:text>COMP</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="contextConductionInd">
        <xsl:text>true</xsl:text>
      </xsl:attribute>
    </component>
  </xsl:template>
</xsl:transform>


