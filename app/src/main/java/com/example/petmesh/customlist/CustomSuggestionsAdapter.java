package com.example.petmesh.customlist;

import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Searches;
import com.example.petmesh.ui.notifications.NotificationsFragment;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;

public class CustomSuggestionsAdapter  extends SuggestionsAdapter<Searches, CustomSuggestionsAdapter.SuggestionHolder> {

    MaterialSearchBar searchBar;
    public CustomSuggestionsAdapter(LayoutInflater inflater, MaterialSearchBar searchBar) {
        super(inflater);
        this.searchBar=searchBar;
    }

    @Override
    public int getSingleViewHeight() {
        return 80;
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(Searches suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getName());

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String term = constraint.toString();
                if(term.isEmpty())
                    suggestions = suggestions_clone;
                else {
                    suggestions = new ArrayList<>();
                    for (Searches item: suggestions_clone)
                        if(item.getName().toLowerCase().contains(term.toLowerCase()))
                            suggestions.add(item);
                }
                results.values = suggestions;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<Searches>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemViewClickListener {
        void OnItemClickListener(int position, View v);

        void OnItemDeleteListener(int position, View v);
    }

    class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected ImageView image;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(getSuggestions().get(getAdapterPosition()));
                    searchBar.setText(getSuggestions().get(getAdapterPosition()).getName());
                }
            });
        }
    }

}
