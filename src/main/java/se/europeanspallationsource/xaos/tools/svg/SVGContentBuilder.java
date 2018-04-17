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
package se.europeanspallationsource.xaos.tools.svg;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.StringUtils;


/**
 * Utility that parses the SVG stream and build a corresponding {@link SVGContent}
 * object.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/skrb/SVGLoader">SVGLoader</a>
 */
class SVGContentBuilder {

	private static final Logger LOGGER = Logger.getLogger(SVGContentBuilder.class.getName());
	private static final String TYPE_KEY = "type";

	private final Map<String, Paint> gradients;
	private final SVGContent root;
	private final InputStream stream;
	private StringBuilder styleBuilder = null;
	private final Map<String, String> stylesMap = new TreeBidiMap<>();
	private final URL url;

	SVGContentBuilder( URL url ) {
		this.url = url;
		this.root = new SVGContent();
		this.gradients = new HashMap<>(1);
		this.stream = null;
	}

	SVGContentBuilder( InputStream stream ) {
		this.url = null;
		this.root = new SVGContent();
		this.gradients = new HashMap<>(1);
		this.stream = stream;
	}

	SVGContent build() throws IOException, XMLStreamException {

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

	private void applyDoubleAttribute (
		String svgAttribute,	//	e.g. "opacity"
		StartElement element,
		List<String> stylesList,
		DoubleConsumer consumer
	) {

		String value = attributeValueFromStyle(svgAttribute, stylesList);
		Attribute attribute = element.getAttributeByName(new QName(svgAttribute));

		if ( attribute != null ) {

			String attributeValue = attribute.getValue();

			if ( StringUtils.isNotBlank(attributeValue) ) {
				value = attributeValue;
			}

		}

		if ( StringUtils.isNotBlank(value) ) {
			try {
				consumer.accept(Double.parseDouble(value));
			} catch ( NumberFormatException nfex ) {
				LOGGER.warning(MessageFormat.format(
					"''{0}'' cannot be parsed to double [{1}, {2}].",
					svgAttribute,
					element.getName().toString(),
					value
				));
			}
		}

	}

	private void applyPaintAttribute (
		String svgAttribute,	//	e.g. "fill"
		StartElement element,
		List<String> stylesList,
		Consumer<Paint> consumer
	) {

		String value = attributeValueFromStyle(svgAttribute, stylesList);
		Attribute attribute = element.getAttributeByName(new QName(svgAttribute));

		if ( attribute != null ) {

			String attributeValue = attribute.getValue();

			if ( StringUtils.isNotBlank(attributeValue) ) {
				value = attributeValue;
			}

		}

		if ( StringUtils.isNotBlank(value) ) {
			consumer.accept(expressPaint(value));
		}

	}

	private void applyStrokeLineCapAttribute (
		StartElement element,
		List<String> stylesList,
		Consumer<StrokeLineCap> consumer
	) {

		String svgAttribute = "stroke-linecap";
		String value = attributeValueFromStyle(svgAttribute, stylesList);
		Attribute attribute = element.getAttributeByName(new QName(svgAttribute));

		if ( attribute != null ) {

			String attributeValue = attribute.getValue();

			if ( StringUtils.isNotBlank(attributeValue) ) {
				value = attributeValue;
			}

		}

		if ( StringUtils.isNotBlank(value) ) {

			StrokeLineCap linecap = StrokeLineCap.BUTT;

			if ( "round".equals(value) ) {
				linecap = StrokeLineCap.ROUND;
			} else if ( "square".equals(value) ) {
				linecap = StrokeLineCap.SQUARE;
			} else if ( !"butt".equals(value) ) {
				LOGGER.warning(MessageFormat.format(
					"Unsupported stroke-linecap [{0}, {1}].",
					element.getName().toString(),
					value
				));
				return;
			}

			consumer.accept(linecap);

		}

	}

	private void applyStrokeLineJoinAttribute (
		StartElement element,
		List<String> stylesList,
		Consumer<StrokeLineJoin> consumer
	) {

		String svgAttribute = "stroke-linejoin";
		String value = attributeValueFromStyle(svgAttribute, stylesList);
		Attribute attribute = element.getAttributeByName(new QName(svgAttribute));

		if ( attribute != null ) {

			String attributeValue = attribute.getValue();

			if ( StringUtils.isNotBlank(attributeValue) ) {
				value = attributeValue;
			}

		}

		if ( StringUtils.isNotBlank(value) ) {

			StrokeLineJoin linejoin = StrokeLineJoin.MITER;

			if ( "bevel".equals(value) ) {
				linejoin = StrokeLineJoin.BEVEL;
			} else if ( "round".equals(value) ) {
				linejoin = StrokeLineJoin.ROUND;
			} else if ( !"miter".equals(value) ) {
				LOGGER.warning(MessageFormat.format(
					"Unsupported stroke-linejoin [{0}, {1}].",
					element.getName().toString(),
					value
				));
				return;
			}

			consumer.accept(linejoin);

		}

	}

	private void applyStyles( Node node, StartElement element ) {

		List<String> stylesList = new ArrayList<>(1);
		Attribute classAttribute = element.getAttributeByName(new QName("class"));

		if ( classAttribute != null ) {

			String[] classes = classAttribute.getValue().split(" ");

			for ( String clazz : classes ) {

				String key = "." + clazz;

				if ( stylesMap.containsKey(key) ) {
					stylesList.add(stylesMap.get(key));
				} else {

					key = node.getProperties().get(TYPE_KEY) + key;

					if ( stylesMap.containsKey(key) ) {
						stylesList.add(stylesMap.get(key));
					} else if ( StringUtils.isNotBlank(node.getId()) ) {

						key = "#" + node.getId();

						if ( stylesMap.containsKey(key) ) {
							stylesList.add(stylesMap.get(key));
						}

					}

				}

			}

		}

		Attribute styleAttribute = element.getAttributeByName(new QName("style"));

		if ( styleAttribute != null ) {

			String style = styleAttribute.getValue();

			if ( StringUtils.isNotBlank(style) ) {
				stylesList.add(style);
			}

		}

		if ( node instanceof Shape ) {

			Shape shape = (Shape) node;

			applyPaintAttribute("fill", element, stylesList, f -> shape.setFill(f));
			applyPaintAttribute("stroke", element, stylesList, s -> shape.setStroke(s));
			applyDoubleAttribute("stroke-width", element, stylesList, w -> shape.setStrokeWidth(w));
			applyDoubleAttribute("stroke-miterlimit", element, stylesList, l -> shape.setStrokeMiterLimit(l));
			applyStrokeLineCapAttribute(element, stylesList, c -> shape.setStrokeLineCap(c));
			applyStrokeLineJoinAttribute(element, stylesList, j -> shape.setStrokeLineJoin(j));

		}

		applyDoubleAttribute("opacity", element, stylesList, o -> node.setOpacity(o));
		applyTransformAttribute(element, stylesList, t -> node.getTransforms().add(t));

	}

	private void applyTransformAttribute (
		StartElement element,
		List<String> stylesList,
		Consumer<Transform> consumer
	) {

		String svgAttribute = "transform";
		String value = attributeValueFromStyle(svgAttribute, stylesList);
		Attribute attribute = element.getAttributeByName(new QName(svgAttribute));

		if ( attribute != null ) {

			String attributeValue = attribute.getValue();

			if ( StringUtils.isNotBlank(attributeValue) ) {
				value = attributeValue;
			}

		}

		if ( StringUtils.isNotBlank(value) ) {
			try {
				consumer.accept(extractTransform(value));
			} catch ( UnsupportedOperationException uoex ) {
				LOGGER.warning(MessageFormat.format(
					"Unsupported ''{0}'' transform [{1}, {2}].",
					uoex.getMessage(),
					element.getName().toString(),
					value
				));
			}
		}

	}

	private String attributeValueFromStyle ( String attribute, List<String> stylesList ) {

		String value = null;

		for ( String style : stylesList ) {

			String[] attributes = style.split(";");

			for ( String attr : attributes ) {

				String[] nameValue = StringUtils.stripAll(attr.split(":"));

				if ( nameValue.length == 2 ) {

					if ( nameValue[0].equals(attribute) ) {
						value = nameValue[1];
						break;
					}

				}

			}

		}

		return value;

	}

	private void build( XMLEventReader reader, Group group ) throws IOException, XMLStreamException {

		while ( reader.hasNext() ) {

			XMLEvent event = reader.nextEvent();

			if ( event.isStartElement() ) {

				StartElement element = (StartElement) event;
				Node node = null;
				String type = element.getName().toString();

//	TODO:CR create a new attributes stack frame a populate it from what read for
//			the current object.

				switch ( type ) {
					case "circle":
						node = buildCircle(element);
						break;
					case "ellipse":
						node = buildEllipse(element);
						break;
					case "g":
					case "svg":
						node = buildGroup(reader);
						break;
					case "image":
						node = buildImage(element);
						break;
					case "line":
						node = buildLine(element);
						break;
					case "linearGradient":
						buildLinearGradient(reader, element);
						break;
					case "path":
						node = buildPath(element);
						break;
					case "polygon":
						node = buildPolygon(element);
						break;
					case "polyline":
						node = buildPolyline(element);
						break;
					case "radialGradient":
						buildRadialGradient(reader, element);
						break;
					case "rect":
						node = buildRect(element);
						break;
					case "style":
						styleBuilder = new StringBuilder(256);
						break;
					case "text":
						node = buildText(reader, element);
						break;
					default:
						LOGGER.warning(MessageFormat.format("Non Support Element: {0}", element));
						break;
				}

				if ( node != null ) {

					Attribute idAttribute = element.getAttributeByName(new QName("id"));

					if ( idAttribute != null ) {

						String id = idAttribute.getValue();

						node.setId(id);
						root.putNode(id, node);

					}
					
					node.getProperties().put(TYPE_KEY, type);
//	TODO:CR applyStyles remove an attributes stack frame from the stack and uses
//			it to set the values.
					applyStyles(node, element);
					group.getChildren().add(node);

				}

			} else if ( event.isEndElement() ) {

				EndElement element = (EndElement) event;

				switch ( element.getName().toString() ) {
					case "g":
					case "svg":
						return;
					case "style":
						parseStyleElement(styleBuilder.toString());
						styleBuilder = null;
						break;
					default:
						break;
				}

			} else if ( event.isCharacters() ) {
				if ( styleBuilder != null ) {
					styleBuilder.append(((Characters) event).getData());
				}
			}

		}

	}

	private Shape buildCircle( StartElement element ) {

		Attribute cxAttribute = element.getAttributeByName(new QName("cx"));
		Attribute cyAttribute = element.getAttributeByName(new QName("cy"));
		Attribute rAttribute = element.getAttributeByName(new QName("r"));

		try {
			return new Circle(
				( cxAttribute != null ) ? Double.parseDouble(cxAttribute.getValue()) : 0.0,
				( cyAttribute != null ) ? Double.parseDouble(cyAttribute.getValue()) : 0.0,
				( rAttribute  != null ) ? Double.parseDouble(rAttribute.getValue())  : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A circle's attribute cannot be parsed to double: cx [{0}], cy [{1}], r [{2}].",
				( cxAttribute != null ) ? cxAttribute.getValue() : "-",
				( cyAttribute != null ) ? cyAttribute.getValue() : "-",
				( rAttribute  != null ) ? rAttribute.getValue()  : "-"
			));
			return null;
		}

	}

