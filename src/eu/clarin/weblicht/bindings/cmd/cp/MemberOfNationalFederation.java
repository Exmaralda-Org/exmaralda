package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-MemberOfNationalFederation-8-clarin.eu.cr1.c_1320657629669", propOrder = {
    "value"
})
public class MemberOfNationalFederation {

    @XmlValue
    private SimpleMemberOfNationalFederation value;

    public SimpleMemberOfNationalFederation getValue() {
        return value;
    }
}
