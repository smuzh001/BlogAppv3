package com.smuzh001.blogappv3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BlogFeed extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    /**/
private DatabaseReference myDBref;
private FirebaseDatabase myDB;
@Override
/**/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_feed);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
/**/
        myDB = FirebaseDatabase.getInstance();
        myDBref = FirebaseDatabase.getInstance().getReference().child("Blog");
        myDBref.keepSynced(true);
/**/
    }
    //menu items new post and logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signOut:
                mAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.newPost:
                if(mUser != null) {
                    startActivity(new Intent(BlogFeed.this, AddPostActivity.class));
                    //we finish previous activity so they can't press back.
                    finish();
                }
                break;
            default:

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
