package comma.khutimetablehelper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

    AppContext app;

    //시간표색상
    int[] colors = {Color.parseColor("#B8F3B8"),Color.parseColor("#FFA9B0"),Color.parseColor("#CCD1FF"),Color.parseColor("#FFDDA6"),Color.parseColor("#FFADC5")};
    int colorIndex = 0;

    ArrayList<String> groupData;
    ArrayList<ArrayList<Subject>> childData;
    public TextView timeTable[][] = new TextView[140][5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maderesult);

        app = (AppContext)getApplicationContext();

        Button btnSave = (Button) findViewById(R.id.maderesult_btn_save);
        Button btnMain = (Button) findViewById(R.id.maderesult_btn_main);
        setTextId();

        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.maderesult_elv_List);
        setData();
        final ExpandableListAdapter expAdapter = new CustomElvAdapter(this, groupData, childData);
        expListView.setAdapter(expAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSaveDialog();
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MadeResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // child목록을 클릭했을때 위 시간표에 뜨도록 이벤트설정
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                TextView tv;
                Subject selectedSubject = (Subject) expAdapter.getChild(groupPosition, childPosition);
                int start = (int) ((selectedSubject.cStart - 9.0)*2.0);
                int end = (int)((selectedSubject.cEnd - 9.0)*2.0 - 1.0);

                tv = timeTable[start][selectedSubject.cDay];
                tv.setText(selectedSubject.cName);
                for(int i = start; i <= end;i++){
                    tv = timeTable[i][selectedSubject.cDay];
                    tv.setBackgroundColor(colors[colorIndex]);
                }
                colorIndex++;
                return false;
            }
        });


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
        childData = new ArrayList<ArrayList<Subject>>();
        int sizeList = 0;

        groupData.add("수학과");
        childData.add(new ArrayList<Subject>());
        //childData.get(sizeList).add(subjectList[0]);

        groupData.add("물리학과");
        childData.add(new ArrayList<Subject>());
        sizeList++;

    }

    //다이얼로그
    private void ShowSaveDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout saveLayout = (LinearLayout) inflater.inflate(R.layout.maderesult_savedialog, null);

        final EditText TimeTableTitle = (EditText) saveLayout.findViewById(R.id.dialog_edt_title);
        // dialog띄우면 edt에 자동포커스->키보드
        TimeTableTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        final InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);


        //저장,취소 버튼
        Button dialogBtnSave = (Button) saveLayout.findViewById(R.id.dialog_btn_save);
        Button dialogBtnCancel = (Button) saveLayout.findViewById(R.id.dialog_btn_cancel);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("이 시간표를 저장하시겠습니까?").setView(saveLayout);

        final AlertDialog dialog;
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialogBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MadeResultActivity.this, TimeTableTitle.getText() + " 이 저장되었습니다", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.cancel();
            }
        });

        dialog.show();
    }
}

class CustomElvAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> groupData;
    ArrayList<ArrayList<Subject>> childData;
    LayoutInflater madeResultInflater;

    CustomElvAdapter(Context context, ArrayList<String> groupData, ArrayList<ArrayList<Subject>> childData) {
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
        return childData.get(groupPosition).get(childPosition);
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

        TextView childName = (TextView) convertView.findViewById(R.id.maderesult_tv_child);
        final Subject selectedChild = (Subject)getChild(groupPosition, childPosition);
        childName.setText((String) selectedChild.getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}























