package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Position extends AbstractComponent {

    @XmlElement(name = "PositionType", required = true)
    private List<StringBinding> positionTypes;
    @XmlElement(name = "StartPosition", required = true)
    private StringBinding startPosition;
    @XmlElement(name = "EndPosition", required = true)
    private StringBinding endPosition;

    public List<StringBinding> getPositionTypes() {
        return positionTypes;
    }

    public StringBinding getStartPosition() {
        return startPosition;
    }

    public StringBinding getEndPosition() {
        return endPosition;
    }

    @Override
    public Position copy() {
        Position position = (Position) super.copy();
        position.positionTypes = shallowCopy(positionTypes);
        return position;
    }
}
