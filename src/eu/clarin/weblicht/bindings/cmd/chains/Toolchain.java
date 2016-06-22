package eu.clarin.weblicht.bindings.cmd.chains;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"toolsInChain"})
public class Toolchain extends AbstractComponent {

    @XmlElement(name = "ToolInChain", required = true)
    private List<ToolInChain> toolsInChain;

    private Toolchain() {
    }

    public Toolchain(List<ToolInChain> toolsInChain) {
        this.toolsInChain = toolsInChain;
    }

    public List<ToolInChain> getToolsInChain() {
        return toolsInChain;
    }
}
