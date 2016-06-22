package eu.clarin.weblicht.bindings.cmd.chains;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import eu.clarin.weblicht.bindings.cmd.Description;
import eu.clarin.weblicht.bindings.cmd.Descriptions;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"generalInfo", "toolchain"})
public class WebServiceToolChain extends AbstractRefBinding {

    @XmlElement(name = "GeneralInfo", required = true)
    private GeneralInfo generalInfo;
    @XmlElement(name = "Toolchain", required = true)
    private Toolchain toolchain;

    private WebServiceToolChain() {
    }

    public WebServiceToolChain(StringBinding name, Description description, Toolchain toolchain) {
        this.generalInfo = new GeneralInfo(SimpleResourceClass.TOOLCHAIN, name, new Descriptions(description));
        this.toolchain = toolchain;
    }

    public GeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    public Toolchain getToolchain() {
        return toolchain;
    }
}
