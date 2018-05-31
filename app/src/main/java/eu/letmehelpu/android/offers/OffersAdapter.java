package eu.letmehelpu.android.offers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.letmehelpu.android.network.OfferItem;
import eu.letmehelpu.android.view.OfferItemView;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    List<OfferItem> offerItems = new ArrayList<>();

    public void setItems(List<OfferItem> offerItems) {
        this.offerItems = offerItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OffersViewHolder(new OfferItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull OffersViewHolder holder, int position) {
        holder.setItem(offerItems.get(position));
    }

    @Override
    public int getItemCount() {
        return offerItems.size();
    }

    class OffersViewHolder extends RecyclerView.ViewHolder {
        OfferItemView offerItemView;
        public OffersViewHolder(OfferItemView itemView) {
            super(itemView);
            offerItemView = itemView;

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int _8dp = (int) (itemView.getResources().getDisplayMetrics().density * 8);
            int _10dp = (int) (itemView.getResources().getDisplayMetrics().density * 10);

            params.setMargins(_8dp, _10dp, _8dp, 0);

            offerItemView.setLayoutParams(params);
            offerItemView = itemView;
        }

        public void setItem(OfferItem offerItem) {



            offerItemView.setOfferTitle(offerItem.getTitle());
            offerItemView.setContractor(offerItem.getContractor());
            offerItemView.setOfferStatus(offerItem.getStatus());
            offerItemView.setThumbnail(offerItem.getThumbnail());
        }
    }
}
