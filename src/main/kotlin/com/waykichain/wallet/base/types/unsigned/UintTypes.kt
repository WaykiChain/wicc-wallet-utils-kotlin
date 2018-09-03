/*
 * Developed by Richard Chen on 9/1/18 10:20 AM
 * Last modified 9/1/18 10:20 AM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet.base.types.unsigned

class Uint256 {
    val data = Array(256) { _ -> Uint(0) }
}

class Uint64 {
    val data = Array(64) { _ -> Uint(0) }
}

class Uint8 {
    val data = Array(8) { _ -> Uint(0) }
}

fun getUnsignedByte(value: Byte): Int {
    return value.toInt() and 0xFF
}

fun getUnsignedShort(value: Short):Int{
    return value.toInt() and 0xFFFF
}

fun getUnsignedInt(value: Int): Long{
    return value.toLong() and 0xFFFFFFFF
}