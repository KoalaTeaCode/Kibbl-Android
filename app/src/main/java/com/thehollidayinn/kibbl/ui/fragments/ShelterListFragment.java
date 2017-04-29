package com.thehollidayinn.kibbl.ui.fragments;

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
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Facebook;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import java.util.ArrayList;
import java.util.List;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        this.context = getContext();

        adapter = new ShelterListFragment.ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<String>() {
            @Override
            public void call(String petId) {
//                Intent detailViewIntent = new Intent(ShelterListFragment.this.getContext(), ShelterDetailActivity.class);
//                detailViewIntent.putExtra("PET_ID", petId);
//                ShelterListFragment.this.getActivity().startActivity(detailViewIntent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.filter = getArguments().getString("FILTER");
        loadShelters();

        return recyclerView;
    }

    private void loadShelters() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        if (!this.filter.isEmpty()) {
//            query.put("type", this.filter);
//            filters.type = this.filter;
        }

        mService.getShelters(filters.toMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Shelter>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Shelter>> response) {
                        adapter.updateShelters(response.data);
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
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final Drawable[] mPlaceAvators;

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Shelter> pets = new ArrayList<Shelter>();

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mPlaces = resources.getStringArray(R.array.places);
            mPlaceDesc = resources.getStringArray(R.array.place_desc);
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
            mPlaceAvators = new Drawable[a.length()];
            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            a.recycle();
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
            } else {
                holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
                holder.name.setText(mPlaces[position % mPlaces.length]);
                holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
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