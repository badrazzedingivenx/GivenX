package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Post ─────────────────────────────────────────────────────────────────────
// Matches: GET  /api/v1/posts → { "success": true, "data": { "posts": [...] } }
//          POST /api/v1/posts → { "success": true, "data": <PostDto> }
data class PostDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("content")
    val content: String = "",

    // DB column: `media_url` — absolute URL, pass directly to Coil
    @SerializedName(value = "media_url", alternate = ["mediaUrl"])
    val mediaUrl: String? = null,

    // DB column: `hashtags` — comma-separated string
    @SerializedName("hashtags")
    val hashtags: String? = null,

    // DB column: `mentions` — JSON array of user IDs
    @SerializedName("mentions")
    val mentions: List<Long>? = null,

    // DB column: `likes_count`
    @SerializedName(value = "likes_count", alternate = ["likesCount"])
    val likesCount: Int = 0,

    @SerializedName(value = "is_liked", alternate = ["isLiked"])
    val isLiked: Boolean = false,

    // DB column: `comments_count`
    @SerializedName(value = "comments_count", alternate = ["commentsCount"])
    val commentsCount: Int = 0,

    // DB column: `created_at`
    @SerializedName(value = "created_at", alternate = ["createdAt"])
    val createdAt: String = "",

    // DB column: `lawyer_name` — flat fallback when nested object absent
    @SerializedName(value = "lawyer_name", alternate = ["lawyerName"])
    val lawyerNameFlat: String = "",

    // Nested lawyer object — avatar_url inside is an absolute URL
    @SerializedName("lawyer")
    val lawyer: LawyerDto? = null,

    @SerializedName("user")
    val user: HaqUserDto? = null
) {
    /** Resolved display name: nested lawyer → flat field → nested user → fallback */
    val authorName: String
        get() = lawyer?.name?.takeIf { it.isNotBlank() }
            ?: lawyerNameFlat.ifBlank { user?.fullName ?: "Avocat" }

    /** Resolved avatar URL (absolute): nested lawyer → nested user → empty */
    val authorAvatarUrl: String
        get() = lawyer?.avatarUrl?.takeIf { it.isNotBlank() }
            ?: user?.avatar ?: ""

    /** Parsed hashtag list: splits comma-separated string and ensures `#` prefix. */
    val hashtagList: List<String>
        get() = hashtags
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.map { if (it.startsWith("#")) it else "#$it" }
            ?: emptyList()
}

// ─── Request body for creating a post (JSON, no media) ───────────────────────
data class CreatePostRequest(
    @SerializedName("content")  val content:  String,
    @SerializedName("hashtags") val hashtags: String?     = null,
    @SerializedName("mentions") val mentions: List<Long>? = null
)

// ─── Paginated response wrapper ───────────────────────────────────────────────
/** GET /posts → {"success":true,"data":{"posts":[...],"pagination":{...}}} */
data class PostsResponseDto(
    @SerializedName("posts")      val posts:      List<PostDto>   = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta? = null
)
