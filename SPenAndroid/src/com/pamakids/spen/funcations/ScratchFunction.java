package com.pamakids.spen.funcations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adobe.fre.*;
import com.pamakids.spen.SPenContext;
import com.pamakids.spen.vo.MotionVO;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-22
 * Time: AM12:31
 * To change this template use File | Settings | File Templates.
 */
public class ScratchFunction implements FREFunction {

    public static final String TAG = "ScratchFunction";

//    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    @Override
    public FREObject call(final FREContext context, FREObject[] args){

        final MotionVO vo = new MotionVO();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final SCanvasView canvasView = new SCanvasView(context.getActivity().getBaseContext());


        SPenContext sPenContext = (SPenContext) context;
        sPenContext.canvasView = canvasView;


        ViewGroup layoutView = null;
        layoutView = (ViewGroup) context.getActivity().getWindow().getDecorView();
        sPenContext.layoutView = layoutView;


        layoutView.addView(canvasView);

        String bg = "";
        try {
             bg = args[0].getAsBool() ? "drawable.startbg_day" : "drawable.startbg_night";
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }



        final Bitmap bitmap = BitmapFactory.decodeResource(context.getActivity().getResources(), context.getResourceId(bg));
        sPenContext.scratchBitmap = bitmap;

        canvasView.setSCanvasInitializeListener(new SCanvasInitializeListener() {
            @Override
            public void onInitialized() {
                try {
                    Log.e(TAG, "Canvas Initialized!");
                    canvasView.setCanvasZoomEnable(false);
                    canvasView.setHistoricalOperationSupport(false);
                    canvasView.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
                    canvasView.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);
//                    canvasView.setClearImageBitmap(bitmap);

                    context.dispatchStatusEventAsync("107", "true");
//                    canvasView.setVisibility(View.INVISIBLE);



                }catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }
        });


        canvasView.setSPenTouchListener(new SPenTouchListener() {
            @Override
            public boolean onTouchPen(View view, MotionEvent motionEvent) {
                fillVo(motionEvent, vo);
                Log.e(TAG, vo.toString());
                context.dispatchStatusEventAsync(Integer.toString(motionEvent.getAction()), vo.toString());
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean onTouchPenEraser(View view, MotionEvent motionEvent) {
                fillVo(motionEvent, vo);
                context.dispatchStatusEventAsync(Integer.toString(motionEvent.getAction()), vo.toString());
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean onTouchFinger(View view, MotionEvent motionEvent) {
                fillVo(motionEvent, vo);
                context.dispatchStatusEventAsync(Integer.toString(motionEvent.getAction()), vo.toString());
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTouchButtonDown(View view, MotionEvent motionEvent) {
                fillVo(motionEvent, vo);
                context.dispatchStatusEventAsync("103", vo.toString());
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTouchButtonUp(View view, MotionEvent motionEvent) {
                fillVo(motionEvent, vo);
                context.dispatchStatusEventAsync("104", vo.toString());
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        return null;
    }

    private void fillVo(MotionEvent motionEvent, MotionVO vo) {
        vo.action = motionEvent.getAction();
        vo.pressure = motionEvent.getPressure();
        vo.x = motionEvent.getX();
        vo.y = motionEvent.getY();
    }

}
