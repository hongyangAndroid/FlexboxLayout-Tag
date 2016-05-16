package com.zhy.flexboxlayout.tag;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhy on 16/5/16.
 */
public abstract class TagAdapter<T>
{
    private List<T> mDatas;

    public TagAdapter(List<T> datas)
    {
        if (datas == null)
            datas = new ArrayList<>(0);
        mDatas = datas;
    }

    public TagAdapter(T[] datas)
    {
        mDatas = new ArrayList<>(Arrays.asList(datas));
    }

    protected abstract View getView(ViewGroup parent, int position, T t);

    /**
     * 传入position，如果返回true则认为默认选中
     *
     * @param position
     * @return
     */
    protected boolean select(int position)
    {
        return false;
    }

    /**
     * 未选中->选中，可以在这里设置样式
     */
    protected void onSelect(ViewGroup parent, View view, int position)
    {
    }

    /**
     * 选中->未选中，可以在这里设置样式
     */
    protected void onUnSelect(ViewGroup parent, View view, int position)
    {
    }


    protected boolean enabled(int position)
    {
        return true;
    }


    protected T getItem(int position)
    {
        return mDatas.get(position);
    }

    protected int getCount()
    {
        return mDatas.size();
    }

    public void notifyDataSetChanged()
    {
        if (mOnAdapterDataChanged != null)
        {
            mOnAdapterDataChanged.onChange();
        }
    }

    private OnAdapterDataChanged mOnAdapterDataChanged;

    void setOnAdapterDataChanged(OnAdapterDataChanged onAdapterDataChanged)
    {
        mOnAdapterDataChanged = onAdapterDataChanged;
    }

    interface OnAdapterDataChanged
    {
        void onChange();
    }

}
