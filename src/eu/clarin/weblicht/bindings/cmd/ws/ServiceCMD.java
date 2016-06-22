package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractCMD;
import eu.clarin.weblicht.bindings.cmd.Header;
import eu.clarin.weblicht.bindings.cmd.ResourceProxy;
import eu.clarin.weblicht.bindings.cmd.ResourceType;
import eu.clarin.weblicht.bindings.cmd.Resources;
import eu.clarin.weblicht.bindings.cmd.SimpleResourceType;
import java.net.URI;
import java.util.Collections;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "CMD")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceCMD extends AbstractCMD {

    private static final String CMD_VERSION = "1.1";
    private static final String WADL_MIMETYPE = "application/vnd.sun.wadl+xml";
    private static final String PROXY_ID = "s001";
    @XmlElement(name = "Components", required = true)
    private Components components;

    private ServiceCMD() {
    }

    ServiceCMD(URI wadlLocation) {
        cmdVersion = CMD_VERSION;
        header = new Header(null);
        ResourceProxy wadlResource = new ResourceProxy(new ResourceType(SimpleResourceType.RESOURCE, WADL_MIMETYPE), wadlLocation, PROXY_ID);
        resources = new Resources(Collections.singletonList(wadlResource));
        components = new Components(wadlResource);
    }

    @Override
    public Resources getResources() {

        return resources;
    }

    public Components getComponents() {

        return components;
    }

    public Service extractService() {
        return components != null && components.getWebLichtWebService() != null ? components.getWebLichtWebService().getService() : null;
    }
    
    public void setServiceDescriptionLocationId(String id){
        ResourceProxy resourceProxy = resources.getFirstResourceProxy();
        resourceProxy.setId(id);
        components.getWebLichtWebService().getService().setServiceDescriptionLocation(resourceProxy);
    }

    @Override
    public ServiceCMD copy() {
        ServiceCMD service = (ServiceCMD) super.copy();
        ResourceProxy wadlResource = service.resources.getFirstResourceProxy();
        service.components = copy(components);
        components.getWebLichtWebService().getService().getServiceDescriptionLocation().setRef(wadlResource);
        return service;
    }
}
