package com.pamakids.spen.funcations;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.pamakids.spen.vo.MotionVO;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenDetachmentListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-18
 * Time: PM5:36
 * To change this template use File | Settings | File Templates.
 */
public class InitFunction implements FREFunction {

    public static final String TAG = "InitFunction";

    @Override
    public FREObject call(final FREContext context, FREObject[] args) {

        Log.e(TAG, "Star"+Runtime.getRuntime().maxMemory());

        final MotionVO vo = new MotionVO();

        final SPenEventLibrary sPenEventLibrary = new SPenEventLibrary();
        View view = context.getActivity().getCurrentFocus();
//        context.dispatchStatusEventAsync(TAG, view.toString());
//        Log.e(TAG, view.toString());


        try {
            sPenEventLibrary.setSPenTouchListener(view, new SPenTouchListener() {

                @Override
                public boolean onTouchPen(View view, MotionEvent motionEvent) {
                    fillVo(motionEvent, vo, true);
                    context.dispatchStatusEventAsync("106", vo.toString());
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
                    fillVo(motionEvent, vo, false);
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

            sPenEventLibrary.setSPenHoverListener(view, new SPenHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    fillVo(motionEvent, vo, true);
                    context.dispatchStatusEventAsync(Integer.toString(motionEvent.getAction()), vo.toString());
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onHoverButtonDown(View view, MotionEvent motionEvent) {
                    fillVo(motionEvent, vo);
                    context.dispatchStatusEventAsync("101", vo.toString());
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onHoverButtonUp(View view, MotionEvent motionEvent) {
                    fillVo(motionEvent, vo);
                    context.dispatchStatusEventAsync("102", vo.toString());
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });

            sPenEventLibrary.registerSPenDetachmentListener(context.getActivity().getApplicationContext(), new SPenDetachmentListener() {
                @Override
                public void onSPenDetached(boolean b) {
                    context.dispatchStatusEventAsync("100", Boolean.toString(b));
                }
            });

        } catch (Exception e) {
            context.dispatchStatusEventAsync(TAG, e.toString());
        }

        return null;
    }

    private void fillVo(MotionEvent motionEvent, MotionVO vo, Boolean ...args) {
        vo.action = motionEvent.getAction();
        vo.pressure = motionEvent.getPressure();
        vo.x = motionEvent.getX();
        vo.y = motionEvent.getY();
        vo.onTouchPen = args.length > 0 ? args[0].booleanValue() : false;
    }

}
