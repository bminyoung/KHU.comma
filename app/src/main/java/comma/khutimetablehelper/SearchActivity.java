package comma.khutimetablehelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<Subject> list;          // 데이터를 넣은 리스트변수(리스트목록)
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<Subject> arraylist;

    //검색 성공
    static final int SUCCESS = 1;

    static int selectNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if(AppContext.first[4]) {
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
            dialog.setTitle("사용법");
            dialog.setMessage("* 찾고 싶은 강의를 검색하세요.\n* 교수명, 강의명, 학수번호로 검색가능합니다.\n* 다시보고 싶으시면 상단 물음표 버튼을 눌러주세요.");
            dialog.setPositiveButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppContext.first[4] = false ;
                }
            });
            dialog.show();
        }

        //주의사항 버튼
        ImageButton warningBtn = (ImageButton) findViewById(R.id.search_btn_warning);

        warningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(SearchActivity.this);
                dialog.setTitle("사용법");
                dialog.setMessage("* 찾고 싶은 강의를 검색하세요. \n * 교수명, 강의명, 학수번호로 검색가능합니다.");
                dialog.setPositiveButton("확인", null);
                dialog.show();
            }
        });

        editSearch = (EditText) findViewById(R.id.search_edt_input);
        listView = (ListView) findViewById(R.id.search_lstv_list);

        // 리스트를 생성한다.
        list = new ArrayList<Subject>();

        // 검색에 사용할 데이터을 미리 저장한다.
        settingList();

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        arraylist = new ArrayList<Subject>();
        arraylist.addAll(list);

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        //스피너 선언
        Spinner selectSpinner;
        selectSpinner = (Spinner) findViewById(R.id.search_spinner_select);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.select, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSpinner.setAdapter(adapter);

        //스피너 클릭이벤트
        selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectNum = 0;
                        break;
                    case 1:
                        selectNum = 1;
                        break;

                    case 2:
                        selectNum = 2;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //리스트뷰 아이템 클릭하면 need의 리스트뷰에 아이템을 넘김
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View childView, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchActivity.this);
                if (NeedExpandableListAdapter.need_inum < 25) {
                    int i = 0;
                    Subject sub = new Subject();
                    while (i < AppContext.onlySubjectList.size()) {
                        if (list.get(position).cRow == AppContext.onlySubjectList.get(i++).cRow) {
                            sub = list.get(position);
                        }
                    }
                    NeedExpandableListAdapter.need_inum = NeedExpandableListAdapter.need_inum + sub.cCredit;

                    final Subject finalSub = sub;
                    dialogBuilder.setTitle("과목 추가").setMessage(sub.getName() + "을 추가하시겠습니까?")
                            .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent data = new Intent();
                                    data.putExtra("subject", finalSub);
                                    setResult(SUCCESS, data);
                                    finish();
                                }
                            }).setNegativeButton("취소", null).show();

                } else {
                    Toast.makeText(getApplicationContext(), "더이상 입력할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);

            }
        });


    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();


        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else {
            if (selectNum == 0) {
                for (int i = 0; i < arraylist.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (arraylist.get(i).cName.contains(charText.toString())) {
                        // 검색된 데이터를 리스트에 추가한다.
                        list.add(arraylist.get(i));
                    }
                }
            } else if (selectNum == 1) {
                // 리스트의 모든 데이터를 검색한다.
                for (int i = 0; i < arraylist.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (arraylist.get(i).cNum.contains(charText.toString().toUpperCase())) {
                        // 검색된 데이터를 리스트에 추가한다.
                        list.add(arraylist.get(i));
                    }
                }
            } else if (selectNum == 2) {

                for (int i = 0; i < arraylist.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (arraylist.get(i).cProf.contains(charText.toString())) {
                        list.add(arraylist.get(i));
                    }
                    // 검색된 데이터를 리스트에 추가한다.

                }

            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList() {

        for (int i = 0; i < AppContext.onlySubjectList.size(); i++)
            list.add(AppContext.onlySubjectList.get(i));

    }
}

































