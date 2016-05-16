package com.zhy.flexboxlayout.tag;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhy on 16/5/16.
 */
public class TagFlowLayout extends FlexboxLayout implements TagAdapter.OnAdapterDataChanged
{
    private TagAdapter mAdapter;
    /**
     * 如果为false，则checkable无效
     */
    private boolean mCheckable = true;

    private Set<Integer> mSelectedItem = new HashSet<Integer>();
    private Set<Integer> mPreSelectedItem = new HashSet<Integer>();


    public TagFlowLayout(Context context)
    {
        super(context);
        init();
    }

    public TagFlowLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
        setFlexWrap(FLEX_WRAP_WRAP);

    }

    public void setCheckable(boolean isCheckable)
    {
        mCheckable = isCheckable;
    }

    public void setAdapter(TagAdapter adapter)
    {
        mAdapter = adapter;
        adapterChanged();
    }

    private void clearForDataChanged()
    {
        removeAllViews();
        mSelectedItem.clear();
    }


    private void adapterChanged()
    {
        TagAdapter adapter = mAdapter;

        if (adapter == null)
        {
            clearForDataChanged();
            return;
        }
        adapter.setOnAdapterDataChanged(this);
        dataChanged();
    }

    private void dataChanged()
    {
        clearForDataChanged();
        TagAdapter adapter = mAdapter;
        if (adapter == null) return;
        for (int i = 0, n = adapter.getCount(); i < n; i++)
        {
            View view = adapter.getView(this, i, adapter.getItem(i));
            TagView tagView = null;
            if (!(view instanceof TagView) && mCheckable)
            {
                tagView = TagView.wrap(getContext(), view);
                addView(tagView);
                viewClickableSet(tagView, i);

                if (adapter.select(i))
                {
                    mPreSelectedItem.add(i);
                    tagView.setChecked(true);
                }

            } else
            {
                addView(view);
                viewClickableSet(view, i);

                if (adapter.select(i))
                {
                    mPreSelectedItem.add(i);
                    adapter.onSelect(this, view, i);
                }
            }

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mPreSelectedItem.size() > 0)
        {
            mSelectedItem.addAll(mPreSelectedItem);
            mPreSelectedItem.clear();
        }

    }

    private void viewClickableSet(final View view, final int position)
    {

        if (!mAdapter.enabled(position)) return;

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doSupportCheckable(v, position);

                if (mOnTagClickListener != null)
                {
                    mOnTagClickListener.onTagClick(TagFlowLayout.this, TagView.unwarp(v), position);
                }

                doCustomSelect(v, position);
            }
        });
    }

    private void doCustomSelect(View v, int position)
    {
        if (!mCheckable)
        {
            if (mSelectedItem.contains(position))
            {
                mSelectedItem.remove(position);
                mAdapter.onUnSelect(TagFlowLayout.this, v, position);
            } else
            {
                mSelectedItem.add(position);
                mAdapter.onSelect(TagFlowLayout.this, v, position);
            }
        }
    }

    private void doSupportCheckable(View v, int position)
    {
        if ((v instanceof TagView) && mCheckable)
        {
            TagView tagView = ((TagView) v);
            boolean checked = tagView.isChecked();
            if (checked)
            {
                tagView.setChecked(false);
                mSelectedItem.remove(position);
            } else
            {
                tagView.setChecked(true);
                mSelectedItem.add(position);
            }

        }
    }

    @Override
    public void onChange()
    {
        dataChanged();
    }

    private OnTagClickListener mOnTagClickListener;

    public interface OnTagClickListener
    {
        void onTagClick(ViewGroup parent, View view, int position);
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener)
    {
        mOnTagClickListener = onTagClickListener;
    }


    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_CHECKABLE = "key_checkable";
    private static final String KEY_DEFAULT = "key_default";


    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        String selectPos = "";
        if (mSelectedItem.size() > 0)
        {
            for (int key : mSelectedItem)
            {
                selectPos += key + "|";
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        bundle.putBoolean(KEY_CHECKABLE, mCheckable);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (mAdapter == null)
        {
            super.onRestoreInstanceState(state);
            return;
        }

        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            mCheckable = bundle.getBoolean(KEY_CHECKABLE, true);
            if (!TextUtils.isEmpty(mSelectPos))
            {
                String[] split = mSelectPos.split("\\|");
                for (String pos : split)
                {
                    int index = Integer.parseInt(pos);
                    mSelectedItem.add(index);
                }

                if (mSelectedItem.size() > 0)
                {
                    for (int i : mPreSelectedItem)
                    {
                        if (!mCheckable)
                            mAdapter.onUnSelect(this, getChildAt(i), i);
                        else
                            ((TagView) getChildAt(i)).setChecked(false);
                    }
                    mPreSelectedItem.clear();

                } else
                {
                    mSelectedItem.addAll(mPreSelectedItem);
                    mPreSelectedItem.clear();
                }

                for (int index : mSelectedItem)
                {
                    if (mCheckable)
                    {
                        TagView tagView = (TagView) getChildAt(index);
                        if (tagView != null)
                            tagView.setChecked(true);
                    } else
                    {
                        mAdapter.onSelect(this, getChildAt(index), index);
                    }
                }


            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }


}
