<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
    <xsl:param name="database">tec</xsl:param>
    <xsl:param name="table">Taxonomy</xsl:param>
    <xsl:param name="idcol">iID</xsl:param>
    <xsl:param name="namecol">szName</xsl:param>
    <xsl:param name="parentcol">iParent</xsl:param>

    <xsl:template match="/">
        <rdf:RDF xmlns:rdf= "http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
            <xsl:apply-templates />
        </rdf:RDF>
    </xsl:template>
    
    <xsl:template match="database">
        <xsl:apply-templates select="./table[@name=$table]" />
    </xsl:template>
    
    <xsl:template match="table">
        <xsl:apply-templates mode="class" />
    </xsl:template>
    
    <xsl:template match="row" mode="parent">
        <xsl:param name="parentID">-1</xsl:param>
        <xsl:if test="$parentID=./field[@name=$idcol]">
            <rdfs:subClassOf>
                <xsl:attribute name="rdf:resource">#<xsl:choose>
                        <xsl:when test="contains(./field[@name=$namecol], '(')">
                            <xsl:value-of select="translate(substring-before(./field[@name='szName'], '('), ' ', '_')" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="translate(./field[@name=$namecol], ' ', '_')" />
                        </xsl:otherwise>
                    </xsl:choose></xsl:attribute>        
            </rdfs:subClassOf>
        </xsl:if>    
    </xsl:template>
    
    <xsl:template match="row" mode="class">
        <rdfs:Class>
            <xsl:attribute name="rdf:ID"><xsl:choose>
                    <xsl:when test="contains(./field[@name=$namecol], '(')">
                         <xsl:value-of select="translate(substring-before(./field[@name='szName'], '('), ' ', '_')" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="translate(./field[@name=$namecol], ' ', '_')" />
                    </xsl:otherwise>
                </xsl:choose></xsl:attribute>
                            
            <rdfs:label><xsl:value-of select="./field[@name=$idcol]" /></rdfs:label>
            <rdfs:comment><xsl:value-of select="./field[@name=$namecol]" /></rdfs:comment>
                
            <xsl:if test="./field[@name='iParent'] &gt; -1">
                <xsl:apply-templates select="../row" mode="parent">
                    <xsl:with-param name="parentID" select="./field[@name=$parentcol]" />
                </xsl:apply-templates>
            </xsl:if>
        </rdfs:Class>
    </xsl:template>
        
</xsl:transform>