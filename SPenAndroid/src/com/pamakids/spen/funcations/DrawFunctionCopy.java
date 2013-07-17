package com.pamakids.spen.funcations;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.adobe.fre.*;
import com.pamakids.spen.utils.SPenSDKUtils;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.*;

import java.io.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-20
 * Time: PM11:16
 * To change this template use File | Settings | File Templates.
 */
public class DrawFunctionCopy implements FREFunction {

    public static final String TAG = "DrawFunction";

    //==============================
    // Variables
    //==============================
    Context mContext = null;

    private SCanvasView sCanvasView;

    private FrameLayout mLayoutContainer;
    private RelativeLayout mCanvasContainer;
    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView mFillingBtn;
    private ImageView mColorPickerBtn;
    private ImageView mUndoBtn;
    private ImageView mRedoBtn;
    private ImageView mOKBtn;
    private Bitmap bitmap = null;

    private FREContext context;
    private View preView = null;

    ViewGroup layoutView = null;
    View drawView = null;

    @Override
    public FREObject call(final FREContext freContext, final FREObject[] args) {

        Log.e(TAG, "Called Draw Function");

        SPenSDKUtils.freContext = freContext;
        context = freContext;

        final Activity activity = freContext.getActivity();
        preView = freContext.getActivity().getWindow().getDecorView();

        layoutView = (ViewGroup) preView;
        drawView = freContext.getActivity().getLayoutInflater().inflate(freContext.getResourceId("layout.drawing_board"), layoutView, false);
        layoutView.addView(drawView);

        mContext = activity.getApplicationContext();

        //------------------------------------
        // UI Setting
        //------------------------------------
        mPenBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.penBtn"));
        mPenBtn.setOnClickListener(mBtnClickListener);
        mPenBtn.setOnLongClickListener(mBtnLongClickListener);
        mEraserBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.eraseBtn"));
        mEraserBtn.setOnClickListener(mBtnClickListener);
        mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
//        mTextBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.textBtn"));
//        mTextBtn.setOnClickListener(mBtnClickListener);
//        mTextBtn.setOnLongClickListener(mBtnLongClickListener);
        mFillingBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.fillingBtn"));
        mFillingBtn.setOnClickListener(mBtnClickListener);
        mFillingBtn.setOnLongClickListener(mBtnLongClickListener);
//        mInsertBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.insertBtn"));
//        mInsertBtn.setOnClickListener(mInsertBtnClickListener);
        mColorPickerBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.colorPickerBtn"));
        mColorPickerBtn.setOnClickListener(mColorPickerListener);

        mOKBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.okBtn"));
        mOKBtn.setOnClickListener(okListener);

        mUndoBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.undoBtn"));
        mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
        mRedoBtn = (ImageView) activity.findViewById(freContext.getResourceId("id.redoBtn"));
        mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

        mLayoutContainer = (FrameLayout) activity.findViewById(freContext.getResourceId("id.layout_container"));
        mCanvasContainer = (RelativeLayout) activity.findViewById(freContext.getResourceId("id.canvas_container"));
        String name = "";

        try {
            name = "drawable." + args[0].getAsString();
            Log.e(TAG, name);
            bitmap = BitmapFactory.decodeResource(activity.getResources(), context.getResourceId(name));
        } catch (FRETypeMismatchException e) {
            Log.e(TAG, e.toString());
        } catch (FREInvalidObjectException e) {
            Log.e(TAG, e.toString());
        } catch (FREWrongThreadException e) {
            Log.e(TAG, e.toString());
        }
        sCanvasView = (SCanvasView) activity.findViewById(freContext.getResourceId("id.scanvasView"));
        sCanvasView.setSCanvasInitializeListener(new SCanvasInitializeListener() {
            @Override
            public void onInitialized() {
                try {
                    Log.e(TAG, "Canvas Initialized");
                    sCanvasView.setCanvasZoomEnable(false);
                    sCanvasView.setBackgroundColor(Color.parseColor("#ffffffff"));
                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_DEFAULT);
//                    sCanvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
//                    sCanvasView.setBackground();
//                    FREBitmapData bitmapData = (FREBitmapData) args[0];
//                    bitmapData.acquire();
//                    Bitmap.Config config = Bitmap.Config.ARGB_8888;
//                    Bitmap bitmap = Bitmap.createBitmap(bitmapData.getWidth(), bitmapData.getHeight(), config);
//                    bitmap.copyPixelsFromBuffer(bitmapData.getBits());
//                    sCanvasView.setBackgroundImage(bitmap);
//                    bitmapData.release();
//                    if(bitmap != null)
//                      sCanvasView.setBackgroundImage(bitmap);
                    final SObjectImage sObjectImage = getImageFromBitmap(bitmap, false, false);
//                    sObjectImage.setImageBitmap(bitmap);
//                    sCanvasView.getLayoutParams().width = bitmap.getWidth();
//                    sCanvasView.getLayoutParams().height = bitmap.getHeight();
                    Log.e(TAG, bitmap.getWidth() + ":" + bitmap.getHeight());

                    sCanvasView.createSCanvasView(bitmap.getWidth(), bitmap.getHeight());


                    Boolean insertResult = sCanvasView.insertSAMMImage(sObjectImage, false);
                    Log.e(TAG, bitmap.getWidth() + ":" + bitmap.getHeight() + "-" + insertResult);

                    bitmap.recycle();

                    updateModeState();
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

        return null;
    }

    private static final String IMAGE_SCALED_TO_BACKGROUND = "isBackground";

    private SObjectImage getImageFromBitmap(final Bitmap bitmap, final boolean compression, final boolean scaleToCanvas)
            throws IOException {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        final SObjectImage image = new SObjectImage();
        RectF rect;
        int canvasWidth = sCanvasView.getWidth();
        int canvasHeight = sCanvasView.getHeight();
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
        File file = new File(context.getActivity().getExternalFilesDir(null), ".temp");
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

    private OnClickListener okListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            try {
                layoutView.removeView(drawView);
                drawView = null;
                Log.e(TAG, "OK");
                context.dispatchStatusEventAsync("105", "true");

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };

    private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
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

    private OnClickListener mBtnClickListener = new OnClickListener() {
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
                    Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private OnLongClickListener mBtnLongClickListener = new OnLongClickListener() {
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
                    Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            return false;
        }
    };

    // color picker mode
    private OnClickListener mColorPickerListener = new OnClickListener() {
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
        mFillingBtn.setSelected(nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);

        // Reset color picker tool when Eraser Mode
        if (nCurMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER)
            sCanvasView.setColorPickerMode(false);
        mColorPickerBtn.setEnabled(nCurMode != SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
        mColorPickerBtn.setSelected(sCanvasView.isColorPickerMode());
    }
}
