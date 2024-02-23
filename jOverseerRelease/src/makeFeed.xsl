<?xml version="1.0"?>
<!-- extract the first element of Changelog and insert it if needed into the feed.xml file -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"/>
	<xsl:param name="version"/>
	<xsl:variable name="changelog" select="unparsed-text('../../joverseerjar/resources/changelog.txt')"/>
	<xsl:variable name="lines" select="tokenize($changelog, '\r\n|\r|\n')[not(position()=last() and .='')]"/>
	<xsl:variable name="latestChangelogVersion" select="$lines[starts-with(.,'VERSION')][1]"/>
	<xsl:variable name="secondChangelogVersion" select="$lines[starts-with(.,'VERSION')][2]"/>
	<xsl:variable name="countOfVersionsInChangelog" select="count($lines[starts-with(.,'VERSION')])"/>
	<xsl:variable name="countOfLines" select="count($lines)"/>
	<xsl:variable name="count" select="($lines[starts-with(.,'VERSION')])
	<xsl:template match="/rss">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="channel">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="item[1]">
		<xsl:call-template name="firstItem"/>
	</xsl:template>
	<xsl:template name="firstItem">
		<xsl:text>
	</xsl:text>
		<item>
			<xsl:choose>
				<xsl:when test="title=$version">
				<xsl:text> -match title version- </xsl:text>
					<xsl:copy-of select="title"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> -generate title version from first changelog title- </xsl:text>
					<title>
						<xsl:value-of select="$version"/>
					</title>
				</xsl:otherwise>
			</xsl:choose>
			<pubDate>
				<xsl:value-of select="format-dateTime(current-dateTime(), '[D01] [MNn,3-3] [Y0001]')"/>
			</pubDate>
			<description>
				<xsl:value-of select="$countOfLines"/>
				<xsl:text> - </xsl:text>
				<xsl:value-of select="$latestChangelogVersion"/>
				<!-- lines has all the lines of the change log -->
				<!-- want all the lines that have the position less than the position of the line containing the second Version -->
				<xsl:copy-of select="$lines[position() &lt; 10]"/>
			</description>
		</item>
		<xsl:text>
 -rest of file follows- </xsl:text>
	</xsl:template>
	<xsl:template match="*">
		<xsl:copy-of select="."/>
	</xsl:template>
</xsl:stylesheet>
