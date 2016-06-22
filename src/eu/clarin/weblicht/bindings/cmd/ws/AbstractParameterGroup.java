package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractParameterGroup extends AbstractNamedComponent {

    @XmlElement(name = "MIMEType")
    private StringBinding mimeType;
    @XmlElement(name = "DataType")
    private StringBinding dataType;
    @XmlElement(name = "DataCategory")
    @XmlSchemaType(name = "anyURI")
    private String dataCategory;
    @XmlElement(name = "SemanticType")
    private StringBinding semanticType;

    protected AbstractParameterGroup() {
    }

    protected AbstractParameterGroup(StringBinding name) {
        super(name);
    }

    public StringBinding getMIMEType() {
        return mimeType;
    }

    public StringBinding getDataType() {
        return dataType;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public StringBinding getSemanticType() {
        return semanticType;
    }
}
