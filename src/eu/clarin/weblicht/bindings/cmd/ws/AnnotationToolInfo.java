package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AnnotationToolInfo extends AbstractToolInfo {

    @XmlElement(name = "AnnotationTool")
    private List<StringBinding> annotationTools;

    private AnnotationToolInfo() {
    }

    public List<StringBinding> getAnnotationTools() {
        return annotationTools;
    }

    @Override
    public AnnotationToolInfo copy() {
        AnnotationToolInfo annotationToolInfo = (AnnotationToolInfo) super.copy();
        annotationToolInfo.annotationTools = shallowCopy(annotationTools);
        return annotationToolInfo;
    }
}
