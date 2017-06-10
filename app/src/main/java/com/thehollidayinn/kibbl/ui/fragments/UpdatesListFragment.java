package com.thehollidayinn.kibbl.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.thehollidayinn.kibbl.data.models.Following;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.Updates;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by krh12 on 6/10/2017.
 */

public class UpdatesListFragment extends Fragment {
    private UpdatesListFragment.ContentAdapter adapter;
    private static Context context;
    private static RecyclerView recyclerView;
    private static RelativeLayout emptyTextView;

    public UpdatesListFragment() {
        // Required empty public constructor
    }

    public static UpdatesListFragment newInstance() {
        UpdatesListFragment fragment = new UpdatesListFragment();
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

        adapter = new UpdatesListFragment.ContentAdapter(recyclerView.getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lodaUpdates();

        return view;
    }

    private void lodaUpdates() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        mService.getUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Updates>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("keithtest", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Updates>> genericResponse) {
                        List<Updates> favorties = genericResponse.data;
                        adapter.updatePets(favorties);
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

    public static class ContentAdapter extends RecyclerView.Adapter<UpdatesListFragment.ViewHolder> {
        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Updates> favorites = new ArrayList<>();

        public ContentAdapter(Context context) {
        }

        @Override
        public UpdatesListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UpdatesListFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(UpdatesListFragment.ViewHolder holder, final int position) {
            if (favorites != null && favorites.size() > position) {
                Updates favorite = favorites.get(position);
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

        public void updatePets(List<Updates> favorites) {
            this.favorites = favorites;
            if (favorites.size() != 0) {
                emptyTextView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}
