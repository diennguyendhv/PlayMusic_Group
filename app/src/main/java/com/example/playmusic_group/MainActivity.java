package com.example.playmusic_group;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import com.example.playmusic_group.Alarm.AlarmReceiver;

import context.app_activity.LoginActivity;

import com.example.playmusic_group.equalizer.DialogEqualizerFragment;
import com.example.playmusic_group.play_music_background.MusicConfig;
import com.example.playmusic_group.play_music_background.MusicService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import context.DataMusic;
import context.app_default.AppDefault;
import context.app_sql.CustomerHelper;
import danhsach.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    DataMusic data = new DataMusic();
    ArrayList<BaiHat> arrayBaiHat = data.arrayBaiHat;
    TextView txtTitle, txtCasi, txtTimeStart, txtTimeEnd;
    SeekBar skBar;
    ImageButton btnPre, btnPlay, btnNext, btnTron, btnLap, btnSetting, btnList;
    ImageView music_compact;
    MediaPlayer mediaPlayer;
    Animation animation;
    private Button btnScheduleAlarm;
    int playId = 0;
    int lap = 0;
    int tron = 0;
    private int selectedHour;
    private int selectedMinute;

    Random random = new Random();
    DialogEqualizerFragment settingFragment;
    private static final int ALARM_REQUEST_CODE = 123;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MusicConfig.Actions.PLAY_ACTION, MusicConfig.Actions.PAUSE_ACTION -> playMusic();
                    case MusicConfig.Actions.PREV_ACTION -> onPreClicked();
                    case MusicConfig.Actions.NEXT_ACTION -> onNextClicked();
                    case MusicConfig.Actions.CANCEL_ACTION -> {
                        mediaPlayer.stop();
                        stopMusicService();
                        return;
                    }
                }
                startMusicService(action);
            }
        }
    };

    private void logoutUser() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();
        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

    }

    void auth() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");

        if (!savedUsername.isEmpty()) {
           // Toast.makeText(MainActivity.this, "Đã đăng nhập vào tài khoản: " + savedUsername, Toast.LENGTH_SHORT).show();
        } else {
            CustomerHelper customerHelper = new CustomerHelper(this);
            customerHelper.InsertCustomerDefault();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppDefault.CurrentCustomerKey, AppDefault.CustomerDefaultUsername);
            editor.apply();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        auth();

        AnhXa();
        // Set up an OnPreDrawListener to the root view.
        Intent intent = getIntent();
        playId = intent.getIntExtra("playId", 0);
        if (playId > 0) {
            Log.d("id", String.valueOf(playId));
        } else {
            playId = 1;
        }
        PlayMusic();
//        mediaPlayer = new MediaPlayer();

        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        settingFragment = DialogEqualizerFragment.newBuilder()
                .setAudioSessionId(mediaPlayer.getAudioSessionId())
                .themeColor(ContextCompat.getColor(this, R.color.primaryColor))
                .textColor(ContextCompat.getColor(this, R.color.textColor))
                .accentAlpha(ContextCompat.getColor(this, R.color.playingCardColor))
                .darkColor(ContextCompat.getColor(this, R.color.primaryDarkColor))
                .setAccentColor(ContextCompat.getColor(this, R.color.secondaryColor))
                .build();
