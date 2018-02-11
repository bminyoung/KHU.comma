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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.sl.usermodel.Line;


public class SaveListActivity extends Activity{

    ListView listview;
    ListViewAdapter lvAdapter;
    ArrayList<Subject> timeTableList = new ArrayList<Subject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savelist);

        Button btnNext = (Button) findViewById(R.id.savelist_btn_main);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaveListActivity.this, LoadResultActivity.class);
                startActivity(intent);
            }
        });

        lvAdapter = new ListViewAdapter(timeTableList);
        listview = (ListView) findViewById(R.id.savelist_lv_savedTable);
        listview.setAdapter(lvAdapter);

        setData();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void setData(){

        lvAdapter.addItem(new Subject());

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

    private ArrayList<Subject> itemList = new ArrayList<Subject>();

    public ListViewAdapter(ArrayList<Subject> list){ itemList = list; }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.savelist_customlist, parent, false);
        }

        //수정버튼 다이얼로그 애들
        LayoutInflater dInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogLayout = (LinearLayout) dInflater.inflate(R.layout.maderesult_savedialog, null);
        final EditText timeTableTitle = (EditText) dialogLayout.findViewById(R.id.dialog_edt_title);
        //저장,취소 버튼
        final Button dialogBtnSave = (Button) dialogLayout.findViewById(R.id.dialog_btn_save);
        final Button dialogBtnCancel = (Button) dialogLayout.findViewById(R.id.dialog_btn_cancel);

        //리스트뷰 애들
        TextView tableName = (TextView) convertView.findViewById(R.id.savelist_lv_tv_tableName);
        Button deleteBtn = (Button) convertView.findViewById(R.id.savelist_lv_btn_delete);
        Button changeBtn = (Button) convertView.findViewById(R.id.savelist_lv_btn_change);

        tableName.setText(AppContext.timeTableNameList.get(position)); // 저장된 시간표 이름

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("시간표 이름변경").setView(dialogLayout);
                final AlertDialog dialog;
                dialog = dialogBuilder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialogBtnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppContext.timeTableNameList.set(position, timeTableTitle.getText()+""); //이름 바꾸길
                        notifyDataSetChanged();
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
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("시간표 삭제");
                dialog.setMessage("삭제하시겠습니까?");
                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        itemList.remove(position);//삭제해도 되나?
                        AppContext.timeTableList.remove(position); //db에서 삭제
                        AppContext.timeTableNameList.remove(position); //이름도 같이 삭제
                        notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("취소", null);
                dialog.show();
            }
        });


        return convertView;
    }


    public void addItem(Subject sub){
        itemList.add(sub);
    }
}
