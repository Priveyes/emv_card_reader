package com.example.sebastianczuma.czytnikkart.LayoutClassesEmvInfo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sebastianczuma.czytnikkart.MainActivity;
import com.example.sebastianczuma.czytnikkart.R;

/**
 * Created by sebastianczuma on 26.11.2016.
 */
class RecyclerViewHoldersHospital extends RecyclerView.ViewHolder {
    TextView ask;
    TextView answer;
    TextView decoded;
    MainActivity context;

    RecyclerViewHoldersHospital(View itemView) {
        super(itemView);

        ask = (TextView) itemView.findViewById(R.id.ask);
        answer = (TextView) itemView.findViewById(R.id.answer);
        decoded = (TextView) itemView.findViewById(R.id.decoded);
    }
}