//        checkExistingAlarm();
        btnSetting.setOnClickListener(view -> {
            showSetting();
        });
        btnScheduleAlarm.setOnClickListener(view -> {
            showTimePickerDialog();
        });


        btnPre.setOnClickListener(v -> onPreClicked());

        btnNext.setOnClickListener(v -> onNextClicked());

        btnPlay.setOnClickListener(view -> {
            playMusic();
        });

        btnLap.setOnClickListener(v -> {
            if (lap == 0) {
                lap = 1;
                btnLap.setImageResource(R.drawable.lap_on);
            } else if (lap == 1) {
                lap = 2;
                btnLap.setImageResource(R.drawable.lap_mot);
            } else if (lap == 2) {
                lap = 0;
                btnLap.setImageResource(R.drawable.lap_off);
            }
        });

        btnTron.setOnClickListener(v -> {
            if (tron == 0) {
                tron = 1;
                btnTron.setImageResource(R.drawable.tron_bai_on);
            } else if (tron == 1) {
                tron = 0;
                btnTron.setImageResource(R.drawable.tron_bai_off);
            }
        });

        skBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(skBar.getProgress());
            }
        });

        btnList.setOnClickListener(view -> {
            showList();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        startMusicService(MusicConfig.Actions.CANCEL_ACTION);
        unRegisterReceiver();
        super.onDestroy();
    }

    private void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startMusicService(MusicConfig.Actions.PAUSE_ACTION);
            btnPlay.setImageResource(R.drawable.play);
            music_compact.clearAnimation();
        } else {
            startMusicService(MusicConfig.Actions.PLAY_ACTION);
            mediaPlayer.start();
            btnPlay.setImageResource(R.drawable.pause);
            music_compact.startAnimation(animation);
        }
        mediaPlayer.setLooping(false);
        SetTimeEnd();
        UpdateTime();
    }

    private void onPreClicked() {
        if (tron == 1) {
            playRandom();
        } else {
            playNormal(false);
        }
    }

    private void onNextClicked() {
        if (tron == 1) {
            playRandom();
        } else {
            playNormal(true);
        }
    }

    private int generateUniqueCode() {
        return (int) System.currentTimeMillis(); // Sử dụng thời gian hiện tại làm mã duy nhất
    }

    private void UpdateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat time = new SimpleDateFormat("mm:ss");
                txtTimeStart.setText(time.format(mediaPlayer.getCurrentPosition()));
                skBar.setProgress(mediaPlayer.getCurrentPosition());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (tron == 1) {
                            playRandom();
                        } else if (tron == 0) {
                            if (lap == 0) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                }
                                mediaPlayer.start();
                                btnPlay.setImageResource(R.drawable.play);
                                music_compact.clearAnimation();
                                mediaPlayer.stop();
                            } else if (lap == 1) {
                                playNormal(true);
                            } else if (lap == 2) {
                                mediaPlayer.start();
                            }
                        }
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private void SetTimeEnd() {
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        txtTimeEnd.setText(time.format(mediaPlayer.getDuration()));
        skBar.setMax(mediaPlayer.getDuration());
    }

    private void PlayMusic() {
        if (getById(playId).getFile() > 0) {
            mediaPlayer = MediaPlayer.create(MainActivity.this, getById(playId).getFile());
            txtTitle.setText(getById(playId).getTenBaiHat());
            txtCasi.setText(getById(playId).getTenCaSi());
        }
    }

    private void AnhXa() {
        txtTitle = findViewById(R.id.textViewTitle);
        txtCasi = findViewById(R.id.textViewSinger);
        txtTimeStart = findViewById(R.id.textViewTimeStart);
        txtTimeEnd = findViewById(R.id.textViewTimeEnd);
        skBar = findViewById(R.id.seekBar);
        btnPre = findViewById(R.id.buttonPre);
        btnPlay = findViewById(R.id.buttonPlay);
        btnNext = findViewById(R.id.buttonNext);
        btnTron = findViewById(R.id.buttonNgauNhien);
        btnLap = findViewById(R.id.buttonLap);
        music_compact = findViewById(R.id.musicCompact);
        btnSetting = findViewById(R.id.buttonSetting);
        btnList = findViewById(R.id.buttonList);
        btnScheduleAlarm = findViewById(R.id.btnScheduleAlarm);

    }

    private void showSetting() {
        if (settingFragment != null) {
            settingFragment.show(getSupportFragmentManager(), "eq");
        }
    }

    private void showList() {
        Intent intentActiveList = new Intent(this, List.class);
        int id = playId;
        intentActiveList.putExtra("id", id);
        startActivity(intentActiveList);
    }

    private void showLogin() {
        Intent intentActiveLogin = new Intent(this, LoginActivity.class);
        startActivity(intentActiveLogin);
    }

    private BaiHat getById(int id) {
        for (BaiHat baiHat : arrayBaiHat) {
            if (baiHat.getId() == id) {
                return baiHat;
            }
        }
        return null;
    }

    private int getRandomId() {
        Random random = new Random();
        int randomIndex = random.nextInt(arrayBaiHat.size());
        return arrayBaiHat.get(randomIndex).getId();
    }

    private void TronBai() {
    }

    private void playRandom() {
        int idRandom = getRandomId();
        playId = idRandom;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playStart();
    }

    private void playNormal(boolean tien) {
        if (tien == false) {
            playId--;
            if (playId < 1) {
                playId = getMaxId();
            }
        } else {
            playId++;
            if (playId > getMaxId()) {
                playId = 1;
            }
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playStart();
    }

    public void playStart() {
        PlayMusic();
        mediaPlayer.start();
        btnPlay.setImageResource(R.drawable.pause);
        music_compact.startAnimation(animation);
        SetTimeEnd();
        UpdateTime();
    }

    private int getMaxId() {
        int maxId = 1;
        for (BaiHat baiHat : arrayBaiHat) {
            int currentId = baiHat.getId();
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return maxId;
    }

    private void showTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time (e.g., schedule alarm)
                        handleSelectedTime(hourOfDay, minute);
                    }
                }, hour, minute, false);

        // Show the dialog
        timePickerDialog.show();
    }


    private void checkExistingAlarm(int hourOfDay, int minute) {
        // Check if there's an existing alarm
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);

        if (!alarmUp) {
            // If no existing alarm, schedule a new one with the specified time
            scheduleAlarm(hourOfDay, minute);
        }
    }


    private void scheduleAlarm(int hourOfDay, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Kiểm tra xem thời gian đã chọn có phải là thời điểm trong quá khứ hay không
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            // Nếu là thời điểm trong quá khứ, thêm 1 ngày để đặt hẹn giờ cho ngày mai
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.d("Giờ hẹn ", "Scheduled for: " + calendar.getTime());

        // Tạo một intent để phát sóng khi hẹn giờ đến
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        // Create an intent to be broadcast when the alarm triggers
        int uniqueCode = generateUniqueCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Đặt hẹn giờ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Sử dụng hàm setExactAndAllowWhileIdle trên Android 6.0 trở lên
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    // Sửa lại hàm handleSelectedTime để nhận thời gian từ TimePickerDialog
    private void handleSelectedTime(int hourOfDay, int minute) {
        // Handle the selected time (e.g., schedule alarm)
        long selectedTimeMillis = getTimeInMillis(hourOfDay, minute);


        String time = String.format("%02d:%02d", hourOfDay, minute);
        Toast.makeText(this, "Giờ bạn đã hẹn: " + time, Toast.LENGTH_SHORT).show();

        scheduleAlarm(hourOfDay, minute);

    }

    private long getTimeInMillis(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


    private Intent getMusicService(String musicAction) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(musicAction);
        intent.putExtra(MusicConfig.MUSIC_TITLE_KEY, getById(playId).getTenBaiHat());
        intent.putExtra(MusicConfig.MUSIC_ARTISTS_KEY, getById(playId).getTenCaSi());
        return intent;
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicConfig.Actions.PLAY_ACTION);
        intentFilter.addAction(MusicConfig.Actions.PAUSE_ACTION);
        intentFilter.addAction(MusicConfig.Actions.PREV_ACTION);
        intentFilter.addAction(MusicConfig.Actions.NEXT_ACTION);
        intentFilter.addAction(MusicConfig.Actions.CANCEL_ACTION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private void unRegisterReceiver() {
        unregisterReceiver(broadcastReceiver);
    }

    private void startMusicService(String musicAction) {
        startService(getMusicService(musicAction));
    }

    private void stopMusicService() {
        stopService(new Intent(this, MusicService.class));
    }
}
