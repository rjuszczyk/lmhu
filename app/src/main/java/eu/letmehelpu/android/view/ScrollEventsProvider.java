package eu.letmehelpu.android.view;

import android.view.View;

public interface ScrollEventsProvider {
    void startProvidingScrollEventsFromView(View view, ScrollConsumer scrollConsumer);
    void stopProvidingScrollEventsFromView(View view, ScrollConsumer scrollConsumer);
    int getScrollFromView(View view);
}
