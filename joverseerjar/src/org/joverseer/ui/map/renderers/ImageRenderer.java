package org.joverseer.ui.map.renderers;

import org.springframework.richclient.image.ImageSource;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.awt.image.*;
import java.awt.*;

/**
 * Base class for renderers that use images 
 * Provides utility methods
 * 
 * @author Marios Skounakis
 */
public abstract class ImageRenderer extends AbstractBaseRenderer {
    protected HashMap images = new HashMap();

    static Logger logger = Logger.getLogger(ImageRenderer.class);

    // injected dependencies
    ImageSource imgSource;

	public ImageSource getImgSource() {
		return this.imgSource;
	}

	public void setImgSource(ImageSource imgSource) {
		this.imgSource = imgSource;
	}

	public BufferedImage copyImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(),
                image.getHeight(),
                image.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    /**
     * Gets the image with the given name from the image source
     * Provides caching for speed
     */
    protected BufferedImage getImage(String imgName) {
        if (!this.images.containsKey(imgName)) {
            try {
//                ImageSource imgSource = joApplication.getImageSource();
                Image img = this.imgSource.getImage(imgName);
                BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bimg.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                //img = makeColorTransparent(img, Color.white);
                this.images.put(imgName, bimg);
                return bimg;
            }
            catch (Exception exc) {
                logger.error(String.format("Error %s loading image %s.", exc.getMessage(), imgName));
            }
        }
        return (BufferedImage) this.images.get(imgName);
    }
    
    /**
     * Gets the image with the given name from the image source, and scales it to the given dimension
     * Provides caching for speed
     */
    protected BufferedImage getImage(String imgName, int desiredWidth, int desiredHeight) {
        if (!this.images.containsKey(imgName)) {
            try {
//                ImageSource imgSource = joApplication.getImageSource();
                Image img = this.imgSource.getImage(imgName);
                
               
                BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bimg.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                
                int w = desiredWidth;
                int h = desiredHeight;
                BufferedImage bufimg2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) bufimg2.getGraphics();
                
                double sw = (double) w / bimg.getWidth();
                double sh = (double) h / bimg.getHeight();
                if (sw < 1) {
                	sw += 1d / bimg.getWidth();
                }
                if (sh < 1) {
                	sh += 1d / bimg.getHeight();	
                }
                g2d.scale(sw, sh);
                g2d.drawImage(img,0,0,null);
                bimg = bufimg2; 
                
                //img = makeColorTransparent(img, Color.white);
                this.images.put(imgName, bimg);
                return bimg;
            }
            catch (Exception exc) {
                logger.error(String.format("Error %s loading image %s.", exc.getMessage(), imgName));
            }
        }
        return (BufferedImage) this.images.get(imgName);
    }
    
    protected BufferedImage getImage(String imgName, double d) {
        if (!this.images.containsKey(imgName)) {
            try {
//                ImageSource imgSource = joApplication.getImageSource();
                Image img = this.imgSource.getImage(imgName);
                
               
                BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bimg.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                
                int w = (int) (img.getWidth(null) * d);
                int h = (int) (img.getHeight(null) * d);
                BufferedImage bufimg2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) bufimg2.getGraphics();
                
                double sw = (double) w / bimg.getWidth();
                double sh = (double) h / bimg.getHeight();
                if (sw < 1) {
                	sw += 1d / bimg.getWidth();
                }
                if (sh < 1) {
                	sh += 1d / bimg.getHeight();	
                }
                g2d.scale(sw, sh);
                g2d.drawImage(img,0,0,null);
                bimg = bufimg2; 
                
                //img = makeColorTransparent(img, Color.white);
                this.images.put(imgName, bimg);
                return bimg;
            }
            catch (Exception exc) {
                logger.error(String.format("Error %s loading image %s.", exc.getMessage(), imgName));
            }
        }
        return (BufferedImage) this.images.get(imgName);
    }

    /**
     * Make the given color transparent to the given image
     */
    public static Image makeColorTransparent(Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
			public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == this.markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    /**
     * Replace one color with the other
     */
    protected void changeColor(BufferedImage src, Color remove, Color replace) {
        int w = src.getWidth();
        int h = src.getHeight();
        int rgbRemove = remove.getRGB();
        int rgbReplace = replace.getRGB();
        // Copy pixels a scan line at a time
        int buf[] = new int[w];
        for (int y = 0; y < h; y++) {
            src.getRGB(0, y, w, 1, buf, 0, w);
            for (int x = 0; x < w; x++) {
                if (buf[x] == rgbRemove) {
                    buf[x] = rgbReplace;
                }
            }
            src.setRGB(0, y, w, 1, buf, 0, w);
        }
    }

    /**
     * Used to generate the effect of dashed lines to designate hidden pop centers
     */
    protected void makeHidden(BufferedImage src, Color remove, Color replace) {
        int w = src.getWidth();
        int h = src.getHeight();
        int rgbRemove = remove.getRGB();
        int rgbReplace = replace.getRGB();
        // Copy pixels a scan line at a time
        int buf[] = new int[w];
        for (int y = 0; y < h; y++) {
            src.getRGB(0, y, w, 1, buf, 0, w);
            for (int x = 0; x < w; x++) {
                if ((x + y) % 3 == 0 && buf[x] == rgbRemove) {
                    buf[x] = rgbReplace;
                }
            }
            src.setRGB(0, y, w, 1, buf, 0, w);
        }
    }

	@Override
	public void refreshConfig() {
		// TODO Auto-generated method stub
		
	}
}
