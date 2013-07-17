package com.pamakids.spen.funcations;

import android.util.Log;
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
public class ShowScratchFunction implements FREFunction {

    @Override
    public FREObject call(FREContext context, FREObject[] args) {

        Log.e("Show", "Show222");

        try {
            SPenContext sPenContext = (SPenContext) context;

//            sPenContext.canvasView.setVisibility(View.INVISIBLE);
//            sPenContext.canvasView.setVisibility(View.VISIBLE);
//            sPenContext.getActivity().setVisible(false);
//            sPenContext.layoutView.in
            sPenContext.canvasView.setClearImageBitmap(sPenContext.scratchBitmap);
        }catch (Exception e) {
            Log.e("Show", e.toString());
        }

        return null;
    }

}
