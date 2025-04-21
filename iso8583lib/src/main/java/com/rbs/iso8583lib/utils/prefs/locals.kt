package com.rbs.iso8583lib.utils.prefs

import com.rbs.iso8583lib.utils.cores.PrefManager

var stanCounterValue: Int
    get() {
        if (!PrefManager.contains("stanCounterValue")) return 100000
        return PrefManager.getInt("stanCounterValue", 100000)
    }
    set(value) {
        PrefManager.putAny("stanCounterValue", value)
    }
var batchNumberCounterValue: Int
    get() {
        if (!PrefManager.contains("batchNumberCounterValue")) return 100000
        return PrefManager.getInt("batchNumberCounterValue", 100000)
    }
    set(value) {
        PrefManager.putAny("batchNumberCounterValue", value)
    }
var originalBatchNumberUsedFor0320: String?
    get() {
        if (!PrefManager.contains("originalBatchNumberUsedFor0320")) return null
        return PrefManager.getString("originalBatchNumberUsedFor0320", null)
    }
    set(value) {
        value?.let { PrefManager.putAny("originalBatchNumberUsedFor0320", it) }
    }