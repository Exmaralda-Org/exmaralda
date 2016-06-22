package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author akislev
 */
public class JournalFileProxy {

    @XmlElement(name = "JournalFileRef", required = true)
    @XmlSchemaType(name = "anyURI")
    private String journalFileRef;

    private JournalFileProxy() {
    }

    public String getJournalFileRef() {
        return journalFileRef;
    }
}
