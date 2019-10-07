package com.example.pdffromjson.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ashish on 10/6/2019
 */
public class UploadImageHelper {

    private static final String TAG = "UploadImageHelper";

    private static final int CAMERA_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private Activity activity;
    private Fragment fragment;
    private OnImageUploadListener onImageUploadListener;
    private String cameraPath;
    private File myFile;
    private Context context;
    private Bitmap rotatedBitmap = null;
    private Uri mPhotoURI;


    private Intent cameraIntent, externalLibraryIntent;
    private String[] reqPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};


    public UploadImageHelper(Context context, Activity activity, OnImageUploadListener onImageUploadListener) {
        this.activity = activity;
        this.onImageUploadListener = onImageUploadListener;
        this.context = activity;
    }

    public UploadImageHelper(Context context, Fragment fragment, OnImageUploadListener onImageUploadListener) {
        this.fragment = fragment;
        this.onImageUploadListener = onImageUploadListener;
        this.context = context;
    }

    public static boolean hasCameraCaptureAndAccessPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent = intent;
                    cameraPath = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg";
                    mPhotoURI = FileProvider.getUriForFile(context,
                            /*"com.example.pdffromjson"*/context.getPackageName() +
                                    ".provider",
                            new File(cameraPath)); //Works on all api level
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, /*Uri.fromFile(new File(cameraPath))*/mPhotoURI);
                    operateCamera(intent);


                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    externalLibraryIntent = intent;
                    intent.setType("image/*");
                    operateLibrary(context, (Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            Intent.createChooser(intent, "Select File"));


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void operateLibrary(Context context, Activity activityContext,
                                String[] permissions, Intent chooser) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(context2,
                    readExternalStorage)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {*/
            // No explanation needed, we can request the permission.
            if (activity != null) {
                ActivityCompat.requestPermissions(activityContext,
                        permissions,
                        Constants.READ_EXTERNAL_STORAGE);
            } else if (fragment != null) {
                fragment.requestPermissions(permissions, Constants.READ_EXTERNAL_STORAGE);
            }
            //}
        } else {
            if (activity != null)
                activity.startActivityForResult(chooser, UploadImageHelper.RESULT_LOAD_IMAGE);
            else
                fragment.startActivityForResult(chooser, UploadImageHelper.RESULT_LOAD_IMAGE);
        }
    }


    private void operateCamera(final Intent intent) {

        if (!hasCameraCaptureAndAccessPermissions(context, reqPermissions)) {
            Log.e(TAG, "operateCamera: Req Permission");
            if (activity != null) {
                ActivityCompat.requestPermissions((Activity) context,
                        reqPermissions,
                        Constants.TAKE_PHOTO_FROM_CAMERA);
            } else if (fragment != null) {
                fragment.requestPermissions(reqPermissions, Constants.TAKE_PHOTO_FROM_CAMERA);

            }
        } else {
            if (activity != null)
                activity.startActivityForResult(intent, CAMERA_REQUEST);
            else
                fragment.startActivityForResult(intent, CAMERA_REQUEST);
        }

    }

    public void handleRequestPermissionResult(int requestCode, String permission, int isPermissionGranted) {
        if (requestCode == Constants.TAKE_PHOTO_FROM_CAMERA
                && isPermissionGranted == PackageManager.PERMISSION_GRANTED) {
            if (cameraIntent != null) {
                if (activity != null) {
                    activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Log.e(TAG, "handleRequestPermissionResult: Open Camera for fragment");
                    fragment.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        }

        if (requestCode == Constants.READ_EXTERNAL_STORAGE
                && isPermissionGranted == PackageManager.PERMISSION_GRANTED) {
            if (externalLibraryIntent != null) {
                if (activity != null) {
                    activity.startActivityForResult(externalLibraryIntent, RESULT_LOAD_IMAGE);
                } else if (fragment != null) {
                    fragment.startActivityForResult(externalLibraryIntent, RESULT_LOAD_IMAGE);
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath1 = cursor.getString(columnIndex);
                cursor.close();

                rotatedBitmap = rotateImage(picturePath1);
                new compressImage(rotatedBitmap, 1).execute();
                Log.d("image path", "" + myFile);
            } else {
                File myFile = new File(selectedImage.getPath());
                String filePath = myFile.getAbsolutePath();
                if (myFile.exists()) {
                    rotatedBitmap = rotateImage(filePath);
                    new compressImage(rotatedBitmap, 1).execute();
                    Log.d("image path", "" + myFile);
                }
            }

        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult: Camera ok");
            if (cameraPath != null) {
                Log.e(TAG, "onActivityResult: Camera Path" + cameraPath);
                rotatedBitmap = rotateImage(cameraPath);
                new compressImage(rotatedBitmap, 1).execute();
                Log.d("image path", "" + myFile);
                /*if(rotatedBitmap!=null) {
                    onImgaeUploadListener.OnUploadSuccess("", myFile, rotatedBitmap);
                }*/
            }


        }

    }

    public File reduceFileSize(Bitmap bitmap) {
        // String  path = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg";
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, System.currentTimeMillis() + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }


        return imageFile;
    }

    public Bitmap rotateImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap photo = BitmapFactory.decodeFile(path);

        Bitmap newBitmapImage = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    newBitmapImage = getRotateImage(photo, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    newBitmapImage = getRotateImage(photo, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    newBitmapImage = getRotateImage(photo, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newBitmapImage == null) {
            return photo;
        }
        return newBitmapImage;
    }

    public Bitmap getRotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public interface OnImageUploadListener {
        void OnUploadSuccess(String imagePath, File imageFile, Bitmap imageBitmap);

    }

    @SuppressLint("StaticFieldLeak")
    class compressImage extends AsyncTask<Integer, Void, File> {
        private final Bitmap bitmap;
        private int image_flag;

        // Constructor
        public compressImage(Bitmap bitmap, int image_flag) {
            this.bitmap = bitmap;
            this.image_flag = image_flag;
        }

        // Compress and Decode image in background.
        @Override
        protected File doInBackground(Integer... params) {
            return reduceFileSize(bitmap);
        }

        // This method is run on the UI thread
        @Override
        protected void onPostExecute(File file) {
            myFile = file;
            if (rotatedBitmap != null) {
                onImageUploadListener.OnUploadSuccess("", myFile, rotatedBitmap);
            }
        }
    }


}
