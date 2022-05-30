package com.vanthien113.tiancamera.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.vanthien113.tiancamera.utils.LoadingDialog
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseFragment<V : ViewBinding> : Fragment() {
    private lateinit var binding: V
    protected val disposable by lazy { CompositeDisposable() }

    protected abstract fun inflateViewBinding(): V

    protected fun getBinding(): V {
        return binding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflateViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private val dialogLoading by lazy { LoadingDialog(requireContext()) }

    fun showLoading(canCancel: Boolean = false) {
        dialogLoading.setCancelable(canCancel)
        if (!dialogLoading.isShowing) {
            dialogLoading.show()
        }
    }

    fun hideLoading() {
        if (dialogLoading.isShowing) {
            dialogLoading.dismiss()
        }
    }

    abstract fun initialize()

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}