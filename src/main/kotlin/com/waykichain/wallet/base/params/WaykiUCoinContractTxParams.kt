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

class WaykiUCoinContractTxParams(userPubKey: String, nValidHeight: Long, fees: Long, val value: Long, val srcRegId: String,
                            val destRegId: String, val vContract: ByteArray?,feeSymbol:String,val coinSymbol:String):
        BaseSignTxParams(feeSymbol,userPubKey, null, nValidHeight, fees, WaykiTxType.UCONTRACT_INVOKE_TX, 1) {
    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        val publicKey = Utils.HEX.decode(userPubKey)
        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(VarInt(nTxType.value.toLong()).encodeInOldWay())
        ss.write(VarInt(nValidHeight).encodeInOldWay())
        ss.writeUserId(srcRegId, publicKey)
        ss.writeRegId(destRegId)
        ss.write(VarInt(vContract!!.size.toLong()).encodeInOldWay())
        ss.write(vContract)
        ss.write(VarInt(fees).encodeInOldWay())
        ss.add(feeSymbol)
        ss.add(coinSymbol)
        ss.write(VarInt(value).encodeInOldWay())

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
        val hashStr = Utils.HEX.encode(hash)
        System.out.println("hash: $hashStr")

        return hash
    }

    override fun signTx(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        val ecSig = key.sign(Sha256Hash.wrap(sigHash))
        signature = ecSig.encodeToDER()
        return signature!!
    }

    override fun serializeTx(): String {
        assert(signature != null)
        val ss = HashWriter()
        val publicKey = Utils.HEX.decode(userPubKey)
        ss.write(VarInt(nTxType.value.toLong()).encodeInOldWay())
        ss.write(VarInt(nVersion).encodeInOldWay())
        ss.write(VarInt(nValidHeight).encodeInOldWay())
        ss.writeUserId(srcRegId, publicKey)
        ss.writeRegId(destRegId)
        ss.write(VarInt(vContract!!.size.toLong()).encodeInOldWay())
        ss.write(vContract)
        ss.write(VarInt(fees).encodeInOldWay())
        ss.add(feeSymbol)
        ss.add(coinSymbol)
        ss.write(VarInt(value).encodeInOldWay())
        val sigSize = signature!!.size
        ss.write(VarInt(sigSize.toLong()).encodeInOldWay())
        ss.write(signature)
        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }

    companion object {
        fun unSerializeTx(ss: HashReader): BaseSignTxParams {
            val nVersion = ss.readVarInt().value
            val nValidHeight = ss.readVarInt().value
            val array = ss.readUserId()
            val srcRegId = array[0]
            val publicKey = array[1]
            val destRegId = ss.readRegId()
            val vContract = ss.readByteArray()
            val fees = ss.readVarInt().value
            val feeSymbol = ss.readString()
            val coinSymbol = ss.readString()
            val value = ss.readVarInt().value
            val signature = ss.readByteArray()
            val ret = WaykiUCoinContractTxParams(publicKey, nValidHeight, fees, value, srcRegId, destRegId, vContract, feeSymbol, coinSymbol)
            ret.signature = signature
            ret.nVersion = nVersion
            return ret
        }
    }
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[nTxType]=").append(nTxType).append("\n")
                .append("[nVersion]=").append(nVersion).append("\n")
                .append("[nValidHeight]=").append(nValidHeight).append("\n")
                .append("[srcRegId]=").append(srcRegId).append("\n")
                .append("[destRegId]=").append(destRegId).append("\n")
                .append("[pubKey]=").append(userPubKey).append("\n")
                .append("[feeSymbol]=").append(feeSymbol).append("\n")
                .append("[coinSymbol]=").append(coinSymbol).append("\n")
                .append("[fees]=").append(fees).append("\n")
                .append("[value]=").append(value).append("\n")
                .append("[vContract]=").append(Utils.HEX.encode(vContract)).append("\n")
                .append("[signature]=").append(Utils.HEX.encode(signature)).append("\n")
        return builder.toString()
    }


}
