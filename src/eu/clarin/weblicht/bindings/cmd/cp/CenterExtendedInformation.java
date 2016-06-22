package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"website", "longTermArchivingPolicy", "repositorySystem", "pidStatus", "strictVersioning", "assessmentStatus", "infrastructureServices", "webReference", "aai", "metadata", "organisationalInformation", "monitoring"})
public class CenterExtendedInformation extends AbstractComponent {

    @XmlElement(name = "Website", required = true)
    @XmlSchemaType(name = "anyURI")
    private String website;
    @XmlElement(name = "LongTermArchivingPolicy", required = true)
    private String longTermArchivingPolicy;
    @XmlElement(name = "RepositorySystem", required = true)
    private String repositorySystem;
    @XmlElement(name = "PidStatus", required = true)
    private String pidStatus;
    @XmlElement(name = "StrictVersioning")
    private boolean strictVersioning;
    @XmlElement(name = "AssessmentStatus", required = true)
    private String assessmentStatus;
    @XmlElement(name = "InfrastructureServices")
    private String infrastructureServices;
    @XmlElement(name = "WebReference")
    private List<WebReference> webReference;
    @XmlElement(name = "AAI", required = true)
    private AAI aai;
    @XmlElement(name = "Metadata", required = true)
    private List<Metadata> metadata;
    @XmlElement(name = "OrganisationalInformation", required = true)
    private OrganisationalInformation organisationalInformation;
    @XmlElement(name = "Monitoring", required = true)
    private Monitoring monitoring;

    public String getWebsite() {
        return website;
    }

    public String getLongTermArchivingPolicy() {
        return longTermArchivingPolicy;
    }

    public String getRepositorySystem() {
        return repositorySystem;
    }

    public String getPidStatus() {
        return pidStatus;
    }

    public boolean isStrictVersioning() {
        return strictVersioning;
    }

    public String getAssessmentStatus() {
        return assessmentStatus;
    }

    public String getInfrastructureServices() {
        return infrastructureServices;
    }

    public List<WebReference> getWebReference() {
        return this.webReference;
    }

    public AAI getAAI() {
        return aai;
    }

    public List<Metadata> getMetadata() {
        return this.metadata;
    }

    public OrganisationalInformation getOrganisationalInformation() {
        return organisationalInformation;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }
}
