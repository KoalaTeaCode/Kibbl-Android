package com.thehollidayinn.kibbl.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Contact;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.PetMedia;
import com.thehollidayinn.kibbl.data.models.PetResponse;
import com.thehollidayinn.kibbl.data.models.Place;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.realm.PetMediaRealm;
import com.thehollidayinn.kibbl.data.realm.PetRealm;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.data.repositories.UserRepository;
import com.thehollidayinn.kibbl.ui.activities.PetDetailActivity;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by krh12 on 1/3/2017.
 */

public class PetListFragment extends Fragment implements NestedScrollView.OnScrollChangeListener {
    private ContentAdapter adapter;
    private static Context context;
    private String filter;
    private Filters filters;
    private ProgressDialog dialog;
    private Boolean dataSetManually = false;
    private List<Pet> pets;
    private String shelterId = "";
    private RelativeLayout empty_view;
    private UserRepository userLogin;
    private Realm realmUI;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public PetListFragment() {
    }

    public static PetListFragment newInstance(String shelterId, List<Pet> pets) {
        PetListFragment f = new PetListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);
        f.filters = Filters.getSharedInstance();

        if (pets != null) {
            f.pets = pets;
            f.dataSetManually = true;
        }

        if (!shelterId.isEmpty()) {
            f.shelterId = shelterId;
        }

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filters = Filters.getSharedInstance();
        userLogin = UserRepository.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        this.context = getContext();

        empty_view = (RelativeLayout) this.getActivity().findViewById(R.id.empty_view);

        adapter = new ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<String>() {
            @Override
            public void call(String petId) {
                Intent detailViewIntent = new Intent(PetListFragment.this.getContext(), PetDetailActivity.class);
                detailViewIntent.putExtra("PET_ID", petId);
                PetListFragment.this.getActivity().startActivity(detailViewIntent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        this.filter = getArguments().getString("FILTER");

        if (!dataSetManually) {
            loadPets("");
        } else {
            adapter.updatePets(this.pets);
        }

        return recyclerView;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmUI != null) {
            realmUI.close();
        }
    }

    private void loadFromLocal (Map<String, String> query) {
        realmUI = Realm.getDefaultInstance();
        Log.v("keithtest", query.toString());

        RealmQuery<PetRealm> realmQuery = realmUI
                .where(PetRealm.class);

        String type = query.get("type");
        if (type != null && !type.isEmpty()) {
            realmQuery.equalTo("animal", type);
        }

        String breed = query.get("breed");
        if (breed != null && !breed.isEmpty()) {
            realmQuery.contains("breeds", breed, Case.INSENSITIVE);
        }

        String age = query.get("age");
        if (age != null && !age.isEmpty()) {
            realmQuery.equalTo("age", age);
        }

        String gender = query.get("gender");
        if (gender != null && !gender.isEmpty()) {
            realmQuery.equalTo("sex", type);
        }

        String city = query.get("city");
        if (city == null) {
            city = "";
        }

        String state = query.get("state");
        if (state == null) {
            state = "";
        }

        if (!city.isEmpty() || !state.isEmpty()) {
            realmQuery.beginGroup();
            if (!city.isEmpty()) {
                realmQuery
                        .equalTo("city", city)
                        .or();
            }

            if (!state.isEmpty()) {
                realmQuery
                        .equalTo("state", state);
            }
            realmQuery.endGroup();
        }

        RealmResults<PetRealm> results = realmQuery.findAll();

        List<Pet> pets = new ArrayList<>();
        for (PetRealm petRealm : results) {
            Pet newPet = new Pet();
            newPet.setName(petRealm.getName());
            newPet.setDescription(petRealm.getDescription());
            newPet.setId(petRealm.getId());
            newPet.setLastUpdate(petRealm.getLastUpdate());
            newPet.setAnimal(petRealm.getAnimal());
            newPet.setBreeds(petRealm.getBreeds());
            newPet.setAge(petRealm.getAge());
            newPet.setSex(petRealm.getSex());

            Contact contact = new Contact();
            contact.setState(petRealm.getState());

            List<PetMediaRealm> media = petRealm.getMedia();
            for (PetMediaRealm item : media) {
                PetMedia petMedia = new PetMedia();
                petMedia.urlSecureFullsize = item.getUrlSecureFullsize();
                petMedia.urlSecureThumbnail = item.getUrlSecureThumbnail();
                newPet.getMedia().add(petMedia);
            }

            pets.add(newPet);
        }

        if (results.size() > 0) {
            adapter.updatePets(pets);
            loading = false;
        }
    }
    
    private boolean updatedToday (Map<String, String> query) {
        Date lastUpdate = userLogin.getCacheUpdateDateForKey("Pets-" + query.toString());
        if (lastUpdate == null) {
            return false;
        }
        Calendar lastPetUpdate = Calendar.getInstance();
        lastPetUpdate.setTime(lastUpdate);

        Date now = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);

        return nowCalendar.get(Calendar.DATE) == lastPetUpdate.get(Calendar.DATE);
    }

