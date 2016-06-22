package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-MetadataScheme-9-clarin.eu.cr1.c_1320657629669", propOrder = {
    "value"
})
public class MetadataScheme {

    @XmlValue
    private SimpleMetadataScheme value;

    public SimpleMetadataScheme getValue() {
        return value;
    }
}
