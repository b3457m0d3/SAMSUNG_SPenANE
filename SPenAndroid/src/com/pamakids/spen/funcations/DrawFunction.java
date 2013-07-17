package com.pamakids.spen.funcations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.*;
import com.adobe.fre.*;
import com.pamakids.spen.activities.DrawActivity;
import com.pamakids.spen.utils.SPenSDKUtils;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SOptionSAMM;
import com.samsung.samm.common.SOptionSCanvas;
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
public class DrawFunction implements FREFunction {

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

//    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    private FREContext context;
    private LinearLayout drawView = null;
    private View preView = null;

    @Override
    public FREObject call(final FREContext freContext, final FREObject[] args) {

        Log.e(TAG, "Called Draw Function 244");

        SPenSDKUtils.freContext = freContext;

        Intent intent = new Intent(freContext.getActivity().getApplicationContext(), DrawActivity.class);
        try {
            intent.putExtra("arg", args[0].getAsString());
        } catch (FRETypeMismatchException e) {
            Log.e(TAG, e.toString());
        } catch (FREInvalidObjectException e) {
            Log.e(TAG, e.toString());
        } catch (FREWrongThreadException e) {
            Log.e(TAG, e.toString());
        }
        freContext.getActivity().startActivity(intent);


        return null;
    }

}
