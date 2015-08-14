<?xml version="1.0" encoding="UTF-8"?>

<!--
Author: Franklin Chen
Based on Romeo's

Note the careful generation of whitespace.
-->
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:tb="http://www.talkbank.org/ns/talkbank">

<xsl:output method="text" encoding="UTF-8"/>

<xsl:strip-space elements="*"/>

<!-- Note use of tabs (&#9;) in some places -->
<xsl:template match="CHAT">
  <xsl:text>@UTF8
@Begin
</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>@End
</xsl:text>
</xsl:template>

<xsl:template match="Participants">
  <xsl:text>@Participants:&#9;</xsl:text>
  <xsl:apply-templates mode="list"/>
  <xsl:text>
</xsl:text>
  <xsl:apply-templates mode="Id"/>
  <xsl:apply-templates/>
  <!-- fmc not in amy20
@Date:&#9;<xsl:text >&#9;</xsl:text><xsl:value-of select="//@Date" />
-->
</xsl:template>

<!-- List the participants -->
<xsl:template match="participant" mode="list">
  <xsl:if test="position()!=1">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:value-of select="@id"/><xsl:text> </xsl:text>
  <xsl:if test="@name">
    <xsl:value-of select="@name"/><xsl:text> </xsl:text>
  </xsl:if>
  <xsl:value-of select="@role"/>
  <xsl:if test="position()!=last()">
    <xsl:text>,</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="participant" mode="Id">
  <xsl:text>@ID:&#9;</xsl:text>

  <xsl:value-of select="/CHAT/@Lang"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="/CHAT/@Corpus"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@id"/>

  <xsl:text>|</xsl:text>

  <!-- Format age as in "P2Y3M4D" to "2;3.4" -->
  <xsl:value-of select="translate(@age, 'YMPD', ';.')"/>
  <xsl:if test="@ageTo">
    <xsl:text> - </xsl:text>
    <xsl:value-of select="translate(@ageTo, 'YMPD', ';.')"/>
  </xsl:if>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@sex"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@group"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@SES"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@role"/>

  <xsl:text>|</xsl:text>

  <xsl:value-of select="@setting"/>

  <xsl:text>|</xsl:text>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="participant">
  <xsl:if test="@birthday">
    <xsl:text>@Birth of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@birthday"/>
    <xsl:text>
</xsl:text>
  </xsl:if>
<!-- Don't output old stuff.
  <xsl:text>@Age of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
  <xsl:if test="@age">
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@age"/>
  </xsl:if>
  <xsl:text>
</xsl:text>
  <xsl:text>@Sex of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
  <xsl:if test="@sex">
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@sex"/>
  </xsl:if>
  <xsl:text>
</xsl:text>
  <xsl:text>@Group of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
  <xsl:if test="@group">
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@group"/>
  </xsl:if>
  <xsl:text>
</xsl:text>
  <xsl:text>@SES of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
  <xsl:if test="@SES">
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@SES"/>
  </xsl:if>
  <xsl:text>
</xsl:text>
  <xsl:text>@Role of </xsl:text><xsl:value-of select="@id"/><xsl:text>:</xsl:text>
  <xsl:if test="@Setting">
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="@role"/>
  </xsl:if>
  <xsl:text>
</xsl:text>
-->
</xsl:template>

<!-- fmc check a document with such? -->
<xsl:template match="tcu">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="begin-gem">
  <xsl:choose>
    <xsl:when test="@label = ''">
      <xsl:text>@Bg</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>@Bg:&#9;</xsl:text>
      <xsl:value-of select="@label"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="end-gem">
  <xsl:choose>
    <xsl:when test="@label = ''">
      <xsl:text>@Eg</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>@Eg:&#9;</xsl:text>
      <xsl:value-of select="@label"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="lazy-gem">
  <xsl:text>@G:&#9;</xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>
</xsl:text>
  <xsl:apply-templates/>
</xsl:template>



<!-- fmc ?
xml:space="default"
test media, mor
s k etc?
-->
<xsl:template match="u">
  <xsl:text>*</xsl:text>
  <xsl:value-of select="@who"/>
  <xsl:text>:&#9;</xsl:text>
  <xsl:apply-templates select="linker"/>
  <xsl:apply-templates select="precode"/>
  <xsl:apply-templates select="w|g|e|wn|freecode|pause|s"/>
  <xsl:apply-templates select="t"/>
  <xsl:apply-templates select="postcode"/>
  <!--fmc test media parts
       float sloppy also-->
  <xsl:if test="media/@href">
    <xsl:text> URLBULLET%snd:&quot;</xsl:text>
    <xsl:value-of select="media/@href"/>
    <xsl:text>&quot;_</xsl:text>
    <xsl:value-of select="round(1000 * media/@start)"/>
    <xsl:text>_</xsl:text>
    <xsl:value-of select="round(1000 * media/@end)"/>
    <xsl:text>URLBULLET</xsl:text>
  </xsl:if>
  <xsl:text>
</xsl:text>
  <xsl:if test="mor">
    <xsl:text>%mor:&#9;</xsl:text>
    <xsl:apply-templates select="mor"/>
  </xsl:if>
  <xsl:apply-templates select="a"/>
</xsl:template>

<xsl:template match="comment">
  <xsl:text>@</xsl:text>
  <xsl:choose>
    <xsl:when test="@type='Generic'">
      <xsl:text>Comment</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@type"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="@type='New Episode'">
      <!-- No colon -->
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>:&#9;</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="s">
  <xsl:choose>
    <xsl:when test="@type='comma'">
      <xsl:text>,</xsl:text>
    </xsl:when>
    <xsl:when test="@type='semicolon'">
      <xsl:text>;</xsl:text>
    </xsl:when>
    <xsl:when test="@type='clause delimiter'">
      <xsl:text>[^c]</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        Error: separator &quot;<xsl:value-of select="@type"/> not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Do not generate delimiting spaces if in a word net -->
<xsl:template match="wn/w">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="w">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<!-- fmc exhaustive application of inner contents? -->
<xsl:template match="g">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>&lt;</xsl:text>
  <xsl:apply-templates select="w|g|e|wn|pause|s"/>
  <xsl:text>&gt;</xsl:text>
  <xsl:apply-templates select="k|error|overlap|a|tone|r"/>
</xsl:template>


<xsl:template match="wn">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<!-- Precode:  new with Brian -->
<xsl:template match="precode">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>[- </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text>
</xsl:template>


<xsl:template match="error">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>[*</xsl:text>
  <xsl:if test="text()">
    <xsl:text> </xsl:text>
    <xsl:value-of select="text()"/>
  </xsl:if>
  <xsl:text>]</xsl:text>
</xsl:template>


<!-- Handle indices
     No longer output [<>]
 -->
<xsl:template match="overlap">
  <xsl:text> </xsl:text>
  <xsl:text>[</xsl:text>
  <xsl:choose>
    <xsl:when test="@type='overlap follows'">
      <xsl:text>&gt;</xsl:text>
    </xsl:when>
    <xsl:when test="@type='overlap precedes'">
      <xsl:text>&lt;</xsl:text>
    </xsl:when>
  </xsl:choose>
  <xsl:if test="@index">
    <xsl:value-of select="@index"/>
  </xsl:if>
  <xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="freecode">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>[^ </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="postcode">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:text>[+ </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text>
</xsl:template>

<!-- fmc Brian modified, but I am worried because he is adding spaces at
     random places and I don't know why -->
<xsl:template match="w/a|e/a|wn/a|g/a|precodes/a">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:choose>

    <!--fmc
    <xsl:when test="@type='extension'">
      <xsl:choose>
        <xsl:when test="@flavor">
          <xsl:choose>
            <xsl:when test="@flavor='code'">
              <xsl:text>[$ </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:message terminate="yes">
                extension flavor &quot;<xsl:value-of select="@flavor"/>&quot; not recognized by stylesheet.
              </xsl:message>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">
            extension has no flavor specified.
          </xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="@type='alternative'"><xsl:text>[%alt: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='cohesion'"><xsl:text> [%coh: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='comments'"><xsl:text> [%com: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='completion'"><xsl:text>[0 </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='SALT'"><xsl:text> [%def: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='english translation'"><xsl:text> [%eng: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='errcoding'"><xsl:text>[%err: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='facial'"><xsl:text> [%fac: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='flow'"><xsl:text>[%flo: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='target gloss'"><xsl:text>[%gls: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='language'"><xsl:text>[=: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='language'"><xsl:text>[%lan: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='phonomodel'"><xsl:text>[%mod: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='orthography'"><xsl:text>[%ort: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='phonetic'"><xsl:text> [%pho: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='speech act'"><xsl:text> [%spa: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='syntactic structure'"><xsl:text>[%syn: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='time stamp'"><xsl:text> [%tim: </xsl:text><xsl:value-of select="text()"/></xsl:when>
 -->
    <xsl:when test="@type='actions'"><xsl:text> [%act: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='addressee'"><xsl:text> [%add: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='alternative'"><xsl:text> [=? </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='comments'"><xsl:text> [% </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='explanation'"><xsl:text> [= </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='gesture'"><xsl:text> [%gpx: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='intonation'"><xsl:text> [%int: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='paralinguistics'"><xsl:text> [=! </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='replacement'"><xsl:text> [: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='situation'"><xsl:text> [%sit: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:when test="@type='spelling'"><xsl:text> [%spe: </xsl:text><xsl:value-of select="text()"/><xsl:text>]</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        annotation &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="u/a">
  <xsl:choose>
    <xsl:when test="@type='actions'"><xsl:text>%act:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='addressee'"><xsl:text>%add:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='alternative'"><xsl:text>%alt:&#9;</xsl:text></xsl:when>
    <!-- fmc %co1 etc? -->
    <xsl:when test="@type='extension'">
      <xsl:choose>
        <xsl:when test="@flavor">
          <xsl:choose>
            <xsl:when test="@flavor='cod'">
              <xsl:text>%cod:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='co1'">
              <xsl:text>%co1:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='co2'">
              <xsl:text>%co2:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='co3'">
              <xsl:text>%co3:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='trn'">
              <xsl:text>%trn:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='xch'">
              <xsl:text>%xch:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='pse'">
              <xsl:text>%pse:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='fan'">
              <xsl:text>%fan:&#9;</xsl:text>
            </xsl:when>
            <xsl:when test="@flavor='wor'">
              <xsl:text>%wor:&#9;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:message terminate="yes">
                extension flavor &quot;<xsl:value-of select="@flavor"/>&quot; not recognized by stylesheet.
              </xsl:message>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message terminate="yes">
            extension has no flavor specified.
          </xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="@type='cohesion'"><xsl:text>%coh:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='comments'"><xsl:text>%com:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='SALT'"><xsl:text>%def:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='english translation'"><xsl:text>%eng:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='errcoding'"><xsl:text>%err:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='explanation'"><xsl:text>%exp:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='flow'"><xsl:text>%flo:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='facial'"><xsl:text>%fac:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='target gloss'"><xsl:text>%gls:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='gesture'"><xsl:text>%gpx:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='intonation'"><xsl:text>%int:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='language'"><xsl:text>%lan:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='orthography'"><xsl:text>%ort:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='paralinguistics'"><xsl:text>%par:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='phonetic'"><xsl:text>%pho:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='phonetic old'"><xsl:text>%pht:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='phonomodel'"><xsl:text>%mod:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='situation'"><xsl:text>%sit:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='speech act'"><xsl:text>%spa:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='spelling'"><xsl:text>%spe:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='syntactic structure'"><xsl:text>%syn:&#9;</xsl:text></xsl:when>
    <xsl:when test="@type='time stamp'"><xsl:text>%tim:&#9;</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        annotation &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select="text()"/>
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="k">
  <xsl:choose>
    <xsl:when test="@type='stressing'"><xsl:text> [!]</xsl:text></xsl:when>
    <xsl:when test="@type='contrastive stressing'"><xsl:text> [!!]</xsl:text></xsl:when>
    <xsl:when test="@type='quotation'"><xsl:text> ["]</xsl:text></xsl:when>
    <xsl:when test="@type='best guess'"><xsl:text> [?]</xsl:text></xsl:when>
    <xsl:when test="@type='retracing unclear'"><xsl:text> [/?]</xsl:text></xsl:when>
    <xsl:when test="@type='false start'"><xsl:text> [/-]</xsl:text></xsl:when>
    <xsl:when test="@type='retracing'"><xsl:text> [/]</xsl:text></xsl:when>
    <xsl:when test="@type='retracing with correction'"><xsl:text> [//]</xsl:text></xsl:when>
    <xsl:when test="@type='retracing reformulation'"><xsl:text> [///]</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        markers &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!--fmc completion problem? not if rule out -->
<xsl:template match="f">
  <xsl:choose>
    <xsl:when test="@type='approximation'"><xsl:value-of select="text()"/><xsl:text>@ap</xsl:text></xsl:when>
    <xsl:when test="@type='babbling'"><xsl:value-of select="text()"/><xsl:text>@b</xsl:text></xsl:when>
    <xsl:when test="@type='child-invented'"><xsl:value-of select="text()"/><xsl:text>@c</xsl:text></xsl:when>
    <xsl:when test="@type='cue'"><xsl:value-of select="text()"/><xsl:text>@cue</xsl:text></xsl:when>
    <xsl:when test="@type='dialect'"><xsl:value-of select="text()"/><xsl:text>@d</xsl:text></xsl:when>
    <xsl:when test="@type='family-specific'"><xsl:value-of select="text()"/><xsl:text>@f</xsl:text></xsl:when>
    <xsl:when test="@type='filled pause'"><xsl:value-of select="text()"/><xsl:text>@fp</xsl:text></xsl:when>
    <xsl:when test="@type='generic'"><xsl:value-of select="text()"/><xsl:text>@g</xsl:text></xsl:when>
    <xsl:when test="@type='interjection'"><xsl:value-of select="text()"/><xsl:text>@i</xsl:text></xsl:when>
    <xsl:when test="@type='informal sign'"><xsl:value-of select="text()"/><xsl:text>@inf</xsl:text></xsl:when>
    <xsl:when test="@type='informal sign with speech'"><xsl:value-of select="text()"/><xsl:text>@ins</xsl:text></xsl:when>
    <xsl:when test="@type='letter'"><xsl:value-of select="text()"/><xsl:text>@l</xsl:text></xsl:when>
    <xsl:when test="@type='neologism'"><xsl:value-of select="text()"/><xsl:text>@n</xsl:text></xsl:when>
    <xsl:when test="@type='no voice'"><xsl:value-of select="text()"/><xsl:text>@nv</xsl:text></xsl:when>
    <xsl:when test="@type='onomatopoeia'"><xsl:value-of select="text()"/><xsl:text>@o</xsl:text></xsl:when>
    <xsl:when test="@type='phonology consistent'"><xsl:value-of select="text()"/><xsl:text>@p</xsl:text></xsl:when>
    <xsl:when test="@type='phrasal repetition'"><xsl:value-of select="text()"/><xsl:text>@pr</xsl:text></xsl:when>
    <xsl:when test="@type='second language'"><xsl:value-of select="text()"/><xsl:text>@s</xsl:text></xsl:when>
    <xsl:when test="@type='schwa'"><xsl:value-of select="text()"/><xsl:text>@sc</xsl:text></xsl:when>
    <xsl:when test="@type='signed language'"><xsl:value-of select="text()"/><xsl:text>@sl</xsl:text></xsl:when>
    <xsl:when test="@type='sign speech'"><xsl:value-of select="text()"/><xsl:text>@sas</xsl:text></xsl:when>
    <xsl:when test="@type='schwa'"><xsl:value-of select="text()"/><xsl:text>@sc</xsl:text></xsl:when>
    <xsl:when test="@type='singing'"><xsl:value-of select="text()"/><xsl:text>@si</xsl:text></xsl:when>
    <xsl:when test="@type='test'"><xsl:value-of select="text()"/><xsl:text>@t</xsl:text></xsl:when>
    <xsl:when test="@type='UNIBET'"><xsl:value-of select="text()"/><xsl:text>@u</xsl:text></xsl:when>
    <xsl:when test="@type='words to be excluded'"><xsl:value-of select="text()"/><xsl:text>@x</xsl:text></xsl:when>
    <!--fmc redundant @w -->
    <xsl:when test="@type='word play'"><xsl:value-of select="text()"/><xsl:text>@wp</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        form &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="shortening">
  <xsl:text>(</xsl:text>
  <xsl:value-of select="text()"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="e">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="@type='unintelligible-word'"><xsl:text>xx</xsl:text></xsl:when>
    <xsl:when test="@type='unintelligible-word-with-pho'"><xsl:text>yy</xsl:text></xsl:when>
    <xsl:when test="@type='unintelligible'"><xsl:text>xxx</xsl:text></xsl:when>
    <xsl:when test="@type='unintelligible-with-pho'"><xsl:text>yyy</xsl:text></xsl:when>
    <xsl:when test="@type='untranscribed'"><xsl:text>www</xsl:text></xsl:when>
    <xsl:when test="@type='action'">
      <xsl:text>0</xsl:text>
      <xsl:if test="child::*">
        <xsl:text> </xsl:text>
      </xsl:if>
    </xsl:when>

    <!-- Stuff with content -->
    <xsl:when test="@type='omission'"><xsl:text>0</xsl:text></xsl:when>
    <xsl:when test="@type='incorrect omission'"><xsl:text>0*</xsl:text></xsl:when>
    <xsl:when test="@type='ellipsis'"><xsl:text>00</xsl:text></xsl:when>
    <xsl:when test="@type='fragment'"><xsl:text>&amp;</xsl:text></xsl:when>
    <xsl:when test="@type='happening'"><xsl:text>&amp;=</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        event &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="pho">
  <xsl:value-of select="text()"/>
</xsl:template>


<xsl:template match="p">
  <xsl:choose>
    <xsl:when test="@type='stress'"><xsl:value-of select="text()"/><xsl:text>/</xsl:text></xsl:when>
    <xsl:when test="@type='accented nucleus'"><xsl:value-of select="text()"/><xsl:text>//</xsl:text></xsl:when>
    <xsl:when test="@type='contrastive stress'"><xsl:value-of select="text()"/><xsl:text>///</xsl:text></xsl:when>
    <xsl:when test="@type='drawl'"><xsl:value-of select="text()"/><xsl:text>:</xsl:text></xsl:when>
    <xsl:when test="@type='pause'"><xsl:value-of select="text()"/><xsl:text>::</xsl:text></xsl:when>
    <xsl:when test="@type='blocking'"><xsl:value-of select="text()"/><xsl:text>^</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        prosody &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Brian changed from (*3) to [x 3] -->
<xsl:template match="r">
  <xsl:text> [x </xsl:text>
  <xsl:value-of select="@times"/>
  <xsl:text>]</xsl:text>
</xsl:template>


<xsl:template match="mod">
  <xsl:value-of select="text()"/>
</xsl:template>


<!--
     Originally, did not output a space for p, e, q, but
     we decided to always output a space
-->
<xsl:template match="t">
  <!-- Note always output a space -->
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="@type='p'"><xsl:text>.</xsl:text></xsl:when>
    <xsl:when test="@type='e'"><xsl:text>!</xsl:text></xsl:when>
    <xsl:when test="@type='q'"><xsl:text>?</xsl:text></xsl:when>

    <!-- special utterance terminators -->
    <xsl:when test="@type='interruption'"><xsl:text>+/.</xsl:text></xsl:when>
    <xsl:when test="@type='interruption question'"><xsl:text>+/?</xsl:text></xsl:when>
    <xsl:when test="@type='question exclamation'"><xsl:text>+!?</xsl:text></xsl:when>
    <xsl:when test="@type='quotation next line'"><xsl:text>+&quot;/.</xsl:text></xsl:when>
    <xsl:when test="@type='quotation precedes'"><xsl:text>+&quot;.</xsl:text></xsl:when>
    <xsl:when test="@type='self interruption'"><xsl:text>+//.</xsl:text></xsl:when>
    <xsl:when test="@type='self interruption question'"><xsl:text>+//?</xsl:text></xsl:when>
    <xsl:when test="@type='trail off'"><xsl:text>+...</xsl:text></xsl:when>
    <xsl:when test="@type='trail off question'"><xsl:text>+..?</xsl:text></xsl:when>

    <!-- tone -->
    <xsl:when test="@type='rising final'"><xsl:text>-?</xsl:text></xsl:when>
    <xsl:when test="@type='falling final'"><xsl:text>-.</xsl:text></xsl:when>
    <xsl:when test="@type='final exclamation'"><xsl:text>-!</xsl:text></xsl:when>
    <xsl:when test="@type='rise fall'"><xsl:text>-'.</xsl:text></xsl:when>
    <xsl:when test="@type='fall rise'"><xsl:text>-,.</xsl:text></xsl:when>

    <xsl:otherwise>
      <xsl:message terminate="yes">
        terminator &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Always precede with space because scoped to something preceding -->
<xsl:template match="tone">
  <xsl:text> </xsl:text>

  <xsl:choose>
    <xsl:when test="@type='level nonfinal'"><xsl:text>-,</xsl:text></xsl:when>
    <xsl:when test="@type='falling nonfinal'"><xsl:text>-_</xsl:text></xsl:when>
    <xsl:when test="@type='rising nonfinal'"><xsl:text>-'</xsl:text></xsl:when>
    <xsl:when test="@type='low level'"><xsl:text>-</xsl:text></xsl:when>
    <xsl:when test="@type='tag question'"><xsl:text>,,</xsl:text></xsl:when>
    <!--fmc
    <xsl:when test="@type='syntactic juncture'"><xsl:text>,</xsl:text></xsl:when>
-->
    <xsl:when test="@type='word pause'"><xsl:text>#</xsl:text></xsl:when>
    <xsl:when test="@type='lengthened'"><xsl:text>-:</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        tone &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="linker">
  <xsl:choose>
    <xsl:when test="@type='quoted utterance next'"><xsl:text>+"</xsl:text></xsl:when>
    <xsl:when test="@type='quick uptake'"><xsl:text>+^</xsl:text></xsl:when>
    <xsl:when test="@type='lazy overlap mark'"><xsl:text>+&lt;</xsl:text></xsl:when>
    <xsl:when test="@type='self completion'"><xsl:text>+,</xsl:text></xsl:when>
    <xsl:when test="@type='other completion'"><xsl:text>++</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        linker &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!--
     Output the utterance's terminator and postcodes (fmc)
-->
<xsl:template match="mor">
  <xsl:apply-templates/>
  <xsl:apply-templates select="../t"/>
  <!-- Brian said to rid
  <xsl:apply-templates select="../postcode"/>
-->
  <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="mwa">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="mwn">
  <xsl:if test="preceding-sibling::*">
    <xsl:choose>
      <xsl:when test="name(..) = 'mwa'">
        <xsl:text>^</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="mw">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="pos">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="pos/err">
  <xsl:text>*</xsl:text>
</xsl:template>

<xsl:template match="pos/c">
  <xsl:value-of select="text()"/>
</xsl:template>

<xsl:template match="pos/s">
  <xsl:text>:</xsl:text>
  <xsl:value-of select="text()"/>
</xsl:template>

<xsl:template match="stem">
  <xsl:text>|</xsl:text>
  <xsl:value-of select="text()"/>
</xsl:template>

<xsl:template match="mk">
  <xsl:choose>
    <xsl:when test="@type='pfx'">
      <xsl:text>#</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='sfx'">
      <xsl:text>-</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='sfxf'">
      <xsl:text>&amp;</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='oafx'">
      <xsl:text>-0</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='ioafx'">
      <xsl:text>-0*</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='mc'">
      <xsl:text>:</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:when test="@type='enx'">
      <xsl:text>=</xsl:text>
      <xsl:value-of select="text()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        morphemic_marker &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- For both main line and %mor -->
<xsl:template match="wk">
  <xsl:choose>
    <xsl:when test="@type='cmp'"><xsl:text>+</xsl:text></xsl:when>
    <xsl:when test="@type='cli'"><xsl:text>~</xsl:text></xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        wordnet_marker &quot;<xsl:value-of select="@type"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="pause">
  <xsl:if test="preceding-sibling::*">
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="@symbolic-length='simple'">
      <xsl:text>#</xsl:text>
    </xsl:when>
    <xsl:when test="@symbolic-length='long'">
      <xsl:text>##</xsl:text>
    </xsl:when>
    <xsl:when test="@symbolic-length='very long'">
      <xsl:text>###</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        pause symbol &quot;<xsl:value-of select="@symbolic-length"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="@fluency='fluent'">
      <!-- no suffix -->
    </xsl:when>
    <xsl:when test="@fluency='disfluent'">
      <xsl:text>d</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        pause fluency &quot;<xsl:value-of select="@fluency"/>&quot; not recognized by stylesheet.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="@length">
    <xsl:call-template name="format-pause-length">
      <xsl:with-param name="length" select="@length"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:apply-templates select="a"/>
</xsl:template>

<!-- regenerate 2:45_13 from 65.13 -->
<xsl:template name="format-pause-length">
  <xsl:param name="length"/>
  <!-- Assume decimal point representation -->
  <xsl:variable name="partSeconds" select="substring-after($length, '.')"/>
  <xsl:variable name="totalSeconds" select="floor($length)"/>
  <xsl:variable name="minutes" select="floor($totalSeconds div 60)"/>
  <xsl:variable name="seconds" select="$totalSeconds - 60 * $minutes"/>
  <xsl:choose>
    <xsl:when test="$minutes &gt; 0">
      <xsl:value-of select="$minutes"/>
      <xsl:text>:</xsl:text>
      <xsl:value-of select="format-number($seconds, '00')"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="format-number($seconds, '#0')"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="$partSeconds">
    <xsl:text>_</xsl:text>
    <xsl:value-of select="$partSeconds"/>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
