package com.github.cc007.blueart.endpoints.content.art

import com.github.cc007.blueart.components.richtext.renderRichText
import com.github.cc007.blueart.components.topBanner
import com.github.cc007.blueart.endpoints.auth.AtProtoAuthentication
import io.github.cc007.kolostyles.dsl.font.fontSans
import io.github.cc007.kolostyles.dsl.font.text2xl
import io.github.cc007.kolostyles.dsl.font.textBase
import io.github.cc007.kolostyles.dsl.kolo
import io.github.cc007.kolostyles.dsl.koloStylesheetLink
import io.github.cc007.kolostyles.dsl.layout.display.block
import io.github.cc007.kolostyles.dsl.layout.display.flex
import io.github.cc007.kolostyles.dsl.layout.display.grid
import io.github.cc007.kolostyles.dsl.layout.objectContain
import io.github.cc007.kolostyles.dsl.renderKoloHtml
import io.github.cc007.kolostyles.dsl.sizing.SizingNamed.FULL
import io.github.cc007.kolostyles.dsl.sizing.SizingNamed.SCREEN
import io.github.cc007.kolostyles.dsl.sizing.maxH
import io.github.cc007.kolostyles.dsl.sizing.maxW
import io.github.cc007.kolostyles.dsl.sizing.minH
import io.github.cc007.kolostyles.dsl.sizing.w
import io.github.cc007.kolostyles.dsl.spacing.*
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.html.*
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import work.socialhub.kbsky.BlueskyFactory
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetPostThreadRequest
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetPostsRequest
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineRequest
import work.socialhub.kbsky.auth.BearerTokenAuthProvider
import work.socialhub.kbsky.model.app.bsky.embed.*
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsThreadUnion
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost
import work.socialhub.kbsky.model.app.bsky.richtext.RichtextFacet
import work.socialhub.kbsky.model.share.RecordUnion

private val logger = KotlinLogging.logger {}

@Controller
class ArtContentController {

