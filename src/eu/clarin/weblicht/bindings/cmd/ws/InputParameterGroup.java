package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class InputParameterGroup extends AbstractParameterGroup {

    @XmlElement(name = "Parameters", required = true)
    private InputParameters parameters;

    private InputParameterGroup() {
    }

    InputParameterGroup(StringBinding name) {
        super(name);
    }

    public InputParameters getParameters() {
        if (parameters == null) {
            parameters = new InputParameters();
        }
        return parameters;
    }

    @Override
    public InputParameterGroup copy() {
        InputParameterGroup parameterGroup = (InputParameterGroup) super.copy();
        parameterGroup.parameters = copy(parameters);
        return parameterGroup;
    }
}
