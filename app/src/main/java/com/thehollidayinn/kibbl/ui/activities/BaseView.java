package com.thehollidayinn.kibbl.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.CommonModel;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by krh12 on 6/2/2017.
 */

public class BaseView extends AppCompatActivity {
    protected TextView titleTextView;
    protected TextView titleTextView2;
    protected TextView descriptionTextView;
    protected ImageView image;
    protected Context context;
    protected CommonModel pet;
    protected String petId;
    protected String type;
    protected TextView secondayTitle;
    protected TextView turnaryTitle;
    protected UserLogin user;

    protected void setUp(Activity context) {
        user = UserLogin.getInstance(this);
        secondayTitle = (TextView) context.findViewById(R.id.secondayTextView);
        turnaryTitle = (TextView) context.findViewById(R.id.turnaryTextView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        } else if (id == R.id.action_favorite) {
            if (!user.isLoggedIn()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You must be logged in to favorite.")
                        .setTitle("Woops!");
                AlertDialog dialog = builder.create();
                builder.setPositiveButton("Got it.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }

            favoritePet(petId);
            if (pet.favorited == null) {
                pet.favorited = false;
            }
            pet.favorited = !pet.favorited;
            if (pet.favorited) {
                item.setIcon(R.drawable.favorited_icon);
                return true;
            }
            item.setIcon(R.drawable.ic_action_favorite_icon);
            return true;
        } else if (id == R.id.action_comments) {
            Intent intent = new Intent(context, CommentActivity.class);
            Bundle b = new Bundle();
            b.putString("itemId", petId);
            intent.putExtras(b);
            context.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void displayPetInfo(String name, String description, String imageSrc) {
        titleTextView.setText(name);
        titleTextView2.setText(name);
        descriptionTextView.setText(Html.fromHtml(description));

        findViewById(R.id.detail_content2).setVisibility(View.INVISIBLE);
        findViewById(R.id.detail_content2).setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        if (type.equals("event")) {
            final Event event = (Event) pet;

            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTimeInMillis(event.getStartTime().getTime());
            String dayString = startDateCalendar.get(Calendar.MONTH) + "/" + startDateCalendar.get(Calendar.DAY_OF_MONTH) + "/" + startDateCalendar.get(Calendar.YEAR);

            String amPm = "AM";
            // @TOOD: Fix
            String startTimeString = startDateCalendar.get(Calendar.HOUR) + ":" + startDateCalendar.get(Calendar.MINUTE) + " " + amPm;

            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTimeInMillis(event.getEndTime().getTime());
            String dayEndString = endDateCalendar.get(Calendar.MONTH) + "/" + endDateCalendar.get(Calendar.DAY_OF_MONTH) + "/" + endDateCalendar.get(Calendar.YEAR);
            String endTimeString = endDateCalendar.get(Calendar.HOUR) + ":" + endDateCalendar.get(Calendar.MINUTE) + " " + endDateCalendar.get(Calendar.AM_PM);

            secondayTitle.setText(dayString + " - " + dayEndString);
            turnaryTitle.setText(startTimeString + " - " + endTimeString);

            findViewById(R.id.contact_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String facebookUrl = "https://www.facebook.com/events/" + event.getFacebookid();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                    startActivity(browserIntent);
                }
            });
        }

        if (type.equals("pet")) {
            Pet petModel = (Pet) pet;

            secondayTitle.setText(petModel.getContact().getEmail());
            turnaryTitle.setText(petModel.getContact().getPhone().toString());
        }

        if (type.equals("shelter")) {
            final Shelter shelter = (Shelter) pet;

            secondayTitle.setText(shelter.getEmail());
            turnaryTitle.setText(shelter.getPhone().toString());

            findViewById(R.id.contact_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String facebookUrl = "https://www.facebook.com/" + shelter.getFacebook().getId();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                    startActivity(browserIntent);
                }
            });
        }

        if (imageSrc.isEmpty()) {
            return;
        }

        Picasso.with(this)
                .load(imageSrc)
                .transform(new ColorFilterTransformation(Color.argb(80, 0, 0, 0)))
                .into(image);
    }

    protected void favoritePet(String petId) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(this);

        Map<String, String> options = new HashMap<>();
        options.put("type", type);
        options.put("itemId", petId);

        mService.favorite(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<Pet>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse petResponse) {
                    }
                });
    }
}
