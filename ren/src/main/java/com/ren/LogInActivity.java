package com.ren;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;


public class LogInActivity extends AppCompatActivity {

    private boolean isLoggingIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.log_in_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        //getSupportActionBar().setHomeButtonEnabled(true); // Show the return button

        // Do not allow back button on login page.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Return only one level

        setTitle(getResources().getString(R.string.log_in));
        // Add listeners
        final CheckBox checkBox = (CheckBox) findViewById(R.id.new_user_checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Button button = (Button) findViewById(R.id.log_in_register);
                if (isChecked) {
                    button.setText(R.string.log_in_register);
                } else {
                    button.setText(R.string.log_in);
                }
            }
        });

        Button loginButton = (Button) findViewById(R.id.log_in_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.login_username);
                String uName = editText.getText().toString();
                editText = (EditText) findViewById(R.id.login_password);
                String password = editText.getText().toString();
                if (uName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_uname), Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_password), Toast.LENGTH_LONG).show();
                    return;
                }
                // See whether to log in or register
                if (checkBox.isChecked()) {
                    registerNewUser(uName, password);
                } else {
                    logIn(uName, password);
                }
            }
        });
    }

    private void registerNewUser(String uName, String password) {
        BackgroundConn bckConn = new BackgroundConn(this);
        // Register process needs modification
        bckConn.execute("register", uName, password);
        CheckBox checkBox = (CheckBox) findViewById(R.id.new_user_checkBox);
        checkBox.setChecked(false);
    }

    private void logIn(String uName, String password) {

        if( !isLoggingIn ) {
            BackgroundConn bckConn = new BackgroundConn(this);
//        Log.e("LoginActivity", "Sending background login attempt");
            bckConn.execute("login", uName, password);
            isLoggingIn = true;
        }

    }

    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EditText editText = (EditText) findViewById(R.id.login_username);
        editText.setText(prefs.getString("Login_uname", ""));
        editText = (EditText) findViewById(R.id.login_password);
        editText.setText(prefs.getString("Login_password", ""));
    }

    protected void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        EditText editText = (EditText) findViewById(R.id.login_username);
        editor.putString("Login_uname", editText.getText().toString());
        editText = (EditText) findViewById(R.id.login_password);
        editor.putString("Login_password", editText.getText().toString());
        editor.apply();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String is_logged_in = sp.getString( "Login uname", "" );

        if( is_logged_in != null && !is_logged_in.equals("") ) {
            if( ContactsFragment.mTabs.get() != null && ContactsFragment.mTabs.get().getViewPager() != null && ContactsFragment.mTabs.get().getViewPager().getAdapter() != null ) {
                ((MyPagerAdapter) ContactsFragment.mTabs.get().getViewPager().getAdapter()).refreshTabs();
            }

            isLoggingIn = false;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
