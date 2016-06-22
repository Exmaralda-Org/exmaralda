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
public class InputParameter extends AbstractParameter<InputValue> {

    @XmlElement(name = "WebServiceArgValue")
    private StringBinding webServiceArgValue;
    @XmlElement(name = "AllowManualSelectionFallback")
    private Boolean allowManualSelectionFallback;
    @XmlElement(name = "Values")
    private InputValues values;

    private InputParameter() {
    }

    InputParameter(StringBinding name, StringBinding webServiceArgValue, boolean allowManualSelectionFallback) {
        super(name);
        this.webServiceArgValue = webServiceArgValue;
        this.allowManualSelectionFallback = allowManualSelectionFallback;
    }

    public StringBinding getWebServiceArgValue() {
        return webServiceArgValue;
    }

    public Boolean getAllowManualSelectionFallback() {
        return allowManualSelectionFallback;
    }

    @Override
    protected InputValues getValues() {
        return values;
    }

    public boolean add(InputValue value) {
        if (values == null) {
            values = new InputValues();
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
    public InputParameter copy() {
        InputParameter parameter = (InputParameter) super.copy();
        parameter.values = copy(values);
        return parameter;
    }
}
