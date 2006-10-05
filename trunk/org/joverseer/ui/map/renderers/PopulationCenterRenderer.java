package org.joverseer.ui.map.renderers;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.ui.map.MapMetadata;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.application.Application;

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 8:02:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenterRenderer implements Renderer {
    HashMap images = new HashMap();
    MapMetadata mapMetadata = null;

    public boolean appliesTo(Object obj) {
        return PopulationCenter.class.isInstance(obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }



    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        PopulationCenter popCenter = (PopulationCenter)obj;

        BufferedImage fortImage = null;
        if (popCenter.getFortification() != FortificationSizeEnum.none) {
            fortImage = getImage(popCenter.getFortification());
        }

        BufferedImage pcImage = getImage(popCenter.getSize());

        Point hexCenter = new Point(x + mapMetadata.getHexSize() / 2 * mapMetadata.getGridCellWidth(),
                                    y + mapMetadata.getHexSize() / 2 * mapMetadata.getGridCellHeight());


        BufferedImage img = copyImage(pcImage);

        //changeColor(img, Color.black, Color.green);
        //changeColor(img, Color.red, Color.black);
        //makeHidden(img, Color.black, Color.green);

        if (fortImage != null) {
            g.drawImage(fortImage, hexCenter.x - fortImage.getWidth() / 2, hexCenter.y - fortImage.getHeight(null) + pcImage.getHeight(null) / 2, null);
        }
        g.drawImage(img, hexCenter.x - pcImage.getWidth(null) / 2, hexCenter.y - pcImage.getHeight(null) / 2, null);

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

    private BufferedImage getImage(Enum item) {
        if (!images.containsKey(item)) {
            ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
            Image img = imgSource.getImage(item.toString() + ".image");
            BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bimg.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            //img = makeColorTransparent(img, Color.white);
            images.put(item, bimg);
            return bimg;
        }
        return (BufferedImage)images.get(item);
    }

    public static Image makeColorTransparent(Image im, final Color color) {
    ImageFilter filter = new RGBImageFilter() {
      // the color we are looking for... Alpha bits are set to opaque
      public int markerRGB = color.getRGB() | 0xFF000000;

      public final int filterRGB(int x, int y, int rgb) {
        if ( ( rgb | 0xFF000000 ) == markerRGB ) {
          // Mark the alpha bits as zero - transparent
          return 0x00FFFFFF & rgb;
          }
        else {
          // nothing to do
          return rgb;
          }
        }
      };

    ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
    return Toolkit.getDefaultToolkit().createImage(ip);
    }

    private void changeColor(BufferedImage src, Color remove, Color replace)
    {
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

    private void makeHidden(BufferedImage src, Color remove, Color replace) {
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
}
