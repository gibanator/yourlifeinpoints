package com.example.lifeinpoints.data.remote.category

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName(value = "id", alternate = ["categoryId", "category_id"])
    val id: Int? = null,
    val name: String,
    @SerializedName(value = "active", alternate = ["isActive", "is_active"])
    val active: Boolean = true,
    @SerializedName(value = "visible", alternate = ["isVisible", "is_visible"])
    val visible: Boolean = true
)

data class CategoryCreateRequest(
    val name: String
)

data class CategoryCreateResponse(
    @SerializedName(value = "id", alternate = ["categoryId", "category_id"])
    val id: Long? = null
)

data class CategoryUpdateRequest(
    val name: String,
    val active: Boolean,
    val visible: Boolean
)

data class CategoryVisibilitySwitchResponse(
    @SerializedName(value = "id", alternate = ["categoryId", "category_id"])
    val id: Int? = null,
    @SerializedName(value = "visible", alternate = ["isVisible", "is_visible"])
    val visible: Boolean
)
