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
public class OutputParameterGroup extends AbstractParameterGroup {

    @XmlElement(name = "ReplacesInput", required = true)
    private Boolean replacesInput;
    @XmlElement(name = "Parameters", required = true)
    private OutputParameters parameters;

    private OutputParameterGroup() {
    }

    OutputParameterGroup(StringBinding name) {
        super(name);
        this.parameters = new OutputParameters();
    }

    public Boolean getReplacesInput() {
        if (replacesInput == null) {
            replacesInput = false;
        }
        return replacesInput;
    }

    public void setReplacesInput(boolean replacesInput) {
        this.replacesInput = replacesInput;
    }

    public OutputParameters getParameters() {
        if (parameters == null) {
            parameters = new OutputParameters();
        }
        return parameters;
    }

    @Override
    public OutputParameterGroup copy() {
        OutputParameterGroup parameterGroup = (OutputParameterGroup) super.copy();
        parameterGroup.parameters = copy(parameters);
        return parameterGroup;
    }
}
