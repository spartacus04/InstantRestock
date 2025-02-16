package me.spartacus04.instantrestock.config

import com.google.gson.annotations.SerializedName

enum class FieldLanguageMode {
    @SerializedName("default")
    DEFAULT,

    @SerializedName("custom")
    CUSTOM;
}