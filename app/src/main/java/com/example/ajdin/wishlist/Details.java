package com.example.ajdin.wishlist;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajdin.wishlist.helpers.ErrorResponse;
import com.example.ajdin.wishlist.helpers.GeneralHelper;
import com.example.ajdin.wishlist.helpers.RetrofitBuilder;
import com.example.ajdin.wishlist.models.Access;
import com.example.ajdin.wishlist.models.Wish;
import com.example.ajdin.wishlist.repositories.WishesRepository;
import com.example.ajdin.wishlist.viewModels.PaymentVM;
import com.example.ajdin.wishlist.viewModels.WishDetailsVM;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Details extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Bundle bundleDetails = new Bundle();

    private int wishId;
    private Access access;

    private WishDetailsVM wishDetails;
    private WishesRepository wishesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");
        Intent in = getIntent();

        access = GeneralHelper.readAccessSharedPreferences(Details.this);
        wishId = in.getIntExtra("wish_id", 0);

        if(access == null || access.AuthToken.isEmpty() || wishId <= 0)
        {
            GeneralHelper.logoutUser(Details.this);
            finish();
        }

        bundleDetails.putInt("wish_id",wishId);
        bundleDetails.putString("auth_token",access.AuthToken);

        wishesRepository = RetrofitBuilder.Build(Details.this).create(WishesRepository.class);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar.
        getMenuInflater().inflate(R.menu.popup_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.edit:

                Call<WishDetailsVM> editResponse = wishesRepository.GetDetails(access.AuthToken, wishId);
                editResponse.enqueue(new Callback<WishDetailsVM>() {
                    @Override
                    public void onResponse(Call<WishDetailsVM> call, Response<WishDetailsVM> response) {
                        if(response.isSuccessful())
                        {
                            wishDetails = response.body();

                            Wish wishToEdit = new Wish(wishDetails.Id,wishDetails.Name,wishDetails.Amount,wishDetails.UserId);

                            startActivity(new Intent(Details.this,AddEdit.class).putExtra("edit_wish", wishToEdit));
                            finish();
                        }
                        else
                        {
                            if(response.code() == 401)
                            {
                                GeneralHelper.logoutUser(Details.this);
                            }
                            else
                            {
                                try {
                                    ErrorResponse.ToastErrorMessage(Details.this, response.errorBody().string()).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WishDetailsVM> call, Throwable t) {
                        Toast.makeText(Details.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case R.id.delete:
            {
                ShowDialogDelete();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
               case 0:
                    TabWishDetails tgd = new TabWishDetails();
                    tgd.setArguments(bundleDetails);
                    return  tgd;
               case 1:
                   TabWishPayments tgp = new TabWishPayments();
                   tgp.setArguments(bundleDetails);
                   return  tgp;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position)
            {
                case 0: return "DETAILS";
                case 1: return "PAYMENTS";
                default: return null;
            }
        }
    }

    private void ShowDialogDelete() {

        final Dialog deleteWishConfirmation = new Dialog(Details.this);

        deleteWishConfirmation.setContentView(R.layout.delete_dialog);

        TextView subheading = (TextView) deleteWishConfirmation.findViewById(R.id.subheading);
        subheading.setText("Are you sure you want to delete this wish?");

        deleteWishConfirmation.show();

        TextView okBtn = (TextView) deleteWishConfirmation.findViewById(R.id.btn_payments_add_dialog_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Call<ResponseBody> deleteResponse = wishesRepository.Delete(access.AuthToken, wishId);
                deleteResponse.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            Toast.makeText(Details.this, "Wish deleted successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            if(response.code() == 401)
                            {
                                GeneralHelper.logoutUser(Details.this);
                            }
                            else
                            {
                                try {
                                    ErrorResponse.ToastErrorMessage(Details.this, response.errorBody().string()).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Details.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

                deleteWishConfirmation.dismiss();
            }
        });

        TextView cancelBtn = (TextView) deleteWishConfirmation.findViewById(R.id.btn_payments_add_dialog_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteWishConfirmation.dismiss();
            }
        });
    }
}
