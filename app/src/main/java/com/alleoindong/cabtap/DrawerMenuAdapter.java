package com.alleoindong.cabtap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.models.DrawerMenu;

import java.util.ArrayList;

/**
 * Created by alleoindong on 11/21/16.
 */

public class DrawerMenuAdapter extends ArrayAdapter<DrawerMenu> {
    public DrawerMenuAdapter(Context context, ArrayList<DrawerMenu> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerMenu menu = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
        }

        TextView tvMenu = (TextView) convertView.findViewById(R.id.tvMenu);
        tvMenu.setText(menu.title);
        tvMenu.setCompoundDrawablesWithIntrinsicBounds(menu.icon, 0, 0, 0);

        return convertView;
    }
}
