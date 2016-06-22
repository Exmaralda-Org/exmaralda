package eu.clarin.weblicht.bindings.oai;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {

    @XmlAttribute
    private String verb;
    @XmlAttribute
    private String metadataPrefix;
    @XmlValue
    private URI url;

    protected Request() {
    }

    public Request(String verb, String metadataPrefix, URI url) {
        this.verb = verb;
        this.metadataPrefix = metadataPrefix;
        this.url = url;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public URI getUrl() {
        return url;
    }

    public String getVerb() {
        return verb;
    }

    @Override
    public String toString() {
        return "Request{" + "verb=" + verb + ", metadataPrefix=" + metadataPrefix + ", url=" + url + '}';
    }
}
