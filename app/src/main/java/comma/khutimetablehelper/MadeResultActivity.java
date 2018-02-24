package comma.khutimetablehelper;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;


public class MadeResultActivity extends AppCompatActivity {

    //시간표색상
    static int[] colors = {Color.parseColor("#FFCDD2"), Color.parseColor("#BBDEFB"), Color.parseColor("#F0F4C3"), Color.parseColor("#FFCC80"),
            Color.parseColor("#E1BEE7"), Color.parseColor("#80DEEA"), Color.parseColor("#D7CCC8"), Color.parseColor("#D1C4E9"),
            Color.parseColor("#F8BBD0"),Color.parseColor("#DCEDC8"),Color.parseColor("#FFF59D"),Color.parseColor("#81D4FA"),
            Color.parseColor("#CFD8DC"),Color.parseColor("#B2DFDB"),Color.parseColor("#FFCCBC"),Color.parseColor("#C5CAE9"),
            Color.parseColor("#C8E6C9"),Color.parseColor("#FFECB3"),Color.parseColor("#E0E0E0"),Color.parseColor("#EF9A9A"), Color.parseColor("#90CAF9")};
    int colorIndex = 0;

    ArrayList<Subject> needSubject;
    ArrayList<Subject> subSubject;
    ArrayList<Integer> spinStatus;
    ArrayList<Integer> spinValue;
    ArrayList<Subject> filteredSubSubject = new ArrayList<Subject>();
    boolean filteringNumber = false;
    int filterCount = 0;

    ArrayList<ArrayList<Subject>> listData = new ArrayList<ArrayList<Subject>>();
    TextView timeTable[][] = new TextView[140][5]; //시간표 각 칸
    int focusOn; // 저장될 시간표의 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maderesult);

        Button btnSave = (Button) findViewById(R.id.maderesult_btn_save);
        ImageButton btnMain = (ImageButton) findViewById(R.id.maderesult_btn_main);
        setTextId();
        focusOn = -1;

        final ListView listView = (ListView) findViewById(R.id.maderesult_lv_List);
        final CustomLvAdapter adapter = new CustomLvAdapter(listData);
        listView.setAdapter(adapter);

