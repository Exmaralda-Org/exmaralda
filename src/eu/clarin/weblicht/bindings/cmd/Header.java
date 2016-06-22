package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"mdCreator", "mdCreationDate", "mdSelfLink", "mdProfile", "mdCollectionDisplayName"})
public class Header extends Copyable {

    private static final String MD_PROFILE = "clarin.eu:cr1:p_1320657629644";
    @XmlElement(name = "MdCreator")
    private String mdCreator;
    @XmlElement(name = "MdCreationDate")
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar mdCreationDate;
    @XmlElement(name = "MdSelfLink")
    @XmlSchemaType(name = "anyURI")
    private String mdSelfLink;
    @XmlElement(name = "MdProfile")
    @XmlSchemaType(name = "anyURI")
    private String mdProfile;
    @XmlElement(name = "MdCollectionDisplayName")
    private String mdCollectionDisplayName;

    private Header() {
    }

    public Header(String mdSelfLink) {
        this.mdSelfLink = mdSelfLink;
        this.mdProfile = MD_PROFILE;
    }

    public String getMdCreator() {
        return mdCreator;
    }

    public XMLGregorianCalendar getMdCreationDate() {
        return mdCreationDate;
    }

    public String getMdSelfLink() {
        return mdSelfLink;
    }

    public void setMdSelfLink(String mdSelfLink) {
        this.mdSelfLink = mdSelfLink;
    }

    public String getMdProfile() {
        return mdProfile;
    }

    public String getMdCollectionDisplayName() {
        return mdCollectionDisplayName;
    }

    @Override
    public Header copy() {
        Header header = (Header) super.copy();
        if (mdCreationDate != null) {
            header.mdCreationDate = (XMLGregorianCalendar) mdCreationDate.clone();
        }
        return header;
    }
}
