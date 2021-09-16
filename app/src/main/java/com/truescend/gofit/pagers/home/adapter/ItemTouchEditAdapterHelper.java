package com.truescend.gofit.pagers.home.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.GridLayoutManagerFix;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者:东芝(2018/12/21).
 * 功能:ilDeviceWallpaperTempLeft
 */

public class ItemTouchEditAdapterHelper {

    private static final int DURATION = 100;
    private static final float SCALE_MAX = 1.01f;
    private static final float SCALE_MIN = 0.9f;
    private static final float SCALE_NORMAL = 1.0f;
    private final ItemAdapter itemAdapter;
    private final WeakReference<Context> contextWeakReference;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;
    private RecyclerView mRecyclerView;


    public ItemTouchEditAdapterHelper(Context context) {
        this.itemAdapter = new ItemAdapter(context);
        this.contextWeakReference = new WeakReference<>(context);
    }

    public ItemAdapter getAdapter() {
        return itemAdapter;
    }


    public void attachToRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        mRecyclerView.setAdapter(itemAdapter);
        GridLayoutManager manager = new GridLayoutManagerFix(contextWeakReference.get(), 2);
        manager.setSmoothScrollbarEnabled(true);
        manager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);


        itemAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });

        itemAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemLongClickListener != null) {
                    if (itemAdapter.isEditMode()) {
                        return true;
                    }
                    return onItemLongClickListener.onItemLongClick(parent, view, position, id);
                } else {
                    return false;
                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public ViewHolder getItemTypeViewHolder(int position) {
        return (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
    }


    public int getItemTypePosition(int itemType) {
        List<ItemTouchEditAdapterHelper.ItemObject> itemObjectList = itemAdapter.getList();
        if (itemObjectList != null) {
            int size = itemObjectList.size();
            for (int position = 0; position < size; position++) {
                ItemTouchEditAdapterHelper.ItemObject itemObject = itemObjectList.get(position);
                if (itemObject.getItemType() == itemType) {
                    return position;
                }
            }
        }
        return -1;
    }

    public void notifyItemDataChange(int itemType, ItemDataWrapper itemDataWrapper) {
        if (mRecyclerView == null || itemAdapter == null) return;
        List<ItemTouchEditAdapterHelper.ItemObject> itemObjectList = itemAdapter.getList();
        if (itemObjectList == null) return;
        int itemTypePosition = getItemTypePosition(itemType);
        if (itemTypePosition == -1) return;
        ItemTouchEditAdapterHelper.ItemObject itemObject = itemObjectList.get(itemTypePosition);
        if (itemObject.getItemType() == itemType) {

            ItemDataWrapper lastData = itemObject.getData();
            int dataWrapperSize = itemDataWrapper.getSize();

            //防止重复刷新
            if (lastData!=null && lastData.getSize() == dataWrapperSize && dataWrapperSize > 0) {
                boolean isDiff = false;
                for (int i = 0; i < dataWrapperSize; i++) {
                    if (!lastData.getDataIndex(i).equals(itemDataWrapper.getDataIndex(i))) {
                        isDiff = true;
                        break;
                    }
                }
                if (!isDiff) {
                    return;
                }
            }
            itemObject.setData(itemDataWrapper);
            ViewHolder vh = getItemTypeViewHolder(itemTypePosition);
            if (vh == null) {
                return;
            }
            ViewHolder.BaseViewHolder viewHolder = vh.getViewHolder();
            if (viewHolder != null) {
                if (dataWrapperSize == 2) {
                    CharSequence title = itemDataWrapper.getDataIndex(0);
                    CharSequence subTitle = itemDataWrapper.getDataIndex(1);
                    viewHolder.setTextView(R.id.tvHomeCardTitle, title);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle, subTitle);
                }
                if (dataWrapperSize == 4) {
                    CharSequence subTitle1 = itemDataWrapper.getDataIndex(0);
                    CharSequence subTitle2 = itemDataWrapper.getDataIndex(1);
                    CharSequence subTitle3 = itemDataWrapper.getDataIndex(2);
                    @DrawableRes int icon = itemDataWrapper.getDataIndex(3);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle, subTitle1);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle2, subTitle2);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle3, subTitle3);
                    viewHolder.setImageView(R.id.ivHomeCardPicture, icon);
                }
            }
        }
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

        private View itemView;

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
            int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
            if (fromPosition == -1) {
                fromPosition = 0;
            }
            if (toPosition == -1) {
                toPosition = 0;
            }
            if (!itemAdapter.getItem(toPosition).isOpen()) {
                return true;
            }

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(itemAdapter.getList(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(itemAdapter.getList(), i, i - 1);
                }
            }

            itemAdapter.notifyItemMoved(fromPosition, toPosition);
            if (viewHolder.itemView != null) {
                viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return itemAdapter.isCanMove();
        }

        @Override
        public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder, int actionState) {
            //长按进入编辑模式,松开恢复正常模式
//            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                itemView = viewHolder.itemView;
//                itemAdapter.setScale(true);
//                int itemCount = itemAdapter.getItemCount();
//                for (int i = 0; i < itemCount; i++) {
//                    RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForLayoutPosition(i);
//                    if (vh == null || vh.itemView == viewHolder.itemView) {
//                        continue;
//                    }
//                    showItemScale(vh.itemView, DURATION, SCALE_MIN);
//                    if (!itemAdapter.isEditMode()) {
//                        showItemShakeAnimation(vh.itemView);
//                    }
//
//                }
//                showItemScale(itemView, DURATION, SCALE_MAX);
//                if (!itemAdapter.isEditMode()) {
//                     showItemShakeAnimation(itemView);
//                }
//
//            } else {
//                itemAdapter.setScale(false);
//
//                int itemCount = itemAdapter.getItemCount();
//                for (int i = 0; i < itemCount; i++) {
//                    RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForLayoutPosition(i);
//                    if (vh == null) {
//                        continue;
//                    }
//                    if (itemAdapter.isEditMode()) {
//                        showItemScale(vh.itemView, DURATION, SCALE_MIN);
//                        if (!itemAdapter.isEditMode()) {
//                            showItemShakeAnimation(vh.itemView);
//                        }
//                    } else {
//                            showItemScale(vh.itemView, DURATION, SCALE_NORMAL);
//                        if (!itemAdapter.isEditMode()) {
//                            cancelItemSnakeAnimation(vh.itemView);
//                        }
//                    }
//                }
//            }
            //长按进入编辑模式  松开仍然是编辑模式
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                itemView = viewHolder.itemView;
                showItemScale(itemView, DURATION, SCALE_MAX);
            } else if (itemView != null) {
                showItemScale(itemView, DURATION, SCALE_MIN);
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                if (!itemAdapter.isEditMode()) {
                    itemAdapter.setEditMode(true);
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

//        @Override
//        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//            if (mRecyclerView.findChildViewUnder(viewHolder.itemView.getX(), viewHolder.itemView.getY()) != null) {
//                System.out.println(mRecyclerView.findChildViewUnder(viewHolder.itemView.getX(), viewHolder.itemView.getY()).getTag());
//                System.out.println(viewHolder.itemView.getX() + " " +viewHolder.itemView.getY());
//            }
//
//        }
//        @Override
//        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
//            final int direction = (int) Math.signum(viewSizeOutOfBounds);
//            return 10 * direction;
//        }
    });

    public class ItemAdapter extends BaseRecycleViewAdapter<ItemObject> {

        private boolean editMode;
        private boolean canMove;


        ItemAdapter(Context context) {
            super(context);
        }

        public void setEditMode(boolean editMode) {
            this.editMode = editMode;
            notifyDataSetChanged();
        }

        public boolean isCanMove() {
            return canMove;
        }

        public boolean isEditMode() {
            return editMode;
        }

        public List<Integer> getItemTypeOrder() {
            List<Integer> itemTypeOrder = new ArrayList<>();
            if (lists != null) {
                for (ItemObject itemObject : lists) {
                    itemTypeOrder.add(itemObject.getItemType());
                }
            }
            return itemTypeOrder;
        }

        @Override
        public void onItemInflate(final int position, final ItemObject item, final ViewHolder.BaseViewHolder viewHolder, View rootView) {

            final CheckBox cbCardControlButton = viewHolder.getView(R.id.cbCardControlButton);
            ViewCompat.setElevation(cbCardControlButton, 30);
            cbCardControlButton.bringToFront();

            canMove = true;
            View itemView = viewHolder.getViewHolder().itemView;
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (editMode) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                                event.getAction() == MotionEvent.ACTION_MOVE) {
                            canMove = cbCardControlButton.isChecked();
                            if (canMove) {
                                mItemTouchHelper.startDrag(viewHolder.getViewHolder());
                            } else {
                                return true;
                            }

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            canMove = true;
                        }
                    }
                    return false;
                }
            });


            if (editMode/* || showItemScale*/) {
                showItemScale(itemView, DURATION, SCALE_MIN);
                showItemShakeAnimation(itemView);
                showEditButton(cbCardControlButton, 1.0f);
            } else {
                if (itemView.getScaleX() == SCALE_NORMAL && itemView.getScaleY() == SCALE_NORMAL) {
                    itemView.setScaleX(SCALE_MIN);
                    itemView.setScaleY(SCALE_MIN);

                }
                showItemScale(itemView, DURATION, SCALE_NORMAL);
                cancelItemSnakeAnimation(itemView);
                showEditButton(cbCardControlButton, 0f);
            }
            cbCardControlButton.setOnCheckedChangeListener(null);
            cbCardControlButton.setChecked(item.isOpen());
            cbCardControlButton.setTag(viewHolder.getViewHolder());
            cbCardControlButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    int fromPosition = viewHolder.getViewHolder().getAdapterPosition();
                    List<ItemObject> items = getList();
                    int toPosition = getLastButtonOnPosition(items);
                    items.get(fromPosition).setOpen(isChecked);


                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(itemAdapter.getList(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(itemAdapter.getList(), i, i - 1);
                        }
                    }

                    itemAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            });


            ////////////////////////////////////////////////////////////////////////////////////////
            //--------------------------------------adapter逻辑-----------------------------------
            ////////////////////////////////////////////////////////////////////////////////////////

            switch (item.getItemType()) {
                case ItemObject.ITEM_CARD_SLEEP:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_sleep);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.sleep_record);

                    break;
                case ItemObject.ITEM_CARD_CHECK:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_check);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.check_self);

                    break;
                case ItemObject.ITEM_CARD_HEART:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_heart);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.heart_record);
                    break;
                case ItemObject.ITEM_CARD_BLOOD_PRESSURE:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_blood_pressure);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.blood_pressure_record);
                    break;
                case ItemObject.ITEM_CARD_BLOOD_OXYGEN:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_blood_oxygen);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.blood_oxygen_record);
                    break;
                case ItemObject.ITEM_CARD_RANKING:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_card_health_advisor);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.title_health_advisor);
                    break;
                case ItemObject.ITEM_CARD_SPORT_MODE:
                    viewHolder.setImageView(R.id.ivHomeCardPicture, R.mipmap.icon_sport_mode_walking);
                    viewHolder.setTextView(R.id.tvHomeCardType, R.string.title_sport_mode);
                    break;
            }
            ItemDataWrapper itemDataWrapper = item.getData();
            int dataWrapperSize = itemDataWrapper==null?0:itemDataWrapper.getSize();
            if (item.getItemType() == ItemObject.ITEM_CARD_CHECK ||
                    item.getItemType() == ItemObject.ITEM_CARD_RANKING) {
                viewHolder.setTextView(R.id.tvHomeCardTitle, "");
                viewHolder.setTextView(R.id.tvHomeCardSubTitle, "");
            } else if (item.getItemType() == ItemObject.ITEM_CARD_SPORT_MODE) {
                if(dataWrapperSize == 4){
                    CharSequence subTitle1 = itemDataWrapper.getDataIndex(0);
                    CharSequence subTitle2 = itemDataWrapper.getDataIndex(1);
                    CharSequence subTitle3 = itemDataWrapper.getDataIndex(2);
                    @DrawableRes int icon = itemDataWrapper.getDataIndex(3);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle,subTitle1);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle2,subTitle2);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle3,subTitle3);
                    viewHolder.setImageView(R.id.ivHomeCardPicture,icon);
                }
            } else {
                if(dataWrapperSize == 2){
                    CharSequence title = itemDataWrapper.getDataIndex(0);
                    CharSequence subTitle = itemDataWrapper.getDataIndex(1);
                    viewHolder.setTextView(R.id.tvHomeCardTitle, title);
                    viewHolder.setTextView(R.id.tvHomeCardSubTitle, subTitle);
                }
            }

        }

        private int getLastButtonOnPosition(List<ItemObject> items) {
            if (items == null) {
                return 0;
            }
            int size = items.size();
            if (size == 0) {
                return 0;
            }
            for (int i = 0; i < size; i++) {
                ItemObject item = items.get(i);
                if (!item.isOpen()) {
                    return i;
                }
            }
            return size - 1;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemType();
        }

        @Override
        public int initLayout(int viewType) {
            switch (viewType) {
                case ItemObject.ITEM_CARD_SPORT_MODE:
                    return R.layout.item_home_card_sport_mode_control;
                default:
                    return R.layout.item_home_card_control;
            }

        }
    }


    private void showEditButton(final View v, final float to) {

        v.setVisibility(View.GONE);

        ViewPropertyAnimator animator = v.animate().scaleX(to).scaleY(to).setDuration(200);
        animator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (to == 0) {
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (to == 1) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();
    }

    private void cancelItemSnakeAnimation(View view) {
        Object tag = view.getTag(R.id.always);
        if (tag != null && tag instanceof ValueAnimator) {
            ValueAnimator valueAnimator = (ValueAnimator) tag;
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
            valueAnimator.end();
            valueAnimator.cancel();
            view.setRotation(0);
            view.clearAnimation();
        }
    }


    private void showItemShakeAnimation(final View view) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0 - makeUncertaintyValue(), 0 + makeUncertaintyValue());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (itemAdapter.isEditMode()) {
                    view.setRotation(animatedValue);
                } else {
                    view.setRotation(0);
                    animation.removeAllUpdateListeners();
                    animation.removeAllListeners();
                    animation.end();
                    animation.cancel();
                }
            }
        });
        valueAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        valueAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        valueAnimator.setDuration(100);
        valueAnimator.start();
        view.setTag(R.id.always, valueAnimator);

    }

    private synchronized void showItemScale(final View view, int duration, float value) {
        if (view.getScaleX() == value && view.getScaleY() == value) {
            return;
        }
//        ViewPropertyAnimator animate = view.animate();
//        if (animate != null) {
//            animate.setDuration(duration)
//                    .scaleX(value)
//                    .scaleY(value)
//                    .start();
//        }
        view.setScaleX(value);
        view.setScaleY(value);
    }

    private float makeUncertaintyValue() {
        return 0.4f;
    }

    public static class ItemDataWrapper {
        private Object[] dataArray;

        public ItemDataWrapper(Object... dataArray) {
            this.dataArray = dataArray;
        }

        public int getSize() {
            return dataArray == null ? 0 : dataArray.length;
        }

        public <T> T getDataIndex(int index) {
            return (T) dataArray[index];
        }
    }

    public static class ItemObject {
        public final static int ITEM_CARD_SLEEP = 0;
        public final static int ITEM_CARD_CHECK = 1;
        public final static int ITEM_CARD_HEART = 2;
        public final static int ITEM_CARD_BLOOD_PRESSURE = 3;
        public final static int ITEM_CARD_BLOOD_OXYGEN = 4;
        public final static int ITEM_CARD_RANKING = 5;
        public final static int ITEM_CARD_SPORT_MODE = 6;


        private ItemDataWrapper data;
        private boolean open;
        private boolean support;
        private int itemType;

        public ItemObject(int itemType, boolean open, boolean support, ItemDataWrapper data) {
            this.data = data;
            this.open = open;
            this.support = support;
            this.itemType = itemType;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public void setData(ItemDataWrapper data) {
            this.data = data;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public ItemDataWrapper getData() {
            return data;
        }

        public boolean isOpen() {
            return open;
        }

        public boolean isSupport() {
            return support;
        }

        public void setSupport(boolean support) {
            this.support = support;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof ItemObject) {
                return getItemType() == ((ItemObject) obj).getItemType();
            }
            if (obj instanceof Integer) {
                return getItemType() == ((Integer) obj);
            }
            return false;
        }

        @Override
        public String toString() {
            return "ItemObject{" +
                    "data=" + data +
                    ", open=" + open +
                    ", support=" + support +
                    ", itemType=" + itemType +
                    '}';
        }
    }
}
