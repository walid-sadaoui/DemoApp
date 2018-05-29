package com.walidsadaoui.demoapp;

import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<CellInfo> mCellInfoList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView arfcn;
        private TextView bsic;
        private TextView rxLev;
        private TextView c1;
        private TextView c2;

        private CellInfo currentCell;



        public ViewHolder(View itemView) {
            super(itemView);

            arfcn = (TextView) itemView.findViewById(R.id.headerArfcn);
            bsic = (TextView) itemView.findViewById(R.id.headerBsic);
            rxLev = (TextView) itemView.findViewById(R.id.headerRxLev);
            c1 = (TextView) itemView.findViewById(R.id.headerC1);
            c2 = (TextView) itemView.findViewById(R.id.headerC2);
        }

        public void updateUI(CellInfo cellInfo) {
            currentCell = cellInfo;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<CellInfo> cellInfoList) {
        mCellInfoList = cellInfoList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_cell, parent, false);
        return new ViewHolder(view);

        // create a new view
       /* TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_2g, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;*/
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CellInfo currentCell = mCellInfoList.get(position);
        holder.updateUI(currentCell);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCellInfoList.size();
    }
}
