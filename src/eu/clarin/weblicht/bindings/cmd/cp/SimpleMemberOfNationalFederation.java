package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-MemberOfNationalFederation-8-clarin.eu.cr1.c_1320657629669")
@XmlEnum
public enum SimpleMemberOfNationalFederation {

    NL,
    DE,
    FI;

    public String value() {
        return name();
    }

    public static SimpleMemberOfNationalFederation fromValue(String v) {
        return valueOf(v);
    }
}
