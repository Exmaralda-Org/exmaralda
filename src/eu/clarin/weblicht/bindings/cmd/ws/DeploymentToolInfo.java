package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DeploymentToolInfo extends AbstractToolInfo {

    @XmlElement(name = "DeploymentTool")
    private List<StringBinding> deploymentTools;

    private DeploymentToolInfo() {
    }

    public List<StringBinding> getDeploymentTools() {
        return this.deploymentTools;
    }

    @Override
    public DeploymentToolInfo copy() {
        DeploymentToolInfo deploymentToolInfo = (DeploymentToolInfo) super.copy();
        deploymentToolInfo.deploymentTools = shallowCopy(deploymentTools);
        return deploymentToolInfo;
    }
}
