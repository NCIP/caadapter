<?xml version="1.0" encoding="UTF-8"?>
<!-- messages2java.xsl - generates Java Config code from Messages.xml dump.

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

    <xsl:template match="messages">
        <xsl:param name="messages-package" select="@packagename"/>
        <xsl:param name="messages-path" select="concat($base-path,'/',f:path-from-package($messages-package))"/>
        <xsl:param name="classname" select="@classname"/>

        <xsl:result-document href="{$messages-path}/{$classname}.java" format="java">
            <xsl:apply-templates mode="instantiate" select="$header-template">
                <xsl:with-param name="classname" select="@classname"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="node()"/>
            <xsl:apply-templates mode="instantiate" select="$tail-template">
            </xsl:apply-templates>
        </xsl:result-document>
    </xsl:template>

<xsl:template match="message">
        VALIDATION_MESSAGES.put("<xsl:value-of select="@id"/>", "<xsl:value-of select="normalize-space(template/text())"/>");</xsl:template>

<xsl:template mode="instantiate" match="className">
   <xsl:param name="classname"/>
   <xsl:value-of select="$classname"/>
</xsl:template>

<xsl:variable name="header-template">/** THIS FILE IS GENERATED AUTOMATICALLY - DO NOT MODIFY. PLEASE EDIT MESSAGES.XML INSTEAD
 */
package gov.nih.nci.caadapter.common;

import java.util.HashMap;
import java.util.Map;
import gov.nih.nci.caadapter.common.Message;


/** Messages Resource File */
public class <className/>
{
    public static final Map&lt;String, String&gt; VALIDATION_MESSAGES = new HashMap&lt;String, String&gt;();
    static
    {
</xsl:variable>

<xsl:variable name="tail-template">
    }

    public static String getMessageTemplate(String id){
        return VALIDATION_MESSAGES.get(id);
    }

    public static Message getMessage(String id, Object[] args){
        return new Message(VALIDATION_MESSAGES.get(id), args);
    }

 }
 // The End of Messages Resource file
</xsl:variable>

</xsl:transform>
