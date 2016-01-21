package aws.apps.wifiKeyRecovery.ui;

import android.content.Context;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class IconFriendlyPopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    private final Context mContext;
    private final MenuBuilder mMenu;
    private final View mAnchor;
    private final MenuPopupHelper mPopup;
    private OnMenuItemClickListener mMenuItemClickListener;
    private OnDismissListener mDismissListener;

    public IconFriendlyPopupMenu(final Context context, final View anchor) {
        this(context, anchor, false);
    }

    public IconFriendlyPopupMenu(final Context context, final View anchor, final boolean showIcons) {
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

    public void inflate(final int menuRes) {
        getMenuInflater().inflate(menuRes, this.mMenu);
    }

    @Override
    public void onCloseMenu(final MenuBuilder menu, final boolean allMenusAreClosing) {
        if (this.mDismissListener != null)
            this.mDismissListener.onDismiss(this);
    }

    public void onCloseSubMenu(final SubMenuBuilder menu) {
    }

    @Override
    public boolean onMenuItemSelected(final MenuBuilder menu, final MenuItem item) {
        return this.mMenuItemClickListener != null
                && this.mMenuItemClickListener.onMenuItemClick(item);
    }

    @Override
    public void onMenuModeChange(final MenuBuilder menu) {
    }

    @Override
    public boolean onOpenSubMenu(final MenuBuilder subMenu) {
        if (subMenu == null) return false;

        if (!subMenu.hasVisibleItems()) {
            return true;
        }

        new MenuPopupHelper(this.mContext, subMenu, this.mAnchor).show();
        return true;
    }

    public void setOnDismissListener(final OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void setOnMenuItemClickListener(final OnMenuItemClickListener listener) {
        this.mMenuItemClickListener = listener;
    }

    public void show() {
        this.mPopup.show();
    }

    public interface OnDismissListener {
        void onDismiss(IconFriendlyPopupMenu paramPopupMenu);
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem paramMenuItem);
    }
}
