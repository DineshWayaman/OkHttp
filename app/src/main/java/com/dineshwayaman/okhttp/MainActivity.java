package com.dineshwayaman.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dineshwayaman.okhttp.Models.ResponseModel;
import com.google.gson.Gson;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    Realm realm;

    EditText edtName, edtPassword;
    Button btnLogin;
    String loginApi = "https://dineshwayaman.com/cba/";


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        int id = sharedPreferences.getInt("id", 0);

        if(id == 0){

            initWidgets();
            realmConfig();

            btnLogin.setOnClickListener(v -> {
                String name = edtName.getText().toString();
                String password = edtPassword.getText().toString();
                if(name.length() == 0 || password.length() == 0){
                    Toast.makeText(this, "Both fields are mandatory", Toast.LENGTH_SHORT).show();
                }else {
                    siginIn(name, password);
                }

            });
        }else{

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

        }











    }

    private void realmConfig() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);
    }

    private void siginIn(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody postData = new FormBody.Builder()
                        .add("name", name)
                        .add("password", password)
                        .build();

                Request request = new Request.Builder()
                        .url(loginApi)
                        .post(postData)
                        .build();

                OkHttpClient httpClient = new OkHttpClient();
                Call call = httpClient.newCall(request);

                Response response = null;

                try {
                    response = call.execute();
                    String serverResponse = response.body().string();

                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(serverResponse, ResponseModel.class);


                    if (responseModel.getRes_code() == 0){

                        int userID = responseModel.getUser_data().getId();



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtRes = findViewById(R.id.textView);
                                txtRes.setText(String.valueOf(userID));

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("id", userID);
                                editor.apply();



                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        realm.deleteAll();

                                        try{
                                            realm.copyToRealm(responseModel.getUser_data());
                                            Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(i);
                                        }catch (Exception e){
                                            Log.e("TAG", e.toString(), e);
                                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        });


                    }else{

                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "User Name or Password Incorrect", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        };
                        thread.start();

                    }




                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initWidgets() {

        edtName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);


    }
}