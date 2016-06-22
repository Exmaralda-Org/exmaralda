package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class WebLichtWebService extends AbstractRefBinding {

    @XmlElement(name = "Service", required = true)
    private Service service;

    private WebLichtWebService() {
    }

    WebLichtWebService(Object proxyId) {
        service = new Service(proxyId);
    }

    public Service getService() {
        return service;
    }

    @Override
    public WebLichtWebService copy() {
        WebLichtWebService wlws = (WebLichtWebService) super.copy();
        wlws.service = copy(service);
        return wlws;
    }
}
