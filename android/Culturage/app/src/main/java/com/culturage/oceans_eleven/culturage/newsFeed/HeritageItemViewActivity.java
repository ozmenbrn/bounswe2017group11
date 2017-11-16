package com.culturage.oceans_eleven.culturage.newsFeed;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.culturage.oceans_eleven.culturage.R;
import com.culturage.oceans_eleven.culturage.adapters.HorizontalRecyclerViewAdapter;
import com.culturage.oceans_eleven.culturage.baseClasses.CustomDialogClass;
import com.culturage.oceans_eleven.culturage.baseClasses.HeritageItem;
import com.culturage.oceans_eleven.culturage.baseClasses.ProfilePage;
import com.culturage.oceans_eleven.culturage.network.Fetcher;
import com.culturage.oceans_eleven.culturage.network.PostJSON;
import com.culturage.oceans_eleven.culturage.network.ProfilePageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HeritageItemViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<HeritageItem> recommendations = new ArrayList<>();
    private HorizontalRecyclerViewAdapter recommendationAdapter;

    private String profileURL = "http://18.220.108.135/api/profile/";
    private String recommendationsUrl = "http://18.220.108.135/api/recommendation/item/";
    private static final String itemUrl = "http://18.220.108.135/api/items";
    private static final String LOG_TAG = "heritageItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heritage_item_view);


        // First get necessary values from the incoming intent and place them.
        Intent incomingIntent = getIntent();
        int heritageItemPostID = incomingIntent.getIntExtra("postId", -1);

        ImageView iw = (ImageView) findViewById(R.id.her_item_photo);
        String imageUri = incomingIntent.getStringExtra("imageUrl");
        // 400 looks cool
        Picasso.with(this).load(imageUri).resize(400, 0).into(iw);

        TextView title = (TextView) findViewById(R.id.her_item_Title);
        title.setText(incomingIntent.getStringExtra("title"));

        TextView desc_view = (TextView) findViewById(R.id.her_item_description);
        desc_view.setText(incomingIntent.getStringExtra("description"));

        // Secondly get necessary fields from the backend.
        //TODO: implement API code here.
        TextView date = (TextView) findViewById(R.id.her_item_date);

        TextView loc = (TextView) findViewById(R.id.her_item_location);

        TextView tags = (TextView) findViewById(R.id.her_item_tags);

        LinearLayout likeCommentFrame = (LinearLayout) findViewById(R.id.item_like_comment_buttons_container);
        LinearLayout commentContainer = (LinearLayout) likeCommentFrame.findViewById(R.id.comment_container);
        TextView commentCount = (TextView) commentContainer.findViewById(R.id.comment_count);

        LinearLayout likeContainer = (LinearLayout) likeCommentFrame.findViewById(R.id.like_container);
        TextView likeCount = (TextView) likeContainer.findViewById(R.id.like_count);

        TextView guest = (TextView) findViewById(R.id.guest_profile);
        ImageView guestPic = (ImageView) findViewById(R.id.guest_profile_pict);

        // Now implement listeners.
        commentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass cdd = new CustomDialogClass(HeritageItemViewActivity.this);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

        likeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HeritageItemViewActivity.this, "Will like soon", Toast.LENGTH_SHORT).show();
            }
        });

        //Will be implemented soon!!
        this.itemUrl = itemUrl + heritageItemPostID;
        // This part waits API code as well
//        int creator_id = getIntent().getIntExtra("creator_id");
//        creator_username = getIntent().getStringExtra("creator_username");
//        guest.setText(" " + creator_username);
        profileURL = profileURL + 1;
//        guest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HeritageItemViewActivity.this, ProfileGuestActivity.class));
//                Intent intent = new Intent(HeritageItemViewActivity.this, ProfileGuestActivity.class);
//                intent.putExtra("creator_id", creator_id); //New
//                startActivity(intent);
//            }
//        });
//        guestPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HeritageItemViewActivity.this, ProfileGuestActivity.class));
//                Intent intent = new Intent(HeritageItemViewActivity.this, ProfileGuestActivity.class);
//                intent.putExtra("creator_id", creator_id); //New
//                startActivity(intent);
//            }
//        });

        //Profile Loader
