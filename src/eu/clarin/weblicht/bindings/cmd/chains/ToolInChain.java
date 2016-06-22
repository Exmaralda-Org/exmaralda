package eu.clarin.weblicht.bindings.cmd.chains;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"pid", "parameters"})
@XmlRootElement(name = "ToolInChain")
public class ToolInChain extends AbstractComponent {

    @XmlElement(name = "PID", required = true)
    private StringBinding pid;
    @XmlElement(name = "Parameter")
    private List<Parameter> parameters;

    private ToolInChain() {
    }

    public ToolInChain(StringBinding pid, List<Parameter> parameters) {
        this.pid = pid;
        this.parameters = parameters;
    }

    public StringBinding getPID() {
        return pid;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }
}
