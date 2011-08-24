<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:output method="xml" indent="yes" />
  <xsl:template match="/">
    <xsl:for-each select="ADT_A03">
      <PRPA_IN402003UV02>
        <xsl:attribute name="ITSVersion">
          <xsl:text>XML_1.0</xsl:text>
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
        <xsl:call-template name="_template_13" />
        <xsl:call-template name="_template_14" />
        <xsl:call-template name="_template_15" />
        <xsl:call-template name="_template_16" />
        <xsl:call-template name="_template_22" />
        <xsl:call-template name="_template_23" />
      </PRPA_IN402003UV02>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_0">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_1">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_2">
    <xsl:for-each select="MSH/MSH.3">
      <templateId>
        <xsl:attribute name="root">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="extension">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="assigningAuthorityName">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </templateId>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_3">
    <xsl:for-each select="MSH/MSH.4">
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
        <xsl:apply-templates select="." />
      </id>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_4">
    <xsl:for-each select="MSH/MSH.7">
      <creationTime>
        <xsl:attribute name="value">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="nullFlavor">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </creationTime>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_5">
    <securityText>
      <xsl:attribute name="representation">
        <xsl:text>TXT</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="mediaType">
        <xsl:text>text/plain</xsl:text>
      </xsl:attribute>
    </securityText>
  </xsl:template>
  <xsl:template name="_template_6">
    <xsl:for-each select="MSH/MSH.2">
      <versionCode>
        <xsl:apply-templates select="." />
      </versionCode>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_7">
    <interactionId />
  </xsl:template>
  <xsl:template name="_template_8">
    <profileId />
  </xsl:template>
  <xsl:template name="_template_9">
    <xsl:for-each select="MSH/MSH.5/HD.1">
      <processingCode>
        <xsl:apply-templates select="." />
      </processingCode>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_10">
    <xsl:for-each select="MSH/MSH.5/HD.2">
      <processingModeCode>
        <xsl:apply-templates select="." />
      </processingModeCode>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_11">
    <acceptAckCode />
  </xsl:template>
  <xsl:template name="_template_12">
    <sequenceNumber />
  </xsl:template>
  <xsl:template name="_template_13">
    <attachmentText />
  </xsl:template>
  <xsl:template name="_template_14">
    <receiver />
  </xsl:template>
  <xsl:template name="_template_15">
    <respondTo />
  </xsl:template>
  <xsl:template name="_template_16">
    <xsl:for-each select="MSH/MSH.6">
      <sender>
        <xsl:call-template name="_template_17" />
        <xsl:call-template name="_template_18" />
        <xsl:call-template name="_template_19" />
        <xsl:call-template name="_template_20" />
        <xsl:call-template name="_template_21" />
      </sender>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_17">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_18">
    <typeId>
      <xsl:attribute name="root">
        <xsl:value-of select="." />
      </xsl:attribute>
      <xsl:attribute name="extension">
        <xsl:value-of select="." />
      </xsl:attribute>
    </typeId>
  </xsl:template>
  <xsl:template name="_template_19">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_20">
    <telecom />
  </xsl:template>
  <xsl:template name="_template_21">
    <device />
  </xsl:template>
  <xsl:template name="_template_22">
    <attentionLine />
  </xsl:template>
  <xsl:template name="_template_23">
    <xsl:for-each select="MSH">
      <controlActProcess>
        <xsl:call-template name="_template_24" />
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
        <xsl:call-template name="_template_35" />
        <xsl:call-template name="_template_36" />
        <xsl:call-template name="_template_37" />
        <xsl:call-template name="_template_38" />
        <xsl:call-template name="_template_57" />
      </controlActProcess>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_24">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_25">
    <xsl:for-each select="MSH.9">
      <typeId>
        <xsl:attribute name="root">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="extension">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="assigningAuthorityName">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </typeId>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_26">
    <xsl:for-each select="MSH.9">
      <templateId>
        <xsl:attribute name="root">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="extension">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="assigningAuthorityName">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </templateId>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_27">
    <xsl:for-each select="MSH.10">
      <id>
        <xsl:apply-templates select="." />
      </id>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_28">
    <xsl:for-each select="MSH.12">
      <code>
        <xsl:attribute name="codeSystem">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="codeSystemName">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </code>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_29">
    <text />
  </xsl:template>
  <xsl:template name="_template_30">
    <effectiveTime />
  </xsl:template>
  <xsl:template name="_template_31">
    <priorityCode />
  </xsl:template>
  <xsl:template name="_template_32">
    <reasonCode />
  </xsl:template>
  <xsl:template name="_template_33">
    <xsl:for-each select="MSH.19">
      <languageCode>
        <xsl:attribute name="codeSystem">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:attribute name="codeSystemName">
          <xsl:value-of select="." />
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </languageCode>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_34">
    <overseer />
  </xsl:template>
  <xsl:template name="_template_35">
    <authorOrPerformer />
  </xsl:template>
  <xsl:template name="_template_36">
    <dataEnterer />
  </xsl:template>
  <xsl:template name="_template_37">
    <informationRecipient />
  </xsl:template>
  <xsl:template name="_template_38">
    <subject>
      <xsl:call-template name="_template_39" />
      <xsl:call-template name="_template_40" />
      <xsl:call-template name="_template_41" />
      <xsl:call-template name="_template_42" />
    </subject>
  </xsl:template>
  <xsl:template name="_template_39">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_40">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_41">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_42">
    <inpatientEncounterEvent>
      <xsl:call-template name="_template_43" />
      <xsl:call-template name="_template_44" />
      <xsl:call-template name="_template_45" />
      <xsl:call-template name="_template_46" />
      <xsl:call-template name="_template_47" />
      <xsl:call-template name="_template_48" />
      <xsl:call-template name="_template_49" />
      <xsl:call-template name="_template_50" />
      <xsl:call-template name="_template_51" />
      <xsl:call-template name="_template_52" />
      <xsl:call-template name="_template_53" />
      <xsl:call-template name="_template_54" />
      <xsl:call-template name="_template_55" />
      <xsl:call-template name="_template_56" />
    </inpatientEncounterEvent>
  </xsl:template>
  <xsl:template name="_template_43">
    <realmCode />
  </xsl:template>
  <xsl:template name="_template_44">
    <typeId />
  </xsl:template>
  <xsl:template name="_template_45">
    <templateId />
  </xsl:template>
  <xsl:template name="_template_46">
    <id />
  </xsl:template>
  <xsl:template name="_template_47">
    <code />
  </xsl:template>
  <xsl:template name="_template_48">
    <statusCode />
  </xsl:template>
  <xsl:template name="_template_49">
    <effectiveTime />
  </xsl:template>
  <xsl:template name="_template_50">
    <activityTime />
  </xsl:template>
  <xsl:template name="_template_51">
    <lengthOfStayQuantity />
  </xsl:template>
  <xsl:template name="_template_52">
    <dischargeDispositionCode />
  </xsl:template>
  <xsl:template name="_template_53">
    <xsl:for-each select="../DG1">
      <subject>
        <xsl:apply-templates select="." />
      </subject>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_54">
    <discharger />
  </xsl:template>
  <xsl:template name="_template_55">
    <reason />
  </xsl:template>
  <xsl:template name="_template_56">
    <departedBy />
  </xsl:template>
  <xsl:template name="_template_57">
    <reasonOf />
  </xsl:template>
</xsl:transform>

