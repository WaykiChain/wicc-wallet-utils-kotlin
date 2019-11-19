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

import com.waykichain.wallet.base.HashReader
import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*

class WaykiRegisterAccountTxParams(userPubKey: String, minerPubKey: ByteArray?, nValidHeight: Long, fees: Long,feeSymbol:String):
        BaseSignTxParams(feeSymbol,userPubKey, minerPubKey, nValidHeight, fees, WaykiTxType.TX_REGISTERACCOUNT, 1) {
    final override fun getSignatureHash(): ByteArray {
        val pubKey=Utils.HEX.decode(userPubKey)
        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(VarInt(33).encodeInOldWay())
                .add(pubKey)
                .add(VarInt(0).encodeInOldWay())
                .add(minerPubKey)
                .add(VarInt(fees).encodeInOldWay())

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
//        val hashStr = Utils.HEX.encode(hash)
//        System.out.println("hash: $hashStr")
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
    final override fun signTx(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        val ecSig =key.sign(Sha256Hash.wrap(sigHash))
        signature = ecSig.encodeToDER()//NativeSecp256k1.sign(sigHash, key.privKeyBytes)
        return signature!!
    }

    final override fun serializeTx(): String {
        assert (signature != null)
        val pubKey=Utils.HEX.decode(userPubKey)
        val sigSize = signature!!.size
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .add(33)
                .add(pubKey)
                .add(0)
                .add(minerPubKey)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val bytes = ss.toByteArray()
        val hexStr =  Utils.HEX.encode(bytes)
        return hexStr
    }

    companion object {
        fun unSerializeTx(ss: HashReader): BaseSignTxParams {
            //val ss = HashReader(Utils.HEX.decode(param))
           // val nTxType = WaykiTxType.init(ss.readVarInt().value.toInt())
            val nVersion = ss.readVarInt().value
            val nValidHeight = ss.readVarInt().value
            val userPubKey = ss.readPubKey()
            ss.read()// 0
            val fees = ss.readVarInt().value
            val signature = ss.readByteArray()
            val ret = WaykiRegisterAccountTxParams(userPubKey, null, nValidHeight, fees, "" )
            //ret.nTxType = nTxType
            ret.nVersion = nVersion
            ret.signature = signature
            return ret
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[nTxType]=").append(nTxType).append("\n")
                .append("[nVersion]=").append(nVersion).append("\n")
                .append("[nValidHeight]=").append(nValidHeight).append("\n")
                .append("[pubKey]=").append(userPubKey).append("\n")
                .append("[fees]=").append(fees).append("\n")
                .append("[signature]=").append(Utils.HEX.encode(signature)).append("\n")

        return builder.toString()
    }

}