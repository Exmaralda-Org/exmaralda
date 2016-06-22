package eu.clarin.weblicht.bindings.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ResumptionToken {

    @XmlAttribute
    private String completeListSize;
    @XmlAttribute
    private String cursor;
    @XmlValue
    private String value;

    private ResumptionToken() {
    }

    public ResumptionToken(String resumptionToken) {
        this.value = resumptionToken;
    }

    public String getCompleteListSize() {
        return completeListSize;
    }

    public String getCursor() {
        return cursor;
    }

    public String getValue() {
        return value;
    }
}
