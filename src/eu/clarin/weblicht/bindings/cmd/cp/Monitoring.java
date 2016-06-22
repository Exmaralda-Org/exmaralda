package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import java.net.URI;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"spTestSite"})
public class Monitoring extends AbstractRefBinding {

    @XmlElement(name = "SpTestSite", required = true)
    @XmlSchemaType(name = "anyURI")
    private URI spTestSite;

    public URI getSpTestSite() {
        return spTestSite;
    }
}
