package eu.clarin.weblicht.bindings.cmd.chains;

import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlRootElement(name = "CLARIN-D")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ServiceChains {

    @XmlElementWrapper(name = "chains")
    @XmlElement(name = "CMD")
    private List<ToolchainCMD> bindings;

    private ServiceChains() {
    }

    public ServiceChains(List<ToolchainCMD> bindings) {
        this.bindings = bindings;
    }

    public List<ToolchainCMD> getBindings() {
        return bindings;
    }
}
