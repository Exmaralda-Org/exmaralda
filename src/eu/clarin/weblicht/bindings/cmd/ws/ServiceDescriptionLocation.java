package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ServiceDescriptionLocation extends AbstractRefBinding {

    private ServiceDescriptionLocation() {
    }

    ServiceDescriptionLocation(List<Object> ref) {
        super(ref);
    }
}
