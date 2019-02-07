/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package util;

/**
 * To be removed.
 * 
 * @author Grzegorz Kruk
 */
   
    
import java.io.InputStream;
 
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
 
public class Utils {
    
    /**
     * Calls {@link Thread#sleep(long)}
     * @param millis number of milliseconds to sleep
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //
        }
    }
 
    public static void clearClipboard() {

            final Clipboard clipboard = Clipboard.getSystemClipboard();

            clipboard.clear();

    }
 
//To copy 
 
    public static void copyToClipboardText(String s) {

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            content.putString(s);
            clipboard.clear();
            clipboard.setContent(content);

    }

    public static void copyToClipboardImage(Label lbl) {

            WritableImage snapshot = lbl.snapshot(new SnapshotParameters(), null);
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            content.putImage(snapshot);
            clipboard.setContent(content);

    }

    public static void copyToClipboardImageFromFile(String path) {

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            content.putImage(Utils.getImage(path));
            clipboard.setContent(content);

    }

    public static Image getImage(String path) {

            InputStream is = Utils.class.getResourceAsStream(path);
            return new Image(is);
    }


    public static ImageView setIcon(String path) {

            InputStream is = Utils.class.getResourceAsStream(path);
            ImageView iv = new ImageView(new Image(is));

            iv.setFitWidth(100);
            iv.setFitHeight(100);
            return iv;
    }
 
	
	
}
   
