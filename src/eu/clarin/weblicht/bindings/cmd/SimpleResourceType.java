//
package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Resourcetype_simple")
@XmlEnum
public enum SimpleResourceType {

    /**
     * The ResourceProxy refers to another component metadata instance (e.g. for
     * grouping metadata descriptions into collections)
     *
     */
    @XmlEnumValue("Metadata")
    METADATA("Metadata"),
    /**
     * The ResourceProxy refers to a file that is not a metadata instance (e.g.
     * a text document)
     *
     */
    @XmlEnumValue("Resource")
    RESOURCE("Resource");
    private final String value;

    private SimpleResourceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleResourceType fromValue(String v) {
        for (SimpleResourceType c : SimpleResourceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
