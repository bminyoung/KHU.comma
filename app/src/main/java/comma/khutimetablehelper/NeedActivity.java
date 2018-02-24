package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    //처음화면 뜰때 다이얼로그 boolean 값 선언
    public static boolean first = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        if(first) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("시간표 요약");
            dialog.setMessage("* 본인이 꼭! 들어야 하는 과목을 선택하세요.\n * 강의 시간이 따로 명시되어있지 않은 강의를 추가하고 싶으면 " +
                    "이 화면에서 선택해야합니다. (ex 사이버 강의) \n * ");
            dialog.setNeutralButton("확인", yesButtonClickListener);
            dialog.show();
        }


        Button nextBtn = (Button) findViewById(R.id.need_btn_nextbutton);
        intentToSub = new Intent(NeedActivity.this, SubActivity.class);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToSub.putExtra("NeedSubject", needSubject);
                startActivity(intentToSub);
            }
        }); //다음버튼
        Button searchBtn = (Button) findViewById(R.id.need_btn_searchbutton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NeedActivity.this, SearchActivity.class);
                startActivityForResult(intent, NEED);
            }
        }); //검색버튼

        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.need_elstv_showsubject);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Subject>>();

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
                listDataChild.clear();
                listDataHeader.clear();
                prepareListData(position);
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

    // 단과대학별 리스트목록 저장
    public void prepareChildData(){
        int i = 0;
//        List<Subject> major =
//
//
//        i = 0; //경제학과
//        economic = new ArrayList<Subject>();
//        while (i < AppContext.onlySubjectList.size()) {
//            if(AppContext.onlySubjectList.get(i).cCol == "경제") {
//                economic.add(AppContext.onlySubjectList.get(i));
//            }
//            i++;
//        }
//
//        i = 0; //무역학과
//        trade = new ArrayList<Subject>();
//        while (i < AppContext.onlySubjectList.size()) {
//            if(AppContext.onlySubjectList.get(i).cCol == "무역") {
//                trade.add(AppContext.onlySubjectList.get(i));
//            }
//            i++;
//        }

    }

    // 스피너값에 따른 리스트 출력
    public void prepareListData(int position) {

        String[] college;
        ArrayList<Subject> sub = new ArrayList<Subject>();
        // 그룹 1의 차일드 데이터 입력
        List<Subject> major2 = new ArrayList<Subject>();
        while (!(AppContext.onlySubjectList.get(i).getName().equals("물리학및실험1"))) {
            major2.add(AppContext.onlySubjectList.get(i++));
        }

        //그룹 2의 차일드 데이터 입력
        List<Subject> major1 = new ArrayList<Subject>();
        while (!(AppContext.onlySubjectList.get(i).getName().equals("교과교육론(수학)"))) {
            major1.add(AppContext.onlySubjectList.get(i++));
        }

        switch (position){ //case 0 실행되는지 확인하자
            case 0: //정경대
                college = getResources().getStringArray(R.array.polEco);
                for(int i = 0; i < college.length;i++){ // i는 학과
                    listDataHeader.add(college[i]);
                    for(int j = 0; j <AppContext.onlySubjectList.size();j++) {
                        if(AppContext.onlySubjectList.get(j).cCol == i)
                            sub.add(AppContext.onlySubjectList.get(j));
                    }
                    listDataChild.put(listDataHeader.get(i), (List<Subject>) sub.clone());
                    sub.clear();
                }

//                listDataChild.put(listDataHeader.get(0), major2);//경제학과 붙이기
//                listDataChild.put(listDataHeader.get(1), major2);//무역학과 붙이기
//                listDataChild.put(listDataHeader.get(2), major2);//정외과 붙이기
//                listDataChild.put(listDataHeader.get(3), major2);//행정학과 붙이기
//                listDataChild.put(listDataHeader.get(4), major2);//사회학과 붙이기
//                listDataChild.put(listDataHeader.get(5), major2);//언정과 붙이기
//                listDataChild.put(listDataHeader.get(6), major2);//국제통상과 붙이기
                break;
            case 1: //생과대
                college = getResources().getStringArray(R.array.livingScience);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//생과대 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//식영과 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//의상학과 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//아가과 붙이기
                listDataChild.put(listDataHeader.get(4), major2);//주거과 붙이기
                break;
            case 2: //의대
                college = getResources().getStringArray(R.array.medical);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//의학전공 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//의예전공 붙이기
                break;
            case 3: //한의대
                college = getResources().getStringArray(R.array.korMedical);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//한의학전공 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//한의예 붙이기
                break;
            case 4: //치의대
                college = getResources().getStringArray(R.array.tooth);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//치의학전공 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//치의예 붙이기
                break;
            case 5: //약대
                college = getResources().getStringArray(R.array.medicine);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//약대공통 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//한약학 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//약과학 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//약학전공(2+4) 붙이기
                break;
            case 6: //음대
                college = getResources().getStringArray(R.array.music);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//음대 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//기악과 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//성악과 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//작곡과 붙이기
                break;
            case 7: // 호관대
                college = getResources().getStringArray(R.array.hotel);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//관광학부 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//H경영 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//문콘 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//관광학과 붙이기
                listDataChild.put(listDataHeader.get(4), major2);//호경 붙이기
                listDataChild.put(listDataHeader.get(5), major2);//컨경 붙이기
                listDataChild.put(listDataHeader.get(6), major2);//외경 붙이기
                listDataChild.put(listDataHeader.get(7), major2);//조리경영 붙이기
                listDataChild.put(listDataHeader.get(8), major2);//문화관광산업 붙이기
                listDataChild.put(listDataHeader.get(9), major2);//조리산업 붙이기
                break;
            case 8: //자전
                college = getResources().getStringArray(R.array.self);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//자전 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//자전글로벌리더 붙이기
                break;
            case 9: // 문대
                college = getResources().getStringArray(R.array.write);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//국문 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//사학 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//철학 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//영문 붙이기
                listDataChild.put(listDataHeader.get(4), major2);//통번역 붙이기
                break;
            case 10: //경영대
                college = getResources().getStringArray(R.array.ceo);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//경영대 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//회세 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//경영 붙이기
                break;
            case 11: // 이과대
                college = getResources().getStringArray(R.array.science);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//지리 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//정대 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//수학 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//물리 붙이기
                listDataChild.put(listDataHeader.get(4), major2);//화학 붙이기
                listDataChild.put(listDataHeader.get(5), major2);//생물 붙이기
                break;
            case 12: //간호대
                college = getResources().getStringArray(R.array.nurse);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//간호 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//간호(야) 붙이기
                break;
            case 13: //미대
                college = getResources().getStringArray(R.array.art);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//미술학부 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//한국화 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//회화 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//조소 붙이기
                break;
            case 14: // 무용대
                college = getResources().getStringArray(R.array.dance);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//무용학부 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//한국무용 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//현대무용 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//발레 붙이기
                break;
            case 15: // 후마니타스
                college = getResources().getStringArray(R.array.huma);
                for(int i = 0; i < college.length;i++){
                    listDataHeader.add(college[i]);
                }
                listDataChild.put(listDataHeader.get(0), major2);//인가탐 붙이기
                listDataChild.put(listDataHeader.get(1), major2);//우사세 붙이기
                listDataChild.put(listDataHeader.get(2), major2);//생명 붙이기
                listDataChild.put(listDataHeader.get(3), major2);//자연 붙이기
                listDataChild.put(listDataHeader.get(4), major2);//의미 붙이기
                listDataChild.put(listDataHeader.get(5), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(6), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(7), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(8), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(9), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(10), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(11), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(12), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(13), major2);//사회 붙이기
                listDataChild.put(listDataHeader.get(5), major2);//사회 붙이기

            case 16:

            case 17:
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

    public static boolean isValid(Subject sub){ // 리스트에 과목이 없다-true 있다-false
        boolean ret = true;
        int i;
        for(i = 0; i < selectedNeedList.size();i++) {
            if (selectedNeedList.get(i).cNum.substring(0, 8).equals(sub.cNum.substring(0, 8))) {
                ret = false;
                break;
            }
        }
        for(i = 0; i < selectedNeedList.size(); i++) {
            if (selectedNeedList.get(i).cStart > sub.cEnd) {
                return ret ;
            } else {
                if (selectedNeedList.get(i).cEnd > sub.cStart) {
                    ret = false;
                    break;
                }
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

