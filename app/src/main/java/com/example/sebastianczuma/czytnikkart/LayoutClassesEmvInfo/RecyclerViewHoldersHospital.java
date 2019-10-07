package com.example.sebastianczuma.czytnikkart.LayoutClassesEmvInfo;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sebastianczuma.czytnikkart.MainActivity;
import com.example.sebastianczuma.czytnikkart.R;

class RecyclerViewHoldersHospital extends RecyclerView.ViewHolder {
    TextView ask;
    TextView answear;
    TextView decoded;
    MainActivity context;

    RecyclerViewHoldersHospital(View itemView) {
        super(itemView);

        ask = (TextView) itemView.findViewById(R.id.ask);
        answear = (TextView) itemView.findViewById(R.id.answear);
        decoded = (TextView) itemView.findViewById(R.id.decoded);
    }
}