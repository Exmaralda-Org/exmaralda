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
public class OutputParameters extends AbstractRefBinding implements Iterable<OutputParameter> {

    @XmlElement(name = "Parameter", required = true)
    private List<OutputParameter> parameters;

    OutputParameters() {
    }

    public Iterator<OutputParameter> iterator() {
        if (parameters == null) {
            return Collections.<OutputParameter>emptyList().iterator();
        }
        return parameters.iterator();
    }

    public void clear() {
        parameters = null;
    }

    public boolean add(OutputParameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<OutputParameter>();

        }
        return parameters.add(parameter);
    }

    @Override
    public OutputParameters copy() {
        OutputParameters params = (OutputParameters) super.copy();
        params.parameters = copy(parameters);
        return params;
    }
}
