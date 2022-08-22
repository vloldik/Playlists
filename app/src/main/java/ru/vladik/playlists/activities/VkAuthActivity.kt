package ru.vladik.playlists.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.vladik.playlists.R
import ru.vladik.playlists.utils.*

class VkAuthActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)
        webView = findViewById(R.id.web_view)
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView,
                                                  request: WebResourceRequest): Boolean {
                if (request.url.toString().contains("access_token=")) {
                    val text = ("<head>" +
                            "<meta name='viewport' content='width=device-width," +
                            " shrink-to-fit=YES' user-scalable='no'>" +
                            "<script type=\"text/x-mathjax-config\">MathJax.Hub.Config" +
                            "({tex2jax: {inlineMath: [['\\(','\\)']],processEscapes: true},\"HTML-CSS\":" +
                            " { linebreaks: { automatic: true, width: \"container\" } } } )</script>" +
                            "<script id=\"MathJax-script\" async src=\"" +
                            "https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js\" >" +
                            "</script>" +
                            "</head>" +
                            "<body style=\"font-size:100%; color:blue; font-family: Arial;\"  > "
                            + "Успешно пройдена авторизация" + "</body></html>")
                    val url = request.url.toString()
                    var token = url.substring(url.indexOf("=", 0) + 1)
                    token = token.substring(0, token.indexOf('&'))
                    LoginUtil.vkLogIn(this@VkAuthActivity, token, true)
                    view.loadData(text, "text/html", "UTF-8")
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    view.loadUrl(request.url.toString())
                }
                return true
            }
        })
        val webSettings: WebSettings = webView.settings
        webSettings.domStorageEnabled = true
        webView.loadUrl(
            "https://oauth.vk.com/authorize?client_id=6463690&scope=1073737727&redirect_uri" +
                    "=https://oauth.vk.com/blank.html&display=page&response_type=token&revoke=1"
        )
    }
}