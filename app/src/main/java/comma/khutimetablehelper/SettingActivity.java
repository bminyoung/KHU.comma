package comma.khutimetablehelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by MinYoung on 2018-01-30.
 */

public class SettingActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SpinnerInit();

        Button nextBtn = (Button) findViewById(R.id.setting_btn_next);
        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MadeResultActivity.class);
                startActivity(intent);
            }
        });

    }

    // 스피너 기본값을 해당없음으로 설정하는 메서드
    private void SpinnerInit(){
        Spinner firstTime_spin = (Spinner) findViewById(R.id.setting_spin_subFirstStartTime);
        firstTime_spin.setSelection(2);

        Spinner timeFlic_spin = (Spinner) findViewById(R.id.setting_spin_subTimeFlic);
        timeFlic_spin.setSelection(2);

        Spinner day_spin= (Spinner) findViewById(R.id.setting_spin_subDay);
        day_spin.setSelection(2);

        Spinner dayCount_spin= (Spinner) findViewById(R.id.setting_spin_subDayCount);
        dayCount_spin.setSelection(2);

        Spinner credit_spin = (Spinner) findViewById(R.id.setting_spin_subCredit);
        credit_spin.setSelection(0);
        credit_spin.setEnabled(false);

        Spinner lunch_spin = (Spinner) findViewById(R.id.setting_spin_subLunch);
        lunch_spin.setSelection(2);
    }
}




















