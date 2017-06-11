package com.thehollidayinn.kibbl.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.Event;
import com.thehollidayinn.kibbl.data.models.Favorite;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.PetResponse;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.thehollidayinn.kibbl.ui.activities.EventDetailActivity;
import com.thehollidayinn.kibbl.ui.activities.PetDetailActivity;
import com.squareup.picasso.Picasso;
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

public class FavoritesListFragment extends Fragment {
    private ContentAdapter adapter;
    private static Context context;
    private static RecyclerView recyclerView;
    private static RelativeLayout emptyView;
    private ProgressDialog dialog;

    public FavoritesListFragment() {
        // Required empty public constructor
    }

    public static FavoritesListFragment newInstance() {
        FavoritesListFragment fragment = new FavoritesListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (RelativeLayout) view.findViewById(R.id.empty_view);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new FavoritesListFragment.ContentAdapter(recyclerView.getContext());
        adapter.getPositionClicks().subscribe(new Action1<Favorite>() {
            @Override
            public void call(Favorite favorite) {
                if (favorite.pet != null) {
                    Intent detailViewIntent = new Intent(FavoritesListFragment.this.getContext(), PetDetailActivity.class);
                    detailViewIntent.putExtra("PET_ID", favorite.pet.getId());
                    FavoritesListFragment.this.getActivity().startActivity(detailViewIntent);
                } else if (favorite.shelter != null) {
                    Intent detailViewIntent = new Intent(FavoritesListFragment.this.getContext(), ShelterDetailActivity.class);
                    detailViewIntent.putExtra("PET_ID", favorite.shelter.getId());
                    FavoritesListFragment.this.getActivity().startActivity(detailViewIntent);
                } else if (favorite.event != null) {
                    Intent detailViewIntent = new Intent(FavoritesListFragment.this.getContext(), EventDetailActivity.class);
                    detailViewIntent.putExtra("PET_ID", favorite.event.getId());
                    FavoritesListFragment.this.getActivity().startActivity(detailViewIntent);
                }

            }
        });

        adapter. getFavoriteClicks().subscribe(new Action1<Favorite>() {
            @Override
            public void call(Favorite favorite) {
                loadFavorites();
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadFavorites();

        return view;
    }

    public void loadFavorites() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);
        mService.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GenericResponse<List<Favorite>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse<List<Favorite>> genericResponse) {
                        List<Favorite> favorties = genericResponse.data;
                        adapter.updatePets(favorties);
                        dialog.hide();
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public ImageView favoriteButton;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_favorite, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            favoriteButton = (ImageView) itemView.findViewById(R.id.favoriteButton);
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;

        private final PublishSubject<Favorite> onClickSubject = PublishSubject.create();
        private final PublishSubject<Favorite> onFavoriteClickSubject = PublishSubject.create();

        private List<Favorite> favorites = new ArrayList<>();


        public ContentAdapter(Context context) {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (favorites != null && favorites.size() > position) {
                Favorite favorite = favorites.get(position);

                if (favorite.pet != null) {
                    Pet currentPet = favorite.pet;
                    if (currentPet.getMedia() != null && currentPet.getMedia().size() > 0 && currentPet.getMedia().get(0).urlSecureThumbnail != null) {
                        String petImageUrl = currentPet.getMedia().get(0).urlSecureThumbnail;
                        Picasso.with(context)
                                .load(petImageUrl)
                                .resize(50, 50)
                                .centerCrop()
                                .into(holder.avator);
                    }

                    holder.name.setText(currentPet.getName());
                    holder.description.setText(currentPet.getDescription());
                } else if (favorite.shelter != null) {
                    Shelter currentPet = favorite.shelter;
                    if (currentPet.getFacebook() != null && currentPet.getFacebook().getCover() != null) {
                        String petImageUrl = currentPet.getFacebook().getCover();
                        Picasso.with(context)
                                .load(petImageUrl)
                                .resize(50, 50)
                                .centerCrop()
                                .into(holder.avator);
                    }

                    holder.name.setText(currentPet.getName());
                    holder.description.setText(currentPet.getDescription());
                } else if (favorite.event != null) {
                    Event currentPet = favorite.event;
                    if (currentPet.getFacebook() != null && currentPet.getFacebook().getCover() != null) {
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

                holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favorite currentPet = favorites.get(position);
                        if (currentPet.event != null) {
                            favoritePet(currentPet.event.getId());
                        } else if (currentPet.pet != null) {
                            favoritePet(currentPet.pet.getId());
                        } else if (currentPet.shelter != null) {
                            favoritePet(currentPet.shelter.getId());
                        }
                        onFavoriteClickSubject.onNext(currentPet);
                    }
                });

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Favorite currentPet = favorites.get(position);
                    onClickSubject.onNext(currentPet);
                }
            });
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        public Observable<Favorite> getPositionClicks(){
            return onClickSubject.asObservable();
        }

        public Observable<Favorite> getFavoriteClicks(){
            return onFavoriteClickSubject.asObservable();
        }

        public void updatePets(List<Favorite> favorites) {
            this.favorites = favorites;
            if (favorites.size() != 0) {
                emptyView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }

        protected void favoritePet(String petId) {
            KibblAPIInterface mService = ApiUtils.getKibbleService(context);
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
                        }
                    });
        }
    }
}
