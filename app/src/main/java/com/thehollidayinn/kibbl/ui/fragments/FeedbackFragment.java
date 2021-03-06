package com.thehollidayinn.kibbl.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.FeedbackResponse;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Feedback;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class FeedbackFragment extends Fragment {
    private FeedbackFragment.ContentAdapter adapter;
    private static Context context;
    private static RecyclerView recyclerView;
    private static RelativeLayout emptyTextView;
    private Button sendFeedback;
    private EditText newFeedback;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
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
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyTextView = (RelativeLayout) view.findViewById(R.id.empty_view);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new FeedbackFragment.ContentAdapter(recyclerView.getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        newFeedback = (EditText) view.findViewById(R.id.newFeedback);
        sendFeedback = (Button) view.findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSendFeedback(newFeedback.getText().toString());
                newFeedback.setText("");
                sendFeedback.setEnabled(false);
            }
        });

        loadFeedbacks();

        return view;
    }

    private void setSendFeedback(String message) {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        Map<String, String> data = new HashMap<>();
        data.put("text", message);

        mService.postFeedback(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Feedback>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(Feedback genericResponse) {
                        // @TODO: can we simply add without?
                        sendFeedback.setEnabled(true);
                        loadFeedbacks();
                    }
                });
    }

    private void loadFeedbacks() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        mService.getFeedbacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FeedbackResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(FeedbackResponse genericResponse) {
                        List<Feedback> favorties = genericResponse.feedback;

                        Collections.sort(favorties, new Comparator<Feedback>() {
                            @Override
                            public int compare(Feedback fruit2, Feedback fruit1)
                            {
                                return  fruit1.createdAt.compareTo(fruit2.createdAt);
                            }
                        });

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

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<FeedbackFragment.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Feedback> favorites = new ArrayList<>();

        public ContentAdapter(Context context) {
        }

        @Override
        public FeedbackFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FeedbackFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(FeedbackFragment.ViewHolder holder, final int position) {
            if (favorites != null && favorites.size() > position) {
                Feedback favorite = favorites.get(position);


                holder.name.setText(favorite.text);
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

        public void updatePets(List<Feedback> favorites) {
            this.favorites = favorites;
            if (favorites.size() != 0) {
                emptyTextView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}
