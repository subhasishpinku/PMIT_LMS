package com.dgc.photoediting.frame;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dgc.photoediting.BackButtonHandlerInterface;
import com.dgc.photoediting.OnBackClickListener;
import com.dgc.photoediting.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trending extends Fragment implements OnBackClickListener {
    private BackButtonHandlerInterface backButtonHandler;
    boolean doubleBackToExitPressedOnce = false;
    RecyclerView recyclerView;
    ///////////////////////////////
    RecyclerView recyclerview1;
    ArrayList<String> Number;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout ;
    View ChildView ;
    int RecyclerViewItemPosition ;
    ArrayList images = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
            R.drawable.i, R.drawable.j));
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        backButtonHandler = (BackButtonHandlerInterface) activity;
        backButtonHandler.addBackClickListener(this);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trading_layout, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        Adapter adapter = new Adapter(getContext(), images);
        recyclerView.setAdapter(adapter);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview1);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        RecyclerViewHorizontalAdapter = new RecyclerViewAdapter(Number);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        list_iten();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //    @Override
//    public void onDetach() {
//        super.onDetach();
//
//    }
    @Override
    public void onDetach() {
        super.onDetach();
        backButtonHandler.removeBackClickListener(this);
        backButtonHandler = null;
    }
    @Override
    public boolean onBackClick() {
        //Toast.makeText(getContext(), "Do not exit", Toast.LENGTH_SHORT).show();
        return true;
    }
    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        ArrayList images;
        Context context;
        public Adapter(Context context, ArrayList images) {
            this.context = context;
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflating the Layout(Instantiates list_item.xml layout file into View object)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

            // Passing view to ViewHolder
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // Binding data to the into specified position
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // TypeCast Object to int type
            int res = (int) images.get(position);
            holder.images.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            // Returns number of items currently available in Adapter
            return images.size();
        }

        // Initializing the Views
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView images;

            public ViewHolder(View view) {
                super(view);
                images = (ImageView) view.findViewById(R.id.imageView);
            }
        }
    }
    public void list_iten(){
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    // Showing clicked item value on screen using toast message.
                    // Toast.makeText(getContext(), Number.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }
    public void AddItemsToRecyclerViewArrayList(){
        Number = new ArrayList<>();
        Number.add("cartoon");
        Number.add("make up");
        Number.add("make up");
        Number.add("selfie");
        Number.add("birthday");
        Number.add("hearts");
        Number.add("background");
        Number.add("kids");
        Number.add("bokeh");
        Number.add("winter");


    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {
        private List<String> list;
        public class MyView extends RecyclerView.ViewHolder {
            public TextView textView;
            public MyView(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.textview1);
            }
        }
        public RecyclerViewAdapter(List<String> horizontalList) {
            this.list = horizontalList;
        }
        @Override
        public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
            return new MyView(itemView);
        }
        @Override
        public void onBindViewHolder(final MyView holder, final int position) {
            holder.textView.setText(list.get(position));
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}