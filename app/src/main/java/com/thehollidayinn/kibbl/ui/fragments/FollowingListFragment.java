package com.thehollidayinn.kibbl.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.Following;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyTextView = (RelativeLayout) view.findViewById(R.id.empty_view);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new FollowingListFragment.ContentAdapter(recyclerView.getContext());
        adapter. getUnfollowClicks().subscribe(new Action1<Following>() {
            @Override
            public void call(Following favorite) {
                Shelter currentPet = favorite.shelterId;
                unfollow(currentPet.getId());
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadNotifications();

        return view;
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
        private final PublishSubject<String> onClickSubject = PublishSubject.create();
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
//                    Pet currentPet = pets.get(position);
//                    onClickSubject.onNext(String.valueOf(currentPet.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        public Observable<String> getPositionClicks(){
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
