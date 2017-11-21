package com.culturage.oceans_eleven.culturage.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.culturage.oceans_eleven.culturage.R;
import com.culturage.oceans_eleven.culturage.baseClasses.Comment;
import com.culturage.oceans_eleven.culturage.network.PostJSON;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter {

    private final static String LOG_TAG = "commentAdapter";

    public CommentAdapter(Activity context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        Log.v("adapter", "view rendering");
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_item, parent, false);
        }
        final Comment currentComment = (Comment) getItem(position);

        TextView usernameView = (TextView) listItemView.findViewById(R.id.commentOwner);
        Log.v(LOG_TAG, usernameView.toString());
        usernameView.setText(currentComment.getUsername());

        final TextView contentView = (TextView) listItemView.findViewById(R.id.commentContent);
        contentView.setText(currentComment.getContent());

        ImageButton userImage = (ImageButton) listItemView.findViewById(R.id.user_image);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentView.getMaxLines() == 3) {
                    contentView.setMaxLines(Integer.MAX_VALUE);
                } else {
                    contentView.setMaxLines(3);
                }
            }
        });

        // delete button. Needs to be revised.
        ImageButton deleteComment = (ImageButton) listItemView.findViewById(R.id.delete_comment);
        deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new deleteLoader(currentComment.getCommentId()).execute();
            }
        });

//        imageView.setImageResource(R.drawable.sample_0);
        String imageUri = currentComment.getImageUri();
        // 400 looks cool
        Picasso.with(this.getContext()).load(imageUri).resize(120, 0).into(userImage);
        Log.v("adapter", "view rendering done");
        return listItemView;
    }

    private class deleteLoader extends AsyncTask<String, String, String> {

        private int deleteCommentId;
        private boolean isDeleted;

        private deleteLoader(int deleteCommentId) {
            this.deleteCommentId = deleteCommentId;
            this.isDeleted = false;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = preferences.getString("token", "null");
                isDeleted = deleteComment(token);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            if (isDeleted) {
                Toast.makeText(getContext(), "Comment Deleted Successfuly", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean deleteComment(String token) {
            String result;
            String deleteCommentUrl = "http://18.220.108.135/api/comments/" + deleteCommentId;
            try {
                result = PostJSON.deleteToApi(deleteCommentUrl, token);
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            return !(result == null || result.equals("400") || result.equals("401") || result.equals("403"));

        }

    }


}
