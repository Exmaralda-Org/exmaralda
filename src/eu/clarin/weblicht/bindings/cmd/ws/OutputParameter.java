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
public class OutputParameter extends AbstractParameter<OutputValue> {

    @XmlElement(name = "RefInputParameter")
    private StringBinding refInputParameter;
    @XmlElement(name = "Values")
    private OutputValues values;

    private OutputParameter() {
    }

    OutputParameter(StringBinding name, StringBinding refInputParameter) {
        super(name);
        this.refInputParameter = refInputParameter;
    }

    public StringBinding getRefInputParameter() {
        return refInputParameter;
    }

    @Override
    protected OutputValues getValues() {
        return values;
    }

    public boolean add(OutputValue value) {
        if (values == null) {
            values = new OutputValues();
        }
        return values.add(value);
    }

    @Override
    public String toString() {
        if (values != null) {
            return this.getName() + ":" + values.getValues();
        } else {
            return this.getName().toString();
        }
    }

    @Override
    public OutputParameter copy() {
        OutputParameter parameter = (OutputParameter) super.copy();
        parameter.values = copy(values);
        return parameter;
    }
}
