package com.example.jetpack.compose.mvi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * MVI开发框架：
 * M ViewModel 视图模型(可被观测数据)
 * V View试图
 * I Intent意图
 */
class MVIComposeActivity : ComponentActivity() {

    private val viewModel by viewModels<ContentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContentView(this@MVIComposeActivity, viewModel = viewModel)
        }
    }
}

@Composable
fun ContentView(activity: MVIComposeActivity, viewModel: ContentViewModel) {
    //请求数据
    val viewState = viewModel.uiState
    //请求数据的意图
    LaunchedEffect(key1 = true) {
        //发送意图
        viewModel.newsChannel.send(ContentIntent.GetContent)
    }
    //数据与View的绑定
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        viewState.dataList.forEachIndexed { index, it ->
            NewsItem(
                viewModel,
                position = index,
                news = it
            )
        }

    }

    viewState.detailContent?.let {
        Toast.makeText(activity,
            it,
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun NewsItem(viewModel: ContentViewModel, position: Int, news: String) {
    Card(modifier = Modifier.padding(10.dp)) {
        val modifier = Modifier
            .clickable(
                onClick = {
                    viewModel.viewModelScope.launch {
                        //发送意图
                        viewModel.newsChannel.send(ContentIntent.GetItemDetail(position))
                    }
                }
            )
        Row(
            modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(news)
        }
    }

}
