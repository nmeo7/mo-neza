package com.rmsoft.moneza.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.rmsoft.moneza.R


class OnboardingPagerAdapter(
    var mContext: Context,
    mListScreen: List<OnboardingItem>
) :
    PagerAdapter() {
    var mListScreen: List<OnboardingItem>
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.layout_onboarding, null)
        val imgSlide =
            layoutScreen.findViewById<ImageView>(R.id.img_intro)
        val title = layoutScreen.findViewById<TextView>(R.id.tv_title)
        val description = layoutScreen.findViewById<TextView>(R.id.tv_description)
        title.text = mListScreen[position].title
        description.text = mListScreen[position].description
        imgSlide.setImageResource(mListScreen[position].screenImg)
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    init {
        this.mListScreen = mListScreen
    }
}