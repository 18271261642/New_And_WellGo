package com.truescend.gofit.pagers.base.adapter;

/**
 * 作者:东芝(2017/11/17).
 * 功能:
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者:东芝 2015/12/24.
 * RecyclerView的ViewHolder
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private BaseViewHolder viewHolder;

    public ViewHolder(View itemView) {
        super(itemView);
        viewHolder = BaseViewHolder.getViewHolder(itemView);
        viewHolder.setViewHolder(this);
    }

    public BaseViewHolder getViewHolder() {
        return viewHolder;
    }


    public static class BaseViewHolder {
        private SparseArray<View> viewSparseArray;
        private View view;
        private ViewHolder viewHolder;

        public static BaseViewHolder getViewHolder(View view) {
            BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new BaseViewHolder(view);
                view.setTag(viewHolder);
            }
            return viewHolder;
        }

        private BaseViewHolder(View view) {
            this.view = view;
            viewSparseArray = new SparseArray<View>();
            view.setTag(viewSparseArray);
        }

        public <T extends View> T getView(int id) {
            View childView = viewSparseArray.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewSparseArray.put(id, childView);
            }
            return (T) childView;
        }

        public View getConvertView() {
            return view;
        }

        public TextView getTextView(int id) {
            return getView(id);
        }

        public TextView setTextView(int id, CharSequence charSequence) {
            TextView textView = getTextView(id);
            if (charSequence != null) {
                textView.setText(charSequence);
            }
            return textView;
        }

        public TextView setTextView(int id, int resid) {
            TextView textView = getTextView(id);
            textView.setText(resid);
            return textView;
        }

        public Button getButton(int id) {
            return getView(id);
        }

        public ImageView getImageView(int id) {
            return getView(id);
        }

        public ImageView setImageView(int id, int resId) {
            ImageView imageView = getImageView(id);
            imageView.setImageResource(resId);
            return imageView;
        }

        public ImageView setImageView(int id, Bitmap bitmap) {
            ImageView imageView = getImageView(id);
            imageView.setImageBitmap(bitmap);
            return imageView;
        }


        public ImageView setImageView(int id, Drawable drawable) {
            ImageView imageView = getImageView(id);
            imageView.setImageDrawable(drawable);
            return imageView;
        }

        public ViewHolder getViewHolder() {
            return viewHolder;
        }

        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }
}