package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NeedActivity extends Activity {

    //확장 리스트뷰
    NeedExpandableListAdapter explistAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Subject>> listDataChild;
    Intent intentToSub;
    static ArrayList<Subject> needSubject = new ArrayList<Subject>(); //다음으로 넘길 과목
    int i = 0;
    //확장 리스트뷰에 복사할 리스트 선언
    List<String> mlistDataHeader;
    HashMap<String, List<Subject>> mlistDataChild;


    //어느 액티비티에서 search를 호출했는지
    static final int NEED = 0;

    //다른 그룹 오픈시 열려있는 그룹 닫기 메서드 선언부
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    //리스트뷰
    private ListView mlistView = null;
    private static ArrayList<Subject> selectedNeedList = new ArrayList<Subject>(); //위 리스트에 표시되는 과목
    protected static CustomListAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        Button nextBtn = (Button) findViewById(R.id.need_btn_nextbutton);
        intentToSub = new Intent(NeedActivity.this, SubActivity.class);



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToSub.putExtra("NeedSubject", needSubject);
                startActivity(intentToSub);
            }
        });

        Button searchBtn = (Button) findViewById(R.id.need_btn_searchbutton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NeedActivity.this, SearchActivity.class);
                startActivityForResult(intent, NEED);
            }
        });

        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.need_elstv_showsubject);

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


        // 확장리스트 뷰 어댑터 준비;
        mlistDataChild = listDataChild;
        mlistDataHeader = listDataHeader;
        explistAdapter = new NeedExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(explistAdapter);


        //스피너 설정
        final Spinner collegeSpinner;
        collegeSpinner = (Spinner) findViewById(R.id.need_spinner_college);
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
        mlistView = (ListView) findViewById(R.id.need_lstv_showSelet);
        madapter = new CustomListAdapter(selectedNeedList, needSubject);
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
    }//온크리에이트의 끝

    protected void onDestroy() {
        selectedNeedList.clear();
        needSubject.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if((requestCode == NEED) && (resultCode == SearchActivity.SUCCESS)) {
            Subject selected = (Subject) data.getSerializableExtra("subject");

            if(isValid(selected)) { // 리스트에 이미 있으면 실행안됨
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
                Toast.makeText(NeedActivity.this, "이미 담긴 과목입니다", Toast.LENGTH_LONG).show();
            }
        }
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
        int i = 0;
        for(i = 0; i < selectedNeedList.size();i++){
            if(selectedNeedList.get(i).cNum.substring(0, 8).equals(sub.cNum.substring(0,8))){
                ret = false;
                break;
            }
        }
        for(i = 0 ; i < needSubject.size(); i++){
            if(needSubject.get(i).cNum.substring(0, 8).equals(sub.cNum.substring(0, 8))){
                ret = false;
                break;
            }
        }
        return ret;
    }

}

//확장 리스트뷰 어댑터
class NeedExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private static List<String> _listDataHeader;
    private static HashMap<String, List<Subject>> _listDataChild;


    public NeedExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Subject>> listChildData) {
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
                if(NeedActivity.isValid(selectedSubject)) { // 리스트에 이미 있으면 실행안됨
                    int i = 0;
                    NeedActivity.madapter.additem(selectedSubject);
                    while(i < AppContext.subjectList.length){
                        Subject sub = AppContext.subjectList[i++];
                        if(sub.cNum.equals(selectedSubject.cNum)){
                            NeedActivity.madapter.addNeed(sub);
                        }
                    }
                    NeedActivity.madapter.notifyDataSetChanged();
                }
            }
        });

        childText += child.getName() + " / " + child.cProf + "교수\n" + child.day() + " " + child.getTime();

        //같은과목(ex 선대1반 화욜/목욜)시간표시
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

//리스트뷰 어댑터
class CustomListAdapter extends BaseAdapter {

    private ArrayList<Subject> oData = new ArrayList<Subject>();
    private ArrayList<Subject> intentSubject = new ArrayList<Subject>(); //다음으로 넘겨줄 과목값
    LayoutInflater inflater = null;

    CustomListAdapter(ArrayList<Subject> list, ArrayList<Subject> intentSubject) {
        oData = list;
        this.intentSubject = intentSubject;
    };

    @Override
    public int getCount() {
        return oData.size();
    }

    @Override
    public Object getItem(int position) {
        return oData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup parent) {
        if (convertview == null) {
            Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertview = inflater.inflate(R.layout.need_lstv_item, parent, false);
        }

        Button deleteBtn = (Button) convertview.findViewById(R.id.need_lstv_btn);
        TextView need_lstv_tv_choosedsubject = (TextView) convertview.findViewById(R.id.need_lstv_tv);
        need_lstv_tv_choosedsubject.setText(oData.get(position).getName());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = intentSubject.size() - 1;
                while(i >= 0 ){
                    Subject sub = intentSubject.get(i--);
                    if(sub.cNum.equals(((Subject)getItem(position)).cNum)){
                        intentSubject.remove(sub);
                    }
                }
                oData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertview;
    }

    public void additem(Subject sub) {
        oData.add(sub);
    }

    public void addNeed(Subject need) { intentSubject.add(need); }
}

