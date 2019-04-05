/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package plugins;


import chart.XYChartPlugin;
import java.util.ArrayDeque;
import java.util.Deque;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.Chart;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import se.europeanspallationsource.xaos.ui.control.NavigatorPopup;


/**
 * Opens a {@link NavigatorPopup} when ALT key is pressed within the chart.
 * <p>The popup can be opened pressing ALT on a focused chart. If the popup
 * is still opened, pressing ALT again will move it under the current mouse
 * cursor position. The popup can be closed pressing ESC or clicking outside
 * it.</p>
 *
 * @author Reuben Lindroos
 * @author claudio.rosati@esss.se
 */
public final class CursorTool extends XYChartPlugin {

	private double cursorScreenX;
	private double cursorScreenY;
	private final EventHandler<KeyEvent> keyPressedHandler = this::keyPressed;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMovedHandler = this::mouseMoved;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;
	private final NavigatorPopup popup = new NavigatorPopup();

	public CursorTool() {
		popup.setOnPanDown(e -> panDown());

	}

	@Override
	protected void chartConnected( Chart newChart ) {
		//	Using event filters instead of event handlers to capture the event
		//	earlier and block other plugins using it.
		newChart.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, mouseEnteredHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitedHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
	}

	@Override
	protected void chartDisconnected( Chart oldChart ) {
		oldChart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, mouseEnteredHandler);
		oldChart.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
	}

	private void keyPressed ( KeyEvent event ) {
		switch ( event.getCode() ) {
			case ALT:
				//	If the popup is hidden then show it, otherwise move it at
				//	the current mouse cursor position.
				popup.show(getChart(), cursorScreenX, cursorScreenY);
				event.consume();
				break;
			case DOWN:
			case LEFT:
			case RIGHT:
			case UP:
				//	Blobk all other plugins using those keys. when the popup is
				//	visible.
				if ( popup.isShowing() ) {
					event.consume();
				}
				break;
		}
	}

	private void mouseEntered( MouseEvent event ) {
		getChart().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
	}

	private void mouseExited( MouseEvent event ) {
		getChart().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
	}

	private void mouseMoved( MouseEvent event ) {
		cursorScreenX = event.getScreenX();
		cursorScreenY = event.getScreenY();
	}

	private void mouseReleased( MouseEvent event ) {
		if ( popup.isShowing() ) {
			popup.hide();
		}
	}

	private void panDown() {

//		this.setChart(newChart);
//		if ( viewReset ) {
//			resetPlotSize();
//			viewReset = false;
//		};

		double plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();

		getYValueAxis().setAutoRanging(false);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1 * plotHeight);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1 * plotHeight);

	}














	private static double plotHeightHH;
	private static double plotWidthWW;

//	private boolean altPressed = false;
//	private Circle bigCircle = new Circle();
//	// Makes the object dissappear when key is released by removing the object
//	// from the plugin list
//	private final EventHandler<KeyEvent> keyReleaseHandler = ( KeyEvent ke ) -> {
//		altPressed = false;
//		getPlotChildren().removeAll(tool.getImages());
//	};
//	private final EventHandler<MouseEvent> mousePressHandler = ( MouseEvent event ) -> {
//		Point2D mouseLocation = getLocationInPlotArea(event);
//
//		if ( getChart() instanceof DensityChartFX<?, ?> ) {
//			mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(), 0.0));
//		}
//
//		if ( smallCircle.contains(mouseLocation) ) {
//			autoScale(getChart());
//			viewReset = true;
//			event.consume();
//		} else if ( bigCircle.contains(mouseLocation) ) {
//			Double yVal = mouseLocation.getY() - bigCircle.getCenterY();
//			Double xVal = mouseLocation.getX() - bigCircle.getCenterX();
//
//			if ( Math.atan(Math.abs(yVal) / xVal) < Math.PI / 4 && Math.atan(Math.abs(yVal) / xVal) > 0 ) {
//				panRight(getChart());
//			} else if ( Math.atan(Math.abs(yVal) / xVal) > -Math.PI / 4 && Math.atan(Math.abs(yVal) / xVal) < 0 ) {
//				panLeft(getChart());
//			} else if ( yVal < 0 ) {
//				panUp(getChart());
//			} else if ( yVal > 0 ) {
//				panDown(getChart());
//			}
//			event.consume();
//		} else if ( outerRectangle.contains(mouseLocation) ) {
//			viewReset = true;
//			if ( mouseLocation.getX() < bigCircle.getCenterX() && mouseLocation.getY() < bigCircle.getCenterY() ) {
//
//			}
//			if ( mouseLocation.getX() > bigCircle.getCenterX() && mouseLocation.getY() < bigCircle.getCenterY() ) {
//				zoomIn(getChart());
//			}
//			if ( mouseLocation.getX() > bigCircle.getCenterX() && mouseLocation.getY() > bigCircle.getCenterY() ) {
//				zoomOut(getChart());
//			}
//			if ( mouseLocation.getX() < bigCircle.getCenterX() && mouseLocation.getY() > bigCircle.getCenterY() ) {
//
//			}
//			event.consume();
//		}
//
//	};
//	private Rectangle outerRectangle = new Rectangle();
//	private Circle smallCircle = new Circle();
	private Point2D toolCenter = null;
	private final double toolSize = 125.0;
	private final Deque<Rectangle2D> zoomStack = new ArrayDeque<>();

	private void autoScale( Chart newChart ) {
		this.setChart(newChart);
		getXValueAxis().setAutoRanging(true);
		getYValueAxis().setAutoRanging(true);
	}

	private void panLeft( Chart newChart ) {
		this.setChart(newChart);
		if ( viewReset ) {
			resetPlotSize();
			viewReset = false;
		};
		getXValueAxis().setAutoRanging(false);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1 * plotWidthWW);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1 * plotWidthWW);
	}

	private void panRight( Chart newChart ) {
		this.setChart(newChart);
		if ( viewReset ) {
			resetPlotSize();
			viewReset = false;
		};
		getXValueAxis().setAutoRanging(false);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1 * plotWidthWW);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1 * plotWidthWW);
	}

	private void panUp( Chart newChart ) {
		this.setChart(newChart);
		if ( viewReset ) {
			resetPlotSize();
			viewReset = false;
		}
		getYValueAxis().setAutoRanging(false);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1 * plotHeightHH);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1 * plotHeightHH);

	}

	private void zoomIn( Chart newChart ) {
		this.setChart(newChart);
		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		this.setChart(newChart);
		if ( viewReset ) {
			resetPlotSize();
			viewReset = false;
		};
		zoomStack.addFirst(new Rectangle2D(getXValueAxis().getLowerBound(), getYValueAxis().getLowerBound(), getXValueAxis().getUpperBound()
			- getXValueAxis().getLowerBound(), getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound()));
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1 * plotWidthWW);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1 * plotWidthWW);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1 * plotHeightHH);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1 * plotHeightHH);
	}

	private void zoomOut( Chart newChart ) {
		this.setChart(newChart);
		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		this.setChart(newChart);
		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		this.setChart(newChart);
		if ( viewReset ) {
			resetPlotSize();
			viewReset = false;
		};
		zoomStack.addFirst(new Rectangle2D(getXValueAxis().getLowerBound(), getYValueAxis().getLowerBound(), getXValueAxis().getUpperBound()
			- getXValueAxis().getLowerBound(), getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound()));
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1 * plotWidthWW);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1 * plotWidthWW);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1 * plotHeightHH);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1 * plotHeightHH);
	}

	private void resetPlotSize() {
		plotHeightHH = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
		plotWidthWW = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();
	}

