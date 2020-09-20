package com.example.thermal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thermal.model.Data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class DataActivity extends AppCompatActivity {
    EditText tdry;
    EditText twet;
    EditText tcanopy;
    EditText timeDay;
    TextView resultData;
    Button mReviveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        tdry = findViewById(R.id.t_dry);
        twet = findViewById(R.id.t_wet);
        tcanopy = findViewById(R.id.t_canopy);
        timeDay = findViewById(R.id.time_day);
        mReviveBtn = findViewById(R.id.revive_btn);
        resultData =findViewById(R.id.resultData);

        mReviveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Data data= new Data(
                        Integer.parseInt(tdry.getText().toString()),
                        Integer.parseInt(twet.getText().toString()),
                        Integer.parseInt(tcanopy.getText().toString()),
                        Integer.parseInt(timeDay.getText().toString())
                );

                reviveData(data);
            }
        });
    }
        private void  reviveData(Data data){
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(200, TimeUnit.SECONDS)
                    .readTimeout(200, TimeUnit.SECONDS).build();

            //HttpLoggingInterceptor logging= new HttpLoggingInterceptor();
           // logging.setLevel(HttpLoggingInterceptor.Level.BODY)
           // okHttpClientBuilder.addInterceptor(logging);

            Gson gson = new GsonBuilder()
                    .setLenient() //building as lenient mode`enter code here`
                   .create();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://102d8c1b.ngrok.io")
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson));

            Retrofit retrofit =builder.build();

            UserClient userClient = retrofit.create(UserClient.class);
            Call<ResponseBody> call = userClient.reviveData(data);

            call.enqueue(new Callback <ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response <ResponseBody> response) {
                    Log.d("Main", "done: " + response);
                    //Toast.makeText(DataActivity.this, "Success"+ response.body().toString(), Toast.LENGTH_SHORT).show();
                    String revive_result= "";
                    try {
                        revive_result = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(DataActivity.this, "Success "+revive_result, Toast.LENGTH_SHORT).show();
                    resultData.setText(revive_result);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Main", "onFailure: " + t);
                    Toast.makeText(DataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    String test = "We are sorry";
                    resultData.setText(test);
                    //Toast.makeText(DataActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }
