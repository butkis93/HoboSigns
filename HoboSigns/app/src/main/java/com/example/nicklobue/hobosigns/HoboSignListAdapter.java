package com.example.nicklobue.hobosigns;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.util.ArrayList;
import java.util.List;

import util.HoboSign;

/**
 * Created by Dominick on 12/5/2015.
 */
public class HoboSignListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<HoboSign> mItems = new ArrayList<HoboSign>();

    public HoboSignListAdapter(Context context) {
        mContext = context;
    }

    public void add(HoboSign item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        mItems.remove(pos);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();

    }

    public void removeFromFilter(ArrayList<Integer> listOfThingsToRemove) {
        ArrayList<HoboSign> newList = new ArrayList<>();

        for(int i = 0; i < mItems.size(); i++) {
            if(listOfThingsToRemove.get(i) != i) {
                newList.add(mItems.get(i));
            }
        }

        mItems = newList;
        notifyDataSetChanged();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mItems.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoboSign hoboSign = (HoboSign) this.getItem(position);
        LinearLayout itemLayout =
                (LinearLayout) LayoutInflater.from(mContext).
                        inflate(R.layout.hobo_sign_list_item, parent, false);

        ImageView preview = (ImageView) itemLayout.findViewById(R.id.hobo_sign_preview_view);
        preview.setImageBitmap(hoboSign.getSign());

        itemLayout.setTag(null);
        return itemLayout;
    }


}