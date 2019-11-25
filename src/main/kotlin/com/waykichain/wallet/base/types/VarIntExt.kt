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

package com.waykichain.wallet.base.types

import org.bitcoinj.core.VarInt
import java.io.ByteArrayInputStream

fun VarInt.encodeInOldWay(): ByteArray {
    val size = size()
    val tmp = ByteArray((size*8+6)/7) { _ -> 0 }
    val ret = ArrayList<Byte>()
    var len = 0
    var n = value

    while (true) {
        val h: Long = if (len == 0) 0x00 else 0x80
        tmp[len] = n.and( 0x7F ).or(h).toByte()
        if (n <= 0x7F)
            break
        n = ( n shr 7) - 1
        len++
    }

    do {
        ret.add( tmp[len] )
    } while (len-- > 0)

    return ret.toByteArray()
}

fun VarInt.size(): Int {
    var ret = 0
    var n = value

    while (true) {
        ret++
        if (n <= 0x7F)
            break
        n = (n shr 7) - 1
    }
    return ret
}

fun VarInt.decode(array: ByteArrayInputStream):VarInt {
    var n: Long = 0
    while (true) {
        val c: Long
        c = array.read().toLong()
        n = (n shl 7).or(c.and(0x7F))
        if (c.and(0x80) != 0L)
            n++
        else return VarInt(n)
    }
}