package com.dineshwayaman.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.dineshwayaman.okhttp.Models.UserData;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HomeActivity extends AppCompatActivity {

    TextView txtName, txtEmail, txtDOB, txtCompany, txtPosition;
    SharedPreferences sharedPreferences;

    int u_id;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        u_id = sharedPreferences.getInt("id", 0);


        initWidgets();
        realmConfig();

        showLogedUserData(u_id);


        
        
    }

    private void showLogedUserData(final int u_id) {
        final UserData dataModel = realm.where(UserData.class).equalTo("id", u_id).findFirst();

        txtName.setText(dataModel.getName());
        txtEmail.setText(dataModel.getEmail());
        txtDOB.setText(dataModel.getDob());
        txtCompany.setText(dataModel.getCompany());
        txtPosition.setText(dataModel.getPosition());

    }

    private void initWidgets() {
        
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtDOB = findViewById(R.id.txtDOB);
        txtCompany = findViewById(R.id.txtCompany);
        txtPosition = findViewById(R.id.txtPosition);
        
        
    }

    private void realmConfig() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);
    }
}