	private Shape buildEllipse( StartElement element ) {

		Attribute cxAttribute = element.getAttributeByName(new QName("cx"));
		Attribute cyAttribute = element.getAttributeByName(new QName("cy"));
		Attribute rxAttribute = element.getAttributeByName(new QName("rx"));
		Attribute ryAttribute = element.getAttributeByName(new QName("ry"));

		try {
			return new Ellipse(
				( cxAttribute != null ) ? Double.parseDouble(cxAttribute.getValue()) : 0.0,
				( cyAttribute != null ) ? Double.parseDouble(cyAttribute.getValue()) : 0.0,
				( rxAttribute != null && !"auto".equals(rxAttribute.getValue()) ) ? Double.parseDouble(rxAttribute.getValue()) : 0.0,
				( ryAttribute != null && !"auto".equals(ryAttribute.getValue()) ) ? Double.parseDouble(ryAttribute.getValue()) : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"An ellipse's attribute cannot be parsed to double: cx [{0}], cy [{1}], rx [{2}], ry [{3}].",
				( cxAttribute != null ) ? cxAttribute.getValue() : "-",
				( cyAttribute != null ) ? cyAttribute.getValue() : "-",
				( rxAttribute != null ) ? rxAttribute.getValue() : "-",
				( ryAttribute != null ) ? ryAttribute.getValue() : "-"
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

		Attribute hrefAttribute = element.getAttributeByName(new QName("href"));

		if ( hrefAttribute != null ) {

			URL imageUrl = null;

			try {
				imageUrl = new URL(hrefAttribute.getValue());
			} catch ( MalformedURLException ex1 ) {
				try {
					imageUrl = new URL(url, hrefAttribute.getValue());
				} catch ( MalformedURLException ex2 ) {
					LOGGER.warning(MessageFormat.format(
						"Image's href attribute is not a valid URL [{0}].",
						hrefAttribute.getValue()
					));
				}
			}

			if ( imageUrl != null ) {

				Attribute xAttribute = element.getAttributeByName(new QName("x"));
				Attribute yAttribute = element.getAttributeByName(new QName("y"));
				Attribute widthAttribute = element.getAttributeByName(new QName("width"));
				Attribute heightAttribute = element.getAttributeByName(new QName("height"));

				try {

					Image image = new Image(
						imageUrl.toString(),
						( widthAttribute  != null ) ? Double.parseDouble(widthAttribute.getValue())  : 0.0,
						( heightAttribute != null ) ? Double.parseDouble(heightAttribute.getValue()) : 0.0,
						true,
						true
					);

					ImageView imageView = new ImageView(image);

					imageView.setLayoutX(( xAttribute != null ) ? Double.parseDouble(xAttribute.getValue()) : 0.0);
					imageView.setLayoutY(( yAttribute != null ) ? Double.parseDouble(yAttribute.getValue()) : 0.0);

					return imageView;

				} catch ( NumberFormatException ex ) {
					LOGGER.warning(MessageFormat.format(
						"An image's attribute cannot be parsed to double: x [{0}], y [{1}], width [{2}], height [{3}].",
						( xAttribute      != null ) ? xAttribute.getValue()      : "-",
						( yAttribute      != null ) ? yAttribute.getValue()      : "-",
						( widthAttribute  != null ) ? widthAttribute.getValue()  : "-",
						( heightAttribute != null ) ? heightAttribute.getValue() : "-"
					));
				}
			}

		} else {
			LOGGER.warning("An image's attribute is null: href.");
		}

		return null;

	}

	private Shape buildLine( StartElement element ) {

		Attribute x1Attribute = element.getAttributeByName(new QName("x1"));
		Attribute y1Attribute = element.getAttributeByName(new QName("y1"));
		Attribute x2Attribute = element.getAttributeByName(new QName("x2"));
		Attribute y2Attribute = element.getAttributeByName(new QName("y2"));

		try {
			return new Line(
				( x1Attribute != null ) ? Double.parseDouble(x1Attribute.getValue()) : 0.0,
				( y1Attribute != null ) ? Double.parseDouble(y1Attribute.getValue()) : 0.0,
				( x2Attribute != null ) ? Double.parseDouble(x2Attribute.getValue()) : 0.0,
				( y2Attribute != null ) ? Double.parseDouble(y2Attribute.getValue()) : 0.0
			);
		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A line's attribute cannot be parsed to double: x1 [{0}], y1 [{1}], x2 [{2}], y2 [{3}].",
				( x1Attribute != null ) ? x1Attribute.getValue() : "-",
				( y1Attribute != null ) ? y1Attribute.getValue() : "-",
				( x2Attribute != null ) ? x2Attribute.getValue() : "-",
				( y2Attribute != null ) ? y2Attribute.getValue() : "-"
			));
		}

		return null;

	}

