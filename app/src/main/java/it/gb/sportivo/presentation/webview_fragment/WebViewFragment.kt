package it.gb.sportivo.presentation.webview_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import it.gb.sportivo.Backpressedlistener
import it.gb.sportivo.databinding.FragmentWebViewBinding
import it.gb.sportivo.MainActivity


private const val ARG_URL = ""

class WebViewFragment : Fragment(), Backpressedlistener {
    private var url: String? = null

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding
        get() = _binding ?: throw RuntimeException("Object FragmentWebViewBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_URL)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    println("123")
                }
            }
        )

        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = (activity as MainActivity).ChromeClient()
        val webSettings = binding.webView.settings

        webSettings.setJavaScriptEnabled(true);
        if (savedInstanceState != null)
            binding.webView.restoreState(savedInstanceState)
        else
            binding.webView.loadUrl(url!!)

        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if( binding.webView.canGoBack()){
            binding.webView.goBack();
        }
    }

    override fun onPause() {
        backpressedlistener =null;
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        backpressedlistener =this;
    }

    companion object {

        var  backpressedlistener: Backpressedlistener? = null
        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
    }
}


