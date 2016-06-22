package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-SegmentationUnit-clarin.eu.cr1.c_1290431694523")
public class SegmentationUnit {

    @XmlValue
    private SimpleSegmentationUnit value;

    private SegmentationUnit() {
    }

    public SimpleSegmentationUnit getValue() {
        return value;
    }
}
