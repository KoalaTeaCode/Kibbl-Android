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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.data.models.CommentResponse;
import com.thehollidayinn.kibbl.data.models.Feedback;
import com.thehollidayinn.kibbl.data.models.GenericResponse;
import com.thehollidayinn.kibbl.data.models.Comment;
import com.thehollidayinn.kibbl.data.models.Shelter;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by krh12 on 4/30/2017.
 */

public class CommentListFragment extends Fragment {
    private CommentListFragment.ContentAdapter adapter;
    private static Context context;
    private static RecyclerView recyclerView;
    private static RelativeLayout emptyTextView;
    private Button sendFeedback;
    private EditText newFeedback;
    private String itemId;
    private UserLogin user;

    public CommentListFragment() {
        // Required empty public constructor
    }

    public static CommentListFragment newInstance(String itemId) {
        CommentListFragment fragment = new CommentListFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        fragment.itemId = itemId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = UserLogin.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyTextView = (RelativeLayout) view.findViewById(R.id.empty_view);
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new CommentListFragment.ContentAdapter(recyclerView.getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadComments();

        newFeedback = (EditText) view.findViewById(R.id.newFeedback);
        sendFeedback = (Button) view.findViewById(R.id.send_feedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComments(newFeedback.getText().toString());
                newFeedback.setText("");
                sendFeedback.setEnabled(false);
            }
        });

        return view;
    }

    private void sendComments(String message) {
        if (!user.isLoggedIn()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("You must be logged in to comment.")
                    .setTitle("Whoops!");
            AlertDialog dialog = builder.create();
            builder.setPositiveButton("Got it.", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }

        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());

        Map<String, String> data = new HashMap<>();
        data.put("text", message);
        data.put("itemId", itemId);

        mService.postComment(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Comment>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("keithtest", e.toString());
                    }

                    @Override
                    public void onNext(Comment genericResponse) {
                        // @TODO: can we simply add without?
                        sendFeedback.setEnabled(true);
                        loadComments();
                    }
                });
    }

    private void loadComments() {
        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());
        
        Map<String, String> options = new HashMap<>();
        options.put("itemId", itemId);
        
        mService.getComments(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(CommentResponse genericResponse) {
                        List<Comment> favorties = genericResponse.comments;
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
    public static class ContentAdapter extends RecyclerView.Adapter<CommentListFragment.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;

        private final PublishSubject<String> onClickSubject = PublishSubject.create();

        private List<Comment> favorites = new ArrayList<>();

        public ContentAdapter(Context context) {
        }

        @Override
        public CommentListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentListFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CommentListFragment.ViewHolder holder, final int position) {
            if (favorites != null && favorites.size() > position) {
                Comment favorite = favorites.get(position);

//                String petImageUrl = currentPet.getFacebook().getCover();
//                Picasso.with(context)
//                        .load(petImageUrl)
//                        .resize(50, 50)
//                        .centerCrop()
//                        .into(holder.avator);

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

        public void updatePets(List<Comment> favorites) {
            this.favorites = favorites;
            if (favorites.size() != 0) {
                emptyTextView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}
