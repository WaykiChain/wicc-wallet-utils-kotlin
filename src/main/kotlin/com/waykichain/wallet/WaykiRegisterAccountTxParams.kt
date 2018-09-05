/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 9/1/18 12:35 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.BaseSignTxParams
import org.bitcoin.NativeSecp256k1
import org.bitcoinj.core.ECKey
import java.io.ByteArrayOutputStream
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt

import com.waykichain.wallet.base.types.encodeInOldWay

class WaykiRegisterAccountTxParams: BaseSignTxParams() {

    init {
        nTxType = 2
        nVersion = 1
        minerPubKey = ByteArray(0)
    }

    override fun getSignatureHash(): ByteArray {

        val ss = ByteArrayOutputStream()
        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(nTxType.toInt())
        ss.write(VarInt(nValidHeight).encodeInOldWay())

        ss.write(VarInt(33).encodeInOldWay())
        ss.write(userPubKey)

        ss.write(VarInt(0).encodeInOldWay())
        ss.write(minerPubKey)

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

        ss.write(VarInt(nTxType).encodeInOldWay())
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