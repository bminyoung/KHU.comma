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
    Spinner emptyDay_subspin;
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
    Spinner emptyDay_spin;
    Spinner dayCount_spin;
    Spinner lunchStart_spin;
    Spinner lunchEnd_spin;
    Spinner classTime_spin;
    Spinner maxClassNum_spin;
    Spinner maxClassTime_spin;

    ArrayList<Subject> needSubject = new ArrayList<Subject>();
    ArrayList<Subject> subSubject = new ArrayList<Subject>();
    ArrayList<Integer> spinStatus = new ArrayList<Integer>();// 필수인지 해당x인지 (0,1);
    ArrayList<Integer> spinValue = new ArrayList<Integer>(); // 스피너 선택값

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        int i = 0;
        int index = 0;
        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        subSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("SubSubject");

        //임시시간표 만들기부분
        AppContext.tempTimeTableList.add(new ArrayList<Subject>());
        while(i < needSubject.size()) {
            AppContext.tempTimeTableList.get(index).add(needSubject.get(i++));
        }
        i = 0;
        while(i < subSubject.size()) {
            AppContext.tempTimeTableList.get(index).add(subSubject.get(i++));
        }

        index++;
        i = 0;
        AppContext.tempTimeTableList.add(new ArrayList<Subject>());
        while(i < subSubject.size()) {
            AppContext.tempTimeTableList.get(index).add(subSubject.get(i++));
        }

        index++;
        i = 0;
        AppContext.tempTimeTableList.add(new ArrayList<Subject>());
        while(i < needSubject.size()) {
            AppContext.tempTimeTableList.get(index).add(needSubject.get(i++));
        } //임시 시간표 만들기부분 끝

        SpinnerInit();

        Button nextBtn = (Button) findViewById(R.id.setting_btn_next);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (valid()) {
                    case 0:
                        Intent intent = new Intent(SettingActivity.this, MadeResultActivity.class);
                        intent.putExtra("NeedSubject", needSubject);
                        intent.putExtra("SubSubject", subSubject);
                        saveSpinner();
                        intent.putExtra("spinStatus", spinStatus);
                        intent.putExtra("spinValue", spinValue);
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

    private void saveSpinner(){

        spinStatus.add(0); //학점 필수
        spinStatus.add(firstTime_subspin.getSelectedItemPosition());
        spinStatus.add(timeFlic_subspin.getSelectedItemPosition());
        spinStatus.add(emptyDay_subspin.getSelectedItemPosition());
        spinStatus.add(dayCount_subspin.getSelectedItemPosition());
        spinStatus.add(lunch_subspin.getSelectedItemPosition());
        spinStatus.add(classTime_subspin.getSelectedItemPosition());
        spinStatus.add(maxClassNum_subspin.getSelectedItemPosition());
        spinStatus.add(maxClassTime_subspin.getSelectedItemPosition());
        spinStatus.add(dayEndTime_subspin.getSelectedItemPosition());

        spinValue.add(creditMin_spin.getSelectedItemPosition());
        spinValue.add(creditMax_spin.getSelectedItemPosition());
        spinValue.add(firstTime_spin.getSelectedItemPosition());
        spinValue.add(timeFlic_spin.getSelectedItemPosition());
        spinValue.add(emptyDay_spin.getSelectedItemPosition());
        spinValue.add(dayCount_spin.getSelectedItemPosition());
        spinValue.add(lunchStart_spin.getSelectedItemPosition());
        spinValue.add(lunchEnd_spin.getSelectedItemPosition());
        spinValue.add(classTime_spin.getSelectedItemPosition());
        spinValue.add(maxClassNum_spin.getSelectedItemPosition());
        spinValue.add(maxClassTime_spin.getSelectedItemPosition());
        spinValue.add(dayMon.getSelectedItemPosition());
        spinValue.add(dayTue.getSelectedItemPosition());
        spinValue.add(dayWed.getSelectedItemPosition());
        spinValue.add(dayThu.getSelectedItemPosition());
        spinValue.add(dayFri.getSelectedItemPosition());


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
        emptyDay_subspin = (Spinner) findViewById(R.id.setting_spin_subEmptyDay);
        emptyDay_subspin.setSelection(1);
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
        emptyDay_spin = (Spinner) findViewById(R.id.setting_spin_emptyDay);
        dayCount_spin = (Spinner) findViewById(R.id.setting_spin_dayCount);
        lunchStart_spin = (Spinner) findViewById(R.id.setting_spin_lunchStart);
        lunchEnd_spin = (Spinner) findViewById(R.id.setting_spin_lunchEnd);
        classTime_spin = (Spinner) findViewById(R.id.setting_spin_classTime);
        maxClassNum_spin = (Spinner) findViewById(R.id.setting_spin_maxClassNum);
        maxClassTime_spin = (Spinner) findViewById(R.id.setting_spin_maxClassTime);

        classTime_subspin = (Spinner) findViewById(R.id.setting_spin_subClassTime);
        maxClassNum_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassNum);
        maxClassTime_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassTime);
        dayMon = (Spinner) findViewById(R.id.setting_spin_Mon);
        dayTue = (Spinner) findViewById(R.id.setting_spin_Tue);
        dayWed = (Spinner) findViewById(R.id.setting_spin_Wed);
        dayThu = (Spinner) findViewById(R.id.setting_spin_Thu);
        dayFri = (Spinner) findViewById(R.id.setting_spin_Fri);

    }

}




















