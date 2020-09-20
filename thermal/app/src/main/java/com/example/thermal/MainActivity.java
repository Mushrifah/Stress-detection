package com.example.thermal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mChooseBtn;
    Button mAnalyzeBtn;
    TextView result;
    TextView link;


    private Uri uri = null;
    private static final int MY_PERMISSION_REQUEST = 100;
    private static final int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_img_btn);
        mAnalyzeBtn = findViewById(R.id.analyze_btn);
        result= findViewById(R.id.responseText);
        link= findViewById(R.id.link_text);


        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, DataActivity.class);
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "you clicked", Toast.LENGTH_SHORT).show();
            }
        });

        String linkText= "To check if plant can be revived click here";
        SpannableString ss= new SpannableString(linkText);
        SpannableStringBuilder ssb= new SpannableStringBuilder(linkText);



        //clickable string
        ClickableSpan clickableSpan= new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //Toast.makeText(MainActivity.this, "one", Toast.LENGTH_SHORT).show();
            }

            //color the clickable string
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
            }
        };
        ss.setSpan(clickableSpan, 33, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        link.setText(ss);
        link.setMovementMethod(LinkMovementMethod.getInstance());
      // result.setText("hey");


        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_FROM_GALLERY_REQUEST);
            }
        });

        mAnalyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bmp = getBitmap(MainActivity.this, uri);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, 512, 512, true);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                            byte[] byteArray = stream.toByteArray();
                            bmp.recycle();
                            uploadImage(byteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void uploadImage(byte[] imageBytes) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", "photo.jpg", requestFile);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://102d8c1b.ngrok.io")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        ImageClient imageClient = retrofit.create(ImageClient.class);
        Call<ResponseBody> call = imageClient.uploadImage(file);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String final_result = response.body().string();
                    //Toast.makeText(MainActivity.this, "Success "+final_result, Toast.LENGTH_SHORT).show();
                    result.setText("Result: " + final_result);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Main", "onFailure: " + t);
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                String test = "anam abcded";
                result.setText(test);
            }
        });

    }

    private void stressResult(){
        //result= findViewById(R.id.responseText);
//        result.setText(anamAbc);
       //Log.d("Main", "onSuccess:" +res.toString());

        //System.out.println(res);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST) {
            uri = data.getData();
            Log.d("Main", "onActivityResult: " + uri);
            mImageView.setImageURI(uri);

        }
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

}
