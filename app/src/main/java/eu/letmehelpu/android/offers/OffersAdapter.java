package eu.letmehelpu.android.offers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.letmehelpu.android.network.OfferItem;
import eu.letmehelpu.android.view.OfferItemView;

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<OfferItem> offerItems = new ArrayList<>();
    private int transparentItem;

    public void setItems(List<OfferItem> offerItems) {
        this.offerItems = offerItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0)
        return new OffersViewHolder(new OfferItemView(parent.getContext()));

        if(viewType == 1) {
            return new RecyclerView.ViewHolder(new View(parent.getContext())) {

            };
        }

        throw new RuntimeException();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OffersViewHolder) {
            OffersViewHolder offersViewHolder = (OffersViewHolder) holder;
            offersViewHolder.setItem(offerItems.get(position));
        } else {
            holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(10, transparentItem));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(transparentItem == 0) {
            return 0;
        } else {
            if(offerItems.size() == position)
            return 1;
            else
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return offerItems.size() + (transparentItem == 0 ? 0 : 1);
    }

    public void setTransparentItem(int transparentItem) {
        this.transparentItem = transparentItem;
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
