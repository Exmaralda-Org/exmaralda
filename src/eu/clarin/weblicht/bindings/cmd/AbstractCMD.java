package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "header",
    "resources"
})
public abstract class AbstractCMD extends Copyable {

    @XmlAttribute(name = "CMDVersion", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String cmdVersion;
    @XmlElement(name = "Header", required = true)
    protected Header header;
    @XmlElement(name = "Resources", required = true)
    protected Resources resources;

    protected AbstractCMD() {
    }

    public AbstractCMD(String cmdVersion, Header header, Resources resources) {
        this.cmdVersion = cmdVersion;
        this.header = header;
        this.resources = resources;
    }

    public String getCMDVersion() {
        return cmdVersion;
    }

    public Header getHeader() {
        return header;
    }

    public Resources getResources() {
        return resources;
    }

    @Override
    public AbstractCMD copy() {
        AbstractCMD cmd = (AbstractCMD) super.copy();
        cmd.header = copy(header);
        cmd.resources = copy(resources);
        return cmd;
    }
}
