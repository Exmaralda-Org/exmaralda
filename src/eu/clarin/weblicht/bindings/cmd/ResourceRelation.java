package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"relationType", "res1", "res2"})
public class ResourceRelation {

    @XmlElement(name = "RelationType", required = true)
    private Object relationType;
    @XmlElement(name = "Res1", required = true)
    private ResourceRelation.Res res1;
    @XmlElement(name = "Res2", required = true)
    private ResourceRelation.Res res2;

    private ResourceRelation() {
    }

    public Object getRelationType() {
        return relationType;
    }

    public ResourceRelation.Res getRes1() {
        return res1;
    }

    public ResourceRelation.Res getRes2() {
        return res2;
    }

    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Res extends AbstractRefBinding {
    }
}
