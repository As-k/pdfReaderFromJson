package com.example.pdffromjson.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.pdffromjson.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Ashish on 10/6/2019
 */

public class LoadImage {

    public static void imageLoader(ImageView iv, String url) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisk(false).showImageForEmptyUri(R.drawable.ic_default_user).showImageOnFail(R.drawable.ic_default_user).build();//showImageOnLoading(R.drawable.noimage).

        ImageLoader.getInstance().displayImage(url, iv, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {

                        String message = null;


                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {

                    }
                });
    }


}
