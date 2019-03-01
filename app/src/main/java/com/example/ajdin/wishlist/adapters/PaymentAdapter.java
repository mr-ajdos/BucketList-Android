package com.example.ajdin.wishlist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ajdin.wishlist.R;
import com.example.ajdin.wishlist.viewModels.PaymentVM;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentAdapter extends ArrayAdapter<PaymentVM> {

    private  int resource;

    public PaymentAdapter(Context context, int resource, List<PaymentVM> objects)
    {
        super(context,resource,objects);

        this.resource=resource;
    }

    static class ViewHolder {
        TextView date;
        TextView amount;
        TextView id;

        ViewHolder(View view){
            date = (TextView) view.findViewById(R.id.lbl_payment_date);
            amount = (TextView) view.findViewById(R.id.lbl_payment_amount);
            id = (TextView) view.findViewById(R.id.lbl_payment_id);
        }
    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PaymentVM payment= getItem(position);

        if (payment != null) {
            viewHolder.date.setText(payment.CreatedDate);
            viewHolder.amount.setText("+"+ String.format("%.2f", payment.Amount)+" KM");
            viewHolder.id.setText(String.valueOf(payment.Id));
        }

        return convertView;
    }
}
