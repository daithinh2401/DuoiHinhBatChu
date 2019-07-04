package com.example.daithinh.doan.Player;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.daithinh.doan.R;
import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * Created by Dai Thinh on 2/28/2017.
 */
public class ListAdapter extends ArrayAdapter<PlayerInfo> {


    public ListAdapter(Context context, int resource, List<PlayerInfo> list) {
        super(context, resource, list);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        final PlayerInfo pi = getItem(position);
        if(pi != null){
            TextView tv1 = (TextView) v.findViewById(R.id.txt_TenNguoiChoi);
            tv1.setText(pi.getTenNguoiDung());

            TextView tv2 = (TextView) v.findViewById(R.id.txt_SoCauDaQua);
            tv2.setText("Số câu đạt được : " + pi.getSoCauDaVuotQua() + "");

            TextView tv3 = (TextView) v.findViewById(R.id.txt_SoRuby);
            tv3.setText("Số ruby : " + pi.getSoRubyHienTai() + "");

            ProfilePictureView imv =  (ProfilePictureView) v.findViewById(R.id.imageViewItem);
            imv.setProfileId(pi.getId());




        }



        return v;
    }

}
