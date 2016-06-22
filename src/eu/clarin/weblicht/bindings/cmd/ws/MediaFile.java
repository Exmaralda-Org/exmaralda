package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractDescribedComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class MediaFile extends AbstractDescribedComponent {

    @XmlElement(name = "CatalogueLink", required = true)
    private List<StringBinding> catalogueLinks;
    @XmlElement(name = "Type", required = true)
    private Type type;
    @XmlElement(name = "Format")
    private List<StringBinding> formats;
    @XmlElement(name = "Size")
    private List<StringBinding> sizes;
    @XmlElement(name = "Quality", required = true)
    private Quality quality;
    @XmlElement(name = "RecordingConditions", required = true)
    private List<StringBinding> recordingConditions;
    @XmlElement(name = "Position", required = true)
    private Position position;
    @XmlElement(name = "Access", required = true)
    private Access access;
    @XmlElement(name = "Keys")
    private List<Keys> keys;

    private MediaFile() {
    }

    public List<StringBinding> getCatalogueLinks() {
        return catalogueLinks;
    }

    public Type getType() {
        return type;
    }

    public List<StringBinding> getFormats() {
        return formats;
    }

    public List<StringBinding> getSizes() {
        return sizes;
    }

    public Quality getQuality() {
        return quality;
    }

    public List<StringBinding> getRecordingConditions() {
        return recordingConditions;
    }

    public Position getPosition() {
        return position;
    }

    public Access getAccess() {
        return access;
    }

    public List<Keys> getKeys() {
        return keys;
    }

    @Override
    public MediaFile copy() {
        MediaFile mediaFile = (MediaFile) super.copy();
        mediaFile.catalogueLinks = shallowCopy(catalogueLinks);
        mediaFile.formats = shallowCopy(formats);
        mediaFile.sizes = shallowCopy(sizes);
        mediaFile.recordingConditions = shallowCopy(recordingConditions);
        mediaFile.position = copy(position);
        mediaFile.access = copy(access);
        mediaFile.keys = copy(keys);
        return mediaFile;
    }
}
