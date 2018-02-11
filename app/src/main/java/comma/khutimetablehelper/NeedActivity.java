package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NeedActivity extends Activity {

    //확장 리스트뷰
    CustomExpandableListAdapter explistAdapter;
    static List<String> listDataHeader;
    static HashMap<String, List<Subject>> listDataChild;
    Intent intentToSub;
    static ArrayList<Subject> needSubject = new ArrayList<Subject>();
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
        setContentView(R.layout.activity_need);

        Button nextBtn = (Button) findViewById(R.id.need_btn_nextbutton);
        intentToSub = new Intent(NeedActivity.this, SubActivity.class);



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needSubject = madapter.getSubjectList();
                intentToSub.putExtra("NeedSubject", needSubject);
                startActivity(intentToSub);
            }
        });

        Button searchBtn = (Button) findViewById(R.id.need_btn_searchbutton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NeedActivity.this, SearchActivity.class);
                for(int group_num = 0; group_num<listDataHeader.size();group_num++) {
                    for (int child_num = 0; child_num < listDataChild.size(); child_num++) {
                        intent.putExtra(listDataChild.get(listDataHeader.get(group_num)).get(child_num).toString(),(group_num*child_num)+child_num);
                    }
                }
                startActivity(intent);
            }
        });

        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.need_elstv_showsubject);

        // 확장리스트 뷰 어댑터 준비
        prepareListData();
        explistAdapter = new CustomExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(explistAdapter);

        //리스트뷰
        mlistView = (ListView) findViewById(R.id.need_lstv_showSelet);
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

//확장 리스트뷰 어댑터
class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Subject>> _listDataChild;


    public CustomExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Subject>> listChildData) {
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

        final String childText = ((Subject)getChild(groupPosition, childPosition)).getName();

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
                String activityName;

                ActivityManager activitymanager = (ActivityManager)_context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> runningtaskinfo= activitymanager.getRunningTasks(1);
                activityName = runningtaskinfo.get(0).topActivity.getShortClassName();

                if(activityName.equals(".NeedActivity")){
                    needAdd(selectedSubject);
                }
                else if(activityName.equals(".SubActivity")){
                    subAdd(selectedSubject);
                }
                btn.setClickable(false);
            }
        });
        txtListChild.setText(childText);
        return convertView;
    }

    public void needAdd(Subject sub){
        NeedActivity.madapter.additem(sub);
        NeedActivity.madapter.notifyDataSetChanged();
    }

    public void subAdd(Subject sub){
        SubActivity.madapter.additem(sub);
        SubActivity.madapter.notifyDataSetChanged();
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
    LayoutInflater inflater = null;

    CustomListAdapter() {};

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
            final Context context = parent.getContext();
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
                oData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertview;
    }

    public void additem(Subject sub) {
        oData.add(sub);
    }

    public ArrayList<Subject> getSubjectList(){ return oData; }
}

