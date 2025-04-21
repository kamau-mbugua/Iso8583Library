package com.rbs.iso8583lib.utils

import com.rbs.iso8583lib.utils.prefs.batchNumberCounterValue
import com.rbs.iso8583lib.utils.prefs.stanCounterValue
import java.util.concurrent.atomic.AtomicInteger

object STANGenerator {
    private val stanCounter: AtomicInteger = AtomicInteger(loadLastSTAN())

    private fun loadLastSTAN(): Int {
        return stanCounterValue ?:  100000 // Default start at 100000
    }

    private fun saveLastSTAN(stan: Int) {
        stanCounterValue = stan
    }

    fun generateSTANPrimary(): String {
        val newStan = stanCounter.getAndUpdate { current ->
            val next = (current + 1) % 1000000 // Ensure STAN rolls over at 999999
            if (next < 100000) 100000 else next // Keep within 6-digit range
        }
        saveLastSTAN(newStan)
        return newStan.toString().padStart(6, '0') // Ensure N6 format
    }
}

object BatchNoGenerator {
    private val batchNoCounter: AtomicInteger = AtomicInteger(loadLastBatchNo())

    private fun loadLastBatchNo(): Int {
        return batchNumberCounterValue ?: 1 // Start at 000001
    }

    private fun saveLastBatchNo(batchNo: Int) {
        batchNumberCounterValue = batchNo
    }

    fun generateBatchNumberPrimary(): String {
        val newBatchNo = batchNoCounter.getAndUpdate { current ->
            val next = (current + 1) % 1_000_000 // Wrap after 999999
            if (next == 0) 1 else next // Ensure we never get 000000
        }
        saveLastBatchNo(newBatchNo)
        return newBatchNo.toString().padStart(6, '0') // Always return N6
    }
}