	private void buildLinearGradient( XMLEventReader reader, StartElement element ) throws IOException, XMLStreamException {

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
					transform = extractTransform(attribute.getValue());
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

		Attribute dAttribute = element.getAttributeByName(new QName("d"));

		if ( dAttribute != null ) {

			SVGPath path = new SVGPath();

			path.setContent(dAttribute.getValue());

			return path;

		} else {
			LOGGER.warning("A path's attribute is null: d.");
		}

		return null;

	}

	private Shape buildPolygon( StartElement element ) {

		Attribute pointsAttribute = element.getAttributeByName(new QName("points"));

		if ( pointsAttribute != null ) {
			try {

				Polygon polygon = new Polygon();
				StringTokenizer tokenizer = new StringTokenizer(pointsAttribute.getValue(), " ");

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
					pointsAttribute.getValue()
				));
			}

		} else {
			LOGGER.warning("A polygon's attribute is null: points.");
		}

		return null;

	}

	private Shape buildPolyline( StartElement element ) {

		Attribute pointsAttribute = element.getAttributeByName(new QName("points"));

		if ( pointsAttribute != null ) {
			try {

				Polyline polyline = new Polyline();
				StringTokenizer tokenizer = new StringTokenizer(pointsAttribute.getValue(), " ");

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
					pointsAttribute.getValue()
				));
			}

		} else {
			LOGGER.warning("A polyline's attribute is null: points.");
		}

		return null;

	}

	private void buildRadialGradient( XMLEventReader reader, StartElement element ) throws IOException, XMLStreamException {

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
					transform = extractTransform(attribute.getValue());
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

		Attribute xAttribute = element.getAttributeByName(new QName("x"));
		Attribute yAttribute = element.getAttributeByName(new QName("y"));
		Attribute widthAttribute = element.getAttributeByName(new QName("width"));
		Attribute heightAttribute = element.getAttributeByName(new QName("height"));
		Attribute rxAttribute = element.getAttributeByName(new QName("rx"));
		Attribute ryAttribute = element.getAttributeByName(new QName("ry"));

		try {

			Rectangle rectangle = new Rectangle(
				( xAttribute      != null ) ? Double.parseDouble(xAttribute.getValue())      : 0.0,
				( yAttribute      != null ) ? Double.parseDouble(yAttribute.getValue())      : 0.0,
				( widthAttribute  != null ) ? Double.parseDouble(widthAttribute.getValue())  : 0.0,
				( heightAttribute != null ) ? Double.parseDouble(heightAttribute.getValue()) : 0.0
			);

			rectangle.setArcWidth(
				( rxAttribute != null && !"auto".equals(rxAttribute.getValue()) ) ? Double.parseDouble(rxAttribute.getValue()) : 0.0
			);
			rectangle.setArcHeight(
				( ryAttribute != null && !"auto".equals(ryAttribute.getValue()) ) ? Double.parseDouble(ryAttribute.getValue()) : 0.0
			);

			return rectangle;

		} catch ( NumberFormatException ex ) {
			LOGGER.warning(MessageFormat.format(
				"A rect's attribute cannot be parsed to double: x [{0}], y [{1}], width [{2}], height [{3}], rx [{4}], ry [{5}].",
				( xAttribute      != null ) ? xAttribute.getValue()      : "-",
				( yAttribute      != null ) ? yAttribute.getValue()      : "-",
				( widthAttribute  != null ) ? widthAttribute.getValue()  : "-",
				( heightAttribute != null ) ? heightAttribute.getValue() : "-",
				( rxAttribute     != null ) ? rxAttribute.getValue()     : "-",
				( ryAttribute     != null ) ? ryAttribute.getValue()     : "-"
			));
			return null;
		}

	}

	private List<Stop> buildStops( XMLEventReader reader, String kindOfGradient ) throws XMLStreamException {

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

	private Shape buildText( XMLEventReader reader, StartElement element ) throws XMLStreamException {

		Font font = null;
		Attribute fontFamilyAttribute = element.getAttributeByName(new QName("font-family"));
		Attribute fontSizeAttribute = element.getAttributeByName(new QName("font-size"));

		if ( fontFamilyAttribute != null && fontSizeAttribute != null ) {
			try {
				font = Font.font(
					fontFamilyAttribute.getValue().replace("'", ""),
					Double.parseDouble(fontSizeAttribute.getValue())
				);
			} catch ( NumberFormatException ex ) {
				LOGGER.warning(MessageFormat.format(
					"A text's attribute cannot be parsed to double: font-size [{0}].",
					fontSizeAttribute.getValue()
				));
			}
		}

		XMLEvent event = reader.nextEvent();

		if ( event.isCharacters() ) {

			Text text = new Text(((Characters) event).getData());

			if ( font != null ) {
				text.setFont(font);
			}

			return text;

		} else {
			throw new XMLStreamException("Illegal Element: " + event);
		}

	}

	private Paint expressPaint( String value ) {

		Paint paint = null;

		if ( !"none".equals(value) ) {
			if ( value.startsWith("url(#") ) {
				paint = gradients.get(value.substring(5, value.length() - 1));
			} else {
				paint = Color.web(value);
			}
		}

		return paint;

	}

	private Transform extractTransform( String transforms ) {

		Transform transform = null;
		StringTokenizer tokenizer = new StringTokenizer(transforms, ")");

		while ( tokenizer.hasMoreTokens() ) {

			String transformTxt = tokenizer.nextToken();

//	TODO:CR Implements other types ot transformations.
			if ( transformTxt.startsWith("translate(") ) {
				throw new UnsupportedOperationException("transform:translate");
			} else if ( transformTxt.startsWith("scale(") ) {
				throw new UnsupportedOperationException("transform:scale");
			} else if ( transformTxt.startsWith("rotate(") ) {
				throw new UnsupportedOperationException("transform:rotate");
			} else if ( transformTxt.startsWith("skewX(") ) {
				throw new UnsupportedOperationException("transform:skewX");
			} else if ( transformTxt.startsWith("skewY(") ) {
				throw new UnsupportedOperationException("transform:skewY");
			} else if ( transformTxt.startsWith("matrix(") ) {

				transformTxt = transformTxt.substring(7);

				StringTokenizer tokenizer2 = new StringTokenizer(transformTxt, " ");

				try {

					double mxx = Double.parseDouble(tokenizer2.nextToken());
					double myx = Double.parseDouble(tokenizer2.nextToken());
					double mxy = Double.parseDouble(tokenizer2.nextToken());
					double myy = Double.parseDouble(tokenizer2.nextToken());
					double tx = Double.parseDouble(tokenizer2.nextToken());
					double ty = Double.parseDouble(tokenizer2.nextToken());

					transform = Transform.affine(mxx, myx, mxy, myy, tx, ty);

				} catch ( NumberFormatException ex ) {
					LOGGER.warning(MessageFormat.format(
						"Matrix transform contains value not parsed to double [{0}].",
						transformTxt
					));
				}

			}
		}

		return transform;

	}

	private void parseStyleElement( String styleElement ) {

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
				stylesMap.put(nameValues[0], nameValues[1]);
			}

		}

	}

}
