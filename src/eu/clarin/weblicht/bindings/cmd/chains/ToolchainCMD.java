package eu.clarin.weblicht.bindings.cmd.chains;

import eu.clarin.weblicht.bindings.cmd.AbstractCMD;
import eu.clarin.weblicht.bindings.cmd.ResourceProxy;
import eu.clarin.weblicht.bindings.cmd.Resources;
import java.util.Collections;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "components"
})
@XmlRootElement(name = "CMD")
public class ToolchainCMD extends AbstractCMD {

    @XmlElement(name = "Components", required = true)
    private Components components;

    private ToolchainCMD() {
    }

    public ToolchainCMD(WebServiceToolChain webServiceToolChain) {
        super("1.1", null, new Resources(Collections.<ResourceProxy>emptyList()));
        this.components = new Components(webServiceToolChain);
    }

    public Components getComponents() {
        return components;
    }
}
