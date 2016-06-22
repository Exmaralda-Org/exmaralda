package eu.clarin.weblicht.bindings.cmd.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class InputValues extends AbstractValues<InputValue> {

    @XmlElement(name = "ParameterValue", required = true)
    private List<InputValue> values;

    InputValues() {
    }

    @Override
    protected List<InputValue> getValues() {
        return values;
    }

    public boolean add(InputValue value) {
        if (values == null) {
            values = new ArrayList<InputValue>();
        }
        return values.add(value);
    }

    @Override
    public InputValues copy() {
        InputValues v = (InputValues) super.copy();
        v.values = copy(values);
        return v;
    }
}
