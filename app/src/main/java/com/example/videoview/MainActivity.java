package com.example.videoview;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    ViewPager2 viewPageVideos;

    ArrayList<HashMap<String, String>> arrayList, onlineVideoList, deleteVideoList;

    ArrayList<String> pathList ;

    HashMap<String, String> hashMap = new HashMap<>();

    MediaPlayer mediaPlayer, mediaPlayer2 ;

    ProgressBar progressBar;



    int videoCount = 2;


    int videoNumber = 0;
    int request_code = 100 ;

    boolean isFrist = true ;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPageVideos = findViewById(R.id.viewPageVideos);
        progressBar = findViewById(R.id.progressBar);



        if (isFrist) {
            deleteVideoList = new ArrayList<>() ;
            pathList = new ArrayList<>() ;
            isFrist= false ;
        }




        permission();
        fetchData();




    }



    public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {



        class VideoViewHolder extends RecyclerView.ViewHolder{

            VideoView videoView ;
            TextView profile_name, description, like_count, comments_count, song_name;
            CircleImageView profile_image, add, song_image ;
            ImageView favourite, comments, share, download ;




            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);


                videoView = itemView.findViewById(R.id.videoView);



                description = itemView.findViewById(R.id.description);
                profile_name = itemView.findViewById(R.id.profile_name);
                song_name = itemView.findViewById(R.id.song_name);
                like_count = itemView.findViewById(R.id.like_count);
                comments_count = itemView.findViewById(R.id.comments_count);

                profile_image = itemView.findViewById(R.id.profile_image);
                add = itemView.findViewById(R.id.add);
                song_image = itemView.findViewById(R.id.song_image);

                favourite = itemView.findViewById(R.id.favourite);
                comments = itemView.findViewById(R.id.comments);
                share = itemView.findViewById(R.id.share);
                download = itemView.findViewById(R.id.download);


            }




            void setVideoData(String videoUrl) {



                videoView.setVideoPath(videoUrl);
                videoView.setOnPreparedListener(mp -> {

                    try {
                        mp.start();
                        mediaPlayer = mp ;

                        float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight() ;
                        float screenRatio = videoView.getWidth() / (float) videoView.getHeight() ;

                        float scale = videoRatio / screenRatio ;


                        if (scale >= 1f ) {
                            videoView.setScaleX(scale);
                        }
                        else  {
                            videoView.setScaleY(1f / scale);
                        }
                    }catch (Exception e) {

                    }


                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                       try {
                           mp.start();
                       } catch (Exception e) {

                       }

                    }
                });







            }






        }





        @NonNull
        @Override
        public VideosAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VideosAdapter.VideoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.items_videos_container, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onBindViewHolder(@NonNull VideosAdapter.VideoViewHolder holder, int position) {


            progressBar.setVisibility(View.GONE);


            try {

               hashMap = arrayList.get(position);
               String url_two = hashMap.get("videourl");
                String id = hashMap.get("videoid");
               String vtitle = hashMap.get("vtitle");
               String description = hashMap.get("description");
               holder.setVideoData(url_two);






                if (pathList.contains(url_two)) {

               } else {

                   deleteVideoList.add(hashMap) ;

               }



//                pathList.add(url_two);


               int positonnumber = position+1 ;


                holder.profile_name.setText("@"+ positonnumber);
                holder.description.setText(""+arrayList.size());

//                Picasso.get()
//                        .load(profile)
//                        .placeholder(R.drawable.user)
//                        .into(holder.profile_image) ;


           } catch (Exception e) {

           }












//
//            holder.videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    mediaPlayer.pause();
//                }
//            });
//
//
//            holder.share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//
//
//


//
//
            int number = position+1 ;

            int half = arrayList.size()/2 ;


            if ( half == number) {
                videoDownLoad(videoNumber+10);

                Toast.makeText(MainActivity.this, "half", Toast.LENGTH_SHORT).show();
            }


            if (number == arrayList.size()) {
                deleteVideo();
                Toast.makeText(MainActivity.this, "LastVideo", Toast.LENGTH_SHORT).show();

            }

//
//



        }

        @Override
        public int getItemCount() {


            return arrayList.size();
        }

    }





    private void permission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
        {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},request_code );
        }
        else {

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == request_code){


        }else {


        }
    }

    private void LoadData() {

        String url = "https://ohomasti.com/canvit/ohomastapi/videosforyou.php?fbclid=IwAR3PjuU0GlZudPyo5Lh3_rJAMpHR8GGcG5cbKuX7kp6kfPC7PY2WGe02who";


        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);

                        arrayList = new ArrayList<>();

                        try {
                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject jsonObject = data.getJSONObject(i);

                                String vid = jsonObject.getString("vid");
                                String vtitle = jsonObject.getString("vtitle");
                                String videourl = jsonObject.getString("videourl");
                                String video_thum = jsonObject.getString("video_thum");
                                String likes = jsonObject.getString("likes");
                                String views = jsonObject.getString("views");
                                String description = jsonObject.getString("description");
                                String status = jsonObject.getString("status");
                                String date = jsonObject.getString("date");
                                String tags = jsonObject.getString("tags");
                                String uid = jsonObject.getString("uid");
                                String profile = jsonObject.getString("profile");
                                String aid = jsonObject.getString("aid");
                                String audio_url = jsonObject.getString("audio_url");


                                hashMap = new HashMap<>();
                                hashMap.put("vid", vid);
                                hashMap.put("vtitle", vtitle);
                                hashMap.put("videourl", videourl);
                                hashMap.put("video_thum", video_thum);
                                hashMap.put("likes", likes);
                                hashMap.put("views", views);
                                hashMap.put("description", description);
                                hashMap.put("status", status);
                                hashMap.put("date", date);
                                hashMap.put("tags", tags);
                                hashMap.put("uid", uid);
                                hashMap.put("profile", profile);
                                hashMap.put("aid", aid);
                                hashMap.put("audio_url", audio_url);


                                arrayList.add(hashMap);


                            }

                            viewPageVideos.setAdapter(new VideosAdapter());

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);


            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);


    }

    private void fetchData() {



        progressBar.setVisibility(View.VISIBLE);


        String url = "https://xpart.top/file/load_video.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        progressBar.setVisibility(View.GONE);


                        onlineVideoList = new ArrayList<>();
                        for (int i=0;i<response.length();i++) {

                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                String id = jsonObject.getString("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String video_url = jsonObject.getString("video_url");



                                hashMap = new HashMap<>();
                                hashMap.put("id",id);
                                hashMap.put("title",title);
                                hashMap.put("description",description);
                                hashMap.put("video_url",video_url);


                                onlineVideoList.add(hashMap);




                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }



                        }

                        videoDownLoad(10);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.VISIBLE);


                Toast.makeText(MainActivity.this,"check  your internet",Toast.LENGTH_LONG).show();

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonArrayRequest);



    }


    private void deleteVideo() {

        Toast.makeText(this, "delete Calling", Toast.LENGTH_SHORT).show();

        try {
            for (int i=0; i<deleteVideoList.size()-10; i++) {

                hashMap = deleteVideoList.get(i);
                String path = hashMap.get("videourl");
                String id = hashMap.get("videoid");

                File file = new File(path) ;


                Uri contentUri = ContentUris
                        .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(id));



                if (file.exists()) {
                    boolean delete = file.delete();

                    if (delete){
                        MainActivity.this.getContentResolver().delete(contentUri, null, null) ;
                        Toast.makeText(MainActivity.this, "file deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "file con not deleted", Toast.LENGTH_SHORT).show();
                    }




                }


            }

        } catch (Exception e) {

        }
        offLineVideosList();

    }
    private void videoDownLoad(int j) {

        try {
            if (onlineVideoList.size() >= videoNumber) {


                for(int a = videoNumber ; a<j;  a++){


                    hashMap = onlineVideoList.get(a);
                    String getUrl = hashMap.get("video_url") ;

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getUrl)) ;
                    String title = URLUtil.guessFileName(getUrl, null, null);
                    String cookie = CookieManager.getInstance().getCookie(getUrl);
                    request.setDestinationInExternalPublicDir("ohomasti", title) ;
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE) ;

                    downloadManager.enqueue(request);
                    Toast.makeText(MainActivity.this, "downloading" +  a, Toast.LENGTH_SHORT).show();


                    videoNumber++ ;
                }
            }

            if (arrayList.size() == 0) {
                offLineVideosList();
            }


        } catch (Exception e) {

        }


    }




    public void offLineVideosList() {


        viewPageVideos.setVisibility(View.GONE);

        arrayList = new ArrayList<>() ;

        Uri url = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {

                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME

        } ;


        Cursor cursor = getContentResolver().query(url, projection, null, null, null) ;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String videoid = cursor.getString(0) ;
                String path = cursor.getString(1) ;





                if (path.contains("/storage/emulated/0/ohomasti/")) {
                    hashMap = new HashMap<>();
                    hashMap.put("videourl", path);
                    hashMap.put("videoid", videoid);
                    hashMap.put("uid", "uid");
                    hashMap.put("description", "description");

                    arrayList.add(hashMap) ;
                    Log.d("path", path) ;

                }

            }

            cursor.close();
        }



        if (arrayList.size() != 0){
            viewPageVideos.setVisibility(View.VISIBLE);

            Toast.makeText(this, "off video List", Toast.LENGTH_SHORT).show();
            viewPageVideos.setAdapter(new VideosAdapter());
            videoCount = arrayList.size() ;



        }
        else {
            fetchData();
        }

        viewPageVideos.setVisibility(View.VISIBLE);


    }



}