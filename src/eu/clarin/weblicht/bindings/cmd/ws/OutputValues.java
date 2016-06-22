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
public class OutputValues extends AbstractValues<OutputValue> {

    @XmlElement(name = "ParameterValue", required = true)
    private List<OutputValue> values;

    OutputValues() {
    }

    @Override
    protected List<OutputValue> getValues() {
        return values;
    }

    public boolean add(OutputValue value) {
        if (values == null) {
            values = new ArrayList<OutputValue>();
        }
        return values.add(value);
    }

    @Override
    public OutputValues copy() {
        OutputValues v = (OutputValues) super.copy();
        v.values = copy(values);
        return v;
    }
}
