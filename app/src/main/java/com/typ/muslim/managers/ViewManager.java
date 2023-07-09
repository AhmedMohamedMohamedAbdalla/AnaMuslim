/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furkanakdemir.surroundcardview.SurroundCardView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.typ.muslim.R;
import com.typ.muslim.adapters.CitiesAdapter;
import com.typ.muslim.interfaces.OnItemClickListener;
import com.typ.muslim.libs.EnhancedScaleTouchListener;
import com.typ.muslim.models.Location;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Contains some custom views and ready, self-handling BottomSheets
 */
public class ViewManager {

    // TODO: 2/24/21 Create InfoBottomSheet that shows title,visual animated gif that describes it, message and understood button

    public static class PreviewLocationBottomSheet {

        /**
         * Creates a new instance of PreviewLocationBottomSheet.
         */
        @SuppressLint("InflateParams")
        public PreviewLocationBottomSheet(Context context, Location location, View.OnClickListener onClickListener) {
            // Setup views
            View bsView = LayoutInflater.from(context).inflate(R.layout.bs_preview_location, null, false);
            BottomSheetDialog bs = new BottomSheetDialog(context);
            bs.setContentView(bsView);
            bs.setDismissWithAnimation(true);
            // Show Location info in views
            Objects.requireNonNull(((TextInputLayout) bsView.findViewById(R.id.til_country_name)).getEditText()).setText(location.getCountryName());
            Objects.requireNonNull(((TextInputLayout) bsView.findViewById(R.id.til_city_name)).getEditText()).setText(location.getCityName());
            Objects.requireNonNull(((TextInputLayout) bsView.findViewById(R.id.til_latitude)).getEditText()).setText(String.valueOf(location.getLatitude()));
            Objects.requireNonNull(((TextInputLayout) bsView.findViewById(R.id.til_longitude)).getEditText()).setText(String.valueOf(location.getLongitude()));
            Objects.requireNonNull(((TextInputLayout) bsView.findViewById(R.id.til_timezone)).getEditText()).setText(String.format(Locale.getDefault(), "%.1f", location.getTimezone()));
            // Click listeners
            bsView.findViewById(R.id.btn_continue).setOnClickListener(v -> {
                onClickListener.onClick(v);
                bs.cancel();
            });
            // Show BottomSheet
            bs.show();
        }

    }

    public static abstract class SearchCityBottomSheet implements OnItemClickListener<Location> {

        private final LocationManager locationManager;
        private final CitiesAdapter citiesAdapter;
        // Views
        private final EditText inputSearch;
        private final BottomSheetDialog bs;
        private final EasyRecyclerView rv;

