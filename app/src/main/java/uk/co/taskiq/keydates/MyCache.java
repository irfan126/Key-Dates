package uk.co.taskiq.keydates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 24/02/2016.
 */
public class MyCache implements com.android.volley.toolbox.ImageLoader.ImageCache {

@Override
public Bitmap getBitmap(String path) {
        if (path.contains("file://")) {
        return BitmapFactory.decodeFile(path);
        } else {
        // Here you can add an actual cache
        return null;
        }
        }
@Override
public void putBitmap(String key, Bitmap bitmap) {
        // Here you can add an actual cache
        }
        }