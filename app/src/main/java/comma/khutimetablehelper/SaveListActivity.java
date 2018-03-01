package comma.khutimetablehelper;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.sl.usermodel.Line;


public class SaveListActivity extends Activity {

    ListView listview;
    ListViewAdapter lvAdapter;
    ArrayList<String> timeTableList = new ArrayList<String>();

    //처음화면 뜰때 다이얼로그 boolean 값 선언
    public static boolean first = true ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savelist);

        if(first) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
            dialog.setTitle("사용법");
            dialog.setMessage("* 저장된 시간표 목록을 띄워주는 화면입니다. \n * 시간표 이름 변경 가능하고, 삭제도 가능합니다.");
            dialog.setPositiveButton("다시 보지 않기", yesButtonClickListener);
            dialog.show();
        }

        ImageButton warningBtn = (ImageButton) findViewById(R.id.sub_btn_warning);

        warningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(SaveListActivity.this);
                dialog.setTitle("사용법");
                dialog.setMessage("* 저장된 시간표 목록을 띄워주는 화면입니다. \n * 시간표 이름 변경 가능하고, 삭제도 가능합니다.");
                dialog.setPositiveButton("확인", null);
                dialog.show();
            }
        }); //주의사항 버튼

        lvAdapter = new ListViewAdapter(timeTableList, this);
        listview = (ListView) findViewById(R.id.savelist_lv_savedTable);
        listview.setAdapter(lvAdapter);

        setData();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SaveListActivity.this, LoadResultActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    // DialogInterface.OnClickListener 인터페이스를 구현
    private DialogInterface.OnClickListener yesButtonClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ChangeFirst() ;
        }
    };

    private void setData() {
        for (int i = 0; i < AppContext.timeTableNameList.size(); i++) {
            timeTableList.add(AppContext.timeTableNameList.get(i));
        }
    }

    //처음뜨는 다이얼로그 확인버튼 클릭시 false로 바꿈
    protected boolean ChangeFirst() {
        first = false ;
        return  first;
    }

}

class ListViewAdapter extends BaseAdapter {

    private ArrayList<String> itemList = new ArrayList<String>();
    private Context context;

    public ListViewAdapter(ArrayList<String> list, Context context) {
        itemList = list;
        this.context = context;
    }

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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.savelist_customlist, parent, false);
        }

        //수정버튼 다이얼로그 애들
        LayoutInflater dInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogLayout = (LinearLayout) dInflater.inflate(R.layout.maderesult_savedialog, null);
        final EditText timeTableTitle = (EditText) dialogLayout.findViewById(R.id.dialog_edt_title);

        //리스트뷰 애들
        TextView tableName = (TextView) convertView.findViewById(R.id.savelist_lv_tv_tableName);
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.savelist_lv_btn_delete);
        ImageButton changeBtn = (ImageButton) convertView.findViewById(R.id.savelist_lv_btn_change);

        tableName.setText((String)getItem(position)); // 저장된 시간표 이름

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("시간표 이름변경").setView(dialogLayout).setNegativeButton("취소", null)
                        .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (timeTableTitle.getText().length() == 0) {
                                    Toast.makeText(context, "변경할 시간표이름을 입력해주세요", Toast.LENGTH_LONG).show();
                                }
                                else if(AppContext.timeTableNameList.contains(timeTableTitle.getText()+"")){
                                    Toast.makeText(context, timeTableTitle.getText() + "은(는) 이미 있습니다.", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    changeName(AppContext.timeTableNameList.get(position), timeTableTitle.getText().toString());
                                    AppContext.timeTableNameList.set(position, timeTableTitle.getText() + ""); //이름 바꾸길
                                    itemList.set(position, timeTableTitle.getText()+"");
                                    notifyDataSetChanged();
                                }
                            }
                        });
                final AlertDialog dialog;
                dialog = dialogBuilder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
                        String s = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SaveList/";
                        File file = new File(  s + AppContext.timeTableNameList.get(position) + ".csv"); // 경로 + 시간표이름
                        if (file.exists()) {
                            if (file.delete()) {
                                Log.d("tag","시간표 삭제 성공");
                            } else {
                                Log.d("tag","시간표 삭제 실패");
                            }
                        } else {
                            Log.d("tag","시간표가 존재하지 않습니다.");
                        }
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

    public void changeName(String before, String after) {
        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SaveList/" + before + ".csv");
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SaveList/" + after + ".csv");
        file1.renameTo(file2);
    }

//    public void addItem(ArrayList<Subject> timeTable) {
//        itemList.add(timeTable);
//    }
}
