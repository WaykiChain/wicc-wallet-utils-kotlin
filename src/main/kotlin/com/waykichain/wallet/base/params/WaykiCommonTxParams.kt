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

package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiRegId
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoin.NativeSecp256k1
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt
import java.io.ByteArrayOutputStream

/**
 * srcRegId: (regHeight-regIndex)
 * destAddr: 20-byte PubKeyHash
 */
class WaykiCommonTxParams(nValidHeight: Long, fees: Long, val value: Long, val srcRegId: String, val destAddr: ByteArray):
        BaseSignTxParams(null, null, nValidHeight, fees, WaykiTxType.TX_COMMON, 1) {
    override fun getSignatureHash(): ByteArray {
        val regId = parseRegId(srcRegId)!! //(regHeight, regIndex)
        val vContract: ByteArray? = null  //vContract: can be used for sending notes

        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(VarInt(4).encodeInOldWay())
                .add(VarInt(regId.regHeight).encodeInOldWay())
                .add(VarInt(regId.regIndex).encodeInOldWay())
                .add(VarInt(destAddr.size.toLong()).encodeInOldWay())
                .add(destAddr)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(value).encodeInOldWay())
                .add(VarInt(0).encodeInOldWay())
                .add(vContract)

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
        val hashStr = Utils.HEX.encode(hash)
        System.out.println("hash: $hashStr")

        return hash
    }

    /**
     * run this test with -Djava.library.path=$PATH_LIBSECP256K1_DIR where $PATH_LIBSECP256K1_DIR is a directory that
     * contains libsecp256k1.so. For example:
     * mvn test  -DargLine="-Djava.library.path=$PATH_LIBSECP256K1_DIR"
     * To create libsecp256k1.so:
     * clone libsecp256k1
     * $./autogen.sh && ./configure --enable-experimental --enable-module_ecdh --enable-jni && make clean && make && make check
     * libsecp256k1.so should be in the .libs/ directory
     */
    override fun signTx(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        signature = NativeSecp256k1.sign(sigHash, key.privKeyBytes)

        return signature!!
    }

    override fun serializeTx(): String {
        assert (signature != null)

        val regId = parseRegId(srcRegId)!!    //regData: regHeight, regIndex
        val sigSize = signature!!.size
        val vContract: ByteArray? = null

        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(VarInt(4).encodeInOldWay())
                .add(VarInt(regId.regHeight).encodeInOldWay())
                .add(VarInt(regId.regIndex).encodeInOldWay())
                .add(VarInt(destAddr.size.toLong()).encodeInOldWay())
                .add(destAddr)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(value).encodeInOldWay())
                .add(VarInt(0).encodeInOldWay())
                .add(vContract)
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr =  Utils.HEX.encode(ss.toByteArray())
        return hexStr
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
