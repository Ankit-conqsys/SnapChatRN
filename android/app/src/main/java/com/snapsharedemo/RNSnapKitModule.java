package com.snapsharedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.snapchat.kit.sdk.SnapCreative;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.creative.api.SnapCreativeKitApi;
import com.snapchat.kit.sdk.creative.exceptions.SnapStickerSizeException;
import com.snapchat.kit.sdk.creative.media.SnapMediaFactory;
import com.snapchat.kit.sdk.creative.media.SnapSticker;
import com.snapchat.kit.sdk.creative.models.SnapLiveCameraContent;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.EasyPermissions;

import static com.snapchat.kit.sdk.SnapLogin.isUserLoggedIn;

public class RNSnapKitModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    Context mContext;
    SnapCreativeKitApi snapCreativeKitApi;
    boolean photoPicker;
    List<File> imageFiles;
    Callback callback;
    private final ReactApplicationContext reactContext;
    private static final int WRITE_STORAGE_PERM = 123;
    private ResponseHelper responseHelper = new ResponseHelper();
    final LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener =
            new LoginStateController.OnLoginStateChangedListener() {
                @Override
                public void onLoginSucceeded() {
                    boolean isLoggedIn = true;
                    callback.invoke(isLoggedIn);
                }

                @Override
                public void onLoginFailed() {
                    boolean isLoggedIn = false;
                    callback.invoke(isLoggedIn);
                }

                @Override
                public void onLogout() {
                    // Here you could update UI to reflect logged out state
                    Log.d("snapchat","logged out");
                }
            };

    public RNSnapKitModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        this.reactContext = reactContext;
        responseHelper.cleanResponse();
        this.reactContext.addActivityEventListener(this);
        SnapLogin.getLoginStateController(getReactApplicationContext()).addOnLoginStateChangedListener(mLoginStateChangedListener);
    }

    @Override
    public String getName() {
        return "RNSnapKit";
    }

    @ReactMethod
    public void pickImage(Callback imageResponse) {
        EasyImage.configuration(mContext)
                .setAllowMultiplePickInGallery(false);

        photoPicker = false;
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            //has permissions
            callback = imageResponse;
            EasyImage.openGallery(this.getCurrentActivity(), 0);
            photoPicker = true;
        } else {
            //does not have permissions
            EasyPermissions.requestPermissions(this.getCurrentActivity(), "Hey", WRITE_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @ReactMethod
    public void send(String caption, String attachmentUrl, Callback shareSnap) {
        snapCreativeKitApi = SnapCreative.getApi(getReactApplicationContext());
        SnapMediaFactory snapMediaFactory = SnapCreative.getMediaFactory(mContext);
        if (imageFiles.size() == 1) {
            SnapLiveCameraContent snapLiveCameraContent = new SnapLiveCameraContent();
            SnapSticker snapSticker = null;
            try {
                snapSticker = snapMediaFactory.getSnapStickerFromFile(imageFiles.get(0));
            } catch (SnapStickerSizeException e) {
                Log.e("snap-Kit error", e.getMessage());
                return;
            }
// Height and width~~ ~~in pixels
            snapSticker.setWidth(300);
            snapSticker.setHeight(300);

// Position is specified as a ratio between 0 & 1 to place the center of the sticker
            snapSticker.setPosX(0.5f);
            snapSticker.setPosY(0.4f);
// Specify clockwise rotation desired
            snapSticker.setRotationDegreesClockwise(0); // degrees clockwise
            snapLiveCameraContent.setSnapSticker(snapSticker);
            snapLiveCameraContent.setCaptionText(caption);
            snapLiveCameraContent.setAttachmentUrl(attachmentUrl);
            snapCreativeKitApi.send(snapLiveCameraContent);
            Log.d("Snapchat", "Photo sent to snapchat");
        }

    }

    @ReactMethod
    public void loginSnapchat(Callback loginResponse) {
        callback = loginResponse;
        SnapLogin.getAuthTokenManager(getCurrentActivity()).startTokenGrant();
    }

    @ReactMethod
    public void hasSnapAccess(Callback accessResponse) {
        boolean isLoggedIn = SnapLogin.isUserLoggedIn(getReactApplicationContext());
        accessResponse.invoke(isLoggedIn);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (photoPicker) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, getCurrentActivity(), new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    photoPicker = false;
                    Log.e("ImagePicker error", e.getMessage());
                    //Some error handling
                }

                @Override
                public void onImagesPicked(List<File> pickedImages, EasyImage.ImageSource source, int type) {
                    //Handle the images
                    photoPicker = false;
                    imageFiles = pickedImages;
                    responseHelper.cleanResponse();
                    responseHelper.putString("uri", imageFiles.get(0).getAbsolutePath());
                    responseHelper.invokeResponse(callback);
                }
            });
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

}