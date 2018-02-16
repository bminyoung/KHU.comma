package comma.khutimetablehelper;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class LoadResultActivity extends AppCompatActivity {

    int[] colors = {Color.parseColor("#B8F3B8"),Color.parseColor("#FFA9B0"),Color.parseColor("#CCD1FF"),Color.parseColor("#FFDDA6"),Color.parseColor("#FFADC5")};
    int colorIndex = 0;
    enum Day{ 월, 화, 수, 목, 금 } //나중에 쓸수도

    TextView timeTable[][] = new TextView[140][5]; //시간표 각 칸
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadresult);

        TextView tableName = (TextView) findViewById(R.id.loadresult_tv_title);
        position = getIntent().getIntExtra("position",0);
        tableName.setText(AppContext.timeTableNameList.get(position)); //시간표 이름표시

        setTextId();

        showTimeTable();
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
        ArrayList<Subject> selectedTimeTable = AppContext.timeTableList.get(position);

        colorIndex = 0;
        for(int i = 0; i <selectedTimeTable.size();i++) {
            ShowSubject(selectedTimeTable.get(i));
        }

    }

    public void ShowSubject(Subject selected){
        TextView tv;
        int start = (int) ((selected.cStart - 9.0)*2.0);
        int end = (int)((selected.cEnd - 9.0)*2.0 - 1.0);

        for(int i = end; i >= start;i--){
            tv = timeTable[i][selected.cDay];
            tv.setBackgroundColor(colors[colorIndex]);
        }
        timeTable[start][selected.cDay].setText(selected.cName);
        colorIndex++;
    }

    public void Summery(View view) {
        String msg = "";
        ArrayList<Subject> timeTable = AppContext.timeTableList.get(position);


        for(int i = 0; i < timeTable.size();i++){
            Subject sub = timeTable.get(i);
            msg += sub.getName() + " / " + sub.cProf + "교수 / " + sub.cCredit + "학점 / " + sub.day() + "요일 / " + sub.getTime() + "\n";
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("시간표 요약");
        dialog.setMessage(msg);
        dialog.setNeutralButton("확인",null);
        dialog.show();
    }


}
