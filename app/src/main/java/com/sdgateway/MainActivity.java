package com.sdgateway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sdgateway.Adapters.SendListAdapter;
import com.sdgateway.Contracts.Message;
import com.sdgateway.Helper.SharedPref;
import com.sdgateway.Modules.Models.Message.FetchMessageResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Handler mWaitHandler = new Handler();
    private final static int SEND_SMS_PERMISSION_REQ = 1;

    Retrofit retrofit;
    Message apiService;
    Disposable disposable;
    private SendListAdapter sendListAdapter;
    private String device_id;
    private RecyclerView recyclerView;
    private List<FetchMessageResponse> messages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, SEND_SMS_PERMISSION_REQ);

        TextView userPhoneNumber = findViewById(R.id.userPhoneNumber);
        TextView userDeviceId = findViewById(R.id.userDeviceId);
        recyclerView = findViewById(R.id.send_list);

        sendListAdapter = new SendListAdapter(getApplicationContext(), messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

//        recyclerView.addItemDecoration(new DividerItemDecoration(this,1));

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(sendListAdapter);


        device_id =  SharedPref.getSharedPreferenceString(this, "device_id", "");
        userDeviceId.setText(String.format("Device ID : %s", device_id));
        userPhoneNumber.setText(String.format("Phone Number : %s", SharedPref.getSharedPreferenceString(this, "phone_number", "")));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(Message.class);


        disposable = Observable.interval(5000, 5000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callMessageEndpoint, this::onError);
    }

    private boolean checkIfSuccess(String phoneNumber, String code) {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},"address=?", new String[]{phoneNumber},null);


        cursor.moveToFirst();
        while  (cursor.moveToNext())
        {
            String date = cursor.getString(2);
            String body = cursor.getString(3);

            if (body.contains(code)) {
                return true;
            }

        }
        return false;
    }


    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
    }

    private void callMessageEndpoint(Long aLong) {


        Call<List<FetchMessageResponse>> observable = apiService.fetch(device_id );
        observable.enqueue(new Callback<List<FetchMessageResponse>>() {
            @Override
            public void onResponse(Call<List<FetchMessageResponse>> call, Response<List<FetchMessageResponse>> response) {
                if(checkPermission(Manifest.permission.SEND_SMS))
                {
                    for(FetchMessageResponse r : response.body()) {
                        MainActivity.this.sendSMS(r.getPhoneNumber(), r.getMessage());
                        MainActivity.this.messages.addAll(response.body());
                        MainActivity.this.sendListAdapter.notifyDataSetChanged();
                        messages.addAll(response.body());
                        sendListAdapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, "Processing new message", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FetchMessageResponse>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }


    /* access modifiers changed from: private */
    public void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        String str = "deprecation";
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                int resultCode = getResultCode();
                if (resultCode == -1) {
                    Toast.makeText(MainActivity.this, "SMS sent", Toast.LENGTH_LONG).show();
                } else if (resultCode != 1) {
                    if (resultCode == 3 || resultCode != 4) {
                    }
                } else {
                    Toast.makeText(MainActivity.this, "SMS failed to send", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(SENT));
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                int resultCode = getResultCode();
                if (resultCode == -1) {
                    Toast.makeText(MainActivity.this, "SMS delivered", Toast.LENGTH_LONG).show();
                } else if (resultCode == 0) {
                    Toast.makeText(MainActivity.this, "SMS not delivered", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    private boolean checkPermission(String sendSms) {

        int checkpermission = ContextCompat.checkSelfPermission(this,sendSms);
        return checkpermission == PackageManager.PERMISSION_GRANTED;
    }









}
