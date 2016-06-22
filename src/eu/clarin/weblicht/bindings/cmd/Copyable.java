package eu.clarin.weblicht.bindings.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base class for deep copying
 *
 * @author akislev
 */
public class Copyable implements Cloneable {

    @Override
    protected Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException("use copy() method to get a deep copy of this object");
    }
    
    public Copyable copy() {
        try {
            return (Copyable) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Copyable> E copy(E copyable) {
        if (copyable == null) {
            return null;
        } else {
            return (E) copyable.copy();
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Copyable> List<E> copy(final List<E> list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return Collections.emptyList();
        } else if (list.size() == 1) {
            return Collections.<E>singletonList((E) list.get(0).copy());
        } else {
            return new ArrayList<E>(list.size()) {
                {
                    for (Copyable c : list) {
                        add((E) c.copy());
                    }
                }
            };
        }
    }

    public static <E> List<E> shallowCopy(final List<E> list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return Collections.emptyList();
        } else if (list.size() == 1) {
            return Collections.<E>singletonList(list.get(0));
        } else {
            return new ArrayList<E>(list);
        }
    }
}
