<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/">
    <printorder>
      <xsl:attribute name="orderid">
        <xsl:value-of select="concat(shiporder/@orderid,string(&quot;localPrintType&quot;))" />
      </xsl:attribute>
      <xsl:attribute name="printType">
        <xsl:text>text/html</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_0" />
      <xsl:call-template name="_template_1" />
      <xsl:call-template name="_template_6" />
    </printorder>
  </xsl:template>
  <xsl:template name="_template_0">
    <buyer>
      <xsl:value-of select="concat(shiporder/orderperson,current-date())" />
    </buyer>
  </xsl:template>
  <xsl:template name="_template_1">
    <address>
      <xsl:call-template name="_template_2" />
      <xsl:call-template name="_template_3" />
      <xsl:call-template name="_template_4" />
      <xsl:call-template name="_template_5" />
    </address>
  </xsl:template>
  <xsl:template name="_template_2">
    <xsl:for-each select="shiporder/shipto/name">
      <name>
        <xsl:attribute name="PUBLICID">
          <xsl:text>2222323</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="VERSION">
          <xsl:text>1.0</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </name>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_3">
    <xsl:for-each select="shiporder/shipto/address">
      <street>
        <xsl:apply-templates select="." />
      </street>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_4">
    <xsl:for-each select="shiporder/shipto/city">
      <city>
        <xsl:apply-templates select="." />
      </city>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_5">
    <xsl:for-each select="shiporder/shipto/country">
      <country>
        <xsl:apply-templates select="." />
      </country>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_6">
    <xsl:for-each select="shiporder/item">
      <item>
        <xsl:call-template name="_template_7" />
        <xsl:call-template name="_template_8" />
        <xsl:call-template name="_template_9" />
        <xsl:call-template name="_template_10" />
      </item>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_7">
    <name>
      <xsl:value-of select="current-dateTime()" />
    </name>
  </xsl:template>
  <xsl:template name="_template_8">
    <description>
      <xsl:value-of select="concat(title,note)" />
    </description>
  </xsl:template>
  <xsl:template name="_template_9">
    <xsl:for-each select="quantity">
      <quantity>
        <xsl:apply-templates select="." />
      </quantity>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_10">
    <price>
      <xsl:value-of select="day-from-date(xs:date(string(&quot;2010-01-25&quot;))) - day-from-date(xs:date(string(&quot;2010-01-01&quot;)))" />
    </price>
  </xsl:template>
</xsl:transform>

