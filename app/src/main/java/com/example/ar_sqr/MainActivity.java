package com.example.ar_sqr;


import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Camera;
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
    private  static Scene scene;// ทำตรงนี้เพื่อให้ค่า scene เป็นค่าล่าสุดที่ถูก set สามารุตรงจสอบว่ามีจำนวน child กี่ตัวได้จาก List<Node> nodeList = new ArrayList<(arFragment.getArSceneView().getScene().getChildren());
    private ModelRenderable renderable;

    private ImageButton imgbut;

    private String plan_name="screen18.sfb";
    private String cardname[]={"musicial","singer"};

    private ArrayMap<String,PlayAR> plan_all = new ArrayMap<>();


    private String mode ="vdo";

    //Integer num[] = new Integer[cardname.length];
    Integer count =cardname.length+1;
    Integer num[] = new Integer[count];

    ///--- เวลาจะเคลัยร์ค่าให้เรียกจาก class PlayAR ลบobj ออกจาก scene เพราะเรากำหนดค่า static scene ดังนั้นมันจะจำค่าที่ update ล่าสุดเอาไว้ทดสอบใน test_static แล้วมันเก็บค่าล่สุดเอาไว้เพราะงั้นน่าจะทำได้





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgbut = (ImageButton)findViewById(R.id.imageButton2);

        imgbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode= "3d";
            }
        });


        /*
        texture = new ExternalTexture();

        mediaPlayer = MediaPlayer.create(this, R.raw.video);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable
                .builder()
                .setSource(this,Uri.parse("screen13.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการreder video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;
                });

         */

        arFragment = (CustomArFragment)
                getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene = arFragment.getArSceneView().getScene();





        for(int i=0;i<cardname.length;i++){

            PlayAR pAR = new PlayAR();
            pAR.setScene(scene);
            pAR.setArFragment(arFragment);

            plan_all.put(cardname[i],pAR);


        }

        resetnum();


        scene.addOnUpdateListener(this::onUpdate);

    }

    private void onUpdate(FrameTime frameTime) {

        /*if (isImageDetected)
            return;
            */

        Frame frame = arFragment.getArSceneView().getArFrame();
/*
            getcam Position
        Camera cam=  frame.getCamera();

 */

        //Log.e("LOG","--mode="+mode);

        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        Boolean chkdetect;

        // เป็นส่วนในการ detect รูปเป้าหมาย image target
        for (AugmentedImage image : augmentedImages) {

            if (image.getTrackingState() == TrackingState.TRACKING) {

                // get cam position
               // CameraPos = cam.getDisplayOrientedPose();
               // detectAnchor(CameraPos);

                if (image.getName().equals("musicial")) {

                    //isImageDetected = true;

                   // Log.i("LOG","---found music");



                    //ตอนนี้คือให้มันเรีกครั้งเดียวพอหละ พอเรียกซ้ำๆมันเลยขึ้นทีละหลายอัน
                    //chkdetect = isDecteted.get(image.getName());

                    if(num[0]<2) {
                        PlayAR pAR1= plan_all.get(image.getName());

                        Log.e("LOG", image.getName() + "====== setMedia& PlayVideo");

                        pAR1.SetMedia(image.getName(), this, plan_name);
                        pAR1.playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(), image.getExtentZ(), image.getName());

                        num[0]++;
                    }

                    }

                if(image.getName().equals("singer")) {

                  //  Log.d("LOG","++found singer");

                    if(num[1]<2) {
                        PlayAR pAR2= plan_all.get(image.getName());

                        Log.e("LOG", image.getName() + "====== setMedia& PlayVideo");

                        pAR2.SetMedia(image.getName(), this, plan_name);
                        pAR2.playVideo(image.createAnchor(image.getCenterPose()), image.getExtentX(), image.getExtentZ(), image.getName());

                        num[1]++;
                    }


                }

                if(mode.equals("3d")){
                    //if(num[2]<1){

                    if(num[2]<1) {
                        PlayAR pARdel = new PlayAR();
                        pARdel.Reset_Sence();

                        num[2]++;
                    }



                }



                    break;
                }

            }

        }




    private  void resetnum(){


        //for(int i=0;i<cardname.length;i++){
        for(int i=0; i<count; i++){

                num[i]=0;


        }
    }




