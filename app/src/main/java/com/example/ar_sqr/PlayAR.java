package com.example.ar_sqr;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

public class PlayAR {

    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private static CustomArFragment arFragment;
    private static Scene scene;
    private  ModelRenderable renderable;
     private  static TransformableNode pre_plan_node;

    public void setArFragment(CustomArFragment arF){
        arFragment= arF;

    }
    public void setScene(Scene sc){
        this.scene= sc;

    }



    public void SetMedia(String cardname, Context contxt,String pname){

        texture = new ExternalTexture();


        if(cardname.equals("musicial")) {

            Log.d("LOG","call musicial");

            mediaPlayer = MediaPlayer.create(contxt, R.raw.musicial);
            mediaPlayer.setSurface(texture.getSurface());
            mediaPlayer.setLooping(true);

        }else if(cardname.equals("singer")){
            Log.d("LOG","call singer");
            mediaPlayer = MediaPlayer.create(contxt, R.raw.singer);
            mediaPlayer.setSurface(texture.getSurface());
            mediaPlayer.setLooping(true);

        }



        ModelRenderable
                .builder()
                .setSource(contxt, Uri.parse(pname))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture",
                            texture);
                    //---- มันคือ choma key ซึ่งเป็นการreder video บน plan 3D
                    modelRenderable.getMaterial().setFloat4("keyColor",
                            new Color(0.01843f, 1f, 0.098f));


                    renderable = modelRenderable;
                });


    }

    public  void playVideo(Anchor anchor, float extentX, float extentZ,String fname){

        mediaPlayer.start();
        if( pre_plan_node!=null){
            scene.onRemoveChild(pre_plan_node); // remove render before from scene
            pre_plan_node.setParent(null);
            pre_plan_node= null;

        }


        AnchorNode anchorNode = new AnchorNode(anchor);



        Log.i("LOG","======================"+fname+"=====================");





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





        try {

            TransformableNode plan_node = new TransformableNode(arFragment.getTransformationSystem());



            // plan_node.setWorldScale(new Vector3(extentX,extentZ,1));

            // plan_node.setLocalScale(new Vector3(scale_width, scale_height, 0));



            // plan_node.setLocalScale(new Vector3(scaleX,scaleY,scaleZ));


            // ขึ้นมาตรงที่imagetarget เลย
            //plan_node.setWorldPosition(anchorNode.getWorldPosition());
            plan_node.setWorldPosition(new Vector3(posX,posY,posZ));


            //Quaternion roY = Quaternion.axisAngle(new Vector3(0, 1.0f, 0f), angle);


           // plan_node.setLocalRotation(roY);


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
            pre_plan_node = plan_node;

            // plan_node.onDeactivate();


            scene.addChild(plan_node);


            // plan_node.onDeactivate();
        }catch (Exception ex){
            Log.e("LOG","Error gesture control::"+ex.getMessage());
        }

    }

    public void Reset_Sence(){

        List<Node> nodeList = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());

        Log.d("LOG","All node="+nodeList.size());

        for(Node chnode: nodeList){

            //scene.onRemoveChild(chnode);
            if(chnode instanceof TransformableNode){

                Log.e("LOG","found TransformableNode");

                scene.onRemoveChild(chnode);

                chnode.setParent(null);

               /* if (((AnchorNode) chnode).getAnchor() != null){
                    ((AnchorNode) chnode).getAnchor().detach();
                    ((AnchorNode) chnode).setParent(null);
                    scene.onRemoveChild(chnode);

                }*/

            }

        }




    }


}
