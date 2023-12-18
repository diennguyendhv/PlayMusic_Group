package context.app_activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmusic_group.MainActivity;
import com.example.playmusic_group.R;

import context.app_data.Customer;
import context.app_default.AppDefault;
import context.app_sql.CustomerHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, nameEditText, repasswordEditText;
    private Button registerButton;

    private TextView loginTexView;

    CustomerHelper _customerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _customerHelper = new CustomerHelper(this);
        registerButton = findViewById(R.id.RegiterButton);
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordRegisterEditText);
        repasswordEditText = findViewById(R.id.rePasswordEditText);
        loginTexView = findViewById(R.id.loginTextView);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform login authentication
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String name = nameEditText.getText().toString();

                if(username.length() < 5){
                    Toast.makeText(RegisterActivity.this, "Tên đăng nhập phải có ít nhất 5 ký tự", Toast.LENGTH_SHORT).show();
                }

                if(name.length() < 3){
                    Toast.makeText(RegisterActivity.this, "Tên người dùng phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
                }

                if(password.length() < 5){
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 5 ký tự", Toast.LENGTH_SHORT).show();
                }

                if(password != password){
                    Toast.makeText(RegisterActivity.this, "Mật khẩu nhập  lại không chính xác", Toast.LENGTH_SHORT).show();
                }

                if(!CheckExist((username))){
                    // Load saved credentials if available
                    SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Customer customer = new Customer(name,username,password,1);
                    _customerHelper.insertCustomer(customer);
                    editor.putString(AppDefault.CurrentCustomerKey, username);
                    editor.apply();
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    GoHome();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Đã có người sử dụng tên đăng nhập này", Toast.LENGTH_SHORT).show();
                }


            }
        });


        loginTexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogin();
            }
        });
    }
    private void  showLogin(){
        Intent intentActiveLogin = new Intent(this, LoginActivity.class);
        startActivity(intentActiveLogin);
    }

    private void  GoHome(){
        Intent intentActiveList = new Intent(this, MainActivity.class);
        intentActiveList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentActiveList);
        finish();
    }

    private boolean CheckExist(String username){
       Customer customer = _customerHelper.getCustomerByUsername(username);
       if(customer != null){
           return  true;
       }
       return  false;
    }



}
