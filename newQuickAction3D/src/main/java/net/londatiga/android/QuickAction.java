package net.londatiga.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical 
 * and horizontal layout.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 * - Alexandros Schillings https://github.com/alt236
 * 
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private View mScroller;
	private TextView mLine1;
	private TextView mLine2;
	private ImageView mImage;

	private OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;

	private List<ActionItem> actionItems = new ArrayList<ActionItem>();

	private boolean mDidAction;
	private View mLastAnchor;

	private int mRootViewId = -1;
	private int mChildPos;
	private int mInsertPos;
	private int mAnimStyle;
	private int mOrientation;
	private int mColour;
	private int rootWidth=0;

	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_VERTICAL = 1;

	public static final int COLOUR_LIGHT = 0;
	public static final int COLOUR_DARK = 1;
	
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;

	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context  Context
	 */
	public QuickAction(Context context) {
		this(context, ORIENTATION_VERTICAL, COLOUR_DARK);
	}

	/**
	 * Constructor allowing orientation override
	 * 
	 * @param context    Context
	 * @param orientation Layout orientation, can be vartical or horizontal
	 */
	public QuickAction(Context context, int orientation, int colour) {
		super(context);

		mOrientation = orientation;
		mColour = colour;
				
		mInflater 	 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (mOrientation == ORIENTATION_HORIZONTAL) {
			setRootViewId(R.layout.quickaction_item_popup_horizontal);
		} else {
			setRootViewId(R.layout.quickaction_item_popup_vertical);
		}

		mAnimStyle 	= ANIM_AUTO;
		mChildPos 	= 0;
	}

	/**
	 * Get action item at an index
	 * 
	 * @param index  Index of item (position from callback)
	 * 
	 * @return  Action Item at the position
	 */
	public ActionItem getActionItem(int index) {
		return actionItems.get(index);
	}

	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootViewId = id;
		mRootView	= (ViewGroup) mInflater.inflate(id, null);
		mTrack 		= (ViewGroup) mRootView.findViewById(R.id.tracks);
		mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);
		mScroller	= mRootView.findViewById(R.id.scroller);

		if(mRootViewId == R.layout.quickaction_item_popup_horizontal){
			mLine1      = (TextView) mRootView.findViewById(R.id.line1);
			mLine2      = (TextView) mRootView.findViewById(R.id.line2);
			mImage      = (ImageView) mRootView.findViewById(R.id.image);
		}

		colorisePopup(mRootView.findViewById(R.id.content));
		
		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quickaction dialog.
		//Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}
	
	/**
	 * Set animation style
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener Listener
	 */
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(ActionItem action) {
		actionItems.add(action);

		String title 	= action.getTitle();
		Drawable icon 	= action.getIcon();

		View container;

		if (mOrientation == ORIENTATION_HORIZONTAL) {
			container = mInflater.inflate(R.layout.quickaction_item_horizontal, null);
		} else {
			container = mInflater.inflate(R.layout.quickaction_item_vertical, null);
		}

		ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
		TextView text 	= (TextView) container.findViewById(R.id.tv_title);

		if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}

		if (title != null) {
			text.setText(title);
			coloriseTextView(text);
		} else {
			text.setVisibility(View.GONE);
		}

		final int pos 		=  mChildPos;
		final int actionId 	= action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
				}

				if (!getActionItem(pos).isSticky()) {  
					mDidAction = true;

					dismiss();
				}
			}
		});

		container.setFocusable(true);
		container.setClickable(true);

		if (mOrientation == ORIENTATION_HORIZONTAL && mChildPos != 0) {
			View separator = mInflater.inflate(R.layout.quickaction_horiz_separator, null);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

			separator.setLayoutParams(params);
			separator.setPadding(5, 0, 5, 0);

			mTrack.addView(separator, mInsertPos);

			mInsertPos++;
		}

		mTrack.addView(container, mInsertPos);

		mChildPos++;
		mInsertPos++;
	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */


	public void show (View anchor) {
		this.show(anchor, null, "", "");
	}




	private void populateAdditionalFields(Drawable d, String line1, String line2){
		//Log.d(Constants.TAG, "^ mRootViewId: " +  mRootViewId);
		//Log.d(Constants.TAG, "^ Horiz      : " +  R.layout.quickaction_popup_horizontal );
		
		if(mRootViewId == R.layout.quickaction_item_popup_horizontal){
			if(d == null){
				mImage.setVisibility(View.GONE);
			} else {
				mImage.setVisibility(View.VISIBLE);
				mImage.setImageDrawable(d);
			}

			if(line1 == null || !(line1.length()>0)){
				mLine1.setVisibility(View.GONE);
			}else{
				mLine1.setVisibility(View.VISIBLE);
				mLine1.setText(line1);
				coloriseTextView(mLine1);
			}

			if(line2 == null || !(line2.length()>0)){
				mLine2.setVisibility(View.GONE);
			}else{
				mLine2.setVisibility(View.VISIBLE);
				mLine2.setText(line2);
				coloriseTextView(mLine2);
			}

			if(mImage.getVisibility() == View.GONE && 
					mLine1.getVisibility() == View.GONE && 
					mLine2.getVisibility() == View.GONE){
				mRootView.findViewById(R.id.info).setVisibility(View.GONE);
			}else{
				mRootView.findViewById(R.id.info).setVisibility(View.VISIBLE);
			}
		}
		else if (mRootViewId == R.layout.quickaction_item_popup_vertical){

		}
	}

	private void colorisePopup(View view){
		if (view == null){
			return;
		}
		
		if(mColour == COLOUR_DARK){
			view.setBackgroundResource(R.drawable.quickaction_item_popup_dark);
			mArrowUp.setImageResource(R.drawable.quickaction_item_arrow_up_dark);
			mArrowDown.setImageResource(R.drawable.quickaction_item_arrow_down_dark);
		}else if (mColour == COLOUR_LIGHT){
			view.setBackgroundResource(R.drawable.quickaction_item_popup_light);
			mArrowUp.setImageResource(R.drawable.quickaction_item_arrow_up_light);
			mArrowDown.setImageResource(R.drawable.quickaction_item_arrow_down_light);
		}
	}
	private void coloriseTextView(TextView tv){
		if (tv == null){
			return;
		}
		
		if(mColour == COLOUR_DARK){
			tv.setTextColor(Color.WHITE);
		}else if (mColour == COLOUR_LIGHT){
			tv.setTextColor(Color.BLACK);
		}
	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */

	public void show (View anchor, Drawable d, String line1, String line2) {
		populateAdditionalFields(d, line1, line2);
		
		preShow();

		int xPos, yPos, arrowPos;
		mDidAction 			= false;
		int[] location 		= new int[2];

		// Set the anchor view
		mLastAnchor = anchor;

		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
				+ anchor.getHeight());

		//mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight 		= mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth		= mRootView.getMeasuredWidth();
		}

		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight	= mWindowManager.getDefaultDisplay().getHeight();

		//automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos 		= anchorRect.left - (rootWidth-anchor.getWidth());			
			xPos 		= (xPos < 0) ? 0 : xPos;

			arrowPos 	= anchorRect.centerX()-xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth/2);
			} else {
				xPos = anchorRect.left;
			}

			arrowPos = anchorRect.centerX()-xPos;
		}

		int dyTop			= anchorRect.top;
		int dyBottom		= screenHeight - anchorRect.bottom;

		boolean onTop		= (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos 			= 15;
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) { 
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyBottom;
			}
		}

		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public View getLastAnchorView(){
		return mLastAnchor;
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth screen width
	 * @param requestedX distance from left edge
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
	 * 		  and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	
	
	private void showArrow(int whichArrow, int requestedX) {
		View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
		View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, int pos, int actionId);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}