package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class InputParameters extends AbstractRefBinding implements Iterable<InputParameter> {

    @XmlElement(name = "Parameter", required = true)
    private List<InputParameter> parameters;

    InputParameters() {
    }

    public Iterator<InputParameter> iterator() {
        if (parameters == null) {
            return Collections.<InputParameter>emptyList().iterator();
        }
        return parameters.iterator();
    }

    public void clear() {
        parameters = null;
    }

    public boolean add(InputParameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<InputParameter>();

        }
        return parameters.add(parameter);
    }

    @Override
    public InputParameters copy() {
        InputParameters params = (InputParameters) super.copy();
        params.parameters = copy(parameters);
        return params;
    }
}
