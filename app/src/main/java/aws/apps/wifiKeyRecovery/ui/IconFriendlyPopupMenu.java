package aws.apps.wifiKeyRecovery.ui;

import android.content.Context;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class IconFriendlyPopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    private Context mContext;
    private MenuBuilder mMenu;
    private View mAnchor;
    private MenuPopupHelper mPopup;
    private OnMenuItemClickListener mMenuItemClickListener;
    private OnDismissListener mDismissListener;

    public IconFriendlyPopupMenu(Context context, View anchor) {
        this(context, anchor, false);
    }

    public IconFriendlyPopupMenu(Context context, View anchor, boolean showIcons) {
        this.mContext = context;
        this.mMenu = new MenuBuilder(context);
        this.mMenu.setCallback(this);
        this.mAnchor = anchor;
        this.mPopup = new MenuPopupHelper(context, this.mMenu, anchor);
        this.mPopup.setCallback(this);
        mPopup.setForceShowIcon(showIcons);
    }

    public void dismiss() {
        this.mPopup.dismiss();
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContext);
    }

    public void inflate(int menuRes) {
        getMenuInflater().inflate(menuRes, this.mMenu);
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (this.mDismissListener != null)
            this.mDismissListener.onDismiss(this);
    }

    public void onCloseSubMenu(SubMenuBuilder menu) {
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        if (this.mMenuItemClickListener != null) {
            return this.mMenuItemClickListener.onMenuItemClick(item);
        }
        return false;
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {
    }

    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        if (subMenu == null) return false;

        if (!subMenu.hasVisibleItems()) {
            return true;
        }

        new MenuPopupHelper(this.mContext, subMenu, this.mAnchor).show();
        return true;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mMenuItemClickListener = listener;
    }

    public void show() {
        this.mPopup.show();
    }

    public static abstract interface OnDismissListener {
        public abstract void onDismiss(IconFriendlyPopupMenu paramPopupMenu);
    }

    public static abstract interface OnMenuItemClickListener {
        public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
    }
}
