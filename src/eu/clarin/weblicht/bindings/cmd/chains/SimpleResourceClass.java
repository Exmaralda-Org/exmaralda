package eu.clarin.weblicht.bindings.cmd.chains;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlType(name = "simpletype-ResourceClass-clarin.eu.cr1.c_1290431694495")
@XmlEnum
public enum SimpleResourceClass {

    @XmlEnumValue("Lexicon")
    LEXICON("Lexicon"),
    @XmlEnumValue("Corpus")
    CORPUS("Corpus"),
    @XmlEnumValue("Tool")
    TOOL("Tool"),
    @XmlEnumValue("Grammar")
    GRAMMAR("Grammar"),
    @XmlEnumValue("Fieldwork Material")
    FIELDWORK_MATERIAL("Fieldwork Material"),
    @XmlEnumValue("Experimental Data")
    EXPERIMENTAL_DATA("Experimental Data"),
    @XmlEnumValue("Survey Data")
    SURVEY_DATA("Survey Data"),
    @XmlEnumValue("Test Data")
    TEST_DATA("Test Data"),
    @XmlEnumValue("Toolchain")
    TOOLCHAIN("Toolchain"),
    @XmlEnumValue("ResourceBundle")
    RESOURCE_BUNDLE("ResourceBundle");
    private final String value;

    private SimpleResourceClass(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleResourceClass fromValue(String v) {
        for (SimpleResourceClass c : SimpleResourceClass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
