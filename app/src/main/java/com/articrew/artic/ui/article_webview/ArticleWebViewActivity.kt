package com.articrew.artic.ui.article_webview

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.articrew.artic.R
import com.articrew.artic.logger.Logger
import com.articrew.artic.repository.ArticRepository
import com.articrew.artic.ui.collect_archive.CollectArchiveDialogFragment
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.activity_article_web_view.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

/**
 * it must need article id (Int) intent["articleId"]
 * [reference](https://github.com/delight-im/Android-AdvancedWebView)
 * @author greedy0110
 * */
class ArticleWebViewActivity : AppCompatActivity() {
    private val repository: ArticRepository by inject()
    private val logger: Logger by inject()
    private var articleId = -1
    private var articleUrl = "https://brunch.co.kr/@waltzfordebby/3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_web_view)

        articleId = intent.getIntExtra("articleId", -1)
        logger.log("ArticleWebViewActivity articleId : $articleId")

        repository.readArticle(
            articleIdx = articleId,
            successCallback = {
                logger.log("$articleId read article $it")
            }
        )

        // article id를 사용해서 데이터를 받아와야함. article url, article isLiked 여부
        repository.getArticle(
            articleId = articleId,
            successCallback = {
                articleUrl = it.url
                // 세부적인 내용 처리가 필요하니?
                wv_article_web_view.setListener(this@ArticleWebViewActivity, object : AdvancedWebView.Listener{
                    override fun onPageFinished(url: String?) {

                    }

                    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
                    }

                    override fun onDownloadRequested(
                        url: String?,
                        suggestedFilename: String?,
                        mimeType: String?,
                        contentLength: Long,
                        contentDisposition: String?,
                        userAgent: String?
                    ) {
                    }

                    override fun onExternalPageRequest(url: String?) {
                    }

                    override fun onPageStarted(url: String?, favicon: Bitmap?) {
                    }
                })

                wv_article_web_view.loadUrl(articleUrl)

                btn_article_web_view_like.isSelected = it.isLiked!!

                btn_article_web_view_like.setOnClickListener {
//                    it.isSelected = !it.isSelected // toggle 구현

                    repository.postArticleLike(
                        articleIdx = articleId,
                        successCallback = {
                        },
                        errorCallback = {
                            toast(R.string.network_error)
                        },
                        statusCallback = {status, success, message ->
                            run {

                                if (status == 200) {

                                    if (message == "아티클 좋아요 성공") { // 좋아요 성공
                                        btn_article_web_view_like.isSelected = true
                                    }
                                    else { // 좋아요 취소 성공
                                        btn_article_web_view_like.isSelected = false
                                    }

//                                    toast(message)
                                }
                                else if (status == 400) {
//                                    toast(message)
                                }
                                else {
//                                    toast("오잉")
                                }
                            }
                        }

                    )

                }

                btn_article_web_view_collect.setOnClickListener {
                    // TODO 담기 Dialog 를 띄워줘야함.
                    var bundle = Bundle()
                    bundle.putInt("article_idx", articleId)

                    var putFragment = CollectArchiveDialogFragment()
                    putFragment.arguments = bundle

                    putFragment.show(this@ArticleWebViewActivity.supportFragmentManager, putFragment.tag)
                }
            },
            errorCallback = {
                toast(R.string.network_error)
            })
    }

    override fun onResume() {
        super.onResume()
        wv_article_web_view.onResume()
    }

    override fun onPause() {
        wv_article_web_view.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        wv_article_web_view.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        wv_article_web_view.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (!wv_article_web_view.onBackPressed()) {
            return; }
        super.onBackPressed()
    }
}
