package com.gc.materialdesign.views;

import com.gc.materialdesign.R;
import com.gc.materialdesign.utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Slider extends CustomView {
	
	// Event when slider change value
	public interface OnValueChangedListener {
		public void onValueChanged(int value);
	}
	
	private Ball ball;
	public Indicator numberIndicator;

    private TextView txtNumberIndicator;

	boolean showNumberIndicator = false;
	boolean press = false;

	int value = 0;
	int max = 100;
	int min = 0;

    String[]  indicatorValues;

    public void setIndicatorValues(String[] indicatorValues) {
        this.indicatorValues = indicatorValues;
    }

	private OnValueChangedListener onValueChangedListener;

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(attrs);
	}

	@Override
	protected void onInitDefaultValues() {
		minWidth = 80;// size of view
		minHeight = 48;
		backgroundColor = Color.parseColor("#4CAF50");
		backgroundResId = R.drawable.background_transparent;
	}
	
	@Override
	protected void setAttributes(AttributeSet attrs) {
		super.setAttributes(attrs);
		if (!isInEditMode()) {
			getBackground().setAlpha(0);
		}
		showNumberIndicator = attrs.getAttributeBooleanValue(MATERIALDESIGNXML,"showNumberIndicator", false);
		min = attrs.getAttributeIntValue(MATERIALDESIGNXML, "min", 0);
		max = attrs.getAttributeIntValue(MATERIALDESIGNXML, "max", 100);// max > min
        final String indicatorsStrId = attrs.getAttributeValue(MATERIALDESIGNXML, "indicatorValues");
        if(!isInEditMode()) {
            if (indicatorsStrId != null) {
                final String indicators = getResources().getString(Integer.valueOf(indicatorsStrId.substring(1)));
                if (indicators != null)
                    indicatorValues = indicators.split("[\\\\s,]+");
            }
        }
		value = attrs.getAttributeIntValue(MATERIALDESIGNXML, "value", min);

		float size = 10;
		String thumbSize = attrs.getAttributeValue(MATERIALDESIGNXML, "thumbSize");
		if (thumbSize != null) {
			size = Utils.dipOrDpToFloat(thumbSize);
		}

		ball = new Ball(getContext());
        ball.setId(R.id.sliderBall);
		setBallParams(size);
		addView(ball);

//        txtNumberIndicator = new TextView(getContext());
//        txtNumberIndicator.setTextColor(Color.BLACK);
//        txtNumberIndicator.setGravity(Gravity.CENTER);
//        txtNumberIndicator.setText("1");
//        RelativeLayout.LayoutParams params = new LayoutParams(
//                Utils.dpToPx(15, getResources()), Utils.dpToPx(15, getResources()));
//        txtNumberIndicator.setLayoutParams(params);
//        addView(txtNumberIndicator);

		// Set if slider content number indicator
/*		if (showNumberIndicator) {
			if (!isInEditMode()) {
				numberIndicator = new Indicator(getContext());
                RelativeLayout.LayoutParams params = new LayoutParams(
                        Utils.dpToPx(20, getResources()), Utils.dpToPx(20, getResources()));
        		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_LEFT,R.id.sliderBall);
                numberIndicator.setLayoutParams(params);
                addView(numberIndicator);
			}
		}*/
	}
	
	private void setBallParams(float size) {
		RelativeLayout.LayoutParams params = new LayoutParams(
				Utils.dpToPx(size, getResources()), Utils.dpToPx(size, getResources()));
//		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0,0,0,5);
		ball.setLayoutParams(params);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!placedBall) {
			placeBall();
		}
        float division = (ball.xFin - ball.xIni) / (max - min);
		if (value == min) {
			// Crop line to transparent effect
			Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas temp = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#B0B0B0"));
			paint.setStrokeWidth(Utils.dpToPx(2, getResources()));
			temp.drawLine(getHeight() / 2, getHeight() / 2 + 15, getWidth() - getHeight() / 2, getHeight() / 2 + 15, paint);
			Paint transparentPaint = new Paint();
			transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
			transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			temp.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2,
					ViewHelper.getY(ball) + ball.getHeight() / 2, 
					ball.getWidth() / 2, transparentPaint);

			canvas.drawBitmap(bitmap, 0, 0, new Paint());
		} else {
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#B0B0B0"));
			paint.setStrokeWidth(Utils.dpToPx(2, getResources()));
			canvas.drawLine(getHeight() / 2, getHeight() / 2 + 15, getWidth() - getHeight() / 2, getHeight() / 2 + 15, paint);
			paint.setColor(backgroundColor);
			int value = this.value - min;
			canvas.drawLine(getHeight() / 2, getHeight() / 2 + 15, value * division + getHeight() / 2, getHeight() / 2 + 15, paint);
			// init ball's X
			ViewHelper.setX(ball, value * division + getHeight() / 2 - ball.getWidth() / 2);
			ball.changeBackground();
		}


		if (press && !showNumberIndicator) {
			/**
			 * 如果按住，在不显示指示器的状态下，会将ball大小扩大来给用户反馈。
			 * 最后一个参数：getHeight() / x，表示的是按下去后显示的圆球的半径
			 * 如果x=2，那么按下后圆球的直径就是这个view的高
			 * 如果x=3，按下后显示圆球的半径就是这个view高的三分之一
			 */
			Paint paint = new Paint();
			paint.setColor(backgroundColor);
			paint.setAntiAlias(true);
			canvas.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2, getHeight() / 2 + 15, getHeight() / 3, paint);
		}


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(3);

        float f = ball.xIni + 5;
        final float end = ball.xFin - ball.xIni;
        while(f < ball.xFin) {
            canvas.drawPoint(f, getHeight() / 2 + 15, paint);
            f += division;
        }
        canvas.drawPoint(ball.xFin+5, getHeight() / 2 + 15, paint);

        final float cx = ViewHelper.getX(ball) + ball.getWidth() / 2;
        final float y = 15;
        final float size = 15;
        canvas.drawCircle( cx, y, size, paint );

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(cx - size, y + 4);
        path.lineTo(cx, y + size + 7);//Utils.dpToPx(3, getResources()));
        path.lineTo(cx + size,y+4);
        path.lineTo(cx - size,y+4);
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(15);
        String strValue = "";
        if(indicatorValues != null && indicatorValues.length >= value) {
            strValue = indicatorValues[value];
        }
        else {
            strValue = String.valueOf(value);
        }

        float[] sizes = new float[strValue.length()];
        float textSize = (float)0.0;
        paint.getTextWidths(strValue, sizes);
        for(float charSize : sizes) textSize += charSize;
        canvas.drawText(strValue, cx- textSize/2, y + 5, paint);

		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isLastTouch = true;
		if (isEnabled()) {
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//				if (numberIndicator != null && numberIndicator.isShowing() == false)
//					numberIndicator.show();
				if ((event.getX() <= getWidth() && event.getX() >= 0)) {
					press = true;
					// calculate value
					int newValue = 0;
					float division = (ball.xFin - ball.xIni) / (max - min);
					if (event.getX() > ball.xFin) {
						newValue = max;
					} else if (event.getX() < ball.xIni) {
						newValue = min;
					} else {
						newValue = min + Math.round ((event.getX() - ball.xIni) / division);
					}
					if (value != newValue) {
						value = newValue;
						if (onValueChangedListener != null)
							onValueChangedListener.onValueChanged(newValue);
					}
					// move ball indicator
					float x = newValue;
					x = (x < ball.xIni) ? ball.xIni : x;
					x = (x > ball.xFin) ? ball.xFin : x;
					ViewHelper.setX(ball, x);
					ball.changeBackground();

				} else {
					press = false;
					isLastTouch = false;
				}

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				isLastTouch = false;
				press = false;
				if ((event.getX() <= getWidth() && event.getX() >= 0)) {

				}
			}
		}
		return true;
	}

	private void placeBall() {
		ViewHelper.setX(ball, getHeight() / 2 - ball.getWidth() / 2);
		ball.xIni = ViewHelper.getX(ball);
		ball.xFin = getWidth() - getHeight() / 2 - ball.getWidth() / 2;
		ball.xCen = getWidth() / 2 - ball.getWidth() / 2;
		placedBall = true;
	}

	// GETERS & SETTERS

	public OnValueChangedListener getOnValueChangedListener() {
		return onValueChangedListener;
	}

	public void setOnValueChangedListener(
			OnValueChangedListener onValueChangedListener) {
		this.onValueChangedListener = onValueChangedListener;
	}
	
	public void setThumbSize(float size) {
		setBallParams(size);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		setValue(value, false);
	}
	/**
	 * @param value
	 * @param inRunnable 如果为true表示在runnable中跟新进度，否则在主线程中更新
	 */
	public void setValue(int value,boolean inRunnable) {
		if (value <= min) {
			value = min;
		}
		if (value >= max) {
			value = max;
		}
		setValueInRunnable(value,inRunnable);
	}
	
	
	private void setValueInRunnable(final int value,final boolean inRunnable) {
		if(placedBall == false && inRunnable == true)
			post(new Runnable() {
				@Override
				public void run() {
					setValue(value,inRunnable);
				}
			});
		else{
			this.value = value;
			float division = (ball.xFin - ball.xIni) / max;
			ViewHelper.setX(ball,value*division + getHeight()/2 - ball.getWidth()/2);
			ball.changeBackground();
		}
	}
	
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public boolean isShowNumberIndicator() {
		return showNumberIndicator;
	}

	public void showNumberIndicator(boolean showNumberIndicator) {
//		this.showNumberIndicator = showNumberIndicator;
//		if (!isInEditMode()) {
//			numberIndicator = (showNumberIndicator) ? new NumberIndicator(getContext()) : null;
//		}
	}
	
	@Override
	public void setBackgroundColor(int color) {
		backgroundColor = color;
		if (isEnabled()) {
			beforeBackground = backgroundColor;
		}
	}

	private boolean placedBall = false;

	private class Ball extends View {

		private float xIni, xFin, xCen;

		public Ball(Context context) {
			super(context);
			if (!isInEditMode()) {
				setBackgroundResource(R.drawable.background_switch_ball_uncheck);
			} else {
				setBackgroundResource(android.R.drawable.radiobutton_off_background);
			}
		}

		public void changeBackground() {
			if (!isInEditMode()) {
				if (value != min) {
					setBackgroundResource(R.drawable.background_checkbox);
					LayerDrawable layer = (LayerDrawable) getBackground();
					GradientDrawable shape = (GradientDrawable) layer
							.findDrawableByLayerId(R.id.shape_bacground);
					shape.setColor(backgroundColor);
				} else {
					setBackgroundResource(R.drawable.background_switch_ball_uncheck);
				}
			}
		}

	}

	// Slider Number Indicator

	public class NumberIndicator extends View {

		private Indicator indicator;
		private TextView numberIndicator;

		public NumberIndicator(Context context) {
            super(context);

            RelativeLayout content = (RelativeLayout) this.findViewById(R.id.number_indicator_spinner_content);
            indicator = new Indicator(this.getContext());
            content.addView(indicator);

            numberIndicator = new TextView(getContext());
            numberIndicator.setTextColor(Color.WHITE);
            numberIndicator.setGravity(Gravity.CENTER);
            content.addView(numberIndicator);

            indicator.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
		}
	}

	private class Indicator extends RelativeLayout {

		// Position of number indicator
		private float x = 0;
		private float y = 15;
		// Size of number indicator
		private float size = 15;

		// Final y position after animation
		private float finalY = 0;
		// Final size after animation
		private float finalSize = 0;

		private boolean animate = false;

		private boolean numberIndicatorResize = false;

        private TextView txtNumberIndicator;

		public Indicator(Context context) {
			super(context);
			setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); //transparent

            txtNumberIndicator = new TextView(getContext());
            txtNumberIndicator.setTextColor(Color.WHITE);
            txtNumberIndicator.setGravity(Gravity.CENTER);
            txtNumberIndicator.setText("1");
            addView(txtNumberIndicator);
		}



		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

