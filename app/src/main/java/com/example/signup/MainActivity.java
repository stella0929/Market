package com.example.signup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainActivity extends AppCompatActivity {
// 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
// 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
// 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextphonenumber;
    private EditText editTextname;
    private String email = "";
    private String password = "";
    private String name = "";
    private String phone = "";


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersRef = mRootRef.child("users");
    Button sendmailButton;
    EditText checknumber;
    Button checkButton;
    boolean useful=false;

    public static class User {

        public String name;
        public String birth;

        public User(String name, String birth) {
            this.name=name;
            this.birth=birth;
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
// 파이어베이스 인증 객체 선언
        sendmailButton= findViewById(R.id.sendmailButton);
        checkButton=findViewById(R.id.checkButton);
        checknumber=findViewById(R.id.checknumber);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.passwords);
        editTextname= findViewById(R.id.name);
        editTextphonenumber=findViewById(R.id.phonenumber);
        Button button=findViewById(R.id.button);
      final int random = (int) (Math.random() * 10000000+100000);

        sendmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                try {
                    GMailSender gMailSender = new GMailSender("enjoynow0929@gmail.com", "비번");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("제목입니다","인증번호"+random, email);
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//                final String user = "enjoynow0929@gmail.com";
//                final String password = "비번";
//
//                Properties prop = new Properties();
//                prop.put("mail.smtp.host", "smtp.gmail.com");
//                prop.put("mail.smtp.port", 465);
//                prop.put("mail.smtp.auth", "true");
//                prop.put("mail.smtp.ssl.enable", "true");
//                prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//
//                Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(user, password);
//                    }
//                });
//
//                try {
//                    MimeMessage message = new MimeMessage(session);
//                    message.setFrom(new InternetAddress(user));
//
//                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//
//                    message.setSubject("메일 테스팅중....!");
//                    message.setText("메일 내용 테스트");
//
//                    Transport.send(message);
//
//                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
//
//                } catch (AddressException e) {
//                    // TODO: handle exception
//                    Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
//                } catch (MessagingException e) {
//                    // TODO: handle exception
//                    Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
//                }
//            }});
        checkButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                if (random == Integer.valueOf(checknumber.getText().toString()))
                    useful = true;

            }
            });
        button.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                signUp(v);
            }
            });
        }

    public void signUp(View view) {

        password = editTextPassword.getText().toString();
        name=editTextname.getText().toString();
        phone=editTextphonenumber.getText().toString();


        if(isValidEmail() && isValidPasswd()&&isVaildname()&&useful) {
            createUser(email, password,name,phone);
        }

    }


// public void signIn(View view) {

// email = editTextEmail.getText().toString();

// password = editTextPassword.getText().toString();

//

// if(isValidEmail() && isValidPasswd()) {

// loginUser(email, password);

// }

// }

    private boolean isVaildname(){
        if (name.equals("")) {
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidEmail() {
        if (email.isEmpty()) {
// 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
// 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }
// 비밀번호 유효성 검사

    private boolean isValidPasswd() {

        if (password.isEmpty()) {
// 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
// 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }


// 회원가입

    private void createUser(final String email, String password,final String name, final String birth) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// 회원가입 성공
                            Toast.makeText(MainActivity.this, "회원가입성공", Toast.LENGTH_SHORT).show();
                            Map<String, User> users = new HashMap<>();
                            users.put(email, new User(name,birth));
                            usersRef.setValue(users);
                        } else {
// 회원가입 실패
                            Toast.makeText(MainActivity.this,"회원가입실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void loginUser(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// 로그인 성공
                            Toast.makeText(MainActivity.this, "로그인성공", Toast.LENGTH_SHORT).show();
                        } else {
// 로그인 실패
                            Toast.makeText(MainActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();

                        }

                    }

                });

    }



}

