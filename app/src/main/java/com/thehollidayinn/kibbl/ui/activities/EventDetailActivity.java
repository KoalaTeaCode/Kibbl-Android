package com.thehollidayinn.kibbl.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EventDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView image;
    private TextView descriptionTextView;
    private TextView titleTextView;
    private TextView titleTextView2;
    private String petId;
    private Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");
        image = (ImageView) findViewById(R.id.image);
        descriptionTextView = (TextView) findViewById(R.id.description);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView2 = (TextView) findViewById(R.id.titleTextView2);

        Bundle extras = getIntent().getExtras();
        petId = extras.getString("PET_ID");
        loadPet(petId);
//

//        favoriteButton = (Button) findViewById(R.id.favoriteButton);
//        favoriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                favoritePet(petId);
//            }
//        });
//
//        Button shareButton = (Button) findViewById(R.id.shareButton);
//        shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                String shareBody = "Here is the share content body";
//                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void favoritePet(String petId) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(this);
        mService.favoritePet(petId)
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
                        togglePetText();
                    }
                });
    }

    private void togglePetText() {
        if (favoriteButton.getText().equals("Favorite")) {
            favoriteButton.setText("Unfavorite");
        } else {
            favoriteButton.setText("Favorite");
        }
    }

    private void loadPet(String petId) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(this);
        mService.getEventDetail(petId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<Event>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<Event> petResponse) {
                        Event pet = (Event) petResponse.data;
                        displayPetInfo(pet);
                    }
                });
    }

    private void displayPetInfo(Event pet) {
        titleTextView.setText(pet.getName());
        titleTextView2.setText(pet.getName());
        descriptionTextView.setText(pet.getDescription());

        if (pet.getFacebook() != null && pet.getFacebook().getCover() != null) {
            findViewById(R.id.details_top).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.detail_content2).setVisibility(View.INVISIBLE);
            findViewById(R.id.detail_content2).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        Picasso.with(this)
                .load(pet.getFacebook().getCover())
//                .resize(300, 300)
//                .centerCrop()
                .into(image);

//        if (pet.favorited) {
//            togglePetText();
//        }
    }
}
