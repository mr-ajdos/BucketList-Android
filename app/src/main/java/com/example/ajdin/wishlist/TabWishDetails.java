package com.example.ajdin.wishlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.repositories.WishesRepository;
import com.example.ajdin.wishlist.viewModels.WishDetailsVM;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabWishDetails extends Fragment{

    private WishDetailsVM wishInfo;
    private int wishId;
    private String authToken;
    private WishesRepository wishesRepository;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_wish_details, container, false);



        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
        {
            wishId= getArguments().getInt("wish_id",0);
            authToken = getArguments().getString("auth_token","");


            if(wishId <= 0 || authToken.isEmpty())
            {
                GeneralHelper.logoutUser(getContext());
            }

            wishesRepository = RetrofitBuilder.Build(getContext()).create(WishesRepository.class);

            Call<WishDetailsVM> detailsResponse = wishesRepository.GetDetails(authToken, wishId);

            detailsResponse.enqueue(new Callback<WishDetailsVM>() {
                @Override
                public void onResponse(Call<WishDetailsVM> call, Response<WishDetailsVM> response) {
                    if(response.isSuccessful())
                    {
                        wishInfo = response.body();

                        ProgressBar details_progress_bar = (ProgressBar) rootView.findViewById(R.id.progressBar_details);
                        TextView saved_lbl = (TextView) rootView.findViewById(R.id.lbl_progress_saved);
                        TextView remaining_lbl = (TextView) rootView.findViewById(R.id.lbl_progress_remaining);
                        TextView total_lbl = (TextView) rootView.findViewById(R.id.lbl_progress_total);
                        TextView percentage_state = (TextView) rootView.findViewById(R.id.lbl_progress_state);


                        details_progress_bar.setMin(0);
                        details_progress_bar.setMax(wishInfo.Amount.intValue());
                        details_progress_bar.setProgress(wishInfo.AmountSaved.intValue());
                        details_progress_bar.animate();

                        saved_lbl.setText(String.format("%.2f", wishInfo.AmountSaved)+" KM");
                        total_lbl.setText(String.format("%.2f", wishInfo.Amount)+" KM");
                        remaining_lbl.setText(String.format("%.2f", wishInfo.Difference)+" KM");
                        percentage_state.setText(String.format("%.2f", wishInfo.Percentage)+"%");
                    }
                    else
                    {
                        if(response.code() == 401)
                        {
                            GeneralHelper.logoutUser(getContext());
                        }
                        else
                        {
                            try {
                                ErrorResponse.ToastErrorMessage(getContext(), response.errorBody().string()).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<WishDetailsVM> call, Throwable t) {
                    Toast.makeText(getContext(), "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

