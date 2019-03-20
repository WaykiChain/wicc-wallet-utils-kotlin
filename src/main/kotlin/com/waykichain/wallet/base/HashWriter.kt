/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 The Waykichain Core developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package com.waykichain.wallet.base

import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.VarInt
import java.io.ByteArrayOutputStream

class HashWriter: ByteArrayOutputStream() {

    fun add(data: ByteArray?): HashWriter {
        if (data != null)
            this.write(data)
        return this
    }
    /** Vote: "$voteType-$pubKey-$votes" */
    fun add(operVoteFund: Array<OperVoteFund>):HashWriter {
        this.write(VarInt(operVoteFund.size.toLong()).encodeInOldWay())
        for (oper in operVoteFund) {
            this.write(VarInt(oper.voteType.toLong()).encodeInOldWay())
            this.write(VarInt(33).encodeInOldWay())
            this.write(oper.pubKey)
            this.write(VarInt(oper.voteValue).encodeInOldWay())
        }
        return this
    }

    fun add(data: Int): HashWriter {
        this.write(data)
        return this
    }

    /** regIdStr: "$regHeight-$regIndex" */
    fun writeRegId(regIdStr: String): HashWriter {
        val regId = parseRegId(regIdStr)!!
        val heightBytes = VarInt(regId.regHeight).encodeInOldWay()
        val indexBytes = VarInt(regId.regIndex).encodeInOldWay()
        val regIdLen = heightBytes.size.toLong() + indexBytes.size.toLong()

        this.write(VarInt(regIdLen).encodeInOldWay())
        this.write(heightBytes)
        this.write(indexBytes)
        return this
    }

    fun parseRegId(regId: String): WaykiRegId? {
        val arr = regId.split("-")
        if (!intOrString(arr[0])) return null
        if (!intOrString(arr[1])) return null
        val height = arr[0].toLong()
        val index = arr[1].toLong()
        return WaykiRegId(height, index)
    }

    fun intOrString(str: String): Boolean {
        val v = str.toIntOrNull()
        return when(v) {
            null -> false
            else -> true
        }
    }
}

data class WaykiRegId(var regHeight: Long, var regIndex: Long)
data class OperVoteFund(var voteType: Int, var pubKey: ByteArray, var voteValue:Long)