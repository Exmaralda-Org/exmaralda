package eu.clarin.weblicht.bindings.cmd.ws;

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
public class DerivationToolInfo extends AbstractToolInfo {

    @XmlElement(name = "DerivationTool")
    protected List<StringBinding> derivationTools;

    public List<StringBinding> getDerivationTools() {
        return derivationTools;
    }

    @Override
    public DerivationToolInfo copy() {
        DerivationToolInfo derivationToolInfo = (DerivationToolInfo) super.copy();
        derivationToolInfo.derivationTools = shallowCopy(derivationTools);
        return derivationToolInfo;
    }
}
