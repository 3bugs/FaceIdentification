package com.example.faceidentification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    private View screenWait;

    private EditText editName, editLastname, editEmail, editPassword, editConfirmPassword, editPhone;
    private Button btnRegister, btnCancel;

    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        screenWait = findViewById(R.id.screen_wait);

        editName = (EditText) findViewById(R.id.name);
        editLastname = (EditText) findViewById(R.id.lastname);
        editEmail = (EditText) findViewById(R.id.email);
        editPassword = (EditText) findViewById(R.id.password);
        editConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        editPhone = (EditText) findViewById(R.id.phone);

        btnRegister = (Button) findViewById(R.id.register_button);
        btnCancel = (Button) findViewById(R.id.cancel_button);

        btnRegister.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // ปุ่ม Log in
        if (viewId == R.id.register_button) {
            if (validateRegisterForm()) {
                screenWait.setVisibility(View.VISIBLE);

                String name = editName.getText().toString().trim();
                String lastname = editLastname.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String phone = editPhone.getText().toString().trim();
                try {
                    registerNewUser(name, lastname, email, password, phone);
                } catch (IOException e) {
                    e.printStackTrace();
                    showModalOkDialog("Error", "Unexpected server response: " + e.getMessage());
                }
            }
        }
        // ปุ่ม Log out
        else if (viewId == R.id.cancel_button) {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Registration")
                    .setMessage("Are you sure?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private boolean validateRegisterForm() {
        boolean validForm = true;

        if ("".equals(editName.getText().toString().trim())) {
            editName.setError("Enter name");
            validForm = false;
        }
        if ("".equals(editLastname.getText().toString().trim())) {
            editLastname.setError("Enter lastname");
            validForm = false;
        }
        if ("".equals(editEmail.getText().toString().trim())) {
            editEmail.setError("Enter e-mail");
            validForm = false;
        }

        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        if ("".equals(password)) {
            editPassword.setError("Enter password");
            validForm = false;
        }
        if ("".equals(confirmPassword)) {
            editConfirmPassword.setError("Enter password again");
            validForm = false;
        } else if (!"".equals(password) && !password.equals(confirmPassword)) {
            editConfirmPassword.setError("Password and confirm password must be the same");
            validForm = false;
        }

        return validForm;
    }

    private void registerNewUser(String name, String lastname, String email, String password,
                                 String phone) throws IOException {
        String url = Uri.parse("http://promlert.com/faceid/insert.php")
                .buildUpon()
                .appendQueryParameter(MainActivity.COL_NAME, name)
                .appendQueryParameter(MainActivity.COL_LASTNAME, lastname)
                .appendQueryParameter(MainActivity.COL_EMAIL, email)
                .appendQueryParameter(MainActivity.COL_PASSWORD, password)
                .appendQueryParameter(MainActivity.COL_PHONE, phone)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        screenWait.setVisibility(View.VISIBLE);
                        showModalOkDialog("Error", "Unable to connect to server.");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        screenWait.setVisibility(View.GONE);
                    }
                });

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    int success = json.getInt("success");

                    if (success == 1) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "Register successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        String email = editEmail.getText().toString();
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.USERNAME_INTENT_KEY, email);
                        setResult(RESULT_OK, intent);
                        finish();

                    } else if (success == 0) {
                        // แจ้ง error ด้วย dialog
                        final String message = json.getString("message");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showModalOkDialog("Error", message);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showModalOkDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