/*			if (numberIndicatorResize == false) {
				RelativeLayout.LayoutParams params = (LayoutParams) numberIndicator.
						numberIndicator.getLayoutParams();
				params.height = (int) finalSize * 2;
				params.width = (int) finalSize * 2;
				numberIndicator.numberIndicator.setLayoutParams(params);
			}*/

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(backgroundColor);
/*			if (animate) {
				if (y == 0)
					y = finalY + finalSize * 2;
				y -= Utils.dpToPx(3, getResources());
				size += Utils.dpToPx(2, getResources());
			}*/
            final float cx = ViewHelper.getX(ball)+ ball.getWidth() / 2;
			canvas.drawCircle(
					cx, y, size, paint);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(cx - size, y+4);
            path.lineTo(cx, y + size + 7);//Utils.dpToPx(3, getResources()));
            path.lineTo(cx + size,y+4);
            path.lineTo(cx - size,y+4);
            path.close();
            canvas.drawPath(path, paint);


/*			if (animate && size >= finalSize)
				animate = false;
			if (animate == false) {
				ViewHelper.setX(numberIndicator.numberIndicator,
						(ViewHelper.getX(ball) + Utils.getRelativeLeft((View) ball.getParent()) + ball.getWidth() / 2) - size);
				ViewHelper.setY(numberIndicator.numberIndicator, y - size);
                if(indicatorValues != null && indicatorValues.length >= value)
                    numberIndicator.numberIndicator.setText(indicatorValues[value] + "");
                else
				    numberIndicator.numberIndicator.setText(value + "");
			}*/

		}

        public void setText(String s) {
            txtNumberIndicator.setText(s);
        }
    }

}