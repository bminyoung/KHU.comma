package comma.khutimetablehelper;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoadResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadresult);
    }

    public void Summery(View view) {
        String t = "요약내용";
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("수강신청 요약본");
        dialog.setMessage(t);
        dialog.setNeutralButton("종료",null);
        dialog.show();
    }
}
