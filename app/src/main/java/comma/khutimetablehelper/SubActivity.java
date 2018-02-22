package comma.khutimetablehelper;

import java.lang.reflect.Array;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity extends AppCompatActivity {

    //확장 리스트뷰
    SubExpandableListAdapter explistAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Subject>> listDataChild;
    static ArrayList<Subject> needSubject;
    Intent intentToSetting;
    int i = 0;

    //확장 리스트뷰에 복사할 리스트 선언
    List<String> mlistDataHeader;
    HashMap<String, List<Subject>> mlistDataChild;

    //어느 액티비티에서 search를 호출했는지
    static final int SUB = 1;

    //다른 그룹 오픈시 열려있는 그룹 닫기 메서드 선언부
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    //리스트뷰
    private ListView mlistView = null;
    private static ArrayList<Subject> selectedSubList = new ArrayList<Subject>(); // 위 리스트에 표시될 과목
    private ArrayList<Subject> subSubject = new ArrayList<Subject>(); // 다음으로 넘길 과목
    protected static CustomListAdapter madapter;

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
                if(needSubject.size() + selectedSubList.size() == 0){
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
                startActivityForResult(intent, SUB);
            }
        });

        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.sub_elstv_showSubject);

        //preparelistdata() 함수 내용을 밖으로 뺏음.. major1,2를 불러오기 위해
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Subject>>();
        // 그룹 데이터 입력
        listDataHeader.add("2학년 전공");
        listDataHeader.add("1학년 전공");
        // 그룹 1의 차일드 데이터 입력
        final List<Subject> major2 = new ArrayList<Subject>();
        while (!(AppContext.onlySubjectList.get(i).getName().equals("물리학및실험1"))) {
            major2.add(AppContext.onlySubjectList.get(i++));
        }
        //그룹 2의 차일드 데이터 입력
        final List<Subject> major1 = new ArrayList<Subject>();
        while (i < AppContext.onlySubjectList.size()) {
            major1.add(AppContext.onlySubjectList.get(i++));
        }
        //그룹에 데이터 할당
        ; // Header, Child data
        listDataChild.put(listDataHeader.get(0), major2);
        listDataChild.put(listDataHeader.get(1), major1);

        // 확장리스트 뷰 어댑터 준비
        mlistDataChild = listDataChild;
        mlistDataHeader = listDataHeader;
        explistAdapter = new SubExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(explistAdapter);

        //스피너 설정
        final Spinner collegeSpinner;
        collegeSpinner = (Spinner) findViewById(R.id.sub_spinner_college);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.searchkeyselect, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeSpinner.setAdapter(adapter);

        //스피너 값 변경 이벤트
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {
//                String charText = collegeSpinner.getSelectedItem().toString();
//                search(charText);
                TextView textView;
                String charText;
                listDataChild.clear();
                listDataHeader.clear();

                if (position == 0) {
                    listDataHeader.add("2학년 전공");
                    listDataChild.put(listDataHeader.get(0), major2);

                } else {
                    listDataHeader.add("1학년 전공");
                    listDataChild = mlistDataChild;
                    listDataChild.put(listDataHeader.get(0), major1);

                }
                explistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        //리스트뷰
        mlistView = (ListView) findViewById(R.id.sub_lstv_showSelet);
        madapter = new CustomListAdapter(selectedSubList, subSubject);
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

    protected void onDestroy() {
        selectedSubList.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if((requestCode == SUB) && (resultCode == SearchActivity.SUCCESS)) {
            Subject selected = (Subject) data.getSerializableExtra("subject");

            if(isValid(selected)) { // 리스트에 이미 있으면
                int i = 0;
                madapter.additem(selected);
                while(i < AppContext.subjectList.length){
                    Subject sub = AppContext.subjectList[i++];
                    if(sub.cNum.equals(selected.cNum)){
                        madapter.addNeed(sub);
                    }
                }
                madapter.notifyDataSetChanged();
            }else{
                Toast.makeText(SubActivity.this, "이미 담긴 과목입니다", Toast.LENGTH_LONG).show();
            }
        }
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

    public static boolean isValid(Subject sub){ // 리스트에 과목이 없다-true 있다-false
        boolean ret = true;
        int i ;
        int j ;
        for(i = 0; i < selectedSubList.size();i++){
            if(selectedSubList.get(i).cNum.substring(0, 8).equals(sub.cNum.substring(0, 8))){
                ret = false;
                break;
            }
        }
        for(j = 0 ; j < needSubject.size(); j++){
            if(needSubject.get(j).cNum.substring(0, 8).equals(sub.cNum.substring(0, 8))){
                ret = false;
                break;
            }
        }
        return ret;
    }

}

//확장 리스트뷰 어댑터
class SubExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Subject>> _listDataChild;


    public SubExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Subject>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Subject child = (Subject) getChild(groupPosition, childPosition);
        String childText = "";
        int i = 0;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.need_elstv_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.need_elstv_item_header);
        final Button btn = (Button) convertView.findViewById(R.id.need_elstv_item_btn);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            //차일드 버튼 클릭 -> 리스트뷰 데이터 입력
            public void onClick(View view) {
                Subject selectedSubject = _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
                int i = 0;
                SubActivity.madapter.additem(selectedSubject);
                while(i < AppContext.subjectList.length){
                    Subject sub = AppContext.subjectList[i++];
                    if(sub.cNum.equals(selectedSubject.cNum)){
                        SubActivity.madapter.addNeed(sub);
                    }
                }
                SubActivity.madapter.notifyDataSetChanged();
            }
        });

        childText += child.getName() + " / " + child.cProf + "교수\n" + child.day() + " " + child.getTime();

        while(i < AppContext.subjectList.length){
            Subject sub = AppContext.subjectList[i];
            if((sub.cNum.equals(child.cNum)) && ((sub.cStart != child.cStart) || sub.cDay != child.cDay)){
                childText += " / " + AppContext.subjectList[i].day() + " " + AppContext.subjectList[i].getTime();
            }
            i++;
        }
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(_listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.need_elstv_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.need_elstv_group_header);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}



