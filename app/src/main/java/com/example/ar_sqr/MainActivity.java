package com.example.ar_sqr;


import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texture = new ExternalTexture();

        mediaPlayer = MediaPlayer.create(this, R.raw.video);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable
                .builder()
                .setSource(this,Uri.parse("screen7.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการreder video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;
                });

        arFragment = (CustomArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene = arFragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);

    }

    private void onUpdate(FrameTime frameTime) {

        if (isImageDetected)
            return;


        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        // เป็นส่วนในการ detect รูปเป้าหมาย image target
        for (AugmentedImage image : augmentedImages) {

            if (image.getTrackingState() == TrackingState.TRACKING) {

                if (image.getName().equals("image")) {

                    isImageDetected = true;



                    playVideo (image.createAnchor(image.getCenterPose()), image.getExtentX(),
                            image.getExtentZ(),image);

                    break;
                }

            }

        }

    }

    private void playVideo(Anchor anchor, float extentX, float extentZ,AugmentedImage image1) {

        mediaPlayer.start();

        AnchorNode anchorNode = new AnchorNode(anchor);

        /*
        หลักการเปลี่ยน pixel เป็น หน่วยวัด ตามนี้
        https://stackoverflow.com/questions/52379289/how-to-completely-fit-an-ar-viewrenderable-on-target-image
                image car = 912x380  72 dpi

                convert = 32.2x13.4
                Scal  100cm= 1
         */
        //convert  pixel to meter.//เพราะ extentZ มันเป็น meter แล่วเอาไปเทียบอัตราส่วนเพื่อปรับscale
        //convert height=0.14393333/width=0.17533055
        float img_height= converPixel2CM(408f,72f)/100;
        float img_width=converPixel2CM(497f,72f)/100;

        float scal_down = 200;

        // ระยะความห่างการส่องimage target มีผลต่อขนาด scal ของ Plan ซึ่งอันนี้ กำหนดขนาด scale โดยคำนวณมาจาก รูปเป้าหมายที่ส่อง ที่ไม่ใช้เพราะการส่องในแต่ละครั้งขนาดที่ได้ไม่เท่ากันเพราะมุมที่เปลี่ยนไปนึงนิดมีผลทำให้ขนาดเปลี่ยนไป
        //float scale_height = (img_height/extentZ)/scal_down;
        //float scale_width =  (img_width /extentX)/scal_down;
        //อันนี้เป็นการ fit ขนาดของ plan ที่จะมาแสดง
        float scale_height= 0.0021171132f;
        float scale_width = 0.0031450423f;


        Log.i("LOG","convert height="+img_height+"/width="+img_width);

        Log.d("LOG","Scale height="+scale_height+"/width="+scale_width);




        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });


        Pose img_pos= anchor.getPose();
        float posX= img_pos.tx();
        float posY= img_pos.ty();
        float posZ= img_pos.tz()-0.3f;



        //ส่วนค่าตรงนี้คือ เป็นค่าจริงของ model ต้นฉบับซึ่งเรายังไม่ได้ผ่านการทำอะไรเลย
        Box box = (Box) renderable.getCollisionShape();


        Log.i("LOG","Box extent="+box.getExtents().toString()+"Box Size="+box.getSize().toString());


        TransformableNode plan_node = new TransformableNode(arFragment.getTransformationSystem());



        plan_node.setWorldScale(new Vector3(scale_width,scale_height,scale_width));
        plan_node.setWorldPosition(new Vector3(posX,posY,posZ));

       // Quaternion roX = Quaternion.axisAngle(new Vector3(1.0f,0,0),90f);
        Quaternion roY = Quaternion.axisAngle(new Vector3(0,1.0f,0f),35f);


         plan_node.setLocalRotation(roY);





        Log.i("LOG"," node Localscale="+plan_node.getLocalScale().toString());
        //Size ของ augimage ในโลกWorld อิงมาจาก size ของรูปใน augimageDB
        // Log.i("LOG","aug image ExtentX="+extentX+",extentZ="+extentZ);
        Log.i("LOG","Augimg center POSE="+image1.getCenterPose().toString());
        Log.i("LOG","Plan pos ="+plan_node.getWorldPosition().toString());
        Log.i("LOG","anchorNode Quataion ="+anchorNode.getLocalRotation().toString());
        Log.e("LOG","Plan Quatation="+plan_node.getLocalRotation().toString());





        //ให้ใช้  model มาrender ที่ transformableNode ไปเลย เราจะสามารถทำการ เซ๊ทค่าตำแหน่งมันได้
        plan_node.setParent(anchorNode);
        plan_node.setRenderable(renderable);
        plan_node.select();

        scene.addChild(plan_node);



    }

    public  float converPixel2CM(float pixel, float dpi){

        float inch = 2.54f;

        float cm;

        cm= (inch/dpi)*pixel;


        return cm;
    }
}
