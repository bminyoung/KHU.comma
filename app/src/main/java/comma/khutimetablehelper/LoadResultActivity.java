package comma.khutimetablehelper;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadResultActivity extends AppCompatActivity {

    static int[] colors = {Color.parseColor("#FFCDD2"), Color.parseColor("#BBDEFB"), Color.parseColor("#F0F4C3"), Color.parseColor("#FFCC80"),
            Color.parseColor("#E1BEE7"), Color.parseColor("#80DEEA"), Color.parseColor("#D7CCC8"), Color.parseColor("#D1C4E9"),
            Color.parseColor("#F8BBD0"),Color.parseColor("#DCEDC8"),Color.parseColor("#FFF59D"),Color.parseColor("#81D4FA"),
            Color.parseColor("#CFD8DC"),Color.parseColor("#B2DFDB"),Color.parseColor("#FFCCBC"),Color.parseColor("#C5CAE9"),
            Color.parseColor("#C8E6C9"),Color.parseColor("#FFECB3"),Color.parseColor("#E0E0E0"),Color.parseColor("#EF9A9A"), Color.parseColor("#90CAF9")};
    int colorIndex = 0;
    enum Day{ 월, 화, 수, 목, 금 } //나중에 쓸수도
    int [] tableContent;
    ArrayList<Subject> subList = new ArrayList<Subject>();

    TextView timeTable[][] = new TextView[140][5]; //시간표 각 칸
    int position ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadresult);

        if(AppContext.first[5]) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
            dialog.setTitle("사용법");
            dialog.setMessage("* 저장된 시간표가 나오는 화면입니다. \n * 담겨진 강의 정보를 확인하고 싶다면 상단 오른쪽 버튼을 눌러주세요.\n* 다시보고 싶으시면 상단 물음표 버튼을 눌러주세요.");
            dialog.setPositiveButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppContext.first[5] = false ;
                }
            });
            dialog.show();
        }

        ImageButton warningBtn = (ImageButton) findViewById(R.id.loadresult_btn_warning);

        warningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(LoadResultActivity.this);
                dialog.setTitle("사용법");
                dialog.setMessage("* 저장된 시간표가 나오는 화면입니다. \n * 담겨진 강의 정보를 확인하고 싶다면 상단 오른쪽 버튼을 눌러주세요.");
                dialog.setPositiveButton("확인", null);
                dialog.show();
            }
        }); //주의사항 버튼

        TextView tableName = (TextView) findViewById(R.id.loadresult_tv_title);
        position = getIntent().getIntExtra("position",0);
        tableName.setText(AppContext.timeTableNameList.get(position)); //시간표 이름표시

        tableContent = loadFile(AppContext.timeTableNameList.get(position));
        for(int i = 0; i < tableContent.length;i++){
            for(int j = 0; j < AppContext.subjectList.length;j++) {
                if (AppContext.subjectList[j].cRow == tableContent[i]){
                    subList.add(AppContext.subjectList[j]);
                    break;
                }
            }
        }

        setTextId();
        showTimeTable();
    }

    public int[] loadFile(String fileName) {
        BufferedReader br = null;
        String cvsSplitBy = ",";
        String line = "";
        String[] field = new String[42];

        int arSize = 0;
        try {
            File csv = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SaveList/" + fileName + ".csv");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
            // Charset.forName("UTF-8");

            while ((line = br.readLine()) != null) {
                field = line.split(cvsSplitBy, -1);
                arSize = field.length;
                break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int[] file = new int[arSize];
        for (int i = 0; i < arSize; i++) {
            double temp = Double.parseDouble(field[i]);
            file[i] = (int) temp;
        }
        return file;
    }

    private void setTextId(){
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 5; j++) {
                int resID = getResources().getIdentifier("loadResult_Tv_" + i + "" + j, "id", "comma.khutimetablehelper");
                timeTable[i][j] = ((TextView) findViewById(resID));
            }
        }
    }

    private void showTimeTable(){
        ArrayList<Subject> selectedTimeTable = subList;
        colorIndex = 0;
        //같은 과목은 같은 색깔로
        for (int i = 0; i < selectedTimeTable.size(); i++) {
            for (int j = i; j < selectedTimeTable.size(); j++) {
                if (selectedTimeTable.get(i).cNum.equals(selectedTimeTable.get(j).cNum)) {
                    ShowSubject(selectedTimeTable.get(j));
                    if (j == selectedTimeTable.size() - 1) {
                        i = selectedTimeTable.size();
                    }
                } else {
                    i = j - 1;
                    break;
                }

            }
            colorIndex++;
        }
    }

    public void ShowSubject(Subject selected){
        if(selected.cDay == -1)
            return;
        TextView tv;
        int start = (int) ((selected.cStart - 9.0)*2.0);
        int end = (int)((selected.cEnd - 9.0)*2.0 - 1.0);

        for(int i = end; i >= start;i--){
            tv = timeTable[i][selected.cDay];
            tv.setBackgroundColor(MadeResultActivity.colors[colorIndex]);
        }
        timeTable[start][selected.cDay].setText(selected.cName);
    }

    public void Summary(View view) {
        String msg = "";

        for(int i = 0; i < subList.size();i++){
            Subject sub = subList.get(i);
            msg += sub.cNum + "/" + sub.getName() + " / " + sub.cProf + "교수 / " + sub.cCredit + "학점 / " + sub.day() + "요일 / " + sub.getTime() + "\n";
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("시간표 요약");
        dialog.setMessage(msg);
        dialog.setNeutralButton("확인",null);
        dialog.show();
    }
}
