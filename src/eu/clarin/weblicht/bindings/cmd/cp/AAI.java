package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"aaiStatus", "memberOfNationalFederation", "memberOfSpf"})
public class AAI extends AbstractRefBinding {

    @XmlElement(name = "AaiStatus", required = true)
    private String aaiStatus;
    @XmlElement(name = "MemberOfNationalFederation")
    private List<MemberOfNationalFederation> memberOfNationalFederation;
    @XmlElement(name = "MemberOfSpf")
    private boolean memberOfSpf;

    public String getAaiStatus() {
        return aaiStatus;
    }

    public List<MemberOfNationalFederation> getMemberOfNationalFederation() {
        return this.memberOfNationalFederation;
    }

    public boolean isMemberOfSpf() {
        return memberOfSpf;
    }
}
