package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Operation extends AbstractNamedComponent {

    @XmlElement(name = "Input", required = true)
    private Input input;
    @XmlElement(name = "Output", required = true)
    private Output output;

    private Operation() {
    }

    Operation(StringBinding name) {
        super(name);
    }

    public Input getInput() {
        if (input == null) {
            input = new Input();
        }
        return input;
    }

    public Output getOutput() {
        if (output == null) {
            output = new Output();
        }
        return output;
    }

    @Override
    public String toString() {
        return input.toString() + output.toString();
    }

    @Override
    public Operation copy() {
        Operation operation = (Operation) super.copy();
        operation.input = copy(input);
        operation.output = copy(output);
        return operation;
    }
}
