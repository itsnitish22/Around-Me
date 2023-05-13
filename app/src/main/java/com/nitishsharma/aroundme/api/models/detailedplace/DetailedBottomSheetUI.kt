package com.nitishsharma.aroundme.api.models.detailedplace

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
fun PlaceImage(
    imageUrl: String? = null,
    @DrawableRes defaultImage: Int? = null
) {

    Card(
        backgroundColor = Color.White,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
            }
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            if (painter.state is ImagePainter.State.Loading) {
                CircularProgressIndicator()
            }
        }
    }
}

