package eu.letmehelpu.android.offers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import dagger.android.support.DaggerFragment;
import eu.letmehelpu.android.R;
import eu.letmehelpu.android.network.OfferItem;
import eu.letmehelpu.android.view.PagerIndicator;

public class OffersFragment extends DaggerFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final View appBar = view.findViewById(R.id.appBar);

        final ViewGroup insetsConsumer = view.findViewById(R.id.insets_consumer);
        View inset = insetsConsumer.getChildAt(0);

        inset.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int paddingLeft = v.getLeft() - insetsConsumer.getLeft();
                int paddingTop = v.getTop() - insetsConsumer.getTop();
                int paddingRight = insetsConsumer.getRight() - v.getRight();
                int paddingBottom = insetsConsumer.getBottom() - v.getBottom();

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) appBar.getLayoutParams();
                lp.setMargins(paddingLeft, paddingTop, paddingRight, paddingBottom);
                appBar.setLayoutParams(lp);
            }
        });

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        ViewPager mViewPager = view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setAdapter(new CustomPagerAdapter(getContext()));

        final PagerIndicator pagerIndicator = view.findViewById(R.id.tabs);
        pagerIndicator.setupWithViewPager(mViewPager);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new OfferListFragment();
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_offer_list, collection, false);

            initView(layout);

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        private void initView(ViewGroup view) {
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


}
