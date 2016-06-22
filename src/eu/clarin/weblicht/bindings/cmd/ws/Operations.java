package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Operations extends AbstractRefBinding {

    private static final StringBinding DEFAULT = new StringBinding("Default");
    @XmlElement(name = "Operation", required = true)
    private List<Operation> operations;

    Operations() {
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public Operation getFirstOperation() {
        Operation operation;
        if (operations == null) {
            operation = new Operation(DEFAULT);
            operations = Collections.singletonList(operation);
        } else if (operations.isEmpty()) {
            operation = new Operation(DEFAULT);
            operations.add(operation);
        } else {
            operation = operations.get(0);
        }
        return operation;
    }

    @Override
    public Operations copy() {
        Operations ops = (Operations) super.copy();
        ops.operations = copy(operations);
        return ops;
    }
}
