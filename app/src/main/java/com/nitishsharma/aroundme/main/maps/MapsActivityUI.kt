package com.nitishsharma.aroundme.main.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SetupLazyColumn(
    placesToGo: ArrayList<Pair<Pair<Int, String>, String>>,
    onClick: ((String) -> Unit)? = null
) {
    LazyRow() {
        items(items = placesToGo) {
            PlaceItem(it, onClick)
        }
    }
}

@Composable
fun PlaceItem(item: Pair<Pair<Int, String>, String>, onClick: ((String) -> Unit)?) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = {
                onClick?.invoke(item.second)
            })
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.first.first),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.first.second,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}