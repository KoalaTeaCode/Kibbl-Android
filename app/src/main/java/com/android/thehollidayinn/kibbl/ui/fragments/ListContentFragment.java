package com.android.thehollidayinn.kibbl.ui.fragments;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.models.PetResponse;
import com.android.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.android.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.android.thehollidayinn.kibbl.ui.activities.PetDetailActivity;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by krh12 on 1/3/2017.
 */

public class ListContentFragment extends Fragment {
    private ContentAdapter adapter;
    private static Context context;
    private String filter;

    public ListContentFragment() {
    }

    public static ListContentFragment newInstance(String filter) {
        ListContentFragment f = new ListContentFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("FILTER", filter);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        this.context = getContext();

        adapter = new ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<String>() {
            @Override
            public void call(String petId) {
                Intent detailViewIntent = new Intent(ListContentFragment.this.getContext(), PetDetailActivity.class);
                detailViewIntent.putExtra("PET_ID", petId);
                ListContentFragment.this.getActivity().startActivity(detailViewIntent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.filter = getArguments().getString("FILTER");
        loadPets();

        return recyclerView;
    }

    private void loadPets() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        Map<String, String> query = new HashMap<>();
        if (!this.filter.isEmpty()) {
            query.put("type", this.filter);
        }

        mService.getPets(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PetResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(PetResponse petResponse) {
                        adapter.updatePets(petResponse.getPets());
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
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final Drawable[] mPlaceAvators;

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Pet> pets = new ArrayList<Pet>();

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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (pets.size() > position) {
                Pet currentPet = pets.get(position);

                String petImageUrl = currentPet.getMedia().get(3);
                Picasso.with(context)
                        .load(petImageUrl)
                        .resize(50, 50)
                        .centerCrop()
                        .into(holder.avator);

                holder.name.setText(currentPet.getName());
                holder.description.setText(currentPet.getDescription());
            } else {
                holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
                holder.name.setText(mPlaces[position % mPlaces.length]);
                holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
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
            return LENGTH;
        }

        public Observable<String> getPositionClicks(){
            return onClickSubject.asObservable();
        }

        public void updatePets(List<Pet> pets) {
            this.pets = pets;
            notifyDataSetChanged();
        }
    }
}
