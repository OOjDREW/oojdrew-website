<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:transform version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">

    <xsl:param name="database">tec</xsl:param>
    <xsl:param name="table">Taxonomy</xsl:param>
    <xsl:param name="idcol">iID</xsl:param>
    <xsl:param name="namecol">szName</xsl:param>
    <xsl:param name="parentcol">iParent</xsl:param>

    <xsl:template match="/">
        <mysqldump>
            <database>
                <xsl:attribute name="name"><xsl:value-of select="$database" /></xsl:attribute>
                <table>
                    <xsl:attribute name="name"><xsl:value-of select="$table" /></xsl:attribute>
                    <xsl:apply-templates mode="class" select="./*" />
                </table>
            </database>            
        </mysqldump>
    </xsl:template>
    
    <xsl:template match="rdfs:Class" mode="class">
        <row>
            <field name="{$idcol}"><xsl:value-of select="./rdfs:label" /></field>
            <field name="{$namecol}"><xsl:value-of select="./rdfs:comment" /></field>
            <xsl:choose>
                <xsl:when test="count(./rdfs:subClassOf) = 0">
                    <field name="{$parentcol}">-1</field>
                </xsl:when> 
                <xsl:otherwise>
                    <xsl:apply-templates mode="subclass" select="../rdfs:Class">
                        <xsl:with-param name="parent" select="./rdfs:subClassOf/@rdf:resource" />
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </row>
    </xsl:template>
    
    <xsl:template match="rdfs:Class" mode="subclass">
        <xsl:param name="parent" />
        <xsl:if test="substring-after($parent, '#')=./@rdf:ID">
            <field name="{$parentcol}">
                    <xsl:value-of select="./rdfs:label" />
            </field>
        </xsl:if>
    </xsl:template>
</xsl:transform>