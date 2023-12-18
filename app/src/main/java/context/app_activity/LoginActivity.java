package context.app_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmusic_group.MainActivity;
import com.example.playmusic_group.R;

import context.app_data.AppLogin;
import context.app_data.Customer;
import context.app_default.AppDefault;
import context.app_sql.CustomerHelper;
import danhsach.List;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, buttonNoLogin;


    private TextView registerTextView;

    CustomerHelper _customerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        _customerHelper = new CustomerHelper(this);

        loginButton = findViewById(R.id.loginButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerTextView = findViewById(R.id.registerTextView);
        buttonNoLogin = findViewById(R.id.button_nologin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform login authentication
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                boolean check = Auth(username,password);

                if( _customerHelper.getCustomerByUsername((username)) == null){
                    Toast.makeText(LoginActivity.this, "Tài khoản chưa được đăng ký", Toast.LENGTH_SHORT).show();
                }
                if (check) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    backHome();
                    return;
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }


            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRegister();
            }
        });

        buttonNoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppDefault.CurrentCustomerKey, AppDefault.CustomerDefaultUsername);
                editor.apply();
                _customerHelper.InsertCustomerDefault();
                backHome();
            }
        });


    }



    public void ShowRegister(){
        Intent intentActiveList = new Intent(this,RegisterActivity.class);
        startActivity(intentActiveList);

    }

    public  boolean Auth(String username, String password){
        AppLogin appLogin = new AppLogin();
        appLogin.Username = username;
        appLogin.Password = password;
        Customer customer = _customerHelper.getCustomerForLogin(appLogin);
        if(customer != null){
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppDefault.CurrentCustomerKey, customer.getUserName());
            editor.apply();
            return  true;
        }
        return false;
    }

    public void backHome() {
        Intent intentActiveList = new Intent(this, MainActivity.class);
        intentActiveList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentActiveList);
        finish();
    }

}