    @GetMapping("/art/{cid}", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun artEndpoint(
        request: HttpServletRequest,
        @PathVariable cid: String,
        @RequestParam("uri", required = false) uri: String?,
    ): String {
        return with(SecurityContextHolder.getContext().authentication as AtProtoAuthentication) {
            val auth = BearerTokenAuthProvider(accessToken)
            val csrfToken = (request.getAttribute("_csrf") as? CsrfToken)?.token

            val feed = BlueskyFactory
                .instance(socialUrl)
                .feed()

            val requestedUri = if (!uri.isNullOrBlank()) uri else null
            val post = fetchPost(feed, auth, cid, requestedUri)
            val threadRoot = post?.uri?.let {
                try {
                    feed.getPostThreadBlocking(
                        FeedGetPostThreadRequest(
                            auth = auth,
                            uri = it,
                            depth = 5,
                        )
                    ).data.thread?.asViewPost
                } catch (ex: Exception) {
                    logger.warn(ex) { "Unable to load thread for uri=$it" }
                    null
                }
            }
            val comments = mutableListOf<CommentView>().also { list ->
                threadRoot?.replies.orEmpty().forEach { reply ->
                    collectComments(reply, 0, list)
                }
            }
            val commentDepthMarginSteps = mapOf(1 to 3, 2 to 6, 3 to 10, 4 to 13)

            val pageTitle = buildPageTitle(post)

            renderKoloHtml {
                head {
                    title(pageTitle)
                    meta(name = "viewport", content = "width=device-width, initial-scale=1")
                    link(rel = "stylesheet", href = "/css/generated/art.css")
                    koloStylesheetLink()
                }
                body(classes = "art-body") {
                    kolo { m(0); fontSans; minH(SCREEN) }
                    topBanner(csrfToken)
                    main(classes = "art-layout") {
                        kolo { p(4); mxAuto; maxW(270) }
                        section(classes = "art-content") {
                            kolo { grid }
                            div(classes = "content-top") {
                                kolo { grid }
                                h1(classes = "art-title") {
                                    kolo { m(0); text2xl }
                                    +pageTitle
                                }
                                post?.author?.handle?.let {
                                    p(classes = "art-byline") {
                                        kolo { m(0) }
                                        +"by @$it"
                                    }
                                }
                            }

                            if (post == null) {
                                section(classes = "art-card") {
                                    kolo { p(4) }
                                    p(classes = "art-empty") {
                                        +"Could not load this artwork. Try opening it from browse again."
                                    }
                                }
                            } else {
                                val feedPost = post.record as? FeedPost
                                section(classes = "art-card") {
                                    kolo { p(4) }
                                    section(classes = "art-embed") {
                                        kolo { p(2) }
                                        renderMainEmbed(post.embed)
                                    }

                                    section(classes = "art-description") {
                                        kolo { mt(4) }
                                        h2 {
                                            kolo { mt(0); mx(0); mb(2); textBase }
                                            +"Description"
                                        }
                                        if (feedPost?.text.isNullOrBlank()) {
                                            p(classes = "art-text") {
                                                kolo { m(0) }
                                                +"No description provided."
                                            }
                                        } else {
                                            p(classes = "art-text") {
                                                kolo { m(0) }
                                                renderRichText(feedPost.text, feedPost.facets)
                                            }
                                        }
                                    }
                                }

                                section(classes = "comments") {
                                    kolo { grid; p(4) }
                                    h2 {
                                        kolo { mt(0); mx(0); mb(2); textBase }
                                        +"Comments"
                                    }
                                    if (comments.isEmpty()) {
                                        p(classes = "art-empty") {
                                            kolo { m(0) }
                                            +"No comments yet."
                                        }
                                    } else {
                                        comments.forEach { comment ->
                                            val depthClass = comment.depth.coerceAtMost(4)
                                            val marginLeftStep = commentDepthMarginSteps[depthClass] ?: 0
                                            article(classes = "comment depth-$depthClass") {
                                                kolo { grid; p(2); ml(marginLeftStep) }
                                                div(classes = "comment-header") {
                                                    kolo { flex }
                                                    comment.avatar?.let { avatar ->
                                                        img(src = avatar, classes = "comment-avatar") {
                                                            alt = ""
                                                            width = "28"
                                                            height = "28"
                                                        }
                                                    }
                                                    div(classes = "comment-meta") {
                                                        kolo { grid }
                                                        strong(classes = "comment-author") { +comment.displayName }
                                                        span(classes = "comment-handle") { +"@${comment.handle}" }
                                                    }
                                                }
                                                p(classes = "comment-text") {
                                                    kolo { m(0) }
                                                    renderRichText(comment.text, comment.facets)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.renderMainEmbed(embed: EmbedViewUnion?) {
    when (embed) {
        is EmbedImagesView -> {
            val images = embed.images.orEmpty()
            if (images.isEmpty()) {
                p(classes = "art-empty") {
                    kolo { m(0) }
                    +"No artwork media attached."
                }
                return
            }
            div(classes = if (images.size > 1) "art-image-grid" else "art-image-single") {
                kolo {
                    if (images.size > 1) {
                        grid
                    } else {
                        block
                    }
                }
                images.forEach { image ->
                    img(src = image.fullsize ?: image.thumb ?: "", classes = "art-image") {
                        kolo { block; w(FULL); maxH(18, 25); objectContain }
                        alt = image.alt ?: "Artwork image"
                    }
                }
            }
        }

        is EmbedVideoView -> {
            embed.thumbnail?.let { thumb ->
                img(src = thumb, classes = "art-image") {
                    kolo { block; w(FULL); maxH(18, 25); objectContain }
                    alt = embed.alt ?: "Artwork video"
                }
            } ?: p(classes = "art-empty") {
                kolo { m(0) }
                +"Video embed has no thumbnail available."
            }
        }

        is EmbedExternalView -> {
            val external = embed.external
            if (external?.thumb != null) {
                img(src = external.thumb!!, classes = "art-image") {
                    kolo { block; w(FULL); maxH(18, 25); objectContain }
                    alt = external.title.ifBlank { "Artwork link" }
                }
            }
            p(classes = "art-external") {
                kolo { m(0) }
                +(external?.title?.ifBlank { external.uri } ?: "External embed")
            }
        }

        is EmbedRecordWithMediaView -> renderMainEmbed(embed.media)
        else -> p(classes = "art-empty") {
            kolo { m(0) }
            +"This post has no supported artwork embed."
        }
    }
}

private fun fetchPost(
    feed: work.socialhub.kbsky.api.app.bsky.FeedResource,
    auth: BearerTokenAuthProvider,
    cid: String,
    uri: String?,
): FeedDefsPostView? {
    val targetUri = uri ?: if (cid.startsWith("at://")) cid else null
    if (targetUri != null) {
        try {
            return feed.getPostsBlocking(
                FeedGetPostsRequest(
                    auth = auth,
                    uris = listOf(targetUri)
                )
            ).data.posts.firstOrNull()
        } catch (ex: Exception) {
            logger.warn(ex) { "Unable to load post by uri=$targetUri" }
        }
    }

    // Best-effort fallback when we only have a CID in the route.
    return try {
        feed.getTimelineBlocking(FeedGetTimelineRequest(auth)).data.feed
            .firstOrNull { it.post.cid == cid }
            ?.post
    } catch (ex: Exception) {
        logger.warn(ex) { "Unable to find post by cid=$cid" }
        null
    }
}

private fun postText(record: RecordUnion?): String? = (record as? FeedPost)?.text

private fun buildPageTitle(post: FeedDefsPostView?): String {
    val author = post?.author?.displayName ?: post?.author?.handle ?: "Artwork"
    val text = postText(post?.record).orEmpty().trim().replace("\n", " ")
    val subject = text.takeIf { it.isNotBlank() }?.take(60) ?: "Post"
    return "$author - $subject"
}

private data class CommentView(
    val displayName: String,
    val handle: String,
    val avatar: String?,
    val text: String,
    val facets: List<RichtextFacet>?,
    val depth: Int,
)

private fun collectComments(
    thread: FeedDefsThreadUnion,
    depth: Int,
    into: MutableList<CommentView>,
) {
    val reply = thread.asViewPost ?: return
    val post = reply.post ?: return
    val commentRecord = post.record as? FeedPost
    val text = commentRecord?.text
    if (!text.isNullOrBlank()) {
        into += CommentView(
            displayName = post.author?.displayName ?: post.author?.handle ?: "Unknown",
            handle = post.author?.handle ?: "unknown",
            avatar = post.author?.avatar,
            text = text,
            facets = commentRecord.facets,
            depth = depth,
        )
    }
    reply.replies.orEmpty().forEach { child -> collectComments(child, depth + 1, into) }
}
