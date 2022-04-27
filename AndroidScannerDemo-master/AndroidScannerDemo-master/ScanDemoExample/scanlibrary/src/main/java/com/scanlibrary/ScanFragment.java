package com.scanlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhansi on 29/03/15.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ScanFragment extends Fragment {

    public static int ANGLE = 0;
    private FloatingActionButton right,left;
    private Button scanButton;
    private ImageView sourceImageView;
    private FrameLayout sourceFrame;
    private PolygonView polygonView;
    private View view;
    private ProgressDialogFragment progressDialogFragment;
    private IScanner scanner;
    private Bitmap original;
    Toolbar toolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) activity;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("CROP");
        view = inflater.inflate(R.layout.scan_fragment_layout, null);
        init();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_save)
                {
                    Map<Integer, PointF> points = polygonView.getPoints();
                    if (isScanPointsValid(points)) {
                        new ScanAsyncTask(points).execute();
                    } else {
                        showErrorDialog();
                    }
                }

                return true;
            }
        });
        view.findViewById(R.id.rotateright).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ANGLE+=90;
                Matrix matrix = new Matrix();
                matrix.postRotate(ANGLE);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, original.getWidth(), original.getHeight(), true);
                final Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
          //      sourceImageView.setImageBitmap(rotatedBitmap);
                original =rotatedBitmap;
                sourceFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        setBitmap(original);
                    }
                });
                ANGLE-=90;
            }
        });


        view.findViewById(R.id.rotateleft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ANGLE-=90;
                Matrix matrix = new Matrix();
                matrix.postRotate(ANGLE);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, original.getWidth(), original.getHeight(), true);
                final Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                //      sourceImageView.setImageBitmap(rotatedBitmap);
                 original =rotatedBitmap;
                sourceFrame.post(new Runnable() {
                    @Override
                    public void run() {
                        setBitmap(original);
                    }
                });
                ANGLE+=90;

            }
        });

        return view;
    }

    public ScanFragment() {

    }

    MenuItem menuItem ;
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        toolbar = (Toolbar) view.findViewById(R.id.toolbar2);
        toolbar.setTitle("CROP");
        toolbar.setTitleTextColor(Color.WHITE);

        final Toolbar toolbar = view.findViewById(R.id.toolbar2);
        toolbar.inflateMenu(R.menu.save_menu);
        Menu menu=toolbar.getMenu();
        MenuItem item = menu.findItem(R.id.menu_save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        sourceImageView = (ImageView) view.findViewById(R.id.sourceImageView);


        sourceFrame = (FrameLayout) view.findViewById(R.id.sourceFrame);
        polygonView = (PolygonView) view.findViewById(R.id.polygonView);
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {
                original = getBitmap();
                if (original != null) {
                    setBitmap(original);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            Bitmap bitmap = Utils.getBitmap(getActivity(), uri);
//            getActivity().getContentResolver().delete(uri, null, null);

            File file = new File(uri.getPath());
            file.delete();
            if(file.exists()){
                file.getCanonicalFile().delete();
                if(file.exists()){
                    getContext().getApplicationContext().deleteFile(file.getName());
                }

            }
           // getActivity().getContentResolver().delete()
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SELECTED_BITMAP);
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setBitmap(Bitmap original) {
        Bitmap scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        sourceImageView.setImageBitmap(scaledBitmap);
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        float[] points = ((ScanActivity) getActivity()).getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        toolbar.inflateMenu(R.menu.save_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showErrorDialog() {
        SingleButtonDialogFragment fragment = new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true);
        FragmentManager fm = getActivity().getFragmentManager();
        fragment.show(fm, SingleButtonDialogFragment.class.toString());
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / sourceImageView.getWidth();
        float yRatio = (float) original.getHeight() / sourceImageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        Bitmap _bitmap = ((ScanActivity) getActivity()).getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
        return _bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;

        public ScanAsyncTask(Map<Integer, PointF> points) {
            this.points = points;
        }

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getString(R.string.scanning));
        }

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap =  getScannedBitmap(original, points);
            Uri uri = Utils.getUri(getActivity(), bitmap);
            scanner.onScanFinish(uri);
            return bitmap;
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bitmap.recycle();
            dismissDialog();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    protected void showProgressDialog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

}