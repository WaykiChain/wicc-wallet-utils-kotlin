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

import com.waykichain.wallet.base.WaykiTxType
import org.bitcoin.NativeSecp256k1
import org.bitcoinj.core.ECKey
import java.io.ByteArrayOutputStream
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt

import com.waykichain.wallet.base.types.encodeInOldWay

class WaykiRegisterAccountTxParams: BaseSignTxParams() {

    init {
        nTxType = WaykiTxType.TX_REGISTERACCOUNT //2
        nVersion = 1
    }

    override fun getSignatureHash(): ByteArray {

        val ss = ByteArrayOutputStream()
        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(nTxType.value)
        ss.write(VarInt(nValidHeight).encodeInOldWay())

        ss.write(VarInt(33).encodeInOldWay())
        ss.write(userPubKey)

        ss.write(VarInt(0).encodeInOldWay())
//        ss.write(minerPubKey)

        ss.write(VarInt(fees).encodeInOldWay())

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

        ss.write(33)
        ss.write(userPubKey)

        ss.write(0)
        ss.write(minerPubKey)

        ss.write(VarInt(fees).encodeInOldWay())

        val sigSize = signature!!.size
        ss.write(VarInt(sigSize.toLong()).encodeInOldWay())
        ss.write(signature)

        val bytes = ss.toByteArray()
        val hexStr =  Utils.HEX.encode(bytes)
        return hexStr

    }

}