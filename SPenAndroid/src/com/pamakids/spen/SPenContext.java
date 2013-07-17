package com.pamakids.spen;/*
 * Copyright (C) <year> <copyright holders>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.pamakids.spen.funcations.*;
import com.samsung.spensdk.SCanvasView;

import java.util.HashMap;
import java.util.Map;

public class SPenContext extends FREContext {
    public static final String TAG = "SPenContext";

    public SCanvasView canvasView;
    public ViewGroup layoutView;
    public Bitmap scratchBitmap;

    @Override
    public void dispose() {
        try{
            if(canvasView != null){

//                canvasView.setVisibility(0);
//                ((ViewGroup)(canvasView.getParent())).removeView(canvasView);
                canvasView.setVisibility(View.GONE);
//                canvasView = null;
            }
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }

        Log.d(TAG, "Context disposed.");
    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();

        functions.put("init", new InitFunction());
        functions.put("draw", new DrawFunction());
        functions.put("initScratch", new ScratchFunction());
        functions.put("hideScratch", new HideScratchFunction());
        functions.put("showScratch", new ShowScratchFunction());
//        functions.put("removeScratch", );

        return functions;
    }
}
