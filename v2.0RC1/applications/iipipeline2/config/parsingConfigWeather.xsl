<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <Document>
        <xsl:for-each select="current_observation">
            <Entity>      
				<campus>UPC</campus>
                <Humidity><xsl:value-of select="relative_humidity"/></Humidity>
                <Latitude><xsl:value-of select="latitude"/></Latitude>           
                <Longitude><xsl:value-of select="longitude"/></Longitude>
                <Temperature><xsl:value-of select="temp_f"/></Temperature>
                <WindSpeed><xsl:value-of select="wind_mph"/></WindSpeed>
                <Pressure><xsl:value-of select="pressure_mb"/></Pressure>
                <Dewpoint><xsl:value-of select="dewpoint_f"/></Dewpoint>
                <Visibility><xsl:value-of select="visibility_mi"/></Visibility>
                <time><xsl:value-of select="observation_time_rfc822"/></time>
            </Entity>           
        </xsl:for-each>
       </Document>
    </xsl:template>   
</xsl:stylesheet>
