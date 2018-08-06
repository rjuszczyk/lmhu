package eu.letmehelpu.android.offers;

import java.util.List;

import eu.letmehelpu.android.network.OfferItem;
import io.reactivex.Single;

public interface LoadOffers {

    Single<List<OfferItem>> loadOffersIWork();
    Single<List<OfferItem>> loadOffersIHire();
}
