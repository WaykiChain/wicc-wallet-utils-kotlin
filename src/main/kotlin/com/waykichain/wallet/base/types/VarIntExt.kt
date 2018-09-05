/*
 * Developed by Richard Chen on 9/4/18 4:33 PM
 * Last modified 9/4/18 4:33 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet.base.types

import org.bitcoinj.core.VarInt

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