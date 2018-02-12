package comma.khutimetablehelper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MadeResultActivity extends AppCompatActivity {

    //시간표색상
    int[] colors = {Color.parseColor("#B8F3B8"),Color.parseColor("#FFA9B0"),Color.parseColor("#CCD1FF"),Color.parseColor("#FFDDA6"),Color.parseColor("#FFADC5")};
    int colorIndex = 0;
    private int lastExpandedPosition = -1;

    ArrayList<String> groupData; //확장리스트뷰 - group
    ArrayList<Subject> table;
    ArrayList<Subject> needSubject;
    ArrayList<Subject> subSubject;
    ArrayList<Integer> spinStatus;
    ArrayList<Integer> spinValue;
    ArrayList<Subject> filteredSubSubject;
    ArrayList<ArrayList<ArrayList<Subject>>> childData; // 확장리스트뷰 - child
    public TextView timeTable[][] = new TextView[140][5]; //시간표 각 칸
    int focusOn; // 저장될 시간표의 위치
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maderesult);

        Button btnSave = (Button) findViewById(R.id.maderesult_btn_save);
        Button btnMain = (Button) findViewById(R.id.maderesult_btn_main);
        setTextId();
        focusOn = -1;

        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.maderesult_elv_List);
        setData();
        final ExpandableListAdapter expAdapter = new CustomElvAdapter(this, groupData, childData);
        expListView.setAdapter(expAdapter);

        //저장, 메인버튼
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(focusOn < 0) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MadeResultActivity.this);
                    dialogBuilder.setTitle("저장할 시간표를 선택해주세요").setNegativeButton("취소", null).show();
                }else{
                    ShowSaveDialog(focusOn);
                }
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MadeResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // child목록을 클릭했을때 위 시간표에 뜨도록 이벤트설정
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                Subject[] subjects = new Subject[10];
                ArrayList<Subject> selectedTimeTable = (ArrayList<Subject>) expAdapter.getChild(groupPosition, childPosition);

                colorIndex = 0;
                for(int i = 0; i <selectedTimeTable.size();i++) {
                    ShowTimeTable(selectedTimeTable.get(i));
                }
                focusOn = childPosition;
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                    lastExpandedPosition = groupPosition;
                } else
                    lastExpandedPosition = groupPosition;
            }
        });

        ArrayList<Subject> needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        ArrayList<Subject> subSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("SubSubject");
        ArrayList<Integer> spinStatus = (ArrayList<Integer>) getIntent().getSerializableExtra("spinStatus");
        ArrayList<Integer> spinValue = (ArrayList<Integer>) getIntent().getSerializableExtra("spinValue");

        Log.d("tag", "minyoung" + needSubject.size() + "" + subSubject.size() + "" + spinStatus.size() + "" + spinValue.size());

        ArrayList<Subject> filteredSubSubject = subSubject;

        // 시작시간, 점심시간, 공강요일, 강의최대시간, 끝나는시간에 맞지않는과목 걸러내기
        for (int i = 0; i < subSubject.size(); i++)
        {
            if(spinStatus.get(1) == 0) {
                filterStartTime(spinValue.get(2), subSubject.get(i).cStart, i);
            }
            if(spinStatus.get(3) == 0) {
                filterBlankDay(spinValue.get(4), subSubject.get(i).cDay, i);
            }
            if(spinStatus.get(5) == 0) {
                filterLunchTime(spinValue.get(6), spinValue.get(7), subSubject.get(i).cStart, subSubject.get(i).cEnd, i);
            }
            if(spinStatus.get(6) == 0) {
                filterMaxLectureTime(subSubject.get(i).cStart, subSubject.get(i).cEnd, i);
            }
            if(spinStatus.get(9) == 0) {
                filterDayEndTime(subSubject.get(i).cDay, subSubject.get(i).cEnd, i);
            }
        }
    }

    private void SaveTimeTable(int position, String timeTableName){
        AppContext.timeTableList.add(AppContext.tempTimeTableList.get(position));
        AppContext.timeTableNameList.add(timeTableName);
        Log.d("tag","minyoung 여기까지 왓다!!!" + timeTableName);

    }

    public void ShowTimeTable(Subject selected){
        TextView tv;
        int start = (int) ((selected.cStart - 9.0)*2.0);
        int end = (int)((selected.cEnd - 9.0)*2.0 - 1.0);

        for(int i = end; i >= start;i--){
            tv = timeTable[i][selected.cDay];
            tv.setBackgroundColor(colors[colorIndex]);
        }
        timeTable[start][selected.cDay].setText(selected.cName);
        colorIndex++;
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
        groupData = new ArrayList<String>();
        childData = new ArrayList<ArrayList<ArrayList<Subject>>>();
        int sizeList = 0;
        int i = 0;

        groupData.add("첫번째 리스트");
        childData.add(new ArrayList<ArrayList<Subject>>());
        childData.get(sizeList).add(AppContext.tempTimeTableList.get(i++));
//        childData.get(sizeList).add(new Subject("","위상수학1","",0,3,0, 0, 15.0, 16.5));
//        childData.get(sizeList).add(new Subject("","해석학1","",0,3,0, 1, 12.0, 13.5));

        groupData.add("두번째 리스트");
        childData.add(new ArrayList<ArrayList<Subject>>());
        sizeList++;
        childData.get(sizeList).add(AppContext.tempTimeTableList.get(i++));
        childData.get(sizeList).add(AppContext.tempTimeTableList.get(i++));
//        childData.get(sizeList).add(new Subject("","물리1","",0,3,0, 2, 12.0, 13.5));
//        childData.get(sizeList).add(new Subject("","물리2","",0,3,0, 3, 15.0, 16.5));

    }

    //다이얼로그
    private void ShowSaveDialog(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout saveLayout = (LinearLayout) inflater.inflate(R.layout.maderesult_savedialog, null);

        final EditText TimeTableTitle = (EditText) saveLayout.findViewById(R.id.dialog_edt_title);

        //저장,취소 버튼
        Button dialogBtnSave = (Button) saveLayout.findViewById(R.id.dialog_btn_save);
        Button dialogBtnCancel = (Button) saveLayout.findViewById(R.id.dialog_btn_cancel);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("이 시간표를 저장하시겠습니까?").setView(saveLayout);

        final AlertDialog dialog;
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false); //다이얼로그 밖 터치해도 안 꺼지도록
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialogBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TimeTableTitle.getText().length() == 0){
                    Toast.makeText(MadeResultActivity.this, "시간표이름을 입력해주세요", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MadeResultActivity.this, TimeTableTitle.getText() + " 이 저장되었습니다", Toast.LENGTH_LONG).show();
                    SaveTimeTable(focusOn, String.valueOf(TimeTableTitle.getText()));
                }
                dialog.cancel();
            }
        });
        dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    // 시작시간이 10시, 11시, 12시이전인 후보과목 걸러내기
    public void filterStartTime(int firstTimeSpinnerNum, double subSubjectStartTime, int subSubjectArrayNum){
        switch(firstTimeSpinnerNum) {
            case 1:
                if (subSubjectStartTime < 10) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 2:
                if (subSubjectStartTime < 11) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 3:
                if (subSubjectStartTime < 12) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
        }
    }

    // 점심시간에 해당되는 후보과목 걸러내기
    public void filterLunchTime(int lunchStartTimeSpinnerNum, int lunchEndTimeSpinnerNum, double subSubjectStartTime, double subSubjectEndTime, int subSubjectArrayNum){
        double tmpLunchStartTime = 0.0;
        double tmpLunchEndTime = 0.0;
        switch(lunchStartTimeSpinnerNum) {
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
        switch(lunchEndTimeSpinnerNum) {
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
            filteredSubSubject.remove(subSubjectArrayNum);
        }
    }

    // 공강요일 설정된 과목 걸러내기
    public void filterBlankDay(int daySpinnerNum, int subSubjectDay, int subSubjectArrayNum) {
        switch (daySpinnerNum) {
            case 0:
                if(subSubjectDay != 0) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 1:
                if(subSubjectDay != 1) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 2:
                if(subSubjectDay != 2) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 3:
                if(subSubjectDay != 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 4:
                if(subSubjectDay != 4) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
        }
    }

    // 요일별 끝나는시간 걸러내기

    public void filterDayEndTime(int subSubjectDay, double subSubjectEndTime, int subSubjectArrayNum){ //첫번째 : subsubject의 요일

        switch (subSubjectDay) {
            case 0:
                if(subSubjectEndTime > spinValue.get(11) + 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 1:
                if(subSubjectEndTime > spinValue.get(12) + 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 2:
                if(subSubjectEndTime > spinValue.get(13) + 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 3:
                if(subSubjectEndTime > spinValue.get(14) + 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 4:
                if(subSubjectEndTime > spinValue.get(15) + 3) {
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
        }
    }

    public void filterMaxLectureTime(double subSubjectStartTime, double subSubjectEndTime, int subSubjectArrayNum){
        double calculatedTime = subSubjectEndTime - subSubjectStartTime;
        switch (spinValue.get(8)) {
            case 0:
                if(calculatedTime > 1.5){
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 1:
                if(calculatedTime > 3.0){
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
            case 2:
                if(calculatedTime > 4.5){
                    filteredSubSubject.remove(subSubjectArrayNum);
                }
                break;
        }
    }

    public void calculateSubject() {
        int[][] SubjectCell = new int[26][5];
        int[][] Code = new int[10][21];

        ArrayList<Subject> tmpSubSubject = filteredSubSubject;
        ArrayList<Subject> getUsedFirstSubSubject = new ArrayList<Subject>();
        ArrayList<Subject> getUsedSecondSubSubject;
        ArrayList<Subject> getUsedThirdSubSubject;
        ArrayList<Subject> getUsedForthSubSubject;
        ArrayList<Subject> getUsedFifthSubSubject;

        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 5; j++) {
                SubjectCell[i][j] = 0;
            }
        }
        boolean registCheck = true;
        int getSettedMinCreditCount = spinValue.get(0) + 15;
        int getSettedMaxCreditCount = spinValue.get(1) + 15;
        int nowCreditCount = 0;
        int monDayClassCount = 0;
        int tuesDayClassCount = 0;
        int wednesDayClassCount = 0;
        int thursDayClassCount = 0;
        int friDayClassCount = 0;
        int dayCount = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < filteredSubSubject.size(); j++) {
                registCheck = true;
                if(spinStatus.get(4) == 0) {        //요일수가 넘어가는지 체크
                    dayCount = 5;
                    switch (filteredSubSubject.get(j).cDay){
                        case 0:
                            if(monDayClassCount == 0) {
                                dayCount++;
                            }
                            break;
                        case 1:
                            if(tuesDayClassCount == 0){
                                dayCount--;
                            }
                            break;
                        case 2:
                            if(wednesDayClassCount == 0){
                                dayCount--;
                            }
                            break;
                        case 3:
                            if(thursDayClassCount == 0){
                                dayCount--;
                            }
                            break;
                        case 4:
                            if(friDayClassCount == 0){
                                dayCount--;
                            }
                            break;
                    }
                    if(monDayClassCount == 0) {
                        dayCount--;
                    }
                    if(tuesDayClassCount == 0){
                        dayCount--;
                    }
                    if(wednesDayClassCount == 0) {
                        dayCount--;
                    }
                    if(thursDayClassCount == 0){
                        dayCount--;
                    }
                    if(friDayClassCount == 0){
                        dayCount--;
                    }
                    if(dayCount > spinValue.get(5) + 3) {
                        registCheck = false;
                    }
                }

                if (nowCreditCount + filteredSubSubject.get(j).cCredit > getSettedMaxCreditCount) {     //학점이 되는지 체크
                    registCheck = false;
                }
                if(spinStatus.get(7) == 0){     // 요일별 최대강의수를 넘었는지 체크
                    switch (filteredSubSubject.get(j).cDay) {
                        case 0:
                            if(monDayClassCount >= spinValue.get(9) + 2) {
                                registCheck = false;
                            }
                            break;
                        case 1:
                            if(tuesDayClassCount >= spinValue.get(9) + 2) {
                                registCheck = false;
                            }
                            break;
                        case 2:
                            if(wednesDayClassCount >= spinValue.get(9) + 2) {
                                registCheck = false;
                            }
                            break;
                        case 3:
                            if(thursDayClassCount >= spinValue.get(9) + 2) {
                                registCheck = false;
                            }
                            break;
                        case 4:
                            if(friDayClassCount >= spinValue.get(9) + 2) {
                                registCheck = false;
                            }
                            break;
                    }
                }

                if (spinStatus.get(2) == 0) {   //공강 체크
                    for (int k = 0; k < spinValue.get(3) + 2; k++) {
                        if (SubjectCell[(int) ((filteredSubSubject.get(j).cStart - 9) * 2 - k)][filteredSubSubject.get(j).cDay] == 0) {
                            registCheck = false;
                            break;
                        }
                    }
                }
                for (int k = 0; k < (int) ((filteredSubSubject.get(j).cEnd - filteredSubSubject.get(j).cStart) * 2); k++) {     // 들어갈 시간이 비었는지 체크
                    if (SubjectCell[(int) ((filteredSubSubject.get(j).cStart - 9) * 2 + k)][filteredSubSubject.get(j).cDay] != 0) {
                        registCheck = false;
                        break;
                    }
                }
                if (registCheck == true) {
                    for (int k = 0; k < (int) ((filteredSubSubject.get(j).cEnd - filteredSubSubject.get(j).cStart) * 2); k++) {
                        SubjectCell[(int) ((filteredSubSubject.get(j).cStart - 9) * 2 + k)][filteredSubSubject.get(j).cDay] = filteredSubSubject.get(j).cRow;
                        nowCreditCount = nowCreditCount + filteredSubSubject.get(j).cCredit;
                        getUsedFirstSubSubject.add(j, filteredSubSubject.get(j));
                        switch (filteredSubSubject.get(j).cDay) {
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
                }
            }
        }
    }
}

//확장리스트뷰 어댑터
class CustomElvAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> groupData;
    ArrayList<ArrayList<ArrayList<Subject>>> childData;
    LayoutInflater madeResultInflater;
    int num = 1;

    CustomElvAdapter(Context context, ArrayList<String> groupData, ArrayList<ArrayList<ArrayList<Subject>>> childData) {
        this.groupData = groupData;
        this.childData = childData;
        madeResultInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return groupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData.get(groupPosition).get(childPosition); // ArrayList<Subject> type
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = madeResultInflater.inflate(R.layout.maderesult_group, null);
        }

        TextView groupName = (TextView) convertView.findViewById(R.id.maderesult_tv_group);
        groupName.setText((String) getGroup(groupPosition));


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = madeResultInflater.inflate(R.layout.maderesult_child, null);
        }
        ArrayList<Subject> selected = (ArrayList<Subject>) getChild(groupPosition, childPosition);

        TextView childName = (TextView) convertView.findViewById(R.id.maderesult_tv_child);
        childName.setText("시간표 " + (childPosition + 1));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}






















