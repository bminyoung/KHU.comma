package comma.khutimetablehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
/**
 * Created by Administrator on 2017-08-07.
 */

public class SearchAdapter extends BaseAdapter {

    private List<Subject> list;
    private LayoutInflater inflater = null;


    public SearchAdapter(List<Subject> list, Context context){
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.search_lstv_row,null);
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        TextView tv = (TextView) convertView.findViewById(R.id.search_lstv_tv_label);
        tv.setText(list.get(position).getName());

        return convertView;
    }

    public void addItem(Subject sub){
        list.add(sub);
    }

}