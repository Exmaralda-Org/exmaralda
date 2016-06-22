package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractDescribedComponent;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AnnotationTypes extends AbstractDescribedComponent {

    @XmlElement(name = "AnnotationType", required = true)
    private List<AnnotationType> annotationTypes;

    private AnnotationTypes() {
    }

    public List<AnnotationType> getAnnotationTypes() {
        return annotationTypes;
    }

    @Override
    public AnnotationTypes copy() {
        AnnotationTypes annotationTypesBinding = (AnnotationTypes) super.copy();
        annotationTypesBinding.annotationTypes = copy(annotationTypes);
        return annotationTypesBinding;
    }
}
