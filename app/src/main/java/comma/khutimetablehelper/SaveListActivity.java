package comma.khutimetablehelper;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class SaveListActivity extends Activity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savelist);

        ListView listview;
        ListViewAdapter adapter = new ListViewAdapter();
        Button btnNext = (Button) findViewById(R.id.savelist_btn_main);

        btnNext.setOnClickListener(this);

        listview = (ListView) findViewById(R.id.savelist_lv_savedTable);
        listview.setAdapter(adapter);

        adapter.addItem("목록1");
        adapter.addItem("목록2");
        adapter.addItem("목록3");
    }

    public void onClick(View v){
        Intent intent = new Intent(SaveListActivity.this, LoadResultActivity.class);
        startActivity(intent);
    }
}

class ListVIewItem{
    private String title;

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }
}

class ListViewAdapter extends BaseAdapter{

    private ArrayList<ListVIewItem> listViewItemList = new ArrayList<ListVIewItem>();

    public ListViewAdapter(){}

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.savelist_customlist, parent, false);
        }

        TextView tableName = (TextView) convertView.findViewById(R.id.savelist_lv_tv_tableName);
        Button deleteBtn = (Button) convertView.findViewById(R.id.savelist_lv_btn_delete);

        ListVIewItem listViewItem = listViewItemList.get(position);

        tableName.setText(((ListVIewItem)getItem(position)).getTitle());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("시간표 삭제");
                dialog.setMessage("삭제하시겠습니까?");
                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listViewItemList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("취소", null);
                dialog.show();
            }
        });

        return convertView;
    }

    public void addItem(String title){
        ListVIewItem item = new ListVIewItem();

        item.setTitle(title);
        listViewItemList.add(item);
    }
}
