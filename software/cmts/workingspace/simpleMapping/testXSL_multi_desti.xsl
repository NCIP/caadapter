<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:output method="xml" indent="yes" />
  <xsl:template match="/">
    <printorder>
      <xsl:attribute name="orderid">
        <xsl:value-of select="shiporder/@orderid" />
      </xsl:attribute>
      <xsl:attribute name="printType">
        <xsl:text>text/html</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="_template_0" />
    </printorder>
  </xsl:template>
  <xsl:template name="_template_0">
    <xsl:for-each select="shiporder/orderperson">
      <buyer>
        <xsl:apply-templates select="." />
      </buyer>
    </xsl:for-each>
    <item>
      <xsl:call-template name="_template_1" />
    </item>
    <xsl:for-each select="shiporder/shipto">
      <addressee>
        <xsl:call-template name="_template_2" />
      </addressee>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_1">
    <xsl:for-each select="shiporder/item/title">
      <name>
        <xsl:apply-templates select="." />
      </name>
    </xsl:for-each>
    <xsl:for-each select="shiporder/item/note">
      <description>
        <xsl:apply-templates select="." />
      </description>
    </xsl:for-each>
    <xsl:for-each select="shiporder/item/quantity">
      <quantity>
        <xsl:apply-templates select="." />
      </quantity>
    </xsl:for-each>
    <price>
      <xsl:value-of select="number(shiporder/item/price)* number(string(&quot;1000&quot;))" />
    </price>
  </xsl:template>
  <xsl:template name="_template_2">
    <name>
      <xsl:call-template name="_template_3" />
    </name>
    <xsl:for-each select="address">
      <street>
        <xsl:apply-templates select="." />
      </street>
    </xsl:for-each>
    <xsl:for-each select="city">
      <city>
        <xsl:apply-templates select="." />
      </city>
    </xsl:for-each>
    <xsl:for-each select="country">
      <country>
        <xsl:apply-templates select="." />
      </country>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="_template_3">
    <xsl:for-each select="name/firstname">
      <given_name>
        <xsl:attribute name="PUBLICID">
          <xsl:text>2179589</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="VERSION">
          <xsl:text>2.0</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </given_name>
    </xsl:for-each>
    <xsl:for-each select="name/lastname">
      <family_name>
        <xsl:attribute name="PUBLICID">
          <xsl:text>2179591</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="VERSION">
          <xsl:text>2.0</xsl:text>
        </xsl:attribute>
        <xsl:apply-templates select="." />
      </family_name>
    </xsl:for-each>
  </xsl:template>
</xsl:transform>

