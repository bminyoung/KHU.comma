package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity extends AppCompatActivity {
    //확장 리스트뷰
    ExpandableListAdapter explistAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Subject>> listDataChild;
    ArrayList<Subject> needSubject;
    static ArrayList<Subject> subSubject = new ArrayList<Subject>();
    Intent intentToSetting;
    int i = 0;

    //다른 그룹 오픈시 열려있는 그룹 닫기 메서드 선언부
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    //리스트뷰
    private ListView mlistView = null;
    protected static CustomListAdapter madapter = new CustomListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Button nextBtn = (Button) findViewById(R.id.sub_btn_nextbutton);
        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        intentToSetting = new Intent(SubActivity.this, SettingActivity.class);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subSubject = madapter.getSubjectList();
                if(needSubject.size() + subSubject.size() == 0){
                    Alert();
                }
                else {
                    intentToSetting.putExtra("SubSubject", subSubject);
                    intentToSetting.putExtra("NeedSubject", needSubject);
                    startActivity(intentToSetting);
                }
            }
        });


        Button searchBtn = (Button) findViewById(R.id.sub_btn_searchButton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.sub_elstv_showSubject);

        // 확장리스트 뷰 어댑터 준비
        prepareListData();
        explistAdapter = new CustomExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(explistAdapter);

        //리스트뷰
        mlistView = (ListView) findViewById(R.id.sub_lstv_showSelet);
        mlistView.setAdapter(madapter);

        //그룹 클릭시 이전 그룹이 닫히게 구현
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                    lastExpandedPosition = groupPosition;
                } else
                    lastExpandedPosition = groupPosition;
            }
        });
    }

    private void Alert(){
        new AlertDialog.Builder(this).setTitle("오류").setMessage("선택된 과목이 없습니다").setNegativeButton("닫기",null).show();
    }

//데이터 입력
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Subject>>();

        // 그룹 데이터 입력
        listDataHeader.add("2학년 전공");
        listDataHeader.add("1학년 전공");

        // 그룹 1의 차일드 데이터 입력
        List<Subject> major2 = new ArrayList<Subject>();
        while(!(AppContext.onlySubjectList.get(i).getName().equals("물리학및실험1"))){
            major2.add(AppContext.onlySubjectList.get(i++));
        }

        //그룹 2의 차일드 데이터 입력
        List<Subject> major1 = new ArrayList<Subject>();
        while(i < AppContext.onlySubjectList.size()){
            major1.add(AppContext.onlySubjectList.get(i++));
        }

        //그룹에 데이터 할당
        listDataChild.put(listDataHeader.get(0), major2); // Header, Child data
        listDataChild.put(listDataHeader.get(1), major1);
    }

}



