package eu.clarin.weblicht.bindings.cmd;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractRefBinding extends Copyable {

    @XmlAttribute(name = "ref")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    private List<Object> refs;

    protected AbstractRefBinding() {
    }

    public AbstractRefBinding(List<Object> refs) {
        this.refs = refs;
    }

    public List<Object> getRefs() {
        return refs;
    }

    public void setRef(Object ref) {
        refs = Collections.singletonList(ref);
    }

    @Override
    public AbstractRefBinding copy() {
        AbstractRefBinding ref = (AbstractRefBinding) super.copy();
        ref.refs = null;
        return ref;
    }
}
