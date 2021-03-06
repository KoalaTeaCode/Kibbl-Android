package com.thehollidayinn.kibbl.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.Following;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.data.repositories.UserRepository;
import com.thehollidayinn.kibbl.ui.activities.EventDetailActivity;
import com.thehollidayinn.kibbl.ui.activities.PetDetailActivity;
import com.thehollidayinn.kibbl.ui.activities.ShelterDetailActivity;

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
 * Created by krh12 on 4/30/2017.
 */

public class FollowingListFragment extends Fragment {
    private FollowingListFragment.ContentAdapter adapter;
    private static Context context;
    private static RecyclerView recyclerView;
    private static RelativeLayout emptyTextView;
    private UserRepository userRepository;

    public FollowingListFragment() {
        // Required empty public constructor
    }

    public static FollowingListFragment newInstance() {
        FollowingListFragment fragment = new FollowingListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        userRepository = UserRepository.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyTextView = (RelativeLayout) view.findViewById(R.id.empty_view);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new FollowingListFragment.ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<Following>() {
            @Override
            public void call(Following favorite) {
               if (favorite.shelterId != null) {
                    Intent detailViewIntent = new Intent(getContext(), ShelterDetailActivity.class);
                    detailViewIntent.putExtra("PET_ID", String.valueOf(favorite.shelterId.getId()));
                    getActivity().startActivity(detailViewIntent);
                }
            }
        });
        adapter. getUnfollowClicks().subscribe(new Action1<Following>() {
            @Override
            public void call(Following favorite) {
                Shelter currentPet = favorite.shelterId;
                unfollow(currentPet.getId());
            }
        });

        Switch enableNotificaitonSwitch = (Switch) view.findViewById(R.id.enablePushSwitch);
        if (userRepository.getSubscribed()) {
            enableNotificaitonSwitch.setChecked(true);
            subscribeUserToPush(true);
        }
        enableNotificaitonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                subscribeUserToPush(isChecked);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadNotifications();

        return view;
    }

    private void subscribeUserToPush(Boolean isChecked) {
        String token = userRepository.getToken();

        if (token.isEmpty()) {
            token = FirebaseInstanceId.getInstance().getToken();
        }

        if (isChecked) {
            userRepository.setSubscribed(true);
            updatePushNotification(token, true);
        } else {
            userRepository.setSubscribed(false);
            updatePushNotification(token, false);
        }
    }

    public void updatePushNotification (String token, Boolean active) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(context);

        Map<String, String> options = new HashMap<>();
        options.put("deviceToken", token);
        options.put("platform", "android");
        options.put("active", String.valueOf(active));

        mService.pushNotification(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<Void>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<Void> response) {

                    }
                });
    }

    protected void unfollow(String petId) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(context);

        Map<String, String> options = new HashMap<>();
        options.put("shelterId", petId);

        mService.subscribe(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Following>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(Following petResponse) {
                        loadNotifications();
                    }
                });
    }

    private void loadNotifications() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        mService.getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Following>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("keithtest", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Following>> genericResponse) {
                        List<Following> favorties = genericResponse.data;
                        adapter.updatePets(favorties);
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public Button unfollowButton;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_follow, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            unfollowButton = (Button) itemView.findViewById(R.id.unfollowButton);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<FollowingListFragment.ViewHolder> {
        private final PublishSubject<Following> onClickSubject = PublishSubject.create();
        private final PublishSubject<Following> onFollowlickSubject = PublishSubject.create();

        private List<Following> favorites = new ArrayList<>();

        public ContentAdapter(Context context) {
        }

        @Override
        public FollowingListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FollowingListFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(FollowingListFragment.ViewHolder holder, final int position) {
            if (favorites != null && favorites.size() > position) {
                Following favorite = favorites.get(position);
                Shelter currentPet = favorite.shelterId;

                if (currentPet.getFacebook() != null) {
                    String petImageUrl = currentPet.getFacebook().getCover();
                    Picasso.with(context)
                            .load(petImageUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.avator);
                }

                holder.name.setText(currentPet.getName());
                holder.description.setText(currentPet.getDescription());
            }

            holder.unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Unfollowing")
                            .setMessage("Do you really want to unfollow?")
//                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Following favorite = favorites.get(position);

                                    onFollowlickSubject.onNext(favorite);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Following currentPet = favorites.get(position);
                    onClickSubject.onNext(currentPet);
                }
            });
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        public Observable<Following> getPositionClicks(){
            return onClickSubject.asObservable();
        }
        public Observable<Following> getUnfollowClicks(){
            return onFollowlickSubject.asObservable();
        }

        public void updatePets(List<Following> favorites) {
            this.favorites = favorites;
            if (favorites.size() != 0) {
                emptyTextView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}
