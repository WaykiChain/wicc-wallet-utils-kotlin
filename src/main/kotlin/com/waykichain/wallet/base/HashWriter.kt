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
import com.waykichain.wallet.util.longToBytes
import com.waykichain.wallet.util.unLongToIntByteArray
import com.waykichain.wallet.util.unLongToShortByteArray
import org.bitcoinj.core.VarInt
import java.io.ByteArrayOutputStream

class HashWriter : ByteArrayOutputStream() {


    fun add(data: String?): HashWriter {
        if (data != null) {
            val arr = data.toByteArray()
            this.write(VarInt(arr.size.toLong()).encodeInOldWay())
            this.write(data.toByteArray())
        }
        return this
    }

    fun add(data: ByteArray?): HashWriter {
        if (data != null)
            this.write(data)
        return this
    }

    /** Vote: "$voteType-$pubKey-$votes" */
    fun add(operVoteFund: Array<OperVoteFund>): HashWriter {
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

    fun writeUserId(userIdStr: String, pubKey: ByteArray?): HashWriter {

        val regId = parseRegId(userIdStr)
        if (regId != null) {
            writeRegId(userIdStr)
        } else if (pubKey != null) {
            this.write(pubKey!!.size)
            this.write(pubKey)
        }
        return this
    }

    fun addAsset(asset: CAsset): HashWriter {
        val buff = HashWriter()
        val mintable = if (asset.minTable) 1 else 0
        buff.add(asset.symbol)
        buff.writeRegId(asset.ownerRegid)
        buff.add(asset.name)
        buff.write(mintable)
        buff.write(VarInt(asset.totalSupply).encodeInOldWay())
        this.write(buff.toByteArray())
        return this
    }

    fun updateAsset(data: AssetUpdateData): HashWriter {
        when (data.enumAsset.type) {
            1 -> {
                this.write(1)
                this.parseRegId(data.value.toString())
            };
            2 -> {
                this.write(2)
                this.add(data.value.toString())
            };
            3 -> {
                this.write(3)
                val amount = VarInt(data.value as Long).encodeInOldWay()
                this.write(amount)
            };
        }
        return this
    }


    fun writeScript(script: ByteArray, description: String): HashWriter {
        val buff = HashWriter()
        buff.writeCompactSize(script.size.toLong())
        buff.write(script)
        buff.writeCompactSize(description.length.toLong())
        buff.write(description.toByteArray())
        this.writeCompactSize(buff.toByteArray().size.toLong())
        this.write(buff.toByteArray())
        return this
    }

    fun writeCompactSize(len: Long) {
        if (len < 253) {
            val arr = ByteArray(1)
            arr[0] = len.toByte()
            this.write(arr)
        } else if (len < 0x10000) {
            val arr1 = ByteArray(1)
            arr1[0] = 253.toByte()
            this.write(arr1)
            val arr2 = len.unLongToShortByteArray(true)
            this.write(arr2)
        } else if (len < 0x100000000) {
            val arr1 = ByteArray(1)
            arr1[0] = 254.toByte()
            val arr2 = len.unLongToIntByteArray(true)
            this.write(arr2)
        } else {
            val arr1 = ByteArray(1)
            arr1[0] = 255.toByte()
            val arr2 = len.longToBytes()
            this.write(arr2)
        }
    }

    fun parseRegId(regId: String): WaykiRegId? {
        val arr = regId?.split("-")
        if (arr.size > 1) {
            if (!intOrString(arr[0])) return null
            if (!intOrString(arr[1])) return null
            val height = arr[0].toLong()
            val index = arr[1].toLong()
            return WaykiRegId(height, index)
        } else {
            return null
        }
    }

    fun intOrString(str: String): Boolean {
        val v = str.toIntOrNull()
        return when (v) {
            null -> false
            else -> true
        }
    }
}

const val cdpHash = "0000000000000000000000000000000000000000000000000000000000000000"
const val SYMBOL_MATCH="[A-Z]{1,7}$"

data class WaykiRegId(var regHeight: Long, var regIndex: Long)
data class OperVoteFund(var voteType: Int, var pubKey: ByteArray, var voteValue: Long)
data class CAsset(var symbol: String, var ownerRegid: String, var name: String, var totalSupply: Long, var minTable: Boolean)