package web.macro.jellybeanmr1java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch;
    Boolean check_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = (Switch)findViewById(R.id.aSwitch);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    check_state = isAirModeOn();
                    settingAirplaneMode(!check_state);
                } else {
                    settingAirplaneMode(false);
                }
            }
        });
    }

    private void settingAirplaneMode(boolean b) {
        Log.d("MainActivity","settingAirplaneMode bbbbbbbbbbbbb::::::::::::"+b);

        Log.d("MainActivity","settingAirplaneMode bbbbbbbbbbbbb::::::::::: SDK_INT :"+Build.VERSION.SDK_INT);
        Log.d("MainActivity","settingAirplaneMode bbbbbbbbbbbbb::::::::::: JELLY_BEAN_MR1 :"+Build.VERSION_CODES.JELLY_BEAN_MR1);

        Settings.System.putInt(this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1){  // 젤리빈 이하버전
            Settings.System.putInt(getContentResolver(),

                    Settings.System.AIRPLANE_MODE_ON, 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", b);
            sendBroadcast(intent);
        }else{

            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);

            startActivity(intent);
        }

        Log.d("MainActivity","isAirModeOn ::::::::::::: "+isAirModeOn());
    }

    private Boolean isAirModeOn() {
        Boolean isAirplaneMode;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){  // 젤리빈 이하버전
            isAirplaneMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        }else{
            isAirplaneMode = Settings.Global.getInt(getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        }
        return isAirplaneMode;
    }
}