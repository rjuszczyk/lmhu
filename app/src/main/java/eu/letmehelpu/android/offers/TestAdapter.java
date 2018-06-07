package eu.letmehelpu.android.offers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    @NonNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestHolder(new TextView(parent.getContext()));
    }

    @Override
    public int getItemCount() {
        return 10000;
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {
        ((TextView)holder.itemView).setText("element " + position);
    }

    class TestHolder extends RecyclerView.ViewHolder {

        public TestHolder(View itemView) {
            super(itemView);
        }
    }
}
