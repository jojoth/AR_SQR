package com.example.ar_sqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {

        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.musician_ori);
        Bitmap img_singer = BitmapFactory.decodeResource(getResources(), R.drawable.singer);
        aid.addImage("musicial", image);
        aid.addImage("singer",img_singer);

        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);
        //Log.i("LOG","+++start use");

        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }
}
