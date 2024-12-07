package org.exmaralda.coma.panels;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;

import org.exmaralda.coma.datatypes.ComaDatatype;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;

public class ComaHyperlinkListener implements HyperlinkListener {

	Coma coma;

	public ComaHyperlinkListener(Coma coma2) {
		super();
		coma = coma2;
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {

		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			AttributeSet atts;
			atts = e.getSourceElement().getAttributes();
			atts = (AttributeSet) atts
					.getAttribute(javax.swing.text.html.HTML.Tag.A);
			String theId = (String) atts
					.getAttribute(javax.swing.text.html.HTML.Attribute.ID);
			String cmd;
			if ((theId == null)
					&& ((String) atts
							.getAttribute(javax.swing.text.html.HTML.Attribute.HREF) != null)) {
				cmd = "href";
			} else {
				cmd = theId.substring(0, theId.indexOf(":"));
			}
			if (!cmd.equals("href")) {

				String id = theId.substring(theId.indexOf(":") + 1);
				if (cmd.equals("edit")) {
					ComaDatatype datatype = coma.getEditableElement(id);
					// coma.status("Datatype:" + datatype);
					datatype.edit();
				} else if (cmd.equals("dele")) {
					ComaDatatype datatype = coma.getEditableElement(id);
					System.out.println(datatype);
					datatype.delete();
				} else if (cmd.startsWith("add")) {
					String elementToAdd = cmd.split("\\.")[1];
					ComaDatatype datatype = coma.getEditableElement(id);
					datatype.add(elementToAdd);
				} else if (cmd.equals("show")) {
					coma.selectRoleTargets(id);
				} else if (cmd.equals("editroles")) {
					coma.editRoles(id);
				} else if (cmd.startsWith("filter")) {
					if (id.contains("Communication")) {
						coma.setCommFilter(id, false);
					}
					if (id.contains("Speaker")) {
						coma.setSpeakerFilter(id, false);
					}
				} else if (cmd.startsWith("column")) {
					coma.setComm2ndColumn(id);
				}

			} else {
				URI href;
				try {
					href = new URI(
							(String) atts
									.getAttribute(javax.swing.text.html.HTML.Attribute.HREF));
					System.out.println(href);
					if (href.isAbsolute()) {
						System.out.println("abs");
						if (href.getScheme().startsWith("http")) {
							try {
								Desktop.getDesktop().browse(href);
							} catch (Exception err) {
								err.printStackTrace();
							}
						} else if (href.getScheme().startsWith("file")) {
							try {
								Desktop.getDesktop().open(new File(href));
							} catch (Exception err) {
								JOptionPane.showMessageDialog(coma,
										err.getMessage());
							}

						}
					} else {
						System.out.println("nicht abs");
						try {
							URI u = new URI(coma.getData().getOpenFile()
									.getParentFile().toURI()
									+ "" + href);
							System.out.println(u);
							File f = new File(u);
							Desktop.getDesktop().open(f);
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(coma,
									e1.getMessage());
						}

					}

				} catch (URISyntaxException e1) {
					System.err
							.println("ComaHyperlinkListener: URI Syntax b0rked.");
				}
				// if (href.startsWith("http:")) {
				// } else if (href.toLowerCase().startsWith("file:")) {
				// System.out.println("fileURI:  "+href);
				// try {
				// Desktop.getDesktop().open(new File(href));
				// } catch (Exception err2) { // relative?
				// System.out.println(err2.getLocalizedMessage());
				// // File f = new File (href);
				// // File cp = (coma.getData().getOpenFile().getParentFile());
				// // String s = cp.getPath();
				// }
				// } else {
				// try {
				// Desktop.getDesktop().open(new
				// File(coma.getData().getOpenFile().getParent() +
				// File.separator + href));
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// }
				// String h = href.substring(href.indexOf(":")+1);
				// System.out.println("file:");
				//
				//
				// try {
				// Desktop.getDesktop().open(new File(h));
				// } catch (Exception err) {
				// System.out.println(err.getMessage());
				// try {
				// File f = new File(href);
				// Desktop.getDesktop().open(new
				// File(coma.getData().getOpenFile().getParentFile().getPath()+File.separator
				// + f.getPath()));
				// // Desktop.getDesktop().open(new
				// File(coma.getData().getOpenFile().getParent() +
				// File.separator + href));
				//
				// // Desktop.getDesktop().open(new
				// File(coma.getData().getOpenFile().getParent() +
				// File.separator + href));
				// } catch (Exception err2) {
				// System.out.println(err2.getMessage());
				//
				// }

			}

		}

	}
}
