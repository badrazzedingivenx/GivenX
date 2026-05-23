package com.example.client_mobile.screens.shared

/**
 * Domain / UI model for a social post.
 * Populated by PostViewModel from PostDto; never exposes Retrofit types to the UI layer.
 */
data class PostItem(
    val id:            String,
    val authorName:    String,
    val authorAvatar:  String?,
    val content:       String,
    val mediaUrl:      String?,
    val hashtags:      List<String>,
    val likesCount:    Int,
    val commentsCount: Int,
    val isLiked:       Boolean,
    val createdAt:     String
)
