package com.example.ajdin.wishlist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.wishlist.adapters.PaymentAdapter;
import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Payment;
import com.example.ajdin.wishlist.repositories.PaymentsRepository;
import com.example.ajdin.wishlist.repositories.WishesRepository;
import com.example.ajdin.wishlist.viewModels.PaymentVM;
import com.example.ajdin.wishlist.viewModels.WishPaymentsVM;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabWishPayments extends Fragment{

    private int wishId;
    private String authToken;
    private WishPaymentsVM wishPayments;
    private ListView paymentsLV;
    private PaymentAdapter adapter;
    private PaymentsRepository paymentsRepository;
    private View detailsView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wish_payments, container, false);
            detailsView = container.getChildAt(0);

            wishId = getArguments().getInt("wish_id",0);
            authToken = getArguments().getString("auth_token","");

            if(wishId <= 0 || authToken.isEmpty())
            {
                GeneralHelper.logoutUser(getContext());
            }


            paymentsLV = (ListView)rootView.findViewById(R.id.list_goal_payments);
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_addNewPayment);

            paymentsRepository = RetrofitBuilder.Build(getContext()).create(PaymentsRepository.class);
            Call<WishPaymentsVM> paymentsResponse = paymentsRepository.GetPayments(authToken, wishId);

            paymentsResponse.enqueue(new Callback<WishPaymentsVM>() {
                @Override
                public void onResponse(Call<WishPaymentsVM> call, Response<WishPaymentsVM> response) {
                    if(response.isSuccessful())
                    {
                        wishPayments = response.body();

                        adapter = new PaymentAdapter(getContext(),R.layout.wish_payments_list_item, wishPayments.Payments);
                        paymentsLV.setAdapter(adapter);

                        paymentsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {

                                PopupMenu menu = new PopupMenu(getContext(),view);

                                menu.getMenuInflater().inflate(R.menu.popup_menu,menu.getMenu());

                                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        PaymentVM payment = (PaymentVM) paymentsLV.getItemAtPosition(i);

                                        if(menuItem.getTitle().equals("Edit"))
                                            ShowDialogAddEditPayment(payment);
                                        else
                                            ShowDialogDelete(payment);

                                        return true;
                                    }
                                });

                                menu.show();

                                return true;
                            }
                        });
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
                public void onFailure(Call<WishPaymentsVM> call, Throwable t) {
                    Toast.makeText(getContext(), "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowDialogAddEditPayment(null);
                }
            });

        return rootView;
    }

    private void ShowDialogAddEditPayment(@Nullable final PaymentVM model) {


        final Dialog addEditPayment = new Dialog(getContext());

        addEditPayment.setContentView(R.layout.payments_addedit_dialog);

        final EditText paymentValue = (EditText) addEditPayment.findViewById(R.id.txt_payment_addedit_value);
        TextView heading = (TextView) addEditPayment.findViewById(R.id.heading);
        final EditText hiddenId = (EditText) addEditPayment.findViewById(R.id.hidden_id);

        if(model != null)
        {
            heading.setText("Edit payment");
            hiddenId.setText(String.valueOf(model.Id));
            paymentValue.setText(model.Amount.toString());
            paymentValue.setSelection(paymentValue.getText().length());
        }
        else
        {
            hiddenId.setText("0");
        }

        addEditPayment.show();

        TextView okBtn = (TextView) addEditPayment.findViewById(R.id.btn_payments_add_dialog_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(Integer.parseInt(hiddenId.getText().toString()) > 0)
                {
                    final Double oldAmount = model.Amount;
                    final int index = wishPayments.Payments.indexOf(model);

                    model.Amount = Double.parseDouble(paymentValue.getText().toString());

                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

                    Date date =  new Date();
                    try {
                        date = formatter.parse(model.CreatedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Payment editPayment = new Payment(model.Id, wishId , date, model.Amount);

                    Call<ResponseBody> editResponse = paymentsRepository.Edit(authToken, editPayment);
                    editResponse.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful())
                            {
                                wishPayments.Payments.get(index).Amount = model.Amount;
                                adapter.notifyDataSetChanged();

                                Double difference = model.Amount - oldAmount;

                                /*UpdateWishDetails(difference);*/
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
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {

                    Payment newPayment = new Payment(0,wishId,new Date(),Double.parseDouble(paymentValue.getText().toString()));

                    Call<PaymentVM> addResponse = paymentsRepository.Add(authToken, newPayment);
                    addResponse.enqueue(new Callback<PaymentVM>() {
                        @Override
                        public void onResponse(Call<PaymentVM> call, Response<PaymentVM> response) {
                            if(response.isSuccessful())
                            {
                                PaymentVM addedPayment = response.body();

                                wishPayments.Payments.add(addedPayment);
                                adapter.notifyDataSetChanged();

                                /*UpdateWishDetails(addedPayment.Amount);*/
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
                        public void onFailure(Call<PaymentVM> call, Throwable t) {
                            Toast.makeText(getContext(), "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                addEditPayment.dismiss();
            }
        });

        TextView cancelBtn = (TextView) addEditPayment.findViewById(R.id.btn_payments_add_dialog_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addEditPayment.dismiss();
            }
        });
    }

    private void ShowDialogDelete(final PaymentVM model) {

        final Dialog deletePaymentConfirmation = new Dialog(getContext());

        deletePaymentConfirmation.setContentView(R.layout.delete_dialog);

        deletePaymentConfirmation.show();

        TextView okBtn = (TextView) deletePaymentConfirmation.findViewById(R.id.btn_payments_add_dialog_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Call<ResponseBody> deleteResponse = paymentsRepository.Delete(authToken, model.Id);
                deleteResponse.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Payment deleted successfully.", Toast.LENGTH_SHORT).show();

                            wishPayments.Payments.remove(model);
                            adapter.notifyDataSetChanged();

                            UpdateWishDetails(model.Amount * -1);
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });


                deletePaymentConfirmation.dismiss();
            }
        });

        TextView cancelBtn = (TextView) deletePaymentConfirmation.findViewById(R.id.btn_payments_add_dialog_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                deletePaymentConfirmation.dismiss();
            }
        });
    }


    private void UpdateWishDetails(Double difference)
    {

        ProgressBar details_progress_bar = (ProgressBar) detailsView.findViewById(R.id.progressBar_details);
        TextView saved_lbl = (TextView) detailsView.findViewById(R.id.lbl_progress_saved);
        TextView remaining_lbl = (TextView) detailsView.findViewById(R.id.lbl_progress_remaining);
        TextView total_lbl = (TextView) detailsView.findViewById(R.id.lbl_progress_total);
        TextView percentage_state = (TextView) detailsView.findViewById(R.id.lbl_progress_state);

        String x = total_lbl.getText().toString().trim().split("\\s+")[0];

        Double total = Double.parseDouble(total_lbl.getText().toString().trim().split("\\s+")[0]);

        Double saved = Double.parseDouble(saved_lbl.getText().toString().trim().split("\\s+")[0]);
        Double remaining = Double.parseDouble(remaining_lbl.getText().toString().trim().split("\\s+")[0]);

        saved += difference;
        remaining += (difference * -1);

        Double percentage = (saved/total) * 100;

        details_progress_bar.setProgress(saved.intValue());
        details_progress_bar.animate();

        saved_lbl.setText(String.format("%.2f", saved)+" KM");
        remaining_lbl.setText(String.format("%.2f", remaining)+" KM");
        percentage_state.setText(String.format("%.2f", percentage) +"%");
    }

}