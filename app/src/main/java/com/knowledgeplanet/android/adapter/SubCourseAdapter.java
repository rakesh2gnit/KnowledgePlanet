package com.knowledgeplanet.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knowledgeplanet.android.R;
import com.knowledgeplanet.android.model.Courses;

import java.util.List;

/**
 * Created by Admin on 26-08-2017.
 */

public class SubCourseAdapter extends RecyclerView.Adapter<SubCourseAdapter.MyViewHolder> {

    private List<Courses> courseList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }

    public SubCourseAdapter(List<Courses> courseList) {
        this.courseList = courseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Courses courses = courseList.get(position);
        holder.title.setText(courses.getTitle());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
}
