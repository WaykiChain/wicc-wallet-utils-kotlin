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

import com.waykichain.wallet.base.WaykiRegId
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoin.NativeSecp256k1
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt
import java.io.ByteArrayOutputStream

class WaykiCommonTxParams : BaseSignTxParams() {

    init {
        nTxType = WaykiTxType.TX_NONE
        nVersion = 1
    }

    override fun getSignatureHash(): ByteArray {

        val ss = ByteArrayOutputStream()

        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(nTxType.value)
        ss.write(VarInt(nValidHeight).encodeInOldWay())

        //regData: regHeight, regIndex
        val regId = parseRegId(srcRegId)!!
        ss.write(VarInt(4).encodeInOldWay())
        ss.write(VarInt(regId.regHeight).encodeInOldWay())
        ss.write(VarInt(regId.regIndex).encodeInOldWay())

        //vDestHash
        ss.write(VarInt(destAddr!!.size.toLong()).encodeInOldWay())
        ss.write(destAddr)

        ss.write(VarInt(fees).encodeInOldWay())
        ss.write(VarInt(value).encodeInOldWay())

        //vContract: can be used for sending notes
//        val vContract: ByteArray? = null
        ss.write(VarInt(0).encodeInOldWay())
//        ss.write(vContract)

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

        val ss = ByteArrayOutputStream()

        ss.write(VarInt(nTxType.value.toLong()).encodeInOldWay())
        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(VarInt(nValidHeight).encodeInOldWay())

        //regData: regHeight, regIndex
        val regId = parseRegId(srcRegId)!!
        ss.write(VarInt(4).encodeInOldWay())
        ss.write(VarInt(regId.regHeight).encodeInOldWay())
        ss.write(VarInt(regId.regIndex).encodeInOldWay())

        //vDestHash
        ss.write(VarInt(destAddr!!.size.toLong()).encodeInOldWay())
        ss.write(destAddr)

        ss.write(VarInt(fees).encodeInOldWay())
        ss.write(VarInt(value).encodeInOldWay())

        //vContract
        val vContract: ByteArray? = null
        ss.write(VarInt(0).encodeInOldWay())
//        ss.write(vContract)

        val sigSize = signature!!.size
        ss.write(VarInt(sigSize.toLong()).encodeInOldWay())
        ss.write(signature)

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

    var srcRegId = "" /** (regHeight-regIndex) */
    var destAddr: ByteArray? = null /** 20-byte PubKeyHash */

    var value = 0L
}
