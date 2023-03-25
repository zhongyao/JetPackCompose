package com.example.jetpack.compose.mvi


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author：zhongyao
 * @date：2023/3/25
 * @description：
 */
class ContentViewModel : ViewModel() {

    // 创建信道newsChannel，用来接受各种用户行为发过来的意图。
    val newsChannel = Channel<ContentIntent>(Channel.UNLIMITED)

    // 创建状态管理uiState，由其完成数据的绑定。
    var uiState by mutableStateOf(NaviViewState())

    init {
        handleIntent();
    }

    /**
     * 初始化的时候，进行意图和获取数据的关系绑定
     */
    private fun handleIntent() {
        //起协程
        viewModelScope.launch {
            newsChannel.consumeAsFlow().collect() {
                //接收用户传递过来的意图
                when (it) {
                    is ContentIntent.GetContent -> getContent()
                    is ContentIntent.GetItemDetail -> getNewsDetail(it)
                }
            }
        }
    }

    private val newsFlow: Flow<List<String>> = flow {
        val list = mutableListOf<String>()
        list.add("NBA-News")
        list.add("CBA-News")
        emit(list)
    }

    private val detailsFlow: Flow<List<String>> = flow {
        val list = mutableListOf<String>()
        list.add("NBA-News-Details")
        list.add("CBA-News-Details")
        emit(list)

    }

    private fun getContent() {
        viewModelScope.launch {
            newsFlow.flowOn(Dispatchers.Default).collect { contents ->
                uiState = uiState.copy(dataList = contents)
            }
        }
    }

    private fun getNewsDetail(it: ContentIntent.GetItemDetail) {
        viewModelScope.launch {
            detailsFlow.flowOn(Dispatchers.Default).collect { contents ->
                uiState = uiState.copy(detailContent = contents[it.select])
            }
        }
    }

}