package eu.letmehelpu.android.network;

import eu.letmehelpu.android.R;

public class OfferItem {
    long id;
    long offer_id;
    String title;
    float longitude;
    float latitude;
    String address;
    int price;
    int offer_price;
    int offer_price_type;
    String currency;
    int type;
    int status;
    long contractor_id;
    String thumbnail; //uploads/offer/desk-office-pen-ruler-450x260.jpg",
    long created_at;
    long updated_at;
    ContractorItem contractor;

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public ContractorItem getContractor() {
        return contractor;
    }

    public int getStatus() {
        return status;
    }


    public static OfferItem create(int i) {
        OfferItem offerItem = new OfferItem();
        offerItem.title = "Item " + i;
        offerItem.status = 0;
        offerItem.thumbnail = "uploads/offer/desk-office-pen-ruler-450x260.jpg";
        offerItem.contractor = new ContractorItem();
        offerItem.contractor.first_name = "First";
        offerItem.contractor.last_name= "Last";
        return offerItem;
    }
}
