package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
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

    //처음화면 뜰때 다이얼로그 boolean 값 선언
    public static boolean first = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        if(first) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
            dialog.setTitle("사용법");
            dialog.setMessage("* 꼭! 들어야 하지는 않지만 듣고 싶은 과목을 선택하세요 \n * 후보과목은 최대 50개까지 선택이 가능합니다.\n " +
                    "* 후보과목을 다량 선택시 계산시간이 다소 소요됩니다. ");
            dialog.setNeutralButton("다시 보지 않기", yesButtonClickListener);
            dialog.show();
        }

        Button nextBtn = (Button) findViewById(R.id.sub_btn_nextbutton);
        needSubject = (ArrayList<Subject>) getIntent().getSerializableExtra("NeedSubject");
        intentToSetting = new Intent(SubActivity.this, SettingActivity.class);

        //다음버튼
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

        //검색버튼
        ImageButton searchBtn = (ImageButton) findViewById(R.id.sub_btn_searchButton);
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
                listDataChild.clear();
                listDataHeader.clear();
                lastExpandedPosition = -1;
                prepareListData(position);
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

    //prepareListData 안에서 쓰는 함수
    public void setList(String[] college, int major) {
        ArrayList<Subject> sub = new ArrayList<Subject>();
        for (int i = 0; i < college.length; i++) { // i는 학과
            listDataHeader.add(college[i]);
            for (int j = 0; j < AppContext.onlySubjectList.size(); j++) {
                if ((AppContext.onlySubjectList.get(j).cDepart == i + major) && (AppContext.onlySubjectList.get(j).cDay != -1))
                    sub.add(AppContext.onlySubjectList.get(j));
            }
            listDataChild.put(listDataHeader.get(i), (List<Subject>) sub.clone());
            sub.clear();
        }
    }

    // 스피너값에 따른 리스트 출력
    public void prepareListData(int position) {

        String[] college;

        switch (position) {
            case 0: //정경대
                college = getResources().getStringArray(R.array.polEco);
                setList(college, 0);
                break;
            case 1: //생과대
                college = getResources().getStringArray(R.array.livingScience);
                setList(college, 7);
                break;
            case 2: //의대
                college = getResources().getStringArray(R.array.medical);
                setList(college, 12);
                break;
            case 3: //한의대
                college = getResources().getStringArray(R.array.korMedical);
                setList(college, 14);
                break;
            case 4: //치의대
                college = getResources().getStringArray(R.array.tooth);
                setList(college, 16);
                break;
            case 5: //약대
                college = getResources().getStringArray(R.array.medicine);
                setList(college, 18);
                break;
            case 6: //음대
                college = getResources().getStringArray(R.array.music);
                setList(college, 22);
                break;
            case 7: // 호관대
                college = getResources().getStringArray(R.array.hotel);
                setList(college, 26);
                break;
            case 8: //자전
                college = getResources().getStringArray(R.array.self);
                setList(college, 36);
                break;
            case 9: // 문대
                college = getResources().getStringArray(R.array.write);
                setList(college, 38);
                break;
            case 10: //경영대
                college = getResources().getStringArray(R.array.ceo);
                setList(college, 43);
                break;
            case 11: // 이과대
                college = getResources().getStringArray(R.array.science);
                setList(college, 46);
                break;
            case 12: //간호대
                college = getResources().getStringArray(R.array.nurse);
                setList(college, 52);
                break;
            case 13: //미대
                college = getResources().getStringArray(R.array.art);
                setList(college, 54);
                break;
            case 14: // 무용대
                college = getResources().getStringArray(R.array.dance);
                setList(college, 58);
                break;
            case 15: // 후마니타스
                college = getResources().getStringArray(R.array.huma);
                setList(college, 62);
                break;
            case 16: //기타
                college = getResources().getStringArray(R.array.etc);
                setList(college, 82);
                break;
        }
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

    //액티비티가 종료될때 static변수 비워주기
    protected void onDestroy() {
        selectedSubList.clear();
        super.onDestroy();
    }

    //검색창에서 선택한 과목 추가
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

    public static boolean isValid(Subject sub){ // 리스트에 과목이 없다-true 있다-false
        boolean ret = true;
        int i, j;
        for(i = 0; i < selectedSubList.size();i++){
            if(selectedSubList.get(i).cNum.equals(sub.cNum)){
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
        final ImageButton btn = (ImageButton) convertView.findViewById(R.id.need_elstv_item_btn);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            //차일드 버튼 클릭 -> 리스트뷰 데이터 입력
            public void onClick(View view) {

                Subject selectedSubject = _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
                if (SubActivity.madapter.getCount() < 50) {
                    if (SubActivity.isValid(selectedSubject)) {
                        int i = 0;
                        SubActivity.madapter.additem(selectedSubject);
                        while (i < AppContext.subjectList.length) {
                            Subject sub = AppContext.subjectList[i++];
                            if (sub.cNum.equals(selectedSubject.cNum)) {
                                SubActivity.madapter.addNeed(sub);
                            }
                        }
                        SubActivity.madapter.notifyDataSetChanged();
                    }
                } else {

                    Toast toast = Toast.makeText(_context, "더이상 등록할 수 없습니다.",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    toast.show();
                }
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



