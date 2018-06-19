package com.example.a1a.sujhav.adapter;

/**
 * Created by Abhishek on 07-12-2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is derived from the class extracted from this blog (http://blog.awnry.com/post/88749232734/changing-tileoverlay-opacity-for-androids-google)
 */
public class TransparentTileOWM implements TileProvider {
    //private String url;
    private Paint opacityPaint = new Paint();
    private String tileType;

    private static final String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=f287a28bbf03187cfd870a8749bd5bdc";

    /**
     * This constructor assumes the <code>url</code> parameter contains three placeholders for the x- and y-positions of
     * the tile as well as the zoom level of the tile. The placeholders are assumed to be <code>{x}</code>,
     * <code>{y}</code>, and <code>{zoom}</code>. An example
     * for an OpenWeatherMap URL would be: http://tile.openweathermap.org/map/precipitation/{zoom}/{x}/{y}.png
     *
     */
    public TransparentTileOWM(String tileType)
    {
        this.tileType = tileType;
        setOpacity(50);
    }

    /**
     * Sets the desired opacity of map {@link Tile}s, as a percentage where 0% is invisible and 100% is completely opaque.
     * @param opacity The desired opacity of map {@link Tile}s (as a percentage between 0 and 100, inclusive)
     */
    public void setOpacity(int opacity)
    {
        int alpha = (int)Math.round(opacity * 2.55);    // 2.55 = 255 * 0.01
        opacityPaint.setAlpha(alpha);
    }

    @Override
    public Tile getTile(int x, int y, int zoom)
    {
        URL tileUrl = getTileUrl(x, y, zoom);

        Tile tile = null;
        ByteArrayOutputStream stream = null;

        try
        {
            Bitmap image = BitmapFactory.decodeStream(tileUrl.openConnection().getInputStream());
            image = adjustOpacity(image);

            stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            tile = new Tile(256, 256, byteArray);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(stream != null)
            {
                try
                {
                    stream.close();
                }
                catch(IOException e) {}
            }
        }

        return tile;
    }

    /**
     * Helper method that returns the {@link URL} of the tile image for the given x/y/zoom location.
     *
     * <p>This method assumes the URL string provided in the constructor contains three placeholders for the x-
     * and y-positions as well as the zoom level of the desired tile; <code>{x}</code>, <code>{y}</code>, and
     * <code>{zoom}</code>. An example for an OpenWeatherMap URL would be:
     * http://tile.openweathermap.org/map/precipitation/{zoom}/{x}/{y}.png</p>
     *
     * @param x The x-position of the tile
     * @param y The y-position of the tile
     * @param zoom The zoom level of the tile
     *
     * @return The {@link URL} of the desired tile image
     */
    private URL getTileUrl(int x, int y, int zoom)
    {
        String tileUrl = String.format(OWM_TILE_URL, tileType, zoom, x, y);
        try
        {
            return new URL(tileUrl);
        }
        catch(MalformedURLException e)
        {
            throw new AssertionError(e);
        }
    }


    private Bitmap adjustOpacity(Bitmap bitmap)
    {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);

        return adjustedBitmap;
    }

}