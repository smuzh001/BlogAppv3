package com.smuzh001.blogappv3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
//think of the RecycleView you did for MindTapp.
// It will help adapt the data into our RecycleView.
public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    //ViewHolder design pattern enables you to access each list item view without the need for the look up, saving valuable processor cycles.
    // Specifically, it avoids frequent call of findViewById() during ListView scrolling, and that will make it smooth.
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecyclerAdapter.ViewHolder viewHolder, int i) {

        Blog blog = blogList.get(i);
        String imageUrl = null;

        viewHolder.title.setText(blog.getTitle());
        viewHolder.desc.setText(blog.getDesc());
        //viewHolder.timestamp.setText(blog.getDesc());

        //system time needs to be reformatted to March 3, 2014.
        //java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        //String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        //String formattedDate = "10/10/10";
        String formattedDate = formatter.format(date);
        viewHolder.timestamp.setText(formattedDate);

        imageUrl = blog.getImage();
        //TODO: use Picasso library to load image
        Picasso.get().load(imageUrl).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    //this class will substantiate our widgets for our post.
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView desc;
        public TextView timestamp;
        public ImageView image;
        public String userID;

        public ViewHolder(View view, Context ctx){
            super(view);

            context = ctx;
            title = (TextView) view.findViewById(R.id.postTitleList);
            desc = (TextView) view.findViewById(R.id.postBodyList);
            timestamp = (TextView) view.findViewById(R.id.postTimeStampList);
            image = (ImageView) view.findViewById(R.id.postImageList);
            userID = null;
        }

    }
}
