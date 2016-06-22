package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.Descriptions;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "topics",
    "creators",
    "creationToolInfos",
    "annotation",
    "sources",
    "descriptions"
})
public class Creation extends AbstractComponent {

    @XmlElement(name = "Topic")
    private List<StringBinding> topics;
    @XmlElement(name = "Creators")
    private Creators creators;
    @XmlElement(name = "CreationToolInfo")
    private List<CreationToolInfo> creationToolInfos;
    @XmlElement(name = "Annotation")
    private Annotation annotation;
    @XmlElement(name = "Source")
    private List<Source> sources;
    @XmlElement(name = "Descriptions")
    private Descriptions descriptions;

    Creation() {
    }

    public List<StringBinding> getTopic() {
        return topics;
    }

    public Creators getCreators() {
        if (creators == null) {
            creators = new Creators();
        }
        return creators;
    }

    public List<CreationToolInfo> getCreationToolInfos() {
        return creationToolInfos;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public List<Source> getSource() {
        return sources;
    }

    public Descriptions getDescriptions() {
        return descriptions;
    }

    @Override
    public String toString() {
        List<Creator> creatorsBindings = creators.getCreators();
        return (creatorsBindings != null && creatorsBindings.size() == 1) ? creatorsBindings.get(0).getContact().toString() : null;
    }

    @Override
    public Creation copy() {
        Creation creation = (Creation) super.copy();
        creation.topics = shallowCopy(topics);
        creation.creators = copy(creators);
        creation.creationToolInfos = copy(creationToolInfos);
        creation.annotation = copy(annotation);
        creation.sources = copy(sources);
        creation.descriptions = copy(descriptions);
        return creation;
    }
}
