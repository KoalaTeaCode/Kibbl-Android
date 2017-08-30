package com.thehollidayinn.kibbl.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.Facebook;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.realm.ShelterRealm;
import com.thehollidayinn.kibbl.data.realm.FacebookRealm;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.data.repositories.UserRepository;
import com.thehollidayinn.kibbl.ui.activities.ShelterDetailActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by krh12 on 4/27/2017.
 */

public class ShelterListFragment extends Fragment {
    private ShelterListFragment.ContentAdapter adapter;
    private static Context context;
    private String filter;
    private Filters filters;
    private ProgressDialog dialog;
    private Realm realmUI;
    private UserRepository userLogin;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public ShelterListFragment() {
    }

    public static ShelterListFragment newInstance(String filter) {
        ShelterListFragment f = new ShelterListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("FILTER", filter);
        f.setArguments(args);
        f.filters = Filters.getSharedInstance();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filters = Filters.getSharedInstance();
        userLogin = UserRepository.getInstance(this.getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realmUI != null) {
            realmUI.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        this.context = getContext();

        adapter = new ShelterListFragment.ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<String>() {
            @Override
            public void call(String petId) {
                Intent detailViewIntent = new Intent(ShelterListFragment.this.getContext(), ShelterDetailActivity.class);
                detailViewIntent.putExtra("PET_ID", petId);
                ShelterListFragment.this.getActivity().startActivity(detailViewIntent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy <= 0) {
                    return;
                }

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    return;
                }

                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                {
                    loading = true;
                    Shelter lastShelter = adapter.pets.get(adapter.pets.size() - 1);
                    loadShelters(lastShelter.getCreatedAt());
                }
            }

        });

        this.filter = getArguments().getString("FILTER");
        loadShelters("");

        return recyclerView;
    }

    private void loadFromLocal () {
        realmUI = Realm.getDefaultInstance();
        RealmResults<ShelterRealm> results = realmUI
                .where(ShelterRealm.class)
                .findAll();

        List<Shelter> shelters = new ArrayList<>();
        for (ShelterRealm shelterRealm : results) {
            Shelter newShelter = new Shelter();
            newShelter.setName(shelterRealm.getName());
            newShelter.setId(shelterRealm.getId());

            FacebookRealm facebookRealm = shelterRealm.getFacebook();
            if (facebookRealm != null) {
                Facebook facebook = new Facebook();
                facebook.setId(facebookRealm.getId());
                facebook.setCover(facebookRealm.getCover());
                newShelter.setFacebook(facebook);
            }

            shelters.add(newShelter);
        }

        if (results.size() > 0) {
            adapter.updateShelters(shelters);
            loading = false;
        }
    }

    private boolean updatedToday () {
        Date lastUpdate = userLogin.getLastShelterUpdate();
        if (lastUpdate == null) {
            return false;
        }
        Calendar lastShelterUpdate = Calendar.getInstance();
        lastShelterUpdate.setTime(lastUpdate);

        Date now = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);

        return nowCalendar.get(Calendar.DATE) == lastShelterUpdate.get(Calendar.DATE);
    }

    private void loadShelters(String createdAtBefore) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        if (updatedToday()) {
            loadFromLocal();
            return;
        }


        if (!this.filter.isEmpty()) {
//            query.put("type", this.filter);
//            filters.type = this.filter;
        }

        if (!createdAtBefore.isEmpty()) {
            filters.createdAtBefore = createdAtBefore;
        }

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        mService.getShelters(filters.toMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Shelter>>>() {
                    @Override
                    public void onCompleted() {
                        dialog.hide();
                        userLogin.setLastShelterUpdate(new Date());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Shelter>> response) {
                        adapter.updateShelters(response.data);
                        Realm realm = Realm.getDefaultInstance();

                        List<Shelter> shelters = response.data;
                        for (final Shelter shelter : shelters) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    ShelterRealm shelterRealm = realm
                                            .where(ShelterRealm.class)
                                            .equalTo("_id", shelter.getId()).findFirst();

                                    if (shelterRealm == null) {
                                        shelterRealm = realm
                                                .createObject(ShelterRealm.class, shelter.getId());
                                    }
                                    // @TODO: Is there a better way to update or add items?
                                    shelterRealm.setName(shelter.getName());

                                    Facebook facebook = shelter.getFacebook();
                                    if (facebook != null) {
                                        FacebookRealm facebookRealm =  realm.createObject(FacebookRealm.class);
                                        facebookRealm.setId(facebook.getId());
                                        facebookRealm.setCover(facebook.getCover());
                                        shelterRealm.setFacebook(facebookRealm);
                                    }
                                }
                            });
                        }
                        realm.close();
                    }
                });
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

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ShelterListFragment.ViewHolder> {
        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Shelter> pets = new ArrayList<Shelter>();

        public ContentAdapter(Context context) {
        }

        @Override
        public ShelterListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ShelterListFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ShelterListFragment.ViewHolder holder, final int position) {
            if (pets.size() > position) {
                Shelter currentShelter = pets.get(position);

                Facebook facebook = currentShelter.getFacebook();
                if (facebook != null) {
                    String petImageUrl = facebook.getCover();
                    Picasso.with(context)
                            .load(petImageUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.avator);
                }

                holder.name.setText(currentShelter.getName());
                holder.description.setText(currentShelter.getDescription());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Shelter currentShelter = pets.get(position);
                    onClickSubject.onNext(String.valueOf(currentShelter.getId()));
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

        public void updateShelters(List<Shelter> pets) {
            this.pets = pets;
            notifyDataSetChanged();
        }
    }
}
