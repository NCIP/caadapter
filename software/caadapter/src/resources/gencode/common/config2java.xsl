<?xml version="1.0" encoding="UTF-8"?>
<!-- config2java.xsl - generates Java Config code from Config-dev(dist).xml dump.

-->
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f" xmlns:saxon="http://saxon.sf.net/" exclude-result-prefixes="xsl f saxon">
    <xsl:output name="java" method="text" encoding="ASCII"/>
    <xsl:strip-space elements="*"/>

    <!-- parameters to construct paths and names -->

    <xsl:param name="base-path" select="'gencode'"/>

    <!-- Some functions to deal with naming conventions -->

    <xsl:function name="f:path-from-package">
        <xsl:param name="package-name"/>
        <xsl:sequence select="replace($package-name,'\.','/')"/>
    </xsl:function>

    <xsl:function name="f:quoted-data-for-String">
      <xsl:param name="data"/>
      <xsl:param name="type"/>
      <xsl:variable name="String" select = "'String'"/>
      <xsl:variable name="result">
        <xsl:choose>
          <xsl:when test="$data and string-length($data) gt 0">
            <xsl:if test="$type = $String"><xsl:text>"</xsl:text></xsl:if>
        <xsl:value-of select="$data"/>
            <xsl:if test="$type = $String"><xsl:text>"</xsl:text></xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>null</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:sequence select="$result"/>
    </xsl:function>

    <xsl:template match="config">
        <xsl:param name="config-package" select="@packagename"/>
        <xsl:param name="config-path" select="concat($base-path,'/',f:path-from-package($config-package))"/>
        <xsl:param name="classname" select="@classname"/>
        <xsl:result-document href="{$config-path}/{$classname}.java" format="java">
            <xsl:apply-templates mode="instantiate" select="$header-template">
                <xsl:with-param name="classname" select="@classname"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="node()"/>
            <xsl:apply-templates mode="instantiate" select="$tail-template">
            </xsl:apply-templates>
        </xsl:result-document>
    </xsl:template>

<xsl:template match="constant">
    public static <xsl:value-of select="@type"/>  <xsl:text> </xsl:text> <xsl:value-of select="@name"/> = <xsl:value-of select="f:quoted-data-for-String(@value, @type)"/>;</xsl:template>

    <xsl:template mode="instantiate" match="className">
      <xsl:param name="classname"/>
      <xsl:value-of select="$classname"/>
    </xsl:template>

    <xsl:variable name="header-template">/** THIS FILE IS GENERATED AUTOMATICALLY - DO NOT MODIFY. PLEASE EDIT CONFIG-DEV(CONFIG-DIST).XML INSTEAD
 */
package gov.nih.nci.caadapter.common.util;

import java.awt.Color;

/** Message Resource File */
public class <className/>
{
    </xsl:variable>

    <xsl:variable name="tail-template">
 }
 // The End of Config file
    </xsl:variable>

</xsl:transform>