//	private void updateCursorTool( MouseEvent event ) {
//		Point2D mouseLocation = getLocationInPlotArea(event);
//
//		if ( getChart() instanceof DensityChartFX<?, ?> ) {
//			mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(), 0.0));
//		}
//
//		if ( smallCircle.contains(mouseLocation) ) {
//			tool = new iconParser();
//			tool.setLocation(new Point2D(smallCircle.getCenterX(), smallCircle.getCenterY()));
//			tool.setChartForIcon(getChart());
//			tool.updateIcon(true, false, false, 0, smallCircle.getCenterX(), smallCircle.getCenterY());
//			event.consume();
//		} else if ( bigCircle.contains(mouseLocation) ) {
//			Double yVal = mouseLocation.getY() - bigCircle.getCenterY();
//			Double xVal = mouseLocation.getX() - bigCircle.getCenterX();
//
//			if ( Math.atan(Math.abs(yVal) / xVal) < Math.PI / 4 && Math.atan(Math.abs(yVal) / xVal) > 0 ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.setChartForIcon(getChart());
//				tool.updateIcon(false, true, false, 3, bigCircle.getCenterX(), bigCircle.getCenterY());
//			} else if ( Math.atan(Math.abs(yVal) / xVal) > -Math.PI / 4 && Math.atan(Math.abs(yVal) / xVal) < 0 ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.setChartForIcon(getChart());
//				tool.updateIcon(false, true, false, 1, bigCircle.getCenterX(), bigCircle.getCenterY());
//			} else if ( yVal < 0 ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.setChartForIcon(getChart());
//				tool.updateIcon(false, true, false, 2, bigCircle.getCenterX(), bigCircle.getCenterY());
//			} else if ( yVal > 0 ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.setChartForIcon(getChart());
//				tool.updateIcon(false, true, false, 4, bigCircle.getCenterX(), bigCircle.getCenterY());
//			}
//			event.consume();
//		} else if ( outerRectangle.contains(mouseLocation) ) {
//			if ( mouseLocation.getX() < bigCircle.getCenterX() && mouseLocation.getY() < bigCircle.getCenterY() ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.updateIcon(false, false, true, 1, bigCircle.getCenterX(), bigCircle.getCenterY());
//			}
//			if ( mouseLocation.getX() > bigCircle.getCenterX() && mouseLocation.getY() < bigCircle.getCenterY() ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.updateIcon(false, false, true, 2, bigCircle.getCenterX(), bigCircle.getCenterY());
//			}
//			if ( mouseLocation.getX() > bigCircle.getCenterX() && mouseLocation.getY() > bigCircle.getCenterY() ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.updateIcon(false, false, true, 3, bigCircle.getCenterX(), bigCircle.getCenterY());
//			}
//			if ( mouseLocation.getX() < bigCircle.getCenterX() && mouseLocation.getY() > bigCircle.getCenterY() ) {
//				tool = new iconParser();
//				tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//				tool.updateIcon(false, false, true, 4, bigCircle.getCenterX(), bigCircle.getCenterY());
//			}
//			event.consume();
//
//		} else {
//			tool = new iconParser();
//			tool.setLocation(new Point2D(bigCircle.getCenterX(), bigCircle.getCenterY()));
//		}
//
//	}

}
