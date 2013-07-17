package com.pamakids.spen.vo;

import com.google.gson.Gson;

/**
 * Created with IntelliJ IDEA.
 * User: mani
 * Date: 13-1-21
 * Time: AM10:21
 * To change this template use File | Settings | File Templates.
 */
public class MotionVO {

    public float x;
    public float y;
    public float pressure;
    public int action;
    public boolean onTouchPen;

    @Override
    public String toString()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