//        getLoaderManager().initLoader(1, null, profileLoader);
        //getLoaderManager().initLoader(2,null, heritageItemLoader);

        // Lastly, populate recommendations
        mRecyclerView = (RecyclerView) findViewById(R.id.recommendation_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        ArrayList<HeritageItem> recommendations = new ArrayList<HeritageItem>();
//        recommendations.add(new HeritageItem(1, "", "", "", true));
//        recommendations.add(new HeritageItem(1, "", "", "", true));
//        recommendations.add(new HeritageItem(1, "", "", "", true));
//        recommendations.add(new HeritageItem(1, "", "", "", true));
//        recommendations.add(new HeritageItem(1, "", "", "", true));
//        recommendations.add(new HeritageItem(1, "", "", "", true));
        new RecommendationRequest(HeritageItemViewActivity.this, recommendationsUrl + heritageItemPostID).execute();
        recommendationAdapter = new HorizontalRecyclerViewAdapter(HeritageItemViewActivity.this, recommendations);
        mRecyclerView.setAdapter(recommendationAdapter);
    }

    private LoaderManager.LoaderCallbacks<ArrayList<ProfilePage>> profileLoader
            = new LoaderManager.LoaderCallbacks<ArrayList<ProfilePage>>() {

        public Loader<ArrayList<ProfilePage>> onCreateLoader(int i, Bundle bundle) {
            Log.v("LoaderProfilePage", "hello");

            String result = null;
            try {
                result = Fetcher.getJSON(Fetcher.createUrl(itemUrl), HeritageItemViewActivity.this);
                Log.v(LOG_TAG, "resulting json " + result);
            } catch (Exception e) {
                Log.v(LOG_TAG, "exception" + Log.getStackTraceString(e));
                Log.v("heritageItem", "invalid url: " + itemUrl);
            }
            try {
                Log.v(LOG_TAG, "resulting json " + result);
                JSONObject values = new JSONObject(result);
                creator_username = values.getJSONObject("created_by").getString("username");
                creator_id = values.getJSONObject("created_by").getInt("id");
                //guestProfile = new HeritageItem(creator_id, username);

            } catch (Exception e) {
                Log.v("heritageItem", "error parsing guestProfile:");
            }

            profileURL = profileURL + creator_id;

            return new ProfilePageLoader(HeritageItemViewActivity.this, profileURL);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<ProfilePage>> loader, ArrayList<ProfilePage> profilePages) {
            if (profilePages == null) return;

            if (!profilePages.get(0).getPhoto().equals("-1")) {
                ImageView photo = (ImageView) findViewById(R.id.guest_profile_pict);
                String imageUri = "http://" + profilePages.get(0).getPhoto();
                Log.v("Uploadtag", imageUri);
                Picasso.with(getBaseContext()).load(imageUri).into(photo);
                // String imageUri = baseURL + profilePages.get(0).getPhoto();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<ProfilePage>> loader) {

        }

    };

    private class RecommendationRequest extends AsyncTask<Void, Void, Void> {
        private String url;
        private Activity context;
        private ArrayList<HeritageItem> tempRecommendations;

        RecommendationRequest(Activity context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getRecommendations(this.url);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            recommendations = tempRecommendations;
            recommendationAdapter.clear();
            recommendationAdapter.addAll(recommendations);
            mRecyclerView.setAdapter(recommendationAdapter);
        }

        private void getRecommendations(String url) {
            tempRecommendations = new ArrayList<>();
            String result = null;
            try {
                result = Fetcher.getJSON(Fetcher.createUrl(url), context);
                Log.v(LOG_TAG, "resulting json " + result);
            } catch (Exception e) {
                Log.v(LOG_TAG, "exception" + Log.getStackTraceString(e));
                Log.v("heritageItem", "invalid url: " + url);
            }
            try {
                Log.v(LOG_TAG, "resulting json " + result);
                JSONArray json_result = new JSONArray(result);
                for (int i = 0; i < json_result.length(); i++) {
                    JSONObject values = json_result.getJSONObject(i);
                    String title = values.getString("name");
                    String imageURL = values.getString("featured_img");
                    int postID = values.getInt("id");
                    tempRecommendations.add(new HeritageItem(postID, title.trim(), imageURL));
                }
            } catch (Exception e) {
                Log.v("heritageItem", "error parsing recomm:");
            }

        }

    }
    /*
    public void updateUi(ArrayList<HeritageItem> heritageItems) {
        itemAdapter.clear();
        if (heritageItems != null || heritageItems.size() > 0) {
            itemAdapter.addAll(heritageItems);
        }
    }

    //WILL BE IMPLEMENT SOON
    private LoaderManager.LoaderCallbacks<ArrayList<HeritageItem>> heritageItemLoader
            = new LoaderManager.LoaderCallbacks<ArrayList<HeritageItem>>() {

    /*
    Will be implemented soon Send token of the user to like or not.
     */
    private class likeAction extends AsyncTask<String, String, String> {

        Context mContext;
        boolean LikeSuccessful;

        private likeAction(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String token = preferences.getString("token", "null");
                LikeSuccessful = uploadLikePic(token);
                if (LikeSuccessful) {
                    try {
                        LikeSuccessful = uploadLikeCount(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (LikeSuccessful) {
                //Post Execute
                ImageButton likeButton = (ImageButton) findViewById(R.id.like_btn);
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.ic_notlike);
                } else {
                    likeButton.setImageResource(R.drawable.ic_like);
                }

            } else {
                Toast.makeText(mContext, "Like unsuccessful", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {

        }

        private boolean uploadLikePic(String token) {
            String result;
            itemUrl = "http://18.220.108.135/api/items/" + heritageItemPostID;
            try {
                result = PostJSON.postToApi(constructTheJSONLikePic(), itemUrl, token);
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            if (result == null || result.equals("400")) {
                return false;
            }

            return true;

        }

        private JSONObject constructTheJSONLikePic() {

            Log.v("HeritageItemLike", "" + getIntent().getBooleanExtra("is_rated", false));
            JSONObject json = new JSONObject();
            try {

                if (getIntent().getBooleanExtra("is_rated", false)) {
                    json.put("is_rated", false);
                    isLiked = false;
                } else {
                    json.put("is_rated", true);
                    isLiked = true;
                }

                Log.v("RATE TAG", "" + json.getBoolean("is_rated"));


                return json;
            } catch (JSONException e) {
                Log.v("like", "Error in json construction");
            }
            return json;
        }

        private boolean uploadLikeCount(String token) {
            String result;
            ratesUrl = "http://18.220.108.135/api/items/" + heritageItemPostID + "/rates";
            try {
                result = PostJSON.postToApi(constructTheJSONLikeCount(), ratesUrl, token);
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            if (result == null || result.equals("400")) {
                return false;
            }

            return true;

        }

        private JSONObject constructTheJSONLikeCount() {

            JSONObject json = new JSONObject();
            try {

                if (!isLiked) {
                    json.put("rate", json.getInt("rate") - 1);
                } else {
                    json.put("rate", json.getInt("rate") + 1);
                }

                Log.v("RATE TAG", "" + json.getInt("rate"));


                return json;
            } catch (JSONException e) {
                Log.v("like", "Error in json construction");
            }
            return json;
        }
    }

}
