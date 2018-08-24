package com.capiyoo.dencables;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder> implements Filterable {

    public static HomeCustomerClickListner customerClickListner;
    ArrayList<DenDetails> denDetails;
    ArrayList<String> customerKey;
    ArrayList<DenDetails> customerListFiltered;
    ArrayList<String> filteredCustomerKey;
    private Context mCtx;

    public HomeAdapter(Context mCtx, ArrayList<DenDetails> denDetails, ArrayList<String> customerKey, HomeCustomerClickListner customerClickListner) {
        this.mCtx = mCtx;
        this.denDetails = denDetails;
        this.customerKey = customerKey;
        this.customerListFiltered = denDetails;
        HomeAdapter.customerClickListner = customerClickListner;
        this.filteredCustomerKey = customerKey;
    }

    @Override
    public HomeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeAdapterViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.list_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeAdapterViewHolder holder, int position) {
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
                    //customerListFiltered.clear();
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


    interface HomeCustomerClickListner {
        void onCustomerClick(DenDetails position);
    }

    public class HomeAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView customerName;
        TextView boxNumber;
        TextView boxStatus;
        TextView customerCode;

        public HomeAdapterViewHolder(View itemView) {
            super(itemView);
            customerCode = itemView.findViewById(R.id.customerCode);
            customerName = itemView.findViewById(R.id.customerName);
            boxStatus = itemView.findViewById(R.id.boxStatus);
            boxNumber = itemView.findViewById(R.id.boxNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    customerClickListner.onCustomerClick(customerListFiltered.get(getAdapterPosition()));
                }
            });

        }


    }


}
