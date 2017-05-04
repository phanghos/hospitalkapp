package org.taitasciore.android.hospitalk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by roberto on 29/04/17.
 */

public class EncodingUtils {

    public static String encodeImage(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] bytes = os.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
