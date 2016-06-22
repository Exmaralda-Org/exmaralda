package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractParameter<V> extends AbstractParameterGroup implements Iterable<V> {

    @XmlElement
    private Boolean isConfigurationParameter;

    protected AbstractParameter() {
    }

    protected AbstractParameter(StringBinding name) {
        super(name);
    }

    public Boolean isConfigurationParameter() {
        return isConfigurationParameter;
    }

    protected abstract AbstractValues<V> getValues();

    public boolean hasValues() {
        AbstractValues<V> values = getValues();
        return values != null && !values.isEmpty();
    }

    public Iterator<V> iterator() {
        AbstractValues<V> values = getValues();
        if (values == null) {
            return Collections.<V>emptyList().iterator();
        }
        return values.iterator();
    }
}
