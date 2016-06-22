package eu.clarin.weblicht.bindings.cmd.chains;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"webServiceToolChain"})
public class Components {

    @XmlElement(name = "WebServiceToolChain", required = true)
    private WebServiceToolChain webServiceToolChain;

    private Components() {
    }

    public Components(WebServiceToolChain webServiceToolChain) {
        this.webServiceToolChain = webServiceToolChain;
    }

    public WebServiceToolChain getWebServiceToolChain() {
        return webServiceToolChain;
    }
}
