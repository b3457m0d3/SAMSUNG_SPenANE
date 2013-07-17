/*
 * Copyright (C) <year> <copyright holders>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;
import com.pamakids.spen.SPenContext;

public class SPen implements FREExtension {
    public static final String TAG = "SPen";

    public static void notifyVolumeChange() {
//            String volume = String.valueOf(flashVolume);
//            String eventName = "volumeChanged";
//            extensionContext.dispatchStatusEventAsync(eventName, volume);
    }

    @Override
    public FREContext createContext(String contextType) {
        return new SPenContext();
    }

    @Override
    public void dispose() {
        Log.d(TAG, "Extension disposed.");
    }

    @Override
    public void initialize() {
        Log.d(TAG, "Extension initialized.");
    }
}
