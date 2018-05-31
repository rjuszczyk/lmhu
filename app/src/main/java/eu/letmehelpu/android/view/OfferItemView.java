package eu.letmehelpu.android.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.network.ContractorItem;

public class OfferItemView extends CardView {
    private TextView offerTitle;
    private ImageView offerThumbnail;
    private TextView offerContractor;
    private TextView offerState;
    public OfferItemView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public OfferItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OfferItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.offer_item_view, this);
        offerTitle = findViewById(R.id.offer_title);
        offerThumbnail = findViewById(R.id.offer_thumbnail);
        offerContractor = findViewById(R.id.offer_contractor);
        offerState = findViewById(R.id.offer_state);
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle.setText(offerTitle);
    }

    public void setThumbnail(String imageUrl) {
        if(imageUrl != null) {
            Glide
                    .with(this)
                    .load("https://letmehelpu-v2.preview.cloudart.pl/" + imageUrl)
                    .into(offerThumbnail);
        } else  {
            Glide.with(this)
                    .load(R.mipmap.sample_img)
                    .into(offerThumbnail);
        }
    }

    public void setContractor(ContractorItem contractor) {
        offerContractor.setText(contractor.getFirstName() + " " + contractor.getLastName());
    }

    public void setOfferStatus(int state) {
        offerState.setText(getOfferStatusText(state));
        offerState.setTextColor(getResources().getColor(getOfferStatusColor(state)));
        offerState.setCompoundDrawablesRelativeWithIntrinsicBounds(getOfferStatusDrawable(state), 0, 0, 0);
    }

    public String getOfferStatusText(int status) {
        switch (status) {
            case 0:
                return "Created";

            case 1:
                return "Accepted";

            case 2:
                return "Expired";

            case 3:
                return "Rejected";

            case 4:
                return "Canceled";
        }
        throw new IllegalStateException("Illegal offer state");
    }

    public int getOfferStatusColor(int status) {
        switch (status) {
            case 0:
                return R.color.ongoingColor;

            case 1:
                return R.color.paymentColor;

            case 2:
                return R.color.finishedColor;

            case 3:
                return R.color.finishedColor;

            case 4:
                return R.color.finishedColor;
        }
        throw new IllegalStateException("Illegal offer state");
    }
    public int getOfferStatusDrawable(int status) {
        switch (status) {
            case 0:
                return R.drawable.ic_ongoing;

            case 1:
                return R.drawable.ic_payment;

            case 2:
                return R.drawable.ic_done;

            case 3:
                return R.drawable.ic_done;

            case 4:
                return R.drawable.ic_done;
        }
        throw new IllegalStateException("Illegal offer state");
    }
}
