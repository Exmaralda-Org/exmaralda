package eu.clarin.weblicht.bindings.cmd;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"resourceProxyList", "journalFileProxyList", "resourceRelationList", "isPartOfList"})
public class Resources extends Copyable {

    @XmlElementWrapper(name = "ResourceProxyList")
    @XmlElement(name = "ResourceProxy", required = true)
    private List<ResourceProxy> resourceProxyList;
    @XmlElementWrapper(name = "JournalFileProxyList")
    @XmlElement(name = "JournalFileProxy", required = true)
    private List<JournalFileProxy> journalFileProxyList;
    @XmlElementWrapper(name = "ResourceRelationList")
    @XmlElement(name = "ResourceRelation", required = true)
    private List<ResourceRelation> resourceRelationList;
    @XmlElement(name = "IsPartOfList")
    private IsPartOfList isPartOfList;

    private Resources() {
    }

    public Resources(List<ResourceProxy> resourceProxyList) {
        this.resourceProxyList = resourceProxyList;
        this.journalFileProxyList = Collections.<JournalFileProxy>emptyList();
        this.resourceRelationList = Collections.<ResourceRelation>emptyList();
    }

    public List<ResourceProxy> getResourceProxyList() {
        return resourceProxyList;
    }

    public List<JournalFileProxy> getJournalFileProxyList() {
        return journalFileProxyList;
    }

    public List<ResourceRelation> getResourceRelationList() {
        return resourceRelationList;
    }

    public IsPartOfList getIsPartOfList() {
        return isPartOfList;
    }

    public ResourceProxy getFirstResourceProxy() {
        if (resourceProxyList == null || resourceProxyList.isEmpty()) {
            return null;
        } else {
            return resourceProxyList.get(0);
        }
    }

    @Override
    public Resources copy() {
        Resources resources = (Resources) super.copy();
        resources.resourceProxyList = copy(resourceProxyList);
        resources.journalFileProxyList = shallowCopy(journalFileProxyList);
        resources.resourceRelationList = shallowCopy(resourceRelationList);
        return resources;
    }
}