        //저장, 메인버튼
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusOn < 0) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MadeResultActivity.this);
                    dialogBuilder.setTitle("저장할 시간표를 선택해주세요").setNegativeButton("취소", null).show();
                } else {
                    ShowSaveDialog(focusOn);
                }
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();

            }
        });

        // child목록을 클릭했을때 위 시간표에 뜨도록 이벤트설정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ArrayList<Subject> selectedTimeTable = (ArrayList<Subject>) adapter.getItem(position);

                if (focusOn != position) {
                    for (int i = 0; i < 28; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i % 2 == 0) {
                                timeTable[i][j].setBackgroundResource(R.drawable.rectangle_openbottom);
                            }else{
                                timeTable[i][j].setBackgroundResource(R.drawable.rectangle_opentop);
                            }
                            timeTable[i][j].setText("");
                        }
                    }

                    focusOn = position;
                    colorIndex = 0; //새로운시간표 클릭시 리셋하고 실행
                    //같은 과목은 같은 색깔로
                    for (int i = 0; i < selectedTimeTable.size(); i++) {
                        for (int j = i; j < selectedTimeTable.size(); j++) {
                            if (selectedTimeTable.get(i).cNum.equals(selectedTimeTable.get(j).cNum)) {
                                ShowTimeTable(selectedTimeTable.get(j));
                                if (j == selectedTimeTable.size() - 1) {
                                    i = selectedTimeTable.size();
                                }
                            } else {
                                i = j - 1;
                                break;
                            }
                        }
                        colorIndex++;
                    }
                }
            }
        });

        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        subSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("SubSubject");
        spinStatus = (ArrayList<Integer>) getIntent().getSerializableExtra("spinStatus");
        spinValue = (ArrayList<Integer>) getIntent().getSerializableExtra("spinValue");

        filteredSubSubject =  (ArrayList<Subject>) subSubject.clone();

        // 필수과목, 시작시간, 점심시간, 공강요일, 강의최대시간, 끝나는시간에 맞지않는과목 걸러내기
        for (int i = 0; i < filteredSubSubject.size(); i++)
        {
            if(i < filteredSubSubject.size() - 1) {
                filterCounting(i);
            }
            if(i < filteredSubSubject.size() - 2){
                tripleFilterCounting(i);
            }
//            Log.d("tag", "minyoung 중복횟수 : " + filterCount);
            for(int j = 0; j < filterCount + 1; j++) {
                filterStackNeedSubject(subSubject.get(i+j).cStart, subSubject.get(i+j).cEnd, subSubject.get(i+j).cDay);
                if (filteringNumber) {
                    Log.d("tag", "minyoung 필수랑 겹쳐서 제외 : " + filteredSubSubject.get(i+j).cName);
                }
                if (spinStatus.get(1) == 0) {
                    filterStartTime(spinValue.get(2), subSubject.get(i+j).cStart);
                }
                if (spinStatus.get(3) == 0) {
                    filterBlankDay(spinValue.get(4), subSubject.get(i+j).cDay);
                }
                if (spinStatus.get(5) == 0) {
                    filterLunchTime(spinValue.get(6), spinValue.get(7), subSubject.get(i+j).cStart, subSubject.get(i+j).cEnd);
                }
                if (spinStatus.get(6) == 0) {
                    filterMaxLectureTime(subSubject.get(i+j).cStart, subSubject.get(i+j).cEnd);
                }
                if (spinStatus.get(9) == 0) {
                    filterDayEndTime(subSubject.get(i+j).cDay, subSubject.get(i+j).cEnd);
                }
            }
//            Log.d("tag", "minyoung filteringNumber값 체크 : " + filteringNumber);
            if(filteringNumber) {
                for(int j = 0; j < filterCount+1 ; j++) {
//                    Log.d("tag", "minyoung filteredSubSubject.remove(" + i + ") : " + filteredSubSubject.get(i).cName);
                    filteredSubSubject.remove(i);       // 문제발생 : 2과목연속으로나오면 같이지워야됨.
                }
                if (i != 0) {
                    i--;
                }
            }
            filteringNumber = false;
            filterCount = 0;
        }

        calculateSubject();
        setData();

    }

    protected void onDestroy() {
        AppContext.tempTimeTableList.clear();
        super.onDestroy();
    }

    //메인으로 돌아갈래?
    public void goToMain(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("메인화면으로 돌아가시겠습니까?").setNegativeButton("아니요", null)
                .setPositiveButton("예", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(MadeResultActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).show();
    }

    public void ShowTimeTable(Subject selected) {
        if(selected.cDay == -1)
            return;
        TextView tv;
        int start = (int) ((selected.cStart - 9.0) * 2.0);
        int end = (int) ((selected.cEnd - 9.0) * 2.0 - 1.0);

        for (int i = end; i >= start; i--) {
            tv = timeTable[i][selected.cDay];
            tv.setBackgroundColor(colors[colorIndex]);
        }
        timeTable[start][selected.cDay].setText(selected.cName);
    }

    private void SaveTimeTable(int position, String timeTableName) {
        ArrayList<Subject> selected = AppContext.tempTimeTableList.get(position);
        AppContext.timeTableList.add(selected);
        AppContext.timeTableNameList.add(timeTableName);

        int[] classes = new int[42];
        ArrayList<Integer> nums = new ArrayList<Integer>();
        for(int i = 0; i < selected.size();i++) {
            nums.add(selected.get(i).cRow);
        }

        for(int j =0; j < nums.size(); j++) {
            int temp = Integer.parseInt(""+nums.get(j));
            classes[j] = temp;
        };

        String fileName = getFilesDir().getAbsolutePath() +timeTableName+".csv";

        UserInputData timeTable = new UserInputData(classes, fileName);
        int i = 0;

        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName));

            while (timeTable.getcClasses()[i] != 0) {
                fw.write(""+timeTable.getcClasses()[i]);
                if(timeTable.getcClasses()[i+1] != 0) {
                    fw.write(",");}
                i++;
            }
            fw.flush();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //시간표 각 칸 변수지정
    private void setTextId() {
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 5; j++) {
                int resID = getResources().getIdentifier("maderesult_tv_" + i + "" + j, "id", "comma.khutimetablehelper");
                timeTable[i][j] = ((TextView) findViewById(resID));
            }
        }
    }

    //확장리스트뷰 목록 초기화
    private void setData() {

        int i = 0;

        // listData-시간표들의 목록
        for (i = 0; i < AppContext.tempTimeTableList.size(); i++) {
            listData.add(AppContext.tempTimeTableList.get(i));
        }

    }

    //다이얼로그
    private void ShowSaveDialog(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout saveLayout = (LinearLayout) inflater.inflate(R.layout.maderesult_savedialog, null);

        final EditText TimeTableTitle = (EditText) saveLayout.findViewById(R.id.dialog_edt_title);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("이 시간표를 저장하시겠습니까?").setView(saveLayout).setNegativeButton("취소", null)
                .setPositiveButton("저장", null);

        final AlertDialog dialog;
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false); //다이얼로그 밖 터치해도 안 꺼지도록
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posBtn = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TimeTableTitle.getText().length() == 0) {
                            Toast.makeText(MadeResultActivity.this, "시간표이름을 입력해주세요", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MadeResultActivity.this, TimeTableTitle.getText() + " 이 저장되었습니다", Toast.LENGTH_LONG).show();
                            SaveTimeTable(focusOn, String.valueOf(TimeTableTitle.getText()));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }
    // 2개로 나뉜 시간 체크
    public void filterCounting(int subSubjectNum){
        if(subSubject.get(subSubjectNum).cNum.equals(subSubject.get(subSubjectNum + 1).cNum)){
            filterCount = 1;
        }
    }
    // 3개로 나뉜 시간 체크
    public void tripleFilterCounting(int subSubjectNum){
        if (subSubject.get(subSubjectNum).cNum.equals(subSubject.get(subSubjectNum + 2).cNum)){
            filterCount = 2;
        }
    }

    // 필수과목과 시간이 겹치는 후보과목 걸러내기
    public  void filterStackNeedSubject(double subSubjectStartTime, double subSubjectEndTime, int subSubjectDay) {
        for(int i = 0; i < needSubject.size() ; i++){
            if(needSubject.get(i).cDay == -1){
                continue;
            }
            if(needSubject.get(i).cDay == subSubjectDay) {
                if (!(subSubjectStartTime >= needSubject.get(i).cEnd || subSubjectEndTime <= needSubject.get(i).cStart)) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 겹치는 필수과목 : " + needSubject.get(i).cName);
                }
            }
        }
    }

    // 시작시간이 10시, 11시, 12시이전인 후보과목 걸러내기
    public void filterStartTime(int firstTimeSpinnerNum, double subSubjectStartTime){
        switch(firstTimeSpinnerNum) {
            case 1:
                if (subSubjectStartTime < 10) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 10시 이전이라서 제외 : ");
                }
                break;
            case 2:
                if (subSubjectStartTime < 11) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 11시 이전이라서 제외 : ");
                }
                break;
            case 3:
                if (subSubjectStartTime < 12) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 12시 이전이라서 제외 : ");
                }
                break;
        }
    }

    // 점심시간에 해당되는 후보과목 걸러내기
    public void filterLunchTime(int lunchStartTimeSpinnerNum, int lunchEndTimeSpinnerNum, double subSubjectStartTime, double subSubjectEndTime){
        double tmpLunchStartTime = 0.0;
        double tmpLunchEndTime = 0.0;
        switch (lunchStartTimeSpinnerNum) {
            case 0:
                tmpLunchStartTime = 11.5;
                break;
            case 1:
                tmpLunchStartTime = 12.0;
                break;
            case 2:
                tmpLunchStartTime = 12.5;
                break;
            case 3:
                tmpLunchStartTime = 13.0;
                break;
        }
        switch (lunchEndTimeSpinnerNum) {
            case 0:
                tmpLunchStartTime = 12.0;
                break;
            case 1:
                tmpLunchStartTime = 12.5;
                break;
            case 2:
                tmpLunchStartTime = 13.0;
                break;
            case 3:
                tmpLunchStartTime = 13.5;
                break;
            case 4:
                tmpLunchStartTime = 14.0;
                break;
        }
        if(!(subSubjectEndTime < tmpLunchStartTime || subSubjectStartTime > tmpLunchEndTime))
        {
            filteringNumber = true;
            Log.d("tag", "minyoung 점심시간이라서 제외 : ");
        }
    }

    // 공강요일 설정된 과목 걸러내기
    public void filterBlankDay(int daySpinnerNum, int subSubjectDay) {
        if(daySpinnerNum != subSubjectDay){
            filteringNumber = true;
            Log.d("tag", "minyoung 공강요일이라서 제외 : ");
        }
    }

    // 요일별 끝나는시간 걸러내기
    public void filterDayEndTime(int subSubjectDay, double subSubjectEndTime){ //첫번째 : subsubject의 요일

        switch (subSubjectDay) {
            case 0:
                if(subSubjectEndTime > spinValue.get(11) + 3) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 1:
                if(subSubjectEndTime > spinValue.get(12) + 3) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 2:
                if(subSubjectEndTime > spinValue.get(13) + 3) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 3:
                if(subSubjectEndTime > spinValue.get(14) + 3) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 4:
                if(subSubjectEndTime > spinValue.get(15) + 3) {
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
        }
    }

    public void filterMaxLectureTime(double subSubjectStartTime, double subSubjectEndTime){
        double calculatedTime = subSubjectEndTime - subSubjectStartTime;
        switch (spinValue.get(8)) {
            case 0:
                if(calculatedTime > 1.5){
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 1:
                if(calculatedTime > 3.0){
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
            case 2:
                if(calculatedTime > 4.5){
                    filteringNumber = true;
                    Log.d("tag", "minyoung 늦게끝나서 제외 : ");
                }
                break;
        }
    }

    public void calculateSubject() {
        int[][] SubjectCell = new int[26][5];
        int[][] needSubjectCell = new int[26][5];

        ArrayList<Subject> tmpSubSubject = new ArrayList<>();
        ArrayList<ArrayList<Subject>> selectedSubSubject = new ArrayList<ArrayList<Subject>>();
        ArrayList<Subject> OKSubSubject = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 5; j++) {
                SubjectCell[i][j] = 0;
                needSubjectCell[i][j] = 0;
            }
        }
        boolean blankcheck;
        int getSettedMinCreditCount = spinValue.get(0) + 15;
        int getSettedMaxCreditCount = spinValue.get(1) + 15;
        int nowCreditCount = 0;
        int monDayClassCount = 0;
        int tuesDayClassCount = 0;
        int wednesDayClassCount = 0;
        int thursDayClassCount = 0;
        int friDayClassCount = 0;
        int dayCount;
        int swapNum = 0;

        //먼저 필수과목 일단 담기

        double needSubjectStartTime;
        double needSubjectEndTime;

        for(int i = 0; i < needSubject.size() ; i++) {
            if(needSubject.get(i).cDay == -1){
                continue;
            }
            needSubjectStartTime = needSubject.get(i).cStart-9;
            needSubjectEndTime = needSubject.get(i).cEnd-9;

            for(int j = 0; j < (int) ((needSubjectEndTime - needSubjectStartTime)*2) ; j++){ // (spinValue.get(3)+1)*(1 - spinStatus.get(2))) 공강크기
                needSubjectCell[(int)needSubjectStartTime+j][needSubject.get(i).cDay] = needSubject.get(i).cRow;
            }
            switch (needSubject.get(i).cDay){
                case 0:
                    monDayClassCount++;
                    break;
                case 1:
                    tuesDayClassCount++;
                    break;
                case 2:
                    wednesDayClassCount++;
                    break;
                case 3:
                    thursDayClassCount++;
                    break;
                case 4:
                    friDayClassCount++;
                    break;
            }
            if(i!=0) {
                if(needSubject.get(i).cNum.equals(needSubject.get(i-1).cNum)) {
                    nowCreditCount = nowCreditCount + needSubject.get(i).cCredit;
                }
            }
        }
        int creditIntent = nowCreditCount;
        int[] pluralChecker = new int[filteredSubSubject.size()];
        int[] pluralCount = new int[filteredSubSubject.size()];
        int[] triplePluralChecker = new int[filteredSubSubject.size()];
        int intentMonDayClassCount = monDayClassCount;
        int intentTuesDayClassCount = tuesDayClassCount;
        int intentWedDayClassCount = wednesDayClassCount;
        int intentThursDayClassCount = thursDayClassCount;
        int intentFriDayClassCount = friDayClassCount;


        for(int i = 0; i < filteredSubSubject.size(); i++) {
            tmpSubSubject = (ArrayList<Subject>) filteredSubSubject.clone();
            for(int j = 0; j < swapNum ; j++){
                if(tmpSubSubject.size() > 3) {
                    if (tmpSubSubject.get(0).cNum.equals(tmpSubSubject.get(1).cNum)) {        // 다음과목과 학수번호가 같으면
                        swapNum++;
                        if(tmpSubSubject.size() > 4) {
                            if (tmpSubSubject.get(0).cNum.equals(tmpSubSubject.get(2).cNum)) {    // 다다음과목과 학수번호가 같으면
                                swapNum++;
                            }
                        }
                    }
                }
                for(int swap = 0; swap < tmpSubSubject.size() - 1 ; swap++) {
//                    Log.d("tag","minyoung swap 과목 : "+tmpSubSubject.get(swap).cName + "와(과)" + tmpSubSubject.get(swap+1).cName + "를 바꿉니다!!");
                    Collections.swap(tmpSubSubject, swap, swap+1);
                }
            }
            swapNum++;
            for(int j = 0; j < 24 ; j++) {
                for(int k = 0; k < 5 ; k++){
                    SubjectCell[j][k] = needSubjectCell[j][k];
                }
            }
            monDayClassCount = intentMonDayClassCount;
            tuesDayClassCount = intentTuesDayClassCount;
            wednesDayClassCount = intentWedDayClassCount;
            thursDayClassCount = intentThursDayClassCount;
            friDayClassCount = intentFriDayClassCount;
            nowCreditCount = creditIntent;

            for (int j = 0; j < tmpSubSubject.size(); j++) {
                int tmpJ;
                for(int l = 0; l < tmpSubSubject.size() ; l++){
                    pluralChecker[l] = 0;
                    pluralCount[l] = 0;
                    triplePluralChecker[l] = 0;
                }
                for(int k = 0; k < tmpSubSubject.size() - 1; k++){
//                    Log.d("tag","minyoung error check : size : " + tmpSubSubject.size());
//                    Log.d("tag","minyoung error check : k    : " + k);
                    for(int l = k+1; l < tmpSubSubject.size(); l++) {
                        if(pluralCount[k] == 0) {
                            if (tmpSubSubject.get(k).cNum.equals(tmpSubSubject.get(l).cNum)) {
//                                Log.d("tag","minyoung 중복과목 :"+tmpSubSubject.get(k).cNum + "&&" + tmpSubSubject.get(l).cNum);
//                                Log.d("tag","minyoung 중복과목 :"+tmpSubSubject.get(k).cName + "&&" + tmpSubSubject.get(l).cName);
                                pluralChecker[k] = l;
                                pluralCount[k] = 1;
                                pluralCount[l] = 3;     // 이미 다른곳에서 중복된걸로 체크된경우
                            }
                        } else if(pluralCount[k] == 1){
                            if(tmpSubSubject.get(k).cNum.equals(tmpSubSubject.get(l).cNum)) {
//                                Log.d("tag","minyoung 3중복과목 : "+k+" &&"+ l + "과목" + tmpSubSubject.get(k).cNum);
                                triplePluralChecker[k] = l;
                                pluralCount[k]++;
                            }
                        }
//                        Log.d("tag","minyoung pluralCount["+k+"] : " + pluralCount[k]); // >> 두번출력이유??
                    }
                }
//                Log.d("tag","minyoung error check : " + tmpSubSubject.get(j).cStart);  // 문제없음
//                Log.d("tag","minyoung error check : " + tmpSubSubject.get(pluralChecker[j]).cStart); // pluralChecker[j] 가 문제 >> 해결된듯?

                Log.d("tag","minyoung pluralCount : "+pluralCount[j]);
                Log.d("tag","minyoung 과목 : "+tmpSubSubject.get(j).cName);
                Log.d("tag","minyoung tmpSubSubject크기 : "+tmpSubSubject.size());
                Log.d("tag","minyoung 현재 j값 : "+j);
                Log.d("tag","minyoung 실행횟수 : "+((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2));
                blankcheck = true;
                    for(int k = 0; k < 26 ; k++){
//                        Log.d("tag","minyoung Cell["+k+"] : "+SubjectCell[k][0]+SubjectCell[k][1]+SubjectCell[k][2]+SubjectCell[k][3]+SubjectCell[k][4]);
                    }

                switch(pluralCount[j]) {//중복횟수만큼
                     case 0:
                         if (nowCreditCount + tmpSubSubject.get(j).cCredit > getSettedMaxCreditCount) {     //학점이 되는지 체크
                             Log.d("tag","minyoung 최대학점 초과");
                             blankcheck = false;
                         }
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }
                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         if(blankcheck){
                             switch (tmpSubSubject.get(j).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                         }
                         break;
                     case 1:
                         if (nowCreditCount + tmpSubSubject.get(j).cCredit > getSettedMaxCreditCount) {     //학점이 되는지 체크
                             Log.d("tag","minyoung 최대학점 초과");
                             blankcheck = false;
                         }
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }
                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         //
                         tmpJ = j;
                         j = pluralChecker[tmpJ];
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k+" / "+j);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }
                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         if(blankcheck){
                             switch (tmpSubSubject.get(j).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                         }
                         j = tmpJ;
                         if(blankcheck){
                             switch (tmpSubSubject.get(j).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                         }
                         break;
                     case 2:
                         if (nowCreditCount + tmpSubSubject.get(j).cCredit > getSettedMaxCreditCount) {     //학점이 되는지 체크
                             Log.d("tag","minyoung 최대학점 초과");
                             blankcheck = false;
                         }
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }
                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         tmpJ = j;
                         j = pluralChecker[tmpJ];
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k+" / "+j);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }

                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         j = triplePluralChecker[tmpJ];
                         for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                             if (SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] != 0) {
                                 blankcheck = false;
                                 Log.d("tag","minyoung 이미 시간이 차있습니다!"+k+" / "+ j);
                                 break;
                             }
                         }
                         if (spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                             dayCount = 5;
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount == 0) {
                                         dayCount--;
                                     }
                                     break;
                             }
                             if (monDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (tuesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (wednesDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (thursDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (friDayClassCount == 0) {
                                 dayCount--;
                             }
                             if (dayCount > spinValue.get(5) + 3) {
                                 blankcheck = false;
                             }
                         }
                         if (spinStatus.get(7) == 0) {     // 요일별 최대강의수를 넘었는지 체크
                             switch (tmpSubSubject.get(j).cDay) {
                                 case 0:
                                     if (monDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 1:
                                     if (tuesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 2:
                                     if (wednesDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 3:
                                     if (thursDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                                 case 4:
                                     if (friDayClassCount >= spinValue.get(9) + 2) {
                                         blankcheck = false;
                                     }
                                     break;
                             }
                         }
                         j = tmpJ;
                         if(blankcheck){
                             switch (tmpSubSubject.get(j).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                             switch (tmpSubSubject.get(pluralChecker[j]).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                             switch (tmpSubSubject.get(triplePluralChecker[j]).cDay){
                                 case 0:
                                     monDayClassCount++;
                                     break;
                                 case 1:
                                     tuesDayClassCount++;
                                     break;
                                 case 2:
                                     wednesDayClassCount++;
                                     break;
                                 case 3:
                                     thursDayClassCount++;
                                     break;
                                 case 4:
                                     friDayClassCount++;
                                     break;
                             }
                         }
                         break;
                     default:
                         blankcheck = false;
                         break;
                 }
                if (blankcheck) {       // ok 면 시간표에 넣기
                    String tmpSubjectcName = tmpSubSubject.get(j).cName;        // 반이 나뉜과목도 이제 제거해야됨!
                    Log.d("tag","minyoung 과목추가 : "+tmpSubSubject.get(j).cName + "// pluralCount[" + j+ "] : " + pluralCount[j]);
//                    Log.d("tag","minyoung pluralCountcheck : "+pluralCount[j]);
//                    Log.d("tag","minyoung for문 k값 check : "+(int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2));
                    switch (pluralCount[j]) {
                        case 0:
                            OKSubSubject.add(tmpSubSubject.get(j));
                            nowCreditCount = nowCreditCount + tmpSubSubject.get(j).cCredit;
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {       // 중복된과목이 없으면 셀에 한번만담아도됨
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
//                            Log.d("tag", "minyoung 제거하는 과목" + tmpSubSubject.get(j).cName);
                            tmpSubSubject.remove(j);
                            j--;    // 과목 1개 삭제되니 빼기
                            break;
                        case 1:
                            int minusCount = 0;
                            if(j > pluralChecker[j]) {
                                minusCount++;
                            }
                            OKSubSubject.add(tmpSubSubject.get(j));
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cDay);
                            nowCreditCount = nowCreditCount + tmpSubSubject.get(j).cCredit;
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {       // 중복된과목이 1개면 셀에2번담아야됨 셀에담기는거 이상없음
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k ][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
                            tmpJ = j;
                            j = pluralChecker[tmpJ];
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cDay);
                            OKSubSubject.add(tmpSubSubject.get(j));
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
                            j = tmpJ;
//                            Log.d("tag", "minyoung 제거하는 과목" + tmpSubSubject.get(j).cName);
                            if (j > tmpJ) {
                                tmpSubSubject.remove(j);                        //
                                tmpSubSubject.remove(pluralChecker[tmpJ]);    //
                            } else {
                                tmpSubSubject.remove(pluralChecker[tmpJ]);                        //
                                tmpSubSubject.remove(j);
                            }
                            j--;
                            if(minusCount == 1) {
                                Log.d("tag", "minyoung MinusPlus");
                                j--;    // 과목 2개 뺏으니 2빼기
                            }
                            break;
                        case 2:
                            int large;
                            int mid;
                            int small;
                            OKSubSubject.add(tmpSubSubject.get(j));
                            nowCreditCount = nowCreditCount + tmpSubSubject.get(j).cCredit;
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {       // 중복된 과목이 3개면 셀에 3번담아야됨
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
                            tmpJ = j;
                            j = pluralChecker[tmpJ];
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            OKSubSubject.add(tmpSubSubject.get(j));
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
                            j = triplePluralChecker[tmpJ];
                            Log.d("tag","minyoung "+tmpSubSubject.get(j).cStart+"~"+tmpSubSubject.get(j).cEnd);
                            OKSubSubject.add(tmpSubSubject.get(j));
                            for (int k = 0; k < (int) ((tmpSubSubject.get(j).cEnd - tmpSubSubject.get(j).cStart) * 2); k++) {
                                SubjectCell[(int) (tmpSubSubject.get(j).cStart - 9)*2 + k][tmpSubSubject.get(j).cDay] = tmpSubSubject.get(j).cRow;
                            }
                            j = tmpJ;
                            if(j > pluralChecker[tmpJ] && j > triplePluralChecker[tmpJ]) {
                                large = j;
                                if (pluralChecker[tmpJ] > triplePluralChecker[tmpJ]) {
                                    mid = pluralChecker[tmpJ];
                                    small = triplePluralChecker[tmpJ];
                                } else {
                                    mid = triplePluralChecker[tmpJ];
                                    small = pluralChecker[tmpJ];
                                }
                            } else if(pluralChecker[tmpJ] > triplePluralChecker[tmpJ]) {
                                large = pluralChecker[tmpJ];
                                if(j > triplePluralChecker[tmpJ]){
                                    mid = j;
                                    small = triplePluralChecker[tmpJ];
                                } else {
                                    mid = triplePluralChecker[tmpJ];
                                    small = j;
                                }
                            } else {
                                large = triplePluralChecker[tmpJ];
                                if (j > pluralChecker[tmpJ]) {
                                    mid = j;
                                    small = pluralChecker[tmpJ];
                                } else {
                                    mid = pluralChecker[tmpJ];
                                    small = j;
                                }
                            }
                            tmpSubSubject.remove(large);                        // 가장 큰거부터 줄어듦
                            tmpSubSubject.remove(mid);    //
                            tmpSubSubject.remove(small);    //
                            if(j == large){
                                Log.d("tag", "minyoung MinusPlusPlus");
                                j--;
                                j--;
                            }
                            if(j == mid){
                                Log.d("tag", "minyoung MinusPlus");
                                j--;
                            }
                            j = j--;    // 과목 3개 뺏으니 3빼기
                            break;
                        default:
                            break;
                    }
                    for(int k = 0; k < tmpSubSubject.size() ; k++){
                        if(tmpSubSubject.get(k).cName.equals(tmpSubjectcName)){
                            Log.d("tag", "minyoung 분반된 과목 제거 : " + tmpSubjectcName + "//" + k);
                            tmpSubSubject.remove(k);
                            k--;
                        }
                    }
                }
                boolean blankOk = true;
                Log.d("tag", "minyoung 현재학점 : " +nowCreditCount + " 최소학점 : " + getSettedMinCreditCount);
                if (nowCreditCount >= getSettedMinCreditCount) {    // 최소학점을 넘었을때,
                    if(spinStatus.get(2) == 0) { // 공강시간 체크
                        for (int k = 0; k < 5; k++) {
                            boolean startcheck = true;
                            boolean endcheck;
                            int blankcount = 0;
                            int Maxblank = 0;
                            for (int l = 0; k < 26; k++) {
                                if(SubjectCell[k][l] != 0){
                                    startcheck = false;
                                    endcheck = true;
                                    blankcount = 0;
                                }
                                else{
                                    if(startcheck){
                                        continue;
                                    }
                                    else {
                                        blankcount++;
                                        endcheck = false;
                                    }
                                }
                                if(blankcount >= Maxblank){
                                    Maxblank = blankcount;
                                }
                                if(endcheck){
                                    if(spinValue.get(3) < Maxblank){
                                        blankOk = false;
                                        break;
                                    }
                                }
                            }
                            if(!blankOk)
                                break;
                        }
                    }
                    if(blankOk) {
                        Log.d("tag", "minyoung 과목 목록이 담겼습니다.");
                        selectedSubSubject.add(new ArrayList<Subject>());
                        for (int k = 0; k < OKSubSubject.size(); k++) {
                            selectedSubSubject.get(selectedSubSubject.size() - 1).add(OKSubSubject.get(k)); // 사용될 과목에 넣기      >>>>>>>>>>>>>>> ???
                        }
                    }
                    if(selectedSubSubject.size() >= 10){
                        break;
                    }
                }
                else {
                    Log.d("tag", "minyoung 아직 최소학점을 못넘었습니다.");
                }
                Log.d("tag", "minyoung tmpSubSubject.size() :" + tmpSubSubject.size());
            }

            Log.d("tag", "minyoung selectedSub.size : " + selectedSubSubject.size());
            for(int j = 0; j <selectedSubSubject.size() ; j++){
                for(int k = j+1; k < selectedSubSubject.size(); k++){
                    if(selectedSubSubject.get(j).equals(selectedSubSubject.get(k))){
                        Log.d("tag", "minyoung selectedSub중복제외" +j+" // "+ k);
                        selectedSubSubject.remove(k);       // 중복된 결과 제외
                        k--;
                    }
                }
            }
            //조건 테스트하기전에 일단 되는경우 다넣기
            if(selectedSubSubject.size() >= 10){
                break;
            }
            // 조건테스트해야되는것 : 공강이 비었는가,  >>>>>>>>>>>>>>> OK
            OKSubSubject.clear();
        }
        for(int i = 0; i < selectedSubSubject.size(); i++) {
            for(int j = 0 ; j < needSubject.size() ; j++) {
                selectedSubSubject.get(i).add(needSubject.get(j));
            }
        }
        if (selectedSubSubject.size() < 10) {
            selectedSubSubject.add(new ArrayList<Subject>());
            for(int i = 0; i < needSubject.size() ; i++){
                selectedSubSubject.get(selectedSubSubject.size()-1).add(needSubject.get(i));
            }
        }

        AppContext.tempTimeTableList.addAll(selectedSubSubject);
    }

}

//리스트뷰 어댑터
class CustomLvAdapter extends BaseAdapter {

    private ArrayList<ArrayList<Subject>> list = new ArrayList<ArrayList<Subject>>();
    LayoutInflater inflater = null;

    CustomLvAdapter(ArrayList<ArrayList<Subject>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        if (convertview == null) {
            Context context = parent.getContext();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.maderesult_listitem, parent, false);
        }

        TextView tv = (TextView) convertview.findViewById(R.id.maderesult_lstv_name);

        tv.setText("시간표 " + (position + 1));
        return convertview;
    }
}

class UserInputData {
    int[] cClasses = new int[42]; // 저장되는 과목
    String cFileName; // 시간표 이름

    public UserInputData() {
    } // 기본생성자

    public UserInputData(int[] classes, String fileName) {
        for (int i = 0; i < 42; i++) {
            cClasses[i] = classes[i];
        }
        cFileName = fileName;
    }

    public String getFileName() {
        return cFileName;
    }

    public int[] getcClasses() {
        return cClasses;
    }
}



















