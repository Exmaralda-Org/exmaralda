package eu.clarin.weblicht.bindings.oai;

import java.net.URI;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlRootElement(name = "OAI-PMH")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"responseDate", "request", "error", "listRecords"})
public class OAIPMH {

    @XmlElement
    private Calendar responseDate;
    @XmlElement
    private Request request;
    @XmlElement
    private String error;
    @XmlElement(name = "ListRecords")
    private ListRecords listRecords;

    private OAIPMH() {
    }

    public OAIPMH(List<Record> records, URI url) {
        this.responseDate = new GregorianCalendar();
        this.request = new Request("ListRecords", "cmdi", url);
        this.listRecords = new ListRecords(records);
    }

    public OAIPMH(List<Record> records, URI url, String resumptionToken) {
        this.responseDate = new GregorianCalendar();
        this.request = new Request("ListRecords", "cmdi", url);
        this.listRecords = new ListRecords(records, resumptionToken);
    }

    public ListRecords getListRecords() {
        return listRecords;
    }

    public String getError() {
        return error;
    }

    public Request getRequest() {
        return request;
    }

    public Calendar getResponseDate() {
        return responseDate;
    }

    @Override
    public String toString() {
        return "OAI-PMH{" + "responseDate=" + responseDate + ", request=" + request + ", listRecords=" + listRecords + '}';
    }
}