/*
    private void playVideo(Anchor anchor, float extentX, float extentZ,AugmentedImage image1) {

        mediaPlayer.start();
        /*
        if( pre_plan_node!=null){
            scene.onRemoveChild(pre_plan_node); // remove render before from scene
            pre_plan_node.setParent(null);
            pre_plan_node= null;

        }


        AnchorNode anchorNode = new AnchorNode(anchor);

        /*
        หลักการเปลี่ยน pixel เป็น หน่วยวัด ตามนี้
        https://stackoverflow.com/questions/52379289/how-to-completely-fit-an-ar-viewrenderable-on-target-image
                image car = 912x380  72 dpi

                convert = 32.2x13.4
                Scal  100cm= 1

        //convert  pixel to meter.//เพราะ extentZ มันเป็น meter แล่วเอาไปเทียบอัตราส่วนเพื่อปรับscale
        //convert height=0.14393333/width=0.17533055
        float img_height= converPixel2CM(408f,72f)/100;
        float img_width=converPixel2CM(497f,72f)/100;
        //ลดขนาด scale
        float scal_down = 200;

        // ระยะความห่างการส่องimage target มีผลต่อขนาด scal ของ Plan ซึ่งอันนี้ กำหนดขนาด scale โดยคำนวณมาจาก รูปเป้าหมายที่ส่อง ที่ไม่ใช้เพราะการส่องในแต่ละครั้งขนาดที่ได้ไม่เท่ากันเพราะมุมที่เปลี่ยนไปนึงนิดมีผลทำให้ขนาดเปลี่ยนไป
        //float scale_height = (img_height/extentZ)/scal_down;
        //float scale_width =  (img_width /extentX)/scal_down;
        //อันนี้เป็นการ fit ขนาดของ plan ที่จะมาแสดง
        //float scale_height= 0.0021171132f;
        //float scale_width = 0.0031450423f;
        //float scale_height= 0.00021171132f;
        //float scale_width = 0.00031450423f;
        //float scale_height= 0.0021171132f/10000;
        //float scale_width = 0.0031450423f/10000;
        // Scale เท่านี้เราโอเคหละในเวลาที่ส่องใกล้ๆจะแสดงขนาดที่โอเคพอควร
       // float scale_height= 0.00001f/1000000000;
       // float scale_width = 0.00001f/1000000000;

        Log.i("LOG","=================================================");
        Log.i("LOG","convert height="+img_height+"/width="+img_width);

      //  Log.d("LOG","Scale height="+scale_height+"/width="+scale_width);




        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            //--render video on texture of plan
            anchorNode.setRenderable(renderable);
            //--remove texture from plan
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });



        anchorNode.setWorldScale(new Vector3(extentX,extentZ,1));
      //  scene.addChild(anchorNode);





        float posX= anchorNode.getWorldPosition().x;
        float posY= anchorNode.getWorldPosition().y;
        float posZ= anchorNode.getWorldPosition().z-0.1f;

        float scaleX= anchorNode.getLocalScale().x;
        float scaleY = anchorNode.getLocalScale().y;
        float scaleZ = anchorNode.getLocalScale().z;





        //posX= 0.045f; posY=-0.0432f; posZ= -0.314f;






         //   angle= angle+inc_an;


        //ส่วนค่าตรงนี้คือ เป็นค่าจริงของ model ต้นฉบับซึ่งเรายังไม่ได้ผ่านการทำอะไรเลย
        Box box = (Box) renderable.getCollisionShape();


        Log.i("LOG","Box extent="+box.getExtents().toString()+"Box Size="+box.getSize().toString());


        try {

            TransformableNode plan_node = new TransformableNode(arFragment.getTransformationSystem());



           // plan_node.setWorldScale(new Vector3(extentX,extentZ,1));

          // plan_node.setLocalScale(new Vector3(scale_width, scale_height, 0));



           // plan_node.setLocalScale(new Vector3(scaleX,scaleY,scaleZ));


            // ขึ้นมาตรงที่imagetarget เลย
            //plan_node.setWorldPosition(anchorNode.getWorldPosition());
            plan_node.setWorldPosition(new Vector3(posX,posY,posZ));


            Quaternion roY = Quaternion.axisAngle(new Vector3(0, 1.0f, 0f), angle);


            plan_node.setLocalRotation(roY);


           // Log.e("LOG","anchorNode scale="+anchorNode.getLocalScale().toString());
            Log.i("LOG", " node Localscale=" + plan_node.getLocalScale().toString());
            //Size ของ augimage ในโลกWorld อิงมาจาก size ของรูปใน augimageDB
            // Log.i("LOG","aug image ExtentX="+extentX+",extentZ="+extentZ);
            //Log.i("LOG", "Augimg center POSE=" + image1.getCenterPose().toString());
            Log.i("LOG", "Plan pos =" + plan_node.getWorldPosition().toString());
            //Log.i("LOG", "anchorNode Quataion =" + anchorNode.getLocalRotation().toString());
            Log.e("LOG", ">>Plan Quatation=" + plan_node.getLocalRotation().toString());
            Log.e("LOG", ">>Anchor WorldScal =" +anchorNode.getWorldScale().toString());


            //ให้ใช้  model มาrender ที่ transformableNode ไปเลย เราจะสามารถทำการ เซ๊ทค่าตำแหน่งมันได้
            plan_node.setParent(anchorNode);
            plan_node.setRenderable(renderable);
            plan_node.select();

           // plan_node.onDeactivate();
            pre_plan_node = plan_node;

            scene.addChild(plan_node);


           // plan_node.onDeactivate();
        }catch (Exception ex){
            Log.e("LOG","Error gesture control::"+ex.getMessage());
        }



    }
    */

    public  float converPixel2CM(float pixel, float dpi){

        float inch = 2.54f;

        float cm;

        cm= (inch/dpi)*pixel;


        return cm;
    }
}
