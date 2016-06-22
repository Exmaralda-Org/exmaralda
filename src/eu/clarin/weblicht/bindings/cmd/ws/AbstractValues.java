package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author akislev
 */
public abstract class AbstractValues<V> extends AbstractRefBinding implements Iterable<V> {

    protected abstract List<V> getValues();

    public boolean isEmpty() {
        List<V> values = getValues();
        return values == null || values.isEmpty();
    }

    public Iterator<V> iterator() {
        List<V> values = getValues();
        if (values == null) {
            return Collections.<V>emptyList().iterator();
        }
        return values.iterator();
    }
}
