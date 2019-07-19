/*
 * 
 * Copyright 2008-2011 Eduardo Vindas C
 * 
 */
/*
 *Created on Oct 9, 2010, 10:23:57 PM(when the magic begins ;) )
 */
package com.aeongames.edi.utils.visual;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import javax.swing.ImageIcon;

/**
 *
 * @author Eduardo Vindas Cordoba <cartman@aeongames.com>
 */
public class ImageUtils {

    /**
     * we want to show a image but we want to keep the ratio, so lets made a image using this approach
     * what it is done is we take the smallest parameter as we want a image that is able to be display
     * within the parameter size we assume the image will be contained within a "box"
     * also we will calculate the position there we will place the image
     * @param width the width of the image container or the context where will be paced
     * @param height the height of the image container or the context where will be paced
     * @param the image that we require to calculate the the ratio for
     * @return a array of integers with the following values 0= width to set 1=height to set
     * 2=the width where the image required to be place 3=the height within the image will be set.
     */
    public static int[] keep_ratio_for_size(int width, int height, Image to_resize) {
        int wtouse, htouse;
        double scale=determineImageScale(to_resize.getWidth(null),to_resize.getHeight(null),width,height);
        wtouse=(int) (to_resize.getWidth(null) * scale);
        if(wtouse<1){
           wtouse=1;
        }
        htouse=(int) (to_resize.getHeight(null) * scale);
        if(htouse<1){
           htouse=1;
        }
        int placew,placeh;
        placew=width/2-wtouse/2;
        placeh=height/2-htouse/2;
        return new int[]{wtouse,htouse,placew,placeh};
    }

    /**
     * determine scale of the image
     * returns the smallest of the scales
     *
     */
    private static double determineImageScale(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {

        double scalex = (double) targetWidth / sourceWidth;
        double scaley = (double) targetHeight / sourceHeight;
        return Math.min(scalex, scaley);
    }
    
    /**
     * converts a Image into a Buffered image. 
     * if the image is already a buffer image returns the parameters (does not create a copy!)
     * @param image the image to transform to buffered image 
     * @return the same image if is instance of BufferedImage or a new BufferedImage with the same content as the parameter
     */
        public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        } else {
            // This code ensures that all the pixels in the image are loaded
            image = new ImageIcon(image).getImage();
            // Determine if the image has transparent pixels; for this method's
            // implementation, see Determining If an Image Has Transparent Pixels
            boolean hasAlpha = hasAlpha(image);

            // Create a buffered image with a format that's compatible with the screen
            BufferedImage bimage = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                // Determine the type of transparency of the new buffered image
                int transparency = Transparency.OPAQUE;
                if (hasAlpha) {
                    transparency = Transparency.BITMASK;
                }

                // Create the buffered image
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gs.getDefaultConfiguration();
                bimage = gc.createCompatibleImage(
                        image.getWidth(null), image.getHeight(null), transparency);
                gs = null;
                gc = null;
            } catch (HeadlessException e) {
                // The system does not have a screen
            }

            if (bimage == null) {
                // Create a buffered image using the default color model
                int type = BufferedImage.TYPE_INT_RGB;
                if (hasAlpha) {
                    type = BufferedImage.TYPE_INT_ARGB;
                }
                bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            }

            // Copy image to buffered image
            Graphics g = bimage.getGraphics();

            // Paint the image onto the buffered image
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return bimage;
        }
    }

    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        if (pg != null) {
            ColorModel cm = pg.getColorModel();
            if (cm != null) {
                return cm.hasAlpha();
            }
        }
        return false;
    }
}
