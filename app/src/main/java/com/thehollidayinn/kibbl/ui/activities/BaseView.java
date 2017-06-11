package com.thehollidayinn.kibbl.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

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
            favoritePet(petId);
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
        descriptionTextView.setText(description);

        findViewById(R.id.detail_content2).setVisibility(View.INVISIBLE);
        findViewById(R.id.detail_content2).setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        if (imageSrc.isEmpty()) {
            return;
        }

        Picasso.with(this)
                .load(imageSrc)
                .transform(new ColorFilterTransformation(Color.argb(120, 0, 0, 0)))
                .into(image);
    }

    protected void favoritePet(String petId) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(this);

        Map<String, String> options = new HashMap<>();
        options.put("type", type);
        options.put("itemId", petId);

        Log.v("keithtest", type);

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
