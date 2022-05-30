package com.vanthien113.tiancamera.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


/**
 * Created by hung.nguyendk on 10/6/20.
 */
fun ImageView.loadImage(id: Int) {
    Glide.with(this)
        .load(id)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

//fun ImageView.onLoadImageUrl(imageLink: String) {
//    Glide.with(this)
//        .load(imageLink)
//        .error(R.drawable.ic_thumb)
//        .placeholder(R.drawable.ic_thumb)
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .into(this)
//}
//
//fun ImageView.onLoadImageConfigCloudflare(imageLink: String, quality: Int = 100) {
//    this.postDelayed({
//        val imageHost = AppUtils.getImagesHost().firstOrNull { imageLink.contains(it) }
//        val imageSource = if (imageHost != null) {
//            val imageWidth = this.measuredWidth
//            imageLink.replace(imageHost, "${imageHost}cdn-cgi/image/width=$imageWidth,quality=$quality/")
//        } else {
//            imageLink
//        }
//        Glide.with(this.context.applicationContext)
//            .load(imageSource)
//            .error(R.drawable.ic_thumb)
//            .placeholder(R.drawable.ic_thumb)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(this)
//    }, 0)
//}

//fun ImageView.loadImageUrlSmallSize(link: String) {
//    Glide.with(this)
//        .load(link)
//        .error(R.drawable.ic_thumb)
//        .placeholder(R.drawable.ic_thumb)
//        .apply(RequestOptions().override(400, 400))
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .into(this)
//}

//fun ImageView.loadSimpleImageUrl(link: String) {
//    Glide.with(this)
//        .load(link)
//        .placeholder(R.color.colorGray)
//        .apply(RequestOptions().override(1200, 1200))
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .into(this)
//}

//fun ImageView.loadImageSvgUrl(link: String) {
//    Glide.with(this)
//        .`as`(PictureDrawable::class.java)
//        .load(link)
//        .error(R.drawable.ic_thumb)
//        .placeholder(R.color.colorTransparent)
//        .into(this)
//}

fun ImageView.loadImageUrl(imageLink: String) {
    Glide.with(this)
        .load(imageLink)
//        .error(R.drawable.ic_thumb)
//        .placeholder(R.drawable.ic_thumb)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}
//
//fun ImageView.loadImageUrlWithBlurEffect(link: String) {
//    Glide.with(this)
//        .load(link)
//        .apply(bitmapTransform(BlurTransformation(25, 3)))
//        .into(this)
//}

fun ImageView.tintColor(color: Int) {
    setColorFilter(
        ContextCompat.getColor(context, color),
        android.graphics.PorterDuff.Mode.MULTIPLY
    )
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun View.removeBackgroundResource() {
    this.setBackgroundResource(0)
}

fun ImageView.imageDrawable(drawable: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, drawable))
}

fun TextView.setColorText(color: Int) {
    setTextColor(ContextCompat.getColor(context, color))
}

fun TextView.setColorBackground(color: Int) {
    setBackgroundColor(ContextCompat.getColor(context, color))
}

fun TextView.setDrawableBackground(drawable: Int) {
    background = ContextCompat.getDrawable(context, drawable)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.setVisible(visible: Boolean) {
    if (visible) this.show()
    else this.hide()
}

fun View.setInVisible(visible: Boolean) {
    if (visible) show()
    else this.inVisible()
}

//fun View.getLocationOnScreen(context: Context): Point {
//    val location = IntArray(2)
//    this.getLocationOnScreen(location)
//    return Point(location[0], location[1] - AppUtils.getStatusBarHeight(context))
//}

fun View.setOnDelayClickListener(method: () -> Unit) {
    this.setOnClickListener {
        this.isEnabled = false
        method.invoke()
        this.postDelayed({
            this.isEnabled = true
        }, 600)
    }
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.clearDrawables() {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun TextView.setLeftDrawables(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setRightDrawables(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun View.isEnableView(enable: Boolean = true) {
    this.isEnabled = enable
    this.alpha = if (enable) 1f else 0.5f
}

fun TextView.setDrawableCompat(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    this.setCompoundDrawablesWithIntrinsicBounds(
        if (left != null) ContextCompat.getDrawable(context, left) else null,
        if (top != null) ContextCompat.getDrawable(context, top) else null,
        if (right != null) ContextCompat.getDrawable(context, right) else null,
        if (bottom != null) ContextCompat.getDrawable(context, bottom) else null
    )
}

fun NestedScrollView.onLoadMore(
    linearLayoutManager: LinearLayoutManager,
    shouldLoadMore: () -> Unit = {}
) {
    setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
        if (v?.getChildAt(v.childCount - 1) != null) {
            if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY) {
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    shouldLoadMore()
                }
            }
        }
    }
}

fun RecyclerView.onLoadMore(
    linearLayoutManager: LinearLayoutManager,
    shouldLoadMore: () -> Unit = {}
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, scrollY: Int, oldScrollY: Int) {
            super.onScrolled(recyclerView, scrollY, oldScrollY)
            if (recyclerView.getChildAt(recyclerView.childCount - 1) != null) {
                if (scrollY >= recyclerView.getChildAt(recyclerView.childCount - 1).measuredHeight - recyclerView.measuredHeight && scrollY > oldScrollY) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        shouldLoadMore()
                    }
                }
            }
        }
    })
}

fun View.forEachChildView(closure: (View) -> Unit) {
    closure(this)
    val groupView = this as? ViewGroup ?: return
    val size = groupView.childCount - 1
    for (i in 0..size) {
        groupView.getChildAt(i).forEachChildView(closure)
    }
}

fun View.enabledView(enabled: Boolean) {
    this.isEnabled = enabled
    // TODO something
}

fun View.enabledForEachChildView(isEnable: Boolean) {
    this.forEachChildView {
        it.enabledView(isEnabled)
    }
}