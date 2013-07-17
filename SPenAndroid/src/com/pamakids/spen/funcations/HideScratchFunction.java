package com.pamakids.spen.funcations;

import android.view.View;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.pamakids.spen.SPenContext;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-25
 * Time: PM12:53
 * To change this template use File | Settings | File Templates.
 */
public class HideScratchFunction implements FREFunction {

    @Override
    public FREObject call(FREContext context, FREObject[] args){

        SPenContext sPenContext = (SPenContext) context;
//        sPenContext.canvasView.getParent()
//        sPenContext.canvasView.setVisibility(View.GONE);
//        sPenContext.canvasView = null;
        if(sPenContext.canvasView != null){
            sPenContext.layoutView.removeView(sPenContext.canvasView);
            sPenContext.canvasView = null;
//            sPenContext.canvasView.setVisibility(View.INVISIBLE);
        }

        return null;
    }

}
