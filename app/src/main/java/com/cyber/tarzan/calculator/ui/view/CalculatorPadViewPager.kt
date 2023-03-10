

package com.cyber.tarzan.calculator.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cyber.tarzan.calculator.R

class CalculatorPadViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var onStateChanged: (open: Boolean) -> Unit = {}

    private val mStaticPagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return 1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return getChildAt(position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            removeViewAt(position)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageWidth(position: Int): Float {
            return if (position == 1) 7.0f / 9.0f else 1.0f
        }
    }

    private val mPageTransformer =
        PageTransformer { view, position ->
            if (position < 0.0f) {

                view.translationX = (width) * -position
                view.alpha = (1.0f + position).coerceAtLeast(0.2f)
            } else {
                view.translationX = 0.0f
                view.alpha = 1.0f
            }
        }

    private val mOnPageChangeListener =
        object : SimpleOnPageChangeListener() {
            private fun recursivelySetEnabled(view: View, enabled: Boolean) {
                if (view is ViewGroup) {
                    for (childIndex in 0 until view.childCount) {
                        recursivelySetEnabled(view.getChildAt(childIndex), enabled)
                    }
                } else {
                    view.isEnabled = enabled
                }
            }

            override fun onPageSelected(position: Int) {
                if (adapter === mStaticPagerAdapter) {
                    for (childIndex in 0 until childCount) {
                        recursivelySetEnabled(getChildAt(childIndex), childIndex == position)
                    }
                    if (position == 1) {
                        onStateChanged(true)
                    } else if (position == 0) {
                        onStateChanged(false)
                    }

                }
            }
        }

    init {
        adapter = mStaticPagerAdapter
        pageMargin = resources.getDimensionPixelSize(R.dimen.pad_page_margin)
        addOnPageChangeListener(mOnPageChangeListener)
        setPageTransformer(false, mPageTransformer)
    }


    fun addScientificPadStateChangeListener(onStateChanged: (open: Boolean) -> Unit) {
        this.onStateChanged = onStateChanged
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (adapter === mStaticPagerAdapter) {
            mStaticPagerAdapter.notifyDataSetChanged()
        }
    }

}