package com.capiyoo.dencables.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.PlansPricing;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PricingAdapter extends RecyclerView.Adapter<PricingAdapter.PricingViewHolder> {
    private Context mCtx;
    private ArrayList<PlansPricing> plansPricings;
    private PriceClickListener priceClickListner;

    public PricingAdapter(Context mCtx, PriceClickListener PriceClickListner, ArrayList<PlansPricing> pricingArrayList) {
        this.mCtx = mCtx;
        this.plansPricings = pricingArrayList;
        this.priceClickListner = PriceClickListner;
    }

    @Override
    public PricingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PricingViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.pricing_den, parent, false));
    }

    @Override
    public void onBindViewHolder(PricingViewHolder holder, int position) {

        switch (position) {
            case 0:
                Picasso.with(mCtx).load(R.drawable.smoke4).into(holder.imagebg);
                break;
            case 1:
                Picasso.with(mCtx).load(R.drawable.smoke3).into(holder.imagebg);
                break;
            case 2:
                Picasso.with(mCtx).load(R.drawable.smoke2).into(holder.imagebg);
                break;
            case 3:
                Picasso.with(mCtx).load(R.drawable.smoke1).into(holder.imagebg);
                break;
        }

        holder.planPrice.setText("₹" + plansPricings.get(position).getmPlanBasePrice());
        holder.usage.setText(plansPricings.get(position).getmUsageDetail());
        holder.planName.setText(plansPricings.get(position).getmPlanName());
        holder.planDesc.setText(plansPricings.get(position).getmPlanDescription());
        holder.advantage.setText(plansPricings.get(position).getmAdvantage());
        holder.charegTitle.setText(plansPricings.get(position).getmPlanServices());


    }

    @Override
    public int getItemCount() {
        return plansPricings.size();
    }

    public interface PriceClickListener {
        void onPriceChoosen(int position);
    }

    public class PricingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button makePayment;
        TextView planName;
        TextView planPrice;
        TextView usage;
        TextView advantage;
        TextView planDesc;
        TextView charegTitle;
        ImageView imagebg;

        public PricingViewHolder(View itemView) {
            super(itemView);
            makePayment = itemView.findViewById(R.id.makePayment);
            planDesc = itemView.findViewById(R.id.plandescription);
            planName = itemView.findViewById(R.id.planName);
            usage = itemView.findViewById(R.id.usage);
            planPrice = itemView.findViewById(R.id.monthlycharge);
            advantage = itemView.findViewById(R.id.advantages);
            imagebg = itemView.findViewById(R.id.imagebg);
            charegTitle = itemView.findViewById(R.id.charge_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            priceClickListner.onPriceChoosen(getAdapterPosition());
        }
    }

}
