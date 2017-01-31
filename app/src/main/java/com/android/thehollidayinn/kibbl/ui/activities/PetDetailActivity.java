package com.android.thehollidayinn.kibbl.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.data.models.GenericResponse;
import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.android.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.squareup.picasso.Picasso;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PetDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView image;
    private TextView descriptionTextView;
    private String petId;
    private Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        petId = extras.getString("PET_ID");
        loadPet(petId);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        image = (ImageView) findViewById(R.id.image);
        descriptionTextView = (TextView) findViewById(R.id.description);

        favoriteButton = (Button) findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritePet(petId);
            }
        });
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
        mService.getPetDetail(petId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse petResponse) {
                        Pet pet = (Pet) petResponse.data;
                        displayPetInfo(pet);
                    }
                });
    }

    private void displayPetInfo(Pet pet) {
        collapsingToolbar.setTitle(pet.getName());
        descriptionTextView.setText(pet.getDescription());

        Picasso.with(this)
                .load(pet.getMedia().get(3))
//                .resize(300, 300)
//                .centerCrop()
                .into(image);

        if (pet.favorited) {
            togglePetText();
        }
    }
}
