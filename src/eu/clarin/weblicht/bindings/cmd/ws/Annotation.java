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
public class Annotation extends AbstractDescribedComponent {

    @XmlElement(name = "AnnotationMode")
    private List<StringBinding> annotationMode;
    @XmlElement(name = "AnnotationStandoff")
    private List<StringBinding> annotationStandoff;
    @XmlElement(name = "InterannotatorAgreement")
    private List<StringBinding> interannotatorAgreement;
    @XmlElement(name = "AnnotationFormat")
    private List<StringBinding> annotationFormat;
    @XmlElement(name = "SegmentationUnits")
    private SegmentationUnits segmentationUnits;
    @XmlElement(name = "AnnotationTypes")
    private AnnotationTypes annotationTypes;
    @XmlElement(name = "AnnotationToolInfo")
    private List<AnnotationToolInfo> annotationToolInfo;

    private Annotation() {
    }

    public List<StringBinding> getAnnotationMode() {
        return annotationMode;
    }

    public List<StringBinding> getAnnotationStandoff() {
        return annotationStandoff;
    }

    public List<StringBinding> getInterannotatorAgreement() {
        return interannotatorAgreement;
    }

    public List<StringBinding> getAnnotationFormat() {
        return annotationFormat;
    }

    public SegmentationUnits getSegmentationUnits() {
        return segmentationUnits;
    }

    public AnnotationTypes getAnnotationTypes() {
        return annotationTypes;
    }

    public List<AnnotationToolInfo> getAnnotationToolInfo() {
        return annotationToolInfo;
    }

    @Override
    public Annotation copy() {
        Annotation annotation = (Annotation) super.copy();
        annotation.annotationMode = shallowCopy(annotationMode);
        annotation.annotationStandoff = shallowCopy(annotationStandoff);
        annotation.interannotatorAgreement = shallowCopy(interannotatorAgreement);
        annotation.annotationFormat = shallowCopy(annotationFormat);
        annotation.segmentationUnits = copy(segmentationUnits);
        annotation.annotationTypes = copy(annotationTypes);
        annotation.annotationToolInfo = copy(annotationToolInfo);
        return annotation;
    }
}