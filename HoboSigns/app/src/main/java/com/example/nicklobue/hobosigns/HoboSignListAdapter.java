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
    private final List<HoboSign> mItems = new ArrayList<HoboSign>();

    public HoboSignListAdapter(Context context) {
        mContext = context;
    }

    public void add(HoboSign item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
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

    public void filter(final Bitmap symbol) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Initialize all of our OpenCV matching stuff
                Bitmap desiredSymbol = HoboSign.getResizedBitmap(symbol, 128);

                ArrayList<Integer> listOfSignsToKeep = new ArrayList<>();
                Mat desiredMat = new Mat(desiredSymbol.getWidth(), desiredSymbol.getHeight(), CvType.CV_8UC1);
                Utils.bitmapToMat(desiredSymbol, desiredMat);

                FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.ORB);
                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

                MatOfKeyPoint testKps = new MatOfKeyPoint();
                MatOfKeyPoint matchingImgKps = new MatOfKeyPoint();

                Mat testDescriptors = new Mat();
                Mat matchingImgDescriptors = new Mat();

                fd.detect(desiredMat, matchingImgKps);
                de.compute(desiredMat, matchingImgKps, matchingImgDescriptors);

                //Gather a list of all HoboSigns whose symbols do not match to within a certain
                //threshold
                int i = 0;
                double threshold = 100000.0d;
                for (HoboSign sign : mItems) {
                    //Bitmap img = sign.getSymbol();
                    Bitmap img = null;
                    Mat m = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC1);
                    Utils.bitmapToMat(img, m);
                    fd.detect(m, testKps);
                    de.compute(m, testKps, testDescriptors);

                    MatOfDMatch matches = new MatOfDMatch();
                    matcher.match(matchingImgDescriptors, testDescriptors, matches);
                    List<DMatch> list = matches.toList();

                    double sum = 0;
                    double score = 0;
                    for (int j = 0; j < list.size(); j++) {
                        double d = list.get(j).distance;
                        sum += d * d;
                    }

                    score = sum / (list.size() * list.size());

                    if (score < threshold) {
                        listOfSignsToKeep.add(i);
                    }

                    i++;
                }

                //Remove all HoboSigns we determined too different from the desired symbol
                for(Integer indx : listOfSignsToKeep) {
                    mItems.remove(indx);
                    notifyDataSetChanged();
                }
            }
        });

        thread.start();

    }
}