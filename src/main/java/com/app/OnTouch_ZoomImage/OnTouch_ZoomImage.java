package com.app.OnTouch_ZoomImage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.io.InputStream;

public class OnTouch_ZoomImage
{
    private static Animator mCurrentAnimator;

    public static void zoomImageFromThumb(Context context, final View thumbView, String imageUrl,final RelativeLayout expanded_list_rltv, final ImageView expanded_list_image, View main_rltv)
    {
        final int mShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (mCurrentAnimator != null)
        {
            mCurrentAnimator.cancel();
        }

        new DownloadImageTask(expanded_list_image).execute(imageUrl);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();


        thumbView.getGlobalVisibleRect(startBounds);
        main_rltv.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height())
        {

            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        }
        else
        {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }


        thumbView.setAlpha(0f);

        expanded_list_rltv.setVisibility(View.VISIBLE);
        expanded_list_image.setVisibility(View.VISIBLE);

        expanded_list_rltv.setPivotX(0f);
        expanded_list_rltv.setPivotY(0f);


        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expanded_list_rltv, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expanded_list_rltv, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expanded_list_rltv, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(expanded_list_rltv,
                View.SCALE_Y, startScale, 1f));

        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expanded_list_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mCurrentAnimator != null)
                {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expanded_list_rltv, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expanded_list_rltv,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expanded_list_rltv,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expanded_list_rltv,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        thumbView.setAlpha(1f);

                        expanded_list_image.setVisibility(View.GONE);
                        expanded_list_rltv.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {
                        thumbView.setAlpha(1f);

                        expanded_list_image.setVisibility(View.GONE);
                        expanded_list_rltv.setVisibility(View.GONE);

                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;

        private DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try
            {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
        }
    }
}
