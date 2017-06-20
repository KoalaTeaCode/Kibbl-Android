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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Facebook;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.ui.activities.EventDetailActivity;

import java.text.SimpleDateFormat;
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

public class EventListFragment extends Fragment {
    private EventListFragment.ContentAdapter adapter;
    private static Context context;
    private String filter;
    private Filters filters;
    private ProgressDialog dialog;
    private Boolean dataSetManually = false;
    private List<Event> events;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public EventListFragment() {
    }

    public static EventListFragment newInstance(String filter, List<Event> events) {
        EventListFragment f = new EventListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("FILTER", filter);
        f.setArguments(args);
        if (events != null) {
            f.events = events;
            f.dataSetManually = true;
        }

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filters = Filters.getSharedInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        this.context = getContext();

        adapter = new EventListFragment.ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<String>() {
            @Override
            public void call(String petId) {
                Intent detailViewIntent = new Intent(EventListFragment.this.getContext(), EventDetailActivity.class);
                detailViewIntent.putExtra("PET_ID", petId);
                EventListFragment.this.getActivity().startActivity(detailViewIntent);
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
                if(dy <= 0 || dataSetManually) {
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
                    Event lastEvent = adapter.pets.get(adapter.pets.size() - 1);
                    loadEvents(lastEvent.getStartTime().toString());
                }
            }

        });

//        this.filter = getArguments().getString("FILTER");
        if (!dataSetManually) {
            loadEvents("");
        } else {
            adapter.updateEvents(this.events);
        }


        return recyclerView;
    }

    private void loadEvents(String createdAtBefore) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

//        if (!this.filter.isEmpty()) {
//            query.put("type", this.filter);
//            filters.type = this.filter;
//        }

        if (!createdAtBefore.isEmpty()) {
            filters.createdAtBefore = createdAtBefore;
        }

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        mService.getEvents(filters.toMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Event>>>() {
                    @Override
                    public void onCompleted() {
                        dialog.hide();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Event>> response) {
                        loading = false;
                        adapter.updateEvents(response.data);
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
    public static class ContentAdapter extends RecyclerView.Adapter<EventListFragment.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final Drawable[] mPlaceAvators;

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Event> pets = new ArrayList<Event>();

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
        public EventListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventListFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(EventListFragment.ViewHolder holder, final int position) {
            if (pets.size() > position) {
                Event currentEvent = pets.get(position);

                Facebook facebook = currentEvent.getFacebook();
                if (facebook != null) {
                    String petImageUrl = facebook.getCover();
                    Picasso.with(context)
                            .load(petImageUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.avator);
                }


                holder.name.setText(currentEvent.getName());
                
                String dateString = android.text.format.DateFormat.format("MMMM dd, yyyy", currentEvent.getStartTime()).toString();
                holder.description.setText(dateString);
            } else {
//                holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
                holder.name.setText(mPlaces[position % mPlaces.length]);
                holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event currentEvent = pets.get(position);
                    onClickSubject.onNext(String.valueOf(currentEvent.getId()));
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

        public void updateEvents(List<Event> pets) {
            this.pets.addAll(pets);
            notifyDataSetChanged();
        }
    }
}
