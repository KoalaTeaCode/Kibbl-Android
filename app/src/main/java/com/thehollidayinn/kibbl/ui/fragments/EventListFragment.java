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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Facebook;
import com.thehollidayinn.kibbl.data.models.Filters;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Location;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.Place;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.realm.EventRealm;
import com.thehollidayinn.kibbl.data.realm.FacebookRealm;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.data.repositories.UserRepository;
import com.thehollidayinn.kibbl.ui.activities.EventDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
 * Created by krh12 on 4/27/2017.
 */

public class EventListFragment extends Fragment implements NestedScrollView.OnScrollChangeListener {
    private EventListFragment.ContentAdapter adapter;
    private static Context context;
    private String shelterId = "";
    private Filters filters;
    private ProgressDialog dialog;
    private Boolean dataSetManually = false;
    private List<Event> events;
    private RelativeLayout empty_view;
    private Realm realmUI;
    private UserRepository userLogin;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public EventListFragment() {
    }

    public static EventListFragment newInstance(String shelterId, List<Event> events) {
        EventListFragment f = new EventListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        if (events != null) {
            f.events = events;
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

        if (!dataSetManually) {
            loadEvents("");
        } else {
            adapter.updateEvents(this.events);
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        RealmQuery<EventRealm> realmQuery = realmUI
            .where(EventRealm.class);

        String startDateString = query.get("startDate");
        if (startDateString != null && !startDateString.isEmpty()) {
            try {
                Date startDate = format.parse(startDateString);
                Log.v("keithtest", startDate.toString());
                realmQuery.greaterThan("startTime", startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String endDateString = query.get("endDate");
        if (endDateString != null && !endDateString.isEmpty()) {
            try {
                Date endDate = format.parse(endDateString);
                realmQuery.lessThan("endTime", endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

        RealmResults<EventRealm> results = realmQuery.findAll();

        List<Event> events = new ArrayList<>();
        for (EventRealm eventRealm : results) {
            Event newEvent = new Event();
            newEvent.setName(eventRealm.getName());
            newEvent.setId(eventRealm.getId());
            newEvent.setStartTime(eventRealm.getStartTime());
            newEvent.setEndTime(eventRealm.getEndTime());

            Place place = new Place();
            Location location = new Location();
            location.city = eventRealm.getCity();
            location.state = eventRealm.getState();
            place.location = location;
            newEvent.setPlace(place);

            FacebookRealm facebookRealm = eventRealm.getFacebook();
            if (facebookRealm != null) {
                Facebook facebook = new Facebook();
                facebook.setId(facebookRealm.getId());
                facebook.setCover(facebookRealm.getCover());
                newEvent.setFacebook(facebook);
            }

            events.add(newEvent);
        }

        if (results.size() > 0) {
            adapter.updateEvents(events);
            loading = false;
        }
    }

    private boolean updatedToday (Map<String, String> query) {
        Date lastUpdate = userLogin.getCacheUpdateDateForKey("Events-" + query.toString());
        if (lastUpdate == null) {
            return false;
        }
        Calendar lastEventUpdate = Calendar.getInstance();
        lastEventUpdate.setTime(lastUpdate);

        Date now = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now);

        return nowCalendar.get(Calendar.DATE) == lastEventUpdate.get(Calendar.DATE);
    }

    private void loadEvents(String createdAtBefore) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        if (!createdAtBefore.isEmpty()) {
            filters.createdAtBefore = createdAtBefore;
        }

        final Map<String, String> query = filters.toMap();
        if (!shelterId.isEmpty()) {
            query.put("shelterId", shelterId);
        }

        if (updatedToday(query) && (filters.search == null || filters.search.isEmpty())) {
            loadFromLocal(query);
            return;
        }

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);


        mService.getEvents(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Event>>>() {
                    @Override
                    public void onCompleted() {
                        dialog.hide();
                        if (filters.search == null || filters.search.isEmpty()) {
                            userLogin.setCacheUpdateDateForKey("Events-" + query.toString(), new Date());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Event>> response) {
                        loading = false;
                        adapter.updateEvents(response.data);
                        if (empty_view != null && response.data.size() > 0) {
                            empty_view.setVisibility(View.INVISIBLE);
                        }

                        Realm realm = Realm.getDefaultInstance();

                        List<Event> events = response.data;
                        for (final Event event : events) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    EventRealm eventRealm = realm
                                            .where(EventRealm.class)
                                            .equalTo("_id", event.getId()).findFirst();

                                    if (eventRealm == null) {
                                        eventRealm = realm
                                                .createObject(EventRealm.class, event.getId());
                                    }
                                    // @TODO: Is there a better way to update or add items?
                                    eventRealm.setName(event.getName());
                                    eventRealm.setStartTime(event.getStartTime());
                                    eventRealm.setEndTime(event.getEndTime());

                                    Place place = event.getPlace();
                                    if (place != null) {
                                        Location location = place.location;
                                        if (location != null) {
                                            eventRealm.setCity(location.city);
                                            eventRealm.setState(location.state);
                                        }
                                    }

                                    Facebook facebook = event.getFacebook();
                                    if (facebook != null) {
                                        FacebookRealm facebookRealm =  realm.createObject(FacebookRealm.class);
                                        facebookRealm.setId(facebook.getId());
                                        facebookRealm.setCover(facebook.getCover());
                                        eventRealm.setFacebook(facebookRealm);
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
            Event lastEvent = adapter.pets.get(adapter.pets.size() - 1);
            loadEvents(lastEvent.getStartTime().toString());
        }

//        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//            loading = true;
//            Event lastEvent = adapter.pets.get(adapter.pets.size() - 1);
//            loadEvents(lastEvent.getStartTime().toString());
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

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<EventListFragment.ViewHolder> {

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Event> pets = new ArrayList<Event>();

        public ContentAdapter(Context context) {
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

                if (currentEvent.getStartTime() != null) {
                    String dateString = android.text.format.DateFormat.format("MMMM dd, yyyy", currentEvent.getStartTime()).toString();
                    holder.description.setText(dateString);
                }
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
