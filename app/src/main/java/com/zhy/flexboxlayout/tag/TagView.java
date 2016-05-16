package com.zhy.flexboxlayout.tag;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class TagView extends FrameLayout implements Checkable
{
    private boolean isChecked;
    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};

    public TagView(Context context)
    {
        super(context);
    }

    public static View unwarp(View view)
    {
        if (view == null)
            throw new IllegalArgumentException("view can not be null .");
        if (view instanceof TagView)
        {
            return ((TagView) view).getChildAt(0);
        } else
        {
            return view;
        }
    }


    public static TagView wrap(Context context, View view)
    {
        if (view == null)
            throw new IllegalArgumentException("view can not be null .");
        view.setDuplicateParentStateEnabled(true);
        TagView tagView = new TagView(context);
        if (view.getLayoutParams() != null)
        {
            tagView.setLayoutParams(view.getLayoutParams());
        } else
        {
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int defaultMargin = DimenUtils.dip2px(context, 4);
            lp.setMargins(defaultMargin,
                    defaultMargin,
                    defaultMargin,
                    defaultMargin);
            tagView.setLayoutParams(lp);
        }
        tagView.addView(view);

        return tagView;
    }


    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }


    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override
    public void setChecked(boolean checked)
    {
        if (this.isChecked != checked)
        {
            this.isChecked = checked;
            refreshDrawableState();
        }
    }

    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked()
    {
        return isChecked;
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle()
    {
        setChecked(!isChecked);
    }


}