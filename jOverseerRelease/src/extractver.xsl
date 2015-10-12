<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="/beans"><xsl:apply-templates/></xsl:template>
  <xsl:template match="bean[@id='applicationDescriptor']"><xsl:apply-templates/></xsl:template>
  <xsl:template match="property[@name='version']"><xsl:value-of select="@value"/></xsl:template>
<!--  <xsl:template match="/beans/bean[@id='applicationDescriptor']/property[@name='version']">
    <xsl:value-of select="@value"/>
  </xsl:template>
-->  <xsl:template match="*"/>
</xsl:stylesheet>
