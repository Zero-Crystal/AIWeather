package com.zero.aiweather.model

import org.litepal.crud.LitePalSupport

data class WebImageTable (
    var imageUrl: String
) : LitePalSupport()
