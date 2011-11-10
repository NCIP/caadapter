<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:output method="xml" indent="yes" />
  <xsl:template match="/">
    <PORR_MT049006UV01.InvestigationEvent>
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
      <xsl:call-template name="_template_13" />
      <xsl:call-template name="_template_25" />
      <xsl:call-template name="_template_26" />
      <xsl:call-template name="_template_27" />
      <xsl:call-template name="_template_28" />
      <xsl:call-template name="_template_29" />
      <xsl:call-template name="_template_30" />
      <xsl:call-template name="_template_31" />
      <xsl:call-template name="_template_32" />
      <xsl:call-template name="_template_33" />
      <xsl:call-template name="_template_34" />
    </PORR_MT049006UV01.InvestigationEvent>
  </xsl:template>
  <xsl:template name="_template_0">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_1">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_2">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_3">
    <id>
      <xsl:attribute name="root">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="assigningAuthorityName">
        <xsl:value-of select="." />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_4">
    <id>
      <xsl:attribute name="root">
        <xsl:value-of select="string(&quot;2.16.840.1.113883.3.965&quot;)" />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
    </id>
  </xsl:template>
  <xsl:template name="_template_5">
    <code>
      <xsl:attribute name="code">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="codeSystem">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="displayName">
        <xsl:value-of select="." />
      </xsl:attribute>
    </code>
  </xsl:template>
  <xsl:template name="_template_6">
    <text>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="concat(BAER/REPORT_INFORMATION/client__ip,BAER/REPORT_INFORMATION/client__agent)" />
    </text>
  </xsl:template>
  <xsl:template name="_template_7">
    <xsl:for-each select="BAER/aer__sender">
      <senderName>
        <xsl:attribute name="PUBLICID">
          <xsl:text>2222323</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="VERSION">
          <xsl:text>1.0</xsl:text>
        </xsl:attribute>
        <!-- <xsl:value-of select="."/> -->
        <!-- Follow 2 lines a workable sample case of DVM translation in XSLT -->
        <xsl:variable name="x" select="."/>
        <xsl:value-of select="document(translate('http://137.187.205.75:8080/caadapter-dvts/restful/context/SampleContext01/domain/maritalStatus/value/{V}', '{V}', $x))/VocabularyMappingData/MappingResults/Result" /> 
        <!-- <xsl:apply-templates select="." />-->
      </senderName>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_8">
    <statusCode>
      <xsl:attribute name="code">
        <xsl:value-of select="string(&quot;New&quot;)" />
      </xsl:attribute>
    </statusCode>
  </xsl:template>
  <xsl:template name="_template_9">
    <activityTime>
      <xsl:attribute name="value">
        <xsl:value-of select="." />
      </xsl:attribute>
    </activityTime>
  </xsl:template>
  <xsl:template name="_template_10">
    <availabilityTime />
  </xsl:template>
  <xsl:template name="_template_11">
    <reasonCode />
  </xsl:template>
  <xsl:template name="_template_12">
    <description>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
    </description>
  </xsl:template>
  <xsl:template name="_template_13">
    <subject>
      <xsl:call-template name="_template_14" />
      <xsl:call-template name="_template_15" />
      <xsl:call-template name="_template_16" />
      <xsl:call-template name="_template_17" />
    </subject>
  </xsl:template>
  <xsl:template name="_template_14">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_15">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_16">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_17">
    <investigativeSubject>
      <xsl:call-template name="_template_18" />
      <xsl:call-template name="_template_19" />
      <xsl:call-template name="_template_20" />
      <xsl:call-template name="_template_21" />
      <xsl:call-template name="_template_22" />
      <xsl:call-template name="_template_23" />
      <xsl:call-template name="_template_24" />
    </investigativeSubject>
  </xsl:template>
  <xsl:template name="_template_18">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_19">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_20">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_21">
    <id />
  </xsl:template>
  <xsl:template name="_template_22">
    <subjectPerson />
    <subjectAnimal />
  </xsl:template>
  <xsl:template name="_template_23">
    <subjectOf />
  </xsl:template>
  <xsl:template name="_template_24">
    <subjectOf />
  </xsl:template>
  <xsl:template name="_template_25">
    <receiver />
  </xsl:template>
  <xsl:template name="_template_26">
    <authorOrPerformer />
  </xsl:template>
  <xsl:template name="_template_27">
    <trigger />
  </xsl:template>
  <xsl:template name="_template_28">
    <support1 />
  </xsl:template>
  <xsl:template name="_template_29">
    <support2 />
  </xsl:template>
  <xsl:template name="_template_30">
    <support3 />
  </xsl:template>
  <xsl:template name="_template_31">
    <pertinentInformation1 />
  </xsl:template>
  <xsl:template name="_template_32">
    <pertinentInformation2 />
  </xsl:template>
  <xsl:template name="_template_33">
    <component />
  </xsl:template>
  <xsl:template name="_template_34">
    <pertainsTo />
  </xsl:template>
</xsl:transform>

