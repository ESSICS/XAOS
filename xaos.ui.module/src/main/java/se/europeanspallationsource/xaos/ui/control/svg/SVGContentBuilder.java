/*
 * Copyright 2018 European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.control.svg;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_FILL;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_ID;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_OPACITY;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_STROKE;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_STROKE_LINECAP;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_STROKE_LINEJOIN;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_STROKE_MITERLIMIT;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_STROKE_WIDTH;
import static se.europeanspallationsource.xaos.ui.control.svg.SVGAttributesStackFrame.ATTR_TRANSFORM;
import static se.europeanspallationsource.xaos.ui.impl.Constants.LOGGER;


/**
 * Utility that parses the SVG stream and build a corresponding {@link SVG}
 * object.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/skrb/SVGLoader">SVGLoader</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
class SVGContentBuilder {

	private static final String TYPE_KEY = "type";

	private final Deque<SVGAttributesStackFrame> attributesStack = new ArrayDeque<>(4);
	private final Map<String, Paint> gradients;
	private final Map<String, QName> qnames = new TreeMap<>();
	private final SVG root;
	private final InputStream stream;
	private StringBuilder styleBuilder = null;
	private final Map<String, String> styles = new TreeMap<>();
	private final URL url;

	SVGContentBuilder( URL url ) {
		this.url = url;
		this.root = new SVG();
		this.gradients = new HashMap<>(1);
		this.stream = null;
	}

	SVGContentBuilder( InputStream stream ) {
		this.url = null;
		this.root = new SVG();
		this.gradients = new HashMap<>(1);
		this.stream = stream;
	}

	SVG build() throws IOException, XMLStreamException {

		XMLInputFactory factory = XMLInputFactory.newInstance();

		factory.setProperty("javax.xml.stream.isValidating", false);
		factory.setProperty("javax.xml.stream.isNamespaceAware", false);
		factory.setProperty("javax.xml.stream.supportDTD", false);

		try ( BufferedInputStream bufferedStream = new BufferedInputStream(stream != null ? stream : url.openStream()) ) {

			XMLEventReader reader = factory.createXMLEventReader(bufferedStream);

			build(reader, root);
			reader.close();

		}

		return root;

	}

	/**
	 * Return the string value of the named attribute inside the given
	 * {@code element}.
	 *
	 * @param attributeName The name of the attribute whose value must be returned.
	 * @param element       The {@link StartElement} possibly containing the
	 *                      attribute whose name is given.
	 * @return The found value or {@code null};
	 */
	String getAttributeValue( String attributeName, StartElement element ) {

		Attribute attribute = element.getAttributeByName(getQName(attributeName));

		return ( attribute != null ) ? StringUtils.trimToNull(attribute.getValue()) : null;

	}

	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	Map<String, QName> getQNames() {
		return qnames;
	}

	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	Map<String, String> getStyles() {
		return styles;
	}

	private void applyStyles( Node node, SVGAttributesStackFrame stackFrame ) {

		if ( node instanceof Shape ) {

			Shape shape = (Shape) node;

			stackFrame.consumeAttribute(ATTR_FILL, this::toPaint, f -> shape.setFill(f));
			stackFrame.consumeAttribute(ATTR_STROKE, this::toPaint, s -> shape.setStroke(s));
			stackFrame.consumeAttribute(ATTR_STROKE_WIDTH, Double::parseDouble, w -> shape.setStrokeWidth(w));
			stackFrame.consumeAttribute(ATTR_STROKE_MITERLIMIT, Double::parseDouble, l -> shape.setStrokeMiterLimit(l));
			stackFrame.consumeAttribute(ATTR_STROKE_LINECAP, this::toStrokeLineCap, c -> shape.setStrokeLineCap(c));
			stackFrame.consumeAttribute(ATTR_STROKE_LINEJOIN, this::toStrokeLineJoin, j -> shape.setStrokeLineJoin(j));

		}

		stackFrame.consumeAttribute(ATTR_OPACITY, Double::parseDouble, o -> node.setOpacity(o));
		stackFrame.consumeAttribute(ATTR_TRANSFORM, this::toTransform, t -> node.getTransforms().add(t));

	}

	private void build( XMLEventReader reader, Group group ) throws IOException, XMLStreamException {

		while ( reader.hasNext() ) {

			XMLEvent event = reader.nextEvent();

			if ( event.isStartElement() ) {

				Node node = null;
				StartElement element = (StartElement) event;
				String type = element.getName().toString();

				switch ( type ) {
					case "circle":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildCircle(element);
						break;
					case "ellipse":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildEllipse(element);
						break;
					case "g":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildGroup(reader);
						break;
					case "image":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildImage(element);
						break;
					case "line":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildLine(element);
						break;
					case "linearGradient":
						buildLinearGradient(reader, element);
						break;
					case "path":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildPath(element);
						break;
					case "polygon":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildPolygon(element);
						break;
					case "polyline":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildPolyline(element);
						break;
					case "radialGradient":
						buildRadialGradient(reader, element);
						break;
					case "rect":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildRect(element);
						break;
					case "style":
						styleBuilder = new StringBuilder(256);
						break;
					case "svg": {

						SVGAttributesStackFrame stackFrame = new SVGAttributesStackFrame(this);

						attributesStack.push(stackFrame);
						stackFrame.populate(element);

						node = buildGroup(reader);

						break;

					}
					case "text":
						attributesStack.push(attributesStack.peek().deriveAndPopulate(element));
						node = buildText(reader, element);
						break;
					default:
						LOGGER.warning(MessageFormat.format("Non Support Element: {0}", element));
						break;
				}

				if ( node != null ) {

					SVGAttributesStackFrame stackFrame = attributesStack.peek();
					String id = stackFrame.get(ATTR_ID);

					if ( id != null ) {
						node.setId(id);
						root.putNode(id, node);
					}

					node.getProperties().put(TYPE_KEY, type);
					applyStyles(node, stackFrame);
					group.getChildren().add(node);

				}

			} else if ( event.isEndElement() ) {

				EndElement element = (EndElement) event;

				switch ( element.getName().toString() ) {
					case "circle":
					case "ellipse":
					case "image":
					case "line":
					case "path":
					case "polygon":
					case "polyline":
					case "rect":
					case "text":
						attributesStack.pop();
						break;
					case "g":
						attributesStack.pop();
						return;
					case "style":
						populateStyles(styleBuilder.toString());
						styleBuilder = null;
						break;
					case "svg":
						return;
					case "linearGradient":
					case "radialGradient":
					default:
						break;
				}

			} else if ( event.isCharacters() ) {
				if ( styleBuilder != null ) {
					styleBuilder.append(( (Characters) event ).getData());
				}
			}

		}

	}

	private Shape buildCircle( StartElement element ) {

		String cxAttribute = getAttributeValue("cx", element);
		String cyAttribute = getAttributeValue("cy", element);
		String rAttribute = getAttributeValue("r", element);

		try {
			return new Circle(
				( cxAttribute != null ) ? Double.parseDouble(cxAttribute) : 0.0,
				( cyAttribute != null ) ? Double.parseDouble(cyAttribute) : 0.0,
				( rAttribute != null ) ? Double.parseDouble(rAttribute) : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A circle's attribute cannot be parsed to double: cx [{0}], cy [{1}], r [{2}].",
				( cxAttribute != null ) ? cxAttribute : "-",
				( cyAttribute != null ) ? cyAttribute : "-",
				( rAttribute != null ) ? rAttribute : "-"
			));
			return null;
		}

	}

	private Shape buildEllipse( StartElement element ) {

		String cxAttribute = getAttributeValue("cx", element);
		String cyAttribute = getAttributeValue("cy", element);
		String rxAttribute = getAttributeValue("rx", element);
		String ryAttribute = getAttributeValue("ry", element);

		try {
			return new Ellipse(
				( cxAttribute != null ) ? Double.parseDouble(cxAttribute) : 0.0,
				( cyAttribute != null ) ? Double.parseDouble(cyAttribute) : 0.0,
				( rxAttribute != null && !"auto".equals(rxAttribute) ) ? Double.parseDouble(rxAttribute) : 0.0,
				( ryAttribute != null && !"auto".equals(ryAttribute) ) ? Double.parseDouble(ryAttribute) : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"An ellipse's attribute cannot be parsed to double: cx [{0}], cy [{1}], rx [{2}], ry [{3}].",
				( cxAttribute != null ) ? cxAttribute : "-",
				( cyAttribute != null ) ? cyAttribute : "-",
				( rxAttribute != null ) ? rxAttribute : "-",
				( ryAttribute != null ) ? ryAttribute : "-"
			));
			return null;
		}

	}

	private Group buildGroup( XMLEventReader reader ) throws IOException, XMLStreamException {

		Group group = new Group();
		build(reader, group);

		return group;

	}

	private ImageView buildImage( StartElement element ) {

		String hrefAttribute = getAttributeValue("href", element);

		if ( hrefAttribute != null ) {

			URL imageUrl = null;

			try {
				imageUrl = new URL(hrefAttribute);
			} catch ( MalformedURLException ex1 ) {
				try {
					imageUrl = new URL(url, hrefAttribute);
				} catch ( MalformedURLException ex2 ) {
					LOGGER.warning(MessageFormat.format(
						"Image's href attribute is not a valid URL [{0}].",
						hrefAttribute
					));
				}
			}

			if ( imageUrl != null ) {

				String xAttribute = getAttributeValue("x", element);
				String yAttribute = getAttributeValue("y", element);
				String widthAttribute = getAttributeValue("width", element);
				String heightAttribute = getAttributeValue("height", element);

				try {

					Image image = new Image(
						imageUrl.toString(),
						( widthAttribute != null ) ? Double.parseDouble(widthAttribute) : 0.0,
						( heightAttribute != null ) ? Double.parseDouble(heightAttribute) : 0.0,
						true,
						true
					);

					ImageView imageView = new ImageView(image);

					imageView.setLayoutX(( xAttribute != null ) ? Double.parseDouble(xAttribute) : 0.0);
					imageView.setLayoutY(( yAttribute != null ) ? Double.parseDouble(yAttribute) : 0.0);

					return imageView;

				} catch ( NumberFormatException ex ) {
					LOGGER.warning(MessageFormat.format(
						"An image's attribute cannot be parsed to double: x [{0}], y [{1}], width [{2}], height [{3}].",
						( xAttribute != null ) ? xAttribute : "-",
						( yAttribute != null ) ? yAttribute : "-",
						( widthAttribute != null ) ? widthAttribute : "-",
						( heightAttribute != null ) ? heightAttribute : "-"
					));
				}
			}

		} else {
			LOGGER.warning("An image's attribute is null: href.");
		}

		return null;

	}

	private Shape buildLine( StartElement element ) {

		String x1Attribute = getAttributeValue("x1", element);
		String y1Attribute = getAttributeValue("y1", element);
		String x2Attribute = getAttributeValue("x2", element);
		String y2Attribute = getAttributeValue("y2", element);

		try {
			return new Line(
				( x1Attribute != null ) ? Double.parseDouble(x1Attribute) : 0.0,
				( y1Attribute != null ) ? Double.parseDouble(y1Attribute) : 0.0,
				( x2Attribute != null ) ? Double.parseDouble(x2Attribute) : 0.0,
				( y2Attribute != null ) ? Double.parseDouble(y2Attribute) : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A line's attribute cannot be parsed to double: x1 [{0}], y1 [{1}], x2 [{2}], y2 [{3}].",
				( x1Attribute != null ) ? x1Attribute : "-",
				( y1Attribute != null ) ? y1Attribute : "-",
				( x2Attribute != null ) ? x2Attribute : "-",
				( y2Attribute != null ) ? y2Attribute : "-"
			));
		}

		return null;

	}

	private void buildLinearGradient( XMLEventReader reader, StartElement element )
		throws IOException, XMLStreamException {

		String id = null;
		double x1 = Double.NaN;
		double y1 = Double.NaN;
		double x2 = Double.NaN;
		double y2 = Double.NaN;
		Transform transform = null;
		@SuppressWarnings( "unchecked" )
		Iterator<Attribute> it = element.getAttributes();

		while ( it.hasNext() ) {

			Attribute attribute = it.next();

			switch ( attribute.getName().getLocalPart() ) {
				case "id":
					id = attribute.getValue();
					break;
				case "gradientUnits":
					if ( !"userSpaceOnUse".equals(attribute.getValue()) ) {
						LOGGER.warning(MessageFormat.format("LinearGradient supports only userSpaceOnUse: {0}", element));
						return;
					}
					break;
				case "x1":
					try {
						x1 = Double.parseDouble(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("LinearGradient's x1 attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "y1":
					try {
						y1 = Double.parseDouble(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("LinearGradient's y1 attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "x2":
					try {
						x2 = Double.parseDouble(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("LinearGradient's x2 attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "y2":
					try {
						y2 = Double.parseDouble(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("LinearGradient's y2 attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "gradientTransform":
					transform = toTransform(attribute.getValue());
					break;
				default:
					LOGGER.warning(MessageFormat.format("LinearGradient doesn''t supports: {0}:{1}", attribute, element));
					break;
			}

		}

		if ( id != null && x1 != Double.NaN && y1 != Double.NaN && x2 != Double.NaN && y2 != Double.NaN ) {

			List<Stop> stops = buildStops(reader, "linearGradient");

			if ( transform != null && transform instanceof Affine ) {

				double x1d = x1;
				double y1d = y1;
				double x2d = x2;
				double y2d = y2;
				Affine affine = (Affine) transform;

				x1 = x1d * affine.getMxx() + y1d * affine.getMxy() + affine.getTx();
				y1 = x1d * affine.getMyx() + y1d * affine.getMyy() + affine.getTy();
				x2 = x2d * affine.getMxx() + y2d * affine.getMxy() + affine.getTx();
				y2 = x2d * affine.getMyx() + y2d * affine.getMyy() + affine.getTy();

			}

			gradients.put(id, new LinearGradient(x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops));

		}

	}

	private Shape buildPath( StartElement element ) {

		String dAttribute = getAttributeValue("d", element);

		if ( dAttribute != null ) {

			SVGPath path = new SVGPath();

			path.setContent(dAttribute);

			return path;

		} else {
			LOGGER.warning("A path's attribute is null: d.");
		}

		return null;

	}

	private Shape buildPolygon( StartElement element ) {

		String pointsAttribute = getAttributeValue("points", element);

		if ( pointsAttribute != null ) {
			try {

				Polygon polygon = new Polygon();
				StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");

				while ( tokenizer.hasMoreTokens() ) {

					String point = tokenizer.nextToken();
					StringTokenizer tokenizer2 = new StringTokenizer(point, ",");
					Double x = Double.valueOf(tokenizer2.nextToken());
					Double y = Double.valueOf(tokenizer2.nextToken());

					polygon.getPoints().add(x);
					polygon.getPoints().add(y);

				}

				return polygon;

			} catch ( NumberFormatException ex ) {
				LOGGER.warning(MessageFormat.format(
					"A polygon's point coordinate cannot be parsed to double [{0}].",
					pointsAttribute
				));
			}

		} else {
			LOGGER.warning("A polygon's attribute is null: points.");
		}

		return null;

	}

	private Shape buildPolyline( StartElement element ) {

		String pointsAttribute = getAttributeValue("points", element);

		if ( pointsAttribute != null ) {
			try {

				Polyline polyline = new Polyline();
				StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ");

				while ( tokenizer.hasMoreTokens() ) {

					String points = tokenizer.nextToken();
					StringTokenizer tokenizer2 = new StringTokenizer(points, ",");
					double x = Double.parseDouble(tokenizer2.nextToken());
					double y = Double.parseDouble(tokenizer2.nextToken());

					polyline.getPoints().add(x);
					polyline.getPoints().add(y);

				}

				return polyline;

			} catch ( NumberFormatException ex ) {
				LOGGER.warning(MessageFormat.format(
					"A polyline's point coordinate cannot be parsed to double [{0}].",
					pointsAttribute
				));
			}

		} else {
			LOGGER.warning("A polyline's attribute is null: points.");
		}

		return null;

	}

	private void buildRadialGradient( XMLEventReader reader, StartElement element )
		throws IOException, XMLStreamException {

		String id = null;
		Double fx = null;
		Double fy = null;
		Double cx = null;
		Double cy = null;
		Double r = null;
		Transform transform = null;
		@SuppressWarnings( "unchecked" )
		Iterator<Attribute> it = element.getAttributes();

		while ( it.hasNext() ) {

			Attribute attribute = it.next();

			switch ( attribute.getName().getLocalPart() ) {
				case "id":
					id = attribute.getValue();
					break;
				case "gradientUnits":
					if ( !"userSpaceOnUse".equals(attribute.getValue()) ) {
						LOGGER.warning(MessageFormat.format("RadialGradient supports only userSpaceOnUse: {0}", element));
						return;
					}
					break;
				case "fx":
					try {
						fx = Double.valueOf(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("RadialGradient's fx attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "fy":
					try {
						fy = Double.valueOf(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("RadialGradient's fy attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "cx":
					try {
						cx = Double.valueOf(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("RadialGradient's cx attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "cy":
					try {
						cy = Double.valueOf(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("RadialGradient's cy attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "r":
					try {
						r = Double.valueOf(attribute.getValue());
					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format("RadialGradient's r attribute cannot be parsed to double [{0}].", attribute.getValue()));
					}
					break;
				case "gradientTransform":
					transform = toTransform(attribute.getValue());
					break;
				default:
					LOGGER.log(Level.INFO, "RadialGradient doesn''t supports: {0}", element);
					break;
			}

		}

		if ( id != null && cx != null && cy != null && r != null ) {

			List<Stop> stops = buildStops(reader, "radialGradient");
			double fDistance = 0.0;
			double fAngle = 0.0;

			if ( transform != null && transform instanceof Affine ) {

				double tempCx = cx;
				double tempCy = cy;
				double tempR = r;
				Affine affine = (Affine) transform;

				cx = tempCx * affine.getMxx() + tempCy * affine.getMxy() + affine.getTx();
				cy = tempCx * affine.getMyx() + tempCy * affine.getMyy() + affine.getTy();
				r = Math.sqrt(tempR * affine.getMxx() * tempR * affine.getMxx() + tempR * affine.getMyx() * tempR * affine.getMyx());

				if ( fx != null && fy != null ) {

					double tempFx = fx;
					double tempFy = fy;

					fx = tempFx * affine.getMxx() + tempFy * affine.getMxy() + affine.getTx();
					fy = tempFx * affine.getMyx() + tempFy * affine.getMyy() + affine.getTy();

				} else {
					fAngle = Math.asin(affine.getMyx()) * 180.0 / Math.PI;
					fDistance = Math.sqrt(( cx - tempCx ) * ( cx - tempCx ) + ( cy - tempCy ) * ( cy - tempCy ));
				}

			}

			if ( fx != null && fy != null ) {
				fDistance = Math.sqrt(( fx - cx ) * ( fx - cx ) + ( fy - cy ) * ( fy - cy )) / r;
				fAngle = Math.atan2(cy - fy, cx - fx) * 180.0 / Math.PI;
			}

			gradients.put(id, new RadialGradient(fAngle, fDistance, cx, cy, r, false, CycleMethod.NO_CYCLE, stops));

		}

	}

	private Shape buildRect( StartElement element ) {

		String xAttribute = getAttributeValue("x", element);
		String yAttribute = getAttributeValue("y", element);
		String widthAttribute = getAttributeValue("width", element);
		String heightAttribute = getAttributeValue("height", element);
		String rxAttribute = getAttributeValue("rx", element);
		String ryAttribute = getAttributeValue("ry", element);

		try {

			Rectangle rectangle = new Rectangle(
				( xAttribute != null ) ? Double.parseDouble(xAttribute) : 0.0,
				( yAttribute != null ) ? Double.parseDouble(yAttribute) : 0.0,
				( widthAttribute != null ) ? Double.parseDouble(widthAttribute) : 0.0,
				( heightAttribute != null ) ? Double.parseDouble(heightAttribute) : 0.0
			);

			rectangle.setArcWidth(
				( rxAttribute != null && !"auto".equals(rxAttribute) ) ? Double.parseDouble(rxAttribute) : 0.0
			);
			rectangle.setArcHeight(
				( ryAttribute != null && !"auto".equals(ryAttribute) ) ? Double.parseDouble(ryAttribute) : 0.0
			);

			return rectangle;

		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A rect's attribute cannot be parsed to double: x [{0}], y [{1}], width [{2}], height [{3}], rx [{4}], ry [{5}].",
				( xAttribute != null ) ? xAttribute : "-",
				( yAttribute != null ) ? yAttribute : "-",
				( widthAttribute != null ) ? widthAttribute : "-",
				( heightAttribute != null ) ? heightAttribute : "-",
				( rxAttribute != null ) ? rxAttribute : "-",
				( ryAttribute != null ) ? ryAttribute : "-"
			));
			return null;
		}

	}

	private List<Stop> buildStops( XMLEventReader reader, String kindOfGradient )
		throws XMLStreamException {

		List<Stop> stops = new ArrayList<>(4);
		XMLEvent event = reader.nextEvent();

		while ( !event.isEndElement() || !event.asEndElement().getName().getLocalPart().equals(kindOfGradient) ) {

			if ( event.isStartElement() ) {

				StartElement element = event.asStartElement();

				if ( element.getName().getLocalPart().equals("stop") ) {

					double offset = Double.NaN;
					String color = null;
					double opacity = 1.0;
					@SuppressWarnings( "unchecked" )
					Iterator<Attribute> it = element.getAttributes();

					while ( it.hasNext() ) {

						Attribute attribute = it.next();

						switch ( attribute.getName().getLocalPart() ) {
							case "offset":
								try {
									offset = Double.parseDouble(attribute.getValue());
								} catch ( NumberFormatException ex ) {
									LOGGER.warning(MessageFormat.format(
										"LinearGradient's stop offset attribute cannot be parsed to double [{0}].",
										attribute.getValue()
									));
								}
								break;
							case "style": {

								String style = attribute.getValue();
								StringTokenizer tokenizer = new StringTokenizer(style, ";");

								while ( tokenizer.hasMoreTokens() ) {

									String item = tokenizer.nextToken().trim();

									if ( item.startsWith("stop-color") ) {
										color = item.substring(11);
									} else if ( item.startsWith("stop-opacity") ) {
										try {
											opacity = Double.parseDouble(item.substring(13));
										} catch ( NumberFormatException ex ) {
											LOGGER.warning(MessageFormat.format(
												"LinearGradient's stop style opacity attribute cannot be parsed to double [{0}].",
												attribute.getValue()
											));
										}
									} else {
										LOGGER.warning(MessageFormat.format(
											"LinearGradient's stop doesn''t supports: {0} [{1}] ''{2}''",
											attribute,
											element,
											item
										));
									}

								}

							}
							break;
							default:
								LOGGER.warning(MessageFormat.format(
									"LinearGradient's stop doesn''t supports: {0} [{1}]",
									attribute,
									element
								));
								break;
						}

					}

					if ( offset != Double.NaN && color != null ) {
						stops.add(new Stop(offset, Color.web(color, opacity)));
					}

				} else {
					LOGGER.warning(MessageFormat.format("LinearGradient doesn''t supports: {0}", element));
				}

			}

			event = reader.nextEvent();

		}

		return stops;

	}

	private Shape buildText( XMLEventReader reader, StartElement element )
		throws XMLStreamException {

		Font font = null;
		String fontFamilyAttribute = getAttributeValue("font-family", element);
		String fontSizeAttribute = getAttributeValue("font-size", element);

		if ( fontFamilyAttribute != null && fontSizeAttribute != null ) {
			try {
				font = Font.font(
					fontFamilyAttribute.replace("'", ""),
					Double.parseDouble(fontSizeAttribute)
				);
			} catch ( NumberFormatException ex ) {
				LOGGER.warning(MessageFormat.format(
					"A text's attribute cannot be parsed to double: font-size [{0}].",
					fontSizeAttribute
				));
			}
		}

		XMLEvent event = reader.nextEvent();

		if ( event.isCharacters() ) {

			Text text = new Text(( (Characters) event ).getData());

			if ( font != null ) {
				text.setFont(font);
			}

			return text;

		} else {
			throw new XMLStreamException("Illegal Element: " + event);
		}

	}

	private QName getQName( String name ) {

		QName qName = qnames.get(name);

		if ( qName == null ) {

			qName = new QName(name);

			qnames.put(name, qName);

		}

		return qName;

	}

	/**
	 * Parse the content of a style element populating the static map of styles,
	 * later on used to populate the current frame.
	 *
	 * @param styleElement The string content of a style element.
	 */
	private void populateStyles( String styleElement ) {

		String content = StringUtils.normalizeSpace(styleElement);

		//	@import rule not currently supported: skip it.
		final String IMPORT = "@import";

		while ( content.contains(IMPORT) ) {

			int start = content.indexOf(IMPORT);
			int end = content.indexOf(';', start + IMPORT.length());

			if ( end == -1 ) {
				content = "";
			} else {
				content = content.substring(0, start) + content.substring(1 + end);
			}

		}

		//	Get the classes and store them;
		String[] classes = content.split("\\}");

		for ( String clazz : classes ) {

			String[] nameValues = StringUtils.stripAll(clazz.split("\\{"));

			if ( nameValues.length == 2 ) {
				styles.put(nameValues[0], nameValues[1]);
			}

		}

	}

	private Paint toPaint( String value ) {

		Paint paint;

		if ( !"none".equals(value) ) {
			if ( value.startsWith("url(#") ) {
				paint = gradients.get(value.substring(5, value.length() - 1));
			} else {
				paint = Color.web(value);
			}
		} else {
			paint = Color.TRANSPARENT;
		}

		return paint;

	}

	private StrokeLineCap toStrokeLineCap( String value ) {

		StrokeLineCap linecap = StrokeLineCap.BUTT;

		if ( StringUtils.isNotBlank(value) ) {
			switch ( value ) {
				case "round":
					linecap = StrokeLineCap.ROUND;
					break;
				case "square":
					linecap = StrokeLineCap.SQUARE;
					break;
				case "butt":
					break;
				default:
					LOGGER.warning(MessageFormat.format(
						"Unsupported stroke-linecap [{0}]: ''butt'' used instead.",
						value
					));
					break;
			}
		}

		return linecap;

	}

	private StrokeLineJoin toStrokeLineJoin( String value ) {

		StrokeLineJoin linejoin = StrokeLineJoin.MITER;

		if ( StringUtils.isNotBlank(value) ) {
			switch ( value ) {
				case "bevel":
					linejoin = StrokeLineJoin.BEVEL;
					break;
				case "round":
					linejoin = StrokeLineJoin.ROUND;
					break;
				case "miter":
					break;
				default:
					LOGGER.warning(MessageFormat.format(
						"Unsupported stroke-linejoin [{0}]: ''miter'' used instead.",
						value
					));
					break;
			}
		}

		return linejoin;

	}

	private Transform toTransform( String value ) {

		Transform transform = new Translate();
		StringTokenizer tokenizer = new StringTokenizer(value, ")");

		while ( tokenizer.hasMoreTokens() ) {

			String transformTxt = tokenizer.nextToken();

			if ( transformTxt.startsWith("translate(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				String[] tokens = StringUtils.split(transformTxt, ", ");

				if ( tokens.length == 1 || tokens.length == 2 ) {
					try {

						double ty = 0.0;
						double tx = Double.parseDouble(tokens[0]);

						if ( tokens.length == 2 ) {
							ty = Double.parseDouble(tokens[2]);
						}

						transform = transform.createConcatenation(Transform.translate(tx, ty));

					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format(
							"''translate'' transform contains value not parsed to double [{0}].",
							transformTxt
						));
					}
				} else {
					LOGGER.warning(MessageFormat.format(
						"''translate'' transform doesn't contain the right number of elements [{0}].",
						transformTxt
					));
				}

			} else if ( transformTxt.startsWith("scale(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				String[] tokens = StringUtils.split(transformTxt, ", ");

				if ( tokens.length == 1 || tokens.length == 2 ) {
					try {

						double sx = Double.parseDouble(tokens[0]);
						double sy = sx;

						if ( tokens.length == 2 ) {
							sy = Double.parseDouble(tokens[2]);
						}

						transform = transform.createConcatenation(Transform.scale(sx, sy));

					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format(
							"''scale'' transform contains value not parsed to double [{0}].",
							transformTxt
						));
					}
				} else {
					LOGGER.warning(MessageFormat.format(
						"''scale'' transform doesn't contain the right number of elements [{0}].",
						transformTxt
					));
				}

			} else if ( transformTxt.startsWith("rotate(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				String[] tokens = StringUtils.split(transformTxt, ", ");

				if ( tokens.length == 1 || tokens.length == 3 ) {
					try {

						double cx = 0.0;
						double cy = 0.0;
						double angle = Double.parseDouble(tokens[0]);

						if ( tokens.length == 3 ) {
							cx = Double.parseDouble(tokens[1]);
							cy = Double.parseDouble(tokens[2]);
						}

						transform = transform.createConcatenation(Transform.rotate(angle, cx, cy));

					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format(
							"''rotate'' transform contains value not parsed to double [{0}].",
							transformTxt
						));
					}
				} else {
					LOGGER.warning(MessageFormat.format(
						"''rotate'' transform doesn't contain the right number of elements [{0}].",
						transformTxt
					));
				}

			} else if ( transformTxt.startsWith("skewX(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				try {

					double angle = Double.parseDouble(transformTxt);

					transform = transform.createConcatenation(Transform.shear(Math.tan(Math.toRadians(angle)), 0.0));

				} catch ( NumberFormatException ex ) {
					LOGGER.warning(MessageFormat.format(
						"''skewX'' transform contains value not parsed to double [{0}].",
						transformTxt
					));
				}

			} else if ( transformTxt.startsWith("skewY(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				try {

					double angle = Double.parseDouble(transformTxt);

					transform = transform.createConcatenation(Transform.shear(0.0, Math.tan(Math.toRadians(angle))));

				} catch ( NumberFormatException ex ) {
					LOGGER.warning(MessageFormat.format(
						"''skewY'' transform contains value not parsed to double [{0}].",
						transformTxt
					));
				}

			} else if ( transformTxt.startsWith("matrix(") ) {

				transformTxt = transformTxt.substring(1 + transformTxt.indexOf('('));

				String[] tokens = StringUtils.split(transformTxt, ", ");

				if ( tokens.length == 6 ) {
					try {

						double mxx = Double.parseDouble(tokens[0]);
						double myx = Double.parseDouble(tokens[1]);
						double mxy = Double.parseDouble(tokens[2]);
						double myy = Double.parseDouble(tokens[3]);
						double tx = Double.parseDouble(tokens[4]);
						double ty = Double.parseDouble(tokens[5]);

						transform = transform.createConcatenation(Transform.affine(mxx, myx, mxy, myy, tx, ty));

					} catch ( NumberFormatException ex ) {
						LOGGER.warning(MessageFormat.format(
							"''matrix'' transform contains value not parsed to double [{0}].",
							transformTxt
						));
					}
				} else {
					LOGGER.warning(MessageFormat.format(
						"''matrix'' transform doesn't contain the right number of elements [{0}].",
						transformTxt
					));
				}

			}

		}

		return transform;

	}

}
