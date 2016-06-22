package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Output extends AbstractRefBinding {

    private static final StringBinding GROUP_NAME = new StringBinding("Output Parameters");
    @XmlElement(name = "ParameterGroup")
    private List<OutputParameterGroup> parameterGroups;

    Output() {
    }

    public List<OutputParameterGroup> getParameterGroups() {
        return parameterGroups;
    }

    public OutputParameterGroup getFirstParameterGroup() {
        OutputParameterGroup group;
        if (parameterGroups == null || parameterGroups.size() != 1) {
            group = new OutputParameterGroup(GROUP_NAME);
            parameterGroups = Collections.singletonList(group);
        } else {
            group = parameterGroups.get(0);
        }
        return group;
    }

    @Override
    public Output copy() {
        Output output = (Output) super.copy();
        output.parameterGroups = copy(parameterGroups);
        return output;
    }
}
