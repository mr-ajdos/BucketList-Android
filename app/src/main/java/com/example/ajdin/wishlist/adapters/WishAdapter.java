package com.example.ajdin.wishlist.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajdin.wishlist.R;
import com.example.ajdin.wishlist.viewModels.WishVM;

import java.util.List;

public class WishAdapter extends ArrayAdapter<WishVM> {

    private  int resource;

    public WishAdapter(Context context, int resource, List<WishVM> objects)
    {
        super(context,resource,objects);

        this.resource=resource;
    }

    static class ViewHolder {
        TextView name;
        ProgressBar progressBar;
        TextView state;

        ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.lbl_bucket_name);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar_bucket_progress);
            state = (TextView) view.findViewById(R.id.lbl_bucket_status);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        WishVM wish = getItem(position);

        if (wish != null) {
            viewHolder.name.setText(wish.Name);
            viewHolder.progressBar.setMin(0);
            viewHolder.progressBar.setMax(wish.Amount.intValue());
            viewHolder.progressBar.setProgress(wish.AmountSaved.intValue(),true);
            Double remaining = wish.Amount - wish.AmountSaved;
            viewHolder.state.setText(String.format("%.2f", remaining)+" KM remaining");
        }

        return convertView;
    }
}