    private void loadPets(String lastUpdatedBefore) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        if (!lastUpdatedBefore.isEmpty()) {
            filters.lastUpdatedBefore = lastUpdatedBefore;
        }

        final Map<String, String> query = filters.toMap();
        if (!shelterId.isEmpty()) {
            query.put("shelterId", shelterId);
        }

        if (updatedToday(query)) {
            loadFromLocal(query);
            return;
        }

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        mService.getPets(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PetResponse>() {
                    @Override
                    public void onCompleted() {
                        dialog.hide();
                        userLogin.setCacheUpdateDateForKey("Pets-" + query.toString(), new Date());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(PetResponse petResponse) {
                        loading = false;
                        adapter.updatePets(petResponse.getPets());
                        if (empty_view != null && petResponse.getPets().size() > 0) {
                            empty_view.setVisibility(View.INVISIBLE);
                        }

                        Realm realm = Realm.getDefaultInstance();

                        List<Pet> pets = petResponse.getPets();
                        for (final Pet pet : pets) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    PetRealm petRealm = realm
                                            .where(PetRealm.class)
                                            .equalTo("_id", pet.getId()).findFirst();

                                    if (petRealm == null) {
                                        petRealm = realm
                                                .createObject(PetRealm.class, pet.getId());
                                    }
                                    // @TODO: Is there a better way to update or add items?
                                    petRealm.setName(pet.getName());
                                    petRealm.setDescription(pet.getDescription());
                                    petRealm.setLastUpdate(pet.getLastUpdate());
                                    petRealm.setAnimal(pet.getAnimal());
                                    petRealm.setBreeds(pet.getBreeds());
                                    petRealm.setAge(pet.getAge());
                                    petRealm.setSex(pet.getSex());

                                    Contact contact = pet.getContact();
                                    if (contact != null) {
                                        petRealm.setState(contact.getState());
                                    }

                                    List<PetMedia> media = pet.getMedia();
                                    for (PetMedia item : media) {
                                        PetMediaRealm petMediaRealm = realm.createObject(PetMediaRealm.class);
                                        petMediaRealm.setUrlSecureFullsize(item.urlSecureFullsize);
                                        petMediaRealm.setUrlSecureThumbnail(item.urlSecureThumbnail);
                                        petRealm.getMedia().add(petMediaRealm);
                                    }
                                }
                            });
                        }
                        realm.close();
                    }
                });
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(scrollY <= 0 || dataSetManually) {
            return;
        }

        visibleItemCount = mLayoutManager.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            return;
        }

        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
            loading = true;
            Pet lastPet = adapter.pets.get(adapter.pets.size() - 1);
            loadPets(lastPet.getLastUpdate());
        }

        // @TODO: this is loading to early
//        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//            loading = true;
//            Pet lastPet = adapter.pets.get(adapter.pets.size() - 1);
//            loadPets(lastPet.getLastUpdate());
//        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Pet> pets = new ArrayList<Pet>();

        public ContentAdapter(Context context) {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (pets.size() > position) {
                Pet currentPet = pets.get(position);

                if (currentPet.getMedia() != null && currentPet.getMedia().size() > 0 && currentPet.getMedia().get(0).urlSecureThumbnail != null) {
                    String petImageUrl = currentPet.getMedia().get(0).urlSecureThumbnail;
                    Picasso.with(context)
                            .load(petImageUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.avator);
                }

                holder.name.setText(currentPet.getName());
                holder.description.setText(Html.fromHtml(currentPet.getDescription()));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pet currentPet = pets.get(position);
                    onClickSubject.onNext(String.valueOf(currentPet.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return pets.size();
        }

        public Observable<String> getPositionClicks(){
            return onClickSubject.asObservable();
        }

        public void updatePets(List<Pet> pets) {
            this.pets.addAll(pets);
            notifyDataSetChanged();
        }
    }
}
