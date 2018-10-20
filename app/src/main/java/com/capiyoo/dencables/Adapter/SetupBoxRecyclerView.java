package com.capiyoo.dencables.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.DenDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetupBoxRecyclerView extends RecyclerView.Adapter<SetupBoxRecyclerView.SetupBoxViewHolder> implements Filterable {

    private Context mCtx;
    ArrayList<DenDetails> denDetails;
    ArrayList<String> customerKey;
    ArrayList<String> filteredCustomerKey;
    ArrayList<DenDetails> customerListFiltered;
    public static CustomerClickListner customerClickListner;

    public SetupBoxRecyclerView(Context mCtx, ArrayList<DenDetails> denDetails, ArrayList<String> customerKey,CustomerClickListner customerClickListner) {
        this.mCtx = mCtx;
        this.denDetails = denDetails;
        this.customerKey = customerKey;
        this.customerListFiltered = denDetails;
        this.filteredCustomerKey = customerKey;
        SetupBoxRecyclerView.customerClickListner = customerClickListner;
    }

    @Override
    public SetupBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SetupBoxViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.list_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(SetupBoxViewHolder holder, int position) {
        holder.boxNumber.setText(customerListFiltered.get(position).getmVCNo());
        holder.boxStatus.setText(customerListFiltered.get(position).getmBoxStatus());
        holder.customerName.setText(customerListFiltered.get(position).getmCustomerName());
        holder.customerCode.setText(customerListFiltered.get(position).getmCCode());
    }

    @Override
    public int getItemCount() {
        return customerListFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    customerListFiltered = denDetails;
                } else {
                    ArrayList<DenDetails> filteredList = new ArrayList<>();
                    ArrayList<String> customerKeyFilter = new ArrayList<>();
                    for (DenDetails row : denDetails) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getmCustomerName().toLowerCase().contains(charString.toLowerCase()) || row.getmCCode().contains(charSequence)
                                || row.getmVCNo().contains(charSequence)) {
                            filteredList.add(row);
                            customerKeyFilter.add(row.getmCustomerKey());
                        }
                    }

                    customerListFiltered = filteredList;
                    filteredCustomerKey = customerKeyFilter;
                }

                FilterResults filterResults = new FilterResults();
                Map<String, ArrayList> map = new HashMap();
                map.put("FilteredList", customerListFiltered);
                map.put("FilteredKey", filteredCustomerKey);

                filterResults.values = map;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Map<String, ArrayList> map = (Map) filterResults.values;
                map.get("FilteredKey");

                filteredCustomerKey = (ArrayList<String>) map.get("FilteredKey");
                customerListFiltered = (ArrayList<DenDetails>) map.get("FilteredList");
                // Toast.makeText(mCtx,Integer.toString(filteredCustomerKey.size()),Toast.LENGTH_LONG).show();
                //Toast.makeText(mCtx,Integer.toString(customerListFiltered.size()),Toast.LENGTH_LONG).show();
                //Log.d("Key",filteredCustomerKey.get(0));

                notifyDataSetChanged();
            }
        };
    }


    public interface CustomerClickListner {
        void onCustomerClick(DenDetails denDetails);
    }

    public class SetupBoxViewHolder extends RecyclerView.ViewHolder {
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customerClickListner.onCustomerClick(customerListFiltered.get(getAdapterPosition()));
                }
            });



        }


    }


}
