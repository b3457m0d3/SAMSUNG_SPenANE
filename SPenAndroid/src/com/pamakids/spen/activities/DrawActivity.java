package com.pamakids.spen.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.adobe.fre.FREContext;
import com.pamakids.spen.utils.SPenSDKUtils;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.*;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-26
 * Time: PM5:40
 * To change this template use File | Settings | File Templates.
 */
public class DrawActivity extends Activity {

    private SCanvasView sCanvasView;

    private FrameLayout mLayoutContainer;
    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView mFillingBtn;
    private ImageView mColorPickerBtn;
    private ImageView mUndoBtn;
    private ImageView mRedoBtn;
    private ImageView mOKBtn;
    FREContext freContext = null;
    String sourceName = "";
//    SObjectImage sObjectImage = null;

    public static final String TAG = "Draw Activity";

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    Bitmap bitmap = null;
    Integer bitmapWdith = 0;
    Integer bitmapHeight = 0;
    Integer undoTime = 0;

    protected void unselectImage(){
//        if (sCanvasView.isSObjectSelected() && sCanvasView.isUndoable() && undoTime < 1) {
//            undoTime++;
//            freContext.dispatchStatusEventAsync(TAG, "OnLongPressed3:");
//            sCanvasView.undo();
//        }else{
//            undoTime = 0;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceStage) {

        super.onCreate(savedInstanceStage);

        System.gc();

        String arg = this.getIntent().getStringExtra("arg");

        if(arg.equals("snowman")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.e(TAG, "Draw Created");

        freContext = SPenSDKUtils.freContext;
        Activity activity = freContext.getActivity();

        setContentView(freContext.getResourceId("layout.drawing_board"));

        mPenBtn = (ImageView) findViewById(freContext.getResourceId("id.penBtn"));
        mPenBtn.setOnClickListener(mBtnClickListener);
        mPenBtn.setOnLongClickListener(mBtnLongClickListener);
        mEraserBtn = (ImageView) findViewById(freContext.getResourceId("id.eraseBtn"));
        mEraserBtn.setOnClickListener(mBtnClickListener);
        mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
//        mFillingBtn = (ImageView) findViewById(freContext.getResourceId("id.fillingBtn"));
//        mFillingBtn.setOnClickListener(mBtnClickListener);
//        mFillingBtn.setOnLongClickListener(mBtnLongClickListener);
//        mColorPickerBtn = (ImageView) findViewById(freContext.getResourceId("id.colorPickerBtn"));
//        mColorPickerBtn.setOnClickListener(mColorPickerListener);

        mOKBtn = (ImageView) findViewById(freContext.getResourceId("id.okBtn"));
        mOKBtn.setOnClickListener(okListener);

        mUndoBtn = (ImageView) findViewById(freContext.getResourceId("id.undoBtn"));
        mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
        mRedoBtn = (ImageView) findViewById(freContext.getResourceId("id.redoBtn"));
        mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

        mLayoutContainer = (FrameLayout) findViewById(freContext.getResourceId("id.layout_container"));

        try {
            sourceName = "drawable." + arg;
            Log.e(TAG, sourceName);
            bitmap = BitmapFactory.decodeResource(getResources(), freContext.getResourceId(sourceName));
            bitmapWdith = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
        } catch (Exception e) {

        }

        sCanvasView = (SCanvasView) findViewById(freContext.getResourceId("id.scanvasView"));

        final Bitmap finalBitmap = bitmap;

        sCanvasView.setSCanvasLongPressListener(new SCanvasLongPressListener() {
            @Override
            public void onLongPressed(float v, float v2) {
                freContext.dispatchStatusEventAsync(TAG, "LP2"+undoTime);
               unselectImage();
            }

            @Override
            public void onLongPressed() {
//                freContext.dispatchStatusEventAsync(TAG, "LP"+undoTime);
//                unselectImage();
            }
        });

        sCanvasView.setSCanvasInitializeListener(new SCanvasInitializeListener() {
            @Override
            public void onInitialized() {
                try {
                    Log.e(TAG, "Canvas Initialized2");
                    sCanvasView.setCanvasZoomEnable(false);
                    sCanvasView.setRemoveLongPressStroke(false);
                    sCanvasView.setTextLongClickSelectOption(false);
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_DEFAULT);

                    new BitmapLoader().execute();

//                    Log.e(TAG, "Schedule");

//                    worker.schedule(runnable, 5, TimeUnit.SECONDS);

                    updateModeState();

                    SettingStrokeInfo strokeInfo = sCanvasView.getSettingViewStrokeInfo();
                    if (strokeInfo != null) {
                        strokeInfo.setStrokeColor(0x6E98B9);
                        strokeInfo.setStrokeWidth(5);
                        sCanvasView.setSettingViewStrokeInfo(strokeInfo);
                    }

                    freContext.dispatchStatusEventAsync(TAG, "info"+strokeInfo);

                } catch (Exception e) {
                    freContext.dispatchStatusEventAsync(TAG, e.toString());
                }
            }
        });
        //------------------------------------
        // SettingView Setting
        //------------------------------------
        // Resource Map for Layout & Locale
        HashMap<String, Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
        // Resource Map for Custom font path
        HashMap<String, String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);
        settingResourceMapString.put(SCanvasConstants.CUSTOM_RESOURCE_ASSETS_PATH, "spen_sdk_resource_default");
        // Create Setting View
        sCanvasView.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);

        //------------------------------------------------
        // History Change Listener
        //------------------------------------------------
        sCanvasView.setHistoryUpdateListener(new HistoryUpdateListener() {
            @Override
            public void onHistoryChanged(boolean b, boolean b2) {
                mUndoBtn.setEnabled(b);
                mRedoBtn.setEnabled(b2);
            }
        });

        //------------------------------------------------
        // SCanvas Mode Changed Listener
        //------------------------------------------------
        sCanvasView.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

            @Override
            public void onModeChanged(int mode) {
                updateModeState();
            }
        });

        //------------------------------------------------
        // Color Picker Listener
        //------------------------------------------------
        sCanvasView.setColorPickerColorChangeListener(new ColorPickerColorChangeListener() {
            @Override
            public void onColorPickerColorChanged(int nColor) {

                int nCurMode = sCanvasView.getCanvasMode();
                if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
                    SettingStrokeInfo strokeInfo = sCanvasView.getSettingViewStrokeInfo();
                    if (strokeInfo != null) {
                        strokeInfo.setStrokeColor(nColor);
                        sCanvasView.setSettingViewStrokeInfo(strokeInfo);
                    }
                } else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
                    // do nothing
                } else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT) {
                    SettingTextInfo textInfo = sCanvasView.getSettingViewTextInfo();
                    if (textInfo != null) {
                        textInfo.setTextColor(nColor);
                        sCanvasView.setSettingViewTextInfo(textInfo);
                    }
                } else if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
                    SettingFillingInfo fillingInfo = sCanvasView.getSettingViewFillingInfo();
                    if (fillingInfo != null) {
                        fillingInfo.setFillingColor(nColor);
                        sCanvasView.setSettingViewFillingInfo(fillingInfo);
                    }
                }
            }
        });

        mUndoBtn.setEnabled(false);
        mRedoBtn.setEnabled(false);
        mPenBtn.setSelected(true);
        sCanvasView.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);

        sCanvasView.setSPenHoverListener(new SPenHoverListener() {

            @Override
            public void onHoverButtonUp(View view, MotionEvent event) {
                int nPreviousMode = sCanvasView.getCanvasMode();

                boolean bIncludeDefinedSetting = true;
                boolean bIncludeCustomSetting = true;
                boolean bIncludeEraserSetting = true;
                SettingStrokeInfo settingInfo = sCanvasView.getNextSettingViewStrokeInfo(bIncludeDefinedSetting, bIncludeCustomSetting, bIncludeEraserSetting);
                if (settingInfo != null) {
                    if (sCanvasView.setSettingViewStrokeInfo(settingInfo)) {
                        // Mode Change : Pen => Eraser					
                        if (nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN
                                && settingInfo.getStrokeStyle() == SObjectStroke.SAMM_STROKE_STYLE_ERASER) {
                            // Change Mode
                            sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
                            // Show Setting View
                            if (sCanvasView.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)) {
                                sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
                                sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
                            }
                            updateModeState();
                        }
                        // Mode Change : Eraser => Pen 
                        if (nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER
                                && settingInfo.getStrokeStyle() != SObjectStroke.SAMM_STROKE_STYLE_ERASER) {
                            // Change Mode
                            sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
                            // Show Setting View
                            if (sCanvasView.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)) {
                                sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
                                sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
                            }
                            updateModeState();
                        }
                    }
                }
            }

            @Override
            public void onHoverButtonDown(View view, MotionEvent event) {

            }

            @Override
            public boolean onHover(View view, MotionEvent event) {
                return false;
            }
        });
    }

    private class   BitmapLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
