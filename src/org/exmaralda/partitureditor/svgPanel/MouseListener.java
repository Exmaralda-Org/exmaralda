package org.exmaralda.partitureditor.svgPanel;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.svg.SVGOMRect;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGRect;

public class MouseListener implements EventListener {

	private JSVGCanvas canvas;

	public MouseListener(JSVGCanvas svgCanvas) {
		this.canvas = svgCanvas;
	}

	@Override
	public void handleEvent(Event evt) {
		if (evt instanceof DOMMouseEvent) {
			EventTarget target = evt.getCurrentTarget();

			if (target instanceof SVGImageElement) {
				int x = ((DOMMouseEvent) evt).getClientX();
				int y = ((DOMMouseEvent) evt).getClientY();
				
				// calculate position in SVG from clicked position
				AffineTransform at = canvas.getViewBoxTransform();
				if (at != null) {
					try {
						at = at.createInverse();
					} catch (NoninvertibleTransformException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Point2D p2d = at.transform(new Point2D.Float(x, y), null);
					x = Math.round(((float) p2d.getX()));
					y = Math.round((float) p2d.getY());
				}

				SVGRect rect = new SVGOMRect();
				rect.setHeight(1.0f);
				rect.setWidth(1.0f);
				rect.setX(new Float(x));
				rect.setY(new Float(y));

				GraphicsNode gvtRoot = canvas.getCanvasGraphicsNode();
				List<?> it = gvtRoot.getRoot().getChildren();
				
				// check which elements contain the point
				List<GraphicsNode> containingNodes = new ArrayList<>();
				for (Object o : ((CanvasGraphicsNode) it.get(0)).getChildren()) {

					Rectangle2D rect2 = ((GraphicsNode) o).getSensitiveBounds();
					if (rect2.contains(x, y)) {
						containingNodes.add((GraphicsNode) o);
					}
				}

				// find smallest enclosing node
				GraphicsNode smallestNode = null;
				if (!containingNodes.isEmpty()) {
					smallestNode = containingNodes.get(0);
					for (GraphicsNode node : containingNodes.subList(1, containingNodes.size())) {
						if (smallestNode.getSensitiveBounds().contains(node.getSensitiveBounds())) {
							smallestNode = node;
						}
					}
				}

				// bridge helps retrieving SVG element from graphical element
				BridgeContext ctx = canvas.getUpdateManager().getBridgeContext();
				String xpointer = getXPath((SVGOMElement) ctx
						.getElement(smallestNode));
				XPointerObservable._instance.setCurrentXPointer(xpointer);
			}

			if (target instanceof SVGOMElement
					&& !(target instanceof SVGImageElement)) {
				String xpointer = getXPath(((SVGOMElement) target));
				XPointerObservable._instance.setCurrentXPointer(xpointer);
			}
		}
	}

	public String getXPath(SVGOMElement node) {
		if (node.hasAttribute("id")) {
			//return "//*[@id='" + node.getAttribute("id") + "']";
                        return "id('" + node.getAttribute("id") + "')";
		}
		Node parent = node.getParentNode();
		if (parent instanceof SVGOMElement) {
			return getXPath((SVGOMElement) parent) + "/" + node.getLocalName();
		}
		return node.getLocalName();
	}

}
