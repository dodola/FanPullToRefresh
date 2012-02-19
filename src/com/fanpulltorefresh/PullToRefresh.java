package com.fanpulltorefresh;



import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

public class PullToRefresh extends FrameLayout implements
		GestureDetector.OnGestureListener {
	public static final int STATE_CLOSE = 1;
	public static final int STATE_OPEN = 2;
	public static final int STATE_OPEN_MAX = 4;
	public static final int STATE_OPEN_MAX_RELEASE = 5;
	public static final int STATE_OPEN_RELEASE = 3;
	public static final int STATE_UPDATE = 6;
	public static final int STATE_UPDATE_SCROLL = 7;
	private final int MAXHEIGHT = 80;
	private final String TAG = "PullToRefresh";

	private ImageView mArrow;
	private String mDate;
	private GestureDetector mDetector;
	private Flinger mFlinger;
	private boolean mIsAutoScroller;
	private int mPading;
	private ProgressBar mProgressBar;
	private int mState;
	private TextView mTitle;
	private FrameLayout mUpdateContent;

	private UpdateHandle mUpdateHandle;

	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;

	private boolean mIsOpen = true;

	public PullToRefresh(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addUpdateBar();
		init();
	}

	public PullToRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		addUpdateBar();
		init();
	}

	public PullToRefresh(Context context) {
		super(context);
		addUpdateBar();
		init();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d(TAG, "[dispatchTouchEvent]");

		boolean bool1 = this.mIsAutoScroller;
		GestureDetector localGestureDetector = this.mDetector;

		localGestureDetector.onTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int i1 = getChildAt(1).getTop();
			if (i1 != 0) {
				updateView();
			}

			break;
		case MotionEvent.ACTION_UP:

			if (this.mState == STATE_OPEN) {
				this.mState = STATE_OPEN_RELEASE;
			}
			if (this.mState == STATE_OPEN_MAX) {
				this.mState = STATE_OPEN_MAX_RELEASE;
			}
			Log.d(TAG, "[onScroll] ActionUP mState=" + mState);

			release();
			break;
		}
		if (mState != STATE_UPDATE) {
			bool1 = super.dispatchTouchEvent(ev);
		}

		int i1 = getChildAt(1).getTop();
		Log.d(TAG, "[dispatchTouchEvent] getTop()=" + i1);
		if (i1 != 0) {
			ev.setAction(3);
			super.dispatchTouchEvent(ev);
			updateView();
		}
		return bool1;
	}

	private void init() {
		GestureDetector localGestureDetector = new GestureDetector(this);
		this.mDetector = localGestureDetector;
		Flinger localFlinger = new Flinger();
		this.mFlinger = localFlinger;
		this.mState = 1;
		setDrawingCacheEnabled(true);
		setClipChildren(true);
		this.mDetector.setIsLongpressEnabled(false);
	}

	private void updateView() {
		Log.d("下拉控件", String.format("updateView %d", this.mState));
		View localView1 = getChildAt(0);
		View localView2 = getChildAt(1);
		if (this.mDate == null)
			this.mDate = "";

		switch (this.mState) {
		case STATE_CLOSE:
			if (localView1.getVisibility() != View.INVISIBLE)
				localView1.setVisibility(View.INVISIBLE);
		case STATE_OPEN:
		case STATE_OPEN_RELEASE:
			Log.d(TAG, "updateView STATE_OPEN");
			// STATE_OPEN
			int m = localView2.getTop();
			int n = -this.mPading - m;

			localView2.offsetTopAndBottom(n);
			if (localView1.getVisibility() != 0)
				localView1.setVisibility(View.VISIBLE);
			int i1 = localView1.getTop();// 相对于父窗口的顶部大小
			int i2 = -MAXHEIGHT;
			int i3 = this.mPading;
			int i4 = i2 - i3 - i1;
			localView1.offsetTopAndBottom(i4);
			TextView localTextView1 = this.mTitle;
			String str1 = "下拉可以刷新";
			StringBuilder localStringBuilder1 = new StringBuilder(str1)
					.append("\n");
			localStringBuilder1.append(this.mDate);
			localTextView1.setText(localStringBuilder1.toString());
			this.mProgressBar.setVisibility(View.INVISIBLE);
			this.mArrow.setVisibility(View.VISIBLE);
			if (!mIsOpen) {
				mIsOpen = true;
				this.mArrow.setAnimation(mReverseFlipAnimation);
				mReverseFlipAnimation.start();

			}
			break;
		case STATE_OPEN_MAX_RELEASE:
		case STATE_OPEN_MAX:
			int i5 = localView2.getTop();
			int i6 = -this.mPading - i5;
			localView2.offsetTopAndBottom(i6);
			if (localView1.getVisibility() != View.VISIBLE)
				localView1.setVisibility(View.VISIBLE);
			int i7 = localView1.getTop();
			int i8 = -MAXHEIGHT;
			int i9 = this.mPading;
			int i10 = i8 - i9 - i7;
			localView1.offsetTopAndBottom(i10);
			TextView localTextView2 = this.mTitle;
			String str4 = "松开可以刷新";// release_update:松开可以刷新
			StringBuilder localStringBuilder2 = new StringBuilder(str4)
					.append("\n");

			localStringBuilder2.append(this.mDate);
			localTextView2.setText(localStringBuilder2.toString());
			this.mProgressBar.setVisibility(View.INVISIBLE);
			this.mArrow.setVisibility(View.VISIBLE);
			if (mIsOpen) {
				mIsOpen = false;
				this.mArrow.setAnimation(mFlipAnimation);
				mFlipAnimation.start();

			}
			break;
		case STATE_UPDATE:
			// STATE_UPDATE
			int i11 = localView2.getTop();
			int i12 = -this.mPading - i11;
			localView2.offsetTopAndBottom(i12);
			int i13 = localView1.getTop();
			if (this.mProgressBar.getVisibility() != View.VISIBLE)
				this.mProgressBar.setVisibility(View.VISIBLE);
			if (this.mArrow.getVisibility() != View.INVISIBLE)
				this.mArrow.setVisibility(View.INVISIBLE);
			TextView localTextView3 = this.mTitle;
			String str7 = "加载中...";// doing_update:加载中...
			StringBuilder localStringBuilder3 = new StringBuilder(str7)
					.append("\n");

			localStringBuilder3.append(this.mDate);
			localTextView3.setText(localStringBuilder3.toString());
			int i14 = -MAXHEIGHT;
			int i15 = this.mPading;
			int i16 = i14 - i15 - i13;
			localView1.offsetTopAndBottom(i16);
			if (localView1.getVisibility() != 0)
				localView1.setVisibility(0);
			this.mProgressBar.setVisibility(View.VISIBLE);
			this.mArrow.setVisibility(View.GONE);
			mArrow.clearAnimation();
			break;
		}
		invalidate();
	}

	private void addUpdateBar() {
		Context localContext1 = getContext();
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(200);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(200);
		mReverseFlipAnimation.setFillAfter(true);

		View localView = LayoutInflater.from(localContext1).inflate(
				R.layout.vw_update_bar, null);
		localView.setVisibility(4);
		addView(localView);

		this.mArrow = new ImageView(localContext1);

		FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);

		ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_CENTER;
		this.mArrow.setScaleType(localScaleType);
		this.mArrow.setLayoutParams(localLayoutParams1);
		this.mArrow.setImageResource(R.drawable.arrow_down);

		this.mUpdateContent = (FrameLayout) getChildAt(0).findViewById(
				R.id.iv_content);
		FrameLayout localFrameLayout2 = this.mUpdateContent;
		localFrameLayout2.addView(this.mArrow);
		FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		localLayoutParams2.gravity = Gravity.CENTER_VERTICAL;
		this.mProgressBar = new ProgressBar(localContext1);
		int i = getResources().getDimensionPixelSize(R.dimen.updatebar_padding);
		this.mProgressBar.setPadding(i, i, i, i);
		this.mProgressBar.setLayoutParams(localLayoutParams2);

		this.mUpdateContent.addView(mProgressBar);
		this.mTitle = (TextView) findViewById(R.id.tv_title);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		View localView1 = getChildAt(0);
		int i = -MAXHEIGHT;
		int j = this.mPading;
		int k = i - j;
		int l = getMeasuredWidth();
		int i1 = -this.mPading;
		localView1.layout(0, k, l, i1);

		View localView2 = getChildAt(1);
		int i2 = -this.mPading;
		int i3 = getMeasuredWidth();
		int i4 = getMeasuredHeight();
		int i5 = this.mPading;
		int i6 = i4 - i5;
		localView2.layout(0, i2, i3, i6);
	}

	public void endUpdate(String paramString) {
		this.mDate = paramString;
		Log.d(TAG, "[endUpdate]  mPading=" + this.mPading);
		if (this.mPading != 0) {
			this.mState = STATE_CLOSE;
			scrollToClose();
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d(TAG, "[onScroll] paramFloat2=" + distanceY + " mPadding="
				+ this.mPading);
		AdapterView localAdapterView = (AdapterView) getChildAt(1);
		int k = localAdapterView.getCount();
		if (k == 0) {
			return false;
		}
		k = localAdapterView.getFirstVisiblePosition();// 获取第一个显示项目的position
		if (k == 0) {
			int t = localAdapterView.getChildAt(0).getTop();
			Log.d(TAG, "[onScroll]getTop()=" + t);
			if (t != 0) {
				return false;
			} else {

				Log.d(TAG, "[onScroll] ACTION_MOVE mState=" + mState);
				this.mPading = (int) (this.mPading + distanceY / 2);

				if (this.mPading > 0)
					this.mPading = 0;
				// if (distanceY < 0) {

				if (Math.abs(this.mPading) <= MAXHEIGHT) {
					this.mState = STATE_OPEN;

				} else {
					this.mState = STATE_OPEN_MAX;

				}

				// }
				updateView();

			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	private int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			scrollRange = Math
					.max(0,
							child.getHeight()
									- (getHeight()
											- this.getBottomPaddingOffset() - this
												.getTopPaddingOffset()));
		}
		return scrollRange;
	}

	private boolean release() {
		int tempStatus = STATE_OPEN_MAX_RELEASE;
		int i = this.mPading;
		if (i >= 0) {
			return true;
		}
		int j = this.mState;
		switch (j) {
		case STATE_OPEN_RELEASE:
			int k = Math.abs(this.mPading);
			int i1 = MAXHEIGHT;
			if (k < i1) {
				tempStatus = STATE_OPEN_MAX_RELEASE;
				this.mState = tempStatus;
			}
			scrollToClose();
			break;
		case STATE_OPEN_MAX_RELEASE:

			this.mState = tempStatus;
			scrollToUpdate();
			break;
		}
		return false;
	}

	private void scrollToClose() {
		Log.d(TAG, "[scrollToClose]");
		Flinger localFlinger = this.mFlinger;
		int i = -this.mPading;
		localFlinger.startUsingDistance(i, 2000);
	}

	private void scrollToUpdate() {
		Log.d(TAG, "[scrollToUpdate]");
		Flinger localFlinger = this.mFlinger;

		int k = -this.mPading - MAXHEIGHT;
		localFlinger.startUsingDistance(k, 1000);
	}

	class Flinger implements Runnable {
		private int mLastFlingX;
		private Scroller mScroller;

		public Flinger() {
			Context localContext = PullToRefresh.this.getContext();
			Scroller localScroller = new Scroller(localContext);
			this.mScroller = localScroller;
		}

		private void startCommon() {
			PullToRefresh.this.removeCallbacks(this);
		}

		public void run() {
			Log.d(TAG, "[Flinger.run] mPading=" + mPading);
			boolean bool1 = Math.abs(mPading) != MAXHEIGHT;
			Scroller localScroller = this.mScroller;
			boolean bool2 = localScroller.computeScrollOffset();
			int i = localScroller.getCurrX();
			int j = this.mLastFlingX - i;
			PullToRefresh localPullDownView = PullToRefresh.this;

			localPullDownView.move(j, bool1);
			PullToRefresh.this.updateView();
			if (bool2) {
				this.mLastFlingX = i;
				PullToRefresh.this.post(this);
			} else {
				PullToRefresh.this.mIsAutoScroller = bool1;
				PullToRefresh.this.removeCallbacks(this);
			}
		}

		public void startUsingDistance(int paramInt1, int paramInt2) {
			Log.d(TAG, String.format(
					"[Flinger.startUsingDistance]paramInt1=%d,paramInt2=%d",
					paramInt1, paramInt2));
			int i = 0;
			if (paramInt1 == 0)
				--paramInt1;
			startCommon();
			this.mLastFlingX = i;
			Scroller localScroller = this.mScroller;

			localScroller.startScroll(i, 0, -paramInt1, 0, paramInt2);
			PullToRefresh.this.mIsAutoScroller = true;
			PullToRefresh.this.post(this);
		}
	}

	/**
	 * 释放的时候使用
	 * 
	 * @param f
	 * @param bool1
	 */
	public void move(float f, boolean bool1) {
		Log.d(TAG, "[move]mIsAutoScroller=" + mIsAutoScroller);
		if (this.mState != STATE_CLOSE) {
			if (!bool1) {
				// 刷新
				Log.d(TAG, "[move]refresh");
				if (mState == STATE_OPEN_MAX_RELEASE) {
					this.mState = STATE_UPDATE;
					if (mUpdateHandle != null) {
						mUpdateHandle.onUpdate();
					}
				}
			}
			if (this.mState == STATE_OPEN_MAX_RELEASE
					|| this.mState == STATE_OPEN_RELEASE) {
				this.mPading += f;

			}
		} else {
			Log.d(TAG, "[move]up top");
			if (mIsAutoScroller) {
				this.mPading += f;
			}
		}

	}

	public abstract interface UpdateHandle {
		public abstract void onUpdate();
	}

	public void setUpdateDate(String paramString) {
		this.mDate = paramString;
	}

	public void setUpdateHandle(UpdateHandle paramUpdateHandle) {
		this.mUpdateHandle = paramUpdateHandle;
	}

	public void updateWithoutOffset() {
		this.mState = STATE_UPDATE_SCROLL;
		invalidate();
	}

}