package com.rbs.iso8583lib.model

import com.google.gson.annotations.SerializedName

data class IsoDataClass(
    @SerializedName("P-2")
    val p2: Long?, // 4243132000540450
    @SerializedName("P-3")
    val p3: String?, // 000000
    @SerializedName("P-4")
    val p4: String?, // 000000000069
    @SerializedName("P-11")
    val p11: Int?, // 100314
    @SerializedName("P-12")
    val p12: Int?, // 163839
    @SerializedName("P-13")
    val p13: String?, // 0317
    @SerializedName("P-23")
    val p23: String?, // 000
    @SerializedName("P-37")
    val p37: String?, // 000667514662
    @SerializedName("P-38")
    val p38: Int?, // 727091
    @SerializedName("P-39")
    val p39: String?, // 00
    @SerializedName("P-41")
    val p41: Int?, // 14150026
    @SerializedName("P-49")
    val p49: Int?, // 404
    @SerializedName("P-55")
    val p55: String?, // 8A023030
    @SerializedName("P-57")
    val p57: String?, // 003930
    @SerializedName("P-63")
    val p63: String? // 003930
)