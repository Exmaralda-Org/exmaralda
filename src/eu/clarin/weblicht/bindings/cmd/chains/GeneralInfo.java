package eu.clarin.weblicht.bindings.cmd.chains;

import eu.clarin.weblicht.bindings.cmd.AbstractDescribedComponent;
import eu.clarin.weblicht.bindings.cmd.Descriptions;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"resourceName", "resourceTitle", "resourceClass", "pid", "version", "lifeCycleStatus", "startYear", "completionYear", "publicationDate", "lastUpdate", "timeCoverage", "legalOwner", "location"})
public class GeneralInfo extends AbstractDescribedComponent {

    @XmlElement(name = "ResourceName")
    private List<StringBinding> resourceName;
    @XmlElement(name = "ResourceTitle")
    private List<StringBinding> resourceTitle;
    @XmlElement(name = "ResourceClass", required = true)
    private List<ResourceClass> resourceClass;
    @XmlElement(name = "PID")
    private StringBinding pid;
    @XmlElement(name = "Version")
    private List<StringBinding> version;
    @XmlElement(name = "LifeCycleStatus")
    private LifeCycleStatus lifeCycleStatus;
    @XmlElement(name = "StartYear")
    @XmlSchemaType(name = "gYear")
    private XMLGregorianCalendar startYear;
    @XmlElement(name = "CompletionYear")
    @XmlSchemaType(name = "gYear")
    private XMLGregorianCalendar completionYear;
    @XmlElement(name = "PublicationDate")
    private StringBinding publicationDate;
    @XmlElement(name = "LastUpdate")
    private StringBinding lastUpdate;
    @XmlElement(name = "TimeCoverage")
    private List<StringBinding> timeCoverage;
    @XmlElement(name = "LegalOwner")
    private List<StringBinding> legalOwner;
    @XmlElement(name = "Location")
    private Location location;

    private GeneralInfo() {
    }

    public GeneralInfo(SimpleResourceClass resourceClass, StringBinding resourceName, Descriptions descriptions) {
        super(descriptions);
        this.resourceClass = Collections.singletonList(new ResourceClass(resourceClass));
        this.resourceName = Collections.singletonList(resourceName);
    }

    public List<StringBinding> getResourceName() {
        return this.resourceName;
    }

    public List<StringBinding> getResourceTitle() {
        return this.resourceTitle;
    }

    public List<ResourceClass> getResourceClass() {
        return this.resourceClass;
    }

    public StringBinding getPID() {
        return pid;
    }

    public List<StringBinding> getVersion() {
        return this.version;
    }

    public LifeCycleStatus getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public XMLGregorianCalendar getStartYear() {
        return startYear;
    }

    public XMLGregorianCalendar getCompletionYear() {
        return completionYear;
    }

    public StringBinding getPublicationDate() {
        return publicationDate;
    }

    public StringBinding getLastUpdate() {
        return lastUpdate;
    }

    public List<StringBinding> getTimeCoverage() {
        return this.timeCoverage;
    }

    public List<StringBinding> getLegalOwner() {
        return this.legalOwner;
    }

    public Location getLocation() {
        return location;
    }
}
