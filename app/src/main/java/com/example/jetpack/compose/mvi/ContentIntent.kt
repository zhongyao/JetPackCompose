package com.example.jetpack.compose.mvi

/**
 * @author：zhongyao
 * @date：2023/3/25
 * @description：
 */

/**
 * UI状态
 */
data class NaviViewState(
    var detailContent: String? = null,
    val dataList: List<String> = emptyList()
)

/**
 * Intent意图
 */
sealed class ContentIntent {
    object GetContent : ContentIntent()

    class GetItemDetail(val select: Int) : ContentIntent()
}
