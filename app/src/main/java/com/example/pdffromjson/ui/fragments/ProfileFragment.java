package com.example.pdffromjson.ui.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.example.pdffromjson.R;
import com.example.pdffromjson.ui.activity.MainActivity;
import com.example.pdffromjson.utils.Constants;
import com.example.pdffromjson.utils.LoadImage;
import com.example.pdffromjson.utils.UploadImageHelper;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements UploadImageHelper.OnImageUploadListener {

    private static final String TAG = "ProfileFragment";
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    UploadImageHelper uploadImageHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.profile_image)
    void selectImage() {
        uploadImageHelper = new UploadImageHelper(getActivity(), ProfileFragment.this, this);
        uploadImageHelper.selectImage();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: Profile Fragment****" + uploadImageHelper + "Result Code" + requestCode);
        if (uploadImageHelper != null) {
            uploadImageHelper.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: Profile Fragment" + Arrays.toString(permissions) + "Req Code   " + requestCode);

        if (requestCode == Constants.TAKE_PHOTO_FROM_CAMERA) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), permission) != PackageManager.PERMISSION_GRANTED) {
                    toastMessage("Please Allow Access File Permission to access Camera`s Picture ");
                } else {
                    if (uploadImageHelper != null) {
                        uploadImageHelper.handleRequestPermissionResult(requestCode, permissions[2], grantResults[2]);
                    }
                }
            }
        } else if (requestCode == Constants.READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && uploadImageHelper != null) {
                uploadImageHelper.handleRequestPermissionResult(requestCode, permissions[0], grantResults[0]);
            }
        } else {
            toastMessage("Permission are not Granted");
        }
    }

    @Override
    public void OnUploadSuccess(String imagePath, File imageFile, Bitmap imageBitmap) {
        Log.d("ImagePath", imageFile.getAbsolutePath());
        LoadImage.imageLoader(profileImage, "file://" + imageFile.getAbsolutePath());

    }
}
