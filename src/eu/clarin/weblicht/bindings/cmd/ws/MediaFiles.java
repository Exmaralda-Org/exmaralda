package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractDescribedComponent;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class MediaFiles extends AbstractDescribedComponent {

    @XmlElement(name = "MediaFile", required = true)
    private List<MediaFile> mediaFiles;
    @XmlAttribute(name = "id")
    private String id;

    private MediaFiles() {
    }

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public String getId() {
        return id;
    }

    @Override
    public MediaFiles copy() {
        MediaFiles mf = (MediaFiles) super.copy();
        mf.mediaFiles = copy(mediaFiles);
        return mf;
    }
}
