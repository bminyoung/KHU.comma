package comma.khutimetablehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
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
    HashMap<String, List<String>> listDataChild;

    //다른 그룹 오픈시 열려있는 그룹 닫기 메서드 선언부
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    //리스트뷰
    private ListView mlistView = null;
    protected static Sub_ListAdapter madapter = new Sub_ListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


        //확장 리스트 뷰 가져오기
        expListView = (ExpandableListView) findViewById(R.id.sub_elstv_showSubject);

        // 확장리스트 뷰 어댑터 준비
        prepareListData();
        explistAdapter = new Sub_ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(explistAdapter);

        //리스트뷰

        mlistView = (ListView) findViewById(R.id.sub_lstv_showSelet);

        mlistView.setAdapter(madapter);
        madapter.additem("김준성");

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
        listDataChild = new HashMap<String, List<String>>();

        // 그룹 데이터 입력
        listDataHeader.add("이과대학");
        listDataHeader.add("문과대학");
        listDataHeader.add("경영대학");

        // 그룹 1의 차일드 데이터 입력
        List<String> 이과대학 = new ArrayList<String>();
        이과대학.add("수학과");
        이과대학.add("물리학과");
        이과대학.add("화학과");

        //그룹 2의 차일드 데이터 입력
        List<String> 문과대학 = new ArrayList<String>();
        문과대학.add("국문학과");
        문과대학.add("문헌정보학과");
        문과대학.add("사학과");

        //그룹 3의 차일드 데이터 입력
        List<String> 경영대학 = new ArrayList<String>();
        경영대학.add("경영학과");
        경영대학.add("경제학과");
        경영대학.add("경영경제학과");

        //그룹에 데이터 할당
        listDataChild.put(listDataHeader.get(0), 이과대학); // Header, Child data
        listDataChild.put(listDataHeader.get(1), 문과대학);
        listDataChild.put(listDataHeader.get(2), 경영대학);
    }
}
//확장 리스트뷰 어댑터

class Sub_ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;
    private ExpandableListView expListView;
    int num = 0;


    public Sub_ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sub_elstv_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.sub_elstv_item_header);
        Button btn = (Button) convertView.findViewById(R.id.sub_elstv_item_btn);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            //차일드 버튼 클릭 -> 리스트뷰 데이터 입력
            public void onClick(View view) {
                String str = _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition) + num + "";

                if (num != 5) {
                    Toast.makeText(_context, str, Toast.LENGTH_SHORT).show();
                    num++;
                    SubActivity.madapter.additem(str);
                    SubActivity.madapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(_context, "초과", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
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
            convertView = infalInflater.inflate(R.layout.sub_elstv_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.sub_elstv_group_header);
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
class Sub_ListAdapter extends BaseAdapter {

    private ArrayList<ListData> oData = new ArrayList<>();
    LayoutInflater inflater = null;

    Sub_ListAdapter() {};

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
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
    public View getView(int position, View convertview, ViewGroup parent) {
        if (convertview == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertview = inflater.inflate(R.layout.sub_lstv_item, parent, false);
        }
        TextView need_lstv_tv_choosedsubject = (TextView) convertview.findViewById(R.id.sub_lstv_tv);
        need_lstv_tv_choosedsubject.setText(oData.get(position).mName);
        return convertview;
    }

    public void additem(String mName) {
        ListData oItem = new ListData();
        oItem.mName = mName;
        oData.add(oItem);
    }
}

class ListData {
    public String mName;
}