//            mProgressDialog = ProgressDialog.show(DrawActivity.this, getString(R.string.please_wait),
//                    getString(R.string.loading_bitmap), true, false);
            Log.e(TAG, "PreExcute");
        }

        @Override
        protected Void doInBackground(final Void... params) {
            SObjectImage sObjectImage = null;
            Log.e(TAG, "do in background");
//            Bitmap bitmap = null;
            try {

//                bitmap = BitmapFactory.decodeResource(freContext.getActivity().getResources(), freContext.getResourceId(sourceName));
                sObjectImage = getImageFromBitmap(bitmap, false, false);
//                bitmap.recycle();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            Log.e(TAG, bitmap.getWidth() + ":" + bitmap.getHeight() + "---");
            final SObjectImage finalSObjectImage = sObjectImage;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Boolean insertResult = sCanvasView.insertSAMMImage(finalSObjectImage, false);
                    Log.e(TAG, insertResult + "__");
                    bitmap.recycle();
//                    mUndoBtn.setEnabled(false);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
//            mProgressDialog.dismiss();
            Log.e(TAG, "Excute");
        }
    }

    private static final String IMAGE_SCALED_TO_BACKGROUND = "isBackground";

    RectF rect;

    private SObjectImage getImageFromBitmap(final Bitmap bitmap, final boolean compression, final boolean scaleToCanvas)
            throws IOException {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        final SObjectImage image = new SObjectImage();

        int canvasWidth = sCanvasView.getWidth();
        int canvasHeight = sCanvasView.getHeight();
        Log.e(TAG, "sw:" + canvasWidth + "-sh:" + canvasHeight+"iw:"+imageWidth+"ih:"+imageHeight);
        if (scaleToCanvas) {
            image.putExtra(IMAGE_SCALED_TO_BACKGROUND, true);
            if (imageWidth > imageHeight) {
                int shift = canvasHeight - imageHeight * canvasWidth / imageWidth;
                rect = new RectF(0, shift / 2, canvasWidth, canvasHeight - shift / 2);
            } else {
                int shift = canvasWidth - imageWidth * canvasHeight / imageHeight;
                rect = new RectF(shift / 2, 0, canvasWidth - shift / 2, canvasHeight);
            }
        } else {
            image.putExtra(IMAGE_SCALED_TO_BACKGROUND, false);

            rect = new RectF((canvasWidth - imageWidth) / 2, (canvasHeight - imageHeight) / 2,
                    (canvasWidth - imageWidth) / 2 + imageWidth, (canvasHeight - imageHeight) / 2 + imageHeight);
        }
        image.setRect(rect);
        if (compression) {
            final File imageFile = compressBitmap(bitmap);
            image.setImagePath(imageFile.getAbsolutePath());
        } else {
            image.setImageBitmap(bitmap);
        }
        return image;
    }

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESS_QUALITY = 100;

    private File compressBitmap(final Bitmap bitmap) throws IOException {
        File file = new File(freContext.getActivity().getExternalFilesDir(null), ".temp");
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, output);
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return file;
    }

    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {
                Bitmap bitmap1 = sCanvasView.getBitmap(true);
//                Log.e(TAG, bitmap1.getHeight()+"--"+rect.top)
                int x = (int) rect.left;
                if(x < 0)
                    x = 0;
                Bitmap finalBitmap = Bitmap.createBitmap(bitmap1, x, (int) rect.top, bitmapWdith, bitmapHeight);
                if(sourceName.equals("drawable.snowman"))
                    finalBitmap = Bitmap.createScaledBitmap(finalBitmap, bitmapWdith/4, bitmapHeight/4, false);
                saveIamge(finalBitmap);
                bitmap1.recycle();
                finalBitmap.recycle();
                if(mUndoBtn.isEnabled())
                    freContext.dispatchStatusEventAsync("105", "true");
                else
                    freContext.dispatchStatusEventAsync("105", "false");
                finish();
//                preView.setVisibility(View.GONE);
//                context.getActivity().setContentView(preView);
//                drawView.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };


    private void saveIamge(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/drawed_images");
        myDir.mkdirs();
        String fname = sourceName + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private View.OnClickListener undoNredoBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mUndoBtn)) {
                sCanvasView.undo();
            } else if (v.equals(mRedoBtn)) {
                sCanvasView.redo();
            }
            mUndoBtn.setEnabled(sCanvasView.isUndoable());
            mRedoBtn.setEnabled(sCanvasView.isRedoable());
        }
    };

    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int nBtnID = v.getId();
            Log.e(TAG, Integer.toString(nBtnID) + "-" + Integer.toString(v.getId()));
            // If the mode is not changed, open the setting view. If the mode is same, close the setting view.
            if (nBtnID == mPenBtn.getId()) {
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
                    try {
                        sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                        sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
                    updateModeState();
                }
            } else if (nBtnID == mEraserBtn.getId()) {
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
                    sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                    sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
                    updateModeState();
                }
            } else if (nBtnID == mFillingBtn.getId()) {
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
                    sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
                    sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
                    updateModeState();
//                    Toast.makeText(freContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private View.OnLongClickListener mBtnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            int nBtnID = v.getId();
            // If the mode is not changed, open the setting view. If the mode is same, close the setting view.
            if (nBtnID == mPenBtn.getId()) {
                sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
                    sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
                    updateModeState();
                }
                return true;
            } else if (nBtnID == mEraserBtn.getId()) {
                sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
                    sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
                    updateModeState();
                }
                return true;
            } else if (nBtnID == mFillingBtn.getId()) {
                sCanvasView.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
                if (sCanvasView.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
                    sCanvasView.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
                } else {
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
                    sCanvasView.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, true);
                    updateModeState();
//                    Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            return false;
        }
    };

//    // insert image
//    private OnClickListener mInsertBtnClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v.equals(mInsertBtn)) {
//                callGalleryForInputImage(REQUEST_CODE_INSERT_IMAGE_OBJECT);
//            }
//        }
//    };                                            ø

    // color picker mode
    private View.OnClickListener mColorPickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mColorPickerBtn)) {
                // Toggle
                boolean bIsColorPickerMode = !sCanvasView.isColorPickerMode();
                sCanvasView.setColorPickerMode(bIsColorPickerMode);
                mColorPickerBtn.setSelected(bIsColorPickerMode);
            }
        }
    };

    // Update tool button
    private void updateModeState() {
        int nCurMode = sCanvasView.getCanvasMode();
        mPenBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
        mEraserBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
//        mFillingBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);

        // Reset color picker tool when Eraser Mode
//        if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER)
//            sCanvasView.setColorPickerMode(false);
//        mColorPickerBtn.setEnabled(nCurMode != SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
//        mColorPickerBtn.setSelected(sCanvasView.isColorPickerMode());
    }

}
