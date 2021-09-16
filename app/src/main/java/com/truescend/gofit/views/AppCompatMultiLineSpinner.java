package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.truescend.gofit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/6/26).
 * 功能:支持item内容/下拉item内容 换行的Spinner
 */

public class AppCompatMultiLineSpinner extends AppCompatSpinner {

    public AppCompatMultiLineSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppCompatMultiLineSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AppCompatMultiLineSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init();
    }

    public AppCompatMultiLineSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
        init();
    }


    private void init() {
        SpinnerAdapter adapter = getAdapter();
        if (adapter != null) {
            List<CharSequence> list = new ArrayList<>();
            int count = adapter.getCount();
            if(count>0) {
                for (int i = 0; i < count; i++) {
                    list.add((CharSequence) adapter.getItem(i));
                }
                MyArrayAdapter adapter_new = new MyArrayAdapter(getContext(), R.layout.spinner_dropdown_item, list);
                setAdapter(adapter_new);
            }
        }
    }


    public void setItems(List<CharSequence> items){
        MyArrayAdapter adapter_new = new MyArrayAdapter(getContext(), R.layout.spinner_dropdown_item, items);
        setAdapter(adapter_new);
    }


    static class MyArrayAdapter extends ArrayAdapter<CharSequence> {


        private final LayoutInflater inflater;

        MyArrayAdapter(@NonNull Context context, int resource, @NonNull List<CharSequence> objects) {
            super(context, resource, objects);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        static class ViewHolder {
            TextView itemText;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getViewInternal(position, convertView, parent, false);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getViewInternal(position, convertView, parent, true);
        }

        private View getViewInternal(int position, View convertView, ViewGroup parent, boolean isDropdownView) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_dropdown_item, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.itemText = (TextView) view.findViewById(R.id.itemText);
                view.setTag(viewHolder);
            }
//            if (isDropdownView) {
//
//            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.itemText.setText(getItem(position));
            return view;
        }


//
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            final View view = super.getView(position, convertView, parent);
//            final TextView textView = view.findViewById(android.R.id.text1);
//            textView.post(new Runnable() {
//                @Override
//                public void run() {
//                    int height_tmp = textView.getHeight();
//                    if (height_tmp > height) {
//                        height = height_tmp;
//                        LayoutParams layoutParams = getLayoutParams();
//                        layoutParams.height = height +Math.round(height*0.5f) ;
//                        setLayoutParams(layoutParams);
//                        view.setLayoutParams(layoutParams);
//                    }
//
//                }
//            });
//            return view;
//        }
//
//        @Override
//        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            View view = super.getDropDownView(position, convertView, parent);
//            final TextView text1 = view.findViewById(android.R.id.text1);
//            text1.setSingleLine(false);
//            text1.setText(getItem(position));
//            text1.setTag(view);
//            text1.post(new Runnable() {
//                @Override
//                public void run() {
//                    text1.setSingleLine(false);
//
//                    requestLayout();
//                }
//            });
//            return view;
//        }
    }

}
