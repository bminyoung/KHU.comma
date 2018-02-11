package comma.khutimetablehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by MinYoung on 2018-01-30.
 */

public class SettingActivity extends AppCompatActivity {

    Spinner credit_subspin;
    Spinner firstTime_subspin;
    Spinner timeFlic_subspin;
    Spinner day_subspin;
    Spinner dayCount_subspin;
    Spinner lunch_subspin;
    Spinner classTime_subspin;
    Spinner maxClassNum_subspin;
    Spinner maxClassTime_subspin;
    Spinner dayEndTime_subspin;
    Spinner dayMon;
    Spinner dayTue;
    Spinner dayWed;
    Spinner dayThu;
    Spinner dayFri;

    Spinner creditMin_spin;
    Spinner creditMax_spin;
    Spinner firstTime_spin;
    Spinner timeFlic_spin;
    Spinner day_spin;
    Spinner dayCount_spin;
    Spinner lunchStart_spin;
    Spinner lunchEnd_spin;
    Spinner classTime_spin;
    Spinner maxClassNum_spin;
    Spinner maxClassTime_spin;
    Spinner dayEndTime_spin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView test = (TextView) findViewById(R.id.test);
        ArrayList<Subject> needSubject = new ArrayList<Subject>();
        ArrayList<Subject> subSubject = new ArrayList<Subject>();
        int i = 0;
        int index = 0;
        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        subSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("SubSubject");

        while(i < needSubject.size()) {
            AppContext.tempTimeTableList.add(new ArrayList<Subject>());
            AppContext.tempTimeTableList.get(index).add(needSubject.get(i++));
        }

        i = 0;
        while(i < subSubject.size()) {
            AppContext.tempTimeTableList.get(index).add(subSubject.get(i++));
        }
        index++;
        i = 0;
        while(i < subSubject.size()) {
            AppContext.tempTimeTableList.add(new ArrayList<Subject>());
            AppContext.tempTimeTableList.get(index).add(subSubject.get(i++));
        }

        index++;
        i = 0;
        while(i < needSubject.size()) {
            AppContext.tempTimeTableList.add(new ArrayList<Subject>());
            AppContext.tempTimeTableList.get(index).add(needSubject.get(i++));
        }



        SpinnerInit();

        Button nextBtn = (Button) findViewById(R.id.setting_btn_next);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (valid()) {
                    case 0:
                        Intent intent = new Intent(SettingActivity.this, MadeResultActivity.class);
                        startActivity(intent);
                        break;
                    case 1: //학점오류 다이얼로그
                    case 2: //점심시간오류 다이얼로그
                        showAlertDialog(valid());
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void showAlertDialog(int errorNum){
        String error = "";
        if(errorNum == 1) { error = "학점설정";  }
        else{ error = "점심시간설정"; }
        new AlertDialog.Builder(this).setTitle("오류").setMessage(error + "값이 잘못 되었습니다.").setNegativeButton("닫기",null).show();
    }

    //범위의 경우 최소<최대인지 검사
    private int valid() {
        int ret = 0; // 정상
        if (creditMin_spin.getSelectedItemPosition() > creditMax_spin.getSelectedItemPosition())
            ret = 1; //학점오류
        else if (lunchStart_spin.getSelectedItemPosition() > lunchEnd_spin.getSelectedItemPosition())
            ret = 2; // 점심시간오류

        return ret;
    }

    // 스피너 매치시키고 기본값을 설정하는 메서드
    private void SpinnerInit() {
        credit_subspin = (Spinner) findViewById(R.id.setting_spin_subCredit);
        credit_subspin.setSelection(0);
        credit_subspin.setEnabled(false);

        firstTime_subspin = (Spinner) findViewById(R.id.setting_spin_subFirstStartTime);
        firstTime_subspin.setSelection(1);

        timeFlic_subspin = (Spinner) findViewById(R.id.setting_spin_subTimeFlic);
        timeFlic_subspin.setSelection(1);

        day_subspin = (Spinner) findViewById(R.id.setting_spin_subDay);
        day_subspin.setSelection(1);

        dayCount_subspin = (Spinner) findViewById(R.id.setting_spin_subDayCount);
        dayCount_subspin.setSelection(1);

        lunch_subspin = (Spinner) findViewById(R.id.setting_spin_subLunch);
        lunch_subspin.setSelection(1);

        classTime_subspin = (Spinner) findViewById(R.id.setting_spin_subClassTime);
        classTime_subspin.setSelection(1);

        maxClassNum_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassNum);
        maxClassNum_subspin.setSelection(1);

        maxClassTime_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassTime);
        maxClassTime_subspin.setSelection(1);

        dayEndTime_subspin = (Spinner) findViewById(R.id.setting_spin_subDayEndTime);
        dayEndTime_subspin.setSelection(1);

        creditMin_spin = (Spinner) findViewById(R.id.setting_spin_creditMin);
        creditMax_spin = (Spinner) findViewById(R.id.setting_spin_creditMax);
        firstTime_spin = (Spinner) findViewById(R.id.setting_spin_firstStartTime);
        timeFlic_spin = (Spinner) findViewById(R.id.setting_spin_timeFlic);
        day_spin = (Spinner) findViewById(R.id.setting_spin_day);
        dayCount_spin = (Spinner) findViewById(R.id.setting_spin_dayCount);
        lunchStart_spin = (Spinner) findViewById(R.id.setting_spin_lunchStart);
        lunchEnd_spin = (Spinner) findViewById(R.id.setting_spin_lunchEnd);
        classTime_subspin = (Spinner) findViewById(R.id.setting_spin_classTime);
        maxClassNum_subspin = (Spinner) findViewById(R.id.setting_spin_maxClassNum);
        maxClassTime_subspin = (Spinner) findViewById(R.id.setting_spin_maxClassTime);
        dayMon = (Spinner) findViewById(R.id.setting_spin_Mon);
        dayTue = (Spinner) findViewById(R.id.setting_spin_Tue);
        dayWed = (Spinner) findViewById(R.id.setting_spin_Wed);
        dayThu = (Spinner) findViewById(R.id.setting_spin_Thu);
        dayFri = (Spinner) findViewById(R.id.setting_spin_Fri);

    }
}




















