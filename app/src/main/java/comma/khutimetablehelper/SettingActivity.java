package comma.khutimetablehelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    Spinner credit_subspin;
    Spinner firstTime_subspin;
    Spinner timeFlic_subspin;
    Spinner emptyDay_subspin;
    Spinner lunch_subspin;
    Spinner classTime_subspin;
    Spinner maxClassNum_subspin;
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
    Spinner lunchStart_spin;
    Spinner lunchEnd_spin;
    Spinner classTime_spin;
    Spinner maxClassNum_spin;

    ArrayList<Subject> needSubject = new ArrayList<Subject>();
    ArrayList<Subject> subSubject = new ArrayList<Subject>();
    ArrayList<Integer> spinStatus = new ArrayList<Integer>();// 필수인지 해당x인지 (0,1);
    ArrayList<Integer> spinValue = new ArrayList<Integer>(); // 스피너 선택값

    //처음화면 뜰때 다이얼로그 boolean 값 선언
    public static boolean first = true ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(first) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
            dialog.setTitle("사용법");
            dialog.setMessage("* 시간표 생성시 고려해야할 항목을 선택하세요. \n * 많은 항목을 '꼭! 필요해!' 선택시 조금더 정확한 시간표가 생성됩니다.");
            dialog.setNeutralButton("다시 보지 않기", yesButtonClickListener);
            dialog.show();
        }

        int i = 0;
        int index = 0;
        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        subSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("SubSubject");

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


        //스피너 값 변경 이벤트
        lunch_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    lunchStart_spin.setEnabled(false);
                    lunchEnd_spin.setEnabled(false);
                }else{
                    lunchStart_spin.setEnabled(true);
                    lunchEnd_spin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        firstTime_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    firstTime_spin.setEnabled(false);
                }else{
                    firstTime_spin.setEnabled(true);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        timeFlic_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    timeFlic_spin.setEnabled(false);
                }else{
                    timeFlic_spin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        emptyDay_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    emptyDay_spin.setEnabled(false);
                }else{
                    emptyDay_spin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        classTime_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    classTime_spin.setEnabled(false);
                }else{
                    classTime_spin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        maxClassNum_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    maxClassNum_spin.setEnabled(false);
                }else{
                    maxClassNum_spin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dayEndTime_subspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {

                if (position == 1) {
                    dayMon.setEnabled(false);
                    dayTue.setEnabled(false);
                    dayWed.setEnabled(false);
                    dayThu.setEnabled(false);
                    dayFri.setEnabled(false);
                }else{
                    dayMon.setEnabled(true);
                    dayTue.setEnabled(true);
                    dayWed.setEnabled(true);
                    dayThu.setEnabled(true);
                    dayFri.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // DialogInterface.OnClickListener 인터페이스를 구현
    private DialogInterface.OnClickListener yesButtonClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ChangeFirst() ;
        }
    };



    //처음뜨는 다이얼로그 확인버튼 클릭시 false로 바꿈
    protected boolean ChangeFirst() {
        first = false ;
        return  first;
    }

    private void saveSpinner(){

        spinStatus.add(0); //학점 필수
        spinStatus.add(lunch_subspin.getSelectedItemPosition());
        spinStatus.add(firstTime_subspin.getSelectedItemPosition());
        spinStatus.add(dayEndTime_subspin.getSelectedItemPosition());
        spinStatus.add(timeFlic_subspin.getSelectedItemPosition());
        spinStatus.add(emptyDay_subspin.getSelectedItemPosition());
        spinStatus.add(classTime_subspin.getSelectedItemPosition());
        spinStatus.add(maxClassNum_subspin.getSelectedItemPosition());


        spinValue.add(creditMin_spin.getSelectedItemPosition());
        spinValue.add(creditMax_spin.getSelectedItemPosition());
        spinValue.add(lunchStart_spin.getSelectedItemPosition());
        spinValue.add(lunchEnd_spin.getSelectedItemPosition());
        spinValue.add(firstTime_spin.getSelectedItemPosition());
        spinValue.add(dayMon.getSelectedItemPosition());
        spinValue.add(dayTue.getSelectedItemPosition());
        spinValue.add(dayWed.getSelectedItemPosition());
        spinValue.add(dayThu.getSelectedItemPosition());
        spinValue.add(dayFri.getSelectedItemPosition());
        spinValue.add(timeFlic_spin.getSelectedItemPosition());
        spinValue.add(emptyDay_spin.getSelectedItemPosition());
        spinValue.add(classTime_spin.getSelectedItemPosition());
        spinValue.add(maxClassNum_spin.getSelectedItemPosition());



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
        lunch_subspin = (Spinner) findViewById(R.id.setting_spin_subLunch);
        lunch_subspin.setSelection(1);
        classTime_subspin = (Spinner) findViewById(R.id.setting_spin_subClassTime);
        classTime_subspin.setSelection(1);
        maxClassNum_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassNum);
        maxClassNum_subspin.setSelection(1);;
        dayEndTime_subspin = (Spinner) findViewById(R.id.setting_spin_subDayEndTime);
        dayEndTime_subspin.setSelection(1);

        creditMin_spin = (Spinner) findViewById(R.id.setting_spin_creditMin);
        creditMax_spin = (Spinner) findViewById(R.id.setting_spin_creditMax);
        firstTime_spin = (Spinner) findViewById(R.id.setting_spin_firstStartTime);
        timeFlic_spin = (Spinner) findViewById(R.id.setting_spin_timeFlic);
        emptyDay_spin = (Spinner) findViewById(R.id.setting_spin_emptyDay);
        lunchStart_spin = (Spinner) findViewById(R.id.setting_spin_lunchStart);
        lunchEnd_spin = (Spinner) findViewById(R.id.setting_spin_lunchEnd);
        classTime_spin = (Spinner) findViewById(R.id.setting_spin_classTime);
        maxClassNum_spin = (Spinner) findViewById(R.id.setting_spin_maxClassNum);

        classTime_subspin = (Spinner) findViewById(R.id.setting_spin_subClassTime);
        maxClassNum_subspin = (Spinner) findViewById(R.id.setting_spin_subMaxClassNum);
        dayMon = (Spinner) findViewById(R.id.setting_spin_Mon);
        dayTue = (Spinner) findViewById(R.id.setting_spin_Tue);
        dayWed = (Spinner) findViewById(R.id.setting_spin_Wed);
        dayThu = (Spinner) findViewById(R.id.setting_spin_Thu);
        dayFri = (Spinner) findViewById(R.id.setting_spin_Fri);

    }

}




















