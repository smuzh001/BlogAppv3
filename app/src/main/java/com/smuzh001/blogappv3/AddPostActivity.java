package com.smuzh001.blogappv3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//This activity will serve as our canvas where everything written will be
//stored on the cloud.
public class AddPostActivity extends AppCompatActivity {
    private ImageButton postImage;
    private EditText postTitle;
    private EditText postDesc;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    //progress dialog that allows user to know something is going on in the background.
    private ProgressDialog progressDialog;
    private DatabaseReference myDBref;
    private StorageReference mStorageRef;
    private Uri selectedImage;
    private String downloadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myDBref = FirebaseDatabase.getInstance().getReference().child("Blog");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("blogImages");

        postImage = (ImageButton) findViewById(R.id.imageButton);
        postTitle = (EditText) findViewById(R.id.postTitleText);
        postDesc = (EditText) findViewById(R.id.postBodyText);
        submitButton = (Button) findViewById(R.id.postButton);
        progressDialog = new ProgressDialog(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

    }

    public void getPhoto(){
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //any type of image
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
    //by calling the getPhoto() your activity does an Action.
    //this function runs as a result of the action which handles displaying the image on the Activity.

    /**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uniformed resource identifier. unique to every object in Android. This is our path to the Image.
        if(requestCode == 1 && resultCode == RESULT_OK ) {
            selectedImage = data.getData();
            //if the Activity did return an image, the requestCode would be 1 and the resultCode = OK.
            postImage.setImageURI(selectedImage);
        }
        /*
        if(requestCode == 1 && resultCode == RESULT_OK ){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                postImage.setImageBitmap(bitmap);
                //store image data in a Bytestream to upload to the database (ref. Firebase:Upload from data in memory)
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        */
    }
/**/
    private void startPosting() {
        progressDialog.setMessage("Posting to blog...");
        progressDialog.show();
        //.trim removes unesessary spaces from our title
        final String titleVal = postTitle.getText().toString().trim();
        final String descVal = postDesc.getText().toString().trim();

        if(!titleVal.isEmpty() && !descVal.isEmpty()){
            //upload info.
            //uri.getLastPathSegment() returns /images/myphoto.jpeg clever way to get image name
            mStorageRef.child(selectedImage.getLastPathSegment())
                    .putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //improved method of getting downloadURL of file.
                    //String downloadURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                downloadURL = task.getResult().toString();
                                Log.i("imageURL", downloadURL);
                                //with success you can now push into DB
                                DatabaseReference newPost = myDBref.push();
                                //using HashMap to push id@yyBlog class to DB. **Keys must be exactly the same**
                                Map<String, String> BlogMap = new HashMap<>();
                                BlogMap.put("title", titleVal);
                                BlogMap.put("desc", descVal);
                                BlogMap.put("image", downloadURL);
                                BlogMap.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                                //Date date = new Date();
                                //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                //BlogMap.put("timeStamp", String.valueOf(formatter.format(date)));
                                BlogMap.put("userID", mUser.getUid());
                                //now that our HashMap is ready, we update our Push.
                                newPost.setValue(BlogMap);
                            }
                            else{
                                //getting Download url fails
                            }
                        }
                    });

                }
            });
            /*
            Blog blog = new Blog(titleVal, descVal, "downloadURL", "timeStamp", "userID");
            myDBref.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Post Added", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            */
            progressDialog.dismiss();
            startActivity(new Intent(AddPostActivity.this, BlogFeed.class));
        }
    }
}
