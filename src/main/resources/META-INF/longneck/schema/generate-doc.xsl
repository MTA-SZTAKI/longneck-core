<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tns="urn:hu.sztaki.ilab.longneck:1.0"
    xmlns:exsl="http://exslt.org/common"
    xmlns:d="urn:hu.sztaki.ilab.longneck.doc:1.0"
    exclude-result-prefixes="tns d xs exsl" extension-element-prefixes="tns d xs exsl">
  
  <!-- xmlns="http://www.w3.org/1999/xhtml" -->
  
  <xsl:output method="html"/>
  
  <xsl:template match="/">

    <div>
      <div class="page-header">
        <h1>XML API <small>Process XML language reference</small></h1>
      </div>
      <xsl:call-template name="constraints-navbar"/>
      <xsl:call-template name="blocks-navbar"/>
      
      <h1>Constraints</h1>
      <xsl:apply-templates select="/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-constraint']">
        <xsl:sort select="@name"/>
      </xsl:apply-templates>
      <h1>Blocks</h1>        
      <xsl:apply-templates select="/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-block']">
        <xsl:sort select="@name"/>
      </xsl:apply-templates>
    </div>
      
  </xsl:template>

  <!-- XML Element documentation -->
  <xsl:template match="xs:element">
    <section class="longneck-docblock">
      <a class="anchor">
        <xsl:attribute name="id"><xsl:value-of select="@name"/></xsl:attribute>
      </a>
      <h3>
        <xsl:text>&lt;</xsl:text><xsl:value-of select="@name"/><xsl:text>&gt;</xsl:text>
      </h3>
      <xsl:apply-templates select="self::node()" mode="doc"/>

      <h4>
        <xsl:text>Attributes</xsl:text>      
      </h4>
      <xsl:variable name="attributeCount">
        <xsl:apply-templates select="self::node()" mode="attribute-count"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$attributeCount &gt; 0">
          <table class="table" style="width: 100%">
            <thead>
              <tr>
                <th>name</th>
                <th><xsl:text> </xsl:text></th>
                <th>content</th>
                <th>description</th>
              </tr>
            </thead>
            <tbody>
              <xsl:apply-templates select="self::node()" mode="attribute-doc"/>
            </tbody>
          </table>    
        </xsl:when>
        <xsl:otherwise>
          <i><xsl:text>This element does not have attributes.</xsl:text></i>
        </xsl:otherwise>
      </xsl:choose>

      <h4>Content</h4>
      <xsl:variable name="childrenCount">
        <xsl:apply-templates select="self::node()" mode="children-count"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$childrenCount &gt; 0">
          <ul>
            <xsl:apply-templates select="self::node()" mode="children-doc"/>
          </ul>
        </xsl:when>
        <xsl:otherwise>
          <i><xsl:text>This element does not have children.</xsl:text></i>
        </xsl:otherwise>
      </xsl:choose>
      
    </section>
    <br/>
  </xsl:template>

  <!-- ========= Element documentation ========= -->
  
  <xsl:template match="xs:element | xs:extension | xs:complexType" mode="doc">
    <xsl:if test="xs:annotation">
      <div>
        <xsl:apply-templates select="xs:annotation"/>
      </div>
    </xsl:if>    
      
    <!-- Process the the type definition of this element (only if element definition). -->
    <xsl:if test="@type">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@type, 'tns:')]" mode="doc"/>
    </xsl:if>

    <!-- Process ancestor node, specified as extension. -->
    <xsl:for-each select="descendant::xs:extension">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@base, 'tns:')]" mode="doc"/>
    </xsl:for-each>
  </xsl:template>
  
  <!-- ========= Attribute documentation ========= -->
  
  <!-- Attribute containing nodes -->
  
  <xsl:template match="xs:element | xs:extension | xs:complexType" mode="attribute-doc">
    <!-- Process extended ancestor type. -->
    <xsl:for-each select="descendant::xs:extension">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@base, 'tns:')]" mode="attribute-doc"/>
      <xsl:apply-templates select="xs:attribute | xs:attributeGroup" mode="attribute-doc"/>
    </xsl:for-each>
    
    <!-- Process the the type definition of this element (only if element definition). -->
    <xsl:if test="@type">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@type, 'tns:')]" mode="attribute-doc"/>
    </xsl:if>
    
    <xsl:apply-templates select="xs:attribute | xs:attributeGroup" mode="attribute-doc"/>
  </xsl:template>
  
  <!-- Attribute template -->
  
  <xsl:template match="xs:attribute" mode="attribute-doc">
    <tr>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="@use">
            <xsl:value-of select="@use"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>optional</xsl:text>
          </xsl:otherwise>
        </xsl:choose>        
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="substring-before(@type, ':') = 'xs'">
            <xsl:value-of select="substring-after(@type, ':')"/>
          </xsl:when>
          <xsl:when test="/xs:schema/xs:simpleType[@name = substring-after(current()/@type, 'tns:')]">
            <xsl:apply-templates select="/xs:schema/xs:simpleType[@name = substring-after(current()/@type, 'tns:')]"/>
          </xsl:when>
          <xsl:when test="xs:simpleType">
            <xsl:apply-templates select="xs:simpleType"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>n/a</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
          <xsl:when test="substring-before(@type, ':') = 'xs'">
            <xsl:apply-templates select="xs:annotation"/>
          </xsl:when>
          <xsl:when test="/xs:schema/xs:simpleType[@name = substring-after(current()/@type, 'tns:')]">
            <xsl:apply-templates select="/xs:schema/xs:simpleType[@name = substring-after(current()/@type, 'tns:')]/xs:annotation"/>
          </xsl:when>
          <xsl:when test="xs:simpleType">
            <xsl:apply-templates select="xs:annotation"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>n/a</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>
  
  <!-- Attribute group template -->
  
  <xsl:template match="xs:attributeGroup" mode="attribute-doc">
    <xsl:apply-templates select="/xs:schema/xs:attributeGroup[@name = substring-after(current()/@ref, 'tns:')]/xs:attribute" mode="attribute-doc"/>
  </xsl:template>
  
  <!-- Attribute count -->
    
  <xsl:template match="xs:element | xs:complexType | xs:attributeGroup" mode="attribute-count">    
    <!-- Count local attributes -->
    <xsl:variable name="localCount" select="count(descendant::xs:attribute)"/>
    
    <!-- Count numbers from type -->
    <xsl:variable name="typeCounts">
      <xsl:choose>
        <xsl:when test="@type">
          <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@type, 'tns:')]" mode="attribute-count"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>    
    <xsl:variable name="typeCount" select="sum($typeCounts)"/>
    
    <!--  Count number from inheritance -->
    <xsl:variable name="inheritedCounts">
      <xsl:choose>
        <xsl:when test="current()//xs:extension">
          <xsl:for-each select="current()//xs:extension">
            <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@base, 'tns:')]" mode="attribute-count"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="inheritedCount" select="sum($inheritedCounts)"/>
    
    <!--  Count number from attribute groups -->
    <xsl:variable name="groupCounts">
      <xsl:choose>
        <xsl:when test="current()//xs:attributeGroup">
          <xsl:for-each select="current()//xs:attributeGroup">
            <xsl:apply-templates select="/xs:schema/xs:attributeGroup[@name = substring-after(current()/@ref, 'tns:')]" mode="attribute-count"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="groupCount" select="sum($groupCounts)"/>
    
    <xsl:value-of select="$localCount + $typeCount + $inheritedCount + $groupCount"/>
  </xsl:template>
  
  

  <!-- Simple type description -->
    
  <xsl:template match="xs:simpleType">
    <xsl:choose>
      
      <!-- String with a pattern -->
      <xsl:when test="xs:restriction[@base = 'xs:string']/xs:pattern">
        <code>
          <xsl:value-of select="xs:restriction[@base = 'xs:string']/xs:pattern/@value"/>
        </code>
      </xsl:when>
      
      <!-- Enumeration of possible values -->
      <xsl:when test="xs:restriction[@base = 'xs:string']/xs:enumeration">
        <xsl:text>One of </xsl:text>
        <xsl:for-each select="xs:restriction[@base = 'xs:string']/xs:enumeration">
          <code>
            <xsl:value-of select="@value"/>
          </code>
          <xsl:if test="following-sibling::xs:enumeration">
            <xsl:text>, </xsl:text>            
          </xsl:if>
        </xsl:for-each>
      </xsl:when>
      
      <!-- List of another type -->
      <xsl:when test="xs:list">
        <xsl:text>List of </xsl:text>
        <xsl:apply-templates select="/xs:schema/xs:simpleType[@name = substring-after(current()/xs:list/@itemType, 'tns:')]"/>
      </xsl:when>
      
    </xsl:choose>
  </xsl:template>

  <xsl:template match="xs:annotation">
    <xsl:apply-templates select="xs:documentation"/>
  </xsl:template>
  
  <xsl:template match="xs:documentation">
    <xsl:choose>
      <xsl:when test="@d:example">
        <h4>Example</h4>
        <pre class="prettyprint linenums lang-xml">
          <xsl:apply-templates select="child::*" mode="serialize"/>            
        </pre>
      </xsl:when>
      <xsl:when test="count(child::*) = 0">
        <p>
          <xsl:copy-of select="child::* | text()"/>
        </p>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="child::* | text()"/>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
  <!-- ========= Children documentation ========= -->
  
  <xsl:template match="xs:element | xs:extension | xs:complexType | xs:group" mode="children-doc">
    <!-- Process ancestor node, specified as extension. -->
    <xsl:for-each select="descendant::xs:extension">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@base, 'tns:')]" mode="children-doc"/>
      <xsl:apply-templates select="xs:sequence" mode="children-doc"/>
    </xsl:for-each>
      
    <!-- Process the the type definition of this element (only if element definition). -->
    <xsl:if test="@type">
      <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@type, 'tns:')]" mode="children-doc"/>
    </xsl:if>

    <xsl:apply-templates select="xs:sequence" mode="children-doc"/>
  </xsl:template>
  
  <!-- Process a sequence node -->
  
  <xsl:template match="xs:sequence" mode="children-doc">
    <xsl:for-each select="xs:element | xs:group">
      <xsl:choose>
        <!-- Local element declaration -->
        <xsl:when test="local-name() = 'element' and @name">
          <li>
            <strong>
              <xsl:apply-templates select="self::node()" mode="multiplicity"/>
            </strong>
            <xsl:call-template name="element-bookmark"/>
          </li>
        </xsl:when>
        
        <!-- Referenced element declaration -->
        <xsl:when test="local-name() = 'element' and @ref">
          <li>
            <strong>
              <xsl:apply-templates select="self::node()" mode="multiplicity"/>
            </strong>

            <!-- Check if the referenced element is abstract -->
            <xsl:choose>
              <!-- Abstract element -->
              <xsl:when test="/xs:schema/xs:element[@name = substring-after(current()/@ref, 'tns:') and @abstract = 'true']">
                <xsl:variable name="abstractName" select="/xs:schema/xs:element[@name = substring-after(current()/@ref, 'tns:') and @abstract = 'true']/@name"/>

                <!-- Expand substitution group for the abstract element -->
                <xsl:for-each select="/xs:schema/xs:element[@substitutionGroup = concat('tns:', $abstractName)]">
                  <xsl:call-template name="element-bookmark"/>
                  <xsl:if test="position() != last()">
                    <xsl:text>, </xsl:text>
                  </xsl:if>                  
                </xsl:for-each>              
              </xsl:when>
            </xsl:choose>
          </li>
        </xsl:when>        
        
        <!-- Referenced group -->
        <xsl:otherwise>
          <xsl:apply-templates select="/xs:schema/xs:group[@name = substring-after(current()/@ref, 'tns:')]" mode="children-doc"/>
        </xsl:otherwise>
        
      </xsl:choose>

    </xsl:for-each>      
  </xsl:template>
  
  <!-- Show multiplicity -->
  <xsl:template match="xs:element" mode="multiplicity">
    <xsl:choose>
      <xsl:when test="@minOccurs and @maxOccurs">
        <xsl:value-of select="@minOccurs"/>
        <xsl:text>..</xsl:text>
        <xsl:choose>
          <xsl:when test="@maxOccurs = 'unbounded'">
            <xsl:text>*</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@maxOccurs"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>
      </xsl:when>
      <xsl:when test="@minOccurs">
        <xsl:value-of select="@minOccurs"/>
        <xsl:text>..1: </xsl:text>
      </xsl:when>
      <xsl:when test="@maxOccurs">
        <xsl:text>1..</xsl:text>
        <xsl:choose>
          <xsl:when test="@maxOccurs = 'unbounded'">
            <xsl:text>*</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@maxOccurs"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>        
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Children count -->
  
  <xsl:template match="xs:element | xs:complexType | xs:attributeGroup" mode="children-count">    
    <!-- Count local attributes -->
    <xsl:variable name="localCount" select="count(descendant::xs:element)"/>
    
    <!-- Count numbers from type -->
    <xsl:variable name="typeCounts">
      <xsl:choose>
        <xsl:when test="@type">
          <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@type, 'tns:')]" mode="children-count"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>    
    <xsl:variable name="typeCount" select="sum($typeCounts)"/>
    
    <!--  Count number from inheritance -->
    <xsl:variable name="inheritedCounts">
      <xsl:choose>
        <xsl:when test="current()//xs:extension">
          <xsl:for-each select="current()//xs:extension">
            <xsl:apply-templates select="/xs:schema/xs:complexType[@name = substring-after(current()/@base, 'tns:')]" mode="children-count"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="inheritedCount" select="sum($inheritedCounts)"/>
    
    <!--  Count number from attribute groups -->
    <xsl:variable name="groupCounts">
      <xsl:choose>
        <xsl:when test="current()//xs:group">
          <xsl:for-each select="current()//xs:group">
            <xsl:apply-templates select="/xs:schema/xs:group[@name = substring-after(current()/@ref, 'tns:')]" mode="children-count"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="groupCount" select="sum($groupCounts)"/>
    
    <xsl:value-of select="$localCount + $typeCount + $inheritedCount + $groupCount"/>
  </xsl:template>
  
      
  <!-- ========= SERIALIZE templates ========= -->
  
  <xsl:template match="*" mode="serialize">
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="name()"/>
    
    <xsl:apply-templates select="attribute::*" mode="serialize"/>
    <xsl:choose>
      <xsl:when test="child::*">
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates mode="serialize"/>
        
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>/&gt;</xsl:text>        
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

  <xsl:template match="text()" mode="serialize">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="attribute::*" mode="serialize">
    <xsl:text> </xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text>=&quot;</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>&quot;</xsl:text>
  </xsl:template>

  <!-- === Navigation === -->
  
  <xsl:template match="xs:element" mode="navigation">
    <li>
      <xsl:call-template name="element-bookmark"/>
    </li>
  </xsl:template>  

  <xsl:template name="element-bookmark">
    <xsl:param name="name" select="@name"/>
    <a rel="bookmark">
      <xsl:attribute name="href">
        <xsl:text>#</xsl:text><xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="title"><xsl:value-of select="substring-before(xs:annotation/xs:documentation[1], '.')"/></xsl:attribute>
      <xsl:text>&lt;</xsl:text><xsl:value-of select="$name"/><xsl:text>&gt;</xsl:text>
    </a>
  </xsl:template>  
  
  <xsl:template name="constraints-navbar">
    <xsl:variable name="itemsPerColumn" select="ceiling(count(/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-constraint']) div 4)"/>
    
    <xsl:variable name="items">
      <xsl:for-each select="/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-constraint']">
        <xsl:sort select="@name"/>
        <xsl:copy-of select="self::node()"/>
      </xsl:for-each>
    </xsl:variable>
    
    <div class="well" style="margin-top: 1em;">
      <h4>Constraints</h4>
      <div class="row">
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &lt;= $itemsPerColumn]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; $itemsPerColumn and position() &lt;= (2 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; (2 * $itemsPerColumn) and position() &lt;= (3 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
        <div class="span2">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; (3 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template name="blocks-navbar">
    <xsl:variable name="itemsPerColumn" select="ceiling(count(/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-block']) div 4)"/>
    
    <xsl:variable name="items">
      <xsl:for-each select="/xs:schema/xs:element[@substitutionGroup = 'tns:abstract-block']">
        <xsl:sort select="@name"/>
        <xsl:copy-of select="self::node()"/>
      </xsl:for-each>
    </xsl:variable>
    
    <div class="well" style="margin-top: 1em;">
      <h4>Blocks</h4>
      <div class="row">
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &lt;= $itemsPerColumn]" mode="navigation"/>
          </ul>
        </div>
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; $itemsPerColumn and position() &lt;= (2 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
        <div class="span3">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; (2 * $itemsPerColumn) and position() &lt;= (3 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
        <div class="span2">
          <ul class="nav nav-list">
            <xsl:apply-templates select="exsl:node-set($items)/xs:element[position() &gt; (3 * $itemsPerColumn)]" mode="navigation">
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </ul>
        </div>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
