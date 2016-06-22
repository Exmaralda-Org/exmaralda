package eu.clarin.weblicht.bindings.oai;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"records", "resumptionToken"})
public class ListRecords {

    @XmlElement(name = "record")
    private List<Record> records;
    @XmlElement
    private ResumptionToken resumptionToken;

    private ListRecords() {
    }

    public ListRecords(List<Record> records) {
        this.records = records;
    }
    
    public ListRecords(List<Record> records, String resumptionToken) {
        this.records = records;
        this.resumptionToken = new ResumptionToken(resumptionToken);
    }

    public List<Record> getRecords() {
        return records;
    }

    public ResumptionToken getResumptionToken() {
        return resumptionToken;
    }
}
