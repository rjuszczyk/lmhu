package eu.letmehelpu.android.offers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.network.OfferItem;

public class OfferListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offer_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.offer_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration((int) (getResources().getDisplayMetrics().density*10));

        recyclerView.addItemDecoration(verticalSpaceItemDecoration);
        OffersAdapter offersAdapter = new OffersAdapter();
        offersAdapter.setItems(Arrays.asList(
                OfferItem.create(1),
                OfferItem.create(2),
                OfferItem.create(3),
                OfferItem.create(4),
                OfferItem.create(5),
                OfferItem.create(6),
                OfferItem.create(7),
                OfferItem.create(8),
                OfferItem.create(9),
                OfferItem.create(10),
                OfferItem.create(11),
                OfferItem.create(12),
                OfferItem.create(13),
                OfferItem.create(14),
                OfferItem.create(15),
                OfferItem.create(16),
                OfferItem.create(17),
                OfferItem.create(18),
                OfferItem.create(19)
        ));
        recyclerView.setAdapter(offersAdapter);
    }
}
