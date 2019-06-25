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
package se.europeanspallationsource.xaos.ui.plot;


import java.util.HashMap;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.chart.Axis;


/**
 * A one-to-one replacement of the no more available
 * {@code com.sun.javafx.charts.ChartLayoutAnimator} class.
 * <p>
 * It runs any number of animations of {@link KeyFrame}s calling
 * {@link Parent#requestLayout()} on the given parent node for every frame while
 * one of those animations is running.</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ChartLayoutAnimator extends AnimationTimer implements EventHandler<ActionEvent> {

	private final Map<Object, Animation> activeTimeLines = new HashMap<>(8);
	private final boolean isAxis;
	private final Parent nodeToLayout;

	public ChartLayoutAnimator( Parent nodeToLayout ) {
		this.nodeToLayout = nodeToLayout;
		this.isAxis = ( nodeToLayout instanceof Axis );
	}

	/**
	 * Play an animation containing the given {@link KeyFrame}s.
	 *
	 * @param keyFrames The {@link KeyFrame}s to animate.
	 * @return An id reference to the animation that can be used to stop the
	 *         animation if needed.
	 */
	public Object animate( KeyFrame... keyFrames ) {

		Timeline t = new Timeline();

		t.setAutoReverse(false);
		t.setCycleCount(1);
		t.getKeyFrames().addAll(keyFrames);
		t.setOnFinished(this);

		//	Start animation timer if needed.
		if ( activeTimeLines.isEmpty() ) {
			start();
		}

		//	Get id and add to map.
		activeTimeLines.put(t, t);

		//	Play animation.
		t.play();

		return t;

	}

	/**
	 * Play the given {@Link Animation}.
	 *
	 * @param animation The animation to play.
	 * @return An id reference to the animation that can be used to stop the
	 *         animation if needed.
	 */
	public Object animate( Animation animation ) {

		SequentialTransition t = new SequentialTransition();

		t.getChildren().add(animation);
		t.setOnFinished(this);

		//	Start animation timer if needed.
		if ( activeTimeLines.isEmpty() ) {
			start();
		}

		//	Get id and add to map.
		activeTimeLines.put(t, t);

		//	Play animation.
		t.play();
		
		return t;

	}

	@Override public void handle( long l ) {
		if ( isAxis ) {
			( (Axis<?>) nodeToLayout ).requestAxisLayout();
		} else {
			nodeToLayout.requestLayout();
		}
	}

	@Override public void handle( ActionEvent actionEvent ) {

		activeTimeLines.remove(actionEvent.getSource());

		if ( activeTimeLines.isEmpty() ) {
			stop();
		}

		//	Cause one last re-layout to make sure final values were used.
		handle(0l);
		
	}

	/**
	 * Stop the animation with the given ID.
	 *
	 * @param animationID The identifier of the animation to stop.
	 */
	public void stop( Object animationID ) {

		Animation t = activeTimeLines.remove(animationID);

		if ( t != null ) {
			t.stop();
		}

		if ( activeTimeLines.isEmpty() ) {
			stop();
		}

	}

}
