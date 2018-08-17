package com.capiyoo.dencables;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SetupBoxRecyclerView extends RecyclerView.Adapter<SetupBoxRecyclerView.SetupBoxViewHolder> {

    private Context mCtx;
    ArrayList<DenDetails> denDetails;
    ArrayList<String> customerKey;
    public static CustomerClickListner customerClickListner;

    public SetupBoxRecyclerView(Context mCtx, ArrayList<DenDetails> denDetails, ArrayList<String> customerKey,CustomerClickListner customerClickListner) {
        this.mCtx = mCtx;
        this.denDetails = denDetails;
        this.customerKey = customerKey;
        this.customerClickListner =   customerClickListner;
    }

    @Override
    public SetupBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SetupBoxViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.list_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(SetupBoxViewHolder holder, int position) {
        holder.boxNumber.setText(denDetails.get(position).getmVCNo());
        holder.boxStatus.setText(denDetails.get(position).getmBoxStatus());
        holder.customerName.setText(denDetails.get(position).getmCustomerName());
        holder.customerCode.setText(denDetails.get(position).getmCCode());
    }

    @Override
    public int getItemCount() {
        return denDetails.size();
    }

    interface CustomerClickListner {
        void onCustomerClick(int position);
    }

    public class SetupBoxViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView customerName;
        TextView boxNumber;
        TextView boxStatus;
        TextView customerCode;

        public SetupBoxViewHolder(View itemView) {
            super(itemView);
            customerCode = itemView.findViewById(R.id.customerCode);
            customerName = itemView.findViewById(R.id.customerName);
            boxStatus = itemView.findViewById(R.id.boxStatus);
            boxNumber = itemView.findViewById(R.id.boxNumber);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            customerClickListner.onCustomerClick(position);
        }
    }

    public void updateList(ArrayList<DenDetails> denDetail) {
        denDetails = new ArrayList<>();
        denDetails.addAll(denDetail);
        notifyDataSetChanged();

    }
}
