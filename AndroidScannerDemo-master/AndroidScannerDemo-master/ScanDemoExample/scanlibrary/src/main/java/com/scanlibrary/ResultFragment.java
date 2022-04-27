package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment {

    private View view;
    private ImageView scannedImageView;
    private Bitmap original;
    private FloatingActionButton originalButton;
    private FloatingActionButton MagicColorButton;
    private FloatingActionButton grayModeButton;
    private FloatingActionButton bwButton;
    private FloatingActionButton Sharpness;
    private Bitmap transformed;
    SeekBar seekBar;
    boolean greyScale = false;
    boolean BW = false;
    boolean sharp = false;
    boolean magic = false;
    private static ProgressDialogFragment progressDialogFragment;

    public ResultFragment() {
    }

    public  static int valueof=50;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);
        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueof = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                change();
            }
        });

        Sharpness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                magic = false;
                BW = false;
                sharp = true;
                greyScale = false;

            change();
            }
        });
        return view;
    }


    public Bitmap doSharpen(Bitmap original, float[] radius) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(this.getContext());

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(radius);

        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;

    }

    public void change(){

        Toast.makeText(getContext(),String.valueOf(valueof),Toast.LENGTH_SHORT).show();
        showProgressDialog(getResources().getString(R.string.applying_filter));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    if(greyScale== true) {

                        transformed = doGreyscale(original);
                    }else if(BW==true){

                        transformed = createContrast(original);
                    }
                    else if(sharp==true){

                        float[] sharp = { 0.0f*valueof/80, -1.0f*valueof/80, 0.0f*valueof/80, -1.0f*valueof/80, 5.0f*valueof/80, -1.0f*valueof/80, 0.0f*valueof/80, -1.0f*valueof/80,
                                0.0f*valueof/80};

                                transformed = doSharpen(original,sharp);
                    }else {

                        transformed = original;
                    }

                          //  transformed = edit(original);
                } catch (final OutOfMemoryError e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            transformed = original;
                            scannedImageView.setImageBitmap(original);
                            e.printStackTrace();
                            dismissDialog();
                        }
                    });
                }catch (Exception ex){
                    transformed = original;
                    scannedImageView.setImageBitmap(original);
                    ex.printStackTrace();
                    dismissDialog();
                }



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannedImageView.setImageBitmap(transformed);
                        dismissDialog();
                    }
                });
            }
        });

    }

    public  Bitmap doGreyscale(Bitmap src) {
        // constant factors

        final double GS_RED = 0.299*valueof/50;;
        final double GS_GREEN = 0.587*valueof/50;;
        final double GS_BLUE = 0.114*valueof/50;;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image

        return bmOut;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        Sharpness = view.findViewById(R.id.shapness);
        seekBar = view.findViewById(R.id.seekBar2);
        scannedImageView = (ImageView) view.findViewById(R.id.scannedImage);
        originalButton =  view.findViewById(R.id.original);
        originalButton.setOnClickListener(new OriginalButtonClickListener());
        MagicColorButton = view.findViewById(R.id.magicColor);
        MagicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        grayModeButton =  view.findViewById(R.id.grayMode);
        grayModeButton.setOnClickListener(new GrayButtonClickListener());
        bwButton =  view.findViewById(R.id.BWMode);
        bwButton.setOnClickListener(new BWButtonClickListener());
        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);

        toolbar = (Toolbar) view.findViewById(R.id.resultToolbar);
        toolbar.setTitle("ENHANCER");
        toolbar.setTitleTextColor(Color.WHITE);

        final Toolbar toolbar = view.findViewById(R.id.resultToolbar);
        toolbar.inflateMenu(R.menu.save_menu);
        Menu menu=toolbar.getMenu();
        MenuItem item = menu.findItem(R.id.menu_save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showProgressDialog(getResources().getString(R.string.loading));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent data = new Intent(getActivity(),Class.forName("com.e.smartscanner.ui.home.AddImage"));

                            Bitmap bitmap = transformed;
                            if (bitmap == null) {
                                bitmap = original;
                            }


                            Uri uri = Utils.getUri(getActivity(), bitmap);
                            data.putExtra("image",uri.toString());

                            startActivity(data);
                            getActivity().finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        });
    }


    Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        toolbar.inflateMenu(R.menu.save_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_save) {
            showProgressDialog(getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent data = new Intent();
                        Bitmap bitmap = transformed;
                        if (bitmap == null) {
                            bitmap = original;
                        }
                        Uri uri = Utils.getUri(getActivity(), bitmap);
                        data.putExtra(ScanConstants.SCANNED_RESULT, uri);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        original.recycle();
                        System.gc();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                getActivity().finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } else if (itemId == android.R.id.home) {
            //startActivity(new Intent(DisplayImage.this, OpenCamera.class));
            //this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            original = Utils.getBitmap(getActivity(), uri);


            File file = new File(uri.getPath());
            file.delete();
            if(file.exists()){
                file.getCanonicalFile().delete();
                if(file.exists()){
                    getContext().getApplicationContext().deleteFile(file.getName());
                }
            }

            return original;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        scannedImageView.setImageBitmap(scannedImage);
    }

    public  Bitmap createContrast(Bitmap src) {

        // image size
        int value =valueof;
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.red(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.red(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }

    private class BWButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {


                    magic = false;
                    BW = true;
                    sharp = false;
                    greyScale = false;

                    try {
                        transformed = ((ScanActivity) getActivity()).getBWBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class MagicColorButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    try {

                        magic = true;
                        BW = false;
                        sharp = false;
                        greyScale = false;

                        transformed = ((ScanActivity) getActivity()).getMagicColorBitmap(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class OriginalButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                magic = false;
                BW = false;
                sharp = false;
                greyScale = false;
                showProgressDialog(getResources().getString(R.string.applying_filter));
                transformed = original;
                scannedImageView.setImageBitmap(original);
                dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                dismissDialog();
            }
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            showProgressDialog(getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        magic = false;
                        BW = false;
                        sharp = false;
                        greyScale = true;
                        transformed = ((ScanActivity) getActivity()).getGrayBitmap(original);

                  //  transformed = doGreyscale(original);
                    } catch (final OutOfMemoryError e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transformed = original;
                                scannedImageView.setImageBitmap(original);
                                e.printStackTrace();
                                dismissDialog();
                                onClick(v);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scannedImageView.setImageBitmap(transformed);
                            dismissDialog();
                        }
                    });
                }
            });
        }
    }

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }




}