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
public class SegmentationUnits extends AbstractDescribedComponent {

    @XmlElement(name = "SegmentationUnit", required = true)
    private List<SegmentationUnit> segmentationUnits;

    public List<SegmentationUnit> getSegmentationUnits() {
        return segmentationUnits;
    }

    @Override
    public SegmentationUnits copy() {
        SegmentationUnits segmentationUnitsBinding = (SegmentationUnits) super.copy();
        segmentationUnitsBinding.segmentationUnits = shallowCopy(segmentationUnits);
        return segmentationUnitsBinding;
    }
}