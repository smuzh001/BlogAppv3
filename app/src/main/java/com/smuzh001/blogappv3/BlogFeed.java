package com.smuzh001.blogappv3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BlogFeed extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    /**/
    private DatabaseReference myDBref;
    private FirebaseDatabase myDB;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<Blog> blogList;

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

        blogList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.blogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    //We setup the RecyclerView before everything sets up.
    @Override
    protected void onStart() {
        super.onStart();
        //once something happens to the database
        myDBref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //whatever we get will be mapped to our blog object for each in DB
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);
                blogRecyclerAdapter = new BlogRecyclerAdapter(BlogFeed.this, blogList);
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
