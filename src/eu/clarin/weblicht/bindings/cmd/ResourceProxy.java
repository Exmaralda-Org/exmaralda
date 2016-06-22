package eu.clarin.weblicht.bindings.cmd;

import java.net.URI;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"resourceType", "resourceRef"})
public class ResourceProxy extends Copyable {

    @XmlElement(name = "ResourceType", required = true)
    private ResourceType resourceType;
    @XmlElement(name = "ResourceRef", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI resourceRef;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(value = CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    private ResourceProxy() {
    }

    public ResourceProxy(ResourceType resourceType, URI resourceRef, String id) {
        this.resourceType = resourceType;
        this.resourceRef = resourceRef;
        this.id = id;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public URI getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(URI resourceRef) {
        this.resourceRef = resourceRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
