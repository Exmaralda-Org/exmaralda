package org.exmaralda.fedora;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.exmaralda.coma.resources.ResourceHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.GetObjectHistory;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.response.GetObjectHistoryResponse;

public class CreateFedoraObjectTest {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();

		properties.load(new ResourceHandler().fedoraCredentialsStream());
		FedoraCredentials credentials = new FedoraCredentials(
				properties.getProperty("url"), properties.getProperty("user"),
				properties.getProperty("pass"));
		FedoraClient fedora = new FedoraClient(credentials);
		Document fox = getFoxMl();

		System.out.println(fox.toString());

		try {
			new Ingest("hzsk:test").content(
					new XMLOutputter(Format.getPrettyFormat())
							.outputString(fox)).execute(fedora);
		} catch (FedoraClientException e) {
			if (e.getMessage().contains("ObjectExistsException")) {
				System.out.println("hzsk:test already exists");
			} else {
				System.out.println(e.getMessage());
			}
		}
		try {
			FedoraClient.modifyObject("hzsk:test").label("Java-Testobjekt")
					.lastModifiedDate(new Date()).ownerId("fedoraAdmin")
					.execute(fedora);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		
//		try {
//			GetObjectHistoryResponse response = FedoraClient.getObjectHistory("hzsk:test").execute(fedora);
//			System.out.println(response.getObjectChangeDate());
//		} catch (FedoraClientException e) {
//			e.printStackTrace();
//		}

	}

	private static Document getFoxMl() {
		Namespace fox = Namespace.getNamespace("foxml",
				"info:fedora/fedora-system:def/foxml#");
		Document d = new Document();
		Element r = new Element("digitalObject", fox);
		r.setAttribute("VERSION", "1.1");
		d.setRootElement(r);
		r.addContent(new Element("objectProperties", fox)
				.addContent(new Element("property", fox).setAttribute("NAME",
						"info:fedora/fedora-system:def/model#state")
						.setAttribute("VALUE", "Active")));
		return d;
	}

}
