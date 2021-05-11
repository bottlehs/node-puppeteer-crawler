package web.macro.jellybean;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch;
    Boolean check_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = (Switch) findViewById(R.id.aSwitch);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_state = isAirModeOn();
                    settingAirplaneMode(!check_state);
                } else {
                    settingAirplaneMode(false);
                }
            }
        });
    }

    private void settingAirplaneMode(boolean b) {
        Log.d("MainActivity", "settingAirplaneMode bbbbbbbbbbbbb::::::::::::" + b);

        Log.d("MainActivity", "settingAirplaneMode bbbbbbbbbbbbb::::::::::: SDK_INT :" + Build.VERSION.SDK_INT);
        Log.d("MainActivity", "settingAirplaneMode bbbbbbbbbbbbb::::::::::: JELLY_BEAN_MR1 :" + Build.VERSION_CODES.JELLY_BEAN_MR1);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {  // 젤리빈 이하버전
            Settings.System.putInt(getContentResolver(),

                    Settings.System.AIRPLANE_MODE_ON, b ? 1 : 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", b);
            sendBroadcast(intent);
        } else {
            int enabled = b ? 1 : 0;
            String command = "settings put global airplane_mode_on" + " " + enabled;
            executeCommandWithoutWait(MainActivity.this, "-c", command);
            command = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state" + " " + enabled;
            executeCommandWithoutWait(MainActivity.this, "-c", command);
        }

        Log.d("MainActivity", "isAirModeOn ::::::::::::: " + isAirModeOn());
    }

    private void executeCommandWithoutWait(Context context, String option, String command) {
        boolean success = false;
        String su = "su";
        for (int i = 0; i < 3; i++) {
            // "su" command executed successfully.
            if (success) {
                // Stop executing alternative su commands below.
                break;
            }
            if (i == 1) {
                su = "/system/xbin/su";
            } else if (i == 2) {
                su = "/system/bin/su";
            }
            try {
                // execute command
                Runtime.getRuntime().exec(new String[]{su, option, command});
            } catch (IOException e) {
                Log.e("MainActivity", "su command has failed due to: " + e.fillInStackTrace());
            }
        }

    }

    private Boolean isAirModeOn() {
        Boolean isAirplaneMode;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {  // 젤리빈 이하버전
            isAirplaneMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        } else {
            isAirplaneMode = Settings.Global.getInt(getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        }
        return isAirplaneMode;
    }
}
