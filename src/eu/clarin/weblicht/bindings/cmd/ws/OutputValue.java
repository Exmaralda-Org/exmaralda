package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OutputValue extends AbstractValue {

    @XmlElement(name = "RefInputParameterValue")
    private List<StringBinding> refInputParameterValues;

    private OutputValue() {
    }

    OutputValue(StringBinding name, List<StringBinding> refInputParameterValues) {
        super(name);
        this.refInputParameterValues = refInputParameterValues;
    }

    public List<StringBinding> getRefInputParameterValues() {
        return refInputParameterValues;
    }

    @Override
    public String toString() {
        if (refInputParameterValues != null) {
            return getValue() + "(ref=" + refInputParameterValues + ")";
        } else {
            return getValue().toString();
        }
    }

    @Override
    public OutputValue copy() {
        OutputValue value = (OutputValue) super.copy();
        value.refInputParameterValues = shallowCopy(refInputParameterValues);
        return value;
    }
}