        /**
         * Creates a new instance of SearchCityBottomSheet.
         */
        @SuppressLint("InflateParams")
        public SearchCityBottomSheet(Context context, LocationManager locationManager) {
            // Runtime
            this.locationManager = locationManager;
            // Setup views
            View bsView = LayoutInflater.from(context).inflate(R.layout.bs_search_city, null, false);
            bs = new BottomSheetDialog(context);
            bs.setContentView(bsView);
            bs.setCanceledOnTouchOutside(false);
            bs.setDismissWithAnimation(true);
            bs.getBehavior().setFitToContents(false);
            inputSearch = bsView.findViewById(R.id.input_search_city);
            rv = bsView.findViewById(R.id.erv_search_cities);
            citiesAdapter = new CitiesAdapter(context, rv); // Cities Adapter
            rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.addItemDecoration(createStickyHeaderDecoration());
            rv.setAdapterWithProgress(citiesAdapter);
            // Item click listeners
            citiesAdapter.setOnItemClickListener(position -> {
                this.onItemClick(citiesAdapter.getItem(position));
                bs.cancel();
            });
            inputSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) refreshWithQuery(v.getText().toString());
                return true;
            });
            // Show BottomSheet
            bs.getBehavior().setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            bs.show();
        }

        private StickyHeaderDecoration createStickyHeaderDecoration() {
            return new StickyHeaderDecoration(new StickyHeaderDecoration.IStickyHeaderAdapter<HeaderItemView>() {
                @Override
                public long getHeaderId(int position) {
                    return Objects.hash(citiesAdapter.getItem(position).getCountryCode());
                }

                @Override
                public HeaderItemView onCreateHeaderViewHolder(ViewGroup parent) {
                    return new HeaderItemView(parent);
                }

                @Override
                public void onBindHeaderViewHolder(HeaderItemView headerItemView, int i) {
                    headerItemView.setData(citiesAdapter.getItem(i));
                }
            });
        }

        public void refresh(List<Location> locations) {
            citiesAdapter.refresh(locations);
            // Adapt bottom sheet height with items count
            adaptBottomSheetState();
        }

        /**
         * Perform new search operation in local DB using newQuery if changed
         */
        public void refreshWithQuery(String newQuery) {
            // Update runtime
            inputSearch.setText(newQuery);
            // Perform search if query changed
            citiesAdapter.refresh(locationManager.searchForCities(newQuery));
            // Adapt bottom sheet height with items count
            adaptBottomSheetState();
        }

        private void adaptBottomSheetState() {
            if (citiesAdapter.getCount() == 0) bs.getBehavior().setFitToContents(true);
            else if (citiesAdapter.getCount() <= 5) bs.getBehavior().setFitToContents(true);
            else if (citiesAdapter.getCount() > 5 && citiesAdapter.getCount() < 20) {
                bs.getBehavior().setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                bs.getBehavior().setFitToContents(false);
            } else if (citiesAdapter.getCount() >= 20) {
                bs.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                bs.getBehavior().setFitToContents(false);
            }
        }

        private static class HeaderItemView extends BaseViewHolder<Location> {

            public HeaderItemView(ViewGroup parent) {
                super(parent, android.R.layout.simple_list_item_1);
            }

            @Override
            public void setData(Location location) {
                AManager.log("SHV", "setData: " + location);
                ((TextView) itemView).setText(location.getCountryName());
                ((TextView) itemView).setTextColor(ResMan.getColor(getContext(), R.color.colorText));
                itemView.setBackgroundColor(ResMan.getColor(getContext(), R.color.bg_input_box));
            }
        }
    }

    public static class ChoiceSelectorBottomSheet {

        @SuppressLint("InflateParams")
        public ChoiceSelectorBottomSheet(Context context, String title, String subtitle, Choice currentChoice, List<Choice> choices, OnItemClickListener<Choice> listener) {
            // Runtime
            ChoicesAdapter choicesAdapter = new ChoicesAdapter(context, choices, currentChoice);
            // Setup Views
            View bsView = LayoutInflater.from(context).inflate(R.layout.bs_choice_selector, null, false);
            BottomSheetDialog bs = new BottomSheetDialog(context);
            bs.setContentView(bsView);
            bs.setDismissWithAnimation(true);
            bs.setCanceledOnTouchOutside(false);
            bsView.findViewById(R.id.btn_cancel).setOnTouchListener(new EnhancedScaleTouchListener() {
                @Override
                public void onClick(View v, float x, float y) {
                    bs.dismiss();
                }
            }); // Cancel button.
            ((MaterialTextView) bsView.findViewById(R.id.tv_title)).setText(title);
            ((MaterialTextView) bsView.findViewById(R.id.tv_subtitle)).setText(subtitle);
            EasyRecyclerView rv = bsView.findViewById(R.id.erv_choices);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(choicesAdapter);
            // Item Click Listener
            choicesAdapter.setItemClickListener(choice -> {
                listener.onItemClick(choice);
                bs.cancel();
            });
            // Show BottomSheet
            bs.show();
        }

        public static class Choice {

            private final String id;
            private final String title;
            private final String desc;

            public Choice(String id, String title, String desc) {
                this.id = id;
                this.title = title;
                this.desc = desc;
            }

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getDesc() {
                return desc;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null) return false;
                if (this == o) return true;
                if (!(o instanceof Choice)) return false;
                Choice choice = (Choice) o;
                return id.equals(choice.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }

            @Override
            public String toString() {
                return "Choice{" +
                        "id='" + id + '\'' +
                        ", title='" + title + '\'' +
                        ", desc='" + desc + '\'' +
                        '}';
            }


        }

        private static class ChoicesAdapter extends RecyclerArrayAdapter<Choice> {

            // Runtime
            private final Choice currentChoice;
            private com.typ.muslim.interfaces.OnItemClickListener<Choice> listener;

            public ChoicesAdapter(Context context, List<Choice> objects, Choice currentChoice) {
                super(context, objects);
                this.currentChoice = currentChoice;
            }

            public void setItemClickListener(com.typ.muslim.interfaces.OnItemClickListener<Choice> listener) {
                this.listener = listener;
            }

            @Override
            public BaseViewHolder<Choice> OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChoiceViewHolder(parent);
            }

            private class ChoiceViewHolder extends BaseViewHolder<Choice> {

                // Views
                private final MaterialTextView tvChoiceTitle;
                private final MaterialTextView tvChoiceDesc;

                public ChoiceViewHolder(ViewGroup parent) {
                    super(parent, R.layout.item_choice);
                    // Setup Views
                    tvChoiceTitle = $(R.id.tv_choice_title);
                    tvChoiceDesc = $(R.id.tv_choice_desc);
                }

                @Override
                public void setData(Choice choice) {
                    SurroundCardView card = (SurroundCardView) itemView;
                    AManager.log("ViewManager", "setChoiceData: " + choice + " | " + currentChoice);
                    // Bind views
                    tvChoiceTitle.setText(choice.getTitle());
                    tvChoiceDesc.setText(choice.getDesc());
                    // Release the selection from this card at first
                    card.setSurrounded(false);
                    // Check if this choice is selected
                    if (currentChoice != null && Integer.parseInt(currentChoice.getId()) == Integer.parseInt(choice.getId())) card.surround();
                    else if (card.isCardSurrounded()) card.setSurrounded(false);
                    // Listeners
                    card.setOnClickListener(v -> {
                        card.surround();
                        listener.onItemClick(choice);
                    });
                }
            }

        }
    }
}
